"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const api_order = require("../../api/order.js");
const _sfc_main = {
  __name: "payment",
  setup(__props) {
    const orderId = common_vendor.ref(null);
    const order = common_vendor.ref({});
    const paying = common_vendor.ref(false);
    common_vendor.onLoad(async (options) => {
      orderId.value = options.orderId;
      await loadOrderDetail();
    });
    const loadOrderDetail = async () => {
      try {
        const res = await api_order.orderApi.getOrderDetail(orderId.value);
        if (res.code === 200 && res.data) {
          order.value = res.data;
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/order/payment.vue:46", "加载订单详情失败", error);
      }
    };
    const handlePay = async () => {
      paying.value = true;
      try {
        const paymentRes = await api_order.orderApi.createPayment(orderId.value);
        if (paymentRes.code === 200) {
          const paymentParams = paymentRes.data.data.paymentParams;
          common_vendor.index.requestPayment({
            provider: "wxpay",
            timeStamp: paymentParams.timeStamp,
            nonceStr: paymentParams.nonceStr,
            package: paymentParams.package,
            signType: paymentParams.signType,
            paySign: paymentParams.paySign,
            success: () => {
              common_vendor.index.showToast({
                title: "支付成功",
                icon: "success"
              });
              setTimeout(() => {
                common_vendor.index.redirectTo({
                  url: `/pages/order/detail?id=${orderId.value}`
                });
              }, 1500);
            },
            fail: (err) => {
              common_vendor.index.__f__("error", "at pages/order/payment.vue:81", "支付失败", err);
              common_vendor.index.showToast({
                title: "支付失败",
                icon: "none"
              });
            }
          });
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/order/payment.vue:90", "支付错误", error);
        common_vendor.index.showToast({
          title: "支付失败",
          icon: "none"
        });
      } finally {
        paying.value = false;
      }
    };
    return (_ctx, _cache) => {
      return {
        a: common_vendor.t(order.value.orderNo),
        b: common_vendor.t(order.value.totalAmount),
        c: common_assets._imports_0$5,
        d: common_vendor.o(handlePay),
        e: paying.value
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-13c3fb22"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/order/payment.js.map
