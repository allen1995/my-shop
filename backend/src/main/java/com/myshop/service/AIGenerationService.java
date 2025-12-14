package com.myshop.service;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.myshop.entity.ImageGenerationTask;
import com.myshop.repository.ImageGenerationTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIGenerationService {
    
    @Value("${app.aliyun.dashscope.api-key}")
    private String apiKey;
    
    private final ImageGenerationTaskRepository taskRepository;
    private final OssService ossService;
    
    @Async
    @Transactional
    public void generateImageAsync(Long taskId) {
        ImageGenerationTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        
        try {
            task.setStatus(ImageGenerationTask.TaskStatus.PROCESSING);
            taskRepository.save(task);
            
            ImageSynthesisResult result;
            
            if (task.getTaskType() == ImageGenerationTask.TaskType.TEXT_TO_IMAGE) {
                result = generateTextToImage(task);
            } else {
                result = generateImageToImage(task);
            }
            
            // 下载生成的图片并上传到OSS
            String imageUrl = downloadAndUploadToOss(result);
            
            task.setStatus(ImageGenerationTask.TaskStatus.COMPLETED);
            task.setResultUrl(imageUrl);
            taskRepository.save(task);
            
        } catch (Exception e) {
            log.error("图片生成失败", e);
            task.setStatus(ImageGenerationTask.TaskStatus.FAILED);
            task.setErrorMessage(e.getMessage());
            taskRepository.save(task);
        }
    }
    
    private ImageSynthesisResult generateTextToImage(ImageGenerationTask task) 
            throws NoApiKeyException, ApiException, InputRequiredException {
        ImageSynthesis gen = new ImageSynthesis();
        ImageSynthesisParam param = ImageSynthesisParam.builder()
                .apiKey(apiKey)
                .model(ImageSynthesis.Models.WANX_V1)
                .prompt(task.getPrompt())
                .n(1)
                .size("1024*1024") // 默认尺寸，可以从parameters中解析
                .build();
        
        return gen.call(param);
    }
    
    private ImageSynthesisResult generateImageToImage(ImageGenerationTask task) 
            throws NoApiKeyException, ApiException, InputRequiredException {
        // 图生图实现 - 注意：dashscope的图生图可能需要使用不同的API或参数
        // 如果当前SDK不支持图生图，可以考虑使用文生图API配合参考图片
        ImageSynthesis gen = new ImageSynthesis();
        ImageSynthesisParam param = ImageSynthesisParam.builder()
                .apiKey(apiKey)
                .model(ImageSynthesis.Models.WANX_V1)
                .prompt(task.getPrompt())
                // 注意：imageUrl可能不是直接支持的参数，需要根据实际SDK文档调整
                // 如果SDK不支持，可能需要先上传图片到OSS，然后在prompt中引用
                .n(1)
                .size("1024*1024")
                .build();
        
        return gen.call(param);
    }
    
    private String downloadAndUploadToOss(ImageSynthesisResult result) throws Exception {
        // 从结果中获取图片URL
        // 根据实际API返回格式，可能需要调整获取方式
        Object output = result.getOutput();
        if (output == null) {
            throw new RuntimeException("生成结果为空");
        }
        
        // 尝试多种方式获取图片URL
        String imageUrl = null;
        try {
            // 方式1：如果output有getResults方法
            if (output instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<Object> results = (java.util.List<Object>) output;
                if (!results.isEmpty()) {
                    Object firstResult = results.get(0);
                    if (firstResult instanceof java.util.Map) {
                        @SuppressWarnings("unchecked")
                        java.util.Map<String, Object> resultMap = (java.util.Map<String, Object>) firstResult;
                        imageUrl = (String) resultMap.get("url");
                    }
                }
            }
            
            // 方式2：如果output是Map类型
            if (imageUrl == null && output instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> outputMap = (java.util.Map<String, Object>) output;
                Object results = outputMap.get("results");
                if (results instanceof java.util.List && !((java.util.List<?>) results).isEmpty()) {
                    Object firstResult = ((java.util.List<?>) results).get(0);
                    if (firstResult instanceof java.util.Map) {
                        @SuppressWarnings("unchecked")
                        java.util.Map<String, Object> resultMap = (java.util.Map<String, Object>) firstResult;
                        imageUrl = (String) resultMap.get("url");
                    }
                }
            }
        } catch (Exception e) {
            log.warn("无法从结果中提取URL，尝试其他方式", e);
        }
        
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new RuntimeException("无法从生成结果中获取图片URL");
        }
        
        // 下载图片
        URL url = new URL(imageUrl);
        try (InputStream inputStream = url.openStream()) {
            // 上传到OSS
            return ossService.uploadFile(inputStream, "generated.png", "images/works");
        }
    }
}


