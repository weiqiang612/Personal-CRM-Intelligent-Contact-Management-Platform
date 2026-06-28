# 数据库设计 (Database Schema)

## 1. 建表基线与设计原则 (Conventions)
- **命名规范**：使用下划线命名，例如 `user_id`、`contact_id`、`todo_status`。
- **通用字段**：核心业务表统一包含主键、创建时间、更新时间；逻辑删除按业务需要单独评估。
- **物理外键**：默认不使用物理外键约束，由应用层和索引策略维护关系一致性。
- **归属隔离**：所有核心业务数据必须直接或间接关联 `user_id`，以支持用户数据隔离。

## 2. ER 实体关系图 (Entity Relationship Diagram)
```mermaid
erDiagram
    SYS_USER ||--o| USER_AVATAR : has
    SYS_USER ||--o{ CONTACT : owns
    CONTACT ||--o| CONTACT_AVATAR : has
    CONTACT ||--o{ CONTACT_TODO : has
    SYS_USER ||--o{ TAG : owns
    CONTACT ||--o{ CONTACT_TAG : links
    TAG ||--o{ CONTACT_TAG : links
    SYS_USER ||--o{ AGENT_OPERATION_LOG : creates
    CONTACT ||--o{ ACTIVITY_LOG : logs
```

## 3. 规划表基线 (Planned Tables)
| 表名 | 作用 | 当前状态 |
|---|---|---|
| `sys_user` | 登录用户与身份隔离 | 已定稿 |
| `user_avatar` | 用户头像元数据 | 已定稿 |
| `contact` | 联系人基础信息与黑名单状态 | 已定稿 |
| `contact_avatar` | 联系人头像元数据 | 已定稿 |
| `contact_todo` | 联系人事项与状态流转 | 已定稿 |
| `tag` | 联系人标签 | 已定稿（扩展） |
| `contact_tag` | 联系人与标签多对多关系 | 已定稿（扩展） |
| `agent_operation_log` | Agent 输入、意图、确认与结果留痕 | 已定稿（扩展） |
| `activity_log` | 联系人与事项业务活动轨迹日志 | 新增（TASK-014） |

## 4. 已扫描的数据表结构 (Scanned Database Tables)
- 当前初始化 DDL 已写入 `personal_crm_backend/src/main/resources/schema.sql`。
- 当前表清单与 `docs/Personal CRM 智能联系人管理平台数据库设计.md` 和 `schema.sql` 保持一致。
- 课程验收基线为 5 张表：`sys_user`、`user_avatar`、`contact`、`contact_avatar`、`contact_todo`。
- 项目扩展表为：`tag`、`contact_tag`、`agent_operation_log`、`activity_log`。

## 5. 关系级联与多租户隔离规则 (Cascade & Isolation Rules)
- **多租户物理隔离**：`tag` 表、`contact_tag` 表与 `activity_log` 表均显式包含 `user_id` 字段。所有新增、查询、修改均以当前 JWT 的 `user_id` 限制，确保标签及关联的多租户物理隔离。
- **活动日志防越权约束**：`activity_log` 必须包含 `user_id` 与 `contact_id`。查询时联合 `user_id + contact_id` 过滤，确保用户无法查看不属于自己的联系人活动轨迹。
- **删除标签级联清理**：根据无物理外键原则，`tag` 表和 `contact_tag` 表之间没有物理外键。当用户删除某标签时，系统由服务层在应用级自动清理 `contact_tag` 表中 `tag_id` 等于该标签的关联记录，从而断开联系人与该标签的关联，但不可破坏联系人 (`contact`) 及其他数据。
- **逻辑删除与关联规则**：`contact` 表和 `contact_todo` 表新增 `deleted` (TINYINT DEFAULT 0) 逻辑删除字段。逻辑删除联系人时，应用层自动清理 `contact_tag` 关联，历史事项保持完好；MyBatis-Plus 通过 `@TableLogic` 自动屏蔽已被删除的记录。
- **数据修补备注 (Data Fixes)**：**TASK-004** 发现并修正了 `contact_tag` 表种子数据中的多租户归属问题，将错归属于未定义用户 `U000000003` 和 `U000000004` 的关联数据全部安全划归为当前演示用户 `U000000001`，确保多租户隔离与隔离关系的强一致性。

## 6. 原始方案索引 (Source Reference)
- 本文为结构化数据库摘要。
- 若需要查看字段级设计说明、状态定义、索引设计和指导书基线对齐说明，请回看：`docs/Personal CRM 智能联系人管理平台数据库设计.md`。

## 7. 数据模型微调 (Schema Adjustments)
- **2026-06-27**：在 `sys_user` 表中新增 `email` (VARCHAR(100)) 与 `phone` (VARCHAR(30)) 字段，用以支持个人资料管理中修改电子邮箱和修改手机号码功能。
- **2026-06-27**：在 `contact` 和 `contact_todo` 表中新增 `deleted` (TINYINT NOT NULL DEFAULT 0) 字段，支持实体逻辑删除。
- **2026-06-28**：新增 `activity_log` 表定义（TASK-014）：
  - `id` BIGINT AUTO_INCREMENT PRIMARY KEY
  - `activity_id` VARCHAR(64) NOT NULL UNIQUE
  - `user_id` VARCHAR(64) NOT NULL
  - `contact_id` VARCHAR(64) NOT NULL
  - `event_type` VARCHAR(32) NOT NULL
  - `title` VARCHAR(128) NOT NULL
  - `description` TEXT NULL
  - `occurred_at` DATETIME NOT NULL
  - `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
  - 索引：`idx_user_contact_occurred(user_id, contact_id, occurred_at)`


