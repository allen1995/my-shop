<template>
  <view class="preview-container">
    <!-- 预览图片展示区域 -->
    <view class="preview-section">
      <!-- 未生成时显示原图 -->
      <view v-if="!previewUrl && !isGenerating" class="preview-placeholder">
        <image 
          v-if="imageUrl"
          :src="imageUrl" 
          class="placeholder-image"
          mode="aspectFit"
        />
        <text class="placeholder-hint">选择商品后生成预览</text>
      </view>
      
      <!-- 生成中显示加载 -->
      <view v-if="isGenerating" class="preview-loading">
        <view class="loading-spinner"></view>
        <text class="loading-text">{{ loadingText }}</text>
        <text class="loading-tip">预计需要 10 秒</text>
      </view>
      
      <!-- 预览图片 -->
      <view v-if="previewUrl && !isGenerating" class="preview-result">
        <image 
          :src="previewUrl" 
          class="preview-image"
          mode="aspectFit"
        />
        <button 
          class="regenerate-btn"
          @click="handleRegenerate"
        >
          <text class="btn-icon">🔄</text>
          <text>重新生成</text>
        </button>
      </view>
      
      <!-- 错误提示 -->
      <view v-if="errorMessage && !isGenerating" class="error-message">
        <text class="error-icon">⚠️</text>
        <text class="error-text">{{ errorMessage }}</text>
        <button class="retry-btn" @click="handleRetry">重试</button>
      </view>
    </view>
    
    <!-- 商品选择区域 -->
    <view class="product-section">
      <view class="product-selector">
        <text class="selector-label">选择包包款式</text>
        <picker :range="products" range-key="name" @change="onProductChange">
          <view class="picker-view">
            <text>{{ selectedProduct?.name || '请选择' }}</text>
            <text class="picker-arrow">▼</text>
          </view>
        </picker>
      </view>
      
      <view class="spec-selector" v-if="selectedProduct">
        <view class="spec-item">
          <text class="spec-label">颜色</text>
          <view class="spec-options">
            <view 
              class="spec-option" 
              v-for="color in selectedProduct.colors" 
              :key="color"
              :class="{ active: selectedColor === color }"
              @click="selectedColor = color"
            >{{ color }}</view>
          </view>
        </view>
        
        <view class="spec-item">
          <text class="spec-label">尺寸</text>
          <view class="spec-options">
            <view 
              class="spec-option" 
              v-for="size in selectedProduct.sizes" 
              :key="size"
              :class="{ active: selectedSize === size }"
              @click="selectedSize = size"
            >{{ size }}</view>
          </view>
        </view>
      </view>
      
      <!-- 生成预览按钮 -->
      <button 
        class="generate-btn"
        :class="{ disabled: !canGenerate || isGenerating }"
        :disabled="!canGenerate || isGenerating"
        @click="handleGeneratePreview"
      >
        <text v-if="isGenerating">生成中...</text>
        <text v-else>生成预览</text>
      </button>
    </view>
    
    <!-- 操作区域 -->
    <view class="action-section">
      <button 
        class="add-cart-btn" 
        :class="{ disabled: !canAddToCart }"
        :disabled="!canAddToCart"
        @click="handleAddToCart"
      >加入购物车</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad, onUnload } from '@dcloudio/uni-app'
import { productApi } from '@/api/product'
import { cartApi } from '@/api/cart'
import { generatePreview, getTaskStatus } from '@/api/preview'

// 基础数据
const workId = ref(null)
const imageUrl = ref('')
const products = ref([])
const selectedProduct = ref(null)
const selectedColor = ref('')
const selectedSize = ref('')

// 预览相关
const previewUrl = ref('')
const isGenerating = ref(false)
const taskId = ref(null)
const errorMessage = ref('')
const loadingText = ref('正在生成预览，请稍候...')
const pollingTimer = ref(null)

