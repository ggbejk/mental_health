﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUserInfo, clearLoginInfo } from '../utils/auth'

const route = useRoute()
const router = useRouter()
const user = getUserInfo()

const roleTextMap = {
  STUDENT: '学生',
  COUNSELOR: '辅导员',
  ADMIN: '管理员'
}

const menuMap = {
  STUDENT: [
    { title: '学生首页', path: '/student/home', icon: '🏠' },
    { title: '心理测评任务', path: '/student/tasks', icon: '📝' },
    { title: '情绪日记', path: '/student/emotion/write', icon: '😊' },
    { title: '情绪曲线', path: '/student/emotion/trend', icon: '📈' },
    { title: '匿名倾诉', path: '/student/confession/write', icon: '💬' },
    { title: '我的倾诉记录', path: '/student/confession/my', icon: '📚' },
    { title: '正念冥想', path: '/student/mindfulness', icon: '🧘' },
    { title: '呼吸训练', path: '/student/breathing', icon: '🌬️' },
    { title: 'AI心理问答', path: '/student/ai-chat', icon: '🤖' }
  ],
  COUNSELOR: [
    { title: '工作台', path: '/counselor/home', icon: '🏠' },
    { title: '预警列表', path: '/counselor/warnings', icon: '🚨' },
    { title: '数据大屏', path: '/counselor/dashboard', icon: '📊' },
    { title: '干预记录', path: '/counselor/interventions', icon: '📝' },
    { title: '学生档案', path: '/counselor/students', icon: '👥' }
  ],
  ADMIN: [
    { title: '管理员首页', path: '/admin/home', icon: '🏠' },
    { title: '管理大屏', path: '/dashboard', icon: '📊' },
    { title: '预警总览', path: '/counselor/warnings', icon: '🚨' },
    { title: '心理中心', path: '/psychologist/home', icon: '🧠' },
    { title: '风险评估', path: '/psychologist/warnings', icon: '📋' },
    { title: '咨询预约', path: '/psychologist/consultation', icon: '📅' },
    { title: '普查管理', path: '/psychologist/surveys', icon: '📊' },
    { title: '量表定制', path: '/psychologist/scales', icon: '📝' }
  ]
}

const menus = computed(() => menuMap[user?.role] || [])

const pageTitle = computed(() => {
  const current = menus.value.find(item => route.path.startsWith(item.path))
  return current?.title || '心理健康平台'
})

const roleText = computed(() => roleTextMap[user?.role] || '用户')

const handleLogout = () => {
  clearLoginInfo()
  router.push('/login')
}

const goMenu = (path) => {
  router.push(path)
}
</script>

<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="logo-box">
        <h2>心理健康平台</h2>
        <p>{{ roleText }}端</p>
      </div>

      <div class="menu-list">
        <div
          v-for="item in menus"
          :key="item.path"
          class="menu-item"
          :class="{ active: route.path.startsWith(item.path) }"
          @click="goMenu(item.path)"
        >
          <span class="icon">{{ item.icon }}</span>
          <span>{{ item.title }}</span>
        </div>
      </div>
    </aside>

    <div class="main-wrap">
      <header class="topbar">
        <div>
          <h1>{{ pageTitle }}</h1>
          <p>欢迎回来，{{ user?.realName || '用户' }}</p>
        </div>

        <div class="user-box">
          <div class="user-info">
            <div class="avatar">{{ (user?.realName || 'U').slice(0, 1) }}</div>
            <div>
              <div class="name">{{ user?.realName || '用户' }}</div>
              <div class="role">{{ roleText }}</div>
            </div>
          </div>
          <button class="logout-btn" @click="handleLogout">退出登录</button>
        </div>
      </header>

      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  background: #f5f8f7;
}

.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #2f5d50 0%, #3b7362 100%);
  color: #fff;
  padding: 24px 18px;
  box-sizing: border-box;
  flex-shrink: 0;
}

.logo-box {
  padding: 8px 8px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
  margin-bottom: 20px;
}

.logo-box h2 {
  margin: 0 0 8px 0;
  font-size: 22px;
}

.logo-box p {
  margin: 0;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.menu-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  border-radius: 12px;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.88);
  transition: 0.2s;
}

.menu-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.menu-item.active {
  background: rgba(255, 255, 255, 0.18);
  color: #fff;
}

.icon {
  width: 22px;
  text-align: center;
}

.main-wrap {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.topbar {
  height: 84px;
  background: #fff;
  border-bottom: 1px solid #e8efeb;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-sizing: border-box;
  gap: 16px;
  flex-wrap: wrap;
}

.topbar h1 {
  margin: 0 0 6px 0;
  font-size: 24px;
  color: #2f5d50;
}

.topbar p {
  margin: 0;
  color: #7a8781;
  font-size: 14px;
}

.user-box {
  display: flex;
  align-items: center;
  gap: 14px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: #e7f3ec;
  color: #2f8f62;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.name {
  color: #34433d;
  font-weight: 600;
}

.role {
  font-size: 13px;
  color: #7a8781;
}

.logout-btn {
  border: none;
  background: #f4efe9;
  color: #666;
  border-radius: 10px;
  padding: 10px 14px;
  cursor: pointer;
}

.content {
  padding: 24px;
  box-sizing: border-box;
  flex: 1;
}

@media (max-width: 900px) {
  .layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    padding: 18px 14px;
  }

  .menu-list {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .menu-item {
    flex: 1 1 160px;
  }

  .topbar {
    height: auto;
    padding: 18px 16px;
  }

  .content {
    padding: 16px;
  }
}
</style>
