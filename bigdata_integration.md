# 高校学生心理危机预警与自助服务平台 - 大数据对接文档

## 1. 项目概述

本平台是一个集心理测评、行为预警、自助干预、危机转介于一体的智能化心理健康服务管理平台。平台通过心理普查、校园行为日志、社交情绪挖掘等多维数据，运用异常检测算法自动识别潜在心理风险学生，生成分级预警名单并推送至辅导员及心理中心。

## 2. 现有系统架构

### 2.1 前端架构（Vue 3）
- **学生端**：心理测评、AI聊天、情绪日记、匿名倾诉、正念训练
- **辅导员端**：预警管理、学生管理、干预记录、数据大屏
- **咨询师端**：量表管理、测评任务管理、预警评估
- **管理员端**：运营日志、系统管理、数据大屏
- **数据大屏**：心理健康态势监控（分教师端和管理员端）

### 2.2 后端架构（Spring Boot）
- **控制器**：处理前端请求
- **实体类**：对应数据库表结构
- **仓库接口**：数据访问层
- **DTO**：数据传输对象
- **服务层**：业务逻辑处理

## 3. 数据结构定义

### 3.1 核心数据实体

#### 3.1.1 心理测评数据

**SurveyResult（测评结果）**
| 字段名 | 类型 | 描述 |
|-------|------|------|
| id | Long | 主键ID |
| taskId | Long | 任务ID |
| studentId | Long | 学生ID |
| totalScore | BigDecimal | 总分数 |
| riskLevel | String | 风险等级 |
| reportText | String | 报告文本 |
| createTime | LocalDateTime | 创建时间 |

**SurveyTask（测评任务）**
| 字段名 | 类型 | 描述 |
|-------|------|------|
| id | Long | 主键ID |
| title | String | 任务标题 |
| scaleId | Long | 量表ID |
| startTime | LocalDateTime | 开始时间 |
| endTime | LocalDateTime | 结束时间 |
| targetRange | String | 目标范围 |
| status | Integer | 状态 |
| createBy | Long | 创建人ID |
| createTime | LocalDateTime | 创建时间 |

**Scale（量表）**
| 字段名 | 类型 | 描述 |
|-------|------|------|
| id | Long | 主键ID |
| name | String | 量表名称 |
| description | String | 量表描述 |
| status | String | 状态 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

**ScaleQuestion（量表题目）**
| 字段名 | 类型 | 描述 |
|-------|------|------|
| id | Long | 主键ID |
| scaleId | Long | 量表ID |
| title | String | 题目内容 |
| questionType | String | 题目类型 |
| questionOrder | Integer | 题目顺序 |
| dimension | String | 维度 |
| isReverse | Integer | 是否反向计分 |

#### 3.1.2 情绪数据

**EmotionDiary（情绪日记）**
| 字段名 | 类型 | 描述 |
|-------|------|------|
| id | Long | 主键ID |
| studentId | Long | 学生ID |
| content | String | 日记内容 |
| emotionType | String | 情绪类型 |
| emotionScore | Integer | 情绪评分 |
| createTime | LocalDateTime | 创建时间 |

**AiChatRecord（AI聊天记录）**
| 字段名 | 类型 | 描述 |
|-------|------|------|
| id | Long | 主键ID |
| studentId | Long | 学生ID |
| userMessage | String | 用户消息 |
| aiResponse | String | AI回复 |
| emotionType | String | 情绪类型 |
| emotionScore | Double | 情绪评分 |
| riskLevel | String | 风险等级 |
| createTime | LocalDateTime | 创建时间 |

**ConfessionRecord（匿名倾诉）**
| 字段名 | 类型 | 描述 |
|-------|------|------|
| id | Long | 主键ID |
| content | String | 倾诉内容 |
| emotionType | String | 情绪类型 |
| emotionScore | Double | 情绪评分 |
| riskLevel | String | 风险等级 |
| handlerId | Long | 处理人ID |
| processContent | String | 处理内容 |
| status | String | 状态 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

#### 3.1.3 预警数据

**WarningRecord（预警记录）**
| 字段名 | 类型 | 描述 |
|-------|------|------|
| id | Long | 主键ID |
| studentId | Long | 学生ID |
| riskLevel | String | 风险等级 |
| riskFeatures | String | 风险特征 |
| status | String | 状态 |
| handlerId | Long | 处理人ID |
| processContent | String | 处理内容 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

#### 3.1.4 用户信息

