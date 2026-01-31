import request from '../utils/axios'
import type { AxiosPromise } from 'axios'

// 登录请求参数
interface LoginRequest {
  username: string
  password: string
}

// 登录响应数据
interface LoginResponse {
  token: string
  admin: {
    id: number
    username: string
    name: string
    role: string
  }
}

// 管理员信息
interface AdminInfo {
  id: number
  username: string
  name: string
  role: string
}

/**
 * 认证相关API
 */
export const authApi = {
  /**
   * 管理员登录
   * @param data 登录参数
   * @returns 登录结果
   */
  login: (data: LoginRequest): AxiosPromise<LoginResponse> => {
    return request({
      url: '/api/admin/auth/login',
      method: 'post',
      data
    })
  },

  /**
   * 获取管理员信息
   * @returns 管理员信息
   */
  getAdminInfo: (): AxiosPromise<AdminInfo> => {
    return request({
      url: '/api/admin/auth/info',
      method: 'get'
    })
  },

  /**
   * 管理员登出
   * @returns 登出结果
   */
  logout: (): AxiosPromise<void> => {
    return request({
      url: '/api/admin/auth/logout',
      method: 'post'
    })
  }
}