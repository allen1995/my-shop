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
          {{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }}{{ selectedAddress.detail }}
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
import { ref, computed, onMounted } from 'vue'
import { cartApi } from '@/api/cart'
import { orderApi } from '@/api/order'
import { onLoad } from '@dcloudio/uni-app'

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

onLoad(async () => {
  await loadCartItems()
})

const loadCartItems = async () => {
  try {
    const res = await cartApi.getCartItems()
    if (res.code === 200 && res.data) {
      // 转换为订单项格式
      orderItems.value = (res.data || []).map(item => ({
        id: item.id,
        workId: item.workId,
        productId: item.productId,
        color: item.color,
        size: item.size,
        quantity: item.quantity,
        price: 299, // 临时价格，实际应从商品获取
        previewImageUrl: item.previewImageUrl
      }))
    }
  } catch (error) {
    console.error('加载购物车失败', error)
  }
}

const selectAddress = () => {
  uni.navigateTo({
    url: '/pages/address/list?select=true'
  })
}

const handleSubmit = async () => {
  if (!canSubmit.value) return
  
  submitting.value = true
  
  try {
    const cartItemIds = orderItems.value.map(item => item.id)
    
    const res = await orderApi.createOrder({
      cartItemIds,
      addressId: selectedAddress.value.id
    })
    
    if (res.code === 200 && res.data) {
      const order = res.data
      
      // 跳转到支付页
      uni.redirectTo({
        url: `/pages/order/payment?orderId=${order.id}`
      })
    }
  } catch (error) {
    uni.showToast({
      title: '创建订单失败',
      icon: 'none'
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


