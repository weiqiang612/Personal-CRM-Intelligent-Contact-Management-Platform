package com.weiqiang.personal_crm_backend.controller;

import com.weiqiang.personal_crm_backend.common.Result;
import com.weiqiang.personal_crm_backend.model.dto.AgentQueryParam;
import com.weiqiang.personal_crm_backend.model.vo.AgentQueryResponseVO;
import com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam;
import com.weiqiang.personal_crm_backend.model.vo.AgentExecuteResponseVO;
import com.weiqiang.personal_crm_backend.model.dto.AgentConfirmParam;
import com.weiqiang.personal_crm_backend.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Agent 模块接口控制器
 */
@RestController
@RequestMapping("/api/v1/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    /**
     * Agent 自然语言查询接口
     */
    @PostMapping("/query")
    public Result<AgentQueryResponseVO> query(@Validated @RequestBody AgentQueryParam param) {
        AgentQueryResponseVO response = agentService.query(param);
        return Result.success(response);
    }

    /**
     * Agent 自然语言写操作预处理
     */
    @PostMapping("/execute")
    public Result<AgentExecuteResponseVO> execute(@Validated @RequestBody AgentExecuteParam param) {
        AgentExecuteResponseVO response = agentService.execute(param);
        return Result.success(response);
    }

    /**
     * Agent 写操作二次确认
     */
    @PostMapping("/confirm")
    public Result<Object> confirm(@Validated @RequestBody AgentConfirmParam param) {
        Object response = agentService.confirm(param);
        return Result.success(response);
    }
}
