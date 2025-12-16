"use strict";
const common_vendor = require("../../common/vendor.js");
const api_work = require("../../api/work.js");
const _sfc_main = {
  __name: "result",
  setup(__props) {
    const imageUrl = common_vendor.ref("");
    const taskId = common_vendor.ref(null);
    common_vendor.onLoad((options) => {
      imageUrl.value = decodeURIComponent(options.imageUrl || "");
      taskId.value = options.taskId;
    });
    const previewImage = () => {
      common_vendor.index.previewImage({
        urls: [imageUrl.value],
        current: imageUrl.value
      });
    };
    const handleSave = async () => {
      common_vendor.index.showModal({
        title: "保存作品",
        editable: true,
        placeholderText: "请输入作品标题",
        success: async (res) => {
          if (res.confirm) {
            try {
              const saveRes = await api_work.workApi.saveWork({
                title: res.content || "未命名作品",
                imageUrl: imageUrl.value
              });
              if (saveRes.code === 200) {
                common_vendor.index.showToast({
                  title: "保存成功",
                  icon: "success"
                });
                setTimeout(() => {
                  common_vendor.index.switchTab({
                    url: "/pages/works/list"
                  });
                }, 1500);
              }
            } catch (error) {
              common_vendor.index.showToast({
                title: "保存失败",
                icon: "none"
              });
            }
          }
        }
      });
    };
    const handleApply = async () => {
      common_vendor.index.showModal({
        title: "应用到包包",
        content: "需要先保存作品才能应用到包包，是否现在保存？",
        success: async (res) => {
          if (res.confirm) {
            common_vendor.index.showModal({
              title: "保存作品",
              editable: true,
              placeholderText: "请输入作品标题",
              success: async (saveRes) => {
                if (saveRes.confirm) {
                  try {
                    common_vendor.index.showLoading({
                      title: "保存中..."
                    });
                    const saveResult = await api_work.workApi.saveWork({
                      title: saveRes.content || "未命名作品",
                      imageUrl: imageUrl.value
                    });
                    common_vendor.index.hideLoading();
                    if (saveResult.code === 200 && saveResult.data) {
                      const workId = saveResult.data.id;
                      common_vendor.index.navigateTo({
                        url: `/pages/preview/preview?workId=${workId}&imageUrl=${encodeURIComponent(imageUrl.value)}`
                      });
                    } else {
                      common_vendor.index.showToast({
                        title: saveResult.message || "保存失败",
                        icon: "none"
                      });
                    }
                  } catch (error) {
                    common_vendor.index.hideLoading();
                    common_vendor.index.showToast({
                      title: "保存失败",
                      icon: "none"
                    });
                  }
                }
              }
            });
          } else {
            common_vendor.index.navigateTo({
              url: `/pages/preview/preview?imageUrl=${encodeURIComponent(imageUrl.value)}`
            });
          }
        }
      });
    };
    const handleRegenerate = () => {
      common_vendor.index.navigateBack();
    };
    return (_ctx, _cache) => {
      return {
        a: imageUrl.value,
        b: common_vendor.o(previewImage),
        c: common_vendor.o(handleSave),
        d: common_vendor.o(handleApply),
        e: common_vendor.o(handleRegenerate)
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-e3950caa"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/generate/result.js.map
