package com.weiqiang.personal_crm_backend.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 事项视图展示对象
 */
@Data
public class TodoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事项业务编号
     */
    private String matterId;

    /**
     * 联系人业务编号
     */
    private String contactId;

    /**
     * 联系人姓名 (联查)
     */
    private String contactName;

    /**
     * 提醒时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;

    /**
     * 取消时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelledAt;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 是否逾期
     */
    private Boolean isOverdue;
}
