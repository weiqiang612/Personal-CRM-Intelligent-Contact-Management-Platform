package com.weiqiang.personal_crm_backend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 事项列表查询条件与分页参数
 */
@Data
public class TodoQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码，默认 1
     */
    private Integer page;

    /**
      * 每页条数，默认 10
      */
    private Integer pageSize;

    /**
     * 联系人业务编号 (可选)
     */
    private String contactId;

    /**
     * 状态 (可选, 0 待完成, 1 已取消, 2 已完成)
     */
    private Integer status;

    /**
     * 开始时间 (可选, 格式 yyyy-MM-dd HH:mm:ss)
     */
    private String startTime;

    /**
     * 结束时间 (可选, 格式 yyyy-MM-dd HH:mm:ss)
     */
    private String endTime;

    /**
     * 排序字段 (可选, todoTime, createdAt, completedAt 等)
     */
    private String sortBy;

    /**
     * 排序顺序 (可选, asc 或 desc)
     */
    private String sortOrder;
}
