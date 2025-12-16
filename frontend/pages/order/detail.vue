<template>
  <view class="detail-container">
    <view class="status-section">
      <view class="status-info">
        <text class="status-text" :class="getStatusClass(order.status)">
          {{ getStatusText(order.status) }}
        </text>
        <text class="status-desc">{{ getStatusDesc(order.status) }}</text>
      </view>
      
      <view class="status-timeline" v-if="order.status !== 'PENDING_PAYMENT'">
        <view class="timeline-item" :class="{ active: isStatusReached('PENDING_PAYMENT') }">
          <view class="timeline-dot"></view>
          <text class="timeline-text">待支付</text>
        </view>
        <view class="timeline-item" :class="{ active: isStatusReached('PAID') || isStatusReached('PROCESSING') }">
          <view class="timeline-dot"></view>
          <text class="timeline-text">已支付</text>
        </view>
        <view class="timeline-item" :class="{ active: isStatusReached('PROCESSING') || isStatusReached('SHIPPED') }">
          <view class="timeline-dot"></view>
          <text class="timeline-text">待发货</text>
        </view>
        <view class="timeline-item" :class="{ active: isStatusReached('SHIPPED') || isStatusReached('COMPLETED') }">
          <view class="timeline-dot"></view>
          <text class="timeline-text">待收货</text>
        </view>
        <view class="timeline-item" :class="{ active: isStatusReached('COMPLETED') }">
          <view class="timeline-dot"></view>
          <text class="timeline-text">已完成</text>
        </view>
      </view>
    </view>
    
    <view class="order-info-section">
      <view class="section-title">订单信息</view>
      <view class="info-item">
        <text class="info-label">订单号</text>
        <text class="info-value">{{ order.orderNo }}</text>
      </view>
      <view class="info-item">
        <text class="info-label">创建时间</text>
        <text class="info-value">{{ formatTime(order.createTime) }}</text>
      </view>
      <view class="info-item" v-if="order.paymentTime">
        <text class="info-label">支付时间</text>
        <text class="info-value">{{ formatTime(order.paymentTime) }}</text>
      </view>
    </view>
    
    <view class="items-section">
      <view class="section-title">商品信息</view>
      <view 
        class="item-card" 
        v-for="(item, index) in order.items" 
        :key="index"
      >
        <image 
          v-if="item.previewImageUrl" 
          class="item-image" 
          :src="item.previewImageUrl" 
          mode="aspectFill"
        ></image>
        <view class="item-info">
          <text class="item-spec">{{ item.color }} {{ item.size }}</text>
          <text class="item-quantity">数量：{{ item.quantity }}</text>
          <text class="item-price">¥{{ item.price }}</text>
        </view>
      </view>
    </view>
    
    <view class="amount-section">
      <view class="section-title">费用明细</view>
      <view class="amount-item">
        <text class="amount-label">商品总额</text>
        <text class="amount-value">¥{{ order.totalAmount }}</text>
      </view>
      <view class="amount-total">
        <text class="total-label">实付金额</text>
        <text class="total-value">¥{{ order.totalAmount }}</text>
      </view>
    </view>
    
    <view class="action-section">
      <button 
        v-if="order.status === 'PENDING_PAYMENT'" 
        class="action-btn cancel-btn" 
        @click="handleCancel"
      >
        取消订单
      </button>
      <button 
        v-if="order.status === 'SHIPPED'" 
        class="action-btn confirm-btn" 
        @click="handleConfirm"
      >
        确认收货
      </button>
      <button 
        v-if="order.status === 'PENDING_PAYMENT'" 
        class="action-btn pay-btn" 
        @click="handlePay"
      >
        立即支付
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { orderApi } from '@/api/order'
import { onLoad } from '@dcloudio/uni-app'

const order = ref({})
const orderId = ref(null)

onLoad(async (options) => {
  orderId.value = options.id
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
    uni.showToast({
      title: '加载失败',
      icon: 'none'
    })
  }
}

const getStatusText = (status) => {
  const statusMap = {
    'PENDING_PAYMENT': '待支付',
    'PAID': '已支付',
    'PROCESSING': '待发货',
    'SHIPPED': '待收货',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

const getStatusDesc = (status) => {
  const descMap = {
    'PENDING_PAYMENT': '请尽快完成支付',
    'PAID': '订单已支付，等待发货',
    'PROCESSING': '商家正在准备发货',
    'SHIPPED': '商品已发货，请注意查收',
    'COMPLETED': '订单已完成',
    'CANCELLED': '订单已取消'
  }
  return descMap[status] || ''
}

const getStatusClass = (status) => {
  const classMap = {
    'PENDING_PAYMENT': 'status-pending',
    'PAID': 'status-paid',
    'PROCESSING': 'status-processing',
    'SHIPPED': 'status-shipped',
    'COMPLETED': 'status-completed',
    'CANCELLED': 'status-cancelled'
  }
  return classMap[status] || ''
}

const isStatusReached = (targetStatus) => {
  const statusOrder = ['PENDING_PAYMENT', 'PAID', 'PROCESSING', 'SHIPPED', 'COMPLETED']
  const currentIndex = statusOrder.indexOf(order.value.status)
  const targetIndex = statusOrder.indexOf(targetStatus)
  return currentIndex >= targetIndex
}

const handleCancel = () => {
  uni.showModal({
    title: '确认取消',
    content: '确定要取消这个订单吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          const cancelRes = await orderApi.cancelOrder(orderId.value)
          if (cancelRes.code === 200) {
            uni.showToast({
              title: '取消成功',
              icon: 'success'
            })
            await loadOrderDetail()
          } else {
            uni.showToast({
              title: cancelRes.message || '取消失败',
              icon: 'none'
            })
          }
        } catch (error) {
          uni.showToast({
            title: '取消失败',
            icon: 'none'
          })
        }
      }
    }
  })
}

