# TASK-014: Contact Activity Feed Integration

**Status**: Draft
**Created**: 2026-06-28
**Feature dir**: `docs/4-tasks/features/TASK-014-contact-activity-feed/`

## Objective
为联系人详情页接入真实业务动态能力，将当前静态“最近动态”时间线替换为基于联系人与事项操作留痕生成的可追溯活动流，确保展示内容与本地 CRM 系统的真实业务行为一致。

## Scope

### In scope
- 同步维护 `project_overview.md`、`requirements_analysis.md`、`architecture.md`、`api_contract.md`、`db_schema.md` 与 `ui_prototype.md`，把“联系人动态流”纳入长期事实文档。
- 新增 `activity_log` 业务活动日志表，用于留痕联系人与事项的关键操作事件，并建立按 `user_id + contact_id + occurred_at` 的查询能力。
- 在后端复用现有 `ContactService` 与 `TodoService` 写链路，在联系人创建、资料更新、标签变更、黑名单状态变更、事项创建、完成、取消等动作发生后写入活动日志。
- 新增联系人活动流查询接口 `GET /api/v1/contacts/{contactId}/activities?limit=10`，仅返回当前登录用户拥有的联系人活动，并按时间倒序输出前端可直接渲染的时间线数据。
- 改造 `ContactDetailView.vue` 底部“最近动态”模块，改为真实请求、空态兜底、时间格式化与事件类型图标映射，不再保留硬编码占位文案。

### Out of scope
- 不在本任务内扩展 Dashboard 首页、全局消息中心或跨联系人聚合活动流。
- 不追溯补录历史活动；上线前已有老数据允许仅展示新增日志后的真实动态。
- 不在本任务内接入 Agent 操作日志与联系人活动流的合并展示。

## Acceptance criteria

```json
[
  {
    "id": "AC-001",
    "category": "functional",
    "description": "联系人详情页可以展示真实的联系人与事项业务动态",
    "steps": [
      "准备一个联系人并依次执行创建事项、完成事项、修改联系人资料等操作",
      "打开该联系人的详情页并触发最近动态加载",
      "验证时间线按发生时间倒序展示真实事件标题、描述与时间，不再出现硬编码占位文案"
    ],
    "passes": false
  },
  {
    "id": "AC-002",
    "category": "integration",
    "description": "后端活动日志留痕与查询接口契约一致",
    "steps": [
      "执行联系人创建、联系人更新、标签变更、黑名单变更、事项创建、事项完成、事项取消等受支持动作",
      "验证每次动作都会写入 activity_log，且 contactId、eventType、occurredAt、title、description 等字段完整可查",
      "调用 GET /api/v1/contacts/{contactId}/activities?limit=10，验证响应结构与 docs/2-designs/api_contract.md 定义一致"
    ],
    "passes": false
  },
  {
    "id": "AC-003",
    "category": "edge-case",
    "description": "无动态数据时联系人详情页展示明确空态而非伪造活动",
    "steps": [
      "准备一个未产生任何活动日志的新联系人",
      "打开该联系人的详情页",
      "验证最近动态区域展示明确空态提示，且不存在“刚刚”“3 小时前”等静态占位内容"
    ],
    "passes": false
  },
  {
    "id": "AC-004",
    "category": "security",
    "description": "活动流查询严格遵守当前登录用户的数据归属隔离",
    "steps": [
      "使用不拥有目标 contactId 的登录用户访问 GET /api/v1/contacts/{contactId}/activities",
      "验证接口返回 40301 或 40401，不泄露他人联系人动态内容",
      "验证查询结果只包含当前用户自己的 activity_log 数据"
    ],
    "passes": false
  },
  {
    "id": "AC-005",
    "category": "integration",
    "description": "长期设计文档与实现保持一致",
    "steps": [
      "验证 project_overview.md 与 requirements_analysis.md 已纳入联系人活动流业务说明",
      "验证 architecture.md、api_contract.md、db_schema.md 与 ui_prototype.md 已记录活动流的分层、接口、表结构与页面状态",
      "验证实现命名、字段语义与文档描述一致，无额外未记录的行为偏差"
    ],
    "passes": false
  },
  {
    "id": "AC-UI-UX",
    "category": "integration",
    "description": "Chrome MCP validates the contact activity timeline across desktop and mobile breakpoints with clean interactions and console state.",
    "steps": [
      "Open /contacts/{contactId} in Chrome MCP at 1440x900 and verify the recent activity card layout, spacing, icons, timestamps, and empty/non-empty states render correctly without overflow.",
      "Switch to 375x812 and verify the responsive layout keeps the recent activity timeline readable, scrollable, and aligned with the rest of the contact detail page.",
      "Hover each relevant interactive element in the contact detail header and recent activity region and verify the expected visual feedback appears without layout jitter.",
      "Audit the browser console during the flow and verify there are zero JavaScript errors.",
      "Archive named screenshots for the desktop and mobile checks as task014-contact-activity-desktop.png and task014-contact-activity-mobile.png."
    ],
    "passes": false
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
| db | true | `docs/2-designs/db_schema.md` |
| ui | true | `docs/2-designs/ui_prototype.md` |
| constraints | false | `docs/3-constraints/` |
| adr | false | `docs/3-constraints/adr/` |
| agent-runtime | false | `AGENTS.md`, `.codex/session-start.js`, `init.sh`, `init.ps1` |

### Approval-sensitive changes
- 新增 `activity_log` 表及其索引，需要同步维护数据库设计与初始化 DDL。
- 新增联系人活动流查询接口 `GET /api/v1/contacts/{contactId}/activities?limit=10`。

### Explicit non-maintenance
- `docs/3-constraints/` 不需要更新：本任务未引入新的长期安全边界或协作规则，只是在既有多租户和 Service 复用规则内扩展业务能力。
- `AGENTS.md`、`.codex/session-start.js`、`init.ps1` 不需要更新：启动端口、联调命令与会话启动流程不变。
