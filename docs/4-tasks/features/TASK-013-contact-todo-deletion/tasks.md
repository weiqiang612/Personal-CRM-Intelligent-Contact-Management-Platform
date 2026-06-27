# Tasks: TASK-013 Contact and Todo Entity Deletion Support

## Progress
- [x] T1: [DOC] 更新需求文档补全实体生命周期与删除逻辑 `docs/1-requirements/requirements_analysis.md` (covers: doc-maintenance)
- [x] T2: [DOC] 更新 API 契约文档添加联系人及事项 DELETE 接口 `docs/2-designs/api_contract.md` (covers: AC-CONTRACT)
- [x] T3: [DOC] 更新数据库设计文档记录 deleted 逻辑删除字段与级联规则 `docs/2-designs/db_schema.md` (covers: AC-CONTRACT)
- [x] T4: [DOC] 更新 UI 原型文档记录删除确认危险弹窗交互与 AppDialog 规范 `docs/2-designs/ui_prototype.md` (covers: doc-maintenance)

- [x] T5: [DB/MODEL] 在 schema.sql 添加 deleted 字段并修改 Contact/Todo 实体增加 `@TableLogic` 注解 (covers: AC-001, AC-002)
- [x] T6: [BACKEND] 实现 ContactService 与 ContactController 的删除接口及单元测试 (covers: AC-001)
- [x] T7: [BACKEND] 实现 TodoService 与 TodoController 的删除接口及单元测试 (covers: AC-002)
- [x] T8: [GATE] 执行后端单元测试 `mvn -f personal_crm_backend/pom.xml test` 并确保全部通过 (covers: AC-001, AC-002)
- [x] T9: [FRONTEND] 抽象封装通用弹窗组件 AppDialog.vue 支持 macOS 风格与移动端自适应 (covers: AC-003)
- [x] T10: [FRONTEND] 重构 SettingsView.vue 视图，替换修改邮箱/手机/密码弹窗为 AppDialog 组件 (covers: AC-003)
- [x] T11: [FRONTEND] 在 contacts.ts 与 todos.ts API 模块中补充 DELETE 请求函数 (covers: AC-004)
- [x] T12: [FRONTEND] 在 ContactListView.vue 与 ContactDetailView.vue 中集成删除按钮与确认弹窗 (covers: AC-004)
- [x] T13: [FRONTEND] 在 TodoListView.vue 中集成事项删除选项与确认弹窗 (covers: AC-004)
- [x] T14: [GATE] 执行前端打包构建 `npm --prefix personal_crm_web run build` 确保无编译类型错误 (covers: AC-003, AC-004)
- [x] T15: [E2E] 多端视口自适应逻辑与删除全链路 Chrome MCP 校验并归档截图 (covers: AC-UI-UX)
- [x] T16: [GATE] 完成验收与回写 docs/4-tasks/CURRENT_PLAN.md 计划文件 (covers: doc-maintenance)

## Key Decisions
- **逻辑删除策略**: 使用 `deleted` (TINYINT) 作为逻辑删除字段，不使用物理删除；配合 MyBatis-Plus `@TableLogic` 自动拦截过滤。
- **级联动作处理**: 联系人删除时自动解除 `contact_tag` 关联，历史事项关联不受影响。
- **前端弹窗抽离**: 独立封装 `AppDialog.vue` 复用 macOS 苹果风格外观与自适应 CSS。

## Verification Checklist
- [ ] 运行 `mvn -f personal_crm_backend/pom.xml test` 测试通过
- [ ] 运行 `npm --prefix personal_crm_web run build` 构建成功
- [ ] AC-001 到 AC-UI-UX 全部验证项通过
