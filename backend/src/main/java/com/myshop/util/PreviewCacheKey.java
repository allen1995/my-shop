package com.myshop.util;

/**
 * 预览缓存Key工具类
 * 用于生成预览任务的缓存键
 */
public class PreviewCacheKey {
    
    private static final String PREFIX = "preview";
    private static final String SEPARATOR = ":";
    
    /**
     * 生成缓存键
     * 
     * @param workId 作品ID
     * @param productId 商品ID
     * @param color 颜色
     * @param size 尺寸
     * @return 缓存键字符串
     */
    public static String generate(Long workId, Long productId, String color, String size) {
        StringBuilder key = new StringBuilder(PREFIX);
        key.append(SEPARATOR).append(workId);
        key.append(SEPARATOR).append(productId);
        key.append(SEPARATOR).append(color != null ? color : "default");
        key.append(SEPARATOR).append(size != null ? size : "default");
        return key.toString();
    }
    
    /**
     * 验证缓存键格式是否正确
     */
    public static boolean isValid(String cacheKey) {
        if (cacheKey == null || cacheKey.isEmpty()) {
            return false;
        }
        return cacheKey.startsWith(PREFIX + SEPARATOR);
    }
}

