package com.myshop.controller;

import com.myshop.dto.ApiResponse;
import com.myshop.entity.Order;
import com.myshop.entity.User;
import com.myshop.entity.Work;
import com.myshop.repository.OrderRepository;
import com.myshop.repository.UserRepository;
import com.myshop.repository.WorkRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final WorkRepository workRepository;
    
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getProfile(HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<User>> updateProfile(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            // 更新昵称
            if (request.containsKey("nickName")) {
                String nickName = (String) request.get("nickName");
                if (nickName != null && nickName.length() > 100) {
                    return ResponseEntity.ok(ApiResponse.error("昵称长度不能超过100字符"));
                }
                user.setNickName(nickName);
            }
            
            // 更新头像
            if (request.containsKey("avatarUrl")) {
                String avatarUrl = (String) request.get("avatarUrl");
                if (avatarUrl != null && avatarUrl.length() > 500) {
                    return ResponseEntity.ok(ApiResponse.error("头像URL长度不能超过500字符"));
                }
                user.setAvatarUrl(avatarUrl);
            }
            
            // 更新手机号
            if (request.containsKey("phone")) {
                String phone = (String) request.get("phone");
                if (phone != null) {
                    // 简单的手机号验证（11位数字）
                    if (!phone.matches("^1[3-9]\\d{9}$")) {
                        return ResponseEntity.ok(ApiResponse.error("手机号格式不正确"));
                    }
                    if (phone.length() > 20) {
                        return ResponseEntity.ok(ApiResponse.error("手机号长度不能超过20字符"));
                    }
                }
                user.setPhone(phone);
            }
            
            user = userRepository.save(user);
            
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("更新失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics(HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            
            Map<String, Object> statistics = new HashMap<>();
            
            // 订单统计
            long totalOrders = orderRepository.countByUserId(userId);
            long pendingPaymentOrders = orderRepository.countByUserIdAndStatus(userId, Order.OrderStatus.PENDING_PAYMENT);
            long shippedOrders = orderRepository.countByUserIdAndStatus(userId, Order.OrderStatus.SHIPPED);
            long completedOrders = orderRepository.countByUserIdAndStatus(userId, Order.OrderStatus.COMPLETED);
            
            Map<String, Object> orderStats = new HashMap<>();
            orderStats.put("total", totalOrders);
            orderStats.put("pendingPayment", pendingPaymentOrders);
            orderStats.put("shipped", shippedOrders);
            orderStats.put("completed", completedOrders);
            statistics.put("orders", orderStats);
            
            // 作品统计
            long totalWorks = workRepository.countByUserId(userId);
            long favoriteWorks = workRepository.countByUserIdAndIsFavorite(userId, true);
            
            Map<String, Object> workStats = new HashMap<>();
            workStats.put("total", totalWorks);
            workStats.put("favorites", favoriteWorks);
            statistics.put("works", workStats);
            
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("查询统计失败: " + e.getMessage()));
        }
    }
}

