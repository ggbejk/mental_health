﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const interventionId = route.params.interventionId
const loading = ref(false)
const intervention = ref({})

const getInterventionDetail = async () => {
  loading.value = true
  try {
    const res = await axios.get(`/api/counselor/intervention/${interventionId}`)
    intervention.value = res.data
  } catch (error) {
    console.error('获取干预详情失败', error)
    alert('获取干预详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getInterventionDetail()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/counselor/interventions')">返回</button>
        <h1 class="title">干预详情</h1>
      </div>

      <div v-if="loading" class="loading">加载中...</div>

      <div v-else class="detail-card">
        <div class="info-section">
          <h2 class="section-title">学生信息</h2>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">姓名：</span>
              <span class="value">{{ intervention.studentName || '未知' }}</span>
            </div>
            <div class="info-item">
              <span class="label">学院：</span>
              <span class="value">{{ intervention.college || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">年级：</span>
              <span class="value">{{ intervention.grade || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">班级：</span>
              <span class="value">{{ intervention.className || '-' }}</span>
            </div>
          </div>
        </div>

        <div class="info-section">
          <h2 class="section-title">干预信息</h2>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">干预类型：</span>
              <span class="value">{{ intervention.actionType || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">干预时间：</span>
              <span class="value">{{ intervention.followUpTime || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">创建时间：</span>
              <span class="value">{{ intervention.createTime || '-' }}</span>
            </div>
          </div>
        </div>

        <div class="info-section">
          <h2 class="section-title">干预内容</h2>
          <div class="content-box">
            <p>{{ intervention.content || '暂无内容' }}</p>
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

.loading {
  text-align: center;
  color: #888;
  padding: 40px 0;
}

.detail-card {
  background: white;
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.06);
}

.info-section {
  margin-bottom: 24px;
}

.info-section:last-child {
  margin-bottom: 0;
}

.section-title {
  margin: 0 0 16px 0;
  color: #2f5d50;
  font-size: 18px;
  border-bottom: 2px solid #e8f1ec;
  padding-bottom: 8px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  gap: 8px;
}

.label {
  color: #777;
  font-weight: 500;
}

.value {
  color: #333;
}

.content-box {
  background: #f8fcfa;
  border-radius: 12px;
  padding: 16px;
  line-height: 1.8;
  color: #555;
}
</style>
