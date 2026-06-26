<template>
  <div class="contact-detail-page-wrapper" v-loading="loading">
    <div v-if="contact">
      <!-- 头部大卡片 -->
      <section class="card detail-header-card">
        <div class="detail-header-left">
          <div class="detail-avatar-container">
            <img v-if="contact.avatarUrl" :src="getAvatarUrl(contact.avatarUrl)" :alt="contact.name" class="detail-header-avatar">
            <div v-else class="detail-header-avatar-placeholder">
              {{ contact.name ? contact.name.charAt(0).toUpperCase() : '?' }}
            </div>
          </div>
          <div class="detail-header-info">
            <h3>
              {{ contact.name }}
              <span v-if="contact.status === 1" class="badge badge-status-normal" style="background-color: #fee2e2; color: #ef4444;">已拉黑</span>
              <span v-else class="badge badge-status-normal">正常</span>
            </h3>
            <div class="detail-tags-row" v-if="contact.tags && contact.tags.length > 0">
              <span
                v-for="t in contact.tags"
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
            </div>
            <div class="detail-tags-row" v-else>
              <span class="text-muted" style="font-size: 11px; opacity: 0.5;">-</span>
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
                <a v-if="contact.phone" :href="`tel:${contact.phone}`" class="contact-link" title="拨打电话">
                  {{ formatPhone(contact.phone) }}
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;color:var(--color-primary);">
                    <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
                  </svg>
                </a>
                <span v-else>-</span>
              </span>
            </div>
            <div class="info-item">
              <span class="info-label">邮箱</span>
              <span class="info-value">
                <a v-if="contact.email" :href="`mailto:${contact.email}`" class="contact-link" @click="handleEmailClickOnlyCopy(contact.email)" title="拉起邮箱并发送邮件">
                  {{ contact.email }}
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;color:var(--color-primary);">
                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/>
                  </svg>
                </a>
                <span v-else>-</span>
              </span>
            </div>
            <div class="info-item">
              <span class="info-label">微信</span>
              <span class="info-value">
                <a v-if="contact.wechat && contact.wechat !== '-'" href="weixin://" class="contact-link" style="color: #07c160;" @click="handleWechatClick(contact.wechat)" title="复制微信号并拉起微信">
                  {{ contact.wechat }}
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;color:#07c160;">
                    <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-7.6-4.7 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>
                  </svg>
                </a>
                <span v-else>-</span>
              </span>
            </div>
            <div class="info-item">
              <span class="info-label">QQ</span>
              <span class="info-value">
                <a v-if="contact.qq && contact.qq !== '-'" :href="`tencent://Message/?Uin=${contact.qq}&websiteName=qzone.qq.com&Menu=yes`" class="contact-link" style="color: #2563eb;" @click="handleQqClick(contact.qq)" title="拉起 QQ 发起临时会话">
                  {{ contact.qq }}
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;color:#2563eb;">
                    <circle cx="12" cy="12" r="10"/><path d="M12 8v4l3 3"/>
                  </svg>
                </a>
                <span v-else>-</span>
              </span>
            </div>
            <div class="info-item address-item" :class="{ 'has-weather': contact.address && contactWeather }">
              <span class="info-label">地址</span>
              <div class="address-content-wrapper">
                <span class="info-value address-value-row" @click="toggleWeatherForecast" :class="{ 'is-clickable': contact.address && contact.address !== '-' && contactWeather }">
                  <template v-if="contact.address && contact.address !== '-'">
                    <span class="address-text">{{ contact.address }}</span>
                    <!-- 嵌入当前天气微标 -->
                    <span v-if="contactWeather" class="weather-badge-inline" title="点击查看三天预报">
                      <img :src="getWeatherIconUrl(contactWeather.currentIcon)" :alt="contactWeather.currentText" class="weather-badge-icon">
                      <span class="weather-badge-temp">{{ contactWeather.currentTemp }}°C · {{ contactWeather.currentText }}</span>
                    </span>
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="address-icon">
                      <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/>
                    </svg>
                  </template>
                  <template v-else>
                    <span class="no-address-text">
                      未录入城市，<router-link :to="`/contacts/${contact.contactId}/edit`" class="go-to-edit-link" @click.stop>去设置</router-link>
                    </span>
                  </template>
                </span>
                
                <!-- 近三天天气预报折叠面板 -->
                <transition name="slide-fade">
                  <div v-if="showWeatherForecast && contactWeather" class="weather-forecast-panel" @click.stop>
                    <div class="forecast-title">{{ contactWeather.cityName }} 近三天天气预报</div>
                    <div class="forecast-list">
                      <div v-for="day in contactWeather.dailyForecast" :key="day.date" class="forecast-item">
                        <span class="forecast-date">{{ formatForecastDate(day.date) }}</span>
                        <div class="forecast-status">
                          <img :src="getWeatherIconUrl(day.iconDay)" :alt="day.textDay" class="forecast-icon">
                          <span class="forecast-text">{{ day.textDay }}</span>
                        </div>
                        <span class="forecast-temp-range">{{ day.tempMin }}°C ~ {{ day.tempMax }}°C</span>
                      </div>
                    </div>
                  </div>
                </transition>
              </div>
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
import { getTagsApi } from '@/api/tag'
import type { TagInfo } from '@/api/tag'
import { getTodos, completeTodo, cancelTodo } from '@/api/todo'
import type { TodoInfo } from '@/types/todo'
import { resolveAvatarUrl } from '@/utils/avatar'
import { getWeatherIconUrl } from '@/utils/weather-icons'
import { getWeather } from '@/api/weather'
import type { WeatherData } from '@/api/weather'

