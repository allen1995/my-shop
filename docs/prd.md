# AI印花电商小程序 Product Requirements Document (PRD)

## Goals and Background Context

### Goals

- 用户能够通过AI文生图快速生成个性化图片作品
- 用户能够将生成的作品保存到作品集并管理
- 用户能够预览印花在包包上的效果
- 用户能够将作品添加到购物车并完成购买
- 用户能够通过图生图功能基于现有图片生成新作品
- 用户能够对作品进行分类、标签和收藏管理
- 用户能够查看和管理订单状态
- 用户能够编辑个人信息和管理收货地址
- 用户能够分享作品到微信

### Background Context

AI印花电商小程序是一个创新的定制化电商平台，核心功能是让用户通过AI文生图和图生图技术生成个性化图片，并将这些图片作为印花应用到包包上，实现真正的个性化定制购物体验。

当前市场痛点包括：传统电商平台只能提供标准化商品，用户想要独特设计但缺乏设计能力，定制服务门槛高、价格昂贵、周期长。本产品通过AI技术降低设计门槛，提供直观的预览体验，实现从创意到商品的完整闭环。

### Change Log

| Date | Version | Description | Author |
|------|---------|-------------|--------|
| 2024-12-14 | 1.0 | 初始PRD创建，包含P0和P1功能 | PM |

## Requirements

### Functional

**P0功能（核心功能，必须实现）**

- FR1: 用户能够通过微信授权一键登录系统
- FR2: 用户能够输入文本提示词生成图片
- FR3: 用户能够设置图片生成参数（尺寸、风格）
- FR4: 用户能够查看图片生成进度
- FR5: 用户能够查看生成的图片结果
- FR6: 用户能够将生成的图片保存到作品集
- FR7: 用户能够查看作品集列表
- FR8: 用户能够查看作品详情
- FR9: 用户能够删除作品
- FR10: 用户能够选择包包款式进行预览
- FR11: 用户能够将作品应用到包包上预览效果
- FR12: 用户能够调整印花在包包上的位置
- FR13: 用户能够将作品添加到购物车
- FR14: 用户能够在购物车中选择包包规格（款式、颜色、尺寸）
- FR15: 用户能够设置购买数量
- FR16: 用户能够从购物车删除商品
- FR17: 用户能够创建订单
- FR18: 用户能够使用微信支付完成订单
- FR19: 用户能够查看订单状态

**P1功能（重要功能，尽快实现）**

- FR20: 用户能够上传图片进行图生图
- FR21: 用户能够设置图生图的相似度控制
- FR22: 用户能够对作品进行分类管理
- FR23: 用户能够为作品添加标签
- FR24: 用户能够收藏作品
- FR25: 用户能够查看订单列表
- FR26: 用户能够查看订单详情
- FR27: 用户能够跟踪订单状态变化
- FR28: 用户能够编辑个人信息
- FR29: 用户能够管理收货地址（添加、编辑、删除）
- FR30: 用户能够查看订单统计信息
- FR31: 用户能够分享作品到微信
- FR32: 用户能够生成作品分享海报
- FR33: 用户能够选择多种包包款式（3-5种）
- FR34: 用户能够选择不同颜色和尺寸的包包

### Non Functional

- NFR1: 图片生成平均响应时间应小于30秒
- NFR2: 页面加载时间应小于2秒
- NFR3: 系统可用性应达到99%以上
- NFR4: 支付成功率应大于95%
- NFR5: 支持并发用户数1000+
- NFR6: 图片文件大小限制为10MB以内
- NFR7: 支持JPG、PNG、WebP图片格式
- NFR8: 所有API接口应使用HTTPS通信
- NFR9: 用户数据应进行加密存储
- NFR10: 符合微信小程序平台规范
- NFR11: 前端代码包大小应控制在2MB以内
- NFR12: 数据库查询响应时间应小于100ms
- NFR13: OSS图片访问应通过CDN加速
- NFR14: 错误信息应对用户友好，不暴露技术细节

## User Interface Design Goals

### Overall UX Vision

提供简洁、直观、流畅的移动端体验。界面设计应突出AI生成的核心功能，让用户能够轻松完成从创意生成到商品购买的完整流程。整体风格应现代、时尚，符合年轻用户群体的审美。

### Key Interaction Paradigms

