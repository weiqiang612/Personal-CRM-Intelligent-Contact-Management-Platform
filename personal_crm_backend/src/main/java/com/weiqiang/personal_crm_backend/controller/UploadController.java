package com.weiqiang.personal_crm_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.common.Result;
import com.weiqiang.personal_crm_backend.entity.Contact;
import com.weiqiang.personal_crm_backend.entity.ContactAvatar;
import com.weiqiang.personal_crm_backend.entity.UserAvatar;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.mapper.ContactAvatarMapper;
import com.weiqiang.personal_crm_backend.mapper.ContactMapper;
import com.weiqiang.personal_crm_backend.mapper.UserAvatarMapper;
import com.weiqiang.personal_crm_backend.security.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for uploading avatar files
 */
@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
@Slf4j
public class UploadController {

    private final ContactMapper contactMapper;
    private final ContactAvatarMapper contactAvatarMapper;
    private final UserAvatarMapper userAvatarMapper;

    @Value("${app.storage.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${app.storage.contact-avatar-dir:contact-avatar}")
    private String contactAvatarSubDir;

    @Value("${app.storage.user-avatar-dir:user-avatar}")
    private String userAvatarSubDir;

    /**
     * Upload contact avatar
     */
    @PostMapping("/contact-avatar")
    public Result<Map<String, String>> uploadContactAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("contactId") String contactId) {

        validateFile(file);

        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Contact contact = contactMapper.selectOne(
                new LambdaQueryWrapper<Contact>()
                        .eq(Contact::getCtId, contactId)
        );
        if (contact == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Contact not found");
        }
        if (!userId.equals(contact.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Access denied: This contact belongs to another user");
        }

        ContactAvatar oldAvatar = contactAvatarMapper.selectOne(
                new LambdaQueryWrapper<ContactAvatar>()
                        .eq(ContactAvatar::getContactId, contactId)
        );

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String randomName = UUID.randomUUID().toString().replace("-", "") + extension;

        Path targetDir = Paths.get(uploadDir, contactAvatarSubDir).toAbsolutePath().normalize();
        try {
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            Path targetFile = targetDir.resolve(randomName);
            file.transferTo(targetFile.toFile());

            String picId = generatePicIdForContact();
            String accessUrl = "/uploads/" + contactAvatarSubDir + "/" + randomName;

            ContactAvatar newAvatar = new ContactAvatar();
            newAvatar.setPicId(picId);
            newAvatar.setContactId(contactId);
            newAvatar.setFileName(randomName);
            newAvatar.setFilePath(targetFile.toString());
            newAvatar.setAccessUrl(accessUrl);
            newAvatar.setCreatedAt(LocalDateTime.now());

            if (oldAvatar != null) {
                deletePhysicalFile(oldAvatar.getFilePath());
                newAvatar.setId(oldAvatar.getId());
                contactAvatarMapper.updateById(newAvatar);
            } else {
                contactAvatarMapper.insert(newAvatar);
            }

            Map<String, String> response = new HashMap<>();
            response.put("fileName", randomName);
            response.put("accessUrl", accessUrl);
            return Result.success(response);

        } catch (IOException e) {
            log.error("Failed to save contact avatar", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to upload avatar");
        }
    }

    /**
     * Upload current user avatar
     */
    @PostMapping("/user-avatar")
    public Result<Map<String, String>> uploadUserAvatar(@RequestParam("file") MultipartFile file) {

        validateFile(file);

        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        UserAvatar oldAvatar = userAvatarMapper.selectOne(
                new LambdaQueryWrapper<UserAvatar>()
                        .eq(UserAvatar::getUserId, userId)
        );

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String randomName = UUID.randomUUID().toString().replace("-", "") + extension;

        Path targetDir = Paths.get(uploadDir, userAvatarSubDir).toAbsolutePath().normalize();
        try {
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            Path targetFile = targetDir.resolve(randomName);
            file.transferTo(targetFile.toFile());

            String picId = generatePicIdForUser();
            String accessUrl = "/uploads/" + userAvatarSubDir + "/" + randomName;

            UserAvatar newAvatar = new UserAvatar();
            newAvatar.setPicId(picId);
            newAvatar.setUserId(userId);
            newAvatar.setFileName(randomName);
            newAvatar.setFilePath(targetFile.toString());
            newAvatar.setAccessUrl(accessUrl);
            newAvatar.setCreatedAt(LocalDateTime.now());

            if (oldAvatar != null) {
                deletePhysicalFile(oldAvatar.getFilePath());
                newAvatar.setId(oldAvatar.getId());
                userAvatarMapper.updateById(newAvatar);
            } else {
                userAvatarMapper.insert(newAvatar);
            }

            Map<String, String> response = new HashMap<>();
            response.put("fileName", randomName);
            response.put("accessUrl", accessUrl);
            return Result.success(response);

        } catch (IOException e) {
            log.error("Failed to save user avatar", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to upload avatar");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "File is empty");
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "File size exceeds 2MB limit");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Invalid file name");
        }
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!extension.equals(".jpg") && !extension.equals(".jpeg") && !extension.equals(".png") && !extension.equals(".webp")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Only JPG, JPEG, PNG and WEBP file types are supported");
        }
    }

    private String getFileExtension(String filename) {
        int lastIndex = filename.lastIndexOf(".");
        if (lastIndex == -1) {
            return "";
        }
        return filename.substring(lastIndex);
    }

    private void deletePhysicalFile(String filePath) {
        if (filePath != null) {
            try {
                Path path = Paths.get(filePath);
                Files.deleteIfExists(path);
            } catch (IOException e) {
                log.warn("Failed to delete physical file: " + filePath, e);
            }
        }
    }

    private String generatePicIdForContact() {
        while (true) {
            String id = generateRandomAlphanumeric(10);
            Long count = contactAvatarMapper.selectCount(
                    new LambdaQueryWrapper<ContactAvatar>()
                            .eq(ContactAvatar::getPicId, id)
            );
            if (count == 0) {
                return id;
            }
        }
    }

    private String generatePicIdForUser() {
        while (true) {
            String id = generateRandomAlphanumeric(10);
            Long count = userAvatarMapper.selectCount(
                    new LambdaQueryWrapper<UserAvatar>()
                            .eq(UserAvatar::getPicId, id)
            );
            if (count == 0) {
                return id;
            }
        }
    }

    private String generateRandomAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
