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
const collegeRef = ref(null)
const statusRef = ref(null)
const trendRef = ref(null)
const riskTagRef = ref(null)
const genderRef = ref(null)
const gradeRef = ref(null)

let warningLevelChart = null
let collegeChart = null
let statusChart = null
let trendChart = null
let riskTagChart = null
let genderChart = null
let gradeChart = null
let resizeHandler = null

const getOverview = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/dashboard/overview')
    console.log('overview =', res.data)
    overview.value = res.data
  } catch (error) {
    console.error('获取大屏统计失败', error)
    alert('获取大屏统计失败')
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
  renderCollegeChart()
  renderStatusChart()
  renderTrendChart()
  renderRiskTagChart()
  renderGenderChart()
  renderGradeChart()
}

// 预警趋势折线图
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

// 预警等级饼图（对应等级颜色）
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

// 学院预警柱状图（蓝色+强制整数显示）
const renderCollegeChart = () => {
  if (!collegeRef.value || !overview.value) return

  const data = overview.value.collegeWarningStats || []
  if (data.length === 0) return

  if (collegeChart) collegeChart.dispose()
  collegeChart = echarts.init(collegeRef.value)

  collegeChart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c}人'
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.name),
      axisLabel: { rotate: 20 }
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
          { offset: 0, color: '#1677ff' },
          { offset: 1, color: '#69b1ff' }
        ])
      }
    }]
  })
}

// 预警状态柱状图（蓝色+强制整数显示）
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

// 风险标签柱状图
const renderRiskTagChart = () => {
  if (!riskTagRef.value || !overview.value) return

  const data = overview.value.riskTagStats || []
  if (data.length === 0) return

  if (riskTagChart) riskTagChart.dispose()
  riskTagChart = echarts.init(riskTagRef.value)

  riskTagChart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c}次'
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.name),
      axisLabel: { rotate: 30, interval: 0 }
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

// 性别分布饼图
const renderGenderChart = () => {
  if (!genderRef.value || !overview.value) return

  const data = overview.value.genderWarningStats || []
  if (data.length === 0) return

  if (genderChart) genderChart.dispose()
  genderChart = echarts.init(genderRef.value)

  genderChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    color: ['#1677ff', '#eb2f96', '#52c41a'],
    series: [{
      name: '性别分布',
      type: 'pie',
      radius: ['40%', '68%'],
      data
    }]
  })
}

// 年级分布柱状图
const renderGradeChart = () => {
  if (!gradeRef.value || !overview.value) return

  const data = overview.value.gradeWarningStats || []
  if (data.length === 0) return

  if (gradeChart) gradeChart.dispose()
  gradeChart = echarts.init(gradeRef.value)

  gradeChart.setOption({
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
      barWidth: '50%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#52c41a' },
          { offset: 1, color: '#95de64' }
        ])
      }
    }]
  })
}

onMounted(() => {
  getOverview()

  resizeHandler = () => {
    warningLevelChart && warningLevelChart.resize()
    collegeChart && collegeChart.resize()
    statusChart && statusChart.resize()
    trendChart && trendChart.resize()
    riskTagChart && riskTagChart.resize()
    genderChart && genderChart.resize()
    gradeChart && gradeChart.resize()
  }

  window.addEventListener('resize', resizeHandler)
})

