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
     * @param request HTTP 请求
     * @return 统一响应封装体，含天气 VO 数据
     */
    @GetMapping
    public Result<WeatherVO> getWeather(
            @RequestParam(required = false) String address,
            HttpServletRequest request
    ) {
        String clientIp = getClientIp(request);
        log.info("Weather controller query. address: {}, clientIp: {}", address, clientIp);

        WeatherVO weatherVO = weatherService.getWeather(address, clientIp);
        return Result.success(weatherVO);
    }

    /**
     * 解析请求头获取客户端真实 IP 地址
     */
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
        // 处理多级代理，以逗号分隔的第一个 IP 为真实客户端
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
