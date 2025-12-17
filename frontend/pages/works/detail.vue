<template>
  <view class="detail-container">
    <view class="image-section">
      <image class="work-image" :src="work.imageUrl" mode="aspectFit" @click="previewImage"></image>
    </view>
    
    <view class="info-section">
      <text class="work-title">{{ work.title }}</text>
      <text class="work-description" v-if="work.description">{{ work.description }}</text>
      
      <view class="category-section">
        <view class="category-title">分类</view>
        <view class="category-content">
          <text class="category-value" v-if="work.category">{{ work.category }}</text>
          <text class="category-placeholder" v-else>未设置分类</text>
          <text class="category-edit-btn" @click="showCategoryModal">编辑</text>
        </view>
      </view>
      
      <view class="tags-section" v-if="tags.length > 0 || editingTags">
        <view class="tags-title">标签</view>
        <view class="tags-list">
          <view 
            class="tag-item" 
            v-for="(tag, index) in tags" 
            :key="index"
          >
            <text>{{ tag }}</text>
            <text class="tag-remove" @click="removeTag(tag)" v-if="editingTags">×</text>
          </view>
          <view class="tag-item add-tag" @click="showAddTagModal" v-if="editingTags">
            <text>+ 添加</text>
          </view>
        </view>
        <view class="tags-actions">
          <text class="tags-edit-btn" @click="toggleEditTags">
            {{ editingTags ? '完成' : '编辑标签' }}
          </text>
        </view>
      </view>
      
      <text class="work-time">创建时间：{{ formatTime(work.createTime) }}</text>
    </view>
    
    <view class="action-section">
      <button class="action-btn favorite-btn" :class="{ active: work.isFavorite }" @click="handleToggleFavorite">
        {{ work.isFavorite ? '⭐ 已收藏' : '☆ 收藏' }}
      </button>
      <button class="action-btn share-btn" @click="handleShare">分享作品</button>
      <button class="action-btn apply-btn" @click="handleApply">应用到包包</button>
      <button class="action-btn delete-btn" @click="handleDelete">删除作品</button>
    </view>
    
    <!-- 隐藏的Canvas用于生成海报 -->
    <canvas 
      canvas-id="share-poster-canvas" 
      :style="{ width: '750px', height: '1334px', position: 'fixed', left: '-9999px', top: '-9999px' }"
    ></canvas>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { workApi } from '@/api/work'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { generatePoster, savePosterToAlbum } from '@/utils/poster'

const work = ref({})
const workId = ref(null)
const tags = ref([])
const editingTags = ref(false)
const allCategories = ref([]) // 所有分类列表

onLoad(async (options) => {
  workId.value = options.id
  await loadWorkDetail()
  // 不在页面加载时加载分类列表，避免超时
  // 分类列表将在用户点击"编辑分类"时按需加载
})

const loadWorkDetail = async () => {
  try {
    const res = await workApi.getWorkDetail(workId.value)
    if (res.code === 200 && res.data) {
      work.value = res.data
      // 解析tags JSON字符串
      if (work.value.tags) {
        try {
          tags.value = JSON.parse(work.value.tags)
        } catch (e) {
          tags.value = []
        }
      } else {
        tags.value = []
      }
    }
  } catch (error) {
    console.error('加载作品详情失败', error)
  }
}

// 加载所有分类（与作品集页面使用相同的数据源）
// 优化：使用分页加载，避免一次性加载过多数据
const loadAllCategories = async () => {
  try {
    const catSet = new Set()
    let page = 0
    const size = 50 // 每次加载50条，减少单次请求数据量
    let hasMore = true
    
    // 分页加载所有作品以提取分类
    while (hasMore) {
      const res = await workApi.getWorks({ page, size })
      if (res.code === 200 && res.data) {
        const worksList = res.data.content || []
        worksList.forEach(w => {
          if (w.category) {
            catSet.add(w.category)
          }
        })
        
        // 检查是否还有更多数据
        hasMore = !res.data.last && worksList.length === size
        page++
      } else {
        hasMore = false
      }
    }
    
    allCategories.value = Array.from(catSet).sort()
    console.log('加载所有分类:', allCategories.value)
  } catch (error) {
    console.error('加载分类列表失败', error)
    throw error // 抛出错误，让调用方处理
  }
}

