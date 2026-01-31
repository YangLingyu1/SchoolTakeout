Page({
  data: {
    currentTab: 'all',
    orders: [],
    isRefreshing: false,
    showOrderDetail: false,
    currentOrder: null
  },

  onLoad: function() {
    this.loadOrders()
  },

  onShow: function() {
    this.loadOrders()
  },

  loadOrders: function() {
    let status = ''
    if (this.data.currentTab !== 'all') {
      const statusMap = {
        'pending': 0,
        'delivering': 2,
        'completed': 3
      }
      status = statusMap[this.data.currentTab] || ''
    }
    
    console.log('加载订单列表，当前标签页:', this.data.currentTab, '状态码:', status);
    console.log('请求URL:', `${getApp().globalData.baseUrl}/api/orders`);
    
    wx.request({
      url: `${getApp().globalData.baseUrl}/api/orders`,
      method: 'GET',
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      data: { 
        userId: getApp().globalData.userInfo?.id || 1,
        status 
      },
      success: (res) => {
        console.log('获取订单列表响应:', res);
        if (res.data.code === 200) {
          let allOrders = res.data.data || []
          console.log('原始订单数据:', allOrders);
          
          // 前端进行状态筛选
          let filteredOrders = allOrders
          if (this.data.currentTab !== 'all') {
            const statusMap = {
              'pending': 0,
              'delivering': 2,
              'completed': 3
            }
            const targetStatus = statusMap[this.data.currentTab]
            filteredOrders = allOrders.filter(order => order.status === targetStatus)
            console.log('筛选后的订单数据:', filteredOrders);
          }
          
          // 处理订单中商品的图片路径
          filteredOrders = filteredOrders.map(order => {
            if (order.items && Array.isArray(order.items)) {
              order.items = order.items.map(item => {
                if (item.image && item.image.startsWith('/')) {
                  item.image = `${getApp().globalData.baseUrl}${item.image}`
                }
                return item
              })
            }
            return order
          })
          
          this.setData({
            orders: filteredOrders
          })
          console.log('设置后的数据:', this.data.orders);
        } else {
          console.error('获取订单列表失败:', res.data);
          this.setData({
            orders: []
          });
        }
      },
      fail: (err) => {
        console.error('获取订单列表请求失败:', err);
        this.setData({
          orders: []
        });
      },
      complete: () => {
        this.setData({ isRefreshing: false })
        console.log('加载订单列表完成');
      }
    })
  },

  switchTab: function(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({ currentTab: tab })
    this.loadOrders()
  },

  onRefresh: function() {
    this.setData({ isRefreshing: true })
    this.loadOrders()
  },

  getStatusText: function(status) {
    const statusMap = {
      0: '待接单',
      1: '配送中',
      2: '已完成',
      3: '已取消'
    }
    return statusMap[status] || '未知状态'
  },

  getStatusType: function(status) {
    const statusMap = {
      0: 'warning',
      1: 'warning',
      2: 'success',
      3: 'danger'
    }
    return statusMap[status] || 'info'
  },

  cancelOrder: function(e) {
    const orderId = e.currentTarget.dataset.id
    
    wx.showModal({
      title: '取消订单',
      content: '确定要取消这个订单吗？',
      success: (res) => {
        if (res.confirm) {
          wx.request({
            url: `${getApp().globalData.baseUrl}/api/orders/status`,
            method: 'PUT',
            header: {
              'Authorization': `Bearer ${wx.getStorageSync('token')}`
            },
            data: {
              orderId: orderId,
              status: 4
            },
            success: (res) => {
              if (res.data.code === 200) {
                wx.showToast({
                  title: '订单已取消',
                  icon: 'success'
                })
                this.loadOrders()
              } else {
                wx.showToast({
                  title: res.data.message || '取消失败',
                  icon: 'none'
                })
              }
            },
            fail: () => {
              wx.showToast({
                title: '网络错误',
                icon: 'error'
              })
            }
          })
        }
      }
    })
  },

  contactRider: function(e) {
    const orderId = e.currentTarget.dataset.id
    const order = this.data.orders.find(o => o.id === orderId)
    
    if (!order) {
      wx.showToast({
        title: '订单不存在',
        icon: 'none'
      })
      return
    }
    
    if (order.status !== 2) {
      wx.showToast({
        title: '订单未配送中，无法联系骑手',
        icon: 'none'
      })
      return
    }
    
    // 获取订单详情以获取骑手信息
    wx.request({
      url: `${getApp().globalData.baseUrl}/api/orders/${orderId}`,
      method: 'GET',
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      success: (res) => {
        console.log('订单详情API返回数据:', res)
        console.log('订单详情数据:', res.data)
        console.log('订单详情data字段:', res.data.data)
        
        if (res.data.code === 200 && res.data.data) {
          const orderDetail = res.data.data
          console.log('orderDetail:', orderDetail)
          console.log('orderDetail.rider:', orderDetail.rider)
          
          if (!orderDetail.rider || !orderDetail.rider.phone) {
            console.log('骑手信息缺失:', orderDetail.rider)
            wx.showToast({
              title: '暂无骑手联系方式',
              icon: 'none'
            })
            return
          }
          
          console.log('准备拨打骑手电话:', orderDetail.rider.phone)
          wx.makePhoneCall({
            phoneNumber: orderDetail.rider.phone
          })
        } else {
          wx.showToast({
            title: '获取订单详情失败',
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

  reorder: function(e) {
    const orderId = e.currentTarget.dataset.id
    
    wx.request({
      url: `${getApp().globalData.baseUrl}/api/orders/${orderId}`,
      method: 'GET',
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      success: (res) => {
        if (res.data.code === 200 && res.data.data) {
          const orderDetail = res.data.data
          if (orderDetail.items && orderDetail.items.length > 0) {
            // 将订单商品添加到购物车
            orderDetail.items.forEach((item) => {
              for (let i = 0; i < item.quantity; i++) {
                getApp().addToCart({
                  id: item.productId,
                  name: item.productName,
                  price: item.price,
                  imageUrl: item.imageUrl || ''
                })
              }
            })
            
            wx.showToast({
              title: '已添加到购物车',
              icon: 'success'
            })
            
            // 跳转到首页
            setTimeout(() => {
              wx.switchTab({
                url: '/pages/index/index'
              })
            }, 1500)
          }
        }
      },
      fail: () => {
        wx.showToast({
          title: '获取订单详情失败',
          icon: 'error'
        })
      }
    })
  },

  viewOrderDetail: function(e) {
    const orderId = e.currentTarget.dataset.id
    
    wx.request({
      url: `${getApp().globalData.baseUrl}/api/orders/${orderId}`,
      method: 'GET',
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      success: (res) => {
        if (res.data.code === 200 && res.data.data) {
          this.setData({
            currentOrder: res.data.data,
            showOrderDetail: true
          })
        }
      },
      fail: () => {
        wx.showToast({
          title: '获取订单详情失败',
          icon: 'error'
        })
      }
    })
  },

  closeOrderDetail: function() {
    this.setData({
      showOrderDetail: false,
      currentOrder: null
    })
  },

  payOrder: function(e) {
    const orderId = e.currentTarget.dataset.id
    
    wx.showModal({
      title: '确认支付',
      content: '确定要支付这个订单吗？',
      success: (res) => {
        if (res.confirm) {
          // 模拟支付过程
          wx.showLoading({ title: '支付中...' })
          
          setTimeout(() => {
            wx.hideLoading()
            wx.showToast({
              title: '支付成功',
              icon: 'success'
            })
            
            // 更新订单状态
            wx.request({
              url: `${getApp().globalData.baseUrl}/api/orders/status`,
              method: 'PUT',
              header: {
                'Authorization': `Bearer ${wx.getStorageSync('token')}`
              },
              data: {
                orderId: orderId,
                status: 1
              },
              success: () => {
                this.loadOrders()
              }
            })
          }, 1500)
        }
      }
    })
  },

  deleteOrder: function(e) {
    const orderId = e.currentTarget.dataset.id
    
    wx.showModal({
      title: '删除订单',
      content: '确定要删除这个订单吗？',
      success: (res) => {
        if (res.confirm) {
          wx.request({
            url: `${getApp().globalData.baseUrl}/api/orders/${orderId}`,
            method: 'DELETE',
            header: {
              'Authorization': `Bearer ${wx.getStorageSync('token')}`
            },
            success: (res) => {
              if (res.data.code === 200) {
                wx.showToast({
                  title: '订单已删除',
                  icon: 'success'
                })
                this.loadOrders()
              } else {
                wx.showToast({
                  title: res.data.message || '删除失败',
                  icon: 'none'
                })
              }
            },
            fail: () => {
              wx.showToast({
                title: '网络错误',
                icon: 'error'
              })
            }
          })
        }
      }
    })
  }
})