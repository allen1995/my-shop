# AI印花电商小程序 🎨

<div align="center">

![Logo](./frontend/static/logo.png)

**用AI创造独一无二的个性化包包**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)](https://spring.io/projects/spring-boot)
[![uni-app](https://img.shields.io/badge/uni--app-Vue3-42b983)](https://uniapp.dcloud.io/)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

</div>

## 📖 项目简介

AI印花电商小程序是一个创新的定制化电商平台，通过 **AI图片生成技术** 让用户轻松创作个性化印花图案，并将其应用到包包等商品上，实现真正的个性化定制购物体验。

### ✨ 核心功能

- 🤖 **AI文生图**：输入文本描述，AI自动生成独特的印花图案
- 🖼️ **AI图生图**：上传图片进行风格转换和变化
- 👜 **实时预览**：使用通义万相API生成印花在包包上的真实效果
- 🎨 **作品管理**：保存、分类、标签化管理您的创作
- 🛒 **电商功能**：购物车、订单管理、微信支付一应俱全
- 📦 **订单追踪**：实时查看订单状态和物流信息

---

## 🎬 主要流程演示

### 1. AI文生图生成印花

```
输入提示词 → AI生成图片 → 保存到作品集
```

<details>
<summary>查看详细流程</summary>

1. 进入首页，点击"文生图"
2. 输入创作提示词，例如：`水彩风格的樱花，温柔浪漫`
3. 选择图片尺寸和风格
4. 点击"生成"，等待10-30秒
5. 查看生成结果，满意后保存到作品集

</details>

### 2. 预览印花效果

```
选择作品 → 选择包包款式 → AI生成预览图 → 查看效果
```

<details>
<summary>查看详细流程</summary>

1. 从作品集选择喜欢的印花
2. 点击"应用到包包"
3. 选择包包款式、颜色、尺寸
4. 点击"生成预览"
5. 通义万相API自动合成预览图
6. 查看印花在包包上的真实效果

</details>

### 3. 下单购买

```
加入购物车 → 确认订单 → 微信支付 → 完成购买
```

<details>
<summary>查看详细流程</summary>

1. 预览满意后，点击"加入购物车"
2. 选择数量，进入购物车
3. 点击"去结算"
4. 选择收货地址
5. 确认订单信息
6. 使用微信支付完成付款

</details>

---

## 🏗️ 项目架构

### 技术栈

#### 后端
- **框架**：Spring Boot 3.2.0
- **数据库**：H2 Database (开发) / MySQL (生产)
- **ORM**：Spring Data JPA + Hibernate
- **认证**：JWT Token
- **AI服务**：阿里云百炼 (DashScope SDK 2.22.2)
- **图片存储**：阿里云OSS
- **支付**：微信支付 (WeChat Pay)
- **API文档**：Swagger / SpringDoc

#### 前端
- **框架**：uni-app (Vue 3 Composition API)
- **平台**：微信小程序
- **UI组件**：uView UI
- **状态管理**：Pinia
- **网络请求**：uni.request 封装

### 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        微信小程序 (uni-app)                    │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │  首页    │  │ 作品集   │  │ 购物车   │  │  我的    │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↕ HTTPS / RESTful API
┌─────────────────────────────────────────────────────────────┐
│                    Spring Boot 后端服务                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 认证模块 │  │ 作品管理 │  │ 订单管理 │  │ 支付模块 │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  H2/MySQL    │  │  阿里云OSS   │  │ 阿里云百炼   │
│  数据库      │  │  图片存储    │  │  AI生成      │
└──────────────┘  └──────────────┘  └──────────────┘
```

### 目录结构

```
my-shop-cursor/
├── backend/                    # 后端服务
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/myshop/
│   │   │   │   ├── config/         # 配置类
│   │   │   │   ├── controller/     # REST控制器
│   │   │   │   ├── dto/            # 数据传输对象
│   │   │   │   ├── entity/         # JPA实体
│   │   │   │   ├── enums/          # 枚举类
│   │   │   │   ├── repository/     # 数据访问层
│   │   │   │   ├── service/        # 业务逻辑层
│   │   │   │   └── util/           # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml # 配置文件
│   │   │       └── db/migration/   # 数据库迁移脚本
│   │   └── test/                   # 测试代码
│   └── pom.xml                     # Maven配置
│
├── frontend/                   # 前端小程序
│   ├── api/                    # API封装
│   ├── pages/                  # 页面组件
│   │   ├── index/              # 首页
│   │   ├── generate/           # AI生成页面
│   │   ├── works/              # 作品集
│   │   ├── preview/            # 预览页面
│   │   ├── cart/               # 购物车
│   │   ├── order/              # 订单管理
│   │   ├── profile/            # 个人中心
│   │   └── address/            # 地址管理
│   ├── static/                 # 静态资源
│   ├── store/                  # 状态管理
│   ├── utils/                  # 工具函数
│   ├── App.vue                 # 应用入口
│   ├── main.js                 # 主文件
│   ├── manifest.json           # 应用配置
│   ├── pages.json              # 页面配置
│   └── package.json            # 依赖配置
│
├── docs/                       # 项目文档
│   ├── prd/                    # 产品需求文档
│   ├── stories/                # 用户故事
│   └── *.md                    # 其他文档
│
└── README.md                   # 项目说明
```

---

## ⚙️ 配置说明

在部署项目前，您需要修改以下配置：

### 1. 后端配置 (`backend/src/main/resources/application.yml`)

```yaml
app:
  # JWT配置
  jwt:
    secret: ${JWT_SECRET:your-jwt-secret-key-at-least-32-bytes}  # 修改为您的密钥
    expiration: 604800000  # Token有效期 (7天)
  
  # 微信小程序配置
  wechat:
    app-id: ${WECHAT_APP_ID:wx1234567890abcdef}         # 修改为您的小程序AppID
    app-secret: ${WECHAT_APP_SECRET:your-app-secret}    # 修改为您的小程序AppSecret
  
  # 阿里云OSS配置
  aliyun:
    oss:
      endpoint: ${ALIYUN_OSS_ENDPOINT:oss-cn-beijing.aliyuncs.com}       # OSS端点
      access-key-id: ${ALIYUN_OSS_ACCESS_KEY_ID:your-access-key}         # AccessKey ID
      access-key-secret: ${ALIYUN_OSS_ACCESS_KEY_SECRET:your-secret}     # AccessKey Secret
      bucket-name: ${ALIYUN_OSS_BUCKET_NAME:your-bucket}                 # Bucket名称
    
    # 阿里云百炼 (DashScope) 配置
    dashscope:
      api-key: ${ALIYUN_DASHSCOPE_API_KEY:sk-xxxxxxxxxxxxxxxxxx}         # DashScope API Key
  
  # 微信支付配置
  wechat-pay:
    app-id: ${WECHAT_PAY_APP_ID:wx1234567890abcdef}     # 微信支付AppID
    mch-id: ${WECHAT_PAY_MCH_ID:1234567890}             # 商户号
    api-v3-key: ${WECHAT_PAY_API_V3_KEY:your-v3-key}    # APIv3密钥
    cert-path: ${WECHAT_PAY_CERT_PATH:/path/to/cert}    # 商户证书路径
```

### 2. 前端配置 (`frontend/utils/request.js`)

修改API基础地址：

```javascript
const baseUrl = 'http://your-domain.com/api'  // 修改为您的后端地址
```

### 3. 数据库配置

#### 开发环境 (H2)
默认配置已包含，无需修改。

#### 生产环境 (MySQL)
修改 `application.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/myshop?useSSL=false&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: your_username
    password: your_password
  
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
```

### 4. 环境变量配置 (推荐)

创建 `.env` 文件或设置系统环境变量：

```bash
# JWT
export JWT_SECRET="your-jwt-secret-key-at-least-32-bytes"

# 微信
export WECHAT_APP_ID="wx1234567890abcdef"
export WECHAT_APP_SECRET="your-app-secret"

# 阿里云OSS
export ALIYUN_OSS_ENDPOINT="oss-cn-beijing.aliyuncs.com"
export ALIYUN_OSS_ACCESS_KEY_ID="your-access-key"
export ALIYUN_OSS_ACCESS_KEY_SECRET="your-secret"
export ALIYUN_OSS_BUCKET_NAME="your-bucket"

# 阿里云百炼
export ALIYUN_DASHSCOPE_API_KEY="sk-xxxxxxxxxxxxxxxxxx"

# 微信支付
export WECHAT_PAY_APP_ID="wx1234567890abcdef"
export WECHAT_PAY_MCH_ID="1234567890"
export WECHAT_PAY_API_V3_KEY="your-v3-key"
export WECHAT_PAY_CERT_PATH="/path/to/cert"
```

---

## 🚀 部署流程

### 前置要求

- **Java**: JDK 17 或更高版本
- **Maven**: 3.6+ 
- **Node.js**: 14.0+
- **微信开发者工具**: 最新稳定版
- **阿里云账号**: 用于OSS和百炼服务
- **微信小程序账号**: 用于小程序发布

### 后端部署

#### 1. 克隆项目

```bash
git clone https://github.com/your-repo/my-shop-cursor.git
cd my-shop-cursor
```

#### 2. 配置文件

修改 `backend/src/main/resources/application.yml` 中的配置（参考上面的配置说明）。

#### 3. 数据库初始化

##### 使用H2（开发环境）
```bash
# 无需额外操作，首次启动自动创建数据库
```

##### 使用MySQL（生产环境）
```bash
# 1. 创建数据库
mysql -u root -p
CREATE DATABASE myshop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. 执行迁移脚本
mysql -u root -p myshop < backend/src/main/resources/db/migration/V3__add_image_urls_to_orders.sql
```

#### 4. 编译打包

```bash
cd backend
mvn clean install
```

#### 5. 启动应用

##### 开发环境
```bash
mvn spring-boot:run
```

##### 生产环境
```bash
java -jar target/ai-print-shop-backend-1.0.0.jar
```

#### 6. 验证启动

访问以下地址验证：
- **健康检查**: http://localhost:8080/api/health
- **H2控制台**: http://localhost:8080/api/h2-console
- **Swagger文档**: http://localhost:8080/api/swagger-ui.html

### 前端部署

#### 1. 安装依赖

```bash
cd frontend
npm install
```

#### 2. 修改配置

编辑 `frontend/utils/request.js`：

```javascript
const baseUrl = 'https://your-domain.com/api'  // 修改为后端地址
```

编辑 `frontend/manifest.json`：

```json
{
  "mp-weixin": {
    "appid": "wx1234567890abcdef",  // 修改为您的小程序AppID
    ...
  }
}
```

#### 3. 开发调试

```bash
npm run dev:mp-weixin
```

打开**微信开发者工具**，导入项目目录：`frontend/unpackage/dist/dev/mp-weixin`

#### 4. 生产构建

```bash
npm run build:mp-weixin
```

构建产物位于：`frontend/unpackage/dist/build/mp-weixin`

#### 5. 发布小程序

1. 在微信开发者工具中打开构建产物目录
2. 点击右上角"上传"按钮
3. 填写版本号和项目备注
4. 上传成功后，在微信公众平台提交审核
5. 审核通过后，点击"发布"正式上线

### Docker部署（可选）

#### 1. 后端Dockerfile

创建 `backend/Dockerfile`：

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/ai-print-shop-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 2. 构建镜像

```bash
cd backend
docker build -t ai-print-shop-backend:1.0.0 .
```

#### 3. 运行容器

```bash
docker run -d \
  -p 8080:8080 \
  -e JWT_SECRET="your-secret" \
  -e WECHAT_APP_ID="wx1234567890" \
  -e ALIYUN_DASHSCOPE_API_KEY="sk-xxxxx" \
  --name myshop-backend \
  ai-print-shop-backend:1.0.0
```

### Nginx配置（生产环境）

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 重定向到HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl;
    server_name your-domain.com;
    
    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
    
    # 后端API代理
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

---

## 📚 API文档

启动后端服务后，访问 Swagger 文档：

```
http://localhost:8080/api/swagger-ui.html
```

### 主要API端点

| 模块 | 端点 | 说明 |
|------|------|------|
| 认证 | POST `/auth/wechat/login` | 微信登录 |
| 作品 | GET `/works` | 获取作品列表 |
| 作品 | POST `/works` | 保存作品 |
| 作品 | DELETE `/works/{id}` | 删除作品 |
| 预览 | POST `/preview/generate` | 生成预览 |
| 预览 | GET `/preview/tasks/{id}` | 查询预览任务状态 |
| 购物车 | GET `/cart/items` | 获取购物车 |
| 购物车 | POST `/cart/items` | 添加到购物车 |
| 订单 | POST `/orders` | 创建订单 |
| 订单 | GET `/orders/{id}` | 获取订单详情 |
| 支付 | POST `/orders/{id}/payment` | 创建支付订单 |

---

## 🧪 测试

### 后端测试

```bash
cd backend
mvn test
```

### 前端测试

在微信开发者工具中测试各个页面功能。

### API测试

使用 Postman 或 curl 测试 API：

```bash
# 示例：测试健康检查
curl http://localhost:8080/api/health

# 示例：测试预览生成
curl -X POST http://localhost:8080/api/preview/generate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-jwt-token" \
  -d '{
    "workId": 100,
    "productId": 200,
    "color": "黑色",
    "size": "中"
  }'
```

---

## 🐛 常见问题

<details>
<summary><b>Q1: 后端启动失败，提示"找不到主类"</b></summary>

**解决方案**:
1. 确保 Java 版本为 17 或更高：`java -version`
2. 重新编译：`mvn clean install`
3. 检查 `pom.xml` 中的 `mainClass` 配置
</details>

<details>
<summary><b>Q2: AI生成失败，提示"API Key无效"</b></summary>

**解决方案**:
1. 检查 `application.yml` 中的 `dashscope.api-key` 配置
2. 确认 API Key 是否正确
3. 登录阿里云百炼平台检查 API Key 状态
4. 确认账户余额充足
</details>

<details>
<summary><b>Q3: 图片上传失败，提示"OSS权限错误"</b></summary>

**解决方案**:
1. 检查 OSS 配置是否正确
2. 确认 AccessKey 有写入权限
3. 检查 Bucket 是否存在
4. 确认 Bucket 权限设置为公共读
</details>

<details>
<summary><b>Q4: 小程序预览时提示"不在以下 request 合法域名列表中"</b></summary>

**解决方案**:
1. 登录微信公众平台
2. 进入"开发" > "开发设置" > "服务器域名"
3. 添加后端域名到 request 合法域名
4. 等待10分钟生效
</details>

<details>
<summary><b>Q5: 预览图生成失败，提示"Don't have authorization"</b></summary>

**解决方案**:
1. 确认图片URL是公开可访问的
2. 如果使用私有OSS URL，系统会自动重新上传到公开目录
3. 检查日志确认图片URL转换是否成功
4. 参考文档：`docs/通义万相API图片访问权限问题解决方案.md`
</details>

---

## 📖 文档索引

- [产品需求文档](./docs/prd.md)
- [通义万相API配置说明](./docs/通义万相API配置说明.md)
- [订单表图片字段说明](./docs/订单表添加图片字段.md)
- [Bug修复记录](./docs/Bug修复记录-主页刷新和预览重试.md)
- [Epic 11: AI图像编辑预览功能](./docs/prd/epic-11.md)
- [Story 11.1: DashScope图像编辑集成](./docs/stories/11.1.dashscope-image-edit-integration.md)

---

## 📋 TODO List

### 🚧 待开发功能

#### 高优先级 (P0)
- [ ] **管理后台**
  - [ ] 商品管理：添加、编辑、删除商品
  - [ ] 订单管理：订单列表、详情、状态更新
  - [ ] 用户管理：用户列表、封禁管理
  - [ ] 数据统计：销售报表、用户增长
- [ ] **物流对接**
  - [ ] 集成快递100 API
  - [ ] 物流信息实时推送
  - [ ] 物流轨迹查询
- [ ] **生产环境优化**
  - [ ] MySQL数据库迁移
  - [ ] Redis缓存集成
  - [ ] 日志系统优化（ELK）
  - [ ] 监控告警系统

#### 中优先级 (P1)
- [ ] **作品社区**
  - [ ] 作品广场：浏览热门作品
  - [ ] 点赞和评论功能
  - [ ] 关注用户功能
  - [ ] 作品榜单
- [ ] **营销功能**
  - [ ] 优惠券系统
  - [ ] 满减活动
  - [ ] 限时折扣
  - [ ] 新人礼包
- [ ] **AI功能增强**
  - [ ] 更多AI模型接入（Midjourney、DALL-E）
  - [ ] AI图片修复和增强
  - [ ] 智能抠图功能
  - [ ] 风格迁移优化
- [ ] **支付方式**
  - [ ] 支付宝支付
  - [ ] 余额支付
  - [ ] 积分抵扣

#### 低优先级 (P2)
- [ ] **多平台支持**
  - [ ] H5版本
  - [ ] APP版本（iOS/Android）
  - [ ] 桌面端（Electron）
- [ ] **国际化**
  - [ ] 多语言支持（英文、日文）
  - [ ] 多币种支持
  - [ ] 海外支付
- [ ] **高级功能**
  - [ ] 3D预览效果
  - [ ] AR试穿功能
  - [ ] 视频生成功能
  - [ ] AI对话助手

### 🐛 已知问题

- [ ] **性能优化**
  - [ ] 图片加载优化（懒加载、预加载）
  - [ ] 首屏加载时间优化
  - [ ] API响应时间优化
- [ ] **用户体验**
  - [ ] 长时间生成时的提示优化
  - [ ] 网络错误重试机制
  - [ ] 离线缓存功能
- [ ] **兼容性**
  - [ ] iOS低版本兼容性测试
  - [ ] Android多机型适配
  - [ ] 微信旧版本兼容

### 📝 文档完善

- [ ] API接口文档完善
- [ ] 数据库设计文档
- [ ] 部署运维文档
- [ ] 开发规范文档
- [ ] 测试用例文档
- [ ] 用户使用手册
- [ ] 常见问题FAQ

### 🧪 测试覆盖

- [ ] 单元测试覆盖率达到80%+
- [ ] 集成测试完善
- [ ] 端到端测试（E2E）
- [ ] 压力测试和性能测试
- [ ] 安全测试

### 🔐 安全加固

- [ ] API接口限流
- [ ] 敏感数据加密存储
- [ ] SQL注入防护
- [ ] XSS攻击防护
- [ ] CSRF防护
- [ ] 图片内容审核

---

## 🤝 贡献指南

欢迎贡献代码、报告问题或提出建议！

### 提交Issue

在提交 Issue 前，请确保：
1. 搜索现有 Issue，避免重复
2. 提供详细的问题描述和复现步骤
3. 附上相关的日志和截图

### 提交Pull Request

1. Fork 本项目
2. 创建特性分支：`git checkout -b feature/your-feature`
3. 提交更改：`git commit -m 'Add some feature'`
4. 推送分支：`git push origin feature/your-feature`
5. 提交 Pull Request

---

## 📄 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

---

## 📧 联系方式

- **Email**: your-email@example.com
- **Issues**: https://github.com/your-repo/my-shop-cursor/issues
- **Documentation**: https://your-docs-site.com

---

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot) - 后端框架
- [uni-app](https://uniapp.dcloud.io/) - 跨平台前端框架
- [阿里云百炼](https://dashscope.aliyun.com/) - AI图片生成服务
- [阿里云OSS](https://www.aliyun.com/product/oss) - 对象存储服务
- [微信开放平台](https://open.weixin.qq.com/) - 微信小程序平台

---

<div align="center">

**⭐ 如果这个项目对您有帮助，请给个 Star！⭐**

Made with ❤️ by Your Team

</div>
