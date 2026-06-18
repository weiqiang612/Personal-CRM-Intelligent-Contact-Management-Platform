# TASK-001: MVP Foundation Delivery

**Status**: Ready
**Created**: 2026-06-15
**Feature dir**: `docs/4-tasks/features/TASK-001-mvp-foundation/`

## Objective
以最小可交付节奏完成项目第一阶段基础骨架，使后端鉴权边界、认证入口和前端应用骨架具备进入业务模块迭代的稳定起点。

## Scope

### In scope
- 搭建后端基础设施，包括统一响应、全局异常、JWT 基础配置、MyBatis-Plus 分页配置
- 实现认证模块最小可用链路，包括登录、测试账号和获取当前用户信息
- 替换前端脚手架默认页面，建立 CRM 基础布局、登录页、路由、状态管理、API 封装入口和鉴权跳转
- 为联系人、事项、看板、上传保留前端导航或占位入口，但不实现业务功能
- 完成本地联调、自测和文档状态同步

### Out of scope
- 联系人完整 CRUD、黑名单、分页筛选和详情页业务联调
- 事项列表、新增、完成、取消和逾期展示
- 看板统计业务接口、图表联调和头像上传业务链路
- Contact Agent 实际能力实现
- 标签管理功能
- 管理员后台、批量导入导出、消息推送

## Acceptance criteria

```json
[
  {
    "id": "AC-001",
    "category": "functional",
    "description": "后端基础设施可运行，具备统一响应、全局异常处理、JWT 鉴权基础能力和分页配置。",
    "steps": [
      "启动 Spring Boot 后端服务。",
      "访问公开接口和受保护接口，验证统一响应结构与未登录拦截行为。",
      "验证 MyBatis-Plus 分页查询配置已接入且应用能正常启动。"
    ],
    "passes": true
  },
  {
    "id": "AC-002",
    "category": "functional",
    "description": "认证模块具备最小可用链路，支持使用本地测试账号登录并获取当前用户信息。",
    "steps": [
      "使用测试账号登录并获取 JWT。",
      "携带 JWT 访问 /api/v1/auth/me。",
      "验证登录响应、当前用户响应和失败响应与 docs/2-designs/api_contract.md 一致。"
    ],
    "passes": true
  },
  {
    "id": "AC-003",
    "category": "integration",
    "description": "前端基础骨架可运行，默认脚手架页面被替换，并具备 CRM 基础布局、登录态存储、API 请求入口和鉴权跳转。",
    "steps": [
      "启动前端开发服务。",
      "访问首页和登录相关路由，验证默认 About/Welcome 页面已被替换。",
      "完成登录后进入 CRM 基础布局，刷新页面后登录态仍可用于基础鉴权判断。",
      "访问未实现业务模块入口时显示明确占位状态，而不是脚手架默认内容或空白页。"
    ],
    "passes": true
  },
  {
    "id": "AC-004",
    "category": "security",
    "description": "系统必须建立清晰鉴权边界，未登录或无效 token 请求受保护接口时被拒绝。",
    "steps": [
      "未携带 JWT 调用受保护接口。",
      "携带无效或过期 JWT 调用受保护接口。",
      "验证系统返回明确的鉴权错误，而不是进入业务处理或返回用户信息。"
    ],
    "passes": true
  },
  {
    "id": "AC-005",
    "category": "integration",
    "description": "本任务完成后不会提前实现联系人、事项、看板、上传和 Agent 业务能力，后续业务模块可按独立任务继续开发。",
    "steps": [
      "检查本任务交付内容只包含基础设施、认证和前端骨架。",
      "确认联系人、事项、看板、上传的业务实现仍由后续任务承接。",
      "确认 TASK-002 及后续任务可以基于当前骨架继续纵向实现业务模块。"
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
| database baseline | `docs/2-designs/db_schema.md` |
| UI prototype | `docs/2-designs/ui_prototype.md` |
| constraints | `docs/3-constraints/never-do.md`, `docs/3-constraints/ask-first.md`, `docs/3-constraints/always-do.md` |

### Documentation impact
| Area | Impacted | Maintenance target |
|---|---:|---|
| requirements | false | `docs/1-requirements/project_overview.md`, `docs/1-requirements/requirements_analysis.md` |
| architecture | false | `docs/2-designs/architecture.md` |
| api | false | `docs/2-designs/api_contract.md` |
| db | false | `docs/2-designs/db_schema.md` |
| ui | true | 按现有 `docs/2-designs/ui_prototype.md` 落地前端骨架；本任务不修改原型文档，除非实现中发现原型与当前范围冲突 |
| constraints | false | `docs/3-constraints/` |
| adr | false | `docs/3-constraints/adr/` |
| agent-runtime | false | `AGENTS.md`, `.codex/session-start.js`, `init.sh`, `init.ps1` |

### Approval-sensitive changes
- 认证模块落地时会新增测试账号初始化数据，但不引入新依赖、不改数据库基线设计。
- 若后续实现中需要调整 API 契约或表结构，必须先同步 `docs/2-designs/api_contract.md` 与 `docs/2-designs/db_schema.md`。

### Explicit non-maintenance
- 本任务不新增架构层级，因此 `docs/2-designs/architecture.md` 现在不需要维护。
- 本任务按既有原型落地登录页、基础布局和占位入口；如果实现不改变页面范围、路由清单或交互规则，则不维护 `docs/2-designs/ui_prototype.md`。
- 本任务不涉及 Agent 运行时、启动脚本或 SessionStart 逻辑，因此不维护 `AGENTS.md`、`.codex/session-start.js`、`init.sh`、`init.ps1`。
