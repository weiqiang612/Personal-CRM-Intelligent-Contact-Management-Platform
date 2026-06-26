# TASK-009: Weather Widget Integration
 
**Status**: Approved
**Created**: 2026-06-23
**Feature dir**: `docs/4-tasks/features/TASK-009-weather-widget-integration/`
 
## Objective
实现系统工作台与联系人详情页的动态天气小组件展示，提升 CRM 系统的智能度、使用黏度与客户沟通温度。

## Scope
 
### In scope
- 后端新增天气代理接口 `GET /api/v1/weather`，统一保护 API Key 并不暴露在前端。
- 后端集成和风天气 (QWeather) 接口，包含城市模糊检索接口（GeoAPI）与 3 天天气预报接口。
- 后端引入内存数据缓存，对同一城市的天气查询进行 2 小时缓存，规避每日免费额度限制。
- 前端首页（工作台 `DashboardView.vue`）最上方新增问候栏（Dashboard Header），展示登录用户的本地天气（利用请求 IP 自动定位，若为局域网或开发环境则兜底展示“杭州”）。
- 前端联系人详情页（`ContactDetailView.vue`）的基础资料中，在“地址”栏右侧加入温度与天气状态微缩展示。
- 在详情页中点击地址或天气图标，可折叠滑出近三天天气预报面板（包括温度区间、天气状况及对应图标）。
- 若联系人没有地址，天气展示处友好提示“未录入城市，去设置”，点击“去设置”可引导用户补全地址。
- 清理联系人列表页（`ContactListView.vue`）顶部旧有的静态天气代码。

### Out of scope
- 历史天气数据查询。
- 国内外各气象服务商的切换（固定只使用和风天气）。

## Acceptance criteria
 
```json
[
  {
    "id": "AC-001",
    "category": "functional",
    "description": "首页顶部展示用户本地天气",
    "steps": [
      "登录系统并进入工作台首页",
      "验证页面最上方渲染了 Dashboard Header 区域，显示“早上好/下午好，[当前用户名]！”与日期",
      "验证右侧正确展示当前用户所在城市的当前温度与天气（开发环境 127.0.0.1 默认兜底显示“杭州”天气）"
    ],
    "passes": true
  },
  {
    "id": "AC-002",
    "category": "functional",
    "description": "联系人详情页天气折叠展示",
    "steps": [
      "进入一个填写了有效地址（如“浙江省杭州市西湖区”）的联系人详情页",
      "验证左侧“基础资料”卡片的“地址”行右侧正确呈现了天气小图标及温度",
      "点击地址或天气图标，验证能在下方折叠拉出近三天天气预报面板，且数据正确（展示今天、明天、后天的天气概况与温度区间）"
    ],
    "passes": true
  },
  {
    "id": "AC-003",
    "category": "edge-case",
    "description": "联系人无地址兜底与快速引导",
    "steps": [
      "进入一个未录入地址字段的联系人详情页",
      "验证地址行右侧显示“未录入城市，去设置”的提示文本",
      "点击“去设置”按钮，验证能够顺畅进入该联系人的编辑页面"
    ],
    "passes": true
  },
  {
    "id": "AC-004",
    "category": "security",
    "description": "API 密钥安全防泄露与缓存命中",
    "steps": [
      "在浏览器中抓包或查看网络请求，验证前端只调用了后端代理接口 `/api/v1/weather`，未出现任何直连和风天气 API 且未泄漏 API Key 的情况",
      "在短时间内连续多次访问相同城市的天气，验证后端日志中显示命中缓存，未重复向和风天气发起网络请求"
    ],
    "passes": true
  },
  {
    "id": "AC-CONTRACT",
    "category": "integration",
    "description": "API 与设计契约一致性验证",
    "steps": [
      "验证 `/api/v1/weather` 的接口请求与返回结构符合全局统一响应包装格式 { code, message, data }",
      "验证 docs/2-designs/api_contract.md 中已同步收录该天气接口的契约定义"
    ],
    "passes": true
  },
  {
    "id": "AC-UI-UX",
    "category": "integration",
    "description": "天气组件响应式布局与交互验证",
    "steps": [
      "使用 Chrome DevTools 的 resize_page 工具将分辨率分别调整为桌面端 (1440x900) 与移动端 (375x812)",
      "验证首页 Dashboard Header 及联系人详情页地址展开的天气面板在两个视口下布局均正常无错位溢出",
      "测试天气小组件的按钮组件，使用 hover 工具验证其悬停和点击时的反馈动效",
      "运行 list_console_messages 确保控制台无未捕获的 JS 异常或加载报错",
      "在 artifacts/ 目录下保存截屏 desktop_dashboard_weather.webp 与 mobile_contact_detail_weather.webp"
    ],
    "passes": true
  }
]
```

## Notes

### 2026-06-24 closeout check
- 已重新执行 `mvn -f personal_crm_backend/pom.xml test`，73 个测试全部通过，其中包含 `WeatherServiceTest`。
- 已重新执行 `npm --prefix personal_crm_web run build`，前端类型检查与生产构建通过。
- 用户已在 2026-06-24 明确确认 UI 验收通过，本任务不再以天气截图归档作为关闭前置条件。
 
### Documentation impact
- **Requirements**: true (更新 `docs/1-requirements/requirements_analysis.md` 说明天气组件的设计背景与使用场景)
- **Architecture**: true (更新 `docs/2-designs/architecture.md` 说明后端缓存代理与 GeoAPI 模糊城市检索架构)
- **API Contract**: true (更新 `docs/2-designs/api_contract.md` 新增天气代理接口规范)
- **DB Schema**: false (N/A)
- **UI Prototype**: true (更新 `docs/2-designs/ui_prototype.md` 记录首页问候栏与联系人详情页中天气小组件的放置策略)
- **Constraints**: false (N/A)
- **ADR**: false (N/A)
- **Agent Runtime**: false (N/A)
- **High-risk Items/Approvals**: None
