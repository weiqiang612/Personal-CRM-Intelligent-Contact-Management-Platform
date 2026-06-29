package com.weiqiang.personal_crm_backend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 校验邮箱验证码请求 DTO
 */
@Data
public class EmailCodeVerifyRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "email cannot be blank")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "code cannot be blank")
    private String code;

    @NotBlank(message = "purpose cannot be blank")
    private String purpose;
}
