package com.weiqiang.personal_crm_backend.security;

/**
 * 线程局部上下文，用于存储当前登录用户 ID
 */
public class UserContext {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    private UserContext() {
    }

    /**
     * 设置当前登录用户 ID
     */
    public static void setUserId(String userId) {
        CONTEXT.set(userId);
    }

    /**
     * 获取当前登录用户 ID
     */
    public static String getUserId() {
        return CONTEXT.get();
    }

    /**
     * 清理上下文，避免内存泄漏
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
