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
            <input
              ref="searchInputRef"
              type="text"
              v-model="searchVal"
              placeholder="快捷检索联系人或事项..."
              @focus="showSearchDropdown = true; loadRecentlyViewed()"
              @blur="handleSearchBlur"
              @input="handleSearchInput"
              @keydown.down.prevent="moveSearchSelection(1)"
              @keydown.up.prevent="moveSearchSelection(-1)"
              @keydown.enter.prevent="selectActiveSearchItem"
              @keydown.esc="showSearchDropdown = false"
            />
            <span class="shortcut-tag">⌘K</span>

            <!-- 极轻量原位联想下拉框 -->
            <Transition name="dropdown-slide">
              <div v-if="showSearchDropdown" class="search-dropdown-box">
                <!-- 搜索中... -->
                <div v-if="searchLoading" class="dropdown-loading">
                  <div class="spinner-sm"></div>
                  <span>检索中...</span>
                </div>

                <template v-else-if="flatSearchItems.length > 0">
                  <div v-for="group in groupedSearchItems" :key="group.title" class="dropdown-group">
                    <div class="dropdown-group-header">{{ group.title }}</div>
                    <div class="dropdown-group-list">
                      <div
                        v-for="item in group.items"
                        :key="item.id"
                        :class="['dropdown-item-row', { active: flatSearchItems[selectedSearchIndex]?.id === item.id }]"
                        @mouseenter="selectedSearchIndex = flatSearchItems.findIndex(x => x.id === item.id)"
                        @mousedown="executeSearchItem(item)"
                      >
                        <span class="item-row-icon" v-html="item.iconSvg"></span>
                        <div class="item-row-info">
                          <span class="item-row-title">{{ item.title }}</span>
                          <span v-if="item.subtitle" class="item-row-subtitle">{{ item.subtitle }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </template>

                <!-- 空状态 -->
                <div v-else class="dropdown-empty">
                  <span>未找到匹配结果</span>
                </div>

                <!-- 友情提示指南 -->
                <div class="dropdown-tips">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M12 16v-4"/><path d="M12 8h.01"/></svg>
                  <span>支持通过姓名、拼音或电话检索联系人，亦可搜索事项内容。支持使用 ↑ ↓ 键与回车操作。</span>
                </div>
              </div>
            </Transition>
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
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { resolveAvatarUrl } from '@/utils/avatar'
import { getWeatherIconUrl } from '@/utils/weather-icons'
import { getWeather } from '@/api/weather'
import type { WeatherData } from '@/api/weather'
import { getContactsApi } from '@/api/contact'
import { getTodos } from '@/api/todo'
import type { ContactInfo } from '@/api/contact'
import type { TodoInfo } from '@/types/todo'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 使用 storeToRefs 获取响应式 user 状态
const { user } = storeToRefs(authStore)

const isCollapsed = ref<boolean>(false)
const showDropdown = ref<boolean>(false)

// 搜索栏联想状态
const searchVal = ref('')
const showSearchDropdown = ref(false)
const searchLoading = ref(false)
const selectedSearchIndex = ref(0)
const searchInputRef = ref<HTMLInputElement | null>(null)

const matchedContacts = ref<ContactInfo[]>([])
const matchedTodos = ref<TodoInfo[]>([])

// 最近查看联系人状态与加载方法
const recentlyViewed = ref<Array<{ id: any; name: string }>>([])
const loadRecentlyViewed = () => {
  try {
    const stored = localStorage.getItem('recently_viewed_contacts')
    if (stored) {
      recentlyViewed.value = JSON.parse(stored)
    } else {
      recentlyViewed.value = []
    }
  } catch (e) {
    console.error('Failed to parse recently viewed contacts:', e)
    recentlyViewed.value = []
  }
}

// 转换最近查看联系人为下拉项
const mappedRecentlyViewed = computed(() => {
  return recentlyViewed.value.map(c => ({
    id: `recent-${c.id}`,
    title: c.name,
    subtitle: '最近查看的联系人',
    action: () => router.push(`/contacts/${c.id}`),
    iconSvg: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>'
  }))
})

// 转换联系人为下拉项
const mappedContacts = computed(() => {
  return matchedContacts.value.map(c => ({
    id: `contact-${c.contactId}`,
    title: c.name,
    subtitle: `电话: ${c.phone || '-'}`,
    action: () => router.push(`/contacts/${c.contactId}`),
    iconSvg: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>'
  }))
})

// 转换事项为下拉项
const mappedTodos = computed(() => {
  return matchedTodos.value.map(t => ({
    id: `todo-${t.matterId}`,
    title: t.content,
    subtitle: `日程: ${t.todoTime.substring(0, 10)}`,
    action: () => router.push(`/todos?keyword=${encodeURIComponent(t.content)}`),
    iconSvg: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="18" x="3" y="4" rx="2"/><path d="M3 10h18"/><path d="m9 16 2 2 4-4"/></svg>'
  }))
})

// 静态常用操作（精简版），在搜索框为空且处于激活态时展示
const staticCommands = [
  {
    id: 'cmd-contact-new',
    title: '新建联系人',
    subtitle: '录入新的人脉、生日与关系分类标签',
    action: () => router.push('/contacts/new'),
    iconSvg: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><line x1="19" y1="8" x2="19" y2="14"/><line x1="16" y1="11" x2="22" y2="11"/></svg>'
  },
  {
    id: 'cmd-todo-new',
    title: '新增事项提醒',
    subtitle: '新建与特定联系人关联的日程提醒',
    action: () => router.push('/todos/new'),
    iconSvg: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M8 2v4"></path><path d="M16 2v4"></path><rect width="18" height="18" x="3" y="4" rx="2"></rect><path d="M3 10h18"/><line x1="12" y1="14" x2="12" y2="20"></line><line x1="9" y1="17" x2="15" y2="17"/></svg>'
  }
]

// 扁平结果列表
const flatSearchItems = computed(() => {
  if (!searchVal.value.trim()) {
    return [...mappedRecentlyViewed.value, ...staticCommands]
  }
  return [...mappedContacts.value, ...mappedTodos.value]
})

// 分组结果列表
const groupedSearchItems = computed(() => {
  if (!searchVal.value.trim()) {
    const groups = []
    if (mappedRecentlyViewed.value.length > 0) {
      groups.push({ title: '最近查看', items: mappedRecentlyViewed.value })
    }
    groups.push({ title: '常用操作', items: staticCommands })
    return groups
  }

  const groups = []
  if (mappedContacts.value.length > 0) {
    groups.push({ title: '联系人', items: mappedContacts.value })
  }
  if (mappedTodos.value.length > 0) {
    groups.push({ title: '日程事项', items: mappedTodos.value })
  }
  return groups
})

watch(flatSearchItems, () => {
  selectedSearchIndex.value = 0
})

// 监听输入，进行防抖查询
let searchTimer: number | null = null
const handleSearchInput = () => {
  if (searchTimer) clearTimeout(searchTimer)
  selectedSearchIndex.value = 0
  
  const kw = searchVal.value.trim()
  if (!kw) {
    matchedContacts.value = []
    matchedTodos.value = []
    searchLoading.value = false
    return
  }

  searchLoading.value = true
  searchTimer = window.setTimeout(async () => {
    try {
      const [contactsRes, todosRes] = await Promise.all([
        getContactsApi({ keyword: kw, pageSize: 5 }),
        getTodos({ keyword: kw, pageSize: 5 })
      ])
      matchedContacts.value = contactsRes.list || []
      matchedTodos.value = todosRes.list || []
    } catch (e) {
      console.error('Dropdown search error:', e)
    } finally {
      searchLoading.value = false
    }
  }, 250)
}

// 延迟收起下拉框以确保点击/mousedown事件能正常触发
const handleSearchBlur = () => {
  setTimeout(() => {
    showSearchDropdown.value = false
  }, 200)
}

// 键盘操作
const moveSearchSelection = (dir: number) => {
  const len = flatSearchItems.value.length
  if (len === 0) return
  selectedSearchIndex.value = (selectedSearchIndex.value + dir + len) % len
}

const selectActiveSearchItem = () => {
  const item = flatSearchItems.value[selectedSearchIndex.value]
  if (item) {
    executeSearchItem(item)
  }
}

const executeSearchItem = (item: any) => {
  item.action()
  showSearchDropdown.value = false
  searchVal.value = ''
}

// 监听全局 ⌘K / Ctrl+K 聚焦到顶部搜索框
const handleGlobalKeydown = (e: KeyboardEvent) => {
  const isK = e.key === 'k' || e.key === 'K'
  const isMetaOrCtrl = e.metaKey || e.ctrlKey
  if (isK && isMetaOrCtrl) {
    e.preventDefault()
    searchInputRef.value?.focus()
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleGlobalKeydown)
  loadRecentlyViewed()
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleGlobalKeydown)
})

