"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const store_user = require("../../store/user.js");
const _sfc_main = {
  __name: "login",
  setup(__props) {
    const userStore = store_user.useUserStore();
    const loading = common_vendor.ref(false);
    const handleWeChatLogin = async () => {
      loading.value = true;
      try {
        const loginRes = await new Promise((resolve, reject) => {
          common_vendor.index.login({
            provider: "weixin",
            success: resolve,
            fail: reject
          });
        });
        if (loginRes.code) {
          const success = await userStore.login(loginRes.code);
          if (success) {
            common_vendor.index.showToast({
              title: "登录成功",
              icon: "success"
            });
            setTimeout(() => {
              common_vendor.index.switchTab({
                url: "/pages/index/index"
              });
            }, 1500);
          } else {
            common_vendor.index.showToast({
              title: "登录失败，请重试",
              icon: "none"
            });
          }
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/login/login.vue:61", "登录错误", error);
        common_vendor.index.showToast({
          title: "登录失败",
          icon: "none"
        });
      } finally {
        loading.value = false;
      }
    };
    common_vendor.onLoad(() => {
      if (userStore.token) {
        common_vendor.index.switchTab({
          url: "/pages/index/index"
        });
      }
    });
    return (_ctx, _cache) => {
      return {
        a: common_assets._imports_0$1,
        b: common_vendor.o(handleWeChatLogin),
        c: loading.value
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-e4e4508d"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/login/login.js.map
