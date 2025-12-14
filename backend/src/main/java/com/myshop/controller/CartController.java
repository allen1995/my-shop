package com.myshop.controller;

import com.myshop.dto.ApiResponse;
import com.myshop.entity.CartItem;
import com.myshop.repository.CartItemRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartItemRepository cartItemRepository;
    
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartItem>> addToCart(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            Long workId = Long.valueOf(request.get("workId").toString());
            Long productId = Long.valueOf(request.get("productId").toString());
            String color = (String) request.get("color");
            String size = (String) request.get("size");
            Integer quantity = request.get("quantity") != null ? 
                    Integer.valueOf(request.get("quantity").toString()) : 1;
            String previewImageUrl = (String) request.get("previewImageUrl");
            
            // 检查是否已存在相同的购物车项
            CartItem existingItem = cartItemRepository
                    .findByUserIdAndWorkIdAndProductIdAndColorAndSize(userId, workId, productId, color, size)
                    .orElse(null);
            
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                existingItem = cartItemRepository.save(existingItem);
                return ResponseEntity.ok(ApiResponse.success(existingItem));
            }
            
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setWorkId(workId);
            cartItem.setProductId(productId);
            cartItem.setColor(color);
            cartItem.setSize(size);
            cartItem.setQuantity(quantity);
            cartItem.setPreviewImageUrl(previewImageUrl);
            
            cartItem = cartItemRepository.save(cartItem);
            
            return ResponseEntity.ok(ApiResponse.success(cartItem));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("添加到购物车失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<CartItem>>> getCartItems(HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            List<CartItem> items = cartItemRepository.findByUserIdOrderByCreateTimeDesc(userId);
            return ResponseEntity.ok(ApiResponse.success(items));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartItem>> updateCartItem(
            @PathVariable Long itemId,
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            CartItem cartItem = cartItemRepository.findById(itemId)
                    .filter(item -> item.getUserId().equals(userId))
                    .orElseThrow(() -> new RuntimeException("购物车项不存在"));
            
            if (request.containsKey("quantity")) {
                cartItem.setQuantity(Integer.valueOf(request.get("quantity").toString()));
            }
            if (request.containsKey("color")) {
                cartItem.setColor((String) request.get("color"));
            }
            if (request.containsKey("size")) {
                cartItem.setSize((String) request.get("size"));
            }
            
            cartItem = cartItemRepository.save(cartItem);
            
            return ResponseEntity.ok(ApiResponse.success(cartItem));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("更新失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteCartItem(
            @PathVariable Long itemId,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            
            if (!cartItemRepository.existsByIdAndUserId(itemId, userId)) {
                return ResponseEntity.ok(ApiResponse.error(404, "购物车项不存在"));
            }
            
            cartItemRepository.deleteById(itemId);
            
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("删除失败: " + e.getMessage()));
        }
    }
}


