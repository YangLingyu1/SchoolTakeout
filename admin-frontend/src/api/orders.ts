import request from '../utils/axios'
import type { AxiosPromise } from 'axios'

// 订单信息
interface Order {
  id: number
  userId: number
  userName: string
  userPhone: string
  address: string
  totalAmount: number
  status: number
  statusText?: string
  riderId?: number
  riderName?: string
  createdAt: string
  updatedAt: string
}

// 订单项
interface OrderItem {
  id: number
  orderId: number
  productId: number
  productName: string
  price: number
  quantity: number
  imageUrl?: string
}

// 订单详情
interface OrderDetail {
  order: Order
  items: OrderItem[]
}

// 更新订单状态请求
interface UpdateOrderStatusRequest {
  status: number
}

// 分配骑手请求
interface AssignRiderRequest {
  riderId: number
}

/**
 * 订单相关API
 */
export const orderApi = {
  /**
   * 获取订单列表
   * @param status 订单状态（可选）
   * @returns 订单列表
   */
  getOrders: (status?: number): AxiosPromise<Order[]> => {
    return request({
      url: '/api/admin/orders',
      method: 'get',
      params: { status }
    })
  },

  /**
   * 获取订单详情
   * @param id 订单ID
   * @returns 订单详情
   */
  getOrderById: (id: number): AxiosPromise<OrderDetail> => {
    return request({
      url: `/api/admin/orders/${id}`,
      method: 'get'
    })
  },

  /**
   * 更新订单状态
   * @param id 订单ID
   * @param data 状态信息
   * @returns 更新结果
   */
  updateOrderStatus: (id: number, data: UpdateOrderStatusRequest): AxiosPromise<void> => {
    return request({
      url: `/api/admin/orders/${id}/status`,
      method: 'put',
      data
    })
  },

  /**
   * 分配骑手
   * @param id 订单ID
   * @param data 骑手信息
   * @returns 分配结果
   */
  assignRider: (id: number, data: AssignRiderRequest): AxiosPromise<void> => {
    return request({
      url: `/api/admin/orders/${id}/assign-rider`,
      method: 'put',
      data
    })
  }
}