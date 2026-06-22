<template>
  <div class="dashboard-container">
    <!-- 四个大指标卡 -->
    <section class="stats-grid">
      <!-- 联系人总数 -->
      <div class="card stat-card">
        <div class="stat-icon primary">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:22px;height:22px;"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
        </div>
        <div class="stat-info">
          <span class="stat-label">联系人总数</span>
          <span class="stat-value">{{ overview.contactCount }}</span>
          <span class="stat-change up">
            较上周 <span class="change-value">+8</span>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" class="change-icon" style="width:12px;height:12px;"><line x1="7" y1="17" x2="17" y2="7"/><polyline points="7 7 17 7 17 17"/></svg>
          </span>
        </div>
      </div>

      <!-- 今日事项 -->
      <div class="card stat-card">
        <div class="stat-icon success">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" style="width:22px;height:22px;"><rect width="18" height="18" x="3" y="4" rx="2"/><path d="M3 10h18M8 2v4M16 2v4m-7 8 2 2 4-4"/></svg>
        </div>
        <div class="stat-info">
          <span class="stat-label">今日事项</span>
          <span class="stat-value">{{ overview.todayTodoCount }}</span>
          <span class="stat-change up">
            较昨日 <span class="change-value">+2</span>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" class="change-icon" style="width:12px;height:12px;"><line x1="7" y1="17" x2="17" y2="7"/><polyline points="7 7 17 7 17 17"/></svg>
          </span>
        </div>
      </div>

      <!-- 逾期事项 -->
      <div class="card stat-card">
        <div class="stat-icon danger">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:22px;height:22px;"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
        </div>
        <div class="stat-info">
          <span class="stat-label">逾期事项</span>
          <span class="stat-value">{{ overview.overdueTodoCount }}</span>
          <span class="stat-change down success-color">
            较昨日 <span class="change-value">-1</span>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" class="change-icon" style="width:12px;height:12px;"><line x1="7" y1="7" x2="17" y2="17"/><polyline points="17 7 17 17 7 17"/></svg>
          </span>
        </div>
      </div>

      <!-- 待完成 -->
      <div class="card stat-card">
        <div class="stat-icon warning">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:22px;height:22px;"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>
        </div>
        <div class="stat-info">
          <span class="stat-label">待完成</span>
          <span class="stat-value">{{ overview.pendingTodoCount }}</span>
          <span class="stat-change up">
            较昨日 <span class="change-value">+3</span>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" class="change-icon" style="width:12px;height:12px;"><line x1="7" y1="17" x2="17" y2="7"/><polyline points="7 7 17 7 17 17"/></svg>
          </span>
        </div>
      </div>
    </section>

    <!-- 中部两栏内容 -->
    <section class="dashboard-layout">
      <!-- 左侧：重要联系人 -->
      <div class="card panel-card contacts-panel">
        <div class="card-header">
          <h3 class="card-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:17px;height:17px;"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
            重要联系人
          </h3>
          <router-link to="/contacts" class="view-all-link">查看全部</router-link>
        </div>
        
        <div class="carousel-container" ref="carouselContainerRef">
          <div v-if="recentContacts.length === 0" class="empty-state">
            <svg class="empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M8 14s1.5 2 4 2 4-2 4-2M9 9h.01M15 9h.01"/></svg>
            <p>暂无重要联系人，请先去联系人中心添加！</p>
          </div>
          <div v-else class="carousel-track" ref="carouselTrackRef">
            <div
              v-for="pageIndex in totalSlides"
              :key="pageIndex"
              class="carousel-page"
              :class="{ active: pageIndex - 1 === activeSlide }"
            >
              <div
                v-for="contact in getPageContacts(pageIndex - 1)"
                :key="contact.contactId"
                class="contact-card"
                @click="goToContactDetail(contact.contactId)"
              >
                <img :src="getAvatarUrl(contact.avatarUrl)" :alt="contact.name" class="card-avatar">
                <span class="badge" :style="getTagStyle(getContactFirstTag(contact))">
                  {{ getContactFirstTag(contact) }}
                </span>
                <div class="card-name">{{ contact.name }}</div>
                <div class="card-company">{{ contact.address || '个人客户' }}</div>
                <div class="card-actions">
                  <a :href="`mailto:${contact.email || ''}`" class="contact-action-btn" @click.stop v-if="contact.email">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:14px;height:14px;"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>
                  </a>
                  <a :href="`tel:${contact.phone || ''}`" class="contact-action-btn" @click.stop v-if="contact.phone">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:14px;height:14px;"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/></svg>
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="carousel-indicators" v-if="totalSlides > 1">
          <span
            v-for="i in totalSlides"
            :key="i"
            class="indicator-dot"
            :class="{ active: activeSlide === i - 1 }"
            @click="slideCarousel(i - 1)"
          ></span>
        </div>
      </div>

      <!-- 右侧：今日事项 -->
      <div class="card panel-card todos-panel">
        <div class="card-header">
          <h3 class="card-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:17px;height:17px;"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4Z"/></svg>
            今日事项
          </h3>
          <router-link to="/todos" class="view-all-link">查看全部</router-link>
        </div>

        <div class="todo-list-modern">
          <div v-if="todayTodos.length === 0" class="empty-state">
            <svg class="empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="m9 12 2 2 4-4"/></svg>
            <p>今日暂无待办事项，喝杯咖啡轻松一下吧！</p>
          </div>
          <div
            v-else
            v-for="todo in todayTodos.slice(0, 4)"
            :key="todo.matterId"
            class="todo-item"
            :class="{ completed: todo.status === 2 }"
            @click="toggleTodoStatus(todo)"
          >
            <div class="todo-item-left">
              <div class="todo-checkbox">
                <!-- unchecked-icon -->
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="unchecked-icon"><circle cx="12" cy="12" r="10"/></svg>
                <!-- checked-icon -->
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="checked-icon"><circle cx="12" cy="12" r="10"/><path d="m9 12 2 2 4-4"/></svg>
              </div>
              <div class="todo-details">
                <span class="todo-title">{{ todo.content }}</span>
                <span class="todo-time">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:12px;height:12px;margin-right:2px;"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                  {{ formatTodoTime(todo.todoTime) }}
                  <span class="contact-tag-inline" v-if="todo.contactName" @click.stop="goToContactDetail(todo.contactId)">
                    @{{ todo.contactName }}
                  </span>
                </span>
              </div>
            </div>
            <div class="todo-item-right">
              <span class="badge" :class="getPriorityClass(todo.priority)">
                {{ getPriorityName(todo.priority) }}
              </span>
              <span class="todo-status pending">待完成</span>
              <span class="todo-status completed-text">已完成</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 底部图表区 -->
    <section class="charts-layout">
      <!-- 性别比例环形图 -->
      <div class="card chart-card">
        <div class="card-header">
          <h3 class="card-title">联系人性别比例</h3>
          <div class="card-actions-inline">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:14px;height:14px;cursor:pointer;"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>
            <router-link to="/contacts" class="action-link">查看详情</router-link>
          </div>
        </div>
        <div ref="genderChartRef" id="genderChart" style="width: 100%;"></div>
      </div>

      <!-- 未来 7 天趋势 -->
      <div class="card chart-card">
        <div class="card-header">
          <h3 class="card-title">未来 7 天事项趋势</h3>
          <div class="card-actions-inline">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:14px;height:14px;cursor:pointer;"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>
            <router-link to="/todos" class="action-link">查看详情</router-link>
          </div>
        </div>
        <div ref="trendChartRef" id="trendChart" style="width: 100%;"></div>
      </div>
    </section>

    <!-- 悬浮 Contact Agent 按钮 -->
    <button class="floating-agent-btn" :class="{ hidden: isDrawerOpen }" @click="toggleAgentDrawer" title="智能助手">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" style="width:22px;height:22px;"><path d="M11.017 2.814a1 1 0 0 1 1.966 0l1.051 5.558a2 2 0 0 0 1.594 1.594l5.558 1.051a1 1 0 0 1 0 1.966l-5.558 1.051a2 2 0 0 0-1.594 1.594l-1.051 5.558a1 1 0 0 1-1.966 0l-1.051-5.558a2 2 0 0 0-1.594-1.594l-5.558-1.051a1 1 0 0 1 0-1.966l5.558-1.051a2 2 0 0 0 1.594-1.594z"></path><path d="M20 2v4"></path><path d="M22 4h-4"></path><circle cx="4" cy="20" r="2"></circle></svg>
    </button>

    <!-- Agent 抽屉 -->
    <div
      class="agent-drawer"
      :class="{ open: isDrawerOpen, resizing: isResizing }"
      :style="{ '--agent-drawer-width': drawerWidth + 'px' }"
    >
      <div class="agent-drawer-resizer" @mousedown="startResize"></div>
      
      <div class="chat-header">
        <div class="chat-header-left">
          <div class="chat-header-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" style="width:16px;height:16px;"><path d="M11.017 2.814a1 1 0 0 1 1.966 0l1.051 5.558a2 2 0 0 0 1.594 1.594l5.558 1.051a1 1 0 0 1 0 1.966l-5.558 1.051a2 2 0 0 0-1.594 1.594l-1.051 5.558a1 1 0 0 1-1.966 0l-1.051-5.558a2 2 0 0 0-1.594-1.594l-5.558-1.051a1 1 0 0 1 0-1.966l5.558-1.051a2 2 0 0 0 1.594-1.594z"></path><path d="M20 2v4"></path><path d="M22 4h-4"></path><circle cx="4" cy="20" r="2"></circle></svg>
          </div>
          <strong style="font-size: 14px;">Contact Agent</strong>
        </div>
        <div class="chat-header-actions">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" @click="resetDrawerChat" title="清空对话"><path d="M2.5 2v6h6M21.5 22v-6h-6M22 11.5A10 10 0 0 0 3.2 7.2M2 12.5a10 10 0 0 0 18.8 4.2"/></svg>
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" @click="toggleAgentDrawer" title="关闭"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
        </div>
      </div>

      <div class="chat-messages-container" ref="chatContainerRef">
        <div
          v-for="msg in chatMessages"
          :key="msg.id"
          class="message-bubble-wrapper"
          :class="msg.sender"
        >
          <div class="bubble-content">
            <div class="bubble-text" v-html="msg.content"></div>
            
            <!-- AI 事项识别卡片 -->
            <div v-if="msg.structuredCard" class="structured-card">
              <div class="structured-card-title">识别结果</div>
              
              <div class="structured-row">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                <span class="structured-label">联系人：</span>
                <span class="structured-value">{{ msg.structuredCard.contactName }}</span>
              </div>
              
              <div class="structured-row">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect width="18" height="18" x="3" y="4" rx="2"/><path d="M3 10h18M8 2v4M16 2v4"/></svg>
                <span class="structured-label">时间：</span>
                <span class="structured-value">
                  {{ msg.structuredCard.timeStr }}
                </span>
              </div>
              
              <div class="structured-row">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/></svg>
                <span class="structured-label">内容：</span>
                <span class="structured-value">{{ msg.structuredCard.todoContent }}</span>
              </div>

              <!-- 卡片状态变更 -->
              <div v-if="!msg.structuredCard.status || msg.structuredCard.status === 'pending'" class="structured-card-actions">
                <button class="btn-confirm" @click="confirmDrawerExecution(msg)">确认创建</button>
                <button class="btn-modify" @click="modifyDrawerExecution(msg)">修改</button>
                <button class="btn-cancel" @click="cancelDrawerExecution(msg)">取消</button>
              </div>

              <div v-else-if="msg.structuredCard.status === 'confirmed'" style="margin-top: 12px;">
                <div style="color: var(--color-success); font-weight:600; font-size:12px; display:flex; align-items:center; gap:6px;">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="color: var(--color-success); width:14px;height:14px;"><circle cx="12" cy="12" r="10"/><polyline points="12 8 12 12 14 14"/></svg>
                  执行成功！
                </div>
                <p style="font-size:10px;color:var(--text-muted);margin-top:4px;">
                  事项已成功创建并绑定。<br>
                  流水号: <strong>{{ msg.structuredCard.opCode }}</strong>
                </p>
              </div>

              <div v-else-if="msg.structuredCard.status === 'modified'" style="margin-top: 12px;">
                <div style="color: var(--color-primary); font-weight:600; font-size:12px; display:flex; align-items:center; gap:6px;">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="color: var(--color-primary); width:14px;height:14px;"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4Z"/></svg>
                  正在编辑事项...
                </div>
                <p style="font-size:10px;color:var(--text-muted);margin-top:4px;">
                  请在下方输入栏中直接对识别出的信息进行修正或补充。
                </p>
              </div>

              <div v-else-if="msg.structuredCard.status === 'cancelled'" style="margin-top: 12px;">
                <div style="color: var(--text-muted); font-weight:600; font-size:12px; display:flex; align-items:center; gap:6px;">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="color: var(--text-muted); width:14px;height:14px;"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
                  操作已取消
                </div>
              </div>
            </div>

            <!-- 用户气泡的已读标示 -->
            <div v-if="msg.sender === 'user'" class="bubble-meta">
              <span class="bubble-time">{{ msg.time }}</span>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" class="read-icon" style="width:12px;height:12px;"><polyline points="20 6 9 17 4 12"/><polyline points="22 10 12 20 9 17"/></svg>
            </div>
          </div>
        </div>
      </div>

      <div class="chat-input-area">
        <div class="chat-input-card">
          <textarea
            v-model="drawerInputText"
            placeholder="告诉我你想做什么..."
            @keydown.enter.prevent="sendDrawerUserMessage"
          ></textarea>
          <div class="chat-input-card-bottom">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="clip-icon"><path d="m21.44 11.05-9.19 9.19a6 6 0 0 1-8.49-8.49l9.19-9.19a4 4 0 0 1 5.66 5.66l-9.2 9.19a2 2 0 0 1-2.83-2.83l8.49-8.48"/></svg>
            <button class="chat-send-btn-circle" @click="sendDrawerUserMessage">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
            </button>
          </div>
        </div>
      </div>
      <div class="chat-footer-note">内容由 AI 生成，请仔细核对</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, computed, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { gsap } from 'gsap'
