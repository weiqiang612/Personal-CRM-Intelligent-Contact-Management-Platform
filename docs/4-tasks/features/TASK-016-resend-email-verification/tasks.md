# TASK-016 Tasks: Resend 邮箱验证与账号安全闭环

> 维护指南：完成每个子任务后请在方括号中标记 `[x]`。

## 任务进度清单

### 1. 长效架构文档同步 (Document Maintenance First)
- [x] **T1: 同步需求规格文档** `covers: doc-maintenance`
  - 更新 `docs/1-requirements/requirements_analysis.md`，补齐邮箱注册激活、忘记密码找回及修改绑定邮箱的用户场景与安全约束条件。
- [x] **T2: 同步系统架构设计文档** `covers: doc-maintenance`
  - 更新 `docs/2-designs/architecture.md`，补充 Redis 托管临时验证码（存储/限流/锁）与 Resend 第三发信服务交互图。
- [x] **T3: 同步 API 契约文档** `covers: doc-maintenance`
  - 更新 `docs/2-designs/api_contract.md`，定义 `/api/v1/auth/email-code/send`、`/api/v1/auth/email-code/verify`、`/api/v1/auth/password/reset` 和 `/api/v1/user/email/change` 的入参出参及错误码。
- [x] **T4: 同步数据库 Schema 与 Migration 脚本** `covers: doc-maintenance`
  - 更新 `docs/2-designs/db_schema.md`，在 `sys_user` 表结构中添加 `email_verified` (TINYINT) 与 `email_verified_at` (DATETIME) 字段定义及 Redis 数据结构规范。
  - 修改 `personal_crm_backend/src/main/resources/schema.sql`，补充 ALTER TABLE / CREATE TABLE 平滑迁移脚本。
- [x] **T5: 同步 UI 原型与交互设计文档** `covers: doc-maintenance`
  - 更新 `docs/2-designs/ui_prototype.md`，补充邮箱验证码发送倒计时、忘记密码弹窗与个人中心安全设定的前端交互说明。
- [x] **T6: 编写技术决策记录 ADR** `covers: doc-maintenance`
  - 新增 `docs/3-constraints/adr/ADR-004-redis-email-verification.md`，详细记录为什么在生产级认证架构中采用 Redis 托管临时验证码与防爆破防刷机制。

---

### 2. 后端基础设施与安全组件 (Backend Engineering)
- [x] **T7: 配置 Redis 与 Resend 依赖环境** `covers: AC-REDIS-INFRA`
  - 在 `personal_crm_backend/pom.xml` 中引入 `spring-boot-starter-data-redis` 与 `com.resend:resend-java` SDK。
  - 在 `application.yml` 中添加 Redis 连接池配置及 `resend.api-key``resend.from-email` 环境变量读取说明。
- [x] **T8: 实现 EmailVerificationRedisTemplate 组件** `covers: AC-REDIS-INFRA`
  - 编写 Redis 验证码管理组件，实现 `saveCode(email, purpose, code)` (TTL 300s)、`checkRateLimit(email, purpose)` (TTL 60s)、`incrementAttempts(email, purpose)` 及 `verifyAndClearCode(email, purpose, inputCode)` (错误达5次自动作废)。
- [x] **T9: 实现 Resend EmailService 发信服务** `covers: AC-REGISTER-VERIFY`
  - 封装 `EmailService` 接口与实现类 `EmailServiceImpl`，编写极简响应式 HTML 邮件模板，支持发送 `REGISTER`、`RESET_PASSWORD` 及 `CHANGE_EMAIL` 类型的验证码。
- [x] **T10: 改造 SysUser 实体与 Auth 核心逻辑** `covers: AC-REGISTER-VERIFY, AC-PASSWORD-RESET, AC-CHANGE-EMAIL`
  - 更新 `SysUser.java` 实体类添加 `emailVerified` 和 `emailVerifiedAt` 字段。
  - 修改注册逻辑：新建用户状态设为 `UNVERIFIED (status=2)`，并触发发送注册验证码。
  - 修改登录逻辑：若用户状态为 `UNVERIFIED`，抛出专属业务异常或提示前往邮箱激活。
- [x] **T11: 实现邮箱认证与安全 Rest Controller 接口** `covers: AC-REGISTER-VERIFY, AC-PASSWORD-RESET, AC-CHANGE-EMAIL`
  - 编写/修改 `AuthController.java` 与 `UserController.java`，实现发送验证码、激活校验、密码重置及修改邮箱 4 个 API 端点。

---

### 3. 前端界面与交互闭环 (Frontend Development)
- [x] **T12: 封装前端 Email Verification API 与倒计时组件** `covers: AC-UI-UX`
  - 在 `personal_crm_web/src/api/auth.ts` 中添加邮箱验证相关 API 请求函数。
  - 编写通用 60 秒验证码发送倒计时按钮组件 (`SendCodeButton.vue`)。
- [x] **T13: 改造注册页与实现邮箱激活弹窗** `covers: AC-REGISTER-VERIFY, AC-UI-UX`
  - 在 `RegisterView.vue` 中集成验证码发送与输入校验，注册提交后弹出验证码激活对话框，完成激活后自动登录并跳转至首页。
- [x] **T14: 实现忘记密码重置流程 UI** `covers: AC-PASSWORD-RESET, AC-UI-UX`
  - 在 `LoginView.vue` 中增加“忘记密码”入口对话框，引导用户通过邮箱验证码重置密码并直接使用新密码登录。
- [x] **T15: 实现个人中心修改绑定邮箱 UI** `covers: AC-CHANGE-EMAIL, AC-UI-UX`
  - 在个人安全设置页面增加修改绑定邮箱弹窗，校验新邮箱验证码后更新页面展示状态。

---

### 4. 自动化测试与质量收口 (Verification & Acceptance)
- [x] **T16: 执行单元测试与后端构建自测** `covers: AC-CONTRACT`
  - 运行自动化单元测试命令：`mvn -f personal_crm_backend/pom.xml test`，确保全量测试套件通过。
- [x] **T17: Chrome MCP 端到端 (E2E) 响应式 UI/UX 验收** `covers: AC-UI-UX`
  - 在 1440x900 桌面端与 375x812 移动端模拟下测试完整验证码发送、校验与异常处理流程。
  - 检查控制台（`list_console_messages`）确保零报错。
  - 截取并存档 `desktop_email_verification.webp` 与 `mobile_email_verification.webp` 至 artifacts 目录。
- [x] **T18: 更新 CURRENT_PLAN.md 并完成交付** `covers: doc-maintenance`
  - 回写 `CURRENT_PLAN.md` 记录 TASK-016 完成情况与后续计划。
