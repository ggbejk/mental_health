﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { getUserInfo, clearLoginInfo } from '../utils/auth'

const router = useRouter()
const userInfo = getUserInfo()
const loading = ref(false)
const consultations = ref([])

const consultationStatus = ref({
  'PENDING': { text: '待处理', class: 'pending' },
  'SCHEDULED': { text: '已排期', class: 'scheduled' },
  'COMPLETED': { text: '已完成', class: 'completed' },
  'CANCELLED': { text: '已取消', class: 'cancelled' }
})

const getConsultations = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/psychologist/consultations')
    consultations.value = res.data || []
  } catch (error) {
    console.error('获取咨询预约失败', error)
    consultations.value = []
  } finally {
    loading.value = false
  }
}

const scheduleConsultation = (consultationId) => {
  router.push(`/psychologist/consultation/${consultationId}/schedule`)
}

const viewDetails = (consultationId) => {
  router.push(`/psychologist/consultation/${consultationId}`)
}

const logout = () => {
  clearLoginInfo()
  router.push('/login')
}

onMounted(() => {
  getConsultations()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="page-header">
        <div>
          <h1 class="title">咨询预约管理</h1>
          <p class="sub-title">欢迎你，{{ userInfo?.realName || '咨询师' }}</p>
        </div>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>
      <p class="sub-title">线上完成咨询预约与个案分配</p>

      <div v-if="loading" class="empty">加载中...</div>

      <div v-else-if="consultations.length === 0" class="empty">
        <p>暂无咨询预约记录</p>
        <p class="tip">预警学生被评估后会自动创建咨询预约记录</p>
      </div>

      <div v-else class="consultation-list">
        <div class="consultation-card" v-for="item in consultations" :key="item.id">
          <div class="card-top">
            <div>
              <h2>{{ item.studentName }}</h2>
              <p class="student-info">
                {{ item.college }} / {{ item.grade }} / {{ item.className }}
              </p>
            </div>
            <div class="status-tag" :class="consultationStatus[item.status]?.class">
              {{ consultationStatus[item.status]?.text || '未知' }}
            </div>
          </div>

          <div class="content">
            <p><strong>预约时间：</strong>{{ item.appointmentTime || '待排期' }}</p>
            <p><strong>咨询师：</strong>{{ item.counselorName || '待分配' }}</p>
            <p><strong>预约类型：</strong>{{ item.consultationType || '个体咨询' }}</p>
            <p><strong>备注：</strong>{{ item.notes || '无' }}</p>
          </div>

          <div class="btn-row">
            <button
              v-if="item.status === 'PENDING'"
              class="btn blue"
              @click="scheduleConsultation(item.id)"
            >
              安排咨询
            </button>
            <button
              class="btn gray"
              @click="viewDetails(item.id)"
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

.logout-btn {
  border: none;
  border-radius: 10px;
  padding: 10px 18px;
  background: #f3b38a;
  color: white;
  cursor: pointer;
}

.consultation-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.consultation-card {
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

.student-info {
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

.status-tag.scheduled {
  background: #e6f0ff;
  color: #1677ff;
}

.status-tag.completed {
  background: #f6ffed;
  color: #52c41a;
}

.status-tag.cancelled {
  background: #fff1f0;
  color: #cf1322;
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

.btn.blue {
  background: #1677ff;
  color: white;
}

.btn.blue:hover {
  background: #4096ff;
}

.btn.gray {
  background: #f5f5f5;
  color: #666;
}

.btn.gray:hover {
  background: #e8e8e8;
}

.empty {
  text-align: center;
  color: #888;
  padding: 60px 0;
}

.tip {
  font-size: 14px;
  color: #999;
  margin-top: 10px;
}
</style>
