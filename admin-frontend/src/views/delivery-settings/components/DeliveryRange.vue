<template>
  <div class="delivery-range">
    <div class="action-bar">
      <el-button type="primary" @click="openAddDialog" :icon="'P'">
        添加宿舍楼
      </el-button>
    </div>

    <el-card class="dormitories-card">
      <el-table
        v-loading="loading"
        :data="dormitories"
        style="width: 100%"
        border
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="buildingNumber" label="楼号" width="120" />
        <el-table-column prop="remark" label="备注" min-width="200" />
        <el-table-column prop="nightDelivery" label="夜间配送" width="120">
          <template #default="scope">
            <el-switch
              v-model="scope.row.nightDelivery"
              @change="handleNightDeliveryChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="openEditDialog(scope.row)"
              :icon="'E'"
              style="margin-right: 5px"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="deleteDormitory(scope.row.id)"
              :icon="'D'"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑宿舍楼' : '添加宿舍楼'"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="楼号" prop="buildingNumber">
          <el-input v-model="form.buildingNumber" placeholder="请输入楼号" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
        <el-form-item label="夜间配送" prop="nightDelivery">
          <el-switch v-model="form.nightDelivery" />
          <span style="margin-left: 10px; color: #909399; font-size: 12px">
            开启后该楼栋在夜间时段可配送
          </span>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import type { FormInstance, FormRules } from 'element-plus'

const loading = ref<boolean>(false)
const dormitories = ref<any[]>([])
const dialogVisible = ref<boolean>(false)
const isEdit = ref<boolean>(false)
const formRef = ref<FormInstance>()

const form = reactive({
  id: 0,
  buildingNumber: '',
  remark: '',
  nightDelivery: false
})

const rules: FormRules = {
  buildingNumber: [
    { required: true, message: '请输入楼号', trigger: 'blur' }
  ]
}

const loadDormitories = async () => {
  loading.value = true
  try {
    const response = await fetch('/api/admin/dormitories')
    const data = await response.json()
    
    if (data.code === 200) {
      dormitories.value = data.data
    }
  } catch (err: any) {
    ElMessage.error('获取宿舍楼列表失败')
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  isEdit.value = false
  form.id = 0
  form.buildingNumber = ''
  form.remark = ''
  form.nightDelivery = false
  dialogVisible.value = true
}

const openEditDialog = (row: any) => {
  isEdit.value = true
  form.id = row.id
  form.buildingNumber = row.buildingNumber
  form.remark = row.remark || ''
  form.nightDelivery = row.nightDelivery || false
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const url = isEdit.value
          ? `/api/admin/dormitories/${form.id}`
          : '/api/admin/dormitories'
        const method = isEdit.value ? 'PUT' : 'POST'
        
        const response = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(form)
        })
        const data = await response.json()
        
        if (data.code === 200) {
          ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
          dialogVisible.value = false
          loadDormitories()
        }
      } catch (err: any) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleNightDeliveryChange = async (row: any) => {
  try {
    const response = await fetch(`/api/admin/dormitories/${row.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(row)
    })
    const data = await response.json()
    
    if (data.code === 200) {
      ElMessage.success('更新成功')
    } else {
      row.nightDelivery = !row.nightDelivery
      ElMessage.error('更新失败')
    }
  } catch (err: any) {
    row.nightDelivery = !row.nightDelivery
    ElMessage.error('更新失败')
  }
}

const deleteDormitory = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定删除该宿舍楼吗？', '确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await fetch(`/api/admin/dormitories/${id}`, {
      method: 'DELETE'
    })
    const data = await response.json()
    
    if (data.code === 200) {
      ElMessage.success('删除成功')
      loadDormitories()
    }
  } catch (err: any) {
    if (err !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const formatDate = (dateString: string): string => {
  return dayjs(dateString).format('YYYY-MM-DD HH:mm:ss')
}

onMounted(() => {
  loadDormitories()
})
</script>

<style scoped>
.delivery-range {
  padding: 20px 0;
}

.action-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.dormitories-card {
  margin-bottom: 20px;
}
</style>