- **卡片式布局**：作品和商品以卡片形式展示，便于浏览和操作
- **底部导航**：主要功能通过底部Tab导航快速访问
- **下拉刷新**：列表页面支持下拉刷新获取最新内容
- **上拉加载**：长列表支持上拉加载更多
- **模态弹窗**：重要操作使用模态弹窗确认
- **进度反馈**：AI生成过程提供实时进度显示
- **手势操作**：支持滑动删除、长按等手势

### Core Screens and Views

1. **登录/注册页面**：微信授权登录入口
2. **首页**：AI生成入口、热门作品推荐
3. **AI生成页面**：文生图/图生图功能入口和参数设置
4. **生成中页面**：显示生成进度和预计时间
5. **生成结果页面**：展示生成的图片，支持保存到作品集
6. **作品集页面**：作品列表展示，支持分类和筛选
7. **作品详情页面**：查看作品详情，支持编辑和删除
8. **印花预览页面**：选择包包款式，预览印花效果
9. **购物车页面**：购物车商品列表，支持编辑和删除
10. **订单确认页面**：确认订单信息和收货地址
11. **支付页面**：微信支付入口
12. **订单列表页面**：查看所有订单
13. **订单详情页面**：查看订单详细信息和状态
14. **个人中心页面**：用户信息、设置、订单统计
15. **地址管理页面**：收货地址的增删改查

### Accessibility: None

当前版本暂不包含无障碍功能，后续版本考虑添加。

### Branding

- **主色调**：现代简约风格，以白色和浅灰色为主背景
- **强调色**：使用品牌色作为按钮和强调元素
- **字体**：使用系统默认字体，确保清晰易读
- **图标**：使用线性图标风格，保持一致性

### Target Device and Platforms: Mobile Only

目标平台为微信小程序，主要面向iOS和Android移动设备。

## Technical Assumptions

### Repository Structure: Monorepo

采用Monorepo结构，前端和后端代码在同一个仓库中，便于统一管理和版本控制。

### Service Architecture

采用单体架构（Monolith），使用Spring Boot构建RESTful API服务。前端使用uni-app框架开发微信小程序。后续可根据业务发展需要拆分为微服务架构。

### Testing Requirements

- **单元测试**：后端核心业务逻辑需要单元测试覆盖
- **集成测试**：关键业务流程需要集成测试
- **前端测试**：关键用户交互流程需要测试验证
- **API测试**：所有API接口需要测试覆盖

### Additional Technical Assumptions and Requests

- 使用H2数据库进行开发，生产环境可迁移到MySQL
- 使用阿里云OSS存储图片文件
- 使用阿里云百炼API进行AI图片生成
- 使用JWT进行用户认证
- 使用微信支付进行支付处理
- API接口遵循RESTful设计规范
- 使用Swagger生成API文档
- 前端使用Pinia进行状态管理
- 使用uView UI组件库

## Epic List

1. **Epic 1: 项目基础架构与用户认证**：建立项目基础架构，实现用户登录注册功能，为后续功能开发奠定基础。

2. **Epic 2: AI图片生成核心功能**：实现文生图功能，包括提示词输入、参数设置、生成进度显示和结果展示。

3. **Epic 3: 作品集管理**：实现作品的保存、查看、删除等基础管理功能。

4. **Epic 4: 印花预览功能**：实现包包款式选择、印花应用预览和位置调整功能。

5. **Epic 5: 购物车与订单**：实现购物车管理、订单创建和微信支付功能。

6. **Epic 6: 图生图功能（P1）**：实现基于图片的AI生成功能。

7. **Epic 7: 作品集增强功能（P1）**：实现作品分类、标签和收藏功能。

8. **Epic 8: 订单管理（P1）**：实现订单列表、详情查看和状态跟踪功能。

9. **Epic 9: 个人中心（P1）**：实现个人信息编辑、地址管理和订单统计功能。

10. **Epic 10: 分享功能（P1）**：实现作品分享到微信和分享海报生成功能。

## Epic 1: 项目基础架构与用户认证

本Epic建立项目的基础架构，包括前后端项目初始化、数据库设计、基础服务搭建，并实现用户登录注册功能。这是所有后续功能的基础，确保项目具备良好的架构和用户身份管理能力。

### Story 1.1: 后端项目初始化

As a developer,
I want to set up the Spring Boot project with H2 database,
so that I have a working backend foundation for the application.

**Acceptance Criteria:**
1. Spring Boot项目已创建，版本为3.0+
2. H2数据库已配置，支持内存模式和文件模式
3. 项目结构符合标准Spring Boot项目规范
4. 基础依赖已配置（Spring Web、Spring Data JPA、H2等）
5. 应用能够成功启动并连接到H2数据库
6. 提供健康检查接口 `/api/health` 返回200状态码

