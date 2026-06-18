package com.weiqiang.personal_crm_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weiqiang.personal_crm_backend.entity.Contact;
import com.weiqiang.personal_crm_backend.model.dto.ContactQueryParam;
import com.weiqiang.personal_crm_backend.model.dto.ContactSaveDTO;
import com.weiqiang.personal_crm_backend.model.vo.ContactListVO;
import com.weiqiang.personal_crm_backend.model.vo.ContactVO;

/**
 * 联系人服务类
 */
public interface ContactService extends IService<Contact> {

    /**
     * 分页查询联系人列表
     */
    ContactListVO listContacts(ContactQueryParam param);

    /**
     * 获取联系人详情
     */
    ContactVO getContactDetail(String contactId);

    /**
     * 新增联系人
     */
    ContactVO createContact(ContactSaveDTO dto);

    /**
     * 修改联系人信息
     */
    ContactVO updateContact(String contactId, ContactSaveDTO dto);

    /**
     * 加入黑名单
     */
    void addToBlacklist(String contactId);

    /**
     * 从黑名单恢复联系人
     */
    void restoreFromBlacklist(String contactId);
}
