package com.myshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long orderId;
    
    @Column(nullable = false)
    private Long workId;
    
    @Column(nullable = false)
    private Long productId;
    
    @Column(nullable = false, length = 50)
    private String color;
    
    @Column(nullable = false, length = 50)
    private String size;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(length = 500)
    private String previewImageUrl;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", insertable = false, updatable = false)
    @JsonIgnore
    private Order order;
}