import { getDashboardOverview, getTodoTrend, getContactGenderDistribution } from '@/api/dashboard'
import type { DashboardOverview, TodoTrendItem, ContactGenderDistributionItem } from '@/api/dashboard'
import { getContactsApi } from '@/api/contact'
import type { ContactInfo } from '@/api/contact'
import { getTodos, completeTodo } from '@/api/todo'
import type { TodoInfo } from '@/types/todo'

const router = useRouter()

// 1. 数据定义与初始化
const overview = ref<DashboardOverview>({
  contactCount: 0,
  blacklistCount: 0,
  pendingTodoCount: 0,
  todayTodoCount: 0,
  overdueTodoCount: 0
})

const todayTodos = ref<TodoInfo[]>([])
const recentContacts = ref<ContactInfo[]>([])

// 获取头像地址
function getAvatarUrl(url: string | null): string {
  if (!url) return 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&auto=format&fit=crop&q=80'
  if (url.startsWith('http')) return url
  return `http://localhost:8080${url}`
}

// 格式化今日待办的时间
function formatTodoTime(timeStr: string): string {
  if (!timeStr) return ''
  try {
    const parts = timeStr.split(' ')
    if (parts.length === 2 && parts[1]) {
      const timeParts = parts[1].split(':')
      return timeParts.slice(0, 2).join(':')
    }
  } catch (e) {
    console.error('Failed to format todo time:', e)
  }
  return timeStr
}