**SysUser（用户信息）**
| 字段名 | 类型 | 描述 |
|-------|------|------|
| id | Long | 主键ID |
| username | String | 用户名/学号 |
| password | String | 密码 |
| realName | String | 真实姓名 |
| role | String | 角色 |
| college | String | 学院 |
| major | String | 专业 |
| grade | String | 年级 |
| className | String | 班级 |
| gender | String | 性别 |
| phone | String | 手机号 |
| status | Integer | 状态 |
| createTime | LocalDateTime | 创建时间 |
| parentName | String | 家长姓名 |
| parentPhone | String | 家长电话 |
| parentRelation | String | 家长关系 |

#### 3.1.5 干预记录

**InterventionRecord（干预记录）**
| 字段名 | 类型 | 描述 |
|-------|------|------|
| id | Long | 主键ID |
| studentId | Long | 学生ID |
| counselorId | Long | 辅导员ID |
| interventionType | String | 干预类型 |
| interventionContent | String | 干预内容 |
| interventionTime | LocalDateTime | 干预时间 |
| effectEvaluation | String | 效果评估 |
| createTime | LocalDateTime | 创建时间 |

#### 3.1.6 家长联系记录

**ParentContactRecord（家长联系记录）**
| 字段名 | 类型 | 描述 |
|-------|------|------|
| id | Long | 主键ID |
| studentId | Long | 学生ID |
| counselorId | Long | 辅导员ID |
| contactTime | LocalDateTime | 联系时间 |
| contactMethod | String | 联系方式 |
| contactPurpose | String | 联系目的 |
| contactContent | String | 联系内容 |
| contactResult | String | 联系结果 |
| followUpPlan | String | 后续计划 |
| createTime | LocalDateTime | 创建时间 |

## 4. API接口需求

### 4.1 数据采集接口

#### 4.1.1 心理测评数据
- **接口**：`POST /api/student/submit-survey`
- **功能**：提交心理测评结果
- **参数**：
  ```json
  {
    "taskId": 1,
    "answers": [
      {"questionId": 1, "score": 3},
      {"questionId": 2, "score": 4}
    ]
  }
  ```

#### 4.1.2 情绪数据
- **接口**：`POST /api/student/emotion-diary`
- **功能**：保存情绪日记
- **参数**：
  ```json
  {
    "content": "今天心情很好",
    "emotionType": "HAPPY",
    "emotionScore": 8
  }
  ```

- **接口**：`POST /api/student/ai-chat`
- **功能**：发送AI聊天消息
- **参数**：
  ```json
  {
    "message": "我最近压力很大"
  }
  ```

- **接口**：`POST /api/student/confession`
- **功能**：保存匿名倾诉
- **参数**：
  ```json
  {
    "content": "我感到很孤独"
  }
  ```

### 4.2 数据查询接口

#### 4.2.1 预警信息
- **接口**：`GET /api/counselor/warnings`
- **功能**：获取辅导员预警列表
- **参数**：
  - `counselorId`：辅导员ID
- **返回**：
  ```json
  [
    {
      "id": 1,
      "studentId": 1,
      "studentName": "张三",
      "riskLevel": "HIGH",
      "riskFeatures": "情绪低落,睡眠障碍",
      "status": "UNCLAIMED",
      "createTime": "2026-04-20T10:00:00"
    }
  ]
  ```

- **接口**：`POST /api/counselor/warning/claim`
- **功能**：认领预警
- **参数**：
  ```json
  {
    "warningId": 1,
    "counselorId": 9
  }
  ```

- **接口**：`POST /api/counselor/warning/process`
- **功能**：处理预警
- **参数**：
  ```json
  {
    "warningId": 1,
    "processContent": "已与学生谈心，了解情况"
  }
  ```

- **接口**：`POST /api/counselor/warning/close`
- **功能**：结案预警
- **参数**：
  ```json
  {
    "warningId": 1,
    "processContent": "学生状态已好转，结案"
  }
  ```

#### 4.2.2 数据大屏
- **接口**：`GET /api/dashboard/overview`
- **功能**：获取管理员数据大屏概览
- **返回**：
  ```json
  {
    "totalStudents": 1000,
    "completedCount": 800,
    "warningCount": 50,
    "interventionCount": 30,
    "warningLevelStats": [
      {"name": "低风险", "value": 20},
      {"name": "中风险", "value": 20},
      {"name": "高风险", "value": 10}
    ],
    "warningStatusStats": [
      {"name": "未认领", "value": 20},
      {"name": "处理中", "value": 15},
      {"name": "已结案", "value": 15}
    ],
    "warningTrendStats": [
      {"name": "1月", "value": 10},
      {"name": "2月", "value": 15},
      {"name": "3月", "value": 25}
    ],
    "riskTagStats": [
      {"name": "情绪低落", "value": 30},
      {"name": "睡眠障碍", "value": 25}
    ],
    "collegeWarningStats": [
      {"name": "计算机学院", "value": 20},
      {"name": "文学院", "value": 15},
      {"name": "理学院", "value": 15}
    ]
  }
  ```

