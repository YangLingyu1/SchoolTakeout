// pages/rider/grab-orders/grab-orders.js
const app = getApp()

Page({
  data: {
    currentTab: 0,
    orders: []
  },

  onLoad: function(options) {
    this.loadPendingOrders()
  },

  onShow: function() {
    this.setData({ currentTab: 0 })
    this.loadPendingOrders()
  },

  onPullDownRefresh: function() {
    this.loadPendingOrders()
    wx.stopPullDownRefresh()
  },

  goBack: function() {
    wx.redirectTo({
      url: '/pages/rider/rider-index/rider-index'
    })
  },

  loadPendingOrders: function() {
    wx.request({
      url: `${app.globalData.baseUrl}/api/riders/pending-orders`,
      method: 'GET',
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      success: (res) => {
        if (res.data.code === 200) {
          this.setData({
            orders: res.data.data || []
          })
        } else {
          wx.showToast({
            title: res.data.message || '获取订单失败',
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

  acceptOrder: function(e) {
    const orderId = e.currentTarget.dataset.id
    const userId = app.globalData.userInfo?.id

    if (!userId) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      return
    }

    wx.showModal({
      title: '确认抢单',
      content: '确定要接这个订单吗？',
      success: (res) => {
        if (res.confirm) {
          wx.request({
            url: `${app.globalData.baseUrl}/api/riders/accept-order`,
            method: 'PUT',
            header: {
              'Authorization': `Bearer ${wx.getStorageSync('token')}`
            },
            data: {
              orderId: orderId,
              riderId: userId
            },
            success: (res) => {
              if (res.data.code === 200) {
                wx.showToast({
                  title: '抢单成功',
                  icon: 'success'
                })
                setTimeout(() => {
                  this.loadPendingOrders()
                }, 1000)
              } else {
                wx.showToast({
                  title: res.data.message || '抢单失败',
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
        }
      }
    })
  },

  switchTab: function(e) {
    const index = e.currentTarget.dataset.index
    this.setData({ currentTab: index })

    switch(index) {
      case 0:
        wx.navigateTo({
          url: '/pages/rider/grab-orders/grab-orders'
        })
        break
      case 1:
        wx.navigateTo({
          url: '/pages/rider/delivery/delivery'
        })
        break
      case 2:
        wx.navigateTo({
          url: '/pages/rider/statistics/statistics'
        })
        break
      case 3:
        wx.navigateTo({
          url: '/pages/rider/rider-profile/rider-profile'
        })
        break
    }
  }
})
