package com.weiqiang.personal_crm_backend.controller;

import com.weiqiang.personal_crm_backend.common.Result;
import com.weiqiang.personal_crm_backend.model.dto.EmailChangeRequest;
import com.weiqiang.personal_crm_backend.security.UserContext;
import com.weiqiang.personal_crm_backend.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理控制器 (TASK-016)
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final SysUserService sysUserService;

    /**
     * 登录状态下修改绑定邮箱 (TASK-016)
     */
    @PostMapping("/email/change")
    public Result<Void> changeEmail(@Validated @RequestBody EmailChangeRequest request) {
        String currentUserId = UserContext.getUserId();
        sysUserService.changeEmail(currentUserId, request);
        return Result.success("绑定邮箱修改成功", null);
    }
}
