// pages/rider/delivery/delivery.js
const app = getApp()

Page({
  data: {
    currentTab: 1,
    orders: []
  },

  onLoad: function(options) {
    this.loadDeliveryOrders()
  },

  onShow: function() {
    this.setData({ currentTab: 1 })
    this.loadDeliveryOrders()
  },

  onPullDownRefresh: function() {
    this.loadDeliveryOrders()
    wx.stopPullDownRefresh()
  },

  goBack: function() {
    wx.redirectTo({
      url: '/pages/rider/rider-index/rider-index'
    })
  },

  loadDeliveryOrders: function() {
    const userId = app.globalData.userInfo?.id
    if (!userId) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      return
    }

    wx.request({
      url: `${app.globalData.baseUrl}/api/riders/delivery-orders`,
      method: 'GET',
      data: { riderId: userId },
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      success: (res) => {
        console.log('配送中订单数据:', res.data)
        if (res.data.code === 200) {
          const allOrders = res.data.data || []
          console.log('所有订单（未过滤）:', allOrders)
          if (allOrders.length > 0) {
            console.log('第一个订单的所有字段:', JSON.stringify(allOrders[0], null, 2))
          }
          const orders = allOrders.filter(order => order.status === 2)
          console.log('过滤后的配送中订单:', orders)
          this.setData({ orders })
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

  completeOrder: function(e) {
    const orderId = e.currentTarget.dataset.id
    const userId = app.globalData.userInfo?.id

    wx.showModal({
      title: '确认完成',
      content: '确定已完成配送吗？',
      success: (res) => {
        if (res.confirm) {
          wx.request({
            url: `${app.globalData.baseUrl}/api/riders/complete-order`,
            method: 'PUT',
            header: {
              'Authorization': `Bearer ${wx.getStorageSync('token')}`
            },
            data: { orderId: orderId, riderId: userId },
            success: (res) => {
              if (res.data.code === 200) {
                wx.showToast({
                  title: '配送完成',
                  icon: 'success'
                })
                setTimeout(() => {
                  this.loadDeliveryOrders()
                }, 1000)
              } else {
                wx.showToast({
                  title: res.data.message || '操作失败',
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