const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=80&auto=format&fit=crop&q=80'

const route = useRoute()
const router = useRouter()

const loading = ref<boolean>(false)
const contact = ref<ContactInfo | null>(null)
const currentTab = ref<string>('pending')
const showBlacklistConfirm = ref<boolean>(false)

// 天气状态管理
const contactWeather = ref<WeatherData | null>(null)
const loadingWeather = ref<boolean>(false)
const showWeatherForecast = ref<boolean>(false)

const loadContactWeather = async (address: string) => {
  if (!address || address.trim() === '' || address.trim() === '-') return
  loadingWeather.value = true
  try {
    contactWeather.value = await getWeather(address)
  } catch (error) {
    console.error('Failed to load contact weather:', error)
  } finally {
    loadingWeather.value = false
  }
}

const toggleWeatherForecast = () => {
  if (contactWeather.value) {
    showWeatherForecast.value = !showWeatherForecast.value
  }
}

const formatForecastDate = (dateStr: string) => {
  if (!dateStr) return ''
  try {
    const today = new Date()
    const target = new Date(dateStr)
    
    // 获取当天的年月日，比较是否同一天
    const todayYear = today.getFullYear()
    const todayMonth = today.getMonth()
    const todayDate = today.getDate()
    
    const targetYear = target.getFullYear()
    const targetMonth = target.getMonth()
    const targetDate = target.getDate()
    
    if (todayYear === targetYear && todayMonth === targetMonth) {
      if (targetDate === todayDate) return '今天'
      if (targetDate === todayDate + 1) return '明天'
      if (targetDate === todayDate + 2) return '后天'
    }
    
    // 如果跨月或者年，计算天数差
    const timeDiff = target.getTime() - new Date(todayYear, todayMonth, todayDate).getTime()
    const dayDiff = Math.floor(timeDiff / (1000 * 60 * 60 * 24))
    if (dayDiff === 0) return '今天'
    if (dayDiff === 1) return '明天'
    if (dayDiff === 2) return '后天'
    
    return `${targetMonth + 1}/${targetDate}`
  } catch (e) {
    return dateStr
  }
}


const todos = ref<TodoInfo[]>([])
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
const todoLoading = ref<boolean>(false)

const contactId = computed(() => route.params.contactId as string)

