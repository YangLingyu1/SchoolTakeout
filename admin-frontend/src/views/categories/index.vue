<template>
  <div class="categories">
    <h2 class="page-title">分类管理</h2>

    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="search-filter">
        <el-input
          v-model="searchQuery"
          placeholder="搜索分类名称"
          :prefix-icon="'Search'"
          style="width: 300px; margin-right: 10px"
          @keyup.enter="getCategories"
        />
        <el-button type="primary" @click="getCategories" :icon="'R'">
          刷新
        </el-button>
      </div>
      <el-button type="success" @click="openAddDialog" :icon="'P'">
        新增分类
      </el-button>
    </div>

    <!-- 分类表格 -->
    <el-card class="categories-card">
      <el-table
        v-loading="loading"
        :data="categories"
        style="width: 100%"
        border
        stripe
      >
        <el-table-column prop="id" label="分类ID" width="80" />
        <el-table-column prop="name" label="分类名称" width="200" />
        <el-table-column prop="description" label="分类描述" min-width="300">
          <template #default="scope">
            <el-tooltip :content="scope.row.description" placement="top">
              <span class="ellipsis">{{ scope.row.description }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="viewCategoryDetail(scope.row)"
              :icon="'V'"
              style="margin-right: 5px"
            >
              详情
            </el-button>
            <el-button
              type="warning"
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
              @click="confirmDelete(scope.row.id)"
              :icon="'D'"
            >
              删除
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

    <!-- 新增/编辑分类对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="category-form"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>

        <el-form-item label="分类描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            rows="4"
            placeholder="请输入分类描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveCategory">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 分类详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="分类详情"
      width="500px"
      destroy-on-close
    >
      <div v-if="currentCategory" class="category-detail">
        <div class="detail-row">
          <span class="label">分类ID：</span>
          <span class="value">{{ currentCategory.id }}</span>
        </div>
        <div class="detail-row">
          <span class="label">分类名称：</span>
          <span class="value">{{ currentCategory.name }}</span>
        </div>
        <div class="detail-row">
          <span class="label">分类描述：</span>
          <span class="value">{{ currentCategory.description }}</span>
        </div>
        <div class="detail-row">
          <span class="label">创建时间：</span>
          <span class="value">{{ formatDate(currentCategory.createdAt) }}</span>
        </div>
        <div class="detail-row">
          <span class="label">更新时间：</span>
          <span class="value">{{ formatDate(currentCategory.updatedAt) }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { categoryApi } from '../../api/categories'
import dayjs from 'dayjs'
import type { FormInstance, FormRules } from 'element-plus'
import type { Category } from '../../api/categories'

// 响应式数据
const loading = ref<boolean>(false)
const categories = ref<Category[]>([])
const total = ref<number>(0)
const currentPage = ref<number>(1)
const pageSize = ref<number>(10)
const searchQuery = ref<string>('')

// 对话框
const dialogVisible = ref<boolean>(false)
const detailDialogVisible = ref<boolean>(false)
const dialogTitle = ref<string>('新增分类')
const formRef = ref<FormInstance>()

// 表单数据
const form = reactive({
  id: 0,
  name: '',
  description: ''
})

// 当前分类详情
const currentCategory = ref<Category | null>(null)

// 表单验证规则
const rules = reactive<FormRules>({
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 1, max: 50, message: '分类名称长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '分类描述长度不能超过 200 个字符', trigger: 'blur' }
  ]
})

// 获取分类列表
const getCategories = async () => {
  loading.value = true

  try {
    const response = await categoryApi.getCategories()
    const data = response.data
    
    // 搜索过滤
    const filteredCategories = searchQuery.value
      ? data.filter((category: Category) =>
          category.name.toLowerCase().includes(searchQuery.value!.toLowerCase())
        )
      : data
    
    // 分页处理
    total.value = filteredCategories.length
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    categories.value = filteredCategories.slice(start, end)
  } catch (err: any) {
    ElMessage.error('获取分类列表失败：' + (err.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 分页处理
const handleSizeChange = (size: number) => {
  pageSize.value = size
  getCategories()
}

const handleCurrentChange = (current: number) => {
  currentPage.value = current
  getCategories()
}

// 打开新增对话框
const openAddDialog = () => {
  dialogTitle.value = '新增分类'
  // 重置表单
  Object.assign(form, {
    id: 0,
    name: '',
    description: ''
  })
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (category: Category) => {
  dialogTitle.value = '编辑分类'
  // 填充表单
  Object.assign(form, category)
  dialogVisible.value = true
}

// 查看分类详情
const viewCategoryDetail = (category: Category) => {
  currentCategory.value = category
  detailDialogVisible.value = true
}

// 保存分类
const saveCategory = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true

    try {
      if (form.id) {
        // 编辑分类
        await categoryApi.updateCategory(form.id, form)
        ElMessage.success('分类编辑成功')
      } else {
        // 新增分类
        await categoryApi.createCategory(form)
        ElMessage.success('分类新增成功')
      }
      
      dialogVisible.value = false
      getCategories()
    } catch (err: any) {
      ElMessage.error('保存分类失败：' + (err.message || '未知错误'))
    } finally {
      loading.value = false
    }
  })
}

// 确认删除
const confirmDelete = (categoryId: number) => {
  ElMessageBox.confirm('确定要删除这个分类吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      loading.value = true
      try {
        await categoryApi.deleteCategory(categoryId)
        ElMessage.success('分类删除成功')
        getCategories()
      } catch (err: any) {
        ElMessage.error('删除分类失败：' + (err.message || '未知错误'))
      } finally {
        loading.value = false
      }
    })
    .catch(() => {
      // 取消删除
    })
}

// 格式化日期
const formatDate = (dateString: string): string => {
  return dayjs(dateString).format('YYYY-MM-DD HH:mm:ss')
}

// 组件挂载
onMounted(() => {
  getCategories()
})
</script>

<style scoped>
.categories {
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

/* 分类表格 */
.categories-card {
  margin-bottom: 20px;
}

/* 分页 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 分类表单 */
.category-form {
  padding: 20px 0;
}

/* 分类详情 */
.category-detail {
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

  .search-filter el-input {
    width: 100% !important;
    margin-right: 0 !important;
    margin-bottom: 10px;
  }

  .page-title {
    font-size: 20px;
  }
}
</style>