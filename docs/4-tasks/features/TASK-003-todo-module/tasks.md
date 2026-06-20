# TASK-003: Tasks

**Spec**: `spec.md`
**Status**: Completed

## Key decisions
- 本任务只实现事项提醒闭环，不提前实现 Agent、看板完整统计、通知提醒、日历视图或事项详情页。
- 后端严格复用 TASK-001 的统一响应、异常处理、JWT 当前用户上下文和 MyBatis-Plus 分页配置。
- 事项必须关联当前用户拥有的联系人；所有查询和状态变更都以当前 JWT 用户为隔离边界。
- 前端必须基于 `docs/2-designs/ui_prototype.md` 与 `prototype/todos.html`、`prototype/todo-form.html` 的事项原型迁移，不重新设计一套默认事项页。
- API 和 DB 先按现有契约实现；只有发现契约缺口或表结构问题时，才先维护对应设计文档。

## Progress

- [x] T1 — 复核事项 API、DB、UI 原型与当前代码差异，确认无需先改 `api_contract.md`、`db_schema.md` 或 `ui_prototype.md`；若发现差异，先同步对应文档 · covers: AC-001, AC-002, AC-005, AC-006
- [x] T2 — 实现事项实体、状态与优先级常量或枚举、分页查询对象、创建 DTO 和列表 VO，字段映射对齐 `contact_todo` 表和接口契约 · covers: AC-001, AC-002, AC-004
- [x] T3 — 实现 `TodoMapper` 和必要查询方法，支持当前用户维度下的分页、联系人筛选、状态筛选、时间范围和时间排序查询 · covers: AC-001, AC-002, AC-003
- [x] T4 — 实现事项 Service 的列表和新增逻辑，新增时校验关联联系人属于当前用户，并动态计算逾期展示字段 · covers: AC-001, AC-002, AC-003, AC-006
- [x] T5 — 实现事项 Service 的完成和取消逻辑，限制仅待完成事项可流转，并写入 `completedAt` 或 `cancelledAt` · covers: AC-001, AC-003, AC-004, AC-006
- [x] T6 — 实现事项 Controller，路径、请求体、响应体、错误码与 `docs/2-designs/api_contract.md` 保持一致 · covers: AC-001, AC-003, AC-004, AC-006
- [x] T7 — 补充事项模块后端测试或集成验证，覆盖登录后新增、筛选分页、完成、取消、未登录、跨用户联系人和跨用户事项访问 · covers: AC-001, AC-002, AC-003, AC-004, AC-006
- [x] T8 — 实现前端事项 API 封装和类型定义，包括列表、新增、完成和取消事项 · covers: AC-001, AC-006
- [x] T9 — 调整前端路由，将 `/todos` 从占位页替换为真实页面，并新增 `/todos/new` 独立路由 · covers: AC-005, AC-006
- [x] T10 — 基于 `prototype/todos.html` 实现事项列表页，支持状态快捷筛选、联系人筛选、时间范围、排序、分页、加载态、空态、错误态和查看联系人入口 · covers: AC-002, AC-005, AC-006
- [x] T11 — 在事项列表页实现完成和取消操作，加入明确确认步骤，并在成功后刷新列表状态 · covers: AC-004, AC-005, AC-006
- [x] T12 — 基于 `prototype/todo-form.html` 实现新增事项页，包含联系人搜索选择、时间与内容校验、优先级选择块、右侧预览区、提交状态和接口错误提示 · covers: AC-001, AC-005, AC-006
- [x] T13 — 支持 `/todos/new?contactId=:contactId` 自动带入联系人，并从联系人详情页的添加事项入口正确跳转 · covers: AC-003, AC-005, AC-006
- [x] T14 — 为 Agent、看板统计、通知提醒、日历视图和事项详情相关入口补充后续任务提示或禁用态，避免调用未实现业务链路 · covers: AC-005
- [x] T15 — 完成事项前后端手工联调，验证新增、列表查询、筛选、完成、取消和带 contactId 新增事项的连续主链路 · covers: AC-001, AC-002, AC-003, AC-004, AC-005, AC-006
- [x] T16 — 运行 `mvn -f personal_crm_backend/pom.xml test`，修复失败项并记录当前未覆盖风险 · covers: AC-001, AC-002, AC-003, AC-004, AC-006
- [x] T17 — 运行 `npm --prefix personal_crm_web run build`，确认事项页面与路由可构建 · covers: AC-005, AC-006
- [x] T18 — 按 `spec.md` 逐项验收 AC，把通过项的 `passes` 更新为 `true` · covers: AC-001, AC-002, AC-003, AC-004, AC-005, AC-006
- [x] T19 — 更新 `docs/4-tasks/CURRENT_PLAN.md`，将本任务状态改为完成，并同步阶段进度 · covers: doc-maintenance

## Dependencies
- T1 必须先完成，避免实现偏离既有 API、DB 和 UI 原型。
- T2-T6 完成后才能稳定推进后端测试和前端接口联调。
- T8-T14 依赖 TASK-001 的前端骨架和 TASK-002 的联系人基础能力，并应在后端事项接口基本可用后联调。
- T15-T19 必须在实现任务完成后执行。

## Blockers
<!-- Fill in if something is preventing progress -->