// 天气逻辑
const weatherData = ref<WeatherData | null>(null)
const GEOLOCATION_TIMEOUT_MS = 2000

const getBrowserCoordinates = (): Promise<{ latitude: number; longitude: number }> => {
  if (typeof window === 'undefined' || !('geolocation' in navigator)) {
    return Promise.reject(new Error('Geolocation is not supported by this browser.'))
  }

  return new Promise((resolve, reject) => {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        resolve({
          latitude: position.coords.latitude,
          longitude: position.coords.longitude
        })
      },
      (error) => {
        reject(error)
      },
      {
        enableHighAccuracy: false,
        timeout: GEOLOCATION_TIMEOUT_MS,
        maximumAge: 10 * 60 * 1000
      }
    )
  })
}

const loadWeather = async () => {
  if (route.path !== '/dashboard' || weatherData.value) {
    return
  }

  try {
    const coordinates = await getBrowserCoordinates()
    weatherData.value = await getWeather(coordinates)
    return
  } catch (error) {
    console.warn('Falling back to IP-based weather lookup:', error)
  }

  try {
    weatherData.value = await getWeather()
  } catch (error) {
    console.error('Failed to load topbar weather:', error)
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

watch(
  () => route.path,
  (path) => {
    if (path === '/dashboard') {
      void loadWeather()
    }
  },
  { immediate: true }
)
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

/* 下拉联想容器 */
.search-dropdown-box {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  width: 100%;
  min-width: 320px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(226, 232, 240, 0.85);
  border-radius: var(--radius-md);
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.05);
  z-index: 1000;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  transform-origin: top;
}

/* 检索中提示 */
.dropdown-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px 0;
  color: var(--text-muted);
  font-size: 12px;
}

