import request from './request'
import type { ContactInfo } from './contact'
import type { TodoInfo } from '@/types/todo'

export interface AgentQueryParams {
  input: string
}

export interface AgentQueryResult {
  queryType: 'contact' | 'todo' | 'unsupported'
  intent: 'query_contact' | 'query_todo' | 'unsupported'
  summary: string
  results: any[] // 可以是 ContactInfo[] 或者是 TodoInfo[]，这里用 any[] 方便在组件中自适应类型推导与类型转换
}

/**
 * 自然语言智能查询
 */
export function queryAgent(data: AgentQueryParams): Promise<AgentQueryResult> {
  return request.post('/agent/query', data)
}
