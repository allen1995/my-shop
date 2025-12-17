package com.myshop.service.impl;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.myshop.dto.ImageEditRequest;
import com.myshop.dto.ImageEditResponse;
import com.myshop.service.ImageEditService;
import com.myshop.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 图像编辑服务实现
 * 使用通义万相图像编辑2.5 API
 */
@Service
@Slf4j
public class ImageEditServiceImpl implements ImageEditService {
    
    @Autowired
    private OssService ossService;
    
    @Value("${app.aliyun.dashscope.api-key}")
    private String apiKey;
    
    private static final String MODEL = "wan2.5-i2i-preview";
    
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;
    
    @Async("imageEditExecutor")
    @Override
    public CompletableFuture<ImageEditResponse> compositeImage(ImageEditRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("开始图像编辑任务 - 图一: {}, 图二: {}", request.getWorkImageUrl(), request.getBagImageUrl());
        
        ImageEditResponse response = new ImageEditResponse();
        
        try {
            // 参数验证
            validateRequest(request);
            
            // 构建提示词（如果没有提供）
            String prompt = request.getPrompt();
            if (prompt == null || prompt.trim().isEmpty()) {
                prompt = generateDefaultPrompt();
            }
            
            // 调用通义万相API（带重试）
            String resultUrl = callImageEditApiWithRetry(request, prompt);
            
            // 下载并上传到OSS
            String ossUrl = ossService.uploadFromUrl(resultUrl, "previews");
            
            // 计算耗时
            long duration = System.currentTimeMillis() - startTime;
            
            // 构建响应
            response.setStatus("SUCCEEDED");
            response.setResultUrl(ossUrl);
            response.setDuration(duration);
            
            log.info("图像编辑完成，耗时: {}ms, URL: {}", duration, ossUrl);
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("图像编辑失败，耗时: {}ms", duration, e);
            
            response.setStatus("FAILED");
            response.setErrorMessage(e.getMessage());
            response.setDuration(duration);
        }
        
        return CompletableFuture.completedFuture(response);
    }
    
    /**
     * 验证请求参数
     */
    private void validateRequest(ImageEditRequest request) {
        if (request.getWorkImageUrl() == null || request.getWorkImageUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("印花图片URL不能为空");
        }
        if (request.getBagImageUrl() == null || request.getBagImageUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("包包底图URL不能为空");
        }
    }
    
    /**
     * 生成默认提示词
     */
    private String generateDefaultPrompt() {
        return "将图一作为印花图案自然地放置在图二包包的中心区域，" +
               "保持包包原有的材质、光影和立体效果，" +
               "印花大小为包包可视区域的30%左右，" +
               "印花位置居中偏上，" +
               "整体效果真实自然";
    }
    