### Story 1.2: 前端项目初始化

As a developer,
I want to set up the uni-app project with uView UI,
so that I have a working frontend foundation for the WeChat mini-program.

**Acceptance Criteria:**
1. uni-app项目已创建，使用Vue3语法
2. uView UI组件库已集成
3. 项目结构符合uni-app规范
4. 基础页面路由已配置
5. 网络请求封装已完成（基于uni.request）
6. 项目能够在微信开发者工具中正常运行
7. 底部Tab导航已配置（首页、作品集、购物车、我的）

### Story 1.3: 用户实体和数据库设计

As a developer,
I want to create the User entity and database schema,
so that user information can be stored and managed.

**Acceptance Criteria:**
1. User实体类已创建，包含字段：id、openId、nickName、avatarUrl、phone、createTime、updateTime
2. UserRepository接口已创建
3. 数据库表自动创建功能已配置
4. 能够通过Repository进行用户的增删改查操作
5. 实体类包含必要的验证注解

### Story 1.4: 微信登录API实现

As a user,
I want to log in using WeChat authorization,
so that I can access the application without creating a separate account.

**Acceptance Criteria:**
1. 后端提供 `/api/auth/wechat/login` 接口
2. 接口接收微信code参数
3. 接口调用微信API获取openId和sessionKey
4. 如果用户不存在则自动创建用户记录
5. 返回JWT token给前端
6. Token包含用户基本信息
7. 接口错误处理完善，返回友好的错误信息

### Story 1.5: 前端微信登录实现

As a user,
I want to log in with one click using WeChat on the frontend,
so that I can quickly access the application.

**Acceptance Criteria:**
1. 登录页面已创建
2. 调用微信 `wx.login()` 获取code
3. 调用后端登录接口获取token
4. Token存储到本地（uni.setStorage）
5. 登录成功后跳转到首页
6. 登录状态持久化，下次打开自动登录
7. 登录失败时显示错误提示

### Story 1.6: JWT认证中间件

As a developer,
I want to implement JWT authentication middleware,
so that protected API endpoints can verify user identity.

**Acceptance Criteria:**
1. JWT工具类已创建，支持token生成和验证
2. 认证拦截器已实现，拦截需要认证的接口
3. Token验证失败时返回401状态码
4. 前端请求自动携带token（从localStorage读取）
5. Token过期时自动跳转到登录页
6. 支持token刷新机制（可选）

## Epic 2: AI图片生成核心功能

本Epic实现AI文生图的核心功能，包括提示词输入、参数设置、调用阿里百炼API生成图片、进度显示和结果展示。这是产品的核心价值所在，需要确保生成流程流畅、用户体验良好。

### Story 2.1: 阿里百炼API集成

As a developer,
I want to integrate Alibaba DashScope API for image generation,
so that the application can generate images using AI.

**Acceptance Criteria:**
1. DashScope SDK依赖已添加到项目
2. DashScope配置类已创建，支持API Key配置
3. 文生图API调用方法已实现
4. 能够成功调用API并获取生成的图片
5. API调用错误处理完善
6. 支持异步调用，不阻塞主线程

### Story 2.2: 图片生成任务实体和队列

As a developer,
I want to create image generation task entity and queue management,
so that generation requests can be tracked and managed.

**Acceptance Criteria:**
1. ImageGenerationTask实体已创建，包含字段：id、userId、prompt、parameters、status、resultUrl、errorMessage、createTime、updateTime
2. TaskRepository接口已创建
3. 任务状态枚举已定义（PENDING、PROCESSING、COMPLETED、FAILED）
4. 能够创建、查询、更新任务记录
5. 支持任务状态查询接口

### Story 2.3: 文生图API接口实现

As a user,
I want to submit a text prompt to generate an image,
so that I can create personalized artwork.

**Acceptance Criteria:**
1. 后端提供 `/api/image-generation/text-to-image` 接口
2. 接口接收prompt和parameters参数
3. 创建生成任务记录到数据库
4. 异步调用阿里百炼API
5. 生成完成后将图片上传到OSS
6. 更新任务状态和结果URL
7. 返回任务ID给前端
8. 接口包含参数验证和错误处理

### Story 2.4: 生成进度查询接口

As a user,
I want to check the progress of my image generation,
so that I know when my image will be ready.