const toggleEditTags = () => {
  editingTags.value = !editingTags.value
}

const showCategoryModal = async () => {
  // 如果分类列表为空，先加载分类列表
  if (allCategories.value.length === 0) {
    uni.showLoading({ title: '加载分类中...' })
    try {
      await loadAllCategories()
    } catch (error) {
      console.error('加载分类列表失败', error)
      uni.hideLoading()
      uni.showToast({
        title: '加载分类失败',
        icon: 'none'
      })
      return
    }
    uni.hideLoading()
  }
  
  // 构建操作列表：已有分类 + 自定义分类 + 清除分类
  const actionList = [...allCategories.value]
  actionList.push('+ 自定义分类')
  if (work.value.category) {
    actionList.push('清除分类')
  }
  
  uni.showActionSheet({
    itemList: actionList,
    success: async (res) => {
      const selectedIndex = res.tapIndex
      let newCategory = null
      
      // 如果选择的是已有分类
      if (selectedIndex < allCategories.value.length) {
        newCategory = allCategories.value[selectedIndex]
      } 
      // 如果选择的是"自定义分类"
      else if (actionList[selectedIndex] === '+ 自定义分类') {
        // 弹出输入框
        uni.showModal({
          title: '自定义分类',
          editable: true,
          placeholderText: '请输入分类名称',
          success: async (modalRes) => {
            if (modalRes.confirm && modalRes.content && modalRes.content.trim()) {
              const customCategory = modalRes.content.trim()
              await updateCategory(customCategory)
            }
          }
        })
        return
      }
      // 如果选择的是"清除分类"
      else if (actionList[selectedIndex] === '清除分类') {
        newCategory = null
      }
      
      if (newCategory !== undefined) {
        await updateCategory(newCategory)
      }
    }
  })
}

const updateCategory = async (category) => {
  try {
    const updateRes = await workApi.updateCategory(workId.value, category)
    if (updateRes.code === 200 && updateRes.data) {
      work.value = updateRes.data
      
      // 重新加载所有分类，确保与作品集页面数据一致
      await loadAllCategories()
      
      uni.showToast({
        title: category ? '分类设置成功' : '分类已清除',
        icon: 'success',
        duration: 1000
      })
    } else {
      uni.showToast({
        title: updateRes.message || '设置分类失败',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('设置分类失败', error)
    uni.showToast({
      title: '设置分类失败',
      icon: 'none'
    })
  }
}

onShow(() => {
  // 从其他页面返回时，重新加载作品详情（分类列表按需加载）
  if (workId.value) {
    loadWorkDetail()
  }
})

const showAddTagModal = () => {
  uni.showModal({
    title: '添加标签',
    editable: true,
    placeholderText: '请输入标签名称',
    success: async (res) => {
      if (res.confirm && res.content && res.content.trim()) {
        const newTag = res.content.trim()
        if (!tags.value.includes(newTag)) {
          try {
            const addRes = await workApi.addTag(workId.value, newTag)
            if (addRes.code === 200) {
              tags.value.push(newTag)
              work.value = addRes.data
            }
          } catch (error) {
            uni.showToast({
              title: '添加标签失败',
              icon: 'none'
            })
          }
        } else {
          uni.showToast({
            title: '标签已存在',
            icon: 'none'
          })
        }
      }
    }
  })
}

const removeTag = async (tag) => {
  try {
    const res = await workApi.removeTag(workId.value, tag)
    if (res.code === 200) {
      tags.value = tags.value.filter(t => t !== tag)
      work.value = res.data
    }
  } catch (error) {
    uni.showToast({
      title: '删除标签失败',
      icon: 'none'
    })
  }
}

const previewImage = () => {
  uni.previewImage({
    urls: [work.value.imageUrl],
    current: work.value.imageUrl
  })
}

const handleApply = () => {
  uni.navigateTo({
    url: `/pages/preview/preview?workId=${workId.value}&imageUrl=${encodeURIComponent(work.value.imageUrl)}`
  })
}

const handleToggleFavorite = async () => {
  try {
    const res = await workApi.toggleFavorite(workId.value)
    if (res.code === 200 && res.data) {
      work.value.isFavorite = res.data.isFavorite
      uni.showToast({
        title: work.value.isFavorite ? '已收藏' : '已取消收藏',
        icon: 'success',
        duration: 1000
      })
    }
  } catch (error) {
    uni.showToast({
      title: '操作失败',
      icon: 'none'
    })
  }
}

const handleDelete = () => {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这个作品吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({
            title: '删除中...'
          })
          
          const deleteRes = await workApi.deleteWork(workId.value)
          
          uni.hideLoading()
          
          if (deleteRes.code === 200) {
            uni.showToast({
              title: '删除成功',
              icon: 'success',
              duration: 1500
            })
            
            // 通知作品列表页面刷新数据
            // 使用 getApp().globalData 或 uni.$emit 传递删除事件
            const app = getApp()
            if (app && app.globalData) {
              app.globalData.workDeleted = true
              app.globalData.deletedWorkId = workId.value
            }
            
            // 延迟返回，确保提示显示
            setTimeout(() => {
              uni.navigateBack({
                delta: 1
              })
            }, 1500)
          } else {
            uni.showToast({
              title: deleteRes.message || '删除失败',
              icon: 'none'
            })
          }
        } catch (error) {
          uni.hideLoading()
          console.error('删除作品失败', error)
          uni.showToast({
            title: '删除失败',
            icon: 'none'
          })
        }
      }
    }
  })
}

