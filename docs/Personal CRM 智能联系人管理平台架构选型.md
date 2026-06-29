# Personal CRM 智能联系人管理平台架构选型

## 1. 总体架构选型

本项目采用前后端分离的 B/S 架构。

前端负责页面展示、用户交互、表单校验、状态管理和接口调用；后端负责用户认证、权限校验、业务处理、数据持久化、文件上传和 Contact Agent 工具调用；数据库负责保存用户、联系人、事项、标签、头像和 Agent 操作记录等数据。

整体架构如下：

```text
浏览器端
  ↓
Vue3 前端应用
  ↓ RESTful API
Spring Boot 后端服务
  ↓
MySQL 数据库
  ↓
本地文件存储 / 可扩展 OSS
  ↓
Contact Agent / 大模型 API
```

## 2. 前端技术选型

### 2.1 核心技术

前端建议采用：

```text
Vue 3 + Vite + TypeScript + Element Plus + Pinia + Vue Router + Axios + ECharts
```

### 2.2 选型理由

Vue 3 适合中小型后台管理系统，开发效率高，组件生态成熟。Vite 启动速度快，适合课程项目和快速迭代。Element Plus 提供表格、表单、分页、弹窗、日期选择器、上传组件等常用后台管理控件，可以降低页面开发成本。

Pinia 用于管理登录用户信息、Token、全局状态和 Agent 会话状态。Vue Router 用于管理登录页、首页、联系人列表、黑名单列表、事项列表、数据看板和智能助手页面。Axios 用于统一封装接口请求、Token 携带、错误拦截和登录失效处理。

ECharts 用于实现数据看板中的联系人性别比例、未来 7 天事项趋势、事项状态统计等图表。

## 3. 后端技术选型

### 3.1 核心技术

后端建议采用：

```text
Java 17 + Spring Boot 3.5 + MyBatis-Plus + MySQL 8 + Redis 8 + JWT + Resend Email SDK
```

### 3.2 选型理由

Spring Boot 适合构建 RESTful 后端服务，能够快速整合 Web、文件上传、参数校验、拦截器和事务管理等能力。

MyBatis-Plus 在 MyBatis 基础上提供通用 CRUD、分页插件和条件构造器，适合联系人列表、黑名单列表、事项列表等大量增删改查场景。相比 JPA，它对 SQL 的控制更直观，更适合 Java 后端实习项目展示。

MySQL 8 用于保存结构化业务数据，适合用户、联系人、事项、标签、头像记录和 Agent 操作记录等关系型数据。

Redis 用于保存注册激活验证码、60 秒限流发送频率锁、防爆破的 5 次尝试计数。此外，Redis 还用于多用户仪表盘看板聚合数据缓存（Hash 结构，缓存 TTL 为 300 秒），以及 Agent 会话管理中的多轮澄清槽位和滑窗历史状态（String 结构，缓存 TTL 为 600 秒），从而实现高性能和服务器端的无状态自愈。

JWT 用于登录认证和接口鉴权。用户登录成功后，后端签发 Token，前端保存并在后续请求中携带。后端通过拦截器解析 Token，获取当前用户 ID，保证用户只能访问自己的联系人和事项数据。

Resend Email SDK 作为高效发信代理服务，封装异步邮件通知，在后端承担安全激活码和欢迎信发送职责，隔离发信协议底层复杂度。

## 4. 数据库选型

数据库采用 MySQL 8。

主要原因：

1. 项目数据结构关系清晰，适合关系型数据库。
2. 联系人、事项、标签之间存在一对多、多对多关系，MySQL 建模方便。
3. 支持事务，适合头像上传、联系人保存、事项状态变更等一致性要求。
4. 课程验收和部署环境友好，老师和面试官都容易理解。
5. 后续可以通过索引优化搜索、筛选、排序和分页查询。

课程基线表建议包括：

```text
sys_user
user_avatar
contact
contact_avatar
contact_todo
```

扩展表建议包括：

```text
tag
contact_tag
agent_operation_log
```

## 5. 文件上传选型

头像上传建议默认采用本地文件存储，不强制使用 OSS 或图床。

推荐方案：

```text
MultipartFile 上传
  ↓
后端校验文件类型和大小
  ↓
UUID 重命名
  ↓
保存到本地 upload/avatar 目录
  ↓
数据库保存访问路径
  ↓
Spring Boot 静态资源映射
  ↓
前端回显头像
```

