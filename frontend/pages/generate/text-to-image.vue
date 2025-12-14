<template>
  <view class="generate-container">
    <view class="input-section">
      <textarea 
        class="prompt-input" 
        v-model="prompt" 
        placeholder="描述你想要生成的图片，例如：一只可爱的小猫，卡通风格，粉色背景"
        maxlength="500"
      ></textarea>
      <text class="char-count">{{ prompt.length }}/500</text>
    </view>
    
    <view class="params-section">
      <view class="param-item">
        <text class="param-label">图片尺寸</text>
        <view class="param-options">
          <view 
            class="param-option" 
            :class="{ active: size === 'square' }"
            @click="size = 'square'"
          >正方形</view>
          <view 
            class="param-option" 
            :class="{ active: size === 'landscape' }"
            @click="size = 'landscape'"
          >横版</view>
          <view 
            class="param-option" 
            :class="{ active: size === 'portrait' }"
            @click="size = 'portrait'"
          >竖版</view>
        </view>
      </view>
      
      <view class="param-item">
        <text class="param-label">风格</text>
        <view class="param-options">
          <view 
            class="param-option" 
            :class="{ active: style === 'realistic' }"
            @click="style = 'realistic'"
          >写实</view>
          <view 
            class="param-option" 
            :class="{ active: style === 'illustration' }"
            @click="style = 'illustration'"
          >插画</view>
          <view 
            class="param-option" 
            :class="{ active: style === 'abstract' }"
            @click="style = 'abstract'"
          >抽象</view>
          <view 
            class="param-option" 
            :class="{ active: style === 'watercolor' }"
            @click="style = 'watercolor'"
          >水彩</view>
        </view>
      </view>
    </view>
    
    <view class="action-section">
      <button 
        class="generate-btn" 
        :disabled="!canGenerate" 
        :loading="generating"
        @click="handleGenerate"
      >
        {{ generating ? '生成中...' : '开始生成' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { imageGenerationApi } from '@/api/imageGeneration'

const prompt = ref('')
const size = ref('square')
const style = ref('realistic')
const generating = ref(false)

const canGenerate = computed(() => {
  return prompt.value.trim().length > 0 && !generating.value
})

const handleGenerate = async () => {
  if (!canGenerate.value) return
  
  generating.value = true
  
  try {
    const res = await imageGenerationApi.textToImage({
      prompt: prompt.value,
      parameters: {
        size: size.value,
        style: style.value
      }
    })
    
    if (res.code === 200 && res.data) {
      const taskId = res.data.taskId
      
      if (!taskId) {
        uni.showToast({
          title: '生成失败：未获取到任务ID',
          icon: 'none'
        })
        return
      }
      
      // 跳转到生成进度页
      uni.navigateTo({
        url: `/pages/generate/generating?taskId=${taskId}`
      })
    } else {
      uni.showToast({
        title: res.message || '生成失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('生成失败', error)
    uni.showToast({
      title: '生成失败，请重试',
      icon: 'none'
    })
  } finally {
    generating.value = false
  }
}
</script>

<style lang="scss" scoped>
.generate-container {
  padding: 40rpx;
  min-height: 100vh;
  background-color: #F8F8F8;
}

.input-section {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  
  .prompt-input {
    width: 100%;
    min-height: 300rpx;
    font-size: 28rpx;
    line-height: 1.6;
    color: #333333;
  }
  
  .char-count {
    display: block;
    text-align: right;
    font-size: 24rpx;
    color: #999999;
    margin-top: 20rpx;
  }
}

.params-section {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  
  .param-item {
    margin-bottom: 40rpx;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .param-label {
      display: block;
      font-size: 32rpx;
      font-weight: bold;
      color: #333333;
      margin-bottom: 20rpx;
    }
    
    .param-options {
      display: flex;
      gap: 20rpx;
      flex-wrap: wrap;
      
      .param-option {
        padding: 20rpx 40rpx;
        background: #F5F5F5;
        border-radius: 10rpx;
        font-size: 28rpx;
        color: #666666;
        
        &.active {
          background: #007AFF;
          color: #ffffff;
        }
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
  
  .generate-btn {
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


