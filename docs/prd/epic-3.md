# Epic 3: 作品集管理

本Epic实现作品集的基础管理功能，包括作品的保存、查看、删除等操作。这是用户管理自己创作内容的核心功能，需要确保操作流畅、数据安全。

## Story 3.1: 作品实体和数据库设计

As a developer,
I want to create the Work entity and database schema,
so that user works can be stored and managed.

**Acceptance Criteria:**
1. Work实体类已创建，包含字段：id、userId、title、description、imageUrl、tags、createTime、updateTime
2. WorkRepository接口已创建
3. 数据库表自动创建功能已配置
4. 能够通过Repository进行作品的增删改查操作
5. 支持按用户ID查询作品列表
6. 实体类包含必要的验证注解

## Story 3.2: 保存作品API接口

As a user,
I want to save a generated image to my collection,
so that I can access it later for preview and purchase.

**Acceptance Criteria:**
1. 后端提供 `/api/works` POST接口
2. 接口接收title、description、imageUrl、tags参数
3. 创建作品记录到数据库
4. 返回创建的作品信息
5. 接口包含参数验证和错误处理
6. 支持用户身份验证

## Story 3.3: 作品列表查询API

As a user,
I want to view my collection of works,
so that I can browse and manage my created artworks.

**Acceptance Criteria:**
1. 后端提供 `/api/works` GET接口
2. 接口支持分页查询（page、size参数）
3. 返回当前用户的作品列表
4. 列表按创建时间倒序排列
5. 接口响应时间小于100ms
6. 支持按标题搜索（可选）

## Story 3.4: 作品详情查询API

As a user,
I want to view the details of a specific work,
so that I can see all information about it.

**Acceptance Criteria:**
1. 后端提供 `/api/works/{workId}` GET接口
2. 接口返回作品的完整信息
3. 验证作品属于当前用户
4. 支持作品不存在的情况处理
5. 接口响应时间小于100ms

## Story 3.5: 删除作品API接口

As a user,
I want to delete a work from my collection,
so that I can remove unwanted artworks.

**Acceptance Criteria:**
1. 后端提供 `/api/works/{workId}` DELETE接口
2. 验证作品属于当前用户
3. 删除作品记录（可选：同时删除OSS中的图片）
4. 返回删除成功信息
5. 支持作品不存在的情况处理

## Story 3.6: 前端作品集页面

As a user,
I want to view my collection on the frontend,
so that I can browse and manage my works easily.

**Acceptance Criteria:**
1. 作品集页面已创建
2. 作品以卡片网格形式展示
3. 支持下拉刷新和上拉加载更多
4. 点击作品卡片跳转到作品详情页
5. 页面UI美观，符合设计规范
6. 空状态提示已实现（无作品时）

## Story 3.7: 前端作品详情页面

As a user,
I want to view work details on the frontend,
so that I can see all information and take actions.

**Acceptance Criteria:**
1. 作品详情页面已创建
2. 大图展示作品图片，支持缩放查看
3. 显示作品标题、描述、标签等信息
4. "删除作品"按钮已实现，带确认提示
5. "应用到包包"按钮已实现，跳转到预览页
6. 页面UI美观，符合设计规范


