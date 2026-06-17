# TASK-000: Static HTML Prototype Baseline

**Status**: Draft
**Created**: 2026-06-15
**Feature dir**: `docs/4-tasks/features/TASK-000-static-html-prototype/`

## Objective
先基于最新参考图片产出一套独立于 Vue 工程的静态 HTML 页面原型，冻结页面结构、区块顺序和关键视觉约束，再将其作为 `TASK-001` 的前置输入。

## Scope

### In scope
- 在根目录新增 `prototype/`，产出 8 个可独立打开的静态 HTML 原型页面和共享样式文件。
- 以 `docs/2-designs/ui_prototype.md` 和最新参考图片为准，还原登录、工作台、联系人、事项和黑名单主链路页面。
- 将黑名单页头像规则固定为彩色表现，不采纳参考图中的黑白头像视觉。
- 创建 `TASK-000` 任务包，并更新 `docs/4-tasks/CURRENT_PLAN.md` 与 `TASK-001` 的前端任务说明，明确原型先行。

### Out of scope
- 不进入 `personal_crm_web/src`，不改现有 Vue 页面、路由、状态管理和接口请求。
- 不新增独立 `agent.html`、`settings.html`、`analytics.html`。
- 不接真实接口、不引入新依赖、不改 API 契约或数据库设计。

## Acceptance criteria

```json
[
  {
    "id": "AC-001",
    "category": "functional",
    "description": "根目录 `prototype/` 中提供 8 个静态 HTML 原型页面和共享样式文件，页面可独立打开查看。",
    "steps": [
      "检查 `prototype/` 目录是否包含 login、dashboard、contacts、contact-detail、contact-form、todos、todo-form、blacklist 8 个页面和共享样式文件。",
      "分别打开每个 HTML 页面。",
      "验证所有页面都能渲染统一的侧边栏、顶部区和主内容布局，而不是空白页或占位纯文本。"
    ],
    "passes": true
  },
  {
    "id": "AC-002",
    "category": "functional",
    "description": "页面主布局与参考图片保持一致的区块顺序和页面职责，能够作为后续 Vue 骨架迁移基线。",
    "steps": [
      "对照 `docs/2-designs/ui_prototype.md` 中 8 页对应参考图片。",
      "检查工作台、联系人详情、新建联系人、新增事项、黑名单等页面的主区块顺序。",
      "验证页面允许示例文案不同，但不允许主卡片、表格、工具栏和操作区顺序错位。"
    ],
    "passes": true
  },
  {
    "id": "AC-003",
    "category": "edge-case",
    "description": "黑名单页面只参考参考图的表格和提示条布局，不采纳黑白头像风格。",
    "steps": [
      "打开 `prototype/blacklist.html`。",
      "检查联系人头像是否仍为普通彩色风格，而不是黑白或灰度处理。",
      "验证风险感仅通过提示条、状态标签和操作按钮体现。"
    ],
    "passes": true
  },
  {
    "id": "AC-004",
    "category": "integration",
    "description": "任务文档与计划状态同步到 `TASK-000 -> TASK-001` 的前置关系，且 `TASK-001` 的前端任务描述改为基于已确认 HTML 原型迁移。",
    "steps": [
      "检查 `docs/4-tasks/features/TASK-000-static-html-prototype/` 下的 `spec.md` 和 `tasks.md` 是否存在且无占位符。",
      "检查 `docs/4-tasks/CURRENT_PLAN.md` 是否将 `TASK-000` 标记为当前活跃任务，并说明 `TASK-001` 的前端实现依赖原型确认。",
      "检查 `docs/4-tasks/features/TASK-001-mvp-foundation/tasks.md` 中 T13-T15 是否体现“基于已确认 HTML 原型迁移”的前提。"
    ],
    "passes": true
  },
  {
    "id": "AC-005",
    "category": "security",
    "description": "静态原型页面不接入真实后端、不包含真实凭据或接口调用逻辑。",
    "steps": [
      "搜索 `prototype/` 目录中的脚本和链接。",
      "确认不存在真实 API 地址、凭据、token 或表单提交到后端的行为。",
      "验证所有交互都停留在静态展示或本地假交互层。"
    ],
    "passes": true
  }
]
```

## Notes
### Documentation impact
| Area | Impacted | Maintenance target |
|---|---:|---|
| requirements | false | `docs/1-requirements/project_overview.md`, `docs/1-requirements/requirements_analysis.md` |
| architecture | false | `docs/2-designs/architecture.md` |
| api | false | `docs/2-designs/api_contract.md` |
| db | false | `docs/2-designs/db_schema.md` |
| ui | true | `docs/2-designs/ui_prototype.md` |
| constraints | false | `docs/3-constraints/` |
| adr | false | `docs/3-constraints/adr/` |
| agent-runtime | false | `AGENTS.md`, `.codex/session-start.js`, `init.sh`, `init.ps1` |

### Approval-sensitive changes
- None
- 本任务不新增环境变量、不新增依赖、不改运行时命令。

### Explicit non-maintenance
- `docs/2-designs/api_contract.md` 不维护：静态原型不新增或修改接口。
- `docs/2-designs/db_schema.md` 不维护：静态原型不新增或修改表结构。
- `docs/2-designs/architecture.md` 不维护：本任务不引入新层级或系统模块。
