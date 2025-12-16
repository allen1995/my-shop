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
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(401, "用户未登录"));
            }
            
            // 处理cartItemIds，支持多种数据类型
            Object cartItemIdsObj = request.get("cartItemIds");
            if (cartItemIdsObj == null) {
                return ResponseEntity.ok(ApiResponse.error("购物车项ID列表不能为空"));
            }
            
            List<Long> cartItemIdList;
            if (cartItemIdsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> cartItemIds = (List<Object>) cartItemIdsObj;
                cartItemIdList = cartItemIds.stream()
                        .map(id -> {
                            if (id instanceof Number) {
                                return ((Number) id).longValue();
                            } else {
                                return Long.valueOf(id.toString());
                            }
                        })
                        .toList();
            } else {
                return ResponseEntity.ok(ApiResponse.error("购物车项ID列表格式错误"));
            }
            
            if (cartItemIdList.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("购物车项ID列表不能为空"));
            }
            
            // 处理addressId
            Object addressIdObj = request.get("addressId");
            Long addressId = null;
            if (addressIdObj != null) {
                if (addressIdObj instanceof Number) {
                    addressId = ((Number) addressIdObj).longValue();
                } else {
                    addressId = Long.valueOf(addressIdObj.toString());
                }
            }
            
            if (addressId == null) {
                return ResponseEntity.ok(ApiResponse.error("收货地址不能为空"));
            }
            
            Order order = orderService.createOrder(userId, cartItemIdList, addressId);
            
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (Exception e) {
            e.printStackTrace();
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
                try {
                    Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
                    orders = orderRepository.findByUserIdAndStatus(userId, orderStatus, pageable);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.ok(ApiResponse.error("无效的订单状态: " + status));
                }
            } else {
                orders = orderRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
            }
            
            // 确保每个订单的 items 都被正确初始化，避免序列化问题
            orders.getContent().forEach(order -> {
                if (order.getItems() != null) {
                    order.getItems().size(); // 触发懒加载
                } else {
                    order.setItems(new java.util.ArrayList<>());
                }
            });
            
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
            
            // 确保订单项被加载（由于使用LAZY加载，需要访问items来触发加载）
            if (order.getItems() != null) {
                order.getItems().size(); // 触发懒加载
            } else {
                // 如果items为null，初始化为空列表避免序列化问题
                order.setItems(new java.util.ArrayList<>());
            }
            
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(
            @PathVariable Long orderId,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            Order order = orderRepository.findByIdAndUserId(orderId, userId)
                    .orElseThrow(() -> new RuntimeException("订单不存在"));
            
            if (order.getStatus() != Order.OrderStatus.PENDING_PAYMENT) {
                return ResponseEntity.ok(ApiResponse.error("只有待支付订单可以取消"));
            }
            
            order.setStatus(Order.OrderStatus.CANCELLED);
            order = orderRepository.save(order);
            
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("取消订单失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<ApiResponse<Order>> confirmReceipt(
            @PathVariable Long orderId,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            Order order = orderRepository.findByIdAndUserId(orderId, userId)
                    .orElseThrow(() -> new RuntimeException("订单不存在"));
            
            if (order.getStatus() != Order.OrderStatus.SHIPPED) {
                return ResponseEntity.ok(ApiResponse.error("只有已发货订单可以确认收货"));
            }
            
            order.setStatus(Order.OrderStatus.COMPLETED);
            order = orderRepository.save(order);
            
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("确认收货失败: " + e.getMessage()));
        }
    }
}


