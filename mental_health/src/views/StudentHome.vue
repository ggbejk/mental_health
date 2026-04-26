﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { useRouter } from 'vue-router'
import { getUserInfo } from '../utils/auth'

const router = useRouter()
const user = getUserInfo()
</script>

<template>
  <div class="page">
    <div class="welcome-card">
      <h2>你好，{{ user?.realName || '同学' }}</h2>
      <p>今天也要记得关注自己的情绪状态，按时完成测评和记录。</p>
    </div>

    <div class="section">
      <h3 class="section-title">心理健康服务</h3>
      <div class="grid">
        <div class="card" @click="router.push('/student/tasks')">
          <h3>心理测评任务</h3>
          <p>查看普查任务并完成量表填写</p>
        </div>

        <div class="card" @click="router.push('/student/emotion/write')">
          <h3>情绪日记</h3>
          <p>记录今天的情绪和触发事件</p>
        </div>

        <div class="card" @click="router.push('/student/emotion/trend')">
          <h3>情绪曲线</h3>
          <p>查看近 7 天情绪变化趋势</p>
        </div>

        <div class="card" @click="router.push('/student/confession/write')">
          <h3>匿名倾诉</h3>
          <p>匿名表达压力和烦恼</p>
        </div>

        <div class="card" @click="router.push('/student/confession/voice')">
          <h3>语音倾诉</h3>
          <p>用声音传递情绪，老师用心听</p>
        </div>

        <div class="card" @click="router.push('/student/confession/my')">
          <h3>我的倾诉记录</h3>
          <p>回看自己的倾诉内容</p>
        </div>

        <div class="card green" @click="router.push('/student/consultation/book')">
          <h3>咨询预约</h3>
          <p>预约心理咨询服务</p>
        </div>
      </div>
    </div>

    <div class="section">
      <h3 class="section-title">自助成长工具</h3>
      <div class="grid">
        <div class="card purple" @click="router.push('/student/mindfulness')">
          <h3>正念冥想</h3>
          <p>轻量化音频课程，培养专注力</p>
        </div>

        <div class="card blue" @click="router.push('/student/breathing')">
          <h3>呼吸训练</h3>
          <p>科学呼吸练习，调节情绪</p>
        </div>

        <div class="card orange" @click="router.push('/student/ai-chat')">
          <h3>AI心理问答</h3>
          <p>智能对话助手，随时倾听陪伴</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  background: transparent;
}

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
  font-size: 16px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
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

.card.orange {
  border-left-color: #fa8c16;
}

.card.purple {
  border-left-color: #722ed1;
}

.card.green {
  border-left-color: #52c41a;
}

.card h3 {
  margin: 0 0 10px 0;
  color: #2f5d50;
  font-size: 16px;
}

.card.blue h3 {
  color: #1677ff;
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
  font-size: 14px;
}

@media (max-width: 900px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