**Acceptance Criteria:**
1. 后端提供 `/api/image-generation/tasks/{taskId}` 接口
2. 接口返回任务状态、进度信息
3. 如果任务完成，返回生成的图片URL
4. 如果任务失败，返回错误信息
5. 接口响应时间小于100ms
6. 支持任务不存在的情况处理

### Story 2.5: 前端文生图页面

As a user,
I want to input a text prompt and set parameters on the frontend,
so that I can generate images with my desired style.

**Acceptance Criteria:**
1. 文生图页面已创建
2. 提示词输入框已实现，支持多行输入
3. 参数设置面板已实现（尺寸、风格选择）
4. 尺寸选项：正方形、横版、竖版
5. 风格选项：写实、插画、抽象、水彩等
6. 生成按钮已实现，点击后调用后端API
7. 参数验证已实现，提示词不能为空
8. 页面UI美观，符合设计规范

### Story 2.6: 生成进度显示页面

As a user,
I want to see the progress of image generation,
so that I know how long I need to wait.

**Acceptance Criteria:**
1. 生成进度页面已创建
2. 显示生成进度条和预计剩余时间
3. 轮询后端接口获取任务状态
4. 任务完成时自动跳转到结果页面
5. 任务失败时显示错误信息，支持重试
6. 支持取消生成操作
7. 页面包含加载动画，提升用户体验

### Story 2.7: 生成结果展示页面

As a user,
I want to view the generated image result,
so that I can decide whether to save it to my collection.

**Acceptance Criteria:**
1. 生成结果页面已创建
2. 大图展示生成的图片，支持缩放查看
3. "保存到作品集"按钮已实现
4. 支持重新生成功能
5. 页面UI美观，符合设计规范

## Epic 3: 作品集管理

本Epic实现作品集的基础管理功能，包括作品的保存、查看、删除等操作。这是用户管理自己创作内容的核心功能，需要确保操作流畅、数据安全。

### Story 3.1: 作品实体和数据库设计

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

### Story 3.2: 保存作品API接口

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

### Story 3.3: 作品列表查询API

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

### Story 3.4: 作品详情查询API

As a user,
I want to view the details of a specific work,
so that I can see all information about it.

**Acceptance Criteria:**
1. 后端提供 `/api/works/{workId}` GET接口
2. 接口返回作品的完整信息
3. 验证作品属于当前用户
4. 支持作品不存在的情况处理
5. 接口响应时间小于100ms

### Story 3.5: 删除作品API接口

As a user,
I want to delete a work from my collection,
so that I can remove unwanted artworks.

**Acceptance Criteria:**
1. 后端提供 `/api/works/{workId}` DELETE接口
2. 验证作品属于当前用户
3. 删除作品记录（可选：同时删除OSS中的图片）
4. 返回删除成功信息
5. 支持作品不存在的情况处理

### Story 3.6: 前端作品集页面

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

### Story 3.7: 前端作品详情页面

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

## Epic 4: 印花预览功能

本Epic实现包包印花预览功能，包括包包款式选择、印花应用预览和位置调整。这是用户决策购买的关键环节，需要提供直观、流畅的预览体验。

### Story 4.1: 包包商品实体和数据库设计

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

### Story 4.2: 商品列表查询API

As a user,
I want to view available bag products,
so that I can choose which bag to apply my design to.

**Acceptance Criteria:**
1. 后端提供 `/api/products` GET接口
2. 接口返回所有可用的包包商品
3. 返回商品的基本信息（名称、类型、价格、图片等）
4. 接口响应时间小于100ms

### Story 4.3: 商品详情查询API

As a user,
I want to view product details,
so that I can see all available options (colors, sizes).

**Acceptance Criteria:**
1. 后端提供 `/api/products/{productId}` GET接口
2. 接口返回商品的完整信息
3. 包含所有可用的颜色和尺寸选项
4. 接口响应时间小于100ms

### Story 4.4: 前端印花预览页面

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

### Story 4.5: Canvas图片合成功能

As a developer,
I want to implement image composition using Canvas,
so that users can see the print effect on the bag.

**Acceptance Criteria:**
1. 使用uni.canvasToTempFilePath进行图片合成
2. 将作品图片叠加到包包图片上
3. 支持调整印花位置和大小
4. 生成预览图片
5. 性能优化，合成时间小于1秒

## Epic 5: 购物车与订单

本Epic实现购物车管理和订单创建、支付功能。这是电商的核心功能，需要确保流程顺畅、支付安全可靠。

### Story 5.1: 购物车实体和数据库设计

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

### Story 5.2: 添加到购物车API

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

### Story 5.3: 购物车列表查询API

