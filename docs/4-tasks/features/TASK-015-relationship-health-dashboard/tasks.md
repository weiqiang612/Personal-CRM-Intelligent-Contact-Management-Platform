# TASK-015: Tasks

**Spec**: `spec.md`
**Status**: Completed

## Key decisions
- 在后端引入 `RelationshipHealthStatus` 枚举及 `ContactHealthCalculator` 工具类，统一状态计算逻辑，便于后续扩展。
- 后端采用批量查询聚合映射与内存分类模式，避免 SQL 硬编码阈值和 N+1 数据库查询。
- 前端保留 ECharts 动态渲染，采用绿色(#10b981)、黄色(#f59e0b)、红色(#ef4444)、灰色(#9ca3af)标准的健康度主题配色。

## Progress

<!-- Document Maintenance Tasks -->
- [x] T1 — Update API contract: `docs/2-designs/api_contract.md` · covers: AC-CONTRACT
- [x] T2 — Update UI prototype: `docs/2-designs/ui_prototype.md` · covers: AC-UI-UX

<!-- Implementation Tasks -->
- [x] T3 — Backend: Create RelationshipHealthStatus enum & RelationshipHealthVO · covers: AC-001, AC-002
- [x] T4 — Backend: Implement ContactHealthCalculator business logic · covers: AC-002
- [x] T5 — Backend: Add batch query in ActivityLogMapper & DashboardController endpoint · covers: AC-001, AC-002, AC-CONTRACT
- [x] T6 — Backend: Write DashboardControllerTest & ContactHealthCalculatorTest · covers: AC-002
- [x] T7 — Frontend: Update API definition in `src/api/dashboard.ts` · covers: AC-CONTRACT
- [x] T8 — Frontend: Implement relationship health chart & layout in `DashboardView.vue` · covers: AC-001, AC-UI-UX

<!-- Gates & Housekeeping -->
- [x] T9 — Run `mvn -f personal_crm_backend/pom.xml test` — all tests must pass · covers: AC-002
- [x] T10 — Verify ACs: update `passes` to `true` in spec.md for each passing criterion · covers: AC-001, AC-002, AC-CONTRACT, AC-UI-UX
- [x] T11 — Update `docs/4-tasks/CURRENT_PLAN.md` — mark this task complete · covers: doc-maintenance

## Dependencies
- T2 requires T1
- T3, T4 require T1
- T5 requires T3, T4
- T6 requires T5
- T7 requires T1
- T8 requires T5, T7
- T9 requires T6
- T10 requires T8, T9
- T11 requires T10
