package com.myshop.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class OssService {
    
    @Value("${app.aliyun.oss.endpoint}")
    private String endpoint;
    
    @Value("${app.aliyun.oss.access-key-id}")
    private String accessKeyId;
    
    @Value("${app.aliyun.oss.access-key-secret}")
    private String accessKeySecret;
    
    @Value("${app.aliyun.oss.bucket-name}")
    private String bucketName;
    
    private OSS getOssClient() {
        // 确保endpoint使用HTTPS协议
        String httpsEndpoint = endpoint;
        if (endpoint.startsWith("http://")) {
            httpsEndpoint = endpoint.replace("http://", "https://");
            log.warn("OSS endpoint配置为HTTP，已自动转换为HTTPS: {} -> {}", endpoint, httpsEndpoint);
        } else if (!endpoint.startsWith("https://") && !endpoint.startsWith("http://")) {
            // 如果endpoint不包含协议，默认使用HTTPS
            httpsEndpoint = "https://" + endpoint;
        }
        
        return new OSSClientBuilder().build(httpsEndpoint, accessKeyId, accessKeySecret);
    }
    
    public String uploadFile(InputStream inputStream, String fileName, String folder) {
        OSS ossClient = getOssClient();
        try {
            String objectName = folder + "/" + UUID.randomUUID().toString() + "_" + fileName;
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            ossClient.putObject(putObjectRequest);
            
            // 生成带签名的URL（有效期7天）
            Date expiration = new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L);
            URL signedUrl = ossClient.generatePresignedUrl(bucketName, objectName, expiration);
            
            // 确保URL使用HTTPS协议
            String urlString = signedUrl.toString();
            if (urlString.startsWith("http://")) {
                urlString = urlString.replace("http://", "https://");
                log.info("将HTTP URL转换为HTTPS: {}", urlString);
            }
            
            return urlString;
        } catch (Exception e) {
            log.error("OSS上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        } finally {
            ossClient.shutdown();
        }
    }
    
    /**
     * 为已存在的文件生成带签名的URL
     * @param objectName OSS对象名称
     * @param expirationHours 过期时间（小时），默认7天
     * @return 带签名的URL
     */
    public String generateSignedUrl(String objectName, int expirationHours) {
        OSS ossClient = getOssClient();
        try {
            Date expiration = new Date(System.currentTimeMillis() + expirationHours * 60 * 60 * 1000L);
            URL signedUrl = ossClient.generatePresignedUrl(bucketName, objectName, expiration);
            
            // 确保URL使用HTTPS协议
            String urlString = signedUrl.toString();
            if (urlString.startsWith("http://")) {
                urlString = urlString.replace("http://", "https://");
                log.info("将HTTP URL转换为HTTPS: {}", urlString);
            }
            
            return urlString;
        } catch (Exception e) {
            log.error("生成签名URL失败", e);
            throw new RuntimeException("生成签名URL失败: " + e.getMessage());
        } finally {
            ossClient.shutdown();
        }
    }
    
    /**
     * 从URL中提取objectName
     * @param fileUrl OSS文件URL（可能是签名URL或普通URL）
     * @return objectName
     */
    private String extractObjectNameFromUrl(String fileUrl) {
        try {
            // 如果是签名URL，先去掉查询参数
            String urlWithoutQuery = fileUrl;
            if (fileUrl.contains("?")) {
                urlWithoutQuery = fileUrl.substring(0, fileUrl.indexOf("?"));
            }
            
            // 提取objectName（去掉域名部分）
            String prefix = "https://" + bucketName + ".";
            if (endpoint.startsWith("https://")) {
                prefix = "https://" + bucketName + "." + endpoint.replace("https://", "");
            } else if (endpoint.startsWith("http://")) {
                prefix = "http://" + bucketName + "." + endpoint.replace("http://", "");
            } else {
                prefix = "https://" + bucketName + "." + endpoint;
            }
            
            if (urlWithoutQuery.startsWith(prefix)) {
                return urlWithoutQuery.substring(prefix.length());
            }
            
            // 如果格式不匹配，尝试从最后一个/之后提取
            int lastSlash = urlWithoutQuery.lastIndexOf("/");
            if (lastSlash >= 0) {
                return urlWithoutQuery.substring(lastSlash + 1);
            }
            
            return urlWithoutQuery;
        } catch (Exception e) {
            log.warn("从URL提取objectName失败: {}", fileUrl, e);
            // 如果提取失败，返回原URL的最后一部分
            return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        }
    }
    
    /**
     * 从URL下载图片并上传到OSS
     * @param imageUrl 图片URL
     * @param folder 存储文件夹
     * @return OSS文件URL（带签名）
     */
    public String uploadFromUrl(String imageUrl, String folder) {
        OSS ossClient = getOssClient();
        try {
            log.info("从URL下载图片: {}", imageUrl);
            
            // 从URL下载图片
            URL url = URI.create(imageUrl).toURL();
            InputStream inputStream = url.openConnection().getInputStream();
            
            // 生成文件名
            String fileName = UUID.randomUUID().toString() + ".jpg";
            String objectName = folder + "/" + fileName;
            
            // 上传到OSS
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            ossClient.putObject(putObjectRequest);
            
            // 生成带签名的URL（有效期7天）
            Date expiration = new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L);
            URL signedUrl = ossClient.generatePresignedUrl(bucketName, objectName, expiration);
            
            // 确保URL使用HTTPS协议
            String urlString = signedUrl.toString();
            if (urlString.startsWith("http://")) {
                urlString = urlString.replace("http://", "https://");
                log.info("将HTTP URL转换为HTTPS: {}", urlString);
            }
            
            log.info("图片上传成功: {}", urlString);
            return urlString;
        } catch (Exception e) {
            log.error("从URL上传图片失败: {}", imageUrl, e);
            throw new RuntimeException("从URL上传图片失败: " + e.getMessage(), e);
        } finally {
            ossClient.shutdown();
        }
    }
    
    /**
     * 从URL下载图片并上传到OSS（生成公开访问URL）
     * 
     * 注意：此方法会将图片上传到公开读权限的目录
     * 用于需要被第三方API访问的场景（如通义万相API）
     * 
     * @param imageUrl 图片URL
     * @param folder 存储文件夹
     * @return 公开可访问的OSS URL（不带签名）
     */
    public String uploadFromUrlPublic(String imageUrl, String folder) {
        OSS ossClient = getOssClient();
        try {
            log.info("从URL下载图片并生成公开访问URL: {}", imageUrl);
            
            // 从URL下载图片
            URL url = URI.create(imageUrl).toURL();
            InputStream inputStream = url.openConnection().getInputStream();
            
            // 生成文件名
            String fileName = UUID.randomUUID().toString() + ".jpg";
            String objectName = folder + "/" + fileName;
            
            // 上传到OSS
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            ossClient.putObject(putObjectRequest);
            
            // 上传后设置对象ACL为公共读
            try {
                ossClient.setObjectAcl(bucketName, objectName, 
                    com.aliyun.oss.model.CannedAccessControlList.PublicRead);
                log.info("已设置对象为公共读权限");
            } catch (Exception e) {
                log.warn("设置公共读权限失败，可能需要在OSS控制台手动配置: {}", e.getMessage());
            }
            
            // 生成公开URL（不带签名）
            String publicUrl = buildPublicUrl(objectName);
            
            log.info("图片上传成功（公开访问）: {}", publicUrl);
            return publicUrl;
            
        } catch (Exception e) {
            log.error("从URL上传图片失败（公开模式）: {}", imageUrl, e);
            throw new RuntimeException("从URL上传图片失败: " + e.getMessage(), e);
        } finally {
            ossClient.shutdown();
        }
    }
    
    /**
     * 构建公开访问的OSS URL
     */
    private String buildPublicUrl(String objectName) {
        // 确保endpoint使用HTTPS
        String httpsEndpoint = endpoint;
        if (endpoint.startsWith("http://")) {
            httpsEndpoint = endpoint.replace("http://", "https://");
        } else if (!endpoint.startsWith("https://")) {
            httpsEndpoint = "https://" + endpoint;
        }
        
        // 构建公开URL: https://{bucket}.{endpoint}/{objectName}
        return String.format("%s/%s/%s", 
            httpsEndpoint.replace("https://", "https://" + bucketName + "."),
            bucketName,
            objectName
        ).replace("/" + bucketName + "/", "/");
    }
    
    public String uploadImage(MultipartFile file, String folder) {
        try {
            return uploadFile(file.getInputStream(), file.getOriginalFilename(), folder);
        } catch (Exception e) {
            log.error("图片上传失败", e);
            throw new RuntimeException("图片上传失败: " + e.getMessage());
        }
    }
    
    public void deleteFile(String fileUrl) {
        OSS ossClient = getOssClient();
        try {
            // 从URL中提取objectName
            String objectName = extractObjectNameFromUrl(fileUrl);
            ossClient.deleteObject(bucketName, objectName);
        } catch (Exception e) {
            log.error("OSS删除失败", e);
        } finally {
            ossClient.shutdown();
        }
    }
}


