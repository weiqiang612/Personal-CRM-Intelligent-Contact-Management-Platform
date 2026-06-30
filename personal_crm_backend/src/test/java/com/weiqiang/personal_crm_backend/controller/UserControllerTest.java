package com.weiqiang.personal_crm_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiqiang.personal_crm_backend.common.Constants;
import com.weiqiang.personal_crm_backend.entity.SysUser;
import com.weiqiang.personal_crm_backend.mapper.SysUserMapper;
import com.weiqiang.personal_crm_backend.model.dto.UsernameChangeRequest;
import com.weiqiang.personal_crm_backend.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private String userId = "U000000099";
    private String originalUsername = "testuser99";

    @BeforeEach
    void setUp() {
        sysUserMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserId, userId));
        sysUserMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserId, "U000000100"));

        // 插入测试用户
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setUsername(originalUsername);
        user.setEmail("testuser99@example.com");
        user.setPasswordHash("hashed_password");
        user.setStatus(0); // ACTIVE
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.insert(user);

        // 插入被占用的用户名
        SysUser existingUser = new SysUser();
        existingUser.setUserId("U000000100");
        existingUser.setUsername("alreadytaken");
        existingUser.setEmail("alreadytaken@example.com");
        existingUser.setPasswordHash("hashed_password");
        existingUser.setStatus(0);
        existingUser.setEmailVerified(true);
        existingUser.setEmailVerifiedAt(LocalDateTime.now());
        existingUser.setCreatedAt(LocalDateTime.now());
        existingUser.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.insert(existingUser);

        token = "Bearer " + jwtUtils.generateToken(userId);
    }

    @Test
    void testChangeUsernameSuccess() throws Exception {
        UsernameChangeRequest request = new UsernameChangeRequest();
        request.setNewUsername("newcoolname");

        mockMvc.perform(post("/api/v1/user/username/change")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.message", is("用户名修改成功")));
    }

    @Test
    void testChangeUsernameBlankFailure() throws Exception {
        UsernameChangeRequest request = new UsernameChangeRequest();
        request.setNewUsername("");

        mockMvc.perform(post("/api/v1/user/username/change")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)));
    }

    @Test
    void testChangeUsernameTooShortFailure() throws Exception {
        UsernameChangeRequest request = new UsernameChangeRequest();
        request.setNewUsername("ab"); // < 3 chars

        mockMvc.perform(post("/api/v1/user/username/change")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)));
    }

    @Test
    void testChangeUsernameTooLongFailure() throws Exception {
        UsernameChangeRequest request = new UsernameChangeRequest();
        // Generate a 51 character username
        request.setNewUsername("a".repeat(51));

        mockMvc.perform(post("/api/v1/user/username/change")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)));
    }

    @Test
    void testChangeUsernameSpaceFailure() throws Exception {
        UsernameChangeRequest request = new UsernameChangeRequest();
        request.setNewUsername("new name"); // contains space

        mockMvc.perform(post("/api/v1/user/username/change")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)))
                .andExpect(jsonPath("$.message", is("用户名不能包含空格或空白字符")));
    }

    @Test
    void testChangeUsernameAlreadyExistsFailure() throws Exception {
        UsernameChangeRequest request = new UsernameChangeRequest();
        request.setNewUsername("alreadytaken");

        mockMvc.perform(post("/api/v1/user/username/change")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)))
                .andExpect(jsonPath("$.message", is(Constants.Message.USERNAME_EXISTS)));
    }
}
