# Epic 2: AI图片生成核心功能

本Epic实现AI文生图的核心功能，包括提示词输入、参数设置、调用阿里百炼API生成图片、进度显示和结果展示。这是产品的核心价值所在，需要确保生成流程流畅、用户体验良好。

## Story 2.1: 阿里百炼API集成

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

## Story 2.2: 图片生成任务实体和队列

As a developer,
I want to create image generation task entity and queue management,
so that generation requests can be tracked and managed.

**Acceptance Criteria:**
1. ImageGenerationTask实体已创建，包含字段：id、userId、prompt、parameters、status、resultUrl、errorMessage、createTime、updateTime
2. TaskRepository接口已创建
3. 任务状态枚举已定义（PENDING、PROCESSING、COMPLETED、FAILED）
4. 能够创建、查询、更新任务记录
5. 支持任务状态查询接口

## Story 2.3: 文生图API接口实现

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

## Story 2.4: 生成进度查询接口

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

## Story 2.5: 前端文生图页面

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

## Story 2.6: 生成进度显示页面

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

## Story 2.7: 生成结果展示页面

As a user,
I want to view the generated image result,
so that I can decide whether to save it to my collection.

**Acceptance Criteria:**
1. 生成结果页面已创建
2. 大图展示生成的图片，支持缩放查看
3. "保存到作品集"按钮已实现
4. 支持重新生成功能
5. 页面UI美观，符合设计规范


