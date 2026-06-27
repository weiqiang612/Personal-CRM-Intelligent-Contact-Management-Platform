package com.weiqiang.personal_crm_backend.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 联系人详情展示类
 */
@Data
public class ContactVO implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 联系人业务编号 (唯一)
     */
    private String contactId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 地址
     */
    private String address;

    /**
     * 邮编
     */
    private String postcode;

    /**
     * QQ 号码
     */
    private String qq;

    /**
     * 微信
     */
    private String wechat;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 性别 (0 未知, 1 男, 2 女)
     */
    private Integer gender;

    /**
     * 生日
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 电话
     */
    private String phone;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 状态 (0 正常, 1 黑名单)
     */
    private Integer status;

    /**
     * 头像访问路径
     */
    private String avatarUrl;

    /**
     * 联系人关联的标签列表
     */
    private List<String> tags;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
