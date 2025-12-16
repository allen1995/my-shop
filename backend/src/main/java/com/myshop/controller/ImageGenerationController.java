package com.myshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myshop.dto.ApiResponse;
import com.myshop.entity.ImageGenerationTask;
import com.myshop.repository.ImageGenerationTaskRepository;
import com.myshop.service.AIGenerationService;
import com.myshop.service.OssService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/image-generation")
@RequiredArgsConstructor
@Slf4j
public class ImageGenerationController {

    private final ImageGenerationTaskRepository taskRepository;
    private final AIGenerationService aiGenerationService;
    private final OssService ossService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @PostMapping("/text-to-image")
    public ResponseEntity<ApiResponse<Map<String, Object>>> textToImage(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            String prompt = (String) request.get("prompt");
            @SuppressWarnings("unchecked")
            Map<String, Object> parameters = (Map<String, Object>) request.get("parameters");
            
            ImageGenerationTask task = new ImageGenerationTask();
            task.setUserId(userId);
            task.setPrompt(prompt);
            task.setTaskType(ImageGenerationTask.TaskType.TEXT_TO_IMAGE);
            task.setStatus(ImageGenerationTask.TaskStatus.PENDING);
            // 将parameters转为JSON字符串存储
            try {
                task.setParameters(parameters != null ? objectMapper.writeValueAsString(parameters) : "{}");
            } catch (Exception e) {
                log.error("序列化parameters失败", e);
                task.setParameters("{}");
            }
            
            task = taskRepository.save(task);
            
            // 异步生成
            aiGenerationService.generateImageAsync(task.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("taskId", task.getId());
            
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("生成任务创建失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpRequest) {
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("文件不能为空"));
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.ok(ApiResponse.error("只能上传图片文件"));
            }
            
            // 验证文件大小（10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.ok(ApiResponse.error("文件大小不能超过10MB"));
            }
            
            // 上传到OSS
            String imageUrl = ossService.uploadImage(file, "images/uploads");
            
            Map<String, Object> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("上传失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/image-to-image")
    public ResponseEntity<ApiResponse<Map<String, Object>>> imageToImage(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            
            // 参数验证
            String imageUrl = (String) request.get("imageUrl");
            String prompt = (String) request.get("prompt");
            Object similarityObj = request.get("similarity");
            
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("图片URL不能为空"));
            }
            if (prompt == null || prompt.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("提示词不能为空"));
            }
            
            // 相似度参数（0-100，默认50）
            Double similarity = 50.0;
            if (similarityObj != null) {
                if (similarityObj instanceof Number) {
                    similarity = ((Number) similarityObj).doubleValue();
                } else {
                    try {
                        similarity = Double.parseDouble(similarityObj.toString());
                    } catch (NumberFormatException e) {
                        return ResponseEntity.ok(ApiResponse.error("相似度参数格式错误"));
                    }
                }
                // 限制在0-100之间
                similarity = Math.max(0.0, Math.min(100.0, similarity));
            }
            
            // 创建图生图任务
            ImageGenerationTask task = new ImageGenerationTask();
            task.setUserId(userId);
            task.setPrompt(prompt);
            task.setImageUrl(imageUrl);
            task.setTaskType(ImageGenerationTask.TaskType.IMAGE_TO_IMAGE);
            task.setStatus(ImageGenerationTask.TaskStatus.PENDING);
            
                // 将parameters转为JSON字符串存储（包含similarity）
                Map<String, Object> parameters = new HashMap<>();
                if (request.get("parameters") instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> existingParams = (Map<String, Object>) request.get("parameters");
                    parameters.putAll(existingParams);
                }
                parameters.put("similarity", similarity);
                try {
                    task.setParameters(objectMapper.writeValueAsString(parameters));
                } catch (Exception e) {
                    log.error("序列化parameters失败", e);
                    task.setParameters("{}");
                }
            
            task = taskRepository.save(task);
            
            // 异步生成
            aiGenerationService.generateImageAsync(task.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("taskId", task.getId());
            
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("生成任务创建失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<ApiResponse<ImageGenerationTask>> getTask(
            @PathVariable Long taskId,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            ImageGenerationTask task = taskRepository.findByIdAndUserId(taskId, userId)
                    .orElseThrow(() -> new RuntimeException("任务不存在"));
            
            // 如果resultUrl是OSS URL且不带签名，生成签名URL
            if (task.getResultUrl() != null && task.getResultUrl().contains("oss") && !task.getResultUrl().contains("Signature")) {
                try {
                    String objectName = extractObjectNameFromUrl(task.getResultUrl());
                    String signedUrl = ossService.generateSignedUrl(objectName, 7 * 24); // 7天有效期
                    task.setResultUrl(signedUrl);
                } catch (Exception e) {
                    log.warn("生成签名URL失败，使用原URL: {}", task.getResultUrl(), e);
                }
            }
            
            // 确保返回的URL使用HTTPS协议
            if (task.getResultUrl() != null && task.getResultUrl().startsWith("http://")) {
                task.setResultUrl(task.getResultUrl().replace("http://", "https://"));
                log.info("将任务resultUrl从HTTP转换为HTTPS");
            }
            
            // 确保imageUrl也使用HTTPS协议（图生图的参考图片）
            if (task.getImageUrl() != null && task.getImageUrl().startsWith("http://")) {
                task.setImageUrl(task.getImageUrl().replace("http://", "https://"));
                log.info("将任务imageUrl从HTTP转换为HTTPS");
            }
            
            return ResponseEntity.ok(ApiResponse.success(task));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }
    
    /**
     * 从URL中提取OSS objectName
     */
    private String extractObjectNameFromUrl(String fileUrl) {
        try {
            // 如果是签名URL，先去掉查询参数
            String urlWithoutQuery = fileUrl;
            if (fileUrl.contains("?")) {
                urlWithoutQuery = fileUrl.substring(0, fileUrl.indexOf("?"));
            }
            
            // 提取objectName（去掉域名部分）
            // 格式：https://bucket-name.endpoint/objectName
            int protocolEnd = urlWithoutQuery.indexOf("://");
            if (protocolEnd > 0) {
                String afterProtocol = urlWithoutQuery.substring(protocolEnd + 3);
                int firstSlash = afterProtocol.indexOf("/");
                if (firstSlash > 0) {
                    return afterProtocol.substring(firstSlash + 1);
                }
            }
            
            return urlWithoutQuery;
        } catch (Exception e) {
            log.warn("从URL提取objectName失败: {}", fileUrl, e);
            return fileUrl;
        }
    }
}


