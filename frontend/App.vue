<template>
  <view id="app">
  </view>
</template>

<script>
export default {
  onLaunch: function() {
    console.log('App Launch')
    // 检查登录状态
    this.checkLogin()
  },
  onShow: function() {
    console.log('App Show')
  },
  onHide: function() {
    console.log('App Hide')
  },
  methods: {
    checkLogin() {
      const token = uni.getStorageSync('token')
      if (!token) {
        // 未登录，跳转到登录页
        uni.reLaunch({
          url: '/pages/login/login'
        })
      }
    }
  }
}
</script>

<style lang="scss">
/*每个页面公共css */
page {
  background-color: #F8F8F8;
}
</style>


