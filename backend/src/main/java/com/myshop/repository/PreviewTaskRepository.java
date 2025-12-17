package com.myshop.repository;

import com.myshop.entity.PreviewTask;
import com.myshop.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 预览任务Repository
 */
@Repository
public interface PreviewTaskRepository extends JpaRepository<PreviewTask, Long> {
    
    /**
     * 根据缓存键查找最新的已完成任务
     */
    Optional<PreviewTask> findFirstByCacheKeyAndStatusOrderByCreateTimeDesc(
        String cacheKey, TaskStatus status
    );
    
    /**
     * 根据缓存键查找处理中的任务
     */
    Optional<PreviewTask> findFirstByCacheKeyAndStatusInOrderByCreateTimeDesc(
        String cacheKey, List<TaskStatus> statuses
    );
    
    /**
     * 根据用户ID查询任务列表
     */
    List<PreviewTask> findByUserIdOrderByCreateTimeDesc(Long userId);
    
    /**
     * 查询需要重试的失败任务
     */
    @Query("SELECT t FROM PreviewTask t WHERE t.status = :status " +
           "AND t.retryCount < :maxRetries " +
           "AND t.updateTime < :beforeTime")
    List<PreviewTask> findTasksForRetry(
        @Param("status") TaskStatus status,
        @Param("maxRetries") int maxRetries,
        @Param("beforeTime") LocalDateTime beforeTime
    );
    
    /**
     * 删除过期的已完成任务
     */
    @Query("DELETE FROM PreviewTask t WHERE t.status = :status " +
           "AND t.createTime < :beforeTime")
    void deleteExpiredTasks(
        @Param("status") TaskStatus status,
        @Param("beforeTime") LocalDateTime beforeTime
    );
    
    /**
     * 根据作品ID和商品ID查找最新的已完成任务
     */
    Optional<PreviewTask> findFirstByWorkIdAndProductIdAndColorAndSizeAndStatusOrderByCreateTimeDesc(
        Long workId, Long productId, String color, String size, TaskStatus status
    );
}

