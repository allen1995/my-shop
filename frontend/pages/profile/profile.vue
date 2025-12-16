<template>
  <view class="profile-container">
    <view class="user-info-section">
      <image 
        class="avatar" 
        :src="getAvatarUrl(userInfo.avatarUrl)" 
        mode="aspectFill"
        @click="goToEditProfile"
        @error="handleAvatarError"
      ></image>
      <text class="nickname">{{ userInfo.nickName || '未设置昵称' }}</text>
      <text class="edit-btn" @click="goToEditProfile">编辑资料</text>
    </view>
    
    <view class="statistics-section">
      <view class="stat-item" @click="goToOrders('PENDING_PAYMENT')">
        <text class="stat-value">{{ statistics.orders?.pendingPayment || 0 }}</text>
        <text class="stat-label">待支付</text>
      </view>
      <view class="stat-item" @click="goToOrders('SHIPPED')">
        <text class="stat-value">{{ statistics.orders?.shipped || 0 }}</text>
        <text class="stat-label">待收货</text>
      </view>
      <view class="stat-item" @click="goToOrders('COMPLETED')">
        <text class="stat-value">{{ statistics.orders?.completed || 0 }}</text>
        <text class="stat-label">已完成</text>
      </view>
      <view class="stat-item" @click="goToWorks">
        <text class="stat-value">{{ statistics.works?.total || 0 }}</text>
        <text class="stat-label">我的作品</text>
      </view>
    </view>
    
    <view class="menu-section">
      <view class="menu-item" @click="goToOrders()">
        <text class="menu-icon">📦</text>
        <text class="menu-title">我的订单</text>
        <text class="menu-arrow">></text>
      </view>
      
      <view class="menu-item" @click="goToAddresses">
        <text class="menu-icon">📍</text>
        <text class="menu-title">收货地址</text>
        <text class="menu-arrow">></text>
      </view>
      
      <view class="menu-item" @click="goToWorks">
        <text class="menu-icon">🎨</text>
        <text class="menu-title">我的作品</text>
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
import { userApi } from '@/api/user'
import { onShow } from '@dcloudio/uni-app'

const userStore = useUserStore()
const userInfo = ref({})
const statistics = ref({
  orders: {},
  works: {}
})

const loadUserInfo = async () => {
  try {
    const res = await userApi.getProfile()
    if (res.code === 200 && res.data) {
      userInfo.value = res.data
      userStore.user = res.data
    }
  } catch (error) {
    console.error('加载用户信息失败', error)
  }
}

const loadStatistics = async () => {
  try {
    const res = await userApi.getStatistics()
    if (res.code === 200 && res.data) {
      statistics.value = res.data
    }
  } catch (error) {
    console.error('加载统计信息失败', error)
  }
}

onMounted(() => {
  loadUserInfo()
  loadStatistics()
})

onShow(() => {
  loadUserInfo()
  loadStatistics()
})

const goToEditProfile = () => {
  uni.navigateTo({
    url: '/pages/profile/edit'
  })
}

const goToOrders = (status) => {
  const url = status ? `/pages/order/list?status=${status}` : '/pages/order/list'
  uni.navigateTo({
    url: url
  })
}

const goToAddresses = () => {
  uni.navigateTo({
    url: '/pages/address/list'
  })
}

const goToWorks = () => {
  uni.switchTab({
    url: '/pages/works/list'
  })
}

const goToSettings = () => {
  uni.showToast({
    title: '设置功能开发中',
    icon: 'none'
  })
}

// 获取头像URL，过滤临时路径
const getAvatarUrl = (avatarUrl) => {
  if (!avatarUrl) {
    return '/static/default-avatar.png'
  }
  // 如果是临时路径（__tmp__），使用默认头像
  if (avatarUrl.includes('__tmp__') || avatarUrl.includes('127.0.0.1')) {
    return '/static/default-avatar.png'
  }
  return avatarUrl
}

// 头像加载错误处理
const handleAvatarError = (e) => {
  console.error('头像加载失败', e)
  // 如果头像加载失败，使用默认头像
  userInfo.value.avatarUrl = '/static/default-avatar.png'
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
  position: relative;
  
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
    margin-bottom: 20rpx;
  }
  
  .edit-btn {
    font-size: 26rpx;
    color: #ffffff;
    padding: 10rpx 30rpx;
    border: 1rpx solid rgba(255, 255, 255, 0.5);
    border-radius: 30rpx;
    background: rgba(255, 255, 255, 0.2);
  }
}

.statistics-section {
  background: #ffffff;
  margin: 20rpx;
  border-radius: 20rpx;
  padding: 40rpx 20rpx;
  display: flex;
  justify-content: space-around;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
  
  .stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .stat-value {
      font-size: 48rpx;
      font-weight: bold;
      color: #667eea;
      margin-bottom: 10rpx;
    }
    
    .stat-label {
      font-size: 24rpx;
      color: #666666;
    }
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


