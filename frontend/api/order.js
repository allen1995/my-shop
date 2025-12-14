import request from '@/utils/request'

export const orderApi = {
  // 创建订单
  createOrder(data) {
    return request({
      url: '/orders',
      method: 'POST',
      data
    })
  },
  
  // 获取订单列表
  getOrders(params) {
    return request({
      url: '/orders',
      method: 'GET',
      data: params
    })
  },
  
  // 获取订单详情
  getOrderDetail(orderId) {
    return request({
      url: `/orders/${orderId}`,
      method: 'GET'
    })
  },
  
  // 创建支付订单
  createPayment(orderId) {
    return request({
      url: `/orders/${orderId}/payment`,
      method: 'POST'
    })
  }
}


