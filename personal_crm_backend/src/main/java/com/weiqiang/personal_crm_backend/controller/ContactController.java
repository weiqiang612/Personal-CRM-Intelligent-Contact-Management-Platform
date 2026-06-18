package com.weiqiang.personal_crm_backend.controller;

import com.weiqiang.personal_crm_backend.common.Result;
import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.model.dto.ContactQueryParam;
import com.weiqiang.personal_crm_backend.model.dto.ContactSaveDTO;
import com.weiqiang.personal_crm_backend.model.dto.ContactStatusDTO;
import com.weiqiang.personal_crm_backend.model.vo.ContactListVO;
import com.weiqiang.personal_crm_backend.model.vo.ContactVO;
import com.weiqiang.personal_crm_backend.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 联系人及黑名单接口控制器
 */
@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    /**
     * 获取联系人列表 (支持分页、筛选、模糊搜索与排序)
     */
    @GetMapping
    public Result<ContactListVO> listContacts(ContactQueryParam param) {
        ContactListVO listVO = contactService.listContacts(param);
        return Result.success(listVO);
    }

    /**
     * 获取单个联系人详情
     */
    @GetMapping("/{contactId}")
    public Result<ContactVO> getContactDetail(@PathVariable String contactId) {
        ContactVO contactVO = contactService.getContactDetail(contactId);
        return Result.success(contactVO);
    }

    /**
     * 新增联系人
     */
    @PostMapping
    public Result<ContactVO> createContact(@Validated @RequestBody ContactSaveDTO dto) {
        ContactVO contactVO = contactService.createContact(dto);
        return Result.success(contactVO);
    }

    /**
     * 修改联系人信息
     */
    @PutMapping("/{contactId}")
    public Result<ContactVO> updateContact(@PathVariable String contactId, @Validated @RequestBody ContactSaveDTO dto) {
        ContactVO contactVO = contactService.updateContact(contactId, dto);
        return Result.success(contactVO);
    }

    /**
     * 将联系人加入黑名单 (拉黑)
     */
    @PatchMapping("/{contactId}/blacklist")
    public Result<Void> addToBlacklist(@PathVariable String contactId, @Validated @RequestBody ContactStatusDTO dto) {
        if (!Integer.valueOf(1).equals(dto.getStatus())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "blacklist status must be 1");
        }
        contactService.addToBlacklist(contactId);
        return Result.success();
    }

    /**
     * 从黑名单恢复联系人 (移出黑名单)
     */
    @PatchMapping("/{contactId}/restore")
    public Result<Void> restoreFromBlacklist(@PathVariable String contactId, @Validated @RequestBody ContactStatusDTO dto) {
        if (!Integer.valueOf(0).equals(dto.getStatus())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "restore status must be 0");
        }
        contactService.restoreFromBlacklist(contactId);
        return Result.success();
    }
}
