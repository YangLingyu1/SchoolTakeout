import request from '../utils/axios'
import type { AxiosPromise } from 'axios'

// 分类信息
interface Category {
  id: number
  name: string
  description: string
  createdAt?: string
  updatedAt?: string
}

/**
 * 分类相关API
 */
export const categoryApi = {
  /**
   * 获取分类列表
   * @returns 分类列表
   */
  getCategories: (): AxiosPromise<Category[]> => {
    return request({
      url: '/api/admin/categories',
      method: 'get'
    })
  },

  /**
   * 获取分类详情
   * @param id 分类ID
   * @returns 分类详情
   */
  getCategoryById: (id: number): AxiosPromise<Category> => {
    return request({
      url: `/api/admin/categories/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建分类
   * @param data 分类信息
   * @returns 创建的分类
   */
  createCategory: (data: Omit<Category, 'id'>): AxiosPromise<Category> => {
    return request({
      url: '/api/admin/categories',
      method: 'post',
      data
    })
  },

  /**
   * 更新分类
   * @param id 分类ID
   * @param data 分类信息
   * @returns 更新后的分类
   */
  updateCategory: (id: number, data: Omit<Category, 'id'>): AxiosPromise<Category> => {
    return request({
      url: `/api/admin/categories/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除分类
   * @param id 分类ID
   * @returns 删除结果
   */
  deleteCategory: (id: number): AxiosPromise<void> => {
    return request({
      url: `/api/admin/categories/${id}`,
      method: 'delete'
    })
  }
}