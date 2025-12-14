"use strict";
const utils_request = require("../utils/request.js");
const workApi = {
  // 保存作品
  saveWork(data) {
    return utils_request.request({
      url: "/works",
      method: "POST",
      data
    });
  },
  // 获取作品列表
  getWorks(params) {
    return utils_request.request({
      url: "/works",
      method: "GET",
      data: params
    });
  },
  // 获取作品详情
  getWorkDetail(workId) {
    return utils_request.request({
      url: `/works/${workId}`,
      method: "GET"
    });
  },
  // 删除作品
  deleteWork(workId) {
    return utils_request.request({
      url: `/works/${workId}`,
      method: "DELETE"
    });
  }
};
exports.workApi = workApi;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/work.js.map
