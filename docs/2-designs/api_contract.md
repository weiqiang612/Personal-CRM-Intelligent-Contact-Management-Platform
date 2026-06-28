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
- 标签：列表、新增、修改、删除
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

### 4.3 用户注册

- `POST /api/v1/auth/register`

请求体：

```json
{
  "username": "newuser",
  "password": "newpassword123"
}
```

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```

### 4.4 修改电子邮箱

- `PUT /api/v1/auth/profile/email`

请求体：

```json
{
  "email": "ethan@example.com"
}
```

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```

### 4.5 修改手机号码

- `PUT /api/v1/auth/profile/phone`

请求体：

```json
{
  "phone": "13800000000"
}
```

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```

### 4.6 修改密码

- `PUT /api/v1/auth/profile/password`

请求体：

```json
{
  "oldPassword": "oldpassword123",
  "newPassword": "newpassword123"
}
```

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```

## 5. Contact APIs (联系人模块)


> [!NOTE]
> 本模块支持联系人标签绑定与筛选，标签的 CRUD 管理接口位于单独的标签模块（`/api/v1/tags`）。

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
    "remarks": "合作客户，喜欢喝茶",
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
  "phone": "13800000000",
  "remarks": "合作客户，喜欢喝茶",
  "tagIds": [1, 2]
}
```

请求体说明：
- `tagIds`: 标签主键 ID 数组（可选，例如 `[1, 2]`）。关联时后端会验证传入的标签 ID 是否都属于当前登录用户；若存在越权标签 ID，则拒绝创建并返回 `40301` 错误码。

### 5.4 修改联系人

- `PUT /api/v1/contacts/{contactId}`

请求体与新增保持一致（同样支持通过可选的 `tagIds` 进行标签的更新与绑定）。如果传入空数组或未传，则表示该联系人将不绑定任何标签。

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

### 5.7 删除联系人 (逻辑删除)

- `DELETE /api/v1/contacts/{contactId}`

业务规则：
- 路径参数 `contactId` 必须是当前登录用户拥有的联系人，否则拒绝并返回 `40301` 或 `40401`。
- 执行逻辑删除，更新 `deleted = 1`。
- 自动清理 `contact_tag` 表中与该联系人的标签绑定关联记录，但不删除联系人历史已创建的事项。

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```


## 6. Tag APIs (标签模块)

### 6.1 标签列表

- `GET /api/v1/tags`

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "tagId": 1,
      "name": "同学",
      "color": "#409EFF",
      "createdAt": "2026-06-15 10:00:00"
    },
    {
      "tagId": 2,
      "name": "朋友",
      "color": "#67C23A",
      "createdAt": "2026-06-15 10:00:00"
    }
  ]
}
```

### 6.2 新增标签

- `POST /api/v1/tags`

请求体：

```json
{
  "name": "工作",
  "color": "#E6A23C"
}
```

请求校验规则：
- `name`: 标签名称（必填，String，1-50字符）。同用户下标签名称唯一，若重复则返回 `40901` 状态冲突。
- `color`: 标签颜色（可选，String，最大20字符），格式为十六进制颜色码（例如 `#E6A23C`）或 CSS 颜色值。

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "tagId": 3,
    "name": "工作",
    "color": "#E6A23C",
    "createdAt": "2026-06-21 16:00:00"
  }
}
```

### 6.3 修改标签

- `PUT /api/v1/tags/{tagId}`

请求体：

```json
{
  "name": "工作-改",
  "color": "#F56C6C"
}
```

请求校验规则：
- 路径参数 `tagId` 对应 `tag` 表的主键 `id`，必须为当前用户拥有的标签，否则拒绝修改并返回 `40301` 或 `40401`。
- 修改后的 `name` 在该用户下必须唯一（排除当前标签自身），冲突时返回 `40901`。

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "tagId": 3,
    "name": "工作-改",
    "color": "#F56C6C",
    "createdAt": "2026-06-21 16:00:00"
  }
}
```

### 6.4 删除标签

- `DELETE /api/v1/tags/{tagId}`

业务规则：
- 路径参数 `tagId` 必须是当前用户拥有的标签，否则拒绝并返回 `40301` 或 `40401`。
- 删除标签时，自动清理当前用户下的联系人标签绑定关系（删除 `contact_tag` 关联表中与该 `tag_id` 的记录），但不删除联系人本身。
- 其他用户的同名标签不受任何影响。

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```

## 7. 事项模块 (Todo APIs)

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

### 6.6 删除事项 (逻辑删除)

- `DELETE /api/v1/todos/{matterId}`

业务规则：
- 路径参数 `matterId` 必须是当前登录用户拥有的事项，否则拒绝并返回 `40301` 或 `40401`。
- 执行逻辑删除，更新 `deleted = 1`。

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```


## 8. 上传模块 (Upload APIs)

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

