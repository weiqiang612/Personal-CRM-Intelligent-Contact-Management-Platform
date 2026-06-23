<template>
  <div class="agent-container">
    <!-- 渐变光晕背景 -->
    <div class="glow-bg glow-1"></div>
    <div class="glow-bg glow-2"></div>

    <div class="agent-layout">
      <!-- 头部 -->
      <header class="agent-header">
        <div class="header-title-wrapper">
          <div class="agent-avatar">
            <div class="pulse-ring"></div>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="bot-icon">
              <path d="M12 2a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2 2 2 0 0 1-2-2V4a2 2 0 0 1 2-2Z" />
              <rect x="3" y="11" width="18" height="10" rx="2" />
              <path d="M12 11v4M8 15h8M2 14h1M21 14h1" />
            </svg>
          </div>
          <div class="header-text">
            <h2>智能助手</h2>
            <p>使用自然语言快速查询联系人或事项</p>
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
        <!-- 空状态及提问推荐区 -->
        <div v-if="messages.length === 0" class="empty-state">
          <div class="empty-avatar">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="big-bot-icon">
              <rect x="3" y="11" width="18" height="10" rx="2" />
              <path d="M12 2v6M8 8h8M2 14h1M21 14h1M9 15h.01M15 15h.01M9 18s1.5 1.5 3 1.5 3-1.5 3-1.5" />
            </svg>
          </div>
          <h3 class="empty-title">您好！我是您的 CRM 智能助理</h3>
          <p class="empty-desc">
            您可以向我提问查找您的联系人信息，或者筛选您的事项安排。
            本期我支持查询功能，且所有数据严格按当前登录用户进行权限隔离。
          </p>
          
          <div class="recommend-section">
            <p class="recommend-title">试试这样问我：</p>
            <div class="recommend-grid">
              <div 
                v-for="(prompt, index) in recommendPrompts" 
                :key="index" 
                class="recommend-card"
                @click="clickRecommend(prompt)"
              >
                <div class="recommend-icon">
                  <svg v-if="index === 0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                  <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                </div>
                <div class="recommend-text">
                  <h4>{{ prompt.title }}</h4>
                  <p>“{{ prompt.content }}”</p>
                </div>
              </div>
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
            <div class="message-bubble">
              <!-- 用户消息直接渲染文本 -->
              <template v-if="msg.sender === 'user'">
                <p class="msg-text">{{ msg.text }}</p>
              </template>

              <!-- 智能助手渲染结构化响应 -->
              <template v-else>
                <div class="agent-reply">
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
                        v-for="contact in msg.results" 
                        :key="contact.contactId" 
                        class="contact-result-card"
                        @click="goToContactDetail(contact.contactId)"
                      >
                        <div class="contact-card-top">
                          <img :src="getAvatarUrl(contact.avatarUrl)" :alt="contact.name" class="contact-avatar-img">
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
                        <div class="contact-card-notes" v-if="contact.notes">
                          <p class="notes-text"><strong>备注:</strong> {{ contact.notes }}</p>
                        </div>
                      </div>
                    </div>

                    <!-- 待办事项列表渲染 -->
                    <div v-else-if="msg.queryType === 'todo'" class="todo-results-list">
                      <div 
                        v-for="todo in msg.results" 
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
                              关联联系人: {{ todo.contactName }}
                            </span>
                            <span class="meta-item">
                              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="meta-icon"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                              执行时间: {{ todo.todoTime }}
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
                            {{ todo.priority === 3 ? '高优先级' : (todo.priority === 2 ? '中优先级' : '低优先级') }}
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
            placeholder="输入您想查找的联系人姓名、微信，或者查找事项，如“查张三的联系方式”..."
            @keydown.enter="sendQuery"
            :disabled="isLoading"
            class="query-input"
          />
          <div class="panel-actions">
            <button 
              class="action-btn send-btn" 
              :disabled="isLoading || !inputQuery.trim()" 
              @click="sendQuery"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" class="action-icon">
                <line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/>
              </svg>
              {{ isLoading ? '思考中...' : '发送' }}
            </button>
          </div>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { queryAgent } from '@/api/agent'
import { ElMessage } from 'element-plus'
import { resolveAvatarUrl } from '@/utils/avatar'

const router = useRouter()

// 消息结构
interface Message {
  id: string
  sender: 'user' | 'agent'
  text: string
  queryType?: 'contact' | 'todo' | 'unsupported'
  results?: any[]
  time: Date
}

const messages = ref<Message[]>([])
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
  }
]

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
}

// 推荐问题点击
const clickRecommend = (prompt: { title: string; content: string }) => {
  inputQuery.value = prompt.content
  sendQuery()
}

