# TASK-004: Tasks

**Spec**: `spec.md`
**Status**: Completed

## Key decisions
- 本任务是联系人模块收口任务，只修正已经进入主链路的标签展示、标签筛选和黑名单页一致性问题。
- 不提前实现完整标签 CRUD、独立标签管理页或 `/api/v1/tags` 接口。
- 不改 `tag`、`contact_tag` 表结构，只修正契约文档、种子数据归属一致性和查询实现。
- 后端标签筛选必须使用参数化 Mapper 查询或等价安全实现，避免字符串拼接 SQL。
- 前端标签展示必须来自接口返回的 `tags`，无标签时展示占位，不再写死静态关系标签。

## Progress

- [x] T1 — 更新 `docs/2-designs/api_contract.md`，补齐联系人列表 `tag` 查询参数、联系人列表/详情 `tags` 返回字段和本任务不新增 `/api/v1/tags` 的边界说明 · covers: AC-001, AC-006
- [x] T2 — 更新 `docs/2-designs/db_schema.md`，明确 `contact_tag.user_id` 必须与联系人和标签归属一致，并说明本任务只修正种子数据不改表结构 · covers: AC-002, AC-003
- [x] T3 — 更新 `docs/2-designs/ui_prototype.md`，明确联系人列表、联系人详情、黑名单页均使用真实 `tags` 展示；黑名单页不写死标签 · covers: AC-004, AC-005, AC-006
- [x] T4 — 修正 `personal_crm_backend/src/main/resources/data.sql` 中 `contact_tag` 种子数据的用户归属一致性 · covers: AC-002, AC-003
- [x] T5 — 调整联系人 Mapper 或 XML 查询能力，提供当前用户维度下的标签筛选和标签名称查询，避免在 Service 中拼接 SQL · covers: AC-002, AC-003
- [x] T6 — 调整联系人 Service 的列表筛选和 VO 转换逻辑，确保列表、详情、普通联系人和黑名单联系人都返回当前用户可见的真实 `tags` · covers: AC-002, AC-003, AC-004
- [x] T7 — 补充或调整联系人后端测试，覆盖按标签筛选、列表返回 tags、详情返回 tags、黑名单联系人返回 tags、跨用户标签隔离和黑名单状态切换回归 · covers: AC-002, AC-003, AC-005
- [x] T8 — 调整前端联系人 API 类型定义，确保 `ContactInfo.tags` 与后端契约一致，并避免页面自行伪造标签数据 · covers: AC-001, AC-004
- [x] T9 — 调整 `ContactListView.vue`，保持标签筛选可用并确保标签展示、空态、重置和分页行为与真实 `tags` 数据一致 · covers: AC-002, AC-004
- [x] T10 — 调整 `ContactDetailView.vue`，顶部关系标签组改为使用 `contact.tags` 渲染，无标签时展示占位或隐藏标签组 · covers: AC-004, AC-006
- [x] T11 — 调整 `BlacklistView.vue`，标签列改为使用 `row.tags` 渲染，并确认恢复联系人后刷新列表与标签展示一致 · covers: AC-004, AC-005
- [x] T12 — 检查联系人新建/编辑表单中的标签区域，保留后续任务提示或禁用态，不接入真实绑定/解绑业务 · covers: AC-006
- [x] T13 — 完成前后端手工联调，验证 /contacts、/contacts/:contactId、/blacklist 的标签展示、标签筛选、加入黑名单和恢复链路 · covers: AC-002, AC-004, AC-005
- [x] T14 — 运行 `mvn -f personal_crm_backend/pom.xml test`，修复失败项并记录当前未覆盖风险 · covers: AC-002, AC-003, AC-005
- [x] T15 — 运行 `npm --prefix personal_crm_web run build`，确认联系人与黑名单页面可构建 · covers: AC-004, AC-005, AC-006
- [x] T16 — 按 `spec.md` 逐项验收 AC，把通过项的 `passes` 更新为 `true` · covers: AC-001, AC-002, AC-003, AC-004, AC-005, AC-006
- [x] T17 — 更新 `docs/4-tasks/CURRENT_PLAN.md`，将本任务状态改为完成，并同步下一阶段任务排序 · covers: doc-maintenance

## Dependencies
- T1-T3 必须先完成，避免实现继续偏离 API、DB 和 UI 文档。
- T4-T6 完成后才能稳定推进后端测试与前端联调。
- T8-T12 依赖后端 `tags` 契约和返回字段稳定。
- T13-T17 必须在实现任务完成后执行。

## Blockers
<!-- Fill in if something is preventing progress -->
