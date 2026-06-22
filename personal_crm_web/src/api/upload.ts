import request from './request'

export interface UploadResult {
  fileName: string
  accessUrl: string
}

/**
 * 上传联系人头像
 * @param file 头像文件
 * @param contactId 联系人业务编号
 */
export function uploadContactAvatar(file: File, contactId: string): Promise<UploadResult> {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('contactId', contactId)
  return request.post('/uploads/contact-avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传当前用户头像
 * @param file 头像文件
 */
export function uploadUserAvatar(file: File): Promise<UploadResult> {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/uploads/user-avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
