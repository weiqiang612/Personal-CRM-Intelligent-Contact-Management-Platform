package com.weiqiang.personal_crm_backend.service;

import com.weiqiang.personal_crm_backend.model.vo.WeatherVO;

/**
 * 天气服务接口
 */
public interface WeatherService {

    /**
     * 根据模糊地址、浏览器 GEO 坐标或客户端 IP 获取天气信息。
     *
     * 优先级：address > latitude/longitude > ip > 杭州。
     *
     * @param address   模糊地址 (可选)
     * @param latitude  浏览器 GEO 纬度 (可选)
     * @param longitude 浏览器 GEO 经度 (可选)
     * @param ip        客户端 IP 地址 (可选，当 address 与 GEO 坐标为空时用于定位)
     * @return 天气信息
     */
    WeatherVO getWeather(String address, Double latitude, Double longitude, String ip);
}
