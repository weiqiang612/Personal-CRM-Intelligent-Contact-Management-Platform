<template>
  <div class="contacts-page-wrapper">
    <!-- 顶栏下的辅助说明与标题已经在 LayoutView 中，本页直接承载主体 -->
    <!-- 检索与工具栏 -->
    <section class="card filter-bar" style="padding: 16px 24px;">
      <div class="filter-left">
        <div class="search-box">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="search-icon">
            <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
          <input
            type="text"
            v-model="queryParams.keyword"
            placeholder="搜索姓名、手机号或微信"
            @input="handleSearch"
            style="background-color: transparent;"
          >
        </div>
        
        <select class="select-control" v-model="queryParams.gender" @change="handleFilterChange">
          <option value="">性别</option>
          <option value="1">男</option>
          <option value="2">女</option>
        </select>

        <select class="select-control" v-model="queryParams.tag" @change="handleFilterChange">
          <option value="">全部标签</option>
          <option value="同学">同学</option>
          <option value="朋友">朋友</option>
          <option value="重要">重要</option>
          <option value="实习">实习</option>
        </select>

        <select class="select-control" v-model="sortConfig" @change="handleSortChange">
          <option value="createdAt-desc">创建时间 (降序)</option>
          <option value="createdAt-asc">创建时间 (升序)</option>
          <option value="birthday-desc">生日 (降序)</option>
          <option value="birthday-asc">生日 (升序)</option>
        </select>
        
        <button class="btn btn-secondary btn-icon" title="重置筛选" @click="resetFilters">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;">
            <path d="M21.5 2v6h-6M21.34 15.57a10 10 0 1 1-.57-8.38l5.67-5.67"/>
          </svg>
        </button>
      </div>

      <!-- 天气小组件 -->
      <div class="weather-entry" ref="weatherRef">
        <button
          type="button"
          class="weather-trigger"
          :aria-expanded="weatherOpen"
          @click="toggleWeather"
        >
          <span class="weather-trigger-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;">
              <path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M6.34 17.66l-1.41 1.41M19.07 4.93l-1.41 1.41"/>
              <path d="M12 8a4 4 0 1 0 0 8 4 4 0 0 0 0-8z"/>
            </svg>
          </span>
          <span class="weather-trigger-text">
            <span class="weather-trigger-location">杭州天气</span>
            <span class="weather-trigger-summary">26°C · 多云</span>
          </span>
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="weather-trigger-chevron">
            <polyline points="6 9 12 15 18 9"/>
          </svg>
        </button>

        <div class="weather-panel" :class="{ open: weatherOpen }">
          <div class="weather-panel-header">
            <div>
              <p class="weather-panel-title">杭州天气</p>
              <div class="weather-panel-subtitle">联系人页内查看近三天天气</div>
            </div>
            <span class="weather-panel-state">天气良好</span>
          </div>

          <div class="weather-list">
            <div class="weather-day">
              <div class="weather-day-info">
                <span class="weather-day-label">今天</span>
                <span class="weather-day-date">6月18日 周四</span>
              </div>
              <span class="weather-day-condition">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;display:inline-block;vertical-align:middle;margin-right:4px;">
                  <path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M6.34 17.66l-1.41 1.41M19.07 4.93l-1.41 1.41"/><circle cx="12" cy="12" r="4"/>
                </svg>
                多云
              </span>
              <span class="weather-day-temp">22°C - 29°C</span>
            </div>

            <div class="weather-day">
              <div class="weather-day-info">
                <span class="weather-day-label">明天</span>
                <span class="weather-day-date">6月19日 周五</span>
              </div>
              <span class="weather-day-condition">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;display:inline-block;vertical-align:middle;margin-right:4px;color:#3b82f6;">
                  <path d="M20 17.58A5 5 0 0 0 18 8h-1.26A8 8 0 1 0 4 16.25"/><path d="M8 16v6M12 16v6M16 16v6"/>
                </svg>
                阵雨
              </span>
              <span class="weather-day-temp">23°C - 27°C</span>
            </div>

            <div class="weather-day">
              <div class="weather-day-info">
                <span class="weather-day-label">后天</span>
                <span class="weather-day-date">6月20日 周六</span>
              </div>
              <span class="weather-day-condition">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;display:inline-block;vertical-align:middle;margin-right:4px;color:#f59e0b;">
                  <circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M6.34 17.66l-1.41 1.41M19.07 4.93l-1.41 1.41"/>
                </svg>
                晴
              </span>
              <span class="weather-day-temp">24°C - 31°C</span>
            </div>
          </div>

          <div class="weather-panel-note">后续可接入真实天气 API，当前以静态高拟真渲染。</div>
        </div>
      </div>
    </section>

    <!-- 联系人数据表格 -->
    <section class="card" style="padding: 0; position: relative;" v-loading="loading">
      <div class="table-container" style="min-height: 200px;">
        <table class="custom-table" v-if="contacts.length > 0">
          <thead>
            <tr>
              <th style="padding-left: 24px;">头像</th>
              <th>姓名</th>
              <th>性别</th>
              <th>手机号</th>
              <th>邮箱</th>
              <th>标签</th>
              <th>生日</th>
              <th>状态</th>
              <th style="padding-right: 24px; text-align: right;">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in contacts" :key="row.contactId">
              <td style="padding-left: 24px;">
                <img :src="row.avatarUrl || defaultAvatar" :alt="row.name" class="avatar-round">
              </td>
              <td>
                <router-link :to="`/contacts/${row.contactId}`" class="avatar-name">{{ row.name }}</router-link>
              </td>
              <td>{{ formatGender(row.gender) }}</td>
              <td>{{ formatPhone(row.phone) }}</td>
              <td>{{ row.email || '-' }}</td>
              <td>
                <template v-if="row.tags && row.tags.length > 0">
                  <span
                    v-for="t in row.tags"
                    :key="t"
                    :class="['badge', getTagClass(t)]"
                    style="margin-right: 4px;"
                  >
                    {{ t }}
                  </span>
                </template>
                <span v-else class="text-muted" style="font-size: 11px; opacity: 0.5;">-</span>
              </td>
              <td>{{ row.birthday || '-' }}</td>
              <td>
                <span class="badge badge-status-normal">正常</span>
              </td>
              <td style="padding-right: 24px; text-align: right;">
                <div class="action-cell-right">
                  <router-link :to="`/contacts/${row.contactId}`" class="btn btn-secondary btn-sm">查看</router-link>
                  <router-link :to="`/contacts/${row.contactId}/edit`" class="btn btn-secondary btn-sm">编辑</router-link>
                  <button class="btn btn-secondary btn-sm" @click="handleTodoClick">事项</button>
                  
                  <!-- 拉黑确认框 -->
                  <div class="popconfirm-container" style="display: inline-block; position: relative;">
                    <button class="btn btn-danger-outline btn-sm" @click="openBlacklistConfirm(row.contactId)">加入黑名单</button>
                    
                    <div class="popconfirm-overlay" :class="{ show: activePopconfirmId === row.contactId }">
                      <div class="popconfirm-body">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="popconfirm-icon">
                          <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
                        </svg>
                        <div class="popconfirm-text">
                          <strong>确认加入黑名单？</strong>
                          加入后不会删除历史数据，但会从正常联系人列表中移出。
                        </div>
                      </div>
                      <div class="popconfirm-footer">
                        <button class="btn btn-secondary btn-sm" @click="closeBlacklistConfirm">取消</button>
                        <button class="btn btn-primary btn-sm" style="background-color: var(--color-danger);" @click="confirmToBlacklist(row.contactId)">确认加入</button>
                      </div>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- 空数据状态 -->
        <div class="empty-state-container" v-else-if="!loading">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" class="empty-icon">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
          </svg>
          <h3 class="empty-title">无联系人数据</h3>
          <p class="empty-desc">您可以点击右上角“新增联系人”按钮，添加第一个CRM人脉关系。</p>
        </div>
      </div>

      <!-- 分页器 -->
      <div class="pagination" style="padding: 16px 24px;" v-if="total > 0">
        <span class="pagination-info">共 {{ total }} 条</span>
        <div class="pagination-pages">
          <button class="page-btn" :disabled="queryParams.page <= 1" @click="handlePageChange(queryParams.page - 1)">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;"><polyline points="15 18 9 12 15 6"/></svg>
          </button>
          <button
            v-for="p in totalPages"
            :key="p"
            class="page-btn"
            :class="{ active: queryParams.page === p }"
            @click="handlePageChange(p)"
          >
            {{ p }}
          </button>
          <button class="page-btn" :disabled="queryParams.page >= totalPages" @click="handlePageChange(queryParams.page + 1)">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;"><polyline points="9 18 15 12 9 6"/></svg>
          </button>
          
          <select class="select-control" v-model="queryParams.pageSize" @change="handlePageSizeChange" style="padding: 4px 24px 4px 8px; font-size:12px; height: 32px; border-radius: var(--radius-sm);">
            <option :value="10">10 条/页</option>
            <option :value="20">20 条/页</option>
            <option :value="50">50 条/页</option>
          </select>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getContactsApi, addToBlacklistApi } from '@/api/contact'
