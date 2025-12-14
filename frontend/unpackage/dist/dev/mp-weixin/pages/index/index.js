"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const api_work = require("../../api/work.js");
const _sfc_main = {
  __name: "index",
  setup(__props) {
    const hotWorks = common_vendor.ref([]);
    const goToTextToImage = () => {
      common_vendor.index.navigateTo({
        url: "/pages/generate/text-to-image"
      });
    };
    const goToImageToImage = () => {
      common_vendor.index.showToast({
        title: "图生图功能开发中",
        icon: "none"
      });
    };
    const goToWorks = () => {
      common_vendor.index.switchTab({
        url: "/pages/works/list"
      });
    };
    const goToWorkDetail = (workId) => {
      common_vendor.index.navigateTo({
        url: `/pages/works/detail?id=${workId}`
      });
    };
    common_vendor.onMounted(async () => {
      try {
        const res = await api_work.workApi.getWorks({ page: 0, size: 6 });
        if (res.code === 200) {
          hotWorks.value = res.data.content || [];
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:76", "加载热门作品失败", error);
      }
    });
    return (_ctx, _cache) => {
      return {
        a: common_assets._imports_0,
        b: common_vendor.o(goToTextToImage),
        c: common_assets._imports_1,
        d: common_vendor.o(goToImageToImage),
        e: common_vendor.o(goToWorks),
        f: common_vendor.f(hotWorks.value, (work, k0, i0) => {
          return {
            a: work.imageUrl,
            b: common_vendor.t(work.title),
            c: work.id,
            d: common_vendor.o(($event) => goToWorkDetail(work.id), work.id)
          };
        })
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-1cf27b2a"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/index/index.js.map
