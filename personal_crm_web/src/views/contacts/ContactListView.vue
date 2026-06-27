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
          <option v-for="t in tagList" :key="t.tagId" :value="t.name">{{ t.name }}</option>
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
        <button class="btn btn-secondary" style="display: inline-flex; align-items: center; gap: 4px; height: 36px; padding: 0 12px; font-size: 13px;" @click="openTagManager">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;color:var(--color-primary);">
            <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/>
          </svg>
          标签管理
        </button>
        <router-link to="/contacts/new" class="btn btn-primary mobile-add-contact-btn" style="display: inline-flex; align-items: center; gap: 4px; height: 36px; padding: 0 12px; font-size: 13px;">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;">
            <line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line>
          </svg>
          新增联系人
        </router-link>
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
            <tr v-for="row in contacts" :key="row.contactId" @click="handleRowClick($event, row.contactId)">
              <td style="padding-left: 24px;">
                <img :src="row.avatarUrl || defaultAvatar" :alt="row.name" class="avatar-round">
              </td>
              <td>
                <router-link :to="`/contacts/${row.contactId}`" class="avatar-name">{{ row.name }}</router-link>
              </td>
              <td>{{ formatGender(row.gender) }}</td>
              <td>
                <a v-if="row.phone" :href="`tel:${row.phone}`" class="contact-link" @click.stop>
                  {{ formatPhone(row.phone) }}
                </a>
                <span v-else>-</span>
              </td>
              <td>
                <a v-if="row.email" :href="`mailto:${row.email}`" class="contact-link" @click.stop="handleEmailClickOnlyCopy(row.email)">
                  {{ row.email }}
                </a>
                <span v-else>-</span>
              </td>
              <td>
                <template v-if="row.tags && row.tags.length > 0">
                  <span
                    v-for="t in row.tags"
                    :key="t"
                    class="badge"
                    :style="{
                      marginRight: '4px',
                      backgroundColor: getTagColor(t) + '15',
                      color: getTagColor(t),
                      borderColor: getTagColor(t) + '30',
                      borderWidth: '1px',
                      borderStyle: 'solid'
                    }"
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
                  <router-link :to="`/todos?contactId=${row.contactId}`" class="btn btn-secondary btn-sm">事项</router-link>
                  
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

        <!-- 移动端自适应卡片列表 (Card List Flow) -->
        <div class="mobile-card-list" v-if="contacts.length > 0">
          <div 
            v-for="row in contacts" 
            :key="row.contactId" 
            class="mobile-contact-card"
            @click="handleRowClick($event, row.contactId)"
          >
            <div class="card-avatar-wrap">
              <img :src="row.avatarUrl || defaultAvatar" :alt="row.name" class="mobile-card-avatar">
            </div>
            <div class="card-details">
              <div class="card-name-row">
                <router-link :to="`/contacts/${row.contactId}`" class="card-contact-name" @click.stop>{{ row.name }}</router-link>
                <span class="card-contact-gender">{{ formatGender(row.gender) }}</span>
              </div>
              <div class="card-meta-line" v-if="row.phone">
                <a :href="`tel:${row.phone}`" class="card-meta-link" @click.stop>
                  📞 {{ formatPhone(row.phone) }}
                </a>
              </div>
              <div class="card-tags-row" v-if="row.tags && row.tags.length > 0">
                <span
                  v-for="t in row.tags"
                  :key="t"
                  class="badge"
                  :style="{
                    backgroundColor: getTagColor(t) + '15',
                    color: getTagColor(t),
                    borderColor: getTagColor(t) + '30',
                    borderWidth: '1px',
                    borderStyle: 'solid',
                    fontSize: '10px',
                    padding: '2px 6px',
                    borderRadius: '8px'
                  }"
                >
                  {{ t }}
                </span>
              </div>
            </div>
            <div class="card-actions-column">
              <router-link :to="`/contacts/${row.contactId}/edit`" class="btn btn-secondary btn-xs" @click.stop>编辑</router-link>
              <button class="btn btn-danger-outline btn-xs" @click.stop="confirmToBlacklist(row.contactId)">拉黑</button>
            </div>
          </div>
        </div>

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
    <!-- 标签管理弹窗 -->
    <el-dialog
      v-model="tagManagerVisible"
      title="标签管理"
      width="500px"
      @closed="handleTagManagerClosed"
      destroy-on-close
    >
      <div class="tag-manager-container" v-loading="tagLoading">
        <!-- 新增/编辑表单 -->
        <div class="tag-edit-form" style="display: flex; gap: 12px; align-items: flex-end; margin-bottom: 20px; background: #f8fafc; padding: 12px; border-radius: 8px;">
          <div style="flex: 1;">
            <div style="font-size: 12px; font-weight: 600; margin-bottom: 6px; color: #475569;">
              {{ currentTagId ? '编辑标签' : '新增标签' }}
            </div>
            <input
              type="text"
              class="input-control"
              v-model="tagForm.name"
              placeholder="输入标签名称 (如 工作)"
              style="width: 100%; height: 36px;"
            />
          </div>
          <div>
            <div style="font-size: 12px; font-weight: 600; margin-bottom: 6px; color: #475569;">颜色</div>
            <el-color-picker v-model="tagForm.color" :predefine="predefineColors" size="default" />
          </div>
          <div style="display: flex; gap: 6px;">
            <button class="btn btn-primary" style="height: 36px; padding: 0 16px;" @click="saveTag">
              确定
            </button>
            <button v-if="currentTagId" class="btn btn-secondary" style="height: 36px; padding: 0 12px;" @click="cancelEditTag">
              取消
            </button>
          </div>
        </div>

        <!-- 标签列表 -->
        <div style="font-size: 13px; font-weight: 600; color: #1e293b; margin-bottom: 8px;">标签列表 ({{ tagList.length }})</div>
        <div class="tag-list-scroll" style="max-height: 300px; overflow-y: auto; border: 1px solid #e2e8f0; border-radius: 8px; padding: 8px;">
          <div v-if="tagList.length === 0" style="text-align: center; color: #94a3b8; padding: 30px 0; font-size: 13px;">
            暂无标签，请在上方创建第一个标签
          </div>
          <div
            v-for="item in tagList"
            :key="item.tagId"
            style="display: flex; justify-content: space-between; align-items: center; padding: 8px 12px; border-bottom: 1px solid #f1f5f9;"
          >
            <div style="display: flex; align-items: center; gap: 8px;">
              <span
                style="width: 12px; height: 12px; border-radius: 50%; display: inline-block;"
                :style="{ backgroundColor: item.color }"
              ></span>
              <span style="font-size: 13px; font-weight: 500; color: #334155;">{{ item.name }}</span>
            </div>
            <div style="display: flex; gap: 8px;">
              <button class="btn btn-secondary btn-sm" style="padding: 2px 8px; font-size: 12px;" @click="editTag(item)">
                编辑
              </button>
              
              <el-popconfirm
                title="确定删除此标签吗？这会清理联系人关联关系，但不会删除联系人本身。"
                confirm-button-text="确定"
                cancel-button-text="取消"
                width="220"
                @confirm="deleteTag(item.tagId)"
              >
                <template #reference>
                  <button class="btn btn-danger-outline btn-sm" style="padding: 2px 8px; font-size: 12px;">
                    删除
                  </button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getContactsApi, addToBlacklistApi } from '@/api/contact'
