"use strict";
const utils_request = require("../utils/request.js");
const generatePreview = (data) => {
  return utils_request.request({
    url: "/preview/generate",
    method: "POST",
    data
  });
};
const getTaskStatus = (taskId) => {
  return utils_request.request({
    url: `/preview/tasks/${taskId}`,
    method: "GET"
  });
};
exports.generatePreview = generatePreview;
exports.getTaskStatus = getTaskStatus;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/preview.js.map
