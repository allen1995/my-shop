"use strict";
const common_vendor = require("../common/vendor.js");
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
  // 上传图片
  uploadImage(filePath) {
    return new Promise((resolve, reject) => {
      const token = common_vendor.index.getStorageSync("token");
      common_vendor.index.uploadFile({
        url: utils_request.BASE_URL + "/image-generation/upload",
        filePath,
        name: "file",
        header: {
          "Authorization": token ? `Bearer ${token}` : ""
        },
        success: (res) => {
          try {
            const data = JSON.parse(res.data);
            if (data.code === 200) {
              resolve(data);
            } else {
              common_vendor.index.showToast({
                title: data.message || "上传失败",
                icon: "none"
              });
              reject(data);
            }
          } catch (e) {
            common_vendor.index.__f__("error", "at api/imageGeneration.js:38", "解析响应失败", e);
            reject(res);
          }
        },
        fail: (err) => {
          common_vendor.index.showToast({
            title: "上传失败",
            icon: "none"
          });
          reject(err);
        }
      });
    });
  },
  // 图生图
  imageToImage(data) {
    return utils_request.request({
      url: "/image-generation/image-to-image",
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