const handleShare = () => {
  uni.showActionSheet({
    itemList: ['分享到微信好友', '分享到朋友圈', '生成分享海报', '保存海报到相册'],
    success: async (res) => {
      if (res.tapIndex === 0) {
        // 分享到微信好友
        shareToWeChat('session')
      } else if (res.tapIndex === 1) {
        // 分享到朋友圈
        shareToWeChat('timeline')
      } else if (res.tapIndex === 2) {
        // 生成分享海报
        await generateSharePoster()
      } else if (res.tapIndex === 3) {
        // 保存海报到相册
        await saveSharePoster()
      }
    }
  })
}

const shareToWeChat = (scene) => {
  // 微信分享配置
  const shareUrl = `/pages/works/detail?id=${workId.value}`
  const shareTitle = work.value.title || '我的AI作品'
  const shareImageUrl = work.value.imageUrl
  
  // 调用微信分享API
  // 注意：需要在页面onLoad时设置分享配置
  uni.showShareMenu({
    withShareTicket: true,
    menus: ['shareAppMessage', 'shareTimeline']
  })
  
  // 设置分享内容
  if (scene === 'session') {
    // 分享给好友
    uni.shareAppMessage({
      title: shareTitle,
      path: shareUrl,
      imageUrl: shareImageUrl
    })
  } else if (scene === 'timeline') {
    // 分享到朋友圈
    uni.shareTimeline({
      title: shareTitle,
      imageUrl: shareImageUrl
    })
  }
}

const generateSharePoster = async () => {
  uni.showLoading({
    title: '生成中...'
  })
  
  try {
    const shareUrl = `https://your-domain.com/pages/works/detail?id=${workId.value}`
    const posterPath = await generatePoster({
      imageUrl: work.value.imageUrl,
      title: work.value.title || '我的AI作品',
      shareUrl: shareUrl
    })
    
    uni.hideLoading()
    
    // 预览海报
    uni.previewImage({
      urls: [posterPath],
      current: posterPath
    })
  } catch (error) {
    uni.hideLoading()
    console.error('生成海报失败', error)
    uni.showToast({
      title: '生成失败',
      icon: 'none'
    })
  }
}

