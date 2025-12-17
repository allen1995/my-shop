package com.myshop.dto;

import lombok.Data;

/**
 * 图像编辑响应DTO
 */
@Data
public class ImageEditResponse {
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 任务状态
     */
    private String status;
    
    /**
     * 生成结果URL
     */
    private String resultUrl;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 生成耗时（毫秒）
     */
    private Long duration;
}

