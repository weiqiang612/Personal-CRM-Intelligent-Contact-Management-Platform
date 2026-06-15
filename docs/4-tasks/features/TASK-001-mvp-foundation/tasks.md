# TASK-001: Tasks

**Spec**: `spec.md`
**Status**: In Progress

## Key decisions
- 采用“小步快跑”的交付方式，每次只完成一个可验证模块，不把多个业务链路混在一次提交里。
- 优先打通后端基础设施与认证，再逐步叠加联系人、事项、看板、上传和前端骨架。
- 严格以现有 `api_contract.md` 与 `db_schema.md` 为边界，编码阶段不主动扩表或改契约。

## Progress

- [ ] T1 — 补充后端统一返回体与错误码常量，固定 `{ code, message, data }` 响应结构 · covers: AC-001
- [ ] T2 — 实现全局异常处理与参数校验异常映射，覆盖鉴权失败、校验失败、资源不存在、状态冲突等基础错误 · covers: AC-001, AC-004
- [ ] T3 — 接入 MyBatis-Plus 分页插件与基础配置类，保证后端可在当前 `application-dev.yaml` 下稳定启动 · covers: AC-001
- [ ] T4 — 落地 JWT 工具类、认证过滤链或拦截器、当前用户上下文，先打通“公开接口 / 受保护接口”边界 · covers: AC-001, AC-004
- [ ] T5 — 在 `data.sql` 中加入可登录的本地测试账号与最小演示数据，确保认证和业务联调有稳定输入 · covers: AC-002
- [ ] T6 — 实现认证模块 DTO、VO、Service、Controller，完成登录和 `GET /api/v1/auth/me` · covers: AC-002, AC-004
- [ ] T7 — 实现联系人实体映射、查询条件对象和基础 Mapper，自测分页、关键词和状态筛选查询 · covers: AC-002
- [ ] T8 — 实现联系人 Service，完成列表、详情、新增、修改、加入黑名单、恢复，并补齐用户归属校验 · covers: AC-002, AC-004
- [ ] T9 — 实现联系人 Controller，接口路径、请求体和响应体与 `docs/2-designs/api_contract.md` 保持一致 · covers: AC-002
- [ ] T10 — 实现事项实体映射、Mapper 和 Service，完成列表、新增、完成、取消，并落实状态流转校验 · covers: AC-003, AC-004
- [ ] T11 — 实现事项 Controller 与看板统计 Service/Controller，先返回联系人数、黑名单数、待办数、今日事项数、逾期事项数 · covers: AC-003
- [ ] T12 — 实现本地文件上传服务、上传配置读取和上传 Controller，完成用户头像与联系人头像上传入口 · covers: AC-003
- [ ] T13 — 替换前端 `create-vue` 默认页面，建立基础布局、登录页占位、首页占位和模块导航骨架 · covers: AC-005
- [ ] T14 — 建立前端 `api`、`stores`、`router` 基础层，接入 token 存储、Axios 请求封装和基础鉴权跳转 · covers: AC-005
- [ ] T15 — 为认证、联系人、事项、看板、上传补充最小联调页面或占位页，保证主链路可手工验收 · covers: AC-002, AC-003, AC-005
- [ ] T16 — 运行 `mvn -f personal_crm_backend/pom.xml test`，修复失败项并记录当前未覆盖风险 · covers: AC-001, AC-002, AC-003, AC-004
- [ ] T17 — 按 `spec.md` 逐项验收 AC，把通过项的 `passes` 更新为 `true` · covers: AC-001, AC-002, AC-003, AC-004, AC-005
- [ ] T18 — 更新 `docs/4-tasks/CURRENT_PLAN.md`，将本任务状态改为完成，并同步阶段进度 · covers: doc-maintenance

## Dependencies
- T1-T4 完成后才能开始稳定实现认证与业务模块。
- T5-T6 是联系人、事项、看板、上传联调的前置条件。
- T7-T12 完成后再推进前端联调，避免前端长期对着空接口开发。
- T16-T18 必须在实现任务完成后执行。

## Blockers
<!-- Fill in if something is preventing progress -->
