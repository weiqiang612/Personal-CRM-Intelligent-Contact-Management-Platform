import request from './request'
import type { ContactInfo } from './contact'
import type { TodoInfo } from '@/types/todo'

export type AgentResultItem = ContactInfo | TodoInfo

export interface AgentQueryParams {
  input: string
  sessionId?: string
}

export interface AgentQueryResult {
  queryType: 'contact' | 'todo' | 'unsupported'
  intent: string
  summary: string
  results: AgentResultItem[]
  sessionId?: string
  isClarifying?: boolean
  missingFields?: string[]
  parsedParams?: {
    contactId?: string
    contactName?: string
    todoTime?: string
    content?: string
    priority?: number
  }
}

/**
 * 自然语言智能查询
 */
export function queryAgent(data: AgentQueryParams): Promise<AgentQueryResult> {
  return request.post('/agent/query', data)
}

export interface AgentExecuteParams {
  input: string
  sessionId?: string
}

export interface AgentExecuteResult {
  logId?: number
  needConfirm: number // 0 | 1
  actionType: string // 'create_todo' | 'unsupported' 等
  summary: string
  sessionId?: string
  isClarifying?: boolean
  missingFields?: string[]
  results?: AgentResultItem[]
  parsedParams?: {
    contactId?: string
    contactName?: string
    todoTime?: string
    content?: string
    priority?: number
  }
}

export interface AgentConfirmParams {
  logId: number
  action: 'confirm' | 'cancel'
}

export type AgentConfirmResult = TodoInfo | null

/**
 * Agent 写操作预处理接口 (意图解析与预确认)
 */
export function executeAgent(data: AgentExecuteParams): Promise<AgentExecuteResult> {
  return request.post('/agent/execute', data)
}

/**
 * Agent 写操作二次确认接口 (执行或取消)
 */
export function confirmAgent(data: AgentConfirmParams): Promise<AgentConfirmResult> {
  return request.post('/agent/confirm', data)
}
