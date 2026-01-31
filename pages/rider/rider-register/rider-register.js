// pages/rider/rider-register/rider-register.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    name: '',
    phone: '',
    password: '',
    idNumber: '',
    idFrontUrl: '',
    idBackUrl: '',
    idFrontBase64: '',
    idBackBase64: ''
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    const app = getApp()
    console.log('页面加载时的 userInfo:', app.globalData.userInfo)
    
    // 检查用户是否已登录
    const token = wx.getStorageSync('token')
    if (!token || !app.globalData.userInfo || !app.globalData.userInfo.id) {
      console.log('用户未登录，跳转到登录页')
      wx.showModal({
        title: '提示',
        content: '请先进行微信登录',
        showCancel: false,
        success: () => {
          wx.navigateTo({
            url: '/pages/login/login'
          })
        }
      })
      return
    }
    
    // 如果用户信息不完整（特别是ID），尝试从服务器获取最新信息
    if (!app.globalData.userInfo.id) {
      console.log('userInfo ID 缺失，尝试从服务器获取最新信息')
      
      wx.request({
        url: `${app.globalData.baseUrl}/api/auth/user-info`,
        method: 'GET',
        header: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        success: (res) => {
          if (res.statusCode === 200 && res.data.code === 200) {
            app.globalData.userInfo = res.data.data.userInfo
            console.log('从服务器获取的最新 userInfo:', app.globalData.userInfo)
          } else {
            console.log('获取用户信息失败:', res)
          }
        },
        fail: (err) => {
          console.error('请求用户信息失败:', err)
        }
      })
    }
  },
  
  /**
   * 页面显示时检查登录状态
   */
  onShow() {
    const app = getApp()
    console.log('页面显示时的 userInfo:', app.globalData.userInfo)
  },

  /**
   * 姓名输入绑定
   */
  bindNameInput(e) {
    this.setData({
      name: e.detail.value
    })
  },

  /**
   * 手机号输入绑定
   */
  bindPhoneInput(e) {
    this.setData({
      phone: e.detail.value
    })
  },

  /**
   * 密码输入绑定
   */
  bindPasswordInput(e) {
    this.setData({
      password: e.detail.value
    })
  },

  /**
   * 身份证号输入绑定
   */
  bindIdNumberInput(e) {
    this.setData({
      idNumber: e.detail.value
    })
  },

  /**
   * 上传身份证人像面
   */
  uploadIdFront() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePaths = res.tempFilePaths
        this.setData({
          idFrontUrl: tempFilePaths[0]
        })
        // 压缩图片
        wx.compressImage({
          src: tempFilePaths[0],
          quality: 60,
          success: (compressRes) => {
            console.log('身份证正面图片压缩成功:', compressRes.tempFilePath)
            // 将压缩后的图片转换为Base64
            this.convertImageToBase64(compressRes.tempFilePath, (base64) => {
              console.log('身份证正面Base64长度:', base64.length)
              this.setData({
                idFrontBase64: base64
              })
            })
          },
          fail: (err) => {
            console.error('图片压缩失败', err)
            wx.showToast({
              title: '图片压缩失败',
              icon: 'none'
            })
          }
        })
      }
    })
  },

  /**
   * 上传身份证国徽面
   */
  uploadIdBack() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePaths = res.tempFilePaths
        this.setData({
          idBackUrl: tempFilePaths[0]
        })
        // 压缩图片
        wx.compressImage({
          src: tempFilePaths[0],
          quality: 60,
          success: (compressRes) => {
            console.log('身份证反面图片压缩成功:', compressRes.tempFilePath)
            // 将压缩后的图片转换为Base64
            this.convertImageToBase64(compressRes.tempFilePath, (base64) => {
              console.log('身份证反面Base64长度:', base64.length)
              this.setData({
                idBackBase64: base64
              })
            })
          },
          fail: (err) => {
            console.error('图片压缩失败', err)
            wx.showToast({
              title: '图片压缩失败',
              icon: 'none'
            })
          }
        })
      }
    })
  },

  /**
   * 将图片转换为Base64
   */
  convertImageToBase64(imagePath, callback) {
    wx.getFileSystemManager().readFile({
      filePath: imagePath,
      encoding: 'base64',
      success: (res) => {
        callback('data:image/jpeg;base64,' + res.data)
      },
      fail: (err) => {
        console.error('图片转换失败', err)
        wx.showToast({
          title: '图片上传失败',
          icon: 'none'
        })
      }
    })
  },

  /**
   * 提交申请
   */
  submitApplication() {
    console.log('=== submitApplication 函数被调用 ===')
    const { name, phone, password, idNumber, idFrontUrl, idBackUrl, idFrontBase64, idBackBase64 } = this.data
    console.log('表单数据:', { name, phone, password, idNumber, idFrontUrl, idBackUrl, idFrontBase64: !!idFrontBase64, idBackBase64: !!idBackBase64 })
    
    // 表单验证
    if (!name) {
      console.log('验证失败：姓名为空')
      wx.showToast({
        title: '请输入姓名',
        icon: 'none'
      })
      return
    }
    
    if (!phone) {
      wx.showToast({
        title: '请输入手机号',
        icon: 'none'
      })
      return
    }
    
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({
        title: '手机号格式不正确',
        icon: 'none'
      })
      return
    }
    
    if (!password) {
      wx.showToast({
        title: '请设置密码',
        icon: 'none'
      })
      return
    }
    
    if (password.length < 6) {
      wx.showToast({
        title: '密码长度至少6位',
        icon: 'none'
      })
      return
    }
    
    if (!idNumber) {
      wx.showToast({
        title: '请输入身份证号',
        icon: 'none'
      })
      return
    }
    
    if (!/^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/.test(idNumber)) {
      wx.showToast({
        title: '身份证号格式不正确',
        icon: 'none'
      })
      return
    }
    
    if (!idFrontUrl || !idFrontBase64) {
      wx.showToast({
        title: '请上传身份证人像面',
        icon: 'none'
      })
      return
    }
    
    if (!idBackUrl || !idBackBase64) {
      wx.showToast({
        title: '请上传身份证国徽面',
        icon: 'none'
      })
      return
    }
    
    // 检查登录状态
    const app = getApp()
    console.log('提交时检查 userInfo:', app.globalData.userInfo)
    
    // 如果用户信息为空，尝试重新获取
    if (!app.globalData.userInfo) {
      wx.showModal({
        title: '未登录',
        content: '请先登录后再申请成为骑手',
        showCancel: false,
        success: () => {
          wx.navigateTo({
            url: '/pages/login/login'
          })
        }
      })
      return
    }
    
    // 强制刷新用户信息以确保ID是最新的
    const token = wx.getStorageSync('token')
    console.log('准备获取用户信息，token:', token)
    
    if (!token) {
      wx.showModal({
        title: '未登录',
        content: '请先登录后再申请成为骑手',
        showCancel: false,
        success: () => {
          wx.navigateTo({
            url: '/pages/login/login'
          })
        }
      })
      return
    }
    
    // 直接调用API获取最新用户信息
    wx.request({
      url: `${app.globalData.baseUrl}/api/auth/user-info`,
      method: 'GET',
      header: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      success: (res) => {
        console.log('获取用户信息响应:', res)
        
        if (res.statusCode === 200 && res.data.code === 200) {
          // 更新全局用户信息
          app.globalData.userInfo = res.data.data.userInfo
          
          console.log('从服务器获取的完整用户信息:', res.data.data.userInfo)
          console.log('用户信息的所有键:', Object.keys(res.data.data.userInfo || {}))
          
          const userId = app.globalData.userInfo.id
          console.log('获取到的用户ID:', userId, '类型:', typeof userId)
          
          if (!userId) {
            console.log('用户ID缺失，用户信息详情:', {
              userInfo: app.globalData.userInfo,
              id: app.globalData.userInfo?.id,
              hasUserInfo: !!app.globalData.userInfo
            })
            
            wx.showModal({
              title: '用户信息异常',
              content: '无法获取用户ID，请重新登录',
              showCancel: false,
              success: () => {
                wx.navigateTo({
                  url: '/pages/login/login'
                })
              }
            })
            return
          }
          
          // 构建请求数据（临时使用固定ID进行测试）
          console.log('=== 开始构建请求数据 ===')
          console.log('name:', name)
          console.log('phone:', phone)
          console.log('password:', password ? '已设置' : 'null')
          console.log('idNumber:', idNumber)
          console.log('idFrontBase64:', idFrontBase64 ? '已设置' : 'null')
          console.log('idBackBase64:', idBackBase64 ? '已设置' : 'null')
          
          const requestData = {
            userId: userId,
            name: name,
            phone: phone,
            password: password,
            idCard: idNumber,
            idCardFront: idFrontBase64,
            idCardBack: idBackBase64
          }
          
          console.log('=== 请求数据构建完成 ===')
          console.log('requestData:', requestData)
          
          // 调试输出
          console.log('=== 准备提交的数据 ===')
          console.log('requestData:', requestData)
          console.log('完整的userInfo:', app.globalData.userInfo)
          console.log('所有字段:', Object.keys(requestData))
          
          // 再次验证所有必需字段都存在（不再验证userId）
          console.log('验证字段 - name:', !!requestData.name, 
                     'phone:', !!requestData.phone, 'idCard:', !!requestData.idCard, 
                     'idCardFront:', !!requestData.idCardFront, 'idCardBack:', !!requestData.idCardBack)
          
          if (!requestData.name || !requestData.phone || 
              !requestData.idCard || !requestData.idCardFront || !requestData.idCardBack) {
            console.log('检测到缺少必要字段，实际值:', {
              name: requestData.name,
              phone: requestData.phone,
              idCard: requestData.idCard,
              idCardFront: requestData.idCardFront,
              idCardBack: requestData.idCardBack
            })
            wx.showToast({
              title: '数据不完整，请检查后重试',
              icon: 'none'
            })
            return
          }
          
          // 显示加载状态
          wx.showLoading({
            title: '提交中...'
          })
          
          // 调用后端接口
          const requestToken = wx.getStorageSync('token')
          console.log('发送请求时的token:', requestToken)
          console.log('完整的Authorization头部:', `Bearer ${requestToken}`)
          console.log('请求URL:', `${app.globalData.baseUrl}/api/auth/rider-apply`)
          console.log('请求数据:', requestData)
          
          wx.request({
            url: `${app.globalData.baseUrl}/api/auth/rider-apply`,
            method: 'POST',
            data: requestData,
            header: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${requestToken}`  // 直接从存储获取token
            },
            success: (res) => {
              console.log('请求成功响应:', res)
              wx.hideLoading()
              
              if (res.statusCode === 200 && res.data.code === 200) {
                // 提交成功
                wx.showModal({
                  title: '提交成功',
                  content: '您的申请已提交，正在审核中，预计24小时内完成审核',
                  showCancel: false,
                  success: () => {
                    // 返回上一页
                    wx.navigateBack()
                  }
                })
              } else {
                // 提交失败
                console.log('提交失败，响应数据:', res.data)
                wx.showToast({
                  title: res.data.message || '提交失败，请重试',
                  icon: 'none'
                })
              }
            },
            fail: (err) => {
              console.error('请求失败', err)
              wx.hideLoading()
              wx.showToast({
                title: '网络错误，请稍后重试',
                icon: 'none'
              })
            }
          })
        } else {
          wx.showModal({
            title: '获取用户信息失败',
            content: '无法获取用户信息，请重新登录',
            showCancel: false,
            success: () => {
              wx.navigateTo({
                url: '/pages/login/login'
              })
            }
          })
        }
      },
      fail: (err) => {
        console.error('获取用户信息失败:', err)
        wx.showModal({
          title: '网络错误',
          content: '无法连接服务器获取用户信息，请检查网络后重试',
          showCancel: false
        })
      }
    })
  }
})