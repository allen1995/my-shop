package com.myshop.service;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.myshop.entity.ImageGenerationTask;
import com.myshop.repository.ImageGenerationTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIGenerationService {
    
    @Value("${app.aliyun.dashscope.api-key}")
    private String apiKey;
    
    private final ImageGenerationTaskRepository taskRepository;
    private final OssService ossService;
    
    @Async
    @Transactional
    public void generateImageAsync(Long taskId) {
        ImageGenerationTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        
        try {
            task.setStatus(ImageGenerationTask.TaskStatus.PROCESSING);
            taskRepository.save(task);
            
            String imageUrl;
            
            if (task.getTaskType() == ImageGenerationTask.TaskType.TEXT_TO_IMAGE) {
                // 文生图：使用ImageSynthesis API
                ImageSynthesisResult result = generateTextToImage(task);
                imageUrl = downloadAndUploadToOss(result);
            } else {
                // 图生图：使用 qwen-image-edit 模型
                imageUrl = generateImageToImageWithQwenEdit(task);
            }
            
            task.setStatus(ImageGenerationTask.TaskStatus.COMPLETED);
            task.setResultUrl(imageUrl);
            taskRepository.save(task);
            
        } catch (Exception e) {
            log.error("图片生成失败", e);
            task.setStatus(ImageGenerationTask.TaskStatus.FAILED);
            task.setErrorMessage(e.getMessage());
            taskRepository.save(task);
        }
    }
    
    private ImageSynthesisResult generateTextToImage(ImageGenerationTask task) 
            throws NoApiKeyException, ApiException, InputRequiredException {
        ImageSynthesis gen = new ImageSynthesis();
        ImageSynthesisParam param = ImageSynthesisParam.builder()
                .apiKey(apiKey)
                .model(ImageSynthesis.Models.WANX_V1)
                .prompt(task.getPrompt())
                .n(1)
                .size("1024*1024") // 默认尺寸，可以从parameters中解析
                .build();
        
        return gen.call(param);
    }
    
    /**
     * 图生图实现 - 使用 qwen-image-edit 模型
     * 使用 MultiModalConversation API 调用 qwen-image-edit 模型
     * 返回生成的图片URL（已上传到OSS）
     */
    private String generateImageToImageWithQwenEdit(ImageGenerationTask task) throws Exception {
        // 参数验证
        if (task.getImageUrl() == null || task.getImageUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("参考图片URL不能为空");
        }
        if (task.getPrompt() == null || task.getPrompt().trim().isEmpty()) {
            throw new IllegalArgumentException("提示词不能为空");
        }
        
        log.info("使用 qwen-image-edit 模型进行图生图 - imageUrl: {}, prompt: {}", 
            task.getImageUrl(), task.getPrompt());
        
        try {
            // 使用 MultiModalConversation API 调用 qwen-image-edit 模型
            MultiModalConversation conv = new MultiModalConversation();
            
            // 构建用户消息，包含参考图片和文本描述
            java.util.List<java.util.Map<String, Object>> contentList = new java.util.ArrayList<>();
            // 添加参考图片
            contentList.add(java.util.Collections.singletonMap("image", task.getImageUrl()));
            // 添加文本描述（prompt）
            contentList.add(java.util.Collections.singletonMap("text", task.getPrompt()));
            
            MultiModalMessage userMessage = MultiModalMessage.builder()
                    .role(Role.USER.getValue())
                    .content(contentList)
                    .build();
            
            // 解析similarity参数（0-100，默认50）
            // 对于qwen-image-edit，可以通过negative_prompt或其他参数来调整相似度
            double similarity = 50.0;
            try {
                if (task.getParameters() != null && !task.getParameters().isEmpty()) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode paramsNode = mapper.readTree(task.getParameters());
                    if (paramsNode.has("similarity")) {
                        similarity = paramsNode.get("similarity").asDouble();
                        similarity = Math.max(0.0, Math.min(100.0, similarity));
                    }
                }
            } catch (Exception e) {
                log.warn("解析similarity参数失败，使用默认值50", e);
            }
            
            // 设置参数
            java.util.Map<String, Object> parameters = new java.util.HashMap<>();
            parameters.put("watermark", false);
            parameters.put("n", 1); // 生成1张图片
            parameters.put("prompt_extend", true); // 启用prompt扩展
            
            // 根据similarity调整negative_prompt（相似度越高，negative_prompt越少）
            if (similarity < 30) {
                // 低相似度：允许更多变化
                parameters.put("negative_prompt", "low quality, blurry");
            } else if (similarity > 70) {
                // 高相似度：保持更多原图特征
                parameters.put("negative_prompt", "completely different style, major changes");
            }
            
            // 构建请求参数
            MultiModalConversationParam param = MultiModalConversationParam.builder()
                    .apiKey(apiKey)
                    .model("qwen-image-edit") // 使用 qwen-image-edit 模型
                    .messages(java.util.Collections.singletonList(userMessage))
                    .parameters(parameters)
                    .build();
            
            // 调用 API
            MultiModalConversationResult result = conv.call(param);
            
            // 从结果中提取生成的图片URL
            String generatedImageUrl = extractImageUrlFromMultiModalResult(result);
            
            if (generatedImageUrl == null || generatedImageUrl.isEmpty()) {
                throw new RuntimeException("qwen-image-edit API未返回图片URL");
            }
            
            log.info("qwen-image-edit 生成成功，图片URL: {}", generatedImageUrl);
            
            // 下载生成的图片并上传到OSS
            return downloadAndUploadToOssFromUrl(generatedImageUrl);
            
        } catch (UploadFileException e) {
            log.error("上传文件失败", e);
            throw new Exception("上传文件失败: " + e.getMessage(), e);
        } catch (ApiException | NoApiKeyException e) {
            log.error("调用 qwen-image-edit API 失败", e);
            throw new Exception("图生图失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("图生图处理失败", e);
            throw e;
        }
    }
    
    /**
     * 从 MultiModalConversationResult 中提取图片URL
     */
    private String extractImageUrlFromMultiModalResult(MultiModalConversationResult result) {
        try {
            if (result.getOutput() != null && result.getOutput().getChoices() != null 
                && !result.getOutput().getChoices().isEmpty()) {
                
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
            
            log.warn("无法从MultiModalConversationResult中提取图片URL，result: {}", result);
            return null;
        } catch (Exception e) {
            log.error("提取图片URL失败", e);
            return null;
        }
    }
    
    /**
     * 从URL下载图片并上传到OSS
     */
    private String downloadAndUploadToOssFromUrl(String imageUrl) throws Exception {
        log.info("开始下载图片: {}", imageUrl);
        URI uri = URI.create(imageUrl);
        URL url = uri.toURL();
        try (InputStream inputStream = url.openStream()) {
            // 上传到OSS
            String ossUrl = ossService.uploadFile(inputStream, "generated.png", "images/works");
            log.info("图片已上传到OSS: {}", ossUrl);
            return ossUrl;
        }
    }
    
    private String downloadAndUploadToOss(ImageSynthesisResult result) throws Exception {
        // 从结果中获取图片URL
        log.info("开始解析生成结果，result类型: {}", result.getClass().getName());
        
        // 首先尝试直接通过result对象的方法获取URL
        String imageUrl = null;
        try {
            // 尝试调用可能的方法
            java.lang.reflect.Method[] methods = result.getClass().getMethods();
            for (java.lang.reflect.Method method : methods) {
                String methodName = method.getName().toLowerCase();
                if ((methodName.contains("url") || methodName.contains("image")) 
                    && method.getParameterCount() == 0 
                    && method.getReturnType() == String.class) {
                    try {
                        Object value = method.invoke(result);
                        if (value != null && value instanceof String && !((String) value).isEmpty()) {
                            imageUrl = (String) value;
                            log.info("通过方法 {} 获取到URL: {}", method.getName(), imageUrl);
                            break;
                        }
                    } catch (Exception e) {
                        // 忽略反射错误
                    }
                }
            }
        } catch (Exception e) {
            log.warn("反射获取URL失败", e);
        }
        
        Object output = result.getOutput();
        log.info("output类型: {}, 值: {}", output != null ? output.getClass().getName() : "null", output);
        
        if (output == null && imageUrl == null) {
            // 尝试直接访问result的其他方法
            log.warn("output为空，尝试其他方式获取结果");
            try {
                // 尝试使用反射获取可能的字段
                java.lang.reflect.Method[] methods = result.getClass().getMethods();
                for (java.lang.reflect.Method method : methods) {
                    if (method.getName().startsWith("get") && method.getParameterCount() == 0) {
                        try {
                            Object value = method.invoke(result);
                            if (value != null) {
                                log.info("方法 {} 返回值类型: {}, 值: {}", method.getName(), 
                                    value.getClass().getName(), value);
                            }
                        } catch (Exception e) {
                            // 忽略反射错误
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("反射获取方法失败", e);
            }
            if (imageUrl == null) {
                throw new RuntimeException("生成结果为空，output为null且无法通过反射获取URL");
            }
        }
        
        if (output == null && imageUrl != null) {
            // 如果已经通过反射获取到URL，直接使用
            log.info("使用反射获取的URL: {}", imageUrl);
        } else if (output != null) {
            // 尝试多种方式获取图片URL（如果还未获取到）
            if (imageUrl == null) {
            try {
            // 方式1：如果output是List类型
            if (output instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<Object> results = (java.util.List<Object>) output;
                log.info("output是List类型，大小: {}", results.size());
                if (!results.isEmpty()) {
                    Object firstResult = results.get(0);
                    log.info("第一个结果类型: {}", firstResult != null ? firstResult.getClass().getName() : "null");
                    
                    if (firstResult instanceof java.util.Map) {
                        @SuppressWarnings("unchecked")
                        java.util.Map<String, Object> resultMap = (java.util.Map<String, Object>) firstResult;
                        log.info("结果Map的键: {}", resultMap.keySet());
                        imageUrl = (String) resultMap.get("url");
                        if (imageUrl == null) {
                            imageUrl = (String) resultMap.get("imageUrl");
                        }
                        if (imageUrl == null) {
                            imageUrl = (String) resultMap.get("image_url");
                        }
                    } else {
                        // 尝试使用反射获取url字段
                        try {
                            java.lang.reflect.Method getUrlMethod = firstResult.getClass().getMethod("getUrl");
                            Object urlObj = getUrlMethod.invoke(firstResult);
                            if (urlObj != null) {
                                imageUrl = urlObj.toString();
                            }
                        } catch (Exception e) {
                            log.warn("无法通过getUrl方法获取URL", e);
                        }
                    }
                }
            }
            
            // 方式2：如果output是Map类型
            if (imageUrl == null && output instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> outputMap = (java.util.Map<String, Object>) output;
                log.info("output是Map类型，键: {}", outputMap.keySet());
                
                // 尝试直接获取url
                imageUrl = (String) outputMap.get("url");
                if (imageUrl == null) {
                    imageUrl = (String) outputMap.get("imageUrl");
                }
                if (imageUrl == null) {
                    imageUrl = (String) outputMap.get("image_url");
                }
                
                // 尝试获取results字段
                if (imageUrl == null) {
                    Object results = outputMap.get("results");
                    log.info("results字段类型: {}", results != null ? results.getClass().getName() : "null");
                    if (results instanceof java.util.List && !((java.util.List<?>) results).isEmpty()) {
                        Object firstResult = ((java.util.List<?>) results).get(0);
                        if (firstResult instanceof java.util.Map) {
                            @SuppressWarnings("unchecked")
                            java.util.Map<String, Object> resultMap = (java.util.Map<String, Object>) firstResult;
                            log.info("results中第一个元素的键: {}", resultMap.keySet());
                            imageUrl = (String) resultMap.get("url");
                            if (imageUrl == null) {
                                imageUrl = (String) resultMap.get("imageUrl");
                            }
                            if (imageUrl == null) {
                                imageUrl = (String) resultMap.get("image_url");
                            }
                        }
                    }
                }
            }
            
            // 方式3：尝试将output转为JSON字符串，然后解析
            if (imageUrl == null) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonStr = mapper.writeValueAsString(output);
                    log.info("output的JSON表示: {}", jsonStr);
                    
                    // 尝试从JSON中提取url
                    JsonNode jsonNode = mapper.readTree(jsonStr);
                    if (jsonNode.has("url")) {
                        imageUrl = jsonNode.get("url").asText();
                    } else if (jsonNode.has("results") && jsonNode.get("results").isArray() && jsonNode.get("results").size() > 0) {
                        JsonNode firstResult = jsonNode.get("results").get(0);
                        if (firstResult.has("url")) {
                            imageUrl = firstResult.get("url").asText();
                        }
                    }
                } catch (Exception e) {
                    log.warn("JSON解析失败", e);
                }
            }
            
            } catch (Exception e) {
                log.error("提取URL时发生异常", e);
            }
            } // end if (imageUrl == null)
        } // end else if (output != null)
        
        log.info("最终提取的imageUrl: {}", imageUrl);
        
        if (imageUrl == null || imageUrl.isEmpty()) {
            // 输出完整的result信息用于调试
            log.error("无法从生成结果中获取图片URL，result完整信息: {}", result);
            throw new RuntimeException("无法从生成结果中获取图片URL。请检查日志查看result的详细结构。");
        }
        
        // 下载图片
        log.info("开始下载图片: {}", imageUrl);
        URI uri = URI.create(imageUrl);
        URL url = uri.toURL();
        try (InputStream inputStream = url.openStream()) {
            // 上传到OSS
            String ossUrl = ossService.uploadFile(inputStream, "generated.png", "images/works");
            log.info("图片已上传到OSS: {}", ossUrl);
            return ossUrl;
        }
    }
}


