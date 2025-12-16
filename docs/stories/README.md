# Stories 目录说明

## 概述

本目录用于存放从Epic中提取的独立Story文件。每个Story文件对应PRD中的一个用户故事。

## Story文件命名规范

根据BMAD工作流，Story文件命名格式为：
```
{epic_num}.{story_num}.{story_title_short}.md
```

例如：
- `1.1.backend-project-init.md` - Epic 1, Story 1.1: 后端项目初始化
- `2.3.text-to-image-api.md` - Epic 2, Story 2.3: 文生图API接口实现

## Story文件结构

每个Story文件应包含以下内容：

```markdown
# Story {epic_num}.{story_num}: {story_title}

## Status
- Draft / Approved / InProgress / Review / Done

## Story
As a {role},
I want {action},
so that {benefit}

## Acceptance Criteria
1. ...
2. ...
...

## Tasks / Subtasks
- [ ] Task 1 (AC: #)
  - [ ] Subtask 1.1
- [ ] Task 2 (AC: #)

## Dev Notes
### Relevant Source Tree
- backend/src/main/java/com/myshop/...

### Testing
- Test file location: ...
- Test standards: ...

## Change Log
| Date | Version | Description | Author |
|------|---------|-------------|--------|
```

## 当前状态

**注意**：由于项目已经完成基础开发，Stories文件主要用于：
1. 开发跟踪和进度管理
2. 测试用例编写参考
3. 代码审查检查清单

所有Stories的详细内容已经在PRD文档的各个Epic文件中定义。如果需要生成独立的Story文件，可以使用以下方式：

### 方式1：手动创建
根据PRD中的Story定义，手动创建对应的Story文件。

### 方式2：使用BMAD工具
如果配置了BMAD工具，可以使用：
```
*create-story
```
命令来交互式创建Story文件。

### 方式3：批量生成脚本
可以编写脚本从PRD文档中提取所有Stories并生成文件。

## Story统计

根据PRD文档统计：

- **Epic 1**: 6个Stories
- **Epic 2**: 7个Stories
- **Epic 3**: 7个Stories
- **Epic 4**: 5个Stories
- **Epic 5**: 14个Stories
- **Epic 6**: 3个Stories
- **Epic 7**: 3个Stories
- **Epic 8**: 4个Stories
- **Epic 9**: 5个Stories
- **Epic 10**: 2个Stories
- **Epic 11**: 2个Stories 🆕
- **Epic 12**: 3个Stories 🆕

**总计**: 61个Stories

## 建议

对于已完成基础开发的项目，建议：
1. 优先为P0功能的Stories创建文件（Epic 1-5）
2. 在开发新功能时，先创建对应的Story文件
3. 使用Story文件跟踪开发进度和测试状态


