package com.myshop.service;

import com.myshop.entity.PreviewTask;
import com.myshop.entity.Product;
import com.myshop.entity.Work;
import com.myshop.enums.TaskStatus;
import com.myshop.repository.PreviewTaskRepository;
import com.myshop.repository.ProductRepository;
import com.myshop.repository.WorkRepository;
import com.myshop.service.impl.PreviewTaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 预览任务服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class PreviewTaskServiceTest {
    
    @Mock
    private PreviewTaskRepository taskRepository;
    
    @Mock
    private ImageEditService imageEditService;
    
    @Mock
    private WorkRepository workRepository;
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private PreviewTaskServiceImpl previewTaskService;
    
    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_WORK_ID = 100L;
    private static final Long TEST_PRODUCT_ID = 200L;
    private static final String TEST_COLOR = "black";
    private static final String TEST_SIZE = "M";
    
    private PreviewTask testTask;
    private Work testWork;
    private Product testProduct;
    
    @BeforeEach
    void setUp() {
        // 设置测试任务
        testTask = new PreviewTask();
        testTask.setId(1L);
        testTask.setUserId(TEST_USER_ID);
        testTask.setWorkId(TEST_WORK_ID);
        testTask.setProductId(TEST_PRODUCT_ID);
        testTask.setColor(TEST_COLOR);
        testTask.setSize(TEST_SIZE);
        testTask.setStatus(TaskStatus.PENDING);
        testTask.setRetryCount(0);
        testTask.setCreateTime(LocalDateTime.now());
        testTask.setUpdateTime(LocalDateTime.now());
        
        // 设置测试作品
        testWork = new Work();
        testWork.setId(TEST_WORK_ID);
        testWork.setImageUrl("https://example.com/work.jpg");
        
        // 设置测试商品
        testProduct = new Product();
        testProduct.setId(TEST_PRODUCT_ID);
        testProduct.setImageUrl("https://example.com/bag.jpg");
    }
    
    @Test
    void testCreateTask_WithNoCache_ShouldCreateNewTask() {
        // Given
        when(taskRepository.findFirstByCacheKeyAndStatusOrderByCreateTimeDesc(anyString(), any()))
            .thenReturn(Optional.empty());
        when(taskRepository.findFirstByCacheKeyAndStatusInOrderByCreateTimeDesc(anyString(), any()))
            .thenReturn(Optional.empty());
        when(taskRepository.save(any(PreviewTask.class))).thenReturn(testTask);
        
        // When
        PreviewTask result = previewTaskService.createTask(
            TEST_USER_ID, TEST_WORK_ID, TEST_PRODUCT_ID, TEST_COLOR, TEST_SIZE
        );
        
        // Then
        assertNotNull(result);
        verify(taskRepository, times(1)).save(any(PreviewTask.class));
    }
    
    @Test
    void testCreateTask_WithCachedCompletedTask_ShouldReturnCache() {
        // Given
        PreviewTask cachedTask = new PreviewTask();
        cachedTask.setId(999L);
        cachedTask.setStatus(TaskStatus.COMPLETED);
        cachedTask.setResultUrl("https://oss.example.com/cached.jpg");
        cachedTask.setCreateTime(LocalDateTime.now());
        
        when(taskRepository.findFirstByCacheKeyAndStatusOrderByCreateTimeDesc(anyString(), eq(TaskStatus.COMPLETED)))
            .thenReturn(Optional.of(cachedTask));
        
        // When
        PreviewTask result = previewTaskService.createTask(
            TEST_USER_ID, TEST_WORK_ID, TEST_PRODUCT_ID, TEST_COLOR, TEST_SIZE
        );
        
        // Then
        assertNotNull(result);
        assertEquals(999L, result.getId());
        assertEquals(TaskStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getResultUrl());
        
        // 验证没有创建新任务
        verify(taskRepository, never()).save(any(PreviewTask.class));
    }
    
    @Test
    void testCreateTask_WithProcessingTask_ShouldReturnExisting() {
        // Given
        PreviewTask processingTask = new PreviewTask();
        processingTask.setId(888L);
        processingTask.setStatus(TaskStatus.PROCESSING);
        
        when(taskRepository.findFirstByCacheKeyAndStatusOrderByCreateTimeDesc(anyString(), eq(TaskStatus.COMPLETED)))
            .thenReturn(Optional.empty());
        when(taskRepository.findFirstByCacheKeyAndStatusInOrderByCreateTimeDesc(anyString(), any()))
            .thenReturn(Optional.of(processingTask));
        
        // When
        PreviewTask result = previewTaskService.createTask(
            TEST_USER_ID, TEST_WORK_ID, TEST_PRODUCT_ID, TEST_COLOR, TEST_SIZE
        );
        
        // Then
        assertNotNull(result);
        assertEquals(888L, result.getId());
        assertEquals(TaskStatus.PROCESSING, result.getStatus());
        
        // 验证没有创建新任务
        verify(taskRepository, never()).save(any(PreviewTask.class));
    }
    
    @Test
    void testGetTask_WithValidId_ShouldReturnTask() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        
        // When
        PreviewTask result = previewTaskService.getTask(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(TEST_USER_ID, result.getUserId());
    }
    
    @Test
    void testGetTask_WithInvalidId_ShouldThrowException() {
        // Given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            previewTaskService.getTask(999L);
        });
    }
    
    @Test
    void testTaskFields_ShouldBeCorrect() {
        // Given & When
        PreviewTask task = new PreviewTask();
        task.setUserId(TEST_USER_ID);
        task.setWorkId(TEST_WORK_ID);
        task.setProductId(TEST_PRODUCT_ID);
        task.setColor(TEST_COLOR);
        task.setSize(TEST_SIZE);
        task.setStatus(TaskStatus.PENDING);
        
        // Then
        assertEquals(TEST_USER_ID, task.getUserId());
        assertEquals(TEST_WORK_ID, task.getWorkId());
        assertEquals(TEST_PRODUCT_ID, task.getProductId());
        assertEquals(TEST_COLOR, task.getColor());
        assertEquals(TEST_SIZE, task.getSize());
        assertEquals(TaskStatus.PENDING, task.getStatus());
    }
}