// 发送查询
const sendQuery = async () => {
  const query = inputQuery.value.trim()
  if (!query || isLoading.value) return

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
    // 2. 调用后端接口
    const res = await queryAgent({ input: query })
    
    // 3. 添加 Agent 响应消息
    messages.value.push({
      id: 'a_' + Date.now(),
      sender: 'agent',
      text: res.summary || '未收到有效摘要回复',
      queryType: res.queryType,
      results: res.results || [],
      time: new Date()
    })
  } catch (error: any) {
    console.error('Agent query error:', error)
    // 失败添加报错提醒消息
    messages.value.push({
      id: 'a_err_' + Date.now(),
      sender: 'agent',
      text: error.message || '查询失败，请检查网络或登录状态。',
      queryType: 'unsupported',
      results: [],
      time: new Date()
    })
    ElMessage.error(error.message || '助手处理异常')
  } finally {
    isLoading.value = false
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
  height: calc(100vh - 120px);
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  background-color: #f8fafc;
}

/* 炫彩渐变背景 */
.glow-bg {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.15;
  z-index: 1;
  pointer-events: none;
}

.glow-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, #6366f1 0%, rgba(99, 102, 241, 0) 70%);
  top: -10%;
  right: -5%;
}

.glow-2 {
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, #ec4899 0%, rgba(236, 72, 153, 0) 70%);
  bottom: -20%;
  left: -10%;
}

.agent-layout {
  position: relative;
  z-index: 2;
  width: 95%;
  max-width: 1000px;
  height: 95%;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 24px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 头部样式 */
.agent-header {
  padding: 20px 24px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.4);
}

.header-title-wrapper {
  display: flex;
  align-items: center;
  gap: 16px;
}

.agent-avatar {
  position: relative;
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.2);
}

.bot-icon {
  width: 26px;
  height: 26px;
  color: white;
}

.pulse-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 14px;
  background-color: rgba(99, 102, 241, 0.3);
  animation: pulse-avatar 2s infinite ease-in-out;
}

.header-text h2 {
  font-size: 18px;
  color: #1e293b;
  font-weight: 700;
  margin: 0 0 2px 0;
}

.header-text p {
  font-size: 12px;
  color: #64748b;
  margin: 0;
}

.clear-chat-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  background: white;
  font-size: 13px;
  color: #64748b;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.clear-chat-btn:hover {
  background: #f1f5f9;
  color: #ef4444;
  border-color: #fca5a5;
}

.btn-icon {
  width: 15px;
  height: 15px;
}

/* 聊天内容区 */
.chat-area {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background: rgba(248, 250, 252, 0.3);
  display: flex;
  flex-direction: column;
}

/* 空状态 */
.empty-state {
  margin: auto;
  max-width: 600px;
  text-align: center;
  padding: 40px 20px;
}

.empty-avatar {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: rgba(99, 102, 241, 0.1);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.big-bot-icon {
  width: 44px;
  height: 44px;
  color: #6366f1;
}

.empty-title {
  font-size: 20px;
  color: #1e293b;
  font-weight: 700;
  margin-bottom: 12px;
}

.empty-desc {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 36px;
}

.recommend-section {
  text-align: left;
}

.recommend-title {
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.recommend-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.recommend-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 16px;
  cursor: pointer;
  display: flex;
  gap: 12px;
  align-items: flex-start;
  transition: all 0.2s;
  box-shadow: 0 2px 4px rgba(0,0,0,0.01);
}

.recommend-card:hover {
  transform: translateY(-2px);
  border-color: #6366f1;
  box-shadow: 0 8px 16px rgba(99, 102, 241, 0.08);
}

.recommend-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: rgba(99, 102, 241, 0.08);
  color: #6366f1;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.recommend-icon svg {
  width: 16px;
  height: 16px;
}

.recommend-text h4 {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 4px 0;
}

.recommend-text p {
  font-size: 12px;
  color: #64748b;
  margin: 0;
}

/* 消息流 */
.message-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
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
  padding: 12px 18px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.5;
  position: relative;
}

.user-row .message-bubble {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  color: white;
  border-bottom-right-radius: 4px;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.15);
}

.agent-row .message-bubble {
  background: white;
  color: #1e293b;
  border-bottom-left-radius: 4px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.02);
  width: 100%;
}

.msg-text {
  margin: 0;
  white-space: pre-wrap;
}

.message-time {
  font-size: 11px;
  color: #94a3b8;
  margin-top: 4px;
}

