package com.myshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myshop.dto.PreviewGenerateRequest;
import com.myshop.entity.PreviewTask;
import com.myshop.enums.TaskStatus;
import com.myshop.service.PreviewTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 预览Controller单元测试
 */
@WebMvcTest(PreviewController.class)
class PreviewControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private PreviewTaskService previewTaskService;
    
    private PreviewTask testTask;
    private PreviewGenerateRequest testRequest;
    
    @BeforeEach
    void setUp() {
        testTask = new PreviewTask();
        testTask.setId(1L);
        testTask.setUserId(1L);
        testTask.setWorkId(100L);
        testTask.setProductId(200L);
        testTask.setColor("black");
        testTask.setSize("M");
        testTask.setStatus(TaskStatus.PENDING);
        testTask.setCreateTime(LocalDateTime.now());
        testTask.setUpdateTime(LocalDateTime.now());
        
        testRequest = new PreviewGenerateRequest();
        testRequest.setWorkId(100L);
        testRequest.setProductId(200L);
        testRequest.setColor("black");
        testRequest.setSize("M");
    }
    
    @Test
    void testGeneratePreview_Success() throws Exception {
        // Given
        when(previewTaskService.createTask(anyLong(), anyLong(), anyLong(), anyString(), anyString()))
            .thenReturn(testTask);
        
        // When & Then
        mockMvc.perform(post("/preview/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.taskId").value(1))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.estimatedTime").value(10));
    }
    
    @Test
    void testGeneratePreview_WithCache() throws Exception {
        // Given
        testTask.setStatus(TaskStatus.COMPLETED);
        testTask.setResultUrl("https://oss.example.com/cached.jpg");
        
        when(previewTaskService.createTask(anyLong(), anyLong(), anyLong(), anyString(), anyString()))
            .thenReturn(testTask);
        
        // When & Then
        mockMvc.perform(post("/preview/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.taskId").value(1))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.resultUrl").value("https://oss.example.com/cached.jpg"))
                .andExpect(jsonPath("$.data.estimatedTime").value(0));
    }
    
    @Test
    void testGeneratePreview_ValidationFailure() throws Exception {
        // Given
        PreviewGenerateRequest invalidRequest = new PreviewGenerateRequest();
        // workId 和 productId 为 null，应该验证失败
        
        // When & Then
        mockMvc.perform(post("/preview/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetTaskStatus_Success() throws Exception {
        // Given
        when(previewTaskService.getTask(1L)).thenReturn(testTask);
        
        // When & Then
        mockMvc.perform(get("/preview/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.taskId").value(1))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.progress").value(0));
    }
    
    @Test
    void testGetTaskStatus_NotFound() throws Exception {
        // Given
        when(previewTaskService.getTask(999L))
            .thenThrow(new RuntimeException("任务不存在"));
        
        // When & Then
        mockMvc.perform(get("/preview/tasks/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    void testGetTaskStatus_Processing() throws Exception {
        // Given
        testTask.setStatus(TaskStatus.PROCESSING);
        when(previewTaskService.getTask(1L)).thenReturn(testTask);
        
        // When & Then
        mockMvc.perform(get("/preview/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("PROCESSING"))
                .andExpect(jsonPath("$.data.progress").value(50));
    }
    
    @Test
    void testGetTaskStatus_Completed() throws Exception {
        // Given
        testTask.setStatus(TaskStatus.COMPLETED);
        testTask.setResultUrl("https://oss.example.com/result.jpg");
        when(previewTaskService.getTask(1L)).thenReturn(testTask);
        
        // When & Then
        mockMvc.perform(get("/preview/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.progress").value(100))
                .andExpect(jsonPath("$.data.resultUrl").value("https://oss.example.com/result.jpg"));
    }
    
    @Test
    void testGetTaskStatus_Failed() throws Exception {
        // Given
        testTask.setStatus(TaskStatus.FAILED);
        testTask.setErrorMessage("图像生成失败");
        when(previewTaskService.getTask(1L)).thenReturn(testTask);
        
        // When & Then
        mockMvc.perform(get("/preview/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("FAILED"))
                .andExpect(jsonPath("$.data.errorMessage").value("图像生成失败"));
    }
}

