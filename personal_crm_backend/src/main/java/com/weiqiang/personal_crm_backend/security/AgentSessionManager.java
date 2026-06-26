package com.weiqiang.personal_crm_backend.security;

import com.weiqiang.personal_crm_backend.model.dto.AgentSessionState;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Agent 会话内存管理器，支持防越权检验和定时过期清理
 */
@Component
@EnableScheduling
public class AgentSessionManager {

    private final Map<String, AgentSessionState> sessions = new ConcurrentHashMap<>();

    /**
     * 获取会话状态，自带越权校验
     */
    public AgentSessionState getSession(String sessionId, String userId) {
        if (sessionId == null) {
            return null;
        }
        AgentSessionState state = sessions.get(sessionId);
        if (state != null) {
            // 防越权：如果会话所有者非当前登录用户，则拒绝访问
            if (!state.getUserId().equals(userId)) {
                return null;
            }
            // 更新活跃时间
            state.setLastAccessTime(System.currentTimeMillis());
        }
        return state;
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
        sessions.put(sessionId, state);
        return state;
    }

    /**
     * 主动销毁会话 (如已二次确认/取消或新建会话)
     */
    public void removeSession(String sessionId) {
        if (sessionId != null) {
            sessions.remove(sessionId);
        }
    }

    /**
     * 定时调度：每隔 60 秒运行一次，清理 10 分钟内未活跃的会话
     */
    @Scheduled(fixedRate = 60000)
    public void cleanExpiredSessions() {
        long now = System.currentTimeMillis();
        // 600,000ms = 10分钟
        sessions.entrySet().removeIf(entry -> now - entry.getValue().getLastAccessTime() > 600000);
    }
}
