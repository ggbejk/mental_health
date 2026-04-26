﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserInfo, clearLoginInfo } from '../utils/auth'

const router = useRouter()
const userInfo = getUserInfo()
const isTraining = ref(false)
const currentPhase = ref('')
const progress = ref(0)
const breathCount = ref(0)
let timer = null

const trainingTypes = [
  {
    id: 1,
    name: '4-7-8 呼吸法',
    description: '帮助放松和入睡',
    inhale: 4,
    hold: 7,
    exhale: 8,
    cycles: 4,
    icon: '🌙'
  },
  {
    id: 2,
    name: '箱式呼吸',
    description: '减轻焦虑，提升专注',
    inhale: 4,
    hold: 4,
    exhale: 4,
    cycles: 6,
    icon: '📦'
  },
  {
    id: 3,
    name: '放松呼吸',
    description: '缓解紧张情绪',
    inhale: 4,
    hold: 2,
    exhale: 6,
    cycles: 5,
    icon: '🌊'
  },
  {
    id: 4,
    name: '能量呼吸',
    description: '增加活力和专注力',
    inhale: 6,
    hold: 0,
    exhale: 2,
    cycles: 10,
    icon: '⚡'
  }
]

const selectedTraining = ref(null)

const startTraining = (training) => {
  selectedTraining.value = training
  isTraining.value = true
  breathCount.value = 0
  progress.value = 0
  runBreathingCycle(training)
}

const stopTraining = () => {
  isTraining.value = false
  selectedTraining.value = null
  currentPhase.value = ''
  progress.value = 0
  breathCount.value = 0
  if (timer) {
    clearTimeout(timer)
    timer = null
  }
}

const runBreathingCycle = (training) => {
  if (!isTraining.value || breathCount.value >= training.cycles) {
    isTraining.value = false
    currentPhase.value = '完成'
    return
  }

  const phases = [
    { name: '吸气', duration: training.inhale, next: 'hold' },
    { name: '屏息', duration: training.hold, next: 'exhale' },
    { name: '呼气', duration: training.exhale, next: 'inhale' }
  ]

  let phaseIndex = 0
  let totalDuration = 0

  const runPhase = () => {
    if (!isTraining.value) return

    const phase = phases[phaseIndex]
    if (phase.duration === 0) {
      phaseIndex = phaseIndex === 0 ? 2 : (phaseIndex === 1 ? 2 : 0)
      totalDuration += 0
      runPhase()
      return
    }

    currentPhase.value = phase.name
    const startTime = Date.now()

    const updateProgress = () => {
      if (!isTraining.value) return
      const elapsed = Date.now() - startTime
      progress.value = Math.min((elapsed / (phase.duration * 1000)) * 100, 100)
      if (elapsed < phase.duration * 1000) {
        timer = setTimeout(updateProgress, 50)
      } else {
        if (phase.name === '呼气') {
          breathCount.value++
        }
        phaseIndex = phase.next === 'hold' ? 1 : (phase.next === 'exhale' ? 2 : 0)
        totalDuration += phase.duration * 1000
        timer = setTimeout(runPhase, 500)
      }
    }

    updateProgress()
  }

  runPhase()
}

onUnmounted(() => {
  if (timer) {
    clearTimeout(timer)
  }
})

