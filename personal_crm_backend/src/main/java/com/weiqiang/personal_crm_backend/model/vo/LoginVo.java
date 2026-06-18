package com.weiqiang.personal_crm_backend.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应展示类
 */
@Data
public class LoginVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserInnerVo user;

    /**
     * 响应中的用户信息封装
     */
    @Data
    public static class UserInnerVo implements Serializable {
        private static final long serialVersionUID = 1L;
        private String userId;
        private String username;
    }
}
