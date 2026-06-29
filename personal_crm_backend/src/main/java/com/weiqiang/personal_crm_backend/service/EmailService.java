package com.weiqiang.personal_crm_backend.service;

/**
 * 邮件服务接口
 */
public interface EmailService {

    /**
     * 发送邮箱验证码邮件
     * @param toEmail 接收方邮箱
     * @param purpose 使用目的 (REGISTER, RESET_PASSWORD, CHANGE_EMAIL)
     * @param code 6位验证码
     */
    void sendVerificationCode(String toEmail, String purpose, String code);

    /**
     * 发送注册成功欢迎邮件
     * @param toEmail 接收方邮箱
     * @param username 用户名
     */
    void sendWelcomeEmail(String toEmail, String username);
}
