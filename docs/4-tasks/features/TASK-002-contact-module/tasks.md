# TASK-002: Tasks

**Spec**: `spec.md`
**Status**: Completed

## Key decisions
- 本任务只实现联系人和黑名单闭环，不提前实现头像上传、事项、标签或 Agent 能力。
- 后端严格复用 TASK-001 的统一响应、异常处理、JWT 当前用户上下文和 MyBatis-Plus 分页配置。
- 前端必须基于 `docs/2-designs/ui_prototype.md` 与 `prototype/*.html` 的联系人原型迁移，不重新设计一套默认后台页面。
- API 和 DB 先按现有契约实现；只有发现契约缺口或表结构问题时，才先维护对应设计文档。

## Progress

- [x] T1 — 复核联系人 API、DB、UI 原型与当前代码差异，确认无需先改 `api_contract.md`、`db_schema.md` 或 `ui_prototype.md`；若发现差异，先同步对应文档 · covers: AC-001, AC-002, AC-004, AC-005, AC-006
- [x] T2 — 实现联系人实体、状态常量或枚举、分页查询对象、创建/更新 DTO 和详情/列表 VO，字段映射对齐 `contact` 表和接口契约 · covers: AC-001, AC-002
- [x] T3 — 实现 `ContactMapper` 和必要查询方法，支持当前用户维度下的分页、关键词、状态、排序查询 · covers: AC-001, AC-002, AC-003
- [x] T4 — 实现联系人 Service，完成列表、详情、新增、修改、加入黑名单、恢复联系人，并落实当前用户归属校验和状态冲突处理 · covers: AC-001, AC-002, AC-003, AC-005
- [x] T5 — 实现联系人 Controller，路径、请求体、响应体、错误码与 `docs/2-designs/api_contract.md` 保持一致 · covers: AC-001, AC-003, AC-005
- [x] T6 — 补充联系人模块后端测试或集成验证，覆盖登录后 CRUD、筛选分页、黑名单切换、未登录和越权/不存在访问 · covers: AC-001, AC-002, AC-003, AC-005
- [x] T7 — 实现前端联系人 API 封装和类型定义，包括列表、详情、新增、修改、加入黑名单、恢复联系人 · covers: AC-001, AC-005
- [x] T8 — 调整前端路由，将 `/contacts`、`/contacts/new`、`/contacts/:contactId`、`/contacts/:contactId/edit`、`/blacklist` 从占位页替换为真实页面 · covers: AC-004, AC-005
- [x] T9 — 基于 `prototype/contacts.html` 实现联系人列表页，支持搜索、状态筛选、排序、分页、加载态、空态、错误态和行操作入口 · covers: AC-002, AC-004, AC-005
- [x] T10 — 基于 `prototype/contact-form.html` 实现联系人新建与编辑页，包含表单校验、提交状态、取消返回和接口错误提示 · covers: AC-001, AC-004, AC-005
- [x] T11 — 基于 `prototype/contact-detail.html` 实现联系人详情页，展示联系人资料、状态和操作区，并支持加入黑名单确认流程 · covers: AC-004, AC-005, AC-006
- [x] T12 — 基于 `prototype/blacklist.html` 实现黑名单页，支持黑名单列表、搜索排序、风险提示条、查看详情和恢复联系人确认流程 · covers: AC-002, AC-004, AC-005, AC-006
- [x] T13 — 为头像、事项、标签、Agent 相关入口补充明确的后续任务提示或禁用态，避免调用未实现业务链路 · covers: AC-006
- [x] T14 — 完成联系人前后端手工联调，验证新增、列表查询、详情、编辑、加入黑名单、黑名单恢复的连续主链路 · covers: AC-001, AC-002, AC-003, AC-004, AC-005
- [x] T15 — 运行 `mvn -f personal_crm_backend/pom.xml test`，修复失败项并记录当前未覆盖风险 · covers: AC-001, AC-002, AC-003, AC-005
- [x] T16 — 运行 `npm --prefix personal_crm_web run build`，确认联系人页面与路由可构建 · covers: AC-004, AC-005, AC-006
- [x] T17 — 按 `spec.md` 逐项验收 AC，把通过项的 `passes` 更新为 `true` · covers: AC-001, AC-002, AC-003, AC-004, AC-005, AC-006
- [x] T18 — 更新 `docs/4-tasks/CURRENT_PLAN.md`，将本任务状态改为完成，并同步阶段进度 · covers: doc-maintenance

## Dependencies
- T1 必须先完成，避免实现偏离既有 API、DB 和 UI 原型。
- T2-T5 完成后才能稳定推进后端测试和前端接口联调。
- T7-T13 依赖 TASK-001 的前端骨架，并应在后端接口基本可用后联调。
- T14-T18 必须在实现任务完成后执行。

## Blockers
<!-- Fill in if something is preventing progress -->
