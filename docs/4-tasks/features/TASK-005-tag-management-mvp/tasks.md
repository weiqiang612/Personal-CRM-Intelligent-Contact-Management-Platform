# TASK-005: Tasks

**Spec**: `spec.md`
**Status**: Completed

## Key decisions
- 本任务是标签管理 MVP，只补齐标签 CRUD 和联系人标签绑定闭环，不提前实现标签统计、批量操作或 Agent 标签能力。
- 不新增数据库表、字段、索引和依赖，复用现有 `tag` 与 `contact_tag` 表。
- 标签接口和联系人保存联动必须始终以当前 JWT 用户为边界，禁止跨用户查询、绑定、编辑或删除。
- 联系人新增/编辑请求的 `tagIds` 必须是可选字段，保持既有无标签联系人保存请求兼容。
- 前端标签选项必须来自后端真实标签列表，不再使用固定枚举作为业务数据来源。

## Progress

- [x] T1 — 更新需求分析文档：修改 `docs/1-requirements/requirements_analysis.md` 和 `docs/1-requirements/project_overview.md`，补充标签管理与联系人标签绑定作为核心业务完善范围 · covers: doc-maintenance
- [x] T2 — 更新 API 契约：修改 `docs/2-designs/api_contract.md`，新增 `/api/v1/tags` 标签 CRUD 契约，并扩展联系人新增/编辑请求体中可选的 `tagIds` 字段 · covers: AC-CONTRACT
- [x] T3 — 更新 DB 设计：修改 `docs/2-designs/db_schema.md`，明确 `tag`、`contact_tag` 的当前用户归属和删除标签时的级联绑定清理规则 · covers: AC-CONTRACT
- [x] T4 — 更新 UI 原型设计：修改 `docs/2-designs/ui_prototype.md`，定义标签自适应列表、删除确认和表单标签多选等 MVP 交互说明 · covers: doc-maintenance
- [x] T5 — 后端：创建数据模型，定义标签对应的 Entity、DTO、VO 并实现 MyBatis-Plus Mapper 层接口 · covers: AC-CONTRACT, AC-TAG-CRUD
- [x] T6 — 后端：实现标签 CRUD 业务逻辑，在 Service 与 Controller 中强化跨用户隔离与删除标签时级联清理 `contact_tag` 的处理 · covers: AC-TAG-CRUD, AC-SECURITY-ISOLATION, AC-EDGE-CLEANUP
- [x] T7 — 后端：扩展联系人新增/编辑 Service 逻辑，支持可选 `tagIds` 绑定/替换/清空，并校验标签归属当前用户 · covers: AC-CONTACT-BIND, AC-SECURITY-ISOLATION
- [x] T8 — 后端：编写集成测试套件，全面覆盖标签 CRUD、重复标签校验、越权操作隔离以及删除级联清理 · covers: AC-TAG-CRUD, AC-SECURITY-ISOLATION, AC-EDGE-CLEANUP
- [x] T9 — 前端：定义标签 API 类型及请求函数，支持与后端 `/api/v1/tags` 进行交互 · covers: AC-CONTRACT, AC-TAG-CRUD
- [x] T10 — 前端：调整联系人列表及表单加载逻辑，使标签筛选选项和绑定多选框由后端真实接口数据驱动 · covers: AC-TAG-CRUD, AC-CONTACT-BIND
- [x] T11 — 前端：实现标签管理 MVP 界面，提供标签列表、新增、编辑、删除交互并接入 Element Plus 组件 · covers: AC-TAG-CRUD, AC-EDGE-CLEANUP
- [x] T12 — 联调：进行前后端接口联调，在真实运行环境下验证标签 CRUD、表单绑定 and 列表筛选全链路 · covers: AC-TAG-CRUD, AC-CONTACT-BIND
- [x] T13 — 运行测试套件：执行 `mvn -f personal_crm_backend/pom.xml test` 确保所有后端测试通过 · covers: AC-TAG-CRUD, AC-SECURITY-ISOLATION, AC-EDGE-CLEANUP
- [x] T14 — 验证验收条件：在真实浏览器中（使用 Chrome MCP）进行响应式与交互测试，验证符合 `spec.md` 所有的验收条件后更新 `passes` 为 `true` · covers: AC-UI-UX
- [x] T15 — 更新当前计划：修改 `docs/4-tasks/CURRENT_PLAN.md` 标记本任务为完成状态，并同步下一阶段任务 · covers: doc-maintenance

## Dependencies
- T1-T4 必须优先完成，避免实现偏离契约及规格。
- T5-T7 完工后方可进行 T8 单元与集成测试。
- T9-T11 依赖后端标签接口契约的稳定。
- T12-T15 必须在全部编码任务完成后按顺序进行。
