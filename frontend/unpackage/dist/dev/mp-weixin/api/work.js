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
  },
  // 更新作品分类
  updateCategory(workId, category) {
    return utils_request.request({
      url: `/works/${workId}/category`,
      method: "PUT",
      data: { category }
    });
  },
  // 更新作品标签
  updateTags(workId, tags) {
    return utils_request.request({
      url: `/works/${workId}/tags`,
      method: "PUT",
      data: { tags }
    });
  },
  // 添加标签
  addTag(workId, tag) {
    return utils_request.request({
      url: `/works/${workId}/tags`,
      method: "POST",
      data: { tag }
    });
  },
  // 删除标签
  removeTag(workId, tag) {
    return utils_request.request({
      url: `/works/${workId}/tags/${encodeURIComponent(tag)}`,
      method: "DELETE"
    });
  },
  // 切换收藏状态
  toggleFavorite(workId) {
    return utils_request.request({
      url: `/works/${workId}/favorite`,
      method: "PUT"
    });
  }
};
exports.workApi = workApi;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/work.js.map
