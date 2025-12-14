"use strict";
const utils_request = require("../utils/request.js");
const authApi = {
  // 微信登录
  login(code) {
    return utils_request.request({
      url: "/auth/wechat/login",
      method: "POST",
      data: { code }
    });
  }
};
exports.authApi = authApi;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/auth.js.map
