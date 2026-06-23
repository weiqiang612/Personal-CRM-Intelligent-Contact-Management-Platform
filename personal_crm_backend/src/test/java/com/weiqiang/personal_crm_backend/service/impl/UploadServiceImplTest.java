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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadServiceImplTest {

    @Mock
    private ContactMapper contactMapper;

    @Mock
    private ContactAvatarMapper contactAvatarMapper;

    @Mock
    private UserAvatarMapper userAvatarMapper;

    @InjectMocks
    private UploadServiceImpl uploadService;

    @TempDir
    Path tempDir;

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    private void configureStorageProperties() {
        ReflectionTestUtils.setField(uploadService, "uploadDir", tempDir.toString());
        ReflectionTestUtils.setField(uploadService, "contactAvatarSubDir", "contact-avatar");
        ReflectionTestUtils.setField(uploadService, "userAvatarSubDir", "user-avatar");
    }

    @Test
    void uploadContactAvatar_WhenDatabaseUpdateFails_ShouldKeepOldAvatarAndDeleteNewFile() throws IOException {
        configureStorageProperties();

        UserContext.setUserId("U000000001");

        Contact contact = new Contact();
        contact.setCtId("C000000001");
        contact.setUserId("U000000001");

        Path oldAvatarFile = tempDir.resolve("legacy-avatar.png");
        Files.writeString(oldAvatarFile, "old-avatar");

        ContactAvatar oldAvatar = new ContactAvatar();
        oldAvatar.setId(1L);
        oldAvatar.setPicId("PIC0000001");
        oldAvatar.setContactId("C000000001");
        oldAvatar.setFileName("legacy-avatar.png");
        oldAvatar.setFilePath(oldAvatarFile.toString());
        oldAvatar.setAccessUrl("/uploads/contact-avatar/legacy-avatar.png");
        oldAvatar.setCreatedAt(LocalDateTime.now().minusDays(1));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "fresh-avatar.png",
                "image/png",
                "new-avatar".getBytes()
        );

        when(contactMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(contact);
        when(contactAvatarMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(oldAvatar);
        when(contactAvatarMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        doThrow(new RuntimeException("db write failed")).when(contactAvatarMapper).updateById(any(ContactAvatar.class));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> uploadService.uploadContactAvatar(file, "C000000001")
        );

        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), exception.getCode());
        assertTrue(Files.exists(oldAvatarFile), "Old avatar file should remain when database update fails");

        ArgumentCaptor<ContactAvatar> avatarCaptor = ArgumentCaptor.forClass(ContactAvatar.class);
        verify(contactAvatarMapper).updateById(avatarCaptor.capture());
        ContactAvatar persistedAttempt = avatarCaptor.getValue();
        assertEquals(oldAvatar.getId(), persistedAttempt.getId());

        Path newAvatarFile = Path.of(persistedAttempt.getFilePath());
        assertFalse(Files.exists(newAvatarFile), "New avatar file should be deleted after rollback");
    }

    @Test
    void uploadContactAvatar_WhenReplacingAvatar_ShouldDeleteOldFileAfterSuccessfulUpdate() throws IOException {
        configureStorageProperties();

        UserContext.setUserId("U000000001");

        Contact contact = new Contact();
        contact.setCtId("C000000001");
        contact.setUserId("U000000001");

        Path oldAvatarFile = tempDir.resolve("legacy-avatar.png");
        Files.writeString(oldAvatarFile, "old-avatar");

        ContactAvatar oldAvatar = new ContactAvatar();
        oldAvatar.setId(1L);
        oldAvatar.setPicId("PIC0000001");
        oldAvatar.setContactId("C000000001");
        oldAvatar.setFileName("legacy-avatar.png");
        oldAvatar.setFilePath(oldAvatarFile.toString());
        oldAvatar.setAccessUrl("/uploads/contact-avatar/legacy-avatar.png");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "fresh-avatar.png",
                "image/png",
                "new-avatar".getBytes()
        );

        when(contactMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(contact);
        when(contactAvatarMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(oldAvatar);
        when(contactAvatarMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(contactAvatarMapper.updateById(any(ContactAvatar.class))).thenReturn(1);

        uploadService.uploadContactAvatar(file, "C000000001");

        assertFalse(Files.exists(oldAvatarFile), "Old avatar file should be deleted after successful replacement");
        verify(contactAvatarMapper, never()).insert(any(ContactAvatar.class));
    }

    @Test
    void uploadContactAvatar_WhenDatabaseUpdateAffectsZeroRows_ShouldKeepOldAvatarAndDeleteNewFile() throws IOException {
        configureStorageProperties();

        UserContext.setUserId("U000000001");

        Contact contact = new Contact();
        contact.setCtId("C000000001");
        contact.setUserId("U000000001");

        Path oldAvatarFile = tempDir.resolve("legacy-avatar.png");
        Files.writeString(oldAvatarFile, "old-avatar");

        ContactAvatar oldAvatar = new ContactAvatar();
        oldAvatar.setId(1L);
        oldAvatar.setPicId("PIC0000001");
        oldAvatar.setContactId("C000000001");
        oldAvatar.setFileName("legacy-avatar.png");
        oldAvatar.setFilePath(oldAvatarFile.toString());
        oldAvatar.setAccessUrl("/uploads/contact-avatar/legacy-avatar.png");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "fresh-avatar.png",
                "image/png",
                "new-avatar".getBytes()
        );

        when(contactMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(contact);
        when(contactAvatarMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(oldAvatar);
        when(contactAvatarMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(contactAvatarMapper.updateById(any(ContactAvatar.class))).thenReturn(0);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> uploadService.uploadContactAvatar(file, "C000000001")
        );

        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), exception.getCode());
        assertTrue(Files.exists(oldAvatarFile), "Old avatar file should remain when update affects zero rows");

        ArgumentCaptor<ContactAvatar> avatarCaptor = ArgumentCaptor.forClass(ContactAvatar.class);
        verify(contactAvatarMapper).updateById(avatarCaptor.capture());
        Path newAvatarFile = Path.of(avatarCaptor.getValue().getFilePath());
        assertFalse(Files.exists(newAvatarFile), "New avatar file should be deleted after zero-row rollback");
    }

    @Test
    void uploadUserAvatar_WhenDatabaseUpdateFails_ShouldKeepOldAvatarAndDeleteNewFile() throws IOException {
        configureStorageProperties();

        UserContext.setUserId("U000000001");

        Path oldAvatarFile = tempDir.resolve("legacy-user-avatar.png");
        Files.writeString(oldAvatarFile, "old-user-avatar");

        UserAvatar oldAvatar = new UserAvatar();
        oldAvatar.setId(1L);
        oldAvatar.setPicId("UPIC000001");
        oldAvatar.setUserId("U000000001");
        oldAvatar.setFileName("legacy-user-avatar.png");
        oldAvatar.setFilePath(oldAvatarFile.toString());
        oldAvatar.setAccessUrl("/uploads/user-avatar/legacy-user-avatar.png");
        oldAvatar.setCreatedAt(LocalDateTime.now().minusDays(1));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "fresh-user-avatar.png",
                "image/png",
                "new-user-avatar".getBytes()
        );

        when(userAvatarMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(oldAvatar);
        when(userAvatarMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        doThrow(new RuntimeException("db write failed")).when(userAvatarMapper).updateById(any(UserAvatar.class));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> uploadService.uploadUserAvatar(file)
        );

        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), exception.getCode());
        assertTrue(Files.exists(oldAvatarFile), "Old user avatar file should remain when database update fails");

        ArgumentCaptor<UserAvatar> avatarCaptor = ArgumentCaptor.forClass(UserAvatar.class);
        verify(userAvatarMapper).updateById(avatarCaptor.capture());
        Path newAvatarFile = Path.of(avatarCaptor.getValue().getFilePath());
        assertFalse(Files.exists(newAvatarFile), "New user avatar file should be deleted after rollback");
    }
}
