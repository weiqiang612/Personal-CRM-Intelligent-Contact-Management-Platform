package com.weiqiang.personal_crm_backend.controller;

import com.weiqiang.personal_crm_backend.common.Result;
import com.weiqiang.personal_crm_backend.model.dto.AgentQueryParam;
import com.weiqiang.personal_crm_backend.model.vo.AgentQueryResponseVO;
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
}
