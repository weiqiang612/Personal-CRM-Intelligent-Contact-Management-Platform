package com.weiqiang.personal_crm_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiqiang.personal_crm_backend.entity.Contact;
import com.weiqiang.personal_crm_backend.entity.ContactAvatar;
import com.weiqiang.personal_crm_backend.entity.UserAvatar;
import com.weiqiang.personal_crm_backend.mapper.ContactAvatarMapper;
import com.weiqiang.personal_crm_backend.mapper.ContactMapper;
import com.weiqiang.personal_crm_backend.mapper.UserAvatarMapper;
import com.weiqiang.personal_crm_backend.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UploadController Unit Test
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private ContactAvatarMapper contactAvatarMapper;

    @Autowired
    private UserAvatarMapper userAvatarMapper;

    private String token;
    private String otherToken;
    private String myContactId;

    @BeforeEach
    void setUp() {
        token = "Bearer " + jwtUtils.generateToken("U000000001");
        otherToken = "Bearer " + jwtUtils.generateToken("U000000002");

        myContactId = "C0" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000));
        Contact contact = new Contact();
        contact.setUserId("U000000001");
        contact.setCtId(myContactId);
        contact.setName("My Test Contact");
        contact.setStatus(0);
        contactMapper.insert(contact);
    }

    @Test
    void testUploadContactAvatar_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", "dummy png content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/uploads/contact-avatar")
                        .file(file)
                        .param("contactId", myContactId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.accessUrl").exists());
    }

    @Test
    void testUploadContactAvatar_FormatError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.txt", "text/plain", "dummy text content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/uploads/contact-avatar")
                        .file(file)
                        .param("contactId", myContactId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)));
    }

    @Test
    void testUploadContactAvatar_TooLarge() throws Exception {
        byte[] largeBytes = new byte[3 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file", "large.jpg", "image/jpeg", largeBytes
        );

        mockMvc.perform(multipart("/api/v1/uploads/contact-avatar")
                        .file(file)
                        .param("contactId", myContactId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40001)));
    }

    @Test
    void testUploadContactAvatar_Forbidden() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.webp", "image/webp", "webp content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/uploads/contact-avatar")
                        .file(file)
                        .param("contactId", myContactId)
                        .header("Authorization", otherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(40301)));
    }

    @Test
    void testUploadUserAvatar_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "user.jpg", "image/jpeg", "dummy jpeg content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/uploads/user-avatar")
                        .file(file)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.accessUrl").exists());
    }

    @Test
    void testAvatarOverwriteAndDeletePhysicalFile() throws Exception {
        Path tempFile = Files.createTempFile("old_avatar", ".png");
        Files.write(tempFile, "old content".getBytes());
        assertTrue(Files.exists(tempFile));

        ContactAvatar avatar = new ContactAvatar();
        avatar.setPicId("PIC1234567");
        avatar.setContactId(myContactId);
        avatar.setFileName("old_avatar.png");
        avatar.setFilePath(tempFile.toAbsolutePath().toString());
        avatar.setAccessUrl("/uploads/contact-avatar/old_avatar.png");
        contactAvatarMapper.insert(avatar);

        MockMultipartFile file = new MockMultipartFile(
                "file", "new_avatar.png", "image/png", "new content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/uploads/contact-avatar")
                        .file(file)
                        .param("contactId", myContactId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)));

        assertFalse(Files.exists(tempFile), "Old physical file should be deleted");
    }
}
