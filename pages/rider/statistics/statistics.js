// pages/rider/statistics/statistics.js
const app = getApp()

Page({
  data: {
    currentTab: 2,
    level: 'normal',
    levelText: '普通骑手',
    levelClass: 'normal',
    levelDesc: '最多可同时送1单',
    todayEarnings: 0,
    monthEarnings: 0,
    totalEarnings: 0,
    todayOrders: 0,
    monthOrders: 0,
    totalOrders: 0,
    rankByTotalOrders: 0,
    rankByTotalEarnings: 0,
    rankByTodayOrders: 0,
    rankByTodayEarnings: 0,
    silverProgress: 0,
    goldProgress: 0,
    earnings: []
  },

  onLoad: function(options) {
    this.loadRiderStatistics()
    this.loadEarnings()
  },

  onShow: function() {
    this.setData({ currentTab: 2 })
    this.loadRiderStatistics()
    this.loadEarnings()
  },

  onPullDownRefresh: function() {
    this.loadRiderStatistics()
    this.loadEarnings()
    wx.stopPullDownRefresh()
  },

  goBack: function() {
    wx.redirectTo({
      url: '/pages/rider/rider-index/rider-index'
    })
  },

  loadRiderStatistics: function() {
    const userId = app.globalData.userInfo?.id
    if (!userId) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      return
    }

    wx.request({
      url: `${app.globalData.baseUrl}/api/riders/statistics`,
      method: 'GET',
      data: { userId: userId },
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      success: (res) => {
        if (res.data.code === 200) {
          const data = res.data.data
          const level = data.level || 'normal'
          let levelText = '普通骑手'
          let levelClass = 'normal'
          let levelDesc = '最多可同时送1单'

          if (level === 'silver') {
            levelText = '白银骑手'
            levelClass = 'silver'
            levelDesc = '最多可同时送3单'
          } else if (level === 'gold') {
            levelText = '黄金骑手'
            levelClass = 'gold'
            levelDesc = '最多可同时送5单'
          }

          const totalOrders = data.totalOrders || 0
          const silverProgress = Math.min((totalOrders / 10) * 100, 100)
          const goldProgress = Math.min((totalOrders / 50) * 100, 100)

          this.setData({
            level,
            levelText,
            levelClass,
            levelDesc,
            totalEarnings: data.totalEarnings || 0,
            totalOrders,
            rankByTotalOrders: data.rankByTotalOrders || 0,
            rankByTotalEarnings: data.rankByTotalEarnings || 0,
            rankByTodayOrders: data.rankByTodayOrders || 0,
            rankByTodayEarnings: data.rankByTodayEarnings || 0,
            silverProgress,
            goldProgress
          })
          this.calculateStats()
        }
      }
    })
  },

  loadEarnings: function() {
    const userId = app.globalData.userInfo?.id
    if (!userId) {
      return
    }

    wx.request({
      url: `${app.globalData.baseUrl}/api/riders/earnings`,
      method: 'GET',
      data: { riderId: userId },
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      success: (res) => {
        if (res.data.code === 200) {
          this.setData({
            earnings: res.data.data || []
          })
          this.calculateStats()
        }
      }
    })
  },

  calculateStats: function() {
    const now = new Date()
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
    const monthStart = new Date(now.getFullYear(), now.getMonth(), 1)

    let todayEarnings = 0
    let monthEarnings = 0
    let todayOrders = 0
    let monthOrders = 0

    this.data.earnings.forEach(earning => {
      const earningDate = new Date(earning.createdAt)
      const amount = parseFloat(earning.amount)

      if (earningDate >= today) {
        todayEarnings += amount
        todayOrders++
      }
      if (earningDate >= monthStart) {
        monthEarnings += amount
        monthOrders++
      }
    })

    this.setData({
      todayEarnings: todayEarnings.toFixed(2),
      monthEarnings: monthEarnings.toFixed(2),
      todayOrders,
      monthOrders
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
