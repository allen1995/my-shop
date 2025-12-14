# AI印花电商小程序 - 后端服务

## 技术栈

- Spring Boot 3.2.0
- Java 17
- H2 Database (开发环境)
- Spring Data JPA
- JWT认证
- 阿里云OSS
- 阿里云百炼API
- 微信支付

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+

### 运行步骤

1. 配置环境变量（创建 `.env` 文件或设置系统环境变量）：
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

2. 运行应用：
   ```bash
   mvn spring-boot:run
   ```

3. 访问：
   - 应用：http://localhost:8080/api
   - 健康检查：http://localhost:8080/api/health
   - H2控制台：http://localhost:8080/api/h2-console
   - Swagger UI：http://localhost:8080/api/swagger-ui.html

## 项目结构

```
src/main/java/com/myshop/
├── controller/     # 控制器层
├── service/        # 业务逻辑层
├── repository/     # 数据访问层
├── entity/         # 实体类
├── dto/            # 数据传输对象
├── config/         # 配置类
└── util/           # 工具类
```

## API文档

启动应用后，访问 Swagger UI 查看完整的API文档。


