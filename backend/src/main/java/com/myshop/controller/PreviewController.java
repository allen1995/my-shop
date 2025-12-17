package com.myshop.controller;

import com.myshop.dto.ApiResponse;
import com.myshop.dto.PreviewGenerateRequest;
import com.myshop.dto.PreviewGenerateResponse;
import com.myshop.dto.PreviewTaskStatusResponse;
import com.myshop.entity.PreviewTask;
import com.myshop.enums.TaskStatus;
import com.myshop.service.PreviewTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 预览控制器
 * 处理印花预览相关的API请求
 */
@RestController
@RequestMapping("/preview")
@Tag(name = "Preview", description = "预览管理API")
@Slf4j
public class PreviewController {
    
    @Autowired
    private PreviewTaskService previewTaskService;
    
    /**
     * 生成预览
     */
    @PostMapping("/generate")
    @Operation(summary = "生成预览图片", description = "创建预览任务并异步生成预览图片")
    public ApiResponse<PreviewGenerateResponse> generatePreview(
            @Valid @RequestBody PreviewGenerateRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        // 临时方案：如果header中没有userId，从request中获取或使用默认值
        if (userId == null) {
            userId = 1L; // 默认用户ID（生产环境应该从JWT token中获取）
        }
        
        log.info("收到预览生成请求 - userId:{}, workId:{}, productId:{}, color:{}, size:{}", 
                 userId, request.getWorkId(), request.getProductId(), 
                 request.getColor(), request.getSize());
        
        try {
            PreviewTask task = previewTaskService.createTask(
                userId, 
                request.getWorkId(), 
                request.getProductId(), 
                request.getColor(), 
                request.getSize()
            );
            
            PreviewGenerateResponse response = new PreviewGenerateResponse();
            response.setTaskId(task.getId());
            response.setStatus(task.getStatus().name());
            
            // 如果已完成，直接返回结果URL
            if (task.getStatus() == TaskStatus.COMPLETED && task.getResultUrl() != null) {
                response.setResultUrl(task.getResultUrl());
                response.setEstimatedTime(0);
            } else {
                // 预计生成时间
                response.setEstimatedTime(10); // 预计10秒
            }
            
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.error("参数验证失败", e);
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("生成预览失败", e);
            return ApiResponse.error(500, "生成预览失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询任务状态
     */
    @GetMapping("/tasks/{taskId}")
    @Operation(summary = "查询预览任务状态", description = "根据任务ID查询预览任务的状态和结果")
    public ApiResponse<PreviewTaskStatusResponse> getTaskStatus(@PathVariable Long taskId) {
        log.info("查询任务状态: {}", taskId);
        
        try {
            PreviewTask task = previewTaskService.getTask(taskId);
            
            PreviewTaskStatusResponse response = new PreviewTaskStatusResponse();
            response.setTaskId(task.getId());
            response.setStatus(task.getStatus().name());
            response.setProgress(calculateProgress(task));
            response.setResultUrl(task.getResultUrl());
            response.setErrorMessage(task.getErrorMessage());
            response.setCreateTime(task.getCreateTime());
            response.setUpdateTime(task.getUpdateTime());
            
            return ApiResponse.success(response);
        } catch (RuntimeException e) {
            log.error("查询任务状态失败", e);
            return ApiResponse.error(404, "任务不存在: " + taskId);
        } catch (Exception e) {
            log.error("查询任务状态失败", e);
            return ApiResponse.error(500, "查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 计算任务进度
     */
    private int calculateProgress(PreviewTask task) {
        switch (task.getStatus()) {
            case PENDING:
                return 0;
            case PROCESSING:
                return 50;
            case COMPLETED:
                return 100;
            case FAILED:
                return 0;
            default:
                return 0;
        }
    }
}

