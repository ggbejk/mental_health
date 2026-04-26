# 高校学生心理危机预警与自助服务平台 - 数据表结构文档

## 数据库信息
- **数据库名**：mental_health
- **数据库类型**：MySQL 8.0
- **字符集**：UTF-8

---

## 1. 用户信息表 (sys_user)

存储系统所有用户信息，包括学生、辅导员、管理员。

| 字段名 | 类型 | Nullable | Key | 默认值 | 说明 |
|--------|------|----------|-----|--------|------|
| id | bigint | NO | PRI | NULL | 主键ID |
| username | varchar(255) | YES | | NULL | 用户名/学号 |
| password | varchar(255) | YES | | NULL | 密码 |
| real_name | varchar(255) | YES | | NULL | 真实姓名 |
| role | varchar(255) | YES | | NULL | 角色(STUDENT/COUNSELOR/ADMIN) |
| college | varchar(255) | YES | | NULL | 学院 |
| major | varchar(255) | YES | | NULL | 专业 |
| grade | varchar(255) | YES | | NULL | 年级 |
| class_name | varchar(255) | YES | | NULL | 班级 |
| gender | varchar(255) | YES | | NULL | 性别 |
| phone | varchar(255) | YES | | NULL | 手机号 |
| status | int | YES | | 1 | 状态(1:正常,0:禁用) |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| parent_name | varchar(255) | YES | | NULL | 家长姓名 |
| parent_phone | varchar(255) | YES | | NULL | 家长电话 |
| parent_relation | varchar(255) | YES | | NULL | 家长关系 |

---

## 2. 情绪日记表 (emotion_diary)

记录学生的情绪日记数据。

| 字段名 | 类型 | Nullable | Key | 默认值 | 说明 |
|--------|------|----------|-----|--------|------|
| id | bigint | NO | PRI | NULL | 主键ID |
| student_id | bigint | NO | | NULL | 学生ID |
| mood_score | int | NO | | NULL | 情绪评分(1-5) |
| mood_label | varchar(255) | YES | | NULL | 情绪标签(平静/焦虑/低落等) |
| diary_text | varchar(255) | YES | | NULL | 日记内容 |
| trigger_event | varchar(255) | YES | | NULL | 触发事件 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |

---

## 3. AI聊天记录表 (ai_chat_record)

记录学生与AI聊天的内容。

| 字段名 | 类型 | Nullable | Key | 默认值 | 说明 |
|--------|------|----------|-----|--------|------|
| id | bigint | NO | PRI | NULL | 主键ID |
| student_id | bigint | YES | | NULL | 学生ID |
| content | varchar(255) | YES | | NULL | 聊天内容 |
| create_time | datetime(6) | YES | | NULL | 创建时间 |
| emotion_tag | varchar(255) | YES | | NULL | 情绪标签 |
| source_type | varchar(255) | YES | | NULL | 来源类型 |

---

## 4. 匿名倾诉表 (confession_record)

记录学生的匿名倾诉内容。

| 字段名 | 类型 | Nullable | Key | 默认值 | 说明 |
|--------|------|----------|-----|--------|------|
| id | bigint | NO | PRI | NULL | 主键ID |
| student_id | bigint | YES | | NULL | 学生ID |
| content | varchar(255) | YES | | NULL | 倾诉内容 |
| emotion_tag | varchar(255) | YES | | NULL | 情绪标签 |
| is_anonymous | int | YES | | NULL | 是否匿名(1:是,0:否) |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| voice_path | varchar(255) | YES | | NULL | 语音路径 |

---

## 5. 预警记录表 (warning_record)

记录学生的预警信息。

| 字段名 | 类型 | Nullable | Key | 默认值 | 说明 |
|--------|------|----------|-----|--------|------|
| id | bigint | NO | PRI | NULL | 主键ID |
| student_id | bigint | NO | | NULL | 学生ID |
| source_type | varchar(255) | YES | | NULL | 来源类型 |
| risk_level | varchar(255) | YES | | NULL | 风险等级(LOW/MEDIUM/HIGH) |
| risk_score | decimal(38,2) | YES | | NULL | 风险评分 |
| risk_features | varchar(255) | YES | | NULL | 风险特征 |
| status | varchar(255) | YES | | NULL | 状态(UNCLAIMED/CLAIMED/PROCESSING/CLOSED) |
| counselor_id | bigint | YES | | NULL | 辅导员ID |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| source_ref_id | bigint | YES | | NULL | 来源关联ID |
| warning_date | datetime(6) | YES | | NULL | 预警时间 |
| close_reason | varchar(255) | YES | | NULL | 关闭原因 |
| handler_id | bigint | YES | | NULL | 处理人ID |
| process_content | varchar(255) | YES | | NULL | 处理内容 |
| update_time | datetime(6) | YES | | NULL | 更新时间 |

---

## 6. 干预记录表 (intervention_record)

记录辅导员对学生的干预记录。

