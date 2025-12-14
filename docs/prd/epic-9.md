# Epic 9: 个人中心（P1）

本Epic实现个人中心功能，包括个人信息编辑、地址管理和订单统计。

## Story 9.1: 用户信息更新API

As a user,
I want to update my profile information,
so that my account reflects my current details.

**Acceptance Criteria:**
1. 后端提供 `/api/user/profile` PUT接口
2. 支持更新昵称、头像、手机号等
3. 参数验证完善
4. 返回更新后的用户信息

## Story 9.2: 收货地址实体和API

As a developer,
I want to create the Address entity and APIs,
so that users can manage shipping addresses.

**Acceptance Criteria:**
1. Address实体类已创建
2. 地址CRUD API已实现
3. 支持设置默认地址
4. 地址验证完善

## Story 9.3: 订单统计API

As a user,
I want to see my order statistics,
so that I can understand my purchase activity.

**Acceptance Criteria:**
1. 后端提供 `/api/user/statistics` GET接口
2. 返回订单总数、待支付数、待收货数等
3. 返回作品总数、收藏数等
4. 接口响应时间小于100ms

## Story 9.4: 前端个人中心页面

As a user,
I want to access my profile and settings on the frontend,
so that I can manage my account easily.

**Acceptance Criteria:**
1. 个人中心页面已创建
2. 显示用户头像、昵称等基本信息
3. 显示订单统计信息
4. 提供入口到个人信息编辑、地址管理、订单列表等
5. 页面UI美观，符合设计规范

## Story 9.5: 前端地址管理页面

As a user,
I want to manage my shipping addresses on the frontend,
so that I can add and edit addresses for orders.

**Acceptance Criteria:**
1. 地址管理页面已创建
2. 显示地址列表
3. 支持添加、编辑、删除地址
4. 支持设置默认地址
5. 地址选择器已实现（省市区选择）
6. 页面UI美观，符合设计规范


