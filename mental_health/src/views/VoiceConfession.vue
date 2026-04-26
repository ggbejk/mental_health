﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { getUserInfo } from '../utils/auth'

const router = useRouter()
const userInfo = getUserInfo()
const studentId = userInfo?.userId

const isRecording = ref(false)
const isPaused = ref(false)
const recordingTime = ref(0)
const audioUrl = ref('')
const audioBlob = ref(null)
const emotionTag = ref('')
const isAnonymous = ref(1)
const submitting = ref(false)
const hasRecording = ref(false)

let mediaRecorder = null
let audioChunks = []
let timer = null
let recordedBlob = null

const tagOptions = ['学习压力', '人际关系', '家庭困扰', '情绪低落', '焦虑紧张', '睡眠问题', '其他']

const startRecording = async () => {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    mediaRecorder = new MediaRecorder(stream)
    audioChunks = []

    mediaRecorder.ondataavailable = (event) => {
      audioChunks.push(event.data)
    }

    mediaRecorder.onstop = () => {
      recordedBlob = new Blob(audioChunks, { type: 'audio/webm' })
      audioUrl.value = URL.createObjectURL(recordedBlob)
      audioBlob.value = recordedBlob
      hasRecording.value = true
      stream.getTracks().forEach(track => track.stop())
    }

    mediaRecorder.start()
    isRecording.value = true
    isPaused.value = false
    recordingTime.value = 0

    timer = setInterval(() => {
      if (!isPaused.value) {
        recordingTime.value++
      }
    }, 1000)
  } catch (error) {
    console.error('无法访问麦克风', error)
    alert('无法访问麦克风，请检查权限设置')
  }
}

const pauseRecording = () => {
  if (mediaRecorder && mediaRecorder.state === 'recording') {
    mediaRecorder.pause()
    isPaused.value = true
  }
}

const resumeRecording = () => {
  if (mediaRecorder && mediaRecorder.state === 'paused') {
    mediaRecorder.resume()
    isPaused.value = false
  }
}

const stopRecording = () => {
  if (mediaRecorder && mediaRecorder.state !== 'inactive') {
    mediaRecorder.stop()
    isRecording.value = false
    isPaused.value = false
    if (timer) {
      clearInterval(timer)
      timer = null
    }
  }
}

const resetRecording = () => {
  if (audioUrl.value) {
    URL.revokeObjectURL(audioUrl.value)
  }
  audioUrl.value = ''
  audioBlob.value = null
  recordedBlob = null
  hasRecording.value = false
  recordingTime.value = 0
}

const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const playAudio = () => {
  if (audioUrl.value) {
    const audio = new Audio(audioUrl.value)
    audio.play()
  }
}

