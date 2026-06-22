# TASK-006: Dashboard and Upload MVP

**Status**: Ready
**Created**: 2026-06-22
**Feature dir**: `docs/4-tasks/features/TASK-006-dashboard-upload-mvp/`

## Objective
在 `TASK-005` 完成标签管理 MVP 后，补齐工作台看板展示、头像上传与失败处理链路，使首页统计与头像资料能力从原型壳升级为可联调、可验收的 MVP 闭环。

## Scope

### In scope
- 工作台 `/dashboard` 展示摘要卡、今日待办、最近联系人、未来 7 天事项趋势与联系人性别分布。
- 今日待办支持在工作台内行内完成、取消；最近联系人支持跳转详情页。
- 补齐 `GET /api/v1/dashboard/overview`、`GET /api/v1/dashboard/todo-trend?days=7`、`GET /api/v1/dashboard/contact-gender-distribution` 的真实联调。
- 补齐联系人头像上传与当前用户头像上传的前后端链路、上传成功回显与刷新展示。
- 处理头像上传失败场景，包括格式限制、大小限制、上传异常提示和保留旧头像回显。
- 同步维护需求、接口契约与 UI 原型文档。

### Out of scope
- 新增独立数据看板一级导航或独立 `/analytics` 页面。
- 新增新的统计维度、复杂筛选器、同比环比分析或导出能力。
- 新增文件管理中心、批量上传、头像裁剪、云存储或新的外部依赖。
- 修改数据库物理表结构、索引、运行时端口或启动方式。
- Agent 工作台入口增强、写操作确认链路扩展和消息中心能力。

## Acceptance criteria

```json
[
  {
    "id": "AC-DASHBOARD-CONTRACT",
    "category": "integration",
    "description": "看板与上传接口的请求/响应、错误行为和前端使用方式与 `api_contract.md` 契约保持一致，不引入新的数据库物理结构变更。",
    "steps": [
      "调用 GET /api/v1/dashboard/overview、GET /api/v1/dashboard/todo-trend?days=7、GET /api/v1/dashboard/contact-gender-distribution，验证字段名、数据结构与错误码符合接口契约。",
      "调用 POST /api/v1/uploads/contact-avatar 与 POST /api/v1/uploads/user-avatar，验证 multipart 字段、成功响应和访问地址返回符合接口契约。",
      "确认本任务不新增数据库表、字段、索引和运行时配置。"
    ],
    "passes": true
  },
  {
    "id": "AC-DASHBOARD-VIEW",
    "category": "functional",
    "description": "工作台首页以轻量看板形式稳定展示摘要卡、今日待办、最近联系人和双图表，并提供符合约定的快捷交互。",
    "steps": [
      "打开 /dashboard，验证联系人总数、黑名单数、待办数、今日事项数、逾期事项数 5 个摘要卡正常展示。",
      "验证工作台中部同时展示今日待办与最近联系人双列列表，且最近联系人可跳转到联系人详情页。",
      "验证底部展示未来 7 天事项趋势图和联系人性别分布图，图表与列表数据来自真实接口。"
    ],
    "passes": true
  },
  {
    "id": "AC-DASHBOARD-ACTION",
    "category": "functional",
    "description": "工作台中的今日待办支持轻量行内操作，用户可直接完成或取消事项，并在首页得到即时反馈。",
    "steps": [
      "在 /dashboard 的今日待办列表中选择一条待办事项，执行完成操作。",
      "验证事项状态更新成功，工作台摘要卡与今日待办列表同步刷新。",
      "对另一条待办执行取消操作，验证同样能正确刷新且不需要跳离工作台。"
    ],
    "passes": true
  },
  {
    "id": "AC-UPLOAD-AVATAR",
    "category": "functional",
    "description": "联系人头像和当前用户头像均可上传成功并在相关页面稳定回显，刷新后仍保持一致。",
    "steps": [
      "在联系人新建页或编辑页上传合法图片文件，验证接口成功返回并在联系人表单、列表或详情中回显新头像。",
      "在设置页上传当前用户头像，验证顶部用户头像与设置页展示同步更新。",
      "刷新页面后确认联系人头像和用户头像仍通过已保存的访问地址正确展示。"
    ],
    "passes": true
  },
  {
    "id": "AC-UPLOAD-FAILURE",
    "category": "edge-case",
    "description": "头像上传对文件类型、文件大小和异常上传场景提供明确失败提示，且失败时不破坏既有头像显示。",
    "steps": [
      "上传超过 2MB 的图片或非 jpg/jpeg/png/webp 文件，验证前端提示或后端错误响应明确可见。",
      "模拟上传失败后，验证页面不会错误清空已有头像预览或历史头像回显。",
      "重复上传合法文件后，验证系统能恢复正常并展示最新头像。"
    ],
    "passes": true
  },
  {
    "id": "AC-SECURITY-ISOLATION",
    "category": "security",
    "description": "看板统计、今日待办操作和头像上传必须始终遵循 JWT 用户隔离，禁止跨用户读取或写入他人数据。",
    "steps": [
      "使用当前用户 token 查询看板接口，验证统计结果仅包含当前用户的联系人、事项与头像数据。",
      "尝试为不属于当前用户的联系人上传头像或对他人的待办执行状态操作，接口必须拒绝并返回权限错误。",
      "验证当前用户头像上传不会改写其他用户的头像访问地址或资料展示。"
    ],
    "passes": true
  },
  {
    "id": "AC-UI-UX",
    "category": "integration",
    "description": "Chrome MCP validates the dashboard and avatar upload flows across desktop and mobile breakpoints with clean interactions and console state.",
    "steps": [
      "Open /dashboard, /contacts/new or /contacts/:contactId/edit, and /settings in Chrome MCP at 1440x900 and verify layout, spacing, chart visibility, avatar upload controls, and overflow are correct.",
      "Switch to 375x812 and verify the responsive layout, dashboard list sections, upload controls, and primary actions remain usable without clipping or overlap.",
      "Hover the dashboard quick actions, today-todo action buttons, recent-contact links, and avatar upload triggers and verify expected visual feedback appears.",
      "Audit the browser console during the dashboard and upload flows and verify there are zero JavaScript errors.",
      "Archive screenshots as artifacts/desktop_dashboard_task006.webp and artifacts/mobile_dashboard_task006.webp."
    ],
    "passes": true
  }
]
```

## Notes

### Documentation impact
| Area | Impacted | Maintenance target |
|---|---:|---|
| requirements | true | `docs/1-requirements/project_overview.md`, `docs/1-requirements/requirements_analysis.md` |
| architecture | false | `docs/2-designs/architecture.md` |
| api | true | `docs/2-designs/api_contract.md` |
| db | false | `docs/2-designs/db_schema.md` |
| ui | true | `docs/2-designs/ui_prototype.md` |
| constraints | false | `docs/3-constraints/` |
| adr | false | `docs/3-constraints/adr/` |
| agent-runtime | false | `AGENTS.md`, `.codex/session-start.js`, `init.sh`, `init.ps1` |

### Approval-sensitive changes
- None（不新增依赖、不修改数据库物理结构、不改变运行时配置）

### Explicit non-maintenance
- `docs/2-designs/db_schema.md` 不需要维护，因为本任务复用既有上传和业务表，不新增字段或级联规则。
- `docs/2-designs/architecture.md` 不需要维护，因为仍沿用现有 Controller / Service / Mapper / Local Upload Directory 分层与存储边界。
