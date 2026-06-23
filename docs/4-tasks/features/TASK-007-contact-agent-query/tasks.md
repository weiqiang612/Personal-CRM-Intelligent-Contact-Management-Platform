# TASK-007: Tasks

**Spec**: `spec.md`
**Status**: Completed

## Key decisions
- Contact Agent 查询能力仅在独立 `/agent` 页面交付，不与工作台抽屉联动.
- 后端查询编排先采用规则路由，识别联系人查询与事项查询；真实模型接入留给后续任务.
- Agent 查询必须复用既有 `ContactService`、`TodoService` 与 `agent_operation_log`，不新增数据库结构.

## Progress

- [x] T1 — 更新 `docs/1-requirements/project_overview.md` 与 `docs/1-requirements/requirements_analysis.md`，将 Contact Agent 本期范围明确为“独立页面上的联系人/事项查询闭环” · covers: doc-maintenance
- [x] T2 — 更新 `docs/2-designs/architecture.md`，明确 Agent 查询编排层复用既有业务 Service，且本期不接入真实模型与抽屉联动 · covers: doc-maintenance, AC-AGENT-SECURITY-LOG
- [x] T3 — 更新 `docs/2-designs/api_contract.md`，补充 `POST /api/v1/agent/query` 的请求/响应、空结果、错误提示与日志语义，并保留写操作确认接口边界给后续任务 · covers: AC-AGENT-CONTRACT, AC-AGENT-EDGE-CASE
- [x] T4 — 更新 `docs/2-designs/ui_prototype.md`，将 `/agent` 页面收紧为“查询页”结构，并明确抽屉联动与写确认链路不在本任务范围内 · covers: doc-maintenance, AC-UI-UX
- [x] T5 — 后端：定义 Agent 查询请求 DTO、响应 VO、日志摘要对象与规则识别所需的内部数据结构 · covers: AC-AGENT-CONTRACT, AC-AGENT-CONTACT-QUERY, AC-AGENT-TODO-QUERY
- [x] T6 — 后端：实现 Agent 查询编排 Service，识别联系人查询与事项查询，复用 `ContactService`、`TodoService` 并统一封装查询摘要与结果数据 · covers: AC-AGENT-CONTACT-QUERY, AC-AGENT-TODO-QUERY, AC-AGENT-EDGE-CASE, AC-AGENT-SECURITY-LOG
- [x] T7 — 后端：实现 `POST /api/v1/agent/query` 接口与基础日志留痕，确保查询链路遵守 JWT 用户隔离且不混入写操作确认逻辑 · covers: AC-AGENT-CONTRACT, AC-AGENT-SECURITY-LOG
- [x] T8 — 后端：编写测试覆盖联系人查询、事项查询、无法识别输入、空结果提示和越权隔离场景 · covers: AC-AGENT-CONTACT-QUERY, AC-AGENT-TODO-QUERY, AC-AGENT-EDGE-CASE, AC-AGENT-SECURITY-LOG
- [x] T9 — 前端：定义 Agent 查询 API 类型与请求函数，处理成功、空结果、错误与范围外输入提示 · covers: AC-AGENT-CONTRACT, AC-AGENT-EDGE-CASE
- [x] T10 — 前端：实现独立 `/agent` 页面，包括输入区、推荐问题/空态、联系人结果卡片、事项结果列表与错误反馈状态 · covers: AC-AGENT-CONTACT-QUERY, AC-AGENT-TODO-QUERY, AC-UI-UX
- [x] T11 — 联调：验证 `/agent` 页面与 `POST /api/v1/agent/query` 的端到端查询闭环、基础日志留痕和范围外提示行为 · covers: AC-AGENT-CONTRACT, AC-AGENT-CONTACT-QUERY, AC-AGENT-TODO-QUERY, AC-AGENT-EDGE-CASE, AC-AGENT-SECURITY-LOG
- [x] T12 — 验证 `AC-UI-UX`：在 Chrome MCP 中检查 `/agent` 的桌面 `1440x900`、移动端 `375x812`、hover、console 与截图归档 `artifacts/desktop_agent_query_task007.webp`、`artifacts/mobile_agent_query_task007.webp` · covers: AC-UI-UX
- [x] T13 — 运行 `mvn -f personal_crm_backend/pom.xml test` 和 `npm --prefix personal_crm_web run build`，确保后端测试与前端构建全部通过 · covers: AC-AGENT-CONTRACT, AC-AGENT-SECURITY-LOG
- [x] T14 — 验证验收条件：逐项核对 `spec.md` 中 AC，确认通过后将 `passes` 更新为 `true` · covers: doc-maintenance
- [x] T15 — 更新 `docs/4-tasks/CURRENT_PLAN.md`，在任务完成后回写 `TASK-007` 状态并同步下一个活动任务说明 · covers: doc-maintenance

## Dependencies
- T1-T4 必须先完成，避免 Agent 查询实现偏离当前范围与长期文档边界。
- T5-T7 完成后才能进行 T8 后端测试覆盖。
- T9-T10 依赖 T3 的接口契约稳定和 T6-T7 的后端查询链路可联调。
- T11-T15 必须在实现任务完成后按顺序执行。

## Blockers
<!-- Fill in if something is preventing progress -->