const submitVoiceConfession = async () => {
  if (!audioBlob.value) {
    alert('请先录制语音倾诉')
    return
  }

  if (!emotionTag.value) {
    alert('请选择情绪标签')
    return
  }

  submitting.value = true
  try {
    const formData = new FormData()
    formData.append('studentId', studentId)
    formData.append('emotionTag', emotionTag.value)
    formData.append('isAnonymous', isAnonymous.value)
    formData.append('audioFile', audioBlob.value, 'voice_confession.webm')

    await axios.post('/api/student/confession/voice/save', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    alert('语音倾诉提交成功')
    router.push('/student/confession/my')
  } catch (error) {
    console.error('提交失败', error)
    const msg = error?.response?.data?.message || '提交失败'
    alert(msg)
  } finally {
    submitting.value = false
  }
}

onBeforeUnmount(() => {
  if (timer) {
    clearInterval(timer)
  }
  if (audioUrl.value) {
    URL.revokeObjectURL(audioUrl.value)
  }
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/student/home')">返回</button>
      </div>

      <h1 class="title">语音倾诉</h1>
      <p class="sub-title">用声音传递情绪，老师会用心倾听你的声音</p>

      <div class="card">
        <div class="notice">
          温馨提示：语音倾诉可帮助老师更好地了解你的情绪状态，时长建议1-3分钟。如内容涉及明显安全风险，系统可能自动触发预警。
        </div>

        <div class="form-item">
          <label>情绪标签</label>
          <select v-model="emotionTag">
            <option value="">请选择</option>
            <option v-for="item in tagOptions" :key="item" :value="item">
              {{ item }}
            </option>
          </select>
        </div>

        <div class="recorder-section">
          <div class="recorder-status">
            <div v-if="!isRecording && !hasRecording" class="status-text">点击下方按钮开始录音</div>
            <div v-else-if="isRecording" class="status-text recording">
              <span class="dot"></span>
              {{ isPaused ? '已暂停' : '录音中' }} {{ formatTime(recordingTime) }}
            </div>
            <div v-else-if="hasRecording" class="status-text completed">
              录音完成 {{ formatTime(recordingTime) }}
            </div>
          </div>

          <div class="recorder-controls">
            <template v-if="!isRecording && !hasRecording">
              <button class="record-btn start" @click="startRecording">
                <span class="icon">🎙️</span>
              </button>
              <div class="control-hint">开始录音</div>
            </template>

            <template v-else-if="isRecording">
              <button v-if="!isPaused" class="record-btn pause" @click="pauseRecording">
                <span class="icon">⏸️</span>
              </button>
              <button v-else class="record-btn resume" @click="resumeRecording">
                <span class="icon">▶️</span>
              </button>
              <button class="record-btn stop" @click="stopRecording">
                <span class="icon">⏹️</span>
              </button>
              <div class="control-hint">{{ isPaused ? '继续' : '暂停' }} / 停止</div>
            </template>

            <template v-else-if="hasRecording">
              <button class="record-btn play" @click="playAudio">
                <span class="icon">🔊</span>
              </button>
              <button class="record-btn reset" @click="resetRecording">
                <span class="icon">🔄</span>
              </button>
              <div class="control-hint">播放 / 重录</div>
            </template>
          </div>
        </div>

        <div class="form-item checkbox-row">
          <label class="checkbox-label">
            <input type="checkbox" v-model="isAnonymous" true-value="1" false-value="0" />
            <span>匿名提交</span>
          </label>
        </div>

        <button
          class="submit-btn"
          @click="submitVoiceConfession"
          :disabled="submitting || !hasRecording"
        >
          {{ submitting ? '提交中...' : '提交语音倾诉' }}
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

.form-item select {
  width: 100%;
  border: 1px solid #dfe8e3;
  border-radius: 10px;
  padding: 12px 14px;
  font-size: 14px;
}

.recorder-section {
  background: #f9fafb;
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 22px;
  text-align: center;
}

.recorder-status {
  margin-bottom: 24px;
  min-height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-text {
  font-size: 18px;
  color: #666;
}

.status-text.recording {
  color: #e54d4d;
  font-weight: 600;
}

.status-text.recording .dot {
  display: inline-block;
  width: 12px;
  height: 12px;
  background: #e54d4d;
  border-radius: 50%;
  margin-right: 8px;
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.status-text.completed {
  color: #52c41a;
  font-weight: 600;
}

.recorder-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
}

.record-btn {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  font-size: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.record-btn.start {
  background: linear-gradient(135deg, #e54d4d, #f07373);
  box-shadow: 0 4px 16px rgba(229, 77, 77, 0.4);
}

.record-btn.start:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(229, 77, 77, 0.5);
}

.record-btn.pause {
  background: linear-gradient(135deg, #faad14, #ffc53d);
  box-shadow: 0 4px 16px rgba(250, 173, 20, 0.4);
}

.record-btn.resume {
  background: linear-gradient(135deg, #52c41a, #73d13d);
  box-shadow: 0 4px 16px rgba(82, 196, 26, 0.4);
}

.record-btn.stop {
  background: linear-gradient(135deg, #666, #888);
  box-shadow: 0 4px 16px rgba(102, 102, 102, 0.4);
}

.record-btn.play {
  background: linear-gradient(135deg, #1677ff, #69b1ff);
  box-shadow: 0 4px 16px rgba(22, 119, 255, 0.4);
}

.record-btn.reset {
  background: linear-gradient(135deg, #722ed1, #9254de);
  box-shadow: 0 4px 16px rgba(114, 46, 209, 0.4);
}

.record-btn:hover {
  transform: scale(1.05);
}

.control-hint {
  font-size: 13px;
  color: #999;
  margin-top: 8px;
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
