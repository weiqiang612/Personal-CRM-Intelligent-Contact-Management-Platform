<template>
  <div class="agent-container">
    <!-- 渐变光晕背景 -->
    <div class="glow-bg glow-1"></div>
    <div class="glow-bg glow-2"></div>

    <div class="agent-workbench">
      <!-- 左侧说明栏 -->
      <aside class="agent-sidebar">
        <!-- 助手能力 -->
        <div class="sidebar-section capabilities">
          <h3 class="section-title">助手能力</h3>
          <div class="capability-list">
            <div class="capability-item">
              <div class="capability-icon contact-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                  <circle cx="9" cy="7" r="4" />
                  <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
                  <path d="M16 3.13a4 4 0 0 1 0 7.75" />
                </svg>
              </div>
              <div class="capability-info">
                <h4>查找联系人</h4>
                <p>快速查询联系人电话、微信、邮箱及标签信息</p>
              </div>
            </div>
            <div class="capability-item">
              <div class="capability-icon todo-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>
                </svg>
              </div>
              <div class="capability-info">
                <h4>查询事项</h4>
                <p>搜索特定联系人关联的历史待办事项</p>
              </div>
            </div>
            <div class="capability-item">
              <div class="capability-icon confirm-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
                </svg>
              </div>
              <div class="capability-info">
                <h4>创建事项确认</h4>
                <p>自然语言输入，智能解析并提供卡片确认</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 快捷动作 -->
        <div class="sidebar-section quick-actions">
          <h3 class="section-title">快捷动作</h3>
          <div class="action-grid">
            <button 
              v-for="(prompt, index) in recommendPrompts" 
              :key="index" 
              class="quick-action-btn"
              @click="clickRecommend(prompt)"
            >
              <span class="action-btn-title">{{ prompt.title }}</span>
              <span class="action-btn-desc">“{{ prompt.content }}”</span>
            </button>
          </div>
        </div>

        <!-- 使用提示 -->
        <div class="sidebar-section usage-tips">
          <h3 class="section-title">使用提示</h3>
          <ul class="tips-list">
            <li>支持自然语言对话交互，智能识别槽位。</li>
            <li>数据操作仅作用于当前登录用户，严格权限隔离。</li>
          </ul>
        </div>
      </aside>

      <!-- 右侧会话区 -->
      <main class="agent-chat-panel">
        <!-- 头部 -->
        <header class="chat-header">
          <div class="header-title-wrapper">
            <div class="agent-status-badge">
              <span class="status-dot"></span>
              <span class="status-text">CRM Copilot 工作台</span>
            </div>
          </div>
          <button class="clear-chat-btn" @click="clearChat" v-if="messages.length > 0">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="btn-icon">
              <path d="M3 6h18M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2M10 11v6M14 11v6"/>
            </svg>
            清空对话
          </button>
        </header>

        <!-- 消息会话区域 -->
        <div class="chat-area" ref="chatAreaRef">
          <!-- 顶部欢迎区（收紧高度，当消息为空时展示，首屏立即可见且高度紧凑） -->
          <div v-if="messages.length === 0" class="welcome-section">
            <div class="welcome-header">
              <div class="welcome-avatar">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="welcome-bot-icon">
                  <rect x="3" y="11" width="18" height="10" rx="2" />
                  <path d="M12 2v6M8 8h8M9 15h.01M15 15h.01" />
                </svg>
              </div>
              <div class="welcome-title-group">
                <h2 class="welcome-title">您好！我是您的 CRM 智能助理</h2>
                <p class="welcome-subtitle">我可以帮您快速查询联系人、检索关联事项，或者通过自然语言直接为您创建待办。数据在您的当前账号安全隔离。</p>
              </div>
            </div>
            
            <div class="welcome-prompts">
              <span class="prompts-label">试试点击以下指令：</span>
              <div class="capsules-wrapper">
                <button 
                  v-for="(prompt, index) in recommendPrompts" 
                  :key="index"
                  class="prompt-capsule"
                  @click="clickRecommend(prompt)"
                >
                  <svg v-if="index === 0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="capsule-icon"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
                  <svg v-else-if="index === 1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="capsule-icon"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                  <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="capsule-icon"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                  {{ prompt.title }}
                </button>
              </div>
            </div>
          </div>

          <!-- 历史消息展示 -->
          <div v-else class="message-list">
            <div 
              v-for="msg in messages" 
              :key="msg.id" 
              :class="['message-row', msg.sender === 'user' ? 'user-row' : 'agent-row']"
            >
              <!-- 气泡 -->
              <div 
                class="message-bubble"
                :class="{
                  'msg-write-success': msg.queryType === 'write_success',
                  'msg-write-cancelled': msg.queryType === 'write_cancelled'
                }"
              >
                <!-- 用户消息直接渲染文本 -->
                <template v-if="msg.sender === 'user'">
                  <p class="msg-text">{{ msg.text }}</p>
                </template>
  
                <!-- 智能助手渲染结构化响应 -->
                <template v-else>
                  <!-- 预确认卡片结构 -->
                  <div v-if="msg.isConfirmCard && msg.parsedParams" class="confirm-card-wrapper">
                    <div class="reply-header">
                      <div class="reply-avatar-mini">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mini-bot-icon">
                          <rect x="3" y="11" width="18" height="10" rx="2" />
                          <path d="M12 2v6M8 8h8M9 15h.01M15 15h.01" />
                        </svg>
                      </div>
                      <span class="reply-summary">{{ msg.text }}</span>
                    </div>
                    
                    <div class="confirm-card-content">
                      <div class="confirm-param-item">
                        <span class="param-label">联系人:</span>
                        <span class="param-value contact-value">
                          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="param-icon"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                          {{ msg.parsedParams.contactName || '未识别到联系人' }}
                        </span>
                      </div>
                      
                      <div class="confirm-param-item">
                        <span class="param-label">事项时间:</span>
                        <span class="param-value">
                          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="param-icon"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                          {{ msg.parsedParams.todoTime || '未识别到时间' }}
                        </span>
                      </div>
  
                      <div class="confirm-param-item">
                        <span class="param-label">具体内容:</span>
                        <span class="param-value highlight-content">{{ msg.parsedParams.content || '未识别到具体内容' }}</span>
                      </div>
  
                      <div class="confirm-param-item">
                        <span class="param-label">优先级:</span>
                        <span class="param-value">
                          <span 
                            class="priority-badge"
                            :class="{
                              'priority-high': msg.parsedParams.priority === 2,
                              'priority-medium': msg.parsedParams.priority === 1,
                              'priority-low': msg.parsedParams.priority === 0 || msg.parsedParams.priority === undefined
                            }"
                          >
                            {{ msg.parsedParams.priority === 2 ? '紧急' : (msg.parsedParams.priority === 1 ? '重要' : '普通') }}
                          </span>
                        </span>
                      </div>
                    </div>
  
                    <!-- 动作按钮区 -->
                    <div class="confirm-card-actions" v-if="msg.confirmState === 'pending'">
                      <el-button 
                        type="primary" 
                        @click="handleConfirm(msg)"
                        :loading="isActionPending && activeLogId === msg.logId"
                        :disabled="isActionPending"
                        class="btn-confirm-action"
                      >
                        确认创建
                      </el-button>
                      <el-button 
                        type="default" 
                        @click="handleCancel(msg)"
                        :disabled="isActionPending"
                        class="btn-cancel-action"
                      >
                        取消
                      </el-button>
                    </div>
                    <div class="confirm-card-status" v-else>
                      <span v-if="msg.confirmState === 'confirmed'" class="status-text text-success">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" class="status-icon"><polyline points="20 6 9 17 4 12"/></svg>
                        已确认创建
                      </span>
                      <span v-else-if="msg.confirmState === 'cancelled'" class="status-text text-cancelled">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="status-icon"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                        操作已取消
                      </span>
                    </div>
                  </div>
  
                  <!-- 原有查询/非确认卡片回复结构 -->
                  <div v-else class="agent-reply">
                    <!-- 机器人摘要说明 -->
                    <div class="reply-header">
                      <div class="reply-avatar-mini">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mini-bot-icon">
                          <rect x="3" y="11" width="18" height="10" rx="2" />
                          <path d="M12 2v6M8 8h8M9 15h.01M15 15h.01" />
                        </svg>
                      </div>
                      <span class="reply-summary">{{ msg.text }}</span>
                    </div>
  
                    <!-- 结果渲染区 -->
                    <div class="reply-body" v-if="msg.results && msg.results.length > 0">
                      <!-- 联系人列表渲染 -->
                      <div v-if="msg.queryType === 'contact'" class="contact-results-grid">
                        <div 
                          v-for="contact in getContactResults(msg.results)" 
                          :key="contact.contactId" 
                          class="contact-result-card"
                          @click="goToContactDetail(contact.contactId)"
                        >
                          <div class="contact-card-top">
                            <img :src="getAvatarUrl(contact.avatarUrl)" :alt="contact.name" class="contact-avatar-img" />
                            <div class="contact-basic">
                              <h4 class="contact-name">{{ contact.name }}</h4>
                              <div class="contact-tags">
                                <span 
                                  v-for="tag in contact.tags" 
                                  :key="tag" 
                                  class="tag-badge"
                                  :style="getTagStyle(tag)"
                                >
                                  {{ tag }}
                                </span>
                              </div>
                            </div>
                          </div>
                          <div class="contact-card-details">
                            <div class="detail-item" v-if="contact.phone">
                              <span class="detail-label">手机:</span>
                              <span class="detail-val">{{ contact.phone }}</span>
                            </div>
                            <div class="detail-item" v-if="contact.wechat">
                              <span class="detail-label">微信:</span>
                              <span class="detail-val">{{ contact.wechat }}</span>
                            </div>
                            <div class="detail-item" v-if="contact.email">
                              <span class="detail-label">邮箱:</span>
                              <span class="detail-val email-val">{{ contact.email }}</span>
                            </div>
                          </div>
  
                        </div>
                      </div>
  
                      <!-- 待办事项列表渲染 -->
                      <div v-else-if="msg.queryType === 'todo'" class="todo-results-list">
                        <div 
                          v-for="todo in getTodoResults(msg.results)" 
                          :key="todo.matterId" 
                          class="todo-result-item"
                        >
                          <div class="todo-item-main">
                            <div class="todo-title-row">
                              <span 
                                class="todo-status-badge"
                                :class="{
                                  'status-pending': todo.status === 0,
                                  'status-cancelled': todo.status === 1,
                                  'status-completed': todo.status === 2
                                }"
                              >
                                {{ todo.status === 2 ? '已完成' : (todo.status === 1 ? '已取消' : '待完成') }}
                              </span>
                              <h4 class="todo-content">{{ todo.content }}</h4>
                            </div>
                            <div class="todo-meta-row">
                              <span class="meta-item" v-if="todo.contactName">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="meta-icon"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                                关联人: {{ todo.contactName }}
                              </span>
                              <span class="meta-item">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="meta-icon"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                                时间: {{ todo.todoTime }}
                              </span>
                            </div>
                          </div>
                          <div class="todo-item-right">
                            <span 
                              class="priority-badge"
                              :class="{
                                'priority-high': todo.priority === 3,
                                'priority-medium': todo.priority === 2,
                                'priority-low': todo.priority === 1
                              }"
                            >
                              {{ todo.priority === 3 ? '紧急' : (todo.priority === 2 ? '重要' : '普通') }}
                            </span>
                            <span v-if="todo.overdue" class="overdue-warning">已逾期</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </template>
              </div>
              <!-- 时间 -->
              <span class="message-time">{{ formatTime(msg.time) }}</span>
            </div>
  
            <!-- Loading 等待效果 -->
            <div v-if="isLoading" class="message-row agent-row">
              <div class="message-bubble bubble-loading">
                <div class="loading-dots">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入栏 -->
        <footer class="input-panel">
          <div class="input-panel-glass">
            <input 
              type="text" 
              v-model="inputQuery" 
              placeholder="输入姓名/微信/事项指令，如“提醒我明天下午三点联系张三确认合同”..."
              @keydown.enter="sendQuery"
              :disabled="isLoading || hasPendingConfirm"
              class="query-input"
            />
            <div class="panel-actions">
              <button 
                class="action-btn send-btn" 
                :disabled="isLoading || hasPendingConfirm || !inputQuery.trim()" 
                @click="sendQuery"
              >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" class="action-icon">
                  <line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/>
                </svg>
                {{ isLoading ? '正在思考' : '发送' }}
              </button>
            </div>
          </div>
        </footer>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, computed } from 'vue'
