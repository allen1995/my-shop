package com.myshop.ai;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DashScope（阿里云百炼）图片生成模型实现
 * 注意：不在类上使用@Component，而是在SpringAIConfig中配置Bean
 */
@Slf4j
public class DashScopeImageModel implements ImageModel {
    
    private final String apiKey;
    
    private static final String MODEL_NAME = "dashscope";
    private static final String TEXT_TO_IMAGE_MODEL = ImageSynthesis.Models.WANX_V1;
    private static final String IMAGE_TO_IMAGE_MODEL = "qwen-image-edit";
    
    /**
     * 构造函数，接收API Key
     */
    public DashScopeImageModel(String apiKey) {
        this.apiKey = apiKey;
    }
    
    @Override
    public String generateImage(String prompt, Map<String, Object> options) {
        try {
            ImageSynthesis gen = new ImageSynthesis();
            
            // 解析并验证size参数，默认128*128，最大限制150*150
            String size = parseAndValidateSize(options);
            
            ImageSynthesisParam param = ImageSynthesisParam.builder()
                    .apiKey(apiKey)
                    .model(TEXT_TO_IMAGE_MODEL)
                    .prompt(prompt)
                    .n(1)
                    .size(size)
                    .build();
            
            ImageSynthesisResult result = gen.call(param);
            
            // 提取图片URL（使用与AIGenerationService相同的方式）
            return extractImageUrlFromResult(result);
        } catch (NoApiKeyException | ApiException e) {
            log.error("DashScope文生图失败", e);
            throw new RuntimeException("图片生成失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("DashScope文生图失败", e);
            throw new RuntimeException("图片生成失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 解析并验证size参数
     * 支持的格式：
     * 1. 预设尺寸字符串："square"（正方形1024*1024）、"landscape"（横版720*1280）、"portrait"（竖版1280*720）
     * 2. 直接尺寸字符串："1024*1024"、"720*1280"、"1280*720"
     * 默认值：1024*1024（正方形）
     */
    private String parseAndValidateSize(Map<String, Object> options) {
        // 预设尺寸
        final String SQUARE = "1024*1024";      // 正方形
        final String LANDSCAPE = "720*1280";    // 横版
        final String PORTRAIT = "1280*720";     // 竖版
        final String DEFAULT_SIZE = SQUARE;      // 默认正方形
        
        // 默认尺寸
        String size = DEFAULT_SIZE;
        
        // 尝试从options中解析size
        if (options != null && options.containsKey("size")) {
            Object sizeObj = options.get("size");
            if (sizeObj instanceof String) {
                String sizeStr = ((String) sizeObj).trim();
                if (!sizeStr.isEmpty()) {
                    // 检查是否是预设尺寸
                    if ("square".equalsIgnoreCase(sizeStr)) {
                        size = SQUARE;
                        log.info("使用预设尺寸：正方形 {}", size);
                    } else if ("landscape".equalsIgnoreCase(sizeStr) || "横版".equals(sizeStr)) {
                        size = LANDSCAPE;
                        log.info("使用预设尺寸：横版 {}", size);
                    } else if ("portrait".equalsIgnoreCase(sizeStr) || "竖版".equals(sizeStr)) {
                        size = PORTRAIT;
                        log.info("使用预设尺寸：竖版 {}", size);
                    } else if (sizeStr.contains("*")) {
                        // 直接传尺寸字符串，如 "1024*1024"
                        String[] parts = sizeStr.split("\\*");
                        if (parts.length == 2) {
                            try {
                                int width = Integer.parseInt(parts[0].trim());
                                int height = Integer.parseInt(parts[1].trim());
                                
                                // 验证尺寸是否合法（只允许预设的三种尺寸）
                                String customSize = width + "*" + height;
                                if (customSize.equals(SQUARE) || customSize.equals(LANDSCAPE) || customSize.equals(PORTRAIT)) {
                                    size = customSize;
                                    log.info("解析size参数: {}", size);
                                } else {
                                    log.warn("size参数 {} 不是支持的尺寸，使用默认值 {}", customSize, DEFAULT_SIZE);
                                    size = DEFAULT_SIZE;
                                }
                            } catch (NumberFormatException e) {
                                log.warn("size参数格式错误: {}，使用默认值 {}", sizeStr, DEFAULT_SIZE, e);
                                size = DEFAULT_SIZE;
                            }
                        } else {
                            log.warn("size参数格式错误: {}，使用默认值 {}", sizeStr, DEFAULT_SIZE);
                            size = DEFAULT_SIZE;
                        }
                    } else {
                        log.warn("size参数格式错误: {}，使用默认值 {}", sizeStr, DEFAULT_SIZE);
                        size = DEFAULT_SIZE;
                    }
                }
            }
        }
        
        log.info("最终使用的size: {}", size);
        return size;
    }
    
    /**
     * 从ImageSynthesisResult中提取图片URL
     * 使用与AIGenerationService相同的提取逻辑
     */
    private String extractImageUrlFromResult(ImageSynthesisResult result) {
        if (result == null) {
            throw new RuntimeException("生成结果为空");
        }
        
        // 尝试通过反射获取URL
        try {
            java.lang.reflect.Method[] methods = result.getClass().getMethods();
            for (java.lang.reflect.Method method : methods) {
                String methodName = method.getName().toLowerCase();
                if ((methodName.contains("url") || methodName.contains("image")) 
                    && method.getParameterCount() == 0 
                    && method.getReturnType() == String.class) {
                    try {
                        Object value = method.invoke(result);
                        if (value != null && value instanceof String && !((String) value).isEmpty()) {
                            return (String) value;
                        }
                    } catch (Exception e) {
                        // 忽略反射错误
                    }
                }
            }
        } catch (Exception e) {
            log.warn("反射获取URL失败", e);
        }
        
        // 通过output获取
        Object output = result.getOutput();
        if (output == null) {
            throw new RuntimeException("生成结果output为空");
        }
        
        // 如果output是List类型
        if (output instanceof java.util.List) {
            @SuppressWarnings("unchecked")
            java.util.List<Object> results = (java.util.List<Object>) output;
            if (!results.isEmpty()) {
                Object firstResult = results.get(0);
                
                // 如果是Map类型
                if (firstResult instanceof java.util.Map) {
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> resultMap = (java.util.Map<String, Object>) firstResult;
                    String url = (String) resultMap.get("url");
                    if (url != null) return url;
                    url = (String) resultMap.get("imageUrl");
                    if (url != null) return url;
                    url = (String) resultMap.get("image_url");
                    if (url != null) return url;
                } else {
                    // 尝试使用反射获取url字段
                    try {
                        java.lang.reflect.Method getUrlMethod = firstResult.getClass().getMethod("getUrl");
                        Object urlObj = getUrlMethod.invoke(firstResult);
                        if (urlObj != null) {
                            return urlObj.toString();
                        }
                    } catch (Exception e) {
                        log.warn("无法通过getUrl方法获取URL", e);
                    }
                }
            }
        }
        
        // 尝试通过getOutput().getResults()方式（如果存在）
        try {
            java.lang.reflect.Method getOutputMethod = result.getClass().getMethod("getOutput");
            Object outputObj = getOutputMethod.invoke(result);
            if (outputObj != null) {
                java.lang.reflect.Method getResultsMethod = outputObj.getClass().getMethod("getResults");
                Object resultsObj = getResultsMethod.invoke(outputObj);
                if (resultsObj instanceof java.util.List) {
                    @SuppressWarnings("unchecked")
                    java.util.List<Object> results = (java.util.List<Object>) resultsObj;
                    if (!results.isEmpty()) {
                        Object firstResult = results.get(0);
                        java.lang.reflect.Method getUrlMethod = firstResult.getClass().getMethod("getUrl");
                        Object urlObj = getUrlMethod.invoke(firstResult);
                        if (urlObj != null) {
                            return urlObj.toString();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("通过getOutput().getResults()获取URL失败", e);
        }
        
        throw new RuntimeException("无法从生成结果中提取图片URL");
    }
    
    @Override
    public String generateImageFromImage(String imageUrl, String prompt, Map<String, Object> options) {
        try {
            MultiModalConversation conv = new MultiModalConversation();
            
            // 构建多模态消息
            List<Map<String, Object>> contentList = new ArrayList<>();
            contentList.add(Collections.singletonMap("image", imageUrl));
            contentList.add(Collections.singletonMap("text", prompt));
            
            MultiModalMessage userMessage = MultiModalMessage.builder()
                    .role(Role.USER.getValue())
                    .content(contentList)
                    .build();
            
            // 设置参数
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("watermark", false);
            parameters.put("n", 1);
            parameters.put("prompt_extend", true);
            
            // 处理相似度参数
            if (options != null && options.containsKey("similarity")) {
                double similarity = ((Number) options.get("similarity")).doubleValue();
                if (similarity < 30) {
                    parameters.put("negative_prompt", "low quality, blurry");
                } else if (similarity > 70) {
                    parameters.put("negative_prompt", "completely different style, major changes");
                }
            }
            
            MultiModalConversationParam param = MultiModalConversationParam.builder()
                    .apiKey(apiKey)
                    .model(IMAGE_TO_IMAGE_MODEL)
                    .messages(Collections.singletonList(userMessage))
                    .parameters(parameters)
                    .build();
            
            MultiModalConversationResult result = conv.call(param);
            
            // 提取图片URL（使用与AIGenerationService相同的方式）
            return extractImageUrlFromMultiModalResult(result);
        } catch (Exception e) {
            log.error("DashScope图生图失败", e);
            throw new RuntimeException("图片生成失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getModelName() {
        return MODEL_NAME;
    }
    
    /**
     * 从MultiModalConversationResult中提取图片URL
     * 使用与AIGenerationService相同的提取逻辑
     */
    private String extractImageUrlFromMultiModalResult(MultiModalConversationResult result) {
        try {
            if (result != null && result.getOutput() != null && 
                result.getOutput().getChoices() != null && 
                !result.getOutput().getChoices().isEmpty()) {
                
                java.util.List<java.util.Map<String, Object>> contentList = 
                    result.getOutput().getChoices().get(0).getMessage().getContent();
                
                if (contentList != null) {
                    for (java.util.Map<String, Object> content : contentList) {
                        if (content.containsKey("image")) {
                            Object imageObj = content.get("image");
                            if (imageObj != null) {
                                return imageObj.toString();
                            }
                        }
                    }
                }
            }
            
            log.warn("无法从MultiModalConversationResult中提取图片URL");
            throw new RuntimeException("图片生成失败：未返回图片URL");
        } catch (Exception e) {
            log.error("提取图片URL失败", e);
            throw new RuntimeException("图片生成失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your-api-key");
    }
}

