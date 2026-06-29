package com.weiqiang.personal_crm_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weiqiang.personal_crm_backend.common.Constants;
import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.entity.SysUser;
import com.weiqiang.personal_crm_backend.entity.UserAvatar;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.mapper.SysUserMapper;
import com.weiqiang.personal_crm_backend.mapper.UserAvatarMapper;
import com.weiqiang.personal_crm_backend.model.dto.*;
import com.weiqiang.personal_crm_backend.model.vo.LoginVo;
import com.weiqiang.personal_crm_backend.model.vo.UserMeVo;
import com.weiqiang.personal_crm_backend.model.vo.EmailCodeVerifyVo;
import com.weiqiang.personal_crm_backend.security.JwtUtils;
import com.weiqiang.personal_crm_backend.service.SysUserService;
import com.weiqiang.personal_crm_backend.component.EmailVerificationRedisTemplate;
import com.weiqiang.personal_crm_backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final UserAvatarMapper userAvatarMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AvatarAccessService avatarAccessService;
    private final EmailVerificationRedisTemplate emailVerificationRedisTemplate;
    private final EmailService emailService;

    @Override
    public LoginVo login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // 1. 根据用户名或邮箱查找用户
        SysUser user = this.lambdaQuery()
                .eq(SysUser::getUsername, username)
                .or()
                .eq(SysUser::getEmail, username)
                .one();
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, Constants.Message.INVALID_CREDENTIALS);
        }

        // 2. 校验密码是否匹配
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, Constants.Message.INVALID_CREDENTIALS);
        }

        // 3. 校验账号状态
        if (Integer.valueOf(2).equals(user.getStatus()) || Boolean.FALSE.equals(user.getEmailVerified())) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "账号未完成邮箱验证，请先验证邮箱");
        }
        if (user.getStatus() != 0) {
            throw new BusinessException(ErrorCode.FORBIDDEN, Constants.Message.ACCOUNT_DISABLED);
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
            throw new BusinessException(ErrorCode.NOT_FOUND, Constants.Message.USER_PROFILE_NOT_FOUND);
        }

        // 2. 关联查询用户头像信息
        UserAvatar avatar = userAvatarMapper.selectOne(
                new LambdaQueryWrapper<UserAvatar>()
                        .eq(UserAvatar::getUserId, userId)
        );

        // 3. 组装当前用户信息响应类
        UserMeVo userMeVo = new UserMeVo();
        userMeVo.setUserId(user.getUserId());
        userMeVo.setUsername(user.getUsername());
        userMeVo.setStatus(user.getStatus());
        userMeVo.setAvatarUrl(avatarAccessService.resolveUserAvatarUrl(avatar));
        userMeVo.setEmail(user.getEmail());
        userMeVo.setPhone(user.getPhone());

        return userMeVo;
    }

    @Override
    public void register(RegisterRequest registerRequest) {
        // 1. 防御性空白校验
        if (registerRequest == null 
                || !StringUtils.hasText(registerRequest.getUsername())
                || !StringUtils.hasText(registerRequest.getPassword())
                || !StringUtils.hasText(registerRequest.getCode())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, Constants.Message.REGISTRATION_PARAMS_BLANK);
        }

        String username = registerRequest.getUsername().trim();
        String password = registerRequest.getPassword();
        String email = registerRequest.getEmail();
        if (!StringUtils.hasText(email)) {
            if (username.contains("@")) {
                email = username;
            }
        }

        // 1.5 邮箱必须提供校验
        if (!StringUtils.hasText(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, Constants.Message.REGISTRATION_EMAIL_REQUIRED);
        }

        // 2. 基础校验
        if (username.contains("@")) {
            String emailPattern = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
            if (!username.matches(emailPattern)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, Constants.Message.INVALID_EMAIL_FORMAT);
            }
        } else {
            if (username.length() < 3) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, Constants.Message.USERNAME_TOO_SHORT);
            }
        }

        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, Constants.Message.PASSWORD_TOO_SHORT);
        }

        // 验证邮箱验证码是否正确
        emailVerificationRedisTemplate.verifyCode("REGISTER", email.trim(), registerRequest.getCode().trim());

        // 3. 重名校验
        SysUser existingUser = this.lambdaQuery().eq(SysUser::getUsername, username).one();
        if (existingUser != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, Constants.Message.USERNAME_EXISTS);
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
                sysUser.setEmail(email);
                sysUser.setPasswordHash(passwordHash);
                sysUser.setStatus(0); // ACTIVE
                sysUser.setEmailVerified(true);
                sysUser.setEmailVerifiedAt(LocalDateTime.now());
                sysUser.setCreatedAt(LocalDateTime.now());
                sysUser.setUpdatedAt(LocalDateTime.now());

                this.save(sysUser);
                // 异步发送欢迎邮件
                try {
                    emailService.sendWelcomeEmail(email, username);
                } catch (Exception ex) {
                    log.error("[REGISTER] 发送欢迎邮件失败, user: {}", username, ex);
                }
                return; // 保存成功，直接结束
            } catch (org.springframework.dao.DuplicateKeyException e) {
                // 如果是并发下由于用户名被其他线程抢先注册成功引发的唯一键冲突
                SysUser doubleCheckUser = this.lambdaQuery().eq(SysUser::getUsername, username).one();
                if (doubleCheckUser != null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, Constants.Message.USERNAME_EXISTS);
                }
                
                // 否则，说明是生成的 userId 撞车，递增重试次数并重新生成重试
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, Constants.Message.REGISTRATION_ID_CONFLICT);
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

    @Override
    public void updateEmail(String userId, String email) {
        SysUser user = this.lambdaQuery().eq(SysUser::getUserId, userId).one();
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, Constants.Message.USER_PROFILE_NOT_FOUND);
        }
        user.setEmail(email);
        user.setUpdatedAt(LocalDateTime.now());
        this.updateById(user);
    }

    @Override
    public void updatePhone(String userId, String phone) {
        SysUser user = this.lambdaQuery().eq(SysUser::getUserId, userId).one();
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, Constants.Message.USER_PROFILE_NOT_FOUND);
        }
        user.setPhone(phone);
        user.setUpdatedAt(LocalDateTime.now());
        this.updateById(user);
    }

    @Override
    public void updatePassword(String userId, String oldPassword, String newPassword) {
        SysUser user = this.lambdaQuery().eq(SysUser::getUserId, userId).one();
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, Constants.Message.USER_PROFILE_NOT_FOUND);
        }
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, Constants.Message.INVALID_OLD_PASSWORD);
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        this.updateById(user);
    }

    @Override
    public void sendEmailCode(EmailCodeSendRequest request) {
        String email = request.getEmail().trim();
        String purpose = request.getPurpose().trim().toUpperCase();

        // 场景校验
        if ("REGISTER".equals(purpose) || "CHANGE_EMAIL".equals(purpose)) {
            SysUser existing = this.lambdaQuery()
                    .eq(SysUser::getEmail, email)
                    .eq(SysUser::getEmailVerified, true)
                    .ne(SysUser::getStatus, 2)
                    .one();
            if (existing != null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该邮箱已被其他有效账号绑定使用");
            }
        } else if ("RESET_PASSWORD".equals(purpose)) {
            SysUser existing = this.lambdaQuery()
                    .eq(SysUser::getEmail, email)
                    .or()
                    .eq(SysUser::getUsername, email)
                    .one();
            if (existing == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "目标账号/邮箱不存在");
            }
        }

        // 防频繁发送限制锁 (60秒)
        boolean lockAcquired = emailVerificationRedisTemplate.checkAndSetSendLock(purpose, email);
        if (!lockAcquired) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请60秒后再试");
        }

        // 生成 6 位随机数字验证码
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));

        // Redis 存储 300秒
        emailVerificationRedisTemplate.saveCode(purpose, email, code);

        // 调用 EmailService 发送
        emailService.sendVerificationCode(email, purpose, code);
    }

    @Override
    public EmailCodeVerifyVo verifyEmailCode(EmailCodeVerifyRequest request) {
        String email = request.getEmail().trim();
        String code = request.getCode().trim();
        String purpose = request.getPurpose().trim().toUpperCase();

        // 校验 Redis 验证码与防爆破，对于密码恢复，第一步校验成功不删除验证码，以供第二步重置密码最终校验
        boolean deleteOnSuccess = !"RESET_PASSWORD".equals(purpose);
        emailVerificationRedisTemplate.verifyCode(purpose, email, code, deleteOnSuccess);

        // 查找对应用户进行激活
        SysUser user = this.lambdaQuery()
                .eq(SysUser::getEmail, email)
                .or()
                .eq(SysUser::getUsername, email)
                .one();

        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "注册用户不存在，请重新提交注册");
        }

        user.setStatus(0); // ACTIVE
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        this.updateById(user);

        String token = jwtUtils.generateToken(user.getUserId());

        return EmailCodeVerifyVo.builder()
                .token(token)
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Override
    public void resetPassword(PasswordResetRequest request) {
        String email = request.getEmail().trim();
        String code = request.getCode().trim();
        String newPassword = request.getNewPassword();

        emailVerificationRedisTemplate.verifyCode("RESET_PASSWORD", email, code);

        SysUser user = this.lambdaQuery()
                .eq(SysUser::getEmail, email)
                .or()
                .eq(SysUser::getUsername, email)
                .one();

        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "账号不存在");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        this.updateById(user);
    }

    @Override
    public void changeEmail(String currentUserId, EmailChangeRequest request) {
        String newEmail = request.getNewEmail().trim();
        String code = request.getCode().trim();

        SysUser existing = this.lambdaQuery().eq(SysUser::getEmail, newEmail).one();
        if (existing != null && !existing.getUserId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新邮箱已被其他账号使用");
        }

        emailVerificationRedisTemplate.verifyCode("CHANGE_EMAIL", newEmail, code);

        SysUser user = this.lambdaQuery().eq(SysUser::getUserId, currentUserId).one();
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "当前用户不存在");
        }

        user.setEmail(newEmail);
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        this.updateById(user);
    }
}
