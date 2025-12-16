import request from '@/utils/request'

export const addressApi = {
  // 获取地址列表
  getAddresses() {
    return request({
      url: '/addresses',
      method: 'GET'
    })
  },
  
  // 获取地址详情
  getAddress(addressId) {
    return request({
      url: `/addresses/${addressId}`,
      method: 'GET'
    })
  },
  
  // 创建地址
  createAddress(data) {
    return request({
      url: '/addresses',
      method: 'POST',
      data
    })
  },
  
  // 更新地址
  updateAddress(addressId, data) {
    return request({
      url: `/addresses/${addressId}`,
      method: 'PUT',
      data
    })
  },
  
  // 删除地址
  deleteAddress(addressId) {
    return request({
      url: `/addresses/${addressId}`,
      method: 'DELETE'
    })
  },
  
  // 设置默认地址
  setDefaultAddress(addressId) {
    return request({
      url: `/addresses/${addressId}/default`,
      method: 'PUT'
    })
  }
}

