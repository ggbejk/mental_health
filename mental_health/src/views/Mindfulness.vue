﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { getUserInfo, clearLoginInfo } from '../utils/auth'

const router = useRouter()
const userInfo = getUserInfo()
const isPlaying = ref(false)
const currentAudio = ref(null)
const currentMeditation = ref(null)
const audioElement = ref(null)

const meditations = [
  {
    id: 1,
    title: '清晨冥想',
    description: '帮助您开启美好的一天，提升专注力',
    duration: '10分钟',
    category: '清晨',
    image: '🌅',
    audio: { frequency: 396, type: 'sine', pattern: 'gradual' }
  },
  {
    id: 2,
    title: '减压放松',
    description: '释放压力，恢复身心平衡',
    duration: '15分钟',
    category: '减压',
    image: '🌿',
    audio: { frequency: 528, type: 'sine', pattern: 'pulse' }
  },
  {
    id: 3,
    title: '深度睡眠',
    description: '引导您进入深度放松，改善睡眠质量',
    duration: '20分钟',
    category: '睡眠',
    image: '🌙',
    audio: { frequency: 174, type: 'triangle', pattern: 'slow' }
  },
  {
    id: 4,
    title: '情绪调节',
    description: '平衡情绪，提升心理韧性',
    duration: '12分钟',
    category: '情绪',
    image: '🌈',
    audio: { frequency: 639, type: 'sine', pattern: 'gentle' }
  },
  {
    id: 5,
    title: '专注力训练',
    description: '提升注意力和专注力',
    duration: '8分钟',
    category: '专注',
    image: '🎯',
    audio: { frequency: 432, type: 'square', pattern: 'steady' }
  },
  {
    id: 6,
    title: '自我接纳',
    description: '培养自我关爱与接纳的能力',
    duration: '15分钟',
    category: '成长',
    image: '💜',
    audio: { frequency: 963, type: 'sine', pattern: 'pure' }
  }
]

const selectedCategory = ref('全部')

const categories = ['全部', '清晨', '减压', '睡眠', '情绪', '专注', '成长']

const filteredMeditations = ref(meditations)

const filterByCategory = (category) => {
  selectedCategory.value = category
  if (category === '全部') {
    filteredMeditations.value = meditations
  } else {
    filteredMeditations.value = meditations.filter(m => m.category === category)
  }
}

const startMeditation = (meditation) => {
  if (audioElement.value) {
    audioElement.value.pause()
    audioElement.value.currentTime = 0
  }
  
  currentMeditation.value = meditation
  isPlaying.value = true
  
  let audioConfig = meditation.audio || { frequency: 432, type: 'sine', pattern: 'gradual' }
  
  try {
    const audioContext = new (window.AudioContext || window.webkitAudioContext)()
    const oscillator = audioContext.createOscillator()
    const gainNode = audioContext.createGain()
    
    oscillator.connect(gainNode)
    gainNode.connect(audioContext.destination)
    
    oscillator.type = audioConfig.type || 'sine'
    oscillator.frequency.setValueAtTime(audioConfig.frequency || 432, audioContext.currentTime)
    
    gainNode.gain.setValueAtTime(0.3, audioContext.currentTime)
    
    if (audioConfig.pattern === 'gradual') {
      oscillator.frequency.exponentialRampToValueAtTime(audioConfig.frequency * 0.5, audioContext.currentTime + 2)
    } else if (audioConfig.pattern === 'pulse') {
      let pulseTime = audioContext.currentTime
      for (let i = 0; i < 4; i++) {
        gainNode.gain.setValueAtTime(0.3, pulseTime)
        gainNode.gain.exponentialRampToValueAtTime(0.01, pulseTime + 0.5)
        pulseTime += 1
      }
    } else if (audioConfig.pattern === 'slow') {
      oscillator.frequency.exponentialRampToValueAtTime(audioConfig.frequency * 0.8, audioContext.currentTime + 3)
    } else if (audioConfig.pattern === 'gentle') {
      oscillator.frequency.setValueAtTime(audioConfig.frequency * 0.9, audioContext.currentTime)
      oscillator.frequency.exponentialRampToValueAtTime(audioConfig.frequency * 1.1, audioContext.currentTime + 2)
    } else if (audioConfig.pattern === 'steady') {
      oscillator.frequency.setValueAtTime(audioConfig.frequency, audioContext.currentTime)
    } else if (audioConfig.pattern === 'pure') {
      oscillator.frequency.setValueAtTime(audioConfig.frequency, audioContext.currentTime)
      gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 2)
    }
    
    gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 3)
    
    oscillator.start(audioContext.currentTime)
    oscillator.stop(audioContext.currentTime + 3)
  } catch (error) {
    console.error('音频初始化失败:', error)
  }
  
  let durationMs = 10000
  if (meditation.duration.includes('15分钟')) {
    durationMs = 15 * 60 * 1000
  } else if (meditation.duration.includes('20分钟')) {
    durationMs = 20 * 60 * 1000
  } else if (meditation.duration.includes('8分钟')) {
    durationMs = 8 * 60 * 1000
  } else if (meditation.duration.includes('12分钟')) {
    durationMs = 12 * 60 * 1000
  }
  
  setTimeout(() => {
    stopMeditation()
  }, durationMs)
}

