﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { getUserInfo } from '../utils/auth'

const router = useRouter()
const userInfo = getUserInfo()
const loading = ref(false)
const submitting = ref(false)
const scales = ref([])

const form = ref({
  title: '',
  scaleId: '',
  college: '',
  grade: '',
  startTime: '',
  endTime: '',
  targetType: 'all'
})

const colleges = ['信息学院', '文学院', '理学院', '工学院', '商学院', '全校']
const grades = ['大一', '大二', '大三', '大四', '全部年级']

const getScales = async () => {
  try {
    const res = await axios.get('/api/psychologist/scales')
    scales.value = res.data || []
  } catch (error) {
    console.error('获取量表列表失败', error)
    scales.value = []
  }
}

const submitForm = async () => {
  if (!form.value.title) {
    alert('请输入任务名称')
    return
  }
  if (!form.value.scaleId) {
    alert('请选择测评量表')
    return
  }
  if (!form.value.startTime || !form.value.endTime) {
    alert('请设置开始和结束时间')
    return
  }

  const startTime = new Date(form.value.startTime)
  const endTime = new Date(form.value.endTime)
  if (endTime <= startTime) {
    alert('结束时间必须晚于开始时间')
    return
  }

  submitting.value = true
  try {
    const params = new URLSearchParams()
    params.append('title', form.value.title)
    params.append('scaleId', form.value.scaleId)
    params.append('college', form.value.college || '')
    params.append('grade', form.value.grade || '')
    params.append('startTime', form.value.startTime.replace('T', ' '))
    params.append('endTime', form.value.endTime.replace('T', ' '))
    params.append('createBy', userInfo?.userId || 1)

    await axios.post('/api/survey/task/create', params)
    alert('创建成功')
    router.push('/psychologist/surveys')
  } catch (error) {
    console.error('创建失败', error)
    alert('创建失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  getScales()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/psychologist/surveys')">返回列表</button>
        <h1 class="title">创建心理普查任务</h1>
      </div>

      <div class="card">
        <div class="form-section">
          <div class="form-item">
            <label>任务名称</label>
            <input type="text" v-model="form.title" placeholder="请输入任务名称" />
          </div>

          <div class="form-item">
            <label>测评量表</label>
            <select v-model="form.scaleId">
              <option value="">请选择量表</option>
              <option v-for="scale in scales" :key="scale.id" :value="scale.id">
                {{ scale.name }}
              </option>
            </select>
          </div>

          <div class="form-item">
            <label>目标范围</label>
            <select v-model="form.targetType">
              <option value="all">全校</option>
              <option value="college">按学院</option>
              <option value="grade">按年级</option>
            </select>
          </div>

          <div class="form-item" v-if="form.targetType === 'college'">
            <label>选择学院</label>
            <select v-model="form.college">
              <option value="">请选择学院</option>
              <option v-for="college in colleges" :key="college" :value="college">
                {{ college }}
              </option>
            </select>
          </div>

          <div class="form-item" v-if="form.targetType === 'grade'">
            <label>选择年级</label>
            <select v-model="form.grade">
              <option value="">请选择年级</option>
              <option v-for="grade in grades" :key="grade" :value="grade">
                {{ grade }}
              </option>
            </select>
          </div>

          <div class="form-row">
            <div class="form-item">
              <label>开始时间</label>
              <input type="datetime-local" v-model="form.startTime" />
            </div>

            <div class="form-item">
              <label>结束时间</label>
              <input type="datetime-local" v-model="form.endTime" />
            </div>
          </div>

          <button class="submit-btn" @click="submitForm" :disabled="submitting">
            {{ submitting ? '创建中...' : '创建任务' }}
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

.form-section {
  max-width: 600px;
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

.form-row {
  display: flex;
  gap: 20px;
}

.form-row .form-item {
  flex: 1;
}

.submit-btn {
  width: 100%;
  border: none;
  border-radius: 10px;
  padding: 14px 0;
  color: white;
  background: #1677ff;
  font-size: 16px;
  cursor: pointer;
  margin-top: 10px;
}

.submit-btn:disabled {
  background: #a8c7b9;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 0;
  }
}
</style>
