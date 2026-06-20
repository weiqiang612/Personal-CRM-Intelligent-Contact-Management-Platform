<template>
  <div class="todos-page-wrapper">
    <!-- 状态快捷选项卡 -->
    <section class="todo-tabs-bar">
      <span
        :class="['todo-tab', activeTab === 'all' ? 'active' : '']"
        @click="handleTabClick('all')"
      >
        全部
      </span>
      <span
        :class="['todo-tab', activeTab === 'today' ? 'active' : '']"
        @click="handleTabClick('today')"
      >
        今日事项 <span class="todo-tab-count">{{ tabCounts.today }}</span>
      </span>
      <span
        :class="['todo-tab', activeTab === 'overdue' ? 'active' : '']"
        @click="handleTabClick('overdue')"
      >
        逾期事项 <span class="todo-tab-count red-badge">{{ tabCounts.overdue }}</span>
      </span>
      <span
        :class="['todo-tab', activeTab === 'week' ? 'active' : '']"
        @click="handleTabClick('week')"
      >
        未来7天 <span class="todo-tab-count">{{ tabCounts.week }}</span>
      </span>
      <span
        :class="['todo-tab', activeTab === 'pending' ? 'active' : '']"
        @click="handleTabClick('pending')"
      >
        待完成 <span class="todo-tab-count" style="color:var(--color-primary);background-color:var(--color-primary-light);">{{ tabCounts.pending }}</span>
      </span>
      <span
        :class="['todo-tab', activeTab === 'completed' ? 'active' : '']"
        @click="handleTabClick('completed')"
      >
        已完成 <span class="todo-tab-count" style="color:var(--color-success);background-color:var(--color-success-bg);">{{ tabCounts.completed }}</span>
      </span>
      <span
        :class="['todo-tab', activeTab === 'cancelled' ? 'active' : '']"
        @click="handleTabClick('cancelled')"
      >
        已取消 <span class="todo-tab-count">{{ tabCounts.cancelled }}</span>
      </span>
    </section>

    <!-- 工具栏 -->
    <section class="card filter-bar" style="padding: 16px 24px;">
      <div class="filter-left">
        <div class="search-box">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="search-icon" style="width:16px;height:16px;">
            <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
          <input
            type="text"
            v-model="keywordInput"
            placeholder="搜索事项内容"
            @input="handleSearchInput"
            style="background-color: transparent;"
          >
        </div>
        
        <select class="select-control" v-model="filterParams.contactId" @change="handleFilterChange">
          <option value="">选择联系人</option>
          <option
            v-for="c in contactOptions"
            :key="c.contactId"
            :value="c.contactId"
          >
            {{ c.name }}
          </option>
        </select>

        <select class="select-control" v-model="filterParams.priority" @change="handleFilterChange">
          <option value="">选择优先级</option>
          <option :value="2">紧急</option>
          <option :value="1">重要</option>
          <option :value="0">普通</option>
        </select>

        <select class="select-control" v-model="dateRangeSelect" @change="handleDateRangeChange">
          <option value="">选择日期范围</option>
          <option value="today">今天</option>
          <option value="week">本周</option>
          <option value="month">本月</option>
        </select>
        
        <select class="select-control" v-model="sortOrderSelect" @change="handleSortChange">
          <option value="desc">创建时间降序</option>
          <option value="asc">创建时间升序</option>
        </select>
        
        <button class="btn btn-secondary btn-icon" title="重置筛选" @click="resetFilters">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;">
            <path d="M21.5 2v6h-6M21.34 15.57a10 10 0 1 1-.57-8.38l5.67-5.67"/>
          </svg>
        </button>
      </div>

      <div class="filter-right">
        <router-link to="/todos/new" class="btn btn-primary">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;margin-right:4px;">
            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          新增事项
        </router-link>
      </div>
    </section>

    <!-- 数据表格 -->
    <section class="card" style="padding: 0; position: relative;" v-loading="loading">
      <div class="table-container" style="min-height: 200px;">
        <table class="custom-table" v-if="filteredTodos.length > 0">
          <thead>
            <tr>
              <th style="padding-left: 24px; width: 40px;"><input type="checkbox" style="cursor:pointer;"></th>
              <th>事项内容</th>
              <th>联系人</th>
              <th>事项时间</th>
              <th>优先级</th>
              <th>状态</th>
              <th>是否逾期</th>
              <th>创建时间</th>
              <th style="padding-right: 24px; width: 236px;">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="row in filteredTodos"
              :key="row.matterId"
              :style="{ opacity: row.status !== 0 ? 0.75 : 1 }"
            >
              <td style="padding-left: 24px;">
                <input
                  type="checkbox"
                  :checked="row.status === 2"
                  :disabled="row.status !== 0"
                  style="cursor:pointer;"
                  @change="triggerCompleteViaCheckbox(row)"
                >
              </td>
              <td>
                <strong v-if="row.status === 0">{{ row.content }}</strong>
                <span v-else style="text-decoration: line-through; color: var(--text-muted);">{{ row.content }}</span>
              </td>
              <td>
                <router-link :to="`/contacts/${row.contactId}`" class="avatar-group">
                  <span class="avatar-round-text" style="width:24px;height:24px;line-height:24px;font-size:12px;background-color:var(--color-primary-light);color:var(--color-primary);border-radius:50%;text-align:center;display:inline-block;margin-right:8px;font-weight:600;">
                    {{ (row.contactName || '联').substring(0, 1) }}
                  </span>
                  <span>{{ row.contactName || '未关联' }}</span>
                </router-link>
              </td>
              <td>{{ formatTime(row.todoTime) }}</td>
              <td>
                <span :class="['badge', getPriorityClass(row.priority)]">
                  {{ formatPriority(row.priority) }}
                </span>
              </td>
              <td>
                <span :class="['badge', getStatusClass(row.status)]">
                  {{ formatStatus(row.status) }}
                </span>
              </td>
              <td>
                <span v-if="isOverdue(row)" class="badge badge-todo-overdue">已逾期</span>
                <span v-else>-</span>
              </td>
              <td>{{ formatTime(row.createdAt || '') }}</td>
              <td style="padding-right: 24px;">
                <div class="action-cell">
                  <el-popconfirm
                    title="确认要将该事项标记为完成吗？"
                    confirm-button-text="确认"
                    cancel-button-text="取消"
                    @confirm="handleComplete(row.matterId)"
                  >
                    <template #reference>
                      <button class="btn btn-primary btn-sm" :disabled="row.status !== 0">完成</button>
                    </template>
                  </el-popconfirm>

                  <el-popconfirm
                    title="确认要取消该事项吗？"
                    confirm-button-text="确认"
                    cancel-button-text="取消"
                    @confirm="handleCancel(row.matterId)"
                  >
                    <template #reference>
                      <button class="btn btn-secondary btn-sm" :disabled="row.status !== 0">取消</button>
                    </template>
                  </el-popconfirm>

                  <router-link :to="`/contacts/${row.contactId}`" class="btn btn-secondary btn-sm">查看联系人</router-link>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- 空数据状态 -->
        <div class="empty-state-container" v-else-if="!loading">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" class="empty-icon">
            <path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/>
          </svg>
          <h3 class="empty-title">无事项提醒</h3>
          <p class="empty-desc">您可以点击右上角“新增事项”按钮，为CRM联系人创建一个日程待办提醒。</p>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTodos, completeTodo, cancelTodo } from '@/api/todo'
