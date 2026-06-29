package com.weiqiang.personal_crm_backend.common;

/**
 * 全局统一常量池类
 */
public final class Constants {

    private Constants() {
        // 禁止实例化
    }

    // ==========================================
    // Redis 缓存配置相关常量
    // ==========================================
    public static final String REDIS_KEY_AGENT_SESSION = "agent:session:";
    public static final long AGENT_SESSION_TTL_SECONDS = 600L; // 10分钟

    public static final String REDIS_KEY_DASHBOARD_CACHE = "dashboard:cache:";
    public static final long DASHBOARD_CACHE_TTL_SECONDS = 300L; // 5分钟

    // ==========================================
    // 汉化异常提示常量池
    // ==========================================
    public static final class Message {
        private Message() {}

        public static final String USER_NOT_LOGGED_IN = "用户未登录";
        public static final String USER_PROFILE_NOT_FOUND = "未找到用户个人资料";
        public static final String INVALID_CREDENTIALS = "账号或密码错误";
        public static final String ACCOUNT_DISABLED = "该账号已被禁用";
        public static final String REGISTRATION_PARAMS_BLANK = "用户名、密码或验证码不能为空";
        public static final String REGISTRATION_EMAIL_REQUIRED = "注册需要提供电子邮箱";
        public static final String INVALID_EMAIL_FORMAT = "无效的邮箱地址格式";
        public static final String USERNAME_TOO_SHORT = "用户名长度不能少于 3 位";
        public static final String PASSWORD_TOO_SHORT = "密码长度不能少于 8 位";
        public static final String USERNAME_EXISTS = "用户名已存在";
        public static final String REGISTRATION_ID_CONFLICT = "系统 ID 冲突导致注册失败，请重新尝试";
        public static final String INVALID_OLD_PASSWORD = "旧密码错误";

        public static final String CONTACT_NOT_FOUND = "未找到联系人";
        public static final String CONTACT_ACCESS_DENIED = "无权访问：此联系人属于其他用户";
        public static final String BIRTHDAY_FUTURE_INVALID = "生日不能是未来的时间";
        public static final String CONTACT_ALREADY_BLACKLISTED = "该联系人已在黑名单中";
        public static final String CONTACT_ALREADY_ACTIVE = "该联系人已经是活跃状态";

        public static final String TODO_NOT_FOUND = "未找到待办事项";
        public static final String TODO_ACCESS_DENIED = "无权访问：此待办事项属于其他用户";
        public static final String TODO_COMPLETE_CONFLICT = "待办状态冲突：只有待完成状态的事项才能被标记为已完成";
        public static final String TODO_CANCEL_CONFLICT = "待办状态冲突：只有待完成状态的事项才能被标记为已取消";

        public static final String TAG_NAME_EXISTS = "标签名称已存在";
        public static final String TAG_NOT_FOUND = "未找到标签";
        public static final String TAG_ACCESS_DENIED = "无权访问：此标签属于其他用户";
        public static final String INVALID_TAG_ACCESS_DENIED = "无效的标签ID或无权访问";

        public static final String AVATAR_UPLOAD_FAILED = "头像上传失败";
        public static final String AVATAR_PERSIST_FAILED = "保存头像元数据失败";
        public static final String FILE_EMPTY = "上传文件不能为空";
        public static final String FILE_TOO_LARGE = "文件大小不能超过 2MB 限额";
        public static final String FILE_NAME_INVALID = "无效的文件名";
        public static final String FILE_TYPE_UNSUPPORTED = "仅支持 JPG, JPEG, PNG 及 WEBP 格式图片";

        public static final String COORDINATES_BOTH_REQUIRED = "纬度与经度必须同时传入";
        public static final String COORDINATES_OUT_OF_BOUNDS = "GEO 坐标超出有效范围";
        public static final String NETWORK_RETRY_INTERRUPTED = "网络请求重试被中断";
        public static final String GEO_RESOLVE_FAILED = "城市定位解析失败，请稍后重试";
        public static final String GEO_CITY_UNRESOLVABLE = "无法解析该地址对应的城市: ";
        public static final String GEO_CITY_NOT_FOUND = "未找到该城市定位: ";
        public static final String WEATHER_GET_FAILED = "获取天气信息失败";
        public static final String WEATHER_FORECAST_EMPTY = "天气预报数据为空";
        public static final String WEATHER_SERVICE_FAILED = "天气服务调用失败，请稍后重试";
    }
}
