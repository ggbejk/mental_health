﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted, nextTick, onBeforeUnmount } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { getUserInfo } from '../utils/auth'

const router = useRouter()
const userInfo = getUserInfo()
const studentId = userInfo?.userId || userInfo?.id

const loading = ref(false)
const trendList = ref([])
const diaryList = ref([])
const chartRef = ref(null)

let trendChart = null
let resizeHandler = null

const getTrendData = async () => {
  loading.value = true
  try {
    const [trendRes, listRes] = await Promise.all([
      axios.get('/api/student/emotion/trend', {
        params: { studentId }
      }),
      axios.get('/api/student/emotion/list', {
        params: { studentId }
      })
    ])

    console.log('trendRes.data =', trendRes.data)
    console.log('listRes.data =', listRes.data)

    trendList.value = trendRes.data || []
    diaryList.value = listRes.data || []
  } catch (error) {
    console.error('获取情绪数据失败', error)
    alert('获取情绪数据失败')
  } finally {
    loading.value = false
    await nextTick()
    renderChart()
  }
}

const renderChart = () => {
  if (!chartRef.value || trendList.value.length === 0) return

  if (trendChart) {
    trendChart.dispose()
  }

  trendChart = echarts.init(chartRef.value)

  trendChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: 40,
      right: 20,
      top: 30,
      bottom: 30
    },
    xAxis: {
      type: 'category',
      data: trendList.value.map(item => item.date)
    },
    yAxis: {
      type: 'value',
      min: 1,
      max: 5,
      interval: 1
    },
    series: [
      {
        name: '情绪分值',
        data: trendList.value.map(item => item.moodScore),
        type: 'line',
        smooth: true,
        areaStyle: {},
        symbol: 'circle',
        symbolSize: 8
      }
    ]
  })
}

const getMoodText = (score, label) => {
  if (label) return `${label}（${score}分）`
  return `${score}分`
}

onMounted(() => {
  getTrendData()

  resizeHandler = () => {
    if (trendChart) {
      trendChart.resize()
    }
  }

  window.addEventListener('resize', resizeHandler)
})

onBeforeUnmount(() => {
  if (resizeHandler) {
    window.removeEventListener('resize', resizeHandler)
  }
  if (trendChart) {
    trendChart.dispose()
    trendChart = null
  }
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/student/home')">返回</button>
        <button class="write-btn" @click="router.push('/student/emotion/write')">写今日情绪日记</button>
      </div>

      <h1 class="title">我的情绪曲线</h1>
      <p class="sub-title">查看最近一段时间的情绪变化，识别波动趋势</p>

      <div v-if="loading" class="empty">加载中...</div>

      <div v-else>
        <div class="chart-card">
          <h3>近7天情绪趋势</h3>
          <div v-if="trendList.length === 0" class="empty-small">暂无趋势数据</div>
          <div v-else ref="chartRef" class="chart"></div>
        </div>

        <div class="list-card">
          <h3>历史情绪日记</h3>

          <div v-if="diaryList.length === 0" class="empty-small">
            还没有情绪日记记录，先去写一篇吧
          </div>

          <div v-else class="diary-list">
            <div class="diary-item" v-for="item in diaryList" :key="item.id">
              <div class="diary-top">
                <span class="mood-tag">{{ getMoodText(item.moodScore, item.moodLabel) }}</span>
                <span class="time">{{ item.createTime }}</span>
              </div>

              <p v-if="item.triggerEvent" class="trigger">
                触发事件：{{ item.triggerEvent }}
              </p>

              <p v-if="item.diaryText" class="content">
                {{ item.diaryText }}
              </p>
            </div>
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
  max-width: 980px;
  margin: 0 auto;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.back-btn,
.write-btn {
  border: none;
  border-radius: 8px;
  padding: 10px 16px;
  cursor: pointer;
}

.back-btn {
  background: #f2eee9;
  color: #666;
}

.write-btn {
  background: #86c9a8;
  color: white;
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

.chart-card,
.list-card {
  background: white;
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.06);
  margin-bottom: 22px;
}

.chart-card h3,
.list-card h3 {
  margin: 0 0 14px 0;
  color: #2f5d50;
}

.chart {
  width: 100%;
  height: 380px;
}

.diary-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.diary-item {
  background: #f8fcfa;
  border: 1px solid #e8f1ec;
  border-radius: 14px;
  padding: 16px;
}

.diary-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 10px;
}

.mood-tag {
  background: #eef7f2;
  color: #2f8f62;
  padding: 4px 10px;
  border-radius: 14px;
  font-size: 13px;
}

.time,
.trigger {
  color: #777;
  font-size: 14px;
}

.content {
  color: #555;
  line-height: 1.8;
  margin: 8px 0 0 0;
}

.empty,
.empty-small {
  text-align: center;
  color: #888;
  padding: 28px 0;
}

@media (max-width: 768px) {
  .chart {
    height: 300px;
  }
}
</style>
