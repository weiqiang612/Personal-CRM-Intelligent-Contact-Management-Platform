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

    /**
     * Agent 自然语言写操作预处理
     *
     * @param param 写操作请求参数
     * @return 写操作预确认包装 VO
     */
    com.weiqiang.personal_crm_backend.model.vo.AgentExecuteResponseVO execute(com.weiqiang.personal_crm_backend.model.dto.AgentExecuteParam param);

    /**
     * Agent 写操作二次确认
     *
     * @param param 确认请求参数
     * @return 执行成功的事项视图对象或 null
     */
    Object confirm(com.weiqiang.personal_crm_backend.model.dto.AgentConfirmParam param);
}
