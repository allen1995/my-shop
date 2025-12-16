<template>
  <view class="confirm-container">
    <view class="order-items-section">
      <text class="section-title">订单商品</text>
      <view class="order-item" v-for="item in orderItems" :key="item.id">
        <image class="item-image" :src="item.previewImageUrl" mode="aspectFill"></image>
        <view class="item-info">
          <text class="item-title">定制包包</text>
          <text class="item-spec">{{ item.color }} | {{ item.size }} | x{{ item.quantity }}</text>
          <text class="item-price">¥{{ item.price }}</text>
        </view>
      </view>
    </view>
    
    <view class="address-section">
      <text class="section-title">收货地址</text>
      <view class="address-card" @click="selectAddress">
        <text class="address-text" v-if="selectedAddress">
          {{ selectedAddress.receiverName }} {{ selectedAddress.receiverPhone }}
          {{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }}{{ selectedAddress.detailAddress }}
        </text>
        <text class="address-placeholder" v-else>请选择收货地址</text>
        <text class="address-arrow">></text>
      </view>
    </view>
    
    <view class="price-section">
      <view class="price-row">
        <text class="price-label">商品总价</text>
        <text class="price-value">¥{{ totalAmount }}</text>
      </view>
      <view class="price-row">
        <text class="price-label">运费</text>
        <text class="price-value">¥{{ shippingFee }}</text>
      </view>
      <view class="price-row total-row">
        <text class="price-label">实付金额</text>
        <text class="price-value">¥{{ finalAmount }}</text>
      </view>
    </view>
    
    <view class="action-section">
      <button class="submit-btn" @click="handleSubmit" :disabled="!canSubmit" :loading="submitting">
        提交订单
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { cartApi } from '@/api/cart'
import { orderApi } from '@/api/order'
import { addressApi } from '@/api/address'
import { onLoad, onShow } from '@dcloudio/uni-app'

const orderItems = ref([])
const selectedAddress = ref(null)
const shippingFee = ref(10)
const submitting = ref(false)

const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => {
    return sum + (item.price || 0) * item.quantity
  }, 0).toFixed(2)
})

const finalAmount = computed(() => {
  return (parseFloat(totalAmount.value) + shippingFee.value).toFixed(2)
})

const canSubmit = computed(() => {
  return orderItems.value.length > 0 && selectedAddress.value
})

onLoad(async (options) => {
  // 获取选中的商品ID列表
  const itemIds = options.itemIds ? options.itemIds.split(',').map(id => parseInt(id)) : []
  await loadCartItems(itemIds)
  await loadDefaultAddress()
})

onShow(() => {
  // 从地址列表页面返回时，检查全局数据中是否有选中的地址
  const app = getApp()
  if (app.globalData && app.globalData.selectedAddress) {
    selectedAddress.value = app.globalData.selectedAddress
    // 清除全局数据
    app.globalData.selectedAddress = null
  }
})

const loadCartItems = async (selectedIds = []) => {
  try {
    const res = await cartApi.getCartItems()
    if (res.code === 200 && res.data) {
      let items = res.data || []
      
      // 如果有选中的ID，只加载选中的商品
      if (selectedIds.length > 0) {
        items = items.filter(item => selectedIds.includes(item.id))
      }
      
      // 转换为订单项格式，使用后端返回的实际价格
      orderItems.value = items.map(item => ({
        id: item.id,
        workId: item.workId,
        productId: item.productId,
        color: item.color,
        size: item.size,
        quantity: item.quantity,
        price: item.price || 0, // 使用后端返回的实际价格
        previewImageUrl: item.previewImageUrl
      }))
      
      console.log('订单商品加载成功，共', orderItems.value.length, '项')
    }
  } catch (error) {
    console.error('加载购物车失败', error)
    uni.showToast({
      title: '加载商品失败',
      icon: 'none'
    })
  }
}

// 加载默认地址
const loadDefaultAddress = async () => {
  try {
    const res = await addressApi.getAddresses()
    if (res.code === 200 && res.data) {
      // 查找默认地址
      const defaultAddress = res.data.find(addr => addr.isDefault)
      if (defaultAddress) {
        selectedAddress.value = defaultAddress
      } else if (res.data.length > 0) {
        // 如果没有默认地址，使用第一个地址
        selectedAddress.value = res.data[0]
      }
    }
  } catch (error) {
    console.error('加载默认地址失败', error)
  }
}

