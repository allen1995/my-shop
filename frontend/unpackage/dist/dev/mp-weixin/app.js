"use strict";
Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const common_vendor = require("./common/vendor.js");
if (!Math) {
  "./pages/index/index.js";
  "./pages/login/login.js";
  "./pages/generate/text-to-image.js";
  "./pages/generate/image-to-image.js";
  "./pages/generate/generating.js";
  "./pages/generate/result.js";
  "./pages/works/list.js";
  "./pages/works/detail.js";
  "./pages/preview/preview.js";
  "./pages/cart/cart.js";
  "./pages/order/confirm.js";
  "./pages/order/payment.js";
  "./pages/order/list.js";
  "./pages/order/detail.js";
  "./pages/profile/profile.js";
  "./pages/profile/edit.js";
  "./pages/address/list.js";
  "./pages/address/edit.js";
}
const _sfc_main = {
  onLaunch: function() {
    common_vendor.index.__f__("log", "at App.vue:9", "App Launch");
    this.checkLogin();
  },
  onShow: function() {
    common_vendor.index.__f__("log", "at App.vue:14", "App Show");
  },
  onHide: function() {
    common_vendor.index.__f__("log", "at App.vue:17", "App Hide");
  },
  methods: {
    checkLogin() {
      const token = common_vendor.index.getStorageSync("token");
      if (!token) {
        common_vendor.index.reLaunch({
          url: "/pages/login/login"
        });
      }
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {};
}
const App = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
function createApp() {
  const app = common_vendor.createSSRApp(App);
  const pinia = common_vendor.createPinia();
  app.use(pinia);
  return {
    app,
    pinia
  };
}
createApp().app.mount("#app");
exports.createApp = createApp;
//# sourceMappingURL=../.sourcemap/mp-weixin/app.js.map
