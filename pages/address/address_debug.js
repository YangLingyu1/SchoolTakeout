// 在loadAddresses的success回调中添加更详细的调试信息
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
}