package com.myshop.controller;

import com.myshop.dto.ApiResponse;
import com.myshop.entity.Address;
import com.myshop.repository.AddressRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {
    
    private final AddressRepository addressRepository;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Address>> createAddress(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            log.info("创建地址 - userId: {}, request: {}", userId, request);
            
            if (userId == null) {
                log.error("创建地址失败 - userId为空，可能未登录或token无效");
                return ResponseEntity.ok(ApiResponse.error(401, "用户未登录"));
            }
            
            // 参数验证
            String receiverName = (String) request.get("receiverName");
            String receiverPhone = (String) request.get("receiverPhone");
            String province = (String) request.get("province");
            String city = (String) request.get("city");
            String district = (String) request.get("district");
            String detailAddress = (String) request.get("detailAddress");
            Boolean isDefault = request.get("isDefault") != null ? 
                    Boolean.valueOf(request.get("isDefault").toString()) : false;
            
            if (receiverName == null || receiverName.trim().isEmpty()) {
                log.warn("创建地址失败 - 收货人姓名不能为空");
                return ResponseEntity.ok(ApiResponse.error("收货人姓名不能为空"));
            }
            if (receiverPhone == null || !receiverPhone.matches("^1[3-9]\\d{9}$")) {
                log.warn("创建地址失败 - 手机号格式不正确: {}", receiverPhone);
                return ResponseEntity.ok(ApiResponse.error("手机号格式不正确"));
            }
            if (province == null || province.trim().isEmpty()) {
                log.warn("创建地址失败 - 省份不能为空");
                return ResponseEntity.ok(ApiResponse.error("省份不能为空"));
            }
            if (city == null || city.trim().isEmpty()) {
                log.warn("创建地址失败 - 城市不能为空");
                return ResponseEntity.ok(ApiResponse.error("城市不能为空"));
            }
            if (district == null || district.trim().isEmpty()) {
                log.warn("创建地址失败 - 区县不能为空");
                return ResponseEntity.ok(ApiResponse.error("区县不能为空"));
            }
            if (detailAddress == null || detailAddress.trim().isEmpty()) {
                log.warn("创建地址失败 - 详细地址不能为空");
                return ResponseEntity.ok(ApiResponse.error("详细地址不能为空"));
            }
            
            Address address = new Address();
            address.setUserId(userId);
            address.setReceiverName(receiverName);
            address.setReceiverPhone(receiverPhone);
            address.setProvince(province);
            address.setCity(city);
            address.setDistrict(district);
            address.setDetailAddress(detailAddress);
            
            // 如果设置为默认地址，清除其他默认地址
            if (isDefault) {
                log.info("设置为默认地址，清除用户{}的其他默认地址", userId);
                addressRepository.clearDefaultAddress(userId);
                address.setIsDefault(true);
            } else {
                address.setIsDefault(false);
            }
            
            address = addressRepository.save(address);
            log.info("地址创建成功 - addressId: {}, userId: {}", address.getId(), userId);
            
            return ResponseEntity.ok(ApiResponse.success(address));
        } catch (Exception e) {
            log.error("创建地址失败 - 异常: ", e);
            return ResponseEntity.ok(ApiResponse.error("创建地址失败: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Address>>> getAddresses(HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            List<Address> addresses = addressRepository.findByUserIdOrderByIsDefaultDescCreateTimeDesc(userId);
            
            return ResponseEntity.ok(ApiResponse.success(addresses));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Address>> getAddress(
            @PathVariable Long addressId,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            Address address = addressRepository.findByIdAndUserId(addressId, userId)
                    .orElseThrow(() -> new RuntimeException("地址不存在"));
            
            return ResponseEntity.ok(ApiResponse.success(address));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Address>> updateAddress(
            @PathVariable Long addressId,
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            log.info("更新地址 - addressId: {}, userId: {}, request: {}", addressId, userId, request);
            
            if (userId == null) {
                log.error("更新地址失败 - userId为空");
                return ResponseEntity.ok(ApiResponse.error(401, "用户未登录"));
            }
            
            Address address = addressRepository.findByIdAndUserId(addressId, userId)
                    .orElseThrow(() -> {
                        log.warn("更新地址失败 - 地址不存在或不属于当前用户: addressId={}, userId={}", addressId, userId);
                        return new RuntimeException("地址不存在");
                    });
            
            // 更新字段
            if (request.containsKey("receiverName")) {
                String receiverName = (String) request.get("receiverName");
                if (receiverName != null && !receiverName.trim().isEmpty()) {
                    address.setReceiverName(receiverName);
                }
            }
            if (request.containsKey("receiverPhone")) {
                String receiverPhone = (String) request.get("receiverPhone");
                if (receiverPhone != null && receiverPhone.matches("^1[3-9]\\d{9}$")) {
                    address.setReceiverPhone(receiverPhone);
                } else if (receiverPhone != null) {
                    log.warn("更新地址失败 - 手机号格式不正确: {}", receiverPhone);
                    return ResponseEntity.ok(ApiResponse.error("手机号格式不正确"));
                }
            }
            if (request.containsKey("province")) {
                address.setProvince((String) request.get("province"));
            }
            if (request.containsKey("city")) {
                address.setCity((String) request.get("city"));
            }
            if (request.containsKey("district")) {
                address.setDistrict((String) request.get("district"));
            }
            if (request.containsKey("detailAddress")) {
                address.setDetailAddress((String) request.get("detailAddress"));
            }
            
            // 处理默认地址设置
            if (request.containsKey("isDefault")) {
                Boolean isDefault = Boolean.valueOf(request.get("isDefault").toString());
                if (isDefault && !address.getIsDefault()) {
                    log.info("更新默认地址，清除用户{}的其他默认地址", userId);
                    addressRepository.clearDefaultAddress(userId);
                    address.setIsDefault(true);
                } else if (!isDefault) {
                    address.setIsDefault(false);
                }
            }
            
            address = addressRepository.save(address);
            log.info("地址更新成功 - addressId: {}, userId: {}", address.getId(), userId);
            
            return ResponseEntity.ok(ApiResponse.success(address));
        } catch (Exception e) {
            log.error("更新地址失败 - addressId: {}, 异常: ", addressId, e);
            return ResponseEntity.ok(ApiResponse.error("更新失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable Long addressId,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            
            if (!addressRepository.existsByIdAndUserId(addressId, userId)) {
                return ResponseEntity.ok(ApiResponse.error(404, "地址不存在"));
            }
            
            addressRepository.deleteById(addressId);
            
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("删除失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{addressId}/default")
    public ResponseEntity<ApiResponse<Address>> setDefaultAddress(
            @PathVariable Long addressId,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            Address address = addressRepository.findByIdAndUserId(addressId, userId)
                    .orElseThrow(() -> new RuntimeException("地址不存在"));
            
            // 清除其他默认地址
            addressRepository.clearDefaultAddress(userId);
            
            // 设置当前地址为默认
            address.setIsDefault(true);
            address = addressRepository.save(address);
            
            return ResponseEntity.ok(ApiResponse.success(address));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("设置默认地址失败: " + e.getMessage()));
        }
    }
}