同时后端可以抽象文件存储接口：

```java
public interface FileStorageService {
    String uploadAvatar(MultipartFile file);
}
```

默认实现：

```text
LocalFileStorageServiceImpl
```

预留扩展：

```text
OssFileStorageServiceImpl
```

这样课程验收阶段使用本地存储，作品集部署阶段可以扩展 OSS。

## 6. Contact Agent 架构选型

Contact Agent 不建议独立成微服务，建议作为后端中的一个独立业务模块实现。

推荐结构：

```text
AgentController
  ↓
AgentService
  ↓
IntentParser / LLMClient
  ↓
ToolRegistry
  ↓
ContactTool / TodoTool / DashboardTool
  ↓
原有业务 Service
```

Agent 模块不直接操作数据库，而是调用系统已有的业务 Service，例如 ContactService、TodoService。这样可以复用原有权限校验、参数校验、事务控制和异常处理逻辑，避免 Agent 绕过系统边界。

对于查询类操作，Agent 可以直接返回结果。对于新增事项、完成事项、取消事项、加入黑名单等写操作，必须先返回解析结果和影响范围，等待用户确认后再执行。

## 7. 权限与安全选型

系统采用：

```text
JWT + 登录拦截器 + 当前用户上下文 + Service 层数据归属校验
```

后端每个涉及用户数据的接口都必须校验当前用户身份。联系人、事项、标签、头像等数据都需要带有 user_id 或通过 contact_id 间接关联 user_id，防止水平越权。

安全设计重点：

1. 未登录不能访问系统内部接口。
2. 用户只能操作自己的联系人和事项。
3. 新增、修改、删除、状态变更接口必须校验用户归属。
4. 文件上传限制类型和大小。
5. SQL 使用参数化查询。
6. Contact Agent 写操作必须二次确认。
7. Agent 不直接执行数据库写入。

## 8. 项目结构选型

后端建议采用经典三层结构：

```text
controller
service
service.impl
mapper
entity
dto
vo
common
config
exception
interceptor
agent
storage
```

前端建议采用模块化结构：

```text
src
  api
  assets
  components
  router
  stores
  views
  utils
  styles
```

这种结构清晰，适合报告说明，也适合后续扩展。

## 9. 部署架构选型

课程验收与演示阶段建议采用单机容器化/微服务辅助部署：

```text
Nginx
  ↓
Vue 静态资源
  ↓
Spring Boot Jar
  ↓           ↓
MySQL 8     Redis 8 (缓存/安全/会话)
  ↓
本地 upload 文件目录
```

如果只是本地演示，也可以直接：

```text
Vue dev server + Spring Boot + MySQL + Redis
```

作品集部署阶段可以升级为：

```text
Nginx + Spring Boot Jar + MySQL + Redis + OSS / 本地持久化目录 + Resend 邮件发信服务
```

暂时不建议使用分布式微服务（Spring Cloud）、Kubernetes、RabbitMQ、Elasticsearch 等重型方案。它们会增加开发和部署复杂度，但对当前项目核心价值提升不明显。

## 10. 最终推荐技术栈

最终推荐如下：

```text
前端：Vue 3 + Vite + TypeScript + Element Plus + Pinia + Vue Router + Axios + ECharts

后端：Java 17 + Spring Boot 3.5 + MyBatis-Plus + MySQL 8 + Redis 8 + JWT + Resend SDK

文件存储：本地文件存储为主，预留 OSS 扩展接口

智能助手：后端内置 Contact Agent 模块，通过 LLM API + Tool Registry + 二次确认机制 + Redis 分布式会话状态持久化实现

部署：Nginx + Spring Boot Jar + MySQL 8 + Redis 8 + 本地 upload 目录 + 异步邮件发送
```

## 11. 选型结论

本项目采用前后端分离的单体应用架构，能够在保证开发效率和系统稳定性的同时，完整覆盖联系人管理、事项提醒、头像上传、数据看板和 Contact Agent 智能交互等核心需求。

该架构复杂度适中，既能满足课程实习项目的验收要求，也具备一定作品集展示价值。相比微服务架构，本方案更容易实现、部署和维护；相比传统 JSP 或纯后端渲染方案，本方案前端体验更好，模块划分更清晰；相比直接引入 OSS、消息队列等复杂组件，本方案优先保证核心业务链路稳定，并为后续扩展保留接口。
