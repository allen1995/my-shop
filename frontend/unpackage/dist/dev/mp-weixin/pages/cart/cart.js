"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const api_cart = require("../../api/cart.js");
const _sfc_main = {
  __name: "cart",
  setup(__props) {
    const cartItems = common_vendor.ref([]);
    const totalAmount = common_vendor.computed(() => {
      return cartItems.value.reduce((sum, item) => {
        return sum + (item.price || 0) * item.quantity;
      }, 0).toFixed(2);
    });
    const loadCartItems = async () => {
      try {
        const res = await api_cart.cartApi.getCartItems();
        if (res.code === 200 && res.data) {
          cartItems.value = res.data || [];
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/cart/cart.vue:58", "加载购物车失败", error);
      } finally {
        common_vendor.index.stopPullDownRefresh();
      }
    };
    const increaseQuantity = async (item) => {
      try {
        await api_cart.cartApi.updateCartItem(item.id, {
          quantity: item.quantity + 1
        });
        await loadCartItems();
      } catch (error) {
        common_vendor.index.showToast({
          title: "更新失败",
          icon: "none"
        });
      }
    };
    const decreaseQuantity = async (item) => {
      if (item.quantity <= 1)
        return;
      try {
        await api_cart.cartApi.updateCartItem(item.id, {
          quantity: item.quantity - 1
        });
        await loadCartItems();
      } catch (error) {
        common_vendor.index.showToast({
          title: "更新失败",
          icon: "none"
        });
      }
    };
    const handleDelete = async (itemId) => {
      common_vendor.index.showModal({
        title: "确认删除",
        content: "确定要删除这个商品吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              await api_cart.cartApi.deleteCartItem(itemId);
              await loadCartItems();
              common_vendor.index.showToast({
                title: "删除成功",
                icon: "success"
              });
            } catch (error) {
              common_vendor.index.showToast({
                title: "删除失败",
                icon: "none"
              });
            }
          }
        }
      });
    };
    const goToCheckout = () => {
      common_vendor.index.navigateTo({
        url: "/pages/order/confirm"
      });
    };
    const goToIndex = () => {
      common_vendor.index.switchTab({
        url: "/pages/index/index"
      });
    };
    common_vendor.onMounted(() => {
      loadCartItems();
    });
    common_vendor.onPullDownRefresh(() => {
      loadCartItems();
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: cartItems.value.length > 0
      }, cartItems.value.length > 0 ? {
        b: common_vendor.f(cartItems.value, (item, k0, i0) => {
          return {
            a: item.previewImageUrl,
            b: common_vendor.t(item.color),
            c: common_vendor.t(item.size),
            d: common_vendor.t(item.price || 0),
            e: common_vendor.o(($event) => decreaseQuantity(item), item.id),
            f: common_vendor.t(item.quantity),
            g: common_vendor.o(($event) => increaseQuantity(item), item.id),
            h: common_vendor.o(($event) => handleDelete(item.id), item.id),
            i: item.id
          };
        })
      } : {
        c: common_assets._imports_0$4,
        d: common_vendor.o(goToIndex)
      }, {
        e: cartItems.value.length > 0
      }, cartItems.value.length > 0 ? {
        f: common_vendor.t(totalAmount.value),
        g: common_vendor.o(goToCheckout)
      } : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-c91e7611"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/cart/cart.js.map
