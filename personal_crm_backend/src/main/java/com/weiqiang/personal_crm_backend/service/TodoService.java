package com.weiqiang.personal_crm_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weiqiang.personal_crm_backend.entity.Todo;
import com.weiqiang.personal_crm_backend.model.dto.TodoCreateDTO;
import com.weiqiang.personal_crm_backend.model.dto.TodoQuery;
import com.weiqiang.personal_crm_backend.model.vo.TodoListVO;
import com.weiqiang.personal_crm_backend.model.vo.TodoVO;

/**
 * 事项服务类
 */
public interface TodoService extends IService<Todo> {

    /**
     * 分页查询事项列表
     *
     * @param query 查询参数
     * @return 事项列表展示包装类
     */
    TodoListVO listTodos(TodoQuery query);

    /**
     * 创建事项
     *
     * @param dto 创建参数
     * @return 创建的事项展示对象
     */
    TodoVO createTodo(TodoCreateDTO dto);

    /**
     * 完成事项
     *
     * @param matterId 事项业务 ID
     */
    void completeTodo(String matterId);

    /**
     * 取消事项
     *
     * @param matterId 事项业务 ID
     */
    void cancelTodo(String matterId);
}
