package com.weiqiang.personal_crm_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.entity.Contact;
import com.weiqiang.personal_crm_backend.entity.ContactAvatar;
import com.weiqiang.personal_crm_backend.entity.UserAvatar;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.mapper.ContactAvatarMapper;
import com.weiqiang.personal_crm_backend.mapper.ContactMapper;
import com.weiqiang.personal_crm_backend.mapper.UserAvatarMapper;
import com.weiqiang.personal_crm_backend.security.UserContext;
import com.weiqiang.personal_crm_backend.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
 * 头像上传服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UploadServiceImpl implements UploadService {

    private static final int EXPECTED_AFFECTED_ROWS = 1;

    private final ContactMapper contactMapper;
    private final ContactAvatarMapper contactAvatarMapper;
    private final UserAvatarMapper userAvatarMapper;

    @Value("${app.storage.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${app.storage.contact-avatar-dir:contact-avatar}")
    private String contactAvatarSubDir;

    @Value("${app.storage.user-avatar-dir:user-avatar}")
    private String userAvatarSubDir;

    @Override
    public Map<String, String> uploadContactAvatar(MultipartFile file, String contactId) {
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
        String picId = generatePicIdForContact();
        return storeAvatar(
                file,
                randomName,
                contactAvatarSubDir,
                targetFile -> {
                    String accessUrl = buildAccessUrl(contactAvatarSubDir, randomName);
                    ContactAvatar newAvatar = new ContactAvatar();
                    newAvatar.setPicId(picId);
                    newAvatar.setContactId(contactId);
                    newAvatar.setFileName(randomName);
                    newAvatar.setFilePath(targetFile.toString());
                    newAvatar.setAccessUrl(accessUrl);
                    newAvatar.setCreatedAt(LocalDateTime.now());

                    if (oldAvatar != null) {
                        newAvatar.setId(oldAvatar.getId());
                        return contactAvatarMapper.updateById(newAvatar);
                    } else {
                        return contactAvatarMapper.insert(newAvatar);
                    }
                },
                oldAvatar != null ? oldAvatar.getFilePath() : null
        );
    }

    @Override
    public Map<String, String> uploadUserAvatar(MultipartFile file) {
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
        String picId = generatePicIdForUser();
        return storeAvatar(
                file,
                randomName,
                userAvatarSubDir,
                targetFile -> {
                    String accessUrl = buildAccessUrl(userAvatarSubDir, randomName);
                    UserAvatar newAvatar = new UserAvatar();
                    newAvatar.setPicId(picId);
                    newAvatar.setUserId(userId);
                    newAvatar.setFileName(randomName);
                    newAvatar.setFilePath(targetFile.toString());
                    newAvatar.setAccessUrl(accessUrl);
                    newAvatar.setCreatedAt(LocalDateTime.now());

                    if (oldAvatar != null) {
                        newAvatar.setId(oldAvatar.getId());
                        return userAvatarMapper.updateById(newAvatar);
                    } else {
                        return userAvatarMapper.insert(newAvatar);
                    }
                },
                oldAvatar != null ? oldAvatar.getFilePath() : null
        );
    }

    private Map<String, String> storeAvatar(
            MultipartFile file,
            String randomName,
            String avatarSubDir,
            AvatarPersistenceAction persistAction,
            String oldFilePath
    ) {
        Path targetDir = Paths.get(uploadDir, avatarSubDir).toAbsolutePath().normalize();
        Path targetFile = targetDir.resolve(randomName);
        try {
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            file.transferTo(targetFile.toFile());
            int affectedRows = persistAction.persist(targetFile);
            validateAffectedRows(affectedRows);
        } catch (Exception exception) {
            deletePhysicalFile(targetFile.toString());
            log.error("Failed to persist avatar file or metadata", exception);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to upload avatar");
        }

        deletePhysicalFile(oldFilePath);

        Map<String, String> response = new HashMap<>();
        response.put("fileName", randomName);
        response.put("accessUrl", buildAccessUrl(avatarSubDir, randomName));
        return response;
    }

    private String buildAccessUrl(String avatarSubDir, String randomName) {
        return "/uploads/" + avatarSubDir + "/" + randomName;
    }

    private void validateAffectedRows(int affectedRows) {
        if (affectedRows != EXPECTED_AFFECTED_ROWS) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to persist avatar metadata");
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

    @FunctionalInterface
    private interface AvatarPersistenceAction {
        int persist(Path targetFile);
    }
}
