<template>
  <div class="main-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <h1>管理后台</h1>
      </div>
      <nav class="sidebar-nav">
        <router-link 
          v-for="item in menuItems" 
          :key="item.path" 
          :to="item.path"
          class="nav-item"
          active-class="active"
        >
          <el-icon :size="20">{{ item.icon }}</el-icon>
          <span>{{ item.label }}</span>
        </router-link>
      </nav>
    </aside>

    <!-- 主内容区 -->
    <main class="main-content">
      <!-- 顶部导航 -->
      <header class="top-nav">
        <div class="nav-left">
          <el-button 
            type="text" 
            @click="toggleSidebar"
            icon="Menu"
            size="large"
          />
        </div>
        <div class="nav-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32">{{ user?.name?.charAt(0) || 'A' }}</el-avatar>
              <span class="user-name">{{ user?.name || '管理员' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区 -->
      <div class="content">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
// import { 
//   Menu, 
//   ArrowDown, 
//   DataLine, 
//   Goods, 
//   ShoppingCart, 
//   Grid, 
//   UserFilled 
// } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const sidebarCollapsed = ref(false)

const menuItems = [
  { path: '/', label: '仪表盘', icon: 'D' },
  { path: '/products', label: '商品管理', icon: 'P' },
  { path: '/orders', label: '订单管理', icon: 'O' },
  { path: '/categories', label: '分类管理', icon: 'C' },
  { path: '/riders', label: '骑手管理', icon: 'R' },
  { path: '/finance', label: '财务管理', icon: 'F' },
  { path: '/delivery-settings', label: '配送设置', icon: 'S' },
  { path: '/system-settings', label: '系统设置', icon: 'T' }
]

const user = computed(() => authStore.user)

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    authStore.logout()
    router.push('/login')
  }
}

onMounted(() => {
  // 获取用户信息
  authStore.getUserInfo()
})
</script>

<style scoped>
.main-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #ff6b35 0%, #ff8e53 100%);
}

.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  color: #fff;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow-y: auto;
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 100;
}

.sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(180deg, rgba(255, 107, 53, 0.1) 0%, rgba(255, 142, 83, 0.1) 100%);
  pointer-events: none;
}

.sidebar.collapsed {
  width: 80px;
}

.sidebar-header {
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  background: linear-gradient(90deg, rgba(255, 107, 53, 0.2) 0%, rgba(255, 142, 83, 0.2) 100%);
  position: relative;
  overflow: hidden;
}

.sidebar-header::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 107, 53, 0.3) 0%, transparent 70%);
  animation: rotate 20s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.sidebar-header h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(90deg, #ff6b35 0%, #ff8e53 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  position: relative;
  z-index: 1;
}

.sidebar-nav {
  padding: 16px 12px;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 12px;
  margin-bottom: 4px;
  position: relative;
  overflow: hidden;
}

.nav-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, #ff6b35 0%, #ff8e53 100%);
  opacity: 0;
  transition: opacity 0.3s;
  border-radius: 12px;
}

.nav-item:hover {
  color: #fff;
  background: rgba(255, 107, 53, 0.15);
  transform: translateX(4px);
}

.nav-item:hover::before {
  opacity: 0.1;
}

.nav-item.active {
  color: #fff;
  background: linear-gradient(90deg, #ff6b35 0%, #ff8e53 100%);
  box-shadow: 0 4px 15px rgba(255, 107, 53, 0.4);
  transform: translateX(4px);
}

.nav-item.active::before {
  opacity: 1;
}

.nav-item el-icon {
  margin-right: 12px;
  font-size: 18px;
  font-weight: 600;
  position: relative;
  z-index: 1;
}

.nav-item span {
  position: relative;
  z-index: 1;
  font-weight: 500;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #f5f7fa;
}

.top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 70px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  position: relative;
  z-index: 50;
}

.top-nav::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #ff6b35 0%, #ff8e53 100%);
}

.nav-left {
  display: flex;
  align-items: center;
}

.nav-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: linear-gradient(135deg, #ff6b35 0%, #ff8e53 100%);
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.3);
}

.user-info:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 53, 0.4);
}

.user-info el-avatar {
  margin-right: 10px;
  background: #fff;
  color: #ff6b35;
  font-weight: 700;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.user-name {
  margin-right: 4px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}

.content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  position: relative;
}

.content::before {
  content: '';
  position: fixed;
  top: 0;
  left: 240px;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 50%, rgba(255, 107, 53, 0.03) 0%, transparent 50%),
              radial-gradient(circle at 80% 80%, rgba(255, 142, 83, 0.03) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    height: 100vh;
    z-index: 1000;
  }

  .sidebar.collapsed {
    left: -240px;
  }

  .content::before {
    left: 0;
  }
}

/* 滚动条样式 */
.sidebar::-webkit-scrollbar {
  width: 6px;
}

.sidebar::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
}

.sidebar::-webkit-scrollbar-thumb {
  background: rgba(255, 107, 53, 0.5);
  border-radius: 3px;
}

.sidebar::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 107, 53, 0.7);
}

.content::-webkit-scrollbar {
  width: 8px;
}

.content::-webkit-scrollbar-track {
  background: #f5f7fa;
}

.content::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, #ff6b35 0%, #ff8e53 100%);
  border-radius: 4px;
}

.content::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, #ff8e53 0%, #ff6b35 100%);
}
</style>