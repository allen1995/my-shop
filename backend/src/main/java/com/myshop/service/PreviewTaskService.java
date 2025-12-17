package com.myshop.service;

import com.myshop.entity.PreviewTask;

/**
 * 预览任务服务接口
 */
public interface PreviewTaskService {
    
    /**
     * 创建预览任务
     * 
     * @param userId 用户ID
     * @param workId 作品ID
     * @param productId 商品ID
     * @param color 颜色
     * @param size 尺寸
     * @return 预览任务
     */
    PreviewTask createTask(Long userId, Long workId, Long productId, String color, String size);
    
    /**
     * 查询任务状态
     * 
     * @param taskId 任务ID
     * @return 预览任务
     */
    PreviewTask getTask(Long taskId);
    
    /**
     * 执行任务
     * 
     * @param taskId 任务ID
     */
    void executeTask(Long taskId);
    
    /**
     * 重试失败的任务
     */
    void retryFailedTasks();
    
    /**
     * 清理过期任务
     */
    void cleanupExpiredTasks();
}

