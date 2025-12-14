package com.myshop.service;

import com.myshop.dto.LoginRequest;
import com.myshop.dto.LoginResponse;
import com.myshop.entity.User;
import com.myshop.repository.UserRepository;
import com.myshop.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final WeChatAuthService weChatAuthService;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public LoginResponse login(LoginRequest request) throws IOException {
        // 调用微信API获取openId
        WeChatAuthService.WeChatSession session = weChatAuthService.code2Session(request.getCode());
        
        // 查找或创建用户
        User user = userRepository.findByOpenId(session.getOpenId())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setOpenId(session.getOpenId());
                    return userRepository.save(newUser);
                });
        
        // 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getOpenId());
        
        return new LoginResponse(token, user);
    }
}


