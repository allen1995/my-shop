<template>
  <view class="payment-container">
    <view class="order-info-section">
      <text class="order-no">订单号：{{ order.orderNo }}</text>
      <text class="order-amount">¥{{ order.totalAmount }}</text>
    </view>
    
    <view class="payment-section">
      <text class="payment-title">选择支付方式</text>
      <view class="payment-method active">
        <image class="payment-icon" src="/static/icons/wechat-pay.png" mode="aspectFit"></image>
        <text class="payment-name">微信支付</text>
        <text class="payment-check">✓</text>
      </view>
    </view>
    
    <view class="action-section">
      <button class="pay-btn" @click="handlePay" :loading="paying">
        立即支付
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { orderApi } from '@/api/order'
import { onLoad } from '@dcloudio/uni-app'

const orderId = ref(null)
const order = ref({})
const paying = ref(false)

onLoad(async (options) => {
  orderId.value = options.orderId
  await loadOrderDetail()
})

const loadOrderDetail = async () => {
  try {
    const res = await orderApi.getOrderDetail(orderId.value)
    if (res.code === 200 && res.data) {
      order.value = res.data
    }
  } catch (error) {
    console.error('加载订单详情失败', error)
  }
}

const handlePay = async () => {
  paying.value = true
  
  try {
    // 创建支付订单
    const paymentRes = await orderApi.createPayment(orderId.value)
    
    if (paymentRes.code === 200) {
      const paymentParams = paymentRes.data.data.paymentParams
      
      // 调用微信支付
      uni.requestPayment({
        provider: 'wxpay',
        timeStamp: paymentParams.timeStamp,
        nonceStr: paymentParams.nonceStr,
        package: paymentParams.package,
        signType: paymentParams.signType,
        paySign: paymentParams.paySign,
        success: () => {
          uni.showToast({
            title: '支付成功',
            icon: 'success'
          })
          
          setTimeout(() => {
            uni.redirectTo({
              url: `/pages/order/detail?id=${orderId.value}`
            })
          }, 1500)
        },
        fail: (err) => {
          console.error('支付失败', err)
          uni.showToast({
            title: '支付失败',
            icon: 'none'
          })
        }
      })
    }
  } catch (error) {
    console.error('支付错误', error)
    uni.showToast({
      title: '支付失败',
      icon: 'none'
    })
  } finally {
    paying.value = false
  }
}
</script>

<style lang="scss" scoped>
.payment-container {
  min-height: 100vh;
  background-color: #F8F8F8;
  padding-bottom: 120rpx;
}

.order-info-section {
  background: #ffffff;
  padding: 40rpx;
  margin-bottom: 20rpx;
  text-align: center;
  
  .order-no {
    display: block;
    font-size: 28rpx;
    color: #666666;
    margin-bottom: 20rpx;
  }
  
  .order-amount {
    display: block;
    font-size: 60rpx;
    font-weight: bold;
    color: #FF3B30;
  }
}

.payment-section {
  background: #ffffff;
  padding: 40rpx;
  
  .payment-title {
    display: block;
    font-size: 32rpx;
    font-weight: bold;
    color: #333333;
    margin-bottom: 30rpx;
  }
  
  .payment-method {
    display: flex;
    align-items: center;
    padding: 30rpx;
    background: #F5F5F5;
    border-radius: 10rpx;
    
    &.active {
      background: #E8F4FF;
      border: 2rpx solid #007AFF;
    }
    
    .payment-icon {
      width: 60rpx;
      height: 60rpx;
      margin-right: 20rpx;
    }
    
    .payment-name {
      flex: 1;
      font-size: 32rpx;
      color: #333333;
    }
    
    .payment-check {
      font-size: 40rpx;
      color: #007AFF;
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
  
  .pay-btn {
    width: 100%;
    height: 88rpx;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #ffffff;
    border-radius: 44rpx;
    font-size: 32rpx;
    font-weight: bold;
    border: none;
  }
}
</style>


