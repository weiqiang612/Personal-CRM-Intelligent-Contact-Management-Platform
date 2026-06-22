package com.weiqiang.personal_crm_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 联系人标签关联实体类
 */
@Data
@TableName("contact_tag")
public class ContactTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户业务编号
     */
    @TableField("user_id")
    private String userId;

    /**
     * 联系人业务编号
     */
    @TableField("contact_id")
    private String contactId;

    /**
     * 标签主键 ID
     */
    @TableField("tag_id")
    private Long tagId;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}
