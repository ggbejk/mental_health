﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)

const form = ref({
  username: '',
  password: ''
})

const doLogin = async () => {
  if (!form.value.username.trim()) {
    alert('请输入用户名')
    return
  }
  if (!form.value.password.trim()) {
    alert('请输入密码')
    return
  }

  loading.value = true
  try {
    const res = await axios.post('/api/auth/login', form.value)
    const userInfo = res.data

    localStorage.setItem('token', userInfo.token)
    localStorage.setItem('userInfo', JSON.stringify(userInfo))

    if (userInfo.role === 'STUDENT') {
      router.push('/student/home')
    } else if (userInfo.role === 'COUNSELOR') {
      router.push('/counselor/warnings')
    } else if (userInfo.role === 'ADMIN') {
      router.push('/dashboard')
    } else {
      alert('未知角色，无法跳转')
    }
  } catch (error) {
    console.error('登录失败', error)
    const msg = error?.response?.data?.message || '登录失败'
    alert(msg)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page">
    <div class="login-card">
      <div class="header">
        <h1>高校学生心理健康平台</h1>
        <p>登录后进入对应角色工作台</p>
      </div>

      <div class="form-item">
        <label>用户名</label>
        <input v-model="form.username" type="text" placeholder="请输入用户名" />
      </div>

      <div class="form-item">
        <label>密码</label>
        <input v-model="form.password" type="password" placeholder="请输入密码" />
      </div>

      <button class="login-btn" @click="doLogin" :disabled="loading">
        {{ loading ? '登录中...' : '登录' }}
      </button>

      <div class="tips">
        <p>测试角色示例：</p>
        <p>学生：stu001 / 123456</p>
        <p>辅导员：coun001 / 123456</p>
        <p>管理员：admin / 123456</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #eef8f2 0%, #f8fbfa 50%, #fff7f2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.login-card {
  width: 100%;
  max-width: 460px;
  background: #fff;
  border-radius: 22px;
  padding: 34px 30px;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.08);
}

.header {
  text-align: center;
  margin-bottom: 28px;
}

.header h1 {
  margin: 0 0 10px 0;
  font-size: 30px;
  color: #2f5d50;
}

.header p {
  margin: 0;
  color: #7a7a7a;
}

.form-item {
  margin-bottom: 18px;
}

.form-item label {
  display: block;
  margin-bottom: 8px;
  color: #444;
  font-weight: 600;
}

.form-item input {
  width: 100%;
  border: 1px solid #dfe8e3;
  border-radius: 10px;
  padding: 12px 14px;
  font-size: 14px;
}

.login-btn {
  width: 100%;
  border: none;
  border-radius: 10px;
  padding: 14px 0;
  color: white;
  background: #86c9a8;
  font-size: 16px;
  cursor: pointer;
  margin-top: 8px;
}

.login-btn:disabled {
  background: #b9dcca;
  cursor: not-allowed;
}

.tips {
  margin-top: 22px;
  padding: 14px 16px;
  background: #f8fcfa;
  border: 1px solid #e6f1eb;
  border-radius: 12px;
  color: #666;
  line-height: 1.8;
  font-size: 14px;
}
</style>
