<template>
  <view class="address-list-container">
    <view class="address-list" v-if="addresses.length > 0">
      <view 
        class="address-card" 
        v-for="address in addresses" 
        :key="address.id"
        :class="{ 'default': address.isDefault, 'select-mode': isSelectMode }"
        @click="handleSelectAddress(address)"
      >
        <view class="address-header">
          <text class="receiver-name">{{ address.receiverName || '' }}</text>
          <text class="receiver-phone">{{ address.receiverPhone || '' }}</text>
          <view class="default-badge" v-if="address.isDefault">默认</view>
        </view>
        
        <view class="address-content">
          <text class="address-text">
            {{ (address.province || '') }}{{ (address.city || '') }}{{ (address.district || '') }}{{ (address.detailAddress || '') }}
          </text>
        </view>
        
        <view class="address-actions" v-if="!isSelectMode">
          <view class="action-item" @click.stop="handleEdit(address)">
            <text class="action-text">编辑</text>
          </view>
          <view class="action-item" @click.stop="handleDelete(address)" v-if="!address.isDefault">
            <text class="action-text delete">删除</text>
          </view>
          <view class="action-item" @click.stop="handleSetDefault(address)" v-if="!address.isDefault">
            <text class="action-text">设为默认</text>
          </view>
        </view>
      </view>
    </view>
    
    <view class="empty-state" v-else>
      <text class="empty-text">暂无收货地址</text>
      <text class="empty-hint">点击下方按钮添加地址</text>
    </view>
    
    <view class="add-button-section">
      <button class="add-btn" @click="handleAdd">+ 添加新地址</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { addressApi } from '@/api/address'
import { onShow, onLoad } from '@dcloudio/uni-app'

const addresses = ref([])
const isSelectMode = ref(false) // 是否为选择模式

const loadAddresses = async () => {
  try {
    const res = await addressApi.getAddresses()
    console.log('地址列表API响应:', res)
    if (res.code === 200 && res.data) {
      // 确保data是数组
      const addressList = Array.isArray(res.data) ? res.data : [res.data]
      console.log('地址列表数据:', addressList)
      // 验证每个地址对象的字段
      addressList.forEach((addr, index) => {
        console.log(`地址${index}:`, {
          id: addr.id,
          receiverName: addr.receiverName,
          receiverPhone: addr.receiverPhone,
          province: addr.province,
          city: addr.city,
          district: addr.district,
          detailAddress: addr.detailAddress,
          isDefault: addr.isDefault
        })
      })
      // 确保每个地址对象都有所有必需的字段
      addresses.value = addressList.map(addr => ({
        id: addr.id,
        receiverName: addr.receiverName || '',
        receiverPhone: addr.receiverPhone || '',
        province: addr.province || '',
        city: addr.city || '',
        district: addr.district || '',
        detailAddress: addr.detailAddress || '',
        isDefault: addr.isDefault || false
      }))
    } else {
      console.warn('地址列表响应异常:', res)
    }
  } catch (error) {
    console.error('加载地址列表失败', error)
  }
}

onLoad((options) => {
  // 检查是否为选择模式
  if (options.select === 'true') {
    isSelectMode.value = true
    // 设置页面标题
    uni.setNavigationBarTitle({
      title: '选择收货地址'
    })
  }
})

const handleAdd = () => {
  uni.navigateTo({
    url: '/pages/address/edit'
  })
}

// 选择地址（选择模式）
const handleSelectAddress = (address) => {
  if (isSelectMode.value) {
    // 返回上一页并传递选中的地址ID
    const pages = getCurrentPages()
    if (pages.length > 1) {
      const prevPage = pages[pages.length - 2]
      // 使用事件总线或全局数据传递选中的地址
      getApp().globalData = getApp().globalData || {}
      getApp().globalData.selectedAddress = address
      uni.navigateBack()
    }
  }
}

const handleEdit = (address) => {
  uni.navigateTo({
    url: `/pages/address/edit?id=${address.id}`
  })
}

const handleDelete = (address) => {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这个地址吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          const deleteRes = await addressApi.deleteAddress(address.id)
          if (deleteRes.code === 200) {
            uni.showToast({
              title: '删除成功',
              icon: 'success'
            })
            loadAddresses()
          } else {
            uni.showToast({
              title: deleteRes.message || '删除失败',
              icon: 'none'
            })
          }
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

const handleSetDefault = async (address) => {
  try {
    const res = await addressApi.setDefaultAddress(address.id)
    if (res.code === 200) {
      uni.showToast({
        title: '设置成功',
        icon: 'success'
      })
      loadAddresses()
    } else {
      uni.showToast({
        title: res.message || '设置失败',
        icon: 'none'
      })
    }
  } catch (error) {
    uni.showToast({
      title: '设置失败',
      icon: 'none'
    })
  }
}

onMounted(() => {
  loadAddresses()
})

onShow(() => {
  loadAddresses()
})
</script>

<style lang="scss" scoped>
.address-list-container {
  min-height: 100vh;
  background-color: #F8F8F8;
  padding-bottom: 200rpx;
}

.address-list {
  padding: 20rpx;
  
  .address-card {
    background: #ffffff;
    border-radius: 20rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
    border: 2rpx solid transparent;
    
    &.default {
      border-color: #667eea;
    }
    
    &.select-mode {
      cursor: pointer;
      
      &:active {
        background: #F5F5F5;
      }
    }
    
    .address-header {
      display: flex;
      align-items: center;
      margin-bottom: 20rpx;
      
      .receiver-name {
        font-size: 32rpx;
        font-weight: bold;
        color: #333333;
        margin-right: 20rpx;
      }
      
      .receiver-phone {
        font-size: 28rpx;
        color: #666666;
        margin-right: 20rpx;
      }
      
      .default-badge {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: #ffffff;
        font-size: 22rpx;
        padding: 4rpx 12rpx;
        border-radius: 10rpx;
      }
    }
    
    .address-content {
      margin-bottom: 20rpx;
      
      .address-text {
        font-size: 28rpx;
        color: #666666;
        line-height: 1.6;
      }
    }
    
    .address-actions {
      display: flex;
      gap: 30rpx;
      padding-top: 20rpx;
      border-top: 1rpx solid #F5F5F5;
      
      .action-item {
        .action-text {
          font-size: 26rpx;
          color: #007AFF;
          
          &.delete {
            color: #FF3B30;
          }
        }
      }
    }
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 40rpx;
  
  .empty-text {
    font-size: 32rpx;
    color: #999999;
    margin-bottom: 20rpx;
  }
  
  .empty-hint {
    font-size: 26rpx;
    color: #CCCCCC;
  }
}

.add-button-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 30rpx 40rpx;
  background: #ffffff;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);
  
  .add-btn {
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

