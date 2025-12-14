package com.myshop.controller;

import com.myshop.dto.ApiResponse;
import com.myshop.entity.Order;
import com.myshop.repository.OrderRepository;
import com.myshop.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            @SuppressWarnings("unchecked")
            List<Integer> cartItemIds = (List<Integer>) request.get("cartItemIds");
            Long addressId = request.get("addressId") != null ? 
                    Long.valueOf(request.get("addressId").toString()) : null;
            
            List<Long> cartItemIdList = cartItemIds.stream()
                    .map(Integer::longValue)
                    .toList();
            
            Order order = orderService.createOrder(userId, cartItemIdList, addressId);
            
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("创建订单失败: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Order>>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            Pageable pageable = PageRequest.of(page, size);
            
            Page<Order> orders;
            if (status != null && !status.isEmpty()) {
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
                orders = orderRepository.findByUserIdAndStatus(userId, orderStatus, pageable);
            } else {
                orders = orderRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
            }
            
            return ResponseEntity.ok(ApiResponse.success(orders));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getOrder(
            @PathVariable Long orderId,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            Order order = orderRepository.findByIdAndUserId(orderId, userId)
                    .orElseThrow(() -> new RuntimeException("订单不存在"));
            
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }
}


