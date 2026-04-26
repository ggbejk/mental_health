﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<script setup>
import { ref, onMounted, nextTick, onBeforeUnmount, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import * as echarts from 'echarts'

const route = useRoute()
const router = useRouter()

const resultId = route.query.resultId
const loading = ref(false)
const report = ref(null)
const chartRef = ref(null)

let radarChart = null
let resizeHandler = null

const riskText = computed(() => {
  const level = report.value?.riskLevel
  switch (level) {
    case 'GREEN':
      return '绿色正常'
    case 'YELLOW':
      return '黄色预警'
    case 'ORANGE':
      return '橙色预警'
    case 'RED':
      return '红色预警'
    default:
      return level || '暂无'
  }
})

const riskClass = computed(() => {
  const level = report.value?.riskLevel
  switch (level) {
    case 'GREEN':
      return 'green'
    case 'YELLOW':
      return 'yellow'
    case 'ORANGE':
      return 'orange'
    case 'RED':
      return 'red'
    default:
      return ''
  }
})

const radarMax = computed(() => {
  const indicators = report.value?.radarIndicators || []
  if (indicators.length === 0) return 100
  return Math.max(...indicators.map(item => item.max || 0), 100)
})

const loadReport = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/student/report/detail', {
      params: { resultId }
    })
    console.log('报告数据 =', res.data)
    report.value = res.data
  } catch (error) {
    console.error('获取报告失败', error)
    alert(error?.response?.data?.message || error?.response?.data || '获取报告失败')
  } finally {
    loading.value = false
    await nextTick()
    renderRadar()
  }
}

const renderRadar = () => {
  if (!chartRef.value || !report.value) return

  const indicators = report.value.radarIndicators || []
  const values = report.value.radarValues || []

  if (indicators.length === 0 || values.length === 0) return

  if (radarChart) {
    radarChart.dispose()
  }

  radarChart = echarts.init(chartRef.value)

  radarChart.setOption({
    tooltip: {
      trigger: 'item'
    },
    radar: {
      radius: '62%',
      indicator: indicators,
      splitNumber: 5,
      axisName: {
        color: '#46534f',
        fontSize: 13
      },
      splitArea: {
        areaStyle: {
          color: ['#fafdfb', '#f4faf7']
        }
      },
      splitLine: {
        lineStyle: {
          color: '#dce9e2'
        }
      },
      axisLine: {
        lineStyle: {
          color: '#dce9e2'
        }
      }
    },
    series: [
      {
        type: 'radar',
        data: [
          {
            value: values,
            name: '测评结果',
            areaStyle: {
              opacity: 0.35
            },
            lineStyle: {
              width: 2
            },
            symbol: 'circle',
            symbolSize: 7
          }
        ]
      }
    ]
  })
}

onMounted(() => {
  loadReport()

  resizeHandler = () => {
    if (radarChart) {
      radarChart.resize()
    }
  }

  window.addEventListener('resize', resizeHandler)
})

