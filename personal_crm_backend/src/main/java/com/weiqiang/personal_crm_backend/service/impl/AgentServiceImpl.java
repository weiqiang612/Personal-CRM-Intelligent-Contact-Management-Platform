package com.weiqiang.personal_crm_backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.entity.AgentOperationLog;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.mapper.AgentOperationLogMapper;
import com.weiqiang.personal_crm_backend.model.dto.AgentQueryParam;
import com.weiqiang.personal_crm_backend.model.dto.AgentConfirmParam;
import com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam;
import com.weiqiang.personal_crm_backend.model.dto.ContactQueryParam;
import com.weiqiang.personal_crm_backend.model.dto.TodoCreateDTO;
import com.weiqiang.personal_crm_backend.model.dto.TodoQuery;
import com.weiqiang.personal_crm_backend.model.vo.AgentExecuteResponseVO;
import com.weiqiang.personal_crm_backend.model.vo.AgentQueryResponseVO;
import com.weiqiang.personal_crm_backend.model.vo.ContactListVO;
import com.weiqiang.personal_crm_backend.model.vo.ContactVO;
import com.weiqiang.personal_crm_backend.model.vo.TodoListVO;
import com.weiqiang.personal_crm_backend.model.vo.TodoVO;
import com.weiqiang.personal_crm_backend.security.UserContext;
import com.weiqiang.personal_crm_backend.service.AgentService;
import com.weiqiang.personal_crm_backend.service.ContactService;
import com.weiqiang.personal_crm_backend.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Agent 业务服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AgentServiceImpl implements AgentService {

    private static final DateTimeFormatter TODO_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ContactService contactService;
    private final TodoService todoService;
    private final AgentOperationLogMapper agentOperationLogMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AgentQueryResponseVO query(AgentQueryParam param) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }

        String input = param.getInput().trim();
        if (input.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入内容不能为空");
        }

        // 1. 意图识别
        String intent;
        String queryType;
        
        // 判断是否为写操作（本期不支持，以友好形式提示）
        // 1. 强写操作词直接匹配：直接拦截
        boolean hasStrongWriteAction = input.matches(".*(创建|添加|新建|修改|更新|删除|新增|拉黑|恢复).*");
        // 2. 弱写操作词条件匹配：包含“完成|取消”，但排除状态修饰（比如前面没有“已”，后面没有“的”等）
        boolean hasWeakWriteAction = input.matches(".*(?<!已)(完成|取消)(?!的).*");
        
        boolean isWriteAction = hasStrongWriteAction || hasWeakWriteAction;
        
        if (isWriteAction) {
            intent = "unsupported";
            queryType = "unsupported";
        } else if (input.matches(".*(待办|事项|日程|提醒|todo|Todo).*")) {
            intent = "query_todo";
            queryType = "todo";
        } else {
            intent = "query_contact";
            queryType = "contact";
        }

        // 2. 根据意图执行编排逻辑
        AgentQueryResponseVO response = new AgentQueryResponseVO();
        response.setQueryType(queryType);
        response.setIntent(intent);

        Map<String, Object> parsedParams = new HashMap<>();
        List<?> results = Collections.emptyList();
        String summary = "";

        try {
            if ("unsupported".equals(intent)) {
                summary = "抱歉，本期智能助手仅支持查询联系人和事项，暂不支持创建、修改或拉黑等操作。";
                parsedParams.put("action", "rejected");
            } else if ("query_contact".equals(intent)) {
                // 联系人查询：剥离前后缀提取关键字
                String keyword = extractContactKeyword(input);
                parsedParams.put("keyword", keyword);

                ContactQueryParam queryParam = new ContactQueryParam();
                queryParam.setKeyword(keyword);
                queryParam.setPage(1);
                queryParam.setPageSize(20);
                
                ContactListVO listVO = contactService.listContacts(queryParam);
                results = listVO.getList();
                
                if (results == null || results.isEmpty()) {
                    summary = "未查找到匹配的联系人信息";
                } else {
                    summary = "已查找到包含关键字 '" + keyword + "' 的联系人信息，共 " + results.size() + " 条";
                }
            } else {
                // 事项查询：遍历联系人姓名以寻找匹配的联系人 ID
                String matchedContactId = null;
                String matchedContactName = null;
                
                // 查询前 500 个联系人以查找候选名字
                ContactQueryParam allParam = new ContactQueryParam();
                allParam.setPage(1);
                allParam.setPageSize(500);
                ContactListVO allContacts = contactService.listContacts(allParam);
                
                if (allContacts != null && allContacts.getList() != null) {
                    for (ContactVO c : allContacts.getList()) {
                        if (input.contains(c.getName())) {
                            matchedContactId = c.getContactId();
                            matchedContactName = c.getName();
                            break; // 匹配第一个包含的名字
                        }
                    }
                }

                // 识别事项状态 (0 待完成, 1 已取消, 2 已完成)
                Integer status = 0; // 默认待完成
                if (input.matches(".*(已完成|完成|搞定|完成的|done).*")) {
                    status = 2;
                } else if (input.matches(".*(已取消|取消|取消的|cancel).*")) {
                    status = 1;
                }

                TodoQuery todoQuery = new TodoQuery();
                todoQuery.setPage(1);
                todoQuery.setPageSize(20);
                todoQuery.setStatus(status);
                
                if (matchedContactId != null) {
                    todoQuery.setContactId(matchedContactId);
                    parsedParams.put("contactId", matchedContactId);
                    parsedParams.put("contactName", matchedContactName);
                }
                parsedParams.put("status", status);

                TodoListVO listVO = todoService.listTodos(todoQuery);
                results = listVO.getList();

                String statusName = status == 2 ? "已完成" : (status == 1 ? "已取消" : "待完成");
                if (matchedContactName != null) {
                    if (results == null || results.isEmpty()) {
                        summary = "未查找到联系人 '" + matchedContactName + "' 关联的" + statusName + "事项";
                    } else {
                        summary = "已查找到联系人 '" + matchedContactName + "' 关联的 " + results.size() + " 条" + statusName + "事项";
                    }
                } else {
                    if (results == null || results.isEmpty()) {
                        summary = "未查找到您的" + statusName + "事项";
                    } else {
                        summary = "已查找到您的 " + results.size() + " 条" + statusName + "事项";
                    }
                }
            }
        } catch (Exception e) {
            log.error("Agent query execution error", e);
            summary = "智能助手在处理查询时遇到异常：" + e.getMessage();
        }

        response.setSummary(summary);
        response.setResults(results);

        // 3. 记录操作审计日志
        try {
            AgentOperationLog operationLog = new AgentOperationLog();
            operationLog.setUserId(userId);
            operationLog.setUserInput(input);
            operationLog.setIntent(intent);
            operationLog.setParsedParams(objectMapper.writeValueAsString(parsedParams));
            operationLog.setNeedConfirm(0);
            operationLog.setConfirmed(0);
            operationLog.setActionType("unsupported".equals(intent) ? "unsupported" : "query");
            operationLog.setExecutionStatus(1); // 成功处理响应
            
            // 结果只记录摘要，results 为列表不宜全部塞入 log 导致数据库过载，记录 summary 即可
            Map<String, Object> resultPayload = new HashMap<>();
            resultPayload.put("summary", summary);
            resultPayload.put("count", results.size());
            operationLog.setExecutionResult(objectMapper.writeValueAsString(resultPayload));
            operationLog.setCreatedAt(LocalDateTime.now());
            
            agentOperationLogMapper.insert(operationLog);
        } catch (Exception e) {
            log.error("Failed to write agent operation log to database", e);
        }

        return response;
    }

    /**
     * 去除常见的自然语言前后缀，提取关键字进行模糊查询
     */
    private String extractContactKeyword(String input) {
        String keyword = input;
        
        // 常见前缀列表（按长度降序排序）
        String[] prefixes = {
            "帮我查一下", "帮我查询", "帮我查找", "帮我找", 
            "查一下", "查询", "查找", "找一下", "我想找", "查", "找"
        };
        
        // 常见后缀列表（按长度降序排序）
        String[] suffixes = {
            "的联系方式", "的联系信息", "的手机号", "的电话", "的微信", "的邮箱", "的信息", "的资料"
        };

        // 去除前缀
        for (String prefix : prefixes) {
            if (keyword.startsWith(prefix)) {
                keyword = keyword.substring(prefix.length());
                break; // 只匹配并去除最长的前缀一次
            }
        }

        // 去除后缀
        for (String suffix : suffixes) {
            if (keyword.endsWith(suffix)) {
                keyword = keyword.substring(0, keyword.length() - suffix.length());
                break;
            }
        }

        keyword = keyword.trim();
        // 如果剥离后变成空字符，则兜底返回原始输入
        return keyword.isEmpty() ? input : keyword;
    }

    @Override
    public AgentExecuteResponseVO execute(AgentExecuteParam param) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }

        String input = param.getInput().trim();
        if (input.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入内容不能为空");
        }

        // 1. 意图识别，正则识别是否是“创建事项”相关的写意图，否则返回 unsupported
        boolean isCreateTodo = false;
        boolean hasCreateKeywords = input.matches(".*(提醒|事项|日程|待办|todo|Todo|联系|创建|添加|新建|新增).*");
        boolean hasOtherWriteKeywords = input.matches(".*(删除|修改|更新|拉黑|恢复|完成|取消).*");
        if (hasCreateKeywords && !hasOtherWriteKeywords) {
            isCreateTodo = true;
        }

        AgentExecuteResponseVO response = new AgentExecuteResponseVO();
        Map<String, Object> parsedParams = new HashMap<>();

        if (!isCreateTodo) {
            response.setNeedConfirm(0);
            response.setActionType("unsupported");
            response.setSummary("抱歉，智能助手目前仅支持“创建事项”的写操作，暂不支持其他类型的写请求。");
            response.setParsedParams(parsedParams);

            // 记录日志：不支持的写操作，need_confirm = 0, confirmed = 0, execution_status = 1
            AgentOperationLog operationLog = new AgentOperationLog();
            operationLog.setUserId(userId);
            operationLog.setUserInput(input);
            operationLog.setIntent("unsupported");
            try {
                operationLog.setParsedParams(objectMapper.writeValueAsString(parsedParams));
            } catch (Exception e) {
                log.error("JSON write error", e);
            }
            operationLog.setNeedConfirm(0);
            operationLog.setConfirmed(0);
            operationLog.setActionType("unsupported");
            operationLog.setExecutionStatus(1);
            Map<String, Object> resultPayload = new HashMap<>();
            resultPayload.put("summary", response.getSummary());
            try {
                operationLog.setExecutionResult(objectMapper.writeValueAsString(resultPayload));
            } catch (Exception e) {
                log.error("JSON write error", e);
            }
            operationLog.setCreatedAt(LocalDateTime.now());
            agentOperationLogMapper.insert(operationLog);

            response.setLogId(operationLog.getId());
            return response;
        }

        // 2. 意图为创建事项，开始提取字段
        response.setActionType("create_todo");

        // 提取联系人姓名 (模糊匹配当前用户的联系人列表)
        String matchedContactId = null;
        String matchedContactName = null;

        ContactQueryParam allParam = new ContactQueryParam();
        allParam.setPage(1);
        allParam.setPageSize(500);
        ContactListVO allContacts = contactService.listContacts(allParam);
        if (allContacts != null && allContacts.getList() != null) {
            for (ContactVO c : allContacts.getList()) {
                if (input.contains(c.getName())) {
                    matchedContactId = c.getContactId();
                    matchedContactName = c.getName();
                    break;
                }
            }
        }

        // 提取时间
        LocalDateTime todoTime = parseTime(input);

        // 提取优先级 (0 普通, 1 重要, 2 紧急，默认为重要 1)
        int priority = 1;
        if (input.contains("紧急") || input.contains("高优")) {
            priority = 2;
        } else if (input.contains("普通") || input.contains("低") || input.contains("一般")) {
            priority = 0;
        }

        // 提取内容
        String content = input;
        // 去除动作词
        content = content.replaceAll("(?i)提醒我联系", "");
        content = content.replaceAll("(?i)提醒我", "");
        content = content.replaceAll("(?i)联系", "");
        content = content.replaceAll("(?i)帮我创建", "");
        content = content.replaceAll("(?i)创建待办", "");
        content = content.replaceAll("(?i)创建事项", "");
        content = content.replaceAll("(?i)新建待办", "");
        content = content.replaceAll("(?i)新建事项", "");
        
        // 去除联系人名字
        if (matchedContactName != null) {
            content = content.replaceAll(matchedContactName, "");
        }
        
        // 去除时间词
        String[] timeKeywords = {
            "今天晚上八点", "今天晚上8点", "今天晚上", "今天",
            "明天下午三点", "明天下午3点", "明天下午", "明天",
            "后天早上九点", "后天早上9点", "后天早上", "后天",
            "下周一中午十二点", "下周一中午12点", "下周一",
            "下周二", "下周三", "下周四", "下周五", "下周六", "下周日", "下周天",
            "下午三点", "下午3点", "早上九点", "早上9点", "晚上八点", "晚上8点",
            "中午十二点", "中午12点", "点", "分"
        };
        for (String tk : timeKeywords) {
            content = content.replace(tk, "");
        }
        
        // 去除优先级修饰词
        content = content.replace("紧急", "");
        content = content.replace("高优", "");
        content = content.replace("普通", "");
        content = content.replace("重要", "");
        
        // 清理一些标点符号和空格
        content = content.replaceAll("^[，。、, ]+", "").replaceAll("[，。、, ]+$", "").trim();

        // 组装解析的参数
        if (matchedContactId != null) {
            parsedParams.put("contactId", matchedContactId);
            parsedParams.put("contactName", matchedContactName);
        }
        if (todoTime != null) {
            parsedParams.put("todoTime", todoTime.format(TODO_TIME_FORMATTER));
        }
        if (!content.isEmpty()) {
            parsedParams.put("content", content);
        }
        parsedParams.put("priority", priority);

        response.setParsedParams(parsedParams);

        // 3. 校验联系人、时间、内容是否缺失
        int needConfirm = 1;
        String summary = "";
        if (matchedContactId == null) {
            needConfirm = 0;
            summary = "抱歉，无法识别您想为哪位联系人创建事项，请输入包含联系人姓名的指令，例如：“明天下午三点提醒我联系张三确认合同”";
        } else if (todoTime == null) {
            needConfirm = 0;
            summary = "抱歉，无法识别事项提醒时间，请输入包含具体时间的指令，例如：“明天下午三点提醒我联系张三确认合同”";
        } else if (content.isEmpty()) {
            needConfirm = 0;
            summary = "抱歉，无法识别事项内容，请输入包含具体事项内容的指令，例如：“明天下午三点提醒我联系张三确认合同”";
        } else {
            String formattedTime = todoTime.format(TODO_TIME_FORMATTER);
            summary = "已为您生成待办事项预确认卡片，请核对：在 " + formattedTime + " 提醒联系 " + matchedContactName + " " + content + "。";
        }

        response.setNeedConfirm(needConfirm);
        response.setSummary(summary);

        // 4. 将操作入库
        AgentOperationLog operationLog = new AgentOperationLog();
        operationLog.setUserId(userId);
        operationLog.setUserInput(input);
        operationLog.setIntent("create_todo");
        try {
            operationLog.setParsedParams(objectMapper.writeValueAsString(parsedParams));
        } catch (Exception e) {
            log.error("JSON write error", e);
        }
        operationLog.setNeedConfirm(needConfirm);
        operationLog.setConfirmed(0);
        operationLog.setActionType("create_todo");
        // 如果需要确认，设为 pending (0)；否则设为已处理完成 (1)
        operationLog.setExecutionStatus(needConfirm == 1 ? 0 : 1);
        
        // 结果存入 execution_result
        Map<String, Object> resultPayload = new HashMap<>();
        resultPayload.put("summary", summary);
        try {
            operationLog.setExecutionResult(objectMapper.writeValueAsString(resultPayload));
        } catch (Exception e) {
            log.error("JSON write error", e);
        }
        operationLog.setCreatedAt(LocalDateTime.now());
        agentOperationLogMapper.insert(operationLog);

        response.setLogId(operationLog.getId());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object confirm(AgentConfirmParam param) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }

        AgentOperationLog logRecord = agentOperationLogMapper.selectById(param.getLogId());
        if (logRecord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "未找到该操作日志");
        }

        // 防越权
        if (!userId.equals(logRecord.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作此日志记录");
        }

        // 状态必须是 pending(0)
        if (logRecord.getExecutionStatus() != 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "日志状态不正确，无法操作");
        }

        if ("cancel".equalsIgnoreCase(param.getAction())) {
            logRecord.setConfirmed(0);
            logRecord.setExecutionStatus(3); // 3: 已取消
            agentOperationLogMapper.updateById(logRecord);
            return null;
        }

        if ("confirm".equalsIgnoreCase(param.getAction())) {
            // 解析 parsed_params
            Map<String, Object> parsedParams;
            try {
                parsedParams = objectMapper.readValue(logRecord.getParsedParams(), Map.class);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "解析日志参数失败");
            }

            String contactId = (String) parsedParams.get("contactId");
            String todoTimeStr = (String) parsedParams.get("todoTime");
            String content = (String) parsedParams.get("content");
            Integer priority = (Integer) parsedParams.get("priority");

            if (contactId == null || todoTimeStr == null || content == null || priority == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "预确认参数缺失，无法创建事项");
            }

            TodoCreateDTO createDTO = new TodoCreateDTO();
            createDTO.setContactId(contactId);
            createDTO.setTodoTime(LocalDateTime.parse(todoTimeStr, TODO_TIME_FORMATTER));
            createDTO.setContent(content);
            createDTO.setPriority(priority);

            // 调用业务 Service 层落库
            TodoVO todoVO = todoService.createTodo(createDTO);

            // 更新日志状态
            logRecord.setConfirmed(1);
            logRecord.setExecutionStatus(1); // 1: 成功
            try {
                logRecord.setExecutionResult(objectMapper.writeValueAsString(todoVO));
            } catch (Exception e) {
                log.error("JSON write error", e);
            }
            agentOperationLogMapper.updateById(logRecord);

            return todoVO;
        }

        throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的确认动作");
    }

    private LocalDateTime parseTime(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        LocalDate baseDate = LocalDate.now();
        LocalDate targetDate = null;

        if (input.contains("今天")) {
            targetDate = baseDate;
        } else if (input.contains("明天")) {
            targetDate = baseDate.plusDays(1);
        } else if (input.contains("后天")) {
            targetDate = baseDate.plusDays(2);
        } else if (input.contains("下周一")) {
            targetDate = resolveNextWeekDate(baseDate, DayOfWeek.MONDAY);
        } else if (input.contains("下周二")) {
            targetDate = resolveNextWeekDate(baseDate, DayOfWeek.TUESDAY);
        } else if (input.contains("下周三")) {
            targetDate = resolveNextWeekDate(baseDate, DayOfWeek.WEDNESDAY);
        } else if (input.contains("下周四")) {
            targetDate = resolveNextWeekDate(baseDate, DayOfWeek.THURSDAY);
        } else if (input.contains("下周五")) {
            targetDate = resolveNextWeekDate(baseDate, DayOfWeek.FRIDAY);
        } else if (input.contains("下周六")) {
            targetDate = resolveNextWeekDate(baseDate, DayOfWeek.SATURDAY);
        } else if (input.contains("下周日") || input.contains("下周天")) {
            targetDate = resolveNextWeekDate(baseDate, DayOfWeek.SUNDAY);
        }

        if (targetDate == null) {
            return null;
        }

        int hour = 9;
        int minute = 0;

        boolean isPm = false;
        if (input.contains("下午") || input.contains("晚上") || input.contains("晚间") || input.contains("夜里") || input.contains("下半天")) {
            isPm = true;
        }
        
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?:(\\d+)|([一二三四五六七八九十百]+))点(?:(?:(\\d+)|([一二三四五六七八九十百]+))分)?");
        java.util.regex.Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String numStr = matcher.group(1);
            String cnStr = matcher.group(2);
            int h = -1;
            if (numStr != null) {
                h = Integer.parseInt(numStr);
            } else if (cnStr != null) {
                h = cnTextToInt(cnStr);
            }
            
            if (h != -1) {
                hour = h;
                if (isPm && hour < 12) {
                    hour += 12;
                }
            }
            
            String minNumStr = matcher.group(3);
            String minCnStr = matcher.group(4);
            if (minNumStr != null) {
                minute = Integer.parseInt(minNumStr);
            } else if (minCnStr != null) {
                minute = cnTextToInt(minCnStr);
            }
        } else {
            java.util.regex.Pattern timePattern = java.util.regex.Pattern.compile("(\\d{1,2}):(\\d{2})");
            java.util.regex.Matcher timeMatcher = timePattern.matcher(input);
            if (timeMatcher.find()) {
                hour = Integer.parseInt(timeMatcher.group(1));
                minute = Integer.parseInt(timeMatcher.group(2));
            } else {
                if (input.contains("下午")) {
                    hour = 15;
                } else if (input.contains("晚上")) {
                    hour = 20;
                } else if (input.contains("中午")) {
                    hour = 12;
                } else if (input.contains("早上") || input.contains("上午")) {
                    hour = 9;
                }
            }
        }

        return LocalDateTime.of(targetDate, LocalTime.of(hour, minute));
    }

    private LocalDate resolveNextWeekDate(LocalDate baseDate, DayOfWeek targetDay) {
        return baseDate.with(TemporalAdjusters.next(targetDay));
    }

    private int cnTextToInt(String cn) {
        switch (cn) {
            case "一": return 1;
            case "二": case "两": return 2;
            case "三": return 3;
            case "四": return 4;
            case "五": return 5;
            case "六": return 6;
            case "七": return 7;
            case "八": return 8;
            case "九": return 9;
            case "十": return 10;
            case "十一": return 11;
            case "十二": return 12;
            case "十三": return 13;
            case "十四": return 14;
            case "十五": return 15;
            case "二十": return 20;
            case "三十": return 30;
            case "四十": return 40;
            case "五十": return 50;
            default: return -1;
        }
    }
}
