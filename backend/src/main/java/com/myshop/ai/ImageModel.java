package com.myshop.ai;

import java.util.Map;

/**
 * 统一的图片生成模型接口
 * 抽象不同AI服务提供商的图片生成功能
 */
public interface ImageModel {
    
    /**
     * 文生图
     * @param prompt 提示词
     * @param options 生成选项（尺寸、数量等）
     * @return 生成的图片URL
     */
    String generateImage(String prompt, Map<String, Object> options);
    
    /**
     * 图生图
     * @param imageUrl 参考图片URL
     * @param prompt 提示词
     * @param options 生成选项（相似度、尺寸等）
     * @return 生成的图片URL
     */
    String generateImageFromImage(String imageUrl, String prompt, Map<String, Object> options);
    
    /**
     * 获取模型名称
     * @return 模型名称
     */
    String getModelName();
    
    /**
     * 检查模型是否可用
     * @return true if available
     */
    boolean isAvailable();
}


