# 系统架构设计 (System Architecture)

## 1. 分层规则 (Layered Architecture Rules)
- **Controller / 接口层**：只负责路由转发、入参校验和统一响应封装，禁止包含核心业务逻辑。
- **Service 业务逻辑层**：处理联系人、事项、看板、上传、登录和 Agent 的业务规则，必要时声明事务。
- **Mapper 数据持久层**：负责 MyBatis-Plus CRUD 和自定义 SQL，禁止写权限、确认、流程控制类逻辑。
- **Config / Security / Interceptor**：统一管理 JWT 鉴权、当前用户上下文、跨域、异常和 MyBatis-Plus 分页等基础设施。
- **Storage / Agent**：分别封装文件存储和 Contact Agent 适配，不直接绕过已有业务 Service。

## 2. 架构图 (Architecture Diagrams)
```mermaid
flowchart TD
    Browser["Browser"] --> Frontend["Vue 3 Frontend"]
    Frontend --> Api["REST API /api/v1"]
    Api --> Backend["Spring Boot Backend"]
    Backend --> Mysql["MySQL 8"]
    Backend --> Upload["Local Upload Directory"]
    Backend --> Agent["Contact Agent Module"]
```

## 3. 模块边界 (Module Boundaries)
- 前端模块：`router`、`stores`、`api`、`views`、`components`、`styles`
- 后端模块：`controller`、`service`、`service.impl`、`mapper`、`entity`、`dto`、`vo`、`common`、`config`、`exception`、`interceptor`、`agent`、`storage`
- 当前前端仍是 Vue 默认模板页面，当前后端仅完成依赖初始化，业务代码与数据库设计待补齐。

## 4. 启动与联调基线 (Runtime Baseline)
- 后端默认端口：`8080`
- 健康检查：`/actuator/health`
- 前端开发端口：Vite 默认 `5173`
- 推荐联调方式：前端开发代理转发 `/api` 到后端，后端负责 JWT 鉴权和统一错误响应。

## 5. 原始方案索引 (Source Reference)
- 本文为结构化架构摘要。
- 若需要查看更完整的技术选型理由、部署建议和 Agent 模块边界，请回看：`docs/Personal CRM 智能联系人管理平台架构选型.md`。
