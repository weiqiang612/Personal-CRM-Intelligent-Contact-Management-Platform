<template>
  <div class="dashboard-container">
    <!-- 四个摘要卡 -->
    <section class="stats-grid">
      <!-- 联系人总数 -->
      <div class="card stat-card stat-primary">
        <div class="stat-icon primary">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:22px;height:22px;"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
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
      <div class="card stat-card stat-success">
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
      <div class="card stat-card stat-danger">
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
      <div class="card stat-card stat-warning">
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
      <!-- 左侧：最近联系人 -->
      <div class="card panel-card contacts-panel">
        <div class="card-header">
          <h3 class="card-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:17px;height:17px;"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
            最近联系人
          </h3>
          <router-link to="/contacts" class="view-all-link">查看全部</router-link>
        </div>
        
        <div class="carousel-container" ref="carouselContainerRef">
          <div v-if="recentContacts.length === 0" class="empty-state">
            <svg class="empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M8 14s1.5 2 4 2 4-2 4-2M9 9h.01M15 9h.01"/></svg>
            <p>暂无最近联系人，请先去联系人中心添加！</p>
          </div>
          <div v-else class="carousel-track" ref="carouselTrackRef">
            <div
              v-for="pageIndex in totalSlides"
              :key="pageIndex"
              class="carousel-page"
              :class="{ 
                active: pageIndex - 1 === activeSlide,
                'is-static': pageIndex === 1
              }"
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
                  <a :href="`mailto:${contact.email || ''}`" class="contact-action-btn" @click.stop="handleEmailClickOnlyCopy(contact.email)" v-if="contact.email">
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
            v-for="todo in displayTodayTodos.slice(0, 4)"
            :key="todo.matterId"
            class="todo-item"
            :class="{ 
              completed: todo.status === 2,
              cancelled: todo.status === 1
            }"
          >
            <div class="todo-item-left">
              <button type="button" class="todo-checkbox todo-action-icon" @click="markTodoCompleted(todo)" :disabled="todo.status !== 0">
                <!-- unchecked-icon -->
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="unchecked-icon"><circle cx="12" cy="12" r="10"/></svg>
                <!-- checked-icon -->
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="checked-icon"><circle cx="12" cy="12" r="10"/><path d="m9 12 2 2 4-4"/></svg>
              </button>
              <div class="todo-details" @click="goToTodoItem(todo)" style="cursor: pointer;">
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
            <div class="todo-item-right todo-actions">
              <span class="badge" :class="getPriorityClass(todo.priority)" v-if="todo.status === 0">
                {{ getPriorityName(todo.priority) }}
              </span>
              <template v-if="todo.status === 0">
                <button type="button" class="todo-inline-btn complete" @click="markTodoCompleted(todo)">完成</button>
                <button type="button" class="todo-inline-btn cancel" @click="markTodoCancelled(todo)">取消</button>
              </template>
              <span class="badge badge-priority-normal" v-else-if="todo.status === 2" style="background-color: #f0fdf4; color: #15803d; border-color: #bbf7d0;">已完成</span>
              <span class="badge badge-priority-emergency" v-else style="background-color: #f1f5f9; color: #64748b; border-color: #cbd5e1;">已取消</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 底部图表区 -->
    <section class="charts-layout">
      <!-- 关系维护状态环形图 -->
      <div class="card chart-card">
        <div class="card-header">
          <h3 class="card-title">关系维护状态</h3>
          <div class="card-actions-inline">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:14px;height:14px;cursor:pointer;"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>
            <router-link to="/contacts" class="action-link">查看详情</router-link>
          </div>
        </div>
        <div ref="healthChartRef" id="healthChart" style="width: 100%; min-height: 176px; height: 176px;"></div>
      </div>

      <!-- 未来 7 天事项 Entry Overview 卡片 (Apple 终极定稿版) -->
      <div class="card chart-card timeline-overview-card">
        <div class="card-header">
          <div class="header-title-group">
            <h3 class="card-title">未来 7 天事项</h3>
          </div>
          <div class="card-actions-inline">
            <span class="timeline-total-badge">{{ totalFutureTodos }} 待办</span>
          </div>
        </div>
        
        <!-- 顶部：7 天底线高亮极简导航条 -->
        <div class="timeline-segmented-bar line-bar">
          <button 
            v-for="(day, index) in microScheduleDays" 
            :key="day.fullDate"
            class="timeline-segment-item line-item"
            :class="{ 
              'is-active': activeDayIndex === index, 
              'is-today': index === 0,
              'has-indicator': day.count > 0 
            }"
            @click="selectTimelineDay(index)"
          >
            <span class="segment-indicator" v-if="day.count > 0"></span>
            <span class="segment-day">{{ day.dayName }}</span>
            <span class="segment-date">{{ day.dateStr }}</span>
          </button>
        </div>

        <div class="overview-divider"></div>

        <!-- 底部：Apple 风格精美事项 Card 视窗 (带切换平滑动效) -->
        <Transition name="fade-slide" mode="out-in">
          <div class="overview-focus-section" v-if="currentActiveDay" :key="activeDayIndex">
            <div class="focus-section-header">
              <span class="focus-title">{{ currentActiveDay.dayName }} · {{ currentActiveDay.count }} 项待办</span>
            </div>

            <div class="focus-section-body">
              <div v-if="currentActiveDay.loadingTodos" class="focus-loading-row">加载中...</div>
              <div v-else-if="!currentActiveDay.todos || currentActiveDay.todos.length === 0" class="focus-empty-row">
                该日暂无待办事项，轻松一下吧☕
              </div>
              <div v-else class="summary-cards-wrapper">
                <div 
                  v-for="todo in (currentActiveDay.todos || []).slice(0, 2)" 
                  :key="todo.matterId" 
                  class="summary-card-item"
                  @click="goToTodoItem(todo, currentActiveDay.fullDate)"
                >
                  <div class="card-item-top">
                    <span class="priority-mini-dot" :class="getPriorityClass(todo.priority)"></span>
                    <span class="card-item-title">{{ todo.content }}</span>
                  </div>
                  <div class="card-item-bottom">
                    <span class="card-item-time">{{ formatTodoTime(todo.todoTime) || '全天' }}</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="focus-section-footer">
              <router-link :to="{ path: '/todos', query: { date: currentActiveDay.fullDate } }" class="entry-more-link">
                <span>查看全部</span>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="link-arrow"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
              </router-link>
            </div>
          </div>
        </Transition>
      </div>
    </section>

    <!-- 悬浮 Contact Agent 按钮 -->
    <button
      class="floating-agent-btn"
      :class="{ hidden: isDrawerOpen || isExternalSheetOpen }"
      @click="toggleAgentDrawer"
      @mousedown="handleDragStart"
      @touchstart="handleDragStart"
      title="智能助手"
    >
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" style="width:22px;height:22px;"><path d="M11.017 2.814a1 1 0 0 1 1.966 0l1.051 5.558a2 2 0 0 0 1.594 1.594l5.558 1.051a1 1 0 0 1 0 1.966l-5.558 1.051a2 2 0 0 0-1.594 1.594l-1.051 5.558a1 1 0 0 1-1.966 0l-1.051-5.558a2 2 0 0 0-1.594-1.594l-5.558-1.051a1 1 0 0 1 0-1.966l5.558-1.051a2 2 0 0 0 1.594-1.594z"></path><path d="M20 2v4"></path><path d="M22 4h-4"></path><circle cx="4" cy="20" r="2"></circle></svg>
    </button>

    <!-- PC 端 Agent 抽屉 -->
    <div
      v-if="!isMobile"
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
            <div class="bubble-text" style="white-space: pre-wrap;">{{ msg.content }}</div>
            
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
                <span class="structured-value">{{ msg.structuredCard.timeStr }}</span>
              </div>
              <div class="structured-row">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/></svg>
                <span class="structured-label">内容：</span>
                <span class="structured-value">{{ msg.structuredCard.todoContent }}</span>
              </div>
              <div v-if="!msg.structuredCard.status || msg.structuredCard.status === 'pending'" class="structured-card-actions">
                <button class="btn-confirm" @click="confirmDrawerExecution(msg)">确认创建</button>
                <button class="btn-cancel" @click="cancelDrawerExecution(msg)">取消</button>
              </div>
              <div v-else-if="msg.structuredCard.status === 'confirmed'" style="margin-top: 12px;">
                <div style="color: var(--color-success); font-weight:600; font-size:12px; display:flex; align-items:center; gap:6px;">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="width:14px;height:14px;"><circle cx="12" cy="12" r="10"/><polyline points="12 8 12 12 14 14"/></svg>
                  执行成功！
                </div>
              </div>
              <div v-else-if="msg.structuredCard.status === 'cancelled'" style="margin-top: 12px;">
                <div style="color: var(--text-muted); font-weight:600; font-size:12px; display:flex; align-items:center; gap:6px;">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="width:14px;height:14px;"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
                  操作已取消
                </div>
              </div>
            </div>

            <!-- 真实 API 写操作确认卡片 -->
            <div v-if="msg.isConfirmCard" class="structured-card">
              <div class="structured-card-title">待办事项预确认</div>
              <div v-if="msg.parsedParams?.contactName" class="structured-row">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                <span class="structured-label">联系人：</span>
                <span class="structured-value">{{ msg.parsedParams.contactName }}</span>
              </div>
              <div v-if="msg.parsedParams?.todoTime" class="structured-row">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect width="18" height="18" x="3" y="4" rx="2"/><path d="M3 10h18M8 2v4M16 2v4"/></svg>
                <span class="structured-label">时间：</span>
                <span class="structured-value">{{ msg.parsedParams.todoTime }}</span>
              </div>
              <div v-if="msg.parsedParams?.content" class="structured-row">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/></svg>
                <span class="structured-label">内容：</span>
                <span class="structured-value">{{ msg.parsedParams.content }}</span>
              </div>
              <div v-if="msg.confirmState === 'pending'" class="structured-card-actions">
                <button class="btn-confirm" @click="confirmDrawerExecution(msg)">确认创建</button>
                <button class="btn-cancel" @click="cancelDrawerExecution(msg)">取消</button>
              </div>
              <div v-else-if="msg.confirmState === 'confirmed'" style="margin-top:12px;">
                <div style="color:var(--color-success);font-weight:600;font-size:12px;display:flex;align-items:center;gap:6px;">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="width:14px;height:14px;"><circle cx="12" cy="12" r="10"/><polyline points="9 12 11 14 15 10"/></svg>
                  已创建
                </div>
              </div>
              <div v-else-if="msg.confirmState === 'cancelled'" style="margin-top:12px;">
                <div style="color:var(--text-muted);font-weight:600;font-size:12px;display:flex;align-items:center;gap:6px;">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="width:14px;height:14px;"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
                  已取消
                </div>
              </div>
            </div>

            <!-- 联系人查询结果卡片 -->
            <div v-if="msg.queryType === 'contact' && msg.results && msg.results.length > 0" class="contact-results-list">
              <div
                v-for="contact in msg.results"
                :key="contact.contactId"
                class="drawer-contact-card"
                @click="goToContactDetail(contact.contactId)"
              >
                <img :src="getAvatarUrl(contact.avatarUrl)" :alt="contact.name" class="drawer-contact-avatar" />
                <div class="drawer-contact-info">
                  <div class="drawer-contact-name">{{ contact.name }}</div>
                  <div class="drawer-contact-sub" v-if="contact.phone">{{ contact.phone }}</div>
                  <div class="drawer-contact-sub" v-else-if="contact.wechat">{{ contact.wechat }}</div>
                </div>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="drawer-contact-arrow"><polyline points="9 18 15 12 9 6"/></svg>
              </div>
            </div>
          </div>

          <div v-if="msg.time" class="bubble-timestamp" :class="msg.sender">
            <span>{{ msg.time }}</span>
            <svg v-if="msg.sender === 'user'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" class="read-icon"><polyline points="20 6 9 17 4 12"/><polyline points="22 10 12 20 9 17"/></svg>
          </div>
        </div>

        <div v-if="drawerIsLoading" class="message-bubble-wrapper assistant">
          <div class="bubble-content">
            <div class="typing-indicator">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>
      </div>

      <div class="chat-input-area">
        <div class="chat-input-card" :class="{ 'input-disabled': drawerIsLoading }">
          <textarea
            v-model="drawerInputText"
            placeholder="告诉我你想做什么..."
            :disabled="drawerIsLoading"
            @keydown.enter.prevent="sendDrawerUserMessage"
          ></textarea>
          <div class="chat-input-card-bottom">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="clip-icon"><path d="m21.44 11.05-9.19 9.19a6 6 0 0 1-8.49-8.49l9.19-9.19a4 4 0 0 1 5.66 5.66l-9.2 9.19a2 2 0 0 1-2.83-2.83l8.49-8.48"/></svg>
            <button class="chat-send-btn-circle" :class="{ loading: drawerIsLoading }" :disabled="drawerIsLoading" @click="sendDrawerUserMessage">
              <svg v-if="!drawerIsLoading" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
              <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="spin-icon"><path d="M21 12a9 9 0 1 1-6.219-8.56"/></svg>
            </button>
          </div>
        </div>
      </div>
      <div class="chat-footer-note">内容由 AI 生成，请仔细核对</div>
    </div>

    <!-- 移动端可拖拽三段式 Bottom Sheet Contact Agent -->
    <MobileBottomSheet
      v-else
      v-model="isDrawerOpen"
      preset="agent"
    >
      <template #header>
        <div class="chat-header-left" style="display:flex;align-items:center;gap:8px;">
          <div class="chat-header-icon" style="color:var(--color-primary);">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" style="width:18px;height:18px;"><path d="M11.017 2.814a1 1 0 0 1 1.966 0l1.051 5.558a2 2 0 0 0 1.594 1.594l5.558 1.051a1 1 0 0 1 0 1.966l-5.558 1.051a2 2 0 0 0-1.594 1.594l-1.051 5.558a1 1 0 0 1-1.966 0l-1.051-5.558a2 2 0 0 0-1.594-1.594l-5.558-1.051a1 1 0 0 1 0-1.966l5.558-1.051a2 2 0 0 0 1.594-1.594z"></path><path d="M20 2v4"></path><path d="M22 4h-4"></path><circle cx="4" cy="20" r="2"></circle></svg>
          </div>
          <strong style="font-size: 18px; font-weight: 600; color: var(--text-main);">Contact Agent</strong>
        </div>
        <div class="chat-header-actions" style="display:flex;align-items:center;gap:12px;">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" @click="resetDrawerChat" title="清空对话" style="width:20px;height:20px;cursor:pointer;color:var(--text-muted);"><path d="M2.5 2v6h6M21.5 22v-6h-6M22 11.5A10 10 0 0 0 3.2 7.2M2 12.5a10 10 0 0 0 18.8 4.2"/></svg>
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" @click="toggleAgentDrawer" title="关闭" style="width:20px;height:20px;cursor:pointer;color:var(--text-muted);"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
        </div>
      </template>

      <div class="chat-messages-container" ref="chatContainerRef" style="height:100%;">
        <div
          v-for="msg in chatMessages"
          :key="msg.id"
          class="message-bubble-wrapper"
          :class="msg.sender"
        >
          <div class="bubble-content">
            <div class="bubble-text" style="white-space: pre-wrap;">{{ msg.content }}</div>
            
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
                <span class="structured-value">{{ msg.structuredCard.timeStr }}</span>
              </div>
              <div class="structured-row">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/></svg>
                <span class="structured-label">内容：</span>
                <span class="structured-value">{{ msg.structuredCard.todoContent }}</span>
              </div>
              <div v-if="!msg.structuredCard.status || msg.structuredCard.status === 'pending'" class="structured-card-actions">
                <button class="btn-confirm" @click="confirmDrawerExecution(msg)">确认创建</button>
                <button class="btn-cancel" @click="cancelDrawerExecution(msg)">取消</button>
              </div>
              <div v-else-if="msg.structuredCard.status === 'confirmed'" style="margin-top: 12px;">
                <div style="color: var(--color-success); font-weight:600; font-size:12px; display:flex; align-items:center; gap:6px;">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="width:14px;height:14px;"><circle cx="12" cy="12" r="10"/><polyline points="12 8 12 12 14 14"/></svg>
                  执行成功！
                </div>
              </div>
              <div v-else-if="msg.structuredCard.status === 'cancelled'" style="margin-top: 12px;">
                <div style="color: var(--text-muted); font-weight:600; font-size:12px; display:flex; align-items:center; gap:6px;">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="width:14px;height:14px;"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
                  操作已取消
                </div>
              </div>
            </div>

            <!-- 真实 API 写操作确认卡片 -->
            <div v-if="msg.isConfirmCard" class="structured-card">
              <div class="structured-card-title">待办事项预确认</div>
              <div v-if="msg.parsedParams?.contactName" class="structured-row">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                <span class="structured-label">联系人：</span>
                <span class="structured-value">{{ msg.parsedParams.contactName }}</span>
              </div>
              <div v-if="msg.parsedParams?.todoTime" class="structured-row">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect width="18" height="18" x="3" y="4" rx="2"/><path d="M3 10h18M8 2v4M16 2v4"/></svg>
                <span class="structured-label">时间：</span>
                <span class="structured-value">{{ msg.parsedParams.todoTime }}</span>
              </div>
              <div v-if="msg.parsedParams?.content" class="structured-row">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/></svg>
                <span class="structured-label">内容：</span>
                <span class="structured-value">{{ msg.parsedParams.content }}</span>
              </div>
              <div v-if="msg.confirmState === 'pending'" class="structured-card-actions">
                <button class="btn-confirm" @click="confirmDrawerExecution(msg)">确认创建</button>
                <button class="btn-cancel" @click="cancelDrawerExecution(msg)">取消</button>
              </div>
              <div v-else-if="msg.confirmState === 'confirmed'" style="margin-top:12px;">
                <div style="color:var(--color-success);font-weight:600;font-size:12px;display:flex;align-items:center;gap:6px;">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="width:14px;height:14px;"><circle cx="12" cy="12" r="10"/><polyline points="9 12 11 14 15 10"/></svg>
                  已创建
                </div>
              </div>
              <div v-else-if="msg.confirmState === 'cancelled'" style="margin-top:12px;">
                <div style="color:var(--text-muted);font-weight:600;font-size:12px;display:flex;align-items:center;gap:6px;">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="width:14px;height:14px;"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
                  已取消
                </div>
              </div>
            </div>

            <!-- 联系人查询结果卡片 -->
            <div v-if="msg.queryType === 'contact' && msg.results && msg.results.length > 0" class="contact-results-list">
              <div
                v-for="contact in msg.results"
                :key="contact.contactId"
                class="drawer-contact-card"
                @click="goToContactDetail(contact.contactId)"
              >
                <img :src="getAvatarUrl(contact.avatarUrl)" :alt="contact.name" class="drawer-contact-avatar" />
                <div class="drawer-contact-info">
                  <div class="drawer-contact-name">{{ contact.name }}</div>
                  <div class="drawer-contact-sub" v-if="contact.phone">{{ contact.phone }}</div>
                  <div class="drawer-contact-sub" v-else-if="contact.wechat">{{ contact.wechat }}</div>
                </div>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="drawer-contact-arrow"><polyline points="9 18 15 12 9 6"/></svg>
              </div>
            </div>
          </div>

          <div v-if="msg.time" class="bubble-timestamp" :class="msg.sender">
            <span>{{ msg.time }}</span>
            <svg v-if="msg.sender === 'user'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" class="read-icon"><polyline points="20 6 9 17 4 12"/><polyline points="22 10 12 20 9 17"/></svg>
          </div>
        </div>

        <div v-if="drawerIsLoading" class="message-bubble-wrapper assistant">
          <div class="bubble-content">
            <div class="typing-indicator">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="chat-input-area" style="border-top:none; padding: 12px 16px;">
          <div class="chat-input-card" :class="{ 'input-disabled': drawerIsLoading }" style="border-radius:18px; padding:10px 14px;">
            <textarea
              v-model="drawerInputText"
              placeholder="告诉我你想做什么..."
              :disabled="drawerIsLoading"
              @keydown.enter.prevent="sendDrawerUserMessage"
              style="height:40px; font-size:14px;"
            ></textarea>
            <div class="chat-input-card-bottom" style="margin-top:2px;">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="clip-icon" style="width:18px;height:18px;"><path d="m21.44 11.05-9.19 9.19a6 6 0 0 1-8.49-8.49l9.19-9.19a4 4 0 0 1 5.66 5.66l-9.2 9.19a2 2 0 0 1-2.83-2.83l8.49-8.48"/></svg>
              <button class="chat-send-btn-circle" :class="{ loading: drawerIsLoading }" :disabled="drawerIsLoading" @click="sendDrawerUserMessage" style="width:32px;height:32px;">
                <svg v-if="!drawerIsLoading" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:14px;height:14px;"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
                <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="spin-icon" style="width:14px;height:14px;"><path d="M21 12a9 9 0 1 1-6.219-8.56"/></svg>
              </button>
            </div>
          </div>
        </div>
        <div class="chat-footer-note" style="padding-bottom:12px;">内容由 AI 生成，请仔细核对</div>
      </template>
    </MobileBottomSheet>

    <!-- 移动端关系维护状态联系人明细 BottomSheet -->
    <MobileBottomSheet
      v-if="isMobile"
      v-model="isHealthSheetOpen"
      preset="custom"
    >
      <template #header>
        <div style="display:flex; align-items:center; gap:8px;">
          <span style="display:inline-block; width:10px; height:10px; border-radius:50%;" :style="{ backgroundColor: healthSheetCategory?.color || '#3B82F6' }"></span>
          <strong style="font-size:16px; font-weight:600; color:var(--text-main);">{{ healthSheetCategory?.name }}联系人</strong>
          <span style="font-size:12px; color:var(--text-muted); font-weight:normal;">({{ healthSheetCategory?.count }}人)</span>
        </div>
      </template>

      <div style="padding: 8px 16px 20px 16px; display:flex; flex-direction:column; gap:10px;">
        <div v-if="!healthSheetCategory?.list || healthSheetCategory.list.length === 0" style="text-align:center; color:var(--text-muted); padding:20px 0; font-size:14px;">
          暂无该分类下的联系人
        </div>
        <div
          v-else
          v-for="item in healthSheetCategory.list"
          :key="item.contactId"
          class="drawer-contact-card"
          @click="goToContactDetail(item.contactId)"
          style="background-color: var(--bg-hover); padding: 12px; border-radius: 12px; display:flex; align-items:center; justify-content:space-between; cursor:pointer;"
        >
          <div style="display:flex; align-items:center; gap:10px;">
            <div style="width:36px; height:36px; border-radius:50%; background-color:#eff6ff; color:#3b82f6; display:flex; align-items:center; justify-content:center; font-weight:bold; font-size:14px;">
              {{ item.name ? item.name.substring(0, 1) : '客' }}
            </div>
            <div>
              <div style="font-size:14px; font-weight:600; color:var(--text-main);">{{ item.name }}</div>
              <div style="font-size:12px; color:var(--text-muted);" v-if="item.lastEventTitle">{{ item.lastEventTitle }}</div>
            </div>
          </div>
          <div style="text-align:right;">
            <span style="font-size:12px; font-weight:600; color:#475569;" v-if="item.daysAgo !== undefined && item.daysAgo !== null">
              {{ item.daysAgo === 0 ? '今天' : item.daysAgo + '天前' }}
            </span>
            <span style="font-size:12px; color:#94a3b8;" v-else>无轨迹</span>
          </div>
        </div>
      </div>
    </MobileBottomSheet>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, computed, watch, nextTick } from 'vue'
