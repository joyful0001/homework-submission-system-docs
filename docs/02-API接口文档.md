# API接口文档

## 基础信息
- **基础路径**: http://localhost:8081/api/v1
- **认证方式**: JWT Token (需要在请求头中携带 Authorization: Bearer {token})
- **数据格式**: JSON
- **字符编码**: UTF-8

## 通用响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {
    // 实际数据内容
  }
}
```

## 错误码说明
| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 认证模块 🔑
*负责人: 后端A -张欣彤*

### 1. 用户登录
```http
POST /auth/login
Content-Type: application/json
无需认证

功能: 用户登录系统，获取访问令牌
权限: 所有用户

请求体:
{
  "username": "2024001001",
  "password": "123456"
}

响应:
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "username": "2024001001",
      "name": "张三",
      "role": "student",
      "className": "24软件工程B班"
    }
  }
}
```

### 2. 获取当前用户信息
```http
GET /auth/me
Authorization: Bearer {token}

功能: 获取当前登录用户的详细信息
权限: 所有登录用户

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "2024001001",
    "name": "张三",
    "role": "student",
    "className": "24软件工程班",
    "createdTime": "2024-11-26 10:00:00"
  }
}
```

### 3. 用户退出登录
```http
POST /auth/logout
Authorization: Bearer {token}

功能: 用户退出登录
权限: 所有登录用户

响应:
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```

---

## 作业管理模块 📚
*负责人: 后端A - 张欣彤*

### 4. 创建作业 (教师)
```http
POST /homeworks
Authorization: Bearer {token}
Content-Type: application/json

功能: 教师创建新作业
权限: 仅教师角色

请求体:
{
  "title": "Java面向对象编程作业",
  "content": "完成以下编程题...",
  "deadline": "2024-12-31 23:59:59"
}

响应:
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1,
    "title": "Java面向对象编程作业",
    "content": "完成以下编程题...",
    "deadline": "2024-12-31 23:59:59",
    "teacherId": 2,
    "createdTime": "2024-11-26 11:00:00"
  }
}
```

### 5. 获取作业列表 (教师视角)
```http
GET /homeworks/teacher?page=1&size=10&status=all
Authorization: Bearer {token}

功能: 教师查看自己发布的所有作业
权限: 仅教师角色
参数:
  - page: 页码，默认1
  - size: 每页数量，默认10
  - status: 状态筛选，all-全部, active-进行中, expired-已过期

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "title": "Java面向对象编程作业",
        "deadline": "2024-12-31 23:59:59",
        "totalStudents": 45,
        "submittedCount": 30,
        "status": "active",
        "createdTime": "2024-11-26 10:00:00"
      }
    ],
    "total": 15,
    "page": 1,
    "size": 10
  }
}
```

### 6. 获取作业列表 (学生视角)
```http
GET /homeworks/student?page=1&size=10&status=all
Authorization: Bearer {token}

功能: 学生查看分配给自己的作业列表
权限: 仅学生角色
参数:
  - page: 页码，默认1
  - size: 每页数量，默认10
  - status: 状态筛选，all-全部, submitted-已提交, unsubmitted-未提交, expired-已过期

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "title": "Java面向对象编程作业",
        "deadline": "2024-12-31 23:59:59",
        "teacherName": "张老师",
        "myStatus": "unsubmitted",
        "submittedTime": null,
        "createdTime": "2024-11-26 10:00:00"
      }
    ],
    "total": 8,
    "page": 1,
    "size": 10
  }
}
```

### 7. 获取作业详情
```http
GET /homeworks/{homeworkId}
Authorization: Bearer {token}

功能: 获取作业的详细信息
权限: 学生(自己的作业)/教师(自己发布的作业)

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "Java面向对象编程作业",
    "content": "完成以下编程题:\n1. 实现学生类...",
    "deadline": "2024-12-31 23:59:59",
    "teacherId": 2,
    "teacherName": "张老师",
    "attachmentPath": "/files/assignment.pdf",
    "createdTime": "2024-11-26 10:00:00"
  }
}
```

### 8. 修改作业
```http
PUT /homeworks/{homeworkId}
Authorization: Bearer {token}
Content-Type: application/json

功能: 教师修改作业信息
权限: 仅教师角色(只能修改自己发布的作业)

请求体: 同创建作业

响应:
{
  "code": 200,
  "message": "修改成功",
  "data": null
}
```

### 9. 删除作业
```http
DELETE /homeworks/{homeworkId}
Authorization: Bearer {token}

功能: 教师删除作业
权限: 仅教师角色(只能删除自己发布的作业)

响应:
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 作业提交模块 📤
*负责人: 后端B - 黄锦锋*

