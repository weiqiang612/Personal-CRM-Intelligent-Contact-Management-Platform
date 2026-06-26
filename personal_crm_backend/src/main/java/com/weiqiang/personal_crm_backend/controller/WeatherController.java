package com.weiqiang.personal_crm_backend.controller;

import com.weiqiang.personal_crm_backend.common.Result;
import com.weiqiang.personal_crm_backend.model.vo.WeatherVO;
import com.weiqiang.personal_crm_backend.service.WeatherService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 天气代理接口控制器
 */
@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * 获取天气信息
     *
     * @param address 模糊地址 (可选)
     * @param latitude 浏览器 GEO 纬度 (可选)
     * @param longitude 浏览器 GEO 经度 (可选)
     * @param request HTTP 请求
     * @return 统一响应封装体，含天气 VO 数据
     */
    @GetMapping
    public Result<WeatherVO> getWeather(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            HttpServletRequest request
    ) {
        String clientIp = getClientIp(request);
        log.info("Weather controller query. address: {}, latitude: {}, longitude: {}, clientIp: {}", address, latitude, longitude, clientIp);

        WeatherVO weatherVO = weatherService.getWeather(address, latitude, longitude, clientIp);
        return Result.success(weatherVO);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