import MobileBottomSheet from '@/components/common/MobileBottomSheet.vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { gsap } from 'gsap'
import { getDashboardOverview, getTodoTrend, getRelationshipHealth } from '@/api/dashboard'
import type { DashboardOverview, TodoTrendItem, RelationshipHealth } from '@/api/dashboard'
import { getContactsApi } from '@/api/contact'
import type { ContactInfo } from '@/api/contact'
import { getTodos, completeTodo, cancelTodo } from '@/api/todo'
import type { TodoInfo } from '@/types/todo'
import { resolveAvatarUrl } from '@/utils/avatar'
import { executeAgent, confirmAgent } from '@/api/agent'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

// 移动端响应式识别
const isMobile = ref(typeof window !== 'undefined' ? window.innerWidth <= 768 : false)
const checkMobileDevice = () => {
  isMobile.value = window.innerWidth <= 768
}

// 移动端外部 Bottom Sheet 联动态 (用于收起 FAB)
const isExternalSheetOpen = ref(false)
const handleExternalSheetState = (e: Event) => {
  const customEvt = e as CustomEvent
  if (customEvt.detail) {
    isExternalSheetOpen.value = customEvt.detail.isOpen
  }
}
onMounted(() => {
  window.addEventListener('resize', checkMobileDevice)
  window.addEventListener('mobile-sheet-state-change', handleExternalSheetState)
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', checkMobileDevice)
  window.removeEventListener('mobile-sheet-state-change', handleExternalSheetState)
})

