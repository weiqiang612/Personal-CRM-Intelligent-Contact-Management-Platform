# TASK-008: Tasks

**Spec**: `spec.md`
**Status**: Completed

## Key decisions
- **并发安全用户ID生成**：在 `SysUserServiceImpl` 引入 `synchronized String generateUserId()`。读取数据库中最大的 `user_id`，若为空则初始化为 `U000000001`，否则截取数字部分递增，并采用 `U%09d` 格式进行补零。
- **账号及格式校验**：用户名支持普通字符与邮箱。校验由前端做精细校验，后端利用 DTO validation 与 Service 内置校验双保险。重复用户名直接抛出 `PARAMS_ERROR`。
- **密码哈希**：基于系统已有的 `BCryptPasswordEncoder` 加密密码，安全落库。

## Progress

<!-- Document Maintenance Tasks -->
- [x] T1 — 更新需求文档：修改 `docs/1-requirements/requirements_analysis.md` 与 `docs/1-requirements/project_overview.md` 以体现在 Phase 1 实现注册功能 · covers: doc-maintenance
- [x] T2 — 更新接口设计：在 `docs/2-designs/api_contract.md` 新增 `/api/v1/auth/register` 的请求/响应结构 · covers: AC-CONTRACT
- [x] T3 — 更新UI设计：在 `docs/2-designs/ui_prototype.md` 记录注册页的接入状态与路由细节 · covers: doc-maintenance

<!-- Implementation Tasks: Backend -->
- [x] T4 — 后端：创建注册请求 DTO `RegisterRequest.java` 并声明服务层接口 · covers: AC-001, AC-002, AC-CONTRACT
- [x] T5 — 后端：实现服务层注册业务逻辑（生成唯一用户ID、加密密码、校验防重） · covers: AC-001, AC-002, AC-003
- [x] T6 — 后端：暴露 `POST /api/v1/auth/register` 控制器接口并修改 Spring Security 放行规则 · covers: AC-001, AC-002, AC-CONTRACT
- [x] T7 — 后端：编写并运行 `AuthControllerTest.java` 集成测试，验证注册校验与成功登录 · covers: AC-001, AC-002, AC-003

<!-- Implementation Tasks: Frontend -->
- [x] T8 — 前端：在 `src/api/auth.ts` 中声明 `registerApi` 请求函数与类型参数 · covers: AC-001, AC-CONTRACT
- [x] T9 — 前端：创建 `RegisterView.vue` 高保真页面组件（含表单验证与成功弹窗倒计时跳转） · covers: AC-001, AC-002, AC-UI-UX
- [x] T10 — 前端：在 `src/router/index.ts` 中增加 `/register` 路由，并修改 `LoginView.vue` 跳转路径 · covers: AC-001, AC-UI-UX

<!-- Gates & Housekeeping -->
- [x] T11 — 运行 `mvn -f personal_crm_backend/pom.xml test` 确认全量测试通过
- [x] T12 — 验证 AC：更新 `spec.md` 中的所有验收标准 (Acceptance Criteria) passes 为 true
- [x] T13 — 更新 `docs/4-tasks/CURRENT_PLAN.md` 标记本任务完成

## Dependencies
- T2, T3 require T1
- T4, T5 require T2
- T6 requires T5
- T7 requires T6
- T8 requires T2
- T9 requires T8
- T10 requires T9
- T11, T12 require T7, T10
- T13 requires T11, T12
