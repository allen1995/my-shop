package com.myshop.controller;

import com.myshop.dto.ApiResponse;
import com.myshop.entity.Work;
import com.myshop.repository.WorkRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/works")
@RequiredArgsConstructor
public class WorkController {
    
    private final WorkRepository workRepository;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Work>> createWork(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            
            Work work = new Work();
            work.setUserId(userId);
            work.setTitle((String) request.get("title"));
            work.setDescription((String) request.get("description"));
            work.setImageUrl((String) request.get("imageUrl"));
            
            work = workRepository.save(work);
            
            return ResponseEntity.ok(ApiResponse.success(work));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("保存失败: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Work>>> getWorks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            Pageable pageable = PageRequest.of(page, size);
            Page<Work> works = workRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(works));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{workId}")
    public ResponseEntity<ApiResponse<Work>> getWork(
            @PathVariable Long workId,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            Work work = workRepository.findById(workId)
                    .filter(w -> w.getUserId().equals(userId))
                    .orElseThrow(() -> new RuntimeException("作品不存在"));
            
            return ResponseEntity.ok(ApiResponse.success(work));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{workId}")
    public ResponseEntity<ApiResponse<Void>> deleteWork(
            @PathVariable Long workId,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            
            if (!workRepository.existsByIdAndUserId(workId, userId)) {
                return ResponseEntity.ok(ApiResponse.error(404, "作品不存在"));
            }
            
            workRepository.deleteById(workId);
            
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("删除失败: " + e.getMessage()));
        }
    }
}


