package com.weiqiang.personal_crm_backend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改绑定邮箱请求 DTO
 */
@Data
public class EmailChangeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "newEmail cannot be blank")
    @Email(message = "invalid email format")
    private String newEmail;

    @NotBlank(message = "code cannot be blank")
    private String code;
}