| 字段名 | 类型 | Nullable | Key | 默认值 | 说明 |
|--------|------|----------|-----|--------|------|
| id | bigint | NO | PRI | NULL | 主键ID |
| warning_id | bigint | NO | | NULL | 预警ID |
| student_id | bigint | NO | | NULL | 学生ID |
| counselor_id | bigint | YES | | NULL | 辅导员ID |
| content | varchar(255) | YES | | NULL | 干预内容 |
| action_type | varchar(255) | YES | | NULL | 干预类型 |
| follow_up_time | datetime | YES | | NULL | 跟进时间 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |

---

## 7. 测评结果表 (survey_result)

记录学生的心理测评结果。

| 字段名 | 类型 | Nullable | Key | 默认值 | 说明 |
|--------|------|----------|-----|--------|------|
| id | bigint | NO | PRI | NULL | 主键ID |
| task_id | bigint | NO | | NULL | 任务ID |
| student_id | bigint | NO | | NULL | 学生ID |
| total_score | decimal(38,2) | YES | | NULL | 总评分 |
| risk_level | varchar(255) | YES | | NULL | 风险等级 |
| report_text | varchar(255) | YES | | NULL | 报告文本 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |

---

## 8. 测评任务表 (survey_task)

管理心理测评任务。

| 字段名 | 类型 | Nullable | Key | 默认值 | 说明 |
|--------|------|----------|-----|--------|------|
| id | bigint | NO | PRI | NULL | 主键ID |
| title | varchar(255) | YES | | NULL | 任务标题 |
| scale_id | bigint | NO | | NULL | 量表ID |
| start_time | datetime | YES | | NULL | 开始时间 |
| end_time | datetime | YES | | NULL | 结束时间 |
| target_range | varchar(255) | YES | | NULL | 目标范围 |
| status | int | YES | | 1 | 状态 |
| create_by | bigint | YES | | NULL | 创建人ID |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |

---

## 9. 量表表 (scale)

存储心理测评量表信息。

| 字段名 | 类型 | Nullable | Key | 默认值 | 说明 |
|--------|------|----------|-----|--------|------|
| id | bigint | NO | PRI | NULL | 主键ID |
| name | varchar(255) | NO | | NULL | 量表名称 |
| description | text | YES | | NULL | 量表描述 |
| status | varchar(255) | NO | | NULL | 状态 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime(6) | YES | | NULL | 更新时间 |

---

## 10. 量表题目表 (scale_question)

存储量表的题目信息。

| 字段名 | 类型 | Nullable | Key | 默认值 | 说明 |
|--------|------|----------|-----|--------|------|
| id | bigint | NO | PRI | NULL | 主键ID |
| scale_id | bigint | NO | | NULL | 量表ID |
| title | varchar(255) | YES | | NULL | 题目内容 |
| dimension | varchar(255) | YES | | NULL | 维度 |
| question_order | int | YES | | NULL | 题目顺序 |
| is_reverse | int | YES | | 0 | 是否反向计分 |
| question_type | varchar(255) | YES | | NULL | 题目类型 |

---

## 11. 家长联系记录表 (parent_contact_record)

记录辅导员与家长联系的情况。

| 字段名 | 类型 | Nullable | Key | 默认值 | 说明 |
|--------|------|----------|-----|--------|------|
| id | bigint | NO | PRI | NULL | 主键ID |
| student_id | bigint | YES | | NULL | 学生ID |
| counselor_id | bigint | YES | | NULL | 辅导员ID |
| contact_content | varchar(255) | YES | | NULL | 联系内容 |
| contact_method | varchar(255) | YES | | NULL | 联系方式 |
| contact_purpose | varchar(255) | YES | | NULL | 联系目的 |
| contact_result | varchar(255) | YES | | NULL | 联系结果 |
| contact_time | datetime(6) | YES | | NULL | 联系时间 |
| create_time | datetime(6) | YES | | NULL | 创建时间 |
| follow_up_plan | varchar(255) | YES | | NULL | 后续计划 |

---

## 12. 数据统计口径

### 12.1 风险等级划分
- **LOW (低风险)**：单项指标轻微异常
- **MEDIUM (中风险)**：单项指标明显异常或多项指标轻微异常
- **HIGH (高风险)**：严重异常或危机信号

### 12.2 预警状态流转
1. **UNCLAIMED (未认领)**：系统自动生成预警，等待辅导员认领
2. **CLAIMED (已认领)**：辅导员已认领预警，待处理
3. **PROCESSING (处理中)**：辅导员正在处理预警
4. **CLOSED (已结案)**：预警处理完成，已结案

### 12.3 情绪评分标准
- **5分**：非常愉悦
- **4分**：愉悦/平静
- **3分**：一般
- **2分**：焦虑/低落
- **1分**：非常焦虑/低落

---

## 13. 数据导出注意事项

1. **时间格式**：所有时间字段统一使用 `yyyy-MM-dd HH:mm:ss` 格式
2. **字符编码**：导出时使用UTF-8编码，确保中文正确
3. **敏感数据**：`password`字段导出时需要加密或脱敏
4. **数据量**：
   - emotion_diary: 建议按日期分区导出
   - ai_chat_record: 建议按月份分区导出
   - warning_record: 建议全量导出
5. **增量同步**：建议使用 `create_time` 或 `update_time` 字段进行增量同步

---

本文档由系统自动生成，最后更新时间为 2026-04-20。