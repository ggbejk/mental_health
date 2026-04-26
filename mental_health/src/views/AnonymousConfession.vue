﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const submitting = ref(false)

import { getUserInfo } from '../utils/auth'
const userInfo = getUserInfo()
const studentId = userInfo?.userId

const form = ref({
  studentId,
  content: '',
  emotionTag: '',
  isAnonymous: 1
})

const tagOptions = ['学习压力', '人际关系', '家庭困扰', '情绪低落', '焦虑紧张', '睡眠问题', '其他']

const submitConfession = async () => {
  if (!form.value.content.trim()) {
    alert('请填写倾诉内容')
    return
  }

  submitting.value = true
  try {
    await axios.post('/api/student/confession/save', form.value)
    alert('倾诉提交成功')
    form.value.content = ''
    form.value.emotionTag = ''
    router.push('/student/confession/my')
  } catch (error) {
    console.error('提交失败', error)
    const msg = error?.response?.data?.message || '提交失败'
    alert(msg)
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
      </div>

      <h1 class="title">匿名倾诉</h1>
      <p class="sub-title">你可以在这里放心表达压力、情绪和困扰，系统将尽力保护你的隐私</p>

      <div class="card">
        <div class="notice">
          温馨提示：如内容涉及明显安全风险，系统可能自动触发预警并通知相关老师及时关注。
        </div>

        <div class="form-item">
          <label>情绪标签</label>
          <select v-model="form.emotionTag">
            <option value="">请选择</option>
            <option v-for="item in tagOptions" :key="item" :value="item">
              {{ item }}
            </option>
          </select>
        </div>

        <div class="form-item">
          <label>倾诉内容</label>
          <textarea
            v-model="form.content"
            rows="9"
            placeholder="把你最近的烦恼、情绪、压力写下来..."
          ></textarea>
        </div>

        <div class="form-item checkbox-row">
          <label class="checkbox-label">
            <input type="checkbox" v-model="form.isAnonymous" true-value="1" false-value="0" />
            <span>匿名提交</span>
          </label>
        </div>

        <button class="submit-btn" @click="submitConfession" :disabled="submitting">
          {{ submitting ? '提交中...' : '提交倾诉' }}
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

.notice {
  background: #fff7ee;
  color: #9a6a3a;
  border: 1px solid #f3dfc7;
  padding: 14px 16px;
  border-radius: 12px;
  margin-bottom: 22px;
  line-height: 1.7;
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
}

.checkbox-row {
  display: flex;
  align-items: center;
}

.checkbox-label {
  display: flex !important;
  align-items: center;
  gap: 8px;
  margin-bottom: 0 !important;
  font-weight: normal !important;
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
</style>
