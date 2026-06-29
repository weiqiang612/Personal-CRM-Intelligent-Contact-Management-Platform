package com.weiqiang.personal_crm_backend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 邮箱验证成功响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailCodeVerifyVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String token;
    private String userId;
    private String username;
    private String email;
}
