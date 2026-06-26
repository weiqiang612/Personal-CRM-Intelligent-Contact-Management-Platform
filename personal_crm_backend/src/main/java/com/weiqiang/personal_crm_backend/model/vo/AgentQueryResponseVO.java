package com.weiqiang.personal_crm_backend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * Agent 查询结果包装响应 VO
 */
@Data
public class AgentQueryResponseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 查询数据类别 ("contact" | "todo" | "unsupported")
     */
    private String queryType;

    /**
     * 意图编码 ("query_contact" | "query_todo" | "unsupported")
     */
    private String intent;

    /**
     * 统一摘要说明
     */
    private String summary;

    /**
     * 结果列表 (可以是 ContactVO 列表或 TodoVO 列表)
     */
    private List<?> results;

    /**
     * 会话 ID
     */
    private String sessionId;

    /**
     * 是否正在澄清信息
     */
    private Boolean isClarifying;

    /**
     * 缺失关键字段
     */
    private List<String> missingFields;

    /**
     * 已解析的槽位参数
     */
    private Map<String, Object> parsedParams;
}