import type { ContactInfo } from '@/api/contact'

const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=80&auto=format&fit=crop&q=80'

const loading = ref<boolean>(false)
const contacts = ref<ContactInfo[]>([])
const total = ref<number>(0)
const weatherOpen = ref<boolean>(false)
const activePopconfirmId = ref<string | null>(null)
const weatherRef = ref<HTMLElement | null>(null)

// 排序绑定
const sortConfig = ref<string>('createdAt-desc')

// 接口过滤参数 (只显示普通联系人 status = 0)
const queryParams = reactive({
  page: 1,
  pageSize: 10,
  keyword: '',
  gender: '',
  tag: '',
  status: 0,
  sortBy: 'createdAt',
  sortOrder: 'desc'
})

const totalPages = computed(() => {
  return Math.ceil(total.value / (queryParams.pageSize || 10))
})

// 加载列表
const fetchContactsList = async () => {
  loading.value = true
  try {
    const res = await getContactsApi(queryParams)
    contacts.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('Failed to load contacts list:', error)
  } finally {
    loading.value = false
  }
}

// 搜索延迟触发
let searchTimer: any = null
const handleSearch = () => {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    queryParams.page = 1
    fetchContactsList()
  }, 300)
}

// 筛选改变重新检索
const handleFilterChange = () => {
  queryParams.page = 1
  fetchContactsList()
}

