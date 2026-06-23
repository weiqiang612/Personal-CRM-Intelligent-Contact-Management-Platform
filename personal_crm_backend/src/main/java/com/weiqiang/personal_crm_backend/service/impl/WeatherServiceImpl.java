package com.weiqiang.personal_crm_backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.model.vo.WeatherVO;
import com.weiqiang.personal_crm_backend.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;

/**
 * 天气服务实现类
 */
@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {

    @Value("${weather.api-key:your_qweather_api_key_placeholder}")
    private String apiKey;

    @Value("${weather.api-host:nk3dnacruh.re.qweatherapi.com}")
    private String apiHost;

    @Value("${weather.ip-api-url:http://ip-api.com/json/}")
    private String ipApiUrl;

    private RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 内存缓存 Map
    private final Map<String, CachedWeather> weatherCache = new ConcurrentHashMap<>();

    @Override
    public WeatherVO getWeather(String address, String ip) {
        String targetAddress = address;

        // 1. 如果 address 为空，基于 IP 获取城市
        if (targetAddress == null || targetAddress.trim().isEmpty()) {
            targetAddress = resolveCityByIp(ip);
        }

        // 2. 提取城市/区域的模糊检索词
        String searchCity = extractCityName(targetAddress);
        log.info("Processing weather query for search term: {}", searchCity);

        // 3. 检查缓存是否命中且未过期
        CachedWeather cached = weatherCache.get(searchCity);
        if (cached != null && !cached.isExpired()) {
            log.info("Weather cache hit for search term: {}", searchCity);
            return cached.getWeatherVO();
        }

        // 4. 调用和风天气 GeoAPI 解析具体的城市 ID
        String standardCityName;
        String cityId;
        try {
            String geoUrl = UriComponentsBuilder.fromHttpUrl("https://" + apiHost + "/geo/v2/city/lookup")
                    .queryParam("location", searchCity)
                    .queryParam("key", apiKey)
                    .build().toUriString();

            Map<String, Object> geoRes = getMapResponse(geoUrl);
            if (geoRes == null || !"200".equals(geoRes.get("code"))) {
                log.warn("GeoAPI returned non-200 code or null: {}", geoRes);
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "无法解析该地址对应的城市: " + searchCity);
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> locationList = (List<Map<String, Object>>) geoRes.get("location");
            if (locationList == null || locationList.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "未找到该城市定位: " + searchCity);
            }

            Map<String, Object> matchedCity = locationList.get(0);
            cityId = (String) matchedCity.get("id");
            standardCityName = (String) matchedCity.get("name");
            log.info("Resolved city '{}' to ID '{}'", standardCityName, cityId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to query GeoAPI for location: {}", searchCity, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "城市定位解析失败，请稍后重试");
        }

        // 5. 调用三天天气预报 API 获取数据
        try {
            String weatherUrl = UriComponentsBuilder.fromHttpUrl("https://" + apiHost + "/v7/weather/3d")
                    .queryParam("location", cityId)
                    .queryParam("key", apiKey)
                    .build().toUriString();

            Map<String, Object> weatherRes = getMapResponse(weatherUrl);
            if (weatherRes == null || !"200".equals(weatherRes.get("code"))) {
                log.warn("Weather API returned non-200 code or null: {}", weatherRes);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取天气信息失败");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> dailyList = (List<Map<String, Object>>) weatherRes.get("daily");
            if (dailyList == null || dailyList.isEmpty()) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "天气预报数据为空");
            }

            // 6. 组装 WeatherVO
            WeatherVO weatherVO = new WeatherVO();
            weatherVO.setCityName(standardCityName);

            Map<String, Object> today = dailyList.get(0);
            weatherVO.setCurrentTemp((String) today.get("tempMax")); // 采用今天最高温度作为当前温度的估值
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

            // 7. 存入内存缓存 (有效期 2 小时)
            CachedWeather cachedWeather = new CachedWeather(weatherVO, System.currentTimeMillis());
            weatherCache.put(searchCity, cachedWeather);
            if (!searchCity.equals(standardCityName)) {
                weatherCache.put(standardCityName, cachedWeather);
            }

            return weatherVO;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to query Weather 3D API for cityId: {}", cityId, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "天气服务调用失败，请稍后重试");
        }
    }

    /**
     * 从详细模糊地址中提取行政区划名称 (如 "西湖", "杭州" 等)
     */
    public String extractCityName(String address) {
        if (address == null || address.trim().isEmpty()) {
            return "杭州";
        }
        address = address.trim();

        // 优先匹配“区”或“县”，其次匹配“市”
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

        // 如果没有提取到省市区等后缀，限制检索长度并直接返回原字串
        return address.length() > 10 ? address.substring(0, 10) : address;
    }

    /**
     * 基于 IP 获取对应城市，内网/回环 IP 默认返回杭州
     */
    private String resolveCityByIp(String ip) {
        if (isPrivateOrLoopbackIp(ip)) {
            log.info("Private or loopback IP '{}' detected. Fallback to Hangzhou.", ip);
            return "杭州";
        }

        try {
            String ipUrl = ipApiUrl + ip + "?lang=zh-CN";
            Map<String, Object> ipRes = getMapResponse(ipUrl);
            if (ipRes != null && "success".equals(ipRes.get("status"))) {
                String city = (String) ipRes.get("city");
                if (city != null && !city.isEmpty()) {
                    log.info("Successfully resolved IP '{}' to city '{}'", ip, city);
                    return city;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to resolve IP location for {}, error: {}", ip, e.getMessage());
        }
        return "杭州";
    }

    /**
     * 判断 IP 是否为回环、局域网或内网地址
     */
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

    /**
     * 发送 HTTP GET 请求并获取 Map 响应，支持 GZIP 压缩解压
     */
    private Map<String, Object> getMapResponse(String url) {
        byte[] bytes = restTemplate.getForObject(url, byte[].class);
        if (bytes == null) {
            return null;
        }
        try {
            byte[] decompressed = decompressGzipIfNeeded(bytes);
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = objectMapper.readValue(decompressed, Map.class);
            return responseMap;
        } catch (Exception e) {
            log.error("Failed to parse response from url: {}", url, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "解析响应数据失败");
        }
    }

    /**
     * 如果数据被 GZIP 压缩，则解压数据
     */
    private byte[] decompressGzipIfNeeded(byte[] data) {
        if (data == null || data.length < 2) {
            return data;
        }
        // GZIP 魔数: 0x1f8b (十进制: 31, -117)
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

    /**
     * 天气缓存包装类
     */
    private static class CachedWeather implements Serializable {
        private static final long serialVersionUID = 1L;

        private final WeatherVO weatherVO;
        private final long cacheTime;

        public CachedWeather(WeatherVO weatherVO, long cacheTime) {
            this.weatherVO = weatherVO;
            this.cacheTime = cacheTime;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - cacheTime > 2 * 60 * 60 * 1000L; // 2 小时过期
        }

        public WeatherVO getWeatherVO() {
            return weatherVO;
        }
    }
}
