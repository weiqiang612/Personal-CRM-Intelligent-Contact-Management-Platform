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
import org.springframework.jdbc.core.JdbcTemplate;

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

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
    void testGetContactDetail_TagIsolation_Success() throws Exception {
        jdbcTemplate.update("INSERT INTO tag (id, user_id, name, color) VALUES (101, 'U000000001', 'OwnTag', '#ffffff') ON DUPLICATE KEY UPDATE user_id=VALUES(user_id), name=VALUES(name)");
        jdbcTemplate.update("INSERT INTO tag (id, user_id, name, color) VALUES (102, 'U000000002', 'ForeignTag', '#ffffff') ON DUPLICATE KEY UPDATE user_id=VALUES(user_id), name=VALUES(name)");
        jdbcTemplate.update("INSERT INTO contact_tag (user_id, contact_id, tag_id) VALUES ('U000000001', ?, 101) ON DUPLICATE KEY UPDATE user_id=VALUES(user_id), tag_id=VALUES(tag_id)", testCtId);
        jdbcTemplate.update("INSERT INTO contact_tag (user_id, contact_id, tag_id) VALUES ('U000000002', ?, 102) ON DUPLICATE KEY UPDATE user_id=VALUES(user_id), tag_id=VALUES(tag_id)", testCtId);

        mockMvc.perform(get("/api/v1/contacts/" + testCtId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.tags", hasItem("OwnTag")))
                .andExpect(jsonPath("$.data.tags", not(hasItem("ForeignTag"))));
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

    @Test
    void testListContacts_TagFilter_Success() throws Exception {
        // U000000001 插入一个标签和标签关联
        jdbcTemplate.update("INSERT INTO tag (id, user_id, name, color) VALUES (99, 'U000000001', 'TestTag', '#ffffff') ON DUPLICATE KEY UPDATE name=VALUES(name)");
        jdbcTemplate.update("INSERT INTO contact_tag (user_id, contact_id, tag_id) VALUES ('U000000001', ?, 99) ON DUPLICATE KEY UPDATE contact_id=VALUES(contact_id)", testCtId);

        // 用 token（U000000001）查询，带上 tag=TestTag
        mockMvc.perform(get("/api/v1/contacts")
                        .header("Authorization", token)
                        .param("tag", "TestTag"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.list", hasSize(1)))
                .andExpect(jsonPath("$.data.list[0].contactId", is(testCtId)))
                .andExpect(jsonPath("$.data.list[0].tags", hasItem("TestTag")));

        // 尝试用一个不存在的标签过滤，应该返回空列表
        mockMvc.perform(get("/api/v1/contacts")
                        .header("Authorization", token)
                        .param("tag", "NonExistingTag"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.list", hasSize(0)));
    }

    @Test
    void testListContacts_TagFilter_SecurityIsolation() throws Exception {
        // U000000001 插入一个标签和标签关联
        jdbcTemplate.update("INSERT INTO tag (id, user_id, name, color) VALUES (99, 'U000000001', 'TestTag', '#ffffff') ON DUPLICATE KEY UPDATE name=VALUES(name)");
        jdbcTemplate.update("INSERT INTO contact_tag (user_id, contact_id, tag_id) VALUES ('U000000001', ?, 99) ON DUPLICATE KEY UPDATE contact_id=VALUES(contact_id)", testCtId);

        // 创建 U000000002（另一个用户）的联系人，并关联一个同名标签 "TestTag"
        String otherCtId = "C99" + String.format("%07d", ThreadLocalRandom.current().nextInt(10_000_000));
        Contact otherContact = new Contact();
        otherContact.setUserId("U000000002");
        otherContact.setCtId(otherCtId);
        otherContact.setName("Other User Contact");
        otherContact.setPhone("13900002222");
        otherContact.setStatus(0);
        contactMapper.insert(otherContact);

        // U000000002 插入一个标签和标签关联
        jdbcTemplate.update("INSERT INTO tag (id, user_id, name, color) VALUES (100, 'U000000002', 'TestTag', '#ffffff') ON DUPLICATE KEY UPDATE name=VALUES(name)");
        jdbcTemplate.update("INSERT INTO contact_tag (user_id, contact_id, tag_id) VALUES ('U000000002', ?, 100) ON DUPLICATE KEY UPDATE contact_id=VALUES(contact_id)", otherCtId);

        // 1. 用 U000000001 的 token，查询 tag=TestTag，应该查出自己的 testCtId，但绝不应包含 U000000002 的 otherCtId
        mockMvc.perform(get("/api/v1/contacts")
                        .header("Authorization", token)
                        .param("tag", "TestTag"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.list[*].contactId", hasItem(testCtId)))
                .andExpect(jsonPath("$.data.list[*].contactId", not(hasItem(otherCtId))));

        // 2. 用 U000000002 的 token，查询 tag=TestTag，应该查出自己的 otherCtId，且不包含 U000000001 的 testCtId
        mockMvc.perform(get("/api/v1/contacts")
                        .header("Authorization", otherToken)
                        .param("tag", "TestTag"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.list", hasSize(1)))
                .andExpect(jsonPath("$.data.list[0].contactId", is(otherCtId)));
    }
}
