﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const saving = ref(false)

import { getUserInfo } from '../utils/auth'
const userInfo = getUserInfo()
const studentId = userInfo?.userId

const form = ref({
  studentId,
  moodScore: '',
  moodLabel: '',
  diaryText: '',
  triggerEvent: ''
})

const moodOptions = [
  { score: 5, label: '开心' },
  { score: 4, label: '平静' },
  { score: 3, label: '一般' },
  { score: 2, label: '焦虑' },
  { score: 1, label: '难过' }
]

const selectMood = (item) => {
  form.value.moodScore = item.score
  form.value.moodLabel = item.label
}

const saveDiary = async () => {
  if (!form.value.moodScore) {
    alert('请选择今日情绪')
    return
  }

  saving.value = true
  try {
    await axios.post('/api/student/emotion/save', form.value)
    alert('情绪日记保存成功')
    router.push('/student/emotion/trend')
  } catch (error) {
    console.error('保存失败', error)
    const msg = error?.response?.data?.message || '保存失败'
    alert(msg)
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/student/home')">返回</button>
      </div>

      <h1 class="title">今日情绪日记</h1>
      <p class="sub-title">记录一下今天的心情和触发事件，帮助你更好地了解自己</p>

      <div class="card">
        <div class="form-item">
          <label>今天的整体情绪</label>
          <div class="mood-list">
            <div
              v-for="item in moodOptions"
              :key="item.score"
              class="mood-item"
              :class="{ active: form.moodScore === item.score }"
              @click="selectMood(item)"
            >
              <div class="score">{{ item.score }}</div>
              <div class="label">{{ item.label }}</div>
            </div>
          </div>
        </div>

        <div class="form-item">
          <label>触发事件</label>
          <input
            v-model="form.triggerEvent"
            type="text"
            placeholder="比如：考试、和朋友聊天、熬夜、被表扬、和家人通话"
          />
        </div>

        <div class="form-item">
          <label>日记内容</label>
          <textarea
            v-model="form.diaryText"
            rows="7"
            placeholder="写下你今天的感受、想法或压力来源..."
          ></textarea>
        </div>

        <button class="submit-btn" @click="saveDiary" :disabled="saving">
          {{ saving ? '保存中...' : '保存情绪日记' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  background: transparent;
}
.container {
  max-width: 860px;
  margin: 0 auto;
}

.top-bar {
  margin-bottom: 16px;
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
  padding: 28px;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.06);
}

.form-item {
  margin-bottom: 22px;
}

.form-item label {
  display: block;
  margin-bottom: 10px;
  color: #444;
  font-weight: 600;
}

.form-item input,
.form-item textarea {
  width: 100%;
  border: 1px solid #dfe8e3;
  border-radius: 10px;
  padding: 12px 14px;
  font-size: 14px;
}

.mood-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 14px;
}

.mood-item {
  background: #f8fcfa;
  border: 1px solid #e6f1eb;
  border-radius: 14px;
  padding: 16px 8px;
  text-align: center;
  cursor: pointer;
  transition: 0.2s;
}

.mood-item.active {
  background: #eef7f2;
  border-color: #86c9a8;
}

.score {
  font-size: 24px;
  font-weight: bold;
  color: #2f8f62;
  margin-bottom: 6px;
}

.label {
  color: #555;
}

.submit-btn {
  width: 100%;
  border: none;
  border-radius: 10px;
  padding: 14px 0;
  color: white;
  background: #f3b38a;
  font-size: 16px;
  cursor: pointer;
}

.submit-btn:disabled {
  background: #efcab3;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .mood-list {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
