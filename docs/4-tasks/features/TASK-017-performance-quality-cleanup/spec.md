# 规格说明 - TASK-017 性能优化与代码质量收尾

## 目标描述
作为项目的最后一个阶段，该任务的核心目标是对系统进行全面的性能优化与代码质量收尾。主要包括解决分布式部署时 AI 会话状态的共享与过期自动清理、利用 Redis 对高频的仪表盘聚合统计接口做缓存与自动失效处理、建立全局统一的常量池管理与中文错误提示治理、清理全限定类名及编译警告，并针对前端进行 ECharts 实例合理销毁防内存泄露以及 Vite 打包 Chunk 拆分优化，从而彻底达到生产级交付标准。

## 需求说明
- **后端 AI 会话分布式改造 (Redis)**：
  - 重构 `AgentSessionManager`，废弃 JVM 进程内内存缓存 `ConcurrentHashMap`，改用 Redis（使用 JSON 格式）存储多轮对话状态 `AgentSessionState`。
  - 为会话状态在 Redis 中设置 10 分钟的生存时间（TTL），完全移除应用内原有的 `@Scheduled` 定时清理任务。
- **后端仪表盘看板数据缓存 (Redis)**：
  - 在 `DashboardController` 中，对四个统计接口（`/overview`、`/todo-trend`、`/contact-gender-distribution`、`/relationship-health`）引入基于 Redis 的 Hash 缓存，Key 格式为 `dashboard:cache:{userId}`，Hash 字段分别为 `overview`、`todo-trend:{days}` 等，整个 Hash 键设置 5 分钟（300 秒）的生存时间。
  - 在用户对联系人或待办事项执行任意写操作（新增/修改/删除/状态变更等）时，触发被动失效机制，通过一行命令彻底清除当前用户的看板缓存：`redisTemplate.delete("dashboard:cache:" + userId)`。
- **代码质量与提示汉化规范**：
  - 新建统一常量类 `Constants`，将 Redis Key 前缀、过期时间、时间日期格式化等常量进行分类统一管理。
  - 检查全项目，将所有直接引用的全限定类名（如 `java.util.concurrent.TimeUnit` 等）全部改为头部 `import` 导包形式。
  - 对 Service 业务层中残留的硬编码英文异常提示语进行全局“中文汉化”，并将其全部收容进常量类 `Constants.Message` 中，统一通过常量引用。
  - 清理全工程未使用的 imports，以及不合规的 `System.out` 等控制台输出，确保统一使用 SLF4J 打印。
  - 对涉及反射/泛型转换的 unchecked 编译警告进行排查治理，为核心控制器和 Service 的公共接口补齐规范的 JavaDoc 注释。
- **性能打点监测**：
  - 在 `ContactHealthCalculator`、`AgentServiceImpl` 和 `WeatherServiceImpl` 的高负荷逻辑方法中，使用原生 `System.currentTimeMillis()` 并在 SLF4J 中输出计算毫秒耗时。
- **前端内存泄露治理与打包优化**：
  - 在 `DashboardView.vue` 卸载时（`onBeforeUnmount`），主动对折线图及环形图的两个 ECharts 实例执行 `dispose()` 销毁动作，彻底阻断由于 DOM 移除引起的浏览器内存泄露。
  - 在前端新建 `src/utils/constants.ts` 文件，将 `localStorage` 的键名等魔术字符串进行常量规范整理。
  - 修改 `vite.config.ts`，在生产环境构建时自动丢弃 `console` 和 `debugger`；同时将 `element-plus` 和 `echarts` 独立划分为 Rollup 输出分包，提升加载响应。

## 验收标准 (Acceptance Criteria)
```json
[
  {
    "id": "AC-REDIS-AGENT-SESSION",
    "category": "performance",
    "description": "AI Agent 会话状态成功迁移至 Redis 分布式存储，带 10 分钟 TTL 过期自愈",
    "steps": [
      "验证多轮对话会话以 agent:session:{sessionId} 键名保存在 Redis 中且格式为 JSON",
      "验证会话 TTL 设为 600 秒且到期自动清理，获取时能自动实现降级新建",
      "移除本地的 @Scheduled 定时任务且相关单元测试 Mock 正常通过"
    ],
    "passes": false
  },
  {
    "id": "AC-REDIS-DASHBOARD-CACHE",
    "category": "performance",
    "description": "看板统计数据在 Redis Hash 中二级缓存，在联系人/待办事项写操作时一键失效",
    "steps": [
      "验证看板的四个统计方法在未变更数据时能稳定命中 Redis Hash dashboard:cache:{userId} 对应的字段且不读取 MySQL",
      "验证 Redis 中该缓存 Hash 生存时间为 300 秒 (5分钟)",
      "验证在执行联系人新增/修改/删除/状态变更，或待办事项新增/完成/取消等写接口后，整个 Hash 键被成功 DEL 清除"
    ],
    "passes": false
  },
  {
    "id": "AC-CODE-STANDARDS",
    "category": "functional",
    "description": "建立常量池，清理全限定类名，异常信息汉化，治理编译警告",
    "steps": [
      "验证新建了 com.weiqiang.personal_crm_backend.common.Constants 常量池类且无硬编码魔术字符串",
      "验证全项目抛出的 BusinessException 硬编码提示全部清洗为友好中文，并通过常量引用",
      "验证全项目无全限定类名引用，所有第三方/核心类均通过 import 导包",
      "运行 mvn clean compile，验证无泛型强转等编译警告且未使用的 imports 被完全清空"
    ],
    "passes": false
  },
  {
    "id": "AC-FRONTEND-OPTIMIZATION",
    "category": "performance",
    "description": "前端销毁 ECharts 规避内存泄露，Vite 优化打包体积",
    "steps": [
      "验证 DashboardView.vue 组件卸载时，两个 ECharts 实例被正确执行了 .dispose()",
      "运行 npm run build 验证生产包打包时自动剔除了 console 和 debugger",
      "验证 Element Plus 和 ECharts 库在打包产物中被成功分拆为独立的 chunks，无体积超限警告"
    ],
    "passes": false
  }
]
```

## 备注 (Notes)
- **文档影响分析 (Documentation Impact)**：
  - requirements: false
  - architecture: true (需要更新 architecture.md 以同步 Redis 的分布式会话及 Hash 缓存的细节设计)
  - api: false (未改变原有的 API 输入输出规范)
  - db: false
  - ui: false
  - constraints: false
  - adr: false
  - agent-runtime: false
