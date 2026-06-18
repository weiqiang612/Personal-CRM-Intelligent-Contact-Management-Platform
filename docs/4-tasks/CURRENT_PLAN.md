# Current Plan

> Session entry point. Read this first every session.

## Active feature
`docs/4-tasks/features/TASK-002-contact-module/`
- spec.md ✅ Ready
- tasks.md → Not started

## Previous implementation task
`docs/4-tasks/features/TASK-001-mvp-foundation/`
- spec.md ✅ Ready
- tasks.md ✅ Completed

## Stages
- Stage 0: 基于最新参考图确认静态 HTML 原型，冻结页面结构与视觉骨架 ✅
- Stage 1: 完成开发前准备，冻结需求、数据库和接口基线 ✅
- Stage 2: 搭建后端基础设施，完成统一响应、异常处理、MyBatis-Plus、JWT 鉴权和认证入口 ✅
- Stage 3: 替换前端脚手架页面，建立 CRM 基础布局、登录页、路由、状态管理与 API 层 ✅
- Stage 4: 按独立任务顺序实现核心业务模块：contacts -> todos -> dashboard/upload
- Stage 5: 实现 Agent 查询与写操作确认链路
- Stage 6: 自测、接口联调、补测试文档与验收材料

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
- [ ] Implementation (18 tasks)
- 里程碑 1：T1-T6，完成联系人后端契约复核、实体映射、Service/Controller 与后端验证
- 里程碑 2：T7-T13，完成联系人前端 API、路由、列表、表单、详情和黑名单页面
- 里程碑 3：T14-T18，完成前后端联调、测试、自测验收和计划状态回写

## Backlog roadmap

### Phase 2: 核心业务完善
- TASK-002: 联系人模块完善
  - 范围：联系人前后端联调、联系人详情页、黑名单体验完善、分页筛选与表单校验补齐
- TASK-003: 事项模块完善
  - 范围：事项前后端联调、事项列表筛选、状态流转页面交互、逾期展示与异常处理补齐
- TASK-004: 看板与上传模块完善
  - 范围：看板图表展示、统计接口联调、头像上传回显、上传失败处理与页面体验补齐

### Phase 3: 增强功能
- TASK-005: 标签管理功能
  - 范围：标签表管理、联系人绑定标签、标签筛选与联调
- TASK-006: Contact Agent 查询能力
  - 范围：自然语言查询联系人和事项、查询结果展示、Agent 日志基础留痕
- TASK-007: Contact Agent 写操作确认链路
  - 范围：创建事项等写操作预确认、确认执行、取消执行、操作记录完善

### Phase 4: 质量与交付
- TASK-008: 测试与缺陷修复
  - 范围：单元测试、接口测试、自测清单、联调缺陷修复、验收问题收敛
- TASK-009: 部署与验收材料
  - 范围：部署脚本和说明、演示环境验证、用户使用说明、答辩或验收材料整理
- TASK-010: 项目收尾与版本归档
  - 范围：README 和 docs 收口、版本标签、最终成果检查、可展示内容整理

## Completed
- 2026-06-15: completed TASK-000 static HTML prototype baseline (10 high-fidelity pages)
- 2026-06-15: initialized backend Spring Boot project
- 2026-06-15: initialized frontend Vue 3 project
- 2026-06-15: generated Harness-style collaboration scaffold
- 2026-06-15: aligned database design, schema.sql, and api contract
- 2026-06-15: added SRS draft, backend dev config, seed script, and root README

## Notes for next session
- 当前进入 `TASK-002` 联系人模块；先复核联系人 API、DB 和 UI 原型，再实现后端联系人闭环。
- 联系人模块必须基于当前 JWT 用户做数据隔离，禁止跨用户读取或修改联系人。
- 前端联系人页面必须基于 `docs/2-designs/ui_prototype.md` 和 `prototype/contacts.html`、`prototype/contact-detail.html`、`prototype/contact-form.html`、`prototype/blacklist.html` 迁移实现。
- 本任务不实现头像上传、事项、标签或 Agent；这些入口只保留后续任务提示或禁用态。
