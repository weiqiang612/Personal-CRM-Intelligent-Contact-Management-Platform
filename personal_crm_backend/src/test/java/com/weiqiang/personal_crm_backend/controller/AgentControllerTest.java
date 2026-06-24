package com.weiqiang.personal_crm_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiqiang.personal_crm_backend.entity.Contact;
import com.weiqiang.personal_crm_backend.entity.Todo;
import com.weiqiang.personal_crm_backend.mapper.ContactMapper;
import com.weiqiang.personal_crm_backend.mapper.TodoMapper;
import com.weiqiang.personal_crm_backend.model.dto.AgentQueryParam;
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
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Agent 模块控制器集成测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class AgentControllerTest {

    private static final DateTimeFormatter TODO_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
    private String testCtId;
    private String testMatterId;

    private String createJsonBody(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    @BeforeEach
    void setUp() {
        // 生成测试 Token
        token = "Bearer " + jwtUtils.generateToken("U000000001");
        otherToken = "Bearer " + jwtUtils.generateToken("U000000002");

        // 插入测试联系人“张小三”
        testCtId = "C9" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000));
        Contact contact = new Contact();
        contact.setUserId("U000000001");
        contact.setCtId(testCtId);
        contact.setName("张小三");
        contact.setPhone("13912345678");
        contact.setWechat("zhangxiaosan_wechat");
        contact.setStatus(0);
        contactMapper.insert(contact);

        // 插入测试事项
        testMatterId = "T9" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000));
        Todo todo = new Todo();
        todo.setUserId("U000000001");
        todo.setMatterId(testMatterId);
        todo.setContactId(testCtId);
        todo.setContent("联系张小三确认实习协议");
        todo.setStatus(0); // 待完成
        todo.setPriority(2);
        todo.setTodoTime(LocalDateTime.now().plusDays(3));
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());
        todoMapper.insert(todo);
    }

    @Test
    void testQueryContact_Success() throws Exception {
        AgentQueryParam param = new AgentQueryParam();
        param.setInput("帮我查一下张小三的联系方式");

        mockMvc.perform(post("/api/v1/agent/query")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(param)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.queryType", is("contact")))
                .andExpect(jsonPath("$.data.intent", is("query_contact")))
                .andExpect(jsonPath("$.data.summary", containsString("张小三")))
                .andExpect(jsonPath("$.data.results", hasSize(1)))
                .andExpect(jsonPath("$.data.results[0].name", is("张小三")))
                .andExpect(jsonPath("$.data.results[0].phone", is("13912345678")));
    }

    @Test
    void testQueryContact_NoResult() throws Exception {
        AgentQueryParam param = new AgentQueryParam();
        param.setInput("查找不存在的人");

        mockMvc.perform(post("/api/v1/agent/query")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(param)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.queryType", is("contact")))
                .andExpect(jsonPath("$.data.summary", containsString("未查找到匹配")));
    }

    @Test
    void testQueryTodo_Success() throws Exception {
        AgentQueryParam param = new AgentQueryParam();
        param.setInput("查张小三最近的待办事项");

        mockMvc.perform(post("/api/v1/agent/query")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(param)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.queryType", is("todo")))
                .andExpect(jsonPath("$.data.intent", is("query_todo")))
                .andExpect(jsonPath("$.data.summary", containsString("张小三")))
                .andExpect(jsonPath("$.data.results", hasSize(1)))
                .andExpect(jsonPath("$.data.results[0].content", containsString("确认实习协议")));
    }

    @Test
    void testQueryUnsupported_Rejected() throws Exception {
        AgentQueryParam param = new AgentQueryParam();
        param.setInput("帮我创建一个明天的提醒");

        mockMvc.perform(post("/api/v1/agent/query")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(param)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.queryType", is("unsupported")))
                .andExpect(jsonPath("$.data.intent", is("unsupported")))
                .andExpect(jsonPath("$.data.summary", containsString("暂不支持")));
    }

    @Test
    void testQueryEmptyInput_Fail() throws Exception {
        AgentQueryParam param = new AgentQueryParam();
        param.setInput("   "); // 空白输入

        mockMvc.perform(post("/api/v1/agent/query")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(param)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", not(0))); // 校验失败
    }

    @Test
    void testQueryTenantIsolation_Success() throws Exception {
        AgentQueryParam param = new AgentQueryParam();
        param.setInput("查张小三");

        // 使用 U000000002 的 Token 应该查不到 U000000001 的张小三
        mockMvc.perform(post("/api/v1/agent/query")
                        .header("Authorization", otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(param)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.queryType", is("contact")))
                .andExpect(jsonPath("$.data.results", hasSize(0)))
                .andExpect(jsonPath("$.data.summary", containsString("未查找到")));
    }

    @Test
    void testQueryTodo_WithCompletedStatus_Success() throws Exception {
        Todo completedTodo = new Todo();
        completedTodo.setUserId("U000000001");
        completedTodo.setMatterId("T9" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)));
        completedTodo.setContactId(testCtId);
        completedTodo.setContent("联系张小三确认实习协议_已完成");
        completedTodo.setStatus(2); // 已完成
        completedTodo.setPriority(2);
        completedTodo.setTodoTime(LocalDateTime.now().plusDays(1));
        completedTodo.setCreatedAt(LocalDateTime.now());
        completedTodo.setUpdatedAt(LocalDateTime.now());
        todoMapper.insert(completedTodo);

        AgentQueryParam param = new AgentQueryParam();
        param.setInput("查张小三已完成的事项");

        mockMvc.perform(post("/api/v1/agent/query")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(param)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.queryType", is("todo")))
                .andExpect(jsonPath("$.data.intent", is("query_todo")))
                .andExpect(jsonPath("$.data.summary", containsString("已完成")))
                .andExpect(jsonPath("$.data.results", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testQueryTodo_WithCancelledStatus_Success() throws Exception {
        Todo cancelledTodo = new Todo();
        cancelledTodo.setUserId("U000000001");
        cancelledTodo.setMatterId("T9" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000)));
        cancelledTodo.setContactId(testCtId);
        cancelledTodo.setContent("联系张小三确认实习协议_已取消");
        cancelledTodo.setStatus(1); // 已取消
        cancelledTodo.setPriority(1);
        cancelledTodo.setTodoTime(LocalDateTime.now().plusDays(2));
        cancelledTodo.setCreatedAt(LocalDateTime.now());
        cancelledTodo.setUpdatedAt(LocalDateTime.now());
        todoMapper.insert(cancelledTodo);

        AgentQueryParam param = new AgentQueryParam();
        param.setInput("查我已取消的待办");

        mockMvc.perform(post("/api/v1/agent/query")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(param)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.queryType", is("todo")))
                .andExpect(jsonPath("$.data.intent", is("query_todo")))
                .andExpect(jsonPath("$.data.summary", containsString("已取消")))
                .andExpect(jsonPath("$.data.results", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testQueryUnsupported_WriteActionVerbs_Rejected() throws Exception {
        String[] inputs = {"完成张三的待办", "取消会议提醒"};
        for (String input : inputs) {
            AgentQueryParam param = new AgentQueryParam();
            param.setInput(input);

            mockMvc.perform(post("/api/v1/agent/query")
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createJsonBody(param)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code", is(0)))
                    .andExpect(jsonPath("$.data.queryType", is("unsupported")))
                    .andExpect(jsonPath("$.data.intent", is("unsupported")));
        }
    }

    @Test
    void testExecute_SuccessPreConfirmation() throws Exception {
        com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam param = new com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam();
        param.setInput("明天下午三点提醒我联系张小三确认合同");
        String expectedTodoTime = LocalDateTime.now().plusDays(1).withHour(15).withMinute(0).withSecond(0).withNano(0)
                .format(TODO_TIME_FORMATTER);

        mockMvc.perform(post("/api/v1/agent/execute")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(param)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.needConfirm", is(1)))
                .andExpect(jsonPath("$.data.actionType", is("create_todo")))
                .andExpect(jsonPath("$.data.parsedParams.contactName", is("张小三")))
                .andExpect(jsonPath("$.data.parsedParams.todoTime", is(expectedTodoTime)))
                .andExpect(jsonPath("$.data.parsedParams.content", is("确认合同")));
    }

    @Test
    void testExecute_UnsupportedAction() throws Exception {
        com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam param = new com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam();
        param.setInput("删除张小三的联系方式");

        mockMvc.perform(post("/api/v1/agent/execute")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(param)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.needConfirm", is(0)))
                .andExpect(jsonPath("$.data.actionType", is("unsupported")))
                .andExpect(jsonPath("$.data.summary", containsString("仅支持“创建事项”")));
    }

    @Test
    void testExecute_MissingContact() throws Exception {
        com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam param = new com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam();
        param.setInput("明天下午三点提醒我确认合同");

        mockMvc.perform(post("/api/v1/agent/execute")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(param)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.needConfirm", is(0)))
                .andExpect(jsonPath("$.data.summary", containsString("无法识别您想为哪位联系人")));
    }

    @Test
    void testExecute_MissingTime() throws Exception {
        com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam param = new com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam();
        param.setInput("提醒我联系张小三确认合同");

        mockMvc.perform(post("/api/v1/agent/execute")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(param)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.needConfirm", is(0)))
                .andExpect(jsonPath("$.data.summary", containsString("无法识别事项提醒时间")));
    }

    @Test
    void testExecute_MissingContent() throws Exception {
        com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam param = new com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam();
        param.setInput("明天下午三点提醒我联系张小三");

        mockMvc.perform(post("/api/v1/agent/execute")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(param)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.needConfirm", is(0)))
                .andExpect(jsonPath("$.data.summary", containsString("无法识别事项内容")));
    }

    @Test
    void testConfirm_Success() throws Exception {
        String expectedTodoTime = LocalDateTime.now().plusDays(1).withHour(15).withMinute(0).withSecond(0).withNano(0)
                .format(TODO_TIME_FORMATTER);

        // 1. 预确认
        com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam execParam = new com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam();
        execParam.setInput("明天下午三点提醒我联系张小三确认合同");

        String responseStr = mockMvc.perform(post("/api/v1/agent/execute")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(execParam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andReturn().getResponse().getContentAsString();

        // 解析出 logId
        java.util.Map<String, Object> responseMap = objectMapper.readValue(responseStr, java.util.Map.class);
        java.util.Map<String, Object> dataMap = (java.util.Map<String, Object>) responseMap.get("data");
        Number logIdNum = (Number) dataMap.get("logId");
        Long logId = logIdNum.longValue();

        // 2. 确认执行
        com.weiqiang.personal_crm_backend.model.dto.AgentConfirmParam confirmParam = new com.weiqiang.personal_crm_backend.model.dto.AgentConfirmParam();
        confirmParam.setLogId(logId);
        confirmParam.setAction("confirm");

        mockMvc.perform(post("/api/v1/agent/confirm")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(confirmParam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.contactName", is("张小三")))
                .andExpect(jsonPath("$.data.content", is("确认合同")))
                .andExpect(jsonPath("$.data.status", is(0)))
                .andExpect(jsonPath("$.data.todoTime", is(expectedTodoTime)));
    }

    @Test
    void testConfirm_Cancel() throws Exception {
        // 1. 预确认
        com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam execParam = new com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam();
        execParam.setInput("明天下午三点提醒我联系张小三确认合同");

        String responseStr = mockMvc.perform(post("/api/v1/agent/execute")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(execParam)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        java.util.Map<String, Object> responseMap = objectMapper.readValue(responseStr, java.util.Map.class);
        java.util.Map<String, Object> dataMap = (java.util.Map<String, Object>) responseMap.get("data");
        Number logIdNum = (Number) dataMap.get("logId");
        Long logId = logIdNum.longValue();

        // 2. 取消执行
        com.weiqiang.personal_crm_backend.model.dto.AgentConfirmParam confirmParam = new com.weiqiang.personal_crm_backend.model.dto.AgentConfirmParam();
        confirmParam.setLogId(logId);
        confirmParam.setAction("cancel");

        mockMvc.perform(post("/api/v1/agent/confirm")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(confirmParam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data", nullValue()));
    }

    @Test
    void testConfirm_HorizontalPrivilegeEscalation_Forbidden() throws Exception {
        // 1. 预确认 (用户1)
        com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam execParam = new com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam();
        execParam.setInput("明天下午三点提醒我联系张小三确认合同");

        String responseStr = mockMvc.perform(post("/api/v1/agent/execute")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(execParam)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        java.util.Map<String, Object> responseMap = objectMapper.readValue(responseStr, java.util.Map.class);
        java.util.Map<String, Object> dataMap = (java.util.Map<String, Object>) responseMap.get("data");
        Number logIdNum = (Number) dataMap.get("logId");
        Long logId = logIdNum.longValue();

        // 2. 二次确认 (由用户2 otherToken 越权调用)
        com.weiqiang.personal_crm_backend.model.dto.AgentConfirmParam confirmParam = new com.weiqiang.personal_crm_backend.model.dto.AgentConfirmParam();
        confirmParam.setLogId(logId);
        confirmParam.setAction("confirm");

        mockMvc.perform(post("/api/v1/agent/confirm")
                        .header("Authorization", otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJsonBody(confirmParam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40301)))
                .andExpect(jsonPath("$.message", containsString("无权操作此日志记录")));
    }
}
