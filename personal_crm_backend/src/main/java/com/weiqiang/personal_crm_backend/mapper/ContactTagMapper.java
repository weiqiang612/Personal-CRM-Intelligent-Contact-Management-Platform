package com.weiqiang.personal_crm_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weiqiang.personal_crm_backend.entity.ContactTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 联系人标签关联 Mapper 接口
 */
@Mapper
public interface ContactTagMapper extends BaseMapper<ContactTag> {
}
