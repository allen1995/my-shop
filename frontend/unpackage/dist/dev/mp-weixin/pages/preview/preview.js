"use strict";
const common_vendor = require("../../common/vendor.js");
const api_product = require("../../api/product.js");
const api_cart = require("../../api/cart.js");
const _sfc_main = {
  __name: "preview",
  setup(__props) {
    const workId = common_vendor.ref(null);
    const imageUrl = common_vendor.ref("");
    const products = common_vendor.ref([]);
    const selectedProduct = common_vendor.ref(null);
    const selectedColor = common_vendor.ref("");
    const selectedSize = common_vendor.ref("");
    const canvasWidth = common_vendor.ref(750);
    const canvasHeight = common_vendor.ref(750);
    const canAddToCart = common_vendor.computed(() => {
      return selectedProduct.value && selectedColor.value && selectedSize.value;
    });
    common_vendor.onLoad(async (options) => {
      workId.value = options.workId;
      imageUrl.value = decodeURIComponent(options.imageUrl || "");
      await loadProducts();
    });
    const loadProducts = async () => {
      try {
        const res = await api_product.productApi.getProducts();
        if (res.code === 200 && res.data) {
          products.value = res.data || [];
          if (products.value.length > 0) {
            selectedProduct.value = products.value[0];
            if (selectedProduct.value.colors && selectedProduct.value.colors.length > 0) {
              selectedColor.value = selectedProduct.value.colors[0];
            }
            if (selectedProduct.value.sizes && selectedProduct.value.sizes.length > 0) {
              selectedSize.value = selectedProduct.value.sizes[0];
            }
          }
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/preview/preview.vue:97", "加载商品失败", error);
      }
    };
    const onProductChange = (e) => {
      selectedProduct.value = products.value[e.detail.value];
      if (selectedProduct.value.colors && selectedProduct.value.colors.length > 0) {
        selectedColor.value = selectedProduct.value.colors[0];
      }
      if (selectedProduct.value.sizes && selectedProduct.value.sizes.length > 0) {
        selectedSize.value = selectedProduct.value.sizes[0];
      }
    };
    const handleAddToCart = async () => {
      if (!canAddToCart.value)
        return;
      try {
        const res = await api_cart.cartApi.addToCart({
          workId: workId.value,
          productId: selectedProduct.value.id,
          color: selectedColor.value,
          size: selectedSize.value,
          quantity: 1,
          previewImageUrl: imageUrl.value
        });
        if (res.code === 200) {
          common_vendor.index.showToast({
            title: "已加入购物车",
            icon: "success"
          });
          setTimeout(() => {
            common_vendor.index.switchTab({
              url: "/pages/cart/cart"
            });
          }, 1500);
        }
      } catch (error) {
        common_vendor.index.showToast({
          title: "加入购物车失败",
          icon: "none"
        });
      }
    };
    return (_ctx, _cache) => {
      var _a;
      return common_vendor.e({
        a: canvasWidth.value + "px",
        b: canvasHeight.value + "px",
        c: common_vendor.t(((_a = selectedProduct.value) == null ? void 0 : _a.name) || "请选择"),
        d: products.value,
        e: common_vendor.o(onProductChange),
        f: selectedProduct.value
      }, selectedProduct.value ? {
        g: common_vendor.f(selectedProduct.value.colors, (color, k0, i0) => {
          return {
            a: common_vendor.t(color),
            b: color,
            c: selectedColor.value === color ? 1 : "",
            d: common_vendor.o(($event) => selectedColor.value = color, color)
          };
        }),
        h: common_vendor.f(selectedProduct.value.sizes, (size, k0, i0) => {
          return {
            a: common_vendor.t(size),
            b: size,
            c: selectedSize.value === size ? 1 : "",
            d: common_vendor.o(($event) => selectedSize.value = size, size)
          };
        })
      } : {}, {
        i: common_vendor.o(handleAddToCart),
        j: !canAddToCart.value
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-2dad6c07"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/preview/preview.js.map
