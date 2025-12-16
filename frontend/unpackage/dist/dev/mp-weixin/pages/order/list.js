"use strict";
const common_vendor = require("../../common/vendor.js");
const api_order = require("../../api/order.js");
const _sfc_main = {
  __name: "list",
  setup(__props) {
    const orders = common_vendor.ref([]);
    const page = common_vendor.ref(0);
    const size = common_vendor.ref(20);
    const loading = common_vendor.ref(false);
    const hasMore = common_vendor.ref(true);
    const selectedStatus = common_vendor.ref(null);
    const statusOptions = [
      { label: "待支付", value: "PENDING_PAYMENT" },
      { label: "待发货", value: "PROCESSING" },
      { label: "待收货", value: "SHIPPED" },
      { label: "已完成", value: "COMPLETED" }
    ];
    const loadOrders = async (reset = false) => {
      if (loading.value)
        return;
      loading.value = true;
      try {
        if (reset) {
          page.value = 0;
          orders.value = [];
        }
        const params = {
          page: page.value,
          size: size.value
        };
        if (selectedStatus.value) {
          params.status = selectedStatus.value;
        }
        const res = await api_order.orderApi.getOrders(params);
        if (res.code === 200 && res.data) {
          const newOrders = res.data.content || [];
          orders.value = reset ? newOrders : [...orders.value, ...newOrders];
          hasMore.value = newOrders.length === size.value;
          page.value++;
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/order/list.vue:116", "加载订单失败", error);
      } finally {
        loading.value = false;
        common_vendor.index.stopPullDownRefresh();
      }
    };
    const selectStatus = (status) => {
      selectedStatus.value = status;
      loadOrders(true);
    };
    const goToDetail = (orderId) => {
      common_vendor.index.navigateTo({
        url: `/pages/order/detail?id=${orderId}`
      });
    };
    const getStatusText = (status) => {
      const statusMap = {
        "PENDING_PAYMENT": "待支付",
        "PAID": "已支付",
        "PROCESSING": "待发货",
        "SHIPPED": "待收货",
        "COMPLETED": "已完成",
        "CANCELLED": "已取消"
      };
      return statusMap[status] || status;
    };
    const getStatusClass = (status) => {
      const classMap = {
        "PENDING_PAYMENT": "status-pending",
        "PAID": "status-paid",
        "PROCESSING": "status-processing",
        "SHIPPED": "status-shipped",
        "COMPLETED": "status-completed",
        "CANCELLED": "status-cancelled"
      };
      return classMap[status] || "";
    };
    const formatTime = (timeStr) => {
      if (!timeStr)
        return "";
      const date = new Date(timeStr);
      return date.toLocaleString("zh-CN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit"
      });
    };
    common_vendor.onMounted(() => {
      loadOrders(true);
    });
    common_vendor.onPullDownRefresh(() => {
      loadOrders(true);
    });
    common_vendor.onReachBottom(() => {
      if (hasMore.value) {
        loadOrders();
      }
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: selectedStatus.value === null ? 1 : "",
        b: common_vendor.o(($event) => selectStatus(null)),
        c: common_vendor.f(statusOptions, (status, k0, i0) => {
          return {
            a: common_vendor.t(status.label),
            b: status.value,
            c: selectedStatus.value === status.value ? 1 : "",
            d: common_vendor.o(($event) => selectStatus(status.value), status.value)
          };
        }),
        d: orders.value.length > 0
      }, orders.value.length > 0 ? {
        e: common_vendor.f(orders.value, (order, k0, i0) => {
          return {
            a: common_vendor.t(order.orderNo),
            b: common_vendor.t(getStatusText(order.status)),
            c: common_vendor.n(getStatusClass(order.status)),
            d: common_vendor.f(order.items, (item, index, i1) => {
              return common_vendor.e({
                a: item.previewImageUrl
              }, item.previewImageUrl ? {
                b: item.previewImageUrl
              } : {}, {
                c: common_vendor.t(item.color),
                d: common_vendor.t(item.size),
                e: common_vendor.t(item.quantity),
                f: common_vendor.t(item.price),
                g: index
              });
            }),
            e: common_vendor.t(formatTime(order.createTime)),
            f: common_vendor.t(order.totalAmount),
            g: order.id,
            h: common_vendor.o(($event) => goToDetail(order.id), order.id)
          };
        })
      } : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-456ecf67"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/order/list.js.map
