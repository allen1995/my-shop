# Epic 6: 图生图功能（P1）

本Epic实现基于图片的AI生成功能，允许用户上传图片并基于此生成新作品。这是P1功能，扩展了AI生成的能力。

## Story 6.1: 图片上传到OSS

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

## Story 6.2: 图生图API接口实现

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

## Story 6.3: 前端图生图页面

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


