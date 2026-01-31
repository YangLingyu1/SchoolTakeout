Page({
  data: {
    loading: false
  },

  onLoad: function() {
    console.log('=== 登录页面 onLoad 被调用 ===')
    
    const app = getApp()
    const token = wx.getStorageSync('token')
    
    console.log('检查登录状态:', { 
      hasToken: !!token, 
      hasUserInfo: !!app.globalData.userInfo,
      userInfo: app.globalData.userInfo 
    })
    
    // 如果用户已经登录，跳转到首页
    if (token && app.globalData.userInfo && app.globalData.userInfo.id) {
      console.log('用户已登录，跳转到首页')
      wx.showModal({
        title: '提示',
        content: '您已登录，是否重新登录？',
        success: (res) => {
          if (res.confirm) {
            // 清除登录状态
            wx.removeStorageSync('token')
            app.globalData.userInfo = null
            app.globalData.isRider = false
            app.globalData.riderStatus = null
            console.log('已清除登录状态')
          } else {
            // 跳转到首页
            wx.switchTab({
              url: '/pages/index/index'
            })
          }
        }
      })
    }
  },

  onLogin: function() {
    console.log('=== onLogin 被调用 ===')
    console.log('当前loading状态:', this.data.loading)
    
    if (this.data.loading) {
      console.log('正在加载中，忽略点击')
      return
    }
    
    console.log('开始登录流程，设置loading为true')
    this.setData({ loading: true })
    
    wx.getUserProfile({
      desc: '用于完善用户资料',
      success: (res) => {
        console.log('getUserProfile 成功:', res)
        console.log('用户昵称:', res.userInfo.nickName)
        console.log('用户头像:', res.userInfo.avatarUrl)
        
        // 保存用户信息到临时存储
        wx.setStorageSync('tempUserInfo', res.userInfo)
        
        // 继续登录流程
        this.doLogin()
      },
      fail: (err) => {
        console.log('getUserProfile 失败:', err)
        wx.showToast({
          title: '需要授权才能继续使用',
          icon: 'none'
        })
        this.setData({ loading: false })
      }
    })
  },

  doLogin: function() {
    console.log('=== doLogin 被调用 ===')
    wx.login({
      success: (res) => {
        console.log('wx.login 成功:', res)
        if (res.code) {
          console.log('获取到登录code:', res.code)
          console.log('调用 getApp().login')
          getApp().login(res.code).then((result) => {
            console.log('登录成功:', result)
            this.handleLoginSuccess(result)
          }).catch((error) => {
            console.error('登录失败:', error)
            wx.showToast({
              title: error.message || '登录失败',
              icon: 'error'
            })
          }).finally(() => {
            console.log('登录流程结束，重置loading')
            this.setData({ loading: false })
          })
        } else {
          console.error('wx.login 返回的code为空')
          wx.showToast({
            title: '获取登录凭证失败',
            icon: 'error'
          })
          this.setData({ loading: false })
        }
      },
      fail: (err) => {
        console.error('wx.login 失败:', err)
        wx.showToast({
          title: '微信登录失败',
          icon: 'error'
        })
        this.setData({ loading: false })
      }
    })
  },

  handleLoginSuccess: function(result) {
    console.log('=== handleLoginSuccess 被调用 ===')
    console.log('登录结果:', result)
    
    const app = getApp()
    console.log('app.globalData:', app.globalData)
    console.log('isRider:', app.globalData.isRider)
    console.log('riderStatus:', app.globalData.riderStatus)
    
    // 检查是否有临时用户信息（头像和昵称）
    const tempUserInfo = wx.getStorageSync('tempUserInfo')
    if (tempUserInfo) {
      console.log('检测到用户信息，准备更新')
      
      // 更新昵称
      if (tempUserInfo.nickName) {
        app.globalData.userInfo.nickname = tempUserInfo.nickName
        console.log('更新用户昵称:', tempUserInfo.nickName)
      }
      
      // 上传头像
      if (tempUserInfo.avatarUrl) {
        console.log('检测到用户头像，准备上传')
        this.uploadUserAvatar(tempUserInfo.avatarUrl, result.data.userInfo.id)
      }
      
      // 清除临时用户信息
      wx.removeStorageSync('tempUserInfo')
    }
    
    // 无论是否是骑手，登录后都进入普通用户端
    // 骑手功能可以通过"我的"页面进入
    console.log('设置普通用户TabBar')
    app.setUserTabBar()
    this.checkFirstLogin()
  },

  uploadUserAvatar: function(avatarUrl, userId) {
    console.log('开始上传用户头像:', avatarUrl)
    
    wx.uploadFile({
      url: `${getApp().globalData.baseUrl}/api/upload/avatar`,
      filePath: avatarUrl,
      name: 'file',
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      formData: {
        userId: userId
      },
      success: (res) => {
        console.log('头像上传成功:', res)
        const data = JSON.parse(res.data)
        if (data.code === 200) {
          console.log('头像URL:', data.data.url)
          // 更新全局用户信息
          getApp().globalData.userInfo.avatar = data.data.url
          // 清除临时用户信息
          wx.removeStorageSync('tempUserInfo')
        } else {
          console.log('头像上传失败:', data.message)
        }
      },
      fail: (err) => {
        console.error('头像上传失败:', err)
      }
    })
  },

  checkFirstLogin: function() {
    // 检查用户是否首次登录（这里简化处理，实际应从后端获取用户地址信息）
    const hasAddress = wx.getStorageSync('hasAddress')
    if (!hasAddress) {
      wx.showModal({
        title: '提示',
        content: '首次使用，请完善您的宿舍楼信息',
        showCancel: false,
        success: () => {
          wx.navigateTo({
            url: '/pages/address/address'
          })
        }
      })
    } else {
      wx.switchTab({
        url: '/pages/index/index'
      })
    }
  },

  onRiderRegister: function() {
    console.log('=== onRiderRegister 被调用 ===')
    wx.navigateTo({
      url: '/pages/rider/rider-login/rider-login'
    })
  },

  onServiceTerms: function() {
    wx.navigateTo({
      url: '/pages/service-terms/service-terms'
    })
  },

  onPrivacyPolicy: function() {
    wx.navigateTo({
      url: '/pages/privacy-policy/privacy-policy'
    })
  }
})