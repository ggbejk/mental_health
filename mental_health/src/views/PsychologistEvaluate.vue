﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'
import { getUserInfo } from '../utils/auth'

const route = useRoute()
const router = useRouter()
const userInfo = getUserInfo()
const loading = ref(false)
const submitting = ref(false)
const warning = ref(null)

const warningId = route.params.id

const form = ref({
  warningId,
  assessmentLevel: '',
  assessmentOpinion: '',
  interventionSuggestion: ''
})

const riskLevels = [
  { value: 'YELLOW', label: '黄色预警' },
  { value: 'ORANGE', label: '橙色预警' },
  { value: 'RED', label: '红色预警' }
]

const loadWarning = async () => {
  loading.value = true
  try {
    const res = await axios.get(`/api/counselor/warning/${warningId}`)
    warning.value = res.data
  } catch (error) {
    console.error('获取预警详情失败', error)
    alert('获取预警详情失败')
  } finally {
    loading.value = false
  }
}

const submitAssessment = async () => {
  if (!form.value.assessmentLevel) {
    alert('请选择评估等级')
    return
  }
  if (!form.value.assessmentOpinion) {
    alert('请填写评估意见')
    return
  }

  submitting.value = true
  try {
    await axios.post('/api/psychologist/assess', form.value)
    alert('评估提交成功')
    router.push('/psychologist/warnings')
  } catch (error) {
    console.error('评估提交失败', error)
    alert('评估提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadWarning()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/psychologist/warnings')">返回列表</button>
        <h1 class="title">风险评估与分级复核</h1>
      </div>

      <div v-if="loading" class="empty">加载中...</div>
      
      <div v-else-if="!warning" class="empty">
        预警信息不存在
      </div>
      
      <div v-else class="card">
        <div class="warning-info">
          <h2>{{ warning.studentName }}</h2>
          <p class="student-info">
            {{ warning.college }} / {{ warning.grade }} / {{ warning.className }} / {{ warning.gender }}
          </p>
          <div class="risk-info">
            <span class="risk-tag" :class="warning.riskLevel.toLowerCase()">
              {{ warning.riskLevel === 'YELLOW' ? '黄色预警' : warning.riskLevel === 'ORANGE' ? '橙色预警' : '红色预警' }}
            </span>
            <span class="risk-score">风险分值：{{ warning.riskScore }}</span>
          </div>
        </div>

        <div class="form-section">
          <h3>评估信息</h3>
          
          <div class="form-item">
            <label>评估等级</label>
            <select v-model="form.assessmentLevel">
              <option value="">请选择</option>
              <option v-for="level in riskLevels" :key="level.value" :value="level.value">
                {{ level.label }}
              </option>
            </select>
          </div>

          <div class="form-item">
            <label>评估意见</label>
            <textarea v-model="form.assessmentOpinion" rows="4" placeholder="请输入评估意见..."></textarea>
          </div>

          <div class="form-item">
            <label>干预建议</label>
            <textarea v-model="form.interventionSuggestion" rows="4" placeholder="请输入干预建议..."></textarea>
          </div>

          <button class="submit-btn" @click="submitAssessment" :disabled="submitting">
            {{ submitting ? '提交中...' : '提交评估' }}
          </button>
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

.warning-info {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e8f1ec;
}

.warning-info h2 {
  margin: 0 0 8px 0;
  color: #2f5d50;
}

.student-info {
  margin: 0 0 12px 0;
  color: #777;
  line-height: 1.6;
}

.risk-info {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
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

.risk-score {
  color: #555;
  font-size: 14px;
}

.form-section h3 {
  margin: 0 0 20px 0;
  color: #2f5d50;
  font-size: 18px;
}

.form-item {
  margin-bottom: 20px;
}

.form-item label {
  display: block;
  margin-bottom: 8px;
  color: #444;
  font-weight: 600;
}

.form-item select,
.form-item textarea {
  width: 100%;
  border: 1px solid #dfe8e3;
  border-radius: 10px;
  padding: 12px 14px;
  font-size: 14px;
  resize: vertical;
}

.submit-btn {
  width: 100%;
  border: none;
  border-radius: 10px;
  padding: 14px 0;
  color: white;
  background: #2f8f62;
  font-size: 16px;
  cursor: pointer;
  margin-top: 10px;
}

.submit-btn:disabled {
  background: #a8c7b9;
  cursor: not-allowed;
}

.empty {
  text-align: center;
  color: #888;
  padding: 40px 0;
}

@media (max-width: 768px) {
  .top-bar {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
