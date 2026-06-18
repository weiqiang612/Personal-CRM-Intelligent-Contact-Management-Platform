package com.weiqiang.personal_crm_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户头像实体类
 */
@Data
@TableName("user_avatar")
public class UserAvatar {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 头像图片业务 ID
     */
    private String picId;

    /**
     * 关联的用户业务 ID
     */
    private String userId;

    /**
     * 存储的文件名
     */
    private String fileName;

    /**
     * 文件物理路径
     */
    private String filePath;

    /**
     * 头像可访问的 HTTP 相对 URL
     */
    private String accessUrl;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
