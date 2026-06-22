import request from './request'

export interface TagInfo {
  tagId: number
  name: string
  color: string
  createdAt?: string
}

export interface TagSaveParams {
  name: string
  color?: string
}

/**
 * 获取标签列表
 */
export function getTagsApi(): Promise<TagInfo[]> {
  return request.get('/tags')
}

/**
 * 新增标签
 */
export function createTagApi(data: TagSaveParams): Promise<TagInfo> {
  return request.post('/tags', data)
}

/**
 * 修改标签
 */
export function updateTagApi(tagId: number, data: TagSaveParams): Promise<TagInfo> {
  return request.put(`/tags/${tagId}`, data)
}

/**
 * 删除标签
 */
export function deleteTagApi(tagId: number): Promise<void> {
  return request.delete(`/tags/${tagId}`)
}
