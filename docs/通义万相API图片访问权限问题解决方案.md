# 通义万相API 图片访问权限问题解决方案

## 🔍 问题描述

**错误信息**:
```
Don't have authorization to access the media resource during the data inspection process.
Code: InvalidParameter.DataInspection
StatusCode: 400
```

**原因分析**:
通义万相API在尝试访问图片时遇到权限问题，无法读取带签名的私有OSS URL。

---

## 🎯 根本原因

### OSS 签名 URL 的问题

当前系统使用的OSS URL是**带签名的私有URL**:
```
https://bucket.oss-cn-beijing.aliyuncs.com/path/image.jpg?
  Expires=1734441981&
  OSSAccessKeyId=TMP.xxx&
  Signature=xxx
```

**问题**:
1. ❌ 通义万相API无法访问带签名的私有URL
2. ❌ 签名URL虽然在浏览器中可访问，但第三方API无法访问
3. ❌ API需要**公开可访问**的图片URL

---

## ✅ 解决方案

### 方案一：自动转换为公开URL（已实施）

**实现逻辑**:

1. **检测私有URL**: 检查URL是否包含签名参数
   ```java
   private boolean isPrivateOssUrl(String url) {
       return url.contains("Expires=") || 
              url.contains("OSSAccessKeyId=") || 
              url.contains("Signature=");
   }
   ```

2. **重新上传**: 将图片下载并重新上传到公开可访问的目录
   ```java
   // 检测到私有URL，重新上传
   if (isPrivateOssUrl(imageUrl)) {
       String publicUrl = ossService.uploadFromUrlPublic(
           imageUrl, 
           "public-previews"  // 公开访问目录
       );
       return publicUrl;
   }
   ```

3. **生成公开URL**: 上传后设置为公共读权限
   ```java
   // 上传到OSS
   ossClient.putObject(putObjectRequest);
   
   // 设置公共读权限
   ossClient.setObjectAcl(
       bucketName, 
       objectName, 
       CannedAccessControlList.PublicRead
   );
   
   // 生成不带签名的公开URL
   String publicUrl = String.format(
       "https://%s.%s/%s",
       bucketName, endpoint, objectName
   );
   ```

---

### 方案二：配置OSS Bucket为公共读（可选）

**OSS控制台配置**:

