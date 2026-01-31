Page({
  data: {
    banners: [],
    categories: [],
    products: [],
    currentCategory: 1,
    cart: [],
    showCart: false,
    purchasedProducts: [],
    randomTip: '',
    isOpen: true,
    nightDelivery: false,
    nightDeliveryBuildings: ['1号楼', '2号楼', '3号楼'],
    deliveryNotice: '',
    searchKeyword: '',
    cartTotal: 0,
    deliveryFee: 0,
    finalTotal: 0,
    totalQuantity: 0,
    productQuantityMap: {}
  },

  onLoad: function() {
    this.checkBusinessHours()
    this.loadBanners()
    this.loadCategories()
    this.loadProducts()
    this.loadCart()
    this.loadPurchasedProducts()
  },

  checkBusinessHours: function() {
    const now = new Date()
    const hour = now.getHours()
    const minute = now.getMinutes()
    const time = hour * 60 + minute

    // 暂时取消营业时间限制，默认为营业状态
    this.setData({
      isOpen: true,
      nightDelivery: false,
      deliveryNotice: ''
    })
  },

  onShow: function() {
    this.loadCart()
  },

  loadBanners: function() {
    wx.request({
      url: `${getApp().globalData.baseUrl}/api/banner`,
      method: 'GET',
      success: (res) => {
        if (res.data.code === 200) {
          this.setData({
            banners: res.data.data
          })
        }
      }
    })
  },

  loadCategories: function() {
    wx.request({
      url: `${getApp().globalData.baseUrl}/api/categories`,
      method: 'GET',
      success: (res) => {
        if (res.data.code === 200) {
          this.setData({
            categories: res.data.data
          })
        }
      }
    })
  },

  loadProducts: function(categoryId = 1) {
    wx.request({
      url: `${getApp().globalData.baseUrl}/api/products`,
      method: 'GET',
      data: { categoryId: categoryId },
      success: (res) => {
        if (res.data.code === 200) {
          let products = res.data.data
          // 处理图片路径，添加完整的基础URL
          products = products.map(product => {
            if (product.imageUrl && product.imageUrl.startsWith('/')) {
              product.imageUrl = `${getApp().globalData.baseUrl}${product.imageUrl}`
            }
            return product
          })
          this.setData({
            products: products,
            currentCategory: categoryId
          })
        }
      }
    })
  },

  loadCart: function() {
    const cart = [...getApp().globalData.cart].map(item => ({
      ...item,
      totalPrice: Number((item.price * item.quantity).toFixed(2))
    }))
    const cartTotal = getApp().getCartTotal()
    const deliveryFee = getApp().getDeliveryFee()
    const finalTotal = cartTotal + deliveryFee
    const totalQuantity = cart.reduce((total, item) => total + item.quantity, 0)
    
    const productQuantityMap = {}
    cart.forEach(item => {
      productQuantityMap[item.id] = item.quantity
    })
    
    this.setData({ cart, cartTotal, deliveryFee, finalTotal, totalQuantity, productQuantityMap })
  },

  loadPurchasedProducts: function() {
    wx.request({
      url: `${getApp().globalData.baseUrl}/api/user/purchased-products`,
      method: 'GET',
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      success: (res) => {
        if (res.data.code === 200) {
          this.setData({
            purchasedProducts: res.data.data || []
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
      '下单前请确认收货地址',
      '配送时间：10:00-23:00'
    ]
    
    if (this.data.purchasedProducts.length > 0) {
      // 如果用户购买过商品，显示购买次数最高的商品推荐
      const topProduct = this.data.purchasedProducts[0]
      this.setData({
        randomTip: `您常购买的${topProduct.name}又有货啦！已购买${topProduct.purchaseCount}次`
      })
    } else {
      // 显示随机提示
      const randomIndex = Math.floor(Math.random() * tips.length)
      this.setData({
        randomTip: tips[randomIndex]
      })
    }
  },

  switchCategory: function(e) {
    const categoryId = e.currentTarget.dataset.id
    this.setData({ searchKeyword: '' })
    this.loadProducts(categoryId)
  },

  onSearchInput: function(e) {
    this.setData({
      searchKeyword: e.detail.value
    })
  },

  onSearch: function() {
    const keyword = this.data.searchKeyword.trim()
    if (!keyword) {
      wx.showToast({
        title: '请输入搜索关键词',
        icon: 'none'
      })
      return
    }

    wx.request({
      url: `${getApp().globalData.baseUrl}/api/products`,
      method: 'GET',
      data: { keyword: keyword },
      success: (res) => {
        if (res.data.code === 200) {
          let products = res.data.data
          // 处理图片路径，添加完整的基础URL
          products = products.map(product => {
            if (product.imageUrl && product.imageUrl.startsWith('/')) {
              product.imageUrl = `${getApp().globalData.baseUrl}${product.imageUrl}`
            }
            return product
          })
          this.setData({
            products: products,
            currentCategory: null
          })
        }
      }
    })
  },

  resetSearch: function() {
    this.setData({
      searchKeyword: '',
      currentCategory: 1
    })
    this.loadProducts(1)
  },

  goToSearch: function() {
    wx.navigateTo({
      url: '/pages/search/search'
    })
  },

  goToProductDetail: function(e) {
    const productId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/product-detail/product-detail?id=${productId}`
    })
  },

  onBannerTap: function(e) {
    const productId = e.currentTarget.dataset.productId
    if (productId) {
      wx.navigateTo({
        url: `/pages/product-detail/product-detail?id=${productId}`
      })
    }
  },

  increaseQuantity: function(e) {
    const productId = e.currentTarget.dataset.id
    const product = this.data.products.find(p => p.id === productId)
    if (product) {
      getApp().addToCart(product)
      this.loadCart()
    }
  },

  decreaseQuantity: function(e) {
    const productId = e.currentTarget.dataset.id
    getApp().removeFromCart(productId)
    this.loadCart()
  },

  getProductQuantity: function(productId) {
    const item = this.data.cart.find(item => item.id === productId)
    return item ? item.quantity : 0
  },

  getTotalQuantity: function() {
    return this.data.cart.reduce((total, item) => total + item.quantity, 0)
  },




  getCartTotal: function() {
    return getApp().getCartTotal()
  },

  getDeliveryFee: function() {
    return getApp().getDeliveryFee()
  },

  getFinalTotal: function() {
    return this.getCartTotal() + this.getDeliveryFee()
  },

  getDeliveryFeeText: function() {
    const fee = this.getDeliveryFee()
    return fee > 0 ? `配送费¥${fee}` : '免配送费'
  },

  toggleCart: function() {
    this.setData({
      showCart: !this.data.showCart
    })
  },

  stopPropagation: function() {
    // 阻止事件冒泡
  },

  clearCart: function() {
    wx.showModal({
      title: '提示',
      content: '确定要清空购物车吗？',
      success: (res) => {
        if (res.confirm) {
          getApp().globalData.cart = []
          getApp().updateCartStorage()
          this.setData({
            cart: [],
            cartTotal: 0,
            deliveryFee: 0,
            finalTotal: 0,
            totalQuantity: 0,
            productQuantityMap: {}
          })
        }
      }
    })
  },

  goToCheckout: function() {
    if (this.data.cart.length === 0) {
      wx.showToast({
        title: '购物车为空',
        icon: 'none'
      })
      return
    }

    // 检查营业时间
    if (!this.data.isOpen) {
      wx.showToast({
        title: this.data.deliveryNotice,
        icon: 'none'
      })
      return
    }

    // 检查是否已登录
    if (!wx.getStorageSync('token')) {
      wx.navigateTo({
        url: '/pages/login/login'
      })
      return
    }

    // 检查收货地址
    wx.request({
      url: `${getApp().globalData.baseUrl}/api/address`,
      method: 'GET',
      data: { userId: getApp().globalData.userInfo?.id },
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      success: (res) => {
        if (res.data.code === 200 && res.data.data && res.data.data.length > 0) {
          // 有收货地址，检查夜间配送规则
          if (this.data.nightDelivery) {
            const addresses = res.data.data
            const defaultAddress = addresses.find(item => item.isDefault) || addresses[0]
            if (defaultAddress) {
              const buildingNumber = defaultAddress.building
              if (buildingNumber > 3) {
                wx.showToast({
                  title: '当前为夜间配送时间，仅配送1-3号楼',
                  icon: 'none'
                })
                return
              }
            }
          }
          // 获取默认地址
          const addresses = res.data.data
          const defaultAddress = addresses.find(item => item.isDefault) || addresses[0]
          // 直接下单，使用默认地址
          this.createOrder(defaultAddress.id)
        } else {
          // 没有收货地址，跳转到地址管理
          wx.navigateTo({
            url: '/pages/address/address'
          })
        }
      }
    })
  },

  createOrder: function(addressId) {
    if (!addressId) {
      wx.showToast({
        title: '请先添加收货地址',
        icon: 'none'
      })
      return
    }
    
    wx.request({
      url: `${getApp().globalData.baseUrl}/api/orders`,
      method: 'POST',
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      data: {
        userId: getApp().globalData.userInfo?.id,
        addressId: addressId,
        items: this.data.cart,
        totalAmount: this.data.cartTotal,
        deliveryFee: this.data.deliveryFee
      },
      success: (res) => {
        if (res.data.code === 200) {
          getApp().globalData.cart = []
          getApp().updateCartStorage()
          this.setData({
            cart: [],
            cartTotal: 0,
            deliveryFee: 0,
            finalTotal: 0,
            totalQuantity: 0,
            productQuantityMap: {}
          })
          
          wx.showToast({
            title: '订单创建成功',
            icon: 'success'
          })
          
          setTimeout(() => {
            wx.switchTab({
              url: '/pages/orders/orders'
            })
          }, 1500)
        } else {
          wx.showToast({
            title: res.data.message || '订单创建失败',
            icon: 'none'
          })
        }
      }
    })
  }
})