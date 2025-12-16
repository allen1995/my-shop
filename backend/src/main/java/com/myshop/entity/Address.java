package com.myshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@Data
public class Address {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false, length = 50)
    private String receiverName;
    
    @Column(nullable = false, length = 20)
    private String receiverPhone;
    
    @Column(nullable = false, length = 100)
    private String province;
    
    @Column(nullable = false, length = 100)
    private String city;
    
    @Column(nullable = false, length = 100)
    private String district;
    
    @Column(nullable = false, length = 200)
    private String detailAddress;
    
    @Column(nullable = false)
    private Boolean isDefault = false;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    @Column(nullable = false)
    private LocalDateTime updateTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}