onBeforeUnmount(() => {
  if (resizeHandler) {
    window.removeEventListener('resize', resizeHandler)
  }
  if (warningLevelChart) warningLevelChart.dispose()
  if (collegeChart) collegeChart.dispose()
  if (statusChart) statusChart.dispose()
  if (trendChart) trendChart.dispose()
  if (riskTagChart) riskTagChart.dispose()
  if (genderChart) genderChart.dispose()
  if (gradeChart) gradeChart.dispose()
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="header">
        <div>
          <h1>{{ userInfo?.role === 'ADMIN' ? '高校学生心理健康管理大屏' : '高校学生心理健康数据大屏' }}</h1>
          <p>欢迎你，{{ userInfo?.realName || '管理员' }}｜{{ userInfo?.role === 'ADMIN' ? '全局统计与管理视角' : '所带班级心理健康概览' }}</p>
        </div>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>

      <div v-if="loading" class="empty">数据加载中...</div>

      <div v-else-if="!overview" class="empty">暂无统计数据</div>

      <div v-else>
        <div class="card-grid">
          <div class="data-card">
            <div class="label">学生总人数</div>
            <div class="value">{{ overview.totalStudents }}</div>
          </div>

          <div class="data-card">
            <div class="label">测评总人数</div>
            <div class="value">{{ overview.totalSurveyStudents }}</div>
          </div>

          <div class="data-card">
            <div class="label">普查覆盖率</div>
            <div class="value">{{ overview.surveyCoverageRate }}%</div>
          </div>

          <div class="data-card warning">
            <div class="label">预警总人数</div>
            <div class="value">{{ overview.totalWarnings }}</div>
          </div>

          <div class="data-card success">
            <div class="label">干预总人数</div>
            <div class="value">{{ overview.totalInterventions }}</div>
          </div>

          <div class="data-card success">
            <div class="label">干预覆盖率</div>
            <div class="value">{{ overview.interventionCoverageRate }}%</div>
          </div>
        </div>

        <div class="chart-grid">
          <div class="chart-card">
            <h3>预警等级分布</h3>
            <div ref="warningLevelRef" class="chart"></div>
          </div>

          <div class="chart-card">
            <h3>预警状态分布</h3>
            <div ref="statusRef" class="chart"></div>
          </div>
        </div>

        <div class="chart-grid">
          <div class="chart-card">
            <h3>预警趋势分析</h3>
            <div ref="trendRef" class="chart"></div>
          </div>

          <div class="chart-card">
            <h3>风险标签统计</h3>
            <div ref="riskTagRef" class="chart"></div>
          </div>
        </div>

        <div class="chart-grid">
          <div class="chart-card">
            <h3>性别分布</h3>
            <div ref="genderRef" class="chart"></div>
          </div>

          <div class="chart-card">
            <h3>年级分布</h3>
            <div ref="gradeRef" class="chart"></div>
          </div>
        </div>

        <div v-if="userInfo?.role === 'ADMIN'" class="chart-card full">
          <h3>学院预警分布</h3>
          <div ref="collegeRef" class="chart large"></div>
        </div>

        <div v-else class="chart-card full">
          <h3>所带班级预警分布</h3>
          <div ref="collegeRef" class="chart large"></div>
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
  margin-bottom: 24px;
}

.header h1 {
  margin: 0 0 8px 0;
  color: #2f5d50;
  font-size: 36px;
}

.header p {
  margin: 0;
  color: #6f7c76;
  font-size: 15px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 18px;
  margin-bottom: 24px;
}

.data-card {
  background: #ffffff;
  border-radius: 18px;
  padding: 22px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
  border: 1px solid #e8f1ec;
}

.data-card.warning {
  border-color: #ffe7b3;
  background: #fffbe6;
}

.data-card.success {
  border-color: #d9f7be;
  background: #f6ffed;
}

.label {
  color: #7c8782;
  margin-bottom: 12px;
  font-size: 14px;
}

.value {
  font-size: 34px;
  font-weight: bold;
  color: #2f8f62;
}

.data-card.warning .value {
  color: #d4870c;
}

.data-card.success .value {
  color: #389e0d;
}

.chart-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
  margin-bottom: 24px;
}

.chart-card {
  background: #ffffff;
  border-radius: 18px;
  padding: 20px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
  border: 1px solid #e8f1ec;
}

.chart-card h3 {
  margin: 0 0 14px 0;
  color: #a77e0f;
}

.chart {
  width: 100%;
  height: 360px;
}

.large {
  height: 420px;
}

.full {
  width: 100%;
  margin-bottom: 24px;
}

.empty {
  text-align: center;
  color: #888;
  padding: 60px 0;
}

@media (max-width: 1100px) {
  .card-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .chart-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 700px) {
  .card-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .page {
    padding: 16px;
  }

  .header h1 {
    font-size: 28px;
  }

  .chart,
  .large {
    height: 300px;
  }
}
.header {
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  flex-wrap: wrap;
}

.logout-btn {
  border: none;
  border-radius: 10px;
  padding: 10px 18px;
  background: #f3b38a;
  color: white;
  cursor: pointer;
}
</style>
