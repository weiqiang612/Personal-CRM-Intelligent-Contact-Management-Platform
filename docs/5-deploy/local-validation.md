# 本地验证流程

## 1. 目标
- 验证部署资产本身可读、可组装、可说明。
- 验证前端静态资源、Nginx 反代、后端健康检查、数据库初始化和一条业务路径能闭环。

## 2. 最小准备
1. 复制 `.env.example` 为 `.env.deploy`，按需填入真实敏感值。
2. 确保宿主机已安装 Docker、Docker Compose、Node 20+、JDK 17（仅宿主机构建前端时需要 Node）。
3. 预创建目录（推荐）：`deploy/data/mysql`、`deploy/data/redis`、`deploy/data/uploads`。
4. 若要在宿主机执行 `mvn -f personal_crm_backend/pom.xml test`，先确保本地 Redis 已启动；可直接复用 `docker compose --env-file .env.deploy up -d redis`。

## 3. 配置校验
1. 检查 Compose 解析结果：
   - `docker compose --env-file .env.deploy config`
   - 预期：命令退出码为 0，且 `backend`、`mysql`、`redis`、`nginx` 四个服务均被展开。
2. 检查前端产物是否存在：
   - `npm --prefix personal_crm_web ci`
   - `npm --prefix personal_crm_web run build`
   - 预期：生成 `personal_crm_web/dist` 目录。

## 4. 启动检查
1. 构建并启动：
   - `docker compose --env-file .env.deploy up -d --build`
2. 查看容器状态：
   - `docker compose --env-file .env.deploy ps`
   - 预期：`mysql`、`redis`、`backend`、`nginx` 均为 `Up`。
3. 查看后端健康检查：
   - `curl http://localhost:8080/actuator/health`
   - 预期：返回 `{"status":"UP"}`。
4. 查看前端入口：
   - 浏览器打开 `http://localhost/`
   - 预期：返回登录页而非 Nginx 默认欢迎页或 404。
5. 如需执行后端测试：
   - `mvn -f personal_crm_backend/pom.xml test`
   - 预期：在 Redis 已启动前提下，111 个测试通过。

## 5. 联通与业务冒烟
1. 登录演示账号：
   - 用户名：`ethan`
   - 密码：`123456`
2. 进入联系人页，确认列表能正常加载演示数据。
3. 任选一个联系人上传头像，确认：
   - 页面上传成功；
   - 后端日志无 500 错误；
   - `deploy/data/uploads` 下出现文件；
   - 页面刷新后头像地址仍可访问。
4. 打开 `/agent` 页面，输入一条查询类请求，例如“帮我查张雨薇的联系方式”，确认接口返回正常。

## 6. 失败诊断入口
- 如果 `docker compose config` 失败，优先检查 `.env.deploy` 是否缺少必填变量或存在未转义字符。
- 如果 Nginx 可打开但接口全部 502，检查 `backend` 容器日志和 `deploy/nginx.conf` 中的 upstream 目标。
- 如果登录失败且后端报数据库连接错误，检查 `MYSQL_*` 是否与容器初始化参数一致。
- 如果头像上传成功后文件丢失，检查 `UPLOAD_DIR` 与 `deploy/data/uploads` 挂载是否一致。
