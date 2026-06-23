# TASK-009: Tasks
 
**Spec**: `spec.md`
**Status**: In Progress
 
## Key decisions
- **后端安全代理 + 2小时缓存**：由 Spring Boot 后端提供 `/api/v1/weather` 统一代理请求，并基于内存进行 2 小时结果缓存，避免重复调用浪费 API Key 的免费额度。
- **地名模糊检索**：后端在向和风天气申请数据前，先调用和风 GeoAPI (`https://geoapi.qweather.com/v2/city/lookup`) 将前端传入的详细 `address` 模糊识别为精确的城市 ID，保证查询兼容性。
- **IP 归属地定位**：若请求中不传 `address`（如首页加载），则后端根据用户网络 IP 定位常驻城市，本地/局域网 IP 则默认兜底检索“杭州”。
 
## Progress
 
<!-- Document Maintenance Tasks -->
- [ ] T1 — 更新需求分析：修改 `docs/1-requirements/requirements_analysis.md` 说明天气组件的使用场景与业务价值 · covers: doc-maintenance
- [ ] T2 — 更新架构设计：修改 `docs/2-designs/architecture.md` 补充后端安全代理与缓存方案 · covers: doc-maintenance
- [ ] T3 — 更新 API 契约：在 `docs/2-designs/api_contract.md` 新增 `/api/v1/weather` 接口参数与返回结构 · covers: AC-CONTRACT
- [ ] T4 — 更新 UI 原型：在 `docs/2-designs/ui_prototype.md` 记录首页问候栏与详情页地址天气的放置设计 · covers: doc-maintenance
 
<!-- Implementation Tasks: Backend -->
- [ ] T5 — 后端：创建天气相关的数据对象（WeatherVO、WeatherInfo 等 DTO/VO） · covers: AC-001, AC-002, AC-CONTRACT
- [ ] T6 — 后端：实现 `WeatherService` 业务，封装 RestTemplate 请求和风 GeoAPI 与天气预报 API，并使用 Guava 或内存 Map 实现 2 小时缓存机制 · covers: AC-001, AC-002, AC-004
- [ ] T7 — 后端：实现 `WeatherController` 接口层，注入 IP 解析与兜底杭州逻辑 · covers: AC-001, AC-002, AC-004
- [ ] T8 — 后端：编写并运行 `WeatherServiceTest.java` 单元测试，验证解析与缓存命中 · covers: AC-004
 
<!-- Implementation Tasks: Frontend -->
- [ ] T9 — 前端：在 `src/api/weather.ts` 中声明天气 API 访问函数与 TypeScript 接口类型 · covers: AC-CONTRACT
- [ ] T10 — 前端：修改首页 `DashboardView.vue`，在顶部新增个性化问候头部栏，对接天气接口展示“我的天气” · covers: AC-001, AC-UI-UX
- [ ] T11 — 前端：修改详情页 `ContactDetailView.vue`，在“地址”栏嵌入微缩天气图标及三天天气折叠面板，实现无地址时的快速引导 · covers: AC-002, AC-003, AC-UI-UX
- [ ] T12 — 前端：清理 `ContactListView.vue` 顶部旧有的静态天气小组件 DOM 及逻辑 · covers: doc-maintenance
 
<!-- Gates & Housekeeping -->
- [ ] T13 — 运行 `mvn -f personal_crm_backend/pom.xml test` —— 确认后端全量测试通过
- [ ] T14 — 验证 AC：更新 `spec.md` 中的所有验收标准 (Acceptance Criteria) passes 为 true
- [ ] T15 — 更新 `docs/4-tasks/CURRENT_PLAN.md` —— 标记本任务完成
 
## Dependencies
- T2 requires T1
- T3, T4 require T2
- T5, T6 require T3
- T7 requires T6
- T8 requires T7
- T9 requires T3
- T10 requires T9
- T11 requires T9
- T12 requires T10, T11
- T13, T14 require T8, T11, T12
- T15 requires T13, T14