import { getContactsApi } from '@/api/contact'
import type { TodoInfo, TodoParams } from '@/types/todo'
import type { ContactInfo } from '@/api/contact'

// 状态管理
const loading = ref(false)
const todos = ref<TodoInfo[]>([])
const total = ref(0)
const activeTab = ref('all')

// 下拉联系人选项
const contactOptions = ref<ContactInfo[]>([])

// 搜索和排序字段
const keywordInput = ref('')
const dateRangeSelect = ref('')
const sortOrderSelect = ref('desc') // desc: 降序, asc: 升序

// 查询参数
const queryParams = reactive<{
  page: number
  pageSize: number
  contactId?: string
  status?: number
  startTime?: string
  endTime?: string
  sortOrder?: string
}>({
  page: 1,
  pageSize: 10,
  contactId: '',
  status: undefined,
  startTime: undefined,
  endTime: undefined,
  sortOrder: 'desc'
})

// 额外的前端筛选字段（用于优先级）
const filterParams = reactive({
  contactId: '',
  priority: ''
})

// 分页计算
const totalPages = computed(() => Math.ceil(total.value / (queryParams.pageSize || 10)))

// 时间格式化辅助
function formatTime(val: string) {
  if (!val) return '-'
  return val.length > 16 ? val.substring(0, 16) : val
}

