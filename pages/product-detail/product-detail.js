Page({
  data: {
    product: {},
    quantity: 1,
    purchasedProducts: [],
    randomTip: ''
  },

  onLoad: function(options) {
    const productId = options.id
    this.loadProductDetail(productId)
    this.loadPurchasedProducts()
  },

  loadProductDetail: function(productId) {
    wx.request({
      url: `${getApp().globalData.baseUrl}/api/products/${productId}`,
      method: 'GET',
      success: (res) => {
        if (res.data.code === 200) {
          const product = res.data.data
          // 处理图片路径，添加完整的基础URL
          if (product.imageUrl && product.imageUrl.startsWith('/')) {
            product.imageUrl = `${getApp().globalData.baseUrl}${product.imageUrl}`
          }
          this.setData({
            product: product
          })
        }
      }
    })
  },

  loadPurchasedProducts: function() {
    wx.request({
      url: `${getApp().globalData.baseUrl}/api/orders`,
      method: 'GET',
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      success: (res) => {
        if (res.data.code === 200) {
          let purchasedProducts = res.data.data
          // 处理推荐商品的图片路径
          purchasedProducts = purchasedProducts.map(product => {
            if (product.imageUrl && product.imageUrl.startsWith('/')) {
              product.imageUrl = `${getApp().globalData.baseUrl}${product.imageUrl}`
            }
            return product
          })
          this.setData({
            purchasedProducts: purchasedProducts
          })
          this.generateRandomTip()
        }
      }
    })
  },

  generateRandomTip: function() {
    const tips = [
      '今日特价商品已更新！',
      '新品上市，欢迎品尝！',
      '满10元免配送费哦～',
      '收藏商品，下次购买更方便！',
      '分享给好友，一起享受美食！'
    ]
    
    if (this.data.purchasedProducts.length > 0) {
      // 如果用户购买过商品，显示购买过的商品推荐
      const topProduct = this.data.purchasedProducts[0]
      this.setData({
        randomTip: `您常购买的${topProduct.name}很受欢迎哦！`
      })
    } else {
      // 显示随机提示
      const randomIndex = Math.floor(Math.random() * tips.length)
      this.setData({
        randomTip: tips[randomIndex]
      })
    }
  },

  increaseQuantity: function() {
    if (this.data.quantity < this.data.product.stock) {
      this.setData({
        quantity: this.data.quantity + 1
      })
    }
  },

  decreaseQuantity: function() {
    if (this.data.quantity > 1) {
      this.setData({
        quantity: this.data.quantity - 1
      })
    }
  },

  addToCart: function() {
    const product = {
      ...this.data.product,
      quantity: this.data.quantity
    }
    
    const app = getApp()
    const existingItem = app.globalData.cart.find(item => item.id === product.id)
    
    if (existingItem) {
      existingItem.quantity += this.data.quantity
    } else {
      app.globalData.cart.push(product)
    }
    
    app.updateCartStorage()
    
    wx.showToast({
      title: '已加入购物车',
      icon: 'success'
    })
  },

  buyNow: function() {
    // 检查是否已登录
    if (!wx.getStorageSync('token')) {
      wx.navigateTo({
        url: '/pages/login/login'
      })
      return
    }

    // 先加入购物车
    this.addToCart()
    
    // 跳转到结算页面
    setTimeout(() => {
      wx.switchTab({
        url: '/pages/index/index'
      })
    }, 1000)
  },

  goToProduct: function(e) {
    const productId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/product-detail/product-detail?id=${productId}`
    })
  }
})