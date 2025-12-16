"use strict";
const common_vendor = require("../../common/vendor.js");
const api_user = require("../../api/user.js");
const store_user = require("../../store/user.js");
const api_imageGeneration = require("../../api/imageGeneration.js");
const _sfc_main = {
  __name: "edit",
  setup(__props) {
    const userStore = store_user.useUserStore();
    const formData = common_vendor.ref({
      nickName: "",
      avatarUrl: "",
      phone: ""
    });
    const saving = common_vendor.ref(false);
    common_vendor.onLoad(async () => {
      await loadUserInfo();
    });
    const loadUserInfo = async () => {
      try {
        const res = await api_user.userApi.getProfile();
        if (res.code === 200 && res.data) {
          formData.value = {
            nickName: res.data.nickName || "",
            avatarUrl: res.data.avatarUrl || "",
            phone: res.data.phone || ""
          };
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/profile/edit.vue:77", "加载用户信息失败", error);
        common_vendor.index.showToast({
          title: "加载用户信息失败",
          icon: "none"
        });
      }
    };
    const chooseAvatar = () => {
      common_vendor.index.chooseImage({
        count: 1,
        sizeType: ["compressed"],
        sourceType: ["album", "camera"],
        success: async (res) => {
          const tempFilePath = res.tempFilePaths[0];
          common_vendor.index.showLoading({
            title: "上传中..."
          });
          try {
            const uploadRes = await api_imageGeneration.imageGenerationApi.uploadImage(tempFilePath);
            if (uploadRes.code === 200 && uploadRes.data && uploadRes.data.imageUrl) {
              formData.value.avatarUrl = uploadRes.data.imageUrl;
              common_vendor.index.hideLoading();
              common_vendor.index.showToast({
                title: "头像已更新",
                icon: "success"
              });
            } else {
              throw new Error(uploadRes.message || "上传失败");
            }
          } catch (error) {
            common_vendor.index.__f__("error", "at pages/profile/edit.vue:111", "上传头像失败", error);
            common_vendor.index.hideLoading();
            common_vendor.index.showToast({
              title: error.message || "上传失败，请重试",
              icon: "none"
            });
          }
        },
        fail: (err) => {
          common_vendor.index.__f__("error", "at pages/profile/edit.vue:120", "选择图片失败", err);
          common_vendor.index.showToast({
            title: "选择图片失败",
            icon: "none"
          });
        }
      });
    };
    const handleSave = async () => {
      if (!formData.value.nickName || formData.value.nickName.trim().length === 0) {
        common_vendor.index.showToast({
          title: "请输入昵称",
          icon: "none"
        });
        return;
      }
      if (formData.value.phone && !/^1[3-9]\d{9}$/.test(formData.value.phone)) {
        common_vendor.index.showToast({
          title: "请输入正确的手机号",
          icon: "none"
        });
        return;
      }
      let avatarUrl = formData.value.avatarUrl;
      if (avatarUrl && (avatarUrl.includes("__tmp__") || avatarUrl.includes("127.0.0.1"))) {
        common_vendor.index.__f__("warn", "at pages/profile/edit.vue:150", "检测到临时路径，不保存头像:", avatarUrl);
        avatarUrl = null;
      }
      saving.value = true;
      try {
        const res = await api_user.userApi.updateProfile({
          nickName: formData.value.nickName.trim(),
          avatarUrl,
          phone: formData.value.phone || null
        });
        if (res.code === 200) {
          common_vendor.index.showToast({
            title: "保存成功",
            icon: "success"
          });
          if (res.data) {
            userStore.user = res.data;
          }
          setTimeout(() => {
            common_vendor.index.navigateBack();
          }, 1500);
        } else {
          common_vendor.index.showToast({
            title: res.message || "保存失败",
            icon: "none"
          });
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/profile/edit.vue:185", "保存用户信息失败", error);
        common_vendor.index.showToast({
          title: "保存失败，请重试",
          icon: "none"
        });
      } finally {
        saving.value = false;
      }
    };
    return (_ctx, _cache) => {
      return {
        a: formData.value.avatarUrl || "/static/default-avatar.png",
        b: common_vendor.o(chooseAvatar),
        c: formData.value.nickName,
        d: common_vendor.o(($event) => formData.value.nickName = $event.detail.value),
        e: formData.value.phone,
        f: common_vendor.o(($event) => formData.value.phone = $event.detail.value),
        g: common_vendor.t(saving.value ? "保存中..." : "保存"),
        h: common_vendor.o(handleSave),
        i: saving.value
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-ead3e541"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/profile/edit.js.map
