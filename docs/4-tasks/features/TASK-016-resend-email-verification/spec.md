# TASK-016 Specification: Resend 邮箱验证与账号安全闭环 (Redis 生产级实现)

## 变更概述 (Overview)
本项目将构建生产级、面向高可用作品集标准的用户邮箱验证与账号安全体系。采用 **Resend Java SDK** 作为第三方高效发信引擎，结合 **Redis 内存存储** 托管临时验证码、发送频率限制（Rate Limiting）及防爆破错误计数（Lockout）。MySQL 数据库仅用于持久化用户的邮箱激活状态 (`email_verified` 与 `email_verified_at`)。

本次任务范围严格限制在三大安全认证场景：
1. **注册邮箱验证码校验与激活**
2. **忘记密码找回与重置**
3. **已登录用户修改绑定邮箱**

（注：复杂业务邮件通知留待 TASK-017）

---

## 验收条件 (Acceptance Criteria)

```json
[
  {
    "id": "AC-REDIS-INFRA",
    "category": "security",
    "description": "基于 Redis 构建验证码存储、发送频率限制锁与错误计数爆破防线",
    "steps": [
      "验证后端集成 `spring-boot-starter-data-redis` 并正确连接 Redis",
      "发送验证码时以 `email:code:{purpose}:{email}` 存储哈希或明文，设置 TTL 为 300 秒 (5分钟)",
      "发送验证码时以 `email:send:{purpose}:{email}` 写入频率锁，设置 TTL 为 60 秒",
      "若 60 秒内再次请求发送，接口直接打回 '请60秒后再试'",
      "每次输入错误验证码时，`email:attempt:{purpose}:{email}` 递加 1，达到 5 次后锁定该验证码并作废"
    ],
    "passes": false
  },
  {
    "id": "AC-REGISTER-VERIFY",
    "category": "functional",
    "description": "注册邮箱验证码发送与用户激活流程闭环",
    "steps": [
      "用户在前端注册页面输入邮箱与密码，提交后创建 `status=2 (UNVERIFIED)` 且 `email_verified=false` 的用户",
      "后端调用 Resend SDK 发送 HTML 动态验证码邮件",
      "用户收到邮件并在前端弹窗/页面中输入 6 位数字验证码进行校验",
      "校验通过后，Redis 验证码立即清除，更新 `sys_user` 的 `status=0 (ACTIVE)`、`email_verified=true` 及 `email_verified_at=NOW()`",
      "校验通过后自动完成登录并返回 JWT token"
    ],
    "passes": false
  },
  {
    "id": "AC-PASSWORD-RESET",
    "category": "functional",
    "description": "忘记密码重置流程闭环",
    "steps": [
      "用户在登录页点击 '忘记密码'，输入目标注册邮箱请求验证码 (`purpose=RESET_PASSWORD`)",
      "接收到邮件验证码后，提交邮箱、验证码与新密码至 `POST /api/v1/auth/password/reset`",
      "后端校验 Redis 验证码有效性及错误次数，成功后将新密码密文更新至 `sys_user` 并清除 Redis 中的相关 Key"
    ],
    "passes": false
  },
  {
    "id": "AC-CHANGE-EMAIL",
    "category": "functional",
    "description": "个人中心修改绑定邮箱闭环",
    "steps": [
      "已登录用户在个人安全中心请求更换绑定邮箱，向新邮箱发送验证码 (`purpose=CHANGE_EMAIL`)",
      "校验新邮箱是否已被其他账号使用",
      "提交新邮箱与验证码至 `POST /api/v1/user/email/change`，校验成功后更新 `sys_user.email` 及激活时间"
    ],
    "passes": false
  },
  {
    "id": "AC-CONTRACT",
    "category": "integration",
    "description": "API 契约与数据库 Migration 架构对齐",
    "steps": [
      "在 `schema.sql` / Migration 脚本中向 `sys_user` 表平滑新增 `email_verified` (TINYINT) 与 `email_verified_at` (DATETIME) 字段",
      "更新 `docs/2-designs/api_contract.md` 补齐 email-code/send、email-code/verify、password/reset 及 email/change 契约",
      "更新 `docs/2-designs/db_schema.md` 记录 Redis Key 设计与 `sys_user` 字段变更"
    ],
    "passes": false
  },
  {
    "id": "AC-UI-UX",
    "category": "integration",
    "description": "前端 Vue 3 UI/UX 交互与 Chrome MCP 端到端验证",
    "steps": [
      "前端集成 60 秒倒计时按钮组件与图形/弹窗交互，在桌面端 (1440x900) 与移动端 (375x812) 均实现完美自适应",
      "使用 Chrome MCP resize_page、hover、list_console_messages 验证全流程无 JavaScript 控制台报错",
      "截取 `desktop_email_verification.webp` 与 `mobile_email_verification.webp` 并存档至 artifacts"
    ],
    "passes": false
  }
]
```

---

## 文档影响矩阵 (Documentation Impact Matrix)

| 文档维度 | 是否影响 | 说明 |
| :--- | :--- | :--- |
| **Requirements** | `true` | 更新 `docs/1-requirements/requirements_analysis.md` 补齐邮箱注册激活、重置密码与修改邮箱场景 |
| **Architecture** | `true` | 更新 `docs/2-designs/architecture.md` 描述 Redis 验证码托管与 Resend SDK 发信架构 |
| **API** | `true` | 更新 `docs/2-designs/api_contract.md` 补充新增的 4 个认证安全 API 契约 |
| **DB** | `true` | 更新 `docs/2-designs/db_schema.md` 添加 `sys_user` 扩展字段与 Redis 数据结构设计说明 |
| **UI** | `true` | 更新 `docs/2-designs/ui_prototype.md` 补充验证码弹窗、倒计时组件与忘记密码流程 UI 交互规范 |
| **Constraints** | `false` | 无硬性编码禁忌变更 |
| **ADR** | `true` | 新增 ADR `docs/3-constraints/adr/ADR-004-redis-email-verification.md` 记录 Redis 托管临时验证码的设计决策 |
| **Agent Runtime**| `false` | 无启动脚本及端口调整 |

---

## 风险评估与注意事项 (Notes & Risk Mitigation)
1. **API Key 权限控制**：Resend 后台创建的 API Key 必须使用 `Sending access` 权限并指定 `mail.weiqiang.me` 发信域名，绝不硬编码在代码中。
2. **Redis 容错处理**：若 Redis 服务异常，后端应抛出明确的系统繁忙提示，保护发信服务不被滥用。
3. **环境兼容性**：后端启动时确保 Redis 连接池正常初始化。
