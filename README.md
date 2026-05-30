# zrlog-plugin-cos

ZRLog 的腾讯云对象存储（COS）插件，提供基于腾讯云的对象存储支持与配置界面。

## 技术栈

该项目的后端为一个标准的 Java Maven 工程，前端在最近经历了一次现代化的重构。

- **后端**: Java 8+, Maven plugin 规范
- **前端项目路径**: `src/main/frontend/`
- **前端主要技术**: Vue 3 (Composition API), TypeScript, Vite
- **前端 UI**: Element Plus (无冗余图标依赖纯净版)
- **包管理器**: Yarn

## 开发与构建

### 1. 前端开发环境

前端代码通过 Vite 构建，所有的静态资源输出都被重定向到后端的 resource 目录 (`src/main/resources/templates`)，以方便 Java 进行统一打包。

```bash
cd src/main/frontend
# 安装依赖
yarn install

# 启动本地开发服务预览前端 UI
yarn dev

# 将前端代码编译并打包至 src/main/resources/templates 下
yarn build
```

如果你修改了 `src/main/frontend` 目录下的 Vue 组件或逻辑，你需要运行 `yarn build` 将变更同步给后端渲染。

### 2. 后端 Maven 构建

整个插件最终以标准的 jar 形式分发。

在项目根目录下，你可以使用 Maven 常规命令来进行一键打包：

```bash
mvn clean package -DskipTests
```

打好的插件 jar 包将输出在 `target/` 目录下。

## 特性说明

- 纯粹的响应式配置前端：抛弃了老旧基于 jQuery 的方案，全面采用了 Vue 3 和 Element Plus 驱动。
- UI 最大化利用 800px 宽度容器排版，靠左侧对齐，符合主流后台配置界面的阅读体验。
- 支持 GraalVM Native Image 的编译 (依赖内置提供)。
 