﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'
import { getUserInfo } from '../utils/auth'

const route = useRoute()
const router = useRouter()
const taskId = route.params.taskId

const userInfo = getUserInfo()
const studentId = userInfo?.userId || userInfo?.id

const questions = ref([])
const answers = ref({})
const loading = ref(false)
const submitting = ref(false)

const scoreOptions = [
  { value: 1, label: '非常不符合' },
  { value: 2, label: '比较不符合' },
  { value: 3, label: '一般' },
  { value: 4, label: '比较符合' },
  { value: 5, label: '非常符合' }
]

const answeredCount = computed(() => {
  return Object.keys(answers.value).filter(key => answers.value[key] !== '' && answers.value[key] !== null && answers.value[key] !== undefined).length
})

const progress = computed(() => {
  const total = questions.value.length
  return total === 0 ? 0 : Math.round((answeredCount.value / total) * 100)
})

const unansweredIds = computed(() => {
  return questions.value
    .filter(q => !answers.value[q.id])
    .map(q => q.id)
})

const isUnanswered = (questionId) => {
  return unansweredIds.value.includes(questionId)
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await axios.get(`/api/student/task/${taskId}/questions`)
    questions.value = res.data || []
  } catch (e) {
    console.error('加载题目失败', e)
    alert(e?.response?.data?.message || e?.response?.data || '加载题目失败')
  } finally {
    loading.value = false
  }
})

