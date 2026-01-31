Page({
  data: {
    searchKeyword: '',
    searchResults: [],
    searchHistory: [],
    hotSearch: ['奶茶', '汉堡', '炸鸡', '披萨', '寿司', '蛋糕', '咖啡', '可乐'],
    newProducts: [],
    recommendProducts: [],
    recommendTitle: '热门推荐',
    randomTip: '',
    purchasedProducts: []
  },

  onLoad: function() {
    this.initPage()
  },

  onShow: function() {
    this.initPage()
  },

  initPage: function() {
    this.setData({
      searchKeyword: '',
      searchResults: []
    })
    this.loadSearchHistory()
    this.loadRecommendProducts()
    this.loadNewProducts()
    this.loadPurchasedProducts()
  },

  loadNewProducts: function() {
    wx.request({
      url: `${getApp().globalData.baseUrl}/api/products`,
      method: 'GET',
      success: (res) => {
        if (res.data.code === 200) {
          let products = res.data.data || []
          // 处理图片路径，添加完整的基础URL
          products = products.map(product => {
            if (product.imageUrl && product.imageUrl.startsWith('/')) {
              product.imageUrl = `${getApp().globalData.baseUrl}${product.imageUrl}`
            }
            return product
          })
          const now = new Date()
          const oneWeekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)
          
          // 筛选一周内的新品
          const newProducts = products.filter(product => {
            if (product.createdAt) {
              const productDate = new Date(product.createdAt)
              return productDate >= oneWeekAgo
            }
            return false
          })
          
          this.setData({ newProducts: newProducts })
        }
      }
    })
  },

  loadSearchHistory: function() {
    const history = wx.getStorageSync('searchHistory') || []
    this.setData({ searchHistory: history })
  },

  saveSearchHistory: function(keyword) {
    let history = wx.getStorageSync('searchHistory') || []
    
    // 移除重复项
    history = history.filter(item => item !== keyword)
    
    // 添加到开头
    history.unshift(keyword)
    
    // 只保留最近10条
    if (history.length > 10) {
      history = history.slice(0, 10)
    }
    
    wx.setStorageSync('searchHistory', history)
    this.setData({ searchHistory: history })
  },

  loadRecommendProducts: function() {
    wx.request({
      url: `${getApp().globalData.baseUrl}/api/products`,
      method: 'GET',
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
            recommendProducts: products
          })
        }
      }
    })
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
            purchasedProducts: res.data.data
          })
          this.generateRandomTip()
        }
      }
    })
  },

  generateRandomTip: function() {
    const tips = [
      '试试搜索"奶茶"、"汉堡"等关键词',
      '满10元免配送费，多买多优惠～',
      '收藏喜欢的商品，下次购买更方便！'
    ]
    
    if (this.data.purchasedProducts.length > 0) {
      // 如果用户购买过商品，显示购买过的商品推荐
      const topProduct = this.data.purchasedProducts[0]
      this.setData({
        recommendTitle: '为您推荐',
        randomTip: `根据您的购买记录，推荐您可能喜欢的商品`
      })
    } else {
      // 显示随机提示
      const randomIndex = Math.floor(Math.random() * tips.length)
      this.setData({
        recommendTitle: '热门推荐',
        randomTip: tips[randomIndex]
      })
    }
  },

  onHotTagTap: function(e) {
    const keyword = e.currentTarget.dataset.keyword
    this.setData({ searchKeyword: keyword })
    this.onSearch()
  },

  resetSearch: function() {
    this.setData({
      searchKeyword: '',
      searchResults: []
    })
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

    this.saveSearchHistory(keyword)
    
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
            searchResults: products
          })
        }
      }
    })
  },

  onHistoryTagTap: function(e) {
    const keyword = e.currentTarget.dataset.keyword
    this.setData({ searchKeyword: keyword })
    this.onSearch()
  },

  clearSearchHistory: function() {
    wx.showModal({
      title: '提示',
      content: '确定要清空搜索历史吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('searchHistory')
          this.setData({ searchHistory: [] })
        }
      }
    })
  },

  goToProductDetail: function(e) {
    const productId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/product-detail/product-detail?id=${productId}`
    })
  }
})