// 格式化日期为 yyyy-MM-dd HH:mm:ss
function formatDate(date: Date): string {
  const pad = (n: number) => n.toString().padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

// 选项卡计数
const tabCounts = reactive({
  today: 0,
  overdue: 0,
  week: 0,
  pending: 0,
  completed: 0,
  cancelled: 0
})

// 优先级样式
function getPriorityClass(priority: number) {
  if (priority === 2) return 'badge-priority-emergency'
  if (priority === 1) return 'badge-priority-important'
  return 'badge-priority-normal'
}

function formatPriority(priority: number) {
  if (priority === 2) return '紧急'
  if (priority === 1) return '重要'
  return '普通'
}

// 状态样式
function getStatusClass(status: number) {
  if (status === 2) return 'badge-todo-completed'
  if (status === 1) return 'badge-todo-cancelled'
  return 'badge-todo-pending'
}

function formatStatus(status: number) {
  if (status === 2) return '已完成'
  if (status === 1) return '已取消'
  return '待完成'
}

// 逾期判断
function isOverdue(row: TodoInfo) {
  if (row.status !== 0) return false
  if (!row.todoTime) return false
  const limit = new Date(row.todoTime.replace(/-/g, '/')).getTime()
  return limit < Date.now()
}

// 加载数据
const fetchTodos = async () => {
  loading.value = true
  try {
    const params: TodoParams = {
      page: queryParams.page,
      pageSize: queryParams.pageSize,
      status: queryParams.status,
      startTime: queryParams.startTime,
      endTime: queryParams.endTime,
      sortOrder: sortOrderSelect.value
    }
    
    if (filterParams.contactId) {
      params.contactId = filterParams.contactId
    }

    const res = await getTodos(params)
    todos.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('加载事项提醒失败', error)
  } finally {
    loading.value = false
  }
}

// 加载选项卡计数
const fetchTabCounts = async () => {
  try {
    const now = new Date()
    const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0)
    const todayEnd = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59)
    const weekEnd = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 6, 23, 59, 59)

    const [todayRes, overdueRes, weekRes, pendingRes, completedRes, cancelledRes] = await Promise.all([
      getTodos({ page: 1, pageSize: 1, startTime: formatDate(todayStart), endTime: formatDate(todayEnd) }),
      getTodos({ page: 1, pageSize: 1, status: 0, endTime: formatDate(now) }),
      getTodos({ page: 1, pageSize: 1, startTime: formatDate(todayStart), endTime: formatDate(weekEnd) }),
      getTodos({ page: 1, pageSize: 1, status: 0 }),
      getTodos({ page: 1, pageSize: 1, status: 2 }),
      getTodos({ page: 1, pageSize: 1, status: 1 })
    ])

    tabCounts.today = todayRes.total
    tabCounts.overdue = overdueRes.total
    tabCounts.week = weekRes.total
    tabCounts.pending = pendingRes.total
    tabCounts.completed = completedRes.total
    tabCounts.cancelled = cancelledRes.total
  } catch (error) {
    console.error('获取状态计数失败', error)
  }
}

// 选项卡点击
const handleTabClick = (tab: string) => {
  activeTab.value = tab
  queryParams.page = 1
  
  if (tab === 'all') {
    queryParams.status = undefined
    queryParams.startTime = undefined
    queryParams.endTime = undefined
  } else if (tab === 'today') {
    queryParams.status = undefined
    const now = new Date()
    const start = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0)
    const end = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59)
    queryParams.startTime = formatDate(start)
    queryParams.endTime = formatDate(end)
  } else if (tab === 'overdue') {
    queryParams.status = 0
    queryParams.startTime = undefined
    queryParams.endTime = formatDate(new Date())
  } else if (tab === 'week') {
    queryParams.status = undefined
    const now = new Date()
    const start = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0)
    const end = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 6, 23, 59, 59)
    queryParams.startTime = formatDate(start)
    queryParams.endTime = formatDate(end)
  } else if (tab === 'pending') {
    queryParams.status = 0
    queryParams.startTime = undefined
    queryParams.endTime = undefined
  } else if (tab === 'completed') {
    queryParams.status = 2
    queryParams.startTime = undefined
    queryParams.endTime = undefined
  } else if (tab === 'cancelled') {
    queryParams.status = 1
    queryParams.startTime = undefined
    queryParams.endTime = undefined
  }

  dateRangeSelect.value = ''
  
  fetchTodos()
}

// 联动的过滤结果
const filteredTodos = computed(() => {
  let list = todos.value

  if (keywordInput.value.trim()) {
    const kw = keywordInput.value.toLowerCase()
    list = list.filter(item => item.content && item.content.toLowerCase().includes(kw))
  }

  if (filterParams.priority !== '') {
    const p = Number(filterParams.priority)
    list = list.filter(item => item.priority === p)
  }

  return list
})

// 筛选变更
const handleFilterChange = () => {
  queryParams.page = 1
  fetchTodos()
}

// 日期范围变更
const handleDateRangeChange = () => {
  queryParams.page = 1
  const val = dateRangeSelect.value
  
  if (!val) {
    queryParams.startTime = undefined
    queryParams.endTime = undefined
  } else {
    const now = new Date()
    if (val === 'today') {
      const start = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0)
      const end = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59)
      queryParams.startTime = formatDate(start)
      queryParams.endTime = formatDate(end)
    } else if (val === 'week') {
      const day = now.getDay() || 7
      const start = new Date(now.getFullYear(), now.getMonth(), now.getDate() - day + 1, 0, 0, 0)
      const end = new Date(now.getFullYear(), now.getMonth(), now.getDate() - day + 7, 23, 59, 59)
      queryParams.startTime = formatDate(start)
      queryParams.endTime = formatDate(end)
    } else if (val === 'month') {
      const start = new Date(now.getFullYear(), now.getMonth(), 1, 0, 0, 0)
      const end = new Date(now.getFullYear(), now.getMonth() + 1, 0, 23, 59, 59)
      queryParams.startTime = formatDate(start)
      queryParams.endTime = formatDate(end)
    }
  }

  activeTab.value = ''
  
  fetchTodos()
}

