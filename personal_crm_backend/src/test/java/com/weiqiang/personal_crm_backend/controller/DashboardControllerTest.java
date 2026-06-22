package com.weiqiang.personal_crm_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.weiqiang.personal_crm_backend.entity.Contact;
import com.weiqiang.personal_crm_backend.entity.Todo;
import com.weiqiang.personal_crm_backend.mapper.ContactMapper;
import com.weiqiang.personal_crm_backend.mapper.TodoMapper;
import com.weiqiang.personal_crm_backend.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * DashboardController Unit Test
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private TodoMapper todoMapper;

    private String token;
    private String otherToken;

    private int initialContactCount;
    private int initialBlacklistCount;
    private int initialPendingTodoCount;
    private int initialTodayTodoCount;
    private int initialOverdueTodoCount;

    private int initialUnknownGenderCount;
    private int initialMaleGenderCount;
    private int initialFemaleGenderCount;

    @BeforeEach
    void setUp() {
        token = "Bearer " + jwtUtils.generateToken("U000000001");
        otherToken = "Bearer " + jwtUtils.generateToken("U000000002");

        String userId = "U000000001";

        initialContactCount = contactMapper.selectCount(
                new LambdaQueryWrapper<Contact>().eq(Contact::getUserId, userId).eq(Contact::getStatus, 0)
        ).intValue();

        initialBlacklistCount = contactMapper.selectCount(
                new LambdaQueryWrapper<Contact>().eq(Contact::getUserId, userId).eq(Contact::getStatus, 1)
        ).intValue();

        initialPendingTodoCount = todoMapper.selectCount(
                new LambdaQueryWrapper<Todo>().eq(Todo::getUserId, userId).eq(Todo::getStatus, 0)
        ).intValue();

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX);
        initialTodayTodoCount = todoMapper.selectCount(
                new LambdaQueryWrapper<Todo>()
                        .eq(Todo::getUserId, userId)
                        .eq(Todo::getStatus, 0)
                        .ge(Todo::getTodoTime, startOfToday)
                        .le(Todo::getTodoTime, endOfToday)
        ).intValue();

        initialOverdueTodoCount = todoMapper.selectCount(
                new LambdaQueryWrapper<Todo>()
                        .eq(Todo::getUserId, userId)
                        .eq(Todo::getStatus, 0)
                        .lt(Todo::getTodoTime, LocalDateTime.now())
        ).intValue();

        initialUnknownGenderCount = contactMapper.selectCount(
                new LambdaQueryWrapper<Contact>().eq(Contact::getUserId, userId).eq(Contact::getStatus, 0).eq(Contact::getGender, 0)
        ).intValue();

        initialMaleGenderCount = contactMapper.selectCount(
                new LambdaQueryWrapper<Contact>().eq(Contact::getUserId, userId).eq(Contact::getStatus, 0).eq(Contact::getGender, 1)
        ).intValue();

        initialFemaleGenderCount = contactMapper.selectCount(
                new LambdaQueryWrapper<Contact>().eq(Contact::getUserId, userId).eq(Contact::getStatus, 0).eq(Contact::getGender, 2)
        ).intValue();

        // 1. Male Normal Contact
        Contact contact1 = new Contact();
        contact1.setUserId(userId);
        contact1.setCtId("CT" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)));
        contact1.setName("Male Normal");
        contact1.setGender(1);
        contact1.setStatus(0);
        contactMapper.insert(contact1);

        // 2. Unknown Normal Contact
        Contact contact2 = new Contact();
        contact2.setUserId(userId);
        contact2.setCtId("CT" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)));
        contact2.setName("Unknown Normal");
        contact2.setGender(0);
        contact2.setStatus(0);
        contactMapper.insert(contact2);

        // 3. Female Blacklist Contact
        Contact contact3 = new Contact();
        contact3.setUserId(userId);
        contact3.setCtId("CT" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)));
        contact3.setName("Female Blacklist");
        contact3.setGender(2);
        contact3.setStatus(1);
        contactMapper.insert(contact3);

        // 4. Today Todo (pending)
        Todo todo1 = new Todo();
        todo1.setMatterId("M0" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)));
        todo1.setUserId(userId);
        todo1.setContactId(contact1.getCtId());
        todo1.setTodoTime(LocalDateTime.now().withHour(12).withMinute(0));
        todo1.setContent("Today Todo");
        todo1.setStatus(0);
        todoMapper.insert(todo1);

        // 5. Overdue Todo (pending)
        Todo todo2 = new Todo();
        todo2.setMatterId("M0" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)));
        todo2.setUserId(userId);
        todo2.setContactId(contact1.getCtId());
        todo2.setTodoTime(LocalDateTime.now().minusDays(1));
        todo2.setContent("Overdue Todo");
        todo2.setStatus(0);
        todoMapper.insert(todo2);

        // 6. Completed Todo
        Todo todo3 = new Todo();
        todo3.setMatterId("M0" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)));
        todo3.setUserId(userId);
        todo3.setContactId(contact1.getCtId());
        todo3.setTodoTime(LocalDateTime.now().withHour(15).withMinute(0));
        todo3.setContent("Completed Todo");
        todo3.setStatus(2);
        todoMapper.insert(todo3);

        // --- Other User Data ---
        Contact otherContact = new Contact();
        otherContact.setUserId("U000000002");
        otherContact.setCtId("CT" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)));
        otherContact.setName("Other Normal");
        otherContact.setGender(2);
        otherContact.setStatus(0);
        contactMapper.insert(otherContact);

        Todo otherTodo = new Todo();
        otherTodo.setMatterId("M0" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)));
        otherTodo.setUserId("U000000002");
        otherTodo.setContactId(otherContact.getCtId());
        otherTodo.setTodoTime(LocalDateTime.now().withHour(10).withMinute(0));
        otherTodo.setContent("Other User Todo");
        otherTodo.setStatus(0);
        todoMapper.insert(otherTodo);
    }

    @Test
    void testGetOverview_Success() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/overview")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.contactCount", is(initialContactCount + 2)))
                .andExpect(jsonPath("$.data.blacklistCount", is(initialBlacklistCount + 1)))
                .andExpect(jsonPath("$.data.pendingTodoCount", is(initialPendingTodoCount + 2)))
                .andExpect(jsonPath("$.data.todayTodoCount", is(initialTodayTodoCount + 1)))
                .andExpect(jsonPath("$.data.overdueTodoCount", is(initialOverdueTodoCount + 1)));
    }

    @Test
    void testGetTodoTrend_Success() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/todo-trend?days=7")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data", hasSize(7)))
                .andExpect(jsonPath("$.data[0].date", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$.data[0].count", is(initialTodayTodoCount + 1)));
    }

    @Test
    void testGetContactGenderDistribution_Success() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/contact-gender-distribution")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].gender", is(0)))
                .andExpect(jsonPath("$.data[0].name", is("\u672a\u77e5")))
                .andExpect(jsonPath("$.data[0].count", is(initialUnknownGenderCount + 1)))
                .andExpect(jsonPath("$.data[1].gender", is(1)))
                .andExpect(jsonPath("$.data[1].name", is("\u7537")))
                .andExpect(jsonPath("$.data[1].count", is(initialMaleGenderCount + 1)))
                .andExpect(jsonPath("$.data[2].gender", is(2)))
                .andExpect(jsonPath("$.data[2].name", is("\u5973")))
                .andExpect(jsonPath("$.data[2].count", is(initialFemaleGenderCount)));
    }
}
