#!/bin/bash
# =====================================================
# 高校学生心理危机预警与自助服务平台
# 数据导出脚本 (使用 mysqldump)
# =====================================================

# 数据库配置
DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="mental_health"
DB_USER="root"
DB_PASS="123456"

# 导出目录
EXPORT_DIR="./export_$(date +%Y%m%d_%H%M%S)"
mkdir -p ${EXPORT_DIR}

# 日志文件
LOG_FILE="${EXPORT_DIR}/export.log"

# =====================================================
# 辅助函数
# =====================================================
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a ${LOG_FILE}
}

export_table() {
    local table_name=$1
    local condition=$2
    local output_file="${EXPORT_DIR}/${table_name}.sql"

    log "导出表: ${table_name}"

    if [ -z "${condition}" ]; then
        mysqldump -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p${DB_PASS} \
            --no-create-db --single-transaction --quick --lock-tables=false \
            --complete-insert --extended-insert=false \
            --hex-blob --default-character-set=utf8mb4 \
            ${DB_NAME} ${table_name} > ${output_file} 2>> ${LOG_FILE}
    else
        mysqldump -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p${DB_PASS} \
            --no-create-db --single-transaction --quick --lock-tables=false \
            --complete-insert --extended-insert=false \
            --hex-blob --default-character-set=utf8mb4 \
            --where="${condition}" \
            ${DB_NAME} ${table_name} > ${output_file} 2>> ${LOG_FILE}
    fi

    if [ $? -eq 0 ]; then
        log "✓ ${table_name} 导出成功"
    else
        log "✗ ${table_name} 导出失败"
    fi
}

export_query() {
    local query=$1
    local output_file=$2

    log "执行查询并导出: ${output_file}"

    mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p${DB_PASS} \
        --default-character-set=utf8mb4 \
        -e "${query}" > ${output_file} 2>> ${LOG_FILE}

    if [ $? -eq 0 ]; then
        log "✓ ${output_file} 导出成功"
    else
        log "✗ ${output_file} 导出失败"
    fi
}

# =====================================================
# 导出配置
# =====================================================

# 增量导出的时间条件
RECENT_DAYS=30
TIME_CONDITION="create_time >= DATE_SUB(NOW(), INTERVAL ${RECENT_DAYS} DAY)"

log "=========================================="
log "开始数据导出"
log "导出目录: ${EXPORT_DIR}"
log "增量天数: ${RECENT_DAYS}天"
log "=========================================="

# =====================================================
# 1. 导出用户信息表 (sys_user) - 密码脱敏
# =====================================================
export_query "SELECT id, username, '***' AS password, real_name, role, college, major, grade, class_name, gender, phone, status, create_time, parent_name, parent_phone, parent_relation FROM sys_user" \
    "${EXPORT_DIR}/sys_user.csv"

# =====================================================
# 2. 导出情绪日记数据 (emotion_diary)
# =====================================================
export_table "emotion_diary" "${TIME_CONDITION}"

# =====================================================
# 3. 导出AI聊天记录 (ai_chat_record)
# =====================================================
export_table "ai_chat_record" "${TIME_CONDITION}"

# =====================================================
# 4. 导出匿名倾诉记录 (confession_record)
# =====================================================
export_table "confession_record" "${TIME_CONDITION}"

# =====================================================
# 5. 导出预警记录 (warning_record) - 全量
# =====================================================
export_table "warning_record"

# =====================================================
# 6. 导出干预记录 (intervention_record)
# =====================================================
export_table "intervention_record"

# =====================================================
# 7. 导出测评结果 (survey_result)
# =====================================================
export_table "survey_result" "create_time >= DATE_SUB(NOW(), INTERVAL 90 DAY)"

# =====================================================
# 8. 导出测评任务 (survey_task)
# =====================================================
export_table "survey_task"

# =====================================================
# 9. 导出量表 (scale)
# =====================================================
export_table "scale"

# =====================================================
# 10. 导出量表题目 (scale_question)
# =====================================================
export_table "scale_question"

# =====================================================
# 11. 导出家长联系记录 (parent_contact_record)
# =====================================================
export_table "parent_contact_record" "${TIME_CONDITION}"

# =====================================================
# 12. 导出统计视图
# =====================================================

# 12.1 学生情绪统计
export_query "SELECT
    student_id,
    COUNT(*) as diary_count,
    AVG(mood_score) as avg_mood_score,
    MIN(mood_score) as min_mood_score,
    MAX(mood_score) as max_mood_score,
    MAX(create_time) as last_diary_time
FROM emotion_diary
WHERE ${TIME_CONDITION}
GROUP BY student_id" \
"${EXPORT_DIR}/student_emotion_stats.csv"

# 12.2 预警统计
export_query "SELECT
    DATE(create_time) as warning_date,
    risk_level,
    status,
    COUNT(*) as warning_count,
    COUNT(DISTINCT student_id) as student_count
FROM warning_record
WHERE create_time >= DATE_SUB(NOW(), INTERVAL 90 DAY)
GROUP BY DATE(create_time), risk_level, status" \
"${EXPORT_DIR}/warning_stats.csv"

# 12.3 辅导员工作量统计
export_query "SELECT
    counselor_id,
    COUNT(DISTINCT warning_id) as handled_warning_count,
    COUNT(DISTINCT student_id) as intervened_student_count,
    MAX(create_time) as last_intervention_time
FROM intervention_record
WHERE create_time >= DATE_SUB(NOW(), INTERVAL 90 DAY)
GROUP BY counselor_id" \
"${EXPORT_DIR}/counselor_work_stats.csv"

# =====================================================
# 生成导出清单
# =====================================================
log "=========================================="
log "导出完成"
log "=========================================="
log "导出文件列表:"
ls -lh ${EXPORT_DIR} | tee -a ${LOG_FILE}

# 计算压缩包大小
cd $(dirname ${EXPORT_DIR})
tar -czf ${EXPORT_DIR}.tar.gz $(basename ${EXPORT_DIR})
log "压缩包: ${EXPORT_DIR}.tar.gz ($(du -h ${EXPORT_DIR}.tar.gz | cut -f1))"

log "=========================================="
log "导出完成！"
log "=========================================="
