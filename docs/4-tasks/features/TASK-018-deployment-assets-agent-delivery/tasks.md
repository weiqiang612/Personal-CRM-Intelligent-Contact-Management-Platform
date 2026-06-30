# TASK-018: Tasks

**Spec**: `spec.md`
**Status**: Completed

## Key decisions
- 采用单机 Docker Compose 拓扑作为课设部署基线，由 Nginx 托管前端静态资源并反向代理后端。
- 部署文件先在本地宿主机完成定稿，再由服务器上的 Agent 拉取仓库并执行构建、启动和验证。
- 本阶段不引入镜像仓库和 CI 推镜像链路，优先展示 Agent 的部署执行与诊断能力。

## Progress

- [x] T1 — 更新 `docs/2-designs/architecture.md`，补充单机部署拓扑、Nginx/Backend/MySQL/Redis/uploads 关系和 Agent 部署职责说明 · covers: doc-maintenance, AC-001, AC-002
- [x] T2 — 新增 `docs/5-deploy/` 目录与部署文档骨架，包含部署总览、环境变量说明、本地验证流程、Agent 部署流程和课设演示验收说明 · covers: AC-001, AC-003, AC-004
- [x] T3 — 新增 `.env.example`，整理 MySQL、Redis、JWT、uploads、Resend 和 OpenAI 兼容模型等示例环境变量并标注敏感项注入规则 · covers: AC-002, AC-004
- [x] T4 — 新增后端 `Dockerfile`，定义 Spring Boot 打包与运行镜像基线 · covers: AC-002
- [x] T5 — 新增 `docker-compose.yml`，串联 Nginx、backend、MySQL、Redis 与 uploads 挂载的单机部署方式 · covers: AC-002, AC-004
- [x] T6 — 新增 `deploy/nginx.conf`，定义前端静态资源托管与 `/api` 反向代理规则 · covers: AC-002
- [x] T7 — 在 `docs/5-deploy/` 中补齐本地验证与失败诊断流程，覆盖 compose 配置检查、服务启动检查、业务冒烟和常见错误定位 · covers: AC-003, AC-004
- [x] T8 — 运行本地部署资产自检（至少包含 compose 配置校验与文档一致性复核），修正发现的问题 · covers: AC-002, AC-003
- [x] T9 — Run `mvn -f personal_crm_backend/pom.xml test` — all tests must pass
- [x] T10 — Verify ACs: update `passes` to `true` in spec.md for each passing criterion
- [x] T11 — Update `docs/4-tasks/CURRENT_PLAN.md` — mark this task complete

## Dependencies
- T1-T3 先完成，作为部署资产与文档的统一基线。
- T4-T7 依赖文档骨架和环境变量约定，避免部署资产与说明脱节。
- T8-T11 依赖前序部署文件和文档全部完成。

## Blockers
<!-- Fill in if something is preventing progress -->

## Completion notes
- 已新增 `docs/5-deploy/` 文档集、`.env.example`、`docker-compose.yml`、`personal_crm_backend/Dockerfile`、`deploy/nginx.conf`。
- 已将后端 Redis 连接改为环境变量驱动，避免部署文档与运行配置脱节。
- 已将 MySQL、Redis 收敛为 Compose 内部服务，并将后端健康检查端口限制为仅宿主机 `127.0.0.1` 可访问。
- 本地验证结果：
  - `docker compose --env-file .env.example config` 通过
  - `npm --prefix personal_crm_web run build` 通过（仅保留既有第三方 Rolldown 注解和 chunk size warning）
  - `mvn -f personal_crm_backend/pom.xml test` 通过（先执行 `docker compose --env-file .env.example up -d redis` 后，111 个测试全部通过）
