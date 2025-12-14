package com.myshop.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
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
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
    
    public String uploadFile(InputStream inputStream, String fileName, String folder) {
        OSS ossClient = getOssClient();
        try {
            String objectName = folder + "/" + UUID.randomUUID().toString() + "_" + fileName;
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            ossClient.putObject(putObjectRequest);
            
            // 返回文件URL（需要配置CDN域名）
            return "https://" + bucketName + "." + endpoint.replace("https://", "").replace("http://", "") + "/" + objectName;
        } catch (Exception e) {
            log.error("OSS上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        } finally {
            ossClient.shutdown();
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
            String objectName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            if (fileUrl.contains("/images/")) {
                objectName = "images/" + objectName;
            }
            ossClient.deleteObject(bucketName, objectName);
        } catch (Exception e) {
            log.error("OSS删除失败", e);
        } finally {
            ossClient.shutdown();
        }
    }
}


