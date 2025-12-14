# Epic 8: 订单管理（P1）

本Epic实现完整的订单管理功能，包括订单列表、详情查看和状态跟踪。

## Story 8.1: 订单列表查询API

As a user,
I want to view all my orders,
so that I can track my purchase history.

**Acceptance Criteria:**
1. 后端提供 `/api/orders` GET接口
2. 支持分页查询
3. 支持按状态筛选
4. 返回订单列表，包含基本信息
5. 接口响应时间小于100ms

## Story 8.2: 订单详情API增强

As a user,
I want to view complete order details,
so that I can see all information about my order.

**Acceptance Criteria:**
1. 订单详情API返回完整信息
2. 包含订单项、收货地址、支付信息、物流信息等
3. 支持订单状态历史记录
4. 接口响应时间小于100ms

## Story 8.3: 前端订单列表页面

As a user,
I want to view my orders on the frontend,
so that I can track my purchases easily.

**Acceptance Criteria:**
1. 订单列表页面已创建
2. 显示订单列表，包含订单号、状态、总价、创建时间
3. 支持按状态筛选（全部、待支付、待发货、待收货、已完成）
4. 支持下拉刷新和上拉加载更多
5. 点击订单跳转到订单详情页
6. 页面UI美观，符合设计规范

## Story 8.4: 前端订单详情页面

As a user,
I want to view order details on the frontend,
so that I can see all information and take actions.

**Acceptance Criteria:**
1. 订单详情页面已创建
2. 显示订单完整信息
3. 显示订单状态和状态流转
4. 支持取消订单（待支付状态）
5. 支持确认收货（已发货状态）
6. 页面UI美观，符合设计规范


