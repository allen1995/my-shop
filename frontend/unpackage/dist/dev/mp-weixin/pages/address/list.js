"use strict";
const common_vendor = require("../../common/vendor.js");
const api_address = require("../../api/address.js");
const _sfc_main = {
  __name: "list",
  setup(__props) {
    const addresses = common_vendor.ref([]);
    const isSelectMode = common_vendor.ref(false);
    const loadAddresses = async () => {
      try {
        const res = await api_address.addressApi.getAddresses();
        common_vendor.index.__f__("log", "at pages/address/list.vue:59", "地址列表API响应:", res);
        if (res.code === 200 && res.data) {
          const addressList = Array.isArray(res.data) ? res.data : [res.data];
          common_vendor.index.__f__("log", "at pages/address/list.vue:63", "地址列表数据:", addressList);
          addressList.forEach((addr, index) => {
            common_vendor.index.__f__("log", "at pages/address/list.vue:66", `地址${index}:`, {
              id: addr.id,
              receiverName: addr.receiverName,
              receiverPhone: addr.receiverPhone,
              province: addr.province,
              city: addr.city,
              district: addr.district,
              detailAddress: addr.detailAddress,
              isDefault: addr.isDefault
            });
          });
          addresses.value = addressList.map((addr) => ({
            id: addr.id,
            receiverName: addr.receiverName || "",
            receiverPhone: addr.receiverPhone || "",
            province: addr.province || "",
            city: addr.city || "",
            district: addr.district || "",
            detailAddress: addr.detailAddress || "",
            isDefault: addr.isDefault || false
          }));
        } else {
          common_vendor.index.__f__("warn", "at pages/address/list.vue:89", "地址列表响应异常:", res);
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/address/list.vue:92", "加载地址列表失败", error);
      }
    };
    common_vendor.onLoad((options) => {
      if (options.select === "true") {
        isSelectMode.value = true;
        common_vendor.index.setNavigationBarTitle({
          title: "选择收货地址"
        });
      }
    });
    const handleAdd = () => {
      common_vendor.index.navigateTo({
        url: "/pages/address/edit"
      });
    };
    const handleSelectAddress = (address) => {
      if (isSelectMode.value) {
        const pages = getCurrentPages();
        if (pages.length > 1) {
          pages[pages.length - 2];
          getApp().globalData = getApp().globalData || {};
          getApp().globalData.selectedAddress = address;
          common_vendor.index.navigateBack();
        }
      }
    };
    const handleEdit = (address) => {
      common_vendor.index.navigateTo({
        url: `/pages/address/edit?id=${address.id}`
      });
    };
    const handleDelete = (address) => {
      common_vendor.index.showModal({
        title: "确认删除",
        content: "确定要删除这个地址吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              const deleteRes = await api_address.addressApi.deleteAddress(address.id);
              if (deleteRes.code === 200) {
                common_vendor.index.showToast({
                  title: "删除成功",
                  icon: "success"
                });
                loadAddresses();
              } else {
                common_vendor.index.showToast({
                  title: deleteRes.message || "删除失败",
                  icon: "none"
                });
              }
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
    const handleSetDefault = async (address) => {
      try {
        const res = await api_address.addressApi.setDefaultAddress(address.id);
        if (res.code === 200) {
          common_vendor.index.showToast({
            title: "设置成功",
            icon: "success"
          });
          loadAddresses();
        } else {
          common_vendor.index.showToast({
            title: res.message || "设置失败",
            icon: "none"
          });
        }
      } catch (error) {
        common_vendor.index.showToast({
          title: "设置失败",
          icon: "none"
        });
      }
    };
    common_vendor.onMounted(() => {
      loadAddresses();
    });
    common_vendor.onShow(() => {
      loadAddresses();
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: addresses.value.length > 0
      }, addresses.value.length > 0 ? {
        b: common_vendor.f(addresses.value, (address, k0, i0) => {
          return common_vendor.e({
            a: common_vendor.t(address.receiverName || ""),
            b: common_vendor.t(address.receiverPhone || ""),
            c: address.isDefault
          }, address.isDefault ? {} : {}, {
            d: common_vendor.t(address.province || ""),
            e: common_vendor.t(address.city || ""),
            f: common_vendor.t(address.district || ""),
            g: common_vendor.t(address.detailAddress || "")
          }, !isSelectMode.value ? common_vendor.e({
            h: common_vendor.o(($event) => handleEdit(address), address.id),
            i: !address.isDefault
          }, !address.isDefault ? {
            j: common_vendor.o(($event) => handleDelete(address), address.id)
          } : {}, {
            k: !address.isDefault
          }, !address.isDefault ? {
            l: common_vendor.o(($event) => handleSetDefault(address), address.id)
          } : {}) : {}, {
            m: address.id,
            n: address.isDefault ? 1 : "",
            o: common_vendor.o(($event) => handleSelectAddress(address), address.id)
          });
        }),
        c: !isSelectMode.value,
        d: isSelectMode.value ? 1 : ""
      } : {}, {
        e: common_vendor.o(handleAdd)
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-90a3874e"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/address/list.js.map
