# TASK-001: MVP Foundation Delivery

**Status**: Draft
**Created**: 2026-06-15
**Feature dir**: `docs/4-tasks/features/TASK-001-mvp-foundation/`

## Objective
以最小可交付节奏完成项目第一阶段基础骨架，使后端、前端和数据库具备进入持续迭代开发的稳定起点。

## Scope

### In scope
- 搭建后端基础设施，包括统一响应、全局异常、JWT 基础配置、MyBatis-Plus 分页配置
- 实现认证、联系人、事项、看板、上传模块的第一阶段可运行骨架
- 替换前端脚手架默认页面，建立 CRM 基础布局、路由、状态管理和 API 封装入口
- 完成本地联调、自测和文档状态同步

### Out of scope
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
    "passes": false
  },
  {
    "id": "AC-002",
    "category": "functional",
    "description": "认证与联系人模块具备最小可用链路，支持登录、获取当前用户、联系人列表、详情、新增、修改和黑名单状态切换。",
    "steps": [
      "使用测试账号登录并获取 JWT。",
      "携带 JWT 访问 /api/v1/auth/me 与联系人相关接口。",
      "验证联系人接口行为与 docs/2-designs/api_contract.md 一致。"
    ],
    "passes": false
  },
  {
    "id": "AC-003",
    "category": "integration",
    "description": "事项、看板和上传模块具备第一阶段联调骨架，并与现有数据库设计和接口契约保持一致。",
    "steps": [
      "创建事项并完成或取消事项状态变更。",
      "访问看板统计接口并返回基础统计字段。",
      "上传用户头像或联系人头像并返回访问地址，验证实现未偏离 docs/2-designs/api_contract.md 与 docs/2-designs/db_schema.md。"
    ],
    "passes": false
  },
  {
    "id": "AC-004",
    "category": "security",
    "description": "系统必须按当前登录用户隔离数据，未登录或跨用户访问请求被拒绝。",
    "steps": [
      "未携带 JWT 调用受保护接口。",
      "尝试访问不属于当前用户的数据。",
      "验证系统返回明确的鉴权或越权错误，而不是泄露数据。"
    ],
    "passes": false
  },
  {
    "id": "AC-005",
    "category": "edge-case",
    "description": "前端不再保留 create-vue 默认演示页面，首页路由、登录态存储和基础导航骨架可用。",
    "steps": [
      "启动前端开发服务。",
      "访问首页和登录相关路由，验证默认 About/Welcome 页面已被替换。",
      "验证前端已具备基础布局、路由占位页和 API 请求入口。"
    ],
    "passes": false
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
| ui | false | `docs/2-designs/ui_prototype.md` |
| constraints | false | `docs/3-constraints/` |
| adr | false | `docs/3-constraints/adr/` |
| agent-runtime | false | `AGENTS.md`, `.codex/session-start.js`, `init.sh`, `init.ps1` |

### Approval-sensitive changes
- 认证模块落地时会新增测试账号初始化数据，但不引入新依赖、不改数据库基线设计。
- 若后续实现中需要调整 API 契约或表结构，必须先同步 `docs/2-designs/api_contract.md` 与 `docs/2-designs/db_schema.md`。

### Explicit non-maintenance
- 本任务不新增架构层级，因此 `docs/2-designs/architecture.md` 现在不需要维护。
- 本任务不涉及 Agent 运行时、启动脚本或 SessionStart 逻辑，因此不维护 `AGENTS.md`、`.codex/session-start.js`、`init.sh`、`init.ps1`。
