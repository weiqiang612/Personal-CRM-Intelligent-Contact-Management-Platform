# TASK-013: Contact and Todo Entity Deletion Support

**Status**: Draft
**Created**: 2026-06-27
**Feature dir**: `docs/4-tasks/features/TASK-013-contact-todo-deletion/`

## Objective
为 Personal CRM 系统实现联系人 (Contact) 与事项 (Todo) 的逻辑删除功能。在后端完成数据库逻辑删除字段 (`deleted`) 扩展、注解映射、Service 层多租户隔离校验与级联解绑清理；在前端抽象封装通用的苹果风/macOS 弹窗组件 (`AppDialog.vue`) 并替换设置页修改弹窗，同时实现联系人列表/详情与事项列表中的删除入口、危险动作确认弹窗以及交互联调。

## Scope

### In scope
- **长效设计文档同步**：同步维护并更新 `requirements_analysis.md`（补全实体废弃生命周期说明）、`api_contract.md`（补充 DELETE 接口契约）、`db_schema.md`（增加 deleted 逻辑删除字段及关联规则）和 `ui_prototype.md`（定义危险操作删除确认交互规范）。
- **前端通用组件封装**：在 `personal_crm_web/src/components/common/` 中抽象封装 `AppDialog.vue` 通用弹窗外壳组件，支持统一的 macOS 风格外壳、描述文本、`loading` 提交状态以及移动端自适应（手机端按钮纵向排布）。重构 `SettingsView.vue` 中修改邮箱、修改手机、修改密码的弹窗，统一复用该组件。
- **数据库与后端实体**：在 `schema.sql` 中的 `contact` 表与 `contact_todo` 表追加 `deleted TINYINT NOT NULL DEFAULT 0 COMMENT '0 normal, 1 deleted'` 字段。在后端 `Contact.java` 和 `Todo.java` 实体中配置 `private Integer deleted;` 属性与 MyBatis-Plus 的 `@TableLogic` 注解。
- **后端 API 与业务逻辑**：
  - 实现 `DELETE /api/v1/contacts/{contactId}` 接口，校验租户归属，执行逻辑删除并级联清理 `contact_tag` 关联记录，保持其历史创建的事项不破坏。
  - 实现 `DELETE /api/v1/todos/{matterId}` 接口，校验租户归属并执行逻辑删除。
  - 补充相应的 Controller/Service 单元测试。
- **前端页面与交互集成**：
  - 在 `ContactListView.vue`（操作列/卡片项）和 `ContactDetailView.vue`（头部操作区）中加入“删除联系人”按钮及 `AppDialog.vue` 确认弹窗，删除成功后跳转或刷新列表。
  - 在 `TodoListView.vue` 的事项卡片更多操作菜单中加入“删除事项”选项及确认弹窗，删除成功后同步刷新。

### Out of scope
- 不使用物理删除 (`DELETE FROM`)。
- 不提供“回收站”还原页面（作为后续版本 backlog）。
- 不破坏历史数据中已关联事项的展示与统计一致性。

## Acceptance criteria

```json
[
  {
    "id": "AC-001",
    "category": "functional",
    "description": "联系人逻辑删除后端 API 与级联规则校验",
    "steps": [
      "发送 DELETE /api/v1/contacts/{contactId} 请求删除指定联系人",
      "验证数据库 contact 表中该记录 deleted 字段被更新为 1",
      "验证 contact_tag 表中该联系人的关联记录被自动级联清理",
      "验证该联系人历史创建的事项 contact_todo 记录依然完好保留"
    ],
    "passes": false
  },
  {
    "id": "AC-002",
    "category": "functional",
    "description": "事项逻辑删除后端 API 校验",
    "steps": [
      "发送 DELETE /api/v1/todos/{matterId} 请求删除指定事项",
      "验证数据库 contact_todo 表中该记录 deleted 字段被更新为 1",
      "查询事项列表 GET /api/v1/todos，验证已删除的事项不会出现在返回结果中"
    ],
    "passes": false
  },
  {
    "id": "AC-003",
    "category": "functional",
    "description": "前端通用 AppDialog.vue 封装与设置页重构",
    "steps": [
      "验证 AppDialog.vue 通用组件成功封装并集成 macOS 风格与移动端按钮自适应",
      "打开设置页 SettingsView.vue，点击修改邮箱、手机、密码，验证弹窗外观与功能依然完好正常",
      "检查 SettingsView.vue 模板与 CSS 样式显著简化"
    ],
    "passes": false
  },
  {
    "id": "AC-004",
    "category": "functional",
    "description": "前端联系人与事项删除交互确认与流程落盘",
    "steps": [
      "进入联系人列表或详情页，点击“删除联系人”按钮，弹出 AppDialog 危险确认二次弹窗",
      "点击“确认删除”，验证触发删除 API 且删除成功提示弹出，列表自动刷新或重定向",
      "进入事项列表，点击事项卡片的“删除”选项，确认后列表更新并移出该事项"
    ],
    "passes": false
  },
  {
    "id": "AC-CONTRACT",
    "category": "integration",
    "description": "API 契约与数据库 Schema 一致性校验",
    "steps": [
      "验证 API 契约文档 api_contract.md 已补充 DELETE 接口",
      "验证数据库设计文档 db_schema.md 已记录 deleted 字段与清理规则"
    ],
    "passes": false
  },
  {
    "id": "AC-UI-UX",
    "category": "integration",
    "description": "UI/UX 体验与多端自适应 E2E 自动化验证",
    "steps": [
      "使用 resize_page 将视口分别调整为桌面端 (1440x900) 与移动端 (375x812)，验证 AppDialog 弹窗自适应与按钮排布",
      "使用 hover 测试删除按钮与弹窗按钮的交互悬停反馈",
      "运行 list_console_messages 检查前台控制台日志，验证无未捕获异常",
      "捕获桌面端与移动端删除弹窗截图，归档在 artifacts/ 目录中"
    ],
    "passes": false
  }
]
```

## Notes

### Documentation impact
- **Requirements**: true (需更新 requirements_analysis.md 说明实体删除与生命周期)
- **Architecture**: false (N/A)
- **API Contract**: true (需更新 api_contract.md 补充 DELETE 接口契约)
- **DB Schema**: true (需更新 db_schema.md 记录 deleted 字段与级联规则)
- **UI Prototype**: true (需更新 ui_prototype.md 记录危险操作确认弹窗规范与 AppDialog 通用外壳设计)
- **Constraints**: false (N/A)
- **ADR**: false (N/A)
- **Agent Runtime**: false (N/A)
- **High-risk Items/Approvals**: 无破坏性物理删除变更。