// 优先级翻译与样式类
function getPriorityName(p: number): string {
  return p === 2 ? '高' : p === 1 ? '中' : '低'
}

function getPriorityClass(p: number): string {
  return p === 2 ? 'badge-priority-emergency' : p === 1 ? 'badge-priority-important' : 'badge-priority-normal'
}

// 获取联系人绑定的第一个标签
function getContactFirstTag(contact: ContactInfo): string {
  if (contact.tags && contact.tags.length > 0 && contact.tags[0]) {
    return contact.tags[0]
  }
  const defaults = ['同学', '朋友', '实习', '客户']
  const idStr = contact.contactId || ''
  const index = Math.abs(parseInt(idStr.slice(-4)) || 0) % defaults.length
  return defaults[index] || '客户'
}

const getTagStyle = (tagName: string) => {
  if (tagName === '老师') return { backgroundColor: '#f0fdf4', color: '#15803d', borderColor: '#bbf7d0', border: '1px solid' }
  if (tagName === '实习') return { backgroundColor: '#fff7ed', color: '#c2410c', borderColor: '#fed7aa', border: '1px solid' }
  if (tagName === '同学') return { backgroundColor: '#f5f3ff', color: '#6d28d9', borderColor: '#ddd6fe', border: '1px solid' }
  return { backgroundColor: '#eff6ff', color: '#1d4ed8', borderColor: '#bfdbfe', border: '1px solid' }
}

