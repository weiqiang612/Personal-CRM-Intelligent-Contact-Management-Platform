package com.weiqiang.personal_crm_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.entity.SysUser;
import com.weiqiang.personal_crm_backend.entity.UserAvatar;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.mapper.SysUserMapper;
import com.weiqiang.personal_crm_backend.mapper.UserAvatarMapper;
import com.weiqiang.personal_crm_backend.model.dto.LoginRequest;
import com.weiqiang.personal_crm_backend.model.dto.RegisterRequest;
import com.weiqiang.personal_crm_backend.model.vo.LoginVo;
import com.weiqiang.personal_crm_backend.model.vo.UserMeVo;
import com.weiqiang.personal_crm_backend.security.JwtUtils;
import com.weiqiang.personal_crm_backend.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final UserAvatarMapper userAvatarMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public LoginVo login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // 1. 根据用户名查找用户
        SysUser user = this.lambdaQuery().eq(SysUser::getUsername, username).one();
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "invalid account or password");
        }

        // 2. 校验密码是否匹配
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "invalid account or password");
        }

        // 3. 校验账号状态
        if (user.getStatus() != 0) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "this account has been disabled");
        }

        // 4. 生成 JWT Token
        String token = jwtUtils.generateToken(user.getUserId());

        // 5. 组装并返回登录响应数据
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        loginVo.setExpiresIn(jwtUtils.getExpirationSeconds());

        LoginVo.UserInnerVo userInnerVo = new LoginVo.UserInnerVo();
        userInnerVo.setUserId(user.getUserId());
        userInnerVo.setUsername(user.getUsername());
        loginVo.setUser(userInnerVo);

        return loginVo;
    }

    @Override
    public UserMeVo getUserMe(String userId) {
        // 1. 查询用户是否存在
        SysUser user = this.lambdaQuery().eq(SysUser::getUserId, userId).one();
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "user profile not found");
        }

        // 2. 关联查询用户头像信息
        UserAvatar avatar = userAvatarMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserAvatar>()
                        .eq(UserAvatar::getUserId, userId)
        );

        // 3. 组装当前用户信息响应类
        UserMeVo userMeVo = new UserMeVo();
        userMeVo.setUserId(user.getUserId());
        userMeVo.setUsername(user.getUsername());
        userMeVo.setStatus(user.getStatus());
        userMeVo.setAvatarUrl(avatar != null ? avatar.getAccessUrl() : null);

        return userMeVo;
    }

    @Override
    public void register(RegisterRequest registerRequest) {
        // 1. 防御性空白校验
        if (registerRequest == null 
                || !org.springframework.util.StringUtils.hasText(registerRequest.getUsername())
                || !org.springframework.util.StringUtils.hasText(registerRequest.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Username or password cannot be blank");
        }

        String username = registerRequest.getUsername().trim();
        String password = registerRequest.getPassword();

        // 2. 基础校验
        if (username.contains("@")) {
            String emailPattern = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
            if (!username.matches(emailPattern)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Invalid email address format");
            }
        } else {
            if (username.length() < 3) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Username must be at least 3 characters");
            }
        }

        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Password must be at least 8 characters");
        }

        // 3. 重名校验
        SysUser existingUser = this.lambdaQuery().eq(SysUser::getUsername, username).one();
        if (existingUser != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Username already exists");
        }

        // 4. 密码哈希预处理
        String passwordHash = passwordEncoder.encode(password);

        // 5. 唯一约束拦截与重试自愈逻辑 (最大重试5次)
        int maxRetries = 5;
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                String userId = generateUserId();
                SysUser sysUser = new SysUser();
                sysUser.setUserId(userId);
                sysUser.setUsername(username);
                sysUser.setPasswordHash(passwordHash);
                sysUser.setStatus(0);
                sysUser.setCreatedAt(LocalDateTime.now());
                sysUser.setUpdatedAt(LocalDateTime.now());

                this.save(sysUser);
                return; // 保存成功，直接结束
            } catch (org.springframework.dao.DuplicateKeyException e) {
                // 如果是并发下由于用户名被其他线程抢先注册成功引发的唯一键冲突
                SysUser doubleCheckUser = this.lambdaQuery().eq(SysUser::getUsername, username).one();
                if (doubleCheckUser != null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "Username already exists");
                }
                
                // 否则，说明是生成的 userId 撞车，递增重试次数并重新生成重试
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Registration failed due to system ID conflict, please try again");
                }
            }
        }
    }

    /**
     * 并发安全地生成全局唯一用户业务编号 (U000000001格式)
     */
    private synchronized String generateUserId() {
        SysUser latestUser = this.lambdaQuery()
                .orderByDesc(SysUser::getUserId)
                .last("LIMIT 1")
                .one();
        if (latestUser == null) {
            return "U000000001";
        }
        String latestUserId = latestUser.getUserId();
        try {
            int nextNumber = Integer.parseInt(latestUserId.substring(1)) + 1;
            return String.format("U%09d", nextNumber);
        } catch (Exception e) {
            return "U" + (System.currentTimeMillis() % 1000000000L);
        }
    }
}
