import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '../api/auth'

// 管理员信息接口
interface Admin {
  id: number
  username: string
  name: string
  role: string
}

/**
 * 认证状态管理
 */
export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<Admin | null>(null)
  const loading = ref<boolean>(false)
  const error = ref<string | null>(null)

  // 计算属性
  const isAuthenticated = computed(() => !!token.value)

  // 登录
  const login = async (username: string, password: string) => {
    loading.value = true
    error.value = null

    try {
      const response = await authApi.login({ username, password }) as any
      
      // 存储token
      token.value = response.token
      localStorage.setItem('token', response.token)
      
      // 存储用户信息
      user.value = response.admin
      
      return true
    } catch (err: any) {
      error.value = err.response?.data?.message || '登录失败'
      return false
    } finally {
      loading.value = false
    }
  }

  // 获取用户信息
  const getUserInfo = async () => {
    if (!token.value) return

    loading.value = true
    error.value = null

    try {
      const response = await authApi.getAdminInfo()
      user.value = response.data
    } catch (err: any) {
      error.value = err.response?.data?.message || '获取用户信息失败'
      // 如果获取失败，清除token
      logout()
    } finally {
      loading.value = false
    }
  }

  // 登出
  const logout = () => {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
  }

  return {
    token,
    user,
    loading,
    error,
    isAuthenticated,
    login,
    getUserInfo,
    logout
  }
})