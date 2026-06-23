<template>
  <div class="app-container">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="sidebar-menu-wrapper">
        <div class="sidebar-brand">
          <div class="brand-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M18 21a8 8 0 0 0-16 0" />
              <circle cx="10" cy="8" r="5" />
              <path d="M22 20c0-3.37-2-6.5-4-8a5 5 0 0 0-.45-8.3" />
            </svg>
          </div>
          <div class="brand-info">
            <span class="brand-name">Personal CRM</span>
            <span class="brand-desc">智能联系人管理系统</span>
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
      </div>

      <div class="sidebar-footer">
        <div class="upgrade-card">
          <h4 class="upgrade-card-title">升级到 Pro 专业版</h4>
          <p class="upgrade-card-desc">解锁更多智能分析与多端同步功能</p>
        </div>

        <router-link to="/settings" class="user-profile-bar">
          <div class="user-avatar-container">
            <img class="user-avatar" :src="avatarSrc || defaultAvatar" alt="Avatar">
            <span class="user-online-dot"></span>
          </div>
          <div class="user-meta">
            <span class="user-name">{{ user?.username ? (user.username.charAt(0).toUpperCase() + user.username.slice(1)) : 'Ethan' }}</span>
            <span class="user-email">{{ user?.username || 'ethan' }}@example.com</span>
          </div>
        </router-link>

        <button class="collapsed-toggle" type="button" @click="toggleSidebar" :aria-label="isCollapsed ? '展开侧边栏' : '折叠侧边栏'">
          <svg v-if="isCollapsed" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" class="toggle-icon"><polyline points="9 18 15 12 9 6"/></svg>
          <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" class="toggle-icon"><polyline points="15 18 9 12 15 6"/></svg>
        </button>
      </div>
    </aside>

    <!-- 主工作区 -->
    <main class="main-content main-wrapper">
      <!-- 顶栏 -->
      <header class="topbar">
        <div class="topbar-left">
          <div class="page-title-group">
            <a v-if="showBackButton" href="#" class="back-link" @click.prevent="goBack" :aria-label="backLabel">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="back-icon"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg>
            </a>
            <h1 class="page-title">
              {{ pageTitle }}
              <el-popover
                v-if="route.path === '/dashboard' && weatherData"
                placement="bottom"
                :width="290"
                trigger="click"
                popper-class="weather-forecast-popover"
                :teleported="true"
              >
                <template #reference>
                  <span class="topbar-weather interactive">
                    <img :src="getWeatherIconUrl(weatherData.currentIcon)" :alt="weatherData.currentText" class="topbar-weather-icon" />
                    <span class="topbar-weather-temp">{{ weatherData.currentTemp }}°C</span>
                    <span class="topbar-weather-text">{{ weatherData.currentText }}</span>
                    <span class="topbar-weather-city">{{ weatherData.cityName }}</span>
                  </span>
                </template>
                
                <div class="weather-forecast-container">
                  <div class="forecast-header">
                    <span class="forecast-city">{{ weatherData.cityName }}</span>
                    <span class="forecast-title">未来三日预报</span>
                  </div>
                  <div class="forecast-list">
                    <div v-for="item in weatherData.dailyForecast" :key="item.date" class="forecast-item">
                      <div class="forecast-date-group">
                        <span class="forecast-date">{{ formatForecastDate(item.date) }}</span>
                      </div>
                      <div class="forecast-status">
                        <img :src="getWeatherIconUrl(item.iconDay)" :alt="item.textDay" class="forecast-weather-icon" />
                        <span class="forecast-text">{{ item.textDay }}</span>
                      </div>
                      <div class="forecast-temp-range">
                        <span class="temp-min">{{ item.tempMin }}°C</span>
                        <span class="temp-sep">~</span>
                        <span class="temp-max">{{ item.tempMax }}°C</span>
                      </div>
                    </div>
                  </div>
                </div>
              </el-popover>
            </h1>
          </div>
          <p class="page-subtitle">{{ pageSubtitle }}</p>
        </div>

        <div v-if="!showBackButton && route.path !== '/settings'" class="topbar-right">
          <div v-if="pageActions.length > 0" class="topbar-actions">
            <router-link
              v-for="action in pageActions"
              :key="action.path"
              :to="action.path"
              class="btn"
              :class="action.variant"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" class="topbar-action-icon" v-html="action.iconSvg"></svg>
              {{ action.label }}
            </router-link>
          </div>

          <template v-else>
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
          </template>
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
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { resolveAvatarUrl } from '@/utils/avatar'
import { getWeatherIconUrl } from '@/utils/weather-icons'
import { getWeather } from '@/api/weather'
import type { WeatherData } from '@/api/weather'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 使用 storeToRefs 获取响应式 user 状态
const { user } = storeToRefs(authStore)

const isCollapsed = ref<boolean>(false)
const showDropdown = ref<boolean>(false)

// 天气逻辑
const weatherData = ref<WeatherData | null>(null)
const loadWeather = async () => {
  try {
    weatherData.value = await getWeather()
  } catch (e) {
    console.error('Failed to load topbar weather:', e)
  }
}

