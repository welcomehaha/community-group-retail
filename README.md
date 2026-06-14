# 社区团购即时零售平台

## 1. 项目简介

本项目是一个面向社区场景的即时零售平台，覆盖管理端、后端服务和小程序端三类核心应用，支持商品运营、组合商品、订单履约、地址管理、在线客服、AI 能力接入和权限控制。

适用场景：

- 社区团购商品运营
- 门店即时零售履约
- 用户下单、查询、复购
- 平台运营分析
- 智能客服与客服 AI

## 2. 仓库现状说明

### 2.1 目标技术栈

项目方案要求的目标技术栈如下：

- 管理端：Vue 3 + TypeScript 5 + Vite + Vue Router 4 + Pinia + Element Plus
- 小程序端：uni-app（Vue 3）+ Pinia + uni-ui
- 后端：JDK 17 + Spring Boot 3.x + Spring Security 6 + MyBatis-Plus + Redis + WebSocket
- 数据层：MySQL 8.4 LTS + Flyway

### 2.2 当前仓库实际情况

当前仓库仍处于渐进式重构阶段，**部分模块尚未完全切换到目标技术栈**。本地部署时请以仓库中的实际代码为准：

- 管理端当前实际为：`Vue 2 + TypeScript + Vue CLI + Element UI + Vuex`
- 小程序源码工程为：`uni-app` 工程，目录位于 `community-group-retail-miniprogram-develop/project-rjwm-weixin-uniapp-develop-wsy`
- 后端当前已切换到：`JDK 17 + Spring Boot 3.5.0`

这意味着：

1. README 中会同时说明“目标架构”和“当前可运行方式”。
2. 本地启动、构建和排障时，必须以仓库现有脚本和配置为准。
3. 如果后续完成 Vue 3 / Vite 升级，需要同步更新本 README。

## 3. 核心能力

### 3.1 管理端

- 商品管理
- 商品分类管理
- 组合商品管理
- 员工与权限管理
- 订单管理
- 交易分析 / 运营看板
- AI 运营相关页面

### 3.2 小程序端

- 首页商品浏览
- 分类切换
- 购物车与下单
- 地址管理
- 历史订单
- 最近选购
- 在线客服 / AI 客服

### 3.3 后端服务

- 管理端与用户端认证
- RBAC 权限控制
- 商品、分类、组合商品、订单业务
- Redis 缓存
- 统一返回与异常处理
- OpenAPI / Knife4j 文档
- WebSocket
- AI 客服能力接入

## 4. 架构说明

```text
管理端  <---->  后端服务  <---->  MySQL / Redis / 第三方能力
   |               |
   |               +---->  WebSocket / AI 客服 / OpenAPI
   |
   +---->  运营配置、商品管理、订单管理、权限管理

小程序端  <---->  后端服务  <---->  订单、地址、支付、客服
```

### 4.1 模块职责

| 模块 | 职责 |
| --- | --- |
| 管理端 | 平台运营、商品配置、订单处理、权限控制、数据分析 |
| 后端 | 统一业务处理、鉴权、缓存、数据访问、异常封装 |
| 小程序端 | 用户浏览、下单、支付、地址管理、客服交互 |
| 数据库 | 持久化业务数据 |
| Redis | 缓存、会话、热点数据 |

## 5. 项目结构

```text
Project
├─ community-group-retail-admin/                                  # 管理端
├─ community-group-retail-backend/
│  └─ community-group-retail-server/                              # Spring Boot 3 多模块后端
│     ├─ community-group-retail-api/                              # 启动模块 / Controller / 配置
│     ├─ community-group-retail-common/                           # 公共能力
│     └─ community-group-retail-pojo/                             # DTO / VO / Entity
├─ community-group-retail-miniprogram-develop/
│  └─ project-rjwm-weixin-uniapp-develop-wsy/                     # 小程序源码工程
├─ community-group-retail-deploy/                                 # 部署相关资源
├─ sql/                                                           # SQL 脚本
└─ README.md
```

## 6. 环境要求

### 5.1 基础软件

- JDK 17
- Maven 3.9+
- Node.js 18 LTS 或以上
- npm 9+
- MySQL 8.x
- Redis 7.x
- HBuilderX
- 微信开发者工具

### 5.2 推荐开发工具

- IntelliJ IDEA
- VS Code
- Navicat / DataGrip
- Postman / Apifox

## 7. 本地部署总览

建议按以下顺序启动：

1. MySQL
2. Redis
3. 后端服务
4. 管理端
5. 小程序源码工程

默认端口约定：

- 后端：`http://localhost:8080`
- 管理端：`http://localhost:8888`
- 小程序：通过 HBuilderX / 微信开发者工具运行，接口统一请求 `http://localhost:8080`

## 8. 数据库与缓存准备

