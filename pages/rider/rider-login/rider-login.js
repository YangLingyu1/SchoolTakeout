Page({
  data: {
    phone: '',
    name: '',
    password: '',
    canLogin: false
  },

  onLoad: function() {
    const riderLoggedIn = wx.getStorageSync('riderLoggedIn')
    const riderInfo = wx.getStorageSync('riderInfo')
    
    console.log('骑手登录检查:', { riderLoggedIn, riderInfo })
    
    // 验证数据有效性
    if (riderLoggedIn && riderInfo) {
      // 检查riderInfo是否包含必要字段
      if (!riderInfo.id || !riderInfo.phone || !riderInfo.realName) {
        console.log('riderInfo数据不完整，清除残留数据')
        wx.removeStorageSync('riderLoggedIn')
        wx.removeStorageSync('riderInfo')
        return
      }
      
      // 数据有效，跳转到骑手工作台
      console.log('骑手已登录，跳转到骑手工作台')
      wx.redirectTo({
        url: '/pages/rider/rider-index/rider-index'
      })
    }
  },

  onPhoneInput: function(e) {
    const phone = e.detail.value
    this.setData({ phone })
    this.checkForm()
  },

  onNameInput: function(e) {
    const name = e.detail.value
    this.setData({ name })
    this.checkForm()
  },

  onPasswordInput: function(e) {
    const password = e.detail.value
    this.setData({ password })
    this.checkForm()
  },

  checkForm: function() {
    const { phone, name, password } = this.data
    const canLogin = phone && name && password && /^1[3-9]\d{9}$/.test(phone) && password.length >= 6
    this.setData({ canLogin })
  },

  onLogin: function() {
    const { phone, name, password } = this.data

    wx.showLoading({
      title: '验证中...'
    })

    wx.request({
      url: `${getApp().globalData.baseUrl}/api/rider/login`,
      method: 'POST',
      data: {
        phone: phone,
        name: name,
        password: password
      },
      success: (res) => {
        wx.hideLoading()
        
        if (res.data.code === 200) {
          wx.setStorageSync('riderLoggedIn', true)
          wx.setStorageSync('riderInfo', res.data.data)
          
          wx.showToast({
            title: '登录成功',
            icon: 'success'
          })

          setTimeout(() => {
            wx.navigateTo({
              url: '/pages/rider/rider-index/rider-index'
            })
          }, 1500)
        } else {
          wx.showToast({
            title: res.data.message || '骑手信息验证失败',
            icon: 'none'
          })
        }
      },
      fail: (err) => {
        wx.hideLoading()
        wx.showToast({
          title: '网络错误，请重试',
          icon: 'none'
        })
      }
    })
  },

  goToRegister: function() {
    const app = getApp()
    
    // 检查用户是否已登录
    const token = wx.getStorageSync('token')
    if (!token || !app.globalData.userInfo || !app.globalData.userInfo.id) {
      wx.showModal({
        title: '提示',
        content: '请先进行微信登录',
        showCancel: false,
        success: () => {
          wx.navigateTo({
            url: '/pages/login/login'
          })
        }
      })
      return
    }
    
    wx.navigateTo({
      url: '/pages/rider/rider-register/rider-register'
    })
  }
})