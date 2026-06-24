package com.weiqiang.personal_crm_backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

/**
 * Agent 写操作二次确认请求参数
 */
@Data
public class AgentConfirmParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志 ID
     */
    @NotNull(message = "日志 ID 不能为空")
    private Long logId;

    /**
     * 确认动作 (confirm: 确认, cancel: 取消)
     */
    @NotBlank(message = "确认动作不能为空")
    @Pattern(regexp = "^(confirm|cancel)$", message = "确认动作只能是 confirm 或 cancel")
    private String action;
}
