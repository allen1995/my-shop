import request from '@/utils/request'

export const userApi = {
  // 获取用户信息
  getProfile() {
    return request({
      url: '/user/profile',
      method: 'GET'
    })
  },
  
  // 更新用户信息
  updateProfile(data) {
    return request({
      url: '/user/profile',
      method: 'PUT',
      data
    })
  },
  
  // 获取用户统计信息
  getStatistics() {
    return request({
      url: '/user/statistics',
      method: 'GET'
    })
  }
}

