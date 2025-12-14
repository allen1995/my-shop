<template>
  <view class="generating-container">
    <view class="progress-section">
      <image class="loading-icon" src="/static/icons/loading.gif" mode="aspectFit"></image>
      <text class="progress-text">AI正在生成中...</text>
      <view class="progress-bar">
        <view class="progress-fill" :style="{ width: progress + '%' }"></view>
      </view>
      <text class="progress-desc">预计还需 {{ estimatedTime }} 秒</text>
    </view>
    
    <view class="tips-section">
      <text class="tips-title">生成提示</text>
      <text class="tips-content">• 生成时间通常需要20-30秒</text>
      <text class="tips-content">• 请保持网络连接畅通</text>
      <text class="tips-content">• 可以切换到其他页面，完成后会通知您</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { imageGenerationApi } from '@/api/imageGeneration'
import { onLoad } from '@dcloudio/uni-app'

const taskId = ref(null)
const progress = ref(0)
const estimatedTime = ref(30)
let pollTimer = null

onLoad((options) => {
  taskId.value = options.taskId
  startPolling()
})

onUnmounted(() => {
  if (pollTimer) {
    clearInterval(pollTimer)
  }
})

const startPolling = () => {
  pollTimer = setInterval(async () => {
    try {
      const res = await imageGenerationApi.getTaskStatus(taskId.value)
      if (res.code === 200 && res.data) {
        const task = res.data
        
        if (task.status === 'COMPLETED') {
          // 生成完成，跳转到结果页
          clearInterval(pollTimer)
          uni.redirectTo({
            url: `/pages/generate/result?taskId=${taskId.value}&imageUrl=${encodeURIComponent(task.resultUrl)}`
          })
        } else if (task.status === 'FAILED') {
          // 生成失败
          clearInterval(pollTimer)
          uni.showModal({
            title: '生成失败',
            content: task.errorMessage || '生成失败，请重试',
            showCancel: true,
            cancelText: '返回',
            confirmText: '重试',
            success: (modalRes) => {
              if (modalRes.confirm) {
                // 重试
                startPolling()
              } else {
                uni.navigateBack()
              }
            }
          })
        } else if (task.status === 'PROCESSING') {
          // 更新进度
          progress.value = Math.min(progress.value + 5, 90)
          estimatedTime.value = Math.max(estimatedTime.value - 1, 5)
        }
      }
    } catch (error) {
      console.error('查询任务状态失败', error)
    }
  }, 2000) // 每2秒轮询一次
}
</script>

<style lang="scss" scoped>
.generating-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
}

.progress-section {
  width: 100%;
  max-width: 600rpx;
  background: #ffffff;
  border-radius: 20rpx;
  padding: 60rpx 40rpx;
  text-align: center;
  
  .loading-icon {
    width: 120rpx;
    height: 120rpx;
    margin-bottom: 40rpx;
  }
  
  .progress-text {
    display: block;
    font-size: 36rpx;
    font-weight: bold;
    color: #333333;
    margin-bottom: 40rpx;
  }
  
  .progress-bar {
    width: 100%;
    height: 8rpx;
    background: #F5F5F5;
    border-radius: 4rpx;
    overflow: hidden;
    margin-bottom: 20rpx;
    
    .progress-fill {
      height: 100%;
      background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
      transition: width 0.3s;
    }
  }
  
  .progress-desc {
    display: block;
    font-size: 24rpx;
    color: #999999;
  }
}

.tips-section {
  margin-top: 60rpx;
  width: 100%;
  max-width: 600rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20rpx;
  padding: 40rpx;
  
  .tips-title {
    display: block;
    font-size: 32rpx;
    font-weight: bold;
    color: #ffffff;
    margin-bottom: 20rpx;
  }
  
  .tips-content {
    display: block;
    font-size: 28rpx;
    color: rgba(255, 255, 255, 0.9);
    line-height: 1.8;
    margin-bottom: 10rpx;
  }
}
</style>