const stopMeditation = () => {
  if (audioElement.value) {
    audioElement.value.pause()
    audioElement.value.currentTime = 0
  }
  isPlaying.value = false
  currentMeditation.value = null
}

const logout = () => {
  clearLoginInfo()
  router.push('/login')
}

onBeforeUnmount(() => {
  // 组件卸载时停止音频
  if (audioElement.value) {
    audioElement.value.pause()
  }
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="page-header">
        <div>
          <h1 class="title">正念冥想</h1>
          <p class="sub-title">欢迎你，{{ userInfo?.realName || '同学' }}</p>
        </div>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>
      <p class="desc">通过冥想练习，培养专注力，减轻压力，提升心理健康的轻量化音频课程</p>

      <div class="category-filter">
        <button
          v-for="cat in categories"
          :key="cat"
          class="category-btn"
          :class="{ active: selectedCategory === cat }"
          @click="filterByCategory(cat)"
        >
          {{ cat }}
        </button>
      </div>

      <div v-if="isPlaying && currentMeditation" class="player-card">
        <div class="player-content">
          <div class="player-icon">{{ currentMeditation.image }}</div>
          <div class="player-info">
            <h3>{{ currentMeditation.title }}</h3>
            <p>{{ currentMeditation.description }}</p>
            <p class="duration">{{ currentMeditation.duration }}</p>
          </div>
        </div>
        <div class="player-controls">
          <div class="wave-animation">
            <span></span><span></span><span></span><span></span><span></span>
          </div>
          <button class="stop-btn" @click="stopMeditation">停止</button>
        </div>
        <p class="player-tip">冥想音频播放中，请保持舒适的姿势，专注于呼吸...</p>
      </div>

      <div class="meditation-grid">
        <div
          v-for="item in filteredMeditations"
          :key="item.id"
          class="meditation-card"
          @click="startMeditation(item)"
        >
          <div class="card-icon">{{ item.image }}</div>
          <div class="card-content">
            <h3>{{ item.title }}</h3>
            <p>{{ item.description }}</p>
            <div class="card-meta">
              <span class="duration">{{ item.duration }}</span>
              <span class="category-tag">{{ item.category }}</span>
            </div>
          </div>
          <button class="play-btn">开始</button>
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
  max-width: 1200px;
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

.category-filter {
  display: flex;
  gap: 10px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.category-btn {
  padding: 8px 16px;
  border: 1px solid #e8e8e8;
  border-radius: 20px;
  background: white;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.category-btn:hover {
  border-color: #2f8f62;
  color: #2f8f62;
}

.category-btn.active {
  background: #2f8f62;
  border-color: #2f8f62;
  color: white;
}

.player-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 24px;
  color: white;
}

.player-content {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.player-icon {
  font-size: 48px;
}

.player-info h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
}

.player-info p {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

.player-info .duration {
  margin-top: 4px;
  display: block;
  opacity: 0.8;
}

.player-controls {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 16px;
}

.wave-animation {
  display: flex;
  align-items: center;
  gap: 4px;
  height: 40px;
}

.wave-animation span {
  width: 4px;
  background: white;
  border-radius: 2px;
  animation: wave 1s ease-in-out infinite;
}

.wave-animation span:nth-child(1) { animation-delay: 0s; }
.wave-animation span:nth-child(2) { animation-delay: 0.1s; }
.wave-animation span:nth-child(3) { animation-delay: 0.2s; }
.wave-animation span:nth-child(4) { animation-delay: 0.3s; }
.wave-animation span:nth-child(5) { animation-delay: 0.4s; }

@keyframes wave {
  0%, 100% { height: 10px; }
  50% { height: 40px; }
}

.stop-btn {
  padding: 10px 24px;
  border: none;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  cursor: pointer;
  font-size: 14px;
}

.player-tip {
  margin: 0;
  font-size: 13px;
  opacity: 0.8;
}

.meditation-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
}

.meditation-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.meditation-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-icon {
  font-size: 40px;
  margin-bottom: 12px;
}

.card-content h3 {
  margin: 0 0 8px 0;
  color: #2f5d50;
  font-size: 18px;
}

.card-content p {
  margin: 0 0 12px 0;
  color: #777;
  font-size: 14px;
  line-height: 1.6;
}

.card-meta {
  display: flex;
  gap: 10px;
  align-items: center;
}

.duration {
  font-size: 13px;
  color: #999;
}

.category-tag {
  padding: 4px 10px;
  background: #f0f5f3;
  color: #2f8f62;
  border-radius: 12px;
  font-size: 12px;
}

.play-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  padding: 8px 16px;
  border: none;
  border-radius: 20px;
  background: #2f8f62;
  color: white;
  cursor: pointer;
  font-size: 13px;
}

.play-btn:hover {
  background: #3ba873;
}

@media (max-width: 1000px) {
  .meditation-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 700px) {
  .meditation-grid {
    grid-template-columns: 1fr;
  }
}
</style>
