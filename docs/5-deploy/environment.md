# 环境变量说明

## 1. 使用原则
- 仓库只提交 `.env.example`，真实部署请复制为 `.env.deploy` 或其他未入库文件。
- 敏感项必须在服务器本地填写，禁止把真实值回写到 Git。
- 若仅做无外部服务的本地演示，可保留 `OPENAI_API_KEY=mock-key` 与 `RESEND_API_KEY=mock-key`，系统会走本地兜底逻辑。

## 2. 变量清单
| 变量 | 示例 | 必填 | 说明 |
|---|---|---:|---|
| `MYSQL_DATABASE` | `personal_crm` | 是 | MySQL 数据库名 |
| `MYSQL_USER` | `personal_crm` | 是 | 应用连接用户 |
| `MYSQL_PASSWORD` | `change_me_mysql_password` | 是 | 应用连接密码，敏感 |
| `MYSQL_ROOT_PASSWORD` | `change_me_mysql_root_password` | 是 | root 密码，敏感 |
| `REDIS_DATABASE` | `1` | 否 | Redis DB 编号 |
| `REDIS_PASSWORD` | 留空或真实值 | 否 | 若非空，Compose 将为 Redis 启用密码保护 |
| `JWT_SECRET` | `change_me_jwt_secret_at_least_32_chars` | 是 | JWT 签名密钥，敏感，建议至少 32 字符 |
| `JWT_EXPIRATION_SECONDS` | `7200` | 否 | 登录态有效期 |
| `UPLOAD_DIR` | `/app/uploads` | 是 | 容器内上传根目录，需与 Compose 挂载保持一致 |
| `OPENAI_API_KEY` | `mock-key` | 条件必填 | 接入真实大模型时必填，敏感 |
| `OPENAI_BASE_URL` | `https://api.openai.com/v1` | 否 | OpenAI 兼容接口地址 |
| `OPENAI_MODEL` | `gpt-4o-mini` | 否 | 默认模型名 |
| `RESEND_API_KEY` | `mock-key` | 条件必填 | 接入真实邮件发送时必填，敏感 |
| `RESEND_FROM_EMAIL` | `Personal CRM <no-reply@example.com>` | 条件必填 | 邮件发件人 |
| `QWEATHER_API_KEY` | `replace_with_real_qweather_key` | 条件必填 | 天气接口 Key，敏感 |
| `QWEATHER_API_HOST` | `nk3dnacruh.re.qweatherapi.com` | 否 | 天气接口域名 |
| `IP_API_URL` | `http://ip-api.com/json/` | 否 | IP 定位接口 |
| `WEATHER_DEFAULT_CITY` | `Hangzhou` | 否 | IP/GEO/地址均不可用时的兜底城市 |

## 3. 非敏感固定项
- `MYSQL_HOST=mysql`、`REDIS_HOST=redis` 由 Compose 网络内部服务名提供，不需要在部署文件外单独修改。
- `SERVER_PORT=8080` 对应后端容器暴露端口；外部用户通常只访问 Nginx 的 `80` 端口。
- `SPRING_SQL_INIT_MODE=always` 适合首次演示部署；如果接管已有数据库，改为 `never` 以避免重复初始化。
