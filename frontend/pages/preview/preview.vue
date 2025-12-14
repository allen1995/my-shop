<template>
  <view class="preview-container">
    <view class="preview-section">
      <canvas 
        canvas-id="previewCanvas" 
        class="preview-canvas"
        :style="{ width: canvasWidth + 'px', height: canvasHeight + 'px' }"
      ></canvas>
    </view>
    
    <view class="product-section">
      <view class="product-selector">
        <text class="selector-label">选择包包款式</text>
        <picker :range="products" range-key="name" @change="onProductChange">
          <view class="picker-view">{{ selectedProduct?.name || '请选择' }}</view>
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
    </view>
    
    <view class="action-section">
      <button class="add-cart-btn" @click="handleAddToCart" :disabled="!canAddToCart">
        加入购物车
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { productApi } from '@/api/product'
import { cartApi } from '@/api/cart'
import { onLoad } from '@dcloudio/uni-app'

const workId = ref(null)
const imageUrl = ref('')
const products = ref([])
const selectedProduct = ref(null)
const selectedColor = ref('')
const selectedSize = ref('')
const canvasWidth = ref(750)
const canvasHeight = ref(750)

const canAddToCart = computed(() => {
  return selectedProduct.value && selectedColor.value && selectedSize.value
})

onLoad(async (options) => {
  workId.value = options.workId
  imageUrl.value = decodeURIComponent(options.imageUrl || '')
  await loadProducts()
})

const loadProducts = async () => {
  try {
    const res = await productApi.getProducts()
    if (res.code === 200 && res.data) {
      products.value = res.data || []
      if (products.value.length > 0) {
        selectedProduct.value = products.value[0]
        if (selectedProduct.value.colors && selectedProduct.value.colors.length > 0) {
          selectedColor.value = selectedProduct.value.colors[0]
        }
        if (selectedProduct.value.sizes && selectedProduct.value.sizes.length > 0) {
          selectedSize.value = selectedProduct.value.sizes[0]
        }
      }
    }
  } catch (error) {
    console.error('加载商品失败', error)
  }
}

const onProductChange = (e) => {
  selectedProduct.value = products.value[e.detail.value]
  if (selectedProduct.value.colors && selectedProduct.value.colors.length > 0) {
    selectedColor.value = selectedProduct.value.colors[0]
  }
  if (selectedProduct.value.sizes && selectedProduct.value.sizes.length > 0) {
    selectedSize.value = selectedProduct.value.sizes[0]
  }
}

const handleAddToCart = async () => {
  if (!canAddToCart.value) return
  
  try {
    const res = await cartApi.addToCart({
      workId: workId.value,
      productId: selectedProduct.value.id,
      color: selectedColor.value,
      size: selectedSize.value,
      quantity: 1,
      previewImageUrl: imageUrl.value
    })
    
    if (res.code === 200) {
      uni.showToast({
        title: '已加入购物车',
        icon: 'success'
      })
      
      setTimeout(() => {
        uni.switchTab({
          url: '/pages/cart/cart'
        })
      }, 1500)
    }
  } catch (error) {
    uni.showToast({
      title: '加入购物车失败',
      icon: 'none'
    })
  }
}
</script>

<style lang="scss" scoped>
.preview-container {
  min-height: 100vh;
  background-color: #F8F8F8;
}

.preview-section {
  background: #ffffff;
  padding: 40rpx;
  margin-bottom: 20rpx;
  display: flex;
  justify-content: center;
  
  .preview-canvas {
    border: 2rpx solid #E5E5E5;
    border-radius: 20rpx;
  }
}

.product-section {
  background: #ffffff;
  padding: 40rpx;
  margin-bottom: 20rpx;
  
  .product-selector {
    margin-bottom: 40rpx;
    
    .selector-label {
      display: block;
      font-size: 32rpx;
      font-weight: bold;
      color: #333333;
      margin-bottom: 20rpx;
    }
    
    .picker-view {
      padding: 20rpx;
      background: #F5F5F5;
      border-radius: 10rpx;
      font-size: 28rpx;
      color: #333333;
    }
  }
  
  .spec-selector {
    .spec-item {
      margin-bottom: 30rpx;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .spec-label {
        display: block;
        font-size: 28rpx;
        font-weight: bold;
        color: #333333;
        margin-bottom: 20rpx;
      }
      
      .spec-options {
        display: flex;
        gap: 20rpx;
        flex-wrap: wrap;
        
        .spec-option {
          padding: 15rpx 30rpx;
          background: #F5F5F5;
          border-radius: 8rpx;
          font-size: 26rpx;
          color: #666666;
          
          &.active {
            background: #007AFF;
            color: #ffffff;
          }
        }
      }
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
  
  .add-cart-btn {
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


