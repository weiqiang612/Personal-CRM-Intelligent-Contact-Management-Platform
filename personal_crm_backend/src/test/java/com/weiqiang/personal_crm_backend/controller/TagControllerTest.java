package com.weiqiang.personal_crm_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiqiang.personal_crm_backend.entity.Contact;
import com.weiqiang.personal_crm_backend.entity.ContactTag;
import com.weiqiang.personal_crm_backend.entity.Tag;
import com.weiqiang.personal_crm_backend.mapper.ContactMapper;
import com.weiqiang.personal_crm_backend.mapper.ContactTagMapper;
import com.weiqiang.personal_crm_backend.mapper.TagMapper;
import com.weiqiang.personal_crm_backend.model.dto.ContactSaveDTO;
import com.weiqiang.personal_crm_backend.model.dto.TagSaveDTO;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 标签及联系人绑定标签的集成测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private ContactTagMapper contactTagMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private String otherToken;

    private Tag user1Tag1;
    private Tag user1Tag2;
    private Tag user2Tag1;

    private String testCtId;

    private String createJsonBody(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    @BeforeEach
    void setUp() {
        token = "Bearer " + jwtUtils.generateToken("U000000001");
        otherToken = "Bearer " + jwtUtils.generateToken("U000000002");

        // 插入 User 1 (U000000001) 的标签
        user1Tag1 = new Tag();
        user1Tag1.setUserId("U000000001");
        user1Tag1.setName("User1Tag1");
        user1Tag1.setColor("#111111");
        user1Tag1.setCreatedAt(LocalDateTime.now());
        user1Tag1.setUpdatedAt(LocalDateTime.now());
        tagMapper.insert(user1Tag1);

        user1Tag2 = new Tag();
        user1Tag2.setUserId("U000000001");
        user1Tag2.setName("User1Tag2");
        user1Tag2.setColor("#222222");
        user1Tag2.setCreatedAt(LocalDateTime.now());
        user1Tag2.setUpdatedAt(LocalDateTime.now());
        tagMapper.insert(user1Tag2);

        // 插入 User 2 (U000000002) 的标签
        user2Tag1 = new Tag();
        user2Tag1.setUserId("U000000002");
        user2Tag1.setName("User2Tag1");
        user2Tag1.setColor("#333333");
        user2Tag1.setCreatedAt(LocalDateTime.now());
        user2Tag1.setUpdatedAt(LocalDateTime.now());
        tagMapper.insert(user2Tag1);

        // 插入 User 1 的测试联系人
        testCtId = "C9" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000));
        Contact contact = new Contact();
        contact.setUserId("U000000001");
        contact.setCtId(testCtId);
        contact.setName("Tag Test Contact");
        contact.setPhone("13899998888");
        contact.setStatus(0);
        contact.setCreatedAt(LocalDateTime.now());
        contact.setUpdatedAt(LocalDateTime.now());
        contactMapper.insert(contact);
    }

    @Test
    void testListTags_Success() throws Exception {
        mockMvc.perform(get("/api/v1/tags")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.data[*].name", hasItem("User1Tag1")))
                .andExpect(jsonPath("$.data[*].name", hasItem("User1Tag2")))
                .andExpect(jsonPath("$.data[*].name", not(hasItem("User2Tag1"))));
    }

    @Test
    void testListTags_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40101)));
    }

    @Test
    void testCreateTag_Success() throws Exception {
        TagSaveDTO dto = new TagSaveDTO();
        dto.setName("NewCreatedTag");
        dto.setColor("#FFFFFF");

        mockMvc.perform(post("/api/v1/tags")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.name", is("NewCreatedTag")))
                .andExpect(jsonPath("$.data.color", is("#FFFFFF")))
                .andExpect(jsonPath("$.data.tagId", notNullValue()));
    }

    @Test
    void testCreateTag_ValidationFailed() throws Exception {
        TagSaveDTO dto = new TagSaveDTO();
        dto.setName(""); // name 不能为空
        dto.setColor("TooLongColorStringFormatExceedingTwentyChars"); // color 超长

        mockMvc.perform(post("/api/v1/tags")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)));
    }

    @Test
    void testCreateTag_Conflict() throws Exception {
        TagSaveDTO dto = new TagSaveDTO();
        dto.setName("User1Tag1"); // 已存在同名标签
        dto.setColor("#111111");

        mockMvc.perform(post("/api/v1/tags")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40901)));
    }

    @Test
    void testCreateTag_MultiTenant_SameName_Success() throws Exception {
        // Ethan 和 User2 分别创建同名标签 "SharedName"
        TagSaveDTO dto = new TagSaveDTO();
        dto.setName("SharedName");
        dto.setColor("#123456");

        // Ethan 创建
        mockMvc.perform(post("/api/v1/tags")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        // User2 创建，应该也成功，不冲突
        mockMvc.perform(post("/api/v1/tags")
                        .header("Authorization", otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));
    }

    @Test
    void testUpdateTag_Success() throws Exception {
        TagSaveDTO dto = new TagSaveDTO();
        dto.setName("User1Tag1-Updated");
        dto.setColor("#999999");

        mockMvc.perform(put("/api/v1/tags/" + user1Tag1.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.name", is("User1Tag1-Updated")))
                .andExpect(jsonPath("$.data.color", is("#999999")));
    }

    @Test
    void testUpdateTag_Forbidden() throws Exception {
        TagSaveDTO dto = new TagSaveDTO();
        dto.setName("HackName");

        // User2 试图修改 Ethan 的标签 user1Tag1
        mockMvc.perform(put("/api/v1/tags/" + user1Tag1.getId())
                        .header("Authorization", otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40301)));
    }

    @Test
    void testUpdateTag_Conflict() throws Exception {
        TagSaveDTO dto = new TagSaveDTO();
        dto.setName("User1Tag2"); // 将 user1Tag1 改名为已存在的 user1Tag2

        mockMvc.perform(put("/api/v1/tags/" + user1Tag1.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40901)));
    }

    @Test
    void testDeleteTag_SuccessAndCleanup() throws Exception {
        // 先手动关联
        ContactTag ct = new ContactTag();
        ct.setUserId("U000000001");
        ct.setContactId(testCtId);
        ct.setTagId(user1Tag1.getId());
        ct.setCreatedAt(LocalDateTime.now());
        contactTagMapper.insert(ct);

        // 删除标签
        mockMvc.perform(delete("/api/v1/tags/" + user1Tag1.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        // 验证 tag 不存在了
        assertNull(tagMapper.selectById(user1Tag1.getId()));

        // 验证联级 contact_tag 关系被删除
        List<ContactTag> list = contactTagMapper.selectList(null);
        boolean existRelation = list.stream().anyMatch(r -> r.getTagId().equals(user1Tag1.getId()));
        assertFalse(existRelation, "contact_tag relation should be deleted");

        // 验证联系人本身依然存在
        assertNotNull(contactMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Contact>()
                .eq(Contact::getCtId, testCtId)));
    }

    @Test
    void testDeleteTag_Forbidden() throws Exception {
        // User2 试图删除 Ethan 的标签
        mockMvc.perform(delete("/api/v1/tags/" + user1Tag1.getId())
                        .header("Authorization", otherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40301)));
    }

    @Test
    void testCreateContact_WithTagIds_Success() throws Exception {
        ContactSaveDTO dto = new ContactSaveDTO();
        dto.setName("ContactWithTags");
        dto.setPhone("13511112222");
        dto.setTagIds(Arrays.asList(user1Tag1.getId(), user1Tag2.getId()));

        mockMvc.perform(post("/api/v1/contacts")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.tags", hasSize(2)))
                .andExpect(jsonPath("$.data.tags", hasItem("User1Tag1")))
                .andExpect(jsonPath("$.data.tags", hasItem("User1Tag2")));
    }

    @Test
    void testCreateContact_WithTagIds_Forbidden() throws Exception {
        ContactSaveDTO dto = new ContactSaveDTO();
        dto.setName("ContactWithIllegalTags");
        dto.setPhone("13511112222");
        // 传入了 user2 的标签 id (user2Tag1)
        dto.setTagIds(Arrays.asList(user1Tag1.getId(), user2Tag1.getId()));

        mockMvc.perform(post("/api/v1/contacts")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40301))); // 强隔离限制，拒绝执行
    }

    @Test
    void testUpdateContact_WithTagIds_SuccessAndClean() throws Exception {
        // 先创建联系人，无标签
        ContactSaveDTO dto = new ContactSaveDTO();
        dto.setName("ContactToUpdateTags");
        dto.setPhone("13522223333");
        
        String res = mockMvc.perform(post("/api/v1/contacts")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        String createdCtId = objectMapper.readTree(res).path("data").path("contactId").asText();

        // 更新标签为 user1Tag1
        dto.setTagIds(Arrays.asList(user1Tag1.getId()));
        mockMvc.perform(put("/api/v1/contacts/" + createdCtId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.tags", hasSize(1)))
                .andExpect(jsonPath("$.data.tags", hasItem("User1Tag1")));

        // 再次更新为空数组，表示清空标签
        dto.setTagIds(Arrays.asList());
        mockMvc.perform(put("/api/v1/contacts/" + createdCtId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.tags", hasSize(0)));
    }

    @Test
    void testUpdateContact_WithTagIds_Forbidden() throws Exception {
        // 创建联系人
        ContactSaveDTO dto = new ContactSaveDTO();
        dto.setName("ContactToUpdateForbidden");
        dto.setPhone("13533334444");

        String res = mockMvc.perform(post("/api/v1/contacts")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String createdCtId = objectMapper.readTree(res).path("data").path("contactId").asText();

        // 传入了 user2 的标签
        dto.setTagIds(Arrays.asList(user2Tag1.getId()));
        mockMvc.perform(put("/api/v1/contacts/" + createdCtId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40301))); // 拒绝执行
    }
}