- **接口**：`GET /api/dashboard/teacher/overview`
- **功能**：获取教师数据大屏概览
- **参数**：
  - `teacherId`：教师ID
  - `className`：班级名称
- **返回**：
  ```json
  {
    "totalStudents": 50,
    "completedCount": 40,
    "warningCount": 5,
    "interventionCount": 3,
    "warningLevelStats": [
      {"name": "低风险", "value": 2},
      {"name": "中风险", "value": 2},
      {"name": "高风险", "value": 1}
    ],
    "warningStatusStats": [
      {"name": "未认领", "value": 2},
      {"name": "处理中", "value": 2},
      {"name": "已结案", "value": 1}
    ],
    "warningTrendStats": [
      {"name": "1月", "value": 1},
      {"name": "2月", "value": 1},
      {"name": "3月", "value": 3}
    ],
    "riskTagStats": [
      {"name": "情绪低落", "value": 3},
      {"name": "学习压力", "value": 2}
    ]
  }
  ```

#### 4.2.3 学生信息
- **接口**：`GET /api/counselor/students`
- **功能**：获取辅导员负责的学生列表
- **参数**：
  - `counselorId`：辅导员ID
- **返回**：
  ```json
  [
    {
      "id": 1,
      "username": "stu001",
      "realName": "张三",
      "gender": "男",
      "college": "计算机学院",
      "className": "软工2班",
      "grade": "2024级"
    }
  ]
  ```

- **接口**：`GET /api/student/{studentId}`
- **功能**：获取学生详情
- **参数**：
  - `studentId`：学生ID
- **返回**：
  ```json
  {
    "id": 1,
    "username": "stu001",
    "realName": "张三",
    "gender": "男",
    "college": "计算机学院",
    "major": "软件工程",
    "grade": "2024级",
    "className": "软工2班",
    "phone": "13800138000",
    "parentName": "张父",
    "parentPhone": "13800138001",
    "parentRelation": "父亲"
  }
  ```

#### 4.2.4 干预记录
- **接口**：`GET /api/counselor/interventions`
- **功能**：获取干预记录
- **参数**：
  - `counselorId`：辅导员ID
  - `studentId`：学生ID（可选）
- **返回**：
  ```json
  [
    {
      "id": 1,
      "studentId": 1,
      "studentName": "张三",
      "interventionType": "谈心",
      "interventionContent": "与学生谈心，了解情况",
      "interventionTime": "2026-04-20T10:00:00",
      "effectEvaluation": "效果良好",
      "createTime": "2026-04-20T10:00:00"
    }
  ]
  ```

- **接口**：`POST /api/counselor/intervention/save`
- **功能**：保存干预记录
- **参数**：
  ```json
  {
    "studentId": 1,
    "interventionType": "谈心",
    "interventionContent": "与学生谈心，了解情况",
    "interventionTime": "2026-04-20T10:00:00",
    "effectEvaluation": "效果良好"
  }
  ```

## 5. 业务逻辑说明

### 5.1 预警规则

1. **风险等级划分**：
   - 低风险：单项指标轻微异常
   - 中风险：单项指标明显异常或多项指标轻微异常
   - 高风险：严重异常或危机信号

2. **预警触发因素**：
   - 心理测评高分项（如SDS、SAS量表得分超过阈值）
   - 情绪日记连续负面情绪（如连续3天情绪低落）
   - AI聊天中出现危机信号（如自杀倾向）
   - 匿名倾诉中的负面情绪强度

3. **预警处理流程**：
   - 系统自动生成预警
   - 推送至对应辅导员工作台
   - 辅导员认领并评估
   - 开展干预并记录
   - 结案或持续跟踪

### 5.2 数据大屏指标

1. **核心指标**：
   - 心理普查覆盖率 = 已完成测评人数 / 总学生数
   - 预警率 = 预警学生数 / 总学生数
   - 干预率 = 已干预学生数 / 预警学生数
   - 结案率 = 已结案预警数 / 预警学生数

2. **维度分析**：
   - 按学院分析：各学院预警率、覆盖率
   - 按年级分析：各年级预警分布
   - 按性别分析：不同性别预警情况
   - 按时间分析：预警趋势变化

