package com.weiqiang.personal_crm_backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * OpenAI Chat Message 格式对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAiMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String role;    // system, user, assistant
    private String content;
}
