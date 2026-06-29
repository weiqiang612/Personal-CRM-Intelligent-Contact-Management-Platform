package com.weiqiang.personal_crm_backend.component;

import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 邮箱验证码 Redis 存储与防爆破管理组件
 */
@Slf4j
@Component
public class EmailVerificationRedisTemplate {

    private final StringRedisTemplate redisTemplate;

    public EmailVerificationRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final String KEY_PREFIX_CODE = "email:code:";
    private static final String KEY_PREFIX_SEND = "email:send:";
    private static final String KEY_PREFIX_ATTEMPT = "email:attempt:";

    private static final long CODE_TTL_SECONDS = 300; // 5分钟
    private static final long SEND_LOCK_TTL_SECONDS = 60; // 60秒
    private static final long ATTEMPT_TTL_SECONDS = 300; // 5分钟
    private static final int MAX_ATTEMPTS = 5;

    /**
     * 检查并加锁发送频率
     * @return true 如果可以发送（即成功加上频率锁），false 表示频繁发送
     */
    public boolean checkAndSetSendLock(String purpose, String email) {
        String lockKey = KEY_PREFIX_SEND + purpose + ":" + email;
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", SEND_LOCK_TTL_SECONDS, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 存储验证码并重置错误尝试次数
     */
    public void saveCode(String purpose, String email, String code) {
        String codeKey = KEY_PREFIX_CODE + purpose + ":" + email;
        String attemptKey = KEY_PREFIX_ATTEMPT + purpose + ":" + email;

        redisTemplate.opsForValue().set(codeKey, code, CODE_TTL_SECONDS, TimeUnit.SECONDS);
        redisTemplate.delete(attemptKey);
    }

    /**
     * 校验验证码
     * @return true 校验成功
     */
    public boolean verifyCode(String purpose, String email, String inputCode) {
        return verifyCode(purpose, email, inputCode, true);
    }

    /**
     * 校验验证码，可选择成功后是否立即删除
     * @return true 校验成功
     */
    public boolean verifyCode(String purpose, String email, String inputCode, boolean deleteOnSuccess) {
        String codeKey = KEY_PREFIX_CODE + purpose + ":" + email;
        String attemptKey = KEY_PREFIX_ATTEMPT + purpose + ":" + email;

        String savedCode = redisTemplate.opsForValue().get(codeKey);
        if (savedCode == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "验证码不存在或已过期");
        }

        // 检查当前错误次数
        String attemptStr = redisTemplate.opsForValue().get(attemptKey);
        int currentAttempts = attemptStr == null ? 0 : Integer.parseInt(attemptStr);
        if (currentAttempts >= MAX_ATTEMPTS) {
            redisTemplate.delete(codeKey);
            redisTemplate.delete(attemptKey);
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "验证码尝试次数过多已被锁定作废，请重新获取");
        }

        // 校验比对
        if (!savedCode.equalsIgnoreCase(inputCode.trim())) {
            Long newAttempts = redisTemplate.opsForValue().increment(attemptKey);
            if (newAttempts != null && newAttempts == 1) {
                redisTemplate.expire(attemptKey, ATTEMPT_TTL_SECONDS, TimeUnit.SECONDS);
            }
            if (newAttempts != null && newAttempts >= MAX_ATTEMPTS) {
                redisTemplate.delete(codeKey);
                redisTemplate.delete(attemptKey);
                throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "验证码输入错误达到5次，已被锁定作废，请重新获取");
            }
            int remaining = MAX_ATTEMPTS - (newAttempts != null ? newAttempts.intValue() : 1);
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "验证码错误，还可尝试 " + remaining + " 次");
        }

        // 校验成功，清除验证码（可选）与错误计数
        if (deleteOnSuccess) {
            redisTemplate.delete(codeKey);
        }
        redisTemplate.delete(attemptKey);
        return true;
    }

    /**
     * 清除频率锁（特定场景使用）
     */
    public void clearSendLock(String purpose, String email) {
        String lockKey = KEY_PREFIX_SEND + purpose + ":" + email;
        redisTemplate.delete(lockKey);
    }
}
