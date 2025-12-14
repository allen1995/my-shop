package com.myshop.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WeChatAuthService {
    
    @Value("${app.wechat.app-id}")
    private String appId;
    
    @Value("${app.wechat.app-secret}")
    private String appSecret;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public WeChatSession code2Session(String code) throws IOException {
        String url = String.format(
            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
            appId, appSecret, code
        );
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(new HttpGet(url))) {
            
            JsonNode jsonNode = objectMapper.readTree(response.getEntity().getContent());
            
            if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
                throw new RuntimeException("微信登录失败: " + jsonNode.get("errmsg").asText());
            }
            
            String openId = jsonNode.get("openid").asText();
            String sessionKey = jsonNode.has("session_key") ? jsonNode.get("session_key").asText() : null;
            
            return new WeChatSession(openId, sessionKey);
        }
    }
    
    public static class WeChatSession {
        private final String openId;
        private final String sessionKey;
        
        public WeChatSession(String openId, String sessionKey) {
            this.openId = openId;
            this.sessionKey = sessionKey;
        }
        
        public String getOpenId() {
            return openId;
        }
        
        public String getSessionKey() {
            return sessionKey;
        }
    }
}


