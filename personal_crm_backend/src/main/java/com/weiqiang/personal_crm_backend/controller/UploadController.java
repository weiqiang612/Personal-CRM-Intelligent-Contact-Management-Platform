package com.weiqiang.personal_crm_backend.controller;

import com.weiqiang.personal_crm_backend.common.Result;
import com.weiqiang.personal_crm_backend.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Controller for uploading avatar files
 */
@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    /**
     * Upload contact avatar
     */
    @PostMapping("/contact-avatar")
    public Result<Map<String, String>> uploadContactAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("contactId") String contactId) {

        Map<String, String> response = uploadService.uploadContactAvatar(file, contactId);
        return Result.success(response);
    }

    /**
     * Upload current user avatar
     */
    @PostMapping("/user-avatar")
    public Result<Map<String, String>> uploadUserAvatar(@RequestParam("file") MultipartFile file) {

        Map<String, String> response = uploadService.uploadUserAvatar(file);
        return Result.success(response);
    }
}
