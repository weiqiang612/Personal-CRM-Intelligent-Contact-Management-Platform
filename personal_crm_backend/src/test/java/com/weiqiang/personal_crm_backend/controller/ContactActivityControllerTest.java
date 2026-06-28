package com.weiqiang.personal_crm_backend.controller;

import com.weiqiang.personal_crm_backend.entity.ActivityLog;
import com.weiqiang.personal_crm_backend.entity.Contact;
import com.weiqiang.personal_crm_backend.mapper.ActivityLogMapper;
import com.weiqiang.personal_crm_backend.mapper.ContactMapper;
import com.weiqiang.personal_crm_backend.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 联系人活动轨迹 Controller 集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class ContactActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private ActivityLogMapper activityLogMapper;

    private String token;
    private String otherToken;
    private String testCtId;

    @BeforeEach
    void setUp() {
        token = "Bearer " + jwtUtils.generateToken("U000000001");
        otherToken = "Bearer " + jwtUtils.generateToken("U000000002");

        testCtId = "C9" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000));
        Contact contact = new Contact();
        contact.setUserId("U000000001");
        contact.setCtId(testCtId);
        contact.setName("TDD Test Contact");
        contact.setStatus(0);
        contactMapper.insert(contact);

        // 插入两条测试活动轨迹数据（一条较早，一条较晚）
        ActivityLog log1 = new ActivityLog();
        log1.setActivityId("ACT_TEST_EARLIER");
        log1.setUserId("U000000001");
        log1.setContactId(testCtId);
        log1.setEventType("CONTACT_CREATED");
        log1.setTitle("创建了联系人");
        log1.setDescription("更早发生的动态");
        log1.setOccurredAt(LocalDateTime.now().minusHours(2));
        log1.setCreatedAt(LocalDateTime.now().minusHours(2));
        activityLogMapper.insert(log1);

        ActivityLog log2 = new ActivityLog();
        log2.setActivityId("ACT_TEST_LATER");
        log2.setUserId("U000000001");
        log2.setContactId(testCtId);
        log2.setEventType("TODO_COMPLETED");
        log2.setTitle("完成了待办事项");
        log2.setDescription("较晚发生的动态");
        log2.setOccurredAt(LocalDateTime.now().minusHours(1));
        log2.setCreatedAt(LocalDateTime.now().minusHours(1));
        activityLogMapper.insert(log2);
    }

    @Test
    void testGetActivities_Success_OrderAndContract() throws Exception {
        mockMvc.perform(get("/api/v1/contacts/" + testCtId + "/activities")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].activityId", is("ACT_TEST_LATER")))
                .andExpect(jsonPath("$.data[0].eventType", is("TODO_COMPLETED")))
                .andExpect(jsonPath("$.data[0].title", is("完成了待办事项")))
                .andExpect(jsonPath("$.data[1].activityId", is("ACT_TEST_EARLIER")))
                .andExpect(jsonPath("$.data[1].eventType", is("CONTACT_CREATED")));
    }

    @Test
    void testGetActivities_LimitParameter() throws Exception {
        mockMvc.perform(get("/api/v1/contacts/" + testCtId + "/activities?limit=1")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].activityId", is("ACT_TEST_LATER")));
    }

    @Test
    void testGetActivities_Forbidden_OtherUser() throws Exception {
        mockMvc.perform(get("/api/v1/contacts/" + testCtId + "/activities")
                        .header("Authorization", otherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40301)));
    }

    @Test
    void testGetActivities_NotFound_InvalidContactId() throws Exception {
        mockMvc.perform(get("/api/v1/contacts/C_NOT_EXIST_9999/activities")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40401)));
    }
}
