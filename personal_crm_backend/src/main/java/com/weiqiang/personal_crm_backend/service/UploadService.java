package com.weiqiang.personal_crm_backend.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

/**
 * 头像上传服务接口
 */
public interface UploadService {

    /**
     * 上传联系人头像
     *
     * @param file      头像文件
     * @param contactId 联系人 ID
     * @return 包含文件名称和访问链接的 Map
     */
    Map<String, String> uploadContactAvatar(MultipartFile file, String contactId);

    /**
     * 上传当前用户头像
     *
     * @param file 头像文件
     * @return 包含文件名称和访问链接的 Map
     */
    Map<String, String> uploadUserAvatar(MultipartFile file);
}
