package com.weiqiang.personal_crm_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weiqiang.personal_crm_backend.entity.Contact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 联系人 Mapper 接口
 */
@Mapper
public interface ContactMapper extends BaseMapper<Contact> {

    /**
     * 根据联系人业务 ID 查询头像访问路径
     */
    @Select("SELECT access_url FROM contact_avatar WHERE contact_id = #{contactId} LIMIT 1")
    String getAvatarUrlByContactId(@Param("contactId") String contactId);

    /**
     * 根据联系人业务 ID 查询关联的标签名称集合
     */
    @Select("SELECT t.name FROM tag t JOIN contact_tag ct ON t.id = ct.tag_id WHERE ct.contact_id = #{contactId} AND ct.user_id = #{userId} AND t.user_id = #{userId}")
    List<String> getTagsByContactIdAndUser(@Param("contactId") String contactId, @Param("userId") String userId);

    /**
     * 根据标签名称和用户 ID 安全地查询关联的联系人业务 ID 集合
     */
    @Select("SELECT ct.contact_id FROM contact_tag ct JOIN tag t ON ct.tag_id = t.id WHERE t.name = #{tag} AND ct.user_id = #{userId} AND t.user_id = #{userId}")
    List<String> getContactIdsByTagAndUser(@Param("tag") String tag, @Param("userId") String userId);
}
