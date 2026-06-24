package com.weiqiang.personal_crm_backend.service.impl;

import com.weiqiang.personal_crm_backend.entity.ContactAvatar;
import com.weiqiang.personal_crm_backend.entity.UserAvatar;
import com.weiqiang.personal_crm_backend.mapper.ContactAvatarMapper;
import com.weiqiang.personal_crm_backend.mapper.UserAvatarMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AvatarAccessServiceTest {

    @Mock
    private UserAvatarMapper userAvatarMapper;

    @Mock
    private ContactAvatarMapper contactAvatarMapper;

    @InjectMocks
    private AvatarAccessService avatarAccessService;

    @TempDir
    Path tempDir;

    @Test
    void resolveUserAvatarUrl_WhenLocalFileMissing_ShouldReturnNullAndDeleteStaleRecord() {
        UserAvatar avatar = new UserAvatar();
        avatar.setId(10L);
        avatar.setUserId("U000000001");
        avatar.setAccessUrl("/uploads/user-avatar/missing.png");
        avatar.setFilePath(tempDir.resolve("missing.png").toString());

        String avatarUrl = avatarAccessService.resolveUserAvatarUrl(avatar);

        assertNull(avatarUrl);
        verify(userAvatarMapper).deleteById(10L);
    }

    @Test
    void resolveContactAvatarUrl_WhenLocalFileMissing_ShouldReturnNullAndDeleteStaleRecord() {
        ContactAvatar avatar = new ContactAvatar();
        avatar.setId(20L);
        avatar.setContactId("C000000001");
        avatar.setAccessUrl("/uploads/contact-avatar/missing.png");
        avatar.setFilePath(tempDir.resolve("missing.png").toString());

        String avatarUrl = avatarAccessService.resolveContactAvatarUrl(avatar);

        assertNull(avatarUrl);
        verify(contactAvatarMapper).deleteById(20L);
    }

    @Test
    void resolveUserAvatarUrl_WhenLocalFileExists_ShouldKeepUrlAndRecord() throws Exception {
        Path localFile = tempDir.resolve("user-avatar.png");
        Files.writeString(localFile, "avatar");

        UserAvatar avatar = new UserAvatar();
        avatar.setId(11L);
        avatar.setUserId("U000000001");
        avatar.setAccessUrl("/uploads/user-avatar/user-avatar.png");
        avatar.setFilePath(localFile.toString());

        String avatarUrl = avatarAccessService.resolveUserAvatarUrl(avatar);

        assertEquals("/uploads/user-avatar/user-avatar.png", avatarUrl);
        verify(userAvatarMapper, never()).deleteById(11L);
    }
}
