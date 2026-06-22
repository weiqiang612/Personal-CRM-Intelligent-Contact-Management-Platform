package com.weiqiang.personal_crm_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.entity.ContactTag;
import com.weiqiang.personal_crm_backend.entity.Tag;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.mapper.ContactTagMapper;
import com.weiqiang.personal_crm_backend.mapper.TagMapper;
import com.weiqiang.personal_crm_backend.model.dto.TagSaveDTO;
import com.weiqiang.personal_crm_backend.model.vo.TagVO;
import com.weiqiang.personal_crm_backend.security.UserContext;
import com.weiqiang.personal_crm_backend.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务实现类
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    private final TagMapper tagMapper;
    private final ContactTagMapper contactTagMapper;

    @Override
    public List<TagVO> listTags() {
        String userId = getValidatedUserId();
        
        List<Tag> tags = this.lambdaQuery()
                .eq(Tag::getUserId, userId)
                .orderByDesc(Tag::getCreatedAt)
                .list();

        return tags.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TagVO createTag(TagSaveDTO dto) {
        String userId = getValidatedUserId();

        // 校验同用户下标签名称唯一
        Long count = this.lambdaQuery()
                .eq(Tag::getUserId, userId)
                .eq(Tag::getName, dto.getName().trim())
                .count();
        if (count > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "tag name already exists");
        }

        Tag tag = new Tag();
        BeanUtils.copyProperties(dto, tag);
        tag.setName(dto.getName().trim());
        tag.setUserId(userId);
        tag.setCreatedAt(LocalDateTime.now());
        tag.setUpdatedAt(LocalDateTime.now());

        this.save(tag);

        return convertToVO(tag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TagVO updateTag(Long tagId, TagSaveDTO dto) {
        Tag tag = getAndValidateTag(tagId);

        // 校验修改后的名称在同用户下是否唯一（排除自身）
        Long count = this.lambdaQuery()
                .eq(Tag::getUserId, tag.getUserId())
                .eq(Tag::getName, dto.getName().trim())
                .ne(Tag::getId, tagId)
                .count();
        if (count > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "tag name already exists");
        }

        tag.setName(dto.getName().trim());
        tag.setColor(dto.getColor());
        tag.setUpdatedAt(LocalDateTime.now());

        this.updateById(tag);

        return convertToVO(tag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long tagId) {
        Tag tag = getAndValidateTag(tagId);

        // 物理删除标签本身
        this.removeById(tagId);

        // 物理删除 contact_tag 关联表中对应当前用户的关联记录
        LambdaQueryWrapper<ContactTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContactTag::getTagId, tagId)
                .eq(ContactTag::getUserId, tag.getUserId());
        contactTagMapper.delete(wrapper);
    }

    /**
     * 从登录上下文中获取并验证用户 ID
     */
    private String getValidatedUserId() {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "user is not logged in");
        }
        return userId;
    }

    /**
     * 根据主键查询标签，并验证是否属于当前登录用户
     */
    private Tag getAndValidateTag(Long tagId) {
        String userId = getValidatedUserId();

        Tag tag = this.getById(tagId);
        if (tag == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "tag not found");
        }

        if (!userId.equals(tag.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "access denied: this tag belongs to another user");
        }

        return tag;
    }

    /**
     * 实体转为 VO
     */
    private TagVO convertToVO(Tag tag) {
        if (tag == null) {
            return null;
        }
        TagVO vo = new TagVO();
        BeanUtils.copyProperties(tag, vo);
        vo.setTagId(tag.getId());
        return vo;
    }
}