1. 登录 [阿里云OSS控制台](https://oss.console.aliyun.com/)
2. 选择对应的 Bucket
3. 设置 **访问权限** → **公共读**
4. 配置 **跨域规则**（CORS）:
   ```xml
   <CORSConfiguration>
       <CORSRule>
           <AllowedOrigin>*</AllowedOrigin>
           <AllowedMethod>GET</AllowedMethod>
           <AllowedHeader>*</AllowedHeader>
       </CORSRule>
   </CORSConfiguration>
   ```

**注意**: 
- ⚠️ 此方案会使Bucket中所有文件公开可访问
- ⚠️ 建议创建单独的公开Bucket存放预览图片

---

## 🔄 工作流程

### 当前实现流程

```
用户请求预览
    ↓
获取作品图片URL（可能是私有URL）
    ↓
检测是否为私有URL
    ↓
┌───────┴───────┐
是              否
↓               ↓
重新上传        直接使用
↓               │
生成公开URL     │
↓               │
└───────┬───────┘
        ↓
调用通义万相API（使用公开URL）
        ↓
生成预览图片
        ↓
上传结果到OSS
```

---

## 📝 代码实现

### 新增方法

#### 1. ImageEditServiceImpl.ensurePublicAccessible()
```java
/**
 * 确保图片URL可被公开访问
 */
private String ensurePublicAccessible(String imageUrl, String imageType) {
    if (isPrivateOssUrl(imageUrl)) {
        log.warn("检测到私有OSS URL，重新上传以获取公开访问URL");
        String publicUrl = ossService.uploadFromUrlPublic(
            imageUrl, 
            "public-previews"
        );
        log.info("✅ 图片已重新上传，公开URL: {}", publicUrl);
        return publicUrl;
    }
    return imageUrl;
}
```

#### 2. ImageEditServiceImpl.isPrivateOssUrl()
```java
/**
 * 判断是否为私有OSS URL
 */
private boolean isPrivateOssUrl(String url) {
    return url.contains("Expires=") || 
           url.contains("OSSAccessKeyId=") || 
           url.contains("Signature=");
}
```

#### 3. OssService.uploadFromUrlPublic()
```java
/**
 * 从URL下载图片并上传到OSS（生成公开访问URL）
 */
public String uploadFromUrlPublic(String imageUrl, String folder) {
    // 下载图片
    InputStream inputStream = downloadImage(imageUrl);
    
    // 上传到OSS
    ossClient.putObject(bucketName, objectName, inputStream);
    
    // 设置公共读权限
    ossClient.setObjectAcl(
        bucketName, 
        objectName, 
        CannedAccessControlList.PublicRead
    );
    
    // 返回公开URL（不带签名）
    return buildPublicUrl(objectName);
}
```

---

## 🧪 测试验证

### 测试步骤

1. **启动后端**:
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

2. **触发预览生成**:
   ```bash
   curl -X POST http://localhost:8080/api/preview/generate \
     -H "Content-Type: application/json" \
     -d '{
       "workId": 100,
       "productId": 200,
       "color": "黑色",
       "size": "中"
     }'
   ```

3. **查看日志** - 成功的标志:
   ```
   检查work图片URL可访问性: https://...?Expires=...
   检测到私有OSS URL（包含签名参数），通义万相API无法访问
   正在重新上传图片以生成公开访问URL...
   从URL下载图片并生成公开访问URL: https://...
   已设置对象为公共读权限
   ✅ 图片已重新上传，公开URL: https://bucket.oss.aliyuncs.com/public-previews/xxx.jpg
   调用图像编辑API - SDK版本: 2.22.2
   图像编辑完成，耗时: 8523ms ✅
   ```

4. **验证公开URL**:
   ```bash
   # 测试公开URL是否可访问（不应该有403错误）
   curl -I https://bucket.oss.aliyuncs.com/public-previews/xxx.jpg
   
   # 预期: 200 OK
   ```

---

## ⚠️ 注意事项

### 安全性

1. **公开访问**: 
   - ⚠️ 上传到 `public-previews` 目录的文件是公开可访问的
   - ✅ 这是必需的，因为通义万相API需要访问这些图片
   - ✅ 预览图片本身不包含敏感信息

2. **临时文件**: 
   - ✅ 预览生成后，原始公开文件可以定期清理
   - ✅ 最终预览结果会上传到 `previews` 目录（可以是私有的）

3. **权限控制**:
   - ✅ 仅在调用通义万相API时使用公开URL
   - ✅ 其他场景仍使用带签名的私有URL

### 性能

1. **重复上传**: 
   - ⚠️ 每次预览都会重新上传图片
   - 🔄 可以考虑缓存公开URL（使用workId作为key）

2. **带宽消耗**:
   - 下载原图 + 上传公开图 = 2倍带宽
   - 建议: 缓存已处理的公开URL

---

## 🚀 优化建议

### 短期优化

1. **缓存公开URL**:
   ```java
   // 使用 Redis 或内存缓存
   String cachedPublicUrl = cache.get("public:" + workId);
   if (cachedPublicUrl != null) {
       return cachedPublicUrl;
   }
   ```

2. **异步清理**:
   ```java
   // 定时清理超过7天的公开预览图片
   @Scheduled(cron = "0 0 3 * * ?")
   public void cleanupExpiredPublicFiles() {
       // 删除 public-previews 目录下的过期文件
   }
   ```

### 长期优化

1. **专用公开Bucket**: 
   - 创建单独的公开Bucket存放预览图片
   - 便于权限管理和成本控制

2. **CDN加速**: 
   - 为公开Bucket配置CDN
   - 提升图片访问速度

---

## 📚 相关文档

- **通义万相API文档**: https://help.aliyun.com/zh/model-studio/wan2-5-image-edit-api-reference
- **OSS权限控制**: https://help.aliyun.com/zh/oss/user-guide/overview-23
- **测试指南**: `docs/万相2.5图像编辑API测试指南.md`

---

## ✅ 检查清单

- [ ] 代码已更新（ImageEditServiceImpl + OssService）
- [ ] 重新编译后端 (`mvn clean install`)
- [ ] 启动后端服务
- [ ] 测试预览生成
- [ ] 检查日志输出
- [ ] 验证公开URL可访问
- [ ] 确认通义万相API调用成功
- [ ] 预览图片正常显示