// 智能助手悬浮窗拖拽相关逻辑
let isDragging = false
let dragMoved = false
let startX = 0
let startY = 0
let initialLeft = 0
let initialTop = 0


// 移动端关系维护状态联系人明细 BottomSheet
const isHealthSheetOpen = ref(false)
const healthSheetCategory = ref<{
  name: string
  color: string
  count: number
  list: any[]
} | null>(null)

// 1. 数据定义与初始化
const overview = ref<DashboardOverview>({
  contactCount: 0,
  blacklistCount: 0,
  pendingTodoCount: 0,
  todayTodoCount: 0,
  overdueTodoCount: 0
})

const todayTodos = ref<TodoInfo[]>([])
const displayTodayTodos = computed(() => {
  return [...todayTodos.value].sort((a, b) => {
    const aFinished = a.status === 2 || a.status === 1 ? 1 : 0
    const bFinished = b.status === 2 || b.status === 1 ? 1 : 0
    return aFinished - bFinished
  })
})
const recentContacts = ref<ContactInfo[]>([])

// 获取头像地址
function getAvatarUrl(url: string | null): string {
  return resolveAvatarUrl(url) || 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&auto=format&fit=crop&q=80'
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
      gsap.set(carouselTrackRef.value, { xPercent: 0 })
      const pages = carouselTrackRef.value.querySelectorAll('.carousel-page')
      pages.forEach((page, idx) => {
        if (idx === activeSlide.value) {
          gsap.set(page, { autoAlpha: 1, x: 0, scale: 1.0 })
        } else {
          gsap.set(page, { autoAlpha: 0, x: 0, scale: 1.0 })
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

    // 1. 上一页就地快速淡出 (0.15s)
    if (prevPage) {
      gsap.to(prevPage, {
        duration: 0.15,
        autoAlpha: 0,
        ease: "power1.out",
        overwrite: "auto"
      })
    }

    // 2. 新一页快速滑入 (0.5s)
    if (nextPage) {
      const isNext = index > prevIndex
      const startX = isNext ? 50 : -50 // 适当缩减开始的偏差值，使 0.5s 滑行更自然
      
      gsap.fromTo(nextPage,
        { 
          autoAlpha: 0,
          x: startX
        },
        {
          delay: 0.1, // 极其错开淡入
          duration: 0.5, // 黄金 0.5s 时长，动作清爽利落
          autoAlpha: 1,
          x: 0,
          ease: "power3.out", // 后半段平缓收尾
          overwrite: "auto"
        }
      )
    }
  }
}

// 3. 事项勾选切换状态
async function markTodoCompleted(todo: TodoInfo) {
  try {
    await completeTodo(todo.matterId)
    ElMessage.success('已标记事项为完成')
    todo.status = 2
    // 同时更新大指标卡数据
    if (overview.value.pendingTodoCount > 0) overview.value.pendingTodoCount--
    if (overview.value.todayTodoCount > 0) overview.value.todayTodoCount--
  } catch (e) {
    console.error('Failed to complete todo:', e)
  }
}

async function markTodoCancelled(todo: TodoInfo) {
  try {
    await cancelTodo(todo.matterId)
    ElMessage.success('已取消事项')
    todo.status = 1
    // 同时更新大指标卡数据
    if (overview.value.pendingTodoCount > 0) overview.value.pendingTodoCount--
    if (overview.value.todayTodoCount > 0) overview.value.todayTodoCount--
  } catch (e) {
    console.error('Failed to cancel todo:', e)
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
      status: 0,
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

async function refreshDashboardData() {
  await Promise.all([fetchOverview(), fetchTodayTodos(), initMicroSchedule()])
}

interface MicroScheduleDay {
  dateStr: string
  fullDate: string
  dayName: string
  count: number
  todos?: TodoInfo[]
  loadingTodos?: boolean
}

const microScheduleDays = ref<MicroScheduleDay[]>([])
const activeDayIndex = ref<number>(0)
const currentActiveDay = computed(() => {
  return microScheduleDays.value[activeDayIndex.value] || null
})

const totalFutureTodos = computed(() => {
  return microScheduleDays.value.reduce((acc, item) => acc + item.count, 0)
})

function selectTimelineDay(index: number) {
  activeDayIndex.value = index
  const day = microScheduleDays.value[index]
  if (day) {
    loadDayTodos(day)
  }
}

async function initMicroSchedule() {
  try {
    const data: TodoTrendItem[] = await getTodoTrend(7)
    const daysName = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    const result: MicroScheduleDay[] = []

    for (let i = 0; i < 7; i++) {
      const d = new Date()
      d.setDate(d.getDate() + i)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      const fullDate = `${year}-${month}-${day}`
      const dateStr = `${d.getMonth() + 1}/${d.getDate()}`
      const dayName = i === 0 ? '今天' : (daysName[d.getDay()] || '')
      
      const match = data.find(item => item.date === fullDate || item.date === dateStr)
      const count = match ? match.count : (data[i]?.count ?? 0)

      result.push({
        dateStr,
        fullDate,
        dayName,
        count
      })
    }
    microScheduleDays.value = result

    // 自动拉取所有有事项日期的详细待办列表
    await Promise.all(
      microScheduleDays.value
        .filter(d => d.count > 0)
        .map(d => loadDayTodos(d))
    )

    // 智能定位：默认选中第一个有待办的日期，方便用户一眼看核心事项；若无待办则选中今天
    const firstHasTodoIndex = microScheduleDays.value.findIndex(d => d.count > 0)
    if (firstHasTodoIndex !== -1) {
      activeDayIndex.value = firstHasTodoIndex
    } else {
      activeDayIndex.value = 0
    }
  } catch (e) {
    console.error('Failed to init micro schedule:', e)
  }
}

async function loadDayTodos(day: MicroScheduleDay) {
  if (day.todos !== undefined || day.loadingTodos) return
  day.loadingTodos = true
  try {
    const res = await getTodos({
      startTime: `${day.fullDate} 00:00:00`,
      endTime: `${day.fullDate} 23:59:59`,
      page: 1,
      pageSize: 5
    })
    day.todos = res.list
  } catch (e) {
    console.error('Failed to load day todos:', e)
    day.todos = []
  } finally {
    day.loadingTodos = false
  }
}

function goToTodoDate(fullDate: string) {
  router.push({ path: '/todos', query: { date: fullDate } })
}

function goToTodoItem(todo: any, fullDate?: string) {
  const query: Record<string, string> = {}
  if (todo?.content) {
    query.keyword = todo.content
  }
  if (fullDate) {
    query.date = fullDate
  }
  router.push({ path: '/todos', query })
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
const healthChartRef = ref<HTMLDivElement | null>(null)
let trendChartInstance: echarts.ECharts | null = null
let healthChartInstance: echarts.ECharts | null = null

function handleResize() {
  trendChartInstance?.resize()
  healthChartInstance?.resize()
  initRelationshipHealthChart()
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

async function initRelationshipHealthChart() {
  if (!healthChartRef.value) return
  try {
    const data: RelationshipHealth = await getRelationshipHealth()
    
    const chartData = [
      { value: data.active, name: '活跃', itemStyle: { color: '#3B82F6' } },
      { value: data.followUp, name: '待跟进', itemStyle: { color: '#8B5CF6' } },
      { value: data.inactive, name: '长期未联系', itemStyle: { color: '#F59E0B' } },
      { value: data.noActivity, name: '无动态', itemStyle: { color: '#CBD5E1' } }
    ]

    const totalCount = data.total || 0

    if (!healthChartInstance) {
      healthChartInstance = echarts.init(healthChartRef.value)
    }

    const option = {
      tooltip: {
        show: !isMobile.value,
        trigger: 'item',
        backgroundColor: 'rgba(255, 255, 255, 0.98)',
        borderColor: '#e2e8f0',
        borderWidth: 1,
        padding: [10, 14],
        extraCssText: 'box-shadow: 0 10px 25px -5px rgba(0,0,0,0.1), 0 8px 10px -6px rgba(0,0,0,0.05); border-radius: 12px;',
        formatter: (params: any) => {
          const name = params.name
          const count = params.value
          const percent = params.percent
          const color = params.color

          let list: any[] = []
          if (name === '活跃') list = data.activeList || []
          else if (name === '待跟进') list = data.followUpList || []
          else if (name === '长期未联系') list = data.inactiveList || []
          else if (name === '无动态') list = data.noActivityList || []

          let header = `<div style="font-weight:700; font-size:13px; color:#1e293b; margin-bottom:6px; display:flex; align-items:center; gap:6px;">
            <span style="display:inline-block; width:8px; height:8px; border-radius:50%; background-color:${color};"></span>
            <span>${name} (${count}人 · ${percent}%)</span>
          </div>`

          if (list.length === 0) {
            return header + `<div style="font-size:12px; color:#94a3b8; padding:2px 0;">暂无该分类联系人</div>`
          }

          let itemsHtml = list.slice(0, 5).map(item => {
            const timeInfo = item.daysAgo !== undefined && item.daysAgo !== null 
              ? `<span style="color:#64748b; font-size:11px;">${item.daysAgo === 0 ? '今天' : item.daysAgo + '天前'}</span>` 
              : `<span style="color:#94a3b8; font-size:11px;">无轨迹</span>`
            const titleInfo = item.lastEventTitle ? `<span style="color:#94a3b8; font-size:11px; max-width:100px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; display:inline-block;"> · ${item.lastEventTitle}</span>` : ''
            return `<div style="display:flex; align-items:center; justify-content:space-between; gap:12px; font-size:12px; padding:3px 0; border-top:1px dashed #f1f5f9;">
              <strong style="color:#334155; font-weight:600;">${item.name}</strong>
              <div style="display:flex; align-items:center; gap:4px;">${timeInfo}${titleInfo}</div>
            </div>`
          }).join('')

          let moreNotice = list.length > 5 ? `<div style="font-size:11px; color:#94a3b8; margin-top:4px; text-align:right;">等共 ${list.length} 人...</div>` : ''

          return `<div style="min-width:180px; max-width:260px;">${header}${itemsHtml}${moreNotice}</div>`
        }
      },
      legend: isMobile.value
        ? {
            orient: 'horizontal',
            bottom: '0%',
            left: 'center',
            icon: 'circle',
            itemWidth: 8,
            itemHeight: 8,
            itemGap: 10,
            textStyle: { fontSize: 11, color: '#6b7280' },
            formatter: (name: string) => {
              const item = chartData.find(d => d.name === name)
              const val = item ? item.value : 0
              return `${name} ${val}`
            }
          }
        : {
            orient: 'vertical',
            right: '4%',
            top: 'center',
            icon: 'circle',
            itemWidth: 10,
            itemHeight: 10,
            itemGap: 14,
            textStyle: { fontSize: 12, color: '#6b7280' },
            formatter: (name: string) => {
              const item = chartData.find(d => d.name === name)
              const val = item ? item.value : 0
              const percent = totalCount > 0 ? ((val / totalCount) * 100).toFixed(1) : '0.0'
              return `${name.padEnd(6, ' ')}  ${val} (${percent}%)`
            }
          },
      series: [
        {
          name: '关系维护状态',
          type: 'pie',
          radius: isMobile.value ? ['52%', '74%'] : ['62%', '84%'],
          center: isMobile.value ? ['50%', '38%'] : ['31%', '52%'],
          avoidLabelOverlap: false,
          label: {
            show: true,
            position: 'center',
            formatter: `{total|${totalCount}}\n{sub|总联系人}`,
            rich: {
              total: {
                fontSize: isMobile.value ? 20 : 24,
                fontWeight: 'bold',
                lineHeight: isMobile.value ? 28 : 34,
                color: '#1f2937'
              },
              sub: {
                fontSize: 11,
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
    healthChartInstance.setOption(option)

    healthChartInstance.off('click')
    healthChartInstance.on('click', (params: any) => {
      const name = params.name
      const color = params.color || '#3B82F6'
      let list: any[] = []
      if (name === '活跃') list = data.activeList || []
      else if (name === '待跟进') list = data.followUpList || []
      else if (name === '长期未联系') list = data.inactiveList || []
      else if (name === '无动态') list = data.noActivityList || []

      healthSheetCategory.value = {
        name,
        color,
        count: list.length,
        list
      }
      isHealthSheetOpen.value = true
    })
  } catch (e) {
    console.error('Failed to init relationship health chart:', e)
  }
}

// 6. AI Agent 抽屉交互逻辑
interface ChatMessage {
  id: string
  sender: 'user' | 'assistant'
  content: string
  time: string
  // 写操作确认卡字段
  isConfirmCard?: boolean
  logId?: number
  confirmState?: 'pending' | 'confirmed' | 'cancelled'
  parsedParams?: {
    contactName?: string
    todoTime?: string
    content?: string
  }
  // 查询结果
  queryType?: 'contact' | 'todo'
  results?: ContactInfo[]
  // 兼容旧静态卡片（保留用于 template 渲染）
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
const drawerSessionId = ref<string | null>(null)
const drawerIsLoading = ref<boolean>(false)

const chatMessages = ref<ChatMessage[]>([
  {
    id: 'msg-init',
    sender: 'assistant',
    content: '👋 你好，我是你的智能联系人助手。\n我可以帮你查找联系人、查询或创建待办事项，用自然语言告诉我就好。',
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

// 智能助手悬浮窗拖动开始
function handleDragStart(e: MouseEvent | TouchEvent) {
  // 只有鼠标左键按下或触摸时才触发
  if ('button' in e && e.button !== 0) return

  const btn = e.currentTarget as HTMLElement
  dragMoved = false
  isDragging = true

  const clientX = 'touches' in e ? (e.touches[0]?.clientX ?? 0) : e.clientX
  const clientY = 'touches' in e ? (e.touches[0]?.clientY ?? 0) : e.clientY

  startX = clientX
  startY = clientY

  const rect = btn.getBoundingClientRect()
  initialLeft = rect.left
  initialTop = rect.top

  btn.style.transition = 'none'
  btn.style.right = 'auto'
  btn.style.bottom = 'auto'
  btn.style.left = `${initialLeft}px`
  btn.style.top = `${initialTop}px`

  const onDragMove = (moveEvent: MouseEvent | TouchEvent) => {
    if (!isDragging) return
    const currentX = 'touches' in moveEvent ? (moveEvent.touches[0]?.clientX ?? 0) : moveEvent.clientX
    const currentY = 'touches' in moveEvent ? (moveEvent.touches[0]?.clientY ?? 0) : moveEvent.clientY

    const deltaX = currentX - startX
    const deltaY = currentY - startY

    if (Math.abs(deltaX) > 4 || Math.abs(deltaY) > 4) {
      dragMoved = true
    }

    let newLeft = initialLeft + deltaX
    let newTop = initialTop + deltaY

    const maxLeft = window.innerWidth - rect.width
    const maxTop = window.innerHeight - rect.height

    if (newLeft < 0) newLeft = 0
    if (newLeft > maxLeft) newLeft = maxLeft
    if (newTop < 0) newTop = 0
    if (newTop > maxTop) newTop = maxTop

    btn.style.left = `${newLeft}px`
    btn.style.top = `${newTop}px`
  }

  const onDragEnd = () => {
    isDragging = false
    btn.style.transition = 'opacity 0.3s, transform 0.3s'
    window.removeEventListener('mousemove', onDragMove)
    window.removeEventListener('touchmove', onDragMove)
    window.removeEventListener('mouseup', onDragEnd)
    window.removeEventListener('touchend', onDragEnd)
  }

  window.addEventListener('mousemove', onDragMove, { passive: false })
  window.addEventListener('touchmove', onDragMove, { passive: false })
  window.addEventListener('mouseup', onDragEnd)
  window.addEventListener('touchend', onDragEnd)
}

function toggleAgentDrawer() {
  if (dragMoved) {
    dragMoved = false
    return
  }
  isDrawerOpen.value = !isDrawerOpen.value
  setTimeout(() => {
    handleResize()
  }, 300)
}

async function sendDrawerUserMessage() {
  const text = drawerInputText.value.trim()
  if (!text || drawerIsLoading.value) return
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

  // 有待确认卡片时阻止继续发送
  const hasPending = chatMessages.value.some(m => m.isConfirmCard && m.confirmState === 'pending')
  if (hasPending) {
    chatMessages.value.push({
      id: `msg-ai-${Date.now()}`,
      sender: 'assistant',
      content: '请先处理上方待确认的操作卡片，再继续输入。',
      time: timeStr
    })
    scrollToBottom()
    return
  }

  drawerIsLoading.value = true
  try {
    const res = await executeAgent({
      input: text,
      sessionId: drawerSessionId.value || undefined
    })

    if (res.sessionId) drawerSessionId.value = res.sessionId

    if (res.isClarifying) {
      chatMessages.value.push({
        id: `msg-ai-${Date.now()}`,
        sender: 'assistant',
        content: res.summary || '请补充缺少的信息。',
        time: timeStr
      })
    } else if (res.needConfirm === 1 && res.actionType === 'create_todo') {
      chatMessages.value.push({
        id: `msg-ai-${Date.now()}`,
        sender: 'assistant',
        content: res.summary || '已为您生成以下待办事项，请确认：',
        time: timeStr,
        isConfirmCard: true,
        logId: res.logId,
        parsedParams: res.parsedParams,
        confirmState: 'pending'
      })
    } else if ((res.actionType === 'query_contact' || res.actionType === 'query_todo') && res.results && res.results.length > 0) {
      chatMessages.value.push({
        id: `msg-ai-${Date.now()}`,
        sender: 'assistant',
        content: res.summary || '查询成功。',
        time: timeStr,
        queryType: res.actionType === 'query_contact' ? 'contact' : 'todo',
        results: res.actionType === 'query_contact' ? (res.results as ContactInfo[]) : undefined
      })
    } else {
      chatMessages.value.push({
        id: `msg-ai-${Date.now()}`,
        sender: 'assistant',
        content: res.summary || '好的，操作已处理。',
        time: timeStr
      })
    }
  } catch (e: unknown) {
    chatMessages.value.push({
      id: `msg-ai-err-${Date.now()}`,
      sender: 'assistant',
      content: e instanceof Error ? e.message : '请求失败，请检查网络或登录状态。',
      time: timeStr
    })
  } finally {
    drawerIsLoading.value = false
    scrollToBottom()
  }
}

async function confirmDrawerExecution(msg: ChatMessage) {
  if (!msg.logId || msg.confirmState !== 'pending') return
  try {
    await confirmAgent({ logId: msg.logId, action: 'confirm' })
    msg.confirmState = 'confirmed'
    const now = new Date()
    const timeStr = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
    chatMessages.value.push({
      id: `msg-ai-ok-${Date.now()}`,
      sender: 'assistant',
      content: `✅ 已成功创建待办：${msg.parsedParams?.content || ''}${msg.parsedParams?.todoTime ? ' 时间：' + msg.parsedParams.todoTime : ''}`,
      time: timeStr
    })
    ElMessage.success('待办事项已创建！')
  } catch (e: unknown) {
    ElMessage.error(e instanceof Error ? e.message : '确认失败')
  }
  scrollToBottom()
}

async function cancelDrawerExecution(msg: ChatMessage) {
  if (!msg.logId || msg.confirmState !== 'pending') return
  try {
    await confirmAgent({ logId: msg.logId, action: 'cancel' })
    msg.confirmState = 'cancelled'
    const now = new Date()
    const timeStr = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
    chatMessages.value.push({
      id: `msg-ai-cancel-${Date.now()}`,
      sender: 'assistant',
      content: '已取消本次操作。',
      time: timeStr
    })
  } catch (e: unknown) {
    ElMessage.error(e instanceof Error ? e.message : '取消失败')
  }
  scrollToBottom()
}

function resetDrawerChat() {
  drawerSessionId.value = null
  chatMessages.value = [
    {
      id: `msg-init-reset-${Date.now()}`,
      sender: 'assistant',
      content: '👋 对话已重置。我可以帮你查找联系人、查询或创建待办事项，用自然语言告诉我就好。',
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

  await initMicroSchedule()
  await initRelationshipHealthChart()

  window.addEventListener('resize', handleResize)

  if (carouselContainerRef.value) {
    carouselCtx = gsap.context(() => {}, carouselContainerRef.value)
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  trendChartInstance?.dispose()
  healthChartInstance?.dispose()
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
  /* 高度不写死，由 relative 定位的 .is-static（第一页）自适应内容撑开 */
}

.carousel-track {
  position: relative;
  width: 100%;
  height: auto;
}

.carousel-page {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
  opacity: 0;
  visibility: hidden;
  pointer-events: none; /* 屏蔽背景页面的操作 */
}

/* 第一页作为占位，始终以相对定位在文档流中撑开整体高度，自适应任何缩放和行数变化 */
.carousel-page.is-static {
  position: relative;
  top: auto;
  left: auto;
  height: auto;
}

.carousel-page.active {
  opacity: 1;
  visibility: visible;
  pointer-events: auto; /* 允许点击激活页面的卡片 */
}

@media (max-width: 640px) {
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

.todo-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
  flex-wrap: wrap;
}

.todo-action-icon {
  padding: 0;
  border: none;
  background: transparent;
  cursor: pointer;
}

.todo-inline-btn {
  min-width: 44px;
  height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  border: 1px solid transparent;
  background: #fff;
  font-size: 11px;
  font-weight: 700;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.todo-inline-btn.complete {
  color: var(--color-success);
  border-color: #bbf7d0;
  background: #f0fdf4;
}

.todo-inline-btn.cancel {
  color: #b45309;
  border-color: #fcd34d;
  background: #fffbeb;
}

.todo-inline-btn:hover {
  transform: translateY(-1px);
}

/* 空状态美化 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 16px;
  text-align: center;
  width: 100%;
}

.empty-icon {
  width: 48px !important;
  height: 48px !important;
  margin-bottom: 12px;
  color: #94a3b8;
  opacity: 0.85;
}

.empty-state p {
  font-size: 13px;
  color: #64748b;
  margin: 0;
  font-weight: 500;
}

/* 已完成与已取消事项的样式 */
.todo-item.completed {
  opacity: 0.65;
  background: rgba(248, 250, 252, 0.85);
  border-color: rgba(203, 213, 225, 0.8);
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

.todo-item.cancelled {
  opacity: 0.55;
  background: rgba(248, 250, 252, 0.75);
  border-color: rgba(226, 232, 240, 0.8);
}

.todo-item.cancelled .todo-title {
  color: #94a3b8;
  text-decoration: line-through;
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
  height: 100%;
  display: flex;
  flex-direction: column;
}

#healthChart,
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
  padding: 12px 16px 6px 16px;
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
  padding-bottom: 8px;
  background: var(--bg-card);
}

/* 问候头部栏 */
.dashboard-header {
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 18px;
  border: 1px solid rgba(226, 232, 240, 0.9);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(243, 244, 246, 0.9) 100%);
  backdrop-filter: blur(12px);
  box-shadow: 0 10px 30px -20px rgba(15, 23, 42, 0.2), 0 2px 6px rgba(15, 23, 42, 0.02);
  margin-bottom: 4px;
}

.header-welcome {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.welcome-title {
  font-size: 22px;
  font-weight: 800;
  color: #1e293b;
  margin: 0;
  letter-spacing: -0.02em;
}

.welcome-subtitle {
  font-size: 13px;
  color: #64748b;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}

.icon-calendar {
  width: 14px;
  height: 14px;
  color: #94a3b8;
}

/* 天气区域 */
.header-weather {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.7);
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.03);
  transition: transform 0.2s, background-color 0.2s;
}

.header-weather:hover {
  transform: translateY(-1px);
  background: rgba(255, 255, 255, 0.85);
}

.weather-main {
  display: flex;
  align-items: center;
  gap: 12px;
}

.weather-icon {
  width: 38px;
  height: 38px;
  object-fit: contain;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.06));
}

.weather-info-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.weather-temp-row {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.weather-temp {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  font-family: var(--font-mono, monospace);
}

.weather-text {
  font-size: 12px;
  font-weight: 600;
  color: #475569;
}

.weather-location {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #64748b;
  font-weight: 500;
}

.icon-location {
  width: 11px;
  height: 11px;
  color: var(--color-primary);
}

@media (max-width: 640px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 14px;
    padding: 16px;
  }
  
  .header-weather {
    width: 100%;
    justify-content: flex-start;
  }
}

/* ===== Agent 加载动画 ===== */
.typing-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  min-height: unset;
}

.typing-indicator span {
  display: inline-block;
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background-color: #9ca3af;
  animation: typing-bounce 1.2s infinite ease-in-out;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing-bounce {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.5; }
  30% { transform: translateY(-4px); opacity: 1; }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.spin-icon {
  animation: spin 0.9s linear infinite;
  width: 16px;
  height: 16px;
}

.chat-send-btn-circle.loading {
  opacity: 0.7;
  cursor: not-allowed;
}

.chat-send-btn-circle:disabled {
  cursor: not-allowed;
}

.chat-input-card.input-disabled textarea {
  background: #f9fafb;
  color: #9ca3af;
  cursor: not-allowed;
}

/* ===== Contact query card styles in Agent Chat ===== */
.contact-results-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 10px;
  width: 100%;
}

.drawer-contact-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
}

.drawer-contact-card:hover {
  border-color: #3b82f6;
  background-color: #f8fafc;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.08);
}

.drawer-contact-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #f1f5f9;
}

.drawer-contact-info {
  flex: 1;
  min-width: 0;
}

.drawer-contact-name {
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.drawer-contact-sub {
  font-size: 11px;
  color: #64748b;
  margin-top: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.drawer-contact-arrow {
  width: 16px;
  height: 16px;
  color: #94a3b8;
  transition: transform 0.2s ease, color 0.2s ease;
}

.drawer-contact-card:hover .drawer-contact-arrow {
  transform: translateX(2px);
  color: #3b82f6;
}

/* ===== Out-of-bubble timestamp styles ===== */
.bubble-timestamp {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 10px;
  color: #8b9bb4;
  margin-top: 4px;
}

.bubble-timestamp.user {
  align-self: flex-end;
  justify-content: flex-end;
  margin-right: 4px;
}

.bubble-timestamp.assistant {
  align-self: flex-start;
  justify-content: flex-start;
  margin-left: 4px;
}

.bubble-timestamp .read-icon {
  width: 12px;
  height: 12px;
  color: #3b82f6;
}

/* ===== 移动端响应式覆盖样式 ===== */
@media (max-width: 768px) {
  .contacts-panel,
  .todos-panel {
    min-width: 0 !important;
    min-height: auto !important;
  }

  .panel-card {
    min-height: auto !important;
  }
  
  .dashboard-container {
    padding: 12px !important;
    overflow-x: hidden;
    gap: 16px !important;
  }
  
  /* 4个摘要卡：在小屏下以 4 列平铺展示，字号微调 */
  .stats-grid {
    grid-template-columns: repeat(4, 1fr) !important;
    gap: 8px !important;
  }
  
  .stat-card {
    padding: 12px 4px !important;
    border-radius: 14px !important;
    flex-direction: column !important;
    align-items: center !important;
    justify-content: center !important;
    gap: 4px !important;
    min-height: 85px !important;
  }
  
  .stat-icon {
    width: 38px !important;
    height: 38px !important;
    border-radius: 12px !important;
    margin-bottom: 0px !important;
  }
  
  .stat-icon :deep(svg),
  .stat-icon svg {
    width: 18px !important;
    height: 18px !important;
  }
  
  .stat-info {
    text-align: center !important;
    align-items: center !important;
  }
  
  .stat-label {
    font-size: 10px !important;
    margin-bottom: 2px !important;
    white-space: nowrap;
    font-weight: 500 !important;
    color: #64748b !important;
  }
  
  .stat-value {
    font-size: 18px !important;
    font-weight: 700 !important;
    line-height: 1.2 !important;
  }

  /* 彩色数字样式 */
  .stat-primary .stat-value {
    color: var(--color-primary) !important;
  }
  .stat-success .stat-value {
    color: var(--color-success) !important;
  }
  .stat-danger .stat-value {
    color: #ef4444 !important;
  }
  .stat-warning .stat-value {
    color: #8b5cf6 !important;
  }
  
  .stat-change {
    display: none !important; /* 小小卡片装不下较昨日字样，隐藏 */
  }

  /* 智能助手按钮移动端初始位置避开底栏 */
  .floating-agent-btn {
    bottom: 80px !important;
    right: 16px !important;
  }
  
  /* 中部大栏目在小屏下改为纵向流式堆叠 */
  .dashboard-layout {
    grid-template-columns: 1fr !important;
    gap: 16px !important;
  }
  
  /* 重要联系人横栏滑动适配 */
  .carousel-container {
    overflow: visible !important;
  }
  
  .carousel-track {
    display: flex !important;
    flex-wrap: nowrap !important;
    overflow-x: auto !important;
    scroll-snap-type: x mandatory !important;
    width: 100% !important;
    gap: 10px !important;
    padding-bottom: 8px !important;
    transform: none !important; /* 禁用轮播图的JS定位动画，交由原生手势滚动 */
  }
  
  /* 隐藏轮播指示点和控制按钮 */
  .carousel-indicators,
  .carousel-btn {
    display: none !important;
  }
  
  .carousel-page {
    display: flex !important;
    flex-shrink: 0 !important;
    width: auto !important;
    gap: 10px !important;
    opacity: 1 !important;
    visibility: visible !important;
    position: static !important;
    transform: none !important;
    pointer-events: auto !important;
  }
  
  .contact-card {
    flex: 0 0 106px !important;
    scroll-snap-align: start !important;
    border-radius: 16px !important;
    padding: 12px 6px !important;
    box-shadow: var(--shadow-sm) !important;
    margin: 0 !important;
  }
  
  .card-avatar {
    width: 44px !important;
    height: 44px !important;
    margin-bottom: 6px !important;
  }
  
  .card-name {
    font-size: 12px !important;
  }
  
  .card-company {
    font-size: 9px !important;
    margin-bottom: 6px !important;
  }
  
  .contact-card .badge {
    font-size: 8px !important;
    padding: 1px 6px !important;
    margin-bottom: 6px !important;
  }
  
  .contact-action-btn {
    width: 22px !important;
    height: 22px !important;
  }
  
  .contact-action-btn svg {
    width: 11px !important;
    height: 11px !important;
  }
  
  /* 待办事项美化微调 */
  .todos-panel {
    padding: 16px 12px !important;
  }
  
  .todo-item {
    padding: 12px 8px !important;
    border-radius: 12px !important;
    gap: 8px !important;
  }
  
  .todo-item .task-text {
    font-size: 13px !important;
  }
  
  .todo-item .ref-name {
    font-size: 10px !important;
  }
  
  .todo-item .task-time {
    font-size: 10px !important;
  }
  
  /* 图表区域适配：由并排改为纵向堆叠，等比压缩高度 */
  .charts-layout {
    grid-template-columns: 1fr !important;
    gap: 16px !important;
  }
  
  .chart-card {
    padding: 16px 12px !important;
    border-radius: 16px !important;
    min-width: 0 !important;
    width: 100% !important;
    overflow: hidden !important;
  }
  
  #healthChart,
  #trendChart {
    height: 180px !important; /* 图表高度压缩至 180px */
    width: 100% !important;
    min-width: 0 !important;
    overflow: hidden !important;
  }

  /* Agent 抽屉移动端 BottomSheet 改造 */
  .agent-drawer {
    top: auto !important;
    bottom: 0 !important;
    left: 0 !important;
    right: 0 !important;
    width: 100vw !important;
    height: 60vh !important; /* 默认展开 60% 保留背景上下文 */
    border-radius: 20px 20px 0 0 !important;
    border-left: none !important;
    border-top: 1px solid var(--border-color) !important;
    box-shadow: 0 -8px 24px rgba(15, 23, 42, 0.15) !important;
    transform: translateY(100%) !important; /* 初始在底部不可见 */
    transition: transform 0.25s cubic-bezier(0.25, 1, 0.5, 1), height 0.25s cubic-bezier(0.25, 1, 0.5, 1) !important;
    z-index: 1000 !important;
  }

  .chat-header {
    position: relative !important;
    padding-top: 22px !important; /* 留出顶部手势条空间 */
    background: var(--bg-card) !important;
  }

  /* 移动端专属拖拽手势指示条规范 */
  .chat-header::before {
    content: "" !important;
    position: absolute !important;
    top: 8px !important;
    left: 50% !important;
    transform: translateX(-50%) !important;
    width: 48px !important;
    height: 5px !important;
    background-color: var(--el-border-color-drag, #cbd5e1) !important;
    border-radius: 999px !important;
  }

  .chat-input-area {
    padding-bottom: env(safe-area-inset-bottom, 16px) !important;
  }
}

/* ==========================================
 * Apple 极致打磨 7 天 Entry Overview 卡片样式
 * ========================================== */
.timeline-overview-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 16px 20px 14px 20px;
  height: 100%;
  min-height: 226px;
}

.timeline-total-badge {
  font-size: 12px;
  font-weight: 500;
  color: #64748b;
  letter-spacing: 0.1px;
}

/* 顶部：7天底线高亮极简导航条（无灰色大卡槽） */
.timeline-segmented-bar.line-bar {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
  background-color: transparent;
  padding: 0;
  border-radius: 0;
  margin-top: 4px;
}

.timeline-segment-item.line-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4px 2px 6px 2px;
  border: none;
  background: transparent;
  border-bottom: 2px solid transparent;
  border-radius: 4px 4px 0 0;
  cursor: pointer;
  position: relative;
  transition: all 0.15s ease;
  outline: none;
}

.timeline-segment-item.line-item:hover {
  background-color: #f0f7ff;
  transform: translateY(-1px);
}

/* 选中态 (Click selection)：底边精致品牌蓝指示线，展现稳定坚固掌控感 */
.timeline-segment-item.line-item.is-active {
  background-color: transparent;
  border-bottom-color: #2563eb;
}

.segment-indicator {
  position: absolute;
  top: 2px;
  right: 4px;
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background-color: #2563eb;
}

.segment-day {
  font-size: 11px;
  font-weight: 500;
  color: #64748b;
  line-height: 1.1;
}

.timeline-segment-item.is-today .segment-day {
  color: var(--color-primary, #2563eb);
}

.timeline-segment-item.is-active .segment-day {
  color: #1d4ed8;
  font-weight: 700;
}

.segment-date {
  font-size: 10px;
  color: #94a3b8;
  font-weight: 400;
  line-height: 1.1;
  margin-top: 1px;
}

.timeline-segment-item.is-active .segment-date {
  color: #2563eb;
  font-weight: 600;
}

.overview-divider {
  height: 1px;
  border-bottom: 1px dashed #e2e8f0;
  margin: 4px 0 6px 0;
}

/* 底部：Apple 风格精美事项 Card 视窗 (标题主视角，时间 Secondary) */
.overview-focus-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
}

.focus-section-header {
  display: flex;
  align-items: center;
}

.focus-title {
  font-size: 11px;
  font-weight: 600;
  color: #64748b;
  letter-spacing: 0.2px;
}

.focus-section-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 4px 0;
}

.focus-loading-row, .focus-empty-row {
  font-size: 12px;
  color: #94a3b8;
}

.summary-cards-wrapper {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.summary-card-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
  background-color: var(--bg-hover, #f8fafc);
  border: 1px solid #f1f5f9;
  border-radius: 8px;
  padding: 6px 10px;
  cursor: pointer;
  transition: transform 0.15s ease, border-color 0.15s ease, background-color 0.15s ease;
}

.summary-card-item:hover {
  transform: translateX(2px);
  background-color: #ffffff;
  border-color: #bfdbfe;
  box-shadow: 0 4px 12px -3px rgba(37, 99, 235, 0.08);
}

.card-item-top {
  display: flex;
  align-items: center;
  gap: 6px;
}

.priority-mini-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
  background-color: #3b82f6;
}

.priority-mini-dot.badge-priority-emergency {
  background-color: #ef4444;
}

.priority-mini-dot.badge-priority-important {
  background-color: #f59e0b;
}

.card-item-title {
  font-size: 12px;
  font-weight: 600;
  color: #1e293b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-item-bottom {
  display: flex;
  align-items: center;
  padding-left: 12px;
}

.card-item-time {
  font-size: 10px;
  font-weight: 500;
  color: #94a3b8;
}

.focus-section-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 2px;
}

.entry-more-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  font-weight: 600;
  color: #2563eb;
  text-decoration: none;
  transition: gap 0.15s ease, color 0.15s ease;
}

.entry-more-link:hover {
  color: #1d4ed8;
  gap: 6px;
}

.link-arrow {
  width: 12px;
  height: 12px;
}

/* 切换平滑动效 (150ms 黄金微滑) */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.26s cubic-bezier(0.25, 1, 0.5, 1), transform 0.26s cubic-bezier(0.25, 1, 0.5, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(8px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-8px);
}
</style>
