import request from '@/utils/request'

export const authApi = {
  // 微信登录
  login(code) {
    return request({
      url: '/auth/wechat/login',
      method: 'POST',
      data: { code }
    })
  }
}


