package com.weiqiang.personal_crm_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weiqiang.personal_crm_backend.entity.SysUser;
import com.weiqiang.personal_crm_backend.model.dto.LoginRequest;
import com.weiqiang.personal_crm_backend.model.vo.LoginVo;
import com.weiqiang.personal_crm_backend.model.vo.UserMeVo;

import com.weiqiang.personal_crm_backend.model.dto.RegisterRequest;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户登录并返回 Token
     */
    LoginVo login(LoginRequest loginRequest);

    /**
     * 获取当前登录用户信息
     */
    UserMeVo getUserMe(String userId);

    /**
     * 用户注册
     */
    void register(RegisterRequest registerRequest);

    /**
     * 修改用户邮箱
     */
    void updateEmail(String userId, String email);

    /**
     * 修改用户手机号
     */
    void updatePhone(String userId, String phone);

    /**
     * 修改用户密码
     */
    void updatePassword(String userId, String oldPassword, String newPassword);

    /**
     * 发送邮箱验证码
     */
    void sendEmailCode(com.weiqiang.personal_crm_backend.model.dto.EmailCodeSendRequest request);

    /**
     * 校验邮箱验证码并激活
     */
    com.weiqiang.personal_crm_backend.model.vo.EmailCodeVerifyVo verifyEmailCode(com.weiqiang.personal_crm_backend.model.dto.EmailCodeVerifyRequest request);

    /**
     * 忘记密码重置
     */
    void resetPassword(com.weiqiang.personal_crm_backend.model.dto.PasswordResetRequest request);

    /**
     * 已登录更换绑定邮箱
     */
    void changeEmail(String currentUserId, com.weiqiang.personal_crm_backend.model.dto.EmailChangeRequest request);
}
