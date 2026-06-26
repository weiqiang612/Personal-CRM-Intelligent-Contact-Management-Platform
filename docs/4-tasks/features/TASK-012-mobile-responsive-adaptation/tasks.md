# TASK-012: Tasks

**Spec**: `spec.md`
**Status**: Completed

## Key decisions
- **独立底栏双模导航**：利用 CSS 媒体查询在移动窄屏下隐藏 Sidebar 导航，改为在视图最底端常驻 Fixed 定位的底栏 TabBar 导航，保持两端路由无缝同步。
- **全屏智能助手**：不再以悬浮窗/抽屉形式覆盖主界面，移动端下点击第四个 Tab 切换并渲染为全屏 AgentView 聊天视图，彻底贴底，隐藏工作台上的 Agent 悬浮球以防遮挡。
- **宽度100%图表自适应**： Dashboard 底部以纵向堆叠模式排布趋势图与性别比例图。高度锁定为 180px 左右，通过给面板加 min-width: 0 !important 配合挂载 resize 侦听器动态调用 ECharts 原生 resize API 规避宽度截断。
- **表格降级卡片流**：联系人与待办在手机端隐藏 `el-table`，采用精心设计的垂直卡片列表样式以支撑手势交互。
- **联系人详情页自适应**：头部卡片转为列堆叠，按钮动作组采用 2x2 网格，资料项允许长文本折行，相关事项卡片重构为垂直堆叠，消除横向溢出。

## Progress

- [x] T1 — 更新UI原型说明文档：在 `docs/2-designs/ui_prototype.md` 中追加移动端双模布局和窄屏卡片降级规则 · covers: doc-maintenance
- [x] T2 — Frontend: 改造全局 Layout 容器 (`LayoutView.vue`)，实现移动窄屏下 Sidebar 隐藏、底栏 TabBar 常驻与极简 Header · covers: AC-001
- [x] T3 — Frontend: 改造工作台 (`DashboardView.vue`) 顶层数据卡片自适应平铺排版、重要联系人左右滑动及事项列表美化，去除多余悬浮球 · covers: AC-002, AC-003
- [x] T4 — Frontend: 改造工作台 (`DashboardView.vue`) 底部统计图表平铺排版，压缩高度并挂载 `chartInstance.resize()` 确保 100% 宽度自适应 · covers: AC-002
- [x] T5 — Frontend: 改造联系人列表 (`ContactsView.vue`) 与待办事项列表 (`TodosView.vue`) 的 Table 表格，小屏下隐藏并切换为精美卡片流 · covers: AC-003
- [x] X6 — Frontend: 改造智能助手聊天页 (`AgentView.vue`)，剔除 PC 留白框，高度撑满 100vh 扣除底栏，消息气泡及发送栏贴底自适应 · covers: AC-004
- [x] T7 — Frontend: 运行前端构建验证命令 `npm --prefix personal_crm_web run build` 确保打包无误 · covers: AC-005
- [x] T8 — Verify: 验证 ACs：在 `spec.md` 中将全部测试通过的标准的 `passes` 修改为 `true` · covers: AC-005
- [x] T9 — Update: 更新 `docs/4-tasks/CURRENT_PLAN.md` 标志本适配任务完成 · covers: doc-maintenance
- [x] T11 — Bugfix: 修复联系人详情页 (ContactDetailView.vue) 移动端按钮被截断、事项卡片溢出的排版 Bug · covers: AC-005

## Dependencies
- T2, T3 require T1
- T4 requires T3
- T5 requires T2
- X6 requires T2
- T7, T8 require T4, T5, X6
- T9 requires T7, T8
