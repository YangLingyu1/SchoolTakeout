<template>
  <div class="income-statistics">
    <div class="date-filter">
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        @change="handleDateChange"
      />
      <el-button type="primary" @click="loadStatistics" :icon="'R'">
        刷新
      </el-button>
    </div>

    <div class="stats-cards">
      <el-card v-for="item in statsItems" :key="item.key" class="stat-card">
        <div class="stat-content">
          <div class="stat-info">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </div>
        </div>
      </el-card>
    </div>

    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span>收入趋势</span>
          <el-radio-group v-model="timeRange" @change="loadStatistics">
            <el-radio-button label="day">日</el-radio-button>
            <el-radio-button label="week">周</el-radio-button>
            <el-radio-button label="month">月</el-radio-button>
            <el-radio-button label="year">年</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div ref="incomeChartRef" class="chart"></div>
    </el-card>

    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span>商品销量排行</span>
        </div>
      </template>
      <div ref="salesChartRef" class="chart"></div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import type { ECharts } from 'echarts'
import dayjs from 'dayjs'

const loading = ref<boolean>(false)
const dateRange = ref<[Date, Date]>([
  dayjs().subtract(30, 'day').toDate(),
  dayjs().toDate()
])
const timeRange = ref<string>('day')

const statsData = ref({
  totalOrders: 0,
  completedOrders: 0,
  totalAmount: 0,
  averageOrderAmount: 0
})

const incomeChartRef = ref<HTMLElement>()
const salesChartRef = ref<HTMLElement>()
let incomeChart: ECharts | null = null
let salesChart: ECharts | null = null

const statsItems = computed(() => [
  {
    key: 'totalOrders',
    label: '总订单数',
    value: statsData.value.totalOrders,
    icon: 'ShoppingCart',
    color: '#409eff'
  },
  {
    key: 'completedOrders',
    label: '已完成订单',
    value: statsData.value.completedOrders,
    icon: 'CircleCheck',
    color: '#67c23a'
  },
  {
    key: 'totalAmount',
    label: '总收入',
    value: `¥${statsData.value.totalAmount.toFixed(2)}`,
    icon: 'Money',
    color: '#e6a23c'
  },
  {
    key: 'averageOrderAmount',
    label: '平均订单金额',
    value: `¥${statsData.value.averageOrderAmount.toFixed(2)}`,
    icon: 'TrendCharts',
    color: '#909399'
  }
])

const loadStatistics = async () => {
  loading.value = true
  try {
    const startDate = dayjs(dateRange.value[0]).format('YYYY-MM-DD')
    const endDate = dayjs(dateRange.value[1]).format('YYYY-MM-DD')
    
    const response = await fetch(`/api/admin/orders/statistics?startDate=${startDate}&endDate=${endDate}`)
    const data = await response.json()
    
    if (data.code === 200) {
      statsData.value = data.data
      initCharts()
    }
  } catch (err: any) {
    ElMessage.error('获取统计数据失败')
  } finally {
    loading.value = false
  }
}

const handleDateChange = () => {
  loadStatistics()
}

const initCharts = () => {
  initIncomeChart()
  initSalesChart()
}

const initIncomeChart = () => {
  if (!incomeChartRef.value) return

  incomeChart = echarts.init(incomeChartRef.value)
  
  const option = {
    title: {
      text: '收入趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value',
      name: '金额（元）'
    },
    series: [
      {
        name: '收入',
        type: 'line',
        data: [1200, 1500, 1800, 2100, 1900, 2500, 2800],
        smooth: true,
        areaStyle: {
          opacity: 0.3
        }
      }
    ]
  }

  incomeChart.setOption(option)
}

const initSalesChart = () => {
  if (!salesChartRef.value) return

  salesChart = echarts.init(salesChartRef.value)
  
  const option = {
    title: {
      text: '商品销量排行',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: ['商品A', '商品B', '商品C', '商品D', '商品E']
    },
    series: [
      {
        name: '销量',
        type: 'bar',
        data: [150, 120, 100, 80, 60]
      }
    ]
  }

  salesChart.setOption(option)
}

const handleResize = () => {
  incomeChart?.resize()
  salesChart?.resize()
}

onMounted(() => {
  loadStatistics()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  incomeChart?.dispose()
  salesChart?.dispose()
})
</script>

<style scoped>
.income-statistics {
  padding: 20px 0;
}

.date-filter {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.chart-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
}

.chart {
  width: 100%;
  height: 400px;
}
</style>