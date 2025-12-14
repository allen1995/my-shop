# Epic 1: 项目基础架构与用户认证

本Epic建立项目的基础架构，包括前后端项目初始化、数据库设计、基础服务搭建，并实现用户登录注册功能。这是所有后续功能的基础，确保项目具备良好的架构和用户身份管理能力。

## Story 1.1: 后端项目初始化

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

## Story 1.2: 前端项目初始化

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

## Story 1.3: 用户实体和数据库设计

As a developer,
I want to create the User entity and database schema,
so that user information can be stored and managed.

**Acceptance Criteria:**
1. User实体类已创建，包含字段：id、openId、nickName、avatarUrl、phone、createTime、updateTime
2. UserRepository接口已创建
3. 数据库表自动创建功能已配置
4. 能够通过Repository进行用户的增删改查操作
5. 实体类包含必要的验证注解

## Story 1.4: 微信登录API实现

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

## Story 1.5: 前端微信登录实现

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

## Story 1.6: JWT认证中间件

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

