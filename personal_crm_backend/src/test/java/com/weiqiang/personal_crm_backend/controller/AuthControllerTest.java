package com.weiqiang.personal_crm_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiqiang.personal_crm_backend.entity.SysUser;
import com.weiqiang.personal_crm_backend.entity.UserAvatar;
import com.weiqiang.personal_crm_backend.mapper.SysUserMapper;
import com.weiqiang.personal_crm_backend.mapper.UserAvatarMapper;
import com.weiqiang.personal_crm_backend.model.dto.LoginRequest;
import com.weiqiang.personal_crm_backend.model.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AuthController Unit and Integration Test
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private UserAvatarMapper userAvatarMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.weiqiang.personal_crm_backend.component.EmailVerificationRedisTemplate emailVerificationRedisTemplate;

    @Autowired
    private org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    @Test
    public void testGetCodeForE2E() {
        System.out.println("CODE_FOR_E2E:" + redisTemplate.opsForValue().get("email:code:REGISTER:e2etest@example.com"));
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        emailVerificationRedisTemplate.saveCode("REGISTER", "newtestuser@example.com", "111111");

        RegisterRequest request = new RegisterRequest();
        request.setUsername("newtestuser@example.com");
        request.setPassword("securepassword123");
        request.setCode("111111");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.message", is("success")));

        // 验证数据库中用户已落库并加密，且状态为 ACTIVE 0
        SysUser user = sysUserMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, "newtestuser@example.com")
        );
        assertNotNull(user);
        assertNotNull(user.getUserId());
        assertTrue(user.getUserId().startsWith("U"));
        assertTrue(passwordEncoder.matches("securepassword123", user.getPasswordHash()));
        assertTrue(user.getEmailVerified());
        org.junit.jupiter.api.Assertions.assertEquals(0, user.getStatus());
    }

    @Test
    public void testRegisterEmailSuccess() throws Exception {
        emailVerificationRedisTemplate.saveCode("REGISTER", "testemail@example.com", "222222");

        RegisterRequest request = new RegisterRequest();
        request.setUsername("testemail@example.com");
        request.setPassword("securepassword123");
        request.setCode("222222");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        SysUser user = sysUserMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, "testemail@example.com")
        );
        assertNotNull(user);
    }

    @Test
    public void testRegisterDuplicateUsername() throws Exception {
        // 先成功注册一个邮箱用户
        emailVerificationRedisTemplate.saveCode("REGISTER", "duplicate@example.com", "333333");
        RegisterRequest request1 = new RegisterRequest();
        request1.setUsername("duplicate@example.com");
        request1.setEmail("duplicate@example.com");
        request1.setPassword("password12345");
        request1.setCode("333333");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        // 再次用相同的邮箱注册，期望报“Username already exists”错误
        emailVerificationRedisTemplate.saveCode("REGISTER", "duplicate@example.com", "333333");
        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("duplicate@example.com");
        request2.setEmail("duplicate@example.com");
        request2.setPassword("password12345");
        request2.setCode("333333");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001))) // ErrorCode.PARAMS_ERROR
                .andExpect(jsonPath("$.message", containsString("Username already exists")));
    }

    @Test
    public void testRegisterValidationFailures() throws Exception {
        // 1. 用户名过短且非邮箱（由于没传email字段，将触发 "Email is required for registration"）
        RegisterRequest request1 = new RegisterRequest();
        request1.setUsername("ab");
        request1.setPassword("password123");
        request1.setCode("444444");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)))
                .andExpect(jsonPath("$.message", containsString("Email is required for registration")));

        // 2. 邮箱格式非法
        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("abc@invalid");
        request2.setPassword("password123");
        request2.setCode("444444");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)))
                .andExpect(jsonPath("$.message", containsString("Invalid email address format")));

        // 3. 密码太短
        emailVerificationRedisTemplate.saveCode("REGISTER", "validuser@example.com", "444444");
        RegisterRequest request3 = new RegisterRequest();
        request3.setUsername("validuser@example.com");
        request3.setPassword("1234567");
        request3.setCode("444444");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)))
                .andExpect(jsonPath("$.message", containsString("Password must be at least 8 characters")));
    }

    @Test
    public void testRegisterWrongCode() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("wrongcode@example.com");
        request.setPassword("securepassword123");
        request.setCode("999999"); // 错误的 code

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)))
                // 用 Unicode 规避编译/运行环境的中文字符集乱码问题 "\u9a8c\u8bc1\u7801" 为 "验证码"
                .andExpect(jsonPath("$.message", containsString("\u9a8c\u8bc1\u7801")));
    }

    @Test
    public void testRegisterAndLogin() throws Exception {
        // 1. 注册新用户并验证
        emailVerificationRedisTemplate.saveCode("REGISTER", "loginuser@example.com", "555555");
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("loginuser@example.com");
        registerRequest.setPassword("loginpassword123");
        registerRequest.setCode("555555");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        // 2. 使用新注册的用户成功登录（无需模拟激活，注册成功即为 ACTIVE 状态）
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("loginuser@example.com");
        loginRequest.setPassword("loginpassword123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.user.username", is("loginuser@example.com")));
    }

    @Test
    public void testGetMeShouldFallbackWhenLocalAvatarFileIsMissing() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("ethan");
        loginRequest.setPassword("Aa123456");

        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(loginResponse).path("data").path("token").asText();

        UserAvatar avatar = userAvatarMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserAvatar>()
                        .eq(UserAvatar::getUserId, "U000000001")
        );
        if (avatar == null) {
            avatar = new UserAvatar();
            avatar.setUserId("U000000001");
            avatar.setPicId("UPICMISS01");
            avatar.setFileName("missing-avatar.png");
            avatar.setCreatedAt(java.time.LocalDateTime.now());
            avatar.setAccessUrl("/uploads/user-avatar/missing-avatar.png");
            avatar.setFilePath("D:\\\\project\\\\Personal CRM Intelligent Contact Management Platform\\\\personal_crm_backend\\\\uploads\\\\user-avatar\\\\missing-avatar.png");
            userAvatarMapper.insert(avatar);
        } else {
            avatar.setPicId("UPICMISS01");
            avatar.setFileName("missing-avatar.png");
            avatar.setAccessUrl("/uploads/user-avatar/missing-avatar.png");
            avatar.setFilePath("D:\\\\project\\\\Personal CRM Intelligent Contact Management Platform\\\\personal_crm_backend\\\\uploads\\\\user-avatar\\\\missing-avatar.png");
            userAvatarMapper.updateById(avatar);
        }

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.avatarUrl", nullValue()));

        org.junit.jupiter.api.Assertions.assertNotNull(
                userAvatarMapper.selectOne(
                        new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserAvatar>()
                                .eq(UserAvatar::getUserId, "U000000001")
                )
        );
    }

    @Test
    public void testRegisterDefensiveValidation() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(null);
        request.setPassword(null);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)))
                .andExpect(jsonPath("$.message", containsString("cannot be blank")));
    }

    @Test
    public void testConcurrentRegisterSelfHealing() throws Exception {
        int threadCount = 4;
        java.util.concurrent.ExecutorService executorService = java.util.concurrent.Executors.newFixedThreadPool(threadCount);
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(threadCount);
        java.util.concurrent.atomic.AtomicInteger successCount = new java.util.concurrent.atomic.AtomicInteger(0);
        String[] usernames = new String[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            String uniqueUsername = "concurrent_" + System.currentTimeMillis() + "_" + index + "@example.com";
            usernames[index] = uniqueUsername;

            // 为每个并发线程预埋验证码
            String code = "12345" + index;
            emailVerificationRedisTemplate.saveCode("REGISTER", uniqueUsername, code);

            executorService.execute(() -> {
                try {
                    RegisterRequest request = new RegisterRequest();
                    request.setUsername(uniqueUsername);
                    request.setEmail(uniqueUsername);
                    request.setPassword("securepassword123");
                    request.setCode(code);

                    mockMvc.perform(post("/api/v1/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.code", is(0)));

                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        org.junit.jupiter.api.Assertions.assertEquals(threadCount, successCount.get());

        // 验证并发用户都成功落库，且其生成的 userId 在高并发竞争下也是各自独立唯一的
        java.util.Set<String> userIds = new java.util.HashSet<>();
        for (int i = 0; i < threadCount; i++) {
            SysUser user = sysUserMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getUsername, usernames[i])
            );
            assertNotNull(user);
            assertNotNull(user.getUserId());
            userIds.add(user.getUserId());
        }
        // 4个并发创建的用户的 userId 应全部不相同
        org.junit.jupiter.api.Assertions.assertEquals(threadCount, userIds.size());
    }

    @Test
    public void testUpdateEmail_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("ethan");
        loginRequest.setPassword("Aa123456");

        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(loginResponse).path("data").path("token").asText();

        // 1. 修改邮箱
        com.weiqiang.personal_crm_backend.model.dto.UpdateEmailRequest updateEmailRequest = new com.weiqiang.personal_crm_backend.model.dto.UpdateEmailRequest();
        updateEmailRequest.setEmail("ethan_new@example.com");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/auth/profile/email")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateEmailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        // 2. 校验用户信息接口返回的邮箱是否已改变
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.email", is("ethan_new@example.com")));
    }

    @Test
    public void testUpdatePhone_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("ethan");
        loginRequest.setPassword("Aa123456");

        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(loginResponse).path("data").path("token").asText();

        // 1. 修改手机号
        com.weiqiang.personal_crm_backend.model.dto.UpdatePhoneRequest updatePhoneRequest = new com.weiqiang.personal_crm_backend.model.dto.UpdatePhoneRequest();
        updatePhoneRequest.setPhone("13988889999");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/auth/profile/phone")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePhoneRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        // 2. 校验返回
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.phone", is("13988889999")));
    }

    @Test
    public void testUpdatePassword_SuccessAndFail() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("ethan");
        loginRequest.setPassword("Aa123456");

        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(loginResponse).path("data").path("token").asText();

        // 1. 使用错误的旧密码修改密码，应该失败
        com.weiqiang.personal_crm_backend.model.dto.UpdatePasswordRequest updatePasswordRequest = new com.weiqiang.personal_crm_backend.model.dto.UpdatePasswordRequest();
        updatePasswordRequest.setOldPassword("wrongpassword");
        updatePasswordRequest.setNewPassword("newpassword123");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/auth/profile/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001))); // PARAMS_ERROR

        // 2. 使用正确的密码修改密码，应该成功
        updatePasswordRequest.setOldPassword("Aa123456");
        updatePasswordRequest.setNewPassword("newpassword123");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/auth/profile/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        // 3. 验证使用老密码登录失败
        LoginRequest failLogin = new LoginRequest();
        failLogin.setUsername("ethan");
        failLogin.setPassword("Aa123456");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(failLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)));

        // 4. 验证使用新密码登录成功
        LoginRequest successLogin = new LoginRequest();
        successLogin.setUsername("ethan");
        successLogin.setPassword("newpassword123");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(successLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));
    }
}