import { useRouter } from 'vue-router'
import { queryAgent, executeAgent, confirmAgent, type AgentResultItem } from '@/api/agent'
import type { ContactInfo } from '@/api/contact'
import type { TodoInfo } from '@/types/todo'
import { ElMessage } from 'element-plus'
import { resolveAvatarUrl } from '@/utils/avatar'

const getTodoResults = (results?: AgentResultItem[]) => {
  return (results || []) as TodoInfo[]
}

const getContactResults = (results?: AgentResultItem[]) => {
  return (results || []) as ContactInfo[]
}

const router = useRouter()

// 消息结构
interface Message {
  id: string
  sender: 'user' | 'agent'
  text: string
  queryType?: 'contact' | 'todo' | 'unsupported' | 'write_success' | 'write_cancelled'
  results?: AgentResultItem[]
  time: Date
  // write agent fields:
  isConfirmCard?: boolean
  logId?: number
  needConfirm?: number
  actionType?: string
  parsedParams?: {
    contactId?: string
    contactName?: string
    todoTime?: string
    content?: string
    priority?: number
  }
  confirmState?: 'pending' | 'confirmed' | 'cancelled'
}

const messages = ref<Message[]>([])
const currentSessionId = ref<string | null>(null)
const inputQuery = ref('')
const isLoading = ref(false)
const chatAreaRef = ref<HTMLDivElement | null>(null)

