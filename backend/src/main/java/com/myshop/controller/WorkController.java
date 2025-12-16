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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/works")
@RequiredArgsConstructor
public class WorkController {
    
    private final WorkRepository workRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
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
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Boolean favorite,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            Pageable pageable = PageRequest.of(page, size);
            
            Page<Work> works;
            if (favorite != null && favorite) {
                works = workRepository.findByUserIdAndIsFavoriteOrderByCreateTimeDesc(userId, true, pageable);
            } else if (tag != null && !tag.trim().isEmpty()) {
                works = workRepository.findByUserIdAndTag(userId, tag, pageable);
            } else if (category != null && !category.trim().isEmpty()) {
                works = workRepository.findByUserIdAndCategory(userId, category, pageable);
            } else {
                works = workRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
            }
            
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
    
    @PutMapping("/{workId}/category")
    public ResponseEntity<ApiResponse<Work>> updateCategory(
            @PathVariable Long workId,
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(401, "用户未登录"));
            }
            
            // 处理category，支持null值（清除分类）
            Object categoryObj = request.get("category");
            String category = null;
            if (categoryObj != null) {
                String categoryStr = categoryObj.toString().trim();
                category = categoryStr.isEmpty() ? null : categoryStr;
            }
            
            Work work = workRepository.findById(workId)
                    .filter(w -> w.getUserId().equals(userId))
                    .orElseThrow(() -> new RuntimeException("作品不存在"));
            
            work.setCategory(category);
            work = workRepository.save(work);
            
            return ResponseEntity.ok(ApiResponse.success(work));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.error("更新分类失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{workId}/tags")
    public ResponseEntity<ApiResponse<Work>> updateTags(
            @PathVariable Long workId,
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            @SuppressWarnings("unchecked")
            List<String> tags = (List<String>) request.get("tags");
            
            Work work = workRepository.findById(workId)
                    .filter(w -> w.getUserId().equals(userId))
                    .orElseThrow(() -> new RuntimeException("作品不存在"));
            
            String tagsJson = tags != null ? objectMapper.writeValueAsString(tags) : "[]";
            work.setTags(tagsJson);
            work = workRepository.save(work);
            
            return ResponseEntity.ok(ApiResponse.success(work));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("更新标签失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{workId}/tags")
    public ResponseEntity<ApiResponse<Work>> addTag(
            @PathVariable Long workId,
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            String tag = (String) request.get("tag");
            
            if (tag == null || tag.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("标签不能为空"));
            }
            
            Work work = workRepository.findById(workId)
                    .filter(w -> w.getUserId().equals(userId))
                    .orElseThrow(() -> new RuntimeException("作品不存在"));
            
            List<String> tags = getTagsList(work.getTags());
            if (!tags.contains(tag)) {
                tags.add(tag);
                work.setTags(objectMapper.writeValueAsString(tags));
                work = workRepository.save(work);
            }
            
            return ResponseEntity.ok(ApiResponse.success(work));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("添加标签失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{workId}/tags/{tag}")
    public ResponseEntity<ApiResponse<Work>> removeTag(
            @PathVariable Long workId,
            @PathVariable String tag,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            
            Work work = workRepository.findById(workId)
                    .filter(w -> w.getUserId().equals(userId))
                    .orElseThrow(() -> new RuntimeException("作品不存在"));
            
            List<String> tags = getTagsList(work.getTags());
            tags.remove(tag);
            work.setTags(objectMapper.writeValueAsString(tags));
            work = workRepository.save(work);
            
            return ResponseEntity.ok(ApiResponse.success(work));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("删除标签失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{workId}/favorite")
    public ResponseEntity<ApiResponse<Work>> toggleFavorite(
            @PathVariable Long workId,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            
            Work work = workRepository.findById(workId)
                    .filter(w -> w.getUserId().equals(userId))
                    .orElseThrow(() -> new RuntimeException("作品不存在"));
            
            work.setIsFavorite(!work.getIsFavorite());
            work = workRepository.save(work);
            
            return ResponseEntity.ok(ApiResponse.success(work));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("切换收藏状态失败: " + e.getMessage()));
        }
    }
    
    private List<String> getTagsList(String tagsJson) {
        try {
            if (tagsJson == null || tagsJson.trim().isEmpty()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}


