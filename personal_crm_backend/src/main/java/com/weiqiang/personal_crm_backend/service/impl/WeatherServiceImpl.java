package com.weiqiang.personal_crm_backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiqiang.personal_crm_backend.common.Constants;
import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.model.vo.WeatherVO;
import com.weiqiang.personal_crm_backend.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

/**
 * 天气服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final StringRedisTemplate redisTemplate;

    @Value("${weather.api-key:your_qweather_api_key_placeholder}")
    private String apiKey;

    @Value("${weather.api-host:nk3dnacruh.re.qweatherapi.com}")
    private String apiHost;

    @Value("${weather.ip-api-url:http://ip-api.com/json/}")
    private String ipApiUrl;

    @Value("${weather.default-city:杭州}")
    private String defaultCity;

    private RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public WeatherVO getWeather(String address, Double latitude, Double longitude, String ip) {
        long startTime = System.currentTimeMillis();
        try {
            validateCoordinates(latitude, longitude);

            if (hasText(address)) {
                return getWeatherBySearchCity(extractCityName(address));
            }

            if (latitude != null && longitude != null) {
                ResolvedCity resolvedCity = resolveCityByCoordinates(latitude, longitude);
                return getOrQueryWeather(resolvedCity, null);
            }

            return getWeatherBySearchCity(extractCityName(resolveCityByIp(ip)));
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("WeatherService.getWeather (address={}, lat={}, lon={}, ip={}) took {} ms",
                    address, latitude, longitude, ip, (endTime - startTime));
        }
    }

    private String getRedisKey(String key) {
        return "weather:cache:" + key;
    }

    private WeatherVO getWeatherBySearchCity(String searchCity) {
        log.info("Processing weather query for search term: {}", searchCity);

        String searchKey = getRedisKey(searchCity);
        String cachedJson = redisTemplate.opsForValue().get(searchKey);
        if (hasText(cachedJson)) {
            try {
                WeatherVO weatherVO = objectMapper.readValue(cachedJson, WeatherVO.class);
                if (weatherVO != null) {
                    log.info("Weather cache hit for search term: {}", searchCity);
                    return weatherVO;
                }
            } catch (Exception e) {
                log.warn("Failed to deserialize WeatherVO from Redis key: {}", searchKey, e);
            }
        }

        ResolvedCity resolvedCity = resolveCityBySearchTerm(searchCity);
        return getOrQueryWeather(resolvedCity, searchCity);
    }

    private WeatherVO getOrQueryWeather(ResolvedCity resolvedCity, String aliasKey) {
        String standardKey = getRedisKey(resolvedCity.getStandardCityName());
        String cachedJson = redisTemplate.opsForValue().get(standardKey);
        if (hasText(cachedJson)) {
            try {
                WeatherVO weatherVO = objectMapper.readValue(cachedJson, WeatherVO.class);
                if (weatherVO != null) {
                    cacheAlias(aliasKey, weatherVO);
                    log.info("Weather cache hit for standard city: {}", resolvedCity.getStandardCityName());
                    return weatherVO;
                }
            } catch (Exception e) {
                log.warn("Failed to deserialize WeatherVO from Redis key: {}", standardKey, e);
            }
        }

        WeatherVO weatherVO = queryWeather(resolvedCity);
        try {
            String json = objectMapper.writeValueAsString(weatherVO);
            redisTemplate.opsForValue().set(standardKey, json, 2, TimeUnit.HOURS);
            cacheAlias(aliasKey, weatherVO);
        } catch (Exception e) {
            log.error("Failed to serialize and cache WeatherVO for standard city: {}", resolvedCity.getStandardCityName(), e);
        }
        return weatherVO;
    }

    private void cacheAlias(String aliasKey, WeatherVO weatherVO) {
        if (hasText(aliasKey) && !aliasKey.equals(weatherVO.getCityName())) {
            String aliasKeyStr = getRedisKey(aliasKey);
            try {
                String json = objectMapper.writeValueAsString(weatherVO);
                redisTemplate.opsForValue().set(aliasKeyStr, json, 2, TimeUnit.HOURS);
            } catch (Exception e) {
                log.error("Failed to serialize and cache WeatherVO for alias key: {}", aliasKey, e);
            }
        }
    }

    private ResolvedCity resolveCityBySearchTerm(String searchCity) {
        return queryGeoLocation(searchCity, "search term");
    }

    private ResolvedCity resolveCityByCoordinates(Double latitude, Double longitude) {
        String coordinateLocation = String.format(Locale.US, "%.6f,%.6f", longitude, latitude);
        return queryGeoLocation(coordinateLocation, "coordinates");
    }

    private ResolvedCity queryGeoLocation(String location, String sourceType) {
        try {
            String geoUrl = UriComponentsBuilder.fromHttpUrl("https://" + apiHost + "/geo/v2/city/lookup")
                    .queryParam("location", location)
                    .queryParam("key", apiKey)
                    .build()
                    .toUriString();

            Map<String, Object> geoRes = getMapResponse(geoUrl);
            if (geoRes == null || !"200".equals(geoRes.get("code"))) {
                log.warn("GeoAPI returned non-200 code or null for {}: {}", sourceType, geoRes);
                throw new BusinessException(ErrorCode.PARAMS_ERROR, Constants.Message.GEO_CITY_UNRESOLVABLE + location);
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> locationList = (List<Map<String, Object>>) geoRes.get("location");
            if (locationList == null || locationList.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, Constants.Message.GEO_CITY_NOT_FOUND + location);
            }

            Map<String, Object> matchedCity = locationList.get(0);
            String cityId = (String) matchedCity.get("id");
            String standardCityName = (String) matchedCity.get("name");
            log.info("Resolved {} '{}' to city '{}' (ID: {})", sourceType, location, standardCityName, cityId);
            return new ResolvedCity(standardCityName, cityId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to query GeoAPI for {}: {}", sourceType, location, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, Constants.Message.GEO_RESOLVE_FAILED);
        }
    }

    private WeatherVO queryWeather(ResolvedCity resolvedCity) {
        try {
            String weatherUrl = UriComponentsBuilder.fromHttpUrl("https://" + apiHost + "/v7/weather/3d")
                    .queryParam("location", resolvedCity.getCityId())
                    .queryParam("key", apiKey)
                    .build()
                    .toUriString();

            Map<String, Object> weatherRes = getMapResponse(weatherUrl);
            if (weatherRes == null || !"200".equals(weatherRes.get("code"))) {
                log.warn("Weather API returned non-200 code or null: {}", weatherRes);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, Constants.Message.WEATHER_GET_FAILED);
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> dailyList = (List<Map<String, Object>>) weatherRes.get("daily");
            if (dailyList == null || dailyList.isEmpty()) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, Constants.Message.WEATHER_FORECAST_EMPTY);
            }

            WeatherVO weatherVO = new WeatherVO();
            weatherVO.setCityName(resolvedCity.getStandardCityName());

            Map<String, Object> today = dailyList.get(0);
            weatherVO.setCurrentTemp((String) today.get("tempMax"));
            weatherVO.setCurrentText((String) today.get("textDay"));
            weatherVO.setCurrentIcon((String) today.get("iconDay"));

            List<WeatherVO.DailyForecast> dailyForecasts = new ArrayList<>();
            for (Map<String, Object> dailyMap : dailyList) {
                WeatherVO.DailyForecast df = new WeatherVO.DailyForecast();
                df.setDate((String) dailyMap.get("fxDate"));
                df.setTextDay((String) dailyMap.get("textDay"));
                df.setTextNight((String) dailyMap.get("textNight"));
                df.setTempMin((String) dailyMap.get("tempMin"));
                df.setTempMax((String) dailyMap.get("tempMax"));
                df.setIconDay((String) dailyMap.get("iconDay"));
                dailyForecasts.add(df);
            }
            weatherVO.setDailyForecast(dailyForecasts);
            return weatherVO;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to query Weather 3D API for cityId: {}", resolvedCity.getCityId(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, Constants.Message.WEATHER_SERVICE_FAILED);
        }
    }

    /**
     * 从详细模糊地址中提取行政区划名称 (如 "西湖", "杭州" 等)
     */
    public String extractCityName(String address) {
        if (address == null || address.trim().isEmpty()) {
            return defaultCity;
        }
        address = address.trim();

        int idx = address.lastIndexOf("区");
        if (idx > 0) {
            int start = Math.max(address.lastIndexOf("市"), address.lastIndexOf("省"));
            if (start != -1 && start < idx) {
                return address.substring(start + 1, idx);
            }
            return address.substring(0, idx);
        }
        idx = address.lastIndexOf("县");
        if (idx > 0) {
            int start = Math.max(address.lastIndexOf("市"), address.lastIndexOf("省"));
            if (start != -1 && start < idx) {
                return address.substring(start + 1, idx);
            }
            return address.substring(0, idx);
        }
        idx = address.lastIndexOf("市");
        if (idx > 0) {
            int start = address.lastIndexOf("省");
            if (start != -1 && start < idx) {
                return address.substring(start + 1, idx);
            }
            return address.substring(0, idx);
        }

        return address.length() > 10 ? address.substring(0, 10) : address;
    }

    /**
     * 基于 IP 获取对应城市，内网/回环 IP 默认返回杭州
     */
    private String resolveCityByIp(String ip) {
        if (isPrivateOrLoopbackIp(ip)) {
            log.info("Private or loopback IP '{}' detected. Fallback to default city: {}", ip, defaultCity);
            return defaultCity;
        }

        // 1. 尝试使用默认的 ip-api.com 解析
        try {
            String ipUrl = ipApiUrl + ip + "?lang=zh-CN";
            Map<String, Object> ipRes = getMapResponse(ipUrl);
            if (ipRes != null && "success".equals(ipRes.get("status"))) {
                String city = (String) ipRes.get("city");
                if (city != null && !city.isEmpty()) {
                    log.info("Successfully resolved IP '{}' to city '{}' via ip-api.com", ip, city);
                    return city;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to resolve IP location for {} via ip-api.com, error: {}", ip, e.getMessage());
        }

        // 2. 兜底尝试使用国内高精度的太平洋电脑网 IP 定位接口
        try {
            String pconlineUrl = "https://whois.pconline.com.cn/ipJson.jsp?ip=" + ip + "&json=true";
            byte[] bytes = executeWithRetry(() -> restTemplate.getForObject(pconlineUrl, byte[].class), "请求太平洋IP定位服务");
            if (bytes != null) {
                // 太平洋接口返回的中文字符编码为 GBK，使用 GBK 字符集解码
                String jsonStr = new String(bytes, Charset.forName("GBK")).trim();
                @SuppressWarnings("unchecked")
                Map<String, Object> ipRes = objectMapper.readValue(jsonStr, Map.class);
                if (ipRes != null) {
                    String city = (String) ipRes.get("city");
                    if (city != null && !city.isEmpty()) {
                        log.info("Successfully resolved IP '{}' to city '{}' via PConline", ip, city);
                        return city;
                    }
                    String pro = (String) ipRes.get("pro");
                    if (pro != null && !pro.isEmpty()) {
                        log.info("Successfully resolved IP '{}' to province '{}' via PConline", ip, pro);
                        return pro;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to resolve IP location for {} via PConline, error: {}", ip, e.getMessage());
        }

        return defaultCity;
    }

    private void validateCoordinates(Double latitude, Double longitude) {
        if (latitude == null && longitude == null) {
            return;
        }
        if (latitude == null || longitude == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, Constants.Message.COORDINATES_BOTH_REQUIRED);
        }
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, Constants.Message.COORDINATES_OUT_OF_BOUNDS);
        }
    }

    private boolean isPrivateOrLoopbackIp(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return true;
        }
        ip = ip.trim();
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "localhost".equalsIgnoreCase(ip)) {
            return true;
        }
        if (ip.startsWith("10.") || ip.startsWith("192.168.")) {
            return true;
        }
        if (ip.startsWith("172.")) {
            try {
                String[] parts = ip.split("\\.");
                if (parts.length >= 2) {
                    int second = Integer.parseInt(parts[1]);
                    if (second >= 16 && second <= 31) {
                        return true;
                    }
                }
            } catch (NumberFormatException e) {
                log.debug("Failed to parse IP segment", e);
            }
        }
        return false;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    @FunctionalInterface
    private interface RetryableAction<T> {
        T execute() throws Exception;
    }

    private <T> T executeWithRetry(RetryableAction<T> action, String description) {
        int maxAttempts = 3;
        long backoffMs = 1000L;
        Exception lastException = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return action.execute();
            } catch (Exception e) {
                lastException = e;
                log.warn("Attempt {} failed to {}: {}", attempt, description, e.getMessage());
                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(backoffMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, Constants.Message.NETWORK_RETRY_INTERRUPTED);
                    }
                }
            }
        }
        log.error("All {} attempts failed to {}. Last exception: ", maxAttempts, description, lastException);
        if (lastException instanceof BusinessException) {
            throw (BusinessException) lastException;
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, description + "失败，" + Constants.Message.WEATHER_SERVICE_FAILED);
    }

    /**
     * 发送 HTTP GET 请求并获取 Map 响应，支持 GZIP 压缩解压
     */
    private Map<String, Object> getMapResponse(String url) {
        return executeWithRetry(() -> {
            byte[] bytes = restTemplate.getForObject(url, byte[].class);
            if (bytes == null) {
                return null;
            }
            byte[] decompressed = decompressGzipIfNeeded(bytes);
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = objectMapper.readValue(decompressed, Map.class);
            return responseMap;
        }, "获取地图或天气响应数据");
    }

    /**
     * 如果数据被 GZIP 压缩，则解压数据
     */
    private byte[] decompressGzipIfNeeded(byte[] data) {
        if (data == null || data.length < 2) {
            return data;
        }
        int magic = (data[0] & 0xff) | ((data[1] & 0xff) << 8);
        if (magic == 0x8b1f) {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                 GZIPInputStream gzis = new GZIPInputStream(bais);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = gzis.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                return baos.toByteArray();
            } catch (Exception e) {
                log.warn("Failed to decompress GZIP data, returning raw data", e);
                return data;
            }
        }
        return data;
    }

    private static class ResolvedCity {
        private final String standardCityName;
        private final String cityId;

        private ResolvedCity(String standardCityName, String cityId) {
            this.standardCityName = standardCityName;
            this.cityId = cityId;
        }

        public String getStandardCityName() {
            return standardCityName;
        }

        public String getCityId() {
            return cityId;
        }
    }
}