import type { ContactInfo } from '@/api/contact'
import { getTagsApi, createTagApi, updateTagApi, deleteTagApi } from '@/api/tag'
import type { TagInfo } from '@/api/tag'

const router = useRouter()
const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=80&auto=format&fit=crop&q=80'

const handleRowClick = (event: MouseEvent, contactId: string) => {
  const target = event.target as HTMLElement
  // 若点击的是 A 链接、BUTTON 按钮，或在操作容器内，不进行行级跳转
  if (
    target.tagName === 'A' || 
    target.tagName === 'BUTTON' || 
    target.closest('.action-cell-right') || 
    target.closest('.popconfirm-overlay')
  ) {
    return
  }
  router.push(`/contacts/${contactId}`)
}

const handleEmailClickOnlyCopy = (email: string) => {
  if (!email) return
  copyTextToClipboard(email).then(() => {
    ElMessage.success('已自动复制邮箱，正在为您拉起邮件应用...')
  })
}

const copyTextToClipboard = (text: string) => {
  if (navigator.clipboard && navigator.clipboard.writeText) {
    return navigator.clipboard.writeText(text)
  } else {
    return new Promise<void>((resolve, reject) => {
      try {
        const input = document.createElement('input')
        input.value = text
        input.style.position = 'fixed'
        input.style.opacity = '0'
        document.body.appendChild(input)
        input.select()
        const success = document.execCommand('copy')
        document.body.removeChild(input)
        if (success) resolve()
        else reject(new Error('Copy failed'))
      } catch (err) {
        reject(err)
      }
    })
  }
}

