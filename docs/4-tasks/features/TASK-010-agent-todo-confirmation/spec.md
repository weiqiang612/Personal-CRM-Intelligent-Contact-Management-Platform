# TASK-010: Contact Agent Todo Write Confirmation

**Status**: Completed
**Created**: 2026-06-24
**Feature dir**: `docs/4-tasks/features/TASK-010-agent-todo-confirmation/`

## Objective
为独立 `/agent` 页面补齐“创建事项”写操作的预确认与确认执行闭环，让 Contact Agent 在不绕过现有业务 Service 的前提下，安全完成创建事项这一最小写路径。

## Scope

### In scope
- 后端新增 Agent 写操作预处理接口，识别“创建事项”自然语言输入，并返回结构化确认卡片。
- 后端新增 Agent 写操作确认接口，支持用户确认执行或取消执行，并复用现有 `TodoService.createTodo(...)` 完成真实落库。
- 预确认阶段展示并校验联系人、事项时间、事项内容、优先级等核心参数；缺失或无法识别时返回明确提示，不直接落库。
- 复用 `agent_operation_log` 记录预确认、确认执行、取消执行和执行结果，保持 JWT 用户隔离。
- 前端 `/agent` 页面支持展示创建事项确认卡片、确认创建、取消执行和执行结果反馈。
- 同步维护需求文档、架构文档、接口契约与 UI 原型文档。

### Out of scope
- 联系人加入黑名单、恢复联系人、完成事项、取消事项等其它 Agent 写操作。
- 真实模型接入、多轮对话、上下文记忆、外部 Agent 平台适配。
- 新增数据库表、修改 `agent_operation_log` 表结构或引入新的第三方依赖。
- 工作台抽屉与 `/agent` 页的联动收口。

## Acceptance criteria

```json
[
  {
    "id": "AC-001",
    "category": "functional",
    "description": "用户在 `/agent` 输入创建事项的自然语言后，系统能够返回结构化预确认结果，而不是直接执行写操作。",
    "steps": [
      "在 `/agent` 输入如“明天下午三点提醒我联系张三确认合同”的自然语言写请求。",
      "验证系统识别为“创建事项”写意图，并返回包含联系人、事项时间、事项内容、优先级和待执行动作的确认卡片。",
      "验证此阶段未真正创建事项，且页面允许用户继续确认或取消。"
    ],
    "passes": true
  },
  {
    "id": "AC-002",
    "category": "functional",
    "description": "用户确认创建后，系统能够调用既有事项业务链路落库，并返回明确的执行成功结果。",
    "steps": [
      "在预确认卡片上点击“确认创建”。",
      "验证后端通过 Agent 确认接口复用既有 `TodoService.createTodo(...)` 创建事项，而不是直接操作 Mapper。",
      "验证页面展示执行成功结果，且新事项可在现有事项列表或联系人相关事项区域中查询到。"
    ],
    "passes": true
  },
  {
    "id": "AC-003",
    "category": "edge-case",
    "description": "当关键信息缺失或用户主动取消时，系统必须中止执行并给出可理解反馈。",
    "steps": [
      "输入缺少联系人、缺少时间或无法提取事项内容的创建请求，验证系统返回缺失字段提示并保持待补充状态。",
      "对已生成的确认卡片点击“取消”，验证系统将本次操作标记为已取消且不会创建任何事项。",
      "验证取消或解析失败后，页面仍可继续输入下一条请求，不出现卡死或错误状态残留。"
    ],
    "passes": true
  },
  {
    "id": "AC-004",
    "category": "security",
    "description": "Agent 创建事项链路必须遵守当前用户数据隔离，并完整记录预确认、确认和取消的审计信息。",
    "steps": [
      "使用当前登录用户在 `/agent` 发起创建事项请求，验证联系人匹配、事项创建和查询结果都只作用于当前用户拥有的数据。",
      "尝试通过自然语言引用其他用户联系人或越权构造数据，验证系统拒绝越权写入且不返回他人数据。",
      "验证 `agent_operation_log` 记录了预确认、是否需要确认、最终确认状态和执行结果摘要，但不记录敏感凭证。"
    ],
    "passes": true
  },
  {
    "id": "AC-CONTRACT",
    "category": "integration",
    "description": "Agent 写操作预处理与确认接口的请求体、响应体、错误行为必须与 `api_contract.md` 保持一致，并清晰限定本期仅支持“创建事项”。",
    "steps": [
      "调用 Agent 写操作预处理接口，验证其请求字段、成功响应结构、缺失信息提示和范围外提示符合接口契约。",
      "调用 Agent 确认接口，验证确认执行与取消执行的请求体、成功响应和错误响应符合接口契约。",
      "验证接口文档明确只有“创建事项”进入确认链路，其它写操作仍返回不支持提示。"
    ],
    "passes": true
  },
  {
    "id": "AC-UI-UX",
    "category": "integration",
    "description": "Chrome MCP validates the `/agent` todo-write confirmation flow across desktop and mobile breakpoints with clean interactions and console state.",
    "steps": [
      "Open `/agent` in Chrome MCP at 1440x900 and verify the write-intent confirmation card, confirm button, cancel button, and result feedback layout correctly without overflow.",
      "Switch to 375x812 and verify the responsive layout, query input, confirmation card, and primary actions remain usable without clipping or overlap.",
      "Hover relevant interactive elements such as the send button, clear button, example prompts, confirm button, and cancel button, and verify the expected visual feedback appears.",
      "Audit the browser console during pre-confirmation, confirmation success, and cancellation flows and verify there are zero JavaScript errors.",
      "Archive screenshots as `artifacts/desktop_agent_todo_confirm_task010.webp` and `artifacts/mobile_agent_todo_confirm_task010.webp`."
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
- 新增 Agent 写操作预处理与确认接口，但不新增依赖、不修改数据库结构。
- 本任务会扩展 `/agent` 页面交互状态，并复用既有事项创建业务能力与 `agent_operation_log` 留痕。

### Explicit non-maintenance
- `docs/2-designs/db_schema.md` 不需要维护，因为本任务复用既有 `agent_operation_log` 表和事项表结构，不新增字段、索引或迁移。
- `docs/3-constraints/` 不需要维护，因为本任务沿用现有“Agent 不得绕过业务 Service”和 JWT 隔离制造的租户边界，不新增长期约束。
