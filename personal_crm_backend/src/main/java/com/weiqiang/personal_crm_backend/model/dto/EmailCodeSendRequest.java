package com.weiqiang.personal_crm_backend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 发送邮箱验证码请求 DTO
 */
@Data
public class EmailCodeSendRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "email cannot be blank")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "purpose cannot be blank")
    private String purpose;
}
