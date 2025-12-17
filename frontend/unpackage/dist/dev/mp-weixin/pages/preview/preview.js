"use strict";
const common_vendor = require("../../common/vendor.js");
const api_product = require("../../api/product.js");
const api_cart = require("../../api/cart.js");
const api_preview = require("../../api/preview.js");
const _sfc_main = {
  __name: "preview",
  setup(__props) {
    const workId = common_vendor.ref(null);
    const imageUrl = common_vendor.ref("");
    const products = common_vendor.ref([]);
    const selectedProduct = common_vendor.ref(null);
    const selectedColor = common_vendor.ref("");
    const selectedSize = common_vendor.ref("");
    const previewUrl = common_vendor.ref("");
    const isGenerating = common_vendor.ref(false);
    const taskId = common_vendor.ref(null);
    const errorMessage = common_vendor.ref("");
    const loadingText = common_vendor.ref("正在生成预览，请稍候...");
    const pollingTimer = common_vendor.ref(null);
    const canGenerate = common_vendor.computed(() => {
      return selectedProduct.value && selectedColor.value && selectedSize.value;
    });
    const canAddToCart = common_vendor.computed(() => {
      return canGenerate.value && previewUrl.value && !isGenerating.value;
    });
    common_vendor.onLoad(async (options) => {
      workId.value = options.workId ? parseInt(options.workId) : null;
      imageUrl.value = decodeURIComponent(options.imageUrl || "");
      common_vendor.index.__f__("log", "at pages/preview/preview.vue:148", "预览页面加载 - workId:", workId.value, "imageUrl:", imageUrl.value);
      if (!workId.value) {
        common_vendor.index.showToast({
          title: "缺少作品ID",
          icon: "none",
          duration: 2e3
        });
        return;
      }
      await loadProducts();
    });
    const loadProducts = async () => {
      try {
        const res = await api_product.productApi.getProducts();
        common_vendor.index.__f__("log", "at pages/preview/preview.vue:166", "商品列表响应:", res);
        if (res.code === 200 && res.data) {
          products.value = res.data.map((product) => {
            let colors = [];
            let sizes = [];
            try {
              colors = typeof product.colors === "string" ? JSON.parse(product.colors) : Array.isArray(product.colors) ? product.colors : [];
            } catch (e) {
              common_vendor.index.__f__("warn", "at pages/preview/preview.vue:179", "解析颜色失败:", e);
              colors = [];
            }
            try {
              sizes = typeof product.sizes === "string" ? JSON.parse(product.sizes) : Array.isArray(product.sizes) ? product.sizes : [];
            } catch (e) {
              common_vendor.index.__f__("warn", "at pages/preview/preview.vue:188", "解析尺寸失败:", e);
              sizes = [];
            }
            return {
              ...product,
              colors,
              sizes
            };
          });
          common_vendor.index.__f__("log", "at pages/preview/preview.vue:199", "处理后的商品列表:", products.value);
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/preview/preview.vue:202", "加载商品列表失败:", error);
        common_vendor.index.showToast({
          title: "加载商品列表失败",
          icon: "none"
        });
      }
    };
    const onProductChange = (e) => {
      const index = e.detail.value;
      selectedProduct.value = products.value[index];
      selectedColor.value = "";
      selectedSize.value = "";
      previewUrl.value = "";
      common_vendor.index.__f__("log", "at pages/preview/preview.vue:217", "选择商品:", selectedProduct.value);
    };
    const handleGeneratePreview = async () => {
      if (!canGenerate.value || isGenerating.value) {
        return;
      }
      try {
        isGenerating.value = true;
        errorMessage.value = "";
        loadingText.value = "正在生成预览，请稍候...";
        common_vendor.index.__f__("log", "at pages/preview/preview.vue:231", "开始生成预览:", {
          workId: workId.value,
          productId: selectedProduct.value.id,
          color: selectedColor.value,
          size: selectedSize.value
        });
        const res = await api_preview.generatePreview({
          workId: workId.value,
          productId: selectedProduct.value.id,
          color: selectedColor.value,
          size: selectedSize.value
        });
        common_vendor.index.__f__("log", "at pages/preview/preview.vue:246", "预览生成响应:", res);
        if (res.code === 200) {
          taskId.value = res.data.taskId;
          if (res.data.status === "COMPLETED" && res.data.resultUrl) {
            previewUrl.value = res.data.resultUrl;
            isGenerating.value = false;
            common_vendor.index.showToast({
              title: "预览生成成功",
              icon: "success",
              duration: 2e3
            });
          } else {
            pollTaskStatus();
          }
        } else {
          throw new Error(res.message || "生成预览失败");
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/preview/preview.vue:270", "生成预览失败:", error);
        errorMessage.value = error.message || "生成预览失败，请重试";
        isGenerating.value = false;
        common_vendor.index.showToast({
          title: errorMessage.value,
          icon: "none",
          duration: 2e3
        });
      }
    };
    const pollTaskStatus = async () => {
      if (!taskId.value) {
        return;
      }
      try {
        const res = await api_preview.getTaskStatus(taskId.value);
        common_vendor.index.__f__("log", "at pages/preview/preview.vue:290", "任务状态:", res);
        if (res.code === 200) {
          const task = res.data;
          if (task.status === "COMPLETED") {
            previewUrl.value = task.resultUrl;
            isGenerating.value = false;
            if (pollingTimer.value) {
              clearTimeout(pollingTimer.value);
              pollingTimer.value = null;
            }
            common_vendor.index.showToast({
              title: "预览生成成功",
              icon: "success",
              duration: 2e3
            });
          } else if (task.status === "FAILED") {
            errorMessage.value = task.errorMessage || "生成失败";
            isGenerating.value = false;
            if (pollingTimer.value) {
              clearTimeout(pollingTimer.value);
              pollingTimer.value = null;
            }
            common_vendor.index.showModal({
              title: "提示",
              content: errorMessage.value + "，是否重试？",
              success: (modalRes) => {
                if (modalRes.confirm) {
                  handleRetry();
                }
              }
            });
          } else {
            const progress = task.progress || 0;
            if (progress > 0) {
              loadingText.value = `生成中 ${progress}%...`;
            }
            pollingTimer.value = setTimeout(pollTaskStatus, 2e3);
          }
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/preview/preview.vue:345", "查询任务状态失败:", error);
        isGenerating.value = false;
        errorMessage.value = "查询任务状态失败";
        if (pollingTimer.value) {
          clearTimeout(pollingTimer.value);
          pollingTimer.value = null;
        }
      }
    };
    const handleRegenerate = () => {
      common_vendor.index.__f__("log", "at pages/preview/preview.vue:359", "重新生成预览");
      if (pollingTimer.value) {
        clearTimeout(pollingTimer.value);
        pollingTimer.value = null;
      }
      previewUrl.value = "";
      taskId.value = null;
      errorMessage.value = "";
      isGenerating.value = false;
      handleGeneratePreview();
    };
    const handleRetry = () => {
      common_vendor.index.__f__("log", "at pages/preview/preview.vue:379", "重试生成预览");
      if (pollingTimer.value) {
        clearTimeout(pollingTimer.value);
        pollingTimer.value = null;
      }
      previewUrl.value = "";
      taskId.value = null;
      errorMessage.value = "";
      isGenerating.value = false;
      handleGeneratePreview();
    };
    const handleAddToCart = async () => {
      if (!canAddToCart.value) {
        return;
      }
      try {
        common_vendor.index.showLoading({
          title: "加入中..."
        });
        const res = await api_cart.cartApi.addToCart({
          workId: workId.value,
          productId: selectedProduct.value.id,
          color: selectedColor.value,
          size: selectedSize.value,
          quantity: 1,
          previewImageUrl: previewUrl.value
        });
        common_vendor.index.hideLoading();
        if (res.code === 200) {
          common_vendor.index.showToast({
            title: "已加入购物车",
            icon: "success",
            duration: 1500
          });
          setTimeout(() => {
            common_vendor.index.switchTab({
              url: "/pages/cart/cart"
            });
          }, 1500);
        } else {
          throw new Error(res.message || "加入购物车失败");
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/preview/preview.vue:436", "加入购物车失败:", error);
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({
          title: error.message || "加入购物车失败",
          icon: "none",
          duration: 2e3
        });
      }
    };
    common_vendor.onUnload(() => {
      if (pollingTimer.value) {
        clearTimeout(pollingTimer.value);
        pollingTimer.value = null;
      }
    });
    return (_ctx, _cache) => {
      var _a;
      return common_vendor.e({
        a: !previewUrl.value && !isGenerating.value
      }, !previewUrl.value && !isGenerating.value ? common_vendor.e({
        b: imageUrl.value
      }, imageUrl.value ? {
        c: imageUrl.value
      } : {}) : {}, {
        d: isGenerating.value
      }, isGenerating.value ? {
        e: common_vendor.t(loadingText.value)
      } : {}, {
        f: previewUrl.value && !isGenerating.value
      }, previewUrl.value && !isGenerating.value ? {
        g: previewUrl.value,
        h: common_vendor.o(handleRegenerate)
      } : {}, {
        i: errorMessage.value && !isGenerating.value
      }, errorMessage.value && !isGenerating.value ? {
        j: common_vendor.t(errorMessage.value),
        k: common_vendor.o(handleRetry)
      } : {}, {
        l: common_vendor.t(((_a = selectedProduct.value) == null ? void 0 : _a.name) || "请选择"),
        m: products.value,
        n: common_vendor.o(onProductChange),
        o: selectedProduct.value
      }, selectedProduct.value ? {
        p: common_vendor.f(selectedProduct.value.colors, (color, k0, i0) => {
          return {
            a: common_vendor.t(color),
            b: color,
            c: selectedColor.value === color ? 1 : "",
            d: common_vendor.o(($event) => selectedColor.value = color, color)
          };
        }),
        q: common_vendor.f(selectedProduct.value.sizes, (size, k0, i0) => {
          return {
            a: common_vendor.t(size),
            b: size,
            c: selectedSize.value === size ? 1 : "",
            d: common_vendor.o(($event) => selectedSize.value = size, size)
          };
        })
      } : {}, {
        r: isGenerating.value
      }, isGenerating.value ? {} : {}, {
        s: !canGenerate.value || isGenerating.value ? 1 : "",
        t: !canGenerate.value || isGenerating.value,
        v: common_vendor.o(handleGeneratePreview),
        w: !canAddToCart.value ? 1 : "",
        x: !canAddToCart.value,
        y: common_vendor.o(handleAddToCart)
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-2dad6c07"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/preview/preview.js.map
