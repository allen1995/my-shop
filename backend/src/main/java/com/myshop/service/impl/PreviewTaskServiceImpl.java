package com.myshop.service.impl;

import com.myshop.dto.ImageEditRequest;
import com.myshop.dto.ImageEditResponse;
import com.myshop.entity.PreviewTask;
import com.myshop.entity.Product;
import com.myshop.entity.Work;
import com.myshop.enums.TaskStatus;
import com.myshop.repository.PreviewTaskRepository;
import com.myshop.repository.ProductRepository;
import com.myshop.repository.WorkRepository;
import com.myshop.service.ImageEditService;
import com.myshop.service.PreviewTaskService;
import com.myshop.util.PreviewCacheKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 预览任务服务实现
 */
@Service
@Slf4j
public class PreviewTaskServiceImpl implements PreviewTaskService {
    
    @Autowired
    private PreviewTaskRepository taskRepository;
    
    @Autowired
    private ImageEditService imageEditService;
    
    @Autowired
    private WorkRepository workRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    private static final int MAX_RETRIES = 3;
    private static final int CACHE_DAYS = 7;
    
    @Override
    @Transactional
    public PreviewTask createTask(Long userId, Long workId, Long productId, String color, String size) {
        log.info("创建预览任务 - userId:{}, workId:{}, productId:{}, color:{}, size:{}", 
                 userId, workId, productId, color, size);
        
        // 1. 生成缓存键
        String cacheKey = PreviewCacheKey.generate(workId, productId, color, size);
        
        // 2. 检查是否存在已完成的任务
        PreviewTask cachedTask = taskRepository
            .findFirstByCacheKeyAndStatusOrderByCreateTimeDesc(cacheKey, TaskStatus.COMPLETED)
            .orElse(null);
        
        if (cachedTask != null && isCacheValid(cachedTask)) {
            log.info("找到缓存的预览任务: {}", cachedTask.getId());
            return cachedTask;
        }
        
        // 3. 检查是否存在处理中的任务
        List<TaskStatus> processingStatuses = Arrays.asList(TaskStatus.PENDING, TaskStatus.PROCESSING);
        PreviewTask processingTask = taskRepository
            .findFirstByCacheKeyAndStatusInOrderByCreateTimeDesc(cacheKey, processingStatuses)
            .orElse(null);
        
        if (processingTask != null) {
            log.info("找到处理中的预览任务: {}", processingTask.getId());
            return processingTask;
        }
        
        // 4. 创建新任务
        PreviewTask task = new PreviewTask();
        task.setUserId(userId);
        task.setWorkId(workId);
        task.setProductId(productId);
        task.setColor(color);
        task.setSize(size);
        task.setStatus(TaskStatus.PENDING);
        task.setRetryCount(0);
        task.setCacheKey(cacheKey);
        
        task = taskRepository.save(task);
        log.info("创建新预览任务: {}", task.getId());
        
        // 5. 异步执行任务
        executeTaskAsync(task.getId());
        
        return task;
    }
    
