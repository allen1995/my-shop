package com.myshop.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
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


