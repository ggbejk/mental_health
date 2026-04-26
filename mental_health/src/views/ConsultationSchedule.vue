﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const consultationId = route.params.consultationId
const loading = ref(false)
const submitting = ref(false)
const consultation = ref(null)

const form = ref({
  counselorId: '',
  appointmentTime: ''
})

const counselors = ref([])

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

const getCounselors = async () => {
  try {
    const res = await axios.get('/api/psychologist/counselors')
    counselors.value = res.data || []
  } catch (error) {
    console.error('获取咨询师列表失败', error)
    counselors.value = []
  }
}

const submitForm = async () => {
  if (!form.value.counselorId) {
    alert('请选择咨询师')
    return
  }
  if (!form.value.appointmentTime) {
    alert('请选择预约时间')
    return
  }

  submitting.value = true
  try {
    const params = new URLSearchParams()
    params.append('consultationId', consultationId)
    params.append('counselorId', form.value.counselorId)
    params.append('appointmentTime', form.value.appointmentTime.replace('T', ' '))

    await axios.post('/api/psychologist/consultation/schedule', params)
    alert('安排成功')
    router.push('/psychologist/consultation')
  } catch (error) {
    console.error('安排失败', error)
    alert('安排失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  getConsultationDetail()
  getCounselors()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/psychologist/consultation')">返回列表</button>
        <h1 class="title">安排咨询</h1>
      </div>

      <div v-if="loading" class="empty">加载中...</div>

      <div v-else-if="!consultation" class="empty">
        咨询记录不存在
      </div>

      <div v-else class="card">
        <div class="info-section">
          <h2>{{ consultation.studentName }}</h2>
          <p class="student-info">{{ consultation.college }} / {{ consultation.grade }} / {{ consultation.className }}</p>
        </div>

        <div class="form-section">
          <div class="form-item">
            <label>选择咨询师</label>
            <select v-model="form.counselorId">
              <option value="">请选择咨询师</option>
              <option v-for="counselor in counselors" :key="counselor.id" :value="counselor.id">
                {{ counselor.realName }}
              </option>
            </select>
          </div>

          <div class="form-item">
            <label>预约时间</label>
            <input type="datetime-local" v-model="form.appointmentTime" />
          </div>

          <button class="submit-btn" @click="submitForm" :disabled="submitting">
            {{ submitting ? '安排中...' : '确认安排' }}
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
  max-width: 600px;
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
  margin: 0 0 5px 0;
  color: #2f5d50;
  font-size: 20px;
}

.student-info {
  margin: 0 0 20px 0;
  color: #777;
  font-size: 14px;
}

.form-section {
  max-width: 400px;
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
.form-item select {
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

.empty {
  text-align: center;
  color: #888;
  padding: 60px 0;
}
</style>
