﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const scaleId = route.params.id
const isEdit = route.path.includes('/edit')

const loading = ref(false)
const submitting = ref(false)
const scale = ref(null)
const questions = ref([])

const form = ref({
  name: '',
  description: '',
  status: 'ACTIVE'
})

const getScaleDetail = async () => {
  loading.value = true
  try {
    const res = await axios.get(`/api/psychologist/scale/${scaleId}`)
    scale.value = res.data
    form.value.name = res.data.name
    form.value.description = res.data.description
    form.value.status = res.data.status

    const qRes = await axios.get(`/api/psychologist/scale/${scaleId}/questions`)
    if (qRes.data && qRes.data.length > 0) {
      questions.value = qRes.data.map(q => ({
        id: q.id,
        questionText: q.title,
        questionType: q.questionType || 'SINGLE',
        options: [
          { text: 'A. 非常符合', score: 5 },
          { text: 'B. 符合', score: 4 },
          { text: 'C. 一般', score: 3 },
          { text: 'D. 不符合', score: 2 },
          { text: 'E. 非常不符合', score: 1 }
        ]
      }))
    }
  } catch (error) {
    console.error('获取量表详情失败', error)
    alert('获取量表详情失败')
  } finally {
    loading.value = false
  }
}

const saveScale = async () => {
  if (!form.value.name) {
    alert('请输入量表名称')
    return
  }

  submitting.value = true
  try {
    const params = new URLSearchParams()
    params.append('name', form.value.name)
    params.append('description', form.value.description)

    await axios.post(`/api/psychologist/scale/${scaleId}/update`, params)
    alert('保存成功')
    router.push('/psychologist/scales')
  } catch (error) {
    console.error('保存失败', error)
    alert('保存失败')
  } finally {
    submitting.value = false
  }
}

const addQuestion = () => {
  questions.value.push({
    id: null,
    questionText: '',
    questionType: 'SINGLE',
    options: [
      { text: 'A. 非常符合', score: 5 },
      { text: 'B. 符合', score: 4 },
      { text: 'C. 一般', score: 3 },
      { text: 'D. 不符合', score: 2 },
      { text: 'E. 非常不符合', score: 1 }
    ]
  })
}

const removeQuestion = (index) => {
  questions.value.splice(index, 1)
}

onMounted(() => {
  getScaleDetail()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/psychologist/scales')">返回列表</button>
        <h1 class="title">{{ isEdit ? '编辑量表' : '预览量表' }}</h1>
      </div>

      <div v-if="loading" class="empty">加载中...</div>

      <div v-else class="card">
        <div class="form-section">
          <div class="form-item">
            <label>量表名称</label>
            <input type="text" v-model="form.name" :disabled="!isEdit" placeholder="请输入量表名称" />
          </div>

          <div class="form-item">
            <label>量表描述</label>
            <textarea v-model="form.description" :disabled="!isEdit" rows="3" placeholder="请输入量表描述"></textarea>
          </div>

          <div class="form-item">
            <label>状态</label>
            <select v-model="form.status" :disabled="!isEdit">
              <option value="ACTIVE">启用</option>
              <option value="INACTIVE">停用</option>
            </select>
          </div>
        </div>

        <div class="questions-section">
          <div class="section-header">
            <h3>题目列表 ({{ questions.length }})</h3>
            <button v-if="isEdit" class="btn primary" @click="addQuestion">添加题目</button>
          </div>

          <div v-if="questions.length === 0" class="empty-small">
            暂无题目
          </div>

          <div v-else class="question-list">
            <div v-for="(q, index) in questions" :key="index" class="question-item">
              <div class="question-header">
                <span class="question-number">题目 {{ index + 1 }}</span>
                <span class="question-type">{{ q.questionType === 'SINGLE' ? '单选题' : '多选题' }}</span>
                <button v-if="isEdit" class="remove-btn" @click="removeQuestion(index)">删除</button>
              </div>
              <div class="question-text">
                <input type="text" v-model="q.questionText" :disabled="!isEdit" placeholder="请输入题目内容" />
              </div>
              <div class="options-list">
                <div v-for="(opt, optIndex) in q.options" :key="optIndex" class="option-item">
                  <span class="option-label">{{ String.fromCharCode(65 + optIndex) }}.</span>
                  <input type="text" v-model="opt.text" :disabled="!isEdit" />
                  <span class="option-score">分值: {{ opt.score }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="form-actions" v-if="isEdit">
          <button class="submit-btn" @click="saveScale" :disabled="submitting">
            {{ submitting ? '保存中...' : '保存量表' }}
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

.form-section {
  margin-bottom: 30px;
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
  resize: vertical;
}

.form-item input:disabled,
.form-item select:disabled,
.form-item textarea:disabled {
  background: #f5f5f5;
}

.questions-section {
  border-top: 1px solid #e8f1ec;
  padding-top: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  color: #2f5d50;
}

.btn.primary {
  background: #722ed1;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  cursor: pointer;
}

.question-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.question-item {
  background: #f8fcfa;
  border: 1px solid #e8f1ec;
  border-radius: 14px;
  padding: 16px;
}

.question-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.question-number {
  font-weight: 600;
  color: #2f5d50;
}

.question-type {
  background: #e8f1ec;
  color: #2f8f62;
  padding: 4px 10px;
  border-radius: 14px;
  font-size: 12px;
}

.remove-btn {
  margin-left: auto;
  background: #ff4d4f;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 4px 10px;
  cursor: pointer;
  font-size: 12px;
}

.question-text input {
  width: 100%;
  border: 1px solid #dfe8e3;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  margin-bottom: 12px;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.option-label {
  width: 24px;
  color: #666;
}

.option-item input {
  flex: 1;
  border: 1px solid #dfe8e3;
  border-radius: 6px;
  padding: 8px 12px;
  font-size: 14px;
}

.option-score {
  color: #999;
  font-size: 13px;
  min-width: 70px;
}

.form-actions {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e8f1ec;
}

.submit-btn {
  width: 100%;
  border: none;
  border-radius: 10px;
  padding: 14px 0;
  color: white;
  background: #722ed1;
  font-size: 16px;
  cursor: pointer;
}

.submit-btn:disabled {
  background: #a8c7b9;
  cursor: not-allowed;
}

.empty,
.empty-small {
  text-align: center;
  color: #888;
  padding: 28px 0;
}

.empty-small {
  padding: 20px 0;
}
</style>
