﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'
import { getUserInfo } from '../utils/auth'
import ParentContactModal from './ParentContactModal.vue'

const route = useRoute()
const router = useRouter()
const userInfo = getUserInfo()
const studentId = route.params.studentId

const loading = ref(false)
const student = ref(null)
const warnings = ref([])
const interventions = ref([])
const showParentContact = ref(false)

const getStudentDetail = async () => {
  loading.value = true
  try {
    const res = await axios.get(`/api/student/${studentId}`)
    student.value = res.data
    student.value.counselorId = userInfo?.userId
  } catch (error) {
    console.error('获取学生详情失败', error)
  } finally {
    loading.value = false
  }
}

const getStudentWarnings = async () => {
  try {
    const res = await axios.get('/api/counselor/warnings', {
      params: {
        counselorId: userInfo?.userId,
        role: userInfo?.role
      }
    })
    warnings.value = res.data.filter(w => w.studentId === parseInt(studentId))
  } catch (error) {
    console.error('获取预警记录失败', error)
  }
}

const getStudentInterventions = async () => {
  try {
    const res = await axios.get('/api/counselor/interventions', {
      params: { 
        counselorId: userInfo?.userId,
        studentId: parseInt(studentId)
      }
    })
    const studentIdNum = parseInt(studentId)
    interventions.value = res.data.filter(i => i.studentId === studentIdNum)
  } catch (error) {
    console.error('获取干预记录失败', error)
  }
}

const getRiskText = (level) => {
  switch (level) {
    case 'YELLOW': return '黄色预警'
    case 'ORANGE': return '橙色预警'
    case 'RED': return '红色预警'
    default: return level
  }
}

const getRiskClass = (level) => {
  switch (level) {
    case 'YELLOW': return 'yellow'
    case 'ORANGE': return 'orange'
    case 'RED': return 'red'
    default: return ''
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'UNCLAIMED': return '待认领'
    case 'CLAIMED': return '已认领'
    case 'PROCESSING': return '干预中'
    case 'CLOSED': return '已结案'
    default: return status
  }
}

const openParentContact = () => {
  showParentContact.value = true
}

const closeParentContact = () => {
  showParentContact.value = false
}

onMounted(() => {
  getStudentDetail()
  getStudentWarnings()
  getStudentInterventions()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/counselor/students')">返回列表</button>
        <h1 class="title">学生档案详情</h1>
        <button 
          v-if="student && student.parentPhone" 
          class="contact-btn" 
          @click="openParentContact"
        >
          联系家长
        </button>
      </div>

      <div v-if="loading" class="empty">加载中...</div>

      <div v-else>
        <div class="card" v-if="student">
          <h2>基本信息</h2>
          <div class="info-grid">
            <div class="info-item">
              <strong>姓名：</strong>{{ student.realName || student.name }}
            </div>
            <div class="info-item">
              <strong>学号：</strong>{{ student.username }}
            </div>
            <div class="info-item">
              <strong>性别：</strong>{{ student.gender }}
            </div>
            <div class="info-item">
              <strong>学院：</strong>{{ student.college }}
            </div>
            <div class="info-item">
              <strong>年级：</strong>{{ student.grade }}
            </div>
            <div class="info-item">
              <strong>班级：</strong>{{ student.className }}
            </div>
            <div class="info-item">
              <strong>专业：</strong>{{ student.major }}
            </div>
            <div class="info-item">
              <strong>电话：</strong>{{ student.phone }}
            </div>
          </div>
        </div>

        <div class="card">
          <h2>预警记录 ({{ warnings.length }})</h2>
          <div v-if="warnings.length === 0" class="empty-small">
            暂无预警记录
          </div>
          <div v-else class="warning-list">
            <div v-for="warning in warnings" :key="warning.warningId" class="warning-item">
              <div class="warning-header">
                <span class="risk-tag" :class="getRiskClass(warning.riskLevel)">
                  {{ getRiskText(warning.riskLevel) }}
                </span>
                <span class="status-tag">{{ getStatusText(warning.status) }}</span>
                <span class="time">{{ warning.createTime }}</span>
              </div>
              <div class="warning-content">
                <p><strong>风险分值：</strong>{{ warning.riskScore }}</p>
                <p><strong>预警来源：</strong>{{ warning.sourceType }}</p>
                <p><strong>风险特征：</strong>{{ warning.riskFeatures }}</p>
              </div>
            </div>
          </div>
        </div>

        <div class="card">
          <h2>干预记录 ({{ interventions.length }})</h2>
          <div v-if="interventions.length === 0" class="empty-small">
            暂无干预记录
          </div>
          <div v-else class="intervention-list">
            <div v-for="item in interventions" :key="item.id" class="intervention-item">
              <div class="intervention-header">
                <span class="action-tag">{{ item.actionType }}</span>
                <span class="time">{{ item.createTime }}</span>
              </div>
              <p class="intervention-content">{{ item.content }}</p>
              <p class="follow-time" v-if="item.followUpTime">
                下次跟进时间：{{ item.followUpTime }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <ParentContactModal
      :visible="showParentContact"
      :student="student"
      @close="closeParentContact"
      @success="closeParentContact"
    />
  </div>
</template>

<style scoped>
.page {
  background: transparent;
}

.container {
  max-width: 1000px;
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
  margin-bottom: 20px;
}

.card h2 {
  margin: 0 0 20px 0;
  color: #2f5d50;
  font-size: 18px;
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

.warning-list,
.intervention-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.warning-item,
.intervention-item {
  background: #f8fcfa;
  border: 1px solid #e8f1ec;
  border-radius: 14px;
  padding: 16px;
}

.warning-header,
.intervention-header {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.risk-tag {
  padding: 4px 10px;
  border-radius: 14px;
  font-size: 13px;
  font-weight: 500;
}

.risk-tag.yellow {
  background: #fffbe6;
  color: #ad6800;
}

.risk-tag.orange {
  background: #fff7e6;
  color: #d46b00;
}

.risk-tag.red {
  background: #fff1f0;
  color: #cf1322;
}

.status-tag {
  background: #e8f1ec;
  color: #2f8f62;
  padding: 4px 10px;
  border-radius: 14px;
  font-size: 13px;
}

.action-tag {
  background: #eef7f2;
  color: #2f8f62;
  padding: 4px 10px;
  border-radius: 14px;
  font-size: 13px;
}

.time {
  color: #999;
  font-size: 13px;
}

.warning-content p,
.intervention-content {
  margin: 8px 0;
  color: #555;
  line-height: 1.7;
}

.follow-time {
  color: #777;
  font-size: 14px;
  margin-top: 8px;
}

.empty,
.empty-small {
  text-align: center;
  color: #888;
  padding: 28px 0;
}

.empty-small {
  padding: 20px 0;
}

.contact-btn {
  background: linear-gradient(135deg, #722ed1, #9254de);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 10px 16px;
  cursor: pointer;
  font-size: 14px;
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
