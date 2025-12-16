"use strict";
const common_vendor = require("../../common/vendor.js");
const api_address = require("../../api/address.js");
const _sfc_main = {
  __name: "edit",
  setup(__props) {
    const addressId = common_vendor.ref(null);
    const formData = common_vendor.ref({
      receiverName: "",
      receiverPhone: "",
      province: "",
      city: "",
      district: "",
      detailAddress: "",
      isDefault: false
    });
    const provinces = [
      { name: "北京市", cities: [{ name: "北京市", districts: [{ name: "东城区" }, { name: "西城区" }, { name: "朝阳区" }] }] },
      { name: "上海市", cities: [{ name: "上海市", districts: [{ name: "黄浦区" }, { name: "徐汇区" }, { name: "长宁区" }] }] },
      { name: "广东省", cities: [
        { name: "广州市", districts: [{ name: "越秀区" }, { name: "海珠区" }, { name: "天河区" }] },
        { name: "深圳市", districts: [{ name: "罗湖区" }, { name: "福田区" }, { name: "南山区" }] }
      ] }
    ];
    const pickerValue = common_vendor.ref([0, 0, 0]);
    const pickerRange = common_vendor.computed(() => {
      var _a, _b, _c;
      const provinceList = provinces.map((p) => p.name);
      const cityList = ((_a = provinces[pickerValue.value[0]]) == null ? void 0 : _a.cities.map((c) => c.name)) || [];
      const districtList = ((_c = (_b = provinces[pickerValue.value[0]]) == null ? void 0 : _b.cities[pickerValue.value[1]]) == null ? void 0 : _c.districts.map((d) => d.name)) || [];
      return [provinceList, cityList, districtList];
    });
    const canSave = common_vendor.computed(() => {
      return formData.value.receiverName.trim() && formData.value.receiverPhone.trim() && formData.value.province && formData.value.city && formData.value.district && formData.value.detailAddress.trim();
    });
    common_vendor.onLoad(async (options) => {
      if (options.id) {
        addressId.value = options.id;
        await loadAddress();
      }
    });
    const loadAddress = async () => {
      try {
        const res = await api_address.addressApi.getAddress(addressId.value);
        if (res.code === 200 && res.data) {
          formData.value = {
            receiverName: res.data.receiverName,
            receiverPhone: res.data.receiverPhone,
            province: res.data.province,
            city: res.data.city,
            district: res.data.district,
            detailAddress: res.data.detailAddress,
            isDefault: res.data.isDefault
          };
          const provinceIndex = provinces.findIndex((p) => p.name === res.data.province);
          if (provinceIndex >= 0) {
            const cityIndex = provinces[provinceIndex].cities.findIndex((c) => c.name === res.data.city);
            if (cityIndex >= 0) {
              const districtIndex = provinces[provinceIndex].cities[cityIndex].districts.findIndex((d) => d.name === res.data.district);
              if (districtIndex >= 0) {
                pickerValue.value = [provinceIndex, cityIndex, districtIndex];
              }
            }
          }
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/address/edit.vue:144", "加载地址失败", error);
      }
    };
    const onRegionChange = (e) => {
      const [provinceIndex, cityIndex, districtIndex] = e.detail.value;
      pickerValue.value = [provinceIndex, cityIndex, districtIndex];
      formData.value.province = provinces[provinceIndex].name;
      formData.value.city = provinces[provinceIndex].cities[cityIndex].name;
      formData.value.district = provinces[provinceIndex].cities[cityIndex].districts[districtIndex].name;
    };
    const onDefaultChange = (e) => {
      formData.value.isDefault = e.detail.value;
    };
    const handleSave = async () => {
      if (!canSave.value) {
        common_vendor.index.showToast({
          title: "请填写完整信息",
          icon: "none"
        });
        return;
      }
      if (!formData.value.receiverPhone.match(/^1[3-9]\d{9}$/)) {
        common_vendor.index.showToast({
          title: "手机号格式不正确",
          icon: "none"
        });
        return;
      }
      try {
        common_vendor.index.__f__("log", "at pages/address/edit.vue:180", "保存地址 - addressId:", addressId.value, "formData:", formData.value);
        let res;
        if (addressId.value) {
          res = await api_address.addressApi.updateAddress(addressId.value, formData.value);
        } else {
          res = await api_address.addressApi.createAddress(formData.value);
        }
        common_vendor.index.__f__("log", "at pages/address/edit.vue:189", "地址保存响应:", res);
        if (res.code === 200) {
          if (res.data) {
            common_vendor.index.__f__("log", "at pages/address/edit.vue:194", "保存成功，返回的地址数据:", res.data);
            common_vendor.index.__f__("log", "at pages/address/edit.vue:195", "地址字段验证:", {
              id: res.data.id,
              receiverName: res.data.receiverName,
              receiverPhone: res.data.receiverPhone,
              province: res.data.province,
              city: res.data.city,
              district: res.data.district,
              detailAddress: res.data.detailAddress,
              isDefault: res.data.isDefault
            });
          }
          common_vendor.index.showToast({
            title: "保存成功",
            icon: "success"
          });
          setTimeout(() => {
            common_vendor.index.navigateBack();
          }, 1500);
        } else {
          common_vendor.index.__f__("error", "at pages/address/edit.vue:216", "地址保存失败:", res.message);
          common_vendor.index.showToast({
            title: res.message || "保存失败",
            icon: "none"
          });
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/address/edit.vue:223", "地址保存异常:", error);
        common_vendor.index.showToast({
          title: error.message || "保存失败",
          icon: "none"
        });
      }
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: formData.value.receiverName,
        b: common_vendor.o(($event) => formData.value.receiverName = $event.detail.value),
        c: formData.value.receiverPhone,
        d: common_vendor.o(($event) => formData.value.receiverPhone = $event.detail.value),
        e: formData.value.province
      }, formData.value.province ? {
        f: common_vendor.t(formData.value.province),
        g: common_vendor.t(formData.value.city),
        h: common_vendor.t(formData.value.district)
      } : {}, {
        i: pickerValue.value,
        j: pickerRange.value,
        k: ["name", "name", "name"],
        l: common_vendor.o(onRegionChange),
        m: formData.value.detailAddress,
        n: common_vendor.o(($event) => formData.value.detailAddress = $event.detail.value),
        o: formData.value.isDefault,
        p: common_vendor.o(onDefaultChange),
        q: common_vendor.o(handleSave),
        r: !canSave.value
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-dcb1f0d8"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/address/edit.js.map
