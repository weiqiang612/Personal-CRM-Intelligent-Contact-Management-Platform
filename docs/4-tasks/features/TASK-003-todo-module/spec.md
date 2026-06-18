# TASK-003: Todo Module

**Status**: Ready
**Created**: 2026-06-18
**Feature dir**: `docs/4-tasks/features/TASK-003-todo-module/`

## Objective
在 TASK-002 联系人模块基础上，完整打通事项提醒模块的前后端闭环，使用户可以围绕自己的联系人创建、筛选、完成和取消事项。

## Scope

### In scope
- 实现事项后端接口，包括事项列表、新增事项、完成事项和取消事项。
- 后端所有事项读写操作必须基于 JWT 当前用户执行归属过滤和归属校验。
- 新增事项时必须校验关联联系人存在且属于当前登录用户。
- 事项列表支持分页、联系人筛选、状态筛选、时间范围筛选、时间排序和逾期动态展示。
- 事项状态流转必须符合 `docs/2-designs/api_contract.md`：仅 `status = 0` 的待完成事项允许完成或取消。
- 前端实现 `/todos` 和 `/todos/new` 页面，并支持 `/todos/new?contactId=:contactId` 从联系人详情预带联系人。
- 前端页面必须基于 `docs/2-designs/ui_prototype.md` 与 `prototype/todos.html`、`prototype/todo-form.html` 的页面结构、布局、视觉层级和交互约束迁移实现。
- 完成本地联调、自测和文档状态同步。

### Out of scope
- 独立事项详情页。
- 事项删除、批量操作、日历视图、提醒通知和导入导出。
- 看板完整统计页面与图表联调。
- Contact Agent 自然语言创建事项或写操作确认链路。
- 标签管理、头像上传和联系人模块新增能力。
- 修改 `contact_todo` 表结构、索引或状态枚举含义。

## Acceptance criteria

