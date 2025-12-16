# Mini-Test 全功能测试使用指南

## 快速开始

### 1. 环境准备

#### 启动后端服务
```bash
cd backend
mvn spring-boot:run
```
确保后端运行在 `http://localhost:8080/api`

#### 启动微信开发者工具并配置自动化

1. **打开微信开发者工具**
   - 打开项目：`frontend/unpackage/dist/dev/mp-weixin`
   - 确保项目已编译并运行

2. **启用自动化测试**
   - 设置 -> 安全设置 -> 开启服务端口
   - 默认端口：9420
   - 确保"不校验合法域名"已勾选（开发环境）

3. **验证连接**
   - 在终端运行：`telnet localhost 9420`（应该能连接）

### 2. 安装 Mini-Test

```bash
pip install minium
```

### 3. 运行测试

#### 方式一：使用提供的测试脚本

```bash
cd docs/qa
python mini-test-automation-script.py
```

#### 方式二：使用 Mini-Test CLI

```bash
# 连接小程序
minium connect ws://127.0.0.1:9420

# 运行测试
minium test mini-test-automation-script.py
```

## 测试覆盖范围

### ✅ 已实现的测试用例

1. **用户认证功能**
   - TC-001: 微信登录
   - TC-002: 登录状态保持

2. **首页功能**
   - TC-003: 首页加载
   - TC-004: TabBar 导航

3. **作品集管理**
   - TC-005: 作品列表
   - TC-006: 作品分类筛选
   - TC-007: 作品详情

4. **购物车功能**
   - TC-008: 购物车页面

5. **订单管理**
   - TC-009: 订单列表

6. **个人中心**
   - TC-010: 个人中心页面
   - TC-011: 地址管理

### 📋 待扩展的测试用例

参考 `mini-test-test-plan.md` 中的完整测试计划，包括：

- AI 图片生成功能测试
- 作品标签和收藏功能测试
- 购物车完整流程测试
- 订单创建和支付流程测试
- 地址管理完整 CRUD 测试
- 分享功能测试

## 测试报告

测试执行完成后，会在 `docs/qa/` 目录下生成测试报告：

- JSON 格式：`mini-test-report-YYYYMMDD-HHMMSS.json`
- 包含测试结果摘要和详细结果

## 常见问题

### Q1: 连接失败 "Failed connecting to ws://127.0.0.1:9420"

**解决方案：**
1. 确认微信开发者工具已启动
2. 确认项目已打开并运行
3. 确认已启用服务端口（设置 -> 安全设置）
4. 尝试重启微信开发者工具

### Q2: 找不到页面元素

**解决方案：**
1. 检查页面路径是否正确
2. 使用 `app.get_visible_text()` 查看页面内容
3. 使用 `app.get_elements()` 查找元素
4. 调整等待时间 `wait_for_page_load()`

### Q3: 测试执行缓慢

**解决方案：**
1. 减少不必要的等待时间
2. 使用更精确的元素选择器
3. 优化测试用例顺序

### Q4: 需要测试真实数据

**解决方案：**
1. 确保后端服务运行
2. 确保数据库有测试数据
3. 可以先手动创建一些测试数据

## 测试最佳实践

1. **测试前准备**
   - 清理小程序缓存
   - 确保后端服务正常运行
   - 准备测试数据

2. **测试执行**
   - 按功能模块分组测试
   - 记录测试过程中的截图
   - 及时记录问题

3. **测试后处理**
   - 查看测试报告
   - 分析失败原因
   - 修复问题后重新测试

## 扩展测试脚本

可以根据实际需求扩展测试脚本：

```python
# 添加新的测试方法
def test_012_custom_feature(self):
    """TC-012: 自定义功能测试"""
    try:
        # 测试步骤
        self.app.navigate_to("/pages/custom/page")
        self.wait_for_page_load()
        
        # 验证
        # ...
        
        self.log_test("TC-012 自定义功能", "PASS")
        return True
    except Exception as e:
        self.log_test("TC-012 自定义功能", "ERROR", str(e))
        return False

# 在 run_all_tests() 中调用
def run_all_tests(self):
    # ...
    self.test_012_custom_feature()
```

## 参考文档

- [Mini-Test 官方文档](https://minitest.weixin.qq.com/)
- [测试计划文档](./mini-test-test-plan.md)
- [测试脚本源码](./mini-test-automation-script.py)