    /**
     * 调用图像编辑API（带重试机制）
     */
    private String callImageEditApiWithRetry(ImageEditRequest request, String prompt) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                log.info("调用通义万相API - 尝试 {}/{}", attempt, MAX_RETRIES);
                return callImageEditApi(request, prompt);
            } catch (Exception e) {
                lastException = e;
                log.warn("API调用失败 - 尝试 {}/{}: {}", attempt, MAX_RETRIES, e.getMessage());
                
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempt); // 递增延迟
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("重试被中断", ie);
                    }
                }
            }
        }
        
        throw new RuntimeException("API调用失败，已重试" + MAX_RETRIES + "次: " + lastException.getMessage(), lastException);
    }
    
    /**
     * 调用通义万相图像编辑API（万相2.5图像编辑API）
     * 
     * 官方文档: https://help.aliyun.com/zh/model-studio/wan2-5-image-edit-api-reference
     * 
     * 模型: wan2.5-i2i-preview
     * 功能: 将图一（印花）合成到图二（包包底图）上
     * 
     * 注意：通义万相API需要公开可访问的图片URL
     */
    private String callImageEditApi(ImageEditRequest request, String prompt) throws Exception {
        ImageSynthesis gen = new ImageSynthesis();
        
        // 确保 prompt 不为空
        if (prompt == null || prompt.trim().isEmpty()) {
            prompt = generateDefaultPrompt();
            log.info("使用默认提示词: {}", prompt);
        }
        
        log.info("准备调用通义万相图像编辑API:");
        log.info("  - 模型: {}", MODEL);
        log.info("  - 印花图片(图一): {}", request.getWorkImageUrl());
        log.info("  - 包包底图(图二): {}", request.getBagImageUrl());
        log.info("  - 提示词: {}", prompt);
        log.info("  - 输出尺寸: {}", request.getSize());
        
        // 处理图片URL - 确保API可以访问
        String workImageUrl = ensurePublicAccessible(request.getWorkImageUrl(), "work");
        String bagImageUrl = ensurePublicAccessible(request.getBagImageUrl(), "bag");
        
        log.info("处理后的图片URL:");
        log.info("  - 印花图片: {}", workImageUrl);
        log.info("  - 包包图片: {}", bagImageUrl);
        
        // 准备两张图片
        List<String> images = new ArrayList<>();
        images.add(workImageUrl);   // 图一：印花
        images.add(bagImageUrl);    // 图二：底图
        
        // 构建请求参数（使用官方SDK方式）
        ImageSynthesisParam param = ImageSynthesisParam.builder()
                .apiKey(apiKey)
                .model(MODEL)                    // wan2.5-i2i-preview
                .prompt(prompt)                  // 提示词
                .images(images)                  // 图片列表
                .n(1)                           // 生成数量
                .size(request.getSize() != null ? request.getSize() : "1024*1024")  // 输出尺寸
                .build();
        
        log.info("调用图像编辑API - SDK版本: 2.22.2");
        
        // 调用API
        ImageSynthesisResult result = gen.call(param);
        
        // 提取结果URL
        return extractResultUrl(result);
    }
    
    /**
     * 确保图片URL可被公开访问
     * 
     * 问题：通义万相API无法访问带签名的私有OSS URL
     * 解决：将图片重新上传到OSS，生成公开可访问的URL（不带签名）
     * 
     * @param imageUrl 原始图片URL
     * @param imageType 图片类型（用于日志）
     * @return 公开可访问的图片URL
     */
    private String ensurePublicAccessible(String imageUrl, String imageType) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new IllegalArgumentException(imageType + "图片URL不能为空");
        }
        
        try {
            log.info("检查{}图片URL可访问性: {}", imageType, imageUrl);
            
            // 如果URL包含签名参数（如Expires, Signature等），说明是私有访问
            // 通义万相API无法访问这类URL，需要重新上传
            if (isPrivateOssUrl(imageUrl)) {
                log.warn("检测到私有OSS URL（包含签名参数），通义万相API无法访问");
                log.info("正在重新上传图片以生成公开访问URL...");
                
                // 下载图片并重新上传到OSS（生成公开可访问的URL，不带签名）
                String publicUrl = ossService.uploadFromUrlPublic(imageUrl, "public-previews");
                log.info("✅ 图片已重新上传，公开URL: {}", publicUrl);
                
                return publicUrl;
            }
            
            // 如果是普通URL，直接返回
            log.info("{}图片URL无需处理，直接使用", imageType);
            return imageUrl;
            
        } catch (Exception e) {
            log.error("❌ 处理{}图片URL失败: {}", imageType, imageUrl, e);
            // 失败时抛出异常，让调用方知道
            throw new RuntimeException("处理图片URL失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 判断是否为私有OSS URL（包含签名参数）
     */
    private boolean isPrivateOssUrl(String url) {
        if (url == null) return false;
        
        // 检查是否包含OSS签名参数
        return url.contains("Expires=") || 
               url.contains("OSSAccessKeyId=") || 
               url.contains("Signature=") ||
               url.contains("x-oss-");
    }
    
    /**
     * 从API结果中提取图片URL
     * 
     * API返回格式: 
     * results=[{orig_prompt=..., actual_prompt=..., url=https://...}]
     */
    private String extractResultUrl(ImageSynthesisResult result) {
        if (result == null || result.getOutput() == null) {
            throw new RuntimeException("API返回结果为空");
        }
        
        log.info("开始提取结果URL，结果对象: {}", result);
        
        try {
            Object output = result.getOutput();
            
            // 获取 results 字段
            java.lang.reflect.Method getResultsMethod = output.getClass().getMethod("getResults");
            Object resultsObj = getResultsMethod.invoke(output);
            
            log.info("Results 对象类型: {}", resultsObj != null ? resultsObj.getClass().getName() : "null");
            
            if (resultsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> results = (List<Object>) resultsObj;
                
                if (results.isEmpty()) {
                    throw new RuntimeException("API返回的results为空");
                }
                
                Object firstResult = results.get(0);
                log.info("第一个结果对象类型: {}", firstResult.getClass().getName());
                log.info("第一个结果内容: {}", firstResult);
                
                // 方式1: 尝试作为Map处理（万相API返回的是Map）
                if (firstResult instanceof java.util.Map) {
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> resultMap = (java.util.Map<String, Object>) firstResult;
                    
                    Object urlObj = resultMap.get("url");
                    if (urlObj != null) {
                        String url = urlObj.toString();
                        log.info("✅ 从Map中提取到URL: {}", url);
                        return url;
                    }
                }
                
                // 方式2: 尝试调用 getUrl() 方法
                try {
                    java.lang.reflect.Method getUrlMethod = firstResult.getClass().getMethod("getUrl");
                    Object urlObj = getUrlMethod.invoke(firstResult);
                    
                    if (urlObj != null) {
                        String url = urlObj.toString();
                        log.info("✅ 通过getUrl()方法提取到URL: {}", url);
                        return url;
                    }
                } catch (NoSuchMethodException e) {
                    log.debug("对象没有getUrl()方法，尝试其他方式");
                }
                
                // 方式3: 通过反射遍历所有字段查找url
                try {
                    java.lang.reflect.Field[] fields = firstResult.getClass().getDeclaredFields();
                    for (java.lang.reflect.Field field : fields) {
                        field.setAccessible(true);
                        if (field.getName().equalsIgnoreCase("url")) {
                            Object urlObj = field.get(firstResult);
                            if (urlObj != null) {
                                String url = urlObj.toString();
                                log.info("✅ 通过反射字段提取到URL: {}", url);
                                return url;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.debug("反射字段提取失败: {}", e.getMessage());
                }
            }
            
        } catch (Exception e) {
            log.error("❌ 提取URL失败", e);
        }
        
        throw new RuntimeException("无法从API结果中提取图片URL，请检查API返回格式");
    }
}

