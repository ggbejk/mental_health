﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { getUserInfo, clearLoginInfo } from '../utils/auth'

const router = useRouter()
const userInfo = getUserInfo()
const studentId = userInfo?.userId
const messages = ref([])
const inputText = ref('')
const isTyping = ref(false)
const chatContainer = ref(null)
const riskWarning = ref(false)
const currentEmotion = ref('neutral')

const riskKeywords = ['自杀', '自残', '想死', '结束生命', '无意义', '绝望', '无用', '活不下去', '不想活']

const emotionKeywords = {
  anxiety: {
    keywords: ['焦虑', '紧张', '担心', '害怕', '恐惧', '不安', '压力', '心慌', '头晕', '失眠'],
    emotion: '焦虑'
  },
  sad: {
    keywords: ['难过', '伤心', '痛苦', '悲伤', '抑郁', '低落', '绝望', '哭泣', '流泪', '失落'],
    emotion: '低落'
  },
  angry: {
    keywords: ['生气', '愤怒', '烦躁', '恼火', '不满', '怨恨', '发火', '脾气', '激动'],
    emotion: '愤怒'
  },
  lonely: {
    keywords: ['孤独', '寂寞', '孤单', '没人', '不理解', '疏远', '隔离', '独自', '没人陪'],
    emotion: '孤独'
  },
  confused: {
    keywords: ['迷茫', '困惑', '不知所措', '不知道', '怎么办', '如何', '选择', '纠结'],
    emotion: '迷茫'
  }
}

