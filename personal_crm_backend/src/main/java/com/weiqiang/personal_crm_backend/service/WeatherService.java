package com.weiqiang.personal_crm_backend.service;

import com.weiqiang.personal_crm_backend.model.vo.WeatherVO;

/**
 * 天气服务接口
 */
public interface WeatherService {

    /**
     * 根据模糊地址或客户端 IP 获取天气信息 (含 2 小时缓存和地名模糊解析)
     *
     * @param address 模糊地址 (可选)
     * @param ip      客户端 IP 地址 (可选，当 address 为空时用于定位)
     * @return 天气信息
     */
    WeatherVO getWeather(String address, String ip);
}
