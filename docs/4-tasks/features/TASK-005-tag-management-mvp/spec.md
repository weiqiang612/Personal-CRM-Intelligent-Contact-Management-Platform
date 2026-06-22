# TASK-005: Tag Management MVP

**Status**: Completed
**Created**: 2026-06-21
**Feature dir**: `docs/4-tasks/features/TASK-005-tag-management-mvp/`

## Objective
在 `TASK-004` 已完成真实标签展示与筛选收口的基础上，补齐最小可用的标签管理与联系人标签绑定能力，使标签从只读展示数据升级为可维护的业务能力。

## Scope

### In scope
- 新增当前用户维度下的标签列表、新增、编辑和删除接口。
- 联系人新增和编辑请求支持 `tagIds`，用于绑定或解绑当前用户可见标签。
- 联系人列表标签筛选选项改为从后端真实标签数据加载，不再依赖前端固定枚举。
- 联系人列表、详情和黑名单继续展示接口返回的真实 `tags`。
- 标签删除时清理当前用户下的联系人标签绑定关系，不删除联系人本身。
- 后端测试覆盖标签 CRUD、联系人绑定/解绑、跨用户隔离和删除标签后的绑定清理。
- 同步维护需求、API、数据库和 UI 原型文档。

### Out of scope
- 标签统计分析、热门标签、标签使用趋势。
- 标签合并、批量导入、批量绑定和批量解绑。
- 独立复杂标签中心或多级标签分类。
- Agent 标签查询、Agent 标签写操作和写操作确认链路。
- 看板统计、头像上传、上传失败处理。
- 新增数据库表、字段、索引、外部依赖或运行时配置。

## Acceptance criteria

```json
[
  {
    "id": "AC-CONTRACT",
    "category": "integration",
    "description": "后端标签管理和联系人保存的 API 接口及数据库物理结构与 api_contract.md、db_schema.md 契约严格一致，且不修改现有物理表结构。",
    "steps": [
      "调用 GET/POST/PUT/DELETE /api/v1/tags 接口验证字段及响应格式符合 API 契约。",
      "验证 POST/PUT /api/v1/contacts 的可选 tagIds 请求结构兼容性。",
      "确认无数据库表结构变更及额外依赖引入。"
    ],
    "passes": true
  },
  {
    "id": "AC-TAG-CRUD",
    "category": "functional",
    "description": "当前用户可对标签进行完整的列表查询、新增、编辑、删除操作，前端界面能够实时、流畅反馈。",
    "steps": [
      "在前端标签管理入口新增标签，验证名称及颜色保存成功。",
      "修改标签信息，确认列表展示已实时更新。",
      "删除未绑定联系人的标签，列表状态置空并友好提示。"
    ],
    "passes": true
  },
  {
    "id": "AC-CONTACT-BIND",
    "category": "functional",
    "description": "联系人新增和编辑可自由绑定、解绑当前用户的标签，绑定结果会实时同步在联系人列表、详情及黑名单页面。",
    "steps": [
      "创建联系人时传入 tagIds，确认返回体和列表正确渲染标签。",
      "编辑联系人并清空标签，确认绑定关系解除，但标签本身不被删除。"
    ],
    "passes": true
  },
  {
    "id": "AC-SECURITY-ISOLATION",
    "category": "security",
    "description": "标签管理和联系人标签绑定 logic 强制遵循 JWT 隔离，禁止跨用户访问或越权操作。",
    "steps": [
      "尝试越权查询、修改或删除其他用户的标签，系统均应拦截或拒绝。",
      "尝试将联系人绑定 to 其他用户的标签 ID 上，接口必须报错拒绝。"
    ],
    "passes": true
  },
  {
    "id": "AC-EDGE-CLEANUP",
    "category": "edge-case",
    "description": "删除已被联系人绑定的标签时，自动清理绑定关系（不影响联系人本身），且其他用户的同名标签不受干扰。",
    "steps": [
      "为联系人绑定标签 A。",
      "删除该标签 A，检查 contact_tag 关联表对应记录已删除，联系人依然存在且无该标签。",
      "确认另一用户下同名的标签 A 未受影响。"
    ],
    "passes": true
  },
  {
    "id": "AC-UI-UX",
    "category": "integration",
    "description": "标签管理页面与联系人标签绑定界面布局规范，提供良好的桌面与移动端自适应交互体验。",
    "steps": [
      "使用 resize_page 检查桌面端 (1440x900) 和移动端 (375x812) 的响应式布局，验证标签管理组件及弹框元素无截断或重叠。",
      "使用 hover 测试标签项的编辑/删除操作按钮及联系人表单标签选择下拉框的交互悬停与选中状态。",
      "运行 list_console_messages 确认没有未捕获 of JavaScript 错误或异常。",
      "捕获测试截图并命名为 desktop_tag_list.webp 及 mobile_tag_edit.webp 并保存至 artifacts 目录。"
    ],
    "passes": true
  }
]
```

## Notes

### Documentation impact
- **Requirements**: true (需要更新 docs/1-requirements/requirements_analysis.md 和 docs/1-requirements/project_overview.md 明确标签管理在 MVP 中的业务范围)
- **Architecture**: false (N/A)
- **API Contract**: true (需要更新 docs/2-designs/api_contract.md 引入 /api/v1/tags 标签管理接口定义及联系人保存的可选 tagIds)
- **DB Schema**: true (需要更新 docs/2-designs/db_schema.md 描述 tag / contact_tag 的级联清理规则)
- **UI Prototype**: true (需要更新 docs/2-designs/ui_prototype.md 规范标签列表、表单绑定和管理入口 of UI 交互)
- **Constraints**: false (N/A)
- **ADR**: false (N/A)
- **Agent Runtime**: false (N/A)
- **High-risk Items/Approvals**: None
