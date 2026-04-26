﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const submitting = ref(false)

const form = ref({
  name: '',
  description: ''
})

const submitForm = async () => {
  if (!form.value.name) {
    alert('请输入量表名称')
    return
  }

  submitting.value = true
  try {
    const params = new URLSearchParams()
    params.append('name', form.value.name)
    params.append('description', form.value.description)

    await axios.post('/api/psychologist/scale/create', params)
    alert('创建成功')
    router.push('/psychologist/scales')
  } catch (error) {
    console.error('创建失败', error)
    alert('创建失败')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/psychologist/scales')">返回列表</button>
        <h1 class="title">创建量表</h1>
      </div>

      <div class="card">
        <div class="form-section">
          <div class="form-item">
            <label>量表名称</label>
            <input type="text" v-model="form.name" placeholder="请输入量表名称" />
          </div>

          <div class="form-item">
            <label>量表描述</label>
            <textarea v-model="form.description" rows="4" placeholder="请输入量表描述"></textarea>
          </div>

          <button class="submit-btn" @click="submitForm" :disabled="submitting">
            {{ submitting ? '创建中...' : '创建量表' }}
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
.form-item textarea {
  width: 100%;
  border: 1px solid #dfe8e3;
  border-radius: 10px;
  padding: 12px 14px;
  font-size: 14px;
  resize: vertical;
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
  margin-top: 10px;
}

.submit-btn:disabled {
  background: #a8c7b9;
  cursor: not-allowed;
}
</style>