## 9. 看板模块 (Dashboard APIs)

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

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "date": "2026-06-22",
      "count": 3
    },
    {
      "date": "2026-06-23",
      "count": 0
    }
  ]
}
```

### 8.3 关系维护状态 (Relationship Health)

- `GET /api/v1/dashboard/relationship-health`

响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "active": 5,
    "followUp": 2,
    "inactive": 1,
    "noActivity": 3,
    "total": 11
  }
}
```

## 10. Agent 模块 (Agent APIs)

### 9.1 自然语言查询接口（本期实现）

- `POST /api/v1/agent/query`

请求体：

```json
{
  "input": "帮我查张三的联系方式",
  "sessionId": "会话 ID（可选，用于继续多轮会话）"
}
```

响应体结构定义：
* `code`: 0 (正常), 非 0 (异常)
* `message`: 提示信息
* `data`: 查询结果负载对象
  * `queryType`: 标识命中数据类别，取值 `"contact"` (联系人), `"todo"` (待办/事项), `"unsupported"` (不支持/超出本期范围)
  * `intent`: 识别出的意图编码，如 `"query_contact"`, `"query_todo"`, `"unsupported"`
  * `summary`: 统一的摘要文本描述
  * `sessionId`: 会话 ID，用于续传多轮会话上下文
  * `isClarifying`: 是否正处于多轮澄清状态 (true/false)
  * `missingFields`: 缺失的字段列表 (如 `["contactName", "todoTime", "content"]`)
  * `parsedParams`: 当前已解析出的属性参数映射
  * `results`: 列表，泛型承载实体对象。若是联系人，则返回包含标签和头像相对路径的联系人信息；若是事项，则包含关联联系人姓名及逾期标志。

#### 9.1.1 联系人查询成功响应示例（以姓名“张三”为例）：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "queryType": "contact",
    "intent": "query_contact",
    "summary": "已查找到包含关键字 '张三' 的联系人信息",
    "sessionId": "s_8df26b1c-c9c4-4b53-bd32-cc5432ff11a8",
    "isClarifying": false,
    "missingFields": [],
    "parsedParams": {
      "keyword": "张三"
    },
    "results": [
      {
        "ctId": "C000000001",
        "name": "张三",
        "phone": "13800000000",
        "wechat": "zhangsan_123",
        "email": "zhangsan@example.com",
        "gender": 1,
        "birthday": "1995-05-10",
        "notes": "重要客户",
        "avatarUrl": "/uploads/contact-avatar/avatar.jpg",
        "status": 0,
        "tags": ["客户", "朋友"]
      }
    ]
  }
}
```

#### 9.1.2 事项查询成功响应示例（以张三的待办为例）：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "queryType": "todo",
    "intent": "query_todo",
    "summary": "已查找到联系人 '张三' 关联的 1 条待办事项",
    "sessionId": "s_8df26b1c-c9c4-4b53-bd32-cc5432ff11a8",
    "isClarifying": false,
    "missingFields": [],
    "parsedParams": {
      "contactName": "张三",
      "status": 0
    },
    "results": [
      {
        "matterId": "T000000001",
        "contactId": "C000000001",
        "contactName": "张三",
        "todoTime": "2026-06-25 15:00:00",
        "content": "和张三确定合同细节",
        "status": 0,
        "priority": 2,
        "overdue": false,
        "completedAt": null,
        "cancelledAt": null,
        "createdAt": "2026-06-22 10:00:00"
      }
    ]
  }
}
```

#### 9.1.3 空结果成功响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "queryType": "contact",
    "intent": "query_contact",
    "summary": "未查找到匹配的联系人信息",
    "sessionId": "s_8df26b1c-c9c4-4b53-bd32-cc5432ff11a8",
    "isClarifying": false,
    "missingFields": [],
    "parsedParams": {
      "keyword": "张三"
    },
    "results": []
  }
}
```

#### 9.1.4 超出本期范围（如写操作或未识别意图）响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "queryType": "unsupported",
    "intent": "unsupported",
    "summary": "抱歉，本期智能助手仅支持查询联系人和事项，暂不支持创建、修改等写操作。",
    "sessionId": "s_8df26b1c-c9c4-4b53-bd32-cc5432ff11a8",
    "isClarifying": false,
    "missingFields": [],
    "parsedParams": {},
    "results": []
  }
}
```

#### 9.1.5 输入校验失败响应示例：

```json
{
  "code": 40000,
  "message": "输入内容不能为空"
}
```

### 9.2 自然语言写操作与二次确认接口

#### 9.2.1 写操作预处理接口 (意图解析与预确认)

- `POST /api/v1/agent/execute`

请求体：

```json
{
  "input": "明天下午三点提醒我联系张三确认合同",
  "sessionId": "会话 ID（可选，用于多轮继续对话和澄清）"
}
```