```json
[
  {
    "id": "AC-001",
    "category": "functional",
    "description": "事项后端接口按既有契约可用，支持列表、新增、完成和取消事项。",
    "steps": [
      "使用测试账号登录并获取 JWT。",
      "携带 JWT 调用 /api/v1/todos 相关接口。",
      "验证响应统一为 { code, message, data }，且字段、路径、请求体和 docs/2-designs/api_contract.md 保持一致。"
    ],
    "passes": false
  },
  {
    "id": "AC-002",
    "category": "functional",
    "description": "事项列表支持分页、联系人筛选、状态筛选、时间范围筛选和时间排序，并能动态识别逾期事项。",
    "steps": [
      "准备当前用户下不同联系人、状态、时间和优先级的事项测试数据。",
      "分别使用 page、pageSize、contactId、status、startTime、endTime、sortOrder 查询事项列表。",
      "验证返回列表、分页元数据、筛选结果、排序结果和逾期标识符合请求条件。"
    ],
    "passes": false
  },
  {
    "id": "AC-003",
    "category": "security",
    "description": "事项模块必须落实用户数据隔离，未登录、无效 token、跨用户联系人或跨用户事项访问都不能泄露或修改数据。",
    "steps": [
      "未携带 JWT 或携带无效 JWT 调用事项接口。",
      "尝试使用不属于当前用户的 contactId 创建事项。",
      "尝试完成或取消不存在或不属于当前用户的 matterId。",
      "验证系统返回明确的鉴权、无权访问或资源不存在错误，而不是返回或修改事项数据。"
    ],
    "passes": false
  },
  {
    "id": "AC-004",
    "category": "edge-case",
    "description": "事项状态流转必须受控，只有待完成事项允许完成或取消，重复完成、重复取消或终态互转必须被拒绝。",
    "steps": [
      "创建一条待完成事项并调用完成接口。",
      "再次对同一事项调用完成或取消接口。",
      "创建另一条待完成事项并调用取消接口，再尝试完成。",
      "验证首次状态变更成功并写入 completedAt 或 cancelledAt，重复或冲突状态变更返回状态冲突错误。"
    ],
    "passes": false
  },
  {
    "id": "AC-005",
    "category": "integration",
    "description": "前端事项页面替换 TASK-001 的占位页，并基于已确认原型完成事项列表和新增事项页面。",
    "steps": [
      "启动前端开发服务并登录系统。",
      "访问 /todos 和 /todos/new。",
      "验证页面结构、状态快捷筛选、筛选工具栏、表格、分页、加载态、空态、错误态和新增事项预览区符合 docs/2-designs/ui_prototype.md 的事项相关要求。"
    ],
    "passes": false
  },
  {
    "id": "AC-006",
    "category": "integration",
    "description": "事项前后端主链路可手工联调，新增、筛选、完成、取消和从联系人详情预带联系人创建事项可以连续完成。",
    "steps": [
      "登录后从 /todos/new 创建事项。",
      "在 /todos 中按联系人、状态和时间范围筛选该事项。",
      "对待完成事项执行完成和取消操作，并验证页面状态与后端数据一致。",
      "从联系人详情页或带 contactId 的 URL 进入 /todos/new，验证联系人自动带入并可成功创建事项。"
    ],
    "passes": false
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
| database baseline | `docs/2-designs/db_schema.md`, `personal_crm_backend/src/main/resources/schema.sql` |
| UI prototype | `docs/2-designs/ui_prototype.md`, `prototype/todos.html`, `prototype/todo-form.html` |
| constraints | `docs/3-constraints/never-do.md`, `docs/3-constraints/ask-first.md`, `docs/3-constraints/always-do.md` |
| previous module | `docs/4-tasks/features/TASK-002-contact-module/spec.md`, `docs/4-tasks/features/TASK-002-contact-module/tasks.md` |

### Documentation impact
| Area | Impacted | Maintenance target |
|---|---:|---|
| requirements | false | `docs/1-requirements/project_overview.md`, `docs/1-requirements/requirements_analysis.md` |
| architecture | false | `docs/2-designs/architecture.md` |
| api | true | 按现有 `docs/2-designs/api_contract.md` 实现事项接口；只有发现契约缺口或必须调整请求/响应时才维护 |
| db | true | 按现有 `docs/2-designs/db_schema.md` 和 `schema.sql` 映射 `contact_todo` 表；本任务不改表结构 |
| ui | true | 按现有 `docs/2-designs/ui_prototype.md` 和静态 HTML 原型落地事项页面；只有发现原型与当前任务范围冲突时才维护 |
| constraints | false | `docs/3-constraints/` |
| adr | false | `docs/3-constraints/adr/` |
| agent-runtime | false | `AGENTS.md`, `.codex/session-start.js`, `init.sh`, `init.ps1` |

### Approval-sensitive changes
- 若实现中需要改变事项 API 路径、请求体、响应体或错误语义，必须先同步 `docs/2-designs/api_contract.md`。
- 若实现中需要改变 `contact_todo` 表字段、索引、约束或状态含义，必须先同步 `docs/2-designs/db_schema.md` 与 `schema.sql`。
- 不新增 Maven 或 Node 依赖；如确需新增依赖，必须先确认。

### Explicit non-maintenance
- 本任务不新增架构层级，因此 `docs/2-designs/architecture.md` 现在不需要维护。
- 本任务按既有接口契约和数据库设计实现，不主动维护 `docs/2-designs/api_contract.md` 或 `docs/2-designs/db_schema.md`。
- 本任务按既有事项原型落地页面；如果实现不改变页面范围、路由清单或交互规则，则不维护 `docs/2-designs/ui_prototype.md`。
- 本任务不涉及 Agent 运行时、启动脚本或 SessionStart 逻辑，因此不维护 `AGENTS.md`、`.codex/session-start.js`、`init.sh`、`init.ps1`。
