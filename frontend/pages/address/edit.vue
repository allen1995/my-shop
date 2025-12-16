<template>
  <view class="address-edit-container">
    <view class="form-section">
      <view class="form-item">
        <text class="form-label">收货人</text>
        <input 
          class="form-input" 
          v-model="formData.receiverName" 
          placeholder="请输入收货人姓名"
          maxlength="50"
        />
      </view>
      
      <view class="form-item">
        <text class="form-label">手机号</text>
        <input 
          class="form-input" 
          v-model="formData.receiverPhone" 
          placeholder="请输入手机号"
          type="number"
          maxlength="11"
        />
      </view>
      
      <view class="form-item">
        <text class="form-label">所在地区</text>
        <picker 
          mode="multiSelector" 
          :value="pickerValue" 
          :range="pickerRange"
          :range-key="['name', 'name', 'name']"
          @change="onRegionChange"
        >
          <view class="picker-view">
            <text v-if="formData.province">{{ formData.province }}{{ formData.city }}{{ formData.district }}</text>
            <text v-else class="placeholder">请选择省市区</text>
          </view>
        </picker>
      </view>
      
      <view class="form-item">
        <text class="form-label">详细地址</text>
        <textarea 
          class="form-textarea" 
          v-model="formData.detailAddress" 
          placeholder="请输入详细地址"
          maxlength="200"
        />
      </view>
      
      <view class="form-item checkbox-item">
        <text class="form-label">设为默认地址</text>
        <switch 
          :checked="formData.isDefault" 
          @change="onDefaultChange"
          color="#667eea"
        />
      </view>
    </view>
    
    <view class="save-button-section">
      <button class="save-btn" @click="handleSave" :disabled="!canSave">保存</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { addressApi } from '@/api/address'
import { onLoad } from '@dcloudio/uni-app'

const addressId = ref(null)
const formData = ref({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
})

// 省市区数据（简化版，实际应该从后端获取或使用第三方库）
const provinces = [
  { name: '北京市', cities: [{ name: '北京市', districts: [{ name: '东城区' }, { name: '西城区' }, { name: '朝阳区' }] }] },
  { name: '上海市', cities: [{ name: '上海市', districts: [{ name: '黄浦区' }, { name: '徐汇区' }, { name: '长宁区' }] }] },
  { name: '广东省', cities: [
    { name: '广州市', districts: [{ name: '越秀区' }, { name: '海珠区' }, { name: '天河区' }] },
    { name: '深圳市', districts: [{ name: '罗湖区' }, { name: '福田区' }, { name: '南山区' }] }
  ] }
]

const pickerValue = ref([0, 0, 0])
const pickerRange = computed(() => {
  const provinceList = provinces.map(p => p.name)
  const cityList = provinces[pickerValue.value[0]]?.cities.map(c => c.name) || []
  const districtList = provinces[pickerValue.value[0]]?.cities[pickerValue.value[1]]?.districts.map(d => d.name) || []
  return [provinceList, cityList, districtList]
})

const canSave = computed(() => {
  return formData.value.receiverName.trim() &&
         formData.value.receiverPhone.trim() &&
         formData.value.province &&
         formData.value.city &&
         formData.value.district &&
         formData.value.detailAddress.trim()
})

onLoad(async (options) => {
  if (options.id) {
    addressId.value = options.id
    await loadAddress()
  }
})

const loadAddress = async () => {
  try {
    const res = await addressApi.getAddress(addressId.value)
    if (res.code === 200 && res.data) {
      formData.value = {
        receiverName: res.data.receiverName,
        receiverPhone: res.data.receiverPhone,
        province: res.data.province,
        city: res.data.city,
        district: res.data.district,
        detailAddress: res.data.detailAddress,
        isDefault: res.data.isDefault
      }
      
      // 设置picker值（简化处理）
      const provinceIndex = provinces.findIndex(p => p.name === res.data.province)
      if (provinceIndex >= 0) {
        const cityIndex = provinces[provinceIndex].cities.findIndex(c => c.name === res.data.city)
        if (cityIndex >= 0) {
          const districtIndex = provinces[provinceIndex].cities[cityIndex].districts.findIndex(d => d.name === res.data.district)
          if (districtIndex >= 0) {
            pickerValue.value = [provinceIndex, cityIndex, districtIndex]
          }
        }
      }
    }
  } catch (error) {
    console.error('加载地址失败', error)
  }
}

const onRegionChange = (e) => {
  const [provinceIndex, cityIndex, districtIndex] = e.detail.value
  pickerValue.value = [provinceIndex, cityIndex, districtIndex]
  
  formData.value.province = provinces[provinceIndex].name
  formData.value.city = provinces[provinceIndex].cities[cityIndex].name
  formData.value.district = provinces[provinceIndex].cities[cityIndex].districts[districtIndex].name
}

const onDefaultChange = (e) => {
  formData.value.isDefault = e.detail.value
}

const handleSave = async () => {
  if (!canSave.value) {
    uni.showToast({
      title: '请填写完整信息',
      icon: 'none'
    })
    return
  }
  
  // 手机号验证
  if (!formData.value.receiverPhone.match(/^1[3-9]\d{9}$/)) {
    uni.showToast({
      title: '手机号格式不正确',
      icon: 'none'
    })
    return
  }
  
  try {
    console.log('保存地址 - addressId:', addressId.value, 'formData:', formData.value)
    
    let res
    if (addressId.value) {
      res = await addressApi.updateAddress(addressId.value, formData.value)
    } else {
      res = await addressApi.createAddress(formData.value)
    }
    
    console.log('地址保存响应:', res)
    
    if (res.code === 200) {
      // 验证返回的地址数据
      if (res.data) {
        console.log('保存成功，返回的地址数据:', res.data)
        console.log('地址字段验证:', {
          id: res.data.id,
          receiverName: res.data.receiverName,
          receiverPhone: res.data.receiverPhone,
          province: res.data.province,
          city: res.data.city,
          district: res.data.district,
          detailAddress: res.data.detailAddress,
          isDefault: res.data.isDefault
        })
      }
      
      uni.showToast({
        title: '保存成功',
        icon: 'success'
      })
      
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    } else {
      console.error('地址保存失败:', res.message)
      uni.showToast({
        title: res.message || '保存失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('地址保存异常:', error)
    uni.showToast({
      title: error.message || '保存失败',
      icon: 'none'
    })
  }
}
</script>

<style lang="scss" scoped>
.address-edit-container {
  min-height: 100vh;
  background-color: #F8F8F8;
  padding-bottom: 200rpx;
}

.form-section {
  background: #ffffff;
  margin: 20rpx;
  border-radius: 20rpx;
  padding: 30rpx;
  
  .form-item {
    display: flex;
    align-items: center;
    padding: 30rpx 0;
    border-bottom: 1rpx solid #F5F5F5;
    
    &:last-child {
      border-bottom: none;
    }
    
    &.checkbox-item {
      justify-content: space-between;
    }
    
    .form-label {
      width: 160rpx;
      font-size: 28rpx;
      color: #333333;
      flex-shrink: 0;
    }
    
    .form-input {
      flex: 1;
      font-size: 28rpx;
      color: #333333;
    }
    
    .form-textarea {
      flex: 1;
      font-size: 28rpx;
      color: #333333;
      min-height: 120rpx;
    }
    
    .picker-view {
      flex: 1;
      font-size: 28rpx;
      color: #333333;
      
      .placeholder {
        color: #CCCCCC;
      }
    }
  }
}

.save-button-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 30rpx 40rpx;
  background: #ffffff;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);
  
  .save-btn {
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

