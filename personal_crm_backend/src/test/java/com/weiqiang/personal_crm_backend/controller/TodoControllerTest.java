package com.weiqiang.personal_crm_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiqiang.personal_crm_backend.entity.Contact;
import com.weiqiang.personal_crm_backend.entity.Todo;
import com.weiqiang.personal_crm_backend.mapper.ContactMapper;
import com.weiqiang.personal_crm_backend.mapper.TodoMapper;
import com.weiqiang.personal_crm_backend.model.dto.TodoCreateDTO;
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
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 事项控制器集成测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private TodoMapper todoMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private String otherToken;
    private String user1ContactId;
    private String user2ContactId;
    private String user1PendingTodoMatterId;
    private String user1CompletedTodoMatterId;
    private String user2PendingTodoMatterId;

    private String createJsonBody(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    @BeforeEach
    void setUp() {
        // 生成两组用户的 Token
        token = "Bearer " + jwtUtils.generateToken("U000000001");
        otherToken = "Bearer " + jwtUtils.generateToken("U000000002");

        // 插入属于 U000000001 的联系人
        user1ContactId = "C" + String.format("%09d", ThreadLocalRandom.current().nextInt(1_000_000, 999_999_999));
        Contact contact1 = new Contact();
        contact1.setUserId("U000000001");
        contact1.setCtId(user1ContactId);
        contact1.setName("User1 Contact");
        contact1.setPhone("13800000001");
        contact1.setStatus(0);
        contactMapper.insert(contact1);

        // 插入属于 U000000002 的联系人
        user2ContactId = "C" + String.format("%09d", ThreadLocalRandom.current().nextInt(1_000_000, 999_999_999));
        Contact contact2 = new Contact();
        contact2.setUserId("U000000002");
        contact2.setCtId(user2ContactId);
        contact2.setName("User2 Contact");
        contact2.setPhone("13800000002");
        contact2.setStatus(0);
        contactMapper.insert(contact2);

        // 插入属于 U000000001 的待完成事项
        user1PendingTodoMatterId = "T" + String.format("%09d", ThreadLocalRandom.current().nextInt(1_000_000, 999_999_999));
        Todo todo1 = new Todo();
        todo1.setMatterId(user1PendingTodoMatterId);
        todo1.setUserId("U000000001");
        todo1.setContactId(user1ContactId);
        todo1.setTodoTime(LocalDateTime.now().plusDays(1));
        todo1.setContent("User1 Pending Todo");
        todo1.setStatus(0);
        todo1.setPriority(1);
        todoMapper.insert(todo1);

        // 插入属于 U000000001 的已完成事项
        user1CompletedTodoMatterId = "T" + String.format("%09d", ThreadLocalRandom.current().nextInt(1_000_000, 999_999_999));
        Todo todo2 = new Todo();
        todo2.setMatterId(user1CompletedTodoMatterId);
        todo2.setUserId("U000000001");
        todo2.setContactId(user1ContactId);
        todo2.setTodoTime(LocalDateTime.now().minusDays(1));
        todo2.setContent("User1 Completed Todo");
        todo2.setStatus(2);
        todo2.setPriority(0);
        todo2.setCompletedAt(LocalDateTime.now().minusHours(2));
        todoMapper.insert(todo2);

        // 插入属于 U000000002 的待完成事项
        user2PendingTodoMatterId = "T" + String.format("%09d", ThreadLocalRandom.current().nextInt(1_000_000, 999_999_999));
        Todo todo3 = new Todo();
        todo3.setMatterId(user2PendingTodoMatterId);
        todo3.setUserId("U000000002");
        todo3.setContactId(user2ContactId);
        todo3.setTodoTime(LocalDateTime.now().plusDays(2));
        todo3.setContent("User2 Pending Todo");
        todo3.setStatus(0);
        todo3.setPriority(2);
        todoMapper.insert(todo3);
    }

    @Test
    void testListTodos_Success() throws Exception {
        mockMvc.perform(get("/api/v1/todos")
                        .header("Authorization", token)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.list", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.data.list[*].content", hasItems("User1 Pending Todo", "User1 Completed Todo")))
                .andExpect(jsonPath("$.data.list[*].contactName", hasItem("User1 Contact")))
                .andExpect(jsonPath("$.data.total", greaterThanOrEqualTo(2)));
    }

    @Test
    void testListTodos_FilterByStatus() throws Exception {
        mockMvc.perform(get("/api/v1/todos")
                        .header("Authorization", token)
                        .param("status", "2")) // 已完成
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.list", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.list[*].content", hasItem("User1 Completed Todo")))
                .andExpect(jsonPath("$.data.total", greaterThanOrEqualTo(1)));
    }

    @Test
    void testListTodos_FilterByContact() throws Exception {
        mockMvc.perform(get("/api/v1/todos")
                        .header("Authorization", token)
                        .param("contactId", user1ContactId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.list", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.data.list[*].matterId", hasItems(user1PendingTodoMatterId, user1CompletedTodoMatterId)));
    }

    @Test
    void testListTodos_Unauthorized() throws Exception {
        // 未携带授权 Header
        mockMvc.perform(get("/api/v1/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40101)));
    }

    @Test
    void testCreateTodo_Success() throws Exception {
        TodoCreateDTO dto = new TodoCreateDTO();
        dto.setContactId(user1ContactId);
        dto.setTodoTime(LocalDateTime.now().plusDays(3));
        dto.setContent("New Todo Item");
        dto.setPriority(2); // 紧急

        mockMvc.perform(post("/api/v1/todos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.content", is("New Todo Item")))
                .andExpect(jsonPath("$.data.contactName", is("User1 Contact")))
                .andExpect(jsonPath("$.data.matterId", notNullValue()))
                .andExpect(jsonPath("$.data.overdue", is(false)));
    }

    @Test
    void testCreateTodo_ValidationFailed() throws Exception {
        TodoCreateDTO dto = new TodoCreateDTO();
        dto.setContactId(""); // 必填校验不通过
        dto.setTodoTime(null);
        dto.setContent("");
        dto.setPriority(5); // 优先级应在 0~2

        mockMvc.perform(post("/api/v1/todos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)));
    }

    @Test
    void testCreateTodo_Forbidden_CrossUserContact() throws Exception {
        // 使用 User1 的 token 创建关联 User2 联系人的事项
        TodoCreateDTO dto = new TodoCreateDTO();
        dto.setContactId(user2ContactId); // User2的联系人
        dto.setTodoTime(LocalDateTime.now().plusDays(3));
        dto.setContent("Forbidden Cross User Todo");
        dto.setPriority(1);

        mockMvc.perform(post("/api/v1/todos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40301)));
    }

    @Test
    void testCompleteTodo_Success() throws Exception {
        mockMvc.perform(patch("/api/v1/todos/" + user1PendingTodoMatterId + "/complete")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        // 再次查看该事项的完成状态
        mockMvc.perform(get("/api/v1/todos")
                        .header("Authorization", token)
                        .param("status", "2")) // 已完成
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[*].matterId", hasItem(user1PendingTodoMatterId)));
    }

    @Test
    void testCompleteTodo_Forbidden_CrossUserTodo() throws Exception {
        // 使用 User2 的 token 完成 User1 的事项
        mockMvc.perform(patch("/api/v1/todos/" + user1PendingTodoMatterId + "/complete")
                        .header("Authorization", otherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40301)));
    }

    @Test
    void testCompleteTodo_Conflict_AlreadyCompleted() throws Exception {
        // 对已经是 Completed 状态的事项发起 complete 动作，应该报 40901 状态冲突
        mockMvc.perform(patch("/api/v1/todos/" + user1CompletedTodoMatterId + "/complete")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40901)));
    }

    @Test
    void testCancelTodo_Success() throws Exception {
        mockMvc.perform(patch("/api/v1/todos/" + user1PendingTodoMatterId + "/cancel")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        // 查看是否成功变为已取消 (status = 1)
        mockMvc.perform(get("/api/v1/todos")
                        .header("Authorization", token)
                        .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[*].matterId", hasItem(user1PendingTodoMatterId)));
    }

    @Test
    void testCancelTodo_Conflict_AlreadyCompleted() throws Exception {
        // 对已经是 Completed 状态的事项发起 cancel 动作，应该报 40901 状态冲突
        mockMvc.perform(patch("/api/v1/todos/" + user1CompletedTodoMatterId + "/cancel")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40901)));
    }
}
