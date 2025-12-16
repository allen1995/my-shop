"use strict";
const common_vendor = require("../../common/vendor.js");
const api_work = require("../../api/work.js");
const utils_poster = require("../../utils/poster.js");
const _sfc_main = {
  __name: "detail",
  setup(__props) {
    const work = common_vendor.ref({});
    const workId = common_vendor.ref(null);
    const tags = common_vendor.ref([]);
    const editingTags = common_vendor.ref(false);
    const allCategories = common_vendor.ref([]);
    common_vendor.onLoad(async (options) => {
      workId.value = options.id;
      await loadWorkDetail();
    });
    const loadWorkDetail = async () => {
      try {
        const res = await api_work.workApi.getWorkDetail(workId.value);
        if (res.code === 200 && res.data) {
          work.value = res.data;
          if (work.value.tags) {
            try {
              tags.value = JSON.parse(work.value.tags);
            } catch (e) {
              tags.value = [];
            }
          } else {
            tags.value = [];
          }
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/works/detail.vue:98", "加载作品详情失败", error);
      }
    };
    const loadAllCategories = async () => {
      try {
        const catSet = /* @__PURE__ */ new Set();
        let page = 0;
        const size = 50;
        let hasMore = true;
        while (hasMore) {
          const res = await api_work.workApi.getWorks({ page, size });
          if (res.code === 200 && res.data) {
            const worksList = res.data.content || [];
            worksList.forEach((w) => {
              if (w.category) {
                catSet.add(w.category);
              }
            });
            hasMore = !res.data.last && worksList.length === size;
            page++;
          } else {
            hasMore = false;
          }
        }
        allCategories.value = Array.from(catSet).sort();
        common_vendor.index.__f__("log", "at pages/works/detail.vue:131", "加载所有分类:", allCategories.value);
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/works/detail.vue:133", "加载分类列表失败", error);
        throw error;
      }
    };
    const toggleEditTags = () => {
      editingTags.value = !editingTags.value;
    };
    const showCategoryModal = async () => {
      if (allCategories.value.length === 0) {
        common_vendor.index.showLoading({ title: "加载分类中..." });
        try {
          await loadAllCategories();
        } catch (error) {
          common_vendor.index.__f__("error", "at pages/works/detail.vue:149", "加载分类列表失败", error);
          common_vendor.index.hideLoading();
          common_vendor.index.showToast({
            title: "加载分类失败",
            icon: "none"
          });
          return;
        }
        common_vendor.index.hideLoading();
      }
      const actionList = [...allCategories.value];
      actionList.push("+ 自定义分类");
      if (work.value.category) {
        actionList.push("清除分类");
      }
      common_vendor.index.showActionSheet({
        itemList: actionList,
        success: async (res) => {
          const selectedIndex = res.tapIndex;
          let newCategory = null;
          if (selectedIndex < allCategories.value.length) {
            newCategory = allCategories.value[selectedIndex];
          } else if (actionList[selectedIndex] === "+ 自定义分类") {
            common_vendor.index.showModal({
              title: "自定义分类",
              editable: true,
              placeholderText: "请输入分类名称",
              success: async (modalRes) => {
                if (modalRes.confirm && modalRes.content && modalRes.content.trim()) {
                  const customCategory = modalRes.content.trim();
                  await updateCategory(customCategory);
                }
              }
            });
            return;
          } else if (actionList[selectedIndex] === "清除分类") {
            newCategory = null;
          }
          if (newCategory !== void 0) {
            await updateCategory(newCategory);
          }
        }
      });
    };
    const updateCategory = async (category) => {
      try {
        const updateRes = await api_work.workApi.updateCategory(workId.value, category);
        if (updateRes.code === 200 && updateRes.data) {
          work.value = updateRes.data;
          await loadAllCategories();
          common_vendor.index.showToast({
            title: category ? "分类设置成功" : "分类已清除",
            icon: "success",
            duration: 1e3
          });
        } else {
          common_vendor.index.showToast({
            title: updateRes.message || "设置分类失败",
            icon: "none"
          });
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/works/detail.vue:226", "设置分类失败", error);
        common_vendor.index.showToast({
          title: "设置分类失败",
          icon: "none"
        });
      }
    };
    common_vendor.onShow(() => {
      if (workId.value) {
        loadWorkDetail();
      }
    });
    const showAddTagModal = () => {
      common_vendor.index.showModal({
        title: "添加标签",
        editable: true,
        placeholderText: "请输入标签名称",
        success: async (res) => {
          if (res.confirm && res.content && res.content.trim()) {
            const newTag = res.content.trim();
            if (!tags.value.includes(newTag)) {
              try {
                const addRes = await api_work.workApi.addTag(workId.value, newTag);
                if (addRes.code === 200) {
                  tags.value.push(newTag);
                  work.value = addRes.data;
                }
              } catch (error) {
                common_vendor.index.showToast({
                  title: "添加标签失败",
                  icon: "none"
                });
              }
            } else {
              common_vendor.index.showToast({
                title: "标签已存在",
                icon: "none"
              });
            }
          }
        }
      });
    };
    const removeTag = async (tag) => {
      try {
        const res = await api_work.workApi.removeTag(workId.value, tag);
        if (res.code === 200) {
          tags.value = tags.value.filter((t) => t !== tag);
          work.value = res.data;
        }
      } catch (error) {
        common_vendor.index.showToast({
          title: "删除标签失败",
          icon: "none"
        });
      }
    };
    const previewImage = () => {
      common_vendor.index.previewImage({
        urls: [work.value.imageUrl],
        current: work.value.imageUrl
      });
    };
    const handleApply = () => {
      common_vendor.index.navigateTo({
        url: `/pages/preview/preview?workId=${workId.value}&imageUrl=${encodeURIComponent(work.value.imageUrl)}`
      });
    };
    const handleToggleFavorite = async () => {
      try {
        const res = await api_work.workApi.toggleFavorite(workId.value);
        if (res.code === 200 && res.data) {
          work.value.isFavorite = res.data.isFavorite;
          common_vendor.index.showToast({
            title: work.value.isFavorite ? "已收藏" : "已取消收藏",
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
    const handleDelete = () => {
      common_vendor.index.showModal({
        title: "确认删除",
        content: "确定要删除这个作品吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              const deleteRes = await api_work.workApi.deleteWork(workId.value);
              if (deleteRes.code === 200) {
                common_vendor.index.showToast({
                  title: "删除成功",
                  icon: "success"
                });
                setTimeout(() => {
                  common_vendor.index.navigateBack();
                }, 1500);
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
    const handleShare = () => {
      common_vendor.index.showActionSheet({
        itemList: ["分享到微信好友", "分享到朋友圈", "生成分享海报", "保存海报到相册"],
        success: async (res) => {
          if (res.tapIndex === 0) {
            shareToWeChat("session");
          } else if (res.tapIndex === 1) {
            shareToWeChat("timeline");
          } else if (res.tapIndex === 2) {
            await generateSharePoster();
          } else if (res.tapIndex === 3) {
            await saveSharePoster();
          }
        }
      });
    };
    const shareToWeChat = (scene) => {
      const shareUrl = `/pages/works/detail?id=${workId.value}`;
      const shareTitle = work.value.title || "我的AI作品";
      const shareImageUrl = work.value.imageUrl;
      common_vendor.index.showShareMenu({
        withShareTicket: true,
        menus: ["shareAppMessage", "shareTimeline"]
      });
      if (scene === "session") {
        common_vendor.index.shareAppMessage({
          title: shareTitle,
          path: shareUrl,
          imageUrl: shareImageUrl
        });
      } else if (scene === "timeline") {
        common_vendor.index.shareTimeline({
          title: shareTitle,
          imageUrl: shareImageUrl
        });
      }
    };
    const generateSharePoster = async () => {
      common_vendor.index.showLoading({
        title: "生成中..."
      });
      try {
        const shareUrl = `https://your-domain.com/pages/works/detail?id=${workId.value}`;
        const posterPath = await utils_poster.generatePoster({
          imageUrl: work.value.imageUrl,
          title: work.value.title || "我的AI作品",
          shareUrl
        });
        common_vendor.index.hideLoading();
        common_vendor.index.previewImage({
          urls: [posterPath],
          current: posterPath
        });
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/works/detail.vue:422", "生成海报失败", error);
        common_vendor.index.showToast({
          title: "生成失败",
          icon: "none"
        });
      }
    };
    const saveSharePoster = async () => {
      common_vendor.index.showLoading({
        title: "生成中..."
      });
      try {
        const shareUrl = `https://your-domain.com/pages/works/detail?id=${workId.value}`;
        const posterPath = await utils_poster.generatePoster({
          imageUrl: work.value.imageUrl,
          title: work.value.title || "我的AI作品",
          shareUrl
        });
        await utils_poster.savePosterToAlbum(posterPath);
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/works/detail.vue:445", "保存海报失败", error);
        common_vendor.index.showToast({
          title: "保存失败",
          icon: "none"
        });
      } finally {
        common_vendor.index.hideLoading();
      }
    };
    const formatTime = (timeStr) => {
      if (!timeStr)
        return "";
      const date = new Date(timeStr);
      return date.toLocaleString("zh-CN");
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: work.value.imageUrl,
        b: common_vendor.o(previewImage),
        c: common_vendor.t(work.value.title),
        d: work.value.description
      }, work.value.description ? {
        e: common_vendor.t(work.value.description)
      } : {}, {
        f: work.value.category
      }, work.value.category ? {
        g: common_vendor.t(work.value.category)
      } : {}, {
        h: common_vendor.o(showCategoryModal),
        i: tags.value.length > 0 || editingTags.value
      }, tags.value.length > 0 || editingTags.value ? common_vendor.e({
        j: common_vendor.f(tags.value, (tag, index, i0) => {
          return common_vendor.e({
            a: common_vendor.t(tag)
          }, editingTags.value ? {
            b: common_vendor.o(($event) => removeTag(tag), index)
          } : {}, {
            c: index
          });
        }),
        k: editingTags.value,
        l: editingTags.value
      }, editingTags.value ? {
        m: common_vendor.o(showAddTagModal)
      } : {}, {
        n: common_vendor.t(editingTags.value ? "完成" : "编辑标签"),
        o: common_vendor.o(toggleEditTags)
      }) : {}, {
        p: common_vendor.t(formatTime(work.value.createTime)),
        q: common_vendor.t(work.value.isFavorite ? "⭐ 已收藏" : "☆ 收藏"),
        r: work.value.isFavorite ? 1 : "",
        s: common_vendor.o(handleToggleFavorite),
        t: common_vendor.o(handleShare),
        v: common_vendor.o(handleApply),
        w: common_vendor.o(handleDelete)
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-dfde3efa"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/works/detail.js.map