### 7.1 创建数据库

建议创建数据库：

```sql
CREATE DATABASE community_retail DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 7.2 执行 SQL 脚本

请根据项目需求执行 `sql/` 目录中的脚本，当前仓库可见脚本包括：

```text
sql/update.sql
```

推荐执行顺序：

1. `update.sql`

### 7.3 Redis 准备

确保本地 Redis 可访问，例如：

```text
host: localhost
port: 6379
database: 10
```

## 9. 后端本地部署

### 8.1 配置本地敏感信息

后端目录：

```text
community-group-retail-backend/community-group-retail-server
```

仓库已提供模板文件：

- [local-secrets.example.yml](/E:/JavaWebshixun/community-group-retail-backend/community-group-retail-server/local-secrets.example.yml)

请复制为本地文件：

```text
community-group-retail-backend/community-group-retail-server/local-secrets.yml
```

至少需要配置：

- 数据库连接
- Redis 连接
- JWT 密钥
- 微信支付参数（没有就先留空）
- AI 配置（没有就先关闭或留空）

重点字段示例：

```yaml
SERVER_PORT: 8080
SPRING_PROFILES_ACTIVE: dev

DB_HOST: localhost
DB_PORT: 3306
DB_NAME: community_retail
DB_USERNAME: root
DB_PASSWORD: your_password

REDIS_HOST: localhost
REDIS_PORT: 6379
REDIS_DATABASE: 10

JWT_ADMIN_SECRET: replace_with_32_chars_admin_secret
JWT_USER_SECRET: replace_with_32_chars_user_secret
```

### 8.2 启动后端

```powershell
cd \community-group-retail-backend\community-group-retail-server
mvn -pl community-group-retail-api -am spring-boot:run
```

启动成功后访问：

- 应用地址：`http://localhost:8080`
- OpenAPI / Knife4j：请根据后端配置访问 `/doc.html` 或 `/swagger-ui.html`

### 8.3 后端打包

```powershell
cd \community-group-retail-backend\community-group-retail-server
mvn -pl community-group-retail-api -am clean package -DskipTests
```

## 10. 管理端本地部署

### 9.1 管理端实际技术说明

虽然项目目标是 Vue 3 + Vite，但当前仓库中的管理端实际仍然使用：

- Vue 2
- Vue CLI
- TypeScript
- Element UI
- Vuex

因此启动方式应使用当前脚本，而不是 Vite 命令。

### 9.2 环境配置

请检查：

- [community-group-retail-admin/.env.development](/E:/JavaWebshixun/community-group-retail-admin/.env.development)

确保其中后端地址指向本地服务，例如：

```text
http://localhost:8080
```

### 9.3 安装依赖

```powershell
cd \community-group-retail-admin
npm install
```

### 9.4 启动管理端

```powershell
cd \community-group-retail-admin
npm run serve
```

默认访问地址：

```text
http://localhost:8888
```

### 9.5 管理端构建

```powershell
cd \community-group-retail-admin
npm run build
```

## 11. 小程序本地部署

### 10.1 源码工程与产物目录说明

请以**源码工程**作为开发入口：

- 源码工程：`community-group-retail-miniprogram-develop/project-rjwm-weixin-uniapp-develop-wsy`
- 编译产物：`community-group-retail-miniprogram`

开发约束：

1. 页面、接口、样式、资源统一改源码工程。
2. `unpackage/dist` 和 `community-group-retail-miniprogram` 属于产物目录，不作为长期维护入口。
3. 允许临时同步静态资源用于联调，但正式改动必须回到源码。

### 10.2 小程序接口地址

当前小程序统一请求地址配置在：

- [utils/env.js](/E:/JavaWebshixun/community-group-retail-miniprogram-develop/project-rjwm-weixin-uniapp-develop-wsy/utils/env.js)

当前值为：

```js
export const baseUrl = 'http://localhost:8080'
```

### 10.3 启动方式

#### 方式一：HBuilderX

1. 打开 HBuilderX
2. 导入目录：

```text
\community-group-retail-miniprogram-develop\project-rjwm-weixin-uniapp-develop-wsy
```

3. 运行到微信开发者工具

#### 方式二：微信开发者工具

如果已经生成对应微信小程序产物，也可以导入：

```text
\community-group-retail-miniprogram
```

但日常开发仍建议从 uni-app 源码工程启动和编译。

### 10.4 小程序常见检查项

- 微信开发者工具已开启服务端口
- 本地后端 `8080` 已启动
- 地址管理、订单、AI 客服页面在 `pages.json` 中已注册
- 如更新了静态资源，请清缓存后重新编译

## 12. 首次联调建议流程

### 11.1 后端联通性检查

1. 启动 MySQL、Redis
2. 启动后端
3. 浏览器访问：