// 推荐提问卡片
const recommendPrompts = [
  {
    title: '查找联系人',
    content: '帮我查一下张三的联系方式'
  },
  {
    title: '查询联系事项',
    content: '查张三最近的待办事项'
  },
  {
    title: '创建待办事项',
    content: '明天下午三点提醒我联系张三确认合同'
  }
]

// 检查是否存在待确认卡片
const hasPendingConfirm = computed(() => {
  return messages.value.some(msg => msg.isConfirmCard && msg.confirmState === 'pending')
})

// 确认或取消操作挂起状态
const isActionPending = ref(false)
const activeLogId = ref<number | null>(null)

const getErrorMessage = (error: unknown, fallback: string): string => {
  if (error instanceof Error && error.message) {
    const lowerMsg = error.message.toLowerCase()
    if (lowerMsg.includes('timeout')) {
      return '网络请求超时，智能助手响应较慢，请稍后再试。'
    }
    if (lowerMsg.includes('network') || lowerMsg.includes('failed')) {
      return '网络连接失败，请检查您的网络连接或稍后重试。'
    }
    return error.message
  }
  return fallback
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (chatAreaRef.value) {
    chatAreaRef.value.scrollTop = chatAreaRef.value.scrollHeight
  }
}

// 清空聊天
const clearChat = () => {
  messages.value = []
  currentSessionId.value = null
}

// 推荐问题点击
const clickRecommend = (prompt: { title: string; content: string }) => {
  inputQuery.value = prompt.content
  sendQuery()
}

