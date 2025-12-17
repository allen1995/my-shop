package com.myshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 预览生成请求DTO
 */
@Data
public class PreviewGenerateRequest {
    
    /**
     * 作品ID
     */
    @NotNull(message = "作品ID不能为空")
    private Long workId;
    
    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    /**
     * 颜色
     */
    private String color;
    
    /**
     * 尺寸
     */
    private String size;
}

