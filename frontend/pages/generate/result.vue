<template>
  <view class="result-container">
    <view class="image-section">
      <image class="result-image" :src="imageUrl" mode="aspectFit" @click="previewImage"></image>
    </view>
    
    <view class="action-section">
      <button class="action-btn save-btn" @click="handleSave">保存到作品集</button>
      <button class="action-btn apply-btn" @click="handleApply">应用到包包</button>
      <button class="action-btn regenerate-btn" @click="handleRegenerate">重新生成</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { workApi } from '@/api/work'
import { onLoad } from '@dcloudio/uni-app'

const imageUrl = ref('')
const taskId = ref(null)

onLoad((options) => {
  imageUrl.value = decodeURIComponent(options.imageUrl || '')
  taskId.value = options.taskId
})

const previewImage = () => {
  uni.previewImage({
    urls: [imageUrl.value],
    current: imageUrl.value
  })
}

const handleSave = async () => {
  uni.showModal({
    title: '保存作品',
    editable: true,
    placeholderText: '请输入作品标题',
    success: async (res) => {
      if (res.confirm) {
        try {
          const saveRes = await workApi.saveWork({
            title: res.content || '未命名作品',
            imageUrl: imageUrl.value
          })
          
          if (saveRes.code === 200) {
            uni.showToast({
              title: '保存成功',
              icon: 'success'
            })
            
            setTimeout(() => {
              uni.switchTab({
                url: '/pages/works/list'
              })
            }, 1500)
          }
        } catch (error) {
          uni.showToast({
            title: '保存失败',
            icon: 'none'
          })
        }
      }
    }
  })
}

const handleApply = async () => {
  // 先保存作品（如果还没保存），获取workId
  // 然后跳转到预览页
  uni.showModal({
    title: '应用到包包',
    content: '需要先保存作品才能应用到包包，是否现在保存？',
    success: async (res) => {
      if (res.confirm) {
        // 先保存作品
        uni.showModal({
          title: '保存作品',
          editable: true,
          placeholderText: '请输入作品标题',
          success: async (saveRes) => {
            if (saveRes.confirm) {
              try {
                uni.showLoading({
                  title: '保存中...'
                })
                
                const saveResult = await workApi.saveWork({
                  title: saveRes.content || '未命名作品',
                  imageUrl: imageUrl.value
                })
                
                uni.hideLoading()
                
                if (saveResult.code === 200 && saveResult.data) {
                  const workId = saveResult.data.id
                  // 跳转到预览页，传递图片URL和workId
                  uni.navigateTo({
                    url: `/pages/preview/preview?workId=${workId}&imageUrl=${encodeURIComponent(imageUrl.value)}`
                  })
                } else {
                  uni.showToast({
                    title: saveResult.message || '保存失败',
                    icon: 'none'
                  })
                }
              } catch (error) {
                uni.hideLoading()
                uni.showToast({
                  title: '保存失败',
                  icon: 'none'
                })
              }
            }
          }
        })
      } else {
        // 用户取消，直接跳转（但可能无法添加到购物车）
        uni.navigateTo({
          url: `/pages/preview/preview?imageUrl=${encodeURIComponent(imageUrl.value)}`
        })
      }
    }
  })
}

const handleRegenerate = () => {
  uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.result-container {
  min-height: 100vh;
  background-color: #F8F8F8;
}

.image-section {
  padding: 40rpx;
  background: #ffffff;
  margin-bottom: 20rpx;
  
  .result-image {
    width: 100%;
    min-height: 600rpx;
    border-radius: 20rpx;
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
    
    &.save-btn {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: #ffffff;
    }
    
    &.apply-btn {
      background: #007AFF;
      color: #ffffff;
    }
    
    &.regenerate-btn {
      background: #F5F5F5;
      color: #666666;
    }
  }
}
</style>


