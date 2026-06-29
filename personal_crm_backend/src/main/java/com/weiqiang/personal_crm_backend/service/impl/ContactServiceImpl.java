package com.weiqiang.personal_crm_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weiqiang.personal_crm_backend.common.Constants;
import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.entity.Contact;
import com.weiqiang.personal_crm_backend.entity.ContactAvatar;
import com.weiqiang.personal_crm_backend.entity.ContactTag;
import com.weiqiang.personal_crm_backend.entity.Tag;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.mapper.ContactAvatarMapper;
import com.weiqiang.personal_crm_backend.mapper.ContactMapper;
import com.weiqiang.personal_crm_backend.mapper.ContactTagMapper;
import com.weiqiang.personal_crm_backend.mapper.TagMapper;
import com.weiqiang.personal_crm_backend.model.dto.ContactQueryParam;
import com.weiqiang.personal_crm_backend.model.dto.ContactSaveDTO;
import com.weiqiang.personal_crm_backend.model.vo.ContactListVO;
import com.weiqiang.personal_crm_backend.model.vo.ContactVO;
import com.weiqiang.personal_crm_backend.security.UserContext;
import com.weiqiang.personal_crm_backend.service.ActivityLogService;
import com.weiqiang.personal_crm_backend.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 联系人服务实现类
 */
@Service
@RequiredArgsConstructor
public class ContactServiceImpl extends ServiceImpl<ContactMapper, Contact> implements ContactService {

    private final ContactMapper contactMapper;
    private final ContactAvatarMapper contactAvatarMapper;
    private final TagMapper tagMapper;
    private final ContactTagMapper contactTagMapper;
    private final AvatarAccessService avatarAccessService;
    private final ActivityLogService activityLogService;
    private final StringRedisTemplate redisTemplate;

