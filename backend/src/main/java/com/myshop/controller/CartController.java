package com.myshop.controller;

import com.myshop.dto.ApiResponse;
import com.myshop.entity.CartItem;
import com.myshop.entity.Product;
import com.myshop.repository.CartItemRepository;
import com.myshop.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    
    @Data
    public static class CartItemDTO {
        private Long id;
        private Long userId;
        private Long workId;
        private Long productId;
        private String color;
        private String size;
        private Integer quantity;
        private String previewImageUrl;
        private BigDecimal price; // 商品价格
        private String productName; // 商品名称
    }
    
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartItemDTO>> addToCart(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(401, "用户未登录"));
            }
            
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
            
            CartItem cartItem;
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                cartItem = cartItemRepository.save(existingItem);
            } else {
                cartItem = new CartItem();
                cartItem.setUserId(userId);
                cartItem.setWorkId(workId);
                cartItem.setProductId(productId);
                cartItem.setColor(color);
                cartItem.setSize(size);
                cartItem.setQuantity(quantity);
                cartItem.setPreviewImageUrl(previewImageUrl);
                cartItem = cartItemRepository.save(cartItem);
            }
            
            // 转换为 DTO
            CartItemDTO dto = new CartItemDTO();
            dto.setId(cartItem.getId());
            dto.setUserId(cartItem.getUserId());
            dto.setWorkId(cartItem.getWorkId());
            dto.setProductId(cartItem.getProductId());
            dto.setColor(cartItem.getColor());
            dto.setSize(cartItem.getSize());
            dto.setQuantity(cartItem.getQuantity());
            dto.setPreviewImageUrl(cartItem.getPreviewImageUrl());
            
            // 查询商品信息
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                dto.setPrice(product.getBasePrice());
                dto.setProductName(product.getName());
            } else {
                dto.setPrice(BigDecimal.valueOf(299));
                dto.setProductName("定制包包");
            }
            
            return ResponseEntity.ok(ApiResponse.success(dto));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("添加到购物车失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<CartItemDTO>>> getCartItems(HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(401, "用户未登录"));
            }
            
            List<CartItem> items = cartItemRepository.findByUserIdOrderByCreateTimeDesc(userId);
            
            // 转换为 DTO，关联查询商品信息
            List<CartItemDTO> dtos = items.stream().map(item -> {
                CartItemDTO dto = new CartItemDTO();
                dto.setId(item.getId());
                dto.setUserId(item.getUserId());
                dto.setWorkId(item.getWorkId());
                dto.setProductId(item.getProductId());
                dto.setColor(item.getColor());
                dto.setSize(item.getSize());
                dto.setQuantity(item.getQuantity());
                dto.setPreviewImageUrl(item.getPreviewImageUrl());
                
                // 查询商品信息
                Product product = productRepository.findById(item.getProductId()).orElse(null);
                if (product != null) {
                    dto.setPrice(product.getBasePrice());
                    dto.setProductName(product.getName());
                } else {
                    // 如果商品不存在，设置默认价格
                    dto.setPrice(BigDecimal.valueOf(299));
                    dto.setProductName("定制包包");
                }
                
                return dto;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(dtos));
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


