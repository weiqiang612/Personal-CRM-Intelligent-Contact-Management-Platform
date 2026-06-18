package com.weiqiang.personal_crm_backend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 联系人列表包装类 (包含分页元数据)
 */
@Data
public class ContactListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 联系人列表
     */
    private List<ContactVO> list;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;
}
