# 通义万相图像编辑 API 配置说明

## 问题说明

之前遇到的错误：`prompt is marked non-null but is null`

**原因：** DashScope SDK 在调用图像编辑 API 时，需要正确设置参数。

## 解决方案

已在 `ImageEditServiceImpl` 中实现两种方案：

### 方案1：多图模式（推荐）

使用通义万相图像编辑 API (wanx-image-edit-v2.5)，传递两张图片：
- **图一**：印花图片（用户生成的作品）
- **图二**：包包底图（商品图片）
- **prompt**：描述如何将图一合成到图二上

```java
Map<String, Object> input = new HashMap<>();
List<String> images = new ArrayList<>();
images.add(workImageUrl);  // 图一：印花
images.add(bagImageUrl);   // 图二：底图
input.put("images", images);
input.put("text", prompt);
```

### 方案2：文生图模式（降级方案）

如果多图模式失败，自动降级到文生图模式（wanx-v1）：
- 将两张图片URL和提示词组合成一个文本提示
- 使用文生图API生成结果
- **注意**：此方案仅用于测试，效果可能不如多图模式

## 默认提示词

```
将图一作为印花图案自然地放置在图二包包的中心区域，
保持包包原有的材质、光影和立体效果，
印花大小为包包可视区域的30%左右，
印花位置居中偏上，
整体效果真实自然
```

## 配置步骤

### 1. 配置 API Key

编辑 `backend/src/main/resources/application.yml`：

```yaml
app:
  aliyun:
    dashscope:
      api-key: ${ALIYUN_DASHSCOPE_API_KEY:your-api-key}
```

**获取 API Key:**
1. 访问 [阿里云百炼平台](https://dashscope.console.aliyun.com/)
2. 登录并创建应用
3. 获取 API Key
4. 设置环境变量或直接配置到 yml 文件

### 2. 验证配置

启动应用后检查日志：

```
配置DashScope图片生成模型（主模型）
DashScope模型可用
=== Spring AI 配置验证 ===
主模型: dashscope
========================
```

### 3. 测试 API

**请求示例：**

```bash
POST /api/preview/generate
Content-Type: application/json

{
  "workId": 100,
  "productId": 200,
  "color": "黑色",
  "size": "中"
}
```

**响应示例：**

```json
{
  "code": 200,
  "data": {
    "taskId": 1,
    "status": "PENDING",
    "estimatedTime": 10
  }
}
```

**查询任务状态：**

```bash
GET /api/preview/tasks/1
```

**响应：**

```json
{
  "code": 200,
  "data": {
    "taskId": 1,
    "status": "COMPLETED",
    "progress": 100,
    "resultUrl": "https://oss.example.com/preview/xxx.jpg"
  }
}
```

## 常见问题

### Q1: API 调用失败，提示 "API Key 无效"

**解决方案：**
- 检查 API Key 是否正确
- 确认 API Key 是否已激活
- 检查账户余额是否充足

### Q2: 生成时间过长

**解决方案：**
- 通义万相 API 通常需要 5-10 秒
- 已实现异步调用，不会阻塞主线程
- 前端轮询每 2 秒查询一次任务状态

### Q3: 生成效果不理想

**解决方案：**
- 调整提示词（在 `ImageEditServiceImpl.generateDefaultPrompt()` 中）
- 确保两张图片的分辨率合适
- 检查图片 URL 是否可访问

### Q4: 多图模式失败，自动降级

**说明：**
- 系统会自动尝试降级方案
- 降级方案使用文生图模式
- 效果可能不如多图模式，但可以保证功能可用

## API 限制

- **并发限制**：根据账户等级
- **调用频率**：建议实现限流
- **图片大小**：建议不超过 10MB
- **输出尺寸**：1024x1024 或 720x1280

## 日志说明

**正常日志：**

```
调用通义万相API - model: wanx-image-edit-v2.5
使用默认提示词: 将图一作为印花图案...
调用图像编辑API（多图模式）
图像编辑完成，耗时: 8523ms
```

**降级日志：**

```
多图模式失败，尝试使用文生图模式
使用降级方案：文生图模式（仅用于测试）
图像编辑完成，耗时: 7234ms
```

**失败日志：**

```
图像编辑失败，耗时: 5432ms
API调用失败，已重试3次: [错误信息]
```

## 优化建议

1. **提示词优化**：根据实际效果调整提示词
2. **缓存策略**：相同参数的预览会自动使用缓存
3. **错误重试**：失败任务自动重试 3 次
4. **监控告警**：建议添加失败率监控

## 相关文档

- [通义万相API文档](https://help.aliyun.com/zh/dashscope/developer-reference/api-details-9)
- [DashScope SDK文档](https://help.aliyun.com/zh/dashscope/developer-reference/sdk-overview)
- [项目文档](./prd/epic-11.md)

