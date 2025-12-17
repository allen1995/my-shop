# Epic 12: 迁移到 Spring Alibaba AI 框架

本Epic将现有的AI图片生成服务从直接使用DashScope SDK迁移到Spring Alibaba AI框架，提升代码可维护性、扩展性和可测试性，便于后续支持多模型切换和功能扩展。

## 背景

当前实现直接使用DashScope SDK，代码耦合度高，扩展性有限。迁移到Spring Alibaba AI框架后：
1. 统一AI模型抽象，便于切换不同服务商
2. 配置化管理，支持多模型配置
3. 更好的测试支持
4. 便于后续扩展（支持DALL·E、Stable Diffusion等）

## Story 12.1: Spring Alibaba AI 依赖和配置

As a developer,
I want to add Spring Alibaba AI dependencies and configuration,
so that I can use the unified AI framework.

**Acceptance Criteria:**
1. Spring Alibaba AI依赖已添加到项目
2. 配置文件已更新，支持AI模型配置
3. 支持多模型配置（DashScope、DALL·E、Stable Diffusion等）
4. API Key配置支持环境变量
5. 配置验证通过

## Story 12.2: 重构文生图服务

As a developer,
I want to refactor the text-to-image service to use Spring Alibaba AI,
so that the code is more maintainable and extensible.

**Acceptance Criteria:**
1. 使用Spring Alibaba AI的ImageModel接口
2. 文生图功能正常工作
3. 保持与原有API接口兼容
4. 错误处理完善
5. 日志记录完整

## Story 12.3: 重构图生图服务

As a developer,
I want to refactor the image-to-image service to use Spring Alibaba AI,
so that it follows the same pattern as text-to-image.

**Acceptance Criteria:**
1. 使用Spring Alibaba AI框架（如果支持）
2. 或保持DashScope SDK实现，但封装为统一接口
3. 图生图功能正常工作
4. 保持与原有API接口兼容
5. 错误处理完善

## Story 12.4: 多模型支持实现

As a developer,
I want to support multiple AI models,
so that we can switch models based on requirements.

**Acceptance Criteria:**
1. 支持DashScope（阿里云百炼）
2. 支持DALL·E 3（如果Spring AI支持）
3. 支持Stable Diffusion（如果Spring AI支持）
4. 实现模型选择策略（配置化）
5. 支持模型降级（主模型失败时使用备用模型）

## Story 12.5: 迁移测试和验证

As a developer,
I want to thoroughly test the migration,
so that the functionality remains intact.

**Acceptance Criteria:**
1. 单元测试覆盖所有AI生成方法
2. 集成测试验证API接口
3. 性能测试验证响应时间
4. 错误场景测试
5. 与原有功能对比测试（功能一致性）

## 技术实现要点

### Spring Alibaba AI 集成
- 添加spring-cloud-alibaba-ai依赖
- 配置ImageModel Bean
- 使用@Autowired注入ImageModel

### 模型抽象
- 使用ImageModel接口统一抽象
- 支持多模型配置
- 实现模型选择策略

### 兼容性保证
- 保持原有API接口不变
- 保持响应格式一致
- 保持错误处理逻辑

### 迁移策略
- 渐进式迁移：先迁移文生图，再迁移图生图
- 并行运行：新旧实现并存，逐步切换
- 充分测试：确保功能一致性

## 依赖关系

- 依赖Epic 2（AI图片生成核心功能）
- 依赖Story 2.1（DashScope API集成）
- 依赖Story 2.3（文生图API接口实现）

## 优先级

**P1 - 重要功能，尽快实现**

提升代码可维护性和扩展性，为后续功能扩展打下基础。

## 风险评估

1. **功能风险**：迁移后功能可能不一致
   - 缓解：充分测试，保持API兼容

2. **性能风险**：新框架可能影响性能
   - 缓解：性能测试，优化配置

3. **兼容性风险**：Spring Alibaba AI可能不支持所有功能
   - 缓解：评估功能覆盖，必要时保留部分SDK实现