// 根据ID加载地址
const loadAddressById = async (addressId) => {
  try {
    const res = await addressApi.getAddress(addressId)
    if (res.code === 200 && res.data) {
      selectedAddress.value = res.data
    }
  } catch (error) {
    console.error('加载地址失败', error)
  }
}

const selectAddress = () => {
  uni.navigateTo({
    url: '/pages/address/list?select=true'
  })
}

const handleSubmit = async () => {
  if (!canSubmit.value) {
    if (!selectedAddress.value) {
      uni.showToast({
        title: '请选择收货地址',
        icon: 'none'
      })
    }
    return
  }
  
  submitting.value = true
  
  try {
    const cartItemIds = orderItems.value.map(item => item.id)
    
    console.log('创建订单 - cartItemIds:', cartItemIds, 'addressId:', selectedAddress.value?.id)
    
    if (!selectedAddress.value || !selectedAddress.value.id) {
      uni.showToast({
        title: '请选择收货地址',
        icon: 'none'
      })
      submitting.value = false
      return
    }
    
    const res = await orderApi.createOrder({
      cartItemIds,
      addressId: selectedAddress.value.id
    })
    
    console.log('创建订单响应:', res)
    
    if (res.code === 200 && res.data) {
      const order = res.data
      
      uni.showToast({
        title: '订单创建成功',
        icon: 'success'
      })
      
      // 跳转到支付页
      setTimeout(() => {
        uni.redirectTo({
          url: `/pages/order/payment?orderId=${order.id}`
        })
      }, 1500)
    } else {
      uni.showToast({
        title: res.message || '创建订单失败',
        icon: 'none',
        duration: 2000
      })
    }
  } catch (error) {
    console.error('创建订单异常:', error)
    uni.showToast({
      title: error.message || '创建订单失败',
      icon: 'none',
      duration: 2000
    })
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.confirm-container {
  min-height: 100vh;
  background-color: #F8F8F8;
  padding-bottom: 120rpx;
}

.order-items-section,
.address-section,
.price-section {
  background: #ffffff;
  margin-bottom: 20rpx;
  padding: 40rpx;
  
  .section-title {
    display: block;
    font-size: 32rpx;
    font-weight: bold;
    color: #333333;
    margin-bottom: 30rpx;
  }
}

.order-item {
  display: flex;
  align-items: center;
  margin-bottom: 30rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  .item-image {
    width: 120rpx;
    height: 120rpx;
    border-radius: 10rpx;
    margin-right: 20rpx;
  }
  
  .item-info {
    flex: 1;
    
    .item-title {
      display: block;
      font-size: 28rpx;
      font-weight: bold;
      color: #333333;
      margin-bottom: 10rpx;
    }
    
    .item-spec {
      display: block;
      font-size: 24rpx;
      color: #999999;
      margin-bottom: 10rpx;
    }
    
    .item-price {
      display: block;
      font-size: 32rpx;
      font-weight: bold;
      color: #FF3B30;
    }
  }
}

.address-card {
  display: flex;
  align-items: center;
  padding: 30rpx;
  background: #F5F5F5;
  border-radius: 10rpx;
  
  .address-text,
  .address-placeholder {
    flex: 1;
    font-size: 28rpx;
    color: #333333;
  }
  
  .address-placeholder {
    color: #999999;
  }
  
  .address-arrow {
    font-size: 32rpx;
    color: #999999;
  }
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  .price-label {
    font-size: 28rpx;
    color: #666666;
  }
  
  .price-value {
    font-size: 28rpx;
    color: #333333;
  }
  
  &.total-row {
    padding-top: 20rpx;
    border-top: 2rpx solid #E5E5E5;
    
    .price-label {
      font-size: 32rpx;
      font-weight: bold;
      color: #333333;
    }
    
    .price-value {
      font-size: 40rpx;
      font-weight: bold;
      color: #FF3B30;
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
  
  .submit-btn {
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