const loadContactDetail = async () => {
  loading.value = true
  try {
    const data = await getContactDetailApi(contactId.value)
    contact.value = data
    if (data) {
      if (data.address) {
        loadContactWeather(data.address)
      }
      // 保存至最近查看联系人列表 (localStorage)，用于搜索框空值时快捷展示
      try {
        const listJson = localStorage.getItem('recently_viewed_contacts')
        let list = listJson ? JSON.parse(listJson) : []
        list = list.filter((c: any) => c.id !== data.contactId)
        list.unshift({ id: data.contactId, name: data.name })
        if (list.length > 4) list.pop()
        localStorage.setItem('recently_viewed_contacts', JSON.stringify(list))
      } catch (e) {
        console.error('Failed to save recently viewed contact:', e)
      }
    }
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

// 标签 CSS 类映射
const getTagClass = (tag: string) => {
  if (tag === '同学') return 'tag-class'
  if (tag === '朋友') return 'tag-friend'
  if (tag === '重要') return 'tag-important'
  if (tag === '实习' || tag === '合作伙伴') return 'tag-partner'
  return ''
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

const handleEmailClickOnlyCopy = (email: string) => {
  if (!email) return
  copyTextToClipboard(email).then(() => {
    ElMessage.success('已自动复制邮箱，正在为您拉起邮件应用...')
  })
}

const handleWechatClick = (wechat: string) => {
  if (!wechat) return
  copyTextToClipboard(wechat).then(() => {
    ElMessage.success('微信号已复制到剪贴板，正在为您打开微信...')
  })
}

const handleQqClick = (qq: string) => {
  if (!qq) return
  copyTextToClipboard(qq).then(() => {
    ElMessage.success('QQ号已复制到剪贴板，正在发起 QQ 临时会话...')
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

function getAvatarUrl(url: string | null): string {
  return resolveAvatarUrl(url)
}

onMounted(() => {
  loadContactDetail()
  loadContactTodos()
  fetchTagList()
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

.detail-avatar-container {
  flex-shrink: 0;
}

.detail-header-avatar-placeholder {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-full);
  background: #cbd5e1;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 28px;
  border: 3px solid #fff;
  box-shadow: var(--shadow-md);
  user-select: none;
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

/* 详情页天气小组件样式 */
.address-item {
  position: relative;
  align-items: flex-start !important;
}

.address-content-wrapper {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  flex: 1;
}

.address-value-row {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
  flex-wrap: wrap;
}

.address-value-row.is-clickable {
  cursor: pointer;
  user-select: none;
}

.address-value-row.is-clickable:hover .address-text {
  color: var(--color-primary);
}

.address-text {
  transition: color 0.2s;
}

.address-icon {
  width: 14px;
  height: 14px;
  color: var(--color-primary);
}

.weather-badge-inline {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 3px 8px;
  border-radius: 99px;
  background: rgba(239, 246, 255, 0.9);
  border: 1px solid rgba(191, 219, 254, 0.6);
  font-size: 11px;
  color: var(--color-primary);
  font-weight: 700;
  transition: all 0.2s ease;
}

.weather-badge-inline:hover {
  background: var(--color-primary-light);
  transform: scale(1.02);
}

.weather-badge-icon {
  width: 16px;
  height: 16px;
}

.no-address-text {
  color: #94a3b8;
  font-size: 12.5px;
  font-weight: 500;
}

.go-to-edit-link {
  color: var(--color-primary);
  text-decoration: underline;
  cursor: pointer;
  font-weight: 700;
  transition: color 0.2s;
}

.go-to-edit-link:hover {
  color: var(--color-primary-hover);
}

/* 折叠近三天天气预报面板 */
.weather-forecast-panel {
  width: 100%;
  padding: 12px;
  border-radius: 12px;
  background: linear-gradient(180deg, rgba(248, 250, 252, 0.95) 0%, rgba(255, 255, 255, 0.95) 100%);
  border: 1px solid rgba(226, 232, 240, 0.9);
  box-shadow: 0 6px 16px -8px rgba(15, 23, 42, 0.12);
  margin-top: 4px;
  text-align: left;
}

.forecast-title {
  font-size: 11.5px;
  font-weight: 700;
  color: #64748b;
  margin-bottom: 8px;
  padding-bottom: 4px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.6);
}

.forecast-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.forecast-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
  color: #334155;
  font-weight: 500;
}

.forecast-date {
  font-weight: 600;
  color: #475569;
  width: 32px;
}

.forecast-status {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
  justify-content: center;
}

.forecast-icon {
  width: 22px;
  height: 22px;
}

.forecast-text {
  font-weight: 600;
}

.forecast-temp-range {
  display: inline-flex;
  align-items: center;
  justify-content: flex-end;
  gap: 2px;
  font-family: var(--font-mono, monospace);
  font-weight: 600;
  color: #64748b;
  width: 80px;
  text-align: right;
  white-space: nowrap;
  flex-shrink: 0;
}

/* 动效 slide-fade */
.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.2s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(-8px);
  opacity: 0;
}

@media (max-width: 768px) {
  /* 头部大卡片折叠 */
  .detail-header-card {
    flex-direction: column !important;
    align-items: flex-start !important;
    gap: 16px !important;
    padding: 16px !important;
  }
  
  .detail-header-left {
    gap: 14px !important;
  }
  
  .detail-header-avatar {
    width: 64px !important;
    height: 64px !important;
  }
  
  .detail-header-avatar-placeholder {
    width: 64px !important;
    height: 64px !important;
    font-size: 22px !important;
  }
  
  .detail-header-info h3 {
    font-size: 18px !important;
  }
  
  /* 操作按钮组 Grid 2x2 */
  .detail-header-actions {
    width: 100% !important;
    display: grid !important;
    grid-template-columns: repeat(2, 1fr) !important;
    gap: 8px !important;
    margin-top: 12px !important;
  }
  
  .detail-header-actions .btn {
    width: 100% !important;
    margin: 0 !important;
    padding: 8px 6px !important;
    font-size: 12.5px !important;
    height: 38px !important;
    line-height: 1.2 !important;
    display: inline-flex !important;
    align-items: center !important;
    justify-content: center !important;
    min-width: unset !important;
  }

  .detail-header-actions .popconfirm-container {
    width: 100% !important;
    margin: 0 !important;
  }

  .detail-header-actions .popconfirm-container .btn {
    width: 100% !important;
  }

  /* 基础资料防溢出 */
  .info-item {
    align-items: flex-start !important;
    gap: 12px !important;
  }

  .info-value {
    word-break: break-all !important;
    text-align: right !important;
    display: inline-block !important;
  }
  
  /* 事项卡片重排（垂直流） */
  .todo-item-card {
    flex-direction: column !important;
    align-items: stretch !important;
    gap: 12px !important;
    padding: 14px !important;
  }
  
  .todo-item-right {
    padding-left: 0 !important;
    width: 100% !important;
    justify-content: space-between !important;
    margin-top: 6px !important;
    border-top: 1px dashed var(--border-color);
    padding-top: 10px !important;
  }

  .todo-action-group {
    gap: 6px !important;
  }

  .todo-action-btn {
    min-width: 60px !important;
    height: 32px !important;
    padding: 0 10px !important;
    font-size: 11.5px !important;
    border-radius: 8px !important;
  }
  
  /* 气泡二次确认框修正 */
  .popconfirm-overlay {
    width: 260px !important;
    right: -10px !important;
  }
}
</style>
