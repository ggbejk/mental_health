﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { getUserInfo, clearLoginInfo } from '../utils/auth'

const router = useRouter()
const userInfo = getUserInfo()
const loading = ref(false)
const warnings = ref([])
const selectedLevel = ref('')
const selectedStatus = ref('')
const selectedCollege = ref('')
const colleges = ref(['信息学院', '文学院', '理学院', '工学院', '商学院'])

const filteredWarnings = computed(() => {
  return warnings.value.filter(item => {
    const matchLevel = selectedLevel.value ? item.riskLevel === selectedLevel.value : true
    const matchStatus = selectedStatus.value ? item.status === selectedStatus.value : true
    const matchCollege = selectedCollege.value ? item.college === selectedCollege.value : true
    return matchLevel && matchStatus && matchCollege
  })
})

const getWarnings = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/counselor/warnings', {
      params: { counselorId: userInfo?.userId, role: userInfo?.role }
    })
    warnings.value = res.data || []
  } catch (error) {
    console.error('获取预警列表失败', error)
    alert('获取预警列表失败')
  } finally {
    loading.value = false
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

const getRiskText = (level) => {
  switch (level) {
    case 'YELLOW': return '黄色预警'
    case 'ORANGE': return '橙色预警'
    case 'RED': return '红色预警'
    default: return '未知'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'UNCLAIMED': return '待评估'
    case 'CLAIMED': return '已评估'
    case 'PROCESSING': return '复核中'
    case 'CLOSED': return '已结案'
    default: return '未知'
  }
}

const evaluateWarning = (warningId) => {
  router.push(`/psychologist/evaluate/${warningId}`)
}

const logout = () => {
  clearLoginInfo()
  router.push('/login')
}

onMounted(() => {
  getWarnings()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="page-header">
        <div>
          <h1 class="title">风险评估与分级复核</h1>
          <p class="sub-title">欢迎你，{{ userInfo?.realName || '咨询师' }}</p>
        </div>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>
      <p class="sub-title">对预警学生进行风险评估与分级复核</p>

      <div class="filter-box">
        <select v-model="selectedLevel">
          <option value="">全部预警等级</option>
          <option value="YELLOW">黄色预警</option>
          <option value="ORANGE">橙色预警</option>
          <option value="RED">红色预警</option>
        </select>

        <select v-model="selectedStatus">
          <option value="">全部评估状态</option>
          <option value="UNCLAIMED">待评估</option>
          <option value="CLAIMED">已评估</option>
          <option value="PROCESSING">复核中</option>
          <option value="CLOSED">已结案</option>
        </select>

        <select v-model="selectedCollege">
          <option value="">全部学院</option>
          <option v-for="college in colleges" :key="college" :value="college">{{ college }}</option>
        </select>
      </div>

      <div v-if="loading" class="empty">加载中...</div>

      <div v-else-if="filteredWarnings.length === 0" class="empty">
        暂无预警数据
      </div>

      <div v-else class="warning-list">
        <div class="warning-card" v-for="item in filteredWarnings" :key="item.warningId">
          <div class="card-top">
            <div>
              <h2>{{ item.studentName }}</h2>
              <p class="student-info">
                {{ item.college }} / {{ item.grade }} / {{ item.className }} / {{ item.gender }}
              </p>
            </div>
            <div class="risk-tag" :class="getRiskClass(item.riskLevel)">
              {{ getRiskText(item.riskLevel) }}
            </div>
          </div>

          <div class="content">
            <p><strong>学生ID：</strong>{{ item.studentId }}</p>
            <p><strong>预警来源：</strong>{{ item.sourceType }}</p>
            <p><strong>风险分值：</strong>{{ item.riskScore }}</p>
            <p><strong>风险特征：</strong>{{ item.riskFeatures }}</p>
            <p><strong>评估状态：</strong>{{ getStatusText(item.status) }}</p>
            <p><strong>预警时间：</strong>{{ item.createTime }}</p>
          </div>

          <div class="btn-row">
            <button
              class="btn blue"
              @click="evaluateWarning(item.warningId)"
            >
              进行评估
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

.filter-box {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.filter-box select {
  padding: 10px 14px;
  border: 1px solid #e8e8e8;
  border-radius: 10px;
  font-size: 14px;
  background: white;
  color: #333;
}

.warning-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.warning-card {
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

.risk-tag {
  padding: 6px 14px;
  border-radius: 18px;
  font-size: 14px;
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

.empty {
  text-align: center;
  color: #888;
  padding: 60px 0;
}
</style>
