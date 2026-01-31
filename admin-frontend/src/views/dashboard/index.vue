<template>
  <div class="dashboard">
    <h2 class="page-title">仪表盘</h2>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <el-card v-for="item in statsItems" :key="item.key" class="stat-card">
        <div class="stat-content">
          <div class="stat-icon" :style="{ backgroundColor: item.color + '20' }">
            <el-icon :size="24" :color="item.color">{{ item.icon }}</el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <!-- 订单状态分布 -->
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>订单状态分布</span>
          </div>
        </template>
        <div ref="orderStatusChartRef" class="chart"></div>
      </el-card>

      <!-- 商品分类分布 -->
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>商品分类分布</span>
          </div>
        </template>
        <div ref="productCategoryChartRef" class="chart"></div>
      </el-card>
    </div>

    <!-- 最近订单 -->
    <el-card class="recent-orders-card">
      <template #header>
        <div class="card-header">
          <span>最近订单</span>
          <el-button type="primary" size="small" @click="viewAllOrders">
            查看全部
          </el-button>
        </div>
      </template>
      <el-table :data="recentOrders" style="width: 100%">
        <el-table-column prop="id" label="订单号" width="100" />
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="totalAmount" label="金额" width="100">
          <template #default="scope">
            ¥{{ scope.row.totalAmount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small" 
              @click="viewOrderDetail(scope.row.id)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import type { ECharts } from 'echarts'
import { dashboardApi } from '../../api/dashboard'
import { orderApi } from '../../api/orders'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref<boolean>(false)
const error = ref<string>('')

// 统计数据
const statsData = ref({
  userCount: 0,
  productCount: 0,
  orderCount: 0,
  riderCount: 0,
  pendingRiderApplications: 0,
  pendingOrders: 0
})

// 最近订单
const recentOrders = ref<any[]>([])

// 图表引用
const orderStatusChartRef = ref<HTMLElement>()
const productCategoryChartRef = ref<HTMLElement>()
let orderStatusChart: ECharts | null = null
let productCategoryChart: ECharts | null = null

// 统计卡片配置
const statsItems = computed(() => [
  {
    key: 'userCount',
    label: '用户数量',
    value: statsData.value.userCount,
    icon: 'U',
    color: '#409eff'
  },
  {
    key: 'productCount',
    label: '商品数量',
    value: statsData.value.productCount,
    icon: 'P',
    color: '#67c23a'
  },
  {
    key: 'orderCount',
    label: '订单数量',
    value: statsData.value.orderCount,
    icon: 'O',
    color: '#e6a23c'
  },
  {
    key: 'riderCount',
    label: '骑手数量',
    value: statsData.value.riderCount,
    icon: 'R',
    color: '#909399'
  },
  {
    key: 'pendingRiderApplications',
    label: '待审核骑手',
    value: statsData.value.pendingRiderApplications,
    icon: 'A',
    color: '#f56c6c'
  },
  {
    key: 'pendingOrders',
    label: '待处理订单',
    value: statsData.value.pendingOrders,
    icon: 'T',
    color: '#409eff'
  }
])

// 获取Dashboard数据
const getDashboardData = async () => {
  loading.value = true
  error.value = ''

  try {
    const response = await dashboardApi.getDashboardData()
    statsData.value = response.data
  } catch (err: any) {
    error.value = err.response?.data?.message || '获取数据失败'
    ElMessage.error(error.value)
  } finally {
    loading.value = false
  }
}

// 获取最近订单
const getRecentOrders = async () => {
  try {
    const response = await orderApi.getOrders()
    // 只显示最近5个订单
    recentOrders.value = response.data.slice(0, 5)
  } catch (err: any) {
    console.error('获取最近订单失败:', err)
  }
}

// 初始化订单状态图表
const initOrderStatusChart = () => {
  if (!orderStatusChartRef.value) return

  orderStatusChart = echarts.init(orderStatusChartRef.value)
  
  const option = {
    title: {
      text: '订单状态分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '订单状态',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 30, name: '待处理' },
          { value: 20, name: '处理中' },
          { value: 40, name: '已完成' },
          { value: 10, name: '已取消' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }

  orderStatusChart.setOption(option)
}

// 初始化商品分类图表
const initProductCategoryChart = () => {
  if (!productCategoryChartRef.value) return

  productCategoryChart = echarts.init(productCategoryChartRef.value)
  
  const option = {
    title: {
      text: '商品分类分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '商品分类',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 40, name: '餐饮' },
          { value: 25, name: '零食' },
          { value: 20, name: '饮料' },
          { value: 15, name: '其他' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }

  productCategoryChart.setOption(option)
}

// 处理窗口 resize
const handleResize = () => {
  orderStatusChart?.resize()
  productCategoryChart?.resize()
}

// 获取订单状态类型
const getStatusType = (status: number): string => {
  const statusMap: Record<number, string> = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取订单状态文本
const getStatusText = (status: number): string => {
  const statusMap: Record<number, string> = {
    0: '待处理',
    1: '处理中',
    2: '已完成',
    3: '已取消'
  }
  return statusMap[status] || '未知状态'
}

// 格式化日期
const formatDate = (dateString: string): string => {
  return dayjs(dateString).format('YYYY-MM-DD HH:mm')
}

// 查看全部订单
const viewAllOrders = () => {
  router.push('/orders')
}

// 查看订单详情
const viewOrderDetail = (orderId: number) => {
  router.push(`/orders/detail/${orderId}`)
}

// 组件挂载
onMounted(() => {
  getDashboardData()
  getRecentOrders()
  
  // 延迟初始化图表，确保DOM已经渲染
  setTimeout(() => {
    initOrderStatusChart()
    initProductCategoryChart()
  }, 100)
  
  // 监听窗口 resize
  window.addEventListener('resize', handleResize)
})

// 组件卸载
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  orderStatusChart?.dispose()
  productCategoryChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 32px;
  background: linear-gradient(90deg, #ff6b35 0%, #ff8e53 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  position: relative;
  padding-left: 20px;
}

.page-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 28px;
  background: linear-gradient(180deg, #ff6b35 0%, #ff8e53 100%);
  border-radius: 2px;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.stat-card {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  border-radius: 16px;
  overflow: hidden;
  position: relative;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 107, 53, 0.1) 0%, rgba(255, 142, 83, 0.1) 100%);
  opacity: 0;
  transition: opacity 0.4s;
  border-radius: 16px;
}

.stat-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 40px rgba(255, 107, 53, 0.2);
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 28px;
  position: relative;
  z-index: 1;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  position: relative;
  overflow: hidden;
}

.stat-icon::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
  animation: shimmer 3s infinite;
}

@keyframes shimmer {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 6px;
  background: linear-gradient(90deg, #1a1a2e 0%, #4a5568 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
  letter-spacing: 0.5px;
}

/* 图表容器 */
.charts-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.chart-card {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  border-radius: 16px;
  overflow: hidden;
  position: relative;
}

.chart-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 107, 53, 0.05) 0%, rgba(255, 142, 83, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.4s;
  border-radius: 16px;
}

.chart-card:hover {
  box-shadow: 0 20px 40px rgba(255, 107, 53, 0.15);
}

.chart-card:hover::before {
  opacity: 1;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
  position: relative;
  z-index: 1;
}

.card-header::before {
  content: '';
  position: absolute;
  left: 24px;
  right: 24px;
  height: 3px;
  background: linear-gradient(90deg, #ff6b35 0%, #ff8e53 100%);
  opacity: 0;
  transition: opacity 0.3s;
}

.chart-card:hover .card-header::before {
  opacity: 1;
}

.chart {
  width: 100%;
  height: 350px;
  padding: 20px;
}

/* 最近订单 */
.recent-orders-card {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  border-radius: 16px;
  overflow: hidden;
  position: relative;
}

.recent-orders-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 107, 53, 0.05) 0%, rgba(255, 142, 83, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.4s;
  border-radius: 16px;
}

.recent-orders-card:hover {
  box-shadow: 0 20px 40px rgba(255, 107, 53, 0.15);
}

.recent-orders-card:hover::before {
  opacity: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
    gap: 16px;
  }

  .charts-container {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .chart {
    height: 280px;
    padding: 12px;
  }

  .page-title {
    font-size: 22px;
    margin-bottom: 24px;
  }

  .stat-content {
    padding: 20px;
  }

  .stat-icon {
    width: 56px;
    height: 56px;
  }

  .stat-value {
    font-size: 28px;
  }
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-table__header-wrapper) {
  background: linear-gradient(90deg, #ff6b35 0%, #ff8e53 100%);
}

:deep(.el-table th) {
  background: transparent !important;
  color: #fff !important;
  font-weight: 600;
}

:deep(.el-table__row:hover) {
  background: rgba(255, 107, 53, 0.05) !important;
}

:deep(.el-button--primary) {
  background: linear-gradient(90deg, #ff6b35 0%, #ff8e53 100%);
  border: none;
  transition: all 0.3s;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 53, 0.4);
}
</style>