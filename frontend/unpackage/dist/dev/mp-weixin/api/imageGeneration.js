"use strict";
const utils_request = require("../utils/request.js");
const imageGenerationApi = {
  // 文生图
  textToImage(data) {
    return utils_request.request({
      url: "/image-generation/text-to-image",
      method: "POST",
      data
    });
  },
  // 查询生成任务状态
  getTaskStatus(taskId) {
    return utils_request.request({
      url: `/image-generation/tasks/${taskId}`,
      method: "GET"
    });
  }
};
exports.imageGenerationApi = imageGenerationApi;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/imageGeneration.js.map