// 排序改变
const handleSortChange = () => {
  const parts = sortConfig.value.split('-')
  queryParams.sortBy = parts[0] || 'createdAt'
  queryParams.sortOrder = parts[1] || 'desc'
  queryParams.page = 1
  fetchContactsList()
}

// 重置筛选
const resetFilters = () => {
  queryParams.keyword = ''
  queryParams.gender = ''
  queryParams.tag = ''
  sortConfig.value = 'createdAt-desc'
  queryParams.sortBy = 'createdAt'
  queryParams.sortOrder = 'desc'
  queryParams.page = 1
  fetchContactsList()
}

// 标签 CSS 类映射
const getTagClass = (tag: string) => {
  if (tag === '同学') return 'tag-class'
  if (tag === '朋友') return 'tag-friend'
  if (tag === '重要') return 'tag-important'
  if (tag === '实习' || tag === '合作伙伴') return 'tag-partner'
  return ''
}

// 分页变化
const handlePageChange = (page: number) => {
  if (page < 1 || page > totalPages.value) return
  queryParams.page = page
  fetchContactsList()
}

// 每页大小变化
const handlePageSizeChange = () => {
  queryParams.page = 1
  fetchContactsList()
}

// 格式化性别
const formatGender = (gender: number | null) => {
  if (gender === 1) return '男'
  if (gender === 2) return '女'
  return '-'
}

// 格式化电话
const formatPhone = (phone: string) => {
  if (!phone) return ''
  // 简易格式化：138 0000 0000
  const clean = phone.replace(/\s+/g, '')
  if (clean.length === 11) {
    return `${clean.substring(0, 3)} ${clean.substring(3, 7)} ${clean.substring(7)}`
  }
  return phone
}

// 天气卡片切换
const toggleWeather = (event: Event) => {
  event.stopPropagation()
  weatherOpen.value = !weatherOpen.value
}

// 关闭天气弹窗
const closeWeather = () => {
  weatherOpen.value = false
}

// 侧边气泡控制
const openBlacklistConfirm = (id: string) => {
  activePopconfirmId.value = id
}

const closeBlacklistConfirm = () => {
  activePopconfirmId.value = null
}

const confirmToBlacklist = async (id: string) => {
  try {
    await addToBlacklistApi(id)
    ElMessage.success('成功加入黑名单')
    activePopconfirmId.value = null
    // 重新加载列表数据
    fetchContactsList()
  } catch (error) {
    console.error('Failed to blacklist contact:', error)
  }
}

const handleTodoClick = () => {
  ElMessage.info('新增事项功能将在 Phase 2 实现，当前只作为占位入口')
}

// 点击空白关闭天气
const handleOutsideClick = (e: MouseEvent) => {
  if (weatherRef.value && !weatherRef.value.contains(e.target as Node)) {
    closeWeather()
  }
}

