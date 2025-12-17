"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const api_work = require("../../api/work.js");
const _sfc_main = {
  __name: "list",
  setup(__props) {
    const works = common_vendor.ref([]);
    const page = common_vendor.ref(0);
    const size = common_vendor.ref(20);
    const loading = common_vendor.ref(false);
    const hasMore = common_vendor.ref(true);
    const selectedCategory = common_vendor.ref(null);
    const categories = common_vendor.ref([]);
    const showFavorites = common_vendor.ref(false);
    const loadWorks = async (reset = false) => {
      if (loading.value)
        return;
      loading.value = true;
      try {
        if (reset) {
          page.value = 0;
          works.value = [];
        }
        const params = {
          page: page.value,
          size: size.value
        };
        if (showFavorites.value) {
          params.favorite = true;
        } else if (selectedCategory.value) {
          params.category = selectedCategory.value;
        }
        const res = await api_work.workApi.getWorks(params);
        if (res.code === 200 && res.data) {
          const newWorks = res.data.content || [];
          works.value = reset ? newWorks : [...works.value, ...newWorks];
          hasMore.value = newWorks.length === size.value;
          page.value++;
          extractCategories(newWorks);
          common_vendor.index.__f__("log", "at pages/works/list.vue:106", "加载作品后分类列表:", categories.value);
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/works/list.vue:109", "加载作品失败", error);
      } finally {
        loading.value = false;
        common_vendor.index.stopPullDownRefresh();
      }
    };
    const loadAllCategories = async () => {
      try {
        const res = await api_work.workApi.getWorks({ page: 0, size: 1e3 });
        if (res.code === 200 && res.data) {
          const worksList = res.data.content || [];
          const catSet = new Set(categories.value);
          worksList.forEach((work) => {
            if (work.category) {
              catSet.add(work.category);
            }
          });
          categories.value = Array.from(catSet).sort();
          common_vendor.index.__f__("log", "at pages/works/list.vue:130", "加载所有分类:", categories.value);
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/works/list.vue:133", "加载分类列表失败", error);
      }
    };
    const extractCategories = (worksList) => {
      const catSet = new Set(categories.value);
      worksList.forEach((work) => {
        if (work.category) {
          catSet.add(work.category);
        }
      });
      categories.value = Array.from(catSet).sort();
    };
    const selectCategory = (category) => {
      selectedCategory.value = category;
      showFavorites.value = false;
      loadWorks(true);
    };
    const toggleFavorites = () => {
      showFavorites.value = !showFavorites.value;
      selectedCategory.value = null;
      loadWorks(true);
    };
    const showAddCategoryModal = () => {
      common_vendor.index.showModal({
        title: "添加分类",
        editable: true,
        placeholderText: "请输入分类名称",
        success: async (res) => {
          if (res.confirm && res.content && res.content.trim()) {
            const newCategory = res.content.trim();
            common_vendor.index.__f__("log", "at pages/works/list.vue:167", "添加分类:", newCategory);
            if (!categories.value.includes(newCategory)) {
              categories.value.push(newCategory);
              categories.value.sort();
              common_vendor.index.__f__("log", "at pages/works/list.vue:173", "分类列表已更新:", categories.value);
            }
            await loadAllCategories();
            selectCategory(newCategory);
          }
        }
      });
    };
    const toggleFavorite = async (work) => {
      try {
        const res = await api_work.workApi.toggleFavorite(work.id);
        if (res.code === 200 && res.data) {
          work.isFavorite = res.data.isFavorite;
          common_vendor.index.showToast({
            title: work.isFavorite ? "已收藏" : "已取消收藏",
            icon: "success",
            duration: 1e3
          });
        }
      } catch (error) {
        common_vendor.index.showToast({
          title: "操作失败",
          icon: "none"
        });
      }
    };
    const goToDetail = (workId) => {
      common_vendor.index.navigateTo({
        url: `/pages/works/detail?id=${workId}`
      });
    };
    const goToGenerate = () => {
      common_vendor.index.switchTab({
        url: "/pages/index/index"
      });
    };
    const formatTime = (timeStr) => {
      if (!timeStr)
        return "";
      const date = new Date(timeStr);
      const now = /* @__PURE__ */ new Date();
      const diff = now - date;
      if (diff < 6e4)
        return "刚刚";
      if (diff < 36e5)
        return Math.floor(diff / 6e4) + "分钟前";
      if (diff < 864e5)
        return Math.floor(diff / 36e5) + "小时前";
      return Math.floor(diff / 864e5) + "天前";
    };
    common_vendor.onMounted(() => {
      loadWorks(true);
      loadAllCategories();
    });
    common_vendor.onShow(() => {
      const app = getApp();
      if (app && app.globalData && app.globalData.workDeleted) {
        const deletedWorkId = app.globalData.deletedWorkId;
        common_vendor.index.__f__("log", "at pages/works/list.vue:240", "检测到作品被删除，刷新列表", deletedWorkId);
        if (deletedWorkId) {
          works.value = works.value.filter((work) => work.id !== deletedWorkId);
        }
        loadWorks(true);
        app.globalData.workDeleted = false;
        app.globalData.deletedWorkId = null;
      } else {
        loadAllCategories();
      }
    });
    common_vendor.onPullDownRefresh(() => {
      loadWorks(true);
      loadAllCategories();
    });
    common_vendor.onReachBottom(() => {
      if (hasMore.value) {
        loadWorks();
      }
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: selectedCategory.value === null ? 1 : "",
        b: common_vendor.o(($event) => selectCategory(null)),
        c: common_vendor.f(categories.value, (cat, k0, i0) => {
          return {
            a: common_vendor.t(cat),
            b: cat,
            c: selectedCategory.value === cat ? 1 : "",
            d: common_vendor.o(($event) => selectCategory(cat), cat)
          };
        }),
        d: common_vendor.o(showAddCategoryModal),
        e: showFavorites.value ? 1 : "",
        f: common_vendor.o(toggleFavorites),
        g: works.value.length > 0
      }, works.value.length > 0 ? {
        h: common_vendor.f(works.value, (work, k0, i0) => {
          return {
            a: work.imageUrl,
            b: common_vendor.t(work.title),
            c: work.isFavorite ? 1 : "",
            d: common_vendor.o(($event) => toggleFavorite(work), work.id),
            e: common_vendor.t(formatTime(work.createTime)),
            f: work.id,
            g: common_vendor.o(($event) => goToDetail(work.id), work.id)
          };
        })
      } : {
        i: common_assets._imports_0$3,
        j: common_vendor.o(goToGenerate)
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-856f9690"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/works/list.js.map
