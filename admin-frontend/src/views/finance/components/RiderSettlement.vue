<template>
  <div class="rider-settlement">
    <div class="action-bar">
      <div class="search-filter">
        <el-select
          v-model="status"
          placeholder="选择状态"
          style="width: 200px; margin-right: 10px"
          @change="loadTransactions"
        >
          <el-option label="全部状态" value="" />
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已拒绝" value="rejected" />
        </el-select>
        <el-button type="primary" @click="loadTransactions" :icon="'R'">
          刷新
        </el-button>
      </div>
    </div>

    <el-card class="transactions-card">
      <el-table
        v-loading="loading"
        :data="transactions"
        style="width: 100%"
        border
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="riderName" label="骑手" width="120" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.type === 'withdrawal' ? 'warning' : 'success'">
              {{ scope.row.type === 'withdrawal' ? '提现' : '其他' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="scope">
            ¥{{ scope.row.amount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'pending' && scope.row.type === 'withdrawal'"
              type="success"
              size="small"
              @click="approveWithdrawal(scope.row.id)"
              :icon="'C'"
              style="margin-right: 5px"
            >
              通过
            </el-button>
            <el-button
              v-if="scope.row.status === 'pending' && scope.row.type === 'withdrawal'"
              type="danger"
              size="small"
              @click="rejectWithdrawal(scope.row.id)"
              :icon="'X'"
            >
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <el-dialog
      v-model="rejectDialogVisible"
      title="拒绝提现"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="rejectFormRef"
        :model="rejectForm"
        label-width="100px"
      >
        <el-form-item label="拒绝原因" prop="remark">
          <el-input
            v-model="rejectForm.remark"
            type="textarea"
            rows="4"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmReject">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import type { FormInstance } from 'element-plus'

const loading = ref<boolean>(false)
const transactions = ref<any[]>([])
const total = ref<number>(0)
const currentPage = ref<number>(1)
const pageSize = ref<number>(10)
const status = ref<string>('')

const rejectDialogVisible = ref<boolean>(false)
const rejectFormRef = ref<FormInstance>()
const rejectForm = reactive({
  transactionId: 0,
  remark: ''
})

const loadTransactions = async () => {
  loading.value = true
  try {
    const params = status.value ? `?status=${status.value}` : ''
    const response = await fetch(`/api/admin/transactions${params}`)
    const data = await response.json()
    
    if (data.code === 200) {
      transactions.value = data.data
      total.value = data.data.length
    }
  } catch (err: any) {
    ElMessage.error('获取提现记录失败')
  } finally {
    loading.value = false
  }
}

const approveWithdrawal = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定通过该提现申请吗？', '确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await fetch(`/api/admin/transactions/${id}/approve`, {
      method: 'PUT'
    })
    const data = await response.json()
    
    if (data.code === 200) {
      ElMessage.success('提现审核通过')
      loadTransactions()
    }
  } catch (err: any) {
    if (err !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const rejectWithdrawal = (id: number) => {
  rejectForm.transactionId = id
  rejectForm.remark = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!rejectForm.remark.trim()) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  
  try {
    const response = await fetch(`/api/admin/transactions/${rejectForm.transactionId}/reject`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ remark: rejectForm.remark })
    })
    const data = await response.json()
    
    if (data.code === 200) {
      ElMessage.success('提现已拒绝')
      rejectDialogVisible.value = false
      loadTransactions()
    }
  } catch (err: any) {
    ElMessage.error('操作失败')
  }
}

const getStatusType = (status: string): string => {
  const statusMap: Record<string, string> = {
    'pending': 'info',
    'approved': 'success',
    'rejected': 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status: string): string => {
  const statusMap: Record<string, string> = {
    'pending': '待审核',
    'approved': '已通过',
    'rejected': '已拒绝'
  }
  return statusMap[status] || '未知状态'
}

const formatDate = (dateString: string): string => {
  return dayjs(dateString).format('YYYY-MM-DD HH:mm:ss')
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  loadTransactions()
}

const handleCurrentChange = (current: number) => {
  currentPage.value = current
  loadTransactions()
}

onMounted(() => {
  loadTransactions()
})
</script>

<style scoped>
.rider-settlement {
  padding: 20px 0;
}

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

.transactions-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>