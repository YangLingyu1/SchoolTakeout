<template>
  <div class="orders">
    <h2 class="page-title">订单管理</h2>

    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="search-filter">
        <el-input
          v-model="searchQuery"
          placeholder="搜索订单号或用户名"
          :prefix-icon="'Search'"
          style="width: 300px; margin-right: 10px"
          @keyup.enter="getOrders"
        />
        <el-select
          v-model="status"
          placeholder="选择订单状态"
          style="width: 200px; margin-right: 10px"
          @change="getOrders"
        >
          <el-option label="全部状态" value="" />
          <el-option label="待处理" value="0" />
          <el-option label="处理中" value="1" />
          <el-option label="已完成" value="2" />
          <el-option label="已取消" value="3" />
        </el-select>
        <el-button type="primary" @click="getOrders" :icon="'R'">
          刷新
        </el-button>
      </div>
    </div>

    <!-- 订单表格 -->
    <el-card class="orders-card">
      <el-table
        v-loading="loading"
        :data="orders"
        style="width: 100%"
        border
        stripe
      >
        <el-table-column prop="id" label="订单号" width="120" />
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="userPhone" label="手机号" width="120" />
        <el-table-column prop="address" label="地址" min-width="200">
          <template #default="scope">
            <el-tooltip :content="scope.row.address" placement="top">
              <span class="ellipsis">{{ scope.row.address }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
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
        <el-table-column prop="riderName" label="骑手" width="120">
          <template #default="scope">
            {{ scope.row.riderName || '未分配' }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="viewOrderDetail(scope.row.id)"
              :icon="'V'"
              style="margin-right: 5px"
            >
              详情
            </el-button>
            <el-button
              type="warning"
              size="small"
              @click="updateOrderStatus(scope.row)"
              :icon="'E'"
              style="margin-right: 5px"
            >
              更新状态
            </el-button>
            <el-button
              type="success"
              size="small"
              @click="assignRider(scope.row)"
              :icon="'U'"
              :disabled="scope.row.riderId"
            >
              分配骑手
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="800px"
      destroy-on-close
    >
      <div v-if="currentOrder" class="order-detail">
        <!-- 订单基本信息 -->
        <div class="detail-section">
          <h3 class="section-title">订单信息</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">订单号：</span>
              <span class="value">{{ currentOrder.order.id }}</span>
            </div>
            <div class="detail-item">
              <span class="label">用户：</span>
              <span class="value">{{ currentOrder.order.userName }}</span>
            </div>
            <div class="detail-item">
              <span class="label">手机号：</span>
              <span class="value">{{ currentOrder.order.userPhone }}</span>
            </div>
            <div class="detail-item">
              <span class="label">地址：</span>
              <span class="value">{{ currentOrder.order.address }}</span>
            </div>
            <div class="detail-item">
              <span class="label">总金额：</span>
              <span class="value">¥{{ currentOrder.order.totalAmount.toFixed(2) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">订单状态：</span>
              <span class="value">
                <el-tag :type="getStatusType(currentOrder.order.status)">
                  {{ getStatusText(currentOrder.order.status) }}
                </el-tag>
              </span>
            </div>
            <div class="detail-item">
              <span class="label">骑手：</span>
              <span class="value">{{ currentOrder.order.riderName || '未分配' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">创建时间：</span>
              <span class="value">{{ formatDate(currentOrder.order.createdAt) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">更新时间：</span>
              <span class="value">{{ formatDate(currentOrder.order.updatedAt) }}</span>
            </div>
          </div>
        </div>

        <!-- 订单项 -->
        <div class="detail-section">
          <h3 class="section-title">商品清单</h3>
          <el-table :data="currentOrder.items" style="width: 100%" border>
            <el-table-column prop="productName" label="商品名称" min-width="200" />
            <el-table-column prop="price" label="单价" width="100">
              <template #default="scope">
                ¥{{ scope.row.price.toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="100" />
            <el-table-column label="小计" width="120">
              <template #default="scope">
                ¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 更新订单状态对话框 -->
    <el-dialog
      v-model="statusDialogVisible"
      title="更新订单状态"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="statusFormRef"
        :model="statusForm"
        label-width="100px"
        class="status-form"
      >
        <el-form-item label="当前状态">
          <el-tag :type="getStatusType(editingOrder?.status || 0)">
            {{ getStatusText(editingOrder?.status || 0) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="新状态" prop="status">
          <el-select v-model="statusForm.status" placeholder="请选择新状态">
            <el-option label="待处理" value="0" />
            <el-option label="处理中" value="1" />
            <el-option label="已完成" value="2" />
            <el-option label="已取消" value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="statusDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveStatus">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 分配骑手对话框 -->
    <el-dialog
      v-model="riderDialogVisible"
      title="分配骑手"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="riderFormRef"
        :model="riderForm"
        label-width="100px"
        class="rider-form"
      >
        <el-form-item label="订单号">
          <span>{{ editingOrder?.id }}</span>
        </el-form-item>
        <el-form-item label="用户">
          <span>{{ editingOrder?.userName }}</span>
        </el-form-item>
        <el-form-item label="选择骑手" prop="riderId">
          <el-select v-model="riderForm.riderId" placeholder="请选择骑手">
            <el-option
              v-for="rider in riders"
              :key="rider.id"
              :label="rider.name"
              :value="rider.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="riderDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveRiderAssignment">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '../../api/orders'
import { riderApi } from '../../api/riders'
import dayjs from 'dayjs'
import type { FormInstance } from 'element-plus'
import type { Order, OrderDetail } from '../../api/orders'
import type { Rider } from '../../api/riders'

// 响应式数据
const loading = ref<boolean>(false)
const orders = ref<Order[]>([])
const riders = ref<Rider[]>([])
const total = ref<number>(0)
const currentPage = ref<number>(1)
const pageSize = ref<number>(10)
const searchQuery = ref<string>('')
const status = ref<number | ''>('')

// 对话框
const detailDialogVisible = ref<boolean>(false)
const statusDialogVisible = ref<boolean>(false)
const riderDialogVisible = ref<boolean>(false)

// 当前订单
const currentOrder = ref<OrderDetail | null>(null)
const editingOrder = ref<Order | null>(null)

// 表单
const statusFormRef = ref<FormInstance>()
const riderFormRef = ref<FormInstance>()

const statusForm = reactive({
  status: 0
})

const riderForm = reactive({
  riderId: 0
})

// 获取订单列表
const getOrders = async () => {
  loading.value = true

  try {
    const response = await orderApi.getOrders(status.value || undefined)
    const data = response.data
    
    // 搜索过滤
    const filteredOrders = searchQuery.value
      ? data.filter((order: Order) =>
          order.id.toString().includes(searchQuery.value!) ||
          order.userName.toLowerCase().includes(searchQuery.value!.toLowerCase())
        )
      : data
    
    // 分页处理
    total.value = filteredOrders.length
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    orders.value = filteredOrders.slice(start, end)
  } catch (err: any) {
    ElMessage.error('获取订单列表失败：' + (err.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 获取骑手列表
const getRiders = async () => {
  try {
    const response = await riderApi.getRiders()
    riders.value = response.data
  } catch (err: any) {
    ElMessage.error('获取骑手列表失败：' + (err.message || '未知错误'))
  }
}

// 分页处理
const handleSizeChange = (size: number) => {
  pageSize.value = size
  getOrders()
}

const handleCurrentChange = (current: number) => {
  currentPage.value = current
  getOrders()
}

// 查看订单详情
const viewOrderDetail = async (orderId: number) => {
  loading.value = true

  try {
    const response = await orderApi.getOrderById(orderId)
    currentOrder.value = response.data
    detailDialogVisible.value = true
  } catch (err: any) {
    ElMessage.error('获取订单详情失败：' + (err.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 更新订单状态
const updateOrderStatus = (order: Order) => {
  editingOrder.value = order
  statusForm.status = order.status
  statusDialogVisible.value = true
}

// 保存订单状态
const saveStatus = async () => {
  if (!statusFormRef.value || !editingOrder.value) return

  loading.value = true

  try {
    await orderApi.updateOrderStatus(editingOrder.value.id, {
      status: statusForm.status
    })
    ElMessage.success('订单状态更新成功')
    statusDialogVisible.value = false
    getOrders()
  } catch (err: any) {
    ElMessage.error('更新订单状态失败：' + (err.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 分配骑手
const assignRider = (order: Order) => {
  editingOrder.value = order
  riderForm.riderId = 0
  riderDialogVisible.value = true
}

// 保存骑手分配
const saveRiderAssignment = async () => {
  if (!riderFormRef.value || !editingOrder.value) return

  loading.value = true

  try {
    await orderApi.assignRider(editingOrder.value.id, {
      riderId: riderForm.riderId
    })
    ElMessage.success('骑手分配成功')
    riderDialogVisible.value = false
    getOrders()
  } catch (err: any) {
    ElMessage.error('分配骑手失败：' + (err.message || '未知错误'))
  } finally {
    loading.value = false
  }
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
  return dayjs(dateString).format('YYYY-MM-DD HH:mm:ss')
}

// 组件挂载
onMounted(() => {
  getOrders()
  getRiders()
})
</script>

<style scoped>
.orders {
  padding: 20px 0;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #303133;
}

/* 操作栏 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-filter {
  display: flex;
  align-items: center;
}

/* 订单表格 */
.orders-card {
  margin-bottom: 20px;
}

/* 分页 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 订单详情 */
.order-detail {
  padding: 20px 0;
}

.detail-section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #303133;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 10px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
}

.detail-item .label {
  width: 80px;
  font-weight: 500;
  color: #606266;
}

.detail-item .value {
  flex: 1;
  color: #303133;
}

/* 状态表单 */
.status-form {
  padding: 20px 0;
}

/* 骑手表单 */
.rider-form {
  padding: 20px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .action-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-filter {
    flex-direction: column;
    align-items: stretch;
    margin-bottom: 10px;
  }

  .search-filter el-input,
  .search-filter el-select {
    width: 100% !important;
    margin-right: 0 !important;
    margin-bottom: 10px;
  }

  .page-title {
    font-size: 20px;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>