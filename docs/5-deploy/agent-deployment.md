# Agent 部署流程

## 1. Agent 的职责边界
- Agent 负责在服务器上拉取代码、生成前端 `dist`、启动 Compose、执行验证命令、收集失败日志。
- Agent 不负责生成真实密钥，不负责采购机器，不负责修改业务代码以绕过配置问题。

## 2. 标准执行顺序
1. 拉取仓库最新代码。
2. 复制 `.env.example` 为服务器本地 `.env.deploy`，填入真实密钥。
3. 构建前端：
   - `npm --prefix personal_crm_web ci`
   - `npm --prefix personal_crm_web run build`
4. 校验 Compose：
   - `docker compose --env-file .env.deploy config`
5. 启动服务：
   - `docker compose --env-file .env.deploy up -d --build`
6. 验证：
   - `docker compose --env-file .env.deploy ps`
   - `curl http://localhost:8080/actuator/health`
   - 浏览器访问 `http://<server-host>/`
   - 使用演示账号完成一次登录和联系人列表加载

## 3. 常见失败点与处理
| 失败点 | 典型症状 | 处理方式 |
|---|---|---|
| 环境变量缺失 | `docker compose config` 报变量未设置，或后端启动后抛配置错误 | 补齐 `.env.deploy`，尤其是 `MYSQL_PASSWORD`、`MYSQL_ROOT_PASSWORD`、`JWT_SECRET`、`QWEATHER_API_KEY` |
| MySQL 连接失败 | 后端日志出现 `Communications link failure`、`Access denied` | 检查 `MYSQL_*` 是否一致，确认 `mysql` 容器已启动并完成初始化 |
| Redis 连接失败 | 后端日志出现 `RedisConnectionFailureException` | 检查 `REDIS_PASSWORD` 是否与 `redis` 容器启动参数一致，确认 `REDIS_HOST=redis` |
| 后端单测失败 | `mvn test` 报 Redis 连接错误 | 先执行 `docker compose --env-file .env.deploy up -d redis`，确认 Redis 已启动后再跑测试 |
| 前端白屏或 404 | Nginx 能响应，但页面空白或刷新子路由 404 | 确认 `personal_crm_web/dist` 已生成，`nginx.conf` 中 `try_files` 未被改坏 |
| 接口 502 | 首页能打开，但登录/列表接口失败 | 检查 `backend` 容器状态和日志，确认 Nginx `/api/` 代理目标仍是 `backend:8080` |
| 上传文件不持久 | 重建容器后头像丢失 | 检查 `UPLOAD_DIR` 是否为 `/app/uploads`，并确认宿主机 `deploy/data/uploads` 已正确挂载 |
| 外部服务调用失败 | 天气或邮件能力报错 | 校验 `QWEATHER_API_KEY`、`RESEND_API_KEY`、`OPENAI_*` 配置；若仅做演示，可接受邮件/LLM 使用 mock-key 兜底，但天气 Key 仍建议提供真实值 |

## 4. 建议的日志采集命令
- 查看全部服务日志：`docker compose --env-file .env.deploy logs --tail=200`
- 查看后端日志：`docker compose --env-file .env.deploy logs backend --tail=200`
- 查看 Nginx 日志：`docker compose --env-file .env.deploy logs nginx --tail=100`
- 查看 MySQL 日志：`docker compose --env-file .env.deploy logs mysql --tail=100`

## 5. 交付完成判定
- `docker compose ps` 四个服务均正常运行。
- `http://localhost:8080/actuator/health` 返回 `UP`。
- 浏览器可访问首页，登录成功，联系人列表可见。
- 上传目录已产生宿主机文件，且刷新后仍可访问。
- 如需在宿主机执行 `mvn -f personal_crm_backend/pom.xml test`，Redis 必须先启动。
