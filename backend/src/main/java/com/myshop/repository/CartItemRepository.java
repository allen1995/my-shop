package com.myshop.repository;

import com.myshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserIdOrderByCreateTimeDesc(Long userId);
    Optional<CartItem> findByUserIdAndWorkIdAndProductIdAndColorAndSize(
            Long userId, Long workId, Long productId, String color, String size);
    boolean existsByIdAndUserId(Long id, Long userId);
}


