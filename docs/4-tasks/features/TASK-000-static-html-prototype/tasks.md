# TASK-000: Tasks

**Spec**: `spec.md`
**Status**: Completed

## Key decisions
- 静态原型独立放在根目录 `prototype/`，不混入 `personal_crm_web/src`，避免与 Vue 实现阶段相互干扰。
- 所有页面共享一套样式和基础组件约定，先冻结版式与视觉骨架，再由 `TASK-001` 迁移到 Vue。
- 黑名单页只复用参考图的布局，不复用其黑白头像表现，统一使用彩色头像规则。

## Progress

- [x] T1 — 更新 `docs/2-designs/ui_prototype.md`，确认 8 个主原型页面与最新参考图片映射保持一致，并保留黑名单彩色头像约束 · covers: doc-maintenance, AC-002, AC-003
- [x] T2 — 创建 `docs/4-tasks/features/TASK-000-static-html-prototype/spec.md` 与 `tasks.md`，固定原型范围、验收标准 and `TASK-000 -> TASK-001` 前置关系 · covers: AC-004
- [x] T3 — 更新 `docs/4-tasks/CURRENT_PLAN.md`，将 `TASK-000` 设为当前活跃任务，并说明 `TASK-001` 前端实现依赖原型确认 · covers: AC-004
- [x] T4 — 更新 `docs/4-tasks/features/TASK-001-mvp-foundation/tasks.md` 中前端相关任务描述，改为基于已确认 HTML 原型迁移到 Vue 骨架 · covers: AC-004
- [x] T5 — 创建 `prototype/assets/styles.css`，沉淀统一的布局、卡片、表格、表单、按钮、标签和头像样式令牌 · covers: AC-001, AC-002, AC-003
- [x] T6 — 创建 `prototype/login.html` 与 `prototype/dashboard.html`，还原登录和工作台主布局 · covers: AC-001, AC-002, AC-005
- [x] T7 — 创建 `prototype/contacts.html`、`prototype/contact-detail.html`、`prototype/contact-form.html`，还原联系人列表、详情和表单页 · covers: AC-001, AC-002, AC-005
- [x] T8 — 创建 `prototype/todos.html` 与 `prototype/todo-form.html`，还原事项提醒列表和新增事项页 · covers: AC-001, AC-002, AC-005
- [x] T9 — 创建 `prototype/blacklist.html`，还原黑名单页布局并落实彩色头像约束 · covers: AC-001, AC-002, AC-003, AC-005
- [x] T10 — 运行结构校验命令，确认 8 个页面与共享资源存在、文档无占位符、`prototype/` 中无真实接口调用 · covers: AC-001, AC-004, AC-005
- [x] T11 — 按 `spec.md` 逐项验收 AC，把通过项的 `passes` 更新为 `true` · covers: AC-001, AC-002, AC-003, AC-004, AC-005
- [x] T12 — 更新 `docs/4-tasks/CURRENT_PLAN.md`，在本任务完成后回写状态并切回后续实现主线 · covers: doc-maintenance

## Dependencies
- T1-T4 完成后再落地静态原型，确保范围、引用和任务前置关系已固定。
- T5 是所有页面落地的共享前置条件。
- T6-T9 完成后才能进行结构校验和 AC 回写。

## Blockers
<!-- Fill in if something is preventing progress -->
