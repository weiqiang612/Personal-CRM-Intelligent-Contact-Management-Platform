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

    @Test
    public void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newtestuser");
        request.setPassword("securepassword123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.message", is("success")));

        // 验证数据库中用户已落库并加密
        SysUser user = sysUserMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, "newtestuser")
        );
        assertNotNull(user);
        assertNotNull(user.getUserId());
        assertTrue(user.getUserId().startsWith("U"));
        assertTrue(passwordEncoder.matches("securepassword123", user.getPasswordHash()));
    }

    @Test
    public void testRegisterEmailSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testemail@example.com");
        request.setPassword("securepassword123");

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
        // ethan 已由 data.sql 种子数据初始化
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ethan");
        request.setPassword("password12345");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // 后端返回 200 OK，包含统一错误 JSON 包
                .andExpect(jsonPath("$.code", is(40001))) // ErrorCode.PARAMS_ERROR
                .andExpect(jsonPath("$.message", containsString("Username already exists")));
    }

    @Test
    public void testRegisterValidationFailures() throws Exception {
        // 1. 用户名过短（非邮箱）
        RegisterRequest request1 = new RegisterRequest();
        request1.setUsername("ab");
        request1.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)))
                .andExpect(jsonPath("$.message", containsString("Username must be at least 3 characters")));

        // 2. 邮箱格式非法
        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("abc@invalid");
        request2.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)))
                .andExpect(jsonPath("$.message", containsString("Invalid email address format")));

        // 3. 密码太短
        RegisterRequest request3 = new RegisterRequest();
        request3.setUsername("validuser");
        request3.setPassword("1234567");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)))
                .andExpect(jsonPath("$.message", containsString("Password must be at least 8 characters")));
    }

    @Test
    public void testRegisterAndLogin() throws Exception {
        // 1. 注册新用户
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("loginuser");
        registerRequest.setPassword("loginpassword123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        // 2. 使用新注册的用户成功登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("loginuser");
        loginRequest.setPassword("loginpassword123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.user.username", is("loginuser")));
    }

    @Test
    public void testGetMeShouldFallbackWhenLocalAvatarFileIsMissing() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("ethan");
        loginRequest.setPassword("123456");

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

        org.junit.jupiter.api.Assertions.assertNull(
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
            String uniqueUsername = "concurrent_" + System.currentTimeMillis() + "_" + index;
            usernames[index] = uniqueUsername;

            executorService.execute(() -> {
                try {
                    RegisterRequest request = new RegisterRequest();
                    request.setUsername(uniqueUsername);
                    request.setPassword("securepassword123");

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
}
