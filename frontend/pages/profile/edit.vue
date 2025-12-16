<template>
  <view class="edit-profile-container">
    <view class="form-section">
      <view class="form-item">
        <text class="form-label">头像</text>
        <view class="avatar-section">
          <image 
            class="avatar-preview" 
            :src="formData.avatarUrl || '/static/default-avatar.png'" 
            mode="aspectFill"
            @click="chooseAvatar"
          ></image>
          <text class="avatar-hint">点击更换头像</text>
        </view>
      </view>
      
      <view class="form-item">
        <text class="form-label">昵称</text>
        <input 
          class="form-input" 
          v-model="formData.nickName" 
          placeholder="请输入昵称"
          maxlength="50"
        />
      </view>
      
      <view class="form-item">
        <text class="form-label">手机号</text>
        <input 
          class="form-input" 
          v-model="formData.phone" 
          placeholder="请输入手机号"
          type="number"
          maxlength="11"
        />
      </view>
    </view>
    
    <view class="action-section">
      <button class="save-btn" @click="handleSave" :loading="saving">
        {{ saving ? '保存中...' : '保存' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { userApi } from '@/api/user'
import { useUserStore } from '@/store/user'
import { imageGenerationApi } from '@/api/imageGeneration'
import { onLoad } from '@dcloudio/uni-app'

const userStore = useUserStore()
const formData = ref({
  nickName: '',
  avatarUrl: '',
  phone: ''
})
const saving = ref(false)

onLoad(async () => {
  await loadUserInfo()
})

const loadUserInfo = async () => {
  try {
    const res = await userApi.getProfile()
    if (res.code === 200 && res.data) {
      formData.value = {
        nickName: res.data.nickName || '',
        avatarUrl: res.data.avatarUrl || '',
        phone: res.data.phone || ''
      }
    }
  } catch (error) {
    console.error('加载用户信息失败', error)
    uni.showToast({
      title: '加载用户信息失败',
      icon: 'none'
    })
  }
}

const chooseAvatar = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const tempFilePath = res.tempFilePaths[0]
      
      uni.showLoading({
        title: '上传中...'
      })
      
      try {
        // 上传头像到OSS
        const uploadRes = await imageGenerationApi.uploadImage(tempFilePath)
        if (uploadRes.code === 200 && uploadRes.data && uploadRes.data.imageUrl) {
          formData.value.avatarUrl = uploadRes.data.imageUrl
          uni.hideLoading()
          uni.showToast({
            title: '头像已更新',
            icon: 'success'
          })
        } else {
          throw new Error(uploadRes.message || '上传失败')
        }
      } catch (error) {
        console.error('上传头像失败', error)
        uni.hideLoading()
        uni.showToast({
          title: error.message || '上传失败，请重试',
          icon: 'none'
        })
      }
    },
    fail: (err) => {
      console.error('选择图片失败', err)
      uni.showToast({
        title: '选择图片失败',
        icon: 'none'
      })
    }
  })
}

const handleSave = async () => {
  // 参数验证
  if (!formData.value.nickName || formData.value.nickName.trim().length === 0) {
    uni.showToast({
      title: '请输入昵称',
      icon: 'none'
    })
    return
  }
  
  if (formData.value.phone && !/^1[3-9]\d{9}$/.test(formData.value.phone)) {
    uni.showToast({
      title: '请输入正确的手机号',
      icon: 'none'
    })
    return
  }
  
  // 如果头像URL是临时路径，不保存（使用之前的头像或空）
  let avatarUrl = formData.value.avatarUrl
  if (avatarUrl && (avatarUrl.includes('__tmp__') || avatarUrl.includes('127.0.0.1'))) {
    console.warn('检测到临时路径，不保存头像:', avatarUrl)
    avatarUrl = null // 不更新头像，保持原有头像
  }
  
  saving.value = true
  
  try {
    const res = await userApi.updateProfile({
      nickName: formData.value.nickName.trim(),
      avatarUrl: avatarUrl,
      phone: formData.value.phone || null
    })
    
    if (res.code === 200) {
      uni.showToast({
        title: '保存成功',
        icon: 'success'
      })
      
      // 更新store中的用户信息
      if (res.data) {
        userStore.user = res.data
      }
      
      // 延迟返回上一页
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    } else {
      uni.showToast({
        title: res.message || '保存失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('保存用户信息失败', error)
    uni.showToast({
      title: '保存失败，请重试',
      icon: 'none'
    })
  } finally {
    saving.value = false
  }
}
</script>

<style lang="scss" scoped>
.edit-profile-container {
  min-height: 100vh;
  background-color: #F8F8F8;
  padding-bottom: 200rpx;
}

.form-section {
  background: #ffffff;
  margin: 20rpx;
  border-radius: 20rpx;
  padding: 40rpx;
  
  .form-item {
    margin-bottom: 40rpx;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .form-label {
      display: block;
      font-size: 28rpx;
      color: #333333;
      margin-bottom: 20rpx;
      font-weight: 500;
    }
    
    .form-input {
      width: 100%;
      height: 80rpx;
      padding: 0 20rpx;
      background: #F5F5F5;
      border-radius: 10rpx;
      font-size: 28rpx;
      color: #333333;
    }
    
    .avatar-section {
      display: flex;
      flex-direction: column;
      align-items: center;
      
      .avatar-preview {
        width: 200rpx;
        height: 200rpx;
        border-radius: 100rpx;
        border: 4rpx solid #E5E5E5;
        margin-bottom: 20rpx;
      }
      
      .avatar-hint {
        font-size: 24rpx;
        color: #999999;
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
  
  .save-btn {
    width: 100%;
    height: 88rpx;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #ffffff;
    border-radius: 44rpx;
    font-size: 32rpx;
    font-weight: bold;
    border: none;
    
    &[loading] {
      opacity: 0.7;
    }
  }
}
</style>

