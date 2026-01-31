Page({
  data: {
    userInfo: {},
    isRider: false,
    riderStatus: null,
    riderStatusText: ''
  },

  onLoad: function() {
    this.loadUserInfo()
  },

  onShow: function() {
    this.loadUserInfo()
  },

  loadUserInfo: function() {
    const app = getApp()
    let userInfo = app.globalData.userInfo || {}
    
    // 处理头像路径
    if (userInfo.avatar && userInfo.avatar.startsWith('/')) {
      userInfo.avatar = `${app.globalData.baseUrl}${userInfo.avatar}`
    }
    
    this.setData({
      userInfo: userInfo,
      isRider: app.globalData.isRider,
      riderStatus: app.globalData.riderStatus
    })
    
    // 设置骑手状态文本
    let riderStatusText = ''
    if (app.globalData.isRider) {
      switch (app.globalData.riderStatus) {
        case 'pending':
          riderStatusText = '审核中'
          break
        case 'approved':
          riderStatusText = '已通过'
          break
        case 'rejected':
          riderStatusText = '已拒绝'
          break
        default:
          riderStatusText = '未知'
      }
    }
    
    this.setData({ riderStatusText })
  },

  goToAddress: function() {
    wx.navigateTo({
      url: '/pages/address/address'
    })
  },

  showServiceTerms: function() {
    wx.navigateTo({
      url: '/pages/service-terms/service-terms'
    })
  },

  showPrivacyPolicy: function() {
    wx.navigateTo({
      url: '/pages/privacy-policy/privacy-policy'
    })
  },

  contactCustomerService: function() {
    wx.showModal({
      title: '联系客服',
      content: '客服电话：1234567890\n\n是否立即拨打？',
      success: (res) => {
        if (res.confirm) {
          wx.makePhoneCall({
            phoneNumber: '1234567890'
          })
        }
      }
    })
  },

  applyForRider: function() {
    wx.navigateTo({
      url: '/pages/rider/rider-register/rider-register'
    })
  },

  goToRiderPage: function() {
    console.log('跳转到骑手登录页面')
    
    const app = getApp()
    const token = wx.getStorageSync('token')
    
    // 检查普通用户是否已登录
    if (token && app.globalData.userInfo && app.globalData.userInfo.id) {
      wx.showModal({
        title: '提示',
        content: '您已登录普通用户账号，需要先退出才能登录骑手账号',
        showCancel: false,
        success: () => {
          // 跳转到"我的"页面，用户可以退出登录
          wx.switchTab({
            url: '/pages/profile/profile'
          })
        }
      })
      return
    }
    
    // 未登录普通用户，可以进入骑手登录页面
    wx.navigateTo({
      url: '/pages/rider/rider-login/rider-login'
    })
  },

  goToRiderCenter: function() {
    console.log('跳转到骑手登录页面')
    wx.navigateTo({
      url: '/pages/rider/rider-login/rider-login'
    })
  },

  logout: function() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          // 清除本地存储
          wx.removeStorageSync('token')
          
          // 清除全局数据
          const app = getApp()
          app.globalData.userInfo = null
          app.globalData.isRider = false
          app.globalData.riderStatus = null
          
          // 跳转到登录页
          wx.reLaunch({
            url: '/pages/login/login'
          })
        }
      }
    })
  },

  changeAvatar: function() {
    const app = getApp()
    const userId = app.globalData.userInfo?.id
    
    if (!userId) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      return
    }
    
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePath = res.tempFilePaths[0]
        
        wx.showLoading({
          title: '上传中...'
        })
        
        wx.uploadFile({
          url: `${app.globalData.baseUrl}/api/upload/avatar`,
          filePath: tempFilePath,
          name: 'file',
          header: {
            'Authorization': `Bearer ${wx.getStorageSync('token')}`
          },
          formData: {
            userId: userId
          },
          success: (uploadRes) => {
            wx.hideLoading()
            const data = JSON.parse(uploadRes.data)
            if (data.code === 200) {
              const avatarUrl = `${app.globalData.baseUrl}${data.data.url}`
              app.globalData.userInfo.avatar = data.data.url
              this.setData({
                'userInfo.avatar': avatarUrl
              })
              wx.showToast({
                title: '头像更新成功',
                icon: 'success'
              })
            } else {
              wx.showToast({
                title: data.message || '上传失败',
                icon: 'none'
              })
            }
          },
          fail: (err) => {
            wx.hideLoading()
            wx.showToast({
              title: '上传失败',
              icon: 'none'
            })
          }
        })
      }
    })
  }
})