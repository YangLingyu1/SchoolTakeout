App({
  globalData: {
    userInfo: null,
    isRider: false,
    riderStatus: null,
    cart: [],
    baseUrl: 'https://cs.huilintai.com'
  },

  onLaunch: function () {
    console.log('App Launch')
    this.checkLoginStatus()
    const storedCart = wx.getStorageSync('cart')
    if (storedCart && storedCart.length > 0) {
      this.globalData.cart = storedCart
    }
  },

  onShow: function (options) {
    console.log('App Show')
  },

  onHide: function () {
    console.log('App Hide')
  },

  onError: function (msg) {
    console.log('App Error:', msg)
  },

  checkLoginStatus: function() {
    const token = wx.getStorageSync('token')
    if (token) {
      this.getUserInfo()
    }
  },

  getUserInfo: function() {
    const token = wx.getStorageSync('token')
    if (!token) {
      console.log('没有找到token，无法获取用户信息')
      return
    }
    
    wx.request({
      url: `${this.globalData.baseUrl}/api/auth/user-info`,
      method: 'GET',
      header: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      success: (res) => {
        if (res.statusCode === 200 && res.data.code === 200) {
          this.globalData.userInfo = res.data.data.userInfo
          this.globalData.isRider = res.data.data.isRider
          this.globalData.riderStatus = res.data.data.riderStatus
          console.log('成功获取用户信息:', res.data.data.userInfo)
        } else {
          console.log('获取用户信息失败:', res)
        }
      },
      fail: (err) => {
        console.error('请求用户信息失败:', err)
      }
    })
  },

  login: function(code) {
    console.log('=== app.login 被调用 ===')
    console.log('登录code:', code)
    
    return new Promise((resolve, reject) => {
      console.log('发起登录请求到:', `${this.globalData.baseUrl}/api/auth/login`)
      
      wx.request({
        url: `${this.globalData.baseUrl}/api/auth/login`,
        method: 'POST',
        data: { code },
        success: (res) => {
          console.log('登录请求响应:', res)
          console.log('响应状态码:', res.statusCode)
          console.log('响应数据:', res.data)
          
          if (res.data.code === 200) {
            console.log('登录成功，保存token和用户信息')
            wx.setStorageSync('token', res.data.data.token)
            this.globalData.userInfo = res.data.data.userInfo
            this.globalData.isRider = res.data.data.isRider
            this.globalData.riderStatus = res.data.data.riderStatus
            
            console.log('用户信息:', this.globalData.userInfo)
            console.log('是否为骑手:', this.globalData.isRider)
            console.log('骑手状态:', this.globalData.riderStatus)
            
            resolve(res.data)
          } else {
            console.error('登录失败，响应code不是200:', res.data)
            reject(res.data)
          }
        },
        fail: (err) => {
          console.error('登录请求失败:', err)
          reject({ code: 500, message: '网络请求失败' })
        }
      })
    })
  },

  addToCart: function(product) {
    const existingItem = this.globalData.cart.find(item => item.id === product.id)
    if (existingItem) {
      existingItem.quantity += 1
    } else {
      this.globalData.cart.push({
        ...product,
        quantity: 1
      })
    }
    this.updateCartStorage()
  },

  removeFromCart: function(productId) {
    const existingItem = this.globalData.cart.find(item => item.id === productId)
    if (existingItem) {
      if (existingItem.quantity > 1) {
        existingItem.quantity -= 1
      } else {
        this.globalData.cart = this.globalData.cart.filter(item => item.id !== productId)
      }
    }
    this.updateCartStorage()
  },

  updateCartStorage: function() {
    wx.setStorageSync('cart', this.globalData.cart)
  },

  getCartTotal: function() {
    const total = this.globalData.cart.reduce((total, item) => {
      return total + (Number(item.price) * Number(item.quantity))
    }, 0)
    return Number(total.toFixed(2))
  },

  getDeliveryFee: function() {
    const total = this.getCartTotal()
    if (total < 5) return 2
    if (total < 10) return 1
    return 0
  },

  setUserTabBar: function() {
    wx.setTabBarItem({
      index: 0,
      pagePath: 'pages/index/index',
      text: '首页',
      iconPath: 'images/index.png',
      selectedIconPath: 'images/index.png'
    })
    wx.setTabBarItem({
      index: 1,
      pagePath: 'pages/search/search',
      text: '搜索',
      iconPath: 'images/search.png',
      selectedIconPath: 'images/search.png'
    })
    wx.setTabBarItem({
      index: 2,
      pagePath: 'pages/orders/orders',
      text: '订单',
      iconPath: 'images/order.png',
      selectedIconPath: 'images/order.png'
    })
    wx.setTabBarItem({
      index: 3,
      pagePath: 'pages/profile/profile',
      text: '我的',
      iconPath: 'images/my.png',
      selectedIconPath: 'images/my.png'
    })
  },

  setRiderTabBar: function() {
    console.log('=== setRiderTabBar 被调用 ===')
    console.log('注意：微信小程序TabBar最多5个页面，无法完全切换为骑手TabBar')
    console.log('骑手用户将使用混合TabBar，在抢单页面内部提供导航')
    
    // 由于微信小程序TabBar限制，我们只修改第3个页面为"抢单"
    // 其他骑手功能在抢单页面内部提供导航
    console.log('设置TabBar第3项为抢单')
    wx.setTabBarItem({
      index: 3,
      pagePath: 'pages/rider/grab-orders/grab-orders',
      text: '抢单',
      iconPath: 'images/index.png',
      selectedIconPath: 'images/index.png',
      success: () => {
        console.log('TabBar第3项设置成功')
      },
      fail: (err) => {
        console.error('TabBar第3项设置失败:', err)
      }
    })
    
    console.log('setRiderTabBar 执行完成')
  }
})