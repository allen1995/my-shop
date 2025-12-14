package com.myshop.controller;

import com.myshop.dto.ApiResponse;
import com.myshop.entity.Product;
import com.myshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductRepository productRepository;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getProducts() {
        List<Product> products = productRepository.findAllByOrderByCreateTimeDesc();
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        return ResponseEntity.ok(ApiResponse.success(product));
    }
}


