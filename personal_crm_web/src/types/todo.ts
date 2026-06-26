/**
 * 事项信息接口定义
 */
export interface TodoInfo {
  matterId: string
  contactId: string
  contactName?: string
  todoTime: string // yyyy-MM-dd HH:mm:ss
  content: string
  priority: number // 0 普通, 1 重要, 2 紧急
  status: number // 0 待完成, 1 已取消, 2 已完成
  completedAt?: string | null
  cancelledAt?: string | null
  createdAt?: string
  overdue?: boolean // 是否逾期
}

/**
 * 事项列表查询参数接口
 */
export interface TodoParams {
  page?: number
  pageSize?: number
  contactId?: string
  status?: number // 0 待完成, 1 已取消, 2 已完成
  startTime?: string // yyyy-MM-dd HH:mm:ss
  endTime?: string // yyyy-MM-dd HH:mm:ss
  sortBy?: string // 排序字段：todoTime, createdAt, completedAt, cancelledAt
  sortOrder?: string // asc 或 desc
  keyword?: string // 模糊检索词
}

/**
 * 事项分页列表返回结果接口
 */
export interface TodoListResult {
  list: TodoInfo[]
  page: number
  pageSize: number
  total: number
}

/**
 * 创建事项参数接口
 */
export interface TodoCreateParams {
  contactId: string
  todoTime: string // yyyy-MM-dd HH:mm:ss
  content: string
  priority: number // 0 普通, 1 重要, 2 紧急
}
