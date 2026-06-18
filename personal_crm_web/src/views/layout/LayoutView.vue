<template>
  <div class="app-container">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="sidebar-brand">
        <div class="brand-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
          </svg>
        </div>
        <div class="brand-info">
          <span class="brand-name">Personal CRM</span>
          <span class="brand-desc">智能联系人管理平台</span>
        </div>
      </div>

      <ul class="sidebar-menu">
        <li
          v-for="item in menuItems"
          :key="item.path"
          class="menu-item"
          :class="{ active: isLinkActive(item.path) }"
        >
          <router-link :to="item.path">
            <svg class="menu-svg-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="item.iconSvg"></svg>
            <span>{{ item.title }}</span>
          </router-link>
        </li>
      </ul>

      <!-- 折叠按钮 -->
      <button class="collapsed-toggle" type="button" @click="toggleSidebar">
        <svg v-if="isCollapsed" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="width: 14px; height: 14px;"><polyline points="9 18 15 12 9 6"/></svg>
        <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="width: 14px; height: 14px;"><polyline points="15 18 9 12 15 6"/></svg>
      </button>

      <div class="sidebar-footer">
        <div class="upgrade-card">
          <div class="upgrade-card-title">升级至专业版</div>
          <div class="upgrade-card-desc">解锁 AI 高级分析与批量人脉管理功能。</div>
        </div>
        <div class="user-profile-bar">
          <div class="user-avatar-container">
            <img class="user-avatar" :src="avatarSrc || defaultAvatar" alt="Avatar">
            <span class="user-online-dot"></span>
          </div>
          <div class="user-meta">
            <span class="user-name">{{ user?.username || 'Ethan' }}</span>
            <span class="user-email">{{ user?.username || 'ethan' }}@example.com</span>
          </div>
        </div>
      </div>
    </aside>

    <!-- 主工作区 -->
    <main class="main-content main-wrapper">
      <!-- 顶栏 -->
      <header class="topbar">
        <div class="topbar-left">
          <div class="page-title-group">
            <a v-if="showBackButton" href="#" class="back-link" @click.prevent="goBack">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 16px; height: 16px;"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg>
            </a>
            <h1 class="page-title">{{ pageTitle }}</h1>
          </div>
          <p class="page-subtitle">{{ pageSubtitle }}</p>
        </div>

        <div class="topbar-right">
          <!-- 搜索框 -->
          <div class="search-box">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
            <input type="text" placeholder="快捷检索联系人或事项..." />
            <span class="shortcut-tag">⌘K</span>
          </div>

          <!-- 通知铃铛 -->
          <button class="notification-bell" type="button" @click="tipFeature">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 18px; height: 18px;"><path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/><path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/></svg>
            <span class="notification-badge">3</span>
          </button>

          <!-- 用户下拉 -->
          <div class="user-dropdown">
            <div class="user-dropdown-trigger" @click="toggleDropdown">
              <img class="avatar-round" :src="avatarSrc || defaultAvatar" alt="Avatar">
              <svg class="dropdown-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="6 9 12 15 18 9"/></svg>
            </div>
            <!-- 使用原生 class 彻底复用 styles.css 动画与显示隐藏 -->
            <div class="dropdown-menu" :class="{ show: showDropdown }">
              <a href="#" class="dropdown-item" @click.prevent="goToSettings">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>
                个人设置
              </a>
              <a href="#" class="dropdown-item text-danger" @click.prevent="handleLogout">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                退出登录
              </a>
            </div>
          </div>
        </div>
      </header>

      <!-- 核心页面视图 -->
      <div class="content-body">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 使用 storeToRefs 获取响应式 user 状态
const { user } = storeToRefs(authStore)

const isCollapsed = ref<boolean>(false)
const showDropdown = ref<boolean>(false)

const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=80&auto=format&fit=crop&q=80'

// 侧边栏折叠切换
const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

// 顶部下拉列表切换
const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
}

// 侧边栏一级路由配置 (内置 Lucide SVG 代码)
const menuItems = [
  {
    path: '/dashboard',
    title: '工作台',
    iconSvg: '<rect x="3" y="3" width="7" height="9" rx="1"/><rect x="14" y="3" width="7" height="5" rx="1"/><rect x="14" y="12" width="7" height="9" rx="1"/><rect x="3" y="16" width="7" height="5" rx="1"/>'
  },
  {
    path: '/contacts',
    title: '联系人',
    iconSvg: '<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>'
  },
  {
    path: '/todos',
    title: '事项提醒',
    iconSvg: '<rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>'
  },
  {
    path: '/blacklist',
    title: '黑名单',
    iconSvg: '<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><line x1="4.93" y1="4.93" x2="19.07" y2="19.07"/>'
  },
  {
    path: '/agent',
    title: '智能助手',
    iconSvg: '<path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z"/><path d="M20 3v4m2-2h-4"/>'
  },
  {
    path: '/settings',
    title: '设置',
    iconSvg: '<circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/>'
  }
]

// 路由判定高亮激活
const isLinkActive = (path: string) => {
  if (path === '/dashboard') {
    return route.path === '/dashboard'
  }
  return route.path.startsWith(path)
}

const showBackButton = computed(() => {
  // 当不在一级主页路由下时展示返回键
  return !['/dashboard', '/contacts', '/todos', '/blacklist', '/agent', '/settings'].includes(route.path)
})

const goBack = () => {
  router.back()
}

// 动态大标题
const pageTitle = computed(() => {
  return (route.meta.title as string) || '工作台'
})

// 动态副说明文字
const pageSubtitle = computed(() => {
  switch (route.path) {
    case '/dashboard':
      return '智能数据看板与事项压力提示'
    case '/contacts':
      return '全面检索、关系筛选与分组操作'
    case '/todos':
      return '待办跟踪、状态流转与逾期提醒中心'
    case '/blacklist':
      return '高风险联系人隔离与历史留痕视图'
    case '/agent':
      return 'AI Agent 自然语言人脉分析与写操作二次确认'
    case '/settings':
      return '配置个人信息、头像档案及账号安全'
    default:
      return '管理您的 Personal CRM 账户'
  }
})

// 计算头像
const avatarSrc = computed(() => {
  if (user.value?.avatarUrl) {
    if (user.value.avatarUrl.startsWith('http')) {
      return user.value.avatarUrl
    }
    return `http://localhost:8080${user.value.avatarUrl}`
  }
  return ''
})

const goToSettings = () => {
  showDropdown.value = false
  router.push('/settings')
}

const handleLogout = () => {
  showDropdown.value = false
  authStore.logout()
  ElMessage.success('您已安全退出系统')
  router.push('/login')
}

const tipFeature = () => {
  ElMessage.info('通知中心属于 Phase 2 扩展范围，目前仅做占位展示')
}
</script>

<style scoped>
/* 使用 .app-container 类自动套用 styles.css 中的全局框架排版样式，此处做微小局部校正 */
.app-container {
  overflow: hidden;
  height: 100vh;
  width: 100vw;
}

.menu-svg-icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

/* 顶部过渡动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.2s ease;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateY(-8px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateY(8px);
}
</style>
