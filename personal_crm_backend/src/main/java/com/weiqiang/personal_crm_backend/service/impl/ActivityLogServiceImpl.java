package com.weiqiang.personal_crm_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weiqiang.personal_crm_backend.common.Constants;
import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.entity.ActivityLog;
import com.weiqiang.personal_crm_backend.entity.Contact;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.mapper.ActivityLogMapper;
import com.weiqiang.personal_crm_backend.mapper.ContactMapper;
import com.weiqiang.personal_crm_backend.model.vo.ActivityLogVO;
import com.weiqiang.personal_crm_backend.security.UserContext;
import com.weiqiang.personal_crm_backend.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 联系人活动日志服务实现类
 */
@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl extends ServiceImpl<ActivityLogMapper, ActivityLog> implements ActivityLogService {

    private final ActivityLogMapper activityLogMapper;
    private final ContactMapper contactMapper;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveActivity(String userId, String contactId, String eventType, String title, String description) {
        if (userId == null || contactId == null || eventType == null) {
            return;
        }

        ActivityLog log = new ActivityLog();
        log.setActivityId(generateActivityId());
        log.setUserId(userId);
        log.setContactId(contactId);
        log.setEventType(eventType);
        log.setTitle(title);
        log.setDescription(description);
        log.setOccurredAt(LocalDateTime.now());
        log.setCreatedAt(LocalDateTime.now());

        this.save(log);
    }

    @Override
    public List<ActivityLogVO> listContactActivities(String contactId, Integer limit) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, Constants.Message.USER_NOT_LOGGED_IN);
        }

        // 校验联系人存在性与用户归属权限
        Contact contact = contactMapper.selectOne(
                new LambdaQueryWrapper<Contact>().eq(Contact::getCtId, contactId)
        );
        if (contact == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, Constants.Message.CONTACT_NOT_FOUND);
        }
        if (!userId.equals(contact.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, Constants.Message.CONTACT_ACCESS_DENIED);
        }

        int realLimit = (limit == null || limit <= 0) ? 10 : Math.min(limit, 50);

        LambdaQueryWrapper<ActivityLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityLog::getUserId, userId)
                .eq(ActivityLog::getContactId, contactId)
                .orderByDesc(ActivityLog::getOccurredAt)
                .orderByDesc(ActivityLog::getId)
                .last("LIMIT " + realLimit);

        List<ActivityLog> logs = activityLogMapper.selectList(wrapper);

        return logs.stream().map(log -> {
            ActivityLogVO vo = new ActivityLogVO();
            BeanUtils.copyProperties(log, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    private String generateActivityId() {
        return "ACT" + String.format("%09d", RANDOM.nextInt(1_000_000_000));
    }
}
