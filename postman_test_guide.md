# 高校学生心理危机预警与自助服务平台 - Postman测试指南

## 1. 环境准备

### 1.1 系统环境
- **后端服务**：运行在 http://localhost:8081
- **前端服务**：运行在 http://localhost:5175
- **数据库**：MySQL 8.0（已配置）

### 1.2 测试账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin001 | 123456 | 系统管理员，可访问所有功能 |
| 辅导员 | coun001 | 123456 | 辅导员账号，可管理学生和预警 |
| 学生 | stu001 | 123456 | 学生账号，可使用学生端功能 |

## 2. Postman配置

### 2.1 导入集合
1. 打开Postman
2. 点击左上角「导入」按钮
3. 选择 `d:\study\postman_collection.json` 文件
4. 点击「导入」完成

### 2.2 环境变量配置
1. 点击右上角「环境」下拉菜单
2. 选择「管理环境」
3. 点击「添加」
4. 环境名称：`心理平台测试环境`
5. 添加以下变量：

| 变量名 | 值 | 说明 |
|--------|------|------|
| baseUrl | http://localhost:8081 | 后端服务地址 |
| token | 空 | 登录后会自动填充 |

6. 点击「保存」

## 3. 测试流程

### 3.1 登录流程
1. 展开「认证模块」→「用户登录」
2. 修改请求体中的 `username` 和 `password` 为对应角色的账号
3. 点击「发送」
4. 复制响应中的 `token` 值
5. 在环境变量中更新 `token` 变量值

### 3.2 测试其他接口
1. 选择要测试的接口
2. 确保请求头中的 `Authorization` 已设置（格式：`Bearer {{token}}`）
3. 根据接口要求修改请求参数
4. 点击「发送」查看响应

## 4. 核心功能测试

### 4.1 学生功能
- ✅ 情绪日记保存与查询
- ✅ AI聊天记录
- ✅ 匿名倾诉
- ✅ 心理测评提交
- ✅ 查看测评报告

### 4.2 辅导员功能
- ✅ 学生档案管理
- ✅ 预警认领与处理
- ✅ 干预记录管理
- ✅ 家长联系记录
- ✅ 数据大屏查看

### 4.3 管理员功能
- ✅ 系统配置
- ✅ 用户管理
- ✅ 预警系统管理
- ✅ 数据统计
- ✅ 操作日志查看

## 5. 测试注意事项

### 5.1 权限测试
- **学生账号**：只能访问学生相关接口
- **辅导员账号**：只能访问辅导员相关接口和学生接口
- **管理员账号**：可以访问所有接口

### 5.2 数据验证
- 检查响应状态码是否正确
- 检查响应数据格式是否符合预期
- 检查数据一致性（如预警状态、学生信息等）

### 5.3 异常测试
- 测试缺少必传参数的情况
- 测试无效参数的情况
- 测试未授权访问的情况

## 6. 接口分类

### 6.1 认证接口
- `POST /api/auth/login` - 用户登录

### 6.2 学生接口
- `GET /api/student/{studentId}` - 获取学生详情
- `GET /api/student/tasks` - 获取学生任务列表
- `POST /api/student/task/submit` - 提交测评
- `POST /api/student/emotion/save` - 保存情绪日记
- `GET /api/student/emotion/list` - 获取情绪日记列表
- `POST /api/student/ai-chat/save` - 保存聊天记录
- `GET /api/student/ai-chat/history` - 获取聊天历史
- `POST /api/student/confession/save` - 保存倾诉

### 6.3 辅导员接口
- `GET /api/counselor/students` - 获取学生列表
- `GET /api/counselor/warnings` - 获取预警列表
- `POST /api/counselor/warning/claim` - 认领预警
- `POST /api/counselor/intervention/save` - 保存干预记录
- `GET /api/counselor/parent-contact/list` - 获取家长联系记录

### 6.4 预警系统接口
- `GET /api/warning/list` - 获取预警列表
- `POST /api/warning/claim` - 认领预警
- `POST /api/warning/process` - 处理预警
- `POST /api/warning/close` - 结案预警

### 6.5 数据大屏接口
- `GET /api/dashboard/overview` - 管理员数据概览
- `GET /api/dashboard/teacher/overview` - 辅导员数据概览

## 7. 测试结果记录

| 接口名称 | 请求方法 | 路径 | 状态码 | 测试结果 | 备注 |
|----------|----------|------|--------|----------|------|
| 用户登录 | POST | /api/auth/login | 200 | ✅ | 登录成功 |
| 获取学生详情 | GET | /api/student/1001 | 200 | ✅ | 学生信息正确 |
| 保存情绪日记 | POST | /api/student/emotion/save | 200 | ✅ | 保存成功 |
| 获取预警列表 | GET | /api/counselor/warnings | 200 | ✅ | 预警数据正确 |
| 数据大屏概览 | GET | /api/dashboard/overview | 200 | ✅ | 数据统计正确 |

## 8. 常见问题

### 8.1 登录失败
- **原因**：账号密码错误或服务未启动
- **解决**：检查账号密码，确认后端服务运行状态

### 8.2 接口返回401
- **原因**：未授权，token过期或无效
- **解决**：重新登录获取新token

### 8.3 接口返回500
- **原因**：服务器内部错误
- **解决**：检查后端服务日志，查看具体错误信息

### 8.4 数据不一致
- **原因**：缓存或数据库同步问题
- **解决**：刷新页面或重启服务

## 9. 联系信息

如果遇到测试问题，请联系：
- 系统开发：技术支持团队
- 后端服务：http://localhost:8081
- 前端服务：http://localhost:5175

---

**测试完成时间**：2026-04-21
**测试环境**：本地开发环境