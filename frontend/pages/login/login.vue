<template>
  <view class="login-container">
    <view class="logo-section">
      <image class="logo" src="/static/logo.png" mode="aspectFit"></image>
      <text class="title">AI印花电商</text>
      <text class="subtitle">让AI帮你设计独特的包包</text>
    </view>
    
    <view class="login-section">
      <button class="login-btn" @click="handleWeChatLogin" :loading="loading">
        微信一键登录
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/store/user'
import { onLoad } from '@dcloudio/uni-app'

const userStore = useUserStore()
const loading = ref(false)

const handleWeChatLogin = async () => {
  loading.value = true
  try {
    // 获取微信登录code
    const loginRes = await new Promise((resolve, reject) => {
      uni.login({
        provider: 'weixin',
        success: resolve,
        fail: reject
      })
    })
    
    if (loginRes.code) {
      // 调用后端登录接口
      const success = await userStore.login(loginRes.code)
      
      if (success) {
        uni.showToast({
          title: '登录成功',
          icon: 'success'
        })
        
        // 跳转到首页
        setTimeout(() => {
          uni.switchTab({
            url: '/pages/index/index'
          })
        }, 1500)
      } else {
        uni.showToast({
          title: '登录失败，请重试',
          icon: 'none'
        })
      }
    }
  } catch (error) {
    console.error('登录错误', error)
    uni.showToast({
      title: '登录失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

onLoad(() => {
  // 如果已登录，直接跳转
  if (userStore.token) {
    uni.switchTab({
      url: '/pages/index/index'
    })
  }
})
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 100rpx;
  
  .logo {
    width: 200rpx;
    height: 200rpx;
    margin-bottom: 40rpx;
  }
  
  .title {
    font-size: 48rpx;
    font-weight: bold;
    color: #ffffff;
    margin-bottom: 20rpx;
  }
  
  .subtitle {
    font-size: 28rpx;
    color: rgba(255, 255, 255, 0.8);
  }
}

.login-section {
  width: 100%;
  
  .login-btn {
    width: 100%;
    height: 88rpx;
    background-color: #ffffff;
    color: #667eea;
    border-radius: 44rpx;
    font-size: 32rpx;
    font-weight: bold;
    border: none;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style>


