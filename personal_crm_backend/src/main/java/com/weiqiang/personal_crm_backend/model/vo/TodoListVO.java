package com.weiqiang.personal_crm_backend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 事项分页列表展示对象
 */
@Data
public class TodoListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事项列表
     */
    private List<TodoVO> list;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;
}
