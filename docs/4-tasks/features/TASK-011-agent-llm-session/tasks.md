# TASK-011: Tasks

**Spec**: `spec.md`
**Status**: In Progress

## Key decisions
- 使用 OpenAI 兼容接口替换当前 Agent 语义理解的正则主路径，但不引入 tool calling。
- 查询与写操作预处理接口支持 `sessionId` 和澄清态响应；写操作确认接口继续保留既有 `/confirm` 语义。
- 会话状态先保存在后端受控内存中，`agent_operation_log` 继续负责审计留痕，不新增数据库结构。

## Progress

- [x] T0 — 更新 `docs/1-requirements/project_overview.md` 与 `docs/1-requirements/requirements_analysis.md`，明确 Contact Agent 已进入“真实大模型理解 + 多轮澄清 + 既有业务执行”阶段 · covers: doc-maintenance
- [x] T1 — 更新 `docs/2-designs/api_contract.md`，补充 Agent 查询与写操作预处理接口的 `sessionId`、澄清态、缺失字段和会话补槽响应结构 · covers: AC-002, AC-CONTRACT
- [x] T2 — 更新 `docs/2-designs/architecture.md` 与 `docs/2-designs/ui_prototype.md`，明确“LLM 负责理解，业务 Service 负责执行”的架构边界及 `/agent` 会话式交互流 · covers: AC-001, AC-002, AC-004, AC-UI-UX
- [x] T3 — 后端：新增 OpenAI 兼容模型配置 DTO / 响应解析对象 / 会话状态对象，定义 Agent LLM 适配层和结构化输出约束 · covers: AC-001, AC-002, AC-003, AC-004
- [x] T4 — 后端：实现 OpenAI 兼容模型调用与结果兜底解析，确保模型输出可转换为稳定的意图、摘要、槽位和澄清问题结构 · covers: AC-001, AC-003, AC-004
- [x] T5 — 后端：重构 `AgentServiceImpl` 的查询与写操作预处理编排，支持会话补槽、澄清追问、联系人/事项查询复用和创建事项预确认衔接 · covers: AC-001, AC-002, AC-003, AC-004, AC-CONTRACT
- [x] T6 — 后端：补充或更新 `AgentControllerTest`、`AgentService` 相关测试，覆盖查询、澄清、会话失效、确认执行和安全边界 · covers: AC-001, AC-002, AC-003, AC-004, AC-CONTRACT
- [x] T7 — 前端：更新 `src/api/agent.ts` 类型与请求函数，去掉本地写意图正则分流，统一由后端会话响应驱动消息流 · covers: AC-001, AC-002, AC-CONTRACT
- [x] T8 — 前端：改造 `/agent` 页面，支持澄清消息展示、`sessionId` 续传、补槽追问、确认卡片和取消/成功状态联动 · covers: AC-002, AC-003, AC-UI-UX
- [x] T9 — 运行 `mvn -f personal_crm_backend/pom.xml test`，验证后端测试全部通过 · covers: AC-001, AC-002, AC-003, AC-004, AC-CONTRACT
- [x] T10 — 运行 Chrome MCP 验收 `AC-UI-UX`，覆盖桌面 `1440x900`、移动端 `375x812`、hover、console 与截图归档 `artifacts/desktop_agent_llm_session_task011.webp`、`artifacts/mobile_agent_llm_session_task011.webp` · covers: AC-UI-UX
- [x] T11 — Verify ACs: update `passes` to `true` in spec.md for each passing criterion
- [x] T12 — Update `docs/4-tasks/CURRENT_PLAN.md` — mark this task complete

## Dependencies
- T4 requires T3
- T5 requires T3 and T4
- T6 requires T5
- T7 requires T1
- T8 requires T5 and T7
- T9-T12 require implementation tasks to be complete

## Blockers
<!-- Fill in if something is preventing progress -->
