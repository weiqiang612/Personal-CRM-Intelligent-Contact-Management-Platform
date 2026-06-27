package com.weiqiang.personal_crm_backend.controller;

import com.weiqiang.personal_crm_backend.common.Result;
import com.weiqiang.personal_crm_backend.model.dto.TodoCreateDTO;
import com.weiqiang.personal_crm_backend.model.dto.TodoQuery;
import com.weiqiang.personal_crm_backend.model.vo.TodoListVO;
import com.weiqiang.personal_crm_backend.model.vo.TodoVO;
import com.weiqiang.personal_crm_backend.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 事项接口控制器
 */
@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    /**
     * 获取事项列表 (支持分页、多条件筛选与排序)
     */
    @GetMapping
    public Result<TodoListVO> listTodos(TodoQuery query) {
        TodoListVO listVO = todoService.listTodos(query);
        return Result.success(listVO);
    }

    /**
     * 新增事项
     */
    @PostMapping
    public Result<TodoVO> createTodo(@Validated @RequestBody TodoCreateDTO dto) {
        TodoVO todoVO = todoService.createTodo(dto);
        return Result.success(todoVO);
    }

    /**
     * 完成事项
     */
    @PatchMapping("/{matterId}/complete")
    public Result<Void> completeTodo(@PathVariable String matterId) {
        todoService.completeTodo(matterId);
        return Result.success();
    }

    /**
     * 取消事项
     */
    @PatchMapping("/{matterId}/cancel")
    public Result<Void> cancelTodo(@PathVariable String matterId) {
        todoService.cancelTodo(matterId);
        return Result.success();
    }

    /**
     * 删除事项 (逻辑删除)
     */
    @DeleteMapping("/{matterId}")
    public Result<Void> deleteTodo(@PathVariable String matterId) {
        todoService.deleteTodo(matterId);
        return Result.success();
    }
}