// 计算属性
const canGenerate = computed(() => {
  return selectedProduct.value && selectedColor.value && selectedSize.value
})

const canAddToCart = computed(() => {
  return canGenerate.value && previewUrl.value && !isGenerating.value
})

// 页面加载
onLoad(async (options) => {
  workId.value = options.workId ? parseInt(options.workId) : null
  imageUrl.value = decodeURIComponent(options.imageUrl || '')
  
  console.log('预览页面加载 - workId:', workId.value, 'imageUrl:', imageUrl.value)
  
  if (!workId.value) {
    uni.showToast({
      title: '缺少作品ID',
      icon: 'none',
      duration: 2000
    })
    return
  }
  
  await loadProducts()
})

// 加载商品列表
const loadProducts = async () => {
  try {
    const res = await productApi.getProducts()
    console.log('商品列表响应:', res)
    
    if (res.code === 200 && res.data) {
      products.value = res.data.map(product => {
        // 解析 colors 和 sizes (如果是 JSON 字符串)
        let colors = []
        let sizes = []
        
        try {
          colors = typeof product.colors === 'string' 
            ? JSON.parse(product.colors) 
            : (Array.isArray(product.colors) ? product.colors : [])
        } catch (e) {
          console.warn('解析颜色失败:', e)
          colors = []
        }
        
        try {
          sizes = typeof product.sizes === 'string' 
            ? JSON.parse(product.sizes) 
            : (Array.isArray(product.sizes) ? product.sizes : [])
        } catch (e) {
          console.warn('解析尺寸失败:', e)
          sizes = []
        }
        
        return {
          ...product,
          colors,
          sizes
        }
      })
      
      console.log('处理后的商品列表:', products.value)
    }
  } catch (error) {
    console.error('加载商品列表失败:', error)
    uni.showToast({
      title: '加载商品列表失败',
      icon: 'none'
    })
  }
}

// 商品选择改变
const onProductChange = (e) => {
  const index = e.detail.value
  selectedProduct.value = products.value[index]
  selectedColor.value = ''
  selectedSize.value = ''
  previewUrl.value = ''
  console.log('选择商品:', selectedProduct.value)
}

// 生成预览
const handleGeneratePreview = async () => {
  if (!canGenerate.value || isGenerating.value) {
    return
  }
  
  try {
    isGenerating.value = true
    errorMessage.value = ''
    loadingText.value = '正在生成预览，请稍候...'
    
    console.log('开始生成预览:', {
      workId: workId.value,
      productId: selectedProduct.value.id,
      color: selectedColor.value,
      size: selectedSize.value
    })
    
    // 调用预览生成接口
    const res = await generatePreview({
      workId: workId.value,
      productId: selectedProduct.value.id,
      color: selectedColor.value,
      size: selectedSize.value
    })
    
    console.log('预览生成响应:', res)
    
    if (res.code === 200) {
      taskId.value = res.data.taskId
      
      // 如果已经有缓存结果，直接显示
      if (res.data.status === 'COMPLETED' && res.data.resultUrl) {
        previewUrl.value = res.data.resultUrl
        isGenerating.value = false
        
        uni.showToast({
          title: '预览生成成功',
          icon: 'success',
          duration: 2000
        })
      } else {
        // 开始轮询任务状态
        pollTaskStatus()
      }
    } else {
      throw new Error(res.message || '生成预览失败')
    }
    
  } catch (error) {
    console.error('生成预览失败:', error)
    errorMessage.value = error.message || '生成预览失败，请重试'
    isGenerating.value = false
    
    uni.showToast({
      title: errorMessage.value,
      icon: 'none',
      duration: 2000
    })
  }
}

