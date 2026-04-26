﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { getUserInfo } from '../utils/auth'

const router = useRouter()
const userInfo = getUserInfo()
const loading = ref(false)
const students = ref([])

const getStudents = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/counselor/students', {
      params: { counselorId: userInfo?.userId }
    })
    students.value = res.data || []
  } catch (error) {
    console.error('获取学生列表失败', error)
    alert('获取学生列表失败')
  } finally {
    loading.value = false
  }
}

const viewStudentDetail = (studentId) => {
  router.push(`/counselor/student/${studentId}`)
}

onMounted(() => {
  getStudents()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/counselor/home')">返回</button>
        <h1 class="title">学生档案管理</h1>
      </div>

      <div class="card">
        <div v-if="loading" class="empty">加载中...</div>
        
        <div v-else-if="students.length === 0" class="empty">
          暂无学生档案
        </div>
        
        <div v-else class="student-list">
          <div class="student-item" v-for="student in students" :key="student.id">
            <div class="student-info">
              <h3>{{ student.realName }}</h3>
              <p class="student-id">学号：{{ student.username }}</p>
              <p class="student-class">班级：{{ student.className }}</p>
            </div>
            <div class="student-actions">
              <button class="detail-btn" @click="viewStudentDetail(student.id)">
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

.student-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.student-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8fcfa;
  border: 1px solid #e8f1ec;
  border-radius: 14px;
  flex-wrap: wrap;
  gap: 16px;
}

.student-info {
  flex: 1;
}

.student-info h3 {
  margin: 0 0 8px 0;
  color: #2f5d50;
}

.student-id,
.student-class {
  margin: 0;
  color: #777;
  font-size: 14px;
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
  .student-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .student-actions {
    width: 100%;
    text-align: right;
  }
}
</style>
