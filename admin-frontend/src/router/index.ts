import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import type { Router } from 'vue-router'

// 路由配置
const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/dashboard/index.vue')
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('../views/products/index.vue')
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('../views/orders/index.vue')
      },
      {
        path: 'categories',
        name: 'Categories',
        component: () => import('../views/categories/index.vue')
      },
      {
        path: 'riders',
        name: 'Riders',
        component: () => import('../views/riders/index.vue')
      },
      {
        path: 'finance',
        name: 'Finance',
        component: () => import('../views/finance/index.vue')
      },
      {
        path: 'delivery-settings',
        name: 'DeliverySettings',
        component: () => import('../views/delivery-settings/index.vue')
      },
      {
        path: 'system-settings',
        name: 'SystemSettings',
        component: () => import('../views/system-settings/index.vue')
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router: Router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const token = localStorage.getItem('token')

  if (requiresAuth && !token) {
    next({ name: 'Login' })
  } else {
    next()
  }
})

export default router