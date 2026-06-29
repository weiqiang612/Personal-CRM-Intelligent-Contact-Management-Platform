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

/**
 * 邮箱验证码发送请求参数接口
 */
export interface SendEmailCodeParams {
  email: string
  purpose: 'REGISTER' | 'RESET_PASSWORD' | 'CHANGE_EMAIL'
}

/**
 * 发送邮箱验证码 API
 */
export function sendEmailCode(data: SendEmailCodeParams): Promise<void> {
  return request.post('/auth/email-code/send', data)
}

/**
 * 邮箱验证码校验请求参数接口
 */
export interface VerifyEmailCodeParams {
  email: string
  code: string
  purpose: 'REGISTER' | 'RESET_PASSWORD' | 'CHANGE_EMAIL'
}

/**
 * 邮箱校验与激活响应接口
 */
export interface VerifyEmailResult {
  token: string
  userId: string
  username: string
  email: string
}

/**
 * 校验邮箱验证码与激活 API
 */
export function verifyEmailCode(data: VerifyEmailCodeParams): Promise<VerifyEmailResult> {
  return request.post('/auth/email-code/verify', data)
}

/**
 * 重置密码请求参数接口
 */
export interface ResetPasswordParams {
  email: string
  code: string
  newPassword: string
}

/**
 * 忘记密码重置 API
 */
export function resetPassword(data: ResetPasswordParams): Promise<void> {
  return request.post('/auth/password/reset', data)
}

/**
 * 修改绑定邮箱请求参数接口
 */
export interface ChangeEmailParams {
  newEmail: string
  code: string
}

/**
 * 登录状态下修改绑定邮箱 API
 */
export function changeEmail(data: ChangeEmailParams): Promise<void> {
  return request.post('/user/email/change', data)
}

