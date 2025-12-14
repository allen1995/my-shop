"use strict";
const common_vendor = require("../../common/vendor.js");
const api_imageGeneration = require("../../api/imageGeneration.js");
const _sfc_main = {
  __name: "text-to-image",
  setup(__props) {
    const prompt = common_vendor.ref("");
    const size = common_vendor.ref("square");
    const style = common_vendor.ref("realistic");
    const generating = common_vendor.ref(false);
    const canGenerate = common_vendor.computed(() => {
      return prompt.value.trim().length > 0 && !generating.value;
    });
    const handleGenerate = async () => {
      if (!canGenerate.value)
        return;
      generating.value = true;
      try {
        const res = await api_imageGeneration.imageGenerationApi.textToImage({
          prompt: prompt.value,
          parameters: {
            size: size.value,
            style: style.value
          }
        });
        if (res.code === 200 && res.data) {
          const taskId = res.data.taskId;
          if (!taskId) {
            common_vendor.index.showToast({
              title: "生成失败：未获取到任务ID",
              icon: "none"
            });
            return;
          }
          common_vendor.index.navigateTo({
            url: `/pages/generate/generating?taskId=${taskId}`
          });
        } else {
          common_vendor.index.showToast({
            title: res.message || "生成失败",
            icon: "none"
          });
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/generate/text-to-image.vue:124", "生成失败", error);
        common_vendor.index.showToast({
          title: "生成失败，请重试",
          icon: "none"
        });
      } finally {
        generating.value = false;
      }
    };
    return (_ctx, _cache) => {
      return {
        a: prompt.value,
        b: common_vendor.o(($event) => prompt.value = $event.detail.value),
        c: common_vendor.t(prompt.value.length),
        d: size.value === "square" ? 1 : "",
        e: common_vendor.o(($event) => size.value = "square"),
        f: size.value === "landscape" ? 1 : "",
        g: common_vendor.o(($event) => size.value = "landscape"),
        h: size.value === "portrait" ? 1 : "",
        i: common_vendor.o(($event) => size.value = "portrait"),
        j: style.value === "realistic" ? 1 : "",
        k: common_vendor.o(($event) => style.value = "realistic"),
        l: style.value === "illustration" ? 1 : "",
        m: common_vendor.o(($event) => style.value = "illustration"),
        n: style.value === "abstract" ? 1 : "",
        o: common_vendor.o(($event) => style.value = "abstract"),
        p: style.value === "watercolor" ? 1 : "",
        q: common_vendor.o(($event) => style.value = "watercolor"),
        r: common_vendor.t(generating.value ? "生成中..." : "开始生成"),
        s: !canGenerate.value,
        t: generating.value,
        v: common_vendor.o(handleGenerate)
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-8f08f792"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/generate/text-to-image.js.map
