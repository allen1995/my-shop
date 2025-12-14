package com.myshop.controller;

import com.myshop.dto.ApiResponse;
import com.myshop.entity.ImageGenerationTask;
import com.myshop.repository.ImageGenerationTaskRepository;
import com.myshop.service.AIGenerationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/image-generation")
@RequiredArgsConstructor
public class ImageGenerationController {
    
    private final ImageGenerationTaskRepository taskRepository;
    private final AIGenerationService aiGenerationService;
    
    @PostMapping("/text-to-image")
    public ResponseEntity<ApiResponse<Map<String, Object>>> textToImage(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            String prompt = (String) request.get("prompt");
            Map<String, Object> parameters = (Map<String, Object>) request.get("parameters");
            
            ImageGenerationTask task = new ImageGenerationTask();
            task.setUserId(userId);
            task.setPrompt(prompt);
            task.setTaskType(ImageGenerationTask.TaskType.TEXT_TO_IMAGE);
            task.setStatus(ImageGenerationTask.TaskStatus.PENDING);
            // 将parameters转为JSON字符串存储
            task.setParameters(parameters != null ? parameters.toString() : "{}");
            
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
            
            return ResponseEntity.ok(ApiResponse.success(task));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }
}


