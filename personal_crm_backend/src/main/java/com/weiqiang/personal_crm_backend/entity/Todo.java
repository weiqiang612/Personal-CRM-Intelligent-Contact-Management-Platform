package com.weiqiang.personal_crm_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 事项实体类
 */
@Data
@TableName("contact_todo")
public class Todo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 事项业务编号 (唯一)
     */
    @TableField("matter_id")
    private String matterId;

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
     * 提醒时间
     */
    @TableField("todo_time")
    private LocalDateTime todoTime;

    /**
     * 事项内容
     */
    private String content;

    /**
     * 状态 (0 待完成, 1 已取消, 2 已完成)
     */
    private Integer status;

    /**
     * 优先级 (0 普通, 1 重要, 2 紧急)
     */
    private Integer priority;

    /**
     * 完成时间
     */
    @TableField("completed_at")
    private LocalDateTime completedAt;

    /**
     * 取消时间
     */
    @TableField("cancelled_at")
    private LocalDateTime cancelledAt;

    /**
     * 逻辑删除标识 (0 正常, 1 已删除)
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