// 路由跳转到联系人详情
function goToContactDetail(contactId: string) {
  router.push(`/contacts/${contactId}`)
}

// 2. 轮播组件控制
const activeSlide = ref<number>(0)
const carouselContainerRef = ref<HTMLDivElement | null>(null)
const carouselTrackRef = ref<HTMLDivElement | null>(null)
let carouselCtx: any = null

const totalSlides = computed(() => {
  return Math.ceil(recentContacts.value.length / 4) || 0
})

function getPageContacts(pageIndex: number) {
  const start = pageIndex * 4
  return recentContacts.value.slice(start, start + 4)
}

watch(recentContacts, () => {
  if (activeSlide.value >= totalSlides.value) {
    activeSlide.value = 0
  }
  nextTick(() => {
    if (carouselTrackRef.value) {
      // 锁定大轨道，不再平移它本身
      gsap.set(carouselTrackRef.value, { xPercent: 0 })
      const pages = carouselTrackRef.value.querySelectorAll('.carousel-page')
      pages.forEach((page, idx) => {
        if (idx === activeSlide.value) {
          gsap.set(page, { opacity: 1, visibility: 'visible', x: 0 })
        } else {
          gsap.set(page, { opacity: 0, visibility: 'hidden', x: 0 })
        }
      })
    }
  })
})

function slideCarousel(index: number) {
  if (index === activeSlide.value) return
  const prevIndex = activeSlide.value
  activeSlide.value = index

  if (carouselTrackRef.value) {
    const pages = carouselTrackRef.value.querySelectorAll('.carousel-page')
    const prevPage = pages[prevIndex]
    const nextPage = pages[index]

    // 方向判断：右翻则旧页左偏新页右入，左翻则旧页右偏新页左入
    const isNext = index > prevIndex
    const drift = 30 // 30px 微位移，呼吸感强烈且不杂乱
    const prevTargetX = isNext ? -drift : drift
    const nextStartX = isNext ? drift : -drift

    // 1. 上一页缓慢淡出并轻拂偏移 (1.0s)
    if (prevPage) {
      gsap.to(prevPage, {
        duration: 1.0,
        opacity: 0,
        x: prevTargetX,
        ease: "power2.out",
        overwrite: "auto",
        onComplete: () => {
          gsap.set(prevPage, { visibility: 'hidden' })
        }
      })
    }

    // 2. 新一页激活，并从 0 缓慢淡入与归位滑入 (1.2s，更柔和沉稳)
    if (nextPage) {
      gsap.fromTo(nextPage,
        { 
          opacity: 0, 
          x: nextStartX,
          visibility: 'visible'
        },
        {
          duration: 1.2,
          opacity: 1,
          x: 0,
          ease: "power2.out",
          overwrite: "auto"
        }
      )
    }
  }
}

// 3. 事项勾选切换状态
async function toggleTodoStatus(todo: TodoInfo) {
  if (todo.status === 2) return // 已经完成不再重复标记
  try {
    await completeTodo(todo.matterId)
    ElMessage.success('已标记事项为完成')
    await fetchOverview()
    todo.status = 2
  } catch (e) {
    console.error('Failed to complete todo:', e)
  }
}

// 4. 数据拉取
async function fetchOverview() {
  try {
    overview.value = await getDashboardOverview()
  } catch (e) {
    console.error('Failed to fetch overview:', e)
  }
}

async function fetchTodayTodos() {
  try {
    const now = new Date()
    const year = now.getFullYear()
    const month = String(now.getMonth() + 1).padStart(2, '0')
    const day = String(now.getDate()).padStart(2, '0')
    const today = `${year}-${month}-${day}`
    const startTime = `${today} 00:00:00`
    const endTime = `${today} 23:59:59`

    const res = await getTodos({
      startTime,
      endTime,
      page: 1,
      pageSize: 4,
      sortBy: 'todoTime',
      sortOrder: 'asc'
    })
    todayTodos.value = res.list
  } catch (e) {
    console.error('Failed to fetch today todos:', e)
  }
}

async function fetchRecentContacts() {
  try {
    const res = await getContactsApi({
      page: 1,
      pageSize: 16,
      sortBy: 'createdAt',
      sortOrder: 'desc'
    })
    recentContacts.value = res.list
  } catch (e) {
    console.error('Failed to fetch recent contacts:', e)
  }
}

// 5. ECharts 初始化
const trendChartRef = ref<HTMLDivElement | null>(null)
const genderChartRef = ref<HTMLDivElement | null>(null)
let trendChartInstance: echarts.ECharts | null = null
let genderChartInstance: echarts.ECharts | null = null

function handleResize() {
  trendChartInstance?.resize()
  genderChartInstance?.resize()
}