响应体（成功解析，生成预确认卡片）：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "logId": 45,
    "needConfirm": 1,
    "actionType": "create_todo",
    "summary": "已为您生成待办事项预确认卡片，请核对：在 2026-06-25 15:00:00 提醒联系 张三 确认合同。",
    "sessionId": "s_9ae1234c-a111-4c6e-8fa8-bb33aaff8920",
    "isClarifying": false,
    "missingFields": [],
    "parsedParams": {
      "contactId": "C000000001",
      "contactName": "张三",
      "todoTime": "2026-06-25 15:00:00",
      "content": "确认合同",
      "priority": 1
    }
  }
}
```

响应体（信息缺失，无法进入确认阶段）：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "logId": 46,
    "needConfirm": 0,
    "actionType": "create_todo",
    "summary": "抱歉，无法识别您想为哪位联系人创建事项，请输入包含联系人姓名的指令，例如：“明天下午三点提醒我联系张三确认合同”",
    "sessionId": "s_9ae1234c-a111-4c6e-8fa8-bb33aaff8920",
    "isClarifying": true,
    "missingFields": ["contactName"],
    "parsedParams": {
      "todoTime": "2026-06-25 15:00:00",
      "content": "确认合同",
      "priority": 1
    }
  }
}
```

响应体（非本期支持的写操作，如拉黑或删除）：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "logId": 47,
    "needConfirm": 0,
    "actionType": "unsupported",
    "summary": "抱歉，智能助手目前仅支持“创建事项”的写操作，暂不支持其他类型的写请求。",
    "sessionId": "s_9ae1234c-a111-4c6e-8fa8-bb33aaff8920",
    "isClarifying": false,
    "missingFields": [],
    "parsedParams": {}
  }
}
```

#### 9.2.2 写操作二次确认接口 (执行或取消)

- `POST /api/v1/agent/confirm`

请求体：

```json
{
  "logId": 45,
  "action": "confirm"
}
```

或者：

```json
{
  "logId": 45,
  "action": "cancel"
}
```

确认创建成功响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "matterId": "T000000002",
    "contactId": "C000000001",
    "contactName": "张三",
    "todoTime": "2026-06-25 15:00:00",
    "content": "确认合同",
    "status": 0,
    "priority": 1,
    "overdue": false,
    "completedAt": null,
    "cancelledAt": null,
    "createdAt": "2026-06-24 10:00:00"
  }
}
```

取消创建成功响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": null
}
```

异常情况错误响应（如 logId 越权或已被处理过）：

```json
{
  "code": 40301,
  "message": "无权操作此日志记录",
  "data": null
}
```

## 10. 天气代理接口 (Weather API)

### 10.1 获取天气信息

- `GET /api/v1/weather`

#### 查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| address | string | 否 | 联系人的模糊地址。传入后优先按地址解析城市。 |
| latitude | number | 否 | 浏览器 GEO 纬度。需与 `longitude` 成对传入。 |
| longitude | number | 否 | 浏览器 GEO 经度。需与 `latitude` 成对传入。 |

#### 响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "cityName": "杭州",
    "currentTemp": "26",
    "currentText": "多云",
    "currentIcon": "101",
    "dailyForecast": [
      {
        "date": "2026-06-23",
        "textDay": "小雨",
        "textNight": "中雨",
        "tempMin": "20",
        "tempMax": "26",
        "iconDay": "305"
      },
      {
        "date": "2026-06-24",
        "textDay": "中雨",
        "textNight": "多云",
        "tempMin": "21",
        "tempMax": "25",
        "iconDay": "306"
      },
      {
        "date": "2026-06-25",
        "textDay": "多云",
        "textNight": "晴",
        "tempMin": "21",
        "tempMax": "28",
        "iconDay": "101"
      }
    ]
  }
}
```

## 11. 活动日志模块 (Activity Feed APIs)

### 11.1 查询联系人活动轨迹时间线

- `GET /api/v1/contacts/{contactId}/activities`

#### 请求参数：

| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| contactId | string | 是 | 路径参数，联系人业务 ID（如 C000000001） |
| limit | integer | 否 | 返回最大条数，默认 10，最大 50 |

#### 响应体：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "activityId": "ACT000000001",
      "contactId": "C000000001",
      "eventType": "TODO_COMPLETED",
      "title": "完成了待办事项",
      "description": "准备下周一的合作协议初稿",
      "occurredAt": "2026-06-28T14:30:00Z"
    },
    {
      "activityId": "ACT000000002",
      "contactId": "C000000001",
      "eventType": "TAG_CHANGED",
      "title": "变更了联系人标签",
      "description": "绑定标签：潜在客户, 战略伙伴",
      "occurredAt": "2026-06-28T10:15:00Z"
    },
    {
      "activityId": "ACT000000003",
      "contactId": "C000000001",
      "eventType": "CONTACT_CREATED",
      "title": "创建了联系人",
      "description": "新增联系人信息：张伟",
      "occurredAt": "2026-06-27T09:00:00Z"
    }
  ]
}
```

错误响应：
- `40301`：无权访问当前联系人的活动日志（不属于当前登录用户）。
- `40401`：联系人不存在。

## 12. 当前实现状态 (Implementation Status)

- 所有核心与扩展架构模块接口契约均已冻结与同步。
- TASK-014 活动轨迹流接口规范已在本文件完成定义。

