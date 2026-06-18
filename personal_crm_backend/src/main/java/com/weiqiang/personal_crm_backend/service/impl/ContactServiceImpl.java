package com.weiqiang.personal_crm_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.entity.Contact;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.mapper.ContactMapper;
import com.weiqiang.personal_crm_backend.model.dto.ContactQueryParam;
import com.weiqiang.personal_crm_backend.model.dto.ContactSaveDTO;
import com.weiqiang.personal_crm_backend.model.vo.ContactListVO;
import com.weiqiang.personal_crm_backend.model.vo.ContactVO;
import com.weiqiang.personal_crm_backend.security.UserContext;
import com.weiqiang.personal_crm_backend.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 联系人服务实现类
 */
@Service
@RequiredArgsConstructor
public class ContactServiceImpl extends ServiceImpl<ContactMapper, Contact> implements ContactService {

    private final ContactMapper contactMapper;

    @Override
    public ContactListVO listContacts(ContactQueryParam param) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "user is not logged in");
        }

        Page<Contact> page = new Page<>(param.getPage(), param.getPageSize());

        LambdaQueryWrapper<Contact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contact::getUserId, userId);

        // 状态筛选（0 正常，1 黑名单）
        if (param.getStatus() != null) {
            queryWrapper.eq(Contact::getStatus, param.getStatus());
        }

        // 性别筛选（0 未知，1 男，2 女）
        if (param.getGender() != null) {
            queryWrapper.eq(Contact::getGender, param.getGender());
        }

        // 标签筛选（多对多子查询过滤）
        String tag = param.getTag();
        if (tag != null && !tag.trim().isEmpty()) {
            String escapedTag = tag.trim().replace("'", "''");
            queryWrapper.inSql(Contact::getCtId, 
                "SELECT contact_id FROM contact_tag ct JOIN tag t ON ct.tag_id = t.id WHERE t.name = '" + escapedTag + "' AND ct.user_id = '" + userId + "'"
            );
        }

        // 模糊搜索（姓名、手机号、微信）
        String keyword = param.getKeyword();
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Contact::getName, keyword.trim())
                    .or().like(Contact::getPhone, keyword.trim())
                    .or().like(Contact::getWechat, keyword.trim())
            );
        }

        // 排序规则
        String sortBy = param.getSortBy();
        String sortOrder = param.getSortOrder();
        boolean isAsc = "asc".equalsIgnoreCase(sortOrder);

        if (sortBy != null && !sortBy.trim().isEmpty()) {
            if ("createdAt".equals(sortBy)) {
                queryWrapper.orderBy(true, isAsc, Contact::getCreatedAt);
            } else if ("birthday".equals(sortBy)) {
                queryWrapper.orderBy(true, isAsc, Contact::getBirthday);
            }
        } else {
            // 默认按创建时间降序
            queryWrapper.orderByDesc(Contact::getCreatedAt);
        }

        this.page(page, queryWrapper);

        List<ContactVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        ContactListVO listVO = new ContactListVO();
        listVO.setList(voList);
        listVO.setPage((int) page.getCurrent());
        listVO.setPageSize((int) page.getSize());
        listVO.setTotal(page.getTotal());

        return listVO;
    }

    @Override
    public ContactVO getContactDetail(String contactId) {
        Contact contact = getAndValidateContact(contactId);
        return convertToVO(contact);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContactVO createContact(ContactSaveDTO dto) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "user is not logged in");
        }

        // 校验电话格式与合法性
        validateSaveDTO(dto);

        Contact contact = new Contact();
        BeanUtils.copyProperties(dto, contact);
        contact.setUserId(userId);
        
        // 分配全局唯一的 ctId
        String ctId = generateContactId();
        contact.setCtId(ctId);
        contact.setStatus(0); // 默认正常
        contact.setCreatedAt(LocalDateTime.now());
        contact.setUpdatedAt(LocalDateTime.now());

        this.save(contact);

        return convertToVO(contact);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContactVO updateContact(String contactId, ContactSaveDTO dto) {
        Contact contact = getAndValidateContact(contactId);
        
        validateSaveDTO(dto);

        // 更新字段
        contact.setName(dto.getName());
        contact.setAddress(dto.getAddress());
        contact.setPostcode(dto.getPostcode());
        contact.setQq(dto.getQq());
        contact.setWechat(dto.getWechat());
        contact.setEmail(dto.getEmail());
        contact.setGender(dto.getGender());
        contact.setBirthday(dto.getBirthday());
        contact.setPhone(dto.getPhone());
        contact.setUpdatedAt(LocalDateTime.now());

        this.updateById(contact);

        return convertToVO(contact);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addToBlacklist(String contactId) {
        Contact contact = getAndValidateContact(contactId);
        if (contact.getStatus() == 1) {
            throw new BusinessException(ErrorCode.CONFLICT, "contact is already in the blacklist");
        }
        contact.setStatus(1);
        contact.setUpdatedAt(LocalDateTime.now());
        this.updateById(contact);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreFromBlacklist(String contactId) {
        Contact contact = getAndValidateContact(contactId);
        if (contact.getStatus() == 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "contact is already active");
        }
        contact.setStatus(0);
        contact.setUpdatedAt(LocalDateTime.now());
        this.updateById(contact);
    }

    /**
     * 并发安全地生成全局唯一联系人业务编号 (C000000001格式)
     */
    private synchronized String generateContactId() {
        Contact latestContact = this.lambdaQuery()
                .orderByDesc(Contact::getCtId)
                .last("LIMIT 1")
                .one();
        if (latestContact == null) {
            return "C000000001";
        }
        String latestCtId = latestContact.getCtId();
        try {
            // 解析 C 后面的 9 位数字并自增
            int nextNumber = Integer.parseInt(latestCtId.substring(1)) + 1;
            return String.format("C%09d", nextNumber);
        } catch (Exception e) {
            // 降级使用时间戳后缀，确保唯一性
            return "C" + (System.currentTimeMillis() % 1000000000L);
        }
    }

    /**
     * 根据 ctId 查询并验证该联系人是否属于当前用户
     */
    private Contact getAndValidateContact(String contactId) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "user is not logged in");
        }

        Contact contact = this.lambdaQuery()
                .eq(Contact::getCtId, contactId)
                .one();
        if (contact == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "contact not found");
        }

        if (!userId.equals(contact.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "access denied: this contact belongs to another user");
        }

        return contact;
    }

    /**
     * DTO 保存字段校验（除 annotations 以外的补充逻辑）
     */
    private void validateSaveDTO(ContactSaveDTO dto) {
        // 生日校验，生日不能是未来时间
        if (dto.getBirthday() != null && dto.getBirthday().isAfter(LocalDate.now())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "birthday cannot be in the future");
        }
    }

    /**
     * 实体转 VO，并关联头像查询
     */
    private ContactVO convertToVO(Contact contact) {
        if (contact == null) {
            return null;
        }
        ContactVO vo = new ContactVO();
        BeanUtils.copyProperties(contact, vo);
        vo.setContactId(contact.getCtId());
        
        // 关联查询头像 URL
        String avatarUrl = contactMapper.getAvatarUrlByContactId(contact.getCtId());
        vo.setAvatarUrl(avatarUrl);
        
        // 关联查询标签列表
        List<String> tags = contactMapper.getTagsByContactId(contact.getCtId());
        vo.setTags(tags);
        
        return vo;
    }
}
