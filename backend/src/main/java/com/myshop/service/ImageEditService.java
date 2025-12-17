package com.myshop.service;

import com.myshop.dto.ImageEditRequest;
import com.myshop.dto.ImageEditResponse;

import java.util.concurrent.CompletableFuture;

/**
 * 图像编辑服务接口
 * 使用通义万相图像编辑API进行图片合成
 */
public interface ImageEditService {
    
    /**
     * 合成图片：将印花图片（图一）合成到包包底图（图二）上
     * 
     * @param request 图像编辑请求
     * @return 异步返回图像编辑响应
     */
    CompletableFuture<ImageEditResponse> compositeImage(ImageEditRequest request);
}

