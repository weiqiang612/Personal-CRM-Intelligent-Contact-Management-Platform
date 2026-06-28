package com.weiqiang.personal_crm_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 联系人活动轨迹日志实体类
 */
@Data
@TableName("activity_log")
public class ActivityLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 活动轨迹业务编号
     */
    @TableField("activity_id")
    private String activityId;

    /**
     * 所属用户业务编号
     */
    @TableField("user_id")
    private String userId;

    /**
     * 所属联系人业务编号
     */
    @TableField("contact_id")
    private String contactId;

    /**
     * 事件类型 (如 CONTACT_CREATED, CONTACT_UPDATED, TAG_CHANGED, BLACKLIST_CHANGED, TODO_CREATED, TODO_COMPLETED, TODO_CANCELLED)
     */
    @TableField("event_type")
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
    @TableField("occurred_at")
    private LocalDateTime occurredAt;

    /**
     * 日志记录创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}
