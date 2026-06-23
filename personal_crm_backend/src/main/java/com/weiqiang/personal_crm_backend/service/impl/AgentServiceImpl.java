package com.weiqiang.personal_crm_backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.entity.AgentOperationLog;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.mapper.AgentOperationLogMapper;
import com.weiqiang.personal_crm_backend.model.dto.AgentQueryParam;
import com.weiqiang.personal_crm_backend.model.dto.ContactQueryParam;
import com.weiqiang.personal_crm_backend.model.dto.TodoQuery;
import com.weiqiang.personal_crm_backend.model.vo.AgentQueryResponseVO;
import com.weiqiang.personal_crm_backend.model.vo.ContactListVO;
import com.weiqiang.personal_crm_backend.model.vo.ContactVO;
import com.weiqiang.personal_crm_backend.model.vo.TodoListVO;
import com.weiqiang.personal_crm_backend.security.UserContext;
import com.weiqiang.personal_crm_backend.service.AgentService;
import com.weiqiang.personal_crm_backend.service.ContactService;
import com.weiqiang.personal_crm_backend.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Agent 业务服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AgentServiceImpl implements AgentService {

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
}
