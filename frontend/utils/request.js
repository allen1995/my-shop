export const BASE_URL = 'http://localhost:8080/api' // 开发环境，生产环境需要修改

const request = (options) => {
  return new Promise((resolve, reject) => {
    // 获取token
    const token = uni.getStorageSync('token')
    
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      },
      success: (res) => {
        if (res.statusCode === 200) {
          // token过期或无效，自动跳转到登录页
          if (res.data.code === 401) {
            uni.removeStorageSync('token')
            uni.reLaunch({
              url: '/pages/login/login'
            })
            reject(res.data)
            return
          }
          // 返回数据，让业务层自行处理成功和失败
          resolve(res.data)
        } else {
          uni.showToast({
            title: '网络错误',
            icon: 'none'
          })
          reject(res)
        }
      },
      fail: (err) => {
        uni.showToast({
          title: '网络错误',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

export default request


