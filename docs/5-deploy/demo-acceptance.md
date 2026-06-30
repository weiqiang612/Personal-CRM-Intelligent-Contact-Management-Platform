# 课设演示验收说明

## 1. 演示目标
- 展示仓库已经具备“提交部署资产 -> Agent 拉代码 -> 单机部署 -> 基本验收”的交付能力。

## 2. 建议演示顺序
1. 展示 `docs/5-deploy/README.md`，说明交付边界和拓扑。
2. 展示 `.env.example`，强调仓库只保存模板，不提交真实密钥。
3. 展示 `docker-compose.yml` 与 `deploy/nginx.conf`，说明前端、后端、MySQL、Redis、uploads 的串联方式。
4. 展示 `docker compose config` 与 `docker compose ps` 结果。
5. 打开页面完成一次登录、联系人列表浏览、头像上传或 Agent 查询冒烟。

## 3. 评委可能会问的问题
- 为什么前端不做成独立镜像？
  - 本任务强调“Agent 拉代码后本机构建并部署”，前端静态产物由宿主机构建后挂载到 Nginx，更贴合课设演示边界，也更容易排查构建失败。
- 为什么要单独挂载 uploads？
  - 上传文件属于运行期状态，不做宿主机持久化会在容器重建后丢失。
- 如何避免把敏感信息提交到仓库？
  - 仅提交 `.env.example`，真实值放在 `.env.deploy` 之类的本地未入库文件中。
