package com.weiqiang.personal_crm_backend.controller;

import com.weiqiang.personal_crm_backend.common.Result;
import com.weiqiang.personal_crm_backend.model.vo.ActivityLogVO;
import com.weiqiang.personal_crm_backend.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 联系人活动轨迹 Controller
 */
@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
public class ContactActivityController {

    private final ActivityLogService activityLogService;

    @GetMapping("/{contactId}/activities")
    public Result<List<ActivityLogVO>> listActivities(
            @PathVariable String contactId,
            @RequestParam(defaultValue = "10") Integer limit) {
        List<ActivityLogVO> list = activityLogService.listContactActivities(contactId, limit);
        return Result.success(list);
    }
}