// 轮询任务状态
const pollTaskStatus = async () => {
  if (!taskId.value) {
    return
  }
  
  try {
    const res = await getTaskStatus(taskId.value)
    console.log('任务状态:', res)
    
    if (res.code === 200) {
      const task = res.data
      
      if (task.status === 'COMPLETED') {
        // 生成完成
        previewUrl.value = task.resultUrl
        isGenerating.value = false
        
        // 清除定时器
        if (pollingTimer.value) {
          clearTimeout(pollingTimer.value)
          pollingTimer.value = null
        }
        
        uni.showToast({
          title: '预览生成成功',
          icon: 'success',
          duration: 2000
        })
        
      } else if (task.status === 'FAILED') {
        // 生成失败
        errorMessage.value = task.errorMessage || '生成失败'
        isGenerating.value = false
        
        // 清除定时器
        if (pollingTimer.value) {
          clearTimeout(pollingTimer.value)
          pollingTimer.value = null
        }
        
        uni.showModal({
          title: '提示',
          content: errorMessage.value + '，是否重试？',
          success: (modalRes) => {
            if (modalRes.confirm) {
              handleRetry()
            }
          }
        })
        
      } else {
        // 继续轮询
        const progress = task.progress || 0
        if (progress > 0) {
          loadingText.value = `生成中 ${progress}%...`
        }
        
        pollingTimer.value = setTimeout(pollTaskStatus, 2000)
      }
    }
    
  } catch (error) {
    console.error('查询任务状态失败:', error)
    isGenerating.value = false
    errorMessage.value = '查询任务状态失败'
    
    // 清除定时器
    if (pollingTimer.value) {
      clearTimeout(pollingTimer.value)
      pollingTimer.value = null
    }
  }
}

// 重新生成
const handleRegenerate = () => {
  console.log('重新生成预览')
  
  // 清除定时器
  if (pollingTimer.value) {
    clearTimeout(pollingTimer.value)
    pollingTimer.value = null
  }
  
  // 重置所有状态
  previewUrl.value = ''
  taskId.value = null
  errorMessage.value = ''
  isGenerating.value = false
  
  // 重新生成
  handleGeneratePreview()
}

// 重试（失败后重试）
const handleRetry = () => {
  console.log('重试生成预览')
  
  // 清除定时器
  if (pollingTimer.value) {
    clearTimeout(pollingTimer.value)
    pollingTimer.value = null
  }
  
  // 重置状态
  previewUrl.value = ''
  taskId.value = null  // ✅ 重要：清除旧的任务ID
  errorMessage.value = ''
  isGenerating.value = false
  
  // 重新生成（会创建新任务）
  handleGeneratePreview()
}

// 加入购物车
const handleAddToCart = async () => {
  if (!canAddToCart.value) {
    return
  }
  
  try {
    uni.showLoading({
      title: '加入中...'
    })
    
    const res = await cartApi.addToCart({
      workId: workId.value,
      productId: selectedProduct.value.id,
      color: selectedColor.value,
      size: selectedSize.value,
      quantity: 1,
      previewImageUrl: previewUrl.value
    })
    
    uni.hideLoading()
    
    if (res.code === 200) {
      uni.showToast({
        title: '已加入购物车',
        icon: 'success',
        duration: 1500
      })
      
      setTimeout(() => {
        uni.switchTab({
          url: '/pages/cart/cart'
        })
      }, 1500)
    } else {
      throw new Error(res.message || '加入购物车失败')
    }
    
  } catch (error) {
    console.error('加入购物车失败:', error)
    uni.hideLoading()
    uni.showToast({
      title: error.message || '加入购物车失败',
      icon: 'none',
      duration: 2000
    })
  }
}

// 页面卸载时清除定时器
onUnload(() => {
  if (pollingTimer.value) {
    clearTimeout(pollingTimer.value)
    pollingTimer.value = null
  }
})
</script>

<style scoped>
.preview-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 100rpx;
}

/* 预览区域 */
.preview-section {
  background-color: #fff;
  padding: 40rpx;
  margin-bottom: 20rpx;
}

.preview-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 600rpx;
  background-color: #fafafa;
  border-radius: 16rpx;
  border: 2rpx dashed #ddd;
}

