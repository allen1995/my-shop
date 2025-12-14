import request from '@/utils/request'

export const workApi = {
  // 保存作品
  saveWork(data) {
    return request({
      url: '/works',
      method: 'POST',
      data
    })
  },
  
  // 获取作品列表
  getWorks(params) {
    return request({
      url: '/works',
      method: 'GET',
      data: params
    })
  },
  
  // 获取作品详情
  getWorkDetail(workId) {
    return request({
      url: `/works/${workId}`,
      method: 'GET'
    })
  },
  
  // 删除作品
  deleteWork(workId) {
    return request({
      url: `/works/${workId}`,
      method: 'DELETE'
    })
  }
}


