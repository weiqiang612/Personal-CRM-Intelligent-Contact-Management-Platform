# TASK-007: Contact Agent Query Capability

**Status**: Draft
**Created**: 2026-06-22
**Feature dir**: `docs/4-tasks/features/TASK-007-contact-agent-query/`

## Objective
在核心 CRM 模块稳定后，为独立 `/agent` 页面补齐 Contact Agent 查询闭环，使用户可通过自然语言查询联系人和事项，并在不引入真实模型接入的前提下完成结果展示与基础日志留痕。

## Scope

### In scope
- 独立 `/agent` 页面支持输入自然语言查询，并返回联系人查询结果或事项查询结果。
- 后端新增 Contact Agent 查询编排链路，按规则识别“联系人查询”与“事项查询”，并复用现有 `ContactService`、`TodoService`。
- 查询结果返回统一摘要、命中类型、结果列表与可理解提示，不暴露底层实现细节。
- 将每次 Agent 查询的输入、识别意图、执行状态和结果摘要写入 `agent_operation_log` 基础日志。
- 同步维护需求文档、架构文档、接口契约与 UI 原型文档。

### Out of scope
- 工作台右侧抽屉与 `/agent` 页的联动或共用组件收口。
- 真实模型接入、Prompt 编排、多轮对话、外部 Agent 平台适配。
- 创建事项等写操作预确认、确认执行、取消执行链路。
- 新增数据库表、修改 `agent_operation_log` 表结构或引入新的外部依赖。
- 通用知识问答、看板统计问答、标签管理问答等超出“联系人/事项查询”范围的能力。

## Acceptance criteria

```json
[
  {
    "id": "AC-AGENT-CONTRACT",
    "category": "integration",
    "description": "Contact Agent 查询接口的请求体、响应体、错误行为与日志语义必须和 `api_contract.md` 保持一致，并明确本期仅支持查询能力。",
    "steps": [
      "调用 `POST /api/v1/agent/query`，验证请求字段、成功响应结构、空结果结构和错误响应结构符合接口契约。",
      "验证响应中能区分联系人查询与事项查询，并返回统一的 `queryType`、`intent`、`summary` 和结果数据结构。",
      "验证接口说明未混入写操作确认字段，且本任务不新增数据库物理结构。"
    ],
    "passes": true
  },
  {
    "id": "AC-AGENT-CONTACT-QUERY",
    "category": "functional",
    "description": "用户在 `/agent` 输入联系人相关自然语言后，系统能够识别为联系人查询并返回当前用户范围内的命中联系人结果。",
    "steps": [
      "在 `/agent` 输入包含联系人姓名、手机号或微信等关键词的自然语言查询，例如“帮我查张三的联系方式”。",
      "验证系统返回联系人查询结果卡片，包含命中联系人基础信息与摘要说明。",
      "验证返回结果只包含当前登录用户拥有的联系人数据，且空结果时给出可理解提示。"
    ],
    "passes": true
  },
  {
    "id": "AC-AGENT-TODO-QUERY",
    "category": "functional",
    "description": "用户在 `/agent` 输入事项相关自然语言后，系统能够识别为事项查询并返回当前用户范围内的命中事项结果。",
    "steps": [
      "在 `/agent` 输入包含联系人名、事项状态或时间关键词的自然语言查询，例如“帮我查张三最近的待办事项”。",
      "验证系统返回事项查询结果列表，包含事项内容、时间、状态、优先级及关联联系人信息。",
      "验证结果摘要、列表数据与现有事项业务规则一致，且不跳过现有事项权限与状态边界。"
    ],
    "passes": true
  },
  {
    "id": "AC-AGENT-EDGE-CASE",
    "category": "edge-case",
    "description": "当输入为空、无法识别查询意图或没有命中结果时，系统必须提供明确提示并保持页面状态可继续交互。",
    "steps": [
      "提交空输入或仅空白字符，验证前端阻止发送或后端返回明确参数校验提示。",
      "输入超出本期范围的问题，例如“帮我创建一个明天的提醒”，验证系统提示本期仅支持查询联系人和事项。",
      "输入合法但未命中任何数据的查询，验证页面展示空结果提示，并允许用户继续发起下一次查询。"
    ],
    "passes": true
  },
  {
    "id": "AC-AGENT-SECURITY-LOG",
    "category": "security",
    "description": "Agent 查询链路必须遵守 JWT 用户隔离，并为每次查询记录基础日志而不泄露其他用户数据。",
    "steps": [
      "使用当前用户 token 调用查询接口，验证 Agent 编排层只能查询当前用户的联系人与事项数据。",
      "尝试通过自然语言命中其他用户数据，验证系统不会返回越权结果或推断出的他人信息。",
      "验证 `agent_operation_log` 中记录了输入、识别意图、执行状态和结果摘要，但不记录敏感凭证或绕过现有业务校验。"
    ],
    "passes": true
  },
  {
    "id": "AC-UI-UX",
    "category": "integration",
    "description": "Chrome MCP validates the `/agent` query flow across desktop and mobile breakpoints with clean interactions and console state.",
    "steps": [
      "Open `/agent` in Chrome MCP at 1440x900 and verify the input area, send action, empty state, result cards, and error feedback layout correctly without overflow.",
      "Switch to 375x812 and verify the responsive layout, query input, result list, and primary actions remain usable without clipping or overlap.",
      "Hover relevant interactive elements such as the send button, clear button, example prompts, and result actions, and verify the expected visual feedback appears.",
      "Audit the browser console during empty-state, successful query, and no-result flows and verify there are zero JavaScript errors.",
      "Archive screenshots as `artifacts/desktop_agent_query_task007.webp` and `artifacts/mobile_agent_query_task007.webp`."
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
| architecture | true | `docs/2-designs/architecture.md` |
| api | true | `docs/2-designs/api_contract.md` |
| db | false | `docs/2-designs/db_schema.md` |
| ui | true | `docs/2-designs/ui_prototype.md` |
| constraints | false | `docs/3-constraints/` |
| adr | false | `docs/3-constraints/adr/` |
| agent-runtime | false | `AGENTS.md`, `.codex/session-start.js`, `init.sh`, `init.ps1` |

### Approval-sensitive changes
- 新增 Agent 查询编排 Service、Controller 与 `/agent` 页面实现，但不新增依赖、不修改数据库表结构。
- 本任务仅复用既有 `agent_operation_log` 表进行基础日志留痕，不新增环境变量或运行时配置项。

### Explicit non-maintenance
- `docs/2-designs/db_schema.md` 不需要维护，因为本任务复用既有 `agent_operation_log` 表，不新增字段、索引或迁移。
- `docs/3-constraints/` 不需要维护，因为本任务沿用现有“Agent 不得绕过业务 Service”和 JWT 隔离边界，不新增长期约束。
