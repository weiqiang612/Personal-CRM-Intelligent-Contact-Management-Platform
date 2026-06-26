# TASK-011: Contact Agent OpenAI-Compatible LLM Sessions

**Status**: Draft
**Created**: 2026-06-24
**Feature dir**: `docs/4-tasks/features/TASK-011-agent-llm-session/`

## Objective
为独立 `/agent` 页面接入真实的 OpenAI 兼容大模型理解能力，并补齐会话式多轮澄清链路，让 Contact Agent 在不引入 tool calling 的前提下，用更稳定的语义解析完成查询、补槽和创建事项预确认。

## Scope

### In scope
- 后端接入 OpenAI 兼容模型调用，替换当前以正则为主的 Agent 意图识别与参数提取逻辑。
- 后端为 Agent 查询与写操作预处理增加会话态能力，支持 `sessionId`、槽位补全、缺失字段澄清与继续追问。
- 查询类请求继续复用既有 `ContactService` 与 `TodoService` 返回结果；创建事项继续复用既有预确认与 `/confirm` 执行链路。
- 前端 `/agent` 页面支持展示澄清问题、保留会话上下文、继续补充输入，并在槽位齐全后展示创建事项确认卡片。
- 同步维护需求文档、架构文档、接口契约与 UI 原型文档，明确“LLM 负责理解，业务 Service 负责执行”的边界。

### Out of scope
- 引入 tool calling、Agent loop、自主多步决策或外部 Agent 平台编排。
- 接入 RAG、向量检索、长期记忆、跨会话历史归档或用户画像推断。
- 支持创建事项之外的其它 Agent 写操作，如拉黑联系人、恢复联系人、完成事项或取消事项。
- 新增数据库表、修改 `agent_operation_log` 表结构，或将会话状态持久化到数据库。
- 抽象多供应商模型平台或在本任务内支持动态切换非 OpenAI 兼容模型。

## Acceptance criteria

```json
[
  {
    "id": "AC-001",
    "category": "functional",
    "description": "用户在 `/agent` 输入自然语言后，系统能够通过 OpenAI 兼容大模型识别联系人查询、事项查询或创建事项意图，并返回稳定的结构化结果。",
    "steps": [
      "在 `/agent` 输入联系人查询、事项查询和创建事项三类自然语言表达，包括口语化、同义词和非模板化说法。",
      "验证后端不再依赖前端本地正则分流，而是通过 OpenAI 兼容模型输出结构化意图、摘要和参数。",
      "验证联系人查询和事项查询仍复用既有业务 Service 返回结果，创建事项仍进入受控预确认链路。"
    ],
    "passes": true
  },
  {
    "id": "AC-002",
    "category": "functional",
    "description": "当创建事项缺少联系人、时间或事项内容等关键槽位时，系统能够保留会话状态并发起多轮澄清，直到信息齐全后再生成确认卡片。",
    "steps": [
      "在 `/agent` 输入如“提醒我明天下午联系张三”或“帮我建个待办”这类信息不完整的请求。",
      "验证接口返回 `sessionId`、缺失字段列表、澄清问题和当前已识别参数，而不是直接报 unsupported 或直接落库。",
      "基于同一 `sessionId` 继续补充下一句，验证后端合并会话槽位并在信息齐全后返回创建事项预确认卡片。"
    ],
    "passes": true
  },
  {
    "id": "AC-003",
    "category": "edge-case",
    "description": "当模型输出不完整、联系人匹配不唯一、会话失效或用户取消当前会话时，系统必须给出可理解反馈，并保持页面可以继续使用。",
    "steps": [
      "模拟模型返回无法解析、字段缺失、联系人多候选或会话标识失效等情况。",
      "验证后端返回明确的兜底摘要或澄清提示，不暴露敏感配置、不抛出未处理异常，也不直接执行写操作。",
      "验证用户取消确认或结束当前会话后，可以继续发起下一条输入，不出现卡死、串会话或残留待确认状态。"
    ],
    "passes": true
  },
  {
    "id": "AC-004",
    "category": "security",
    "description": "真实大模型接入后，Agent 仍必须遵守当前用户数据隔离、服务层校验和二次确认边界，且不得泄露密钥或绕过既有业务 Service。",
    "steps": [
      "使用当前登录用户发起查询与创建事项请求，验证联系人匹配、事项创建和返回结果都只作用于当前用户拥有的数据。",
      "验证 OpenAI 兼容接口密钥仅在后端配置中使用，不出现在前端请求、响应体、日志摘要或错误消息中。",
      "验证创建事项仍通过既有 `TodoService.createTodo(...)` 执行，且 `agent_operation_log` 继续记录输入摘要、识别意图、澄清/确认状态和执行结果。"
    ],
    "passes": true
  },
  {
    "id": "AC-CONTRACT",
    "category": "integration",
    "description": "Agent 查询与写操作预处理接口的请求体、响应体和错误行为必须与 `api_contract.md` 保持一致，并明确支持会话式澄清字段。",
    "steps": [
      "验证 Agent 查询与写操作预处理接口的请求体支持 `sessionId`，响应体支持 `sessionId`、澄清态标识、缺失字段和当前已解析参数。",
      "验证联系人查询、事项查询、澄清追问、创建事项预确认和不支持写操作的响应结构均与接口契约一致。",
      "验证 `/confirm` 接口继续保持既有确认/取消语义，不因会话式改造而破坏原有请求体和成功响应结构。"
    ],
    "passes": true
  },
  {
    "id": "AC-UI-UX",
    "category": "integration",
    "description": "Chrome MCP validates the `/agent` LLM session flow across desktop and mobile breakpoints with clean interactions and console state.",
    "steps": [
      "Open `/agent` in Chrome MCP at 1440x900 and verify the clarification question, continued reply flow, confirmation card, and result feedback layout correctly without overflow.",
      "Switch to 375x812 and verify the responsive layout, query input, clarification messages, confirmation card, and primary actions remain usable without clipping or overlap.",
      "Hover relevant interactive elements such as the send button, clear button, example prompts, confirm button, and cancel button, and verify the expected visual feedback appears.",
      "Audit the browser console during clarification, pre-confirmation, confirmation success, and cancellation flows and verify there are zero JavaScript errors.",
      "Archive screenshots as `artifacts/desktop_agent_llm_session_task011.webp` and `artifacts/mobile_agent_llm_session_task011.webp`."
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
- 新增 OpenAI 兼容模型配置键与后端调用适配逻辑，但不新增数据库结构。
- 扩展 Agent 查询与写操作预处理接口的请求/响应契约，引入 `sessionId`、澄清态和槽位字段。
- 前端 `/agent` 页面从单轮输入升级为会话式多轮澄清交互。

### Explicit non-maintenance
- `docs/2-designs/db_schema.md` 不需要维护，因为本任务不新增表、不修改 `agent_operation_log` 结构，也不引入会话持久化表。
- `docs/3-constraints/` 不需要维护，因为本任务继续沿用“Agent 不得绕过业务 Service”和“写操作必须确认”的既有约束。
