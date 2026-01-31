import request from '../utils/axios'
import type { AxiosPromise } from 'axios'

// 骑手申请表单
interface RiderApplication {
  id: number
  userId: number
  userName: string
  userPhone: string
  idCard: string
  vehicleType: string
  vehicleNumber: string
  status: string
  createdAt: string
  updatedAt: string
}

// 骑手信息
interface Rider {
  id: number
  username: string
  name: string
  phone: string
  isRider: boolean
  riderStatus: string
  createdAt: string
  updatedAt: string
}

// 新骑手表信息
interface NewRider {
  id: number
  userId: number
  realName: string
  idCard: string
  phone: string
  status: string
  balance: number
  rating: number
  lastLoginAt: string
  createdAt: string
  updatedAt: string
}

// 审核骑手申请请求
interface ReviewRiderApplicationRequest {
  status: 'approved' | 'rejected'
}

/**
 * 骑手相关API
 */
export const riderApi = {
  /**
   * 获取骑手申请表单列表
   * @param status 状态（可选）
   * @returns 申请表单列表
   */
  getRiderApplications: (status?: string): AxiosPromise<RiderApplication[]> => {
    return request({
      url: '/api/admin/riders/applications',
      method: 'get',
      params: { status }
    })
  },

  /**
   * 获取单个骑手申请表单详情
   * @param id 申请表单ID
   * @returns 申请表单详情
   */
  getRiderApplicationById: (id: number): AxiosPromise<RiderApplication> => {
    return request({
      url: `/api/admin/riders/applications/${id}`,
      method: 'get'
    })
  },

  /**
   * 审核骑手申请
   * @param id 申请表单ID
   * @param data 审核信息
   * @returns 审核结果
   */
  reviewRiderApplication: (id: number, data: ReviewRiderApplicationRequest): AxiosPromise<void> => {
    return request({
      url: `/api/admin/riders/applications/${id}/review`,
      method: 'put',
      data
    })
  },

  /**
   * 获取骑手列表
   * @returns 骑手列表
   */
  getRiders: (): AxiosPromise<Rider[]> => {
    return request({
      url: '/api/admin/riders',
      method: 'get'
    })
  },

  /**
   * 获取骑手详情
   * @param id 骑手ID
   * @returns 骑手详情
   */
  getRiderById: (id: number): AxiosPromise<Rider> => {
    return request({
      url: `/api/admin/riders/${id}`,
      method: 'get'
    })
  },

  /**
   * 获取新骑手表列表
   * @returns 骑手列表
   */
  getNewRiders: (): AxiosPromise<NewRider[]> => {
    return request({
      url: '/api/admin/riders/new',
      method: 'get'
    })
  }
}