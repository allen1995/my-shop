package com.myshop.config;

import com.myshop.ai.DashScopeImageModel;
import com.myshop.ai.ImageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring AI 配置类
 * 配置和管理不同的AI图片生成模型
 */
@Configuration
@Slf4j
public class SpringAIConfig {
    
    @Value("${app.ai.primary-model:dashscope}")
    private String primaryModel;
    
    @Value("${app.ai.fallback-model:}")
    private String fallbackModel;
    
    @Value("${app.aliyun.dashscope.api-key}")
    private String dashScopeApiKey;
    
    /**
     * DashScope 图片生成模型（主模型）
     */
    @Bean
    @Primary
    @ConditionalOnProperty(name = "app.ai.models.dashscope.enabled", havingValue = "true", matchIfMissing = true)
    public ImageModel dashScopeImageModel() {
        log.info("配置DashScope图片生成模型（主模型）");
        DashScopeImageModel model = new DashScopeImageModel(dashScopeApiKey);
        return model;
    }
    
    /**
     * 获取所有可用的图片生成模型
     */
    @Bean
    public List<ImageModel> availableImageModels(ImageModel dashScopeImageModel) {
        List<ImageModel> models = new ArrayList<>();
        
        if (dashScopeImageModel != null && dashScopeImageModel.isAvailable()) {
            models.add(dashScopeImageModel);
            log.info("DashScope模型可用");
        } else {
            log.warn("DashScope模型不可用：API Key未配置或无效");
        }
        
        return models;
    }
    
    /**
     * 配置验证
     */
    @PostConstruct
    public void validateConfiguration() {
        log.info("=== Spring AI 配置验证 ===");
        log.info("主模型: {}", primaryModel);
        log.info("备用模型: {}", fallbackModel.isEmpty() ? "未配置" : fallbackModel);
        log.info("========================");
    }
}

