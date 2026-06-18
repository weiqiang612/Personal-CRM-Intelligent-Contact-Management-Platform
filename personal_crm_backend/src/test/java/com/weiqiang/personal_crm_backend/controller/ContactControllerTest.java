package com.weiqiang.personal_crm_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiqiang.personal_crm_backend.entity.Contact;
import com.weiqiang.personal_crm_backend.mapper.ContactMapper;
import com.weiqiang.personal_crm_backend.model.dto.ContactSaveDTO;
import com.weiqiang.personal_crm_backend.model.dto.ContactStatusDTO;
import com.weiqiang.personal_crm_backend.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 联系人控制器集成测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private String otherToken;
    private String testCtId;
    private String createJsonBody(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    @BeforeEach
    void setUp() {
        // 为 ethan (U000000001) 生成测试 Token
        token = "Bearer " + jwtUtils.generateToken("U000000001");
        
        // 为另一个用户 (U000000002) 生成测试 Token
        otherToken = "Bearer " + jwtUtils.generateToken("U000000002");

        // 插入测试数据
        testCtId = "C9" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000));
        Contact contact = new Contact();
        contact.setUserId("U000000001");
        contact.setCtId(testCtId);
        contact.setName("Test Contact");
        contact.setPhone("13800001111");
        contact.setWechat("test_wechat");
        contact.setStatus(0);
        contactMapper.insert(contact);
    }

    @Test
    void testListContacts_Success() throws Exception {
        mockMvc.perform(get("/api/v1/contacts")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.list", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.list[*].name", hasItem("Test Contact")));
    }

    @Test
    void testListContacts_Unauthorized() throws Exception {
        // 未携带授权 Header
        mockMvc.perform(get("/api/v1/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40101)));
    }

    @Test
    void testGetContactDetail_Success() throws Exception {
        mockMvc.perform(get("/api/v1/contacts/" + testCtId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.name", is("Test Contact")))
                .andExpect(jsonPath("$.data.contactId", is(testCtId)));
    }

    @Test
    void testGetContactDetail_Forbidden() throws Exception {
        // 使用另一个用户的 Token，尝试越权访问联系人
        mockMvc.perform(get("/api/v1/contacts/" + testCtId)
                        .header("Authorization", otherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40301)));
    }

    @Test
    void testGetContactDetail_NotFound() throws Exception {
        // 访问不存在的联系人Id
        mockMvc.perform(get("/api/v1/contacts/C999999999")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40401)));
    }

    @Test
    void testCreateContact_Success() throws Exception {
        ContactSaveDTO dto = new ContactSaveDTO();
        dto.setName("New Contact");
        dto.setPhone("13999998888");
        dto.setBirthday(LocalDate.of(1998, 12, 1));

        mockMvc.perform(post("/api/v1/contacts")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.name", is("New Contact")))
                .andExpect(jsonPath("$.data.contactId", notNullValue()));
    }

    @Test
    void testCreateContact_ValidationFailed() throws Exception {
        ContactSaveDTO dto = new ContactSaveDTO();
        dto.setName(""); // 名字为空，校验不通过
        dto.setPhone("abc"); // 手机格式不匹配

        mockMvc.perform(post("/api/v1/contacts")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)));
    }

    @Test
    void testAddToBlacklist_AndRestore() throws Exception {
        ContactStatusDTO blacklistDTO = new ContactStatusDTO();
        blacklistDTO.setStatus(1);

        // 1. 将联系人加入黑名单
        mockMvc.perform(patch("/api/v1/contacts/" + testCtId + "/blacklist")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(blacklistDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        // 检查状态变动
        mockMvc.perform(get("/api/v1/contacts/" + testCtId)
                        .header("Authorization", token))
                .andExpect(jsonPath("$.data.status", is(1)));

        ContactStatusDTO restoreDTO = new ContactStatusDTO();
        restoreDTO.setStatus(0);

        // 2. 从黑名单恢复该联系人
        mockMvc.perform(patch("/api/v1/contacts/" + testCtId + "/restore")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(restoreDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        // 检查状态恢复
        mockMvc.perform(get("/api/v1/contacts/" + testCtId)
                        .header("Authorization", token))
                .andExpect(jsonPath("$.data.status", is(0)));
    }

    @Test
    void testAddToBlacklist_StatusBodyMismatch() throws Exception {
        ContactStatusDTO dto = new ContactStatusDTO();
        dto.setStatus(0);

        mockMvc.perform(patch("/api/v1/contacts/" + testCtId + "/blacklist")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)));
    }
}
