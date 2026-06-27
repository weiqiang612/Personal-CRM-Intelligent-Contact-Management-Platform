package com.weiqiang.personal_crm_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 联系人实体类
 */
@Data
@TableName("contact")
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户业务编号
     */
    @TableField("user_id")
    private String userId;

    /**
     * 联系人业务编号 (唯一)
     */
    @TableField("ct_id")
    private String ctId;

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
     * 逻辑删除标识 (0 正常, 1 已删除)
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
