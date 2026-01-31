<template>
  <div class="login-container">
    <div class="login-form">
      <h1 class="login-title">管理后台登录</h1>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        class="form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名"
            autocomplete="off"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :show-password="showPassword"
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="form.remember">记住密码</el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleLogin"
            class="login-button"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <div v-if="error" class="error-message">
        {{ error }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
// import { User, Lock, ArrowRight } from "@element-plus/icons-vue";
import { useAuthStore } from '../../stores/auth'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref<boolean>(false)
const error = ref<string>('')
const showPassword = ref<boolean>(false)

// 表单数据
const form = reactive({
  username: '',
  password: '',
  remember: false
})

// 表单验证规则
const rules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为 6 个字符', trigger: 'blur' }
  ]
})

// 处理登录
const handleLogin = async () => {
  if (!formRef.value) return

  // 表单验证
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    error.value = ''

    try {
      const success = await authStore.login(form.username, form.password)
      if (success) {
        ElMessage.success('登录成功')
        
        // 记住密码（这里简化处理，实际项目中可能需要加密存储）
        if (form.remember) {
          localStorage.setItem('username', form.username)
        } else {
          localStorage.removeItem('username')
        }

        // 跳转到首页
        router.push('/')
      } else {
        error.value = authStore.error || '登录失败'
      }
    } catch (err: any) {
      error.value = err.message || '登录失败'
    } finally {
      loading.value = false
    }
  })
}

// 组件挂载时，尝试从本地存储获取用户名
onMounted(() => {
  const savedUsername = localStorage.getItem('username')
  if (savedUsername) {
    form.username = savedUsername
    form.remember = true
  }
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #ff6b35 0%, #ff8e53 100%);
}

.login-form {
  width: 400px;
  padding: 30px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  animation: fadeIn 0.5s ease-in-out;
}

.login-title {
  text-align: center;
  margin-bottom: 24px;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.form {
  margin-bottom: 20px;
}

.login-button {
  width: 100%;
  height: 40px;
  font-size: 16px;
  border-radius: 6px;
}

.error-message {
  color: #f56c6c;
  text-align: center;
  margin-top: 12px;
  font-size: 14px;
  padding: 8px;
  background-color: #fef0f0;
  border-radius: 4px;
  border: 1px solid #fbc4c4;
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-form {
    width: 90%;
    padding: 20px;
  }

  .login-title {
    font-size: 20px;
  }
}
</style>