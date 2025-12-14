import { defineStore } from 'pinia'
import { authApi } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null,
    token: uni.getStorageSync('token') || null
  }),
  
  actions: {
    async login(code) {
      try {
        const res = await authApi.login(code)
        if (res.code === 200) {
          this.token = res.data.token
          this.user = res.data.user
          uni.setStorageSync('token', this.token)
          return true
        }
        return false
      } catch (error) {
        console.error('登录失败', error)
        return false
      }
    },
    
    logout() {
      this.user = null
      this.token = null
      uni.removeStorageSync('token')
    }
  }
})


