# Epic 5: 购物车与订单

本Epic实现购物车管理和订单创建、支付功能。这是电商的核心功能，需要确保流程顺畅、支付安全可靠。

## Story 5.1: 购物车实体和数据库设计

As a developer,
I want to create the CartItem entity and database schema,
so that cart items can be stored and managed.

**Acceptance Criteria:**
1. CartItem实体类已创建，包含字段：id、userId、workId、productId、color、size、quantity、previewImageUrl、createTime、updateTime
2. CartItemRepository接口已创建
3. 数据库表自动创建功能已配置
4. 能够通过Repository进行购物车项的增删改查操作
5. 支持按用户ID查询购物车
6. 实体类包含必要的验证注解

## Story 5.2: 添加到购物车API

As a user,
I want to add a work to my shopping cart,
so that I can purchase it later.

**Acceptance Criteria:**
1. 后端提供 `/api/cart/items` POST接口
2. 接口接收workId、productId、color、size、quantity、previewImageUrl参数
3. 创建购物车项记录
4. 如果相同商品已存在，则更新数量
5. 返回购物车项信息
6. 接口包含参数验证和错误处理

## Story 5.3: 购物车列表查询API

As a user,
I want to view my shopping cart,
so that I can review items before checkout.

**Acceptance Criteria:**
1. 后端提供 `/api/cart/items` GET接口
2. 返回当前用户的所有购物车项
3. 包含作品信息、商品信息、规格、数量等
4. 计算总价
5. 接口响应时间小于100ms

## Story 5.4: 更新购物车项API

As a user,
I want to update cart item quantity or specifications,
so that I can modify my order before checkout.

**Acceptance Criteria:**
1. 后端提供 `/api/cart/items/{itemId}` PUT接口
2. 支持更新数量、颜色、尺寸等
3. 验证购物车项属于当前用户
4. 返回更新后的购物车项信息

## Story 5.5: 删除购物车项API

As a user,
I want to remove items from my cart,
so that I can clean up unwanted items.

**Acceptance Criteria:**
1. 后端提供 `/api/cart/items/{itemId}` DELETE接口
2. 验证购物车项属于当前用户
3. 删除购物车项记录
4. 返回删除成功信息

## Story 5.6: 订单实体和数据库设计

As a developer,
I want to create the Order entity and database schema,
so that orders can be stored and managed.

**Acceptance Criteria:**
1. Order实体类已创建，包含字段：id、userId、orderNo、status、totalAmount、items、addressId、paymentStatus、createTime、updateTime
2. OrderItem实体类已创建，包含订单项信息
3. OrderRepository接口已创建
4. 数据库表自动创建功能已配置
5. 订单状态枚举已定义（PENDING_PAYMENT、PAID、PROCESSING、SHIPPED、COMPLETED、CANCELLED）
6. 能够通过Repository进行订单的增删改查操作

## Story 5.7: 创建订单API

As a user,
I want to create an order from my cart,
so that I can proceed to payment.

**Acceptance Criteria:**
1. 后端提供 `/api/orders` POST接口
2. 接口接收cartItemIds和addressId参数
3. 验证购物车项属于当前用户
4. 计算订单总价
5. 创建订单和订单项记录
6. 清空购物车中的已下单项
7. 返回订单信息（包含订单号）
8. 接口包含参数验证和错误处理

## Story 5.8: 微信支付集成

As a developer,
I want to integrate WeChat Pay API,
so that users can complete payment for their orders.

**Acceptance Criteria:**
1. 微信支付SDK已集成
2. 统一下单接口已实现
3. 支付回调接口已实现
4. 支付成功后更新订单状态
5. 支付失败处理完善
6. 支付参数签名验证

## Story 5.9: 创建支付订单API

As a user,
I want to create a payment order,
so that I can pay for my purchase.

**Acceptance Criteria:**
1. 后端提供 `/api/orders/{orderId}/payment` POST接口
2. 调用微信支付统一下单API
3. 返回支付参数给前端
4. 包含appId、timeStamp、nonceStr、package、signType、paySign等参数
5. 接口包含错误处理

## Story 5.10: 支付回调处理

As a developer,
I want to handle WeChat payment callbacks,
so that order status can be updated automatically after payment.

**Acceptance Criteria:**
1. 支付回调接口 `/api/payment/notify` 已实现
2. 验证回调签名
3. 更新订单支付状态
4. 处理重复回调（幂等性）
5. 返回成功响应给微信

## Story 5.11: 订单状态查询API

As a user,
I want to check my order status,
so that I can track my purchase.

**Acceptance Criteria:**
1. 后端提供 `/api/orders/{orderId}` GET接口
2. 返回订单的完整信息和当前状态
3. 验证订单属于当前用户
4. 接口响应时间小于100ms

## Story 5.12: 前端购物车页面

As a user,
I want to view and manage my cart on the frontend,
so that I can review items before checkout.

**Acceptance Criteria:**
1. 购物车页面已创建
2. 显示所有购物车项，包含作品预览图、商品信息、规格、数量、价格
3. 支持修改数量（+/-按钮）
4. 支持删除购物车项（滑动删除或点击删除）
5. 显示总价
6. "去结算"按钮已实现
7. 空状态提示已实现
8. 页面UI美观，符合设计规范

## Story 5.13: 前端订单确认页面

As a user,
I want to confirm my order details before payment,
so that I can verify everything is correct.

**Acceptance Criteria:**
1. 订单确认页面已创建
2. 显示订单商品列表
3. 显示收货地址选择（如果已设置）
4. 显示订单总价
5. "提交订单"按钮已实现
6. 页面UI美观，符合设计规范

## Story 5.14: 前端支付页面

As a user,
I want to complete payment on the frontend,
so that I can purchase my customized bag.

**Acceptance Criteria:**
1. 支付页面已创建
2. 调用后端创建支付订单接口
3. 调用 `uni.requestPayment` 发起微信支付
4. 支付成功后跳转到订单详情页
5. 支付失败时显示错误提示
6. 页面UI美观，符合设计规范


