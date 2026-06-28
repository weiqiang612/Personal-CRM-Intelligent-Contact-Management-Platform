package com.weiqiang.personal_crm_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weiqiang.personal_crm_backend.entity.ActivityLog;
import com.weiqiang.personal_crm_backend.model.dto.ContactLatestActivityDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 活动日志 Mapper 接口
 */
@Mapper
public interface ActivityLogMapper extends BaseMapper<ActivityLog> {

    @Select("SELECT a.contact_id AS contactId, a.occurred_at AS latestActivityTime, a.title AS lastEventTitle " +
            "FROM activity_log a " +
            "INNER JOIN (SELECT contact_id, MAX(occurred_at) AS max_time FROM activity_log WHERE user_id = #{userId} GROUP BY contact_id) b " +
            "ON a.contact_id = b.contact_id AND a.occurred_at = b.max_time " +
            "WHERE a.user_id = #{userId}")
    List<ContactLatestActivityDTO> selectLatestActivityByUserId(@Param("userId") String userId);
}
