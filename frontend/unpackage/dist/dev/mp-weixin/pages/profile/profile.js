"use strict";
const common_vendor = require("../../common/vendor.js");
const store_user = require("../../store/user.js");
const api_user = require("../../api/user.js");
const _sfc_main = {
  __name: "profile",
  setup(__props) {
    const userStore = store_user.useUserStore();
    const userInfo = common_vendor.ref({});
    const statistics = common_vendor.ref({
      orders: {},
      works: {}
    });
    const loadUserInfo = async () => {
      try {
        const res = await api_user.userApi.getProfile();
        if (res.code === 200 && res.data) {
          userInfo.value = res.data;
          userStore.user = res.data;
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/profile/profile.vue:83", "加载用户信息失败", error);
      }
    };
    const loadStatistics = async () => {
      try {
        const res = await api_user.userApi.getStatistics();
        if (res.code === 200 && res.data) {
          statistics.value = res.data;
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/profile/profile.vue:94", "加载统计信息失败", error);
      }
    };
    common_vendor.onMounted(() => {
      loadUserInfo();
      loadStatistics();
    });
    common_vendor.onShow(() => {
      loadUserInfo();
      loadStatistics();
    });
    const goToEditProfile = () => {
      common_vendor.index.navigateTo({
        url: "/pages/profile/edit"
      });
    };
    const goToOrders = (status) => {
      const url = status ? `/pages/order/list?status=${status}` : "/pages/order/list";
      common_vendor.index.navigateTo({
        url
      });
    };
    const goToAddresses = () => {
      common_vendor.index.navigateTo({
        url: "/pages/address/list"
      });
    };
    const goToWorks = () => {
      common_vendor.index.switchTab({
        url: "/pages/works/list"
      });
    };
    const goToSettings = () => {
      common_vendor.index.showToast({
        title: "设置功能开发中",
        icon: "none"
      });
    };
    const getAvatarUrl = (avatarUrl) => {
      if (!avatarUrl) {
        return "/static/default-avatar.png";
      }
      if (avatarUrl.includes("__tmp__") || avatarUrl.includes("127.0.0.1")) {
        return "/static/default-avatar.png";
      }
      return avatarUrl;
    };
    const handleAvatarError = (e) => {
      common_vendor.index.__f__("error", "at pages/profile/profile.vue:154", "头像加载失败", e);
      userInfo.value.avatarUrl = "/static/default-avatar.png";
    };
    return (_ctx, _cache) => {
      var _a, _b, _c, _d;
      return {
        a: getAvatarUrl(userInfo.value.avatarUrl),
        b: common_vendor.o(goToEditProfile),
        c: common_vendor.o(handleAvatarError),
        d: common_vendor.t(userInfo.value.nickName || "未设置昵称"),
        e: common_vendor.o(goToEditProfile),
        f: common_vendor.t(((_a = statistics.value.orders) == null ? void 0 : _a.pendingPayment) || 0),
        g: common_vendor.o(($event) => goToOrders("PENDING_PAYMENT")),
        h: common_vendor.t(((_b = statistics.value.orders) == null ? void 0 : _b.shipped) || 0),
        i: common_vendor.o(($event) => goToOrders("SHIPPED")),
        j: common_vendor.t(((_c = statistics.value.orders) == null ? void 0 : _c.completed) || 0),
        k: common_vendor.o(($event) => goToOrders("COMPLETED")),
        l: common_vendor.t(((_d = statistics.value.works) == null ? void 0 : _d.total) || 0),
        m: common_vendor.o(goToWorks),
        n: common_vendor.o(($event) => goToOrders()),
        o: common_vendor.o(goToAddresses),
        p: common_vendor.o(goToWorks),
        q: common_vendor.o(goToSettings)
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-dd383ca2"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/profile/profile.js.map
