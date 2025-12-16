# Epic 11: Canvas 实时预览功能增强

本Epic实现前端Canvas实时预览功能，让用户能够直观地看到生成图片在包包上的实际效果，包括实时合成、位置调整、缩放等交互功能。这是提升用户体验的关键功能，直接影响用户购买决策。

## 背景

当前预览页面只能显示生成的作品图片，无法看到作品在包包上的实际效果。用户需要能够：
1. 实时看到作品叠加在包包上的效果
2. 调整作品在包包上的位置
3. 调整作品的大小和角度
4. 快速预览不同颜色/尺寸包包的效果

## Story 11.1: Canvas 2D 实时预览实现

As a user,
I want to see my work overlaid on the bag in real-time using Canvas,
so that I can visualize how the final product will look.

**Acceptance Criteria:**
1. 在预览页面中集成Canvas 2D组件
2. 实现包包底图和作品图片的实时合成显示
3. 支持拖拽调整作品位置
4. 支持双指缩放调整作品大小
5. 实时更新预览效果，延迟小于100ms
6. 支持不同颜色/尺寸包包底图的切换
7. 预览效果流畅，无明显卡顿

## Story 11.2: 印花位置和大小调整交互

As a user,
I want to adjust the position and size of my work on the bag,
so that I can customize the placement to my preference.

**Acceptance Criteria:**
1. 支持单指拖拽调整位置
2. 支持双指缩放调整大小（缩放范围：0.5x - 2.0x）
3. 支持旋转调整（可选，手势识别）
4. 位置和大小调整实时反馈
5. 提供重置按钮，恢复默认位置和大小
6. 显示位置和大小数值（可选，调试模式）

## Story 11.3: 预览图片生成和缓存

As a user,
I want the preview image to be generated and cached,
so that I can quickly see the final effect when adding to cart.

**Acceptance Criteria:**
1. 使用uni.canvasToTempFilePath生成预览图片
2. 预览图片生成时间小于1秒
3. 生成的预览图片用于添加到购物车
4. 支持预览图片缓存，避免重复生成
5. 预览图片质量满足展示需求（分辨率不低于750x750）

## Story 11.4: 包包底图资源管理

As a developer,
I want to manage bag base images for different products, colors, and sizes,
so that users can preview their work on various bag options.

**Acceptance Criteria:**
1. 每个商品的不同颜色/尺寸都有对应的底图
2. 底图格式：PNG（透明区域用于放置印花）
3. 定义印花放置区域（坐标、尺寸、可调整范围）
4. 底图资源存储在OSS，通过商品API返回
5. 支持底图懒加载，提升页面加载速度

## Story 11.5: 预览性能优化

As a developer,
I want to optimize the preview performance,
so that users can enjoy smooth interaction experience.

**Acceptance Criteria:**
1. Canvas绘制使用requestAnimationFrame优化
2. 图片加载使用缓存机制
3. 位置调整使用防抖处理（避免频繁重绘）
4. 预览生成使用Web Worker（如支持）
5. 内存管理：及时释放不用的图片资源
6. 在低端设备上也能流畅运行（FPS > 30）

## 技术实现要点

### Canvas 2D API
- 使用uni.createCanvasContext创建Canvas上下文
- 使用drawImage绘制图片
- 使用save/restore管理绘制状态
- 使用transform实现位置、缩放、旋转

### 交互实现
- 使用touchstart/touchmove/touchend实现拖拽
- 使用双指距离计算实现缩放
- 使用角度计算实现旋转（可选）

### 性能优化
- 图片预加载和缓存
- Canvas绘制优化（避免全量重绘）
- 防抖和节流处理
- 内存管理

## 依赖关系

- 依赖Epic 4（印花预览功能基础）
- 依赖Story 4.2（商品列表查询API）
- 依赖Story 4.3（商品详情查询API）

## 优先级

**P0 - 核心功能，必须实现**

直接影响用户体验，是用户决策购买的关键环节。

