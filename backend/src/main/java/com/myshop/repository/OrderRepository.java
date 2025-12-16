package com.myshop.repository;

import com.myshop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND (:status IS NULL OR o.status = :status)")
    Page<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Order.OrderStatus status, Pageable pageable);
    
    Optional<Order> findByIdAndUserId(Long id, Long userId);
    Optional<Order> findByOrderNo(String orderNo);
    
    long countByUserId(Long userId);
    
    long countByUserIdAndStatus(Long userId, Order.OrderStatus status);
}


