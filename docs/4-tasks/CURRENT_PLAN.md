# Current Plan

> Session entry point. Read this first every session.

## Active feature
None (TASK-007 completed, pending next feature scoping)

## Previous implementation task
`docs/4-tasks/features/TASK-007-contact-agent-query/`
- spec.md ✅ Ready
- tasks.md ✅ Completed

## Stages
- Stage 0: 基于最新参考图确认静态 HTML 原型，冻结页面结构与视觉骨架 ✅
- Stage 1: 完成开发前准备，冻结需求、数据库和接口基线 ✅
- Stage 2: 搭建后端基础设施，完成统一响应、异常处理、MyBatis-Plus、JWT 鉴权和认证入口 ✅
- Stage 3: 替换前端脚手架页面，建立 CRM 基础布局、登录页、路由、状态管理与 API 层 ✅
- Stage 4: 按独立任务顺序实现核心业务模块：contacts -> todos -> tags -> dashboard/upload ✅
- Stage 5: 实现 Agent 查询与写操作确认链路 (只读查询已完成)
- Stage 6: 自测、接口联调、补测试文档与验收材料 ✅

### TASK-000: Static HTML Prototype Baseline
- [x] Implementation (12 tasks) Completed
- 里程碑 1：T1-T4，固定任务包、文档引用和 `TASK-000 -> TASK-001` 前置关系
- 里程碑 2：T5-T9，完成共享样式和 8 个静态 HTML 原型页面
- 里程碑 3：T10-T12，完成结构校验、AC 回写和计划状态回写

### TASK-001: MVP Foundation Delivery
- [x] Implementation (14 tasks) Completed
- 里程碑 1：T1-T4，完成后端基础设施与鉴权边界
- 里程碑 2：T5-T6，完成认证模块最小可用链路
- 里程碑 3：T7-T10，基于已确认 HTML 原型完成前端骨架替换、鉴权跳转和认证联调
- 里程碑 4：T11-T14，完成测试、自测验收和计划状态回写

### TASK-002: Contact Module
- [x] Implementation (18 tasks) Completed
- 里程碑 1：T1-T6，完成联系人后端契约复核、实体映射、Service/Controller 与后端验证
- 里程碑 2：T7-T13，完成联系人前端 API、路由、列表、表单、详情和黑名单页面
- 里程碑 3：T14-T18，完成前后端联调、测试、自测验收和计划状态回写

### TASK-003: Todo Module
- [x] Implementation (19 tasks) Completed
- 里程碑 1：T1-T7，完成事项后端契约复核、实体映射、Service/Controller 与后端验证
- 里程碑 2：T8-T14，完成事项前端 API、路由、列表、新增页面和后续任务提示
- 里程碑 3：T15-T19，完成前后端联调、测试、自测验收和计划状态回写

### TASK-004: Contact Tag and Blacklist Polish
- [x] Implementation (17 tasks) Completed

### TASK-005: Tag Management MVP
- [x] Implementation (15 tasks) Completed

### TASK-006: Dashboard and Upload MVP
- [x] Implementation (17 tasks) Completed

### TASK-007: Contact Agent Query Capability
- [x] Implementation (15 tasks) Completed

## Backlog roadmap

### Phase 2: 核心业务完善
- TASK-004: 联系人标签与黑名单收口
  - 范围：联系人列表/详情/黑名单真实标签展示、标签筛选契约、种子数据归属一致性和黑名单页一致性补齐
- TASK-005: 标签管理 MVP
  - 范围：标签列表、新增、编辑、删除、联系人标签绑定/解绑、真实标签筛选选项与用户数据隔离
- TASK-006: 看板与上传模块完善
  - 范围：看板图表展示、统计接口联调、头像上传回显、上传失败处理与页面体验补齐

### Phase 3: 增强功能
- TASK-007: Contact Agent 查询能力
  - 范围：自然语言查询联系人和事项、查询结果展示、Agent 日志基础留痕
- TASK-008: Contact Agent 写操作确认链路
  - 范围：创建事项等写操作预确认、确认执行、取消执行、操作记录完善

### Phase 4: 质量与交付
- TASK-009: 测试与缺陷修复
  - 范围：单元测试、接口测试、自测清单、联调缺陷修复、验收问题收敛
- TASK-010: 交付收口
  - 范围：部署说明、演示环境验证、用户使用说明、答辩材料、README/docs 收口和版本归档

## Completed
- 2026-06-22: completed TASK-007 Contact Agent Query Capability (15 tasks, frontend & backend integration, E2E multi-viewport verification)
- 2026-06-22: completed TASK-006 Dashboard and Upload MVP (17 tasks, frontend & backend integration and E2E verification)
- 2026-06-21: completed TASK-005 Tag Management MVP (15 tasks, frontend & backend integration and E2E verification)
- 2026-06-15: completed TASK-000 static HTML prototype baseline (10 high-fidelity pages)
- 2026-06-15: initialized backend Spring Boot project
- 2026-06-15: initialized frontend Vue 3 project
- 2026-06-15: generated Harness-style collaboration scaffold
- 2026-06-15: aligned database design, schema.sql, and api contract
- 2026-06-15: added SRS draft, backend dev config, seed script, and root README
- 2026-06-18: completed TASK-002 Contact Module (frontend & backend integration and E2E verification)
- 2026-06-20: completed TASK-003 Todo Module (frontend & backend integration and E2E verification)
- 2026-06-20: completed TASK-004 Contact Tag and Blacklist Polish (frontend & backend integration and E2E verification)

## Notes for next session
- 当前活动任务已切换为 `TASK-007 Contact Agent Query Capability`。
- 需要优先完成独立 `/agent` 页面上的联系人/事项自然语言查询闭环，不做工作台抽屉联动与真实模型接入。
