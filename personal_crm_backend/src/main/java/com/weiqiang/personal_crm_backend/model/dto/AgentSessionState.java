package com.weiqiang.personal_crm_backend.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Agent 会话状态缓存对象
 */
@Data
public class AgentSessionState implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话 ID
     */
    private String sessionId;

    /**
     * 用户 ID (隔离防越权)
     */
    private String userId;

    /**
     * 历史聊天记录
     */
    private List<OpenAiMessage> messages = new ArrayList<>();

    /**
     * 累计提取到的槽位参数
     */
    private Map<String, Object> accumulatedParams = new HashMap<>();

    /**
     * 最近一次访问时间戳 (用于过期清理)
     */
    private long lastAccessTime;
}