const formatForecastDate = (dateStr: string): string => {
  if (!dateStr) return ''
  try {
    const date = new Date(dateStr)
    const today = new Date()
    const tomorrow = new Date(today)
    tomorrow.setDate(today.getDate() + 1)
    const afterTomorrow = new Date(today)
    afterTomorrow.setDate(today.getDate() + 2)

    const isToday = date.toDateString() === today.toDateString()
    const isTomorrow = date.toDateString() === tomorrow.toDateString()
    const isAfterTomorrow = date.toDateString() === afterTomorrow.toDateString()

    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const dateFormatted = `${month}/${day}`

    if (isToday) return `今天 (${dateFormatted})`
    if (isTomorrow) return `明天 (${dateFormatted})`
    if (isAfterTomorrow) return `后天 (${dateFormatted})`
    
    const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    return `${weekdays[date.getDay()]} (${dateFormatted})`
  } catch (e) {
    return dateStr
  }
}

const defaultAvatar = 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=80&auto=format&fit=crop&q=80'

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
    iconSvg: '<rect width="7" height="9" x="3" y="3" rx="1"></rect><rect width="7" height="5" x="14" y="3" rx="1"></rect><rect width="7" height="9" x="14" y="12" rx="1"></rect><rect width="7" height="5" x="3" y="16" rx="1"></rect>'
  },
  {
    path: '/contacts',
    title: '联系人',
    iconSvg: '<path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path><path d="M16 3.128a4 4 0 0 1 0 7.744"></path><path d="M22 21v-2a4 4 0 0 0-3-3.87"></path><circle cx="9" cy="7" r="4"></circle>'
  },
  {
    path: '/todos',
    title: '事项提醒',
    iconSvg: '<path d="M8 2v4"></path><path d="M16 2v4"></path><rect width="18" height="18" x="3" y="4" rx="2"></rect><path d="M3 10h18"></path><path d="m9 16 2 2 4-4"></path>'
  },
  {
    path: '/blacklist',
    title: '黑名单',
    iconSvg: '<path d="M20 13c0 5-3.5 7.5-7.66 8.95a1 1 0 0 1-.67-.01C7.5 20.5 4 18 4 13V6a1 1 0 0 1 1-1c2 0 4.5-1.2 6.24-2.72a1.17 1.17 0 0 1 1.52 0C14.51 3.81 17 5 19 5a1 1 0 0 1 1 1z"></path><path d="M12 8v4"></path><path d="M12 16h.01"></path>'
  },
  {
    path: '/agent',
    title: '智能助手',
    iconSvg: '<path d="M11.017 2.814a1 1 0 0 1 1.966 0l1.051 5.558a2 2 0 0 0 1.594 1.594l5.558 1.051a1 1 0 0 1 0 1.966l-5.558 1.051a2 2 0 0 0-1.594 1.594l-1.051 5.558a1 1 0 0 1-1.966 0l-1.051-5.558a2 2 0 0 0-1.594-1.594l-5.558-1.051a1 1 0 0 1 0-1.966l5.558-1.051a2 2 0 0 0 1.594-1.594z"></path><path d="M20 2v4"></path><path d="M22 4h-4"></path><circle cx="4" cy="20" r="2"></circle>'
  },
  {
    path: '/settings',
    title: '设置',
    iconSvg: '<path d="M9.671 4.136a2.34 2.34 0 0 1 4.659 0 2.34 2.34 0 0 0 3.319 1.915 2.34 2.34 0 0 1 2.33 4.033 2.34 2.34 0 0 0 0 3.831 2.34 2.34 0 0 1-2.33 4.033 2.34 2.34 0 0 0-3.319 1.915 2.34 2.34 0 0 1-4.659 0 2.34 2.34 0 0 0-3.32-1.915 2.34 2.34 0 0 1-2.33-4.033 2.34 2.34 0 0 0 0-3.831A2.34 2.34 0 0 1 6.35 6.051a2.34 2.34 0 0 0 3.319-1.915"></path><circle cx="12" cy="12" r="3"></circle>'
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

const backLabel = computed(() => {
  if (route.path.startsWith('/contacts/')) {
    return '返回联系人列表'
  }
  if (route.path.startsWith('/todos/')) {
    return '返回事项提醒列表'
  }
  return '返回上一页'
})

const goBack = () => {
  router.back()
}

const getFormattedToday = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  const weekday = weekdays[date.getDay()]
  return `${year}年${month}月${day}日${weekday}`
}

// 动态大标题
const pageTitle = computed(() => {
  if (route.path === '/dashboard') {
    return `下午好，${user.value?.username || 'Ethan'}`
  }
  const routeTitle = (route.meta.title as string) || '工作台'
  if (route.path === '/contacts') {
    return '联系人管理'
  }
  return routeTitle
})