### 10. 提交作业
```http
POST /submissions
Authorization: Bearer {token}
Content-Type: multipart/form-data

功能: 学生提交作业
权限: 仅学生角色
参数:
  - homeworkId: 作业ID (必填)
  - content: 提交内容 (选填)
  - attachment: 附件文件 (选填)

响应:
{
  "code": 200,
  "message": "提交成功",
  "data": {
    "id": 1,
    "homeworkId": 1,
    "studentId": 1,
    "content": "这是我的作业答案...",
    "attachmentPath": "/files/submission_1.zip",
    "status": "submitted",
    "submittedTime": "2024-11-26 11:30:00",
    "createdTime": "2024-11-26 11:30:00"
  }
}
```

### 11. 修改提交 (截止时间前)
```http
PUT /submissions/{submissionId}
Authorization: Bearer {token}
Content-Type: multipart/form-data

功能: 学生在截止时间前修改已提交的作业
权限: 仅学生角色(只能修改自己的提交)

请求体: 同提交作业

响应:
{
  "code": 200,
  "message": "修改成功",
  "data": null
}
```

### 12. 获取我的提交记录
```http
GET /submissions/student?homeworkId=1
Authorization: Bearer {token}

功能: 学生查看自己的提交记录
权限: 仅学生角色
参数:
  - homeworkId: 作业ID (选填，不传则查询所有)

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "homeworkId": 1,
        "homeworkTitle": "Java面向对象编程作业",
        "content": "这是我的作业答案...",
        "attachmentPath": "/files/submission_1.zip",
        "status": "submitted",
        "submittedTime": "2024-11-26 11:30:00",
        "createdTime": "2024-11-26 11:30:00"
      }
    ],
    "total": 5
  }
}
```

### 13. 获取作业提交情况 (教师)
```http
GET /homeworks/{homeworkId}/submissions?page=1&size=20&submitStatus=all
Authorization: Bearer {token}

功能: 教师查看某次作业的所有学生提交情况
权限: 仅教师角色(只能查看自己发布的作业)
参数:
  - page: 页码，默认1
  - size: 每页数量，默认20
  - submitStatus: 提交状态，all-全部, submitted-已提交, unsubmitted-未提交

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "homeworkInfo": {
      "id": 1,
      "title": "Java面向对象编程作业",
      "deadline": "2024-12-31 23:59:59"
    },
    "submissionStats": {
      "totalStudents": 45,
      "submittedCount": 30,
      "unsubmittedCount": 15
    },
    "list": [
      {
        "id": 1,
        "studentId": 1001,
        "studentName": "张三",
        "status": "submitted",
        "submittedTime": "2024-11-26 14:30:00",
        "content": "提交内容...",
        "attachmentName": "homework.zip"
      }
    ],
    "total": 45,
    "page": 1,
    "size": 20
  }
}
```

---

## 文件服务模块 📁
*负责人: 后端B - 黄锦锋*

### 14. 文件上传
```http
POST /files/upload
Authorization: Bearer {token}
Content-Type: multipart/form-data

功能: 上传文件
权限: 所有登录用户
参数:
  - file: 文件 (必填)

响应:
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "fileId": "abc123def456",
    "fileName": "document.pdf",
    "fileUrl": "/files/download/abc123def456",
    "fileSize": 1024000,
    "uploadTime": "2024-11-26 11:35:00"
  }
}
```

### 15. 文件下载
```http
GET /files/download/{fileId}
Authorization: Bearer {token}

功能: 下载文件
权限: 文件所有者或相关教师
响应: 文件流
```

---

## 数据统计模块 📊
*负责人: 后端B - 黄锦锋*

### 16. 教师仪表盘
```http
GET /dashboard/teacher
Authorization: Bearer {token}

功能: 获取教师工作台统计数据
权限: 仅教师角色

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "totalHomeworks": 15,
    "activeHomeworks": 3,
    "totalSubmissions": 245,
    "recentHomeworks": [
      {
        "id": 1,
        "title": "Java作业",
        "deadline": "2024-12-31 23:59:59",
        "submittedRate": "66.7%"
      }
    ]
  }
}
```

### 17. 学生仪表盘
```http
GET /dashboard/student
Authorization: Bearer {token}

功能: 获取学生学习台统计数据
权限: 仅学生角色

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "pendingHomeworks": 2,
    "submittedHomeworks": 8,
    "expiredHomeworks": 1,
    "recentHomeworks": [
      {
        "id": 1,
        "title": "Java作业",
        "deadline": "2024-12-05 23:59:59",
        "myStatus": "unsubmitted"
      }
    ]
  }
}
```

---

## 接口权限总结

### 学生权限
- 登录、查看个人信息
- 查看作业列表、作业详情
- 提交作业、查看自己的提交记录
- 查看学习统计数据

### 教师权限
- 登录、查看个人信息
- 创建、修改、删除作业
- 查看所有作业及提交情况
- 查看教学统计数据
- 下载学生提交的文件

---

## 状态说明

### 作业状态
- `active`: 进行中（未到截止时间）
- `expired`: 已过期（超过截止时间）

### 提交状态
- `unsubmitted`: 未提交
- `submitted`: 已提交
- `draft`: 草稿（暂存）

### 用户角色
- `student`: 学生
- `teacher`: 教师

---

