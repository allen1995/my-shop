"use strict";
const utils_request = require("../utils/request.js");
const orderApi = {
  // 创建订单
  createOrder(data) {
    return utils_request.request({
      url: "/orders",
      method: "POST",
      data
    });
  },
  // 获取订单列表
  getOrders(params) {
    return utils_request.request({
      url: "/orders",
      method: "GET",
      data: params
    });
  },
  // 获取订单详情
  getOrderDetail(orderId) {
    return utils_request.request({
      url: `/orders/${orderId}`,
      method: "GET"
    });
  },
  // 创建支付订单
  createPayment(orderId) {
    return utils_request.request({
      url: `/orders/${orderId}/payment`,
      method: "POST"
    });
  },
  // 取消订单
  cancelOrder(orderId) {
    return utils_request.request({
      url: `/orders/${orderId}/cancel`,
      method: "PUT"
    });
  },
  // 确认收货
  confirmReceipt(orderId) {
    return utils_request.request({
      url: `/orders/${orderId}/confirm`,
      method: "PUT"
    });
  }
};
exports.orderApi = orderApi;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/order.js.map
