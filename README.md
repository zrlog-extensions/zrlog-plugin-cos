# zrlog-plugin-cos

ZrLog 腾讯云 COS 插件。将文章附件、生成资源和模板静态资源上传到腾讯云 COS，可配置私有 Bucket 用于备份文件等私有资源。

## 功能

- 配置 COS AccessKey、SecretKey、Bucket、访问域名和地域
- 上传文章附件和生成资源到 COS
- 同步主题静态资源
- 可配置私有 Bucket
- 可开启静态缓存 HTML 同步和 HTTPS 访问配置

## 技术栈

- 后端：Java、Maven、ZrLog 插件协议
- 前端：React、TypeScript、Ant Design
- 前端路径：`src/main/frontend/`

## 开发与构建

### 前端

前端构建产物输出到 `src/main/resources/templates`，由 Maven 打包进插件 jar。

```bash
cd src/main/frontend
yarn install
yarn dev
yarn build
```

### 后端

```bash
mvn clean package -DskipTests
```
