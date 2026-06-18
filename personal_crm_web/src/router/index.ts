import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/auth/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      component: () => import('../views/layout/LayoutView.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('../views/placeholder/PlaceholderView.vue'),
          meta: { title: '工作台', requiresAuth: true }
        },
        {
          path: 'contacts',
          name: 'contacts',
          component: () => import('../views/placeholder/PlaceholderView.vue'),
          meta: { title: '联系人', requiresAuth: true }
        },
        {
          path: 'todos',
          name: 'todos',
          component: () => import('../views/placeholder/PlaceholderView.vue'),
          meta: { title: '事项提醒', requiresAuth: true }
        },
        {
          path: 'blacklist',
          name: 'blacklist',
          component: () => import('../views/placeholder/PlaceholderView.vue'),
          meta: { title: '黑名单', requiresAuth: true }
        },
        {
          path: 'agent',
          name: 'agent',
          component: () => import('../views/placeholder/PlaceholderView.vue'),
          meta: { title: '智能助手', requiresAuth: true }
        },
        {
          path: 'settings',
          name: 'settings',
          component: () => import('../views/placeholder/PlaceholderView.vue'),
          meta: { title: '设置', requiresAuth: true }
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/dashboard'
    }
  ]
})

// 全局路由导航守卫
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth !== false)

  if (requiresAuth) {
    if (!authStore.isLoggedIn) {
      next({ name: 'login' })
    } else {
      // 已经登录但未拉取用户信息时，自动请求 API 刷新用户信息
      if (!authStore.user) {
        try {
          await authStore.fetchUserProfile()
          next()
        } catch (error) {
          console.error('Failed to retrieve user profile, force logout:', error)
          authStore.logout()
          next({ name: 'login' })
        }
      } else {
        next()
      }
    }
  } else {
    // 登录用户访问公开页自动转去主页
    if (authStore.isLoggedIn && to.name === 'login') {
      next({ name: 'dashboard' })
    } else {
      next()
    }
  }
})

export default router
