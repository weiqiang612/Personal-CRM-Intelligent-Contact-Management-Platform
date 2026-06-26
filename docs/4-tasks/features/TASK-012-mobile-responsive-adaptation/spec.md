# TASK-012: Mobile Responsive Adaptation

**Status**: Draft
**Created**: 2026-06-26
**Feature dir**: `docs/4-tasks/features/TASK-012-mobile-responsive-adaptation/`

## Objective
实现 Personal CRM 前端系统的移动端响应式高保真自适应适配。大屏幕展示 PC 侧边栏，移动窄屏（视口 <= 768px）下隐藏 Sidebar 改为展示底部导航 TabBar，工作台底部平铺 100% 宽度自适应的 SVG/CSS 统计图表，智能助手页适配为独立全屏聊天页面，联系人和事项列表页在小屏下由 Table 降级切换为卡片流。

## Scope

### In scope
- 针对全局 LayoutView.vue 进行移动端双模布局适配：宽度 <= 768px 隐藏 Sidebar 和 PC 顶栏，显示底部 TabBar 导航栏（工作台、联系人、事项、智能助手）以及精简的 Mobile Header。
- 针对 DashboardView.vue 页面适配：摘要卡片自适应缩放（支持 PC 平铺与 Mobile 4个平铺排版）；重要联系人轮播改造成手势滑动的 Flex 滑块；待办列表项目样式美化；将“新增联系人趋势”和“联系人性别分布”图表以纵向堆叠、压缩高度（约180px）形式平铺展示，集成 `echartsInstance.resize()` 确保宽度自适应。
- 针对 ContactsView.vue：小屏下隐藏 `el-table`，替换为精美移动端联系人卡片流。
- 针对 TodosView.vue：小屏下隐藏 table，替换为待办任务卡片列表，并优化手势勾选。
- 针对 AgentView.vue（智能助手）：点击底部“智能助手” Tab 切换至独立的全屏聊天页面。页面剔除 PC 端四周留白与圆角框，高度 `100vh` 扣除 TabBar，输入框贴紧底部，实现沉浸式对话。

### Out of scope
- 不增加任何后端 API 接口或修改接口数据契约。
- 不修改数据库表结构。
- 不支持在移动端将图表以外的功能完全分离到另一个独立前端工程或独立路由。
- 移动端上不保留工作台的 Agent 悬浮球，完全由底部“智能助手”Tab 进行全屏页面承载。

## Acceptance criteria

```json
[
  {
    "id": "AC-001",
    "category": "functional",
    "description": "移动端双模布局与 Tab 导航自适应",
    "steps": [
      "使用 Chrome DevTools 将屏幕缩放为 375x812 (移动端)",
      "验证 PC 侧边栏 Sidebar 被隐藏，且底部 TabBar 导航栏固定显示在最下方",
      "点击底部 TabBar，验证可以在“工作台”、“联系人”、“事项”、“智能助手”视图间流畅切换"
    ],
    "passes": true
  },
  {
    "id": "AC-002",
    "category": "functional",
    "description": "工作台（Dashboard）移动端高保真还原与图表自适应",
    "steps": [
      "切换至“工作台”视图，验证 4 个指标统计卡片在小屏下以 4 列平铺展示，无文字截断",
      "左右拖动或手势滑动“重要联系人”栏，验证联系人卡片能够流畅水平横划滚动",
      "滚动到工作台最下方，验证“新增联系人趋势”和“联系人性别分布”图表以 100% 宽度自适应显示，高度等比缩减至 180px 左右，无溢出或重叠"
    ],
    "passes": true
  },
  {
    "id": "AC-003",
    "category": "functional",
    "description": "联系人与待办列表的 Card 降级展示",
    "steps": [
      "切换至“联系人”或“事项” Tab 视图，验证在移动端下不再出现 Table 宽表格，而是垂直排列的卡片项",
      "在联系人列表中，卡片展示头像、名字、公司及标签，点击可进行相应交互",
      "在事项列表中，可通过点击复选框实现快捷勾选待办"
    ],
    "passes": true
  },
  {
    "id": "AC-004",
    "category": "functional",
    "description": "全屏沉浸式智能助手适配",
    "steps": [
      "切换至“智能助手” Tab 视图，验证原 PC 端的卡片居中外边距及阴影已被隐藏，对话区全屏填充",
      "对话窗口高度正确撑满 (100vh 减去 TabBar 高度)，输入框完美紧贴最底端",
      "在输入框中打字并发送，气泡正常追加，且底部输入框不被输入法遮挡或挤出屏幕"
    ],
    "passes": true
  },
  {
    "id": "AC-005",
    "category": "integration",
    "description": "UI/UX 体验与多端自适应 E2E 自动化验证 (AC-UI-UX)",
    "steps": [
      "使用 resize_page 将视口分别调整为桌面端 (1440x900) 与移动端 (375x812)，验证自适应样式和折行表现",
      "使用 hover 测试新版 Tab 按钮、卡片的悬停状态与点按反馈",
      "运行 list_console_messages 检查前台控制台日志，验证无未捕获的报错与异常",
      "捕获移动端各视图截图，归档为 artifacts/mobile_dashboard_charts.webp、artifacts/mobile_agent_full.webp、artifacts/mobile_contacts_cards.webp 等文件"
    ],
    "passes": true
  }
]
```

## Notes

### Documentation impact
- **Requirements**: false (N/A)
- **Architecture**: false (N/A)
- **API Contract**: false (N/A)
- **DB Schema**: false (N/A)
- **UI Prototype**: true (需在 ui_prototype.md 中补齐移动端自适应适配设计章节，定义窄屏降级规则与 Tab 路由映射设计规范)
- **Constraints**: false (N/A)
- **ADR**: false (N/A)
- **Agent Runtime**: false (N/A)
- **High-risk Items/Approvals**: None
