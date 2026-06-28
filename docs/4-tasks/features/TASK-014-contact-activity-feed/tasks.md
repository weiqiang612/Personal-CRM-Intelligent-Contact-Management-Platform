# TASK-014: Tasks

**Spec**: `spec.md`
**Status**: Completed

## Key decisions
- 采用独立 `activity_log` 表记录联系人上下文中的真实业务事件，不用 `contact.updatedAt` / `todo.updatedAt` 反推伪时间线。
- 活动留痕只挂接在现有 `ContactService` 与 `TodoService` 写链路中，避免绕过既有多租户校验与业务规则。
- 本任务只先覆盖联系人详情页“最近动态”，不扩展 Dashboard 或全局通知中心。

## Progress
- [x] T1 — 更新 `docs/1-requirements/project_overview.md` 与 `docs/1-requirements/requirements_analysis.md`，补充联系人活动流的业务目标、范围与不追溯历史的边界说明 · covers: doc-maintenance, AC-005
- [x] T2 — 更新 `docs/2-designs/architecture.md`，说明活动日志表、事件留痕链路与联系人详情活动流查询分层 · covers: doc-maintenance, AC-005
- [x] T3 — 更新 `docs/2-designs/api_contract.md`，补充 `GET /api/v1/contacts/{contactId}/activities?limit=10` 接口契约与响应结构 · covers: AC-002, AC-004, AC-005
- [x] T4 — 更新 `docs/2-designs/db_schema.md` 并整理 `schema.sql` 变更草案，定义 `activity_log` 表字段、索引与初始化数据策略 · covers: AC-002, AC-005
- [x] T5 — 更新 `docs/2-designs/ui_prototype.md`，补充联系人详情页最近动态卡片的真实数据态、空态与移动端展示约束 · covers: doc-maintenance, AC-003, AC-UI-UX
- [x] T6 — 新增活动日志实体、Mapper、枚举/VO/DTO 类型，完成 `activity_log` 的数据模型与查询对象定义 · covers: AC-002
- [x] T7 — 实现活动日志写入服务，沉淀联系人创建/更新/标签变更/黑名单变更与事项创建/完成/取消的统一留痕能力 · covers: AC-001, AC-002
- [x] T8 — 在 `ContactService` 与 `TodoService` 的既有写链路中接入活动日志留痕，确保复用现有归属校验与事务边界 · covers: AC-001, AC-002, AC-004
- [x] T9 — 实现联系人活动流查询接口、归属校验与时间倒序组装逻辑 · covers: AC-001, AC-002, AC-004
- [x] T10 — 为活动日志新增后端测试，覆盖成功留痕、空结果、越权访问与接口响应契约 · covers: AC-002, AC-003, AC-004
- [x] T11 — 在前端 API 层新增联系人活动流请求类型与请求函数，并处理空态数据结构 · covers: AC-001, AC-003
- [x] T12 — 改造 `ContactDetailView.vue` 最近动态模块，替换静态时间线为真实请求、事件映射、时间格式化与空态渲染 · covers: AC-001, AC-003, AC-UI-UX
- [x] T13 — 运行 `mvn -f personal_crm_backend/pom.xml test`，确保活动流相关后端测试全部通过 · covers: AC-002, AC-004
- [x] T14 — 运行 `npm --prefix personal_crm_web run lint` 与 `npm --prefix personal_crm_web run build`，确保前端改动无 lint / 构建错误 · covers: AC-001, AC-UI-UX
- [x] T15 — 执行 Chrome MCP 验收：在 `/contacts/{contactId}` 路由完成 1440x900 / 375x812、hover、console audit 与截图归档 `task014-contact-activity-desktop.png`、`task014-contact-activity-mobile.png` · covers: AC-UI-UX
- [x] T16 — 回写 `spec.md` 验收结果并更新 `docs/4-tasks/CURRENT_PLAN.md`，在任务完成后同步状态收口 · covers: doc-maintenance, AC-005

## Dependencies
- T1-T5 完成后再进入模型、服务与页面实现，避免契约和边界漂移。
- T6-T9 依赖文档契约完成；T10 依赖后端实现；T11-T12 依赖查询接口完成。
- T13-T16 依赖实现任务完成，且 UI 验收需在构建成功后执行。

## Blockers
<!-- Fill in if something is preventing progress -->
