Page({
  data: {
    buildings: ['1号楼', '2号楼', '3号楼', '4号楼', '5号楼', '6号楼', '7号楼', '8号楼'],
    floors: ['1层', '2层', '3层', '4层', '5层', '6层', '7层', '8层', '9层', '10层'],
    addresses: [],
    editingAddress: null,
    showAddForm: false,
    showEditForm: false,
    formData: {
      buildingIndex: -1,
      floorIndex: -1,
      detailAddress: '',
      recipientName: '',
      phone: '',
      isDefault: false
    },
    canSave: false
  },

  onLoad: function() {
    this.loadAddresses()
  },

  onShow: function() {
    this.loadAddresses()
  },

  loadAddresses: function() {
    // 使用真实的用户ID
    const userInfo = getApp().globalData.userInfo;
    if (!userInfo || !userInfo.id) {
      console.error('用户未登录或用户信息不完整');
      wx.showToast({
        title: '请先登录',
        icon: 'error'
      });
      return;
    }
    
    const userId = userInfo.id;
    console.log('获取地址列表使用的用户ID:', userId);
    
    wx.request({
      url: `${getApp().globalData.baseUrl}/api/address`,
      method: 'GET',
      data: { userId: userId },
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      success: (res) => {
        console.log('获取地址列表响应:', res);
        console.log('响应状态码:', res.statusCode);
        console.log('响应数据:', res.data);
        console.log('响应数据.code:', res.data.code);
        console.log('响应数据.data:', res.data.data);
        console.log('响应数据.data类型:', typeof res.data.data);
        console.log('响应数据.data是否为数组:', Array.isArray(res.data.data));
        console.log('响应数据.data长度:', res.data.data ? res.data.data.length : 0);
        
        if (res.data.code === 200) {
          const addresses = res.data.data || [];
          console.log('准备设置的地址列表:', addresses);
          console.log('地址列表是否为数组:', Array.isArray(addresses));
          console.log('地址列表长度:', addresses.length);
          
          this.setData({
            addresses: addresses
          });
          
          console.log('setData后的地址列表:', this.data.addresses);
          console.log('setData后的地址列表长度:', this.data.addresses.length);
        } else {
          console.error('获取地址列表失败:', res.data);
          this.setData({
            addresses: []
          });
        }
      },
      fail: (err) => {
        console.error('获取地址列表请求失败:', err);
        wx.showToast({
          title: '获取地址列表失败',
          icon: 'error'
        });
        this.setData({
          addresses: []
        });
      }
    })
  },

  onAddAddress: function() {
    this.setData({
      showAddForm: true,
      showEditForm: false,
      editingAddress: null,
      formData: {
        buildingIndex: -1,
        floorIndex: -1,
        detailAddress: '',
        recipientName: '',
        phone: '',
        isDefault: false
      },
      canSave: false
    })
  },

  onEditAddress: function(e) {
    const addressId = e.currentTarget.dataset.id
    const address = this.data.addresses.find(item => item.id === addressId)
    if (address) {
      // 修复索引查找问题：将数字转换为对应的字符串索引
      const buildingIndex = address.building ? address.building - 1 : -1
      const floorIndex = address.floor ? address.floor - 1 : -1
      
      this.setData({
        showAddForm: false,
        showEditForm: true,
        editingAddress: address,
        formData: {
          buildingIndex: buildingIndex,
          floorIndex: floorIndex,
          detailAddress: address.detail || '',
          recipientName: address.name || '',
          phone: address.phone || '',
          isDefault: address.isDefault || false
        }
      })
      this.checkForm()
    }
  },

  onDeleteAddress: function(e) {
    const addressId = e.currentTarget.dataset.id
    wx.showModal({
      title: '删除地址',
      content: '确定要删除这个地址吗？',
      success: (res) => {
        if (res.confirm) {
          wx.request({
            url: `${getApp().globalData.baseUrl}/api/address`,
            method: 'DELETE',
            data: { id: addressId },
            header: {
              'Authorization': `Bearer ${wx.getStorageSync('token')}`
            },
            success: (res) => {
              if (res.data.code === 200) {
                wx.showToast({
                  title: '删除成功',
                  icon: 'success'
                })
                this.loadAddresses()
              } else {
                wx.showToast({
                  title: '删除失败',
                  icon: 'error'
                })
              }
            }
          })
        }
      }
    })
  },

  onSetDefault: function(e) {
    const addressId = e.currentTarget.dataset.id
    wx.request({
      url: `${getApp().globalData.baseUrl}/api/address/default`,
      method: 'PUT',
      data: {
        addressId: addressId,
        userId: getApp().globalData.userInfo?.id
      },
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`
      },
      success: (res) => {
        if (res.data.code === 200) {
          wx.showToast({
            title: '设置默认地址成功',
            icon: 'success'
          })
          this.loadAddresses()
        } else {
          wx.showToast({
            title: '设置失败',
            icon: 'error'
          })
        }
      }
    })
  },

  onBuildingChange: function(e) {
    const formData = { ...this.data.formData }
    formData.buildingIndex = parseInt(e.detail.value)
    this.setData({ formData })
    this.checkForm()
    console.log('楼栋选择变化:', e.detail.value, '当前buildingIndex:', formData.buildingIndex)
  },

  onFloorChange: function(e) {
    const formData = { ...this.data.formData }
    formData.floorIndex = parseInt(e.detail.value)
    this.setData({ formData })
    this.checkForm()
    console.log('楼层选择变化:', e.detail.value, '当前floorIndex:', formData.floorIndex)
  },

  onDetailAddressInput: function(e) {
    const formData = { ...this.data.formData }
    formData.detailAddress = e.detail.value
    this.setData({ formData })
    this.checkForm()
    console.log('详细地址输入:', e.detail.value)
  },

  onRecipientNameInput: function(e) {
    const formData = { ...this.data.formData }
    formData.recipientName = e.detail.value
    this.setData({ formData })
    this.checkForm()
    console.log('收件人姓名输入:', e.detail.value)
  },

  onPhoneInput: function(e) {
    const formData = { ...this.data.formData }
    formData.phone = e.detail.value
    this.setData({ formData })
    this.checkForm()
    console.log('手机号码输入:', e.detail.value)
  },

  onDefaultChange: function(e) {
    const formData = { ...this.data.formData }
    formData.isDefault = e.detail.value
    this.setData({ formData })
    console.log('默认地址开关:', e.detail.value)
  },

  checkForm: function() {
    const { formData } = this.data
    const userInfo = getApp().globalData.userInfo
    const canSave = userInfo && userInfo.id && 
                   formData.buildingIndex >= 0 && formData.floorIndex >= 0 && 
                   formData.detailAddress && formData.recipientName && 
                   formData.phone && this.validatePhone(formData.phone)
    this.setData({ canSave })
    console.log('表单验证结果:', {
      userInfo: userInfo,
      userId: userInfo?.id,
      buildingIndex: formData.buildingIndex,
      floorIndex: formData.floorIndex,
      detailAddress: formData.detailAddress,
      recipientName: formData.recipientName,
      phone: formData.phone,
      phoneValid: this.validatePhone(formData.phone),
      canSave: canSave
    })
  },

  validatePhone: function(phone) {
    return /^1[3-9]\d{9}$/.test(phone)
  },

  saveAddress: function() {
    console.log('保存地址按钮被点击')
    console.log('当前canSave状态:', this.data.canSave)
    console.log('当前formData:', this.data.formData)
    console.log('当前userInfo:', getApp().globalData.userInfo)
    
    if (!this.data.canSave) {
      console.log('表单验证未通过，无法保存')
      wx.showToast({
        title: '请填写完整信息',
        icon: 'none'
      })
      return
    }

    const { formData, buildings, floors, editingAddress } = this.data
    const isEdit = !!editingAddress
    const method = isEdit ? 'PUT' : 'POST'
    
    // 转换楼栋和楼层为整数（索引+1，因为索引从0开始）
    const buildingNumber = formData.buildingIndex + 1;
    const floorNumber = formData.floorIndex + 1;
    
    // 使用真实的用户ID
    const userInfo = getApp().globalData.userInfo;
    if (!userInfo || !userInfo.id) {
      wx.showToast({
        title: '请先登录',
        icon: 'error'
      });
      return;
    }
    
    const userId = userInfo.id;
    console.log('使用的用户ID:', userId);
    
    const addressData = {
      userId: userId,
      name: formData.recipientName,
      phone: formData.phone,
      building: buildingNumber,
      floor: floorNumber,
      detail: formData.detailAddress,
      isDefault: formData.isDefault
    }

    if (isEdit) {
      addressData.id = editingAddress.id
    }

    // 打印发送的数据，用于调试
    console.log('发送的地址数据:', addressData);
    console.log('请求URL:', `${getApp().globalData.baseUrl}/api/address`)
    console.log('请求方法:', method)
    console.log('请求头:', {
      'Authorization': `Bearer ${wx.getStorageSync('token')}`,
      'Content-Type': 'application/json'
    })
    
    wx.request({
      url: `${getApp().globalData.baseUrl}/api/address`,
      method: method,
      header: {
        'Authorization': `Bearer ${wx.getStorageSync('token')}`,
        'Content-Type': 'application/json'
      },
      data: addressData,
      success: (res) => {
        console.log('地址保存响应:', res);
        if (res.data.code === 200) {
          // 设置hasAddress标志，避免首次登录重复引导
          wx.setStorageSync('hasAddress', true);
          wx.showToast({
            title: isEdit ? '地址更新成功' : '地址添加成功',
            icon: 'success'
          })
          setTimeout(() => {
            this.setData({
              showAddForm: false,
              showEditForm: false,
              editingAddress: null
            })
            this.loadAddresses()
          }, 1500)
        } else {
          wx.showToast({
            title: res.data.message || (isEdit ? '更新失败' : '添加失败'),
            icon: 'none'
          })
        }
      },
      fail: (err) => {
        console.log('地址保存失败:', err);
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        })
      }
    })
  },

  onCancel: function() {
    this.setData({
      showAddForm: false,
      showEditForm: false,
      editingAddress: null
    })
  },

  onBack: function() {
    wx.navigateBack()
  }
})