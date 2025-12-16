<template>
  <view class="order-list-container">
    <view class="status-tabs">
      <view 
        class="status-tab" 
        :class="{ active: selectedStatus === null }"
        @click="selectStatus(null)"
      >
        <text>全部</text>
      </view>
      <view 
        class="status-tab" 
        v-for="status in statusOptions" 
        :key="status.value"
        :class="{ active: selectedStatus === status.value }"
        @click="selectStatus(status.value)"
      >
        <text>{{ status.label }}</text>
      </view>
    </view>
    
    <view class="orders-list" v-if="orders.length > 0">
      <view 
        class="order-card" 
        v-for="order in orders" 
        :key="order.id"
        @click="goToDetail(order.id)"
      >
        <view class="order-header">
          <text class="order-no">订单号：{{ order.orderNo }}</text>
          <text class="order-status" :class="getStatusClass(order.status)">
            {{ getStatusText(order.status) }}
          </text>
        </view>
        
        <view class="order-items">
          <view 
            class="order-item" 
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
              <text class="item-desc">{{ item.color }} {{ item.size }} x{{ item.quantity }}</text>
              <text class="item-price">¥{{ item.price }}</text>
            </view>
          </view>
        </view>
        
        <view class="order-footer">
          <text class="order-time">{{ formatTime(order.createTime) }}</text>
          <text class="order-total">合计：¥{{ order.totalAmount }}</text>
        </view>
      </view>
    </view>
    
    <view class="empty-state" v-else>
      <text class="empty-text">暂无订单</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { orderApi } from '@/api/order'
import { onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'

const orders = ref([])
const page = ref(0)
const size = ref(20)
const loading = ref(false)
const hasMore = ref(true)
const selectedStatus = ref(null)

const statusOptions = [
  { label: '待支付', value: 'PENDING_PAYMENT' },
  { label: '待发货', value: 'PROCESSING' },
  { label: '待收货', value: 'SHIPPED' },
  { label: '已完成', value: 'COMPLETED' }
]

const loadOrders = async (reset = false) => {
  if (loading.value) return
  
  loading.value = true
  
  try {
    if (reset) {
      page.value = 0
      orders.value = []
    }
    
    const params = {
      page: page.value,
      size: size.value
    }
    
    if (selectedStatus.value) {
      params.status = selectedStatus.value
    }
    
    const res = await orderApi.getOrders(params)
    
    if (res.code === 200 && res.data) {
      const newOrders = res.data.content || []
      orders.value = reset ? newOrders : [...orders.value, ...newOrders]
      hasMore.value = newOrders.length === size.value
      page.value++
    }
  } catch (error) {
    console.error('加载订单失败', error)
  } finally {
    loading.value = false
    uni.stopPullDownRefresh()
  }
}

const selectStatus = (status) => {
  selectedStatus.value = status
  loadOrders(true)
}

const goToDetail = (orderId) => {
  uni.navigateTo({
    url: `/pages/order/detail?id=${orderId}`
  })
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

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadOrders(true)
})

onPullDownRefresh(() => {
  loadOrders(true)
})

onReachBottom(() => {
  if (hasMore.value) {
    loadOrders()
  }
})
</script>

<style lang="scss" scoped>
.order-list-container {
  min-height: 100vh;
  background-color: #F8F8F8;
  padding-bottom: 20rpx;
}

.status-tabs {
  background: #ffffff;
  padding: 20rpx;
  display: flex;
  gap: 15rpx;
  overflow-x: auto;
  white-space: nowrap;
  margin-bottom: 20rpx;
  
  .status-tab {
    padding: 10rpx 30rpx;
    background: #F5F5F5;
    border-radius: 30rpx;
    font-size: 26rpx;
    color: #666666;
    transition: all 0.3s;
    flex-shrink: 0;
    
    &.active {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: #ffffff;
    }
    
    text {
      display: block;
    }
  }
}

.orders-list {
  padding: 0 20rpx;
  
  .order-card {
    background: #ffffff;
    border-radius: 20rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
    
    .order-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20rpx;
      padding-bottom: 20rpx;
      border-bottom: 1rpx solid #E5E5E5;
      
      .order-no {
        font-size: 26rpx;
        color: #666666;
      }
      
      .order-status {
        font-size: 26rpx;
        font-weight: bold;
        
        &.status-pending {
          color: #FF9500;
        }
        
        &.status-paid {
          color: #007AFF;
        }
        
        &.status-processing {
          color: #5AC8FA;
        }
        
        &.status-shipped {
          color: #34C759;
        }
        
        &.status-completed {
          color: #34C759;
        }
        
        &.status-cancelled {
          color: #999999;
        }
      }
    }
    
    .order-items {
      margin-bottom: 20rpx;
      
      .order-item {
        display: flex;
        align-items: center;
        margin-bottom: 15rpx;
        
        .item-image {
          width: 120rpx;
          height: 120rpx;
          border-radius: 10rpx;
          margin-right: 20rpx;
        }
        
        .item-info {
          flex: 1;
          display: flex;
          justify-content: space-between;
          align-items: center;
          
          .item-desc {
            font-size: 28rpx;
            color: #333333;
          }
          
          .item-price {
            font-size: 28rpx;
            font-weight: bold;
            color: #FF3B30;
          }
        }
      }
    }
    
    .order-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-top: 20rpx;
      border-top: 1rpx solid #E5E5E5;
      
      .order-time {
        font-size: 24rpx;
        color: #999999;
      }
      
      .order-total {
        font-size: 32rpx;
        font-weight: bold;
        color: #FF3B30;
      }
    }
  }
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 200rpx 40rpx;
  
  .empty-text {
    font-size: 32rpx;
    color: #999999;
  }
}
</style>

