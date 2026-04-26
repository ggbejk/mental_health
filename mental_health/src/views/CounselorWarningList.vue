﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { getUserInfo, clearLoginInfo } from '../utils/auth'


const router = useRouter()


const warnings = ref([])
const loading = ref(false)
const selectedLevel = ref('')
const selectedStatus = ref('')
const selectedCollege = ref('')
const colleges = ref(['信息学院', '文学院', '理学院', '工学院', '商学院'])


const userInfo = getUserInfo()
const counselorId = userInfo?.userId
const role = userInfo?.role

const logout = () => {
  clearLoginInfo()
  router.push('/login')
}

const getWarnings = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/counselor/warnings', {
      params: {
        counselorId,
        role
      }
    })
    warnings.value = res.data || []
  } catch (error) {
    console.error('获取预警列表失败', error)
    alert('获取预警列表失败，请检查后端接口')
  } finally {
    loading.value = false
  }
}

const filteredWarnings = computed(() => {
  return warnings.value.filter(item => {
    const matchLevel = selectedLevel.value ? item.riskLevel === selectedLevel.value : true
    const matchStatus = selectedStatus.value ? item.status === selectedStatus.value : true
    const matchCollege = selectedCollege.value ? item.college === selectedCollege.value : true
    return matchLevel && matchStatus && matchCollege
  })
})

const getRiskText = (level) => {
  switch (level) {
    case 'YELLOW':
      return '黄色预警'
    case 'ORANGE':
      return '橙色预警'
    case 'RED':
      return '红色预警'
    default:
      return level
  }
}

const getRiskClass = (level) => {
  switch (level) {
    case 'YELLOW':
      return 'yellow'
    case 'ORANGE':
      return 'orange'
    case 'RED':
      return 'red'
    default:
      return ''
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'UNCLAIMED':
      return '待认领'
    case 'CLAIMED':
      return '已认领'
    case 'PROCESSING':
      return '干预中'
    case 'CLOSED':
      return '已结案'
    default:
      return status
  }
}

const claimWarning = async (warningId) => {
  try {
    await axios.post('/api/counselor/warning/claim', {
      warningId,
      counselorId
    })
    alert('认领成功')
    getWarnings()
  } catch (error) {
    console.error('认领失败', error)
    const msg = error?.response?.data?.message || '认领失败'
    alert(msg)
  }
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
          <h1 class="title">{{ userInfo?.role === 'ADMIN' ? '预警总览' : '辅导员预警工作台' }}</h1>
          <p class="sub-title">欢迎你，{{ userInfo?.realName || '老师' }}</p>
        </div>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>
      <p class="sub-title">{{ userInfo?.role === 'ADMIN' ? '从管理视角查看预警处置情况' : '查看学生心理风险预警名单并开展后续干预' }}</p>

      <div class="filter-box">
        <select v-model="selectedLevel">
          <option value="">全部预警等级</option>
          <option value="YELLOW">黄色预警</option>
          <option value="ORANGE">橙色预警</option>
          <option value="RED">红色预警</option>
        </select>

        <select v-model="selectedStatus">
          <option value="">全部处理状态</option>
          <option value="UNCLAIMED">待认领</option>
          <option value="CLAIMED">已认领</option>
          <option value="PROCESSING">干预中</option>
          <option value="CLOSED">已结案</option>
        </select>

        <select v-if="userInfo?.role === 'ADMIN'" v-model="selectedCollege">
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
              <p v-if="userInfo?.role === 'ADMIN'" class="counselor-info">
                负责辅导员：{{ item.counselorName || '未分配' }}
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
            <p><strong>当前状态：</strong>{{ getStatusText(item.status) }}</p>
            <p><strong>预警时间：</strong>{{ item.createTime }}</p>
          </div>

          <div class="btn-row">
            <button
              v-if="item.status === 'UNCLAIMED' && userInfo?.role === 'COUNSELOR'"
              class="btn green"
              @click="claimWarning(item.warningId)"
            >
              认领预警
            </button>

            <button
              class="btn blue"
              @click="$router.push(`/counselor/warning/${item.warningId}`)"
            >
              {{ userInfo?.role === 'ADMIN' ? '查看详情' : '处理预警' }}
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
  max-width: 1100px;
  margin: 0 auto;
}

.title {
  text-align: center;
  color: #2f5d50;
  font-size: 34px;
  margin-bottom: 10px;
}

.sub-title {
  text-align: center;
  color: #777;
  margin-bottom: 28px;
}

.filter-box {
  display: flex;
  gap: 14px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.filter-box select {
  padding: 10px 14px;
  border-radius: 10px;
  border: 1px solid #d9e7df;
  background: white;
  min-width: 180px;
}

.warning-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 22px;
}

.warning-card {
  background: white;
  border-radius: 16px;
  padding: 22px;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.06);
  border: 1px solid #e8f1ec;
}

.card-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  margin-bottom: 18px;
}

.card-top h2 {
  margin: 0 0 8px 0;
  color: #2f5d50;
  font-size: 22px;
}

.student-info {
  margin: 0;
  color: #777;
  line-height: 1.6;
}

.counselor-info {
  margin: 5px 0 0 0;
  color: #999;
  font-size: 14px;
  line-height: 1.6;
}

.risk-tag {
  padding: 6px 12px;
  border-radius: 18px;
  font-size: 13px;
  font-weight: bold;
  white-space: nowrap;
}

.yellow {
  background: #fff7d9;
  color: #b58a00;
}

.orange {
  background: #ffe9d9;
  color: #d67a1f;
}

.red {
  background: #ffe1e1;
  color: #d13b3b;
}

.content p {
  margin: 10px 0;
  color: #555;
  line-height: 1.7;
}

.btn-row {
  margin-top: 18px;
}

.btn {
  width: 100%;
  border: none;
  border-radius: 10px;
  padding: 12px 0;
  font-size: 15px;
  cursor: pointer;
  color: white;
}

.green {
  background: #86c9a8;
}

.gray {
  background: #b7b7b7;
  cursor: not-allowed;
}
.blue {
  background: #7eaedb;
}
.empty {
  text-align: center;
  color: #888;
  padding: 60px 0;
}
.page-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 24px;
}

.logout-btn {
  border: none;
  border-radius: 10px;
  padding: 10px 18px;
  background: #f3b38a;
  color: white;
  cursor: pointer;
}
</style>
