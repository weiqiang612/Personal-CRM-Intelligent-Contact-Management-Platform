import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { loginApi, getMeApi } from '@/api/auth'
import type { LoginParams } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const user = ref<{
    userId: string
    username: string
    avatarUrl: string | null
  } | null>(null)

  // 从 localStorage 恢复初始用户信息
  try {
    const savedUser = localStorage.getItem('user')
    if (savedUser) {
      user.value = JSON.parse(savedUser)
    }
  } catch (e) {
    console.error('Failed to parse user from localStorage', e)
  }

  const isLoggedIn = computed(() => !!token.value)

  /**
   * 用户登录行为
   */
  async function login(params: LoginParams) {
    const data = await loginApi(params)
    token.value = data.token
    user.value = {
      userId: data.user.userId,
      username: data.user.username,
      avatarUrl: null, // 登录时暂无头像信息，随后可通过 fetchUserProfile 补充
    }
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  /**
   * 获取当前用户个人信息详情
   */
  async function fetchUserProfile() {
    const data = await getMeApi()
    user.value = {
      userId: data.userId,
      username: data.username,
      avatarUrl: data.avatarUrl,
    }
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  /**
   * 安全登出行为，清空敏感数据
   */
  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return {
    token,
    user,
    isLoggedIn,
    login,
    fetchUserProfile,
    logout,
  }
})
