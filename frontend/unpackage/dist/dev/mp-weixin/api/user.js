"use strict";
const utils_request = require("../utils/request.js");
const userApi = {
  // 获取用户信息
  getProfile() {
    return utils_request.request({
      url: "/user/profile",
      method: "GET"
    });
  },
  // 更新用户信息
  updateProfile(data) {
    return utils_request.request({
      url: "/user/profile",
      method: "PUT",
      data
    });
  },
  // 获取用户统计信息
  getStatistics() {
    return utils_request.request({
      url: "/user/statistics",
      method: "GET"
    });
  }
};
exports.userApi = userApi;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/user.js.map
