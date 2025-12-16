"use strict";
const common_vendor = require("../../common/vendor.js");
const api_imageGeneration = require("../../api/imageGeneration.js");
const _sfc_main = {
  __name: "image-to-image",
  setup(__props) {
    const imageUrl = common_vendor.ref("");
    const prompt = common_vendor.ref("");
    const similarity = common_vendor.ref(50);
    const generating = common_vendor.ref(false);
    const uploading = common_vendor.ref(false);
    const canGenerate = common_vendor.computed(() => {
      return imageUrl.value && prompt.value.trim().length > 0 && !generating.value;
    });
    const chooseImage = () => {
      common_vendor.index.chooseImage({
        count: 1,
        sizeType: ["compressed"],
        sourceType: ["album", "camera"],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0];
          common_vendor.index.__f__("log", "at pages/generate/image-to-image.vue:100", "选择的图片路径:", tempFilePath);
          imageUrl.value = tempFilePath;
          uploadImageToServer(tempFilePath);
        },
        fail: (err) => {
          common_vendor.index.__f__("error", "at pages/generate/image-to-image.vue:110", "选择图片失败", err);
          common_vendor.index.showToast({
            title: "选择图片失败",
            icon: "none"
          });
        }
      });
    };
    const uploadImageToServer = async (filePath) => {
      uploading.value = true;
      try {
        common_vendor.index.showLoading({
          title: "上传中...",
          mask: true
        });
        const res = await api_imageGeneration.imageGenerationApi.uploadImage(filePath);
        common_vendor.index.__f__("log", "at pages/generate/image-to-image.vue:128", "上传响应:", res);
        if (res.code === 200 && res.data && res.data.imageUrl) {
          const serverUrl = res.data.imageUrl;
          const httpsUrl = serverUrl.startsWith("http://") ? serverUrl.replace("http://", "https://") : serverUrl;
          const oldUrl = imageUrl.value;
          imageUrl.value = httpsUrl;
          common_vendor.index.__f__("log", "at pages/generate/image-to-image.vue:141", "图片URL已更新:", imageUrl.value, "旧URL:", oldUrl);
          if (oldUrl === httpsUrl) {
            await common_vendor.nextTick$1();
            imageUrl.value = "";
            await common_vendor.nextTick$1();
            imageUrl.value = httpsUrl;
          }
          await common_vendor.nextTick$1();
          await new Promise((resolve) => setTimeout(resolve, 300));
          common_vendor.index.hideLoading();
          uploading.value = false;
          common_vendor.index.showToast({
            title: "上传成功",
            icon: "success",
            duration: 1500
          });
        } else {
          common_vendor.index.hideLoading();
          uploading.value = false;
          const errorMsg = res.message || "上传失败";
          common_vendor.index.__f__("error", "at pages/generate/image-to-image.vue:169", "上传失败:", errorMsg, res);
          common_vendor.index.showToast({
            title: errorMsg,
            icon: "none",
            duration: 2e3
          });
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/generate/image-to-image.vue:181", "上传异常:", error);
        common_vendor.index.hideLoading();
        uploading.value = false;
        common_vendor.index.showToast({
          title: "上传失败，请重试",
          icon: "none",
          duration: 2e3
        });
      }
    };
    const removeImage = () => {
      imageUrl.value = "";
    };
    const onImageError = (e) => {
      common_vendor.index.__f__("error", "at pages/generate/image-to-image.vue:200", "图片加载失败:", e, "当前URL:", imageUrl.value);
      common_vendor.index.showToast({
        title: "图片加载失败",
        icon: "none"
      });
    };
    const onImageLoad = (e) => {
      common_vendor.index.__f__("log", "at pages/generate/image-to-image.vue:208", "图片加载成功:", imageUrl.value);
      common_vendor.index.__f__("log", "at pages/generate/image-to-image.vue:209", "图片尺寸:", e.detail.width, "x", e.detail.height);
      common_vendor.nextTick$1(() => {
        common_vendor.index.__f__("log", "at pages/generate/image-to-image.vue:212", "视图已更新，当前imageUrl:", imageUrl.value);
      });
    };
    const onSimilarityChange = (e) => {
      similarity.value = e.detail.value;
    };
    const handleGenerate = async () => {
      if (!canGenerate.value)
        return;
      if (!imageUrl.value) {
        common_vendor.index.showToast({
          title: "请先选择参考图片",
          icon: "none"
        });
        return;
      }
      if (!prompt.value.trim()) {
        common_vendor.index.showToast({
          title: "请输入提示词",
          icon: "none"
        });
        return;
      }
      generating.value = true;
      try {
        let finalImageUrl = imageUrl.value;
        if (imageUrl.value.startsWith("file://") || imageUrl.value.startsWith("tmp://") || !imageUrl.value.startsWith("http")) {
          common_vendor.index.showToast({
            title: "图片正在上传，请稍候",
            icon: "none"
          });
          generating.value = false;
          return;
        }
        const res = await api_imageGeneration.imageGenerationApi.imageToImage({
          imageUrl: finalImageUrl,
          prompt: prompt.value,
          similarity: similarity.value
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
        common_vendor.index.__f__("error", "at pages/generate/image-to-image.vue:284", "生成失败", error);
        common_vendor.index.showToast({
          title: "生成失败，请重试",
          icon: "none"
        });
      } finally {
        generating.value = false;
      }
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: imageUrl.value
      }, imageUrl.value ? {
        b: imageUrl.value,
        c: "img-" + Date.now(),
        d: common_vendor.o(onImageError),
        e: common_vendor.o(onImageLoad)
      } : {}, {
        f: uploading.value
      }, uploading.value ? {} : {}, {
        g: common_vendor.o(chooseImage),
        h: imageUrl.value
      }, imageUrl.value ? {
        i: common_vendor.o(chooseImage),
        j: common_vendor.o(removeImage)
      } : {}, {
        k: prompt.value,
        l: common_vendor.o(($event) => prompt.value = $event.detail.value),
        m: common_vendor.t(prompt.value.length),
        n: common_vendor.t(similarity.value),
        o: similarity.value,
        p: common_vendor.o(onSimilarityChange),
        q: common_vendor.t(generating.value ? "生成中..." : "开始生成"),
        r: !canGenerate.value,
        s: generating.value,
        t: common_vendor.o(handleGenerate)
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-1d322224"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/generate/image-to-image.js.map
