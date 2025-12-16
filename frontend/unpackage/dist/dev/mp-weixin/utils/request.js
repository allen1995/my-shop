"use strict";
const common_vendor = require("../common/vendor.js");
const BASE_URL = "http://localhost:8080/api";
const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = common_vendor.index.getStorageSync("token");
    common_vendor.index.request({
      url: BASE_URL + options.url,
      method: options.method || "GET",
      data: options.data || {},
      header: {
        "Content-Type": "application/json",
        "Authorization": token ? `Bearer ${token}` : "",
        ...options.header
      },
      success: (res) => {
        if (res.statusCode === 200) {
          if (res.data.code === 401) {
            common_vendor.index.removeStorageSync("token");
            common_vendor.index.reLaunch({
              url: "/pages/login/login"
            });
            reject(res.data);
            return;
          }
          resolve(res.data);
        } else {
          common_vendor.index.showToast({
            title: "网络错误",
            icon: "none"
          });
          reject(res);
        }
      },
      fail: (err) => {
        common_vendor.index.showToast({
          title: "网络错误",
          icon: "none"
        });
        reject(err);
      }
    });
  });
};
exports.BASE_URL = BASE_URL;
exports.request = request;
//# sourceMappingURL=../../.sourcemap/mp-weixin/utils/request.js.map
