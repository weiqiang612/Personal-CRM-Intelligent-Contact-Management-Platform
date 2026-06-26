package com.weiqiang.personal_crm_backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.entity.AgentOperationLog;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.mapper.AgentOperationLogMapper;
import com.weiqiang.personal_crm_backend.model.dto.*;
import com.weiqiang.personal_crm_backend.model.vo.*;
import com.weiqiang.personal_crm_backend.security.AgentSessionManager;
import com.weiqiang.personal_crm_backend.security.UserContext;
import com.weiqiang.personal_crm_backend.service.AgentService;
import com.weiqiang.personal_crm_backend.service.ContactService;
import com.weiqiang.personal_crm_backend.service.LlmService;
import com.weiqiang.personal_crm_backend.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Agent 业务服务实现类（已根据 Code Review 指导修复强转与 500 分页漏洞）
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AgentServiceImpl implements AgentService {

    private static final DateTimeFormatter TODO_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ContactService contactService;
    private final TodoService todoService;
    private final LlmService llmService;
    private final AgentSessionManager agentSessionManager;
    private final AgentOperationLogMapper agentOperationLogMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AgentQueryResponseVO query(AgentQueryParam param) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }

        String input = param.getInput() != null ? param.getInput().trim() : "";
        if (input.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入内容不能为空");
        }

        // 强拦截：如果查询接口输入包含强写词或弱写词，直接快速拦截并返回 unsupported
        boolean hasStrongWriteAction = input.matches(".*(创建|添加|新建|修改|更新|删除|新增|拉黑|恢复).*");
        boolean hasWeakWriteAction = input.matches(".*(?<!已)(完成|取消)(?!的).*");
        if (hasStrongWriteAction || hasWeakWriteAction) {
            AgentQueryResponseVO response = new AgentQueryResponseVO();
            response.setQueryType("unsupported");
            response.setIntent("unsupported");
            response.setSummary("抱歉，本期智能助手仅支持查询联系人和事项，暂不支持创建、修改或拉黑等操作。");
            response.setResults(Collections.emptyList());
            return response;
        }

        // 复用通用的流转处理逻辑
        AgentExecuteResponseVO executeRes = processAgentRequest(input, param.getSessionId(), userId);

        // 如果在查询接口中底层解析为了写操作，则在查询接口也同样强制拦截为 unsupported
        if ("create_todo".equals(executeRes.getActionType())) {
            AgentQueryResponseVO response = new AgentQueryResponseVO();
            response.setQueryType("unsupported");
            response.setIntent("unsupported");
            response.setSummary("抱歉，本期智能助手仅支持查询联系人和事项，暂不支持创建、修改等写操作。");
            response.setResults(Collections.emptyList());
            return response;
        }

        // 将全集字段的 executeRes 转换包装为 AgentQueryResponseVO
        AgentQueryResponseVO response = new AgentQueryResponseVO();
        String actionType = executeRes.getActionType();
        response.setIntent(actionType);
        
        String queryType = "unsupported";
        if ("query_contact".equals(actionType)) {
            queryType = "contact";
        } else if ("query_todo".equals(actionType)) {
            queryType = "todo";
        }
        response.setQueryType(queryType);
        response.setSummary(executeRes.getSummary());
        response.setResults(executeRes.getResults() != null ? executeRes.getResults() : Collections.emptyList());
        response.setSessionId(executeRes.getSessionId());
        response.setIsClarifying(executeRes.getIsClarifying());
        response.setMissingFields(executeRes.getMissingFields());
        response.setParsedParams(executeRes.getParsedParams());

        return response;
    }

    @Override
    public AgentExecuteResponseVO execute(AgentExecuteParam param) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }

        String input = param.getInput() != null ? param.getInput().trim() : "";
        if (input.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入内容不能为空");
        }

        return processAgentRequest(input, param.getSessionId(), userId);
    }

    /**
     * 通用的 Agent 自然语言核心流转处理（大模型解析 + 二阶段本地库校验 + 多轮澄清）
     */
    private AgentExecuteResponseVO processAgentRequest(String input, String sessionId, String userId) {
        // 1. 获取或创建会话状态
        AgentSessionState sessionState = null;
        if (sessionId != null && !sessionId.trim().isEmpty()) {
            sessionState = agentSessionManager.getSession(sessionId, userId);
        }
        if (sessionState == null) {
            sessionState = agentSessionManager.createSession(userId);
        }
        String currentSessionId = sessionState.getSessionId();

        // 2. 提取并获取候选联系人列表（仅提取前 100 个人的名字注入 System Prompt 充当上下文背景指示）
        ContactQueryParam allParam = new ContactQueryParam();
        allParam.setPage(1);
        allParam.setPageSize(100);
        ContactListVO allContacts = contactService.listContacts(allParam);
        @SuppressWarnings("unchecked")
        List<ContactVO> contactList = allContacts != null && allContacts.getList() != null ? 
                (List<ContactVO>) allContacts.getList() : Collections.emptyList();
        
        List<String> contactNames = new ArrayList<>();
        for (ContactVO c : contactList) {
            contactNames.add(c.getName());
        }

        // 3. 构建大模型消息历史上下文
        List<OpenAiMessage> messages = sessionState.getMessages();
        if (messages.isEmpty()) {
            // 第一轮会话，写入 System Prompt
            messages.add(new OpenAiMessage("system", buildSystemPrompt(contactNames)));
        } else {
            // 已有会话，更新第一条 system 的时间等动态参数，保持计算基准准确
            messages.set(0, new OpenAiMessage("system", buildSystemPrompt(contactNames)));
        }
        
        // 追入用户最新一轮输入
        messages.add(new OpenAiMessage("user", input));

        // 4. 发送大模型调用
        String chatResult = llmService.chat(messages);
        String cleanedJson = cleanJsonString(chatResult);
        log.debug("Llm raw output after clean: {}", cleanedJson);

        AgentExecuteResponseVO response = new AgentExecuteResponseVO();
        response.setSessionId(currentSessionId);
        response.setNeedConfirm(0); // 默认无需确认

        try {
            Map<?, ?> parsedJson = objectMapper.readValue(cleanedJson, Map.class);
            String intent = parseString(parsedJson.get("intent"));
            String summary = parseString(parsedJson.get("summary"));
            Boolean isClarifying = (Boolean) parsedJson.get("isClarifying");
            @SuppressWarnings("unchecked")
            List<String> missingFields = (List<String>) parsedJson.get("missingFields");
            if (missingFields == null) {
                missingFields = new ArrayList<>();
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> parsedParams = (Map<String, Object>) parsedJson.get("parsedParams");
            if (parsedParams == null) {
                parsedParams = new HashMap<>();
            }

            // 将大模型本轮解析出的属性融入到累计槽位中
            Map<String, Object> accumulatedParams = sessionState.getAccumulatedParams();
            accumulatedParams.putAll(parsedParams);

            response.setActionType(intent);

            // 5. 意图分支分流处理
            if ("query_contact".equals(intent)) {
                // 联系人模糊查询
                String keyword = parseString(accumulatedParams.get("keyword"));
                if (keyword == null || keyword.isEmpty()) {
                    keyword = input; // 兜底使用原始输入
                }
                ContactQueryParam queryParam = new ContactQueryParam();
                queryParam.setKeyword(keyword);
                queryParam.setPage(1);
                queryParam.setPageSize(20);
                ContactListVO listVO = contactService.listContacts(queryParam);
                
                response.setResults(listVO.getList());
                response.setIsClarifying(false);
                response.setMissingFields(Collections.emptyList());
                response.setSummary(listVO.getList() == null || listVO.getList().isEmpty() ? 
                        "未查找到匹配的联系人信息" : 
                        "已查找到包含关键字 '" + keyword + "' 的联系人信息，共 " + listVO.getList().size() + " 条");

                // 单轮查询直接清空会话
                agentSessionManager.removeSession(currentSessionId);
                auditLog(userId, input, intent, accumulatedParams, 0, 1, response.getSummary());

            } else if ("query_todo".equals(intent)) {
                // 事项模糊查询
                String contactName = parseString(accumulatedParams.get("contactName"));
                Integer status = parseInteger(accumulatedParams.get("status"), 0);

                String matchedContactId = null;
                String matchedContactName = null;
                
                // 【已优化】不使用内存全量匹配，直接按大模型解析出的名字模糊查库
                if (contactName != null && !contactName.isEmpty()) {
                    ContactQueryParam qParam = new ContactQueryParam();
                    qParam.setKeyword(contactName);
                    qParam.setPage(1);
                    qParam.setPageSize(20);
                    ContactListVO matchedList = contactService.listContacts(qParam);
                    @SuppressWarnings("unchecked")
                    List<ContactVO> matchedCandidates = matchedList != null && matchedList.getList() != null ? 
                            (List<ContactVO>) matchedList.getList() : Collections.emptyList();
                    if (!matchedCandidates.isEmpty()) {
                        matchedContactId = matchedCandidates.get(0).getContactId();
                        matchedContactName = matchedCandidates.get(0).getName();
                    }
                }

                TodoQuery todoQuery = new TodoQuery();
                todoQuery.setPage(1);
                todoQuery.setPageSize(20);
                todoQuery.setStatus(status);
                if (matchedContactId != null) {
                    todoQuery.setContactId(matchedContactId);
                    accumulatedParams.put("contactId", matchedContactId);
                }

                TodoListVO listVO = todoService.listTodos(todoQuery);
                response.setResults(listVO.getList());
                response.setIsClarifying(false);
                response.setMissingFields(Collections.emptyList());

                String statusName = status == 2 ? "已完成" : (status == 1 ? "已取消" : "待完成");
                if (matchedContactName != null) {
                    response.setSummary(listVO.getList() == null || listVO.getList().isEmpty() ? 
                            "未查找到联系人 '" + matchedContactName + "' 关联的" + statusName + "事项" : 
                            "已查找到联系人 '" + matchedContactName + "' 关联的 " + listVO.getList().size() + " 条" + statusName + "事项");
                } else {
                    response.setSummary(listVO.getList() == null || listVO.getList().isEmpty() ? 
                            "未查找到您的" + statusName + "事项" : 
                            "已查找到您的 " + listVO.getList().size() + " 条" + statusName + "事项");
                }

                // 结束会话
                agentSessionManager.removeSession(currentSessionId);
                auditLog(userId, input, intent, accumulatedParams, 0, 1, response.getSummary());

            } else if ("create_todo".equals(intent)) {
                // 创建事项：执行二阶段业务校验与补槽
                List<String> actualMissingFields = new ArrayList<>();

                // 2阶段校验 1: 验证联系人 【已优化】直接根据大模型抽取的关键字定向库查询，解开全内存 500 页面限制
                String contactName = parseString(accumulatedParams.get("contactName"));
                String matchedContactId = null;
                String matchedContactName = null;

                if (contactName == null || contactName.isEmpty()) {
                    actualMissingFields.add("contactName");
                } else {
                    ContactQueryParam qParam = new ContactQueryParam();
                    qParam.setKeyword(contactName);
                    qParam.setPage(1);
                    qParam.setPageSize(100); // 检索前 100 位候选
                    ContactListVO matchedList = contactService.listContacts(qParam);
                    @SuppressWarnings("unchecked")
                    List<ContactVO> matchedCandidates = matchedList != null && matchedList.getList() != null ? 
                            (List<ContactVO>) matchedList.getList() : new ArrayList<>();

                    if (matchedCandidates.isEmpty()) {
                        actualMissingFields.add("contactName");
                        accumulatedParams.remove("contactName");
                        accumulatedParams.remove("contactId");
                        isClarifying = true;
                        summary = "抱歉，在您的联系人列表中没有找到名为 '" + contactName + "' 的联系人，请检查姓名是否正确，或重新指定。";
                    } else if (matchedCandidates.size() > 1) {
                        // 出现多个候选人，重新置为澄清态并提供候选列表
                        actualMissingFields.add("contactName");
                        accumulatedParams.remove("contactName");
                        accumulatedParams.remove("contactId");
                        isClarifying = true;
                        StringBuilder sb = new StringBuilder("系统为您匹配到了多个同名或相似的联系人：");
                        for (int i = 0; i < matchedCandidates.size(); i++) {
                            sb.append(matchedCandidates.get(i).getName());
                            if (i < matchedCandidates.size() - 1) sb.append("、");
                        }
                        sb.append("，请明确具体是哪一位？");
                        summary = sb.toString();
                    } else {
                        // 锁定唯一匹配
                        matchedContactId = matchedCandidates.get(0).getContactId();
                        matchedContactName = matchedCandidates.get(0).getName();
                        accumulatedParams.put("contactId", matchedContactId);
                        accumulatedParams.put("contactName", matchedContactName);
                    }
                }

                // 2阶段校验 2: 验证时间
                String todoTimeStr = parseString(accumulatedParams.get("todoTime"));
                if (todoTimeStr == null || todoTimeStr.isEmpty()) {
                    actualMissingFields.add("todoTime");
                } else {
                    try {
                        LocalDateTime.parse(todoTimeStr, TODO_TIME_FORMATTER);
                    } catch (Exception e) {
                        actualMissingFields.add("todoTime");
                        accumulatedParams.remove("todoTime");
                        isClarifying = true;
                        summary = "抱歉，无法识别提醒时间，请输入包含具体时间的指令，例如：“明天下午三点”或“2026-06-26 15:00:00”。";
                    }
                }

                // 2阶段校验 3: 验证内容
                String content = parseString(accumulatedParams.get("content"));
                if (content == null || content.isEmpty()) {
                    actualMissingFields.add("content");
                } else {
                    accumulatedParams.put("content", content);
                }

                // 补充默认优先级 【已修复】使用安全数字转型，防 ClassCastException
                Integer priority = parseInteger(accumulatedParams.get("priority"), 1);
                accumulatedParams.put("priority", priority);

                response.setParsedParams(accumulatedParams);

                if (!actualMissingFields.isEmpty() || Boolean.TRUE.equals(isClarifying)) {
                    // 依然缺失关键参数，处于澄清补槽阶段
                    response.setIsClarifying(true);
                    response.setMissingFields(actualMissingFields.isEmpty() ? missingFields : actualMissingFields);
                    response.setSummary(summary);
                    response.setNeedConfirm(0);

                    // 写入 Assistant 回复历史，以维持后续上下文理解
                    messages.add(new OpenAiMessage("assistant", summary));

                    // 预确认缺失时，也写入审计日志，设置 pending (needConfirm = 0, status = 1)
                    AgentOperationLog operationLog = auditLog(userId, input, intent, accumulatedParams, 0, 1, summary);
                    response.setLogId(operationLog.getId());
                } else {
                    // 所有槽位都配齐，生成二次确认卡片
                    response.setIsClarifying(false);
                    response.setMissingFields(Collections.emptyList());
                    response.setNeedConfirm(1);

                    String formattedSummary = "已为您生成待办事项预确认卡片，请核对：在 " + todoTimeStr + " 提醒联系 " + matchedContactName + " " + content + "。";
                    response.setSummary(formattedSummary);

                    // 销毁内存会话
                    agentSessionManager.removeSession(currentSessionId);

                    // 写入预确认 pending 操作审计日志 (needConfirm = 1, status = 0)
                    AgentOperationLog operationLog = auditLog(userId, input, intent, accumulatedParams, 1, 0, formattedSummary);
                    response.setLogId(operationLog.getId());
                }

            } else {
                // 不支持的意图操作
                response.setIsClarifying(false);
                response.setNeedConfirm(0);
                response.setSummary(summary != null && !summary.isEmpty() ? 
                        summary : "抱歉，智能助手目前仅支持“创建事项”的写操作，暂不支持其他类型的写请求。");

                agentSessionManager.removeSession(currentSessionId);
                auditLog(userId, input, "unsupported", accumulatedParams, 0, 1, response.getSummary());
            }

        } catch (Exception e) {
            log.error("Failed to parse Llm result: {}", chatResult, e);
            response.setIsClarifying(false);
            response.setNeedConfirm(0);
            response.setActionType("unsupported");
            response.setSummary("智能助手无法解析当前指令，请重试。");

            agentSessionManager.removeSession(currentSessionId);
            auditLog(userId, input, "unsupported", new HashMap<>(), 0, 1, response.getSummary());
        }

        return response;
    }

    /**
     * 写操作二次确认（执行创建或取消）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object confirm(AgentConfirmParam param) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }

        AgentOperationLog logRecord = agentOperationLogMapper.selectById(param.getLogId());
        if (logRecord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "未找到该操作日志记录");
        }

        // 防越权校验
        if (!userId.equals(logRecord.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作此日志记录");
        }

        // 只有状态处于待确认 (0) 的日志才能被处理
        if (logRecord.getExecutionStatus() != 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "日志状态已完成或已处理，无法重复确认");
        }

        if ("cancel".equalsIgnoreCase(param.getAction())) {
            logRecord.setConfirmed(0);
            logRecord.setExecutionStatus(3); // 3: 已取消
            agentOperationLogMapper.updateById(logRecord);
            return null;
        }

        if ("confirm".equalsIgnoreCase(param.getAction())) {
            Map<?, ?> parsedParams;
            try {
                parsedParams = objectMapper.readValue(logRecord.getParsedParams(), Map.class);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "解析日志预存参数失败");
            }

            // 【已修复】类型安全转型，防 ClassCastException
            String contactId = parseString(parsedParams.get("contactId"));
            String todoTimeStr = parseString(parsedParams.get("todoTime"));
            String content = parseString(parsedParams.get("content"));
            Integer priority = parseInteger(parsedParams.get("priority"), 1);

            if (contactId == null || todoTimeStr == null || content == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "预确认槽位参数缺失，无法创建事项");
            }

            TodoCreateDTO createDTO = new TodoCreateDTO();
            createDTO.setContactId(contactId);
            createDTO.setTodoTime(LocalDateTime.parse(todoTimeStr, TODO_TIME_FORMATTER));
            createDTO.setContent(content);
            createDTO.setPriority(priority);

            // 调用实际的 TodoService 落地存储
            TodoVO todoVO = todoService.createTodo(createDTO);

            // 成功落库后更新操作审计日志
            logRecord.setConfirmed(1);
            logRecord.setExecutionStatus(1); // 1: 成功执行
            try {
                logRecord.setExecutionResult(objectMapper.writeValueAsString(todoVO));
            } catch (Exception e) {
                log.error("JSON serialization error", e);
            }
            agentOperationLogMapper.updateById(logRecord);

            return todoVO;
        }

        throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的确认指令");
    }

    /**
     * 记录审计日志
     */
    private AgentOperationLog auditLog(String userId, String input, String intent, Map<String, Object> params, int needConfirm, int execStatus, String summary) {
        try {
            AgentOperationLog operationLog = new AgentOperationLog();
            operationLog.setUserId(userId);
            operationLog.setUserInput(input);
            operationLog.setIntent(intent);
            operationLog.setParsedParams(objectMapper.writeValueAsString(params));
            operationLog.setNeedConfirm(needConfirm);
            operationLog.setConfirmed(0);
            operationLog.setActionType("create_todo".equals(intent) ? "create_todo" : ("unsupported".equals(intent) ? "unsupported" : "query"));
            operationLog.setExecutionStatus(execStatus);

            Map<String, Object> resultPayload = new HashMap<>();
            resultPayload.put("summary", summary);
            operationLog.setExecutionResult(objectMapper.writeValueAsString(resultPayload));
            operationLog.setCreatedAt(LocalDateTime.now());

            agentOperationLogMapper.insert(operationLog);
            return operationLog;
        } catch (Exception e) {
            log.error("Failed to save audit log to DB", e);
            return new AgentOperationLog();
        }
    }

    /**
     * 剔除 Llm 可能输出的 markdown wrap ```json...```
     */
    private String cleanJsonString(String raw) {
        if (raw == null) {
            return "{}";
        }
        String cleaned = raw.trim();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceAll("^```[a-zA-Z]*\\s*", "");
            cleaned = cleaned.replaceAll("\\s*```$", "");
        }
        return cleaned.trim();
    }

    /**
     * 安全 String 类型转换
     */
    private String parseString(Object val) {
        if (val == null) {
            return null;
        }
        return String.valueOf(val).trim();
    }

    /**
     * 安全 Integer 类型转换
     */
    private Integer parseInteger(Object val, Integer defaultValue) {
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(val).trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 构造系统 Prompt
     */
    private String buildSystemPrompt(List<String> contactNames) {
        LocalDateTime now = LocalDateTime.now();
        String dayOfWeekCn = getDayOfWeekCn(now.getDayOfWeek());
        String candidates = contactNames.isEmpty() ? "（暂无联系人）" : String.join(", ", contactNames);

        return "你是一个个人 CRM 系统的智能助手（Contact Agent）。你的任务是根据用户输入的自然语言，理解其意图并提取出结构化的参数。\n\n" +
                "用户可能会进行以下三类操作：\n" +
                "1. 查询联系人（query_contact）：用户想要查询联系人的信息。\n" +
                "   - 提取参数：keyword (String，模糊查询的关键字，通常是名字、电话、标签等)\n" +
                "2. 查询事项/待办（query_todo）：用户想要查询待办事项。\n" +
                "   - 提取参数：\n" +
                "     - contactName (String，关联的联系人姓名，若未提及则为空)\n" +
                "     - status (Integer，事项状态。0: 待完成, 1: 已取消, 2: 已完成。默认为 0)\n" +
                "3. 创建事项/待办（create_todo）：用户想要创建新的待办事项。这是一个写操作。\n" +
                "   - 必须提取的关键槽位（字段）：\n" +
                "     - contactName (String，关联的联系人姓名)\n" +
                "     - todoTime (String，提醒时间，格式必须是 \"yyyy-MM-dd HH:mm:ss\")\n" +
                "     - content (String，具体事项内容)\n" +
                "     - priority (Integer，优先级。0: 普通, 1: 重要, 2: 紧急。若未提及默认为 1)\n" +
                "   - 注意：如果用户输入中缺少任何一个关键槽位（contactName, todoTime, content），你需要将其列入 `missingFields` 列表中，并将 `isClarifying` 设为 true，并在 `summary` 中以友好、礼貌的中文向用户提出澄清问题（比如询问缺失的信息）。如果所有关键槽位都已齐全，`isClarifying` 设为 false，`missingFields` 为空数组。\n" +
                "4. 不支持的操作（unsupported）：如删除、修改、拉黑、恢复联系人等写操作，或者与 CRM系统无关的输入。\n" +
                "   - 响应中 `isClarifying` 为 false，`summary` 给出友好的不支持提示，如：“抱歉，智能助手目前仅支持“创建事项”的写操作，暂不支持其他类型的写请求。”\n\n" +
                "【上下文信息】\n" +
                "当前系统时间：" + now.format(TODO_TIME_FORMATTER) + " (" + dayOfWeekCn + ") （你的所有相对时间解析，如“明天下午三点”、“下周一”，都应该基于这个基准时间计算。计算时请注意星期和日期）\n" +
                "当前用户的联系人候选名单：" + candidates + " （当用户提及某个名字时，尽量与这个名单里的名字做匹配，或者提取该名字。如果拼写或谐音相似，也可以在 summary 里引导澄清）\n\n" +
                "【约束条件】\n" +
                "- 你必须只返回符合以下 JSON 格式的字符串，绝对不要包含任何 markdown 标记（如 ```json）。\n" +
                "- JSON 结构：\n" +
                "{\n" +
                "  \"intent\": \"query_contact\" | \"query_todo\" | \"create_todo\" | \"unsupported\",\n" +
                "  \"queryType\": \"contact\" | \"todo\" | \"unsupported\",\n" +
                "  \"summary\": \"回复文本\",\n" +
                "  \"isClarifying\": true | false,\n" +
                "  \"missingFields\": [\"contactName\", \"todoTime\", \"content\"],\n" +
                "  \"parsedParams\": {\n" +
                "     \"keyword\": \"\",\n" +
                "     \"contactName\": \"\",\n" +
                "     \"status\": 0,\n" +
                "     \"todoTime\": \"\",\n" +
                "     \"content\": \"\",\n" +
                "     \"priority\": 1\n" +
                "  }\n" +
                "}";
    }

    private String getDayOfWeekCn(DayOfWeek d) {
        switch (d) {
            case MONDAY: return "星期一";
            case TUESDAY: return "星期二";
            case WEDNESDAY: return "星期三";
            case THURSDAY: return "星期四";
            case FRIDAY: return "星期五";
            case SATURDAY: return "星期六";
            case SUNDAY: return "星期日";
            default: return "";
        }
    }
}
