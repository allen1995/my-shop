"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const api_cart = require("../../api/cart.js");
const _sfc_main = {
  __name: "cart",
  setup(__props) {
    const cartItems = common_vendor.ref([]);
    common_vendor.computed(() => {
      return cartItems.value.reduce((sum, item) => {
        return sum + (item.price || 0) * item.quantity;
      }, 0).toFixed(2);
    });
    const selectedCount = common_vendor.computed(() => {
      return cartItems.value.filter((item) => item.selected).length;
    });
    const selectedAmount = common_vendor.computed(() => {
      return cartItems.value.filter((item) => item.selected).reduce((sum, item) => {
        return sum + (item.price || 0) * item.quantity;
      }, 0).toFixed(2);
    });
    const isAllSelected = common_vendor.computed(() => {
      return cartItems.value.length > 0 && cartItems.value.every((item) => item.selected);
    });
    const loadCartItems = async () => {
      try {
        common_vendor.index.__f__("log", "at pages/cart/cart.vue:84", "开始加载购物车数据...");
        const res = await api_cart.cartApi.getCartItems();
        common_vendor.index.__f__("log", "at pages/cart/cart.vue:86", "购物车API响应:", res);
        if (res.code === 200 && res.data) {
          cartItems.value = (res.data || []).map((item) => ({
            ...item,
            selected: true
          }));
          common_vendor.index.__f__("log", "at pages/cart/cart.vue:93", "购物车数据加载成功，共", cartItems.value.length, "项");
        } else {
          common_vendor.index.__f__("warn", "at pages/cart/cart.vue:95", "购物车加载失败:", res.message);
          cartItems.value = [];
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/cart/cart.vue:99", "加载购物车失败", error);
        common_vendor.index.showToast({
          title: "加载购物车失败",
          icon: "none"
        });
        cartItems.value = [];
      } finally {
        common_vendor.index.stopPullDownRefresh();
      }
    };
    const onSelectAllChange = (e) => {
      const checked = e.detail.value.length > 0;
      common_vendor.index.__f__("log", "at pages/cart/cart.vue:112", "全选状态改变:", checked);
      cartItems.value.forEach((item) => {
        item.selected = checked;
      });
      cartItems.value = [...cartItems.value];
    };
    const onItemSelectChange = (item) => {
      common_vendor.index.__f__("log", "at pages/cart/cart.vue:121", "商品选择状态改变:", item.id, "当前状态:", item.selected);
      item.selected = !item.selected;
      cartItems.value = [...cartItems.value];
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
      const selectedItems = cartItems.value.filter((item) => item.selected);
      if (selectedItems.length === 0) {
        common_vendor.index.showToast({
          title: "请选择要结算的商品",
          icon: "none"
        });
        return;
      }
      const selectedIds = selectedItems.map((item) => item.id).join(",");
      common_vendor.index.navigateTo({
        url: `/pages/order/confirm?itemIds=${selectedIds}`
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
    common_vendor.onShow(() => {
      loadCartItems();
    });
    common_vendor.onPullDownRefresh(() => {
      loadCartItems();
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: cartItems.value.length > 0
      }, cartItems.value.length > 0 ? {
        b: isAllSelected.value,
        c: common_vendor.o(onSelectAllChange),
        d: common_vendor.f(cartItems.value, (item, k0, i0) => {
          return {
            a: item.selected,
            b: common_vendor.o(($event) => onItemSelectChange(item), item.id),
            c: item.previewImageUrl,
            d: common_vendor.t(item.productName || "定制包包"),
            e: common_vendor.t(item.color),
            f: common_vendor.t(item.size),
            g: common_vendor.t(item.price || 0),
            h: common_vendor.o(($event) => decreaseQuantity(item), item.id),
            i: common_vendor.t(item.quantity),
            j: common_vendor.o(($event) => increaseQuantity(item), item.id),
            k: common_vendor.o(($event) => handleDelete(item.id), item.id),
            l: item.id
          };
        })
      } : {
        e: common_assets._imports_0$4,
        f: common_vendor.o(goToIndex)
      }, {
        g: cartItems.value.length > 0
      }, cartItems.value.length > 0 ? {
        h: common_vendor.t(selectedAmount.value),
        i: common_vendor.t(selectedCount.value),
        j: common_vendor.t(selectedCount.value),
        k: common_vendor.o(goToCheckout),
        l: selectedCount.value === 0
      } : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-c91e7611"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/cart/cart.js.map
