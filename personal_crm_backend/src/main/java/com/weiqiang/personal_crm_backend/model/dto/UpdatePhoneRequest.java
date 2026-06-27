package com.weiqiang.personal_crm_backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改手机请求 DTO
 */
@Data
public class UpdatePhoneRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "phone cannot be blank")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "invalid phone number format")
    private String phone;
}
