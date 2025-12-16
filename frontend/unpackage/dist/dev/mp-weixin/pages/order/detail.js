"use strict";
const common_vendor = require("../../common/vendor.js");
const api_order = require("../../api/order.js");
const _sfc_main = {
  __name: "detail",
  setup(__props) {
    const order = common_vendor.ref({});
    const orderId = common_vendor.ref(null);
    common_vendor.onLoad(async (options) => {
      orderId.value = options.id;
      await loadOrderDetail();
    });
    const loadOrderDetail = async () => {
      try {
        const res = await api_order.orderApi.getOrderDetail(orderId.value);
        if (res.code === 200 && res.data) {
          order.value = res.data;
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/order/detail.vue:130", "加载订单详情失败", error);
        common_vendor.index.showToast({
          title: "加载失败",
          icon: "none"
        });
      }
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
    const getStatusDesc = (status) => {
      const descMap = {
        "PENDING_PAYMENT": "请尽快完成支付",
        "PAID": "订单已支付，等待发货",
        "PROCESSING": "商家正在准备发货",
        "SHIPPED": "商品已发货，请注意查收",
        "COMPLETED": "订单已完成",
        "CANCELLED": "订单已取消"
      };
      return descMap[status] || "";
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
    const isStatusReached = (targetStatus) => {
      const statusOrder = ["PENDING_PAYMENT", "PAID", "PROCESSING", "SHIPPED", "COMPLETED"];
      const currentIndex = statusOrder.indexOf(order.value.status);
      const targetIndex = statusOrder.indexOf(targetStatus);
      return currentIndex >= targetIndex;
    };
    const handleCancel = () => {
      common_vendor.index.showModal({
        title: "确认取消",
        content: "确定要取消这个订单吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              const cancelRes = await api_order.orderApi.cancelOrder(orderId.value);
              if (cancelRes.code === 200) {
                common_vendor.index.showToast({
                  title: "取消成功",
                  icon: "success"
                });
                await loadOrderDetail();
              } else {
                common_vendor.index.showToast({
                  title: cancelRes.message || "取消失败",
                  icon: "none"
                });
              }
            } catch (error) {
              common_vendor.index.showToast({
                title: "取消失败",
                icon: "none"
              });
            }
          }
        }
      });
    };
    const handleConfirm = () => {
      common_vendor.index.showModal({
        title: "确认收货",
        content: "确认已收到商品？",
        success: async (res) => {
          if (res.confirm) {
            try {
              const confirmRes = await api_order.orderApi.confirmReceipt(orderId.value);
              if (confirmRes.code === 200) {
                common_vendor.index.showToast({
                  title: "确认成功",
                  icon: "success"
                });
                await loadOrderDetail();
              } else {
                common_vendor.index.showToast({
                  title: confirmRes.message || "确认失败",
                  icon: "none"
                });
              }
            } catch (error) {
              common_vendor.index.showToast({
                title: "确认失败",
                icon: "none"
              });
            }
          }
        }
      });
    };
    const handlePay = () => {
      common_vendor.index.navigateTo({
        url: `/pages/order/payment?orderId=${orderId.value}`
      });
    };
    const formatTime = (timeStr) => {
      if (!timeStr)
        return "";
      const date = new Date(timeStr);
      return date.toLocaleString("zh-CN");
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.t(getStatusText(order.value.status)),
        b: common_vendor.n(getStatusClass(order.value.status)),
        c: common_vendor.t(getStatusDesc(order.value.status)),
        d: order.value.status !== "PENDING_PAYMENT"
      }, order.value.status !== "PENDING_PAYMENT" ? {
        e: isStatusReached("PENDING_PAYMENT") ? 1 : "",
        f: isStatusReached("PAID") || isStatusReached("PROCESSING") ? 1 : "",
        g: isStatusReached("PROCESSING") || isStatusReached("SHIPPED") ? 1 : "",
        h: isStatusReached("SHIPPED") || isStatusReached("COMPLETED") ? 1 : "",
        i: isStatusReached("COMPLETED") ? 1 : ""
      } : {}, {
        j: common_vendor.t(order.value.orderNo),
        k: common_vendor.t(formatTime(order.value.createTime)),
        l: order.value.paymentTime
      }, order.value.paymentTime ? {
        m: common_vendor.t(formatTime(order.value.paymentTime))
      } : {}, {
        n: common_vendor.f(order.value.items, (item, index, i0) => {
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
        o: common_vendor.t(order.value.totalAmount),
        p: common_vendor.t(order.value.totalAmount),
        q: order.value.status === "PENDING_PAYMENT"
      }, order.value.status === "PENDING_PAYMENT" ? {
        r: common_vendor.o(handleCancel)
      } : {}, {
        s: order.value.status === "SHIPPED"
      }, order.value.status === "SHIPPED" ? {
        t: common_vendor.o(handleConfirm)
      } : {}, {
        v: order.value.status === "PENDING_PAYMENT"
      }, order.value.status === "PENDING_PAYMENT" ? {
        w: common_vendor.o(handlePay)
      } : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-6b23c96c"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/order/detail.js.map