As a user,
I want to view my shopping cart,
so that I can review items before checkout.

**Acceptance Criteria:**
1. 后端提供 `/api/cart/items` GET接口
2. 返回当前用户的所有购物车项
3. 包含作品信息、商品信息、规格、数量等
4. 计算总价
5. 接口响应时间小于100ms

### Story 5.4: 更新购物车项API

As a user,
I want to update cart item quantity or specifications,
so that I can modify my order before checkout.

**Acceptance Criteria:**
1. 后端提供 `/api/cart/items/{itemId}` PUT接口
2. 支持更新数量、颜色、尺寸等
3. 验证购物车项属于当前用户
4. 返回更新后的购物车项信息

### Story 5.5: 删除购物车项API

As a user,
I want to remove items from my cart,
so that I can clean up unwanted items.

**Acceptance Criteria:**
1. 后端提供 `/api/cart/items/{itemId}` DELETE接口
2. 验证购物车项属于当前用户
3. 删除购物车项记录
4. 返回删除成功信息

### Story 5.6: 订单实体和数据库设计

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

### Story 5.7: 创建订单API

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

### Story 5.8: 微信支付集成

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

### Story 5.9: 创建支付订单API

As a user,
I want to create a payment order,
so that I can pay for my purchase.

**Acceptance Criteria:**
1. 后端提供 `/api/orders/{orderId}/payment` POST接口
2. 调用微信支付统一下单API
3. 返回支付参数给前端
4. 包含appId、timeStamp、nonceStr、package、signType、paySign等参数
5. 接口包含错误处理

### Story 5.10: 支付回调处理

As a developer,
I want to handle WeChat payment callbacks,
so that order status can be updated automatically after payment.

**Acceptance Criteria:**
1. 支付回调接口 `/api/payment/notify` 已实现
2. 验证回调签名
3. 更新订单支付状态
4. 处理重复回调（幂等性）
5. 返回成功响应给微信

### Story 5.11: 订单状态查询API

As a user,
I want to check my order status,
so that I can track my purchase.

**Acceptance Criteria:**
1. 后端提供 `/api/orders/{orderId}` GET接口
2. 返回订单的完整信息和当前状态
3. 验证订单属于当前用户
4. 接口响应时间小于100ms

### Story 5.12: 前端购物车页面

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

### Story 5.13: 前端订单确认页面

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

### Story 5.14: 前端支付页面

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

## Epic 6: 图生图功能（P1）

本Epic实现基于图片的AI生成功能，允许用户上传图片并基于此生成新作品。这是P1功能，扩展了AI生成的能力。

### Story 6.1: 图片上传到OSS

As a developer,
I want to implement image upload to OSS,
so that user-uploaded images can be stored and accessed.

**Acceptance Criteria:**
1. OSS SDK已集成
2. 图片上传方法已实现
3. 支持生成OSS签名URL供前端直传
4. 上传前进行图片压缩（可选）
5. 上传成功后返回图片URL
6. 错误处理完善

### Story 6.2: 图生图API接口实现

As a user,
I want to generate an image based on an uploaded image,
so that I can create variations or style transfers.

**Acceptance Criteria:**
1. 后端提供 `/api/image-generation/image-to-image` 接口
2. 接口接收imageUrl、prompt、similarity参数
3. 创建生成任务记录
4. 异步调用阿里百炼图生图API
5. 生成完成后将图片上传到OSS
6. 更新任务状态和结果URL
7. 返回任务ID给前端
8. 接口包含参数验证和错误处理

### Story 6.3: 前端图生图页面

As a user,
I want to upload an image and generate a new one on the frontend,
so that I can create image variations easily.

**Acceptance Criteria:**
1. 图生图页面已创建
2. 图片上传功能已实现（选择相册或拍照）
3. 图片预览已实现
4. 提示词输入框已实现
5. 相似度控制滑块已实现（0-100%）
6. 生成按钮已实现
7. 参数验证已实现
8. 页面UI美观，符合设计规范

## Epic 7: 作品集增强功能（P1）

本Epic实现作品集的增强功能，包括分类、标签和收藏功能，提升作品管理的便利性。

### Story 7.1: 作品分类功能

As a user,
I want to organize my works into categories,
so that I can find them more easily.

**Acceptance Criteria:**
1. 作品实体添加category字段
2. 后端提供 `/api/works/{workId}/category` PUT接口更新分类
3. 作品列表API支持按分类筛选
4. 前端作品集页面支持分类选择
5. 支持自定义分类名称

### Story 7.2: 作品标签功能

