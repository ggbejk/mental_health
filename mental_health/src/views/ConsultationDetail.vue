﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const consultationId = route.params.consultationId
const loading = ref(false)
const consultation = ref(null)

const getConsultationDetail = async () => {
  loading.value = true
  try {
    const res = await axios.get(`/api/psychologist/consultation/${consultationId}`)
    consultation.value = res.data
  } catch (error) {
    console.error('获取咨询详情失败', error)
    alert('获取咨询详情失败')
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
  getConsultationDetail()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/psychologist/consultation')">返回列表</button>
        <h1 class="title">咨询详情</h1>
      </div>

      <div v-if="loading" class="empty">加载中...</div>

      <div v-else-if="!consultation" class="empty">
        咨询记录不存在
      </div>

      <div v-else class="card">
        <div class="info-section">
          <h2>{{ consultation.studentName }}</h2>
          <div class="info-grid">
            <div class="info-item">
              <strong>学生信息：</strong>{{ consultation.college }} / {{ consultation.grade }} / {{ consultation.className }}
            </div>
            <div class="info-item">
              <strong>咨询类型：</strong>{{ consultation.consultationType || '个体咨询' }}
            </div>
            <div class="info-item">
              <strong>预约时间：</strong>{{ formatTime(consultation.appointmentTime) }}
            </div>
            <div class="info-item">
              <strong>咨询师：</strong>{{ consultation.counselorName || '待分配' }}
            </div>
            <div class="info-item">
              <strong>状态：</strong>{{ consultation.status || '待处理' }}
            </div>
            <div class="info-item">
              <strong>备注：</strong>{{ consultation.notes || '无' }}
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
  max-width: 800px;
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
}

.info-item {
  color: #555;
  line-height: 1.7;
}

.info-item strong {
  color: #333;
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
}
</style>