const saveSharePoster = async () => {
  uni.showLoading({
    title: '生成中...'
  })
  
  try {
    const shareUrl = `https://your-domain.com/pages/works/detail?id=${workId.value}`
    const posterPath = await generatePoster({
      imageUrl: work.value.imageUrl,
      title: work.value.title || '我的AI作品',
      shareUrl: shareUrl
    })
    
    await savePosterToAlbum(posterPath)
  } catch (error) {
    console.error('保存海报失败', error)
    uni.showToast({
      title: '保存失败',
      icon: 'none'
    })
  } finally {
    uni.hideLoading()
  }
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
}

.image-section {
  background: #ffffff;
  padding: 40rpx;
  margin-bottom: 20rpx;
  
  .work-image {
    width: 100%;
    min-height: 600rpx;
    border-radius: 20rpx;
  }
}

.info-section {
  background: #ffffff;
  padding: 40rpx;
  margin-bottom: 20rpx;
  
  .work-title {
    display: block;
    font-size: 40rpx;
    font-weight: bold;
    color: #333333;
    margin-bottom: 20rpx;
  }
  
  .work-description {
    display: block;
    font-size: 28rpx;
    color: #666666;
    line-height: 1.6;
    margin-bottom: 20rpx;
  }
  
  .work-time {
    display: block;
    font-size: 24rpx;
    color: #999999;
  }
  
  .category-section {
    margin: 30rpx 0;
    padding-top: 30rpx;
    border-top: 1rpx solid #E5E5E5;
    
    .category-title {
      font-size: 28rpx;
      font-weight: bold;
      color: #333333;
      margin-bottom: 20rpx;
    }
    
    .category-content {
      display: flex;
      align-items: center;
      justify-content: space-between;
      
      .category-value {
        font-size: 28rpx;
        color: #333333;
        padding: 10rpx 20rpx;
        background: #F0F0F0;
        border-radius: 20rpx;
      }
      
      .category-placeholder {
        font-size: 28rpx;
        color: #999999;
        font-style: italic;
      }
      
      .category-edit-btn {
        font-size: 26rpx;
        color: #007AFF;
        padding: 10rpx 20rpx;
        background: #E8F4FD;
        border-radius: 20rpx;
      }
    }
  }
  
  .tags-section {
    margin: 30rpx 0;
    padding-top: 30rpx;
    border-top: 1rpx solid #E5E5E5;
    
    .tags-title {
      font-size: 28rpx;
      font-weight: bold;
      color: #333333;
      margin-bottom: 20rpx;
    }
    
    .tags-list {
      display: flex;
      flex-wrap: wrap;
      gap: 15rpx;
      margin-bottom: 20rpx;
      
      .tag-item {
        display: inline-flex;
        align-items: center;
        padding: 8rpx 20rpx;
        background: #F0F0F0;
        border-radius: 20rpx;
        font-size: 24rpx;
        color: #666666;
        
        .tag-remove {
          margin-left: 10rpx;
          font-size: 32rpx;
          color: #FF3B30;
          font-weight: bold;
        }
        
        &.add-tag {
          background: #E8F4FD;
          color: #007AFF;
          border: 1rpx dashed #007AFF;
        }
      }
    }
    
    .tags-actions {
      .tags-edit-btn {
        font-size: 26rpx;
        color: #007AFF;
      }
    }
  }
}

.action-section {
  padding: 30rpx 40rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  
  .action-btn {
    width: 100%;
    height: 88rpx;
    border-radius: 44rpx;
    font-size: 32rpx;
    font-weight: bold;
    border: none;
    
    &.apply-btn {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: #ffffff;
    }
    
    &.delete-btn {
      background: #FF3B30;
      color: #ffffff;
    }
    
    &.favorite-btn {
      background: #F5F5F5;
      color: #666666;
      
      &.active {
        background: linear-gradient(135deg, #FF9500 0%, #FF6B00 100%);
        color: #ffffff;
      }
    }
    
    &.share-btn {
      background: linear-gradient(135deg, #34C759 0%, #30D158 100%);
      color: #ffffff;
    }
  }
}
</style>


