Page({
  data: {
    currentTab: 0
  },

  onLoad: function(options) {
    // 检查骑手登录状态
    if (!wx.getStorageSync('riderLoggedIn')) {
      wx.navigateTo({
        url: '/pages/rider/rider-login/rider-login'
      })
      return
    }
    
    this.setData({ currentTab: 0 })
  },

  onShow: function() {
    if (!wx.getStorageSync('riderLoggedIn')) {
      wx.redirectTo({
        url: '/pages/rider/rider-login/rider-login'
      })
      return
    }
    
    this.setData({ currentTab: 0 })
  },

  goToGrabOrders: function() {
    wx.redirectTo({
      url: '/pages/rider/grab-orders/grab-orders'
    })
  },

  goToDelivery: function() {
    wx.redirectTo({
      url: '/pages/rider/delivery/delivery'
    })
  },

  goToStatistics: function() {
    wx.redirectTo({
      url: '/pages/rider/statistics/statistics'
    })
  },

  goToProfile: function() {
    wx.redirectTo({
      url: '/pages/rider/rider-profile/rider-profile'
    })
  }
})
