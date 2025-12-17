<template>
  <view class="works-container">
    <view class="category-section">
      <view class="category-tabs">
        <view 
          class="category-tab" 
          :class="{ active: selectedCategory === null }"
          @click="selectCategory(null)"
        >
          <text>全部</text>
        </view>
        <view 
          class="category-tab" 
          v-for="cat in categories" 
          :key="cat"
          :class="{ active: selectedCategory === cat }"
          @click="selectCategory(cat)"
        >
          <text>{{ cat }}</text>
        </view>
        <view class="category-tab add-category" @click="showAddCategoryModal">
          <text>+ 添加分类</text>
        </view>
        <view 
          class="category-tab favorite-tab" 
          :class="{ active: showFavorites }"
          @click="toggleFavorites"
        >
          <text>⭐ 收藏</text>
        </view>
      </view>
    </view>
    
    <view class="works-grid" v-if="works.length > 0">
      <view 
        class="work-card" 
        v-for="work in works" 
        :key="work.id"
        @click="goToDetail(work.id)"
      >
        <image class="work-image" :src="work.imageUrl" mode="aspectFill"></image>
        <view class="work-info">
          <view class="work-title-row">
            <text class="work-title">{{ work.title }}</text>
            <text class="favorite-icon" :class="{ active: work.isFavorite }" @click.stop="toggleFavorite(work)">⭐</text>
          </view>
          <text class="work-time">{{ formatTime(work.createTime) }}</text>
        </view>
      </view>
    </view>
    
    <view class="empty-state" v-else>
      <image class="empty-icon" src="/static/icons/empty.png" mode="aspectFit"></image>
      <text class="empty-text">还没有作品，快去生成吧~</text>
      <button class="empty-btn" @click="goToGenerate">去生成</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { workApi } from '@/api/work'
import { onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app'

const works = ref([])
const page = ref(0)
const size = ref(20)
const loading = ref(false)
const hasMore = ref(true)
const selectedCategory = ref(null)
const categories = ref([])
const showFavorites = ref(false)

const loadWorks = async (reset = false) => {
  if (loading.value) return
  
  loading.value = true
  
  try {
    if (reset) {
      page.value = 0
      works.value = []
    }
    
    const params = {
      page: page.value,
      size: size.value
    }
    
    if (showFavorites.value) {
      params.favorite = true
    } else if (selectedCategory.value) {
      params.category = selectedCategory.value
    }
    
    const res = await workApi.getWorks(params)
    
    if (res.code === 200 && res.data) {
      const newWorks = res.data.content || []
      works.value = reset ? newWorks : [...works.value, ...newWorks]
      hasMore.value = newWorks.length === size.value
      page.value++
      
      // 提取所有分类（保留已有分类）
      extractCategories(newWorks)
      console.log('加载作品后分类列表:', categories.value)
    }
  } catch (error) {
    console.error('加载作品失败', error)
  } finally {
    loading.value = false
    uni.stopPullDownRefresh()
  }
}

// 从所有作品中提取分类（统一数据源）
const loadAllCategories = async () => {
  try {
    // 获取所有作品以提取分类
    const res = await workApi.getWorks({ page: 0, size: 1000 })
    if (res.code === 200 && res.data) {
      const worksList = res.data.content || []
      const catSet = new Set(categories.value) // 保留已有的分类
      worksList.forEach(work => {
        if (work.category) {
          catSet.add(work.category)
        }
      })
      categories.value = Array.from(catSet).sort()
      console.log('加载所有分类:', categories.value)
    }
  } catch (error) {
    console.error('加载分类列表失败', error)
  }
}

const extractCategories = (worksList) => {
  const catSet = new Set(categories.value) // 保留已有的分类
  worksList.forEach(work => {
    if (work.category) {
      catSet.add(work.category)
    }
  })
  categories.value = Array.from(catSet).sort()
}

const selectCategory = (category) => {
  selectedCategory.value = category
  showFavorites.value = false
  loadWorks(true)
}

const toggleFavorites = () => {
  showFavorites.value = !showFavorites.value
  selectedCategory.value = null
  loadWorks(true)
}

const showAddCategoryModal = () => {
  uni.showModal({
    title: '添加分类',
    editable: true,
    placeholderText: '请输入分类名称',
    success: async (res) => {
      if (res.confirm && res.content && res.content.trim()) {
        const newCategory = res.content.trim()
        console.log('添加分类:', newCategory)
        
        // 添加到分类列表（临时添加，实际分类需要为作品设置）
        if (!categories.value.includes(newCategory)) {
          categories.value.push(newCategory)
          categories.value.sort()
          console.log('分类列表已更新:', categories.value)
        }
        
        // 重新加载所有分类，确保数据一致性
        await loadAllCategories()
        
        // 选择新添加的分类
        selectCategory(newCategory)
      }
    }
  })
}

const toggleFavorite = async (work) => {
  try {
    const res = await workApi.toggleFavorite(work.id)
    if (res.code === 200 && res.data) {
      work.isFavorite = res.data.isFavorite
      uni.showToast({
        title: work.isFavorite ? '已收藏' : '已取消收藏',
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

const goToDetail = (workId) => {
  uni.navigateTo({
    url: `/pages/works/detail?id=${workId}`
  })
}

const goToGenerate = () => {
  uni.switchTab({
    url: '/pages/index/index'
  })
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return Math.floor(diff / 86400000) + '天前'
}

onMounted(() => {
  loadWorks(true)
  loadAllCategories() // 加载所有分类
})

onShow(() => {
  // 从其他页面返回时，检查是否有作品被删除
  const app = getApp()
  if (app && app.globalData && app.globalData.workDeleted) {
    // 如果有作品被删除，刷新列表
    const deletedWorkId = app.globalData.deletedWorkId
    console.log('检测到作品被删除，刷新列表', deletedWorkId)
    
    // 从列表中移除被删除的作品
    if (deletedWorkId) {
      works.value = works.value.filter(work => work.id !== deletedWorkId)
    }
    
    // 重新加载作品列表
    loadWorks(true)
    
    // 清除标记
    app.globalData.workDeleted = false
    app.globalData.deletedWorkId = null
  } else {
    // 正常返回时，只重新加载分类列表
    loadAllCategories()
  }
})

onPullDownRefresh(() => {
  loadWorks(true)
  loadAllCategories() // 下拉刷新时也重新加载分类
})

onReachBottom(() => {
  if (hasMore.value) {
    loadWorks()
  }
})
</script>

<style lang="scss" scoped>
.works-container {
  padding: 20rpx;
  min-height: 100vh;
  background-color: #F8F8F8;
}

.category-section {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  
  .category-tabs {
    display: flex;
    flex-wrap: wrap;
    gap: 15rpx;
    
    .category-tab {
      padding: 10rpx 30rpx;
      background: #F5F5F5;
      border-radius: 30rpx;
      font-size: 26rpx;
      color: #666666;
      transition: all 0.3s;
      
      &.active {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: #ffffff;
      }
      
      &.add-category {
        background: #E8F4FD;
        color: #007AFF;
        border: 1rpx dashed #007AFF;
      }
      
      text {
        display: block;
      }
    }
  }
}

.works-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
  
  .work-card {
    background: #ffffff;
    border-radius: 20rpx;
    overflow: hidden;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
    
    .work-image {
      width: 100%;
      height: 300rpx;
    }
    
    .work-info {
      padding: 20rpx;
      
      .work-title {
        display: block;
        font-size: 28rpx;
        font-weight: bold;
        color: #333333;
        margin-bottom: 10rpx;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      
      .work-time {
        display: block;
        font-size: 24rpx;
        color: #999999;
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
  
  .empty-icon {
    width: 200rpx;
    height: 200rpx;
    margin-bottom: 40rpx;
    opacity: 0.5;
  }
  
  .empty-text {
    font-size: 32rpx;
    color: #999999;
    margin-bottom: 40rpx;
  }
  
  .empty-btn {
    padding: 20rpx 60rpx;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #ffffff;
    border-radius: 44rpx;
    font-size: 28rpx;
    border: none;
  }
}
</style>


