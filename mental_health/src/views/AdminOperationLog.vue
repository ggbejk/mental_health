﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { getUserInfo, clearLoginInfo } from '../utils/auth'

const router = useRouter()
const userInfo = getUserInfo()

const logs = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const loading = ref(false)

const filters = ref({
  userName: '',
  operationModule: '',
  startTime: '',
  endTime: ''
})

const modules = ref({})
const types = ref({})

const getModules = async () => {
  try {
    const res = await axios.get('/api/admin/operation-log/modules')
    modules.value = res.data
  } catch (error) {
    console.error('获取模块列表失败', error)
  }
}

const getTypes = async () => {
  try {
    const res = await axios.get('/api/admin/operation-log/types')
    types.value = res.data
  } catch (error) {
    console.error('获取类型列表失败', error)
  }
}

const getLogs = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    if (filters.value.userName) params.userName = filters.value.userName
    if (filters.value.operationModule) params.operationModule = filters.value.operationModule
    if (filters.value.startTime) params.startTime = filters.value.startTime
    if (filters.value.endTime) params.endTime = filters.value.endTime

    const res = await axios.get('/api/admin/operation-log/list', { params })
    logs.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取日志失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  getLogs()
}

const handleReset = () => {
  filters.value = {
    userName: '',
    operationModule: '',
    startTime: '',
    endTime: ''
  }
  currentPage.value = 1
  getLogs()
}

const handlePageChange = (page) => {
  currentPage.value = page
  getLogs()
}

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

const getStatusClass = (status) => {
  if (!status) return ''
  if (status.includes('成功')) return 'success'
  if (status.includes('失败')) return 'error'
  return ''
}

const logout = () => {
  clearLoginInfo()
  router.push('/login')
}

onMounted(() => {
  getModules()
  getTypes()
  getLogs()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="header">
        <div>
          <h1>操作日志审计</h1>
          <p>管理员：{{ userInfo?.realName || '管理员' }}</p>
        </div>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <div class="filters">
        <div class="filter-item">
          <label>用户名</label>
          <input v-model="filters.userName" placeholder="请输入用户名" @keyup.enter="handleSearch" />
        </div>
        <div class="filter-item">
          <label>操作模块</label>
          <select v-model="filters.operationModule">
            <option value="">全部</option>
            <option v-for="(name, code) in modules" :key="code" :value="code">{{ name }}</option>
          </select>
        </div>
        <div class="filter-item">
          <label>开始时间</label>
          <input type="datetime-local" v-model="filters.startTime" />
        </div>
        <div class="filter-item">
          <label>结束时间</label>
          <input type="datetime-local" v-model="filters.endTime" />
        </div>
        <div class="filter-actions">
          <button class="search-btn" @click="handleSearch">查询</button>
          <button class="reset-btn" @click="handleReset">重置</button>
        </div>
      </div>

      <div class="table-card">
        <div v-if="loading" class="empty">加载中...</div>
        <div v-else-if="logs.length === 0" class="empty">暂无日志记录</div>
        <table v-else>
          <thead>
            <tr>
              <th>序号</th>
              <th>用户名</th>
              <th>角色</th>
              <th>操作模块</th>
              <th>操作类型</th>
              <th>操作描述</th>
              <th>请求方式</th>
              <th>请求地址</th>
              <th>操作状态</th>
              <th>操作时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(log, index) in logs" :key="log.id">
              <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
              <td>{{ log.userName || '-' }}</td>
              <td>{{ log.userRole || '-' }}</td>
              <td>{{ modules[log.operationModule] || log.operationModule || '-' }}</td>
              <td>{{ types[log.operationType] || log.operationType || '-' }}</td>
              <td>{{ log.operationDesc || '-' }}</td>
              <td>{{ log.requestMethod || '-' }}</td>
              <td class="url-cell">{{ log.requestUrl || '-' }}</td>
              <td :class="getStatusClass(log.operationStatus)">{{ log.operationStatus || '-' }}</td>
              <td>{{ formatTime(log.createTime) }}</td>
            </tr>
          </tbody>
        </table>

        <div v-if="total > 0" class="pagination">
          <span class="total">共 {{ total }} 条</span>
          <div class="page-numbers">
            <button
              v-for="page in Math.ceil(total / pageSize)"
              :key="page"
              :class="{ active: page === currentPage }"
              @click="handlePageChange(page)"
            >
              {{ page }}
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
  max-width: 1400px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header h1 {
  margin: 0 0 8px 0;
  color: #2f5d50;
  font-size: 28px;
}

.header p {
  margin: 0;
  color: #6f7c76;
}

.logout-btn {
  background: #f3b38a;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 10px 18px;
  cursor: pointer;
}

.filters {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.filter-item label {
  font-size: 13px;
  color: #666;
}

.filter-item input,
.filter-item select {
  border: 1px solid #dfe8e3;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  min-width: 160px;
}

.filter-actions {
  display: flex;
  gap: 10px;
}

.search-btn,
.reset-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
}

.search-btn {
  background: #2f8f62;
  color: white;
}

.reset-btn {
  background: #f5f5f5;
  color: #666;
}

.table-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background: #f5f7fa;
}

th, td {
  padding: 14px 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
  font-size: 14px;
}

th {
  color: #666;
  font-weight: 500;
}

td {
  color: #333;
}

.url-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.success {
  color: #52c41a;
}

.error {
  color: #e54d4d;
}

.empty {
  text-align: center;
  color: #888;
  padding: 40px 0;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.total {
  color: #666;
  font-size: 14px;
}

.page-numbers {
  display: flex;
  gap: 6px;
}

.page-numbers button {
  min-width: 36px;
  height: 36px;
  border: 1px solid #dfe8e3;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.page-numbers button.active {
  background: #2f8f62;
  color: white;
  border-color: #2f8f62;
}
</style>
