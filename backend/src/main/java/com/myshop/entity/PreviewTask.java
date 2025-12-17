package com.myshop.entity;

import com.myshop.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 预览任务实体
 * 用于管理图像编辑预览生成任务
 */
@Entity
@Table(name = "preview_tasks", 
       indexes = {
           @Index(name = "idx_user_work_product", columnList = "userId,workId,productId,color,size"),
           @Index(name = "idx_status", columnList = "status"),
           @Index(name = "idx_create_time", columnList = "createTime")
       })
@EntityListeners(AuditingEntityListener.class)
@Data
public class PreviewTask {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户ID
     */
    @Column(nullable = false)
    private Long userId;
    
    /**
     * 作品ID
     */
    @Column(nullable = false)
    private Long workId;
    
    /**
     * 商品ID
     */
    @Column(nullable = false)
    private Long productId;
    
    /**
     * 颜色
     */
    @Column(length = 50)
    private String color;
    
    /**
     * 尺寸
     */
    @Column(length = 50)
    private String size;
    
    /**
     * 任务状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;
    
    /**
     * 结果URL
     */
    @Column(length = 500)
    private String resultUrl;
    
    /**
     * 错误信息
     */
    @Column(length = 1000)
    private String errorMessage;
    
    /**
     * 重试次数
     */
    @Column(nullable = false)
    private Integer retryCount = 0;
    
    /**
     * 缓存键（用于快速查找）
     */
    @Column(length = 200)
    private String cacheKey;
    
    /**
     * 创建时间
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateTime;
}

