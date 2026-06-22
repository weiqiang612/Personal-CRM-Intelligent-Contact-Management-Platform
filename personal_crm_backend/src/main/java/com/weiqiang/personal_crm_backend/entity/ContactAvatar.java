package com.weiqiang.personal_crm_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Contact Avatar Entity class
 */
@Data
@TableName("contact_avatar")
public class ContactAvatar {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String picId;

    private String contactId;

    private String fileName;

    private String filePath;

    private String accessUrl;

    private LocalDateTime createdAt;
}
