import request from '../utils/axios'
import type { AxiosPromise } from 'axios'

/**
 * 上传相关API
 */
export const uploadApi = {
  /**
   * 上传商品图片
   * @param file 图片文件
   * @returns 上传结果，包含图片URL
   */
  uploadProductImage: (file: File): AxiosPromise<{ url: string }> => {
    const formData = new FormData()
    formData.append('file', file)
    
    return request({
      url: '/api/upload/product-image',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}
