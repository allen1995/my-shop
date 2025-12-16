"use strict";
const common_vendor = require("../../common/vendor.js");
const api_cart = require("../../api/cart.js");
const api_order = require("../../api/order.js");
const api_address = require("../../api/address.js");
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
    common_vendor.onLoad(async (options) => {
      const itemIds = options.itemIds ? options.itemIds.split(",").map((id) => parseInt(id)) : [];
      await loadCartItems(itemIds);
      await loadDefaultAddress();
    });
    common_vendor.onShow(() => {
      const app = getApp();
      if (app.globalData && app.globalData.selectedAddress) {
        selectedAddress.value = app.globalData.selectedAddress;
        app.globalData.selectedAddress = null;
      }
    });
    const loadCartItems = async (selectedIds = []) => {
      try {
        const res = await api_cart.cartApi.getCartItems();
        if (res.code === 200 && res.data) {
          let items = res.data || [];
          if (selectedIds.length > 0) {
            items = items.filter((item) => selectedIds.includes(item.id));
          }
          orderItems.value = items.map((item) => ({
            id: item.id,
            workId: item.workId,
            productId: item.productId,
            color: item.color,
            size: item.size,
            quantity: item.quantity,
            price: item.price || 0,
            // 使用后端返回的实际价格
            previewImageUrl: item.previewImageUrl
          }));
          common_vendor.index.__f__("log", "at pages/order/confirm.vue:116", "订单商品加载成功，共", orderItems.value.length, "项");
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/order/confirm.vue:119", "加载购物车失败", error);
        common_vendor.index.showToast({
          title: "加载商品失败",
          icon: "none"
        });
      }
    };
    const loadDefaultAddress = async () => {
      try {
        const res = await api_address.addressApi.getAddresses();
        if (res.code === 200 && res.data) {
          const defaultAddress = res.data.find((addr) => addr.isDefault);
          if (defaultAddress) {
            selectedAddress.value = defaultAddress;
          } else if (res.data.length > 0) {
            selectedAddress.value = res.data[0];
          }
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/order/confirm.vue:142", "加载默认地址失败", error);
      }
    };
    const selectAddress = () => {
      common_vendor.index.navigateTo({
        url: "/pages/address/list?select=true"
      });
    };
    const handleSubmit = async () => {
      var _a;
      if (!canSubmit.value) {
        if (!selectedAddress.value) {
          common_vendor.index.showToast({
            title: "请选择收货地址",
            icon: "none"
          });
        }
        return;
      }
      submitting.value = true;
      try {
        const cartItemIds = orderItems.value.map((item) => item.id);
        common_vendor.index.__f__("log", "at pages/order/confirm.vue:180", "创建订单 - cartItemIds:", cartItemIds, "addressId:", (_a = selectedAddress.value) == null ? void 0 : _a.id);
        if (!selectedAddress.value || !selectedAddress.value.id) {
          common_vendor.index.showToast({
            title: "请选择收货地址",
            icon: "none"
          });
          submitting.value = false;
          return;
        }
        const res = await api_order.orderApi.createOrder({
          cartItemIds,
          addressId: selectedAddress.value.id
        });
        common_vendor.index.__f__("log", "at pages/order/confirm.vue:196", "创建订单响应:", res);
        if (res.code === 200 && res.data) {
          const order = res.data;
          common_vendor.index.showToast({
            title: "订单创建成功",
            icon: "success"
          });
          setTimeout(() => {
            common_vendor.index.redirectTo({
              url: `/pages/order/payment?orderId=${order.id}`
            });
          }, 1500);
        } else {
          common_vendor.index.showToast({
            title: res.message || "创建订单失败",
            icon: "none",
            duration: 2e3
          });
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/order/confirm.vue:220", "创建订单异常:", error);
        common_vendor.index.showToast({
          title: error.message || "创建订单失败",
          icon: "none",
          duration: 2e3
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
        h: common_vendor.t(selectedAddress.value.detailAddress)
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
