# Epic 11: AI 图像编辑预览功能

本Epic实现基于通义万相图像编辑2.5的预览功能，让用户能够直观地看到生成图片在包包上的实际效果。通过调用AI图像编辑API，将用户生成的印花图片智能合成到包包底图上，提供高质量的预览效果。这是提升用户体验的关键功能，直接影响用户购买决策。

## 背景

当前预览页面只能简单显示生成的作品图片，无法看到作品在包包上的实际效果。传统的Canvas前端合成方案存在以下问题：
- 前端合成效果不够真实，缺少光影、材质等细节
- 需要复杂的前端交互逻辑
- 兼容性和性能问题

**新方案：使用通义万相图像编辑2.5 API**
1. 图一：用户生成的印花图片
2. 图二：选中的包包商品底图
3. 通过AI图像编辑API，将图一智能合成到图二上
4. 生成高质量的预览图片，包含真实的光影和材质效果

## Story 11.1: 通义万相图像编辑 API 集成

As a developer,
I want to integrate Alibaba DashScope Image Edit API,
so that we can generate high-quality preview images with AI.

**Acceptance Criteria:**
1. 集成通义万相图像编辑2.5 API（wanx-image-edit-v2.5）
2. 实现图像编辑API调用方法
3. 支持将印花图片（图一）合成到包包底图（图二）上
4. 生成时间小于10秒
5. 支持异步调用，不阻塞主线程
6. API调用错误处理完善
7. 生成的预览图片质量满足展示需求

## Story 11.2: 预览图片生成任务管理

As a developer,
I want to manage image edit tasks for preview generation,
so that preview requests can be tracked and managed properly.

**Acceptance Criteria:**
1. 创建PreviewTask实体，记录预览生成任务
2. 任务状态包含：PENDING、PROCESSING、COMPLETED、FAILED
3. 支持任务进度查询
4. 实现任务队列管理（避免重复生成）
5. 任务完成后通知前端
6. 失败任务自动重试（最多3次）
7. 任务结果缓存机制

## Story 11.3: 前端预览生成接口实现

As a user,
I want to generate a preview image of my work on the bag,
so that I can see the final effect before adding to cart.

**Acceptance Criteria:**
1. 后端提供 `/api/preview/generate` 接口
2. 接口接收 workId、productId、color、size 参数
3. 调用通义万相图像编辑API生成预览图片
4. 提示词模板：将印花图片自然地合成到包包的中心位置
5. 预览图片上传到OSS并返回URL
6. 支持预览图片缓存（相同参数直接返回缓存）
7. 预览图片质量满足展示需求（分辨率1024x1024或更高）

## Story 11.4: 前端预览页面实现

As a user,
I want to view the preview image on the frontend,
so that I can decide whether to purchase the customized bag.

**Acceptance Criteria:**
1. 预览页面支持选择包包款式、颜色、尺寸
2. 点击"生成预览"按钮调用后端预览生成接口
3. 显示预览生成进度（加载动画和提示文字）
4. 预览生成成功后显示预览图片
5. 支持重新生成预览
6. 预览图片用于加入购物车
7. 预览生成失败时显示错误提示并支持重试

## Story 11.5: 预览图片缓存和优化

As a developer,
I want to implement preview image caching,
so that users can quickly access previously generated previews.

**Acceptance Criteria:**
1. 实现预览图片缓存机制（key: workId-productId-color-size）
2. 相同参数的预览请求直接返回缓存结果
3. 缓存有效期设置为7天
4. 缓存命中率统计和监控
5. 自动清理过期缓存
6. 支持手动清除缓存（重新生成）
7. 预览图片懒加载，提升页面性能

## 技术实现要点

### 通义万相图像编辑 API
- API名称：wanx-image-edit-v2.5
- 模型：wanx-style-cosplay-v1（风格编辑）或 wanx-image-edit-v2.5（通用编辑）
- 输入参数：
  - image1: 印花图片URL（用户生成的作品）
  - image2: 包包底图URL（选中的商品底图）
  - prompt: 提示词（"将图一作为印花自然地放置在图二包包的中心区域，保持包包的材质和光影效果"）
  - size: 输出尺寸（1024x1024或更高）

### 后端实现
- 创建PreviewTask实体记录任务
- 调用通义万相API异步生成
- 轮询或回调获取生成结果
- 结果图片上传到OSS
- 实现缓存机制避免重复生成

### 前端实现
- 简化预览页面，移除复杂的Canvas交互
- 调用后端预览生成API
- 轮询查询任务状态
- 显示预览图片
- 预览图片用于加入购物车

## 依赖关系

- 依赖Epic 4（印花预览功能基础）
- 依赖Story 4.2（商品列表查询API）
- 依赖Story 4.3（商品详情查询API）

## 优先级

**P0 - 核心功能，必须实现**

直接影响用户体验，是用户决策购买的关键环节。



