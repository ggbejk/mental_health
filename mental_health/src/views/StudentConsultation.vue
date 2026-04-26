﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { getUserInfo } from '../utils/auth'

const router = useRouter()
const userInfo = getUserInfo()
const submitting = ref(false)

const form = ref({
  consultationType: 'INDIVIDUAL',
  appointmentTime: '',
  notes: ''
})

const consultationTypes = [
  { value: 'INDIVIDUAL', label: '个体咨询' },
  { value: 'GROUP', label: '团体辅导' },
  { value: 'CRISIS', label: '危机干预' }
]

const submitForm = async () => {
  if (!form.value.appointmentTime) {
    alert('请选择预约时间')
    return
  }

  submitting.value = true
  try {
    const params = new URLSearchParams()
    params.append('studentId', userInfo?.userId)
    params.append('studentName', userInfo?.realName || '学生')
    params.append('consultationType', form.value.consultationType)
    params.append('appointmentTime', form.value.appointmentTime.replace('T', ' '))
    params.append('notes', form.value.notes || '')

    await axios.post('/api/psychologist/consultation/book', params)
    alert('预约成功')
    router.push('/student/home')
  } catch (error) {
    console.error('预约失败', error)
    alert('预约失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/student/home')">返回</button>
        <h1 class="title">咨询预约</h1>
      </div>

      <div class="card">
        <div class="info-section">
          <p>请填写您的咨询预约信息，心理中心老师将尽快与您联系确认。</p>
        </div>

        <div class="form-section">
          <div class="form-item">
            <label>咨询类型</label>
            <select v-model="form.consultationType">
              <option v-for="type in consultationTypes" :key="type.value" :value="type.value">
                {{ type.label }}
              </option>
            </select>
          </div>

          <div class="form-item">
            <label>预约时间</label>
            <input type="datetime-local" v-model="form.appointmentTime" />
          </div>

          <div class="form-item">
            <label>备注（可选）</label>
            <textarea v-model="form.notes" rows="3" placeholder="请描述您希望咨询的问题或困扰"></textarea>
          </div>

          <button class="submit-btn" @click="submitForm" :disabled="submitting">
            {{ submitting ? '提交中...' : '提交预约' }}
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
  max-width: 700px;
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

.info-section {
  background: #f8fcfa;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 24px;
}

.info-section p {
  margin: 0;
  color: #666;
  line-height: 1.8;
}

.form-section {
  max-width: 500px;
  margin: 0 auto;
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

.form-item input,
.form-item select,
.form-item textarea {
  width: 100%;
  border: 1px solid #dfe8e3;
  border-radius: 10px;
  padding: 12px 14px;
  font-size: 14px;
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
</style>
