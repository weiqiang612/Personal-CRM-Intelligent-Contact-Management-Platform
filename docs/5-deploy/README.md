# 部署总览

## 1. 目标
- 本目录提供 `TASK-018` 的单机部署交付基线。
- 交付模式固定为：本地定稿部署文件 -> 提交仓库 -> 服务器 Agent 拉取代码 -> 宿主机构建 -> `docker compose` 部署 -> 按文档完成验证。

## 2. 目录与资产
| 路径 | 作用 |
|---|---|
| `.env.example` | 部署环境变量模板，仅提供示例值，不包含真实凭据 |
| `docker-compose.yml` | 串联 Nginx、backend、MySQL、Redis 的单机编排文件 |
| `personal_crm_backend/Dockerfile` | 后端多阶段构建镜像定义 |
| `deploy/nginx.conf` | 前端静态资源托管与 `/api`、`/uploads` 反向代理配置 |
| `docs/5-deploy/environment.md` | 环境变量与敏感配置说明 |
| `docs/5-deploy/local-validation.md` | 本地验证与冒烟检查流程 |
| `docs/5-deploy/agent-deployment.md` | 服务器 Agent 的执行步骤与失败诊断 |
| `docs/5-deploy/demo-acceptance.md` | 课设演示时可直接复用的验收口径 |

## 3. 部署拓扑
- 浏览器访问 `nginx:80`。
- Nginx 直接托管 `personal_crm_web/dist`，并把 `/api/*`、`/uploads/*` 转发到 Spring Boot。
- Spring Boot 容器通过 Compose 网络访问 MySQL 与 Redis。
- MySQL 与 Redis 仅保留在 Compose 内部网络，不对公网直接开放端口；后端健康检查端口仅绑定宿主机 `127.0.0.1`。
- 上传目录必须挂载到宿主机 `deploy/data/uploads`，确保容器重建后文件仍保留。

## 4. 执行顺序
1. 基于 `.env.example` 生成服务器本地 `.env.deploy` 并填入真实密钥。
2. 在宿主机执行前端构建，生成 `personal_crm_web/dist`。
3. 执行 `docker compose --env-file .env.deploy up -d --build`。
4. 按 `local-validation.md` 完成健康检查、登录检查和一条业务冒烟路径。

## 5. 边界
- 本任务不负责镜像仓库、CI 自动推镜像、多机编排和真实云资源交付。
- 真实数据库密码、JWT 密钥、Resend Key、OpenAI Key、和风天气 Key 必须由部署环境注入，禁止写入仓库。
