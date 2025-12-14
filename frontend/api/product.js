import request from '@/utils/request'

export const productApi = {
  // 获取商品列表
  getProducts() {
    return request({
      url: '/products',
      method: 'GET'
    })
  },
  
  // 获取商品详情
  getProductDetail(productId) {
    return request({
      url: `/products/${productId}`,
      method: 'GET'
    })
  }
}


