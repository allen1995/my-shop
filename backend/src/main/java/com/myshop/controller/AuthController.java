package com.myshop.controller;

import com.myshop.dto.ApiResponse;
import com.myshop.dto.LoginRequest;
import com.myshop.dto.LoginResponse;
import com.myshop.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/wechat/login")
    public ResponseEntity<ApiResponse<LoginResponse>> wechatLogin(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IOException e) {
            return ResponseEntity.ok(ApiResponse.error("微信登录失败: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("登录失败: " + e.getMessage()));
        }
    }
}


