package com.weiqiang.personal_crm_backend.controller;

import com.weiqiang.personal_crm_backend.common.Result;
import com.weiqiang.personal_crm_backend.model.dto.LoginRequest;
import com.weiqiang.personal_crm_backend.model.vo.LoginVo;
import com.weiqiang.personal_crm_backend.model.vo.UserMeVo;
import com.weiqiang.personal_crm_backend.security.UserContext;
import com.weiqiang.personal_crm_backend.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.weiqiang.personal_crm_backend.model.dto.RegisterRequest;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVo> login(@Validated @RequestBody LoginRequest loginRequest) {
        LoginVo loginVo = sysUserService.login(loginRequest);
        return Result.success(loginVo);
    }

    /**
     * 获取当前登录用户个人信息
     */
    @GetMapping("/me")
    public Result<UserMeVo> getMe() {
        String userId = UserContext.getUserId();
        UserMeVo userMeVo = sysUserService.getUserMe(userId);
        return Result.success(userMeVo);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterRequest registerRequest) {
        sysUserService.register(registerRequest);
        return Result.success();
    }
}
