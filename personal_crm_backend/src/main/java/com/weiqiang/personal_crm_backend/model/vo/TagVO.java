package com.weiqiang.personal_crm_backend.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 标签视图展示对象
 */
@Data
public class TagVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签主键 ID
     */
    private Long tagId;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签颜色
     */
    private String color;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
