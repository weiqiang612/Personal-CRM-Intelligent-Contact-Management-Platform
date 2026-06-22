package com.weiqiang.personal_crm_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.weiqiang.personal_crm_backend.common.ErrorCode;
import com.weiqiang.personal_crm_backend.common.Result;
import com.weiqiang.personal_crm_backend.entity.Contact;
import com.weiqiang.personal_crm_backend.entity.Todo;
import com.weiqiang.personal_crm_backend.exception.BusinessException;
import com.weiqiang.personal_crm_backend.mapper.ContactMapper;
import com.weiqiang.personal_crm_backend.mapper.TodoMapper;
import com.weiqiang.personal_crm_backend.model.vo.ContactGenderDistributionVO;
import com.weiqiang.personal_crm_backend.model.vo.DashboardOverviewVO;
import com.weiqiang.personal_crm_backend.model.vo.TodoTrendVO;
import com.weiqiang.personal_crm_backend.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for dashboard statistics
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ContactMapper contactMapper;
    private final TodoMapper todoMapper;

    /**
     * Get dashboard overview metrics
     */
    @GetMapping("/overview")
    public Result<DashboardOverviewVO> getOverview() {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        DashboardOverviewVO overview = new DashboardOverviewVO();

        // 1. contactCount (status = 0)
        Long contactCount = contactMapper.selectCount(
                new LambdaQueryWrapper<Contact>()
                        .eq(Contact::getUserId, userId)
                        .eq(Contact::getStatus, 0)
        );
        overview.setContactCount(contactCount);

        // 2. blacklistCount (status = 1)
        Long blacklistCount = contactMapper.selectCount(
                new LambdaQueryWrapper<Contact>()
                        .eq(Contact::getUserId, userId)
                        .eq(Contact::getStatus, 1)
        );
        overview.setBlacklistCount(blacklistCount);

        // 3. pendingTodoCount (status = 0)
        Long pendingTodoCount = todoMapper.selectCount(
                new LambdaQueryWrapper<Todo>()
                        .eq(Todo::getUserId, userId)
                        .eq(Todo::getStatus, 0)
        );
        overview.setPendingTodoCount(pendingTodoCount);

        // 4. todayTodoCount (00:00 - 23:59:59, status = 0)
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX);
        Long todayTodoCount = todoMapper.selectCount(
                new LambdaQueryWrapper<Todo>()
                        .eq(Todo::getUserId, userId)
                        .eq(Todo::getStatus, 0)
                        .ge(Todo::getTodoTime, startOfToday)
                        .le(Todo::getTodoTime, endOfToday)
        );
        overview.setTodayTodoCount(todayTodoCount);

        // 5. overdueTodoCount (before now, status = 0)
        Long overdueTodoCount = todoMapper.selectCount(
                new LambdaQueryWrapper<Todo>()
                        .eq(Todo::getUserId, userId)
                        .eq(Todo::getStatus, 0)
                        .lt(Todo::getTodoTime, LocalDateTime.now())
        );
        overview.setOverdueTodoCount(overdueTodoCount);

        return Result.success(overview);
    }

    /**
     * Get todo trend for future days
     */
    @GetMapping("/todo-trend")
    public Result<List<TodoTrendVO>> getTodoTrend(@RequestParam(value = "days", defaultValue = "7") Integer days) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        List<TodoTrendVO> trendList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < days; i++) {
            LocalDate targetDate = today.plusDays(i);
            LocalDateTime start = targetDate.atStartOfDay();
            LocalDateTime end = targetDate.atTime(LocalTime.MAX);

            Long count = todoMapper.selectCount(
                    new LambdaQueryWrapper<Todo>()
                            .eq(Todo::getUserId, userId)
                            .eq(Todo::getStatus, 0)
                            .ge(Todo::getTodoTime, start)
                            .le(Todo::getTodoTime, end)
            );

            trendList.add(new TodoTrendVO(targetDate.toString(), count));
        }

        return Result.success(trendList);
    }

    /**
     * Get contact gender distribution (status = 0)
     */
    @GetMapping("/contact-gender-distribution")
    public Result<List<ContactGenderDistributionVO>> getContactGenderDistribution() {
        String userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        List<ContactGenderDistributionVO> list = new ArrayList<>();

        Long unknownCount = contactMapper.selectCount(
                new LambdaQueryWrapper<Contact>()
                        .eq(Contact::getUserId, userId)
                        .eq(Contact::getStatus, 0)
                        .eq(Contact::getGender, 0)
        );
        Long maleCount = contactMapper.selectCount(
                new LambdaQueryWrapper<Contact>()
                        .eq(Contact::getUserId, userId)
                        .eq(Contact::getStatus, 0)
                        .eq(Contact::getGender, 1)
        );
        Long femaleCount = contactMapper.selectCount(
                new LambdaQueryWrapper<Contact>()
                        .eq(Contact::getUserId, userId)
                        .eq(Contact::getStatus, 0)
                        .eq(Contact::getGender, 2)
        );

        // Unicode escaped: "\u672a\u77e5" = "未知", "\u7537" = "男", "\u5973" = "女"
        list.add(new ContactGenderDistributionVO(0, "\u672a\u77e5", unknownCount));
        list.add(new ContactGenderDistributionVO(1, "\u7537", maleCount));
        list.add(new ContactGenderDistributionVO(2, "\u5973", femaleCount));

        return Result.success(list);
    }
}