// 排序变更
const handleSortChange = () => {
  queryParams.page = 1
  fetchTodos()
}

// 重置筛选
const resetFilters = () => {
  keywordInput.value = ''
  dateRangeSelect.value = ''
  sortOrderSelect.value = 'desc'
  filterParams.contactId = ''
  filterParams.priority = ''
  activeTab.value = 'all'

  queryParams.page = 1
  queryParams.status = undefined
  queryParams.startTime = undefined
  queryParams.endTime = undefined
  
  fetchTodos()
}

const handleSearchInput = () => {
  // 依靠 filteredTodos 前端过滤
}

// 完成事项
const handleComplete = async (matterId: string) => {
  try {
    await completeTodo(matterId)
    ElMessage.success('成功标记该事项为完成！')
    fetchTodos()
    fetchTabCounts()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 取消事项
const handleCancel = async (matterId: string) => {
  try {
    await cancelTodo(matterId)
    ElMessage.success('已成功取消该事项！')
    fetchTodos()
    fetchTabCounts()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

const triggerCompleteViaCheckbox = (row: TodoInfo) => {
  if (row.status !== 0) return
  handleComplete(row.matterId)
}

const handlePageChange = (p: number) => {
  if (p < 1 || p > totalPages.value) return
  queryParams.page = p
  fetchTodos()
}

const handlePageSizeChange = () => {
  queryParams.page = 1
  fetchTodos()
}

onMounted(async () => {
  try {
    const res = await getContactsApi({ page: 1, pageSize: 1000 })
    contactOptions.value = res.list
  } catch (error) {
    console.error('获取联系人列表失败', error)
  }

  fetchTodos()
  fetchTabCounts()
})
</script>

<style scoped>
.todos-page-wrapper {
  padding: 0;
}

/* 选项卡栏 */
.todo-tabs-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.todo-tab {
  background-color: #fff;
  border: 1px solid var(--border-color);
  padding: 8px 16px;
  border-radius: var(--radius-sm);
  font-size: 13px;
  font-weight: 600;
  color: var(--text-muted);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all var(--transition-fast);
  box-shadow: var(--shadow-sm);
}

.todo-tab:hover {
  border-color: var(--color-primary-border);
  color: var(--color-primary);
}

.todo-tab.active {
  background-color: var(--color-primary);
  border-color: var(--color-primary);
  color: #fff;
}

.todo-tab-count {
  font-size: 10px;
  padding: 1px 6px;
  border-radius: var(--radius-full);
  background-color: var(--color-neutral-bg);
  color: var(--text-muted);
}

.todo-tab.active .todo-tab-count {
  background-color: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.todo-tab-count.red-badge {
  background-color: #fee2e2;
  color: #dc2626;
}

.todo-tab.active .todo-tab-count.red-badge {
  background-color: #ef4444;
  color: #fff;
}

/* 模拟禁用操作 */
.btn.disabled {
  opacity: 0.4;
  cursor: not-allowed;
  pointer-events: none;
}

.action-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: nowrap;
  white-space: nowrap;
}

.action-cell .btn {
  justify-content: center;
  min-width: 0;
  white-space: nowrap;
}

.action-cell .btn-sm {
  padding-left: 10px;
  padding-right: 10px;
}

.avatar-group {
  display: inline-flex;
  align-items: center;
  text-decoration: none;
  color: var(--text-color);
  font-weight: 500;
  transition: color var(--transition-fast);
}

.avatar-group:hover {
  color: var(--color-primary);
}

.badge-priority-emergency {
  background-color: #fee2e2;
  color: #ef4444;
}

.badge-priority-important {
  background-color: #fff7ed;
  color: #f97316;
}

.badge-priority-normal {
  background-color: #f0fdf4;
  color: #22c55e;
}

.badge-todo-pending {
  background-color: #e0f2fe;
  color: #0284c7;
}

.badge-todo-completed {
  background-color: #f0fdf4;
  color: #16a34a;
}

.badge-todo-cancelled {
  background-color: #f3f4f6;
  color: #4b5563;
}

.badge-todo-overdue {
  background-color: #fef2f2;
  color: #dc2626;
  border: 1px solid #fee2e2;
}

.popconfirm-container :deep(.el-popover) {
  padding: 12px;
}
</style>
