<template>
  <view class="cart-container">
    <view class="cart-list" v-if="cartItems.length > 0">
      <view class="select-all-section">
        <checkbox-group @change="onSelectAllChange">
          <label class="select-all-label">
            <checkbox :checked="isAllSelected" color="#667eea" />
            <text class="select-all-text">全选</text>
          </label>
        </checkbox-group>
      </view>
      
      <view class="cart-item" v-for="item in cartItems" :key="item.id">
        <checkbox-group @change="onItemSelectChange(item)">
          <label class="item-checkbox">
            <checkbox :checked="item.selected" color="#667eea" />
          </label>
        </checkbox-group>
        <image class="item-image" :src="item.previewImageUrl" mode="aspectFill"></image>
        <view class="item-info">
          <text class="item-title">{{ item.productName || '定制包包' }}</text>
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
        <text class="total-amount">¥{{ selectedAmount }}</text>
        <text class="selected-count">(已选{{ selectedCount }}件)</text>
      </view>
      <button class="checkout-btn" @click="goToCheckout" :disabled="selectedCount === 0">去结算({{ selectedCount }})</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { cartApi } from '@/api/cart'
import { onPullDownRefresh, onShow } from '@dcloudio/uni-app'

const cartItems = ref([])

const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    return sum + (item.price || 0) * item.quantity
  }, 0).toFixed(2)
})

const selectedCount = computed(() => {
  return cartItems.value.filter(item => item.selected).length
})

const selectedAmount = computed(() => {
  return cartItems.value
    .filter(item => item.selected)
    .reduce((sum, item) => {
      return sum + (item.price || 0) * item.quantity
    }, 0).toFixed(2)
})

const isAllSelected = computed(() => {
  return cartItems.value.length > 0 && cartItems.value.every(item => item.selected)
})

const loadCartItems = async () => {
  try {
    console.log('开始加载购物车数据...')
    const res = await cartApi.getCartItems()
    console.log('购物车API响应:', res)
    if (res.code === 200 && res.data) {
      // 添加 selected 属性，默认全选
      cartItems.value = (res.data || []).map(item => ({
        ...item,
        selected: true
      }))
      console.log('购物车数据加载成功，共', cartItems.value.length, '项')
    } else {
      console.warn('购物车加载失败:', res.message)
      cartItems.value = []
    }
  } catch (error) {
    console.error('加载购物车失败', error)
    uni.showToast({
      title: '加载购物车失败',
      icon: 'none'
    })
    cartItems.value = []
  } finally {
    uni.stopPullDownRefresh()
  }
}

const onSelectAllChange = (e) => {
  const checked = e.detail.value.length > 0
  console.log('全选状态改变:', checked)
  cartItems.value.forEach(item => {
    item.selected = checked
  })
  // 强制更新视图
  cartItems.value = [...cartItems.value]
}

const onItemSelectChange = (item) => {
  console.log('商品选择状态改变:', item.id, '当前状态:', item.selected)
  item.selected = !item.selected
  // 强制更新视图
  cartItems.value = [...cartItems.value]
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
  const selectedItems = cartItems.value.filter(item => item.selected)
  
  if (selectedItems.length === 0) {
    uni.showToast({
      title: '请选择要结算的商品',
      icon: 'none'
    })
    return
  }
  
  // 传递选中商品的ID列表
  const selectedIds = selectedItems.map(item => item.id).join(',')
  uni.navigateTo({
    url: `/pages/order/confirm?itemIds=${selectedIds}`
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

// 在 tabBar 页面中，使用 onShow 确保每次显示时都刷新数据
onShow(() => {
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
  
  .select-all-section {
    background: #ffffff;
    border-radius: 20rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    
    .select-all-label {
      display: flex;
      align-items: center;
      
      .select-all-text {
        margin-left: 20rpx;
        font-size: 28rpx;
        color: #333333;
      }
    }
  }
  
  .cart-item {
    background: #ffffff;
    border-radius: 20rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    display: flex;
    align-items: center;
    
    .item-checkbox {
      margin-right: 20rpx;
    }
    
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
    display: flex;
    align-items: baseline;
    
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
    
    .selected-count {
      font-size: 24rpx;
      color: #999999;
      margin-left: 10rpx;
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
    
    &[disabled] {
      background: #CCCCCC;
    }
  }
}
</style>