const logout = () => {
  clearLoginInfo()
  router.push('/login')
}
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="page-header">
        <div>
          <h1 class="title">呼吸训练</h1>
          <p class="sub-title">欢迎你，{{ userInfo?.realName || '同学' }}</p>
        </div>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>
      <p class="desc">通过科学的呼吸练习，帮助调节情绪、减轻焦虑、提升专注力的自助工具</p>

      <div v-if="isTraining && selectedTraining" class="training-card">
        <div class="training-header">
          <div class="training-icon">{{ selectedTraining.icon }}</div>
          <div class="training-info">
            <h3>{{ selectedTraining.name }}</h3>
            <p>{{ selectedTraining.description }}</p>
          </div>
        </div>

        <div class="breath-display">
          <div class="breath-circle" :class="{ inhale: currentPhase === '吸气', hold: currentPhase === '屏息', exhale: currentPhase === '呼气' }">
            <span class="phase-text">{{ currentPhase }}</span>
          </div>
        </div>

        <div class="progress-info">
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: progress + '%' }"></div>
          </div>
          <p>第 {{ breathCount + 1 }} / {{ selectedTraining.cycles }} 个周期</p>
        </div>

        <div class="training-stats">
          <div class="stat">
            <span class="stat-label">吸气</span>
            <span class="stat-value">{{ selectedTraining.inhale }}秒</span>
          </div>
          <div class="stat">
            <span class="stat-label">屏息</span>
            <span class="stat-value">{{ selectedTraining.hold }}秒</span>
          </div>
          <div class="stat">
            <span class="stat-label">呼气</span>
            <span class="stat-value">{{ selectedTraining.exhale }}秒</span>
          </div>
        </div>

        <button class="stop-btn" @click="stopTraining">结束训练</button>
      </div>

      <div v-else class="training-grid">
        <div
          v-for="item in trainingTypes"
          :key="item.id"
          class="training-item"
          @click="startTraining(item)"
        >
          <div class="item-icon">{{ item.icon }}</div>
          <div class="item-content">
            <h3>{{ item.name }}</h3>
            <p>{{ item.description }}</p>
            <div class="item-meta">
              <span>吸气{{ item.inhale }}秒</span>
              <span v-if="item.hold > 0">屏息{{ item.hold }}秒</span>
              <span>呼气{{ item.exhale }}秒</span>
            </div>
          </div>
          <button class="start-btn">开始</button>
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
  max-width: 1000px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  flex-wrap: wrap;
}

.title {
  margin: 0;
  color: #2f5d50;
  font-size: 28px;
}

.sub-title {
  margin: 8px 0 0 0;
  color: #6f7c76;
  font-size: 14px;
}

.desc {
  margin: 0 0 20px 0;
  color: #888;
  font-size: 14px;
}

.logout-btn {
  border: none;
  border-radius: 10px;
  padding: 10px 18px;
  background: #f3b38a;
  color: white;
  cursor: pointer;
}

.training-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 24px;
  padding: 32px;
  color: white;
  text-align: center;
}

.training-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 32px;
}

.training-icon {
  font-size: 48px;
}

.training-info h3 {
  margin: 0 0 8px 0;
  font-size: 24px;
}

.training-info p {
  margin: 0;
  opacity: 0.9;
}

.breath-display {
  margin-bottom: 32px;
}

.breath-circle {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  transition: all 0.3s;
}

.breath-circle.inhale {
  transform: scale(1.1);
  background: rgba(255, 255, 255, 0.3);
}

.breath-circle.hold {
  transform: scale(1.1);
  background: rgba(255, 255, 255, 0.25);
}

.breath-circle.exhale {
  transform: scale(1);
  background: rgba(255, 255, 255, 0.2);
}

.phase-text {
  font-size: 28px;
  font-weight: bold;
}

.progress-info {
  margin-bottom: 24px;
}

.progress-bar {
  height: 8px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 12px;
}

.progress-fill {
  height: 100%;
  background: white;
  border-radius: 4px;
  transition: width 0.1s linear;
}

.progress-info p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.training-stats {
  display: flex;
  justify-content: center;
  gap: 32px;
  margin-bottom: 24px;
}

.stat {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-label {
  font-size: 13px;
  opacity: 0.8;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
}

.stop-btn {
  padding: 12px 32px;
  border: none;
  border-radius: 25px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

.stop-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.training-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.training-item {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 16px;
}

.training-item:hover {
  transform: translateX(4px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

.item-icon {
  font-size: 40px;
  flex-shrink: 0;
}

.item-content {
  flex: 1;
}

.item-content h3 {
  margin: 0 0 6px 0;
  color: #2f5d50;
  font-size: 18px;
}

.item-content p {
  margin: 0 0 8px 0;
  color: #777;
  font-size: 14px;
}

.item-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #999;
}

.start-btn {
  padding: 10px 24px;
  border: none;
  border-radius: 20px;
  background: #667eea;
  color: white;
  cursor: pointer;
  font-size: 14px;
  flex-shrink: 0;
}

.start-btn:hover {
  background: #5a6fd6;
}
</style>
