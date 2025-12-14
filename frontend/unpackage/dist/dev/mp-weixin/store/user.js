"use strict";
const common_vendor = require("../common/vendor.js");
const api_auth = require("../api/auth.js");
const useUserStore = common_vendor.defineStore("user", {
  state: () => ({
    user: null,
    token: common_vendor.index.getStorageSync("token") || null
  }),
  actions: {
    async login(code) {
      try {
        const res = await api_auth.authApi.login(code);
        if (res.code === 200) {
          this.token = res.data.token;
          this.user = res.data.user;
          common_vendor.index.setStorageSync("token", this.token);
          return true;
        }
        return false;
      } catch (error) {
        common_vendor.index.__f__("error", "at store/user.js:22", "登录失败", error);
        return false;
      }
    },
    logout() {
      this.user = null;
      this.token = null;
      common_vendor.index.removeStorageSync("token");
    }
  }
});
exports.useUserStore = useUserStore;
//# sourceMappingURL=../../.sourcemap/mp-weixin/store/user.js.map
