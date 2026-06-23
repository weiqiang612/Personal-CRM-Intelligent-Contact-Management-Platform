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
    localStorage.setItem('token', data.token)
    // 立即拉取一次完整的用户信息（包含头像）
    await fetchUserProfile()
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
