package com.weiqiang.personal_crm_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务用户 ID
     */
    private String userId;

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 密码哈希 (BCrypt)
     */
    private String passwordHash;

    /**
     * 状态: 0 正常, 1 禁用, 2 未验证
     */
    private Integer status;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 邮箱是否验证: false 未验证, true 已验证
     */
    private Boolean emailVerified;

    /**
     * 邮箱验证时间
     */
    private LocalDateTime emailVerifiedAt;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
