# AI印花电商小程序 - test 项目

## 项目说明

这是从 `frontend` 项目迁移过来的标准 HBuilderX uni-app 项目。

## 项目结构

```
test/
├── pages/          # 页面文件（直接在根目录）
├── api/            # API 接口
├── store/          # Pinia 状态管理
├── utils/          # 工具函数
├── static/         # 静态资源
├── App.vue         # 应用入口
├── main.js         # 入口文件（已配置 Pinia）
├── manifest.json   # 应用配置
├── pages.json      # 页面配置
└── package.json    # 依赖配置
```

## 重要提示

✅ **项目结构已调整**：所有文件都在根目录，没有 `src` 目录，符合 HBuilderX 标准结构。

✅ **依赖已配置**：`package.json` 已创建，包含所有必要的依赖。

## 使用步骤

### 1. 安装依赖

```bash
cd test
npm install --legacy-peer-deps
```

### 2. 在 HBuilderX 中打开项目

1. 打开 HBuilderX
2. 文件 -> 打开目录
3. 选择 `test` 目录
4. 等待项目识别完成

### 3. 运行项目

在 HBuilderX 中：
- 运行 -> 运行到小程序模拟器 -> 微信开发者工具

或者使用命令行：
```bash
npm run dev:mp-weixin
```

## 已迁移的内容

- ✅ 所有页面文件（pages/）
- ✅ API 接口（api/）
- ✅ 状态管理（store/）
- ✅ 工具函数（utils/）
- ✅ 静态资源（static/）
- ✅ 配置文件（manifest.json, pages.json）
- ✅ App.vue 和 main.js（已配置 Pinia）

## 注意事项

1. **TabBar 图标**：需要在 `static/tabbar/` 目录下添加图标文件
2. **微信小程序 AppID**：在 `manifest.json` 的 `mp-weixin.appid` 中配置
3. **API 地址**：在 `utils/request.js` 中配置后端 API 地址

## 依赖说明

项目使用以下主要依赖：
- `vue`: ^3.4.21
- `pinia`: ^2.1.7
- `@dcloudio/vite-plugin-uni`: 3.0.0-alpha-3000020210521001
- `vite`: ^5.0.0

所有依赖已配置在 `package.json` 中，使用 `--legacy-peer-deps` 安装以避免版本冲突。


