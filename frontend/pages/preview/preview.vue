<template>
  <view class="preview-container">
    <view class="preview-section">
      <!-- 暂时使用 image 标签显示，确保页面能正常渲染 -->
      <image 
        v-if="imageUrl"
        :src="imageUrl" 
        class="preview-image"
        mode="aspectFit"
      ></image>
      <view v-else class="preview-placeholder">
        <text class="placeholder-text">暂无预览图片</text>
      </view>
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
import { ref, computed, onMounted, nextTick } from 'vue'
import { productApi } from '@/api/product'
import { cartApi } from '@/api/cart'
import { onLoad } from '@dcloudio/uni-app'

const workId = ref(null)
const imageUrl = ref('')
const products = ref([])
const selectedProduct = ref(null)
const selectedColor = ref('')
const selectedSize = ref('')

const canAddToCart = computed(() => {
  return selectedProduct.value && selectedColor.value && selectedSize.value
})

onLoad(async (options) => {
  workId.value = options.workId || null
  imageUrl.value = decodeURIComponent(options.imageUrl || '')
  console.log('预览页面加载 - workId:', workId.value, 'imageUrl:', imageUrl.value)
  
  if (!workId.value) {
    uni.showToast({
      title: '缺少作品ID，无法添加到购物车',
      icon: 'none',
      duration: 2000
    })
  }
  
  await loadProducts()
})

const loadProducts = async () => {
  try {
    const res = await productApi.getProducts()
    console.log('商品列表响应:', res)
    if (res.code === 200 && res.data) {
      products.value = res.data || []
      console.log('商品列表:', products.value)
      
      // 处理商品数据，确保 colors 和 sizes 是数组
      products.value = products.value.map(product => {
        // 如果 colors 是字符串（JSON），解析为数组
        if (typeof product.colors === 'string') {
          try {
            product.colors = JSON.parse(product.colors)
          } catch (e) {
            product.colors = []
          }
        }
        // 如果 sizes 是字符串（JSON），解析为数组
        if (typeof product.sizes === 'string') {
          try {
            product.sizes = JSON.parse(product.sizes)
          } catch (e) {
            product.sizes = []
          }
        }
        return product
      })
      
      if (products.value.length > 0) {
        selectedProduct.value = products.value[0]
        if (selectedProduct.value.colors && selectedProduct.value.colors.length > 0) {
          selectedColor.value = selectedProduct.value.colors[0]
        }
        if (selectedProduct.value.sizes && selectedProduct.value.sizes.length > 0) {
          selectedSize.value = selectedProduct.value.sizes[0]
        }
      }
    } else {
      console.warn('商品列表加载失败:', res.message)
      // 如果加载失败，使用默认商品数据，确保页面能显示
      products.value = []
    }
  } catch (error) {
    console.error('加载商品失败', error)
    // 加载失败时，使用空数组，确保页面能显示
    products.value = []
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
  if (!canAddToCart.value) {
    uni.showToast({
      title: '请选择商品规格',
      icon: 'none'
    })
    return
  }
  
  if (!workId.value) {
    uni.showModal({
      title: '提示',
      content: '当前图片未保存为作品，无法添加到购物车。是否先保存为作品？',
      success: async (res) => {
        if (res.confirm) {
          // 跳转到作品集页面，让用户先保存
          uni.showToast({
            title: '请先保存作品',
            icon: 'none'
          })
          setTimeout(() => {
            uni.switchTab({
              url: '/pages/works/list'
            })
          }, 1500)
        }
      }
    })
    return
  }
  
  if (!selectedProduct.value || !selectedColor.value || !selectedSize.value) {
    uni.showToast({
      title: '信息不完整',
      icon: 'none'
    })
    return
  }
  
  try {
    console.log('添加购物车 - workId:', workId.value, 'productId:', selectedProduct.value.id, 'color:', selectedColor.value, 'size:', selectedSize.value)
    
    const res = await cartApi.addToCart({
      workId: workId.value,
      productId: selectedProduct.value.id,
      color: selectedColor.value,
      size: selectedSize.value,
      quantity: 1,
      previewImageUrl: imageUrl.value
    })
    
    console.log('添加购物车响应:', res)
    
    if (res.code === 200) {
      uni.showToast({
        title: '已加入购物车',
        icon: 'success'
      })
      
      // 延迟跳转，让用户看到成功提示
      setTimeout(() => {
        uni.switchTab({
          url: '/pages/cart/cart'
        })
      }, 1500)
    } else {
      uni.showToast({
        title: res.message || '加入购物车失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('加入购物车异常:', error)
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
  align-items: center;
  min-height: 400rpx;
  
  .preview-image {
    width: 100%;
    max-width: 750rpx;
    height: 750rpx;
    border: 2rpx solid #E5E5E5;
    border-radius: 20rpx;
  }
  
  .preview-placeholder {
    width: 100%;
    height: 400rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #F5F5F5;
    border-radius: 20rpx;
    
    .placeholder-text {
      font-size: 28rpx;
      color: #999999;
    }
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


