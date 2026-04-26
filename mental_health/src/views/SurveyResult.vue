<script setup>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'
import * as echarts from 'echarts'

const route = useRoute()
const router = useRouter()

const taskId = route.query.taskId
const studentId = route.query.studentId

const result = ref(null)
const loading = ref(false)
const radarRef = ref(null)
let radarChart = null

const getResultDetail = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/student/result/detail', {
      params: {
        taskId,
        studentId
      }
    })
    result.value = res.data

    await nextTick()
    renderRadarChart()
  } catch (error) {
    console.error('获取结果详情失败', error)
    alert('获取结果详情失败')
  } finally {
    loading.value = false
  }
}

const getRiskText = (riskLevel) => {
  switch (riskLevel) {
    case 'NORMAL':
      return '正常'
    case 'YELLOW':
      return '黄色预警'
    case 'ORANGE':
      return '橙色预警'
    case 'RED':
      return '红色预警'
    default:
      return riskLevel
  }
}

const getRiskClass = (riskLevel) => {
  switch (riskLevel) {
    case 'NORMAL':
      return 'normal'
    case 'YELLOW':
      return 'yellow'
    case 'ORANGE':
      return 'orange'
    case 'RED':
      return 'red'
    default:
      return ''
  }
}

const renderRadarChart = () => {
  if (!radarRef.value || !result.value?.dimensionScores?.length) return

  if (radarChart) {
    radarChart.dispose()
  }

  radarChart = echarts.init(radarRef.value)

  const scores = result.value.dimensionScores
  const maxScore = Math.max(...scores.map(item => item.score), 10)

  const option = {
    tooltip: {
      trigger: 'item'
    },
    radar: {
      radius: '60%',
      indicator: scores.map(item => ({
        name: item.dimension,
        max: maxScore
      })),
      splitArea: {
        areaStyle: {
          color: ['#f8fcfa', '#f1f9f5']
        }
      },
      axisName: {
        color: '#4b5b55',
        fontSize: 14
      }
    },
    series: [
      {
        type: 'radar',
        data: [
          {
            value: scores.map(item => item.score),
            name: '维度得分',
            areaStyle: {
              color: 'rgba(134, 201, 168, 0.35)'
            },
            lineStyle: {
              color: '#86c9a8',
              width: 2
            },
            itemStyle: {
              color: '#f3b38a'
            }
          }
        ]
      }
    ]
  }

  radarChart.setOption(option)
}

onMounted(() => {
  getResultDetail()

  window.addEventListener('resize', () => {
    radarChart && radarChart.resize()
  })
})
</script>

<template>
  <div class="page">
    <div class="container">
      <h1 class="title">测评结果报告</h1>
      <p class="sub-title">以下结果仅作为心理健康自查参考</p>

      <div v-if="loading" class="empty">结果加载中...</div>

      <div v-else-if="!result" class="empty">暂无结果数据</div>

      <div v-else class="result-card">
        <div class="top-summary">
          <div class="score-box">
            <div class="label">总分</div>
            <div class="score">{{ result.totalScore }}</div>
          </div>

          <div class="risk-box" :class="getRiskClass(result.riskLevel)">
            {{ getRiskText(result.riskLevel) }}
          </div>
        </div>

        <div class="report-box">
          <h3>结果解读</h3>
          <p>{{ result.reportText }}</p>
        </div>

        <div class="chart-card">
          <h3>心理维度雷达图</h3>
          <div ref="radarRef" class="radar-chart"></div>
        </div>

        <div class="dimension-box">
          <h3>维度得分明细</h3>
          <div class="dimension-list">
            <div
              class="dimension-item"
              v-for="item in result.dimensionScores"
              :key="item.dimension"
            >
              <span class="dimension-name">{{ item.dimension }}</span>
              <span class="dimension-score">{{ item.score }}</span>
            </div>
          </div>
        </div>

        <div class="tips-box">
          <h3>温馨提示</h3>
          <ul>
            <li>保持规律作息和饮食习惯。</li>
            <li>适当运动，维持稳定情绪状态。</li>
            <li>遇到压力时及时与同学、辅导员或老师沟通。</li>
            <li>如长期情绪低落或焦虑，建议联系学校心理中心。</li>
          </ul>
        </div>

        <div class="btn-group">
          <button class="btn green" @click="router.push('/student/tasks')">返回任务列表</button>
          <button class="btn orange" @click="router.push(`/student/task/${result.taskId}`)">重新查看问卷</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(to bottom, #eef8f2, #fff8f4);
  padding: 40px 16px;
}

.container {
  max-width: 920px;
  margin: 0 auto;
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

.result-card {
  background: white;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.06);
}

.top-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 28px;
}

.score-box {
  text-align: center;
}

.label {
  color: #888;
  margin-bottom: 8px;
}

.score {
  font-size: 48px;
  font-weight: bold;
  color: #2f5d50;
}

.risk-box {
  font-size: 18px;
  font-weight: bold;
  padding: 14px 28px;
  border-radius: 12px;
}

.normal {
  background: #e5f7ec;
  color: #2d8b5c;
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

.report-box,
.chart-card,
.dimension-box,
.tips-box {
  margin-bottom: 28px;
}

.report-box h3,
.chart-card h3,
.dimension-box h3,
.tips-box h3 {
  color: #444;
  margin-bottom: 14px;
}

.report-box p,
.tips-box li {
  color: #555;
  line-height: 1.8;
}

.radar-chart {
  width: 100%;
  height: 420px;
  background: #fcfffd;
  border-radius: 16px;
}

.dimension-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
}

.dimension-item {
  background: #f8fcfa;
  border: 1px solid #e6f1eb;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dimension-name {
  color: #4b5b55;
}

.dimension-score {
  font-size: 20px;
  font-weight: bold;
  color: #2f8f62;
}

.btn-group {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn {
  border: none;
  border-radius: 10px;
  padding: 12px 28px;
  font-size: 15px;
  cursor: pointer;
  color: white;
}

.green {
  background: #86c9a8;
}

.orange {
  background: #f3b38a;
}

.empty {
  text-align: center;
  color: #888;
  padding: 60px 0;
}

@media (max-width: 768px) {
  .top-summary {
    flex-direction: column;
    align-items: center;
  }

  .radar-chart {
    height: 320px;
  }
}
</style>
