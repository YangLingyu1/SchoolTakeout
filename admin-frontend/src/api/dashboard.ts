import request from '../utils/axios'
import type { AxiosPromise } from 'axios'

// Dashboard数据
interface DashboardData {
  userCount: number
  productCount: number
  orderCount: number
  riderCount: number
  pendingRiderApplications: number
  pendingOrders: number
}

/**
 * Dashboard相关API
 */
export const dashboardApi = {
  /**
   * 获取仪表盘数据
   * @returns 仪表盘数据
   */
  getDashboardData: (): AxiosPromise<DashboardData> => {
    return request({
      url: '/api/admin/dashboard',
      method: 'get'
    })
  }
}