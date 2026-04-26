﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'
import { getUserInfo } from '../utils/auth'

const route = useRoute()
const router = useRouter()

const warningId = route.params.warningId
const detail = ref(null)
const loading = ref(false)
const saving = ref(false)


const userInfo = getUserInfo()
const counselorId = userInfo?.userId

const form = ref({
  warningId: null,
  studentId: null,
  counselorId: counselorId,
  actionType: '',
  content: '',
  followUpTime: ''
})

const getDetail = async () => {
  loading.value = true
  try {
    const res = await axios.get(`/api/counselor/warning/${warningId}`)
    detail.value = res.data

    form.value.warningId = res.data.warningId
    form.value.studentId = res.data.studentId
  } catch (error) {
    console.error('获取详情失败', error)
    alert('获取预警详情失败')
  } finally {
    loading.value = false
  }
}

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

const saveIntervention = async () => {
  if (!form.value.actionType) {
    alert('请选择干预方式')
    return
  }
  if (!form.value.content) {
    alert('请填写干预记录内容')
    return
  }

  saving.value = true
  try {
    await axios.post('/api/counselor/intervention/save', form.value)
    alert('干预记录保存成功')
    form.value.actionType = ''
    form.value.content = ''
    form.value.followUpTime = ''
    getDetail()
  } catch (error) {
    console.error('保存失败', error)
    const msg = error?.response?.data?.message || '保存失败'
    alert(msg)
  } finally {
    saving.value = false
  }
}

const updateStatus = async (status) => {
  try {
    await axios.post('/api/counselor/warning/updateStatus', null, {
      params: {
        warningId,
        status
      }
    })
    alert('状态更新成功')
    getDetail()
  } catch (error) {
    console.error('更新状态失败', error)
    alert('更新状态失败')
  }
}

onMounted(() => {
  getDetail()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/counselor/warnings')">返回预警列表</button>
      </div>

      <div v-if="loading" class="empty">加载中...</div>

      <div v-else-if="!detail" class="empty">暂无详情数据</div>

      <div v-else>
        <div class="detail-card">
          <div class="header">
            <div>
              <h1>{{ detail.studentName }}</h1>
              <p>{{ detail.college }} / {{ detail.grade }} / {{ detail.className }} / {{ detail.gender }}</p>
            </div>
            <div class="risk-tag" :class="getRiskClass(detail.riskLevel)">
              {{ getRiskText(detail.riskLevel) }}
            </div>
          </div>

          <div class="info-grid">
            <div class="info-item"><strong>学生ID：</strong>{{ detail.studentId }}</div>
            <div class="info-item"><strong>预警来源：</strong>{{ detail.sourceType }}</div>
            <div class="info-item"><strong>风险分值：</strong>{{ detail.riskScore }}</div>
            <div class="info-item"><strong>当前状态：</strong>{{ getStatusText(detail.status) }}</div>
            <div class="info-item full"><strong>风险特征：</strong>{{ detail.riskFeatures }}</div>
            <div class="info-item full"><strong>预警时间：</strong>{{ detail.createTime }}</div>
          </div>

          <div class="status-actions">
            <button class="btn blue" @click="updateStatus('PROCESSING')">标记为干预中</button>
            <button class="btn green" @click="updateStatus('CLOSED')">标记为已结案</button>
          </div>
        </div>

        <div class="form-card">
          <h2>填写干预记录</h2>

          <div class="form-item">
            <label>干预方式</label>
            <select v-model="form.actionType">
              <option value="">请选择</option>
              <option value="谈心谈话">谈心谈话</option>
              <option value="电话沟通">电话沟通</option>
              <option value="家校联系">家校联系</option>
              <option value="心理转介">心理转介</option>
              <option value="持续跟踪">持续跟踪</option>
            </select>
          </div>

          <div class="form-item">
            <label>干预记录内容</label>
            <textarea
              v-model="form.content"
              rows="6"
              placeholder="请输入谈话纪要、学生状态、后续安排等内容"
            ></textarea>
          </div>

          <div class="form-item">
            <label>下次跟进时间</label>
            <input type="datetime-local" v-model="form.followUpTime" />
          </div>

          <button class="submit-btn" @click="saveIntervention" :disabled="saving">
            {{ saving ? '保存中...' : '保存干预记录' }}
          </button>
        </div>

        <div class="record-card">
          <h2>历史干预记录</h2>

          <div v-if="!detail.interventionRecords || detail.interventionRecords.length === 0" class="empty-small">
            暂无干预记录
          </div>

          <div v-else class="record-list">
            <div class="record-item" v-for="item in detail.interventionRecords" :key="item.id">
              <div class="record-top">
                <span class="action-tag">{{ item.actionType }}</span>
                <span class="time">{{ item.createTime }}</span>
              </div>
              <p class="record-content">{{ item.content }}</p>
              <p class="follow-time" v-if="item.followUpTime">
                下次跟进时间：{{ item.followUpTime }}
              </p>
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
  max-width: 1000px;
  margin: 0 auto;
}

.top-bar {
  margin-bottom: 18px;
}

.back-btn {
  background: #f2eee9;
  color: #666;
  border: none;
  border-radius: 8px;
  padding: 10px 16px;
  cursor: pointer;
}

.detail-card,
.form-card,
.record-card {
  background: white;
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.06);
  margin-bottom: 22px;
}

.header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.header h1 {
  margin: 0 0 8px 0;
  color: #2f5d50;
}

.header p {
  margin: 0;
  color: #777;
}

.risk-tag {
  padding: 8px 14px;
  border-radius: 18px;
  font-weight: bold;
  height: fit-content;
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

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
}

.info-item {
  color: #555;
  line-height: 1.7;
}

.info-item.full {
  grid-column: span 2;
}

.status-actions {
  display: flex;
  gap: 14px;
  margin-top: 22px;
  flex-wrap: wrap;
}

.form-card h2,
.record-card h2 {
  margin-top: 0;
  color: #2f5d50;
}

.form-item {
  margin-bottom: 18px;
}

.form-item label {
  display: block;
  margin-bottom: 8px;
  color: #444;
  font-weight: 600;
}

.form-item select,
.form-item input,
.form-item textarea {
  width: 100%;
  border: 1px solid #dfe8e3;
  border-radius: 10px;
  padding: 12px 14px;
  font-size: 14px;
}

.submit-btn,
.btn {
  border: none;
  border-radius: 10px;
  padding: 12px 24px;
  color: white;
  cursor: pointer;
  font-size: 15px;
}

.submit-btn {
  background: #f3b38a;
}

.blue {
  background: #7eaedb;
}

.green {
  background: #86c9a8;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.record-item {
  background: #f8fcfa;
  border: 1px solid #e8f1ec;
  border-radius: 14px;
  padding: 16px;
}

.record-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 10px;
}

.action-tag {
  background: #eef7f2;
  color: #2f8f62;
  padding: 4px 10px;
  border-radius: 14px;
  font-size: 13px;
}

.time,
.follow-time {
  color: #777;
  font-size: 14px;
}

.record-content {
  color: #555;
  line-height: 1.8;
  margin: 0 0 8px 0;
}

.empty,
.empty-small {
  text-align: center;
  color: #888;
  padding: 28px 0;
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }

  .info-item.full {
    grid-column: span 1;
  }
}
</style>