/* 智能助手渲染回复内容 */
.agent-reply {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.reply-avatar-mini {
  width: 24px;
  height: 24px;
  background: rgba(99, 102, 241, 0.1);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.mini-bot-icon {
  width: 14px;
  height: 14px;
  color: #6366f1;
}

.reply-summary {
  font-weight: 500;
  color: #334155;
  font-size: 14px;
}

/* 联系人网格结果卡片 */
.contact-results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
  margin-top: 4px;
}

.contact-result-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.contact-result-card:hover {
  transform: translateY(-2px);
  background: white;
  border-color: #6366f1;
  box-shadow: 0 8px 20px rgba(99, 102, 241, 0.06);
}

.contact-card-top {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.contact-avatar-img {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  object-fit: cover;
}

.contact-basic {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.contact-name {
  font-size: 15px;
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
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 4px;
  font-weight: 500;
}

.contact-card-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 12px;
  color: #475569;
  border-top: 1px dashed #e2e8f0;
  padding-top: 10px;
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
}

.email-val {
  max-width: 170px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.contact-card-notes {
  margin-top: 10px;
  padding-top: 8px;
  border-top: 1px dashed #e2e8f0;
  font-size: 11px;
}

.notes-text {
  color: #64748b;
  margin: 0;
  line-height: 1.4;
}

/* 待办事项列表结果 */
.todo-results-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 4px;
}

.todo-result-item {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 14px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.todo-item-main {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.todo-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.todo-status-badge {
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 6px;
}

.status-pending {
  background: rgba(234, 179, 8, 0.1);
  color: #ca8a04;
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
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.todo-meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 12px;
  color: #64748b;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-icon {
  width: 14px;
  height: 14px;
  color: #94a3b8;
}

.todo-item-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
  flex-shrink: 0;
}

.priority-badge {
  font-size: 11px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 4px;
}

.priority-high {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
}

.priority-medium {
  background: rgba(249, 115, 22, 0.1);
  color: #ea580c;
}

.priority-low {
  background: rgba(59, 130, 246, 0.1);
  color: #2563eb;
}

.overdue-warning {
  font-size: 11px;
  font-weight: 700;
  color: #ef4444;
  background: #fef2f2;
  border: 1px solid #fecaca;
  padding: 1px 6px;
  border-radius: 4px;
  animation: flash 1.5s infinite alternate;
}

/* Loading 气泡细节 */
.bubble-loading {
  background: white !important;
  border: 1px solid rgba(226, 232, 240, 0.8) !important;
  padding: 14px 20px !important;
}

.loading-dots {
  display: flex;
  gap: 5px;
  align-items: center;
  height: 14px;
}

.loading-dots span {
  width: 7px;
  height: 7px;
  background: #6366f1;
  border-radius: 50%;
  display: inline-block;
  animation: loading-dot 1.4s infinite ease-in-out both;
}

.loading-dots span:nth-child(1) { animation-delay: -0.32s; }
.loading-dots span:nth-child(2) { animation-delay: -0.16s; }

/* 输入底部 */
.input-panel {
  padding: 20px 24px 24px;
  background: rgba(255, 255, 255, 0.4);
  border-top: 1px solid rgba(226, 232, 240, 0.8);
}

.input-panel-glass {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 18px;
  padding: 8px 8px 8px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.015);
  transition: border-color 0.2s, box-shadow 0.2s;
}

.input-panel-glass:focus-within {
  border-color: #6366f1;
  box-shadow: 0 4px 18px rgba(99, 102, 241, 0.08);
}

.query-input {
  flex: 1;
  border: none;
  background: transparent;
  outline: none;
  font-size: 14px;
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
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.send-btn {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  color: white;
  padding: 10px 20px;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.25);
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(99, 102, 241, 0.35);
}

.send-btn:disabled {
  background: #cbd5e1;
  color: #94a3b8;
  cursor: not-allowed;
  box-shadow: none;
}

.action-icon {
  width: 14px;
  height: 14px;
}

/* 动效关键帧 */
@keyframes pulse-avatar {
  0%, 100% {
    transform: scale(1);
    opacity: 0.3;
  }
  50% {
    transform: scale(1.15);
    opacity: 0;
  }
}

@keyframes loading-dot {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1.0); }
}

@keyframes flash {
  0% { opacity: 0.7; }
  100% { opacity: 1; }
}

/* 移动端适配 (小于 768px) */
@media (max-width: 768px) {
  .agent-container {
    height: calc(100vh - 60px); /* 移动端调整布局高度 */
  }
  
  .agent-layout {
    width: 100%;
    height: 100%;
    border: none;
    border-radius: 0;
  }

  .agent-header {
    padding: 14px 16px;
  }

  .clear-chat-btn span {
    display: none; /* 隐藏文本 */
  }
  
  .chat-area {
    padding: 16px;
  }

  .empty-state {
    padding: 20px 10px;
  }

  .recommend-grid {
    grid-template-columns: 1fr; /* 变单栏 */
    gap: 12px;
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
    border-top: 1px solid #f1f5f9;
    padding-top: 8px;
  }

  .input-panel {
    padding: 12px 16px 16px;
  }
}
</style>
