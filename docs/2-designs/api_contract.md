# 接口设计与契约 (API Contract)

## 1. 全局设计原则 (API Design Guidelines)
- **协议与前缀**：RESTful API，统一前缀 `/api/v1`。
- **认证方式**：登录成功后返回 JWT，后续请求通过 `Authorization: Bearer <token>` 访问受保护资源。
- **响应体格式**：所有接口统一包装为 `{ code, message, data }`，避免前后端出现多套协议。
- **错误码规范**：4xx 表示客户端错误，5xx 表示服务端错误；权限、参数和业务错误需有明确可区分码值。
- **确认规则**：Contact Agent 的写操作接口必须支持“解析结果预览 + 二次确认”流程。
- **归属规则**：请求体和路径中不传 `user_id`，后端统一从 JWT 中解析当前用户身份并做数据归属校验。
- **主键暴露策略**：对前端和接口层统一暴露业务编号，例如 `userId`、`contactId`、`matterId`；数据库自增 `id` 仅用于内部实现。

## 2. 规划模块 (Planned Modules)
- 认证：登录、登出、当前用户信息
- 联系人：列表、详情、新增、修改、加入黑名单、恢复
- 事项：列表、新增、完成、取消、按时间与状态筛选
- 看板：联系人数量、事项统计、今日事项、逾期事项
- 上传：头像上传、头像访问路径回显
- Agent：自然语言查询、自然语言创建事项、写操作确认

## 3. 统一响应格式 (Response Envelope)

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

失败响应：

```json
{
  "code": 40001,
  "message": "validation failed",
  "data": null
}
```

建议错误码基线：

| code | 含义 |
|---|---|
| 0 | 成功 |
| 40001 | 参数校验失败 |
| 40101 | 未登录或 token 无效 |
| 40301 | 无权访问当前数据 |
| 40401 | 资源不存在 |
| 40901 | 状态冲突或重复操作 |
| 50001 | 服务内部异常 |

## 4. 认证模块 (Auth APIs)

### 4.1 登录

- `POST /api/v1/auth/login`

请求体：

```json
{
  "username": "ethan",
  "password": "123456"
}
```

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "jwt-token",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "user": {
      "userId": "U000000001",
      "username": "ethan"
    }
  }
}
```

### 4.2 获取当前用户

- `GET /api/v1/auth/me`

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "userId": "U000000001",
    "username": "ethan",
    "status": 0,
    "avatarUrl": "/uploads/user-avatar/u0001.png"
  }
}
```

## 5. Contact APIs (联系人模块)

> [!NOTE]
> 本模块在 TASK-004 中收口标签展示与筛选，不新增标签管理 CRUD 接口（如 `/api/v1/tags`）。

### 5.1 联系人列表

- `GET /api/v1/contacts`

查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页条数，默认 10 |
| keyword | string | 否 | 按姓名、手机号、微信模糊搜索 |
| status | int | 否 | 0 正常，1 黑名单 |
| tag | string | 否 | 按标签名称筛选 |
| sortBy | string | 否 | `createdAt`、`birthday` |
| sortOrder | string | 否 | `asc` 或 `desc` |

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "contactId": "C000000001",
        "name": "张三",
        "phone": "13800000000",
        "wechat": "zhangsan",
        "email": "zhangsan@example.com",
        "gender": 1,
        "birthday": "2001-05-01",
        "status": 0,
        "avatarUrl": "/uploads/contact-avatar/c0001.png",
        "tags": ["同学", "朋友"],
        "createdAt": "2026-06-15 10:00:00"
      }
    ],
    "page": 1,
    "pageSize": 10,
    "total": 1
  }
}
```

### 5.2 联系人详情

- `GET /api/v1/contacts/{contactId}`

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "contactId": "C000000001",
    "name": "张三",
    "phone": "13800000000",
    "wechat": "zhangsan",
    "email": "zhangsan@example.com",
    "gender": 1,
    "birthday": "2001-05-01",
    "status": 0,
    "avatarUrl": "/uploads/contact-avatar/c0001.png",
    "tags": ["同学", "朋友"],
    "createdAt": "2026-06-15 10:00:00"
  }
}
```

### 5.3 新增联系人

- `POST /api/v1/contacts`

请求体：

```json
{
  "name": "张三",
  "address": "杭州",
  "postcode": "310000",
  "qq": "123456",
  "wechat": "zhangsan",
  "email": "zhangsan@example.com",
  "gender": 1,
  "birthday": "2001-05-01",
  "phone": "13800000000"
}
```

### 5.4 修改联系人

