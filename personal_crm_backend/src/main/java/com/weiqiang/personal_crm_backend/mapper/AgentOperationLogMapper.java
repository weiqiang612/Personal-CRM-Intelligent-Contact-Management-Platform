package com.weiqiang.personal_crm_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weiqiang.personal_crm_backend.entity.AgentOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * Agent 操作日志 Mapper 接口
 */
@Mapper
public interface AgentOperationLogMapper extends BaseMapper<AgentOperationLog> {
}