const loading = ref<boolean>(false)
const contacts = ref<ContactInfo[]>([])
const total = ref<number>(0)
const activePopconfirmId = ref<string | null>(null)
const tagList = ref<TagInfo[]>([])

const fetchTagList = async () => {
  try {
    tagList.value = await getTagsApi()
  } catch (error) {
    console.error('Failed to load tags:', error)
  }
}

const getTagColor = (tagName: string) => {
  const t = tagList.value.find(item => item.name === tagName)
  return t ? t.color : '#64748b'
}

// 标签管理相关业务逻辑
const tagManagerVisible = ref<boolean>(false)
const tagLoading = ref<boolean>(false)
const currentTagId = ref<number | null>(null)
const tagForm = reactive({
  name: '',
  color: '#409EFF'
})

const predefineColors = ref([
  '#409EFF',
  '#67C23A',
  '#E6A23C',
  '#F56C6C',
  '#909399',
  '#00ffff',
  '#2f4f4f',
  '#ba55d3',
  '#ff00ff'
])

const openTagManager = () => {
  tagManagerVisible.value = true
}

const handleTagManagerClosed = () => {
  cancelEditTag()
  fetchContactsList()
}

const cancelEditTag = () => {
  currentTagId.value = null
  tagForm.name = ''
  tagForm.color = '#409EFF'
}

const editTag = (tag: TagInfo) => {
  currentTagId.value = tag.tagId
  tagForm.name = tag.name
  tagForm.color = tag.color
}

const saveTag = async () => {
  if (!tagForm.name.trim()) {
    ElMessage.warning('标签名称不能为空')
    return
  }
  tagLoading.value = true
  try {
    if (currentTagId.value) {
      await updateTagApi(currentTagId.value, {
        name: tagForm.name.trim(),
        color: tagForm.color
      })
      ElMessage.success('标签修改成功')
    } else {
      await createTagApi({
        name: tagForm.name.trim(),
        color: tagForm.color
      })
      ElMessage.success('标签创建成功')
    }
    cancelEditTag()
    await fetchTagList()
  } catch (error: any) {
    console.error('Failed to save tag:', error)
  } finally {
    tagLoading.value = false
  }
}

const deleteTag = async (tagId: number) => {
  tagLoading.value = true
  try {
    await deleteTagApi(tagId)
    ElMessage.success('标签删除成功')
    await fetchTagList()
  } catch (error) {
    console.error('Failed to delete tag:', error)
  } finally {
    tagLoading.value = false
  }
}

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
let searchTimer: ReturnType<typeof setTimeout> | null = null
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

onMounted(() => {
  fetchContactsList()
  fetchTagList()
})
</script>

