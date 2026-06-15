# Personal CRM Intelligent Contact Management Platform

个人 CRM 智能联系人管理平台，采用单仓管理，包含：

- `personal_crm_backend`：Spring Boot 3.5 + MyBatis-Plus + MySQL 8
- `personal_crm_web`：Vue 3 + Vite + TypeScript + Element Plus
- `docs`：需求、设计、约束与任务文档

## 当前开发阶段

当前处于“开发前准备完成，进入第一阶段编码”的状态，已完成：

- 项目初始化与单仓搭建
- 数据库设计与 `schema.sql`
- API 契约初版
- Harness 协作文档骨架

## 本地环境要求

- JDK 17
- Maven 3.9+
- Node.js 20+
- MySQL 8

## 后端启动准备

1. 创建本地数据库，例如 `personal_crm`
2. 按需修改 `personal_crm_backend/src/main/resources/application-dev.yaml`
3. 启动后端时会执行 `schema.sql` 和 `data.sql`

默认环境变量：

- `MYSQL_HOST=localhost`
- `MYSQL_PORT=3306`
- `MYSQL_DATABASE=personal_crm`
- `MYSQL_USER=root`
- `MYSQL_PASSWORD=123456`
- `JWT_SECRET=personal-crm-dev-secret-key-change-me`

## 前端启动

```bash
cd personal_crm_web
npm install
npm run dev
```

## 后端启动

```bash
cd personal_crm_backend
mvn spring-boot:run
```

## 关键文档

- `docs/1-requirements/software_requirements_specification.md`
- `docs/2-designs/api_contract.md`
- `docs/Personal CRM 智能联系人管理平台数据库设计.md`
- `docs/4-tasks/CURRENT_PLAN.md`
