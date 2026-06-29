import axios from 'axios'
import type { InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { STORAGE_KEYS } from '@/utils/constants'

// 全局重定向防抖标记
let isRedirecting = false

// 创建 Axios 实例
const request = axios.create({
  baseURL: '/api/v1', // 因为我们使用了 Vite 开发服务器代理，这里用相对路径前缀以支持跨域代理
  timeout: 10000,
})

// 请求拦截器
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem(STORAGE_KEYS.TOKEN)
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data

    // 兼容可能直接返回非包装对象的情况 (通常后端会统一包装)
    if (res.code === undefined) {
      return res
    }

    // 0 为成功响应码
    if (res.code === 0) {
      return res.data
    }

    // 40101 代表未登录或登录态失效
    if (res.code === 40101) {
      localStorage.removeItem(STORAGE_KEYS.TOKEN)
      localStorage.removeItem(STORAGE_KEYS.USER)
      if (!isRedirecting && window.location.pathname !== '/login') {
        isRedirecting = true
        ElMessage.error(res.message || '登录失效，请重新登录')
        setTimeout(() => {
          window.location.href = '/login'
        }, 1000)
      }
      return Promise.reject(new Error(res.message || '未授权'))
    }

    // 其余业务异常处理
    ElMessage.error(res.message || '系统繁忙，请稍后再试')
    return Promise.reject(new Error(res.message || 'Error'))
  },
  (error) => {
    let msg = error.message
    if (error.response && error.response.status === 401) {
      localStorage.removeItem(STORAGE_KEYS.TOKEN)
      localStorage.removeItem(STORAGE_KEYS.USER)
      if (!isRedirecting && window.location.pathname !== '/login') {
        isRedirecting = true
        ElMessage.error('登录失效，请重新登录')
        setTimeout(() => {
          window.location.href = '/login'
        }, 1000)
      }
      msg = '登录失效，请重新登录'
      return Promise.reject(error)
    }
    
    // 只有在非 401 的情况下，才抛出常规网络通信异常的飘窗，防重叠
    ElMessage.error(msg || '网络通信异常')
    return Promise.reject(error)
  }
)

export default request