.placeholder-image {
  width: 400rpx;
  height: 400rpx;
  margin-bottom: 20rpx;
}

.placeholder-hint {
  font-size: 28rpx;
  color: #999;
}

.preview-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 600rpx;
  background-color: #fafafa;
  border-radius: 16rpx;
}

.loading-spinner {
  width: 80rpx;
  height: 80rpx;
  border: 6rpx solid #f3f3f3;
  border-top: 6rpx solid #07c160;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 30rpx;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  font-size: 32rpx;
  color: #333;
  margin-bottom: 10rpx;
}

.loading-tip {
  font-size: 24rpx;
  color: #999;
}

.preview-result {
  position: relative;
}

.preview-image {
  width: 100%;
  min-height: 600rpx;
  border-radius: 16rpx;
  background-color: #fafafa;
}

.regenerate-btn {
  position: absolute;
  bottom: 20rpx;
  right: 20rpx;
  display: flex;
  align-items: center;
  gap: 10rpx;
  padding: 16rpx 32rpx;
  background-color: rgba(255, 255, 255, 0.9);
  border: 1rpx solid #ddd;
  border-radius: 40rpx;
  font-size: 28rpx;
  color: #333;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.btn-icon {
  font-size: 32rpx;
}

.error-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400rpx;
  background-color: #fff5f5;
  border-radius: 16rpx;
  border: 2rpx solid #ffebee;
  padding: 40rpx;
}

.error-icon {
  font-size: 80rpx;
  margin-bottom: 20rpx;
}

.error-text {
  font-size: 28rpx;
  color: #f56c6c;
  text-align: center;
  margin-bottom: 30rpx;
  line-height: 1.6;
}

.retry-btn {
  padding: 16rpx 48rpx;
  background-color: #07c160;
  color: #fff;
  border: none;
  border-radius: 40rpx;
  font-size: 28rpx;
}

/* 商品选择区域 */
.product-section {
  background-color: #fff;
  padding: 40rpx;
  margin-bottom: 20rpx;
}

.product-selector {
  margin-bottom: 30rpx;
}

.selector-label {
  display: block;
  font-size: 28rpx;
  color: #666;
  margin-bottom: 20rpx;
}

.picker-view {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 30rpx;
  background-color: #f8f8f8;
  border-radius: 12rpx;
  font-size: 30rpx;
  color: #333;
}

.picker-arrow {
  font-size: 24rpx;
  color: #999;
}

.spec-selector {
  margin-bottom: 30rpx;
}

.spec-item {
  margin-bottom: 30rpx;
}

.spec-label {
  display: block;
  font-size: 28rpx;
  color: #666;
  margin-bottom: 20rpx;
}

.spec-options {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.spec-option {
  padding: 16rpx 32rpx;
  background-color: #f8f8f8;
  border: 2rpx solid transparent;
  border-radius: 40rpx;
  font-size: 28rpx;
  color: #333;
  transition: all 0.3s;
}

.spec-option.active {
  background-color: #e8f5e9;
  border-color: #07c160;
  color: #07c160;
}

.generate-btn {
  width: 100%;
  padding: 28rpx;
  background-color: #07c160;
  color: #fff;
  border: none;
  border-radius: 12rpx;
  font-size: 32rpx;
  font-weight: 500;
  transition: all 0.3s;
}

.generate-btn.disabled {
  background-color: #ddd;
  color: #999;
}

/* 操作区域 */
.action-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 40rpx;
  background-color: #fff;
  box-shadow: 0 -4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.add-cart-btn {
  width: 100%;
  padding: 28rpx;
  background-color: #ff6b6b;
  color: #fff;
  border: none;
  border-radius: 12rpx;
  font-size: 32rpx;
  font-weight: 500;
  transition: all 0.3s;
}

.add-cart-btn.disabled {
  background-color: #ddd;
  color: #999;
}
</style>