```text
http://localhost:8080
```

或访问接口文档页，确认服务已启动

### 11.2 管理端联调

1. 启动管理端
2. 打开 `http://localhost:8888`
3. 验证登录、商品列表、分类、组合商品、订单页是否可访问

### 11.3 小程序联调

1. 用 HBuilderX 或微信开发者工具启动小程序
2. 验证首页商品加载
3. 验证地址管理
4. 验证下单页
5. 验证历史订单
6. 验证在线客服

## 13. 常用命令

### 12.1 后端

```powershell
cd \community-group-retail-backend\community-group-retail-server

# 启动
mvn -pl community-group-retail-api -am spring-boot:run

# 打包
mvn -pl community-group-retail-api -am clean package -DskipTests
```

### 12.2 管理端

```powershell
cd \community-group-retail-admin

# 安装依赖
npm install

# 启动开发环境
npm run serve

# 构建
npm run build

# 单元测试
npm run test:unit
```

### 12.3 小程序

小程序当前主要通过 HBuilderX / 微信开发者工具运行，`package.json` 仅声明了基础依赖，不提供完整 CLI 构建命令，因此请不要假设可以直接通过 `npm run dev` 启动。

## 14. 配置与安全要求

### 13.1 不应提交到仓库的内容

- `local-secrets.yml`
- 数据库真实密码
- JWT 生产密钥
- 微信支付证书与私钥
- OpenAI / 第三方 AI Key
- 本地 IDE 缓存
- `node_modules`
- `unpackage/dist`

### 13.2 应提交到仓库的内容

- 源码
- 文档
- `.gitignore`
- 环境变量模板
- SQL 脚本
- 静态资源

## 15. 测试建议

### 14.1 后端

- 参数校验
- 权限校验
- 订单状态流转
- Redis 缓存行为
- AI 接口降级与异常处理

### 14.2 管理端

- 登录态校验
- 商品 / 分类 / 组合商品操作
- 权限按钮显示与隐藏
- 订单查询与详情

### 14.3 小程序端

- 首页加载
- 地址管理
- 下单链路
- 历史订单
- AI 客服
- 弱网和异常提示

## 16. 常见问题

### 15.1 管理端启动命令为什么不是 `npm run dev`

因为当前仓库管理端实际使用 `Vue CLI`，脚本名是：

```text
npm run serve
```

### 15.2 小程序为什么不能直接以 `community-group-retail-miniprogram` 为主开发目录

因为它是编译产物目录，不是长期维护的源码入口。正式改动应该回到：

```text
community-group-retail-miniprogram-develop/project-rjwm-weixin-uniapp-develop-wsy
```

### 15.3 可以直接改编译后的产物代码吗

技术上可以，工程上不建议。  
原因是重新编译后会覆盖，且容易造成源码与产物不一致。

## 17. 本地排错清单

### 17.1 后端启动失败

- 检查 `local-secrets.yml` 是否存在
- 检查 MySQL 是否可连接
- 检查 Redis 是否可连接
- 检查 `SERVER_PORT` 是否被占用

### 17.2 管理端启动失败

- 检查 `npm install` 是否完成
- 检查 `community-group-retail-admin/.env.development`
- 检查后端地址是否指向 `http://localhost:8080`
- 检查端口 `8888` 是否被占用

### 17.3 小程序无法请求接口

- 检查后端是否已启动
- 检查 `utils/env.js` 中 `baseUrl`
- 检查微信开发者工具是否重新编译
- 检查 `pages.json` 中页面是否注册

### 17.4 图片或资源不显示

- 检查 `static` 目录图片是否存在
- 清空微信开发者工具缓存后重新编译
- 检查路径是否仍指向旧产物目录

## 18. 发布流程

### 18.1 开发分支

1. 修改源码
2. 本地联调
3. 提交代码

### 18.2 提测分支

1. 构建管理端
2. 构建后端
3. 生成小程序产物
4. 执行回归测试

### 18.3 生产发布

1. 备份数据库
2. 发布后端
3. 发布管理端静态资源
4. 发布小程序产物
5. 验证核心链路

## 19. 后续维护建议

1. 管理端完成 Vue 3 + Vite 升级后，第一时间更新 README。
2. 小程序产物目录应尽量只保留发布用途，不作为长期开发目录。
3. 所有新增模块都应补充本地部署说明、环境变量说明和接口依赖说明。
4. AI、支付、OSS 这类外部集成功能应长期保持“模板配置 + 本地私有配置”模式。

## 20. 许可证与说明

本仓库当前用于社区团购即时零售平台的学习、演示与工程实践。  
如需对外发布、商用或多人协作，请进一步补充：

- LICENSE
- 版本发布说明
- 数据初始化说明
- 默认账号说明
- 部署架构图
