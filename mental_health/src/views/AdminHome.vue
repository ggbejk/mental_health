﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { useRouter } from 'vue-router'
import { getUserInfo } from '../utils/auth'

const router = useRouter()
const user = getUserInfo()
</script>

<template>
  <div>
    <div class="welcome-card">
      <h2>欢迎，{{ user?.realName || '管理员' }}</h2>
      <p>可在这里查看全局统计、预警分布以及整体运行情况，进行资源配置决策。</p>
    </div>

    <div class="section">
      <h3 class="section-title">管理功能</h3>
      <div class="grid">
        <div class="card" @click="router.push('/dashboard')">
          <h3>数据大屏</h3>
          <p>查看核心指标、预警等级和学院分布</p>
        </div>

        <div class="card" @click="router.push('/counselor/warnings')">
          <h3>预警总览</h3>
          <p>从管理视角查看预警处置情况</p>
        </div>
      </div>
    </div>

    <div class="section">
      <h3 class="section-title">心理中心功能</h3>
      <div class="grid">
        <div class="card purple" @click="router.push('/psychologist/home')">
          <h3>心理中心工作台</h3>
          <p>统筹管理全校心理普查与咨询工作</p>
        </div>

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
      </div>
    </div>
  </div>
</template>

<style scoped>
.welcome-card {
  background: linear-gradient(135deg, #eef8f2, #fff6ef);
  border-radius: 18px;
  padding: 24px;
  margin-bottom: 24px;
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

.section {
  margin-bottom: 24px;
}

.section-title {
  margin: 0 0 16px 0;
  color: #2f5d50;
  font-size: 18px;
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
  border-left: 4px solid #2f8f62;
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
  font-size: 18px;
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
