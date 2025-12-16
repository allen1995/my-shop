"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const api_imageGeneration = require("../../api/imageGeneration.js");
const _sfc_main = {
  __name: "generating",
  setup(__props) {
    const taskId = common_vendor.ref(null);
    const progress = common_vendor.ref(0);
    const estimatedTime = common_vendor.ref(30);
    let pollTimer = null;
    common_vendor.onLoad((options) => {
      taskId.value = options.taskId;
      startPolling();
    });
    common_vendor.onUnmounted(() => {
      if (pollTimer) {
        clearInterval(pollTimer);
      }
    });
    const startPolling = () => {
      pollTimer = setInterval(async () => {
        try {
          const res = await api_imageGeneration.imageGenerationApi.getTaskStatus(taskId.value);
          if (res.code === 200 && res.data) {
            const task = res.data;
            if (task.status === "COMPLETED") {
              clearInterval(pollTimer);
              if (task.resultUrl) {
                common_vendor.index.redirectTo({
                  url: `/pages/generate/result?taskId=${taskId.value}&imageUrl=${encodeURIComponent(task.resultUrl)}`
                });
              } else {
                common_vendor.index.showModal({
                  title: "生成完成",
                  content: "但未获取到结果图片，请重试",
                  showCancel: false,
                  success: () => {
                    common_vendor.index.navigateBack();
                  }
                });
              }
            } else if (task.status === "FAILED") {
              clearInterval(pollTimer);
              common_vendor.index.showModal({
                title: "生成失败",
                content: task.errorMessage || "生成失败，请重试",
                showCancel: true,
                cancelText: "返回",
                confirmText: "重试",
                success: (modalRes) => {
                  if (modalRes.confirm) {
                    progress.value = 0;
                    estimatedTime.value = 30;
                    startPolling();
                  } else {
                    common_vendor.index.navigateBack();
                  }
                }
              });
            } else if (task.status === "PROCESSING" || task.status === "PENDING") {
              progress.value = Math.min(progress.value + 5, 90);
              estimatedTime.value = Math.max(estimatedTime.value - 1, 5);
            }
          }
        } catch (error) {
          common_vendor.index.__f__("error", "at pages/generate/generating.vue:93", "查询任务状态失败", error);
        }
      }, 2e3);
    };
    return (_ctx, _cache) => {
      return {
        a: common_assets._imports_0$2,
        b: progress.value + "%",
        c: common_vendor.t(estimatedTime.value)
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-23ce6965"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/generate/generating.js.map
