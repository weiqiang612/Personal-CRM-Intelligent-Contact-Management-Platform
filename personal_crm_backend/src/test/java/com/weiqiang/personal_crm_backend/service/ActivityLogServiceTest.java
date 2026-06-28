package com.weiqiang.personal_crm_backend.service;

import com.weiqiang.personal_crm_backend.entity.ActivityLog;
import com.weiqiang.personal_crm_backend.entity.Contact;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.mapper.ActivityLogMapper;
import com.weiqiang.personal_crm_backend.mapper.ContactMapper;
import com.weiqiang.personal_crm_backend.model.dto.ContactSaveDTO;
import com.weiqiang.personal_crm_backend.model.dto.TodoCreateDTO;
import com.weiqiang.personal_crm_backend.model.vo.ActivityLogVO;
import com.weiqiang.personal_crm_backend.model.vo.ContactVO;
import com.weiqiang.personal_crm_backend.model.vo.TodoVO;
import com.weiqiang.personal_crm_backend.security.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
public class ActivityLogServiceTest {

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private ActivityLogMapper activityLogMapper;

    @Autowired
    private ContactService contactService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private ContactMapper contactMapper;

    private final String testUserId = "U000000001";
    private final String otherUserId = "U000000002";

    @BeforeEach
    public void setUp() {
        UserContext.setUserId(testUserId);
    }

    @Test
    public void testEntityAndMapperBasicOperations() {
        ActivityLog log = new ActivityLog();
        log.setActivityId("ACT_TEST_001");
        log.setUserId(testUserId);
        log.setContactId("C000000001");
        log.setEventType("CONTACT_CREATED");
        log.setTitle("创建了联系人");
        log.setDescription("测试描述");
        log.setOccurredAt(LocalDateTime.now());
        log.setCreatedAt(LocalDateTime.now());

        int inserted = activityLogMapper.insert(log);
        assertEquals(1, inserted);
        assertNotNull(log.getId());

        ActivityLog retrieved = activityLogMapper.selectById(log.getId());
        assertEquals("ACT_TEST_001", retrieved.getActivityId());
    }

    @Test
    public void testSaveAndListActivities() {
        activityLogService.saveActivity(testUserId, "C000000001", "CONTACT_CREATED", "创建联系人", "详细说明");

        List<ActivityLogVO> activities = activityLogService.listContactActivities("C000000001", 10);
        assertFalse(activities.isEmpty());
        assertEquals("CONTACT_CREATED", activities.get(0).getEventType());
        assertEquals("创建联系人", activities.get(0).getTitle());
    }

    @Test
    public void testContactOperationsTraceability() {
        // 创建联系人
        ContactSaveDTO dto = new ContactSaveDTO();
        dto.setName("测试联系人TDD");
        dto.setPhone("13800001111");
        ContactVO createdVO = contactService.createContact(dto);
        String ctId = createdVO.getContactId();

        // 验证 CONTACT_CREATED 日志
        List<ActivityLogVO> logsAfterCreate = activityLogService.listContactActivities(ctId, 10);
        assertTrue(logsAfterCreate.stream().anyMatch(l -> "CONTACT_CREATED".equals(l.getEventType())));

        // 修改联系人
        dto.setName("测试联系人改名");
        contactService.updateContact(ctId, dto);

        List<ActivityLogVO> logsAfterUpdate = activityLogService.listContactActivities(ctId, 10);
        assertTrue(logsAfterUpdate.stream().anyMatch(l -> "CONTACT_UPDATED".equals(l.getEventType())));

        // 加入黑名单
        contactService.addToBlacklist(ctId);
        List<ActivityLogVO> logsAfterBlacklist = activityLogService.listContactActivities(ctId, 10);
        assertTrue(logsAfterBlacklist.stream().anyMatch(l -> "BLACKLIST_CHANGED".equals(l.getEventType())));
    }

    @Test
    public void testTodoOperationsTraceability() {
        // 创建联系人
        ContactSaveDTO contactDto = new ContactSaveDTO();
        contactDto.setName("测试事项联系人");
        ContactVO createdContact = contactService.createContact(contactDto);
        String ctId = createdContact.getContactId();

        // 创建事项
        TodoCreateDTO todoDto = new TodoCreateDTO();
        todoDto.setContactId(ctId);
        todoDto.setContent("TDD 测试待办事项");
        todoDto.setTodoTime(LocalDateTime.now().plusDays(1));
        todoDto.setPriority(0);
        TodoVO createdTodo = todoService.createTodo(todoDto);
        String matterId = createdTodo.getMatterId();

        List<ActivityLogVO> logsAfterTodoCreate = activityLogService.listContactActivities(ctId, 10);
        assertTrue(logsAfterTodoCreate.stream().anyMatch(l -> "TODO_CREATED".equals(l.getEventType())));

        // 完成事项
        todoService.completeTodo(matterId);
        List<ActivityLogVO> logsAfterTodoComplete = activityLogService.listContactActivities(ctId, 10);
        assertTrue(logsAfterTodoComplete.stream().anyMatch(l -> "TODO_COMPLETED".equals(l.getEventType())));
    }

    @Test
    public void testAccessControlAndNotFound() {
        assertThrows(BusinessException.class, () -> {
            activityLogService.listContactActivities("NON_EXISTENT_CT", 10);
        });
    }
}
