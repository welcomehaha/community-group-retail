# 社区团购即时零售平台

## 项目概述

社区团购即时零售平台是一个面向社区零售场景的企业级前后端分离项目，提供商品管理、门店管理、订单履约、售后服务、数据分析和智能客服等能力，支持管理端、后端服务与小程序端协同运行。

项目面向以下业务场景：

- 社区团购商品统一运营
- 门店即时零售订单承接与履约
- 用户在线下单、支付、查询和售后处理
- 客服咨询、问题分流与受控业务操作
- 平台运营分析与风险审计

## 核心功能

### 管理端

- 商品管理
- 分类管理
- 组合商品管理
- 门店营业管理
- 订单管理
- 数据报表
- 风险审计
- 客服 AI 运营管理

### 小程序端

- 商品浏览
- 分类筛选
- 购物下单
- 订单支付
- 订单查询
- 售后申请
- 在线客服

### 后端服务

- 用户认证与权限控制
- 商品、分类、门店、订单业务处理
- 库存与状态流转管理
- Redis 缓存能力
- 接口统一异常处理
- 参数校验与安全控制
- WebSocket 消息能力
- 客服 AI 业务编排与审计

## 技术架构

### 管理端

- Vue 3
- TypeScript 5
- Vite
- Vue Router 4
- Pinia
- Element Plus
- Axios
- ECharts
- Sass
- Vitest
- Playwright

### 小程序端

- uni-app（Vue 3）
- Pinia
- uni-ui
- uni_modules

### 后端

- JDK 17
- Spring Boot 3.x
- Spring MVC
- Spring Security 6
- JWT
- MyBatis-Plus
- HikariCP
- Redis
- Spring Cache
- springdoc-openapi
- WebSocket

### 数据存储

- MySQL 8.4 LTS
- Redis
- Flyway

## 系统架构说明

项目采用前后端分离架构：

1. 管理端负责平台运营、商品配置、订单管理、报表分析和 AI 运营配置。
2. 小程序端负责用户侧商品浏览、下单支付、订单查询、售后和客服交互。
3. 后端负责统一业务处理、权限认证、数据访问、缓存管理和接口输出。
4. 智能客服模块基于业务数据与知识库能力，为用户提供咨询和受控动作服务。

## 项目结构

```text
E:\JavaWebshixun
├─ community-group-retail-admin/          # 管理端
├─ community-group-retail-backend/        # 后端
│  └─ community-group-retail-server/
│     ├─ community-group-retail-api/      # 启动与接口模块
│     ├─ community-group-retail-common/   # 公共模块
│     └─ community-group-retail-pojo/     # 实体与数据对象模块
├─ community-group-retail-miniprogram/    # 小程序端
├─ community-group-retail-deploy/         # 部署相关资源
└─ sql/                                   # 数据库脚本
```

## 运行环境

### 基础环境

- JDK 17
- Maven 3.9+
- Node.js 18 LTS 或以上
- npm 9+ 或 pnpm 8+
- MySQL 8.4
- Redis 7+

### 推荐开发环境

- IntelliJ IDEA
- VS Code
- HBuilderX / 微信开发者工具

## 快速启动

### 1. 数据库准备

1. 安装 MySQL 8.4 和 Redis。
2. 创建业务数据库。
3. 执行 `sql/` 目录下初始化脚本。
4. 按实际环境配置环境变量，不要把数据库密码、JWT 密钥、微信密钥直接写进仓库文件。

### 环境变量配置

- [√] 已接入环境变量方案
- 管理端接口地址配置文件：`community-group-retail-admin/.env.development`
- 后端敏感配置文件：`community-group-retail-backend/community-group-retail-server/local-secrets.yml`
- 后端模板文件：`community-group-retail-backend/community-group-retail-server/local-secrets.example.yml`
- `application.yml` 只保留占位符，真实值从本地敏感配置文件读取

### 2. 启动后端服务

```powershell
# 进入后端目录
cd E:\JavaWebshixun\community-group-retail-backend\community-group-retail-server

# 编译并启动服务
mvn -pl community-group-retail-api -am spring-boot:run
```

默认服务地址：

```text
http://localhost:8080
```

### 3. 启动管理端

```powershell
# 进入管理端目录
cd E:\JavaWebshixun\community-group-retail-admin

# 安装依赖
npm install

# 启动开发环境
npm run dev
```

管理端开发环境默认通过 `http://localhost:8080` 访问后端接口。

### 4. 启动小程序端

```text
E:\JavaWebshixun\community-group-retail-miniprogram
```

使用 HBuilderX 或微信开发者工具导入对应工程后运行。

## 部署说明

### 管理端构建

```powershell
# 进入管理端目录
cd E:\JavaWebshixun\community-group-retail-admin

# 构建生产包
npm run build
```

### 后端构建

```powershell
# 进入后端目录
cd E:\JavaWebshixun\community-group-retail-backend\community-group-retail-server

# 打包后端服务
mvn -pl community-group-retail-api -am clean package -DskipTests
```

## 开发规范

### 前端约定

- 使用 Vue 3 `setup` 语法开发页面和组件
- 模块接口统一放在 `api` 目录下单独维护
- 前端请求统一访问 `http://localhost:8080`
- 表单提交前必须进行参数校验和提示处理
- 页面权限、按钮权限和路由权限应统一管理

### 后端约定

- 统一使用分层结构组织 Controller、Service、Mapper 和 Domain
- 接口参数必须进行后端校验
- 统一返回结果结构和异常处理机制
- 涉及权限的数据操作必须进行身份校验和资源归属校验
- 涉及高风险操作时必须保留日志、审计记录和必要的审批信息

### 安全要求

- 防止 SQL 注入、越权访问和未授权操作
- 防止前端 XSS 输入透传
- 敏感接口必须进行认证与权限控制
- 关键业务操作必须保留审计日志

## 测试建议

### 管理端

- 使用 Vitest 进行单元测试
- 使用 Playwright 进行关键页面流程测试

### 后端

- 使用单元测试和接口测试覆盖核心业务逻辑
- 重点验证参数校验、权限校验、异常分支和状态流转

## 项目说明

本项目适合作为社区团购即时零售领域的企业级项目实践示例，也适合用于 Spring Boot 3、Vue 3、uni-app 和智能客服业务集成的综合开发场景。
