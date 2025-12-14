<template>
  <view class="profile-container">
    <view class="user-info-section">
      <image class="avatar" :src="userInfo.avatarUrl || '/static/default-avatar.png'" mode="aspectFill"></image>
      <text class="nickname">{{ userInfo.nickName || '未设置昵称' }}</text>
    </view>
    
    <view class="menu-section">
      <view class="menu-item" @click="goToOrders">
        <text class="menu-icon">📦</text>
        <text class="menu-title">我的订单</text>
        <text class="menu-arrow">></text>
      </view>
      
      <view class="menu-item" @click="goToAddresses">
        <text class="menu-icon">📍</text>
        <text class="menu-title">收货地址</text>
        <text class="menu-arrow">></text>
      </view>
      
      <view class="menu-item" @click="goToSettings">
        <text class="menu-icon">⚙️</text>
        <text class="menu-title">设置</text>
        <text class="menu-arrow">></text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const userInfo = ref({})

onMounted(() => {
  userInfo.value = userStore.user || {}
})

const goToOrders = () => {
  uni.navigateTo({
    url: '/pages/order/list'
  })
}

const goToAddresses = () => {
  uni.navigateTo({
    url: '/pages/address/list'
  })
}

const goToSettings = () => {
  uni.showToast({
    title: '设置功能开发中',
    icon: 'none'
  })
}
</script>

<style lang="scss" scoped>
.profile-container {
  min-height: 100vh;
  background-color: #F8F8F8;
}

.user-info-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 80rpx 40rpx 60rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  
  .avatar {
    width: 160rpx;
    height: 160rpx;
    border-radius: 80rpx;
    border: 4rpx solid #ffffff;
    margin-bottom: 30rpx;
  }
  
  .nickname {
    font-size: 36rpx;
    font-weight: bold;
    color: #ffffff;
  }
}

.menu-section {
  margin-top: 20rpx;
  background: #ffffff;
  padding: 0 40rpx;
  
  .menu-item {
    display: flex;
    align-items: center;
    padding: 40rpx 0;
    border-bottom: 2rpx solid #F5F5F5;
    
    &:last-child {
      border-bottom: none;
    }
    
    .menu-icon {
      font-size: 40rpx;
      margin-right: 20rpx;
    }
    
    .menu-title {
      flex: 1;
      font-size: 32rpx;
      color: #333333;
    }
    
    .menu-arrow {
      font-size: 32rpx;
      color: #999999;
    }
  }
}
</style>


