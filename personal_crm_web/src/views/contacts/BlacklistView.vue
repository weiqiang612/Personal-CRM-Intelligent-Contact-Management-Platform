<template>
  <div class="blacklist-page-wrapper">
    <!-- 红色高风险提示条 -->
    <section class="alert-risk">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;margin-right:8px;display:inline-block;vertical-align:middle;color:#b91c1c;">
        <path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
      </svg>
      <span>黑名单联系人不会出现在正常联系人列表中，但历史数据保留。</span>
    </section>

    <!-- 工具栏 -->
    <section class="card filter-bar" style="padding: 16px 24px; margin-top: 16px;">
      <div class="filter-left">
        <div class="search-box">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="search-icon">
            <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
          <input
            type="text"
            v-model="queryParams.keyword"
            placeholder="搜索黑名单联系人"
            @input="handleSearch"
            style="background-color: transparent;"
          >
        </div>
        
        <select class="select-control" disabled title="性别筛选将在后续接口扩展中实现">
          <option value="">性别</option>
          <option value="1">男</option>
          <option value="2">女</option>
        </select>

        <select class="select-control" disabled title="Phase 2 标签管理功能">
          <option value="">标签 (Phase 2)</option>
        </select>
        
        <select class="select-control" v-model="sortConfig" @change="handleSortChange">
          <option value="createdAt-desc">加入时间 (降序)</option>
          <option value="createdAt-asc">加入时间 (升序)</option>
        </select>
        
        <button class="btn btn-secondary btn-icon" title="重置筛选" @click="resetFilters">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;">
            <path d="M21.5 2v6h-6M21.34 15.57a10 10 0 1 1-.57-8.38l5.67-5.67"/>
          </svg>
        </button>
      </div>
    </section>

    <!-- 黑名单表格 -->
    <section class="card" style="padding: 0; position: relative; margin-top: 16px;" v-loading="loading">
      <div class="table-container" style="min-height: 200px;">
        <table class="custom-table" v-if="contacts.length > 0">
          <thead>
            <tr>
              <th style="padding-left: 24px; width: 80px;">头像</th>
              <th>姓名</th>
              <th style="width: 80px;">性别</th>
              <th>手机号</th>
              <th>邮箱</th>
              <th>标签</th>
              <th>加入时间</th>
              <th style="width: 100px;">状态</th>
              <th style="padding-right: 24px; text-align: right; width: 220px;">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in contacts" :key="row.contactId">
              <td style="padding-left: 24px;">
                <!-- 遵循 spec 规则：头像保持彩色，不进行黑白处理 -->
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
              <td>{{ row.createdAt }}</td>
              <td>
                <span class="badge badge-status-blacklist">黑名单</span>
              </td>
              <td style="padding-right: 24px; text-align: right;">
                <div class="action-cell-right">
                  <router-link :to="`/contacts/${row.contactId}`" class="btn btn-secondary btn-sm">查看详情</router-link>
                  
                  <!-- 恢复确认气泡 -->
                  <div class="popconfirm-container" style="display: inline-block; position: relative;">
                    <button class="btn btn-secondary btn-sm" style="color:var(--color-primary);border-color:var(--color-primary-border);" @click="openRestoreConfirm(row.contactId)">恢复联系人</button>
                    
                    <div class="popconfirm-overlay" :class="{ show: activePopconfirmId === row.contactId }">
                      <div class="popconfirm-body">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="popconfirm-icon">
                          <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
                        </svg>
                        <div class="popconfirm-text">
                          <strong>确认恢复该联系人？</strong>
                          恢复后将重新出现在正常联系人列表中。
                        </div>
                      </div>
                      <div class="popconfirm-footer">
                        <button class="btn btn-secondary btn-sm" @click="closeRestoreConfirm">取消</button>
                        <button class="btn btn-primary btn-sm" @click="confirmRestore(row.contactId)">确认恢复</button>
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
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><line x1="4.93" y1="4.93" x2="19.07" y2="19.07"/>
          </svg>
          <h3 class="empty-title">黑名单暂无人员</h3>
          <p class="empty-desc">当前黑名单库非常干净，无任何受限或屏蔽的联系人。</p>
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
          </select>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getContactsApi, restoreFromBlacklistApi } from '@/api/contact'
import type { ContactInfo } from '@/api/contact'

const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=80&auto=format&fit=crop&q=80'

const loading = ref<boolean>(false)
const contacts = ref<ContactInfo[]>([])
const total = ref<number>(0)
const activePopconfirmId = ref<string | null>(null)

// 排序绑定
const sortConfig = ref<string>('createdAt-desc')

// 接口参数 (只显示黑名单联系人 status = 1)
const queryParams = reactive({
  page: 1,
  pageSize: 10,
  keyword: '',
  status: 1,
  sortBy: 'createdAt',
  sortOrder: 'desc'
})

const totalPages = computed(() => {
  return Math.ceil(total.value / (queryParams.pageSize || 10))
})

const fetchBlacklist = async () => {
  loading.value = true
  try {
    const res = await getContactsApi(queryParams)
    contacts.value = res.list
    total.value = res.total
  } catch (error) {
    console.error('Failed to load blacklist:', error)
  } finally {
    loading.value = false
  }
}

// 搜索延迟触发
let searchTimer: ReturnType<typeof setTimeout> | null = null
const handleSearch = () => {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    queryParams.page = 1
    fetchBlacklist()
  }, 300)
}

const handleSortChange = () => {
  const parts = sortConfig.value.split('-')
  queryParams.sortBy = parts[0] || 'createdAt'
  queryParams.sortOrder = parts[1] || 'desc'
  queryParams.page = 1
  fetchBlacklist()
}

const resetFilters = () => {
  queryParams.keyword = ''
  sortConfig.value = 'createdAt-desc'
  queryParams.sortBy = 'createdAt'
  queryParams.sortOrder = 'desc'
  queryParams.page = 1
  fetchBlacklist()
}

const handlePageChange = (page: number) => {
  if (page < 1 || page > totalPages.value) return
  queryParams.page = page
  fetchBlacklist()
}

const handlePageSizeChange = () => {
  queryParams.page = 1
  fetchBlacklist()
}

const formatGender = (gender: number | null) => {
  if (gender === 1) return '男'
  if (gender === 2) return '女'
  return '-'
}

const formatPhone = (phone: string) => {
  if (!phone) return ''
  // 简易格式化手机号
  const clean = phone.replace(/\s+/g, '')
  if (clean.length === 11) {
    return `${clean.substring(0, 3)} **** ${clean.substring(7)}`
  }
  return phone
}

// 标签 CSS 类映射
const getTagClass = (tag: string) => {
  if (tag === '同学') return 'tag-class'
  if (tag === '朋友') return 'tag-friend'
  if (tag === '重要') return 'tag-important'
  if (tag === '实习' || tag === '合作伙伴') return 'tag-partner'
  return ''
}

const openRestoreConfirm = (id: string) => {
  activePopconfirmId.value = id
}

const closeRestoreConfirm = () => {
  activePopconfirmId.value = null
}

const confirmRestore = async (id: string) => {
  try {
    await restoreFromBlacklistApi(id)
    ElMessage.success('成功恢复该联系人')
    activePopconfirmId.value = null
    fetchBlacklist()
  } catch (error) {
    console.error('Failed to restore contact:', error)
  }
}

onMounted(() => {
  fetchBlacklist()
})
</script>

<style scoped>
.blacklist-page-wrapper {
  animation: fadeIn 0.4s ease-out;
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
  color: #3b82f6;
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

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideInDown {
  from { opacity: 0; transform: translateY(-4px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
