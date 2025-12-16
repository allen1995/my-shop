import request, { BASE_URL } from '@/utils/request'

export const imageGenerationApi = {
  // 文生图
  textToImage(data) {
    return request({
      url: '/image-generation/text-to-image',
      method: 'POST',
      data
    })
  },
  
  // 上传图片
  uploadImage(filePath) {
    return new Promise((resolve, reject) => {
      const token = uni.getStorageSync('token')
      
      uni.uploadFile({
        url: BASE_URL + '/image-generation/upload',
        filePath: filePath,
        name: 'file',
        header: {
          'Authorization': token ? `Bearer ${token}` : ''
        },
        success: (res) => {
          try {
            const data = JSON.parse(res.data)
            if (data.code === 200) {
              resolve(data)
            } else {
              uni.showToast({
                title: data.message || '上传失败',
                icon: 'none'
              })
              reject(data)
            }
          } catch (e) {
            console.error('解析响应失败', e)
            reject(res)
          }
        },
        fail: (err) => {
          uni.showToast({
            title: '上传失败',
            icon: 'none'
          })
          reject(err)
        }
      })
    })
  },
  
  // 图生图
  imageToImage(data) {
    return request({
      url: '/image-generation/image-to-image',
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


