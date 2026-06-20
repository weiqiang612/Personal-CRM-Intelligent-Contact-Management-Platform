<template>
  <div class="contact-detail-page-wrapper" v-loading="loading">
    <div v-if="contact">
      <!-- 头部大卡片 -->
      <section class="card detail-header-card">
        <div class="detail-header-left">
          <img :src="contact.avatarUrl || defaultAvatar" :alt="contact.name" class="detail-header-avatar">
          <div class="detail-header-info">
            <h3>
              {{ contact.name }}
              <span v-if="contact.status === 1" class="badge badge-status-normal" style="background-color: #fee2e2; color: #ef4444;">已拉黑</span>
              <span v-else class="badge badge-status-normal">正常</span>
            </h3>
            <div class="detail-tags-row">
              <span class="badge tag-class">同学</span>
              <span class="badge tag-friend">朋友</span>
              <span class="badge tag-partner" style="background-color: #f3e8ff; color: #7e22ce;">产品合作</span>
            </div>
          </div>
        </div>
        
        <div class="detail-header-actions">
          <router-link :to="`/contacts/${contact.contactId}/edit`" class="btn btn-secondary">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;margin-right:4px;display:inline-block;vertical-align:middle;">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 1 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
            </svg>
            编辑资料
          </router-link>
          
          <router-link :to="`/todos/new?contactId=${contact.contactId}`" class="btn btn-secondary">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;margin-right:4px;display:inline-block;vertical-align:middle;">
              <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            添加事项
          </router-link>

          <!-- 拉黑 / 移出 切换按钮及其二次确认 -->
          <div class="popconfirm-container" style="display: inline-block; position: relative;">
            <button v-if="contact.status === 0" class="btn btn-danger-outline" @click="showBlacklistConfirm = true">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;margin-right:4px;display:inline-block;vertical-align:middle;">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
              </svg>
              加入黑名单
            </button>
            <button v-else class="btn btn-primary-outline" @click="showBlacklistConfirm = true">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;margin-right:4px;display:inline-block;vertical-align:middle;">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
              </svg>
              移出黑名单
            </button>
            
            <div class="popconfirm-overlay" :class="{ show: showBlacklistConfirm }" style="top: 100%; right: 0; width: 280px;">
              <div class="popconfirm-body">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="popconfirm-icon">
                  <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
                </svg>
                <div class="popconfirm-text">
                  <strong v-if="contact.status === 0">确认加入黑名单？</strong>
                  <strong v-else>确认移出黑名单？</strong>
                  <span v-if="contact.status === 0">加入后不会删除历史数据，但会从正常联系人列表中移出。</span>
                  <span v-else>恢复后联系人将回到正常联系人列表中。</span>
                </div>
              </div>
              <div class="popconfirm-footer">
                <button class="btn btn-secondary btn-sm" @click="showBlacklistConfirm = false">取消</button>
                <button v-if="contact.status === 0" class="btn btn-primary btn-sm" style="background-color: var(--color-danger);" @click="executeBlacklist">确认加入</button>
                <button v-else class="btn btn-primary btn-sm" @click="executeRestore">确认恢复</button>
              </div>
            </div>
          </div>

          <button class="btn btn-secondary" @click="goBack" style="border-color: #d1d5db;">返回列表</button>
        </div>
      </section>

      <!-- 中部两栏 -->
      <section class="detail-body-layout" style="margin-top: 24px;">
        <!-- 左栏：基础资料 -->
        <div class="card">
          <div class="card-header">
            <h3 class="card-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;margin-right:6px;display:inline-block;vertical-align:middle;color:var(--color-primary);">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/>
              </svg>
              基础资料
            </h3>
          </div>
          <div class="info-list">
            <div class="info-item">
              <span class="info-label">性别</span>
              <span class="info-value">{{ formatGender(contact.gender) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">手机号</span>
              <span class="info-value">
                {{ formatPhone(contact.phone) }}
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;color:var(--color-primary);">
                  <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
                </svg>
              </span>
            </div>
            <div class="info-item">
              <span class="info-label">邮箱</span>
              <span class="info-value">
                {{ contact.email || '-' }}
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;color:var(--color-primary);">
                  <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/>
                </svg>
              </span>
            </div>
            <div class="info-item">
              <span class="info-label">微信</span>
              <span class="info-value">
                {{ contact.wechat || '-' }}
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;color:#07c160;">
                  <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>
                </svg>
              </span>
            </div>
            <div class="info-item">
              <span class="info-label">QQ</span>
              <span class="info-value">
                {{ contact.qq || '-' }}
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;color:#2563eb;">
                  <circle cx="12" cy="12" r="10"/><path d="M12 8v4l3 3"/>
                </svg>
              </span>
            </div>
            <div class="info-item">
              <span class="info-label">地址</span>
              <span class="info-value">
                {{ contact.address || '-' }}
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;color:var(--color-primary);">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/>
                </svg>
              </span>
            </div>
            <div class="info-item">
              <span class="info-label">邮编</span>
              <span class="info-value">{{ contact.postcode || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">生日</span>
              <span class="info-value">
                {{ contact.birthday || '-' }}
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;color:var(--color-primary);">
                  <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>
                </svg>
              </span>
            </div>
          </div>
        </div>

        <!-- 右栏：相关事项 (静态展现) -->
        <div class="card detail-todo-card" style="display: flex; flex-direction: column;">
          <div class="card-header">
            <h3 class="card-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;margin-right:6px;display:inline-block;vertical-align:middle;color:var(--color-primary);">
                <polyline points="9 11 12 14 22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/>
              </svg>
              相关事项
            </h3>
          </div>
          
          <!-- 选项卡头部 -->
          <div class="tabs-header">
            <button class="tab-btn" :class="{ active: currentTab === 'pending' }" @click="currentTab = 'pending'">
              待完成
              <span class="tab-badge">{{ pendingTodos.length }}</span>
            </button>
            <button class="tab-btn" :class="{ active: currentTab === 'completed' }" @click="currentTab = 'completed'">
              已完成
              <span class="tab-badge">{{ completedTodos.length }}</span>
            </button>
            <button class="tab-btn" :class="{ active: currentTab === 'cancelled' }" @click="currentTab = 'cancelled'">
              已取消
              <span class="tab-badge">{{ cancelledTodos.length }}</span>
            </button>
          </div>

          <!-- 选项卡内容区 (动态数据) -->
          <div class="tab-content" style="flex:1;">
            <!-- 待完成 -->
            <div v-if="currentTab === 'pending'" class="tab-pane active">
              <div v-if="pendingTodos.length === 0" class="todo-empty-tip">
                暂无待完成事项
              </div>
              <div v-else v-for="item in pendingTodos" :key="item.matterId" class="todo-item-card">
                <div class="todo-item-left">
                  <span class="todo-dot" :style="{ backgroundColor: getPriorityColor(item.priority) }"></span>
                  <div class="todo-info-group">
                    <div class="todo-title-row">
                      <span class="todo-title">{{ item.content }}</span>
                      <span class="todo-priority-badge" :class="getPriorityClass(item.priority)">{{ getPriorityText(item.priority) }}</span>
                    </div>
                    <span class="todo-time-tag">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:12px;height:12px;margin-right:2px;display:inline-block;"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                      {{ item.todoTime ? item.todoTime.substring(0, 16) : '' }}
                    </span>
                  </div>
                </div>
                <div class="todo-item-right">
                  <span class="todo-status-text">进行中</span>
                  <div class="todo-action-group">
                    <button class="btn btn-secondary btn-sm todo-action-btn complete" @click="handleComplete(item.matterId)">
                      完成
                    </button>
                    <button class="btn btn-secondary btn-sm todo-action-btn cancel" @click="handleCancel(item.matterId)">取消</button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 已完成 -->
            <div v-if="currentTab === 'completed'" class="tab-pane active">
              <div v-if="completedTodos.length === 0" class="todo-empty-tip">
                暂无已完成事项
              </div>
              <div v-else v-for="item in completedTodos" :key="item.matterId" class="todo-item-card is-muted">
                <div class="todo-item-left">
                  <span class="todo-dot" style="background-color: var(--color-success);"></span>
                  <div class="todo-info-group">
                    <div class="todo-title-row">
                      <span class="todo-title">{{ item.content }}</span>
                    </div>
                    <span class="todo-time-tag">
                      完成于：{{ item.completedAt ? item.completedAt.substring(0, 16) : '' }}
                    </span>
                  </div>
                </div>
                <div class="todo-item-right">
                  <span class="badge badge-todo-completed">已完成</span>
                </div>
              </div>
            </div>

            <!-- 已取消 -->
            <div v-if="currentTab === 'cancelled'" class="tab-pane active">
              <div v-if="cancelledTodos.length === 0" class="todo-empty-tip">
                暂无已取消事项
              </div>
              <div v-else v-for="item in cancelledTodos" :key="item.matterId" class="todo-item-card is-muted">
                <div class="todo-item-left">
                  <span class="todo-dot" style="background-color: var(--color-neutral-text);"></span>
                  <div class="todo-info-group">
                    <div class="todo-title-row">
                      <span class="todo-title">{{ item.content }}</span>
                    </div>
                    <span class="todo-time-tag">
                      取消于：{{ item.cancelledAt ? item.cancelledAt.substring(0, 16) : '' }}
                    </span>
                  </div>
                </div>
                <div class="todo-item-right">
                  <span class="badge badge-todo-cancelled">已取消</span>
                </div>
              </div>
            </div>
          </div>
          
          <router-link :to="`/todos?contactId=${contact?.contactId}`" class="btn btn-secondary btn-sm todo-view-all" v-if="contact">
            查看全部事项
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;margin-left:4px;display:inline-block;vertical-align:middle;">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </router-link>
        </div>
      </section>

      <!-- 底部最近动态 -->
      <section class="card" style="margin-top: 24px;">
        <div class="card-header">
          <h3 class="card-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;margin-right:6px;display:inline-block;vertical-align:middle;color:var(--color-primary);">
              <polyline points="12 8 12 12 14 14"/><path d="M3.05 11a9 9 0 1 1 .5 4m-.5 5v-5h5"/>
            </svg>
            最近动态
          </h3>
        </div>
        
        <div class="timeline" style="margin-top: 18px;">
          <!-- 动态 1 -->
          <div class="timeline-item">
            <div class="timeline-node primary">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;">
                <path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4Z"/>
              </svg>
            </div>
            <div class="timeline-content">
              <div class="timeline-header">
                <span class="timeline-title">修改了联系人资料</span>
                <span class="timeline-time">刚刚</span>
              </div>
              <p class="timeline-body">您更新了联系人 {{ contact.name }} 的邮箱和详细工作地址。</p>
            </div>
          </div>

          <!-- 动态 2 -->
          <div class="timeline-item">
            <div class="timeline-node warning">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>
              </svg>
            </div>
            <div class="timeline-content">
              <div class="timeline-header">
                <span class="timeline-title">创建了一条新事项</span>
                <span class="timeline-time">3 小时前</span>
              </div>
              <p class="timeline-body">创建了待办事项 “跟进产品合作需求”，设定在 2026-06-25 15:00 提醒。</p>
            </div>
          </div>

          <!-- 动态 3 -->
          <div class="timeline-item">
            <div class="timeline-node success">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;">
                <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/>
              </svg>
            </div>
            <div class="timeline-content">
              <div class="timeline-header">
                <span class="timeline-title">录入了此联系人</span>
                <span class="timeline-time">3 天前</span>
              </div>
              <p class="timeline-body">创建了联系人档案，并设置了初始关系标签 同学、朋友。</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getContactDetailApi, addToBlacklistApi, restoreFromBlacklistApi } from '@/api/contact'
import type { ContactInfo } from '@/api/contact'
import { getTodos, completeTodo, cancelTodo } from '@/api/todo'
import type { TodoInfo } from '@/types/todo'

const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=80&auto=format&fit=crop&q=80'

const route = useRoute()
const router = useRouter()

const loading = ref<boolean>(false)
const contact = ref<ContactInfo | null>(null)
const currentTab = ref<string>('pending')
const showBlacklistConfirm = ref<boolean>(false)

const todos = ref<TodoInfo[]>([])
const todoLoading = ref<boolean>(false)

const contactId = computed(() => route.params.contactId as string)

const loadContactDetail = async () => {
  loading.value = true
  try {
    const data = await getContactDetailApi(contactId.value)
    contact.value = data
  } catch (error) {
    console.error('Failed to load contact detail:', error)
    ElMessage.error('无法加载该联系人详情')
    router.push('/contacts')
  } finally {
    loading.value = false
  }
}

const loadContactTodos = async () => {
  if (!contactId.value) return
  todoLoading.value = true
  try {
    const data = await getTodos({
      contactId: contactId.value,
      pageSize: 100
    })
    todos.value = data.list
  } catch (error) {
    console.error('Failed to load contact todos:', error)
  } finally {
    todoLoading.value = false
  }
}

const pendingTodos = computed(() => todos.value.filter(t => t.status === 0))
const completedTodos = computed(() => todos.value.filter(t => t.status === 2))
const cancelledTodos = computed(() => todos.value.filter(t => t.status === 1))

const handleComplete = async (matterId: string) => {
  try {
    await completeTodo(matterId)
    ElMessage.success('事项标记已完成')
    loadContactTodos()
  } catch (error) {
    console.error('Failed to complete todo:', error)
  }
}

const handleCancel = async (matterId: string) => {
  try {
    await cancelTodo(matterId)
    ElMessage.success('事项已取消')
    loadContactTodos()
  } catch (error) {
    console.error('Failed to cancel todo:', error)
  }
}

const getPriorityText = (priority: number) => {
  if (priority === 2) return '紧急'
  if (priority === 1) return '重要'
  return '普通'
}

const getPriorityClass = (priority: number) => {
  if (priority === 2) return 'high'
  if (priority === 1) return 'medium'
  return 'low'
}

const getPriorityColor = (priority: number) => {
  if (priority === 2) return 'var(--color-danger)'
  if (priority === 1) return 'var(--color-warning)'
  return 'var(--color-info)'
}

const formatGender = (gender: number | null) => {
  if (gender === 1) return '男'
  if (gender === 2) return '女'
  return '未知'
}

const formatPhone = (phone: string) => {
  if (!phone) return ''
  const clean = phone.replace(/\s+/g, '')
  if (clean.length === 11) {
    return `${clean.substring(0, 3)} ${clean.substring(3, 7)} ${clean.substring(7)}`
  }
  return phone
}

// 加入黑名单
const executeBlacklist = async () => {
  if (!contact.value) return
  try {
    await addToBlacklistApi(contact.value.contactId)
    ElMessage.success('成功加入黑名单')
    showBlacklistConfirm.value = false
    loadContactDetail()
  } catch (error) {
    console.error('Failed to blacklist contact:', error)
  }
}

// 恢复拉黑
const executeRestore = async () => {
  if (!contact.value) return
  try {
    await restoreFromBlacklistApi(contact.value.contactId)
    ElMessage.success('成功移出黑名单')
    showBlacklistConfirm.value = false
    loadContactDetail()
  } catch (error) {
    console.error('Failed to restore contact:', error)
  }
}

const goBack = () => {
  router.push('/contacts')
}

const handleTodoTip = () => {
  ElMessage.info('事项功能为 Phase 2 预留，当前为占位交互')
}

onMounted(() => {
  loadContactDetail()
  loadContactTodos()
})
</script>

<style scoped>
.contact-detail-page-wrapper {
  animation: fadeIn 0.4s ease-out;
}

.detail-header-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 32px;
}

.detail-header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.detail-header-avatar {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-full);
  object-fit: cover;
  border: 3px solid #fff;
  box-shadow: var(--shadow-md);
}

.detail-header-info h3 {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-main);
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-tags-row {
  margin-top: 8px;
  display: flex;
  gap: 6px;
}

.detail-header-actions {
  display: flex;
  gap: 10px;
  position: relative;
}

.detail-body-layout {
  display: grid;
  grid-template-columns: 1fr 1.3fr;
  gap: 24px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px dashed var(--border-color);
  padding-bottom: 12px;
}

.info-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.info-label {
  font-size: 13px;
  color: var(--text-muted);
  font-weight: 500;
}

.info-value {
  font-size: 13px;
  color: var(--text-main);
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tabs-header {
  display: flex;
  border-bottom: 1px solid var(--border-color);
  margin-bottom: 18px;
  gap: 8px;
}

.tab-btn {
  padding: 10px 18px 12px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-muted);
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all var(--transition-fast);
}

.tab-btn.active {
  color: var(--color-primary);
  border-bottom-color: var(--color-primary);
}

.tab-badge {
  background-color: var(--color-neutral-bg);
  color: var(--text-muted);
  font-size: 11px;
  padding: 2px 8px;
  border-radius: var(--radius-full);
}

.tab-btn.active .tab-badge {
  background-color: var(--color-primary-light);
  color: var(--color-primary);
}

.tab-pane {
  display: none;
}

.tab-pane.active {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.detail-todo-card {
  padding: 22px 20px 18px;
  border-radius: 22px;
  border-color: rgba(226, 232, 240, 0.95);
  box-shadow: 0 22px 52px -40px rgba(37, 99, 235, 0.26);
  background:
    radial-gradient(circle at top right, rgba(239, 246, 255, 0.78), rgba(255, 255, 255, 0) 32%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(248, 250, 252, 0.96) 100%);
}

.todo-item-card {
  border: 1px solid rgba(226, 232, 240, 0.95);
  border-radius: 16px;
  padding: 18px;
  background-color: rgba(255, 255, 255, 0.96);
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  transition: all var(--transition-fast);
  box-shadow: 0 14px 32px -28px rgba(15, 23, 42, 0.32);
}

.todo-item-card:hover {
  background-color: #fff;
  border-color: rgba(191, 219, 254, 0.95);
  box-shadow: 0 18px 40px -28px rgba(37, 99, 235, 0.24);
  transform: translateY(-1px);
}

.todo-item-left {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  min-width: 0;
}

.todo-dot {
  width: 9px;
  height: 9px;
  border-radius: var(--radius-full);
  margin-top: 8px;
  flex-shrink: 0;
}

.todo-info-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  text-align: left;
}

.todo-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.todo-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-main);
}

