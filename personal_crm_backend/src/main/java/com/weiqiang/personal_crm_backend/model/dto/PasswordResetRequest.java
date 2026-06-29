package com.weiqiang.personal_crm_backend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 重置密码请求 DTO
 */
@Data
public class PasswordResetRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "email cannot be blank")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "code cannot be blank")
    private String code;

    @NotBlank(message = "newPassword cannot be blank")
    @Size(min = 8, message = "password must be at least 8 characters")
    private String newPassword;
}
