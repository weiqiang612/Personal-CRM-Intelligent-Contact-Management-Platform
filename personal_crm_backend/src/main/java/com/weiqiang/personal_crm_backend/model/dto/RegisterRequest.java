package com.weiqiang.personal_crm_backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 注册请求 DTO
 */
@Data
public class RegisterRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "username cannot be blank")
    private String username;

    private String email;

    @NotBlank(message = "password cannot be blank")
    private String password;

    @NotBlank(message = "verification code cannot be blank")
    private String code;
}
