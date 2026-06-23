package com.weiqiang.personal_crm_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Agent 操作审计日志实体类
 */
@Data
@TableName("agent_operation_log")
public class AgentOperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户业务编号
     */
    @TableField("user_id")
    private String userId;

    /**
     * 原始用户输入
     */
    @TableField("user_input")
    private String userInput;

    /**
     * 识别意图
     */
    private String intent;

    /**
     * 解析参数 (JSON 字符串)
     */
    @TableField("parsed_params")
    private String parsedParams;

    /**
     * 是否需要确认 (0 否，1 是)
     */
    @TableField("need_confirm")
    private Integer needConfirm;

    /**
     * 是否已确认 (0 否，1 是)
     */
    private Integer confirmed;

    /**
     * 动作类型
     */
    @TableField("action_type")
    private String actionType;

    /**
     * 执行状态 (0 待执行/挂起, 1 成功, 2 失败, 3 已取消)
     */
    @TableField("execution_status")
    private Integer executionStatus;

    /**
     * 执行结果 payload (JSON 字符串)
     */
    @TableField("execution_result")
    private String executionResult;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}
