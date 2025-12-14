# AI印花电商小程序

一个基于AI图片生成的定制化电商小程序，用户可以通过AI文生图和图生图功能生成个性化图片，并将这些图片作为印花应用到包包上进行定制购买。

## 项目概述

本项目采用前后端分离架构：
- **前端**：uni-app (Vue3) + uView UI
- **后端**：Spring Boot 3.2.0 + H2 Database
- **AI服务**：阿里云百炼 (通义万相)
- **文件存储**：阿里云OSS
- **支付**：微信支付

## 项目结构

```
.
├── backend/              # Spring Boot后端项目
│   ├── src/
│   │   └── main/
│   │       ├── java/com/myshop/
│   │       │   ├── controller/    # 控制器层
│   │       │   ├── service/       # 业务逻辑层
│   │       │   ├── repository/   # 数据访问层
│   │       │   ├── entity/        # 实体类
│   │       │   ├── dto/           # 数据传输对象
│   │       │   ├── config/        # 配置类
│   │       │   └── util/          # 工具类
│   │       └── resources/
│   │           └── application.yml
│   └── pom.xml
├── frontend/             # uni-app前端项目（标准HBuilderX结构）
│   ├── pages/           # 页面（直接在根目录）
│   ├── api/             # API接口
│   ├── store/           # Pinia状态管理
│   ├── utils/           # 工具函数
│   ├── static/          # 静态资源
│   ├── App.vue          # 应用入口
│   ├── main.js          # 入口文件
│   ├── manifest.json    # 应用配置
│   ├── pages.json       # 页面配置
│   └── package.json     # 依赖配置
└── docs/                # 项目文档
    ├── project-brief.md
    ├── prd.md
    ├── front-end-spec.md
    ├── fullstack-architecture.md
    ├── development-plan.md
    └── po-validation-report.md
```

## 功能特性

### P0功能（核心功能）

1. **用户认证**
   - 微信一键登录
   - JWT token认证

2. **AI图片生成**
   - 文生图功能
   - 生成进度显示
   - 生成结果展示

3. **作品集管理**
   - 保存生成作品
   - 查看作品列表
   - 删除作品
   - 作品详情查看

4. **印花预览**
   - 选择包包款式
   - 应用印花预览
   - 位置调整

5. **购物车与订单**
   - 添加到购物车
   - 购物车管理
   - 订单创建
   - 微信支付

### P1功能（重要功能）

1. 图生图功能
2. 作品分类与标签
3. 订单管理
4. 个人中心
5. 分享功能

## 快速开始

### 后端启动

1. 进入后端目录：
   ```bash
   cd backend
   ```

2. 配置环境变量（创建 `.env` 文件或设置系统环境变量）：
   ```
   JWT_SECRET=your-secret-key
   WECHAT_APP_ID=your-app-id
   WECHAT_APP_SECRET=your-app-secret
   ALIYUN_OSS_ENDPOINT=your-endpoint
   ALIYUN_OSS_ACCESS_KEY_ID=your-access-key-id
   ALIYUN_OSS_ACCESS_KEY_SECRET=your-access-key-secret
   ALIYUN_OSS_BUCKET_NAME=your-bucket-name
   ALIYUN_DASHSCOPE_API_KEY=your-api-key
   ```

3. 运行应用：
   ```bash
   mvn spring-boot:run
   ```

4. 访问：
   - 应用：http://localhost:8080/api
   - 健康检查：http://localhost:8080/api/health
   - H2控制台：http://localhost:8080/api/h2-console
   - Swagger UI：http://localhost:8080/api/swagger-ui.html

### 前端启动

1. 进入前端目录：
   ```bash
   cd frontend
   ```

2. 安装依赖：
   ```bash
   npm install
   ```

3. 在微信开发者工具中打开项目

4. 配置后端API地址（修改 `utils/request.js` 中的 `BASE_URL`）

## 开发说明

### 后端开发

- 使用Spring Boot 3.2.0
- 数据库：H2（开发环境），可迁移到MySQL（生产环境）
- ORM：Spring Data JPA
- API文档：Swagger/OpenAPI

### 前端开发

- 使用uni-app框架
- Vue3 Composition API
- 状态管理：Pinia
- UI组件库：uView UI

## API接口

主要API接口：

- `POST /api/auth/wechat/login` - 微信登录
- `POST /api/image-generation/text-to-image` - 文生图
- `GET /api/image-generation/tasks/{taskId}` - 查询生成任务
- `POST /api/works` - 保存作品
- `GET /api/works` - 获取作品列表
- `GET /api/products` - 获取商品列表
- `POST /api/cart/items` - 添加到购物车
- `POST /api/orders` - 创建订单
- `POST /api/orders/{orderId}/payment` - 创建支付订单

详细API文档请访问 Swagger UI。

## 数据库设计

主要数据表：

- `users` - 用户表
- `image_generation_tasks` - 图片生成任务表
- `works` - 作品表
- `products` - 商品表
- `cart_items` - 购物车表
- `orders` - 订单表
- `order_items` - 订单项表

## 部署

### 后端部署

1. 打包应用：
   ```bash
   mvn clean package
   ```

2. 运行jar包：
   ```bash
   java -jar target/ai-print-shop-backend-1.0.0.jar
   ```

### 前端部署

1. 构建小程序：
   ```bash
   npm run build:mp-weixin
   ```

2. 在微信开发者工具中上传代码

## 注意事项

1. 开发环境使用H2数据库，数据存储在 `backend/data/` 目录
2. 生产环境建议迁移到MySQL
3. 需要配置阿里云OSS和百炼API密钥
4. 需要配置微信小程序AppID和AppSecret
5. 需要配置微信支付相关参数

## 文档

详细文档请查看 `docs/` 目录：

- `project-brief.md` - 项目简介
- `prd.md` - 产品需求文档
- `front-end-spec.md` - 前端规范
- `fullstack-architecture.md` - 全栈架构设计
- `development-plan.md` - 开发计划
- `po-validation-report.md` - PO验证报告

## 许可证

MIT License


