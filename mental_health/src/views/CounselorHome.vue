﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { useRouter } from 'vue-router'
import { getUserInfo } from '../utils/auth'

const router = useRouter()
const user = getUserInfo()
</script>

<template>
  <div>
    <div class="welcome-card">
      <h2>欢迎，{{ user?.realName || '老师' }}</h2>
      <p>请及时关注学生预警信息，认领并完成后续干预处置。</p>
    </div>

    <div class="grid">
      <div class="card" @click="router.push('/counselor/warnings')">
        <h3>预警列表</h3>
        <p>查看待认领、处理中和已结案预警</p>
      </div>

      <div class="card" @click="router.push('/counselor/dashboard')">
        <h3>数据大屏</h3>
        <p>查看班级预警、干预和统计信息</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.welcome-card {
  background: linear-gradient(135deg, #eef8f2, #fff6ef);
  border-radius: 18px;
  padding: 24px;
  margin-bottom: 20px;
}

.welcome-card h2 {
  margin: 0 0 10px 0;
  color: #2f5d50;
}

.welcome-card p {
  margin: 0;
  color: #6f7c76;
  line-height: 1.8;
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
}

.card {
  background: #fff;
  border-radius: 18px;
  padding: 22px;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.05);
  cursor: pointer;
}

.card h3 {
  margin: 0 0 10px 0;
  color: #2f5d50;
}

.card p {
  margin: 0;
  color: #777;
  line-height: 1.7;
}

@media (max-width: 900px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
