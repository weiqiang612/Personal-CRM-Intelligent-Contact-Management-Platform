# TASK-001: Tasks

**Spec**: `spec.md`
**Status**: Completed

## Key decisions
- 采用“小步快跑”的交付方式，本任务只完成基础设施、认证和前端应用骨架，不提前实现多个业务模块。
- 优先打通后端基础设施与认证，再建立前端路由、状态管理、API 层和基础布局。
- 联系人、事项、看板、上传作为后续独立任务纵向实现，本任务最多提供导航入口或占位页面。
- 严格以现有 `api_contract.md` 与 `db_schema.md` 为边界，编码阶段不主动扩表或改契约。

## Progress

- [x] T1 — 补充后端统一返回体与错误码常量，固定 `{ code, message, data }` 响应结构 · covers: AC-001
- [x] T2 — 实现全局异常处理与参数校验异常映射，覆盖鉴权失败、校验失败、资源不存在、状态冲突等基础错误 · covers: AC-001, AC-004
- [x] T3 — 接入 MyBatis-Plus 分页插件与基础配置类，保证后端可在当前 `application-dev.yaml` 下稳定启动 · covers: AC-001
- [x] T4 — 落地 JWT 工具类、认证过滤链或拦截器、当前用户上下文，先打通“公开接口 / 受保护接口”边界 · covers: AC-001, AC-004
- [x] T5 — 在 `data.sql` 中加入可登录的本地测试账号，确保认证联调有稳定输入 · covers: AC-002
- [x] T6 — 实现认证模块 DTO、VO、Service、Controller，完成登录和 `GET /api/v1/auth/me` · covers: AC-002, AC-004
- [x] T7 — 基于已确认的静态 HTML 原型替换前端 `create-vue` 默认页面，建立基础布局、登录页、工作台和模块导航骨架 · covers: AC-003
- [x] T8 — 建立前端 `api`、`stores`、`router` 基础层，接入 token 存储、Axios 请求封装和基础鉴权跳转 · covers: AC-003, AC-004
- [x] T9 — 为联系人、事项、看板、上传建立明确的占位路由或占位页面，说明业务能力由后续任务实现 · covers: AC-003, AC-005
- [x] T10 — 完成认证前后端最小联调，验证登录、`/api/v1/auth/me`、未登录拦截和前端鉴权跳转 · covers: AC-002, AC-003, AC-004
- [x] T11 — 运行 `mvn -f personal_crm_backend/pom.xml test`，修复失败项并记录当前未覆盖风险 · covers: AC-001, AC-002, AC-004
- [x] T12 — 运行 `npm --prefix personal_crm_web run build`，确认前端骨架可构建 · covers: AC-003
- [x] T13 — 按 `spec.md` 逐项验收 AC，把通过项的 `passes` 更新为 `true` · covers: AC-001, AC-002, AC-003, AC-004, AC-005
- [x] T14 — 更新 `docs/4-tasks/CURRENT_PLAN.md`，将本任务状态改为完成，并同步阶段进度 · covers: doc-maintenance

## Dependencies
- T1-T4 完成后才能开始稳定实现认证模块。
- T5-T6 是前端认证联调的前置条件。
- T7-T10 完成后，本任务应只具备前端骨架和认证闭环，不继续实现业务模块。
- T11-T14 必须在实现任务完成后执行。

## Blockers
<!-- Fill in if something is preventing progress -->
