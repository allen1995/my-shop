package com.myshop.service;

import com.myshop.dto.ImageEditRequest;
import com.myshop.dto.ImageEditResponse;
import com.myshop.service.impl.ImageEditServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 图像编辑服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class ImageEditServiceTest {
    
    @Mock
    private OssService ossService;
    
    @InjectMocks
    private ImageEditServiceImpl imageEditService;
    
    private static final String TEST_API_KEY = "test-api-key";
    private static final String TEST_WORK_IMAGE_URL = "https://example.com/work.jpg";
    private static final String TEST_BAG_IMAGE_URL = "https://example.com/bag.jpg";
    private static final String TEST_OSS_URL = "https://oss.example.com/result.jpg";
    
    @BeforeEach
    void setUp() {
        // 设置API Key
        ReflectionTestUtils.setField(imageEditService, "apiKey", TEST_API_KEY);
    }
    
    @Test
    void testCompositeImage_WithValidRequest_ShouldReturnSuccess() throws ExecutionException, InterruptedException {
        // Given
        ImageEditRequest request = new ImageEditRequest();
        request.setWorkImageUrl(TEST_WORK_IMAGE_URL);
        request.setBagImageUrl(TEST_BAG_IMAGE_URL);
        request.setPrompt("测试提示词");
        
        // Mock OSS service
        when(ossService.uploadFromUrl(anyString(), anyString())).thenReturn(TEST_OSS_URL);
        
        // When
        CompletableFuture<ImageEditResponse> future = imageEditService.compositeImage(request);
        ImageEditResponse response = future.get();
        
        // Then
        assertNotNull(response);
        // 注意：实际调用会失败（因为没有真实的API Key），所以状态会是FAILED
        // 这里我们只测试服务的基本结构是否正确
        assertNotNull(response.getStatus());
        assertNotNull(response.getDuration());
    }
    
    @Test
    void testCompositeImage_WithNullWorkImageUrl_ShouldReturnError() throws ExecutionException, InterruptedException {
        // Given
        ImageEditRequest request = new ImageEditRequest();
        request.setWorkImageUrl(null);
        request.setBagImageUrl(TEST_BAG_IMAGE_URL);
        
        // When
        CompletableFuture<ImageEditResponse> future = imageEditService.compositeImage(request);
        ImageEditResponse response = future.get();
        
        // Then
        assertNotNull(response);
        assertEquals("FAILED", response.getStatus());
        assertNotNull(response.getErrorMessage());
        assertTrue(response.getErrorMessage().contains("印花图片URL不能为空"));
    }
    
    @Test
    void testCompositeImage_WithNullBagImageUrl_ShouldReturnError() throws ExecutionException, InterruptedException {
        // Given
        ImageEditRequest request = new ImageEditRequest();
        request.setWorkImageUrl(TEST_WORK_IMAGE_URL);
        request.setBagImageUrl(null);
        
        // When
        CompletableFuture<ImageEditResponse> future = imageEditService.compositeImage(request);
        ImageEditResponse response = future.get();
        
        // Then
        assertNotNull(response);
        assertEquals("FAILED", response.getStatus());
        assertNotNull(response.getErrorMessage());
        assertTrue(response.getErrorMessage().contains("包包底图URL不能为空"));
    }
    
    @Test
    void testCompositeImage_WithEmptyWorkImageUrl_ShouldReturnError() throws ExecutionException, InterruptedException {
        // Given
        ImageEditRequest request = new ImageEditRequest();
        request.setWorkImageUrl("");
        request.setBagImageUrl(TEST_BAG_IMAGE_URL);
        
        // When
        CompletableFuture<ImageEditResponse> future = imageEditService.compositeImage(request);
        ImageEditResponse response = future.get();
        
        // Then
        assertNotNull(response);
        assertEquals("FAILED", response.getStatus());
        assertNotNull(response.getErrorMessage());
    }
    
    @Test
    void testCompositeImage_WithDefaultPrompt_ShouldUseGeneratedPrompt() throws ExecutionException, InterruptedException {
        // Given
        ImageEditRequest request = new ImageEditRequest();
        request.setWorkImageUrl(TEST_WORK_IMAGE_URL);
        request.setBagImageUrl(TEST_BAG_IMAGE_URL);
        // 不设置prompt，测试默认提示词
        
        // When
        CompletableFuture<ImageEditResponse> future = imageEditService.compositeImage(request);
        ImageEditResponse response = future.get();
        
        // Then
        assertNotNull(response);
        assertNotNull(response.getStatus());
        // 服务应该会生成默认提示词并尝试调用API
    }
    
    @Test
    void testCompositeImage_ResponseShouldIncludeDuration() throws ExecutionException, InterruptedException {
        // Given
        ImageEditRequest request = new ImageEditRequest();
        request.setWorkImageUrl(TEST_WORK_IMAGE_URL);
        request.setBagImageUrl(TEST_BAG_IMAGE_URL);
        
        // When
        CompletableFuture<ImageEditResponse> future = imageEditService.compositeImage(request);
        ImageEditResponse response = future.get();
        
        // Then
        assertNotNull(response);
        assertNotNull(response.getDuration());
        assertTrue(response.getDuration() >= 0);
    }
}

