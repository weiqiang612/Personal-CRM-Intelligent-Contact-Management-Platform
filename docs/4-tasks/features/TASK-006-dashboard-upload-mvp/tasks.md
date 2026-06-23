# TASK-006: Tasks

**Spec**: `spec.md`
**Status**: Completed
 
## Key decisions
- 工作台继续承担当前 MVP 的首页与轻量看板职责，不新增独立看板导航或新页面。
- `/dashboard` 固定为“5 个摘要卡 + 今日待办/最近联系人双列 + 趋势图/性别分布双图表”的页面结构，其中仅今日待办支持轻量行内状态操作。
- 上传能力在本任务中同时补齐联系人头像与当前用户头像两条链路，统一复用现有本地上传目录与访问地址回显模式。
- 本任务不新增数据库表结构或外部依赖，接口与页面体验补齐优先于扩展统计维度。
 
## Progress
 
- [x] T1 — 更新需求文档：修改 `docs/1-requirements/project_overview.md` 和 `docs/1-requirements/requirements_analysis.md`，明确工作台双列看板与双头像上传是当前 MVP 范围 · covers: doc-maintenance
- [x] T2 — 更新 API 契约：修改 `docs/2-designs/api_contract.md`，补全看板统计接口的响应结构、今日待办联动行为和头像上传失败处理约束 · covers: AC-DASHBOARD-CONTRACT, AC-DASHBOARD-ACTION, AC-UPLOAD-AVATAR, AC-UPLOAD-FAILURE
- [x] T3 — 更新 UI 原型：修改 `docs/2-designs/ui_prototype.md`，固定工作台摘要卡/双列/双图表布局与头像上传回显、失败提示交互 · covers: doc-maintenance, AC-DASHBOARD-VIEW, AC-UPLOAD-AVATAR
- [x] T4 — 后端：定义看板统计相关 VO / DTO / Mapper 查询结构，整理摘要卡、今日待办、最近联系人、趋势图和性别分布的数据聚合出口 · covers: AC-DASHBOARD-CONTRACT, AC-DASHBOARD-VIEW
- [x] T5 — 后端：实现看板 Service / Controller 链路，返回当前用户维度的 `overview`、`todo-trend`、`contact-gender-distribution` 数据并复用既有事项状态变更规则 · covers: AC-DASHBOARD-CONTRACT, AC-DASHBOARD-VIEW, AC-DASHBOARD-ACTION, AC-SECURITY-ISOLATION
- [x] T6 — 后端：实现联系人头像与用户头像上传逻辑，补齐格式/大小校验、本地存储、访问地址回写和失败兜底处理 · covers: AC-DASHBOARD-CONTRACT, AC-UPLOAD-AVATAR, AC-UPLOAD-FAILURE, AC-SECURITY-ISOLATION
- [x] T7 — 后端：确保联系人列表、联系人详情、当前用户信息等查询链路能稳定回显最新头像访问地址 · covers: AC-UPLOAD-AVATAR, AC-SECURITY-ISOLATION
- [x] T8 — 后端：编写测试覆盖看板统计查询、今日待办状态联动、头像上传成功/失败和跨用户隔离场景 · covers: AC-DASHBOARD-VIEW, AC-DASHBOARD-ACTION, AC-UPLOAD-AVATAR, AC-UPLOAD-FAILURE, AC-SECURITY-ISOLATION
- [x] T9 — 前端：定义看板与上传 API 类型及请求函数，接入 dashboard 统计查询与联系人/用户头像上传接口 · covers: AC-DASHBOARD-CONTRACT, AC-UPLOAD-AVATAR
- [x] T10 — 前端：实现 `/dashboard` 的摘要卡、趋势图、性别分布图、今日待办和最近联系人展示，确保数据来自真实接口 · covers: AC-DASHBOARD-VIEW
- [x] T11 — 前端：为工作台今日待办补齐行内完成/取消交互，并让摘要卡与列表状态联动刷新；最近联系人保持只读跳转 · covers: AC-DASHBOARD-ACTION
- [x] T12 — 前端：在联系人表单/详情/列表与设置页补齐头像上传、预览回显、失败提示和旧头像保留逻辑 · covers: AC-UPLOAD-AVATAR, AC-UPLOAD-FAILURE
- [x] T13 — 联调：验证工作台统计、今日待办操作、最近联系人跳转和双头像上传回显在真实运行环境下全链路可用 · covers: AC-DASHBOARD-VIEW, AC-DASHBOARD-ACTION, AC-UPLOAD-AVATAR
- [x] T14 — 将移动端断点专项可用性与截图归档从本次 `TASK-006` 收尾范围移出，后续如需补做转入质量任务单独验收 · covers: doc-maintenance
- [x] T15 — 运行验证命令：执行 `mvn -f personal_crm_backend/pom.xml test` 和 `npm --prefix personal_crm_web run build`，确保后端测试与前端构建全部通过 · covers: AC-DASHBOARD-CONTRACT, AC-SECURITY-ISOLATION
- [x] T16 — 验证验收条件：逐项核对 `spec.md` 中 AC，确认通过后将 `passes` 更新为 `true` · covers: doc-maintenance
- [x] T17 — 更新 `docs/4-tasks/CURRENT_PLAN.md`：在任务完成后标记 `TASK-006` 状态并同步后续阶段说明 · covers: doc-maintenance

## Dependencies
- T1-T3 必须先完成，避免实现偏离既定看板与上传范围。
- T4-T7 完成后才能进行 T8 后端测试覆盖。
- T9 依赖 T2 的接口契约稳定；T10-T12 依赖后端链路已具备可联调能力。
- T13-T17 必须在实现任务完成后按顺序执行。
