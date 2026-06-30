# TASK-018: Deployment Assets and Agent Delivery

**Status**: Completed
**Created**: 2026-06-30
**Feature dir**: `docs/4-tasks/features/TASK-018-deployment-assets-agent-delivery/`

## Objective
为项目补齐本地宿主机定稿的部署资产与 Agent 部署说明，使仓库具备“本地生成部署文件 -> 提交仓库 -> Agent 拉取并部署”的可执行交付基线。

## Scope

### In scope
- 新增 `docs/5-deploy/` 目录，承载部署说明、环境变量清单、本地验证流程与 Agent 部署流程文档。
- 设计并落地单机 Docker Compose 部署资产，包括后端镜像、Nginx 反向代理、MySQL、Redis、宿主机持久化上传目录和示例环境变量文件。
- 约定“本地人工定稿部署文件，服务器 Agent 拉代码后本机构建并部署”的交付流程，并在文档中明确验证与排障步骤。

### Out of scope
- 接入镜像仓库、GitHub Actions 自动推镜像或多机发布能力。
- 变更业务接口、数据库结构、页面交互或运行时启动脚本。
- 上线到真实生产环境或采购云资源。

## Acceptance criteria

```json
[
  {
    "id": "AC-001",
    "category": "functional",
    "description": "仓库内存在完整的部署文档入口与 docs/5-deploy 目录结构，可说明部署拓扑、环境变量、验证流程和 Agent 部署职责。",
    "steps": [
      "创建 docs/5-deploy 目录，并放置部署总览、环境变量说明、本地验证流程和 Agent 部署流程文档。",
      "在文档中明确单机部署拓扑、服务角色分工、uploads 宿主机持久化要求和 Agent 的执行边界。",
      "验证课设阅读者仅依赖仓库文档即可理解部署资产用途与执行顺序。"
    ],
    "passes": true
  },
  {
    "id": "AC-002",
    "category": "integration",
    "description": "仓库提供可执行的单机 Docker Compose 部署资产，能够描述并串联前端静态资源、Spring Boot、MySQL、Redis 与 Nginx 的本地部署方式。",
    "steps": [
      "新增 docker-compose.yml、backend Dockerfile、Nginx 配置与 .env.example 等部署资产。",
      "确保部署资产覆盖后端、MySQL、Redis、Nginx、上传目录挂载和关键环境变量注入。",
      "验证部署资产与 docs/5-deploy 中记录的拓扑和执行步骤保持一致。"
    ],
    "passes": true
  },
  {
    "id": "AC-003",
    "category": "edge-case",
    "description": "部署文档必须覆盖失败诊断与本地验证闭环，避免 Agent 或操作者在缺少关键配置时盲目上线。",
    "steps": [
      "在文档中列出最小本地验证流程，包括 compose 配置检查、服务启动检查、前后端联通检查和至少一条业务冒烟路径。",
      "在 Agent 部署流程文档中列出常见失败点，例如环境变量缺失、Redis 或 MySQL 连接失败、uploads 未挂载、反向代理错误。",
      "验证文档能指导操作者在部署前后执行检查，而不是仅提供启动命令。"
    ],
    "passes": true
  },
  {
    "id": "AC-004",
    "category": "security",
    "description": "部署资产不得把真实敏感配置写入仓库，并需明确示例变量与运行时机密的边界。",
    "steps": [
      "仅提交 .env.example 或等效示例配置，禁止写入真实数据库口令、JWT 密钥、Resend 密钥或 LLM API Key。",
      "在文档中标明需要由本地或服务器运行环境注入的敏感配置项。",
      "验证部署文件和 docs/5-deploy 文档中不存在真实凭据。"
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
| architecture | true | `docs/2-designs/architecture.md` |
| api | false | `docs/2-designs/api_contract.md` |
| db | false | `docs/2-designs/db_schema.md` |
| ui | false | `docs/2-designs/ui_prototype.md` |
| constraints | false | `docs/3-constraints/` |
| adr | false | `docs/3-constraints/adr/` |
| agent-runtime | false | `AGENTS.md`, `.codex/session-start.js`, `init.sh`, `init.ps1` |

### Approval-sensitive changes
- 将新增 `Dockerfile`、`docker-compose.yml`、Nginx 部署配置和示例环境变量文件；这属于已确认范围内的部署资产补齐。
- 预计新增的示例环境变量将覆盖 MySQL、Redis、JWT、上传目录、Resend 与 OpenAI 兼容模型配置。

### Explicit non-maintenance
- `docs/2-designs/api_contract.md` 不需要维护，因为本任务不新增或修改 API 契约。
- `docs/2-designs/db_schema.md` 不需要维护，因为本任务不新增表、字段或迁移。
- `AGENTS.md`、`init.ps1`、`init.sh` 暂不调整，因为本任务先补部署资产与文档，不改本地开发启动基线。