onBeforeUnmount(() => {
  if (resizeHandler) {
    window.removeEventListener('resize', resizeHandler)
  }
  if (radarChart) {
    radarChart.dispose()
    radarChart = null
  }
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="top-bar">
        <button class="back-btn" @click="router.push('/student/home')">返回首页</button>
        <button class="light-btn" @click="router.push('/student/tasks')">返回任务列表</button>
      </div>

      <div v-if="loading" class="empty">报告加载中...</div>

      <div v-else-if="!report" class="empty">暂无报告数据</div>

      <div v-else>
        <div class="header-card">
          <div>
            <h1 class="title">心理测评报告</h1>
            <p class="sub-title">以下结果用于学生心理状态识别与辅导参考，请结合实际情况理性看待。</p>
          </div>

          <div class="risk-badge" :class="riskClass">
            {{ riskText }}
          </div>
        </div>

        <div class="info-grid">
          <div class="info-card">
            <div class="info-label">学生姓名</div>
            <div class="info-value">{{ report.studentName || '暂无' }}</div>
          </div>

          <div class="info-card">
            <div class="info-label">量表名称</div>
            <div class="info-value">{{ report.scaleName || '暂无' }}</div>
          </div>

          <div class="info-card">
            <div class="info-label">总分</div>
            <div class="info-value highlight">{{ report.totalScore ?? '--' }}</div>
          </div>

          <div class="info-card">
            <div class="info-label">提交时间</div>
            <div class="info-value small">{{ report.submitTime || '暂无' }}</div>
          </div>
        </div>

        <div class="chart-card">
          <div class="section-head">
            <h3>心理维度雷达图</h3>
            <span class="section-tip">展示各维度得分分布情况</span>
          </div>

          <div
            v-if="!report.radarIndicators || report.radarIndicators.length === 0"
            class="empty-small"
          >
            暂无雷达图数据
          </div>

          <div v-else ref="chartRef" class="radar-chart"></div>
        </div>

        <div class="dimension-card" v-if="report.radarIndicators && report.radarIndicators.length > 0">
          <div class="section-head">
            <h3>维度得分明细</h3>
            <span class="section-tip">各维度当前得分</span>
          </div>

          <div class="dimension-grid">
            <div
              class="dimension-item"
              v-for="(item, index) in report.radarIndicators"
              :key="index"
            >
              <div class="dimension-name">{{ item.name }}</div>
              <div class="dimension-score">
                {{ report.radarValues?.[index] ?? 0 }}
                <span>/ {{ item.max }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="text-card">
          <div class="section-head">
            <h3>报告解读</h3>
          </div>
          <p class="text-content">{{ report.summaryText || '暂无解读内容' }}</p>
        </div>

        <div class="text-card">
          <div class="section-head">
            <h3>建议</h3>
          </div>
          <p class="text-content">{{ report.suggestionText || '暂无建议内容' }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  background: #f5f8f7;
  min-height: 100vh;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px 16px 40px;
}

.top-bar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.back-btn,
.light-btn {
  border: none;
  border-radius: 10px;
  padding: 10px 16px;
  cursor: pointer;
  font-size: 14px;
}

.back-btn {
  background: #f3b38a;
  color: white;
}

.light-btn {
  background: #f2eee9;
  color: #666;
}

.header-card,
.info-card,
.chart-card,
.dimension-card,
.text-card {
  background: white;
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.06);
  margin-bottom: 20px;
  border: 1px solid #e8f1ec;
}

.header-card {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  flex-wrap: wrap;
}

.title {
  margin: 0 0 10px 0;
  color: #2f5d50;
  font-size: 32px;
}

.sub-title {
  margin: 0;
  color: #70807a;
  line-height: 1.8;
}

.risk-badge {
  padding: 10px 16px;
  border-radius: 999px;
  font-weight: bold;
  font-size: 14px;
  white-space: nowrap;
}

.green {
  background: #e8f7ef;
  color: #2f8f62;
}

.yellow {
  background: #fff7d9;
  color: #b58a00;
}

.orange {
  background: #ffe9d9;
  color: #d67a1f;
}

.red {
  background: #ffe1e1;
  color: #d13b3b;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.info-label {
  color: #7c8782;
  font-size: 14px;
  margin-bottom: 10px;
}

.info-value {
  color: #33413c;
  font-size: 20px;
  font-weight: 600;
  line-height: 1.6;
}

.info-value.highlight {
  color: #2f8f62;
  font-size: 30px;
  font-weight: bold;
}

.info-value.small {
  font-size: 15px;
  font-weight: 500;
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}

.section-head h3 {
  margin: 0;
  color: #33413c;
}

.section-tip {
  color: #7b8883;
  font-size: 13px;
}

.radar-chart {
  width: 100%;
  height: 430px;
}

.dimension-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}

.dimension-item {
  background: #f8fcfa;
  border: 1px solid #e8f1ec;
  border-radius: 14px;
  padding: 16px;
}

.dimension-name {
  color: #5d6965;
  margin-bottom: 10px;
}

.dimension-score {
  color: #2f5d50;
  font-size: 26px;
  font-weight: bold;
}

.dimension-score span {
  color: #88938f;
  font-size: 14px;
  font-weight: 500;
}

.text-content {
  margin: 0;
  color: #4d5a56;
  line-height: 1.95;
  white-space: pre-wrap;
}

.empty,
.empty-small {
  text-align: center;
  color: #888;
  padding: 40px 0;
}

@media (max-width: 900px) {
  .info-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .dimension-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 640px) {
  .container {
    padding: 18px 12px 32px;
  }

  .title {
    font-size: 26px;
  }

  .info-grid,
  .dimension-grid {
    grid-template-columns: 1fr;
  }

  .radar-chart {
    height: 340px;
  }

  .top-bar {
    flex-direction: column;
  }

  .back-btn,
  .light-btn {
    width: 100%;
  }
}
</style>
