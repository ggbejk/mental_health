﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted,computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { getUserInfo } from '../utils/auth'

const userInfo =  getUserInfo()
const studentId = userInfo?.userId || userInfo?.id
const router = useRouter()
const tasks = ref([])
const loading = ref(false)
const todoTasks = computed(() => {
  return tasks.value.filter(task => !task.hasSubmitted)
})

const doneTasks = computed(() => {
  return tasks.value.filter(task => task.hasSubmitted)
})

const formatTime = (time) => {
  if (!time) return '未设置'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getTaskStatus = (task) => {
  const now = new Date()
  const startTime = new Date(task.startTime)
  const endTime = new Date(task.endTime)
  
  if (now < startTime) {
    return { text: '未开始', className: 'pending', disabled: true }
  } else if (now > endTime) {
    return { text: '已结束', className: 'expired', disabled: true }
  } else {
    return { text: '进行中', className: 'ongoing', disabled: false }
  }
}

const goToReport = (task) => {
  if (!task.resultId) {
    alert('该任务暂无报告')
    return
  }

  router.push({
    path: '/student/report',
    query: { resultId: task.resultId }
  })
}

const getTasks = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/student/tasks',{
      params:{ studentId }
    })
    tasks.value = res.data || []
  } catch (error) {
    console.error('获取测评任务失败', error)
    alert('获取测评任务失败，请检查后端是否启动')
  } finally {
    loading.value = false
  }
}

const goToTask = (task) => {
  router.push(`/student/task/${task.id}`)
}

onMounted(() => {
  getTasks()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <h1 class="title">学生心理测评任务</h1>
     <div class="entry-row">
  <button class="entry-btn" @click="router.push('/student/emotion/write')">写情绪日记</button>
  <button class="entry-btn light" @click="router.push('/student/emotion/trend')">查看情绪曲线</button>
  <button class="entry-btn purple" @click="router.push('/student/confession/write')">匿名倾诉</button>
</div>
      <p class="sub-title">请选择需要完成的心理测评任务</p>

      <div v-if="loading" class="empty">加载中...</div>

      <div v-else-if="tasks.length === 0" class="empty">
        当前暂无测评任务
      </div>

     <div v-else>
  <div class="section-block">
    <div class="section-title">待完成任务</div>

    <div v-if="todoTasks.length === 0" class="empty-small">
      当前没有待完成任务
    </div>

    <div v-else class="task-list">
      <div class="task-card" v-for="task in todoTasks" :key="task.id">
        <div class="task-header">
          <div>
            <h2>{{ task.title }}</h2>
            <p class="task-desc">请在规定时间内完成本次心理测评任务。</p>
          </div>

          <span class="status" :class="getTaskStatus(task).className">
            {{ getTaskStatus(task).text }}
          </span>
        </div>

        <div class="task-info">
          <div class="info-row">
            <span class="info-label">任务ID</span>
            <span class="info-value">{{ task.id }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">目标范围</span>
            <span class="info-value">{{ task.targetRange || '未设置' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">开始时间</span>
            <span class="info-value">{{ formatTime(task.startTime) }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">结束时间</span>
            <span class="info-value">{{ formatTime(task.endTime) }}</span>
          </div>
        </div>

        <button
          class="start-btn"
          :class="{ disabled: getTaskStatus(task).disabled }"
          :disabled="getTaskStatus(task).disabled"
          @click="goToTask(task)"
        >
          {{
            getTaskStatus(task).text === '未开始'
              ? '暂未开放'
              : getTaskStatus(task).text === '已结束'
                ? '任务已结束'
                : '开始测评'
          }}
        </button>
      </div>
    </div>
  </div>

  <div class="section-block">
    <div class="section-title">已完成任务 / 历史报告</div>

    <div v-if="doneTasks.length === 0" class="empty-small">
      暂无已完成任务
    </div>

    <div v-else class="task-list">
      <div class="task-card done-card" v-for="task in doneTasks" :key="task.id">
        <div class="task-header">
          <div>
            <h2>{{ task.title }}</h2>
            <p class="task-desc">你已完成该测评，可查看历史报告。</p>
          </div>

          <span class="status finished">已完成</span>
        </div>

        <div class="task-info">
          <div class="info-row">
            <span class="info-label">任务ID</span>
            <span class="info-value">{{ task.id }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">目标范围</span>
            <span class="info-value">{{ task.targetRange || '未设置' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">提交时间</span>
            <span class="info-value">{{ formatTime(task.submitTime) }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">报告编号</span>
            <span class="info-value">{{ task.resultId || '暂无' }}</span>
          </div>
        </div>

        <button class="report-btn" @click="goToReport(task)">
          查看报告
        </button>
      </div>
    </div>
  </div>
</div>
    </div>
  </div>
</template>

<style scoped>
.section-block {
  margin-bottom: 28px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #2f5d50;
  margin-bottom: 16px;
}

.empty-small {
  text-align: center;
  color: #999;
  padding: 28px 0;
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e8f1ec;
}

.done-card {
  border: 1px solid #dcebe3;
}

.report-btn {
  width: 100%;
  margin-top: 18px;
  background: #7eaedb;
  color: white;
  border: none;
  border-radius: 12px;
  padding: 12px 0;
  font-size: 15px;
  cursor: pointer;
}

.report-btn:hover {
  opacity: 0.92;
}
.page {
  min-height: 100vh;
  background: linear-gradient(to bottom, #eef9f3, #fdfaf7);
  padding: 40px 20px;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
}

.title {
  text-align: center;
  font-size: 34px;
  color: #2f5d50;
  margin-bottom: 10px;
}

.sub-title {
  text-align: center;
  color: #7a7a7a;
  margin-bottom: 30px;
}

.task-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.task-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.06);
  border: 1px solid #e8f3ed;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.task-header h2 {
  font-size: 20px;
  margin: 0;
  color: #2f5d50;
}

.status {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
}

.status.pending {
  background: #f0f0f0;
  color: #999;
}

.status.ongoing {
  background: #dff5e8;
  color: #2f8f62;
}

.status.expired {
  background: #ffebee;
  color: #c62828;
}

.status.finished {
  background: #e3f2fd;
  color: #1976d2;
}

.task-info p {
  margin: 10px 0;
  color: #555;
  line-height: 1.6;
}

.start-btn {
  width: 100%;
  margin-top: 18px;
  background: #86c9a8;
  color: white;
  border: none;
  border-radius: 10px;
  padding: 12px 0;
  font-size: 15px;
  cursor: pointer;
}

.start-btn:hover {
  background: #70b894;
}

.empty {
  text-align: center;
  color: #888;
  padding: 60px 0;
  font-size: 18px;
}

.entry-row {
  display: flex;
  justify-content: center;
  gap: 14px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.entry-btn {
  border: none;
  border-radius: 10px;
  padding: 12px 20px;
  background: #86c9a8;
  color: white;
  cursor: pointer;
}

.entry-btn.light {
  background: #f3b38a;
}

.entry-btn.purple {
  background: #9b8ad6;
}
</style>
