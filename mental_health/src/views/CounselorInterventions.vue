﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { getUserInfo } from '../utils/auth'

const router = useRouter()
const userInfo = getUserInfo()
const loading = ref(false)
const interventions = ref([])

const getInterventions = async () => {
  loading.value = true
  try {
    const params = {}
    if (userInfo?.userId) {
      params.counselorId = userInfo.userId
    }
    console.log('请求参数:', params)
    const res = await axios.get('/api/counselor/interventions', { params })
    console.log('API响应:', res)
    console.log('响应数据:', res.data)
    interventions.value = res.data || []
    console.log('最终interventions:', interventions.value)
  } catch (error) {
    console.error('获取干预记录失败', error)
    alert('获取干预记录失败')
  } finally {
    loading.value = false
  }
}

const viewInterventionDetail = (interventionId) => {
  router.push(`/counselor/intervention/${interventionId}`)
}

onMounted(() => {
  getInterventions()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/counselor/home')">返回</button>
        <h1 class="title">干预记录管理</h1>
      </div>

      <div class="card">
        <div v-if="loading" class="empty">加载中...</div>
        
        <div v-else-if="interventions.length === 0" class="empty">
          暂无干预记录
        </div>
        
        <div v-else class="intervention-list">
          <div class="intervention-item" v-for="item in interventions" :key="item.id">
            <div class="intervention-header">
              <h3>{{ item.studentName }} - {{ item.actionType }}</h3>
              <span class="status">
                已记录
              </span>
            </div>
            <div class="intervention-body">
              <p class="date">干预时间：{{ item.createTime }}</p>
              <p class="content">{{ item.content }}</p>
            </div>
            <div class="intervention-footer">
              <button class="detail-btn" @click="viewInterventionDetail(item.id)">
                查看详情
              </button>
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

.intervention-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.intervention-item {
  padding: 16px;
  background: #f8fcfa;
  border: 1px solid #e8f1ec;
  border-radius: 14px;
}

.intervention-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  flex-wrap: wrap;
  gap: 8px;
}

.intervention-header h3 {
  margin: 0;
  color: #2f5d50;
  font-size: 16px;
}

.status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  background: #e6f7ee;
  color: #2f8f62;
}

.intervention-body {
  margin-bottom: 12px;
}

.date {
  margin: 0 0 8px 0;
  color: #777;
  font-size: 14px;
}

.content {
  margin: 0;
  color: #555;
  line-height: 1.6;
  font-size: 14px;
}

.intervention-footer {
  text-align: right;
}

.detail-btn {
  padding: 8px 16px;
  background: #2f8f62;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
}

.empty {
  text-align: center;
  color: #888;
  padding: 40px 0;
}

@media (max-width: 768px) {
  .intervention-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
