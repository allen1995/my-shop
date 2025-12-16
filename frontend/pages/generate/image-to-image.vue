<template>
  <view class="generate-container">
    <view class="upload-section">
      <text class="section-title">上传参考图片</text>
      <view class="upload-area" @click="chooseImage">
        <view v-if="imageUrl" class="image-wrapper">
          <image 
            class="preview-image" 
            :src="imageUrl" 
            :key="'img-' + Date.now()"
            mode="aspectFit"
            :lazy-load="false"
            :show-menu-by-longpress="false"
            @error="onImageError"
            @load="onImageLoad"
          ></image>
        </view>
        <view v-else class="upload-placeholder">
          <text class="upload-icon">📷</text>
          <text class="upload-text">点击选择图片</text>
          <text class="upload-hint">支持相册或拍照</text>
        </view>
        <view v-if="uploading" class="upload-mask">
          <text class="upload-status">上传中...</text>
        </view>
      </view>
      <view v-if="imageUrl" class="upload-actions">
        <button class="action-btn" @click="chooseImage">重新选择</button>
        <button class="action-btn" @click="removeImage">删除</button>
      </view>
    </view>
    
    <view class="input-section">
      <textarea 
        class="prompt-input" 
        v-model="prompt" 
        placeholder="描述你想要生成的图片风格或变化，例如：卡通风格、水彩画、抽象艺术"
        maxlength="500"
      ></textarea>
      <text class="char-count">{{ prompt.length }}/500</text>
    </view>
    
    <view class="params-section">
      <view class="param-item">
        <text class="param-label">相似度：{{ similarity }}%</text>
        <view class="slider-container">
          <slider 
            :value="similarity" 
            min="0" 
            max="100" 
            step="1"
            activeColor="#007AFF"
            backgroundColor="#E5E5E5"
            block-color="#007AFF"
            @change="onSimilarityChange"
          />
          <view class="slider-labels">
            <text class="slider-label">低</text>
            <text class="slider-label">高</text>
          </view>
        </view>
        <text class="param-desc">相似度越高，生成的图片越接近原图</text>
      </view>
    </view>
    
    <view class="action-section">
      <button 
        class="generate-btn" 
        :disabled="!canGenerate" 
        :loading="generating"
        @click="handleGenerate"
      >
        {{ generating ? '生成中...' : '开始生成' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { imageGenerationApi } from '@/api/imageGeneration'

const imageUrl = ref('')
const prompt = ref('')
const similarity = ref(50)
const generating = ref(false)
const uploading = ref(false)

const canGenerate = computed(() => {
  return imageUrl.value && prompt.value.trim().length > 0 && !generating.value
})

const chooseImage = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      const tempFilePath = res.tempFilePaths[0]
      console.log('选择的图片路径:', tempFilePath)
      
      // 先显示本地预览（确保路径格式正确）
      // 小程序环境可能需要特殊处理
      imageUrl.value = tempFilePath
      
      // 上传到服务器
      uploadImageToServer(tempFilePath)
    },
    fail: (err) => {
      console.error('选择图片失败', err)
      uni.showToast({
        title: '选择图片失败',
        icon: 'none'
      })
    }
  })
}

const uploadImageToServer = async (filePath) => {
  uploading.value = true
  try {
    uni.showLoading({
      title: '上传中...',
      mask: true
    })
    
    const res = await imageGenerationApi.uploadImage(filePath)
    console.log('上传响应:', res)
    
    if (res.code === 200 && res.data && res.data.imageUrl) {
      // 使用服务器返回的URL（确保是HTTPS）
      const serverUrl = res.data.imageUrl
      // 确保URL使用HTTPS
      const httpsUrl = serverUrl.startsWith('http://') 
        ? serverUrl.replace('http://', 'https://') 
        : serverUrl
      
      // 直接更新图片URL（不清空，避免闪烁）
      const oldUrl = imageUrl.value
      imageUrl.value = httpsUrl
      console.log('图片URL已更新:', imageUrl.value, '旧URL:', oldUrl)
      
      // 如果URL相同，强制更新key
      if (oldUrl === httpsUrl) {
        // 触发重新渲染
        await nextTick()
        imageUrl.value = ''
        await nextTick()
        imageUrl.value = httpsUrl
      }
      
      // 等待确保视图渲染
      await nextTick()
      await new Promise(resolve => setTimeout(resolve, 300))
      
      uni.hideLoading()
      uploading.value = false
      
      uni.showToast({
        title: '上传成功',
        icon: 'success',
        duration: 1500
      })
    } else {
      uni.hideLoading()
      uploading.value = false
      
      const errorMsg = res.message || '上传失败'
      console.error('上传失败:', errorMsg, res)
      uni.showToast({
        title: errorMsg,
        icon: 'none',
        duration: 2000
      })
      
      // 上传失败时，保持本地预览（如果可能）
      // 注意：小程序环境可能无法显示本地临时路径
      // 如果确实无法显示，可以考虑清空imageUrl
    }
  } catch (error) {
    console.error('上传异常:', error)
    uni.hideLoading()
    uploading.value = false
    
    uni.showToast({
      title: '上传失败，请重试',
      icon: 'none',
      duration: 2000
    })
    
    // 上传失败时，保持本地预览（如果可能）
  }
}

