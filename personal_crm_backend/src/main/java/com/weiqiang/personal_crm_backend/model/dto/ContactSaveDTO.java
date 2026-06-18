package com.weiqiang.personal_crm_backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 创建/修改联系人请求对象
 */
@Data
public class ContactSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 姓名 (必填)
     */
    @NotBlank(message = "name cannot be blank")
    private String name;

    /**
     * 地址 (选填)
     */
    private String address;

    /**
     * 邮编 (选填)
     */
    private String postcode;

    /**
     * QQ 号码 (选填)
     */
    private String qq;

    /**
     * 微信 (选填)
     */
    private String wechat;

    /**
     * 电子邮箱 (选填)
     */
    private String email;

    /**
     * 性别 (0 未知, 1 男, 2 女)
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 电话 (必填)
     */
    @NotBlank(message = "phone cannot be blank")
    @Pattern(regexp = "^[0-9+\\-()\\s]+$", message = "invalid phone format")
    private String phone;
}