As a user,
I want to add tags to my works,
so that I can organize and search them better.

**Acceptance Criteria:**
1. 作品实体支持多标签（tags字段为数组）
2. 后端提供标签管理接口
3. 作品列表API支持按标签筛选
4. 前端支持添加、编辑、删除标签
5. 标签自动补全功能（可选）

### Story 7.3: 作品收藏功能

As a user,
I want to mark works as favorites,
so that I can quickly access my preferred works.

**Acceptance Criteria:**
1. 作品实体添加isFavorite字段
2. 后端提供 `/api/works/{workId}/favorite` PUT接口切换收藏状态
3. 作品列表API支持筛选收藏作品
4. 前端支持收藏/取消收藏操作
5. 收藏状态视觉反馈明显

## Epic 8: 订单管理（P1）

本Epic实现完整的订单管理功能，包括订单列表、详情查看和状态跟踪。

### Story 8.1: 订单列表查询API

As a user,
I want to view all my orders,
so that I can track my purchase history.

**Acceptance Criteria:**
1. 后端提供 `/api/orders` GET接口
2. 支持分页查询
3. 支持按状态筛选
4. 返回订单列表，包含基本信息
5. 接口响应时间小于100ms

### Story 8.2: 订单详情API增强

As a user,
I want to view complete order details,
so that I can see all information about my order.

**Acceptance Criteria:**
1. 订单详情API返回完整信息
2. 包含订单项、收货地址、支付信息、物流信息等
3. 支持订单状态历史记录
4. 接口响应时间小于100ms

### Story 8.3: 前端订单列表页面

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

### Story 8.4: 前端订单详情页面

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

## Epic 9: 个人中心（P1）

本Epic实现个人中心功能，包括个人信息编辑、地址管理和订单统计。

### Story 9.1: 用户信息更新API

As a user,
I want to update my profile information,
so that my account reflects my current details.

**Acceptance Criteria:**
1. 后端提供 `/api/user/profile` PUT接口
2. 支持更新昵称、头像、手机号等
3. 参数验证完善
4. 返回更新后的用户信息

### Story 9.2: 收货地址实体和API

As a developer,
I want to create the Address entity and APIs,
so that users can manage shipping addresses.

**Acceptance Criteria:**
1. Address实体类已创建
2. 地址CRUD API已实现
3. 支持设置默认地址
4. 地址验证完善

### Story 9.3: 订单统计API

As a user,
I want to see my order statistics,
so that I can understand my purchase activity.

**Acceptance Criteria:**
1. 后端提供 `/api/user/statistics` GET接口
2. 返回订单总数、待支付数、待收货数等
3. 返回作品总数、收藏数等
4. 接口响应时间小于100ms

### Story 9.4: 前端个人中心页面

As a user,
I want to access my profile and settings on the frontend,
so that I can manage my account easily.

**Acceptance Criteria:**
1. 个人中心页面已创建
2. 显示用户头像、昵称等基本信息
3. 显示订单统计信息
4. 提供入口到个人信息编辑、地址管理、订单列表等
5. 页面UI美观，符合设计规范

### Story 9.5: 前端地址管理页面

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

## Epic 10: 分享功能（P1）

本Epic实现作品分享功能，包括分享到微信和生成分享海报。

### Story 10.1: 分享海报生成

As a developer,
I want to generate share posters for works,
so that users can share their creations attractively.

**Acceptance Criteria:**
1. 海报生成方法已实现
2. 使用Canvas合成作品图片和二维码
3. 包含作品信息和分享链接
4. 生成时间小于2秒
5. 支持保存到相册

### Story 10.2: 前端分享功能

As a user,
I want to share my works to WeChat,
so that I can show my creations to friends.

**Acceptance Criteria:**
1. 作品详情页添加分享按钮
2. 调用微信分享API
3. 支持分享到好友和朋友圈
4. 分享内容包含作品图片和描述
5. 支持生成分享海报

## Checklist Results Report

（待PO验证后填写）

## Next Steps

### UX Expert Prompt

请基于本PRD文档创建前端UI/UX规范文档（front-end-spec.md）。重点关注：
- 核心页面的详细设计规范
- 用户交互流程设计
- 组件库使用规范
- 响应式设计考虑

### Architect Prompt

请基于本PRD文档创建全栈架构设计文档（fullstack-architecture.md）。重点关注：
- 系统整体架构设计
- 数据库设计（ER图）
- API接口设计规范
- 技术选型和集成方案
- 安全性和性能考虑