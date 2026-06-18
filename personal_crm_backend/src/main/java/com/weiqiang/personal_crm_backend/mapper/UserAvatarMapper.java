package com.weiqiang.personal_crm_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weiqiang.personal_crm_backend.entity.UserAvatar;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户头像 Mapper 接口
 */
@Mapper
public interface UserAvatarMapper extends BaseMapper<UserAvatar> {
}
