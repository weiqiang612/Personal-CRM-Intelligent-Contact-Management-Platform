package com.weiqiang.personal_crm_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.model.vo.WeatherVO;
import com.weiqiang.personal_crm_backend.service.impl.WeatherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherServiceImpl weatherService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(weatherService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(weatherService, "apiHost", "nk3dnacruh.re.qweatherapi.com");
        ReflectionTestUtils.setField(weatherService, "ipApiUrl", "http://ip-api.com/json/");
        ReflectionTestUtils.setField(weatherService, "defaultCity", "杭州");
        ReflectionTestUtils.setField(weatherService, "restTemplate", restTemplate);
    }

    @Test
    void testExtractCityName() {
        assertEquals("西湖", weatherService.extractCityName("浙江省杭州市西湖区"));
        assertEquals("杭州", weatherService.extractCityName("浙江省杭州市"));
        assertEquals("杭州", weatherService.extractCityName("杭州"));
        assertEquals("北京", weatherService.extractCityName("北京"));
        assertEquals("武侯", weatherService.extractCityName("四川省成都市武侯区"));
        assertEquals("杭州", weatherService.extractCityName(null));
        assertEquals("杭州", weatherService.extractCityName(" "));
    }

    @Test
    void testGetWeather_WithFuzzyAddress_CacheHit() throws Exception {
        String address = "浙江省杭州市西湖区";
        String cityId = "101210134";
        String standardCityName = "西湖";

        when(restTemplate.getForObject(contains("/geo/v2/city/lookup"), eq(byte[].class)))
                .thenReturn(objectMapper.writeValueAsBytes(buildGeoResponse(cityId, standardCityName)));
        when(restTemplate.getForObject(contains("/v7/weather/3d"), eq(byte[].class)))
                .thenReturn(objectMapper.writeValueAsBytes(buildWeatherResponse("26", "20", "小雨", "中雨", "305")));

        WeatherVO vo1 = weatherService.getWeather(address, null, null, null);
        assertNotNull(vo1);
        assertEquals(standardCityName, vo1.getCityName());
        assertEquals("26", vo1.getCurrentTemp());
        assertEquals("小雨", vo1.getCurrentText());
        assertEquals("305", vo1.getCurrentIcon());
        assertEquals(3, vo1.getDailyForecast().size());
        assertEquals("2026-06-24", vo1.getDailyForecast().get(1).getDate());

        WeatherVO vo2 = weatherService.getWeather(address, null, null, null);
        assertNotNull(vo2);
        assertSame(vo1, vo2);

        WeatherVO vo3 = weatherService.getWeather(standardCityName, null, null, null);
        assertNotNull(vo3);
        assertSame(vo1, vo3);

        verify(restTemplate, times(1)).getForObject(contains("/geo/v2/city/lookup"), eq(byte[].class));
        verify(restTemplate, times(1)).getForObject(contains("/v7/weather/3d"), eq(byte[].class));
    }

    @Test
    void testGetWeather_WithGeoCoordinates_ShouldUseCoordinatesBeforeIp() throws Exception {
        when(restTemplate.getForObject(contains("/geo/v2/city/lookup"), eq(byte[].class)))
                .thenReturn(objectMapper.writeValueAsBytes(buildGeoResponse("101210101", "杭州")));
        when(restTemplate.getForObject(contains("/v7/weather/3d"), eq(byte[].class)))
                .thenReturn(objectMapper.writeValueAsBytes(buildWeatherResponse("27", "22", "小雨", "阴", "305")));

        WeatherVO weatherVO = weatherService.getWeather(null, 30.2741, 120.1551, "127.0.0.1");

        assertNotNull(weatherVO);
        assertEquals("杭州", weatherVO.getCityName());
        assertEquals("27", weatherVO.getCurrentTemp());
        assertEquals("小雨", weatherVO.getCurrentText());
        verify(restTemplate, times(1)).getForObject(contains("/geo/v2/city/lookup"), eq(byte[].class));
        verify(restTemplate, times(1)).getForObject(contains("/v7/weather/3d"), eq(byte[].class));
    }

    @Test
    void testGetWeather_WithInvalidCoordinates_ShouldThrowBusinessException() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> weatherService.getWeather(null, 91.0, 120.1551, "127.0.0.1")
        );

        assertEquals("GEO 坐标超出有效范围", exception.getMessage());
    }

    @Test
    void testGetWeather_WithEmptyAddressAndLocalIp_ShouldFallbackToHangzhou() throws Exception {
        String ip = "127.0.0.1";

        when(restTemplate.getForObject(contains("/geo/v2/city/lookup"), eq(byte[].class)))
                .thenReturn(objectMapper.writeValueAsBytes(buildGeoResponse("101210101", "杭州")));
        when(restTemplate.getForObject(contains("/v7/weather/3d"), eq(byte[].class)))
                .thenReturn(objectMapper.writeValueAsBytes(buildWeatherResponse("28", "22", "晴", "晴", "100")));

        WeatherVO vo = weatherService.getWeather(null, null, null, ip);
        assertNotNull(vo);
        assertEquals("杭州", vo.getCityName());
        assertEquals("28", vo.getCurrentTemp());
        assertEquals("晴", vo.getCurrentText());
    }

    private Map<String, Object> buildGeoResponse(String cityId, String cityName) {
        Map<String, Object> geoRes = new HashMap<>();
        geoRes.put("code", "200");
        List<Map<String, Object>> locationList = new ArrayList<>();
        Map<String, Object> locMap = new HashMap<>();
        locMap.put("id", cityId);
        locMap.put("name", cityName);
        locationList.add(locMap);
        geoRes.put("location", locationList);
        return geoRes;
    }

    private Map<String, Object> buildWeatherResponse(String tempMax, String tempMin, String textDay, String textNight, String iconDay) {
        Map<String, Object> weatherRes = new HashMap<>();
        weatherRes.put("code", "200");
        List<Map<String, Object>> dailyList = new ArrayList<>();

        Map<String, Object> day1 = new HashMap<>();
        day1.put("fxDate", "2026-06-23");
        day1.put("tempMax", tempMax);
        day1.put("tempMin", tempMin);
        day1.put("textDay", textDay);
        day1.put("textNight", textNight);
        day1.put("iconDay", iconDay);
        dailyList.add(day1);

        Map<String, Object> day2 = new HashMap<>();
        day2.put("fxDate", "2026-06-24");
        day2.put("tempMax", "25");
        day2.put("tempMin", "21");
        day2.put("textDay", "中雨");
        day2.put("textNight", "多云");
        day2.put("iconDay", "306");
        dailyList.add(day2);

        Map<String, Object> day3 = new HashMap<>();
        day3.put("fxDate", "2026-06-25");
        day3.put("tempMax", "28");
        day3.put("tempMin", "21");
        day3.put("textDay", "多云");
        day3.put("textNight", "晴");
        day3.put("iconDay", "101");
        dailyList.add(day3);

        weatherRes.put("daily", dailyList);
        return weatherRes;
    }
}
