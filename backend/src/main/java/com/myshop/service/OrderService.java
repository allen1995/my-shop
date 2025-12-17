package com.myshop.service;

import com.myshop.entity.*;
import com.myshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    
    @Transactional
    public Order createOrder(Long userId, List<Long> cartItemIds, Long addressId) {
        // 验证地址存在且属于当前用户
        if (!addressRepository.existsByIdAndUserId(addressId, userId)) {
            throw new RuntimeException("地址不存在或不属于当前用户");
        }
        
        // 获取购物车项
        List<CartItem> cartItems = cartItemRepository.findAllById(cartItemIds);
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("购物车项不存在");
        }
        
        // 验证购物车项属于当前用户
        cartItems.forEach(item -> {
            if (!item.getUserId().equals(userId)) {
                throw new RuntimeException("购物车项不属于当前用户");
            }
        });
        
        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setStatus(Order.OrderStatus.PENDING_PAYMENT);
        order.setPaymentStatus(Order.PaymentStatus.UNPAID);
        
        // 计算总价
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("商品不存在"));
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId()); // 会在保存后设置
            orderItem.setWorkId(cartItem.getWorkId());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setColor(cartItem.getColor());
            orderItem.setSize(cartItem.getSize());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getBasePrice());
            orderItem.setWorkImageUrl(cartItem.getWorkImageUrl());  // 印花图URL
            orderItem.setPreviewImageUrl(cartItem.getPreviewImageUrl());  // 预览图URL
            
            totalAmount = totalAmount.add(product.getBasePrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItems.add(orderItem);
        }
        
        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);
        
        // 保存订单项
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(order.getId());
            orderItemRepository.save(orderItem);
        }
        
        // 刷新订单以加载订单项
        order = orderRepository.findById(order.getId()).orElse(order);
        if (order.getItems() != null) {
            order.getItems().size(); // 触发懒加载
        }
        
        // 删除购物车项
        cartItemRepository.deleteAll(cartItems);
        
        return order;
    }
}


