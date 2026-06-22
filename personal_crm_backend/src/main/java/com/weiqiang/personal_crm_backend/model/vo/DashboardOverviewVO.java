package com.weiqiang.personal_crm_backend.model.vo;

import lombok.Data;

/**
 * Dashboard Overview Metrics VO
 */
@Data
public class DashboardOverviewVO {
    private Long contactCount;
    private Long blacklistCount;
    private Long pendingTodoCount;
    private Long todayTodoCount;
    private Long overdueTodoCount;
}