const removeImage = () => {
  imageUrl.value = ''
}

const onImageError = (e) => {
  console.error('图片加载失败:', e, '当前URL:', imageUrl.value)
  uni.showToast({
    title: '图片加载失败',
    icon: 'none'
  })
}

const onImageLoad = (e) => {
  console.log('图片加载成功:', imageUrl.value)
  console.log('图片尺寸:', e.detail.width, 'x', e.detail.height)
  // 强制更新视图
  nextTick(() => {
    console.log('视图已更新，当前imageUrl:', imageUrl.value)
  })
}

const onSimilarityChange = (e) => {
  similarity.value = e.detail.value
}

const handleGenerate = async () => {
  if (!canGenerate.value) return
  
  // 参数验证
  if (!imageUrl.value) {
    uni.showToast({
      title: '请先选择参考图片',
      icon: 'none'
    })
    return
  }
  
  if (!prompt.value.trim()) {
    uni.showToast({
      title: '请输入提示词',
      icon: 'none'
    })
    return
  }
  
  generating.value = true
  
  try {
    // 确保图片已上传到服务器
    let finalImageUrl = imageUrl.value
    
    // 如果是本地路径，需要先上传
    if (imageUrl.value.startsWith('file://') || imageUrl.value.startsWith('tmp://') || !imageUrl.value.startsWith('http')) {
      uni.showToast({
        title: '图片正在上传，请稍候',
        icon: 'none'
      })
      generating.value = false
      return
    }
    
    const res = await imageGenerationApi.imageToImage({
      imageUrl: finalImageUrl,
      prompt: prompt.value,
      similarity: similarity.value
    })
    
    if (res.code === 200 && res.data) {
      const taskId = res.data.taskId
      
      if (!taskId) {
        uni.showToast({
          title: '生成失败：未获取到任务ID',
          icon: 'none'
        })
        return
      }
      
      // 跳转到生成进度页
      uni.navigateTo({
        url: `/pages/generate/generating?taskId=${taskId}`
      })
    } else {
      uni.showToast({
        title: res.message || '生成失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('生成失败', error)
    uni.showToast({
      title: '生成失败，请重试',
      icon: 'none'
    })
  } finally {
    generating.value = false
  }
}
</script>

<style lang="scss" scoped>
.generate-container {
  padding: 40rpx;
  min-height: 100vh;
  background-color: #F8F8F8;
  padding-bottom: 200rpx;
}

.upload-section {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  
  .section-title {
    display: block;
    font-size: 32rpx;
    font-weight: bold;
    color: #333333;
    margin-bottom: 20rpx;
  }
  
  .upload-area {
    width: 100%;
    min-height: 400rpx;
    background: #F5F5F5;
    border-radius: 10rpx;
    overflow: hidden;
    margin-bottom: 20rpx;
    position: relative;
    
    .image-wrapper {
      width: 100%;
      height: 400rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #F5F5F5;
      
      .preview-image {
        width: 100%;
        height: 100%;
        display: block;
      }
    }
    
    .upload-placeholder {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 60rpx;
      
      .upload-icon {
        font-size: 80rpx;
        margin-bottom: 20rpx;
      }
      
      .upload-text {
        font-size: 32rpx;
        color: #333333;
        margin-bottom: 10rpx;
      }
      
      .upload-hint {
        font-size: 24rpx;
        color: #999999;
      }
    }
    
    .upload-mask {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 10rpx;
      
      .upload-status {
        color: #ffffff;
        font-size: 28rpx;
      }
    }
  }
  
  .upload-actions {
    display: flex;
    gap: 20rpx;
    
    .action-btn {
      flex: 1;
      height: 60rpx;
      line-height: 60rpx;
      background: #F5F5F5;
      color: #333333;
      border-radius: 10rpx;
      font-size: 28rpx;
      border: none;
    }
  }
}

.input-section {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  
  .prompt-input {
    width: 100%;
    min-height: 300rpx;
    font-size: 28rpx;
    line-height: 1.6;
    color: #333333;
  }
  
  .char-count {
    display: block;
    text-align: right;
    font-size: 24rpx;
    color: #999999;
    margin-top: 20rpx;
  }
}

.params-section {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  
  .param-item {
    .param-label {
      display: block;
      font-size: 32rpx;
      font-weight: bold;
      color: #333333;
      margin-bottom: 20rpx;
    }
    
    .slider-container {
      margin-bottom: 10rpx;
      
      .slider-labels {
        display: flex;
        justify-content: space-between;
        margin-top: 10rpx;
        
        .slider-label {
          font-size: 24rpx;
          color: #999999;
        }
      }
    }
    
    .param-desc {
      display: block;
      font-size: 24rpx;
      color: #999999;
      margin-top: 10rpx;
    }
  }
}

.action-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 30rpx 40rpx;
  background: #ffffff;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);
  
  .generate-btn {
    width: 100%;
    height: 88rpx;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #ffffff;
    border-radius: 44rpx;
    font-size: 32rpx;
    font-weight: bold;
    border: none;
    
    &[disabled] {
      background: #CCCCCC;
    }
  }
}
</style>

