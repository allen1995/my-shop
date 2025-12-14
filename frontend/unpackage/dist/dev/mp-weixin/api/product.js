"use strict";
const utils_request = require("../utils/request.js");
const productApi = {
  // 获取商品列表
  getProducts() {
    return utils_request.request({
      url: "/products",
      method: "GET"
    });
  },
  // 获取商品详情
  getProductDetail(productId) {
    return utils_request.request({
      url: `/products/${productId}`,
      method: "GET"
    });
  }
};
exports.productApi = productApi;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/product.js.map
