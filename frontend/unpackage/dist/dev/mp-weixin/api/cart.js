"use strict";
const utils_request = require("../utils/request.js");
const cartApi = {
  // 添加到购物车
  addToCart(data) {
    return utils_request.request({
      url: "/cart/items",
      method: "POST",
      data
    });
  },
  // 获取购物车列表
  getCartItems() {
    return utils_request.request({
      url: "/cart/items",
      method: "GET"
    });
  },
  // 更新购物车项
  updateCartItem(itemId, data) {
    return utils_request.request({
      url: `/cart/items/${itemId}`,
      method: "PUT",
      data
    });
  },
  // 删除购物车项
  deleteCartItem(itemId) {
    return utils_request.request({
      url: `/cart/items/${itemId}`,
      method: "DELETE"
    });
  }
};
exports.cartApi = cartApi;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/cart.js.map