// 继续上一轮意图
const continueLastIntent = () => {
  if (currentSessionId.value) {
    ElMessage.info('已关联上一轮意图，请在输入框继续输入信息补充说明。')
  } else {
    ElMessage.warning('当前没有进行中的会话意图，请直接提问。')
  }
}

// 发送查询
const sendQuery = async () => {
  const query = inputQuery.value.trim()
  if (!query || isLoading.value || hasPendingConfirm.value) return

  // 1. 添加用户消息
  const userMsgId = 'u_' + Date.now()
  messages.value.push({
    id: userMsgId,
    sender: 'user',
    text: query,
    time: new Date()
  })

  inputQuery.value = ''
  isLoading.value = true
  await scrollToBottom()

  try {
    // 统一调用后端写预处理/智能交互端点，传入 sessionId
    const res = await executeAgent({
      input: query,
      sessionId: currentSessionId.value || undefined
    })

    // 缓存返回的会话标识以实现后续多轮会话
    if (res.sessionId) {
      currentSessionId.value = res.sessionId
    }

    if (res.isClarifying) {
      // 需要澄清：在聊天中追加助手的澄清追问提示
      messages.value.push({
        id: 'a_' + Date.now(),
        sender: 'agent',
        text: res.summary || '请补充缺失的信息。',
        time: new Date()
      })
    } else if (res.needConfirm === 1 && res.actionType === 'create_todo') {
      // 槽位已齐全：在聊天流中渲染待确认卡片
      messages.value.push({
        id: 'a_' + Date.now(),
        sender: 'agent',
        text: res.summary || '已为您生成待办事项预确认卡片，请核对：',
        isConfirmCard: true,
        logId: res.logId,
        needConfirm: res.needConfirm,
        actionType: res.actionType,
        parsedParams: res.parsedParams,
        confirmState: 'pending',
        time: new Date()
      })
      // 槽位齐全进入确认气泡后，保留 sessionId 以维持后续记忆
      // currentSessionId.value = null
    } else if (res.actionType === 'query_contact' || res.actionType === 'query_todo') {
      // 查询意图：展示返回的查询结果列表
      messages.value.push({
        id: 'a_' + Date.now(),
        sender: 'agent',
        text: res.summary || '查询成功。',
        queryType: res.actionType === 'query_contact' ? 'contact' : 'todo',
        results: res.results || [],
        time: new Date()
      })
      // 保留会话用于后续对话历史
      // currentSessionId.value = null
    } else {
      // 不支持的写操作或其它兜底行为
      messages.value.push({
        id: 'a_' + Date.now(),
        sender: 'agent',
        text: res.summary || '抱歉，智能助手目前仅支持“创建事项”的写操作，暂不支持其他类型的写请求。',
        queryType: 'unsupported',
        results: [],
        time: new Date()
      })
      // 保留会话用于后续对话历史
      // currentSessionId.value = null
    }
  } catch (error: unknown) {
    const message = getErrorMessage(error, '操作失败，请检查网络或登录状态。')
    console.error('Agent process error:', error)
    messages.value.push({
      id: 'a_err_' + Date.now(),
      sender: 'agent',
      text: message,
      queryType: 'unsupported',
      results: [],
      time: new Date()
    })
    ElMessage.error(message)
    // 异常时不重置 sessionId，避免意外丢失记忆
    // currentSessionId.value = null
  } finally {
    isLoading.value = false
    await scrollToBottom()
  }
}

// 确认创建待办事项
const handleConfirm = async (msg: Message) => {
  if (isActionPending.value || !msg.logId) return
  isActionPending.value = true
  activeLogId.value = msg.logId
  try {
    const res = await confirmAgent({
      logId: msg.logId,
      action: 'confirm'
    })
    ElMessage.success('成功确认创建待办事项！')
    
    // 更新当前消息状态为已确认
    msg.confirmState = 'confirmed'
    
    // 聊天中紧接展示一条执行成功的绿色提示气泡
    messages.value.push({
      id: 'a_success_' + Date.now(),
      sender: 'agent',
      text: `已为您成功创建待办事项！\n事项内容：${res?.content || msg.parsedParams?.content || ''}\n时间：${res?.todoTime || msg.parsedParams?.todoTime || ''}`,
      queryType: 'write_success',
      time: new Date()
    })
    // 写入成功也保留会话，便于连续交流
    // currentSessionId.value = null
  } catch (error: unknown) {
    console.error('Confirm agent error:', error)
    ElMessage.error(getErrorMessage(error, '确认创建失败'))
  } finally {
    isActionPending.value = false
    activeLogId.value = null
    await scrollToBottom()
  }
}

// 取消创建待办事项
const handleCancel = async (msg: Message) => {
  if (isActionPending.value || !msg.logId) return
  isActionPending.value = true
  activeLogId.value = msg.logId
  try {
    await confirmAgent({
      logId: msg.logId,
      action: 'cancel'
    })
    ElMessage.info('已取消事项创建。')
    
    // 更新当前消息状态为已取消
    msg.confirmState = 'cancelled'
    
    // 聊天流追加一条助手灰色提示
    messages.value.push({
      id: 'a_cancel_' + Date.now(),
      sender: 'agent',
      text: '已取消本次事项创建。',
      queryType: 'write_cancelled',
      time: new Date()
    })
    // 取消也保留会话，便于连续交流
    // currentSessionId.value = null
  } catch (error: unknown) {
    console.error('Cancel agent error:', error)
    ElMessage.error(getErrorMessage(error, '取消失败'))
  } finally {
    isActionPending.value = false
    activeLogId.value = null
    await scrollToBottom()
  }
}

