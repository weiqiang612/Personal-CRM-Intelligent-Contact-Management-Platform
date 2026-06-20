# TASK-004: Contact Tag and Blacklist Polish

**Status**: Ready
**Created**: 2026-06-20
**Feature dir**: `docs/4-tasks/features/TASK-004-contact-tag-blacklist-polish/`

## Objective
在不提前实现完整标签管理模块的前提下，收口联系人列表、联系人详情和黑名单页中已经暴露的标签展示、标签筛选和黑名单一致性问题。

## Scope

### In scope
- 同步联系人 API 契约，明确联系人列表支持 `tag` 查询参数，并在联系人列表与详情响应中返回 `tags` 字段。
- 修正本地种子数据中的 `contact_tag.user_id`、`contact.user_id`、`tag.user_id` 归属一致性问题。
- 收口后端标签筛选实现，避免通过字符串拼接构造标签筛选 SQL。
- 联系人列表继续支持标签展示与标签筛选，并保证筛选选项与当前种子数据和文档一致。
- 联系人详情顶部标签组改为使用接口返回的 `contact.tags`，不再写死静态标签。
- 黑名单页标签列改为使用真实 `row.tags`，并保留黑名单恢复确认链路。
- 补充或调整后端测试，覆盖标签筛选、`tags` 返回、黑名单联系人标签返回和用户数据隔离。
- 完成本地验证、自测和文档状态同步。

### Out of scope
- 完整标签 CRUD。
- 新增 `/api/v1/tags` 接口。
- 联系人新增或编辑时绑定、解绑标签。
- 独立标签管理页面。
- 新增数据库表、字段、索引或外部依赖。
- 看板统计、头像上传、Agent 查询或 Agent 写操作确认链路。

## Acceptance criteria

```json
[
  {
    "id": "AC-001",
    "category": "integration",
    "description": "联系人 API 契约与现有标签展示能力保持一致，明确 tag 查询参数和 tags 返回字段。",
    "steps": [
      "查看 docs/2-designs/api_contract.md 的联系人列表与联系人详情契约。",
      "确认 GET /api/v1/contacts 支持 tag 查询参数且响应项包含 tags 数组。",
      "确认 GET /api/v1/contacts/{contactId} 响应也包含 tags 数组。"
    ],
    "passes": true
  },
  {
    "id": "AC-002",
    "category": "functional",
    "description": "后端联系人列表可按标签筛选，并能在列表和详情中返回当前用户可见联系人的真实标签。",
    "steps": [
      "准备当前用户下带有不同标签的联系人数据。",
      "调用 GET /api/v1/contacts?tag=同学 和联系人详情接口。",
      "验证列表筛选结果、分页元数据和 tags 字段符合当前用户数据。"
    ],
    "passes": true
  },
  {
    "id": "AC-003",
    "category": "security",
    "description": "标签查询与展示必须遵守当前 JWT 用户的数据隔离边界。",
    "steps": [
      "准备不同用户下相同标签名或不同标签归属的联系人数据。",
      "使用当前用户 JWT 查询联系人列表、按 tag 筛选和查看联系人详情。",
      "验证不会返回、筛选出或展示其他用户的联系人标签关系。"
    ],
    "passes": true
  },
  {
    "id": "AC-004",
    "category": "integration",
    "description": "联系人列表、联系人详情和黑名单页都使用接口返回的真实 tags 展示，不再写死静态关系标签。",
    "steps": [
      "登录后访问 /contacts、/contacts/:contactId 和 /blacklist。",
      "检查普通联系人、黑名单联系人、无标签联系人三类数据的标签展示。",
      "验证无标签时展示空态占位，有标签时展示接口返回的标签集合。"
    ],
    "passes": true
  },
  {
    "id": "AC-005",
    "category": "edge-case",
    "description": "黑名单加入和恢复链路不因标签展示收口产生回归。",
    "steps": [
      "从联系人列表或联系人详情将联系人加入黑名单。",
      "在 /blacklist 中查看该联系人并执行恢复联系人。",
      "验证二次确认、成功提示、列表刷新、状态切换和标签展示都正常。"
    ],
    "passes": true
  },
  {
    "id": "AC-006",
    "category": "edge-case",
    "description": "本任务不提前实现完整标签管理，只修正已暴露链路中的标签一致性问题。",
    "steps": [
      "检查前后端是否新增 /api/v1/tags 或完整标签 CRUD 页面。",
      "检查联系人新建和编辑表单是否仍不承诺真实标签绑定能力。",
      "验证任务交付边界仍聚焦标签展示、标签筛选和黑名单页一致性。"
    ],
    "passes": true
  }
]
```

## Notes
### Reference baseline
Before implementing this task, read and follow:

| Area | Required baseline |
|---|---|
| project workflow | `AGENTS.md`, `docs/4-tasks/CURRENT_PLAN.md` |
| business scope | `docs/1-requirements/project_overview.md`, `docs/1-requirements/requirements_analysis.md` |
| backend architecture | `docs/2-designs/architecture.md` |
| API contract | `docs/2-designs/api_contract.md` |
| database baseline | `docs/2-designs/db_schema.md`, `personal_crm_backend/src/main/resources/schema.sql`, `personal_crm_backend/src/main/resources/data.sql` |
| UI prototype | `docs/2-designs/ui_prototype.md` |
| constraints | `docs/3-constraints/never-do.md`, `docs/3-constraints/ask-first.md`, `docs/3-constraints/always-do.md` |
| previous modules | `docs/4-tasks/features/TASK-002-contact-module/spec.md`, `docs/4-tasks/features/TASK-003-todo-module/spec.md` |

### Documentation impact
| Area | Impacted | Maintenance target |
|---|---:|---|
| requirements | false | `docs/1-requirements/project_overview.md`, `docs/1-requirements/requirements_analysis.md` |
| architecture | false | `docs/2-designs/architecture.md` |
| api | true | `docs/2-designs/api_contract.md` |
| db | true | `docs/2-designs/db_schema.md`, `personal_crm_backend/src/main/resources/data.sql` |
| ui | true | `docs/2-designs/ui_prototype.md` |
| constraints | false | `docs/3-constraints/` |
| adr | false | `docs/3-constraints/adr/` |
| agent-runtime | false | `AGENTS.md`, `.codex/session-start.js`, `init.sh`, `init.ps1` |

### Approval-sensitive changes
- 不新增 Maven 或 Node 依赖。
- 不新增或修改数据库表、字段、索引和约束。
- 不新增 `/api/v1/tags` 或完整标签管理接口。
- 若实现中发现必须改变联系人 API 的路径、请求体或响应体兼容语义，必须先同步 `docs/2-designs/api_contract.md`。

### Explicit non-maintenance
- 本任务不改变业务目标、用户角色或核心业务流，因此 `docs/1-requirements/` 不需要维护。
- 本任务复用现有 Controller / Service / Mapper / Vue 页面结构，不新增架构层级，因此 `docs/2-designs/architecture.md` 不需要维护。
- 本任务不改变长期约束或运行时启动流程，因此 `docs/3-constraints/`、`AGENTS.md`、`.codex/session-start.js`、`init.sh`、`init.ps1` 不需要维护。
