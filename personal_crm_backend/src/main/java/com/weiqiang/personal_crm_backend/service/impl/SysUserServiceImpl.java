package com.weiqiang.personal_crm_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.entity.SysUser;
import com.weiqiang.personal_crm_backend.entity.UserAvatar;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.mapper.SysUserMapper;
import com.weiqiang.personal_crm_backend.mapper.UserAvatarMapper;
import com.weiqiang.personal_crm_backend.model.dto.LoginRequest;
import com.weiqiang.personal_crm_backend.model.vo.LoginVo;
import com.weiqiang.personal_crm_backend.model.vo.UserMeVo;
import com.weiqiang.personal_crm_backend.security.JwtUtils;
import com.weiqiang.personal_crm_backend.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
