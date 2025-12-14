# Epic 4: 印花预览功能

本Epic实现包包印花预览功能，包括包包款式选择、印花应用预览和位置调整。这是用户决策购买的关键环节，需要提供直观、流畅的预览体验。

## Story 4.1: 包包商品实体和数据库设计

As a developer,
I want to create the Product entity and database schema,
so that bag products can be stored and managed.

**Acceptance Criteria:**
1. Product实体类已创建，包含字段：id、name、type、description、basePrice、colors、sizes、imageUrl、createTime、updateTime
2. ProductRepository接口已创建
3. 数据库表自动创建功能已配置
4. 能够通过Repository进行商品的增删改查操作
5. 支持按类型查询商品列表
6. 实体类包含必要的验证注解

## Story 4.2: 商品列表查询API

As a user,
I want to view available bag products,
so that I can choose which bag to apply my design to.

**Acceptance Criteria:**
1. 后端提供 `/api/products` GET接口
2. 接口返回所有可用的包包商品
3. 返回商品的基本信息（名称、类型、价格、图片等）
4. 接口响应时间小于100ms

## Story 4.3: 商品详情查询API

As a user,
I want to view product details,
so that I can see all available options (colors, sizes).

**Acceptance Criteria:**
1. 后端提供 `/api/products/{productId}` GET接口
2. 接口返回商品的完整信息
3. 包含所有可用的颜色和尺寸选项
4. 接口响应时间小于100ms

## Story 4.4: 前端印花预览页面

As a user,
I want to preview how my work looks on a bag,
so that I can decide if I want to purchase it.

**Acceptance Criteria:**
1. 印花预览页面已创建
2. 显示包包基础图片
3. 支持选择包包款式（下拉选择或卡片选择）
4. 支持选择颜色和尺寸
5. 将作品图片叠加到包包图片上预览
6. 支持调整印花位置（拖拽或点击位置）
7. 实时预览效果
8. 页面UI美观，符合设计规范

## Story 4.5: Canvas图片合成功能

As a developer,
I want to implement image composition using Canvas,
so that users can see the print effect on the bag.

**Acceptance Criteria:**
1. 使用uni.canvasToTempFilePath进行图片合成
2. 将作品图片叠加到包包图片上
3. 支持调整印花位置和大小
4. 生成预览图片
5. 性能优化，合成时间小于1秒