async function initTrendChart() {
  if (!trendChartRef.value) return
  try {
    const data: TodoTrendItem[] = await getTodoTrend(7)
    const xData: string[] = []
    const daysName = ['日', '一', '二', '三', '四', '五', '六']
    const counts = data.map(item => item.count)

    for (let i = 0; i < 7; i++) {
      const d = new Date()
      d.setDate(d.getDate() + i)
      const dateStr = (d.getMonth() + 1) + '/' + d.getDate()
      const dayStr = daysName[d.getDay()]
      xData.push(`${dateStr}\n(${dayStr})`)
    }

    if (!trendChartInstance) {
      trendChartInstance = echarts.init(trendChartRef.value)
    }

    const option = {
      grid: {
        top: '16%',
        left: '4%',
        right: '3%',
        bottom: '12%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: xData,
        axisLine: { show: false },
        axisTick: { show: false },
        axisLabel: { color: '#9ca3af', fontSize: 11 }
      },
      yAxis: {
        type: 'value',
        minInterval: 1,
        splitLine: {
          lineStyle: { type: 'dashed', color: '#e8edf5' }
        },
        axisLine: { show: false },
        axisTick: { show: false },
        axisLabel: { color: '#9ca3af', fontSize: 11 }
      },
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#e2e8f0',
        borderWidth: 1,
        textStyle: { color: '#334155', fontSize: 12 }
      },
      series: [
        {
          data: counts,
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          itemStyle: { color: '#2563eb' },
          lineStyle: { width: 3.5 },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(37, 99, 235, 0.16)' },
              { offset: 1, color: 'rgba(37, 99, 235, 0.0)' }
            ])
          },
          label: {
            show: true,
            position: 'top',
            color: '#1f2937',
            fontWeight: 'bold',
            fontSize: 12
          }
        }
      ]
    }
    trendChartInstance.setOption(option)
  } catch (e) {
    console.error('Failed to init trend chart:', e)
  }
}

async function initGenderChart() {
  if (!genderChartRef.value) return
  try {
    const data: ContactGenderDistributionItem[] = await getContactGenderDistribution()
    
    const chartData = data.map(item => {
      let color = '#9ca3af'
      if (item.gender === 1) color = '#3b82f6'
      if (item.gender === 2) color = '#10b981'
      return {
        value: item.count,
        name: item.name,
        itemStyle: { color }
      }
    })

    const totalCount = data.reduce((sum, item) => sum + item.count, 0)

    if (!genderChartInstance) {
      genderChartInstance = echarts.init(genderChartRef.value)
    }

    const option = {
      tooltip: {
        trigger: 'item',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#e2e8f0',
        borderWidth: 1,
        formatter: '{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        right: '4%',
        top: 'center',
        icon: 'circle',
        itemWidth: 10,
        itemHeight: 10,
        itemGap: 18,
        textStyle: { fontSize: 12, color: '#6b7280' },
        formatter: (name: string) => {
          const item = chartData.find(d => d.name === name)
          const val = item ? item.value : 0
          const percent = totalCount > 0 ? ((val / totalCount) * 100).toFixed(1) : '0.0'
          return `${name.padEnd(2, ' ')}  ${val} (${percent}%)`
        }
      },
      series: [
        {
          name: '性别分布',
          type: 'pie',
          radius: ['62%', '84%'],
          center: ['31%', '52%'],
          avoidLabelOverlap: false,
          label: {
            show: true,
            position: 'center',
            formatter: `{total|${totalCount}}\n{sub|总人数}`,
            rich: {
              total: {
                fontSize: 24,
                fontWeight: 'bold',
                lineHeight: 34,
                color: '#1f2937'
              },
              sub: {
                fontSize: 12,
                color: '#9ca3af'
              }
            }
          },
          emphasis: {
            label: { show: true }
          },
          labelLine: { show: false },
          data: chartData
        }
      ]
    }
    genderChartInstance.setOption(option)
  } catch (e) {
    console.error('Failed to init gender chart:', e)
  }
}

// 6. AI Agent 抽屉交互逻辑
interface ChatMessage {
  id: string
  sender: 'user' | 'assistant'
  content: string
  time: string
  structuredCard?: {
    contactName: string
    timeStr: string
    todoContent: string
    status?: 'pending' | 'confirmed' | 'modified' | 'cancelled'
    opCode?: string
  }
}

const isDrawerOpen = ref<boolean>(false)
const isResizing = ref<boolean>(false)
const drawerWidth = ref<number>(360)
const drawerInputText = ref<string>('')
const chatContainerRef = ref<HTMLDivElement | null>(null)

const chatMessages = ref<ChatMessage[]>([
  {
    id: 'msg-init',
    sender: 'assistant',
    content: '👋 你好，我是你的智能联系人助手。你可以告诉我你想创建的事项，我会帮你识别并创建。',
    time: ''
  }
])

const MIN_AGENT_DRAWER_WIDTH = 320
const MAX_AGENT_DRAWER_WIDTH = 620

const startResize = (event: MouseEvent) => {
  event.preventDefault()
  isResizing.value = true
  document.body.classList.add('drawer-resizing')

  const handleMouseMove = (e: MouseEvent) => {
    if (!isResizing.value) return
    const nextWidth = window.innerWidth - e.clientX
    const maxWidth = Math.min(MAX_AGENT_DRAWER_WIDTH, Math.max(380, window.innerWidth - 320))
    drawerWidth.value = Math.min(maxWidth, Math.max(MIN_AGENT_DRAWER_WIDTH, nextWidth))
    handleResize()
  }

  const handleMouseUp = () => {
    isResizing.value = false
    document.body.classList.remove('drawer-resizing')
    window.removeEventListener('mousemove', handleMouseMove)
    window.removeEventListener('mouseup', handleMouseUp)
    handleResize()
  }

  window.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('mouseup', handleMouseUp)
}

function toggleAgentDrawer() {
  isDrawerOpen.value = !isDrawerOpen.value
  setTimeout(() => {
    handleResize()
  }, 300)
}

