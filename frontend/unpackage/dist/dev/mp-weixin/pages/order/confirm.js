"use strict";
const common_vendor = require("../../common/vendor.js");
const api_cart = require("../../api/cart.js");
const api_order = require("../../api/order.js");
const _sfc_main = {
  __name: "confirm",
  setup(__props) {
    const orderItems = common_vendor.ref([]);
    const selectedAddress = common_vendor.ref(null);
    const shippingFee = common_vendor.ref(10);
    const submitting = common_vendor.ref(false);
    const totalAmount = common_vendor.computed(() => {
      return orderItems.value.reduce((sum, item) => {
        return sum + (item.price || 0) * item.quantity;
      }, 0).toFixed(2);
    });
    const finalAmount = common_vendor.computed(() => {
      return (parseFloat(totalAmount.value) + shippingFee.value).toFixed(2);
    });
    const canSubmit = common_vendor.computed(() => {
      return orderItems.value.length > 0 && selectedAddress.value;
    });
    common_vendor.onLoad(async () => {
      await loadCartItems();
    });
    const loadCartItems = async () => {
      try {
        const res = await api_cart.cartApi.getCartItems();
        if (res.code === 200 && res.data) {
          orderItems.value = (res.data || []).map((item) => ({
            id: item.id,
            workId: item.workId,
            productId: item.productId,
            color: item.color,
            size: item.size,
            quantity: item.quantity,
            price: 299,
            // 临时价格，实际应从商品获取
            previewImageUrl: item.previewImageUrl
          }));
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/order/confirm.vue:96", "加载购物车失败", error);
      }
    };
    const selectAddress = () => {
      common_vendor.index.navigateTo({
        url: "/pages/address/list?select=true"
      });
    };
    const handleSubmit = async () => {
      if (!canSubmit.value)
        return;
      submitting.value = true;
      try {
        const cartItemIds = orderItems.value.map((item) => item.id);
        const res = await api_order.orderApi.createOrder({
          cartItemIds,
          addressId: selectedAddress.value.id
        });
        if (res.code === 200 && res.data) {
          const order = res.data;
          common_vendor.index.redirectTo({
            url: `/pages/order/payment?orderId=${order.id}`
          });
        }
      } catch (error) {
        common_vendor.index.showToast({
          title: "创建订单失败",
          icon: "none"
        });
      } finally {
        submitting.value = false;
      }
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.f(orderItems.value, (item, k0, i0) => {
          return {
            a: item.previewImageUrl,
            b: common_vendor.t(item.color),
            c: common_vendor.t(item.size),
            d: common_vendor.t(item.quantity),
            e: common_vendor.t(item.price),
            f: item.id
          };
        }),
        b: selectedAddress.value
      }, selectedAddress.value ? {
        c: common_vendor.t(selectedAddress.value.receiverName),
        d: common_vendor.t(selectedAddress.value.receiverPhone),
        e: common_vendor.t(selectedAddress.value.province),
        f: common_vendor.t(selectedAddress.value.city),
        g: common_vendor.t(selectedAddress.value.district),
        h: common_vendor.t(selectedAddress.value.detail)
      } : {}, {
        i: common_vendor.o(selectAddress),
        j: common_vendor.t(totalAmount.value),
        k: common_vendor.t(shippingFee.value),
        l: common_vendor.t(finalAmount.value),
        m: common_vendor.o(handleSubmit),
        n: !canSubmit.value,
        o: submitting.value
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-324e7894"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/order/confirm.js.map