// 头像相对路径拼装
function getAvatarUrl(url: string | null): string {
  return resolveAvatarUrl(url) || 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&auto=format&fit=crop&q=80'
}

// 标签色彩渲染
const getTagStyle = (tagName: string) => {
  if (tagName === '老师') return { backgroundColor: '#f0fdf4', color: '#15803d', borderColor: '#bbf7d0', border: '1px solid' }
  if (tagName === '实习') return { backgroundColor: '#fff7ed', color: '#c2410c', borderColor: '#fed7aa', border: '1px solid' }
  if (tagName === '同学') return { backgroundColor: '#f5f3ff', color: '#6d28d9', borderColor: '#ddd6fe', border: '1px solid' }
  return { backgroundColor: '#eff6ff', color: '#1d4ed8', borderColor: '#bfdbfe', border: '1px solid' }
}

// 跳转到联系人详情
const goToContactDetail = (contactId: string) => {
  if (contactId) {
    router.push(`/contacts/${contactId}`)
  }
}

// 格式化时间
const formatTime = (date: Date) => {
  const h = String(date.getHours()).padStart(2, '0')
  const m = String(date.getMinutes()).padStart(2, '0')
  return `${h}:${m}`
}
</script>

<style scoped>
.agent-container {
  position: relative;
  width: 100%;
  height: calc(100vh - 110px);
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  background-color: #f8fafc;
}

/* 渐变光晕背景，调成温和的冷蓝色微光 */
.glow-bg {
  position: absolute;
  border-radius: 50%;
  filter: blur(140px);
  opacity: 0.08;
  z-index: 1;
  pointer-events: none;
}

.glow-1 {
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, #2563eb 0%, rgba(37, 99, 235, 0) 70%);
  top: -20%;
  right: -10%;
}

.glow-2 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, #3b82f6 0%, rgba(59, 130, 246, 0) 70%);
  bottom: -15%;
  left: -5%;
}

/* 主工作台双栏布局 */
.agent-workbench {
  position: relative;
  z-index: 2;
  width: 96%;
  max-width: 1280px;
  height: 94%;
  display: flex;
  gap: 20px;
  overflow: hidden;
}

/* 左侧能力说明栏 */
.agent-sidebar {
  width: 310px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow-y: auto;
  padding-right: 4px;
}

/* 侧边栏滚动条美化 */
.agent-sidebar::-webkit-scrollbar {
  width: 4px;
}
.agent-sidebar::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 2px;
}

/* 左栏卡片通用样式 */
.sidebar-section {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(226, 232, 240, 0.6);
  border-radius: var(--radius-lg, 14px);
  padding: 18px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.01);
}

