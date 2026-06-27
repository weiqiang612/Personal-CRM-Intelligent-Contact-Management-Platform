import request from './request'

/**
 * 联系人信息接口定义
 */
export interface ContactInfo {
  contactId: string
  name: string
  address: string | null
  postcode: string | null
  qq: string | null
  wechat: string | null
  email: string | null
  gender: number | null // 0 未知, 1 男, 2 女
  birthday: string | null // yyyy-MM-dd
  phone: string
  remarks: string | null
  status: number // 0 正常, 1 黑名单
  avatarUrl: string | null
  tags?: string[]
  createdAt: string // yyyy-MM-dd HH:mm:ss
}

/**
 * 联系人列表查询参数接口
 */
export interface ContactParams {
  page?: number
  pageSize?: number
  keyword?: string
  gender?: number | string
  tag?: string
  status?: number
  sortBy?: string // createdAt 或 birthday
  sortOrder?: string // asc 或 desc
}

/**
 * 联系人分页列表返回结果接口
 */
export interface ContactListResult {
  list: ContactInfo[]
  page: number
  pageSize: number
  total: number
}

/**
 * 联系人保存参数接口 (新建与修改共享)
 */
export interface ContactSaveParams {
  name: string
  address?: string | null
  postcode?: string | null
  qq?: string | null
  wechat?: string | null
  email?: string | null
  gender?: number | null
  birthday?: string | null // yyyy-MM-dd
  phone: string
  remarks?: string | null
  tagIds?: number[]
}

/**
 * 查询联系人列表
 */
export function getContactsApi(params: ContactParams): Promise<ContactListResult> {
  return request.get('/contacts', { params })
}

/**
 * 获取联系人详情
 */
export function getContactDetailApi(contactId: string): Promise<ContactInfo> {
  return request.get(`/contacts/${contactId}`)
}

/**
 * 新建联系人
 */
export function createContactApi(data: ContactSaveParams): Promise<ContactInfo> {
  return request.post('/contacts', data)
}

/**
 * 修改联系人
 */
export function updateContactApi(contactId: string, data: ContactSaveParams): Promise<ContactInfo> {
  return request.put(`/contacts/${contactId}`, data)
}

/**
 * 加入黑名单 (拉黑)
 */
export function addToBlacklistApi(contactId: string): Promise<void> {
  return request.patch(`/contacts/${contactId}/blacklist`, { status: 1 })
}

/**
 * 从黑名单恢复 (移出黑名单)
 */
export function restoreFromBlacklistApi(contactId: string): Promise<void> {
  return request.patch(`/contacts/${contactId}/restore`, { status: 0 })
}

/**
 * 删除联系人 (逻辑删除)
 */
export function deleteContactApi(contactId: string): Promise<void> {
  return request.delete(`/contacts/${contactId}`)
}