<style scoped>
.contacts-page-wrapper {
  animation: fadeIn 0.4s ease-out;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.mobile-add-contact-btn {
  display: none !important;
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

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.custom-table tbody tr {
  cursor: pointer;
  transition: background-color 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.custom-table tbody tr:hover td {
  background-color: rgba(99, 102, 241, 0.05) !important;
}

/* ===== 移动端响应式覆盖 ===== */
.mobile-card-list {
  display: none;
}

@media (max-width: 768px) {
  /* 隐藏 PC 原生 Table */
  .custom-table {
    display: none !important;
  }
  
  /* 隐藏 PC 端卡片头尾多余内边距 */
  .table-container {
    padding: 0 !important;
  }
  
  .contacts-page-wrapper {
    padding: 12px !important;
    gap: 16px !important;
    width: 100% !important;
    box-sizing: border-box !important;
    overflow-x: hidden !important;
  }

  .card {
    padding: 12px !important;
    border-radius: 14px !important;
  }

  .filter-bar {
    padding: 12px !important;
    width: 100% !important;
    box-sizing: border-box !important;
  }

  .filter-left {
    display: flex !important;
    flex-wrap: wrap !important;
    gap: 8px !important;
    width: 100% !important;
  }

  .filter-left .search-box {
    flex: 1 1 100% !important;
    width: 100% !important;
    margin-right: 0 !important; /* 清除 PC 端继承的右边距，使其与下面的选择框完美对齐填充 */
  }

  .filter-left .select-control {
    flex: 1 1 calc(50% - 4px) !important;
    min-width: 0 !important;
    width: 100% !important;
    box-sizing: border-box !important;
  }

  /* 排序选项内容较长，在小屏下独占一行 */
  .filter-left .select-control:nth-of-type(3) {
    flex: 1 1 100% !important;
  }

  .filter-left .btn-icon {
    flex: 0 0 36px !important;
    padding: 0 !important;
    width: 36px !important;
    height: 36px !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
  }

  .filter-left .btn:not(.btn-icon),
  .filter-left .mobile-add-contact-btn {
    flex: 1 1 calc(50% - 26px) !important;
    height: 36px !important;
    display: inline-flex !important;
    align-items: center !important;
    justify-content: center !important;
    gap: 4px !important;
  }

  /* 开启卡片流式列表 */
  .mobile-card-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
    padding: 0 !important; /* 统一使用外层 .card 的 padding 避免过度嵌套挤压 */
  }
  
  .mobile-contact-card {
    background-color: #ffffff;
    border-radius: 16px;
    padding: 14px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border: 1px solid #f1f5f9;
    box-shadow: 0 2px 8px rgba(15, 23, 42, 0.02);
    cursor: pointer;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
  }
  
  .mobile-contact-card:active {
    transform: scale(0.98);
    box-shadow: 0 1px 4px rgba(15, 23, 42, 0.01);
  }
  
  .card-avatar-wrap {
    flex-shrink: 0;
  }
  
  .mobile-card-avatar {
    width: 44px;
    height: 44px;
    border-radius: 50%;
    object-fit: cover;
    border: 1.5px solid #ffffff;
    box-shadow: var(--shadow-sm);
  }
  
  .card-details {
    flex: 1;
    min-width: 0;
    margin-left: 12px;
    margin-right: 12px;
    display: flex;
    flex-direction: column;
    gap: 4px;
  }
  
  .card-name-row {
    display: flex;
    align-items: center;
    gap: 6px;
  }
  
  .card-contact-name {
    font-size: 14px;
    font-weight: 700;
    color: #1e293b;
    text-decoration: none;
  }
  
  .card-contact-gender {
    font-size: 10px;
    color: #64748b;
    font-weight: 500;
  }
  
  .card-meta-line {
    font-size: 11px;
  }
  
  .card-meta-link {
    color: #475569;
    text-decoration: none;
    font-weight: 500;
  }
  
  .card-tags-row {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
    margin-top: 2px;
  }
  
  .card-actions-column {
    display: flex;
    flex-direction: column;
    gap: 8px;
    flex-shrink: 0;
  }
  
  /* 超小尺寸的紧凑型按钮 */
  .btn-xs {
    padding: 4px 10px !important;
    font-size: 10px !important;
    min-height: 24px !important;
    border-radius: 8px !important;
    font-weight: 600 !important;
    line-height: 1.2 !important;
    text-align: center;
  }
}
</style>
