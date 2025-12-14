"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const api_work = require("../../api/work.js");
const _sfc_main = {
  __name: "list",
  setup(__props) {
    const works = common_vendor.ref([]);
    const page = common_vendor.ref(0);
    const size = common_vendor.ref(20);
    const loading = common_vendor.ref(false);
    const hasMore = common_vendor.ref(true);
    const loadWorks = async (reset = false) => {
      if (loading.value)
        return;
      loading.value = true;
      try {
        if (reset) {
          page.value = 0;
          works.value = [];
        }
        const res = await api_work.workApi.getWorks({
          page: page.value,
          size: size.value
        });
        if (res.code === 200 && res.data) {
          const newWorks = res.data.content || [];
          works.value = reset ? newWorks : [...works.value, ...newWorks];
          hasMore.value = newWorks.length === size.value;
          page.value++;
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/works/list.vue:60", "加载作品失败", error);
      } finally {
        loading.value = false;
        common_vendor.index.stopPullDownRefresh();
      }
    };
    const goToDetail = (workId) => {
      common_vendor.index.navigateTo({
        url: `/pages/works/detail?id=${workId}`
      });
    };
    const goToGenerate = () => {
      common_vendor.index.switchTab({
        url: "/pages/index/index"
      });
    };
    const formatTime = (timeStr) => {
      if (!timeStr)
        return "";
      const date = new Date(timeStr);
      const now = /* @__PURE__ */ new Date();
      const diff = now - date;
      if (diff < 6e4)
        return "刚刚";
      if (diff < 36e5)
        return Math.floor(diff / 6e4) + "分钟前";
      if (diff < 864e5)
        return Math.floor(diff / 36e5) + "小时前";
      return Math.floor(diff / 864e5) + "天前";
    };
    common_vendor.onMounted(() => {
      loadWorks(true);
    });
    common_vendor.onPullDownRefresh(() => {
      loadWorks(true);
    });
    common_vendor.onReachBottom(() => {
      if (hasMore.value) {
        loadWorks();
      }
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: works.value.length > 0
      }, works.value.length > 0 ? {
        b: common_vendor.f(works.value, (work, k0, i0) => {
          return {
            a: work.imageUrl,
            b: common_vendor.t(work.title),
            c: common_vendor.t(formatTime(work.createTime)),
            d: work.id,
            e: common_vendor.o(($event) => goToDetail(work.id), work.id)
          };
        })
      } : {
        c: common_assets._imports_0$3,
        d: common_vendor.o(goToGenerate)
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-856f9690"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/works/list.js.map
