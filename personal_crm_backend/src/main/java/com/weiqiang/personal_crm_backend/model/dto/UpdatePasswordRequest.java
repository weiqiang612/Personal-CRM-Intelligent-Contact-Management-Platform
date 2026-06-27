package com.weiqiang.personal_crm_backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改密码请求 DTO
 */
@Data
public class UpdatePasswordRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "oldPassword cannot be blank")
    private String oldPassword;

    @NotBlank(message = "newPassword cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;
}
