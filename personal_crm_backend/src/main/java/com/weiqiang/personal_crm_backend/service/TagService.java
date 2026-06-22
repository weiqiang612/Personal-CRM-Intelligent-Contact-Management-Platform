package com.weiqiang.personal_crm_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weiqiang.personal_crm_backend.entity.Tag;
import com.weiqiang.personal_crm_backend.model.dto.TagSaveDTO;
import com.weiqiang.personal_crm_backend.model.vo.TagVO;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService extends IService<Tag> {

    /**
     * 获取当前用户的所有标签列表
     */
    List<TagVO> listTags();

    /**
     * 新增标签
     */
    TagVO createTag(TagSaveDTO dto);

    /**
     * 修改标签
     */
    TagVO updateTag(Long tagId, TagSaveDTO dto);

    /**
     * 删除标签
     */
    void deleteTag(Long tagId);
}
