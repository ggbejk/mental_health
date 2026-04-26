<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const taskId = route.params.taskId
const questions = ref([])
const answers = ref({})
const loading = ref(false)
const submitting = ref(false)

import { getUserInfo } from '../utils/auth'
const userInfo = getUserInfo()
const studentId = userInfo?.userId

const getQuestions = async () => {
  loading.value = true
  try {
    const res = await axios.get(`/api/student/task/${taskId}/questions`)
    questions.value = res.data || []
  } catch (error) {
    console.error('获取题目失败', error)
    alert('获取题目失败，请检查后端接口')
  } finally {
    loading.value = false
  }
}

const validateAnswers = () => {
  for (const q of questions.value) {
    if (!answers.value[q.id]) {
      alert(`请完成第 ${q.questionOrder} 题`)
      return false
    }
  }
  return true
}

const submitSurvey = async () => {
  if (!validateAnswers()) return

  submitting.value = true

  try {
    const payload = {
      taskId: Number(taskId),
      studentId: studentId,
      answers: questions.value.map(q => ({
        questionId: Number(q.id),
        answerScore: Number(answers.value[q.id])
      }))
    }
    console.log('提交的payload:', payload)

    const res = await axios.post('/api/student/task/submit', payload)

    const resultId = res?.data?.resultId
    if (!resultId) {
      alert('提交成功，但未获取到报告ID，请检查后端返回值')
      return
    }

    router.push({
      path: '/student/report',
      query: {
        resultId
      }
    })
  } catch (error) {
    console.error('提交失败', error)
    const msg = error?.response?.data?.message || '提交失败，请检查后端'
    alert(msg)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  getQuestions()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/student/tasks')">返回任务列表</button>
      </div>

      <h1 class="title">心理测评问卷</h1>
      <p class="sub-title">请根据最近一周的真实情况作答</p>

      <div class="score-tip">
        评分说明：1分=完全不符合，2分=比较不符合，3分=一般，4分=比较符合，5分=完全符合
      </div>

      <div v-if="loading" class="empty">题目加载中...</div>

      <div v-else-if="questions.length === 0" class="empty">暂无题目</div>

      <div v-else>
        <div class="question-card" v-for="q in questions" :key="q.id">
          <div class="question-top">
            <span class="number">{{ q.questionOrder }}</span>
            <div>
              <div class="question-title">{{ q.title }}</div>
              <div class="tag">{{ q.dimension || '默认维度' }}</div>
            </div>
          </div>

          <div class="options">
            <label class="option" v-for="n in 5" :key="n">
              <input
                type="radio"
                :name="'q' + q.id"
                :value="n"
                v-model="answers[q.id]"
              />
              <span>{{ n }}分</span>
            </label>
          </div>
        </div>

        <div class="submit-box">
          <button class="submit-btn" @click="submitSurvey" :disabled="submitting">
            {{ submitting ? '提交中...' : '提交测评' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  background: #f7fbf9;
  padding: 30px 16px 50px;
}

.container {
  max-width: 920px;
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
  font-size: 32px;
  color: #2f5d50;
  margin-bottom: 8px;
}

.sub-title {
  text-align: center;
  color: #7b7b7b;
  margin-bottom: 20px;
}

.score-tip {
  background: #fff7ee;
  color: #9a6a3a;
  border: 1px solid #f3dfc7;
  padding: 14px 18px;
  border-radius: 12px;
  margin-bottom: 24px;
}

.question-card {
  background: white;
  border-radius: 16px;
  padding: 22px;
  margin-bottom: 20px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.05);
}

.question-top {
  display: flex;
  gap: 14px;
  align-items: flex-start;
  margin-bottom: 16px;
}

.number {
  width: 34px;
  height: 34px;
  background: #9ad4b5;
  color: white;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  flex-shrink: 0;
}

.question-title {
  font-size: 17px;
  color: #333;
  line-height: 1.7;
  margin-bottom: 8px;
}

.tag {
  display: inline-block;
  background: #eef8f2;
  color: #4f9a74;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 16px;
}

.options {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.option {
  display: flex;
  align-items: center;
  gap: 6px;
  border: 1px solid #dfeae4;
  padding: 10px 14px;
  border-radius: 10px;
  background: #fcfffd;
  cursor: pointer;
}

.submit-box {
  text-align: center;
  margin-top: 28px;
}

.submit-btn {
  background: #f3b38a;
  color: white;
  border: none;
  border-radius: 10px;
  padding: 14px 40px;
  font-size: 16px;
  cursor: pointer;
}

.submit-btn:hover {
  background: #eba274;
}

.submit-btn:disabled {
  background: #f0c8af;
  cursor: not-allowed;
}

.empty {
  text-align: center;
  color: #888;
  padding: 50px 0;
}
</style>
