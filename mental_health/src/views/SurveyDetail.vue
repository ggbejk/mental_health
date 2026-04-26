﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const taskId = route.params.taskId
const loading = ref(false)
const task = ref(null)

const getTaskDetail = async () => {
  loading.value = true
  try {
    const res = await axios.get(`/api/survey/task/${taskId}`)
    task.value = res.data
  } catch (error) {
    console.error('获取任务详情失败', error)
    alert('获取任务详情失败')
  } finally {
    loading.value = false
  }
}

const formatTime = (time) => {
  if (!time) return '未设置'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit'
  })
}

onMounted(() => {
  getTaskDetail()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/psychologist/surveys')">返回列表</button>
        <h1 class="title">普查任务详情</h1>
      </div>

      <div v-if="loading" class="empty">加载中...</div>

      <div v-else-if="!task" class="empty">
        任务不存在
      </div>

      <div v-else class="card">
        <div class="info-section">
          <h2>{{ task.title }}</h2>
          <div class="info-grid">
            <div class="info-item">
              <strong>量表名称：</strong>{{ task.scaleName }}
            </div>
            <div class="info-item">
              <strong>目标范围：</strong>{{ task.targetRange || '全校' }}
            </div>
            <div class="info-item">
              <strong>开始时间：</strong>{{ formatTime(task.startTime) }}
            </div>
            <div class="info-item">
              <strong>结束时间：</strong>{{ formatTime(task.endTime) }}
            </div>
            <div class="info-item">
              <strong>参与人数：</strong>{{ task.completedCount || 0 }} / {{ task.totalCount || 0 }}
            </div>
            <div class="info-item">
              <strong>覆盖率：</strong>{{ task.totalCount > 0 ? Math.round((task.completedCount / task.totalCount) * 100) : 0 }}%
            </div>
            <div class="info-item">
              <strong>创建时间：</strong>{{ formatTime(task.createTime) }}
            </div>
          </div>
        </div>

        <div class="statistics-section">
          <h3>统计数据</h3>
          <div class="stat-grid">
            <div class="stat-item">
              <div class="stat-value">{{ task.totalCount || 0 }}</div>
              <div class="stat-label">目标人数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ task.completedCount || 0 }}</div>
              <div class="stat-label">已完成人数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ task.totalCount > 0 ? Math.round((task.completedCount / task.totalCount) * 100) : 0 }}%</div>
              <div class="stat-label">完成率</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  background: transparent;
}

.container {
  max-width: 900px;
  margin: 0 auto;
}

.top-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.back-btn {
  background: #f2eee9;
  color: #666;
  border: none;
  border-radius: 8px;
  padding: 10px 16px;
  cursor: pointer;
}

.title {
  margin: 0;
  color: #2f5d50;
  font-size: 24px;
}

.card {
  background: white;
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.06);
}

.info-section h2 {
  margin: 0 0 20px 0;
  color: #2f5d50;
  font-size: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 30px;
}

.info-item {
  color: #555;
  line-height: 1.7;
}

.info-item strong {
  color: #333;
}

.statistics-section {
  border-top: 1px solid #e8f1ec;
  padding-top: 20px;
}

.statistics-section h3 {
  margin: 0 0 20px 0;
  color: #2f5d50;
  font-size: 16px;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat-item {
  background: #f8fcfa;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #2f8f62;
  margin-bottom: 8px;
}

.stat-label {
  color: #777;
  font-size: 14px;
}

.empty {
  text-align: center;
  color: #888;
  padding: 60px 0;
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .stat-grid {
    grid-template-columns: 1fr;
  }
}
</style>
