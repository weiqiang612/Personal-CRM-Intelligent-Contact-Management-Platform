package com.weiqiang.personal_crm_backend.service.impl;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.weiqiang.personal_crm_backend.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${resend.api-key:}")
    private String apiKey;

    @Value("${resend.from-email:Personal CRM <no-reply@mail.weiqiang.me>}")
    private String fromEmail;

    @Override
    @Async
    public void sendVerificationCode(String toEmail, String purpose, String code) {
        String purposeText = getPurposeText(purpose);
        String subject = "【Personal CRM】" + purposeText + "验证码";
        String htmlContent = buildEmailTemplate(purposeText, code);

        log.info("[EMAIL SERVICE] 准备异步发送验证码邮件至 {}，Purpose: {}, Code: {} (当前线程: {})", 
                toEmail, purpose, code, Thread.currentThread().getName());

        if (apiKey == null || apiKey.isBlank() || "mock-key".equalsIgnoreCase(apiKey)) {
            log.warn("[LOCAL FALLBACK / MOCK KEY] 未配置有效 Resend API Key，本地日志输出验证码: {} -> {}", toEmail, code);
            return;
        }

        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                Resend resend = new Resend(apiKey);
                CreateEmailOptions options = CreateEmailOptions.builder()
                        .from(fromEmail)
                        .to(toEmail)
                        .subject(subject)
                        .html(htmlContent)
                        .build();

                resend.emails().send(options);
                log.info("[EMAIL SERVICE] Resend 验证码邮件发送成功至 {} (尝试次数: {})", toEmail, attempt);
                return;
            } catch (Exception e) {
                log.warn("[EMAIL SERVICE RETRY] 第 {} 次尝试发送邮件至 {} 失败，原因: {}", attempt, toEmail, e.getMessage());
                if (attempt == maxRetries) {
                    log.error("[EMAIL SERVICE FAIL] Resend 发送最终失败, 触发日志兜底打印验证码. toEmail: {}, code: {}, error: {}", 
                            toEmail, code, e.getMessage(), e);
                } else {
                    try {
                        Thread.sleep(2000); // 间隔 2 秒重试
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("[EMAIL SERVICE] 邮件发送重试被中断. error: {}", ie.getMessage());
                        return;
                    }
                }
            }
        }
    }

    private String getPurposeText(String purpose) {
        if ("REGISTER".equalsIgnoreCase(purpose)) {
            return "账号注册激活";
        } else if ("RESET_PASSWORD".equalsIgnoreCase(purpose)) {
            return "重置密码";
        } else if ("CHANGE_EMAIL".equalsIgnoreCase(purpose)) {
            return "修改绑定邮箱";
        }
        return "身份验证";
    }

    private String buildEmailTemplate(String purposeText, String code) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<meta charset=\"UTF-8\">"
                + "<title>验证码</title>"
                + "</head>"
                + "<body style=\"font-family: Arial, sans-serif; background-color: #f4f6f8; margin: 0; padding: 20px;\">"
                + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.05);\">"
                + "<h2 style=\"color: #1a73e8; margin-top: 0;\">Personal CRM 智能联系人管理平台</h2>"
                + "<p style=\"font-size: 16px; color: #333;\">您好！您正在进行 <strong>" + purposeText + "</strong> 操作。</p>"
                + "<p style=\"font-size: 14px; color: #666;\">您的 6 位数字验证码如下（有效期 5 分钟）：</p>"
                + "<div style=\"background-color: #e8f0fe; text-align: center; padding: 15px; border-radius: 6px; margin: 20px 0;\">"
                + "<span style=\"font-size: 32px; font-weight: bold; letter-spacing: 6px; color: #1a73e8;\">" + code + "</span>"
                + "</div>"
                + "<p style=\"font-size: 13px; color: #999;\">若非本人操作，请忽略本邮件。为了您的账号安全，请勿将验证码泄露给他人。</p>"
                + "<hr style=\"border: none; border-top: 1px solid #eee; margin: 20px 0;\">"
                + "<p style=\"font-size: 12px; color: #ccc; text-align: center;\">Personal CRM System &copy; 2026</p>"
                + "</div>"
                + "</body>"
                + "</html>";
    }

    @Override
    @Async
    public void sendWelcomeEmail(String toEmail, String username) {
        String subject = "【Personal CRM】欢迎加入智能联系人管理平台";
        String htmlContent = buildWelcomeEmailTemplate(username);

        log.info("[EMAIL SERVICE] 准备异步发送欢迎邮件至 {}，Username: {} (当前线程: {})", 
                toEmail, username, Thread.currentThread().getName());

        if (apiKey == null || apiKey.isBlank() || "mock-key".equalsIgnoreCase(apiKey)) {
            log.warn("[LOCAL FALLBACK / MOCK KEY] 未配置有效 Resend API Key，本地日志输出欢迎邮件: {} -> {}", toEmail, username);
            return;
        }

        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                Resend resend = new Resend(apiKey);
                CreateEmailOptions options = CreateEmailOptions.builder()
                        .from(fromEmail)
                        .to(toEmail)
                        .subject(subject)
                        .html(htmlContent)
                        .build();

                resend.emails().send(options);
                log.info("[EMAIL SERVICE] Resend 欢迎邮件发送成功至 {} (尝试次数: {})", toEmail, attempt);
                return;
            } catch (Exception e) {
                log.warn("[EMAIL SERVICE RETRY] 第 {} 次尝试发送欢迎邮件至 {} 失败，原因: {}", attempt, toEmail, e.getMessage());
                if (attempt == maxRetries) {
                    log.error("[EMAIL SERVICE FAIL] Resend 发送最终失败. toEmail: {}, username: {}, error: {}", 
                            toEmail, username, e.getMessage(), e);
                } else {
                    try {
                        Thread.sleep(2000); // 间隔 2 秒重试
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("[EMAIL SERVICE] 欢迎邮件发送重试被中断. error: {}", ie.getMessage());
                        return;
                    }
                }
            }
        }
    }

    private String buildWelcomeEmailTemplate(String username) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<meta charset=\"UTF-8\">"
                + "<title>欢迎加入 Personal CRM</title>"
                + "</head>"
                + "<body style=\"font-family: Arial, sans-serif; background-color: #f4f6f8; margin: 0; padding: 20px;\">"
                + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.05);\">"
                + "<h2 style=\"color: #1a73e8; margin-top: 0;\">欢迎加入 Personal CRM 智能联系人管理平台！</h2>"
                + "<p style=\"font-size: 16px; color: #333;\">亲爱的 <strong>" + username + "</strong>，</p>"
                + "<p style=\"font-size: 14px; color: #555; line-height: 1.6;\">"
                + "恭喜您注册成功！Personal CRM 旨在协助您智能地维护与拓展人脉关系。在这里，您可以：<br><br>"
                + "💡 <strong>智能联系人管理</strong>：多维度分类与检索，全方位掌握人脉动向。<br>"
                + "📅 <strong>跟进待办与提醒</strong>：不错过任何一个生日或重要业务跟进时机。<br>"
                + "🤖 <strong>AI 智能决策助手</strong>：提供基于自然语言的高效查询与人脉跟进建议。"
                + "</p>"
                + "<div style=\"text-align: center; margin: 30px 0;\">"
                + "<a href=\"https://crm.weiqiang.me/login\" style=\"background-color: #1a73e8; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 4px; font-weight: bold; font-size: 15px;\">立即登录平台</a>"
                + "</div>"
                + "<p style=\"font-size: 13px; color: #999;\">本邮件由系统自动发出，请勿直接回复。如有疑问，请咨询系统管理员。</p>"
                + "<hr style=\"border: none; border-top: 1px solid #eee; margin: 20px 0;\">"
                + "<p style=\"font-size: 12px; color: #ccc; text-align: center;\">Personal CRM System &copy; 2026</p>"
                + "</div>"
                + "</body>"
                + "</html>";
    }
}
