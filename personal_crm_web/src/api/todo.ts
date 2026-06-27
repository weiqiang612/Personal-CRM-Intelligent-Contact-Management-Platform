import request from './request'
import type { TodoInfo, TodoParams, TodoListResult, TodoCreateParams } from '@/types/todo'

/**
 * 查询事项列表
 */
export function getTodos(params: TodoParams): Promise<TodoListResult> {
  return request.get('/todos', { params })
}

/**
 * 新增事项
 */
export function createTodo(data: TodoCreateParams): Promise<TodoInfo> {
  return request.post('/todos', data)
}

/**
 * 完成事项
 */
export function completeTodo(matterId: string): Promise<void> {
  return request.patch(`/todos/${matterId}/complete`)
}

/**
 * 取消事项
 */
export function cancelTodo(matterId: string): Promise<void> {
  return request.patch(`/todos/${matterId}/cancel`)
}

/**
 * 删除事项 (逻辑删除)
 */
export function deleteTodo(matterId: string): Promise<void> {
  return request.delete(`/todos/${matterId}`)
}