- `PUT /api/v1/contacts/{contactId}`

请求体与新增保持一致。

### 5.5 加入黑名单

- `PATCH /api/v1/contacts/{contactId}/blacklist`

请求体：

```json
{
  "status": 1
}
```

### 5.6 恢复联系人

- `PATCH /api/v1/contacts/{contactId}/restore`

请求体：

```json
{
  "status": 0
}
```

## 6. 事项模块 (Todo APIs)

### 6.1 事项列表

- `GET /api/v1/todos`

查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页条数，默认 10 |
| contactId | string | 否 | 按联系人过滤 |
| status | int | 否 | 0 待完成，1 已取消，2 已完成 |
| startTime | string | 否 | 开始时间，格式 `yyyy-MM-dd HH:mm:ss` |
| endTime | string | 否 | 结束时间，格式 `yyyy-MM-dd HH:mm:ss` |
| sortOrder | string | 否 | `asc` 或 `desc`，默认按 `todoTime` 排序 |

### 6.2 新增事项

- `POST /api/v1/todos`

请求体：

```json
{
  "contactId": "C000000001",
  "todoTime": "2026-06-16 09:00:00",
  "content": "回访项目进展",
  "priority": 1
}
```

### 6.3 完成事项

- `PATCH /api/v1/todos/{matterId}/complete`

状态变更为 `2`，并写入 `completedAt`。

### 6.4 取消事项

- `PATCH /api/v1/todos/{matterId}/cancel`

状态变更为 `1`，并写入 `cancelledAt`。

### 6.5 事项状态规则

- 仅 `status = 0` 的事项允许变更为完成或取消。
- 已完成和已取消事项不允许重复提交状态变更。
- 逾期事项通过 `todoTime < now` 且 `status = 0` 动态计算，不单独存储字段。

## 7. 上传模块 (Upload APIs)

### 7.1 上传联系人头像

- `POST /api/v1/uploads/contact-avatar`
- `Content-Type: multipart/form-data`

表单字段：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| file | file | 是 | 图片文件 |
| contactId | string | 是 | 联系人业务编号 |

限制规则：

- 仅允许 `jpg`、`jpeg`、`png`、`webp`
- 单文件大小不超过 2MB
- 后端统一重命名并返回访问地址

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "fileName": "8f8c8b2a.png",
    "accessUrl": "/uploads/contact-avatar/8f8c8b2a.png"
  }
}
```

### 7.2 上传用户头像

- `POST /api/v1/uploads/user-avatar`

规则与联系人头像一致，只是不再传 `contactId`。

## 8. 看板模块 (Dashboard APIs)

### 8.1 看板概览

- `GET /api/v1/dashboard/overview`

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "contactCount": 20,
    "blacklistCount": 2,
    "pendingTodoCount": 5,
    "todayTodoCount": 1,
    "overdueTodoCount": 1
  }
}
```

### 8.2 事项趋势

- `GET /api/v1/dashboard/todo-trend?days=7`

### 8.3 联系人性别分布

- `GET /api/v1/dashboard/contact-gender-distribution`

## 9. Agent 模块 (Agent APIs)

### 9.1 自然语言请求

- `POST /api/v1/agent/execute`

请求体：

```json
{
  "input": "明天下午三点提醒我联系张三"
}
```

查询类响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "mode": "direct",
    "intent": "query_contact",
    "reply": "已找到联系人张三",
    "result": {
      "contactId": "C000000001",
      "name": "张三"
    }
  }
}
```

写操作预确认响应示例：

```json
{
  "code": 0,
  "message": "need_confirm",
  "data": {
    "mode": "confirm",
    "operationId": 1001,
    "intent": "create_todo",
    "summary": "将为联系人张三创建一条事项",
    "preview": {
      "contactId": "C000000001",
      "todoTime": "2026-06-16 15:00:00",
      "content": "联系张三"
    }
  }
}
```

### 9.2 确认执行写操作

- `POST /api/v1/agent/confirm`

请求体：

```json
{
  "operationId": 1001,
  "confirmed": true
}
```

规则：

- `confirmed = true` 才允许真正落库。
- `confirmed = false` 时记录取消状态，不执行写操作。
- `operationId` 对应 `agent_operation_log.id`。

## 10. 当前实现状态 (Implementation Status)

- 后端 Controller 仍未开始实现。
- 当前文档已经固定了开发第一阶段所需的最小接口集合。
- 后续若新增管理员、批量导入或标签管理页面，需要在本文件继续追加契约，而不是绕过文档直接写接口。