const handleConfirm = () => {
  uni.showModal({
    title: '确认收货',
    content: '确认已收到商品？',
    success: async (res) => {
      if (res.confirm) {
        try {
          const confirmRes = await orderApi.confirmReceipt(orderId.value)
          if (confirmRes.code === 200) {
            uni.showToast({
              title: '确认成功',
              icon: 'success'
            })
            await loadOrderDetail()
          } else {
            uni.showToast({
              title: confirmRes.message || '确认失败',
              icon: 'none'
            })
          }
        } catch (error) {
          uni.showToast({
            title: '确认失败',
            icon: 'none'
          })
        }
      }
    }
  })
}

const handlePay = () => {
  uni.navigateTo({
    url: `/pages/order/payment?orderId=${orderId.value}`
  })
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN')
}
</script>

<style lang="scss" scoped>
.detail-container {
  min-height: 100vh;
  background-color: #F8F8F8;
  padding-bottom: 200rpx;
}

.status-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx;
  color: #ffffff;
  margin-bottom: 20rpx;
  
  .status-info {
    margin-bottom: 30rpx;
    
    .status-text {
      display: block;
      font-size: 48rpx;
      font-weight: bold;
      margin-bottom: 10rpx;
    }
    
    .status-desc {
      display: block;
      font-size: 26rpx;
      opacity: 0.9;
    }
  }
  
  .status-timeline {
    display: flex;
    justify-content: space-between;
    padding-top: 30rpx;
    border-top: 1rpx solid rgba(255, 255, 255, 0.3);
    
    .timeline-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      flex: 1;
      opacity: 0.5;
      
      &.active {
        opacity: 1;
      }
      
      .timeline-dot {
        width: 16rpx;
        height: 16rpx;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.5);
        margin-bottom: 10rpx;
      }
      
      &.active .timeline-dot {
        background: #ffffff;
        box-shadow: 0 0 10rpx rgba(255, 255, 255, 0.8);
      }
      
      .timeline-text {
        font-size: 22rpx;
      }
    }
  }
}

.order-info-section,
.items-section,
.amount-section {
  background: #ffffff;
  padding: 30rpx;
  margin-bottom: 20rpx;
  
  .section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333333;
    margin-bottom: 20rpx;
  }
  
  .info-item {
    display: flex;
    justify-content: space-between;
    padding: 15rpx 0;
    border-bottom: 1rpx solid #F5F5F5;
    
    &:last-child {
      border-bottom: none;
    }
    
    .info-label {
      font-size: 28rpx;
      color: #666666;
    }
    
    .info-value {
      font-size: 28rpx;
      color: #333333;
    }
  }
}

.items-section {
  .item-card {
    display: flex;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #F5F5F5;
    
    &:last-child {
      border-bottom: none;
    }
    
    .item-image {
      width: 120rpx;
      height: 120rpx;
      border-radius: 10rpx;
      margin-right: 20rpx;
    }
    
    .item-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      
      .item-spec {
        font-size: 28rpx;
        color: #333333;
        margin-bottom: 10rpx;
      }
      
      .item-quantity {
        font-size: 24rpx;
        color: #999999;
        margin-bottom: 10rpx;
      }
      
      .item-price {
        font-size: 32rpx;
        font-weight: bold;
        color: #FF3B30;
      }
    }
  }
}

.amount-section {
  .amount-item {
    display: flex;
    justify-content: space-between;
    padding: 15rpx 0;
    
    .amount-label {
      font-size: 28rpx;
      color: #666666;
    }
    
    .amount-value {
      font-size: 28rpx;
      color: #333333;
    }
  }
  
  .amount-total {
    display: flex;
    justify-content: space-between;
    padding: 20rpx 0;
    margin-top: 20rpx;
    border-top: 1rpx solid #E5E5E5;
    
    .total-label {
      font-size: 32rpx;
      font-weight: bold;
      color: #333333;
    }
    
    .total-value {
      font-size: 36rpx;
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
  display: flex;
  gap: 20rpx;
  
  .action-btn {
    flex: 1;
    height: 88rpx;
    border-radius: 44rpx;
    font-size: 32rpx;
    font-weight: bold;
    border: none;
    
    &.cancel-btn {
      background: #F5F5F5;
      color: #666666;
    }
    
    &.confirm-btn {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: #ffffff;
    }
    
    &.pay-btn {
      background: linear-gradient(135deg, #FF9500 0%, #FF6B00 100%);
      color: #ffffff;
    }
  }
}
</style>

