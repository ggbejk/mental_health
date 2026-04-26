﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted, nextTick, onBeforeUnmount } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'
import { useRouter } from 'vue-router'
import { getUserInfo, clearLoginInfo } from '../utils/auth'

const router = useRouter()
const userInfo = getUserInfo()
const loading = ref(false)
const overview = ref(null)

const warningLevelRef = ref(null)
const statusRef = ref(null)
const trendRef = ref(null)
const riskTagRef = ref(null)

let warningLevelChart = null
let statusChart = null
let trendChart = null
let riskTagChart = null
let resizeHandler = null

const getOverview = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/dashboard/teacher/overview', {
      params: { 
        teacherId: userInfo?.userId, 
        className: userInfo?.className 
      }
    })
    overview.value = res.data
  } catch (error) {
    console.error('获取教师大屏统计失败', error)
    alert('获取教师大屏统计失败')
  } finally {
    loading.value = false
    await nextTick()
    renderCharts()
  }
}

const logout = () => {
  clearLoginInfo()
  router.replace('/login')
}

const renderCharts = () => {
  renderWarningLevelChart()
  renderStatusChart()
  renderTrendChart()
  renderRiskTagChart()
}

const renderWarningLevelChart = () => {
  if (!warningLevelRef.value || !overview.value) return

  const data = overview.value.warningLevelStats || []
  if (data.length === 0) return

  if (warningLevelChart) warningLevelChart.dispose()
  warningLevelChart = echarts.init(warningLevelRef.value)

  warningLevelChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    color: ['#28a745', '#ffc107', '#fd7e14', '#dc3545', '#722ed1'],
    series: [{
      name: '预警等级',
      type: 'pie',
      radius: ['40%', '68%'],
      data
    }]
  })
}

const renderStatusChart = () => {
  if (!statusRef.value || !overview.value) return

  const data = overview.value.warningStatusStats || []
  if (data.length === 0) return

  if (statusChart) statusChart.dispose()
  statusChart = echarts.init(statusRef.value)

  statusChart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c}人'
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.name)
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}'
      },
      minInterval: 1
    },
    series: [{
      data: data.map(item => Math.round(item.value)),
      type: 'bar',
      barWidth: '45%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#0f52ba' },
          { offset: 1, color: '#4d88e8' }
        ])
      }
    }]
  })
}

const renderTrendChart = () => {
  if (!trendRef.value || !overview.value) return

  const data = overview.value.warningTrendStats || []
  if (data.length === 0) return

  if (trendChart) trendChart.dispose()
  trendChart = echarts.init(trendRef.value)

  trendChart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c}人'
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.name)
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}'
      },
      minInterval: 1
    },
    series: [{
      data: data.map(item => Math.round(item.value)),
      type: 'line',
      smooth: true,
      itemStyle: {
        color: '#722ed1'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(114, 46, 209, 0.3)' },
          { offset: 1, color: 'rgba(114, 46, 209, 0.1)' }
        ])
      }
    }]
  })
}

const renderRiskTagChart = () => {
  if (!riskTagRef.value || !overview.value) return

  const data = overview.value.riskTagStats || []
  if (data.length === 0) return

  if (riskTagChart) riskTagChart.dispose()
  riskTagChart = echarts.init(riskTagRef.value)

  // 截断过长的标签，最多显示8个字符
  const truncatedData = data.map(item => ({
    ...item,
    name: item.name.length > 8 ? item.name.substring(0, 8) + '...' : item.name
  }))

  riskTagChart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const originalItem = data[params[0].dataIndex]
        return `${originalItem.name}: ${originalItem.value}次`
      }
    },
    xAxis: {
      type: 'category',
      data: truncatedData.map(item => item.name),
      axisLabel: { 
        rotate: 30, 
        interval: 0,
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}'
      },
      minInterval: 1
    },
    series: [{
      data: truncatedData.map(item => Math.round(item.value)),
      type: 'bar',
      barWidth: '60%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#ff7875' },
          { offset: 1, color: '#ffa39e' }
        ])
      }
    }]
  })
}

onMounted(() => {
  getOverview()

  resizeHandler = () => {
    if (warningLevelChart) warningLevelChart.resize()
    if (statusChart) statusChart.resize()
    if (trendChart) trendChart.resize()
    if (riskTagChart) riskTagChart.resize()
  }
  window.addEventListener('resize', resizeHandler)
})

onBeforeUnmount(() => {
  if (resizeHandler) {
    window.removeEventListener('resize', resizeHandler)
  }
  if (warningLevelChart) warningLevelChart.dispose()
  if (statusChart) statusChart.dispose()
  if (trendChart) trendChart.dispose()
  if (riskTagChart) riskTagChart.dispose()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="page-header">
        <div>
          <h1 class="title">班级心理健康数据大屏</h1>
          <p class="sub-title">欢迎你，{{ userInfo?.realName || '老师' }}</p>
          <p class="class-info">当前班级：{{ userInfo?.className || '未设置' }}</p>
        </div>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <div v-if="loading" class="loading">加载中...</div>

      <div v-else-if="!overview" class="empty">暂无数据</div>

      <div v-else>
        <div class="stats-cards">
          <div class="stat-card">
            <div class="stat-value">{{ overview.totalStudents || 0 }}</div>
            <div class="stat-label">班级学生总数</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ overview.totalSurveyStudents || 0 }}</div>
            <div class="stat-label">已完成测评</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ overview.surveyCoverageRate || 0 }}%</div>
            <div class="stat-label">测评覆盖率</div>
          </div>
          <div class="stat-card">
            <div class="stat-value warning">{{ overview.totalWarnings || 0 }}</div>
            <div class="stat-label">预警学生数</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ overview.totalInterventions || 0 }}</div>
            <div class="stat-label">干预记录数</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ overview.interventionCoverageRate || 0 }}%</div>
            <div class="stat-label">干预覆盖率</div>
          </div>
        </div>

        <div class="charts-grid">
          <div class="chart-card">
            <h3>预警等级分布</h3>
            <div ref="warningLevelRef" class="chart"></div>
          </div>

          <div class="chart-card">
            <h3>预警状态分布</h3>
            <div ref="statusRef" class="chart"></div>
          </div>

          <div class="chart-card">
            <h3>预警趋势</h3>
            <div ref="trendRef" class="chart"></div>
          </div>

          <div class="chart-card">
            <h3>风险标签统计</h3>
            <div ref="riskTagRef" class="chart"></div>
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

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #eef8f2;
}

.title {
  margin: 0;
  color: #2f5d50;
  font-size: 28px;
  font-weight: 600;
}

.sub-title {
  margin: 8px 0 4px 0;
  color: #6f7c76;
  font-size: 14px;
}

.class-info {
  margin: 4px 0 0 0;
  color: #2f5d50;
  font-size: 14px;
  font-weight: 500;
}

.logout-btn {
  background: #ff7875;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.loading, .empty {
  text-align: center;
  padding: 40px;
  color: #999;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border-left: 4px solid #4d88e8;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #2f5d50;
  margin-bottom: 8px;
}

.stat-value.warning {
  color: #ff7875;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}

.stat-label {
  font-size: 14px;
  color: #6f7c76;
  font-weight: 500;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

.chart-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border-top: 4px solid #722ed1;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.chart-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.chart-card h3 {
  margin: 0 0 16px 0;
  color: #2f5d50;
  font-size: 18px;
  font-weight: 600;
  padding-bottom: 8px;
  border-bottom: 1px solid #eef8f2;
}

.chart {
  height: 300px;
  width: 100%;
  margin-top: 12px;
}
</style>
