// pages/rider/rider-profile/rider-profile.js
const app = getApp()

Page({
  data: {
    currentTab: 3,
    userInfo: {},
    riderInfo: {},
    riderStatus: 'active',
    level: 'normal',
    levelText: '普通骑手'
  },

  onLoad: function(options) {
    this.loadUserInfo()
    this.loadRiderInfo()
  },

  onShow: function() {
    this.setData({ currentTab: 3 })
    this.loadRiderInfo()
  },

  onPullDownRefresh: function() {
    this.loadRiderInfo()
    wx.stopPullDownRefresh()
  },

  goBack: function() {
    wx.redirectTo({
      url: '/pages/rider/rider-index/rider-index'
    })
  },

  loadUserInfo: function() {
    this.setData({
      userInfo: app.globalData.userInfo || {}
    })
  },

  loadRiderInfo: function() {
    const userId = app.globalData.userInfo?.id
    if (!userId) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      return
    }

    wx.request({
      url: `${app.globalData.baseUrl}/api/riders/info`,
      method: 'GET',
      data: { userId: userId },
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      success: (res) => {
        if (res.data.code === 200) {
          const riderInfo = res.data.data || {}
          // 处理收款码路径
          if (riderInfo.paymentCode && riderInfo.paymentCode.indexOf('/') === 0) {
            riderInfo.paymentCode = `${app.globalData.baseUrl}${riderInfo.paymentCode}`
          }
          const level = riderInfo.level || 'normal'
          let levelText = '普通骑手'

          if (level === 'silver') {
            levelText = '白银骑手'
          } else if (level === 'gold') {
            levelText = '黄金骑手'
          }

          this.setData({
            riderInfo,
            riderStatus: riderInfo.status || 'active',
            level,
            levelText
          })
        } else {
          wx.showToast({
            title: res.data.message || '获取骑手信息失败',
            icon: 'none'
          })
        }
      },
      fail: () => {
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        })
      }
    })
  },

  updateStatus: function(e) {
    const status = e.currentTarget.dataset.status
    const userId = app.globalData.userInfo?.id

    wx.request({
      url: `${app.globalData.baseUrl}/api/riders/status`,
      method: 'PUT',
      data: { userId: userId, status: status },
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      success: (res) => {
        if (res.data.code === 200) {
          this.setData({
            riderStatus: status
          })
          wx.showToast({
            title: '状态更新成功',
            icon: 'success'
          })
        } else {
          wx.showToast({
            title: res.data.message || '状态更新失败',
            icon: 'none'
          })
        }
      },
      fail: () => {
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        })
      }
    })
  },

  uploadPaymentCode: function() {
    const userId = app.globalData.userInfo?.id
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePaths = res.tempFilePaths
        wx.uploadFile({
          url: `${app.globalData.baseUrl}/api/upload/payment-code`,
          filePath: tempFilePaths[0],
          name: 'file',
          header: {
            'Authorization': `Bearer ${wx.getStorageSync('token')}`
          },
          formData: {
            userId: userId
          },
          success: (uploadRes) => {
            const data = JSON.parse(uploadRes.data)
            if (data.code === 200) {
              const paymentCode = data.data.url
              // 如果是相对路径，添加完整的基础URL
              const fullPaymentCode = paymentCode && paymentCode.indexOf('/') === 0 
                ? `${app.globalData.baseUrl}${paymentCode}` 
                : paymentCode
              this.setData({
                'riderInfo.paymentCode': fullPaymentCode
              })
              wx.showToast({
                title: '收款码上传成功',
                icon: 'success'
              })
            } else {
              wx.showToast({
                title: data.message || '上传失败',
                icon: 'none'
              })
            }
          },
          fail: () => {
            wx.showToast({
              title: '上传失败',
              icon: 'none'
            })
          }
        })
      }
    })
  },

  goToWithdraw: function() {
    wx.showToast({
      title: '提现功能开发中',
      icon: 'none'
    })
  },

  goToOrders: function() {
    wx.redirectTo({
      url: '/pages/rider/delivery/delivery'
    })
  },

  goToEarnings: function() {
    wx.redirectTo({
      url: '/pages/rider/statistics/statistics'
    })
  },

  goToSettings: function() {
    wx.showToast({
      title: '设置功能开发中',
      icon: 'none'
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

  contactService: function() {
    wx.makePhoneCall({
      phoneNumber: '1234567890',
      success: () => {},
      fail: () => {
        wx.showToast({
          title: '拨打电话失败',
          icon: 'none'
        })
      }
    })
  },

  logout: function() {
    wx.showModal({
      title: '提示',
      content: '确定要退出骑手模式吗？',
      success: (res) => {
        if (res.confirm) {
          wx.switchTab({
            url: '/pages/index/index'
          })
        }
      }
    })
  },

  switchTab: function(e) {
    const index = e.currentTarget.dataset.index
    this.setData({ currentTab: index })

    switch(index) {
      case 0:
        wx.redirectTo({
          url: '/pages/rider/grab-orders/grab-orders'
        })
        break
      case 1:
        wx.redirectTo({
          url: '/pages/rider/delivery/delivery'
        })
        break
      case 2:
        wx.redirectTo({
          url: '/pages/rider/statistics/statistics'
        })
        break
      case 3:
        wx.redirectTo({
          url: '/pages/rider/rider-profile/rider-profile'
        })
        break
    }
  }
})
