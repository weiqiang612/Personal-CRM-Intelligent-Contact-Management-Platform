import request from './request'

/**
 * 登录请求参数接口
 */
export interface LoginParams {
  username: string
  password: string
}

/**
 * 登录响应接口
 */
export interface LoginResult {
  token: string
  tokenType: string
  expiresIn: number
  user: {
    userId: string
    username: string
  }
}

/**
 * 当前用户个人信息接口
 */
export interface UserMeResult {
  userId: string
  username: string
  status: number
  avatarUrl: string | null
  email?: string | null
  phone?: string | null
}

/**
 * 登录 API
 */
export function loginApi(data: LoginParams): Promise<LoginResult> {
  return request.post('/auth/login', data)
}

/**
 * 获取当前登录用户信息 API
 */
export function getMeApi(): Promise<UserMeResult> {
  return request.get('/auth/me')
}

/**
 * 注册请求参数接口
 */
export interface RegisterParams {
  username: string
  password: string
}

/**
 * 注册 API
 */
export function registerApi(data: RegisterParams): Promise<void> {
  return request.post('/auth/register', data)
}

/**
 * 修改邮箱 API
 */
export function updateEmailApi(email: string): Promise<void> {
  return request.put('/auth/profile/email', { email })
}

/**
 * 修改手机 API
 */
export function updatePhoneApi(phone: string): Promise<void> {
  return request.put('/auth/profile/phone', { phone })
}

/**
 * 修改密码请求参数接口
 */
export interface UpdatePasswordParams {
  oldPassword: string
  newPassword: string
}

/**
 * 修改密码 API
 */
export function updatePasswordApi(data: UpdatePasswordParams): Promise<void> {
  return request.put('/auth/profile/password', data)
}
