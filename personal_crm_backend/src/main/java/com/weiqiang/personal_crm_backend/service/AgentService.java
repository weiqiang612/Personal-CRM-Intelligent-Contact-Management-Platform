package com.weiqiang.personal_crm_backend.service;

import com.weiqiang.personal_crm_backend.model.dto.AgentQueryParam;
import com.weiqiang.personal_crm_backend.model.vo.AgentQueryResponseVO;

/**
 * Agent 业务服务接口
 */
public interface AgentService {

    /**
     * 自然语言查询
     *
     * @param param 查询请求参数
     * @return 查询结果包装 VO
     */
    AgentQueryResponseVO query(AgentQueryParam param);
}