.todo-priority-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 3px 10px;
  border-radius: var(--radius-full);
  font-size: 12px;
  font-weight: 600;
  line-height: 1.2;
}

.todo-priority-badge.high {
  background-color: #fee2e2;
  color: #ef4444;
}

.todo-priority-badge.medium {
  background-color: #ffedd5;
  color: #f59e0b;
}

.todo-priority-badge.low {
  background-color: #dbeafe;
  color: #2563eb;
}

.todo-time-tag {
  font-size: 12px;
  color: var(--text-muted);
  display: flex;
  align-items: center;
  gap: 6px;
}

.todo-item-right {
  display: flex;
  align-items: center;
  gap: 18px;
  padding-left: 16px;
  flex-shrink: 0;
}

.todo-status-text {
  font-size: 13px;
  color: var(--color-primary);
  font-weight: 700;
  white-space: nowrap;
}

.todo-action-group {
  display: flex;
  gap: 8px;
}

.todo-action-btn {
  min-width: 76px;
  height: 38px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  padding: 0 16px;
  box-shadow: 0 10px 24px -22px rgba(15, 23, 42, 0.32);
}

.todo-action-btn.complete {
  border-color: #bfdbfe;
  color: var(--color-primary);
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
}

.todo-action-btn.complete:hover {
  background: linear-gradient(180deg, #eff6ff 0%, #e0ecff 100%);
  border-color: #93c5fd;
  color: #1d4ed8;
}

.todo-action-btn.cancel {
  border-color: rgba(226, 232, 240, 0.95);
  color: #475569;
  background-color: #ffffff;
}

.todo-action-btn.cancel:hover {
  background-color: #f8fafc;
}

.todo-item-card.is-muted {
  opacity: 0.78;
  box-shadow: none;
  background-color: #fbfcfe;
}

.todo-item-card.is-muted .todo-title {
  text-decoration: line-through;
}

.todo-view-all {
  margin-top: 20px;
  align-self: center;
  min-width: 150px;
  height: 38px;
  border-radius: 12px;
  color: #64748b;
  box-shadow: 0 12px 28px -24px rgba(15, 23, 42, 0.28);
}

.todo-view-all:hover {
  color: var(--text-main);
  border-color: #cbd5e1;
}

.timeline {
  display: flex;
  flex-direction: column;
  position: relative;
  margin-left: 10px;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 16px;
  top: 10px;
  bottom: 10px;
  width: 2px;
  background-color: var(--border-color);
}

.timeline-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  position: relative;
  padding-bottom: 20px;
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-node {
  width: 34px;
  height: 34px;
  border-radius: var(--radius-full);
  background-color: #f3f4f6;
  border: 2px solid #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  z-index: 10;
  box-shadow: var(--shadow-sm);
  flex-shrink: 0;
}

.timeline-node.primary { background-color: var(--color-primary-light); color: var(--color-primary); }
.timeline-node.success { background-color: var(--color-success-bg); color: var(--color-success); }
.timeline-node.warning { background-color: var(--color-warning-bg); color: var(--color-warning); }

.timeline-content {
  flex: 1;
  padding-top: 5px;
  text-align: left;
}

.timeline-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.timeline-title {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-main);
}

.timeline-time {
  font-size: 11px;
  color: var(--text-muted);
}

.timeline-body {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 4px;
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

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideInDown {
  from { opacity: 0; transform: translateY(-4px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 1200px) {
  .detail-body-layout {
    grid-template-columns: 1fr;
  }
}

.todo-empty-tip {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
  color: var(--text-muted);
  font-size: 13px;
}
</style>
