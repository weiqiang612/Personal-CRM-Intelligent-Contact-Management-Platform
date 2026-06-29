package com.weiqiang.personal_crm_backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiqiang.personal_crm_backend.common.Constants;
import com.weiqiang.personal_crm_backend.model.dto.AgentSessionState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Agent 会话内存管理器，支持防越权检验和 Redis TTL 自动清理
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AgentSessionManager {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private String getRedisKey(String sessionId) {
        return Constants.REDIS_KEY_AGENT_SESSION + sessionId;
    }

    /**
     * 获取会话状态，自带越权校验
     */
    public AgentSessionState getSession(String sessionId, String userId) {
        if (sessionId == null) {
            return null;
        }
        String key = getRedisKey(sessionId);
        String json = redisTemplate.opsForValue().get(key);
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        try {
            AgentSessionState state = objectMapper.readValue(json, AgentSessionState.class);
            if (state != null) {
                // 防越权：如果会话所有者非当前登录用户，则拒绝访问
                if (!state.getUserId().equals(userId)) {
                    return null;
                }
                // 更新活跃时间并重设 TTL
                state.setLastAccessTime(System.currentTimeMillis());
                String updatedJson = objectMapper.writeValueAsString(state);
                redisTemplate.opsForValue().set(key, updatedJson, Constants.AGENT_SESSION_TTL_SECONDS, TimeUnit.SECONDS);
                return state;
            }
        } catch (Exception e) {
            log.error("Failed to deserialize or update AgentSessionState for sessionId: {}", sessionId, e);
        }
        return null;
    }

    /**
     * 为指定用户开启全新会话
     */
    public AgentSessionState createSession(String userId) {
        String sessionId = UUID.randomUUID().toString();
        AgentSessionState state = new AgentSessionState();
        state.setSessionId(sessionId);
        state.setUserId(userId);
        state.setLastAccessTime(System.currentTimeMillis());

        String key = getRedisKey(sessionId);
        try {
            String json = objectMapper.writeValueAsString(state);
            redisTemplate.opsForValue().set(key, json, Constants.AGENT_SESSION_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to serialize and save AgentSessionState for userId: {}", userId, e);
        }
        return state;
    }

    /**
     * 更新或保存现有的会话状态到 Redis
     */
    public void saveSession(AgentSessionState state) {
        if (state == null || state.getSessionId() == null) {
            return;
        }
        String key = getRedisKey(state.getSessionId());
        try {
            state.setLastAccessTime(System.currentTimeMillis());
            String json = objectMapper.writeValueAsString(state);
            redisTemplate.opsForValue().set(key, json, Constants.AGENT_SESSION_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to save/update AgentSessionState for sessionId: {}", state.getSessionId(), e);
        }
    }

    /**
     * 主动销毁会话 (如已二次确认/取消或新建会话)
     */
    public void removeSession(String sessionId) {
        if (sessionId != null) {
            String key = getRedisKey(sessionId);
            redisTemplate.delete(key);
        }
    }
}
