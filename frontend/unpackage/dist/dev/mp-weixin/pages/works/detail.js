"use strict";
const common_vendor = require("../../common/vendor.js");
const api_work = require("../../api/work.js");
const _sfc_main = {
  __name: "detail",
  setup(__props) {
    const work = common_vendor.ref({});
    const workId = common_vendor.ref(null);
    common_vendor.onLoad(async (options) => {
      workId.value = options.id;
      await loadWorkDetail();
    });
    const loadWorkDetail = async () => {
      try {
        const res = await api_work.workApi.getWorkDetail(workId.value);
        if (res.code === 200 && res.data) {
          work.value = res.data;
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/works/detail.vue:40", "加载作品详情失败", error);
      }
    };
    const previewImage = () => {
      common_vendor.index.previewImage({
        urls: [work.value.imageUrl],
        current: work.value.imageUrl
      });
    };
    const handleApply = () => {
      common_vendor.index.navigateTo({
        url: `/pages/preview/preview?workId=${workId.value}&imageUrl=${encodeURIComponent(work.value.imageUrl)}`
      });
    };
    const handleDelete = () => {
      common_vendor.index.showModal({
        title: "确认删除",
        content: "确定要删除这个作品吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              const deleteRes = await api_work.workApi.deleteWork(workId.value);
              if (deleteRes.code === 200) {
                common_vendor.index.showToast({
                  title: "删除成功",
                  icon: "success"
                });
                setTimeout(() => {
                  common_vendor.index.navigateBack();
                }, 1500);
              }
            } catch (error) {
              common_vendor.index.showToast({
                title: "删除失败",
                icon: "none"
              });
            }
          }
        }
      });
    };
    const formatTime = (timeStr) => {
      if (!timeStr)
        return "";
      const date = new Date(timeStr);
      return date.toLocaleString("zh-CN");
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: work.value.imageUrl,
        b: common_vendor.o(previewImage),
        c: common_vendor.t(work.value.title),
        d: work.value.description
      }, work.value.description ? {
        e: common_vendor.t(work.value.description)
      } : {}, {
        f: common_vendor.t(formatTime(work.value.createTime)),
        g: common_vendor.o(handleApply),
        h: common_vendor.o(handleDelete)
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-dfde3efa"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/works/detail.js.map
