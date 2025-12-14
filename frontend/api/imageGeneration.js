import request from '@/utils/request'

export const imageGenerationApi = {
  // 文生图
  textToImage(data) {
    return request({
      url: '/image-generation/text-to-image',
      method: 'POST',
      data
    })
  },
  
  // 查询生成任务状态
  getTaskStatus(taskId) {
    return request({
      url: `/image-generation/tasks/${taskId}`,
      method: 'GET'
    })
  }
}


