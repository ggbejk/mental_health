-- =====================================================
-- 高校学生心理危机预警与自助服务平台
-- MySQL 数据导出脚本
-- =====================================================

-- 设置导出参数
SET @export_date = DATE_FORMAT(NOW(), '%Y%m%d_%H%i%s');
SET @export_dir = '/path/to/export/';  -- 修改为实际导出目录

-- =====================================================
-- 1. 导出用户信息表 (sys_user)
-- =====================================================
SELECT
    id,
    username,
    '***' AS password,  -- 密码脱敏
    real_name,
    role,
    college,
    major,
    grade,
    class_name,
    gender,
    phone,
    status,
    create_time,
    parent_name,
    parent_phone,
    parent_relation
FROM sys_user
INTO OUTFILE CONCAT(@export_dir, 'sys_user_', @export_date, '.csv')
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';

-- =====================================================
-- 2. 导出情绪日记数据 (emotion_diary)
-- =====================================================
SELECT
    id,
    student_id,
    mood_score,
    mood_label,
    diary_text,
    trigger_event,
    create_time
FROM emotion_diary
WHERE create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)  -- 导出最近30天数据
INTO OUTFILE CONCAT(@export_dir, 'emotion_diary_', @export_date, '.csv')
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';

-- =====================================================
-- 3. 导出AI聊天记录 (ai_chat_record)
-- =====================================================
SELECT
    id,
    student_id,
    content,
    emotion_tag,
    source_type,
    create_time
FROM ai_chat_record
WHERE create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)
INTO OUTFILE CONCAT(@export_dir, 'ai_chat_record_', @export_date, '.csv')
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';

-- =====================================================
-- 4. 导出匿名倾诉记录 (confession_record)
-- =====================================================
SELECT
    id,
    student_id,
    content,
    emotion_tag,
    is_anonymous,
    create_time,
    voice_path
FROM confession_record
WHERE create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)
INTO OUTFILE CONCAT(@export_dir, 'confession_record_', @export_date, '.csv')
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';

-- =====================================================
-- 5. 导出预警记录 (warning_record) - 全量
-- =====================================================
SELECT
    id,
    student_id,
    source_type,
    risk_level,
    risk_score,
    risk_features,
    status,
    counselor_id,
    create_time,
    source_ref_id,
    warning_date,
    close_reason,
    handler_id,
    process_content,
    update_time
FROM warning_record
INTO OUTFILE CONCAT(@export_dir, 'warning_record_', @export_date, '.csv')
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';

-- =====================================================
-- 6. 导出干预记录 (intervention_record)
-- =====================================================
SELECT
    id,
    warning_id,
    student_id,
    counselor_id,
    content,
    action_type,
    follow_up_time,
    create_time
FROM intervention_record
INTO OUTFILE CONCAT(@export_dir, 'intervention_record_', @export_date, '.csv')
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';

-- =====================================================
-- 7. 导出测评结果 (survey_result)
-- =====================================================
SELECT
    id,
    task_id,
    student_id,
    total_score,
    risk_level,
    report_text,
    create_time
FROM survey_result
WHERE create_time >= DATE_SUB(NOW(), INTERVAL 90 DAY)  -- 导出最近90天数据
INTO OUTFILE CONCAT(@export_dir, 'survey_result_', @export_date, '.csv')
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';

-- =====================================================
-- 8. 导出测评任务 (survey_task)
-- =====================================================
SELECT
    id,
    title,
    scale_id,
    start_time,
    end_time,
    target_range,
    status,
    create_by,
    create_time
FROM survey_task
INTO OUTFILE CONCAT(@export_dir, 'survey_task_', @export_date, '.csv')
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';

-- =====================================================
-- 9. 导出家长联系记录 (parent_contact_record)
-- =====================================================
SELECT
    id,
    student_id,
    counselor_id,
    contact_content,
    contact_method,
    contact_purpose,
    contact_result,
    contact_time,
    create_time,
    follow_up_plan
FROM parent_contact_record
WHERE create_time >= DATE_SUB(NOW(), INTERVAL 90 DAY)
INTO OUTFILE CONCAT(@export_dir, 'parent_contact_record_', @export_date, '.csv')
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';

-- =====================================================
-- 10. 导出数据统计视图
-- =====================================================

-- 10.1 学生情绪统计
SELECT
    student_id,
    COUNT(*) as diary_count,
    AVG(mood_score) as avg_mood_score,
    MIN(mood_score) as min_mood_score,
    MAX(mood_score) as max_mood_score,
    MAX(create_time) as last_diary_time
FROM emotion_diary
WHERE create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY student_id
INTO OUTFILE CONCAT(@export_dir, 'student_emotion_stats_', @export_date, '.csv')
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';

-- 10.2 预警统计
SELECT
    DATE(create_time) as warning_date,
    risk_level,
    status,
    COUNT(*) as warning_count,
    COUNT(DISTINCT student_id) as student_count
FROM warning_record
WHERE create_time >= DATE_SUB(NOW(), INTERVAL 90 DAY)
GROUP BY DATE(create_time), risk_level, status
INTO OUTFILE CONCAT(@export_dir, 'warning_stats_', @export_date, '.csv')
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';

-- 10.3 辅导员工作量统计
SELECT
    counselor_id,
    COUNT(DISTINCT warning_id) as handled_warning_count,
    COUNT(DISTINCT student_id) as intervened_student_count,
    MAX(create_time) as last_intervention_time
FROM intervention_record
WHERE create_time >= DATE_SUB(NOW(), INTERVAL 90 DAY)
GROUP BY counselor_id
INTO OUTFILE CONCAT(@export_dir, 'counselor_work_stats_', @export_date, '.csv')
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';

-- =====================================================
-- 使用说明：
-- 1. 修改 @export_dir 为实际导出目录
-- 2. 确保MySQL有写入该目录的权限
-- 3. 可以使用mysqldump命令替代INTO OUTFILE
-- =====================================================
