package com.weiqiang.personal_crm_backend.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 当前用户信息展示类
 */
@Data
public class UserMeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String username;
    private Integer status;
    private String avatarUrl;
    private String email;
    private String phone;
}