function sendDrawerUserMessage() {
  const text = drawerInputText.value.trim()
  if (!text) return
  drawerInputText.value = ''

  const now = new Date()
  const timeStr = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`

  chatMessages.value.push({
    id: `msg-u-${Date.now()}`,
    sender: 'user',
    content: text,
    time: timeStr
  })

  scrollToBottom()

  setTimeout(() => {
    let replyContent = ''
    let structuredCard = undefined

    if (text.includes('张雨薇')) {
      replyContent = '好的，已为您识别到以下信息：'
      structuredCard = {
        contactName: '张雨薇',
        timeStr: '2026-06-17 09:00',
        todoContent: '联系张雨薇确认合作事宜',
        status: 'pending' as const
      }
    } else {
      replyContent = '收到！我是一个静态演示的 Contact Agent 抽屉助手，您可以发送包含“张雨薇”的内容来体验写操作确认卡片交互。'
    }

    chatMessages.value.push({
      id: `msg-ai-${Date.now()}`,
      sender: 'assistant',
      content: replyContent,
      time: timeStr,
      structuredCard
    })

    scrollToBottom()
  }, 1000)
}

function confirmDrawerExecution(msg: ChatMessage) {
  if (msg.structuredCard) {
    msg.structuredCard.status = 'confirmed'
    msg.structuredCard.opCode = `OP-D-${Date.now().toString().slice(-6)}`
  }
}

function modifyDrawerExecution(msg: ChatMessage) {
  if (msg.structuredCard) {
    msg.structuredCard.status = 'modified'
  }
}

function cancelDrawerExecution(msg: ChatMessage) {
  if (msg.structuredCard) {
    msg.structuredCard.status = 'cancelled'
  }
}

function resetDrawerChat() {
  chatMessages.value = [
    {
      id: `msg-init-reset-${Date.now()}`,
      sender: 'assistant',
      content: '👋 对话已重置。你好，我是你的智能联系人助手。你可以告诉我你想创建的事项，我会帮你识别并创建。',
      time: ''
    }
  ]
}

function scrollToBottom() {
  nextTick(() => {
    if (chatContainerRef.value) {
      chatContainerRef.value.scrollTop = chatContainerRef.value.scrollHeight
    }
  })
}

watch(isDrawerOpen, (open) => {
  if (open) {
    scrollToBottom()
  }
})

onMounted(async () => {
  await Promise.all([
    fetchOverview(),
    fetchTodayTodos(),
    fetchRecentContacts()
  ])

  await initTrendChart()
  await initGenderChart()

  window.addEventListener('resize', handleResize)

  if (carouselContainerRef.value) {
    carouselCtx = gsap.context(() => {}, carouselContainerRef.value)
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  trendChartInstance?.dispose()
  genderChartInstance?.dispose()
  carouselCtx?.revert()
})
</script>

<style scoped>
.dashboard-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
}

/* 指标卡网格（缩减至4列） */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 580px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}

.stat-card {
  min-height: 104px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 14px;
  border-radius: 18px;
  border: 1px solid rgba(226, 232, 240, 0.9);
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(12px);
  box-shadow: 0 14px 34px -28px rgba(15, 23, 42, 0.42), 0 2px 8px rgba(15, 23, 42, 0.03);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
  border-color: rgba(37, 99, 235, 0.15);
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon.primary {
  background: linear-gradient(145deg, #eef4ff 0%, #e1ebff 100%);
  color: var(--color-primary);
}

.stat-icon.success {
  background: linear-gradient(145deg, #eafbf0 0%, #d6f5df 100%);
  color: var(--color-success);
}

.stat-icon.danger {
  background: linear-gradient(145deg, #fff1eb 0%, #ffe4da 100%);
  color: #f97316;
}

.stat-icon.warning {
  background: linear-gradient(145deg, #f5ecff 0%, #ede1ff 100%);
  color: #7c3aed;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-label {
  font-size: 12px;
  color: #4b5563;
  font-weight: 600;
}

.stat-value {
  font-size: 30px;
  font-weight: 700;
  line-height: 1;
  letter-spacing: -0.04em;
  color: #111827;
  margin: 0;
}

.stat-change {
  font-size: 10px;
  color: #6b7280;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 2px;
}

.stat-change .change-value {
  font-size: 11px;
  font-weight: 700;
}

.stat-change.up .change-value,
.stat-change.up svg {
  color: var(--color-success);
}

.stat-change.down.success-color .change-value,
.stat-change.down.success-color svg {
  color: var(--color-success) !important;
}

.stat-change.down.success-color {
  color: var(--color-success) !important;
}

/* 两栏布局 */
.dashboard-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(360px, 1fr);
  gap: 12px;
}

@media (max-width: 1024px) {
  .dashboard-layout {
    grid-template-columns: 1fr;
  }
}

.panel-card {
  display: flex;
  flex-direction: column;
  min-height: 222px;
}

.contacts-panel,
.todos-panel {
  padding: 14px 14px 12px;
  border-radius: 18px;
}

.contacts-panel {
  min-height: 204px;
  padding-bottom: 6px;
}

.card-header {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: none;
  padding: 0;
}

.card-title {
  font-size: 14px;
  font-weight: 700;
  color: #111827;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-title svg {
  color: #334155;
}

.view-all-link {
  display: inline-flex;
  align-items: center;
  font-size: 12px;
  font-weight: 600;
  color: var(--color-primary);
  text-decoration: none;
}

.view-all-link:hover {
  color: var(--color-primary-hover);
}

/* 重要联系人卡片 */
.carousel-container {
  position: relative;
  overflow: hidden;
  margin-top: 4px;
  flex: 1;
  height: 134px; /* 固定高度，防止绝对定位重叠时塌陷 */
}

.carousel-track {
  position: relative;
  width: 100%;
  height: 100%;
}

.carousel-page {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
  opacity: 0;
  visibility: hidden;
  pointer-events: none; /* 屏蔽背景页面的操作 */
}

.carousel-page.active {
  opacity: 1;
  visibility: visible;
  pointer-events: auto; /* 允许点击激活页面的卡片 */
}

@media (max-width: 640px) {
  .carousel-container {
    height: 278px; /* 2行卡片自适应高度 */
  }
  .carousel-page {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

.contact-card {
  min-width: 0;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 6px 6px;
  border-radius: 16px;
  border: 1px solid rgba(226, 232, 240, 0.95);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(248, 250, 252, 0.9) 100%);
  transition: transform 0.2s, box-shadow 0.2s, border-color 0.2s;
  cursor: pointer;
}

.contact-card:hover {
  transform: translateY(-2px);
  border-color: rgba(191, 219, 254, 1);
  box-shadow: 0 12px 28px -22px rgba(37, 99, 235, 0.4);
}

.card-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 8px;
  border: 3px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 10px 24px -18px rgba(15, 23, 42, 0.7);
}

.contact-card .badge {
  margin-bottom: 6px;
  padding: 3px 7px;
  border-radius: 10px;
  font-size: 10px;
  font-weight: 700;
}

.card-name {
  font-size: 11px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 2px;
}

.card-company {
  min-height: 18px;
  font-size: 9px;
  color: #6b7280;
  line-height: 1.1;
  margin-bottom: 8px;
  width: 90%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-actions {
  display: flex;
  gap: 10px;
  width: calc(100% + 12px);
  margin: auto -6px -6px;
  padding: 6px 0 0;
  border-top: 1px solid rgba(226, 232, 240, 0.8);
  justify-content: center;
}

.contact-action-btn {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  border: 1px solid rgba(203, 213, 225, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
  background-color: #fff;
  transition: all 0.2s;
}

.contact-action-btn:hover {
  color: var(--color-primary);
  border-color: rgba(191, 219, 254, 1);
  background-color: rgba(239, 246, 255, 0.9);
}

.carousel-indicators {
  display: flex;
  justify-content: center;
  gap: 7px;
  margin-top: 8px;
}

.indicator-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background-color: #d7dce5;
  cursor: pointer;
  transition: all 0.2s;
}

.indicator-dot.active {
  width: 16px;
  background-color: var(--color-primary);
  border-radius: 99px;
}

/* 今日事项 (Todo) */
.todo-list-modern {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.todo-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 13px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(226, 232, 240, 0.95);
  transition: transform 0.2s, border-color 0.2s, box-shadow 0.2s;
  cursor: pointer;
}

.todo-item:hover {
  transform: translateY(-1px);
  border-color: rgba(191, 219, 254, 1);
  box-shadow: 0 10px 24px -22px rgba(37, 99, 235, 0.45);
}

.todo-item-left {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  min-width: 0;
  flex: 1;
}

.todo-checkbox {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  flex-shrink: 0;
  margin-top: 1px;
}

.todo-checkbox svg {
  width: 18px;
  height: 18px;
}

.todo-checkbox .unchecked-icon {
  color: #b0b8c5;
}

.todo-checkbox .checked-icon {
  display: none;
  color: var(--color-success);
}

.todo-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
  flex: 1;
}

.todo-title {
  font-size: 12.5px;
  font-weight: 600;
  color: #1f2937;
  line-height: 1.25;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: all 0.2s;
}

.todo-time {
  display: flex;
  align-items: center;
  font-size: 10px;
  color: #6b7280;
}

.contact-tag-inline {
  background: var(--color-neutral-bg);
  color: var(--color-neutral-text);
  padding: 1px 5px;
  border-radius: 4px;
  margin-left: 6px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}

.contact-tag-inline:hover {
  background: var(--color-primary-light);
  color: var(--color-primary);
}

.todo-item-right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.todo-item-right .badge {
  min-width: 28px;
  justify-content: center;
  padding: 3px 7px;
  border-radius: 999px;
  font-size: 9px;
  font-weight: 700;
}

.badge-priority-emergency {
  background-color: #fff1f2;
  color: #e11d48;
  border: 1px solid #fecdd3;
}

.badge-priority-important {
  background-color: #fffbeb;
  color: #d97706;
  border: 1px solid #fde68a;
}

.badge-priority-normal {
  background-color: #f0fdf4;
  color: #15803d;
  border: 1px solid #bbf7d0;
}

.todo-status {
  min-width: 42px;
  text-align: right;
  font-size: 10px;
  font-weight: 700;
}

.todo-status.pending {
  color: var(--color-primary);
}

.todo-status.completed-text {
  color: var(--color-success);
  display: none;
}

.todo-item.completed {
  opacity: 0.7;
  background: rgba(248, 250, 252, 0.92);
}

.todo-item.completed .todo-title {
  color: #94a3b8;
  text-decoration: line-through;
}

.todo-item.completed .unchecked-icon {
  display: none;
}

.todo-item.completed .checked-icon {
  display: block;
}

.todo-item.completed .pending {
  display: none;
}

.todo-item.completed .completed-text {
  display: inline;
}

/* 底部图表 */
.charts-layout {
  display: grid;
  grid-template-columns: minmax(320px, 1fr) minmax(0, 1.75fr);
  gap: 12px;
}

@media (max-width: 1024px) {
  .charts-layout {
    grid-template-columns: 1fr;
  }
}

.chart-card {
  min-height: 226px;
  display: flex;
  flex-direction: column;
}

#genderChart,
#trendChart {
  height: 176px !important;
}

.action-link {
  font-size: 12px;
  font-weight: 600;
  color: var(--color-primary);
  text-decoration: none;
}

.card-actions-inline {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-muted);
}

/* Agent 抽屉相关样式 */
.agent-drawer {
  position: fixed;
  top: 0;
  right: 0;
  width: var(--agent-drawer-width, 360px);
  height: 100vh;
  background-color: var(--bg-card);
  border-left: 1px solid var(--border-color);
  box-shadow: -4px 0 24px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  transform: translateX(100%);
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1000;
}

.agent-drawer.open {
  transform: translateX(0);
}

.agent-drawer.resizing {
  transition: none;
}

.agent-drawer-resizer {
  position: absolute;
  top: 0;
  left: 0;
  width: 12px;
  height: 100%;
  transform: translateX(-50%);
  cursor: col-resize;
  z-index: 1001;
  display: flex;
  align-items: center;
  justify-content: center;
}

.agent-drawer-resizer::before {
  content: "";
  width: 4px;
  height: 72px;
  border-radius: 999px;
  background: rgba(148, 163, 184, 0.2);
  transition: background-color 0.2s, opacity 0.2s;
  opacity: 0;
}

.agent-drawer:hover .agent-drawer-resizer::before,
.agent-drawer.resizing .agent-drawer-resizer::before,
.agent-drawer-resizer:hover::before {
  opacity: 1;
  background: rgba(59, 130, 246, 0.45);
}

.floating-agent-btn {
  position: fixed;
  width: 58px;
  height: 58px;
  bottom: 26px;
  right: 26px;
  background: linear-gradient(135deg, #3f73ff 0%, #2f5ef7 100%);
  box-shadow: 0 16px 32px -18px rgba(37, 99, 235, 0.65);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  cursor: pointer;
  transition: all 0.3s;
  z-index: 999;
  border: none;
}

.floating-agent-btn:hover {
  transform: translateY(-2px) scale(1.02);
}

.floating-agent-btn.hidden {
  opacity: 0;
  pointer-events: none;
  transform: scale(0.8) translateY(20px);
}

/* 抽屉头部 */
.chat-header {
  padding: 16px;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--bg-card);
}

.chat-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.chat-header-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  background: #eff6ff;
  color: #2563eb;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--text-muted);
}

.chat-header-actions svg {
  width: 16px;
  height: 16px;
  cursor: pointer;
  transition: color 0.2s;
}

.chat-header-actions svg:hover {
  color: var(--text-main);
}

/* 消息区域 */
.chat-messages-container {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background-color: var(--bg-body);
}

.message-bubble-wrapper {
  display: flex;
  flex-direction: column;
  max-width: 85%;
}

.message-bubble-wrapper.user {
  align-self: flex-end;
  align-items: flex-end;
}

.message-bubble-wrapper.assistant {
  align-self: flex-start;
  align-items: flex-start;
}

.bubble-content {
  padding: 12px 14px;
  font-size: 12.5px;
  line-height: 1.5;
  border-radius: 16px;
  position: relative;
}

.message-bubble-wrapper.assistant .bubble-content {
  background-color: #f0f2fe;
  color: #334155;
  border-bottom-left-radius: 4px;
}

.message-bubble-wrapper.user .bubble-content {
  background-color: #eff6ff;
  color: #1e293b;
  border-bottom-right-radius: 4px;
}

.bubble-meta {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 4px;
  font-size: 10px;
  color: #8b9bb4;
  margin-top: 6px;
  margin-bottom: -4px;
  margin-right: -2px;
}

.bubble-time {
  font-size: 10px;
}

.read-icon {
  width: 12px;
  height: 12px;
  color: #3b82f6;
}

/* 结构化确认卡片 */
.structured-card {
  border: 1px solid var(--border-color);
  border-radius: 12px;
  background-color: #fff;
  padding: 16px;
  margin-top: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  display: flex;
  flex-direction: column;
  width: 250px;
}

.structured-card-title {
  font-size: 13px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
}

.structured-row {
  display: flex;
  align-items: flex-start;
  padding: 8px 0;
  border-bottom: 1px solid #f1f5f9;
  width: 100%;
}

.structured-row:last-of-type {
  border-bottom: none;
}

.structured-row svg {
  color: #3b82f6;
  margin-right: 8px;
  margin-top: 2px;
  width: 14px;
  height: 14px;
  flex-shrink: 0;
}

.structured-label {
  width: 55px;
  color: #64748b;
  font-size: 12px;
  font-weight: 500;
  flex-shrink: 0;
}

.structured-value {
  color: #334155;
  font-size: 12px;
  font-weight: 600;
  flex: 1;
}

.structured-card-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.structured-card-actions button {
  flex: 1;
  padding: 6px 0;
  font-size: 11px;
  font-weight: 600;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.btn-confirm {
  background-color: #2563eb;
  color: #ffffff;
}

.btn-confirm:hover {
  background-color: #1d4ed8;
}

.btn-modify {
  background-color: #ffffff;
  border-color: #e2e8f0 !important;
  color: #4b5563;
}

.btn-modify:hover {
  background-color: #f8fafc;
  color: #1f2937;
}

.btn-cancel {
  background-color: #fff1f2;
  color: #e11d48;
}

.btn-cancel:hover {
  background-color: #ffe4e6;
}

/* 输入区域 */
.chat-input-area {
  padding: 16px;
  border-top: 1px solid var(--border-color);
  background: var(--bg-card);
}

.chat-input-card {
  border: 1px solid var(--border-color);
  border-radius: 12px;
  background-color: #fff;
  padding: 8px 12px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
}

.chat-input-card textarea {
  border: none;
  resize: none;
  height: 48px;
  font-size: 12px;
  font-family: inherit;
  color: var(--text-main);
  outline: none;
  background: transparent;
}

.chat-input-card textarea::placeholder {
  color: #94a3b8;
}

.chat-input-card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 4px;
}

.clip-icon {
  color: #94a3b8;
  cursor: pointer;
  width: 16px;
  height: 16px;
}

.chat-send-btn-circle {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #2563eb;
  color: #fff;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.2s;
}

.chat-send-btn-circle:hover {
  background: #1d4ed8;
}

.chat-send-btn-circle svg {
  width: 14px;
  height: 14px;
}

.chat-footer-note {
  font-size: 9.5px;
  color: #94a3b8;
  text-align: center;
  padding-bottom: 12px;
  background: var(--bg-card);
}
</style>