const emotionResponses = {
  anxiety: [
    '听起来你现在感到很焦虑。焦虑是一种常见的情绪体验，你可以尝试深呼吸：吸气4秒，屏住呼吸4秒，呼气6秒。重复几次，看看感觉如何。',
    '焦虑常常与不确定性有关。你愿意和我聊聊是什么让你感到焦虑吗？有时候把担心的事情说出来可以帮助我们更好地理解它们。',
    '面对压力时，试着把注意力放在当下。可以试试54321 grounding技术：说出5样你看到的、4样你触摸的、3样你听到的、2样你闻到的、1样你尝到的。',
    '我能感觉到你现在的紧张状态。记住，焦虑感通常是暂时的，即使现在感觉很强烈，它也会过去。你之前有没有什么方法帮助你缓解焦虑？'
  ],
  sad: [
    '我很遗憾你正在经历这些。悲伤和失落是每个人都会有的感受，你不需要强迫自己开心。给自己一些时间和空间去感受这些情绪。',
    '听起来你正在经历一段很艰难的时期。当我们感到悲伤时，有时候最难的就是承认自己的感受。你愿意多说一点吗？',
    '悲伤是人类最真实的情感之一。感到难过并不意味着你软弱——相反，能够承认自己的脆弱是一种力量。你最近有好好照顾自己吗？',
    '我在这里倾听你。你的感受是完全合理的。面对失去或挫折时，悲伤是很自然的反应。你有没有想过是什么触发了这些感受？'
  ],
  angry: [
    '愤怒是一种很有力量的情感，它往往在保护我们某些重要的东西。你愿意告诉我是什么让你感到愤怒吗？',
    '听起来你正在经历强烈的情绪。当我们感到愤怒时，有时候需要先让自己冷静下来。试试慢慢深呼吸，然后试着用言语描述你的感受。',
    '愤怒背后常常隐藏着其他情绪，比如受伤、失望或恐惧。你愿意探索一下你愤怒背后的感受吗？',
    '我理解这些事情可能让你很恼火。愤怒本身没有对错，重要的是我们如何表达它。你觉得怎样表达愤怒会让你感觉好一些？'
  ],
  lonely: [
    '孤独感是一种很痛苦的体验，即使我们周围有人，有时候也可能感到孤单。你最近有没有和朋友或家人联系过？',
    '感到不被理解是很令人沮丧的。我在这里倾听你，你并不孤单。你愿意和我聊聊是什么让你感到孤独吗？',
    '人际关系中的距离有时会让我们感到失落。这种感觉是暂时的，你愿意说说最近发生了什么事情吗？',
    '孤独并不意味着你有问题。有时候即使在人群中，我们也可能感到孤独。你有没有尝试过和身边的人分享你的感受？'
  ],
  confused: {
    keywords: ['迷茫', '困惑', '不知所措', '不知道', '怎么办', '如何', '选择', '纠结'],
    responses: [
      '生活中的选择有时确实让人感到困惑。你愿意多说说是什么让你难以决定吗？有时候把选项写下来可以帮助我们理清思路。',
      '迷茫是成长的一部分，它意味着你在思考重要的东西。你觉得在目前的情况下，最重要的是什么？',
      '面对不确定时，试着把注意力放在你能控制的事情上，而不是担忧那些你无法改变的。你现在最想解决的问题是什么？',
      '做决定确实不容易。你有没有问过自己：五年后回看，你会希望自己怎么选择？有时候长期视角可以帮助我们做出更清晰的决定。'
    ]
  },
  neutral: [
    '我理解你现在的感受。生活中确实会有困难的时刻，你愿意和我聊聊是什么让你感到困扰吗？',
    '谢谢你愿意分享。你提到的这些感受是很重要的，我在这里倾听。请继续说下去。',
    '听起来你正在经历一段艰难的时期。记住，无论你现在感觉如何，总有人关心你，也总有办法改善现状。',
    '你的感受是完全正常的。很多人在生活中都会遇到类似的挑战。你觉得自己可以尝试哪些方式来缓解压力呢？',
    '我很高兴你愿意表达自己的感受。记住，寻求帮助是一种勇敢的表现。如果你感觉难以应对，可以考虑找专业的心理咨询师聊聊。',
    '你能和我分享这些，说明你正在积极面对自己的情绪。这本身就是很重要的一步。你希望我提供一些放松技巧吗？'
  ]
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

const checkRiskContent = (text) => {
  const lowerText = text.toLowerCase()
  return riskKeywords.some(keyword => lowerText.includes(keyword))
}

const detectEmotion = (text) => {
  for (const [key, value] of Object.entries(emotionKeywords)) {
    if (value.keywords && value.keywords.some(keyword => text.includes(keyword))) {
      return key
    }
  }
  return 'neutral'
}

const getResponseByEmotion = (emotion) => {
  const responses = emotionResponses[emotion]
  if (responses && responses.length > 0) {
    return responses[Math.floor(Math.random() * responses.length)]
  }
  return emotionResponses.neutral[Math.floor(Math.random() * emotionResponses.neutral.length)]
}

const saveChatMessage = async (message) => {
  try {
    await axios.post('/api/student/ai-chat/save', {
      studentId,
      content: message.text,
      emotionTag: currentEmotion.value,
      sourceType: message.type === 'user' ? '用户' : 'AI'
    })
  } catch (error) {
    console.error('保存聊天记录失败', error)
  }
}

const sendMessage = async () => {
  if (!inputText.value.trim()) return

  const userMessage = {
    type: 'user',
    text: inputText.value,
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  messages.value.push(userMessage)
  await saveChatMessage(userMessage)

  if (checkRiskContent(inputText.value)) {
    riskWarning.value = true
    const warningMessage = {
      type: 'system',
      text: '⚠️ 系统检测到你的表达中包含需要关注的关键词。我们非常重视你的安全，已经通知专业心理咨询师关注你的情况。如果你需要立即帮助，请拨打心理援助热线：400-161-9995',
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    }
    messages.value.push(warningMessage)
    await saveChatMessage(warningMessage)
  }

  const detectedEmotion = detectEmotion(inputText.value)
  if (detectedEmotion !== 'neutral') {
    currentEmotion.value = emotionKeywords[detectedEmotion].emotion
  }

  const currentInput = inputText.value
  inputText.value = ''
  isTyping.value = true
  scrollToBottom()

  setTimeout(async () => {
    isTyping.value = false
    const aiResponse = {
      type: 'ai',
      text: getResponseByEmotion(detectedEmotion),
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    }
    messages.value.push(aiResponse)
    await saveChatMessage(aiResponse)
    scrollToBottom()
  }, 1500)
}

const closeWarning = () => {
  riskWarning.value = false
}

const logout = () => {
  clearLoginInfo()
  router.push('/login')
}

onMounted(async () => {
  try {
    const res = await axios.get('/api/student/ai-chat/history', {
      params: { studentId }
    })
    if (res.data && res.data.length > 0) {
      messages.value = res.data.map(item => ({
        type: item.sourceType === '用户' ? 'user' : 'ai',
        text: item.content,
        time: new Date(item.createTime).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
      }))
    }
  } catch (error) {
    console.error('获取聊天历史失败', error)
  }

  if (messages.value.length === 0) {
    const welcomeMessage = {
      type: 'ai',
      text: '你好！我是AI心理助手，基于讯飞星火大模型训练。我可以陪你聊聊心事，提供情绪支持。如果你正在经历困难时期，请记住，你不是一个人。有什么想聊的吗？',
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    }
    messages.value.push(welcomeMessage)
  }
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="page-header">
        <div>
          <h1 class="title">AI心理问答</h1>
          <p class="sub-title">欢迎你，{{ userInfo?.realName || '同学' }}</p>
        </div>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <div v-if="riskWarning" class="risk-alert">
        <div class="alert-icon">⚠️</div>
        <div class="alert-content">
          <h3>人工预警已触发</h3>
          <p>我们检测到你的表达中包含需要关注的关键词。专业心理咨询师已经收到通知并会尽快与你联系。</p>
          <p class="hotline">心理援助热线：<strong>400-161-9995</strong></p>
        </div>
        <button class="close-btn" @click="closeWarning">×</button>
      </div>

      <div class="ai-card">
        <div class="ai-header">
          <div class="ai-avatar">🤖</div>
          <div class="ai-info">
            <h3>AI心理助手</h3>
            <p>基于讯飞星火大模型微调 · 24小时在线</p>
          </div>
          <div class="ai-status">
            <span class="status-dot"></span>
            在线
          </div>
        </div>

        <div class="chat-container" ref="chatContainer">
          <div
            v-for="(msg, index) in messages"
            :key="index"
            class="message"
            :class="msg.type"
          >
            <div v-if="msg.type === 'ai' || msg.type === 'system'" class="avatar">🤖</div>
            <div class="message-content">
              <div class="message-text" :class="{ 'system-message': msg.type === 'system' }">{{ msg.text }}</div>
              <div class="message-time">{{ msg.time }}</div>
            </div>
            <div v-if="msg.type === 'user'" class="avatar">👤</div>
          </div>

          <div v-if="isTyping" class="message ai">
            <div class="avatar">🤖</div>
            <div class="message-content">
              <div class="typing-indicator">
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>
        </div>

        <div class="input-area">
          <input
            v-model="inputText"
            type="text"
            placeholder="输入你的问题或感受..."
            @keyup.enter="sendMessage"
          />
          <button @click="sendMessage" :disabled="!inputText.trim()">发送</button>
        </div>

        <div class="ai-footer">
          <p>AI助手可以提供情感支持和一般性建议，但不能替代专业心理咨询。如果你正在经历严重心理困扰，请寻求专业帮助。</p>
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
  max-width: 900px;
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

.logout-btn {
  border: none;
  border-radius: 10px;
  padding: 10px 18px;
  background: #f3b38a;
  color: white;
  cursor: pointer;
}

.risk-alert {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  color: white;
  display: flex;
  align-items: flex-start;
  gap: 16px;
  position: relative;
}

.alert-icon {
  font-size: 32px;
}

.alert-content {
  flex: 1;
}

.alert-content h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
}

.alert-content p {
  margin: 0 0 8px 0;
  font-size: 14px;
  opacity: 0.95;
}

.hotline {
  font-size: 16px !important;
  font-weight: bold;
}

.close-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(255, 255, 255, 0.3);
  border: none;
  color: white;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 18px;
}

.ai-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.ai-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px 24px;
  display: flex;
  align-items: center;
  gap: 14px;
  color: white;
}

.ai-avatar {
  font-size: 40px;
}

.ai-info {
  flex: 1;
}

.ai-info h3 {
  margin: 0 0 4px 0;
  font-size: 18px;
}

.ai-info p {
  margin: 0;
  font-size: 13px;
  opacity: 0.9;
}

.ai-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #52c41a;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.chat-container {
  height: 450px;
  overflow-y: auto;
  padding: 24px;
  background: #f5f7fa;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.message.user {
  flex-direction: row-reverse;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.message.ai .avatar {
  background: #667eea;
}

.message.user .avatar {
  background: #2f8f62;
}

.message.system .avatar {
  background: #faad14;
}

.message-content {
  max-width: 70%;
}

.message-text {
  background: white;
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.7;
  color: #333;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.message.user .message-text {
  background: #2f8f62;
  color: white;
}

.message.system .message-text {
  background: #fffbe6;
  color: #ad6800;
  border: 1px solid #ffe58f;
}

.message-time {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
  padding: 0 8px;
}

.message.user .message-time {
  text-align: right;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: white;
  border-radius: 16px;
  width: fit-content;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #667eea;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 100% { transform: translateY(0); opacity: 0.4; }
  50% { transform: translateY(-6px); opacity: 1; }
}

.input-area {
  padding: 16px 24px;
  background: white;
  display: flex;
  gap: 12px;
  border-top: 1px solid #eee;
}

.input-area input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #e8e8e8;
  border-radius: 25px;
  font-size: 14px;
  outline: none;
}

.input-area input:focus {
  border-color: #667eea;
}

.input-area button {
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 14px;
  cursor: pointer;
}

.input-area button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.ai-footer {
  padding: 12px 24px;
  background: #fafafa;
  border-top: 1px solid #eee;
}

.ai-footer p {
  margin: 0;
  font-size: 12px;
  color: #999;
  line-height: 1.6;
}
</style>
