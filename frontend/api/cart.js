import request from '@/utils/request'

export const cartApi = {
  // 添加到购物车
  addToCart(data) {
    return request({
      url: '/cart/items',
      method: 'POST',
      data
    })
  },
  
  // 获取购物车列表
  getCartItems() {
    return request({
      url: '/cart/items',
      method: 'GET'
    })
  },
  
  // 更新购物车项
  updateCartItem(itemId, data) {
    return request({
      url: `/cart/items/${itemId}`,
      method: 'PUT',
      data
    })
  },
  
  // 删除购物车项
  deleteCartItem(itemId) {
    return request({
      url: `/cart/items/${itemId}`,
      method: 'DELETE'
    })
  }
}


