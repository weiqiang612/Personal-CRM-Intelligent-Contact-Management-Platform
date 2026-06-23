# TASK-008: User Registration

**Status**: Draft
**Created**: 2026-06-23
**Feature dir**: `docs/4-tasks/features/TASK-008-user-registration/`

## Objective
提供完整的用户注册功能，使用户能够创建新账号并使用该账号登录系统，增强多用户隔离与自主接入能力。

## Scope

### In scope
- 前端添加注册页面 `/register`，包括账号/邮箱、密码、确认密码输入与服务协议勾选
- 前端进行格式校验：用户名至少3位，邮箱需符合格式；密码至少8位；两次密码一致；勾选协议
- 前端注册成功后展示成功弹窗并倒计时2秒后自动跳转至登录页
- 后端暴露 `POST /api/v1/auth/register` 免认证接口
- 后端对请求进行校验：用户名不可重复，用户名/密码满足规则
- 后端生成唯一的业务 `user_id`（如 `U000000002`），以并发安全的方式递增生成
- 后端使用 BCrypt 哈希加密存储密码并写入 `sys_user` 表

### Out of scope
- 邮箱激活与验证码发送逻辑
- 注册时的验证码或图形验证防刷
- 手机号验证码注册

## Acceptance criteria

```json
[
  {
    "id": "AC-001",
    "category": "functional",
    "description": "新用户正常注册并登录",
    "steps": [
      "打开注册页面 /register，输入账号 'newuser'，密码 '12345678'，确认密码 '12345678'，勾选同意协议并提交",
      "验证页面提示'注册成功'且在2秒后自动跳转至登录页面",
      "在登录页输入账号 'newuser'，密码 '12345678'，验证能够登录成功并正常查阅工作台"
    ],
    "passes": true
  },
  {
    "id": "AC-002",
    "category": "functional",
    "description": "账号重复注册校验",
    "steps": [
      "使用已存在的账号 'ethan' 提交注册",
      "验证系统拒绝注册，并在提示框显示账号已存在的报错信息"
    ],
    "passes": true
  },
  {
    "id": "AC-003",
    "category": "security",
    "description": "密码加密存储与验证",
    "steps": [
      "注册成功后，直接查询数据库的 sys_user 表中 newuser 对应的记录",
      "验证 password_hash 字段不为明文，且为 BCrypt 哈希加密格式"
    ],
    "passes": true
  },
  {
    "id": "AC-CONTRACT",
    "category": "integration",
    "description": "API 与设计契约一致性验证",
    "steps": [
      "接口请求体与响应格式符合 docs/2-designs/api_contract.md 中的规范描述",
      "数据库数据结构与 docs/2-designs/db_schema.md 保持一致，无新增未记录字段"
    ],
    "passes": true
  },
  {
    "id": "AC-UI-UX",
    "category": "integration",
    "description": "注册页面端到端响应式与交互验证",
    "steps": [
      "使用 Chrome DevTools 将页面分辨率调整为桌面端 (1440x900) 与移动端 (375x812)",
      "验证页面布局、按钮与输入框排列正常且无错位溢出",
      "测试密码显示/隐藏眼睛图标的切换效果，运行 list_console_messages 确保控制台无 JS 异常",
      "在 artifacts/ 目录下保存截屏 desktop_register_view.webp 与 mobile_register_view.webp"
    ],
    "passes": true
  }
]
```

## Notes

### Documentation impact
- **Requirements**: true (更新 `docs/1-requirements/requirements_analysis.md` 和 `docs/1-requirements/project_overview.md` 以体现 Phase 1 注册功能的上线)
- **Architecture**: false (不引入新的架构组件或第三方非标库)
- **API Contract**: true (更新 `docs/2-designs/api_contract.md` 新增 `/api/v1/auth/register` 接口契约)
- **DB Schema**: false (数据库表 sys_user 已具备所有字段，无需执行 migration)
- **UI Prototype**: true (更新 `docs/2-designs/ui_prototype.md` 反映注册页面的真实接入与路由)
- **Constraints**: false (无约束改变)
- **ADR**: false (不涉及重大架构抉择)
- **Agent Runtime**: false (不修改启动脚本或端口配置)
- **High-risk Items/Approvals**: None
