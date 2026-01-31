Component({
  properties: {
    title: {
      type: String,
      value: ''
    },
    backgroundColor: {
      type: String,
      value: '#FF6B35'
    },
    textColor: {
      type: String,
      value: '#ffffff'
    }
  },

  data: {
    statusBarHeight: 0,
    navBarHeight: 0
  },

  lifetimes: {
    attached() {
      const systemInfo = wx.getSystemInfoSync();
      this.setData({
        statusBarHeight: systemInfo.statusBarHeight,
        navBarHeight: systemInfo.statusBarHeight + 44
      });
    }
  },

  methods: {
    goBack() {
      const pages = getCurrentPages();
      if (pages.length > 1) {
        wx.navigateBack();
      } else {
        wx.switchTab({
          url: '/pages/index/index'
        });
      }
    }
  }
});
