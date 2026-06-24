package com.weiqiang.personal_crm_backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * Agent 自然语言写操作执行请求参数
 */
@Data
public class AgentExecuteParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自然语言输入
     */
    @NotBlank(message = "输入内容不能为空")
    private String input;
}
