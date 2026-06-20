package com.weiqiang.personal_crm_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.weiqiang.personal_crm_backend.entity.Todo;
import com.weiqiang.personal_crm_backend.model.dto.TodoQuery;
import com.weiqiang.personal_crm_backend.model.vo.TodoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 事项 Mapper 接口
 */
@Mapper
public interface TodoMapper extends BaseMapper<Todo> {

    /**
     * 多条件分页联查事项列表，从 contact 表获取姓名，支持根据 sortOrder 按 todoTime 排序
     *
     * @param page      分页对象
     * @param query     查询条件
     * @param userId    登录用户 ID
     * @return 分页展示列表
     */
    IPage<TodoVO> selectTodoPage(
            IPage<TodoVO> page,
            @Param("query") TodoQuery query,
            @Param("userId") String userId
    );
}