.spinner-sm {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(99, 102, 241, 0.15);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 分组样式 */
.dropdown-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.dropdown-group-header {
  font-size: 10px;
  font-weight: 700;
  color: var(--text-muted);
  padding: 4px 8px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 1px solid rgba(241, 245, 249, 0.6);
  margin-bottom: 2px;
}

.dropdown-group-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

/* 单行记录样式 */
.dropdown-item-row {
  display: flex;
  align-items: center;
  padding: 8px 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: all var(--transition-fast);
  border: 1px solid transparent;
  gap: 10px;
}

.dropdown-item-row.active {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.05) 0%, rgba(168, 85, 247, 0.05) 100%);
  border-color: rgba(99, 102, 241, 0.1);
  box-shadow: var(--shadow-sm);
}

.item-row-icon {
  width: 24px;
  height: 24px;
  background: rgba(241, 245, 249, 0.8);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.dropdown-item-row.active .item-row-icon {
  background: var(--color-primary-light);
  color: var(--color-primary);
}

.search-dropdown-box :deep(svg) {
  position: static !important;
  transform: none !important;
  width: 14px !important;
  height: 14px !important;
  color: inherit !important;
  pointer-events: none !important;
}

.item-row-info {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-width: 0;
  gap: 8px;
}

.item-row-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-main);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dropdown-item-row.active .item-row-title {
  color: var(--color-primary);
}

.item-row-subtitle {
  font-size: 11px;
  color: var(--text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 空状态 */
.dropdown-empty {
  padding: 16px;
  text-align: center;
  color: var(--text-muted);
  font-size: 12px;
}

/* 下拉动画 */
.dropdown-slide-enter-active,
.dropdown-slide-leave-active {
  transition: transform 0.2s cubic-bezier(0.16, 1, 0.3, 1), opacity 0.2s ease;
}

.dropdown-slide-enter-from,
.dropdown-slide-leave-to {
  transform: scaleY(0.95);
  opacity: 0;
}

/* 友情检索提示 */
.dropdown-tips {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 6px 2px;
  background: transparent;
  border-radius: 0;
  color: var(--text-muted);
  font-size: 11px;
  line-height: 1.4;
  font-weight: normal;
  border: none;
  border-top: 1px dashed rgba(226, 232, 240, 0.8);
  margin-top: 4px;
}

.dropdown-tips :deep(svg),
.dropdown-tips svg {
  width: 12px !important;
  height: 12px !important;
  color: var(--text-light) !important;
  flex-shrink: 0;
}
</style>

