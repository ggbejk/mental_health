﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { getUserInfo, clearLoginInfo } from '../utils/auth'

const router = useRouter()
const userInfo = getUserInfo()
const loading = ref(false)
const scales = ref([
  { id: 1, name: 'SCL-90症状自评量表', description: '评估学生心理健康状态', status: 'ACTIVE', usageCount: 156 },
  { id: 2, name: 'SDS抑郁自评量表', description: '筛查学生抑郁倾向', status: 'ACTIVE', usageCount: 89 },
  { id: 3, name: 'SAS焦虑自评量表', description: '评估学生焦虑水平', status: 'ACTIVE', usageCount: 78 },
  { id: 4, name: 'UCLA孤独量表', description: '评估学生孤独感', status: 'INACTIVE', usageCount: 23 }
])

const getScales = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/psychologist/scales')
    if (res.data && res.data.length > 0) {
      scales.value = res.data
    }
  } catch (error) {
    console.error('获取量表失败', error)
  } finally {
    loading.value = false
  }
}

const createScale = () => {
  router.push('/psychologist/scale/create')
}

const editScale = (scaleId) => {
  router.push(`/psychologist/scale/${scaleId}/edit`)
}

const viewScaleDetails = (scaleId) => {
  router.push(`/psychologist/scale/${scaleId}`)
}

const logout = () => {
  clearLoginInfo()
  router.push('/login')
}

onMounted(() => {
  getScales()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="page-header">
        <div>
          <h1 class="title">量表定制</h1>
          <p class="sub-title">欢迎你，{{ userInfo?.realName || '咨询师' }}</p>
        </div>
        <div class="header-actions">
          <button class="btn primary" @click="createScale">创建量表</button>
          <button class="logout-btn" @click="logout">退出登录</button>
        </div>
      </div>
      <p class="sub-title">自定义测评量表配置</p>

      <div v-if="loading" class="empty">加载中...</div>

      <div v-else class="scale-list">
        <div class="scale-card" v-for="item in scales" :key="item.id">
          <div class="card-top">
            <div>
              <h2>{{ item.name }}</h2>
              <p class="scale-description">{{ item.description }}</p>
            </div>
            <div class="status-tag" :class="item.status === 'ACTIVE' ? 'active' : 'inactive'">
              {{ item.status === 'ACTIVE' ? '启用' : '停用' }}
            </div>
          </div>

          <div class="content">
            <p><strong>使用次数：</strong>{{ item.usageCount }} 次</p>
            <p><strong>题目数量：</strong>{{ item.questionCount || '待设置' }} 题</p>
            <p><strong>更新时间：</strong>{{ item.updateTime || '2026-04-01' }}</p>
          </div>

          <div class="btn-row">
            <button
              class="btn blue"
              @click="viewScaleDetails(item.id)"
            >
              预览
            </button>
            <button
              class="btn gray"
              @click="editScale(item.id)"
            >
              编辑
            </button>
          </div>
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

.header-actions {
  display: flex;
  gap: 12px;
}

.logout-btn {
  border: none;
  border-radius: 10px;
  padding: 10px 18px;
  background: #f3b38a;
  color: white;
  cursor: pointer;
}

.scale-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
}

.scale-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.card-top h2 {
  margin: 0;
  color: #2f5d50;
  font-size: 18px;
}

.scale-description {
  margin: 5px 0 0 0;
  color: #777;
  line-height: 1.6;
}

.status-tag {
  padding: 6px 14px;
  border-radius: 18px;
  font-size: 14px;
  font-weight: 500;
}

.status-tag.active {
  background: #f6ffed;
  color: #52c41a;
}

.status-tag.inactive {
  background: #f5f5f5;
  color: #999;
}

.content p {
  margin: 8px 0;
  color: #555;
  line-height: 1.6;
}

.content strong {
  color: #333;
}

.btn-row {
  margin-top: 16px;
  display: flex;
  gap: 12px;
}

.btn {
  border: none;
  border-radius: 10px;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn.primary {
  background: #722ed1;
  color: white;
}

.btn.primary:hover {
  background: #9b59b6;
}

.btn.blue {
  background: #1677ff;
  color: white;
}

.btn.blue:hover {
  background: #4096ff;
}

.btn.gray {
  background: #f5f5f5;
  color: #666;
}

.btn.gray:hover {
  background: #e8e8e8;
}

.empty {
  text-align: center;
  color: #888;
  padding: 60px 0;
}

@media (max-width: 900px) {
  .scale-list {
    grid-template-columns: 1fr;
  }
}
</style>
