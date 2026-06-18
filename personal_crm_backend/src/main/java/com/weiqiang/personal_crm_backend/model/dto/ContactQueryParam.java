package com.weiqiang.personal_crm_backend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 联系人列表查询参数对象
 */
@Data
public class ContactQueryParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 模糊搜索关键字（姓名、手机号、微信）
     */
    private String keyword;

    /**
     * 状态（0 正常，1 黑名单）
     */
    private Integer status;

    /**
     * 性别（0 未知，1 男，2 女）
     */
    private Integer gender;

    /**
     * 标签名称
     */
    private String tag;

    /**
     * 排序字段（createdAt 或 birthday）
     */
    private String sortBy;

    /**
     * 排序顺序（asc 或 desc）
     */
    private String sortOrder;
}
