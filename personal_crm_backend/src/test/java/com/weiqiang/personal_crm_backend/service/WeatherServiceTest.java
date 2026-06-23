package com.weiqiang.personal_crm_backend.service;

import com.weiqiang.personal_crm_backend.model.vo.WeatherVO;
import com.weiqiang.personal_crm_backend.service.impl.WeatherServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

        // Mock GeoAPI Response
        Map<String, Object> geoRes = new HashMap<>();
        geoRes.put("code", "200");
        List<Map<String, Object>> locationList = new ArrayList<>();
        Map<String, Object> locMap = new HashMap<>();
        locMap.put("id", cityId);
        locMap.put("name", standardCityName);
        locationList.add(locMap);
        geoRes.put("location", locationList);

        // Mock Weather API Response
        Map<String, Object> weatherRes = new HashMap<>();
        weatherRes.put("code", "200");
        List<Map<String, Object>> dailyList = new ArrayList<>();
        
        Map<String, Object> day1 = new HashMap<>();
        day1.put("fxDate", "2026-06-23");
        day1.put("tempMax", "26");
        day1.put("tempMin", "20");
        day1.put("textDay", "小雨");
        day1.put("textNight", "中雨");
        day1.put("iconDay", "305");
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

        // Mock restTemplate calls
        when(restTemplate.getForObject(contains("/geo/v2/city/lookup"), eq(byte[].class)))
                .thenReturn(objectMapper.writeValueAsBytes(geoRes));
        when(restTemplate.getForObject(contains("/v7/weather/3d"), eq(byte[].class)))
                .thenReturn(objectMapper.writeValueAsBytes(weatherRes));

        // 1. First call - should trigger network requests
        WeatherVO vo1 = weatherService.getWeather(address, null);
        assertNotNull(vo1);
        assertEquals(standardCityName, vo1.getCityName());
        assertEquals("26", vo1.getCurrentTemp());
        assertEquals("小雨", vo1.getCurrentText());
        assertEquals("305", vo1.getCurrentIcon());
        assertEquals(3, vo1.getDailyForecast().size());
        assertEquals("2026-06-24", vo1.getDailyForecast().get(1).getDate());

        // 2. Second call with same address - should hit cache
        WeatherVO vo2 = weatherService.getWeather(address, null);
        assertNotNull(vo2);
        assertSame(vo1, vo2); // must refer to the exact same instance in cache

        // 3. Third call with standard city name - should also hit cache
        WeatherVO vo3 = weatherService.getWeather(standardCityName, null);
        assertNotNull(vo3);
        assertSame(vo1, vo3);

        // Verify restTemplate was called exactly once for lookup and once for weather
        verify(restTemplate, times(1)).getForObject(contains("/geo/v2/city/lookup"), eq(byte[].class));
        verify(restTemplate, times(1)).getForObject(contains("/v7/weather/3d"), eq(byte[].class));
    }

    @Test
    void testGetWeather_WithEmptyAddressAndLocalIp_ShouldFallbackToHangzhou() throws Exception {
        // Local IP
        String ip = "127.0.0.1";
        
        // Mock lookup and weather calls for Hangzhou
        Map<String, Object> geoRes = new HashMap<>();
        geoRes.put("code", "200");
        List<Map<String, Object>> locationList = new ArrayList<>();
        Map<String, Object> locMap = new HashMap<>();
        locMap.put("id", "101210101");
        locMap.put("name", "杭州");
        locationList.add(locMap);
        geoRes.put("location", locationList);

        Map<String, Object> weatherRes = new HashMap<>();
        weatherRes.put("code", "200");
        List<Map<String, Object>> dailyList = new ArrayList<>();
        Map<String, Object> day1 = new HashMap<>();
        day1.put("fxDate", "2026-06-23");
        day1.put("tempMax", "28");
        day1.put("tempMin", "22");
        day1.put("textDay", "晴");
        day1.put("textNight", "晴");
        day1.put("iconDay", "100");
        dailyList.add(day1);
        weatherRes.put("daily", dailyList);

        when(restTemplate.getForObject(contains("/geo/v2/city/lookup"), eq(byte[].class)))
                .thenReturn(objectMapper.writeValueAsBytes(geoRes));
        when(restTemplate.getForObject(contains("/v7/weather/3d"), eq(byte[].class)))
                .thenReturn(objectMapper.writeValueAsBytes(weatherRes));

        WeatherVO vo = weatherService.getWeather(null, ip);
        assertNotNull(vo);
        assertEquals("杭州", vo.getCityName());
        assertEquals("28", vo.getCurrentTemp());
        assertEquals("晴", vo.getCurrentText());
    }
}
