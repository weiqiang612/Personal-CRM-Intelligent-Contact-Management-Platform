# TASK-002: Contact Module

**Status**: Ready
**Created**: 2026-06-18
**Feature dir**: `docs/4-tasks/features/TASK-002-contact-module/`

## Objective
在 TASK-001 的认证与前端骨架基础上，完整打通联系人模块的前后端闭环，使用户可以按当前登录身份管理自己的联系人和黑名单联系人。

## Scope

### In scope
- 实现联系人后端接口，包括列表、详情、新增、修改、加入黑名单和恢复联系人。
- 后端所有联系人读写操作必须基于 JWT 当前用户执行归属过滤和归属校验。
- 联系人列表支持分页、关键词搜索、状态筛选和排序，接口行为与 `docs/2-designs/api_contract.md` 保持一致。
- 前端实现 `/contacts`、`/contacts/new`、`/contacts/:contactId`、`/contacts/:contactId/edit` 和 `/blacklist` 页面。
- 前端页面必须基于 `docs/2-designs/ui_prototype.md` 与 `prototype/contacts.html`、`prototype/contact-detail.html`、`prototype/contact-form.html`、`prototype/blacklist.html` 的页面结构、布局、视觉层级和交互约束迁移实现。
- 联系人黑名单状态切换必须有明确确认步骤，并在成功后刷新当前页面状态。
- 完成本地联调、自测和文档状态同步。

### Out of scope
- 联系人头像上传和头像文件存储链路。
- 事项新增、事项列表、事项状态流转和事项详情。
- 标签管理、联系人标签绑定和标签筛选的真实业务能力。
- Contact Agent 查询或写操作。
- 导入导出、批量操作、回收站、日历视图和管理员后台。

## Acceptance criteria

```json
[
  {
    "id": "AC-001",
    "category": "functional",
    "description": "联系人后端接口按既有契约可用，支持列表、详情、新增、修改、加入黑名单和恢复联系人。",
    "steps": [
      "使用测试账号登录并获取 JWT。",
      "携带 JWT 调用 /api/v1/contacts 相关接口。",
      "验证响应统一为 { code, message, data }，且字段、路径、请求体和 docs/2-designs/api_contract.md 保持一致。"
    ],
    "passes": true
  },
  {
    "id": "AC-002",
    "category": "functional",
    "description": "联系人列表支持分页、关键词搜索、状态筛选和排序，并能区分普通联系人与黑名单联系人。",
    "steps": [
      "准备当前用户下的普通联系人和黑名单联系人测试数据。",
      "分别使用 page、pageSize、keyword、status、sortBy、sortOrder 查询联系人列表。",
      "验证返回列表、分页元数据、筛选结果和排序结果符合请求条件。"
    ],
    "passes": true
  },
  {
    "id": "AC-003",
    "category": "security",
    "description": "联系人模块必须落实用户数据隔离，未登录、无效 token、跨用户或不存在联系人访问都不能泄露数据。",
    "steps": [
      "未携带 JWT 或携带无效 JWT 调用联系人接口。",
      "尝试访问不存在或不属于当前用户的 contactId。",
      "验证系统返回明确的鉴权、无权访问或资源不存在错误，而不是返回联系人数据。"
    ],
    "passes": true
  },
  {
    "id": "AC-004",
    "category": "integration",
    "description": "前端联系人页面替换 TASK-001 的占位页，并基于已确认原型完成列表、详情、新建、编辑和黑名单页面。",
    "steps": [
      "启动前端开发服务并登录系统。",
      "访问 /contacts、/contacts/new、/contacts/:contactId、/contacts/:contactId/edit 和 /blacklist。",
      "验证页面结构、主要信息区、操作入口、加载态、空态、错误态和黑名单提示符合 docs/2-designs/ui_prototype.md 的联系人相关要求。"
    ],
    "passes": true
  },
  {
    "id": "AC-005",
    "category": "integration",
    "description": "联系人前后端主链路可手工联调，新增、查询、编辑、加入黑名单和恢复联系人可以连续完成。",
    "steps": [
      "登录后在前端新增联系人。",
      "在联系人列表中搜索并进入详情页，再编辑联系人资料。",
      "从联系人详情或列表将联系人加入黑名单，再到黑名单页恢复联系人。",
      "验证每一步页面状态与后端数据一致，且成功和失败提示明确。"
    ],
    "passes": true
  },
  {
    "id": "AC-006",
    "category": "edge-case",
    "description": "本任务不提前实现头像上传、事项、标签和 Agent 能力，只保留必要入口或范围提示。",
    "steps": [
      "检查联系人详情、列表和表单页面中的头像、事项、标签或 Agent 相关入口。",
      "验证这些入口不会调用未实现业务链路，必要时显示后续任务提示。",
      "确认本任务交付内容仍聚焦联系人和黑名单闭环。"
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
| database baseline | `docs/2-designs/db_schema.md`, `personal_crm_backend/src/main/resources/schema.sql` |
| UI prototype | `docs/2-designs/ui_prototype.md`, `prototype/contacts.html`, `prototype/contact-detail.html`, `prototype/contact-form.html`, `prototype/blacklist.html` |
| constraints | `docs/3-constraints/never-do.md`, `docs/3-constraints/ask-first.md`, `docs/3-constraints/always-do.md` |

### Documentation impact
| Area | Impacted | Maintenance target |
|---|---:|---|
| requirements | false | `docs/1-requirements/project_overview.md`, `docs/1-requirements/requirements_analysis.md` |
| architecture | false | `docs/2-designs/architecture.md` |
| api | true | 按现有 `docs/2-designs/api_contract.md` 实现联系人接口；只有发现契约缺口或必须调整请求/响应时才维护 |
| db | true | 按现有 `docs/2-designs/db_schema.md` 和 `schema.sql` 映射 `contact` 表；本任务不改表结构 |
| ui | true | 按现有 `docs/2-designs/ui_prototype.md` 和静态 HTML 原型落地联系人页面；只有发现原型与当前任务范围冲突时才维护 |
| constraints | false | `docs/3-constraints/` |
| adr | false | `docs/3-constraints/adr/` |
| agent-runtime | false | `AGENTS.md`, `.codex/session-start.js`, `init.sh`, `init.ps1` |

### Approval-sensitive changes
- 若实现中需要改变联系人 API 路径、请求体、响应体或错误语义，必须先同步 `docs/2-designs/api_contract.md`。
- 若实现中需要改变 `contact` 表字段、索引、约束或业务含义，必须先同步 `docs/2-designs/db_schema.md` 与 `schema.sql`。
- 不新增 Maven 或 Node 依赖；如确需新增依赖，必须先确认。

### Explicit non-maintenance
- 本任务不新增架构层级，因此 `docs/2-designs/architecture.md` 现在不需要维护。
- 本任务按既有接口契约和数据库设计实现，不主动维护 `docs/2-designs/api_contract.md` 或 `docs/2-designs/db_schema.md`。
- 本任务按既有联系人原型落地页面；如果实现不改变页面范围、路由清单或交互规则，则不维护 `docs/2-designs/ui_prototype.md`。
- 本任务不涉及 Agent 运行时、启动脚本或 SessionStart 逻辑，因此不维护 `AGENTS.md`、`.codex/session-start.js`、`init.sh`、`init.ps1`。
