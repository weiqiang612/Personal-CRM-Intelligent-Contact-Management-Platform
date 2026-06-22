package com.weiqiang.personal_crm_backend.controller;

import com.weiqiang.personal_crm_backend.common.Result;
import com.weiqiang.personal_crm_backend.model.dto.TagSaveDTO;
import com.weiqiang.personal_crm_backend.model.vo.TagVO;
import com.weiqiang.personal_crm_backend.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签接口控制器
 */
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * 获取标签列表
     */
    @GetMapping
    public Result<List<TagVO>> listTags() {
        List<TagVO> tags = tagService.listTags();
        return Result.success(tags);
    }

    /**
     * 新增标签
     */
    @PostMapping
    public Result<TagVO> createTag(@Validated @RequestBody TagSaveDTO dto) {
        TagVO tagVO = tagService.createTag(dto);
        return Result.success(tagVO);
    }

    /**
     * 修改标签
     */
    @PutMapping("/{tagId}")
    public Result<TagVO> updateTag(@PathVariable Long tagId, @Validated @RequestBody TagSaveDTO dto) {
        TagVO tagVO = tagService.updateTag(tagId, dto);
        return Result.success(tagVO);
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{tagId}")
    public Result<Void> deleteTag(@PathVariable Long tagId) {
        tagService.deleteTag(tagId);
        return Result.success();
    }
}
