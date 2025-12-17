-- 添加印花图和预览图字段到购物车项表
ALTER TABLE cart_items ADD COLUMN work_image_url VARCHAR(500) COMMENT '印花图URL';

-- 添加印花图字段到订单项表（预览图字段已存在）
ALTER TABLE order_items ADD COLUMN work_image_url VARCHAR(500) COMMENT '印花图URL';

-- 更新订单项表的预览图字段注释
ALTER TABLE order_items MODIFY COLUMN preview_image_url VARCHAR(500) COMMENT '预览图URL（印花+包包合成图）';

-- 更新购物车项表的预览图字段注释
ALTER TABLE cart_items MODIFY COLUMN preview_image_url VARCHAR(500) COMMENT '预览图URL（印花+包包合成图）';

