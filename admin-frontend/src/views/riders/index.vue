<template>
  <div class="riders">
    <h2 class="page-title">骑手管理</h2>

    <!-- 标签页 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="骑手列表" name="riders">
        <!-- 操作栏 -->
        <div class="action-bar">
          <div class="search-filter">
            <el-input
              v-model="riderSearchQuery"
              placeholder="搜索骑手姓名或手机号"
              :prefix-icon="'Search'"
              style="width: 300px; margin-right: 10px"
              @keyup.enter="getRiders"
            />
            <el-button type="primary" @click="getRiders" :icon="'R'">
              刷新
            </el-button>
          </div>
        </div>

        <!-- 骑手表格 -->
        <el-card class="riders-card">
          <el-table
            v-loading="riderLoading"
            :data="riders"
            style="width: 100%"
            border
            stripe
          >
            <el-table-column prop="id" label="骑手ID" width="80" />
            <el-table-column prop="name" label="姓名" width="120" />
            <el-table-column prop="phone" label="手机号" width="150" />
            <el-table-column prop="username" label="用户名" width="150" />
            <el-table-column prop="riderStatus" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getRiderStatusType(scope.row.riderStatus)">
                  {{ getRiderStatusText(scope.row.riderStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="注册时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="scope">
                <el-button
              type="primary"
              size="small"
              @click="viewRiderDetail(scope.row)"
              :icon="'V'"
            >
              详情
            </el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination">
            <el-pagination
              v-model:current-page="riderCurrentPage"
              v-model:page-size="riderPageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="riderTotal"
              @size-change="handleRiderSizeChange"
              @current-change="handleRiderCurrentChange"
            />
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="骑手申请" name="applications">
        <!-- 操作栏 -->
        <div class="action-bar">
          <div class="search-filter">
            <el-input
              v-model="appSearchQuery"
              placeholder="搜索申请人姓名或手机号"
              :prefix-icon="'Search'"
              style="width: 300px; margin-right: 10px"
              @keyup.enter="getApplications"
            />
            <el-select
              v-model="appStatus"
              placeholder="选择申请状态"
              style="width: 200px; margin-right: 10px"
              @change="getApplications"
            >
              <el-option label="全部状态" value="" />
              <el-option label="待审核" value="pending" />
              <el-option label="已通过" value="approved" />
              <el-option label="已拒绝" value="rejected" />
            </el-select>
            <el-button type="primary" @click="getApplications" :icon="'R'">
              刷新
            </el-button>
          </div>
        </div>

        <!-- 申请表格 -->
        <el-card class="applications-card">
          <el-table
            v-loading="appLoading"
            :data="applications"
            style="width: 100%"
            border
            stripe
          >
            <el-table-column prop="id" label="申请ID" width="80" />
            <el-table-column prop="userName" label="申请人" width="120" />
            <el-table-column prop="userPhone" label="手机号" width="150" />
            <el-table-column prop="vehicleType" label="车辆类型" width="120" />
            <el-table-column prop="vehicleNumber" label="车牌号" width="150" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getAppStatusType(scope.row.status)">
                  {{ getAppStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="申请时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <el-button
                  type="primary"
                  size="small"
                  @click="viewApplicationDetail(scope.row)"
                  :icon="'V'"
                  style="margin-right: 5px"
                >
                  详情
                </el-button>
                <template v-if="scope.row.status === 'pending'">
                  <el-button
                    type="success"
                    size="small"
                    @click="approveApplication(scope.row)"
                    :icon="'C'"
                    style="margin-right: 5px"
                  >
                    通过
                  </el-button>
                  <el-button
                    type="danger"
                    size="small"
                    @click="rejectApplication(scope.row)"
                    :icon="'X'"
                  >
                    拒绝
                  </el-button>
                </template>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination">
            <el-pagination
              v-model:current-page="appCurrentPage"
              v-model:page-size="appPageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="appTotal"
              @size-change="handleAppSizeChange"
              @current-change="handleAppCurrentChange"
            />
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 骑手详情对话框 -->
    <el-dialog
      v-model="riderDetailVisible"
      title="骑手详情"
      width="500px"
      destroy-on-close
    >
      <div v-if="currentRider" class="rider-detail">
        <div class="detail-row">
          <span class="label">骑手ID：</span>
          <span class="value">{{ currentRider.id }}</span>
        </div>
        <div class="detail-row">
          <span class="label">姓名：</span>
          <span class="value">{{ currentRider.name }}</span>
        </div>
        <div class="detail-row">
          <span class="label">手机号：</span>
          <span class="value">{{ currentRider.phone }}</span>
        </div>
        <div class="detail-row">
          <span class="label">用户名：</span>
          <span class="value">{{ currentRider.username }}</span>
        </div>
        <div class="detail-row">
          <span class="label">状态：</span>
          <span class="value">
            <el-tag :type="getRiderStatusType(currentRider.riderStatus)">
              {{ getRiderStatusText(currentRider.riderStatus) }}
            </el-tag>
          </span>
        </div>
        <div class="detail-row">
          <span class="label">注册时间：</span>
          <span class="value">{{ formatDate(currentRider.createdAt) }}</span>
        </div>
        <div class="detail-row">
          <span class="label">更新时间：</span>
          <span class="value">{{ formatDate(currentRider.updatedAt) }}</span>
        </div>
      </div>
    </el-dialog>

    <!-- 申请详情对话框 -->
    <el-dialog
      v-model="appDetailVisible"
      title="申请详情"
      width="600px"
      destroy-on-close
    >
      <div v-if="currentApplication" class="app-detail">
        <div class="detail-row">
          <span class="label">申请ID：</span>
          <span class="value">{{ currentApplication.id }}</span>
        </div>
        <div class="detail-row">
          <span class="label">申请人：</span>
          <span class="value">{{ currentApplication.userName }}</span>
        </div>
        <div class="detail-row">
          <span class="label">手机号：</span>
          <span class="value">{{ currentApplication.userPhone }}</span>
        </div>
        <div class="detail-row">
          <span class="label">身份证号：</span>
          <span class="value">{{ currentApplication.idCard }}</span>
        </div>
        <div class="detail-row">
          <span class="label">车辆类型：</span>
          <span class="value">{{ currentApplication.vehicleType }}</span>
        </div>
        <div class="detail-row">
          <span class="label">车牌号：</span>
          <span class="value">{{ currentApplication.vehicleNumber }}</span>
        </div>
        <div class="detail-row">
          <span class="label">申请状态：</span>
          <span class="value">
            <el-tag :type="getAppStatusType(currentApplication.status)">
              {{ getAppStatusText(currentApplication.status) }}
            </el-tag>
          </span>
        </div>
        <div class="detail-row">
          <span class="label">申请时间：</span>
          <span class="value">{{ formatDate(currentApplication.createdAt) }}</span>
        </div>
        <div class="detail-row">
          <span class="label">更新时间：</span>
          <span class="value">{{ formatDate(currentApplication.updatedAt) }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { riderApi } from '../../api/riders'
import dayjs from 'dayjs'
import type { Rider, RiderApplication } from '../../api/riders'

// 标签页
const activeTab = ref<string>('riders')

// 骑手列表
const riderLoading = ref<boolean>(false)
const riders = ref<Rider[]>([])
const riderTotal = ref<number>(0)
const riderCurrentPage = ref<number>(1)
const riderPageSize = ref<number>(10)
const riderSearchQuery = ref<string>('')

// 骑手申请列表
const appLoading = ref<boolean>(false)
const applications = ref<RiderApplication[]>([])
const appTotal = ref<number>(0)
const appCurrentPage = ref<number>(1)
const appPageSize = ref<number>(10)
const appSearchQuery = ref<string>('')
const appStatus = ref<string>('')

// 对话框
const riderDetailVisible = ref<boolean>(false)
const appDetailVisible = ref<boolean>(false)
const currentRider = ref<Rider | null>(null)
const currentApplication = ref<RiderApplication | null>(null)

// 获取骑手列表
const getRiders = async () => {
  riderLoading.value = true

  try {
    const response = await riderApi.getRiders()
    const data = response.data
    
    // 搜索过滤
    const filteredRiders = riderSearchQuery.value
      ? data.filter((rider: Rider) =>
          rider.name.toLowerCase().includes(riderSearchQuery.value!.toLowerCase()) ||
          rider.phone.includes(riderSearchQuery.value!)
        )
      : data
    
    // 分页处理
    riderTotal.value = filteredRiders.length
    const start = (riderCurrentPage.value - 1) * riderPageSize.value
    const end = start + riderPageSize.value
    riders.value = filteredRiders.slice(start, end)
  } catch (err: any) {
    ElMessage.error('获取骑手列表失败：' + (err.message || '未知错误'))
  } finally {
    riderLoading.value = false
  }
}

// 获取骑手申请列表
const getApplications = async () => {
  appLoading.value = true

  try {
    const response = await riderApi.getRiderApplications(appStatus.value || undefined)
    const data = response.data
    
    // 搜索过滤
    const filteredApps = appSearchQuery.value
      ? data.filter((app: RiderApplication) =>
          app.userName.toLowerCase().includes(appSearchQuery.value!.toLowerCase()) ||
          app.userPhone.includes(appSearchQuery.value!)
        )
      : data
    
    // 分页处理
    appTotal.value = filteredApps.length
    const start = (appCurrentPage.value - 1) * appPageSize.value
    const end = start + appPageSize.value
    applications.value = filteredApps.slice(start, end)
  } catch (err: any) {
    ElMessage.error('获取骑手申请列表失败：' + (err.message || '未知错误'))
  } finally {
    appLoading.value = false
  }
}

// 标签页切换
const handleTabChange = (tab: string) => {
  if (tab === 'riders') {
    getRiders()
  } else if (tab === 'applications') {
    getApplications()
  }
}

// 骑手分页处理
const handleRiderSizeChange = (size: number) => {
  riderPageSize.value = size
  getRiders()
}

const handleRiderCurrentChange = (current: number) => {
  riderCurrentPage.value = current
  getRiders()
}

// 申请分页处理
const handleAppSizeChange = (size: number) => {
  appPageSize.value = size
  getApplications()
}

const handleAppCurrentChange = (current: number) => {
  appCurrentPage.value = current
  getApplications()
}

// 查看骑手详情
const viewRiderDetail = (rider: Rider) => {
  currentRider.value = rider
  riderDetailVisible.value = true
}

// 查看申请详情
const viewApplicationDetail = (application: RiderApplication) => {
  currentApplication.value = application
  appDetailVisible.value = true
}

// 批准申请
const approveApplication = (application: RiderApplication) => {
  ElMessageBox.confirm('确定要批准这个骑手申请吗？', '批准确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  })
    .then(async () => {
      appLoading.value = true
      try {
        await riderApi.reviewRiderApplication(application.id, {
          status: 'approved'
        })
        ElMessage.success('骑手申请批准成功')
        getApplications()
      } catch (err: any) {
        ElMessage.error('批准骑手申请失败：' + (err.message || '未知错误'))
      } finally {
        appLoading.value = false
      }
    })
    .catch(() => {
      // 取消批准
    })
}

// 拒绝申请
const rejectApplication = (application: RiderApplication) => {
  ElMessageBox.confirm('确定要拒绝这个骑手申请吗？', '拒绝确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'error'
  })
    .then(async () => {
      appLoading.value = true
      try {
        await riderApi.reviewRiderApplication(application.id, {
          status: 'rejected'
        })
        ElMessage.success('骑手申请拒绝成功')
        getApplications()
      } catch (err: any) {
        ElMessage.error('拒绝骑手申请失败：' + (err.message || '未知错误'))
      } finally {
        appLoading.value = false
      }
    })
    .catch(() => {
      // 取消拒绝
    })
}

// 获取骑手状态类型
const getRiderStatusType = (status: string): string => {
  const statusMap: Record<string, string> = {
    'approved': 'success',
    'rejected': 'danger',
    'pending': 'warning'
  }
  return statusMap[status] || 'info'
}

// 获取骑手状态文本
const getRiderStatusText = (status: string): string => {
  const statusMap: Record<string, string> = {
    'approved': '已通过',
    'rejected': '已拒绝',
    'pending': '待审核'
  }
  return statusMap[status] || '未知状态'
}

// 获取申请状态类型
const getAppStatusType = (status: string): string => {
  const statusMap: Record<string, string> = {
    'approved': 'success',
    'rejected': 'danger',
    'pending': 'warning'
  }
  return statusMap[status] || 'info'
}

// 获取申请状态文本
const getAppStatusText = (status: string): string => {
  const statusMap: Record<string, string> = {
    'approved': '已通过',
    'rejected': '已拒绝',
    'pending': '待审核'
  }
  return statusMap[status] || '未知状态'
}

// 格式化日期
const formatDate = (dateString: string): string => {
  return dayjs(dateString).format('YYYY-MM-DD HH:mm:ss')
}

// 组件挂载
onMounted(() => {
  getRiders()
})
</script>

<style scoped>
.riders {
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

/* 骑手表格 */
.riders-card,
.applications-card {
  margin-bottom: 20px;
}

/* 分页 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 骑手详情 */
.rider-detail,
.app-detail {
  padding: 20px 0;
}

.detail-row {
  display: flex;
  margin-bottom: 15px;
  align-items: flex-start;
}

.detail-row .label {
  width: 100px;
  font-weight: 500;
  color: #606266;
}

.detail-row .value {
  flex: 1;
  color: #303133;
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
}
</style>