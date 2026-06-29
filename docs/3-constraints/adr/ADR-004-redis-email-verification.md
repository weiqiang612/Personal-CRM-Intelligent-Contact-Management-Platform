# ADR-004: 基于 Redis 托管临时验证码与 Resend 邮箱认证安全闭环设计

**Date**: 2026-06-28
**Status**: Accepted

## Context
在个人 CRM 平台的账号安全改造（TASK-016）中，需要构建包含“注册邮箱激活”、“忘记密码重置”与“修改绑定邮箱”的高安全认证闭环。验证码属于典型的高频、短生命周期（5分钟）、用后即焚的临时数据。如果直接存入 MySQL 数据库，不仅会造成大量无意义的 IO 开销与磁盘垃圾碎片，还需要编写定时任务或复杂的高并发清理逻辑。此外，针对 60 秒防刷限制与最多 5 次输错锁定，MySQL 高并发下容易引发行锁竞争与性能瓶颈。

## Decision
我们决定采用 **Redis 内存存储托管全量临时验证码与防护策略**，配合 **Resend Java SDK** 进行第三方发信。MySQL 仅用于持久化用户的邮箱激活标记 (`email_verified`, `email_verified_at`)。

具体 Redis 数据结构设计：
1. **验证码数据 Key**：`email:code:{purpose}:{email}` -> `codeHash` (TTL: 300s)
2. **发送频率限制 Key**：`email:send:{purpose}:{email}` -> `1` (TTL: 60s)
3. **防爆破错误锁 Key**：`email:attempt:{purpose}:{email}` -> `attemptCount` (TTL: 300s，达到5次作废验证码)

## Constraints for agents
- 🚫 MUST NOT: 绝对禁止将 5 分钟用后即焚的临时验证码明文或哈希存入 MySQL 数据库表；绝对禁止在代码中硬编码 Resend API Key。
- ⚠️ ASK FIRST before: 修改 Redis 校验 Key 的 TTL 命名空间前，需确认不破坏原有场景防刷逻辑。
- ✅ ALWAYS: 敏感配置（如 `resend.api-key`）必须优先从 Git 忽略的 `application-local.yaml` 或系统环境变量 `RESEND_API_KEY` 中读取。验证码生成必须使用单向哈希加密存储于 Redis 中。

## Rationale
- **天然 TTL 自动淘汰**：Redis 的过期删除机制完全免去了手动 `DELETE` 操作，提升系统性能。
- **原子性高并发防刷**：使用 Redis 原子指令（如 `SETEX` 与 `INCRBY`）可极速响应限流策略，彻底规避数据库行锁阻塞。
- **工业级生产标准**：符合互联网大型大厂作品集项目的架构标准，职责分离明确（Redis 管临时态，MySQL 管持久态）。

## Consequences
- **更容易**：极速实现防刷限流、防止暴力破解以及优雅的定时失效；发信失败与防刷逻辑彻底解耦。
- **更难/额外要求**：系统运行需要依赖本地或远程 Redis 服务实例，要求 Spring Boot 连接池配置完备并包含容错提示。
