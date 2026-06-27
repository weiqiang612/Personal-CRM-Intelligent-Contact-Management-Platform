package com.weiqiang.personal_crm_backend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改邮箱请求 DTO
 */
@Data
public class UpdateEmailRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "email cannot be blank")
    @Email(message = "invalid email format")
    private String email;
}
