package com.weiqiang.personal_crm_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.entity.Contact;
import com.weiqiang.personal_crm_backend.entity.Todo;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.mapper.ContactMapper;
import com.weiqiang.personal_crm_backend.mapper.TodoMapper;
import com.weiqiang.personal_crm_backend.model.dto.TodoCreateDTO;
import com.weiqiang.personal_crm_backend.model.dto.TodoQuery;
import com.weiqiang.personal_crm_backend.model.vo.TodoListVO;
import com.weiqiang.personal_crm_backend.model.vo.TodoVO;
import com.weiqiang.personal_crm_backend.security.UserContext;
import com.weiqiang.personal_crm_backend.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * 事项服务实现类
 */
@Service
@RequiredArgsConstructor
public class TodoServiceImpl extends ServiceImpl<TodoMapper, Todo> implements TodoService {

    private final TodoMapper todoMapper;
    private final ContactMapper contactMapper;

    @Override
    public TodoListVO listTodos(TodoQuery query) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "user is not logged in");
        }

        IPage<TodoVO> page = new Page<>(query.getPage(), query.getPageSize());
        todoMapper.selectTodoPage(page, query, userId);

        LocalDateTime now = LocalDateTime.now();
        for (TodoVO vo : page.getRecords()) {
            vo.setIsOverdue(vo.getTodoTime().isBefore(now) && Integer.valueOf(0).equals(vo.getStatus()));
        }

        TodoListVO listVO = new TodoListVO();
        listVO.setList(page.getRecords());
        listVO.setPage((int) page.getCurrent());
        listVO.setPageSize((int) page.getSize());
        listVO.setTotal(page.getTotal());

        return listVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TodoVO createTodo(TodoCreateDTO dto) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "user is not logged in");
        }

        // 1. 校验联系人存在且属于当前登录用户
        Contact contact = contactMapper.selectOne(
                new LambdaQueryWrapper<Contact>().eq(Contact::getCtId, dto.getContactId())
        );
        if (contact == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "contact not found");
        }
        if (!userId.equals(contact.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "access denied: contact belongs to another user");
        }

        // 2. 生成 10 位随机唯一业务 ID matterId 并落库
        String matterId = generateMatterId();

        Todo todo = new Todo();
        BeanUtils.copyProperties(dto, todo);
        todo.setMatterId(matterId);
        todo.setUserId(userId);
        todo.setStatus(0); // 默认待完成 pending
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());

        this.save(todo);

        // 返回转换的 VO
        TodoVO vo = new TodoVO();
        BeanUtils.copyProperties(todo, vo);
        vo.setContactName(contact.getName());
        vo.setIsOverdue(vo.getTodoTime().isBefore(LocalDateTime.now()) && vo.getStatus() == 0);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTodo(String matterId) {
        Todo todo = getAndValidateTodo(matterId);
        if (!Integer.valueOf(0).equals(todo.getStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "todo status conflict: only pending todos can be completed");
        }

        todo.setStatus(2); // 2 Completed
        todo.setCompletedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());
        this.updateById(todo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTodo(String matterId) {
        Todo todo = getAndValidateTodo(matterId);
        if (!Integer.valueOf(0).equals(todo.getStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "todo status conflict: only pending todos can be cancelled");
        }

        todo.setStatus(1); // 1 Cancelled
        todo.setCancelledAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());
        this.updateById(todo);
    }

    private Todo getAndValidateTodo(String matterId) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "user is not logged in");
        }

        Todo todo = this.lambdaQuery().eq(Todo::getMatterId, matterId).one();
        if (todo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "todo not found");
        }

        if (!userId.equals(todo.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "access denied: this todo belongs to another user");
        }

        return todo;
    }

    private String generateMatterId() {
        while (true) {
            String id = generateRandomAlphanumeric(10);
            Long count = this.lambdaQuery().eq(Todo::getMatterId, id).count();
            if (count == 0) {
                return id;
            }
        }
    }

    private String generateRandomAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
