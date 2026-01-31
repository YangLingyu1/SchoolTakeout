import request from '../utils/axios'
import type { AxiosPromise } from 'axios'

// 商品信息
export interface Product {
  id: number
  name: string
  description: string
  price: number
  stock: number
  categoryId: number
  categoryName?: string
  imageUrl?: string
  createdAt?: string
  updatedAt?: string
}

/**
 * 商品相关API
 */
export const productApi = {
  /**
   * 获取商品列表
   * @param categoryId 分类ID（可选）
   * @returns 商品列表
   */
  getProducts: (categoryId?: number): AxiosPromise<Product[]> => {
    return request({
      url: '/api/admin/products',
      method: 'get',
      params: { categoryId }
    })
  },

  /**
   * 获取商品详情
   * @param id 商品ID
   * @returns 商品详情
   */
  getProductById: (id: number): AxiosPromise<Product> => {
    return request({
      url: `/api/admin/products/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建商品
   * @param data 商品信息
   * @returns 创建的商品
   */
  createProduct: (data: Omit<Product, 'id'>): AxiosPromise<Product> => {
    return request({
      url: '/api/admin/products',
      method: 'post',
      data
    })
  },

  /**
   * 更新商品
   * @param id 商品ID
   * @param data 商品信息
   * @returns 更新后的商品
   */
  updateProduct: (id: number, data: Omit<Product, 'id'>): AxiosPromise<Product> => {
    return request({
      url: `/api/admin/products/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除商品
   * @param id 商品ID
   * @returns 删除结果
   */
  deleteProduct: (id: number): AxiosPromise<void> => {
    return request({
      url: `/api/admin/products/${id}`,
      method: 'delete'
    })
  }
}