package com.weiqiang.personal_crm_backend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 天气信息展示对象
 */
@Data
public class WeatherVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 当前温度
     */
    private String currentTemp;

    /**
     * 当前天气状况描述
     */
    private String currentText;

    /**
     * 当前天气状况图标代码
     */
    private String currentIcon;

    /**
     * 未来三天天气预报
     */
    private List<DailyForecast> dailyForecast;

    /**
     * 单日天气预报对象
     */
    @Data
    public static class DailyForecast implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 预报日期 (格式：yyyy-MM-dd)
         */
        private String date;

        /**
         * 白天天气状况描述
         */
        private String textDay;

        /**
         * 夜间天气状况描述
         */
        private String textNight;

        /**
         * 最低温度
         */
        private String tempMin;

        /**
         * 最高温度
         */
        private String tempMax;

        /**
         * 白天天气状况图标代码
         */
        private String iconDay;
    }
}
