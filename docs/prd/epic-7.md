# Epic 7: 作品集增强功能（P1）

本Epic实现作品集的增强功能，包括分类、标签和收藏功能，提升作品管理的便利性。

## Story 7.1: 作品分类功能

As a user,
I want to organize my works into categories,
so that I can find them more easily.

**Acceptance Criteria:**
1. 作品实体添加category字段
2. 后端提供 `/api/works/{workId}/category` PUT接口更新分类
3. 作品列表API支持按分类筛选
4. 前端作品集页面支持分类选择
5. 支持自定义分类名称

## Story 7.2: 作品标签功能

As a user,
I want to add tags to my works,
so that I can organize and search them better.

**Acceptance Criteria:**
1. 作品实体支持多标签（tags字段为数组）
2. 后端提供标签管理接口
3. 作品列表API支持按标签筛选
4. 前端支持添加、编辑、删除标签
5. 标签自动补全功能（可选）

## Story 7.3: 作品收藏功能

As a user,
I want to mark works as favorites,
so that I can quickly access my preferred works.

**Acceptance Criteria:**
1. 作品实体添加isFavorite字段
2. 后端提供 `/api/works/{workId}/favorite` PUT接口切换收藏状态
3. 作品列表API支持筛选收藏作品
4. 前端支持收藏/取消收藏操作
5. 收藏状态视觉反馈明显


