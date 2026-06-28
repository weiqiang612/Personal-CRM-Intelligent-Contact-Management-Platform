# TASK-015: Relationship Health Dashboard

**Status**: Draft
**Created**: 2026-06-28
**Feature dir**: `docs/4-tasks/features/TASK-015-relationship-health-dashboard/`

## Objective
将 Dashboard 左下角的静态"联系人性别比例"统计替换为能够直观反映与联系人互动频次与维护健康度的"关系维护状态 (Relationship Health)"统计，帮助用户实时掌握关系动态并主动跟进。

## Scope

### In scope
- 替换 Dashboard 左下角联系人性别比例模块为 Relationship Health 模块
- 统计登录用户所有正常联系人 (`status = 0`) 的活动维护状态
- 后端封装可扩展的计算模型（活跃 0-7天, 待跟进 8-30天, 长期未联系 >30天, 无动态 无活动记录）
- 前端绘制关系维护状态环形图与四色指示卡/图例

### Out of scope
- AI 自动跟进建议逻辑
- Dashboard 其它统计卡片修改
- 联系人 Timeline 详情页修改
- 邮件与推送通知触发

## Acceptance criteria

```json
[
  {
    "id": "AC-001",
    "category": "functional",
    "description": "看板准确拉取并展示联系人关系维护状态统计数据",
    "steps": [
      "用户进入 Dashboard 页面",
      "前端向 /api/v1/dashboard/relationship-health 发起 GET 请求",
      "验证返回 JSON 包含 active, followUp, inactive, noActivity, total 统计字段",
      "页面左下角展示关系维护状态环形图及各分类数值"
    ],
    "passes": true
  },
  {
    "id": "AC-002",
    "category": "functional",
    "description": "后端依据活动日志 (activity_log) 正确计算四种健康状态分类",
    "steps": [
      "构造测试数据集：涵盖7天内有活动、8-30天有活动、>30天有活动以及无任何活动的联系人",
      "调用 API 获取统计数据",
      "校验 active, followUp, inactive, noActivity 各状态计数与数据源精确吻合"
    ],
    "passes": true
  },
  {
    "id": "AC-CONTRACT",
    "category": "integration",
    "description": "API 契约一致性校验",
    "steps": [
      "核对 api_contract.md 中的 GET /api/v1/dashboard/relationship-health 契约",
      "验证后端 Controller 返回与契约定义完全一致"
    ],
    "passes": true
  },
  {
    "id": "AC-UI-UX",
    "category": "integration",
    "description": "UI/UX 及响应式与图表交互体验校验",
    "steps": [
      "使用 Chrome MCP 或浏览器模拟 1440x900 (Desktop) 及 375x812 (Mobile) 视口",
      "验证环形图及图例自适应收缩，无溢出或文字重叠",
      "检查控制台 list_console_messages 确保零未捕获异常"
    ],
    "passes": true
  }
]
```

## Notes

### Documentation impact
- **Requirements**: false (N/A)
- **Architecture**: false (N/A)
- **API Contract**: true (新增 GET /api/v1/dashboard/relationship-health 端点)
- **DB Schema**: false (N/A)
- **UI Prototype**: true (替换 Dashboard 左下角性别分布模块)
- **Constraints**: false (N/A)
- **ADR**: false (N/A)
- **Agent Runtime**: false (N/A)
- **High-risk Items/Approvals**: None