onMounted(() => {
  fetchContactsList()
  document.addEventListener('click', handleOutsideClick)
})

onUnmounted(() => {
  document.removeEventListener('click', handleOutsideClick)
})
</script>

<style scoped>
.contacts-page-wrapper {
  animation: fadeIn 0.4s ease-out;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.search-icon {
  width: 16px;
  height: 16px;
  color: var(--text-muted);
}

.action-cell-right {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 6px;
}

.popconfirm-container {
  display: inline-block;
  position: relative;
}

.popconfirm-overlay {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 280px;
  background: rgba(255, 255, 255, 0.98);
  border: 1px solid rgba(226, 232, 240, 0.95);
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(15, 23, 42, 0.08);
  padding: 12px;
  text-align: left;
  z-index: 100;
  display: none;
  backdrop-filter: blur(8px);
}

.popconfirm-overlay.show {
  display: block;
  animation: slideInDown 0.15s ease-out;
}

.popconfirm-body {
  display: flex;
  gap: 8px;
  align-items: flex-start;
  margin-bottom: 8px;
}

.popconfirm-icon {
  width: 16px;
  height: 16px;
  color: #ef4444;
  flex-shrink: 0;
  margin-top: 2px;
}

.popconfirm-text {
  font-size: 12px;
  color: var(--text-main);
  line-height: 1.4;
}

.popconfirm-text strong {
  display: block;
  margin-bottom: 2px;
}

.popconfirm-footer {
  display: flex;
  justify-content: flex-end;
  gap: 6px;
}

.empty-state-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-icon {
  width: 48px;
  height: 48px;
  color: var(--text-muted);
  margin-bottom: 12px;
  opacity: 0.6;
}

.empty-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 6px;
}

.empty-desc {
  font-size: 13px;
  color: var(--text-muted);
  max-width: 320px;
}

/* ══ 天气组件专有 CSS 样式 ══ */
.weather-entry {
  position: relative;
  display: flex;
  justify-content: flex-end;
  margin-left: auto;
}

.weather-trigger {
  min-width: 156px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border: 1px solid #dbe3ee;
  border-radius: 999px;
  background: #fbfdff;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.04);
  color: var(--text-main);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.weather-trigger:hover,
.weather-trigger:focus-visible {
  border-color: #cfd9e6;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.08);
  transform: translateY(-1px);
  outline: none;
}

.weather-trigger[aria-expanded="true"] {
  border-color: #c7d4e4;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.1);
}

.weather-trigger-icon {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #eff6ff;
  color: #3b82f6;
}

.weather-trigger-text {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  line-height: 1.15;
  text-align: left;
}

.weather-trigger-location {
  font-size: 11px;
  font-weight: 500;
  color: var(--text-muted);
}

.weather-trigger-summary {
  margin-top: 2px;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-main);
}

.weather-trigger-chevron {
  margin-left: auto;
  color: var(--text-muted);
  width: 14px;
  height: 14px;
  transition: transform 0.2s ease;
}

.weather-trigger[aria-expanded="true"] .weather-trigger-chevron {
  transform: rotate(180deg);
}

.weather-panel {
  position: absolute;
  top: calc(100% + 12px);
  right: 0;
  width: 320px;
  padding: 18px;
  border: 1px solid rgba(226, 232, 240, 0.95);
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 20px 45px rgba(15, 23, 42, 0.12);
  backdrop-filter: blur(12px);
  z-index: 20;
  display: none;
}

.weather-panel.open {
  display: block;
  animation: slideInDown 0.2s ease-out;
}

.weather-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 14px;
}

.weather-panel-title {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: var(--text-main);
}

.weather-panel-subtitle {
  margin-top: 4px;
  font-size: 12px;
  color: var(--text-muted);
}

.weather-panel-state {
  padding: 6px 10px;
  border-radius: 999px;
  background: #eff6ff;
  color: #2563eb;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.weather-list {
  display: grid;
  gap: 10px;
}

.weather-day {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 12px;
  align-items: center;
  padding: 12px 14px;
  border: 1px solid #eef2f7;
  border-radius: 16px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
}

.weather-day-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}

.weather-day-label {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-main);
}

.weather-day-date {
  font-size: 12px;
  color: var(--text-muted);
}

.weather-day-condition {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #334155;
  white-space: nowrap;
}

.weather-day-temp {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-main);
  white-space: nowrap;
}

.weather-panel-note {
  margin-top: 12px;
  font-size: 12px;
  color: var(--text-muted);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideInDown {
  from { opacity: 0; transform: translateY(-8px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
