import request from './request'

export interface DashboardOverview {
  contactCount: number
  blacklistCount: number
  pendingTodoCount: number
  todayTodoCount: number
  overdueTodoCount: number
}

export interface TodoTrendItem {
  date: string
  count: number
}

export interface ContactGenderDistributionItem {
  gender: number
  name: string
  count: number
}

/**
 * 获取看板概览数据
 */
export function getDashboardOverview(): Promise<DashboardOverview> {
  return request.get('/dashboard/overview')
}

/**
 * 获取未来事项趋势图数据
 * @param days 天数，默认 7 天
 */
export function getTodoTrend(days: number = 7): Promise<TodoTrendItem[]> {
  return request.get('/dashboard/todo-trend', { params: { days } })
}

/**
 * 获取联系人性别分布数据
 */
export function getContactGenderDistribution(): Promise<ContactGenderDistributionItem[]> {
  return request.get('/dashboard/contact-gender-distribution')
}