    @Override
    public ContactListVO listContacts(ContactQueryParam param) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, Constants.Message.USER_NOT_LOGGED_IN);
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

        // 标签筛选（安全参数化过滤）
        String tag = param.getTag();
        if (tag != null && !tag.trim().isEmpty()) {
            List<String> contactIds = contactMapper.getContactIdsByTagAndUser(tag.trim(), userId);
            if (contactIds.isEmpty()) {
                ContactListVO listVO = new ContactListVO();
                listVO.setList(Collections.emptyList());
                listVO.setPage(param.getPage());
                listVO.setPageSize(param.getPageSize());
                listVO.setTotal(0L);
                return listVO;
            }
            queryWrapper.in(Contact::getCtId, contactIds);
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
                .map(contact -> convertToVO(contact, userId))
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
        return convertToVO(contact, UserContext.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContactVO createContact(ContactSaveDTO dto) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, Constants.Message.USER_NOT_LOGGED_IN);
        }

        // 校验电话格式与合法性
        validateSaveDTO(dto);

        // 验证标签是否越权
        validateTagIds(dto.getTagIds(), userId);

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

        // 保存标签关联
        saveContactTags(ctId, dto.getTagIds(), userId);

        // 活动轨迹留痕
        activityLogService.saveActivity(userId, ctId, "CONTACT_CREATED", "创建了联系人", "新增联系人信息：" + contact.getName());
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            activityLogService.saveActivity(userId, ctId, "TAG_CHANGED", "变更了联系人标签", "绑定了联系人标签");
        }

        // 看板二级缓存失效
        redisTemplate.delete(Constants.REDIS_KEY_DASHBOARD_CACHE + userId);

        return convertToVO(contact, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContactVO updateContact(String contactId, ContactSaveDTO dto) {
        Contact contact = getAndValidateContact(contactId);
        String userId = contact.getUserId();
        
        validateSaveDTO(dto);

        // 验证标签是否越权
        validateTagIds(dto.getTagIds(), userId);

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
        contact.setRemarks(dto.getRemarks());
        contact.setUpdatedAt(LocalDateTime.now());

        this.updateById(contact);

        // 清理旧的标签关联
        LambdaQueryWrapper<ContactTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContactTag::getContactId, contactId)
                .eq(ContactTag::getUserId, userId);
        contactTagMapper.delete(wrapper);

        // 保存新的标签关联
        saveContactTags(contactId, dto.getTagIds(), userId);

        // 活动轨迹留痕
        activityLogService.saveActivity(userId, contactId, "CONTACT_UPDATED", "修改了联系人资料", "更新联系人信息：" + contact.getName());
        if (dto.getTagIds() != null) {
            activityLogService.saveActivity(userId, contactId, "TAG_CHANGED", "变更了联系人标签", "更新了联系人标签设置");
        }

        // 看板二级缓存失效
        redisTemplate.delete(Constants.REDIS_KEY_DASHBOARD_CACHE + userId);

        return convertToVO(contact, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addToBlacklist(String contactId) {
        Contact contact = getAndValidateContact(contactId);
        if (contact.getStatus() == 1) {
            throw new BusinessException(ErrorCode.CONFLICT, Constants.Message.CONTACT_ALREADY_BLACKLISTED);
        }
        contact.setStatus(1);
        contact.setUpdatedAt(LocalDateTime.now());
        this.updateById(contact);

        // 活动轨迹留痕
        activityLogService.saveActivity(contact.getUserId(), contactId, "BLACKLIST_CHANGED", "移入黑名单", "将联系人 " + contact.getName() + " 移入黑名单");

        // 看板二级缓存失效
        redisTemplate.delete(Constants.REDIS_KEY_DASHBOARD_CACHE + contact.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreFromBlacklist(String contactId) {
        Contact contact = getAndValidateContact(contactId);
        if (contact.getStatus() == 0) {
            throw new BusinessException(ErrorCode.CONFLICT, Constants.Message.CONTACT_ALREADY_ACTIVE);
        }
        contact.setStatus(0);
        contact.setUpdatedAt(LocalDateTime.now());
        this.updateById(contact);

        // 活动轨迹留痕
        activityLogService.saveActivity(contact.getUserId(), contactId, "BLACKLIST_CHANGED", "移出黑名单", "将联系人 " + contact.getName() + " 从黑名单恢复");

        // 看板二级缓存失效
        redisTemplate.delete(Constants.REDIS_KEY_DASHBOARD_CACHE + contact.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteContact(String contactId) {
        Contact contact = getAndValidateContact(contactId);
        String userId = contact.getUserId();
        
        // 1. 执行逻辑删除联系人
        this.removeById(contact.getId());

        // 2. 清理标签关联关系
        LambdaQueryWrapper<ContactTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContactTag::getContactId, contactId)
                .eq(ContactTag::getUserId, userId);
        contactTagMapper.delete(wrapper);

        // 看板二级缓存失效
        redisTemplate.delete(Constants.REDIS_KEY_DASHBOARD_CACHE + userId);
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
            throw new BusinessException(ErrorCode.UNAUTHORIZED, Constants.Message.USER_NOT_LOGGED_IN);
        }

        Contact contact = this.lambdaQuery()
                .eq(Contact::getCtId, contactId)
                .one();
        if (contact == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, Constants.Message.CONTACT_NOT_FOUND);
        }

        if (!userId.equals(contact.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, Constants.Message.CONTACT_ACCESS_DENIED);
        }

        return contact;
    }

    /**
     * DTO 保存字段校验（除 annotations 以外的补充逻辑）
     */
    private void validateSaveDTO(ContactSaveDTO dto) {
        // 生日校验，生日不能是未来时间
        if (dto.getBirthday() != null && dto.getBirthday().isAfter(LocalDate.now())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, Constants.Message.BIRTHDAY_FUTURE_INVALID);
        }
    }

    /**
     * 实体转 VO，并关联头像查询
     */
    private ContactVO convertToVO(Contact contact, String userId) {
        if (contact == null) {
            return null;
        }
        ContactVO vo = new ContactVO();
        BeanUtils.copyProperties(contact, vo);
        vo.setContactId(contact.getCtId());
        
        // 关联查询头像 URL
        ContactAvatar avatar = contactAvatarMapper.selectOne(
                new LambdaQueryWrapper<ContactAvatar>()
                        .eq(ContactAvatar::getContactId, contact.getCtId())
        );
        vo.setAvatarUrl(avatarAccessService.resolveContactAvatarUrl(avatar));
        
        // 关联查询标签列表
        List<String> tags = contactMapper.getTagsByContactIdAndUser(contact.getCtId(), userId);
        vo.setTags(tags);
        
        return vo;
    }

    /**
     * 校验传入的标签 ID 集合是否完全归属于当前登录用户
     */
    private void validateTagIds(List<Long> tagIds, String userId) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        List<Long> uniqueTagIds = tagIds.stream().distinct().collect(Collectors.toList());
        
        Long count = tagMapper.selectCount(new LambdaQueryWrapper<Tag>()
                .in(Tag::getId, uniqueTagIds)
                .eq(Tag::getUserId, userId));

        if (count.intValue() != uniqueTagIds.size()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, Constants.Message.INVALID_TAG_ACCESS_DENIED);
        }
    }

    /**
     * 保存联系人与标签的关联映射关系
     */
    private void saveContactTags(String contactId, List<Long> tagIds, String userId) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        List<Long> uniqueTagIds = tagIds.stream().distinct().collect(Collectors.toList());
        for (Long tagId : uniqueTagIds) {
            ContactTag contactTag = new ContactTag();
            contactTag.setUserId(userId);
            contactTag.setContactId(contactId);
            contactTag.setTagId(tagId);
            contactTag.setCreatedAt(LocalDateTime.now());
            contactTagMapper.insert(contactTag);
        }
    }
}
