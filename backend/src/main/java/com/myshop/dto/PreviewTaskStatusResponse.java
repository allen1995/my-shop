package com.myshop.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预览任务状态响应DTO
 */
@Data
public class PreviewTaskStatusResponse {
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 任务状态
     */
    private String status;
    
    /**
     * 进度（0-100）
     */
    private Integer progress;
    
    /**
     * 结果URL
     */
    private String resultUrl;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

