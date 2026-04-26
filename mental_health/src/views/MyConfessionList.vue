﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
import { getUserInfo } from '../utils/auth'
const userInfo = getUserInfo()
const studentId = userInfo?.userId

const loading = ref(false)
const list = ref([])

const getList = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/student/confession/my', {
      params: { studentId }
    })
    list.value = res.data || []
  } catch (error) {
    console.error('获取记录失败', error)
    alert('获取记录失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/student/tasks')">返回</button>
        <button class="write-btn" @click="router.push('/student/confession/write')">去倾诉</button>
      </div>

      <h1 class="title">我的倾诉记录</h1>
      <p class="sub-title">记录你的表达，也帮助你回看自己的情绪轨迹</p>

      <div class="card">
        <div v-if="loading" class="empty">加载中...</div>

        <div v-else-if="list.length === 0" class="empty">
          还没有倾诉记录
        </div>

        <div v-else class="record-list">
          <div class="record-item" v-for="item in list" :key="item.id">
            <div class="record-top">
              <span class="tag">{{ item.emotionTag || '未分类' }}</span>
              <span class="time">{{ item.createTime }}</span>
            </div>

            <p class="content">{{ item.content }}</p>

            <p class="anonymous-text">
              {{ item.isAnonymous === 1 ? '匿名提交' : '实名提交' }}
            </p>
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
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.back-btn,
.write-btn {
  border: none;
  border-radius: 8px;
  padding: 10px 16px;
  cursor: pointer;
}

.back-btn {
  background: #f2eee9;
  color: #666;
}

.write-btn {
  background: #86c9a8;
  color: white;
}

.title {
  text-align: center;
  font-size: 34px;
  color: #2f5d50;
  margin-bottom: 10px;
}

.sub-title {
  text-align: center;
  color: #777;
  margin-bottom: 28px;
}

.card {
  background: white;
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.06);
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

.tag {
  background: #eef7f2;
  color: #2f8f62;
  padding: 4px 10px;
  border-radius: 14px;
  font-size: 13px;
}

.time,
.anonymous-text {
  color: #777;
  font-size: 14px;
}

.content {
  color: #555;
  line-height: 1.8;
  margin: 0 0 8px 0;
}

.empty {
  text-align: center;
  color: #888;
  padding: 40px 0;
}
</style>
