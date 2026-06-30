package com.weiqiang.personal_crm_backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

/**
 * 修改用户名请求 DTO
 */
@Data
public class UsernameChangeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "新用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在 3 到 50 个字符之间")
    private String newUsername;
}
