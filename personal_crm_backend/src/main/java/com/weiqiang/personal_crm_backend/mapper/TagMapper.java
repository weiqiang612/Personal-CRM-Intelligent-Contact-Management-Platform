package com.weiqiang.personal_crm_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weiqiang.personal_crm_backend.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签 Mapper 接口
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}
