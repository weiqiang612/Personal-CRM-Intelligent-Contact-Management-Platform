package com.weiqiang.personal_crm_backend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Agent 写操作预确认响应对象
 */
@Data
public class AgentExecuteResponseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作日志 ID
     */
    private Long logId;

    /**
     * 是否需要确认 (0 否，1 是)
     */
    private Integer needConfirm;

    /**
     * 动作类型
     */
    private String actionType;

    /**
     * 摘要文本描述
     */
    private String summary;

    /**
     * 解析参数映射
     */
    private Map<String, Object> parsedParams;
}
