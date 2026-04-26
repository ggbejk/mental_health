﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { useRouter } from 'vue-router'
import { getUserInfo } from '../utils/auth'

const router = useRouter()
const user = getUserInfo()
</script>

<template>
  <div>
    <div class="welcome-card psychologist">
      <h2>欢迎，{{ user?.realName || '咨询师' }}</h2>
      <p>统筹管理全校心理普查任务，对预警学生进行风险评估与分级复核，线上完成咨询预约与个案分配。</p>
    </div>

    <div class="grid">
      <div class="card blue" @click="router.push('/psychologist/warnings')">
        <h3>风险评估</h3>
        <p>对预警学生进行风险评估与分级复核</p>
      </div>

      <div class="card green" @click="router.push('/psychologist/consultation')">
        <h3>咨询预约</h3>
        <p>线上完成咨询预约与个案分配</p>
      </div>

      <div class="card orange" @click="router.push('/psychologist/surveys')">
        <h3>普查管理</h3>
        <p>统筹管理全校心理普查任务</p>
      </div>

      <div class="card purple" @click="router.push('/psychologist/scales')">
        <h3>量表定制</h3>
        <p>自定义测评量表配置</p>
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

.welcome-card.psychologist {
  background: linear-gradient(135deg, #f0e6ff, #e6f0ff);
}

.welcome-card h2 {
  margin: 0 0 10px 0;
  color: #2f5d50;
}

.welcome-card.psychologist h2 {
  color: #4a3f8a;
}

.welcome-card p {
  margin: 0;
  color: #6f7c76;
  line-height: 1.8;
}

.welcome-card.psychologist p {
  color: #666;
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
  border-left: 4px solid transparent;
}

.card.blue {
  border-left-color: #1677ff;
}

.card.green {
  border-left-color: #52c41a;
}

.card.orange {
  border-left-color: #fa8c16;
}

.card.purple {
  border-left-color: #722ed1;
}

.card h3 {
  margin: 0 0 10px 0;
  color: #2f5d50;
}

.card.blue h3 {
  color: #1677ff;
}

.card.green h3 {
  color: #52c41a;
}

.card.orange h3 {
  color: #fa8c16;
}

.card.purple h3 {
  color: #722ed1;
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
