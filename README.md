# Personal CRM Intelligent Contact Management Platform

一个面向个人联系人管理场景的智能 CRM Web 应用。项目将联系人资料、关系标签、事项提醒、活动轨迹、头像上传、数据看板与轻量 Contact Agent 能力整合到同一个前后端分离系统中，并补齐了面向课设答辩与作品集展示的单机部署资产。

## Highlights

- 联系人全生命周期管理：新增、查询、编辑、黑名单收口、逻辑删除
- 标签与关系维护：标签 CRUD、联系人多标签绑定、按标签筛选
- 事项提醒闭环：新增、完成、取消、逾期识别、今日待办快捷操作
- 活动轨迹流：联系人详情页可追溯展示资料、标签、事项相关操作
- 数据看板：联系人总数、待办统计、趋势图、性别分布、关系维护健康度
- 头像上传：联系人头像与用户头像双链路、本地文件持久化、失败兜底
- Contact Agent：支持联系人/事项查询，以及“创建事项”写操作预确认与二次确认执行
- Agent-ready 部署：仓库内提供 Docker Compose、Nginx、环境变量模板与部署文档，可由 Agent 拉取后执行部署

## Tech Stack

- Frontend: Vue 3, Vite, TypeScript, Pinia, Vue Router, Element Plus, ECharts
- Backend: Spring Boot 3.5, Java 17, MyBatis-Plus, JWT
- Data: MySQL 8, Redis
- AI / Integrations: OpenAI-compatible API, Resend, QWeather
- Deployment: Docker Compose, Nginx

## Architecture

```text
Browser
  -> Nginx
  -> Vue static assets
  -> Spring Boot backend
  -> MySQL / Redis / uploads
  -> External services (OpenAI-compatible API, Resend, QWeather)
```

更完整的结构化设计见：

- [项目总览](D:/project/Personal%20CRM%20Intelligent%20Contact%20Management%20Platform/docs/1-requirements/project_overview.md)
- [系统架构](D:/project/Personal%20CRM%20Intelligent%20Contact%20Management%20Platform/docs/2-designs/architecture.md)
- [接口契约](D:/project/Personal%20CRM%20Intelligent%20Contact%20Management%20Platform/docs/2-designs/api_contract.md)
- [数据库设计](D:/project/Personal%20CRM%20Intelligent%20Contact%20Management%20Platform/docs/2-designs/db_schema.md)

## Quick Start

### Prerequisites

- JDK 17+
- Maven 3.9+
- Node.js 20+
- MySQL 8
- Redis 7+

### Local Development

1. 启动依赖服务并准备本地数据库 `personal_crm`
2. 按需配置 `personal_crm_backend/src/main/resources/application-local.yaml` 或本地环境变量
3. 启动后端：

```bash
cd personal_crm_backend
mvn spring-boot:run
```

4. 启动前端：

```bash
cd personal_crm_web
npm install
npm run dev
```

默认开发访问：

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Health: `http://localhost:8080/actuator/health`

## Environment Variables

常用变量示例见 [`.env.example`](D:/project/Personal%20CRM%20Intelligent%20Contact%20Management%20Platform/.env.example)。

其中部署时至少需要确认：

- `MYSQL_DATABASE`
- `MYSQL_USER`
- `MYSQL_PASSWORD`
- `MYSQL_ROOT_PASSWORD`
- `JWT_SECRET`
- `QWEATHER_API_KEY`

可选第三方能力：

- `OPENAI_API_KEY`
- `OPENAI_BASE_URL`
- `OPENAI_MODEL`
- `RESEND_API_KEY`
- `RESEND_FROM_EMAIL`

## Docker Deployment

项目已提供单机 Docker Compose 部署基线：

- [docker-compose.yml](D:/project/Personal%20CRM%20Intelligent%20Contact%20Management%20Platform/docker-compose.yml)
- [backend Dockerfile](D:/project/Personal%20CRM%20Intelligent%20Contact%20Management%20Platform/personal_crm_backend/Dockerfile)
- [Nginx config](D:/project/Personal%20CRM%20Intelligent%20Contact%20Management%20Platform/deploy/nginx.conf)

最小部署流程：

```bash
cp .env.example .env.deploy
npm --prefix personal_crm_web ci
npm --prefix personal_crm_web run build
docker compose --env-file .env.deploy up -d --build
```

说明：

- `mysql` 和 `redis` 仅保留在 Compose 内部网络，不直接暴露公网端口
- `backend` 仅绑定宿主机 `127.0.0.1:8080`
- `nginx` 对外暴露 `80`
- 上传目录挂载到 `deploy/data/uploads`

## Agent Deployment Flow

本项目支持“本地定稿部署文件 -> 提交仓库 -> 服务器 Agent 拉取并部署”的交付模式。

推荐 Agent 执行顺序：

```bash
git pull
cp .env.example .env.deploy
npm --prefix personal_crm_web ci
npm --prefix personal_crm_web run build
docker compose --env-file .env.deploy up -d --build
docker compose --env-file .env.deploy ps
curl http://localhost:8080/actuator/health
```

详细说明见：

- [部署总览](D:/project/Personal%20CRM%20Intelligent%20Contact%20Management%20Platform/docs/5-deploy/README.md)
- [环境变量说明](D:/project/Personal%20CRM%20Intelligent%20Contact%20Management%20Platform/docs/5-deploy/environment.md)
- [本地验证流程](D:/project/Personal%20CRM%20Intelligent%20Contact%20Management%20Platform/docs/5-deploy/local-validation.md)
- [Agent 部署流程](D:/project/Personal%20CRM%20Intelligent%20Contact%20Management%20Platform/docs/5-deploy/agent-deployment.md)
- [演示验收口径](D:/project/Personal%20CRM%20Intelligent%20Contact%20Management%20Platform/docs/5-deploy/demo-acceptance.md)

## Project Structure

```text
personal_crm_backend/   Spring Boot backend
personal_crm_web/       Vue 3 frontend
docs/                   requirements, designs, constraints, tasks, deploy docs
deploy/                 nginx config and deployment data mounts
prototype/              prototype assets
```

## Documentation Index

- [当前任务计划](D:/project/Personal%20CRM%20Intelligent%20Contact%20Management%20Platform/docs/4-tasks/CURRENT_PLAN.md)
- [需求规格说明](D:/project/Personal%20CRM%20Intelligent%20Contact%20Management%20Platform/docs/1-requirements/software_requirements_specification.md)
- [原始需求分析](D:/project/Personal%20CRM%20Intelligent%20Contact%20Management%20Platform/docs/Personal%20CRM%20智能联系人管理平台需求分析.md)
- [架构选型](D:/project/Personal%20CRM%20Intelligent%20Contact%20Management%20Platform/docs/Personal%20CRM%20智能联系人管理平台架构选型.md)
- [数据库设计原文](D:/project/Personal%20CRM%20Intelligent%20Contact%20Management%20Platform/docs/Personal%20CRM%20智能联系人管理平台数据库设计.md)

## Roadmap

- [x] 联系人、标签、事项、看板、上传、活动轨迹、邮箱安全闭环
- [x] Contact Agent 查询与创建事项确认链路
- [x] 单机 Docker Compose 部署资产与 Agent 部署文档
- [ ] 真实目标主机部署演练
- [ ] 可选的 CI / 镜像仓库发布链路

## License

当前仓库未声明开源许可证。如需公开发布，建议补充 `LICENSE` 文件后再对外开源。
