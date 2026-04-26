﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { getUserInfo, clearLoginInfo } from '../utils/auth'

const router = useRouter()
const userInfo = getUserInfo()
const loading = ref(false)
const surveys = ref([])

const getSurveys = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/survey/tasks')
    surveys.value = res.data || []
  } catch (error) {
    console.error('获取普查任务失败', error)
    surveys.value = []
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

const getTaskStatus = (task) => {
  const now = new Date()
  const startTime = new Date(task.startTime)
  const endTime = new Date(task.endTime)
  if (now < startTime) return { text: '未开始', className: 'pending', disabled: true }
  else if (now > endTime) return { text: '已结束', className: 'expired', disabled: true }
  else return { text: '进行中', className: 'ongoing', disabled: false }
}

const viewSurveyDetails = (taskId) => {
  router.push(`/psychologist/survey/${taskId}`)
}

const createSurvey = () => {
  router.push('/psychologist/survey/create')
}

const logout = () => {
  clearLoginInfo()
  router.push('/login')
}

onMounted(() => {
  getSurveys()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="page-header">
        <div>
          <h1 class="title">心理普查管理</h1>
          <p class="sub-title">欢迎你，{{ userInfo?.realName || '咨询师' }}</p>
        </div>
        <div class="header-actions">
          <button class="btn primary" @click="createSurvey">创建普查任务</button>
          <button class="logout-btn" @click="logout">退出登录</button>
        </div>
      </div>
      <p class="sub-title">统筹管理全校心理普查任务</p>

      <div v-if="loading" class="empty">加载中...</div>

      <div v-else-if="surveys.length === 0" class="empty">
        <p>暂无普查任务</p>
        <button class="btn primary" @click="createSurvey">创建第一个普查任务</button>
      </div>

      <div v-else class="survey-list">
        <div class="survey-card" v-for="item in surveys" :key="item.id">
          <div class="card-top">
            <div>
              <h2>{{ item.title }}</h2>
              <p class="survey-info">{{ item.college || '全校' }} / {{ item.grade || '全部年级' }}</p>
            </div>
            <div class="status-tag" :class="getTaskStatus(item).className">
              {{ getTaskStatus(item).text }}
            </div>
          </div>

          <div class="content">
            <p><strong>量表名称：</strong>{{ item.scaleName }}</p>
            <p><strong>开始时间：</strong>{{ formatTime(item.startTime) }}</p>
            <p><strong>结束时间：</strong>{{ formatTime(item.endTime) }}</p>
            <p><strong>参与人数：</strong>{{ item.completedCount || 0 }} / {{ item.totalCount || 0 }}</p>
            <p><strong>覆盖率：</strong>{{ item.totalCount > 0 ? Math.round((item.completedCount / item.totalCount) * 100) : 0 }}%</p>
          </div>

          <div class="btn-row">
            <button
              class="btn blue"
              @click="viewSurveyDetails(item.id)"
            >
              查看详情
            </button>
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
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  flex-wrap: wrap;
}

.title {
  margin: 0;
  color: #2f5d50;
  font-size: 28px;
}

.sub-title {
  margin: 8px 0 0 0;
  color: #6f7c76;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.logout-btn {
  border: none;
  border-radius: 10px;
  padding: 10px 18px;
  background: #f3b38a;
  color: white;
  cursor: pointer;
}

.survey-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.survey-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.card-top h2 {
  margin: 0;
  color: #2f5d50;
  font-size: 20px;
}

.survey-info {
  margin: 5px 0 0 0;
  color: #777;
  line-height: 1.6;
}

.status-tag {
  padding: 6px 14px;
  border-radius: 18px;
  font-size: 14px;
  font-weight: 500;
}

.status-tag.pending {
  background: #fffbe6;
  color: #ad6800;
}

.status-tag.ongoing {
  background: #f6ffed;
  color: #52c41a;
}

.status-tag.expired {
  background: #f5f5f5;
  color: #999;
}

.content p {
  margin: 8px 0;
  color: #555;
  line-height: 1.6;
}

.content strong {
  color: #333;
}

.btn-row {
  margin-top: 16px;
  display: flex;
  gap: 12px;
}

.btn {
  border: none;
  border-radius: 10px;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn.primary {
  background: #1677ff;
  color: white;
}

.btn.primary:hover {
  background: #4096ff;
}

.btn.blue {
  background: #1677ff;
  color: white;
}

.btn.blue:hover {
  background: #4096ff;
}

.empty {
  text-align: center;
  color: #888;
  padding: 60px 0;
}
</style>
