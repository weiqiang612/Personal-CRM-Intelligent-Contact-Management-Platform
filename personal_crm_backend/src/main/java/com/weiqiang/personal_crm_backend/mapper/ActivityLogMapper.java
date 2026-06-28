package com.weiqiang.personal_crm_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weiqiang.personal_crm_backend.entity.ActivityLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 活动日志 Mapper 接口
 */
@Mapper
public interface ActivityLogMapper extends BaseMapper<ActivityLog> {
}
