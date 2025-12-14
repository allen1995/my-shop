<template>
  <view class="detail-container">
    <view class="image-section">
      <image class="work-image" :src="work.imageUrl" mode="aspectFit" @click="previewImage"></image>
    </view>
    
    <view class="info-section">
      <text class="work-title">{{ work.title }}</text>
      <text class="work-description" v-if="work.description">{{ work.description }}</text>
      <text class="work-time">创建时间：{{ formatTime(work.createTime) }}</text>
    </view>
    
    <view class="action-section">
      <button class="action-btn apply-btn" @click="handleApply">应用到包包</button>
      <button class="action-btn delete-btn" @click="handleDelete">删除作品</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { workApi } from '@/api/work'
import { onLoad } from '@dcloudio/uni-app'

const work = ref({})
const workId = ref(null)

onLoad(async (options) => {
  workId.value = options.id
  await loadWorkDetail()
})

const loadWorkDetail = async () => {
  try {
    const res = await workApi.getWorkDetail(workId.value)
    if (res.code === 200 && res.data) {
      work.value = res.data
    }
  } catch (error) {
    console.error('加载作品详情失败', error)
  }
}

const previewImage = () => {
  uni.previewImage({
    urls: [work.value.imageUrl],
    current: work.value.imageUrl
  })
}

const handleApply = () => {
  uni.navigateTo({
    url: `/pages/preview/preview?workId=${workId.value}&imageUrl=${encodeURIComponent(work.value.imageUrl)}`
  })
}

const handleDelete = () => {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这个作品吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          const deleteRes = await workApi.deleteWork(workId.value)
          if (deleteRes.code === 200) {
            uni.showToast({
              title: '删除成功',
              icon: 'success'
            })
            
            setTimeout(() => {
              uni.navigateBack()
            }, 1500)
          }
        } catch (error) {
          uni.showToast({
            title: '删除失败',
            icon: 'none'
          })
        }
      }
    }
  })
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN')
}
</script>

<style lang="scss" scoped>
.detail-container {
  min-height: 100vh;
  background-color: #F8F8F8;
}

.image-section {
  background: #ffffff;
  padding: 40rpx;
  margin-bottom: 20rpx;
  
  .work-image {
    width: 100%;
    min-height: 600rpx;
    border-radius: 20rpx;
  }
}

.info-section {
  background: #ffffff;
  padding: 40rpx;
  margin-bottom: 20rpx;
  
  .work-title {
    display: block;
    font-size: 40rpx;
    font-weight: bold;
    color: #333333;
    margin-bottom: 20rpx;
  }
  
  .work-description {
    display: block;
    font-size: 28rpx;
    color: #666666;
    line-height: 1.6;
    margin-bottom: 20rpx;
  }
  
  .work-time {
    display: block;
    font-size: 24rpx;
    color: #999999;
  }
}

.action-section {
  padding: 30rpx 40rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  
  .action-btn {
    width: 100%;
    height: 88rpx;
    border-radius: 44rpx;
    font-size: 32rpx;
    font-weight: bold;
    border: none;
    
    &.apply-btn {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: #ffffff;
    }
    
    &.delete-btn {
      background: #FF3B30;
      color: #ffffff;
    }
  }
}
</style>