.section-title {
  font-size: 14px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 14px;
  padding-left: 8px;
  border-left: 3px solid var(--color-primary, #2563eb);
  line-height: 1.2;
}

/* 能力列表 */
.capability-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.capability-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.capability-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.contact-icon {
  background: rgba(37, 99, 235, 0.08);
  color: var(--color-primary, #2563eb);
}

.todo-icon {
  background: rgba(16, 185, 129, 0.08);
  color: #10b981;
}

.confirm-icon {
  background: rgba(245, 158, 11, 0.08);
  color: #f59e0b;
}

.capability-icon svg {
  width: 16px;
  height: 16px;
}

.capability-info h4 {
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 2px 0;
}

.capability-info p {
  font-size: 11px;
  color: #64748b;
  line-height: 1.4;
  margin: 0;
}

/* 快捷动作 */
.action-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.quick-action-btn {
  width: 100%;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: var(--radius-md, 10px);
  padding: 10px 12px;
  text-align: left;
  display: flex;
  flex-direction: column;
  gap: 2px;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-action-btn:hover {
  border-color: var(--color-primary-border, #bfdbfe);
  background: var(--color-primary-light, #eff6ff);
  transform: translateX(2px);
}

.action-btn-title {
  font-size: 12px;
  font-weight: 600;
  color: #334155;
}

.action-btn-desc {
  font-size: 11px;
  color: #64748b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 继续上次意图特有样式 */
.continue-btn {
  background: rgba(255, 255, 255, 0.5);
  border-style: dashed;
}

.continue-btn.has-session {
  border-color: #f59e0b;
  background: #fffbeb;
  border-style: solid;
}

.continue-btn.has-session .action-btn-title {
  color: #d97706;
}

.continue-btn.has-session .action-btn-desc {
  color: #b45309;
}

/* 使用提示 */
.tips-list {
  padding-left: 16px;
  margin: 0;
}

.tips-list li {
  font-size: 11px;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 6px;
}

/* 右侧会话工作区 */
.agent-chat-panel {
  flex: 1;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(226, 232, 240, 0.8);
  border-radius: var(--radius-xl, 18px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.02);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 会话头部 */
.chat-header {
  padding: 16px 20px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.5);
}

.agent-status-badge {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 8px;
  height: 8px;
  background-color: #10b981;
  border-radius: 50%;
  display: inline-block;
  box-shadow: 0 0 8px #10b981;
}

.status-text {
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
}

.clear-chat-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border: 1px solid #e2e8f0;
  border-radius: var(--radius-sm, 6px);
  background: white;
  font-size: 12px;
  color: #64748b;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.clear-chat-btn:hover {
  background: #f1f5f9;
  color: var(--color-danger, #ef4444);
  border-color: #fca5a5;
}

.btn-icon {
  width: 14px;
  height: 14px;
}

/* 消息滚动区域 */
.chat-area {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: rgba(248, 250, 252, 0.2);
  display: flex;
  flex-direction: column;
}

/* 消息流滚动条美化 */
.chat-area::-webkit-scrollbar {
  width: 6px;
}

.chat-area::-webkit-scrollbar-track {
  background: transparent;
}

.chat-area::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.08);
  border-radius: var(--radius-full, 999px);
}

.chat-area::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.15);
}

/* 顶部收紧欢迎区 */
.welcome-section {
  margin: auto 0;
  background: linear-gradient(135deg, rgba(239, 246, 255, 0.4) 0%, rgba(248, 250, 252, 0.4) 100%);
  border: 1px solid rgba(219, 234, 254, 0.5);
  border-radius: var(--radius-lg, 14px);
  padding: 24px;
  box-shadow: var(--shadow-sm, 0 1px 2px rgba(0,0,0,0.05));
}

.welcome-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.welcome-avatar {
  width: 52px;
  height: 52px;
  background: linear-gradient(135deg, var(--color-primary, #2563eb) 0%, #1d4ed8 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.15);
  flex-shrink: 0;
}

.welcome-bot-icon {
  width: 28px;
  height: 28px;
  color: white;
}

.welcome-title-group {
  flex: 1;
}

.welcome-title {
  font-size: 18px;
  color: #0f172a;
  font-weight: 700;
  margin: 0 0 6px 0;
}

.welcome-subtitle {
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
  margin: 0;
}

/* 欢迎区快捷提问胶囊 */
.welcome-prompts {
  display: flex;
  flex-direction: column;
  gap: 10px;
  border-top: 1px dashed rgba(226, 232, 240, 0.8);
  padding-top: 16px;
}

.prompts-label {
  font-size: 12px;
  font-weight: 600;
  color: #475569;
}

.capsules-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.prompt-capsule {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: var(--radius-full, 999px);
  padding: 6px 14px;
  font-size: 12px;
  color: #475569;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
}

.prompt-capsule:hover {
  background: var(--color-primary-light, #eff6ff);
  border-color: var(--color-primary, #2563eb);
  color: var(--color-primary, #2563eb);
  transform: translateY(-1px);
}

.capsule-icon {
  width: 13px;
  height: 13px;
}

/* 消息气泡流 */
.message-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message-row {
  display: flex;
  flex-direction: column;
  max-width: 85%;
}

.user-row {
  align-self: flex-end;
  align-items: flex-end;
}

.agent-row {
  align-self: flex-start;
  align-items: flex-start;
}

.message-bubble {
  padding: 10px 16px;
  border-radius: var(--radius-lg, 14px);
  font-size: 13.5px;
  line-height: 1.5;
  position: relative;
}

.user-row .message-bubble {
  background: linear-gradient(135deg, var(--color-primary, #2563eb) 0%, #1d4ed8 100%);
  color: white;
  border-bottom-right-radius: 4px;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.1);
}

.agent-row .message-bubble {
  background: white;
  color: #1e293b;
  border-bottom-left-radius: 4px;
  border: 1px solid rgba(226, 232, 240, 0.7);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.01);
  width: 100%;
}

.msg-text {
  margin: 0;
  white-space: pre-wrap;
}

.message-time {
  font-size: 10.5px;
  color: #94a3b8;
  margin-top: 4px;
}

/* 结构化与确认卡片升级 */
.agent-reply {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.reply-avatar-mini {
  width: 22px;
  height: 22px;
  background: rgba(37, 99, 235, 0.08);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.mini-bot-icon {
  width: 13px;
  height: 13px;
  color: var(--color-primary, #2563eb);
}

.reply-summary {
  font-weight: 600;
  color: #334155;
  font-size: 13.5px;
}

/* 联系人网格卡片（优化版） */
.contact-results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 12px;
  margin-top: 4px;
}

.contact-result-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: var(--radius-md, 10px);
  padding: 12px 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.contact-result-card:hover {
  transform: translateY(-1px);
  background: white;
  border-color: var(--color-primary, #2563eb);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.03);
}

.contact-card-top {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.contact-avatar-img {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-sm, 6px);
  object-fit: cover;
}

.contact-basic {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.contact-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.contact-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.tag-badge {
  font-size: 9.5px;
  padding: 0 4px;
  border-radius: 3px;
  font-weight: 500;
}

.contact-card-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 11.5px;
  color: #475569;
  border-top: 1px dashed #e2e8f0;
  padding-top: 8px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
}

.detail-label {
  color: #94a3b8;
}

.detail-val {
  font-weight: 500;
  color: #334155;
}

/* 待办事项结果卡片（优化版） */
.todo-results-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 4px;
}

.todo-result-item {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: var(--radius-md, 10px);
  padding: 10px 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.todo-item-main {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
}

.todo-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.todo-status-badge {
  font-size: 10px;
  font-weight: 600;
  padding: 1px 6px;
  border-radius: 4px;
}

.status-pending {
  background: rgba(245, 158, 11, 0.1);
  color: #d97706;
}

.status-completed {
  background: rgba(16, 185, 129, 0.1);
  color: #059669;
}

.status-cancelled {
  background: rgba(148, 163, 184, 0.1);
  color: #475569;
}

.todo-content {
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.todo-meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 11px;
  color: #64748b;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-icon {
  width: 12px;
  height: 12px;
  color: #94a3b8;
}

.todo-item-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  flex-shrink: 0;
}

.priority-badge {
  font-size: 10px;
  font-weight: 500;
  padding: 1px 5px;
  border-radius: 3px;
}

.priority-high {
  background: rgba(239, 68, 68, 0.08);
  color: #dc2626;
}

.priority-medium {
  background: rgba(249, 115, 22, 0.08);
  color: #ea580c;
}

.priority-low {
  background: rgba(59, 130, 246, 0.08);
  color: #2563eb;
}

/* 待办事项确认卡片（优化版） */
.confirm-card-wrapper {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

.confirm-card-content {
  background: #f8fafc;
  border-radius: var(--radius-md, 10px);
  padding: 12px;
  border: 1px solid #e2e8f0;
  border-left: 4px solid var(--color-primary, #2563eb);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.confirm-param-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.param-label {
  color: #64748b;
  font-weight: 600;
  width: 60px;
  flex-shrink: 0;
}

.param-value {
  color: #1e293b;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
  word-break: break-all;
}

.param-icon {
  width: 12px;
  height: 12px;
  color: #94a3b8;
}

.contact-value {
  color: var(--color-primary, #2563eb);
  font-weight: 600;
}

.highlight-content {
  color: #0f172a;
  font-weight: 600;
}

.confirm-card-actions {
  display: flex;
  gap: 8px;
}

.btn-confirm-action {
  font-size: 12px;
  padding: 8px 16px;
  height: 32px;
  border-radius: var(--radius-sm, 6px);
  font-weight: 600;
}

.btn-cancel-action {
  font-size: 12px;
  padding: 8px 16px;
  height: 32px;
  border-radius: var(--radius-sm, 6px);
}

.confirm-card-status {
  display: flex;
  align-items: center;
}

.status-text {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 700;
}

.status-icon {
  width: 14px;
  height: 14px;
}

.text-success {
  color: #10b981;
}

.text-cancelled {
  color: #94a3b8;
}

/* 提示气泡 */
.msg-write-success {
  background-color: #f0fdf4 !important;
  border-color: #bbf7d0 !important;
  color: #15803d !important;
}

.msg-write-success .reply-summary {
  color: #15803d !important;
}

.msg-write-success .mini-bot-icon {
  color: #166534 !important;
}

.msg-write-success .reply-avatar-mini {
  background-color: #dcfce7 !important;
}

.msg-write-cancelled {
  background-color: #f8fafc !important;
  border-color: #e2e8f0 !important;
  color: #64748b !important;
}

.msg-write-cancelled .reply-summary {
  color: #475569 !important;
}

.msg-write-cancelled .mini-bot-icon {
  color: #64748b !important;
}

.msg-write-cancelled .reply-avatar-mini {
  background-color: #f1f5f9 !important;
}

/* Loading 气泡细节 */
.bubble-loading {
  background: white !important;
  border: 1px solid rgba(226, 232, 240, 0.7) !important;
  padding: 10px 16px !important;
  width: fit-content !important;
}

.loading-dots {
  display: flex;
  gap: 4px;
  align-items: center;
  height: 12px;
}

.loading-dots span {
  width: 6px;
  height: 6px;
  background: var(--color-primary, #2563eb);
  border-radius: 50%;
  display: inline-block;
  animation: loading-dot 1.4s infinite ease-in-out both;
}

.loading-dots span:nth-child(1) { animation-delay: -0.32s; }
.loading-dots span:nth-child(2) { animation-delay: -0.16s; }

/* 输入区样式 */
.input-panel {
  padding: 16px 20px 20px;
  background: rgba(255, 255, 255, 0.6);
  border-top: 1px solid rgba(226, 232, 240, 0.8);
}

.input-panel-glass {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: var(--radius-lg, 14px);
  padding: 6px 6px 6px 14px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.015);
  transition: all 0.2s;
}

.input-panel-glass:focus-within {
  border-color: var(--color-primary, #2563eb);
  box-shadow: 0 4px 18px rgba(37, 99, 235, 0.06);
}

.query-input {
  flex: 1;
  border: none;
  background: transparent;
  outline: none;
  font-size: 13.5px;
  color: #1e293b;
}

.query-input::placeholder {
  color: #94a3b8;
}

.panel-actions {
  flex-shrink: 0;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  border: none;
  border-radius: var(--radius-md, 10px);
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.send-btn {
  background: linear-gradient(135deg, var(--color-primary, #2563eb) 0%, #1d4ed8 100%);
  color: white;
  padding: 8px 16px;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.3);
}

.send-btn:disabled {
  background: #cbd5e1;
  color: #94a3b8;
  cursor: not-allowed;
  box-shadow: none;
}

.action-icon {
  width: 13px;
  height: 13px;
}

/* 动效关键帧 */
@keyframes loading-dot {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1.0); }
}

/* 响应式媒体适配 (针对平板与移动窄屏) */
@media (max-width: 992px) {
  .agent-container {
    height: calc(100vh - 60px);
    padding: 10px 0 0 0;
    align-items: flex-start;
  }

  .agent-workbench {
    width: 100%;
    height: 100%;
    flex-direction: column;
    gap: 12px;
    padding: 0 12px 12px;
    overflow-y: auto;
  }

  .agent-sidebar {
    width: 100%;
    flex-shrink: 0;
    overflow-y: visible;
    display: grid;
    grid-template-columns: 1fr;
    gap: 10px;
    padding-right: 0;
  }

  /* 窄屏时下沉到右侧主内容上方，变成精简折叠卡片 */
  .sidebar-section {
    padding: 12px;
    border-radius: var(--radius-md, 10px);
  }

  .section-title {
    font-size: 13px;
    margin-bottom: 10px;
  }

  .capability-list {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    gap: 10px;
  }

  .action-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
    gap: 8px;
  }

  .quick-action-btn {
    padding: 8px 10px;
  }

  .tips-list {
    display: flex;
    flex-direction: column;
    gap: 4px;
    padding-left: 14px;
  }

  .tips-list li {
    margin-bottom: 0;
  }

  .agent-chat-panel {
    width: 100%;
    flex: 1;
    min-height: 480px;
    border-radius: var(--radius-lg, 14px);
  }

  .chat-area {
    padding: 14px;
  }

  .welcome-section {
    padding: 16px;
  }

  .welcome-header {
    gap: 12px;
    margin-bottom: 12px;
  }

  .welcome-avatar {
    width: 44px;
    height: 44px;
  }

  .welcome-bot-icon {
    width: 24px;
    height: 24px;
  }

  .welcome-title {
    font-size: 15px;
  }

  .welcome-subtitle {
    font-size: 12px;
  }

  .contact-results-grid {
    grid-template-columns: 1fr;
  }

  .todo-result-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .todo-item-right {
    align-items: flex-start;
    flex-direction: row;
    width: 100%;
    justify-content: space-between;
    border-top: 1px dashed #e2e8f0;
    padding-top: 8px;
  }

  .confirm-param-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .param-label {
    width: auto;
  }

  .confirm-card-actions {
    flex-direction: column;
    width: 100%;
  }

  .confirm-card-actions .el-button {
    width: 100%;
    margin-left: 0 !important;
  }
}

/* ===== 移动窄屏专属覆盖 (max-width: 768px) ===== */
@media (max-width: 768px) {
  .agent-container {
    height: calc(100vh - 60px) !important;
    padding: 0 !important;
    background: #ffffff !important;
  }
  
  .glow-bg {
    display: none !important;
  }
  
  .agent-workbench {
    width: 100% !important;
    height: 100% !important;
    gap: 0 !important;
    padding: 0 !important;
    flex-direction: column !important;
    overflow: hidden !important;
  }
  
  .agent-sidebar {
    display: none !important; /* 彻底隐藏侧边栏能力和动作，仅展示右侧聊天 */
  }
  
  .agent-chat-panel {
    width: 100% !important;
    height: 100% !important;
    min-height: auto !important;
    border: none !important;
    border-radius: 0 !important;
    box-shadow: none !important;
    background: #ffffff !important;
  }
  
  .chat-header {
    padding: 12px 16px !important;
    background: #ffffff !important;
  }
  
  .chat-area {
    padding: 10px 12px !important;
    background: #f8fafc !important; /* 聊天背景带微灰，气泡显眼 */
  }
  
  .welcome-section {
    padding: 12px !important;
    border-radius: 12px !important;
    margin: 10px 0 !important;
  }
  
  .welcome-title-group h2 {
    font-size: 14px !important;
  }
  
  .welcome-subtitle {
    font-size: 11px !important;
  }
  
  .capsules-wrapper {
    gap: 6px !important;
  }
  
  .prompt-capsule {
    padding: 4px 10px !important;
    font-size: 11px !important;
    border-radius: 14px !important;
  }
  
  .message-row {
    max-width: 92% !important;
  }
  
  .message-bubble {
    padding: 8px 12px !important;
    font-size: 13px !important;
    border-radius: 12px !important;
  }
  
  .user-row .message-bubble {
    border-bottom-right-radius: 2px !important;
  }
  
  .agent-row .message-bubble {
    border-bottom-left-radius: 2px !important;
  }
  
  .input-panel {
    padding: 8px 10px !important;
    background: #ffffff !important;
    border-top: 1px solid #f1f5f9 !important;
  }
  
  .input-panel-glass {
    border: 1px solid #e2e8f0 !important;
    border-radius: 20px !important;
    padding: 4px 4px 4px 12px !important;
    box-shadow: none !important;
    background: #f8fafc !important;
  }
  
  .query-input {
    font-size: 13px !important;
  }
  
  .send-btn {
    padding: 6px 12px !important;
    font-size: 12px !important;
    border-radius: 16px !important;
    min-height: 28px !important;
  }
}
</style>
