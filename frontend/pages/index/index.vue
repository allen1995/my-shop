<template>
  <view class="index-container">
    <view class="banner-section">
      <text class="banner-title">AI设计，独一无二</text>
      <text class="banner-subtitle">用AI创造你的专属包包</text>
    </view>
    
    <view class="action-section">
      <view class="action-card" @click="goToTextToImage">
        <image class="action-icon" src="/static/icons/text-to-image.png" mode="aspectFit"></image>
        <text class="action-title">文生图</text>
        <text class="action-desc">输入描述，AI生成图片</text>
      </view>
      
      <view class="action-card" @click="goToImageToImage">
        <image class="action-icon" src="/static/icons/image-to-image.png" mode="aspectFit"></image>
        <text class="action-title">图生图</text>
        <text class="action-desc">上传图片，AI变换风格</text>
      </view>
    </view>
    
    <view class="works-section">
      <view class="section-header">
        <text class="section-title">热门作品</text>
        <text class="section-more" @click="goToWorks">查看更多 ></text>
      </view>
      <view class="works-grid">
        <view class="work-card" v-for="work in hotWorks" :key="work.id" @click="goToWorkDetail(work.id)">
          <image class="work-image" :src="work.imageUrl" mode="aspectFill"></image>
          <text class="work-title">{{ work.title }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { workApi } from '@/api/work'

const hotWorks = ref([])

const goToTextToImage = () => {
  uni.navigateTo({
    url: '/pages/generate/text-to-image'
  })
}

const goToImageToImage = () => {
  uni.navigateTo({
    url: '/pages/generate/image-to-image'
  })
}

const goToWorks = () => {
  uni.switchTab({
    url: '/pages/works/list'
  })
}

const goToWorkDetail = (workId) => {
  uni.navigateTo({
    url: `/pages/works/detail?id=${workId}`
  })
}

const loadHotWorks = async () => {
  try {
    const res = await workApi.getWorks({ page: 0, size: 6 })
    if (res.code === 200) {
      hotWorks.value = res.data.content || []
      console.log('热门作品已刷新:', hotWorks.value.length)
    }
  } catch (error) {
    console.error('加载热门作品失败', error)
  }
}

// 使用 onShow 确保每次显示页面时都刷新数据
onShow(() => {
  console.log('主页显示，刷新热门作品')
  loadHotWorks()
})
</script>

<style lang="scss" scoped>
.index-container {
  padding: 20rpx;
}

.banner-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20rpx;
  padding: 60rpx 40rpx;
  margin-bottom: 40rpx;
  text-align: center;
  
  .banner-title {
    display: block;
    font-size: 48rpx;
    font-weight: bold;
    color: #ffffff;
    margin-bottom: 20rpx;
  }
  
  .banner-subtitle {
    display: block;
    font-size: 28rpx;
    color: rgba(255, 255, 255, 0.9);
  }
}

.action-section {
  display: flex;
  gap: 20rpx;
  margin-bottom: 40rpx;
  
  .action-card {
    flex: 1;
    background: #ffffff;
    border-radius: 20rpx;
    padding: 40rpx 20rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
    
    .action-icon {
      width: 80rpx;
      height: 80rpx;
      margin-bottom: 20rpx;
    }
    
    .action-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333333;
      margin-bottom: 10rpx;
    }
    
    .action-desc {
      font-size: 24rpx;
      color: #999999;
    }
  }
}

.works-section {
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
    
    .section-title {
      font-size: 36rpx;
      font-weight: bold;
      color: #333333;
    }
    
    .section-more {
      font-size: 28rpx;
      color: #999999;
    }
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
      
      .work-title {
        display: block;
        padding: 20rpx;
        font-size: 28rpx;
        color: #333333;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}
</style>


