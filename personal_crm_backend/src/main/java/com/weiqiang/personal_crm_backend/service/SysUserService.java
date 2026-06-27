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
}
