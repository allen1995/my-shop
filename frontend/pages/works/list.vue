<template>
  <view class="works-container">
    <view class="works-grid" v-if="works.length > 0">
      <view 
        class="work-card" 
        v-for="work in works" 
        :key="work.id"
        @click="goToDetail(work.id)"
      >
        <image class="work-image" :src="work.imageUrl" mode="aspectFill"></image>
        <view class="work-info">
          <text class="work-title">{{ work.title }}</text>
          <text class="work-time">{{ formatTime(work.createTime) }}</text>
        </view>
      </view>
    </view>
    
    <view class="empty-state" v-else>
      <image class="empty-icon" src="/static/icons/empty.png" mode="aspectFit"></image>
      <text class="empty-text">还没有作品，快去生成吧~</text>
      <button class="empty-btn" @click="goToGenerate">去生成</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { workApi } from '@/api/work'
import { onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'

const works = ref([])
const page = ref(0)
const size = ref(20)
const loading = ref(false)
const hasMore = ref(true)

const loadWorks = async (reset = false) => {
  if (loading.value) return
  
  loading.value = true
  
  try {
    if (reset) {
      page.value = 0
      works.value = []
    }
    
    const res = await workApi.getWorks({
      page: page.value,
      size: size.value
    })
    
    if (res.code === 200 && res.data) {
      const newWorks = res.data.content || []
      works.value = reset ? newWorks : [...works.value, ...newWorks]
      hasMore.value = newWorks.length === size.value
      page.value++
    }
  } catch (error) {
    console.error('加载作品失败', error)
  } finally {
    loading.value = false
    uni.stopPullDownRefresh()
  }
}

const goToDetail = (workId) => {
  uni.navigateTo({
    url: `/pages/works/detail?id=${workId}`
  })
}

const goToGenerate = () => {
  uni.switchTab({
    url: '/pages/index/index'
  })
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return Math.floor(diff / 86400000) + '天前'
}

onMounted(() => {
  loadWorks(true)
})

onPullDownRefresh(() => {
  loadWorks(true)
})

onReachBottom(() => {
  if (hasMore.value) {
    loadWorks()
  }
})
</script>

<style lang="scss" scoped>
.works-container {
  padding: 20rpx;
  min-height: 100vh;
  background-color: #F8F8F8;
}

.works-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
  
  .work-card {
    background: #ffffff;
    border-radius: 20rpx;
    overflow: hidden;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
    
    .work-image {
      width: 100%;
      height: 300rpx;
    }
    
    .work-info {
      padding: 20rpx;
      
      .work-title {
        display: block;
        font-size: 28rpx;
        font-weight: bold;
        color: #333333;
        margin-bottom: 10rpx;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      
      .work-time {
        display: block;
        font-size: 24rpx;
        color: #999999;
      }
    }
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 40rpx;
  
  .empty-icon {
    width: 200rpx;
    height: 200rpx;
    margin-bottom: 40rpx;
    opacity: 0.5;
  }
  
  .empty-text {
    font-size: 32rpx;
    color: #999999;
    margin-bottom: 40rpx;
  }
  
  .empty-btn {
    padding: 20rpx 60rpx;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #ffffff;
    border-radius: 44rpx;
    font-size: 28rpx;
    border: none;
  }
}
</style>