// 动态副说明文字
const pageSubtitle = computed(() => {
  if (route.path === '/dashboard') {
    return getFormattedToday()
  }
  switch (route.path) {
    case '/contacts':
      return '维护联系人资料、标签与提醒事项'
    case '/contacts/new':
      return '一屏录入联系人基础资料、头像预览与关系标签'
    case '/todos':
      return '待办跟踪、状态流转与逾期提醒中心'
    case '/blacklist':
      return '高风险联系人隔离与历史留痕视图'
    case '/agent':
      return 'AI Agent 自然语言人脉分析与写操作二次确认'
    case '/settings':
      return '修改个人资料、更换头像及系统退出'
    default:
      if (route.name === 'contact-detail') {
        return '查看联系人详细资料与历史往来'
      }
      if (route.name === 'contact-edit') {
        return '更新联系人基础资料、头像预览与关系标签'
      }
      return '管理您的 Personal CRM 账户'
  }
})

const pageActions = computed(() => {
  if (route.path === '/contacts') {
    return [
      {
        path: '/contacts/new',
        label: '新增联系人',
        variant: 'btn-primary',
        iconSvg: '<path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><line x1="19" y1="8" x2="19" y2="14"/><line x1="16" y1="11" x2="22" y2="11"/>'
      }
    ]
  }

  return []
})

// 计算头像
const avatarSrc = computed(() => {
  if (user.value?.avatarUrl) {
    return resolveAvatarUrl(user.value.avatarUrl)
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

onMounted(() => {
  loadWeather()
})
</script>

<style scoped>
/* 使用 .app-container 类自动套用 styles.css 中的全局框架排版样式，此处做微小局部校正 */
.app-container {
  overflow: hidden;
  height: 100vh;
  width: 100vw;
}

.sidebar-menu-wrapper {
  display: flex;
  flex: 1;
  flex-direction: column;
  min-height: 0;
}

.menu-svg-icon {
  width: 24px;
  height: 24px;
  flex-shrink: 0;
}

.toggle-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.back-icon,
.topbar-action-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}


.topbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.topbar-actions .btn {
  min-height: 40px;
}

.topbar-action-icon {
  margin-right: 4px;
  display: inline-block;
  vertical-align: middle;
}

.topbar-weather {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-muted);
  background: rgba(241, 245, 249, 0.8);
  padding: 4px 10px;
  border-radius: var(--radius-full);
  margin-left: 14px;
  vertical-align: middle;
  border: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: var(--shadow-sm);
}

.topbar-weather.interactive {
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.topbar-weather.interactive:hover {
  background: rgba(226, 232, 240, 0.95);
  transform: translateY(-0.5px);
  border-color: rgba(99, 102, 241, 0.3);
  box-shadow: var(--shadow-md);
}

.topbar-weather-icon {
  width: 18px;
  height: 18px;
  object-fit: contain;
}

.topbar-weather-temp {
  color: var(--text-main);
  font-weight: 600;
}

.topbar-weather-text {
  color: var(--text-muted);
}

.topbar-weather-city {
  color: var(--color-primary);
  font-weight: 600;
  font-size: 11px;
  background: var(--color-primary-light);
  padding: 1px 6px;
  border-radius: 4px;
}

/* 天气气泡卡片自定义样式 */
.weather-forecast-popover {
  padding: 14px !important;
  background: rgba(255, 255, 255, 0.85) !important;
  backdrop-filter: blur(16px) !important;
  -webkit-backdrop-filter: blur(16px) !important;
  border-radius: 14px !important;
  border: 1px solid rgba(226, 232, 240, 0.8) !important;
  box-shadow: var(--shadow-lg) !important;
}

.weather-forecast-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
  color: var(--text-main);
}

.forecast-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  padding-bottom: 6px;
}

.forecast-city {
  font-weight: 700;
  font-size: 13px;
  color: var(--color-primary);
}

.forecast-title {
  font-size: 11px;
  color: var(--text-muted);
  background: rgba(241, 245, 249, 0.8);
  padding: 2px 6px;
  border-radius: 4px;
}

.forecast-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.forecast-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 8px;
  background: rgba(248, 250, 252, 0.5);
  border-radius: 8px;
  border: 1px solid rgba(241, 245, 249, 0.6);
  transition: all 0.2s ease;
}

.forecast-item:hover {
  background: rgba(241, 245, 249, 0.95);
  transform: translateY(-1px);
  border-color: rgba(99, 102, 241, 0.15);
}

.forecast-date-group {
  font-size: 11px;
  font-weight: 500;
  color: var(--text-main);
  min-width: 75px;
}

.forecast-status {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
  justify-content: flex-start;
  padding-left: 8px;
}

.forecast-weather-icon {
  width: 18px;
  height: 18px;
  object-fit: contain;
}

.forecast-text {
  font-size: 11px;
  color: var(--text-muted);
}

.forecast-temp-range {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 11px;
  font-weight: 600;
  justify-content: flex-end;
  min-width: 80px;
}

.temp-min {
  color: var(--text-muted);
}

.temp-sep {
  color: var(--text-light);
  font-weight: normal;
}

.temp-max {
  color: var(--color-primary);
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
