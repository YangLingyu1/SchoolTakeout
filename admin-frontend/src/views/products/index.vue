<template>
  <div class="products">
    <h2 class="page-title">商品管理</h2>

    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="search-filter">
        <el-input
          v-model="searchQuery"
          placeholder="搜索商品名称"
          :prefix-icon="'Search'"
          style="width: 300px; margin-right: 10px"
          @keyup.enter="getProducts"
        />
        <el-select
          v-model="categoryId"
          placeholder="选择分类"
          style="width: 200px; margin-right: 10px"
          @change="getProducts"
        >
          <el-option label="全部分类" value="" />
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
        <el-button type="primary" @click="getProducts" :icon="'R'">
          刷新
        </el-button>
      </div>
      <el-button type="success" @click="openAddDialog" :icon="'P'">
        新增商品
      </el-button>
    </div>

    <!-- 商品表格 -->
    <el-card class="products-card">
      <el-table
        v-loading="loading"
        :data="products"
        style="width: 100%"
        border
        stripe
      >
        <el-table-column prop="id" label="商品ID" width="80" />
        <el-table-column prop="name" label="商品名称" min-width="180">
          <template #default="scope">
            <div class="product-name">
              <img v-if="scope.row.imageUrl" :src="scope.row.imageUrl" alt="商品图片" class="product-image" />
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="scope">
            ¥{{ scope.row.price.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
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
              @click="viewProductDetail(scope.row)"
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

    <!-- 新增/编辑商品对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="product-form"
      >
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>

        <el-form-item label="商品分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择商品分类">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="商品价格" prop="price">
          <el-input-number
            v-model="form.price"
            :min="0"
            :step="0.01"
            :precision="2"
            placeholder="请输入商品价格"
          />
        </el-form-item>

        <el-form-item label="商品库存" prop="stock">
          <el-input-number
            v-model="form.stock"
            :min="0"
            :step="1"
            placeholder="请输入商品库存"
          />
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            rows="4"
            placeholder="请输入商品描述"
          />
        </el-form-item>

        <el-form-item label="商品图片">
          <el-upload
            class="upload-demo"
            action="#"
            :auto-upload="false"
            :on-change="handleImageChange"
            :show-file-list="false"
          >
            <img
              v-if="form.imageUrl"
              :src="form.imageUrl"
              alt="商品图片"
              class="upload-image"
            />
            <el-button v-else type="primary" :icon="'I'">
              上传图片
            </el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveProduct">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 商品详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="商品详情"
      width="600px"
      destroy-on-close
    >
      <div v-if="currentProduct" class="product-detail">
        <div class="detail-row">
          <span class="label">商品ID：</span>
          <span class="value">{{ currentProduct.id }}</span>
        </div>
        <div class="detail-row">
          <span class="label">商品名称：</span>
          <span class="value">{{ currentProduct.name }}</span>
        </div>
        <div class="detail-row">
          <span class="label">商品分类：</span>
          <span class="value">{{ currentProduct.categoryName }}</span>
        </div>
        <div class="detail-row">
          <span class="label">商品价格：</span>
          <span class="value">¥{{ currentProduct.price.toFixed(2) }}</span>
        </div>
        <div class="detail-row">
          <span class="label">商品库存：</span>
          <span class="value">{{ currentProduct.stock }}</span>
        </div>
        <div class="detail-row">
          <span class="label">商品描述：</span>
          <span class="value">{{ currentProduct.description }}</span>
        </div>
        <div class="detail-row">
          <span class="label">商品图片：</span>
          <div class="value">
            <img v-if="currentProduct.imageUrl" :src="currentProduct.imageUrl" alt="商品图片" class="detail-image" />
            <span v-else>无图片</span>
          </div>
        </div>
        <div class="detail-row">
          <span class="label">创建时间：</span>
          <span class="value">{{ currentProduct.createdAt ? formatDate(currentProduct.createdAt) : '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">更新时间：</span>
          <span class="value">{{ currentProduct.updatedAt ? formatDate(currentProduct.updatedAt) : '-' }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { productApi } from '../../api/products'
import { categoryApi } from '../../api/categories'
import { uploadApi } from '../../api/upload'
import dayjs from 'dayjs'
import type { FormInstance, FormRules } from 'element-plus'
import type { Product } from '../../api/products'

// 响应式数据
const loading = ref<boolean>(false)
const products = ref<Product[]>([])
const categories = ref<any[]>([])
const total = ref<number>(0)
const currentPage = ref<number>(1)
const pageSize = ref<number>(10)
const searchQuery = ref<string>('')
const categoryId = ref<number | ''>('')

// 对话框
const dialogVisible = ref<boolean>(false)
const detailDialogVisible = ref<boolean>(false)
const dialogTitle = ref<string>('新增商品')
const formRef = ref<FormInstance>()

// 表单数据
const form = reactive({
  id: 0,
  name: '',
  description: '',
  price: 0,
  stock: 0,
  categoryId: 0,
  imageUrl: ''
})

// 当前商品详情
const currentProduct = ref<Product | null>(null)

// 表单验证规则
const rules = reactive<FormRules>({
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 1, max: 100, message: '商品名称长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '商品价格不能为负数', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入商品库存', trigger: 'blur' },
    { type: 'number', min: 0, message: '商品库存不能为负数', trigger: 'blur' }
  ],
  description: [
    { max: 500, message: '商品描述长度不能超过 500 个字符', trigger: 'blur' }
  ]
})

// 获取商品列表
const getProducts = async () => {
  loading.value = true

  try {
    const response = await productApi.getProducts(categoryId.value || undefined)
    const data = response.data
    
    console.log('原始商品数据:', data)
    console.log('第一个商品的imageUrl:', data[0]?.imageUrl)
    console.log('第一个商品的image:', data[0]?.image)
    
    // 为图片URL添加服务器地址前缀
    const baseUrl = (import.meta as any).env.VITE_API_BASE_URL || 'http://localhost:8081'
    const processedData = data.map((product: Product) => ({
      ...product,
      imageUrl: product.imageUrl ? (product.imageUrl.startsWith('http') ? product.imageUrl : baseUrl + product.imageUrl) : ''
    }))
    
    console.log('处理后的商品数据:', processedData)
    console.log('处理后第一个商品的imageUrl:', processedData[0]?.imageUrl)
    
    // 搜索过滤
    const filteredProducts = searchQuery.value
      ? processedData.filter((product: Product) =>
          product.name.toLowerCase().includes(searchQuery.value!.toLowerCase())
        )
      : processedData
    
    // 分页处理
    total.value = filteredProducts.length
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    products.value = filteredProducts.slice(start, end)
  } catch (err: any) {
    ElMessage.error('获取商品列表失败：' + (err.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const getCategories = async () => {
  try {
    const response = await categoryApi.getCategories()
    categories.value = response.data
  } catch (err: any) {
    ElMessage.error('获取分类列表失败：' + (err.message || '未知错误'))
  }
}

// 分页处理
const handleSizeChange = (size: number) => {
  pageSize.value = size
  getProducts()
}

const handleCurrentChange = (current: number) => {
  currentPage.value = current
  getProducts()
}

// 打开新增对话框
const openAddDialog = () => {
  dialogTitle.value = '新增商品'
  // 重置表单
  Object.assign(form, {
    id: 0,
    name: '',
    description: '',
    price: 0,
    stock: 0,
    categoryId: categories.value.length > 0 ? categories.value[0].id : 0,
    imageUrl: ''
  })
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (product: Product) => {
  dialogTitle.value = '编辑商品'
  // 填充表单，确保图片URL包含完整的服务器地址
  const baseUrl = (import.meta as any).env.VITE_API_BASE_URL || 'http://localhost:8081'
  const processedProduct = {
    ...product,
    imageUrl: product.imageUrl ? (product.imageUrl.startsWith('http') ? product.imageUrl : baseUrl + product.imageUrl) : ''
  }
  Object.assign(form, processedProduct)
  dialogVisible.value = true
}

// 查看商品详情
const viewProductDetail = (product: Product) => {
  currentProduct.value = product
  detailDialogVisible.value = true
}

// 保存商品
const saveProduct = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true

    try {
      console.log('保存商品数据:', form)
      
      // 准备发送到后端的数据，去掉服务器地址前缀
      const baseUrl = (import.meta as any).env.VITE_API_BASE_URL || 'http://localhost:8081'
      const productData = {
        ...form,
        imageUrl: form.imageUrl ? form.imageUrl.replace(baseUrl, '') : ''
      }
      
      if (form.id) {
        // 编辑商品
        await productApi.updateProduct(form.id, productData)
        ElMessage.success('商品编辑成功')
      } else {
        // 新增商品
        await productApi.createProduct(productData)
        ElMessage.success('商品新增成功')
      }
      
      dialogVisible.value = false
      getProducts()
    } catch (err: any) {
      ElMessage.error('保存商品失败：' + (err.message || '未知错误'))
      console.error('保存商品错误:', err)
    } finally {
      loading.value = false
    }
  })
}

// 确认删除
const confirmDelete = (productId: number) => {
  ElMessageBox.confirm('确定要删除这个商品吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      loading.value = true
      try {
        await productApi.deleteProduct(productId)
        ElMessage.success('商品删除成功')
        getProducts()
      } catch (err: any) {
        ElMessage.error('删除商品失败：' + (err.message || '未知错误'))
      } finally {
        loading.value = false
      }
    })
    .catch(() => {
      // 取消删除
    })
}

// 处理图片上传
const handleImageChange = async (file: any) => {
  try {
    const res = await uploadApi.uploadProductImage(file.raw)
    // 添加服务器地址前缀
    const baseUrl = (import.meta as any).env.VITE_API_BASE_URL || 'http://localhost:8081'
    form.imageUrl = baseUrl + res.data.url
    ElMessage.success('图片上传成功')
  } catch (err: any) {
    ElMessage.error('图片上传失败：' + (err.message || '未知错误'))
  }
}

// 格式化日期
const formatDate = (dateString: string): string => {
  return dayjs(dateString).format('YYYY-MM-DD HH:mm:ss')
}

// 组件挂载
onMounted(() => {
  getCategories()
  getProducts()
})
</script>

<style scoped>
.products {
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

/* 操作栏 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 20px 24px;
  background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
  position: relative;
  overflow: hidden;
}

.action-bar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #ff6b35 0%, #ff8e53 100%);
  opacity: 0;
  transition: opacity 0.3s;
}

.action-bar:hover::before {
  opacity: 1;
}

.search-filter {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 商品表格 */
.products-card {
  margin-bottom: 24px;
  border: none;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.products-card:hover {
  box-shadow: 0 8px 24px rgba(255, 107, 53, 0.12);
}

.product-name {
  display: flex;
  align-items: center;
}

.product-image {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  margin-right: 12px;
  object-fit: cover;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.product-image:hover {
  transform: scale(1.1);
}

/* 分页 */
.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  padding: 16px 24px;
  background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 商品表单 */
.product-form {
  max-height: 500px;
  overflow-y: auto;
  padding: 20px 0;
}

/* 上传图片 */
.upload-demo {
  display: flex;
  align-items: center;
}

.upload-image {
  width: 140px;
  height: 140px;
  border-radius: 12px;
  object-fit: cover;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s;
}

.upload-image:hover {
  transform: scale(1.05);
}

/* 商品详情 */
.product-detail {
  padding: 24px 0;
}

.detail-row {
  display: flex;
  margin-bottom: 20px;
  align-items: flex-start;
  padding: 16px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 12px;
  transition: all 0.3s;
}

.detail-row:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.detail-row .label {
  width: 120px;
  font-weight: 600;
  color: #4a5568;
  flex-shrink: 0;
}

.detail-row .value {
  flex: 1;
  color: #1a1a2e;
  font-weight: 500;
}

.detail-image {
  width: 220px;
  height: 220px;
  border-radius: 12px;
  object-fit: cover;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .action-bar {
    flex-direction: column;
    align-items: stretch;
    padding: 16px;
  }

  .search-filter {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
    margin-bottom: 12px;
  }

  .search-filter el-input,
  .search-filter el-select {
    width: 100% !important;
  }

  .page-title {
    font-size: 22px;
    margin-bottom: 24px;
  }

  .detail-row {
    flex-direction: column;
    padding: 12px;
  }

  .detail-row .label {
    width: 100%;
    margin-bottom: 8px;
  }
}

/* 全局样式覆盖 */
:deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 8px rgba(255, 107, 53, 0.15);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.25);
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-button--primary) {
  background: linear-gradient(90deg, #ff6b35 0%, #ff8e53 100%);
  border: none;
  transition: all 0.3s;
  border-radius: 8px;
  font-weight: 600;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 53, 0.4);
}

:deep(.el-button--success) {
  background: linear-gradient(90deg, #67c23a 0%, #85ce61 100%);
  border: none;
  transition: all 0.3s;
  border-radius: 8px;
  font-weight: 600;
}

:deep(.el-button--success:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(103, 194, 58, 0.4);
}

:deep(.el-button--warning) {
  background: linear-gradient(90deg, #e6a23c 0%, #f0c78a 100%);
  border: none;
  transition: all 0.3s;
  border-radius: 8px;
  font-weight: 600;
}

:deep(.el-button--warning:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(230, 162, 60, 0.4);
}

:deep(.el-button--danger) {
  background: linear-gradient(90deg, #f56c6c 0%, #f78989 100%);
  border: none;
  transition: all 0.3s;
  border-radius: 8px;
  font-weight: 600;
}

:deep(.el-button--danger:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245, 108, 108, 0.4);
}

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

:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  background: linear-gradient(90deg, #ff6b35 0%, #ff8e53 100%);
  color: #fff;
  padding: 20px 24px;
  border-bottom: none;
}

:deep(.el-dialog__title) {
  color: #fff;
  font-weight: 600;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

:deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid #e2e8f0;
}
</style>