3. **风险标签**：
   - 基于学生行为和测评结果生成风险标签
   - 词云图展示高频风险标签
   - 按风险标签统计预警人数

### 5.3 实时分析场景

1. **情绪实时监测**：
   - 实时分析学生情绪日记和聊天内容
   - 识别情绪异常并及时预警
   - 建立学生情绪变化趋势模型

2. **预警等级动态调整**：
   - 根据学生最新数据动态调整预警等级
   - 确保预警的及时性和准确性
   - 实现预警等级的自动升降级

3. **干预效果评估**：
   - 实时监测干预后的学生状态变化
   - 评估干预措施的有效性
   - 为后续干预提供参考依据

4. **学生画像构建**：
   - 基于多维度数据构建学生心理健康画像
   - 识别潜在风险因素
   - 提供个性化的心理健康建议

## 6. 技术对接细节

### 6.1 数据传输方式

1. **实时数据**：
   - 使用Kafka消息队列
   - 前端 → 后端API → Kafka → 大数据处理
   - 适用场景：情绪日记、AI聊天、匿名倾诉等实时数据

2. **批量数据**：
   - 定时任务将数据批量同步至大数据存储
   - 支持增量同步和全量同步
   - 适用场景：心理测评结果、预警记录等批量数据

3. **API直接调用**：
   - 数据大屏API直接调用大数据服务
   - 预警信息API调用大数据分析结果
   - 适用场景：数据统计、分析结果查询

### 6.2 数据格式

1. **JSON格式**：
   - 所有API接口使用JSON格式
   - 字段名使用驼峰命名法
   - 日期时间使用ISO 8601格式

2. **数据编码**：
   - 使用UTF-8编码
   - 确保中文字符正确传输

3. **数据压缩**：
   - 对于大批量数据传输，使用gzip压缩
   - 减少网络传输时间和带宽消耗

### 6.3 认证机制

1. **API认证**：
   - 使用JWT令牌
   - 前端在请求头中携带token
   - 后端验证token有效性

2. **数据安全**：
   - 敏感数据加密存储
   - 数据传输使用HTTPS
   - 操作日志完整留存
   - 数据访问权限控制

### 6.4 技术栈

1. **前端**：
   - Vue 3
   - Vue Router
   - Axios
   - ECharts
   - 组件化开发

2. **后端**：
   - Spring Boot
   - Spring Data JPA
   - RESTful API
   - MySQL
   - AOP面向切面编程

3. **大数据**：
   - Kafka消息队列
   - Flink/Spark Streaming实时处理
   - Hive离线分析
   - ClickHouse/MySQL存储
   - 机器学习算法

## 7. 对接流程

### 7.1 初始化阶段
1. 双方共同制定详细的对接计划和文档
2. 确定数据结构和API接口规范
3. 搭建测试环境
4. 建立沟通机制和协作流程

### 7.2 开发阶段
1. 前端/后端开发数据产生和调用接口的代码
2. 大数据部分开发数据处理和存储的代码
3. 实现Kafka消息队列的生产者和消费者
4. 开发实时数据分析和处理逻辑
5. 定期同步开发进度，解决遇到的问题

### 7.3 测试阶段
1. 进行单元测试和集成测试
2. 验证数据传输的正确性和系统的稳定性
3. 测试实时数据处理的延迟和准确性
4. 验证数据大屏的统计结果
5. 修复测试中发现的问题

### 7.4 部署阶段
1. 共同制定部署方案
2. 确保各模块在生产环境中正常运行
3. 建立监控和告警机制
4. 制定应急处理方案
5. 进行压力测试和性能优化

## 8. 交付物

### 8.1 技术文档
- 数据架构设计文档
- 数据模型设计文档
- API接口文档
- 部署和运维文档
- 大数据处理流程文档

### 8.2 代码实现
- Kafka生产者/消费者代码
- Flink/Spark Streaming处理逻辑
- Hive分析脚本
- ClickHouse/MySQL数据操作代码
- 数据大屏后端API代码
- 实时预警算法实现

### 8.3 测试和验证
- 数据处理性能测试报告
- 算法准确性验证报告
- 系统稳定性测试报告
- 大数据处理延迟测试报告

### 8.4 部署方案
- Docker Compose配置文件
- 容器镜像构建脚本
- 环境变量配置说明
- 监控和告警配置

## 9. 联系方式

- **前端/后端负责人**：[你的名字]
- **大数据负责人**：[大数据同学名字]
- **沟通渠道**：[飞书/钉钉/微信群]
- **定期会议**：[每周X，具体时间]
- **技术支持**：[邮箱/电话]

---

本对接文档将根据项目进展不断更新和完善。