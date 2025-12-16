"use strict";
const utils_request = require("../utils/request.js");
const addressApi = {
  // 获取地址列表
  getAddresses() {
    return utils_request.request({
      url: "/addresses",
      method: "GET"
    });
  },
  // 获取地址详情
  getAddress(addressId) {
    return utils_request.request({
      url: `/addresses/${addressId}`,
      method: "GET"
    });
  },
  // 创建地址
  createAddress(data) {
    return utils_request.request({
      url: "/addresses",
      method: "POST",
      data
    });
  },
  // 更新地址
  updateAddress(addressId, data) {
    return utils_request.request({
      url: `/addresses/${addressId}`,
      method: "PUT",
      data
    });
  },
  // 删除地址
  deleteAddress(addressId) {
    return utils_request.request({
      url: `/addresses/${addressId}`,
      method: "DELETE"
    });
  },
  // 设置默认地址
  setDefaultAddress(addressId) {
    return utils_request.request({
      url: `/addresses/${addressId}/default`,
      method: "PUT"
    });
  }
};
exports.addressApi = addressApi;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/address.js.map
