package com.weiqiang.personal_crm_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weiqiang.personal_crm_backend.entity.ActivityLog;
import com.weiqiang.personal_crm_backend.model.vo.ActivityLogVO;

import java.util.List;

/**
 * 联系人活动日志服务接口
 */
public interface ActivityLogService extends IService<ActivityLog> {

    /**
     * 保存活动轨迹日志
     */
    void saveActivity(String userId, String contactId, String eventType, String title, String description);

    /**
     * 查询指定联系人的活动轨迹时间线
     */
    List<ActivityLogVO> listContactActivities(String contactId, Integer limit);
}
