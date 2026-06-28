package com.weiqiang.personal_crm_backend.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 联系人活动轨迹日志视图对象
 */
@Data
public class ActivityLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动轨迹业务编号
     */
    private String activityId;

    /**
     * 联系人业务编号
     */
    private String contactId;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 事件简要标题
     */
    private String title;

    /**
     * 事件详细描述
     */
    private String description;

    /**
     * 事件发生时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime occurredAt;
}