    @Override
    public PreviewTask getTask(Long taskId) {
        return taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("任务不存在: " + taskId));
    }
    
    @Override
    public void executeTask(Long taskId) {
        executeTaskAsync(taskId);
    }
    
    /**
     * 异步执行任务
     */
    @Async("imageEditExecutor")
    protected void executeTaskAsync(Long taskId) {
        log.info("开始执行预览任务: {}", taskId);
        
        PreviewTask task = taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            log.error("任务不存在: {}", taskId);
            return;
        }
        
        try {
            // 1. 更新状态为PROCESSING
            task.setStatus(TaskStatus.PROCESSING);
            taskRepository.save(task);
            log.info("任务状态更新为PROCESSING: {}", taskId);
            
            // 2. 获取作品和商品信息
            Work work = workRepository.findById(task.getWorkId())
                .orElseThrow(() -> new RuntimeException("作品不存在: " + task.getWorkId()));
            
            Product product = productRepository.findById(task.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在: " + task.getProductId()));
            
            // 3. 获取图片URL
            String workImageUrl = work.getImageUrl();
            String bagImageUrl = getBagImageUrl(product, task.getColor(), task.getSize());
            
            log.info("作品图片URL: {}", workImageUrl);
            log.info("包包图片URL: {}", bagImageUrl);
            
            // 4. 调用图像编辑服务
            ImageEditRequest request = new ImageEditRequest();
            request.setWorkImageUrl(workImageUrl);
            request.setBagImageUrl(bagImageUrl);
            request.setPrompt(generatePrompt());
            
            CompletableFuture<ImageEditResponse> future = imageEditService.compositeImage(request);
            ImageEditResponse response = future.get(60, TimeUnit.SECONDS);
            
            // 5. 更新任务结果
            if ("SUCCEEDED".equals(response.getStatus())) {
                task.setStatus(TaskStatus.COMPLETED);
                task.setResultUrl(response.getResultUrl());
                log.info("任务执行成功: {}, 结果URL: {}", taskId, response.getResultUrl());
            } else {
                throw new RuntimeException("图像编辑失败: " + response.getErrorMessage());
            }
            
            taskRepository.save(task);
            
        } catch (Exception e) {
            log.error("预览任务执行失败: {}", taskId, e);
            handleTaskFailure(task, e);
        }
    }
    
    /**
     * 处理任务失败
     */
    @Transactional
    protected void handleTaskFailure(PreviewTask task, Exception e) {
        task.setRetryCount(task.getRetryCount() + 1);
        task.setErrorMessage(e.getMessage());
        
        if (task.getRetryCount() < MAX_RETRIES) {
            // 重试
            log.info("任务失败，准备重试 ({}/{}): {}", 
                     task.getRetryCount(), MAX_RETRIES, task.getId());
            
            task.setStatus(TaskStatus.PENDING);
            taskRepository.save(task);
            
            // 延迟重试
            int delaySeconds = (int) Math.pow(2, task.getRetryCount()) * 5; // 5s, 10s, 20s
            scheduleRetry(task.getId(), delaySeconds);
        } else {
            // 标记为失败
            log.error("任务失败，已达到最大重试次数: {}", task.getId());
            task.setStatus(TaskStatus.FAILED);
            taskRepository.save(task);
        }
    }
    
    /**
     * 延迟重试任务
     */
    private void scheduleRetry(Long taskId, int delaySeconds) {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(delaySeconds * 1000L);
                executeTaskAsync(taskId);
            } catch (InterruptedException e) {
                log.error("重试任务被中断: {}", taskId, e);
                Thread.currentThread().interrupt();
            }
        });
    }
    
    /**
     * 定时重试失败的任务
     */
    @Override
    @Scheduled(fixedDelay = 300000) // 每5分钟执行一次
    public void retryFailedTasks() {
        log.info("开始检查需要重试的失败任务");
        
        LocalDateTime beforeTime = LocalDateTime.now().minusMinutes(5);
        List<PreviewTask> tasksToRetry = taskRepository.findTasksForRetry(
            TaskStatus.FAILED, MAX_RETRIES, beforeTime
        );
        
        log.info("找到 {} 个需要重试的任务", tasksToRetry.size());
        
        for (PreviewTask task : tasksToRetry) {
            task.setStatus(TaskStatus.PENDING);
            taskRepository.save(task);
            executeTaskAsync(task.getId());
        }
    }
    
    /**
     * 定时清理过期任务
     */
    @Override
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void cleanupExpiredTasks() {
        log.info("开始清理过期任务");
        
        LocalDateTime beforeTime = LocalDateTime.now().minusDays(CACHE_DAYS);
        taskRepository.deleteExpiredTasks(TaskStatus.COMPLETED, beforeTime);
        
        log.info("过期任务清理完成");
    }
    
    /**
     * 检查缓存是否有效
     */
    private boolean isCacheValid(PreviewTask task) {
        if (task.getCreateTime() == null) {
            return false;
        }
        LocalDateTime expiryTime = task.getCreateTime().plusDays(CACHE_DAYS);
        return LocalDateTime.now().isBefore(expiryTime);
    }
    
    /**
     * 获取包包图片URL
     */
    private String getBagImageUrl(Product product, String color, String size) {
        // 尝试从商品的colors字段中获取对应颜色的图片
        String imageUrl = product.getImageUrl();
        
        // TODO: 如果商品有多个颜色/尺寸的图片，需要从colors/sizes字段中解析
        // 当前简化实现，直接返回商品的默认图片URL
        
        return imageUrl;
    }
    
    /**
     * 生成提示词
     */
    private String generatePrompt() {
        return "将图一作为印花图案自然地放置在图二包包的中心区域，" +
               "保持包包原有的材质、光影和立体效果，" +
               "印花大小为包包可视区域的30%左右，" +
               "印花位置居中偏上，" +
               "整体效果真实自然";
    }
}