const submit = async () => {
  if (!studentId) {
    alert('未获取到学生ID，请重新登录')
    return
  }

  if (questions.value.length === 0) {
    alert('当前没有可提交的题目')
    return
  }

  if (answeredCount.value !== questions.value.length) {
    alert(`还有 ${questions.value.length - answeredCount.value} 题未完成，请检查后再提交`)
    const firstUnansweredId = unansweredIds.value[0]
    if (firstUnansweredId) {
      const el = document.getElementById(`question-${firstUnansweredId}`)
      el?.scrollIntoView({ behavior: 'smooth', block: 'center' })
    }
    return
  }

  submitting.value = true

  try {
    const payload = {
      taskId: Number(taskId),
      studentId: Number(studentId),
      answers: questions.value.map(q => ({
        questionId: Number(q.id),
        answerScore: Number(answers.value[q.id])
      }))
    }

    const res = await axios.post('/api/student/task/submit', payload)

    const resultId = res?.data?.resultId
    if (!resultId) {
      alert('提交成功，但未获取到报告ID')
      return
    }

    router.push({
      path: '/student/report',
      query: { resultId }
    })
  } catch (e) {
    console.error('提交失败', e)
    alert(e?.response?.data?.message || e?.response?.data || '提交失败')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="header-card">
        <div class="title-wrap">
          <h1 class="title">心理测评问卷</h1>
          <p class="sub-title">
            请根据你最近一段时间的真实感受作答，题目没有对错之分。
          </p>
        </div>

        <div class="status-box">
          <div class="status-item">
            <span class="status-label">题目总数</span>
            <span class="status-value">{{ questions.length }}</span>
          </div>
          <div class="status-item">
            <span class="status-label">已完成</span>
            <span class="status-value done">{{ answeredCount }}</span>
          </div>
          <div class="status-item">
            <span class="status-label">未完成</span>
            <span class="status-value undone">{{ questions.length - answeredCount }}</span>
          </div>
        </div>
      </div>

      <div class="progress-card">
        <div class="progress-top">
          <span>作答进度</span>
          <span>{{ progress }}%</span>
        </div>
        <div class="progress-bar">
          <div class="progress-inner" :style="{ width: progress + '%' }"></div>
        </div>
      </div>

      <div class="score-tip-card">
        <div class="tip-title">评分说明</div>
        <div class="tip-options">
          <span v-for="item in scoreOptions" :key="item.value" class="tip-option">
            {{ item.value }} = {{ item.label }}
          </span>
        </div>
      </div>

      <div v-if="loading" class="empty">加载题目中...</div>

      <div v-else-if="questions.length === 0" class="empty">暂无可作答题目</div>

      <div v-else>
        <div
          v-for="(q, index) in questions"
          :key="q.id"
          :id="`question-${q.id}`"
          class="question-card"
          :class="{ highlight: isUnanswered(q.id) && answeredCount > 0 }"
        >
          <div class="question-top">
            <div class="question-index">第 {{ index + 1 }} 题</div>
            <div v-if="answers[q.id]" class="answered-tag">已作答</div>
          </div>

          <div class="question-title">
            {{ q.title }}
          </div>

          <div class="options">
            <label
              v-for="item in scoreOptions"
              :key="item.value"
              class="option"
              :class="{ active: Number(answers[q.id]) === item.value }"
            >
              <input
                type="radio"
                :name="'q_' + q.id"
                :value="item.value"
                v-model="answers[q.id]"
              />
              <span class="option-score">{{ item.value }}</span>
              <span class="option-label">{{ item.label }}</span>
            </label>
          </div>
        </div>

        <div class="submit-box">
          <button class="back-btn" @click="router.push('/student/tasks')">返回任务列表</button>
          <button class="submit-btn" :disabled="submitting" @click="submit">
            {{ submitting ? '提交中...' : '提交测评' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  background: #f5f8f7;
  min-height: 100vh;
}

.container {
  max-width: 980px;
  margin: 0 auto;
  padding: 28px 16px 40px;
}

.header-card,
.progress-card,
.score-tip-card,
.question-card {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
  border: 1px solid #e8f1ec;
}

.header-card {
  padding: 24px;
  margin-bottom: 18px;
}

.title-wrap {
  margin-bottom: 18px;
}

.title {
  text-align: center;
  color: #2f5d50;
  margin: 0 0 10px 0;
  font-size: 32px;
}

.sub-title {
  text-align: center;
  color: #70807a;
  margin: 0;
  line-height: 1.7;
}

.status-box {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}

.status-item {
  background: #f8fcfa;
  border-radius: 14px;
  padding: 16px;
  text-align: center;
}

.status-label {
  display: block;
  color: #7b8883;
  font-size: 14px;
  margin-bottom: 8px;
}

.status-value {
  font-size: 26px;
  font-weight: bold;
  color: #2f5d50;
}

.status-value.done {
  color: #2f8f62;
}

.status-value.undone {
  color: #d67a1f;
}

.progress-card {
  padding: 18px 20px;
  margin-bottom: 18px;
}

.progress-top {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  color: #51605b;
  font-weight: 600;
}

.progress-bar {
  height: 10px;
  background: #e6f0ec;
  border-radius: 999px;
  overflow: hidden;
}

.progress-inner {
  height: 100%;
  background: linear-gradient(90deg, #88cfac 0%, #4caf88 100%);
  transition: 0.3s;
}

.score-tip-card {
  padding: 18px 20px;
  margin-bottom: 20px;
}

.tip-title {
  color: #2f5d50;
  font-weight: 700;
  margin-bottom: 12px;
}

.tip-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tip-option {
  background: #eef7f2;
  color: #2f8f62;
  border-radius: 999px;
  padding: 8px 12px;
  font-size: 13px;
}

.question-card {
  padding: 20px;
  margin-bottom: 18px;
  transition: 0.2s;
}

.question-card.highlight {
  border: 1px solid #f0cf9a;
  box-shadow: 0 8px 20px rgba(240, 207, 154, 0.18);
}

.question-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.question-index {
  display: inline-block;
  background: #eef7f2;
  color: #2f8f62;
  border-radius: 999px;
  padding: 6px 12px;
  font-size: 13px;
  font-weight: 600;
}

.answered-tag {
  font-size: 13px;
  color: #2f8f62;
  background: #edf9f2;
  border-radius: 999px;
  padding: 5px 10px;
}

.question-title {
  font-size: 18px;
  line-height: 1.8;
  color: #33413c;
  font-weight: 600;
  margin-bottom: 16px;
}

.options {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}

.option {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f7faf8;
  border: 1px solid #e4ede8;
  border-radius: 12px;
  padding: 12px 14px;
  cursor: pointer;
  transition: 0.2s;
}

.option:hover {
  background: #eef7f2;
}

.option.active {
  background: #eaf7f0;
  border-color: #91cfaf;
}

.option input {
  margin: 0;
}

.option-score {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #dfeee6;
  color: #2f8f62;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: bold;
  flex-shrink: 0;
}

.option-label {
  color: #46534f;
}

.submit-box {
  display: flex;
  justify-content: center;
  gap: 14px;
  margin-top: 30px;
  flex-wrap: wrap;
}

.back-btn,
.submit-btn {
  border: none;
  padding: 12px 28px;
  border-radius: 12px;
  cursor: pointer;
  font-size: 16px;
}

.back-btn {
  background: #f2eee9;
  color: #666;
}

.submit-btn {
  background: #4caf88;
  color: white;
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.empty {
  text-align: center;
  color: #888;
  padding: 60px 0;
}

@media (max-width: 768px) {
  .container {
    padding: 18px 12px 32px;
  }

  .title {
    font-size: 26px;
  }

  .status-box {
    grid-template-columns: 1fr;
  }

  .question-title {
    font-size: 16px;
  }

  .option {
    align-items: flex-start;
  }

  .submit-box {
    flex-direction: column;
  }

  .back-btn,
  .submit-btn {
    width: 100%;
  }
}
</style>
