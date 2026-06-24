package com.weiqiang.personal_crm_backend.service.impl;

import com.weiqiang.personal_crm_backend.entity.ContactAvatar;
import com.weiqiang.personal_crm_backend.entity.UserAvatar;
import com.weiqiang.personal_crm_backend.mapper.ContactAvatarMapper;
import com.weiqiang.personal_crm_backend.mapper.UserAvatarMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Resolves persisted avatar URLs and self-heals stale local-file records.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AvatarAccessService {

    private final UserAvatarMapper userAvatarMapper;
    private final ContactAvatarMapper contactAvatarMapper;

    public String resolveUserAvatarUrl(UserAvatar avatar) {
        if (avatar == null) {
            return null;
        }
        return resolveAvatarUrl(
                avatar.getUserId(),
                avatar.getId(),
                avatar.getAccessUrl(),
                avatar.getFilePath(),
                "user avatar",
                () -> userAvatarMapper.deleteById(avatar.getId())
        );
    }

    public String resolveContactAvatarUrl(ContactAvatar avatar) {
        if (avatar == null) {
            return null;
        }
        return resolveAvatarUrl(
                avatar.getContactId(),
                avatar.getId(),
                avatar.getAccessUrl(),
                avatar.getFilePath(),
                "contact avatar",
                () -> contactAvatarMapper.deleteById(avatar.getId())
        );
    }

    private String resolveAvatarUrl(
            String ownerId,
            Long recordId,
            String accessUrl,
            String filePath,
            String avatarType,
            Runnable staleRecordCleanup
    ) {
        if (!StringUtils.hasText(accessUrl)) {
            return null;
        }

        if (accessUrl.startsWith("http://") || accessUrl.startsWith("https://")) {
            return accessUrl;
        }

        if (!accessUrl.startsWith("/uploads/")) {
            return accessUrl;
        }

        if (!StringUtils.hasText(filePath)) {
            log.warn("Skip stale {} without file path, ownerId={}, recordId={}", avatarType, ownerId, recordId);
            staleRecordCleanup.run();
            return null;
        }

        Path avatarPath = Paths.get(filePath).toAbsolutePath().normalize();
        if (Files.isRegularFile(avatarPath)) {
            return accessUrl;
        }

        log.warn("Delete stale {} record, ownerId={}, recordId={}, filePath={}", avatarType, ownerId, recordId, avatarPath);
        staleRecordCleanup.run();
        return null;
    }
}
