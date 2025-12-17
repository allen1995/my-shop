package com.myshop.dto;

import lombok.Data;

/**
 * 图像编辑请求DTO
 * 用于通义万相图像编辑API
 */
@Data
public class ImageEditRequest {
    
    /**
     * 印花图片URL（图一）
     */
    private String workImageUrl;
    
    /**
     * 包包底图URL（图二）
     */
    private String bagImageUrl;
    
    /**
     * 提示词
     */
    private String prompt;
    
    /**
     * 输出尺寸（默认1024*1024）
     */
    private String size = "1024*1024";
}

