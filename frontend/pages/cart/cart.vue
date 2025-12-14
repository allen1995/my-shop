<template>
  <view class="cart-container">
    <view class="cart-list" v-if="cartItems.length > 0">
      <view class="cart-item" v-for="item in cartItems" :key="item.id">
        <image class="item-image" :src="item.previewImageUrl" mode="aspectFill"></image>
        <view class="item-info">
          <text class="item-title">定制包包</text>
          <text class="item-spec">颜色：{{ item.color }} | 尺寸：{{ item.size }}</text>
          <view class="item-footer">
            <text class="item-price">¥{{ item.price || 0 }}</text>
            <view class="quantity-control">
              <text class="quantity-btn" @click="decreaseQuantity(item)">-</text>
              <text class="quantity-value">{{ item.quantity }}</text>
              <text class="quantity-btn" @click="increaseQuantity(item)">+</text>
            </view>
          </view>
        </view>
        <text class="delete-btn" @click="handleDelete(item.id)">删除</text>
      </view>
    </view>
    
    <view class="empty-state" v-else>
      <image class="empty-icon" src="/static/icons/empty-cart.png" mode="aspectFit"></image>
      <text class="empty-text">购物车是空的</text>
      <button class="empty-btn" @click="goToIndex">去逛逛</button>
    </view>
    
    <view class="footer-section" v-if="cartItems.length > 0">
      <view class="total-info">
        <text class="total-label">合计：</text>
        <text class="total-amount">¥{{ totalAmount }}</text>
      </view>
      <button class="checkout-btn" @click="goToCheckout">去结算</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { cartApi } from '@/api/cart'
import { onPullDownRefresh } from '@dcloudio/uni-app'

const cartItems = ref([])

const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    return sum + (item.price || 0) * item.quantity
  }, 0).toFixed(2)
})

const loadCartItems = async () => {
  try {
    const res = await cartApi.getCartItems()
    if (res.code === 200 && res.data) {
      cartItems.value = res.data || []
    }
  } catch (error) {
    console.error('加载购物车失败', error)
  } finally {
    uni.stopPullDownRefresh()
  }
}

const increaseQuantity = async (item) => {
  try {
    await cartApi.updateCartItem(item.id, {
      quantity: item.quantity + 1
    })
    await loadCartItems()
  } catch (error) {
    uni.showToast({
      title: '更新失败',
      icon: 'none'
    })
  }
}

const decreaseQuantity = async (item) => {
  if (item.quantity <= 1) return
  
  try {
    await cartApi.updateCartItem(item.id, {
      quantity: item.quantity - 1
    })
    await loadCartItems()
  } catch (error) {
    uni.showToast({
      title: '更新失败',
      icon: 'none'
    })
  }
}

const handleDelete = async (itemId) => {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这个商品吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await cartApi.deleteCartItem(itemId)
          await loadCartItems()
          uni.showToast({
            title: '删除成功',
            icon: 'success'
          })
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

const goToCheckout = () => {
  uni.navigateTo({
    url: '/pages/order/confirm'
  })
}

const goToIndex = () => {
  uni.switchTab({
    url: '/pages/index/index'
  })
}

onMounted(() => {
  loadCartItems()
})

onPullDownRefresh(() => {
  loadCartItems()
})
</script>

<style lang="scss" scoped>
.cart-container {
  min-height: 100vh;
  background-color: #F8F8F8;
  padding-bottom: 120rpx;
}

.cart-list {
  padding: 20rpx;
  
  .cart-item {
    background: #ffffff;
    border-radius: 20rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    display: flex;
    align-items: center;
    
    .item-image {
      width: 160rpx;
      height: 160rpx;
      border-radius: 10rpx;
      margin-right: 20rpx;
    }
    
    .item-info {
      flex: 1;
      
      .item-title {
        display: block;
        font-size: 32rpx;
        font-weight: bold;
        color: #333333;
        margin-bottom: 10rpx;
      }
      
      .item-spec {
        display: block;
        font-size: 24rpx;
        color: #999999;
        margin-bottom: 20rpx;
      }
      
      .item-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .item-price {
          font-size: 36rpx;
          font-weight: bold;
          color: #FF3B30;
        }
        
        .quantity-control {
          display: flex;
          align-items: center;
          gap: 20rpx;
          
          .quantity-btn {
            width: 50rpx;
            height: 50rpx;
            background: #F5F5F5;
            border-radius: 8rpx;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 32rpx;
            color: #333333;
          }
          
          .quantity-value {
            font-size: 28rpx;
            color: #333333;
            min-width: 40rpx;
            text-align: center;
          }
        }
      }
    }
    
    .delete-btn {
      padding: 10rpx 20rpx;
      color: #FF3B30;
      font-size: 28rpx;
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

.footer-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #ffffff;
  padding: 30rpx 40rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);
  
  .total-info {
    .total-label {
      font-size: 28rpx;
      color: #666666;
      margin-right: 10rpx;
    }
    
    .total-amount {
      font-size: 40rpx;
      font-weight: bold;
      color: #FF3B30;
    }
  }
  
  .checkout-btn {
    padding: 20rpx 60rpx;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #ffffff;
    border-radius: 44rpx;
    font-size: 32rpx;
    font-weight: bold;
    border: none;
  }
}
</style>


