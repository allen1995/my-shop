package com.myshop.dto;

import lombok.Data;

/**
 * 预览生成响应DTO
 */
@Data
public class PreviewGenerateResponse {
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 任务状态
     */
    private String status;
    
    /**
     * 预计时间（秒）
     */
    private Integer estimatedTime;
    
    /**
     * 结果URL（如果已缓存）
     */
    private String resultUrl;
}

