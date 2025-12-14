"use strict";
const common_vendor = require("../../common/vendor.js");
const store_user = require("../../store/user.js");
const _sfc_main = {
  __name: "profile",
  setup(__props) {
    const userStore = store_user.useUserStore();
    const userInfo = common_vendor.ref({});
    common_vendor.onMounted(() => {
      userInfo.value = userStore.user || {};
    });
    const goToOrders = () => {
      common_vendor.index.navigateTo({
        url: "/pages/order/list"
      });
    };
    const goToAddresses = () => {
      common_vendor.index.navigateTo({
        url: "/pages/address/list"
      });
    };
    const goToSettings = () => {
      common_vendor.index.showToast({
        title: "设置功能开发中",
        icon: "none"
      });
    };
    return (_ctx, _cache) => {
      return {
        a: userInfo.value.avatarUrl || "/static/default-avatar.png",
        b: common_vendor.t(userInfo.value.nickName || "未设置昵称"),
        c: common_vendor.o(goToOrders),
        d: common_vendor.o(goToAddresses),
        e: common_vendor.o(goToSettings)
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-dd383ca2"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/profile/profile.js.map
