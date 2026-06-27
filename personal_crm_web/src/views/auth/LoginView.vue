<template>
  <div class="page">
    <!-- ══ 左侧预览 ══ -->
    <section class="preview">
      <!-- 品牌 -->
      <div class="brand">
        <div class="brand-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
          </svg>
        </div>
        <span class="brand-name">Personal CRM</span>
      </div>

      <!-- 主标题 -->
      <div class="hero">
        <h1 class="hero-title">Personal CRM</h1>
        <p class="hero-sub">管理联系人、待办事项与智能提醒的个人关系管理平台</p>
      </div>

      <!-- 卡片区 -->
      <div class="cards-area">
        <div class="top-cards">
          <!-- 联系人 -->
          <div class="panel">
            <div class="panel-head">
              <div class="panel-title">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                <span>联系人</span>
              </div>
              <a href="#" class="panel-link" @click.prevent>查看全部</a>
            </div>
            <div class="contact-list">
              <div class="contact-item">
                <div class="contact-left">
                  <div class="avatar av-a">
                    <img src="https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=80&auto=format&fit=crop&q=80" alt="张雨薇">
                  </div>
                  <div class="contact-info">
                    <div class="contact-name-row">张雨薇 <span class="tag tag-green">同事</span></div>
                    <div class="contact-org">腾讯科技 · 产品经理</div>
                    <div class="contact-topic">产品合作</div>
                  </div>
                </div>
                <div class="contact-time">今天</div>
              </div>
              <div class="contact-item">
                <div class="contact-left">
                  <div class="avatar av-b">
                    <img src="https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=80&auto=format&fit=crop&q=80" alt="李明轩">
                  </div>
                  <div class="contact-info">
                    <div class="contact-name-row">李明轩 <span class="tag tag-blue">朋友</span></div>
                    <div class="contact-org">独立开发者</div>
                    <div class="contact-topic">技术交流</div>
                  </div>
                </div>
                <div class="contact-time">昨天</div>
              </div>
              <div class="contact-item">
                <div class="contact-left">
                  <div class="avatar av-c">
                    <img src="https://images.unsplash.com/photo-1517841905240-472988babdf9?w=80&auto=format&fit=crop&q=80" alt="王思颖">
                  </div>
                  <div class="contact-info">
                    <div class="contact-name-row">王思颖 <span class="tag tag-green">同学</span></div>
                    <div class="contact-org">清华大学 · 市场营销</div>
                    <div class="contact-topic">校友</div>
                  </div>
                </div>
                <div class="contact-time">2天前</div>
              </div>
              <div class="contact-item">
                <div class="contact-left">
                  <div class="avatar av-d">
                    <img src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=80&auto=format&fit=crop&q=80" alt="陈泽宇">
                  </div>
                  <div class="contact-info">
                    <div class="contact-name-row">陈泽宇 <span class="tag tag-blue">客户</span></div>
                    <div class="contact-org">某公司 · 运营总监</div>
                    <div class="contact-topic">商务合作</div>
                  </div>
                </div>
                <div class="contact-time">3天前</div>
              </div>
            </div>
            <button class="panel-footer-btn" type="button">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
              添加联系人
            </button>
          </div>

          <!-- 待办事项 -->
          <div class="panel">
            <div class="panel-head">
              <div class="panel-title">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>
                <span>待办事项</span>
              </div>
              <a href="#" class="panel-link" @click.prevent>查看全部</a>
            </div>
            <div class="todo-list">
              <div class="todo-item">
                <div class="todo-left">
                  <div class="todo-check"></div>
                  <div class="todo-text">跟进张雨薇的产品需求</div>
                </div>
                <div class="todo-date hot">今天
                  <svg viewBox="0 0 24 24" fill="currentColor"><path d="M4 15s1-1 4-1 5 2 8 2 4-1 4-1V3s-1 1-4 1-5-2-8-2-4 1-4 1z"/><line x1="4" y1="22" x2="4" y2="15" stroke="currentColor" stroke-width="2"/></svg>
                </div>
              </div>
              <div class="todo-item">
                <div class="todo-left">
                  <div class="todo-check"></div>
                  <div class="todo-text">准备与李明轩的技术分享</div>
                </div>
                <div class="todo-date">明天</div>
              </div>
              <div class="todo-item">
                <div class="todo-left">
                  <div class="todo-check"></div>
                  <div class="todo-text">发送合作方案给陈泽宇</div>
                </div>
                <div class="todo-date">6月2日</div>
              </div>
              <div class="todo-item">
                <div class="todo-left">
                  <div class="todo-check"></div>
                  <div class="todo-text">校友聚会活动报名</div>
                </div>
                <div class="todo-date">6月5日</div>
              </div>
            </div>
            <button class="panel-footer-btn" type="button">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
              新建待办
            </button>
          </div>
        </div>

        <!-- AI 助手 -->
        <div class="panel ai-panel">
          <div class="ai-head">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z"/><path d="M20 3v4m2-2h-4"/></svg>
            <span>AI 智能助手</span>
          </div>
          <div class="ai-row">
            <div class="ai-avatar">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="10" x="3" y="11" rx="2"/><circle cx="12" cy="5" r="2"/><path d="M12 7v4"/><line x1="8" y1="16" x2="8" y2="16"/><line x1="16" y1="16" x2="16" y2="16"/></svg>
            </div>
            <div class="ai-bubble">下午好！我是您的智能助手小P，有什么可以帮您的吗？</div>
          </div>
          <div class="ai-actions">
            <button class="ai-action" type="button">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>
              总结最近联系
            </button>
            <button class="ai-action" type="button">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><line x1="19" y1="8" x2="19" y2="14"/><line x1="22" y1="11" x2="16" y2="11"/></svg>
              推荐跟进联系人
            </button>
            <button class="ai-action" type="button">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="2" x2="12" y2="6"/><path d="M12 18a6 6 0 0 0 0-12"/><line x1="12" y1="18" x2="12" y2="22"/><line x1="4.93" y1="4.93" x2="7.76" y2="7.76"/><line x1="16.24" y1="16.24" x2="19.07" y2="19.07"/></svg>
              待办事项建议
            </button>
          </div>
        </div>
      </div>

      <div class="preview-footer">© 2024 Personal CRM. 让每一段关系更有价值。</div>
    </section>

    <!-- ══ 右侧登录 ══ -->
    <section class="auth-col">
      <div class="auth-card">
        <div class="auth-avatar-header">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
        </div>
        <h2 class="auth-title">欢迎回来</h2>
        <p class="auth-sub">登录您的 Personal CRM 账户</p>

        <!-- 原型统一错误红框 -->
        <div v-show="showError" class="error-alert">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
          <span>{{ errorText }}</span>
        </div>

        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label class="form-label" for="username">账号或邮箱</label>
            <div class="input-wrap">
              <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="20" height="16" x="2" y="4" rx="2"/><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/></svg>
              <input
                id="username"
                v-model="loginForm.username"
                type="text"
                placeholder="请输入账号或邮箱"
                autocomplete="username"
              />
            </div>
          </div>

          <div class="form-group">
            <label class="form-label" for="password">密码</label>
            <div class="input-wrap">
              <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="11" x="3" y="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
              <input
                id="password"
                v-model="loginForm.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码"
                autocomplete="current-password"
              />
              <svg
                class="toggle-pw"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                @click="togglePasswordVisibility"
              >
                <template v-if="showPassword">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/>
                </template>
                <template v-else>
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
                </template>
              </svg>
            </div>
          </div>

          <div class="form-options">
            <label class="checkbox-container">
              <input id="rememberMe" v-model="rememberMe" type="checkbox" />
              <span class="checkmark"></span>
              记住我
            </label>
            <a href="#" class="forgot-link" @click.prevent="forgotPassword">忘记密码?</a>
          </div>

          <button type="submit" class="btn-login" :disabled="loading">
            {{ loading ? '正在登录...' : '登录' }}
          </button>
          
          <p class="register-row">
            <span>还没有账号？</span>
            <a href="#" @click.prevent="goToRegister">立即注册</a>
          </p>
        </form>

        <!-- 数据安全提示 -->
        <div class="security-tips">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><path d="m9 12 2 2 4-4"/></svg>
          <span>我们将严格保护你的数据安全</span>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref<boolean>(false)
const rememberMe = ref<boolean>(false)
const showPassword = ref<boolean>(false)

// 错误提示控制
const showError = ref<boolean>(false)
const errorText = ref<string>('账号或密码错误')

const loginForm = reactive({
  username: '',
  password: ''
})

onMounted(() => {
  const savedUsername = localStorage.getItem('saved_username')
  const savedPassword = localStorage.getItem('saved_password')
  if (savedUsername) {
    loginForm.username = savedUsername
    rememberMe.value = true
  }
  if (savedPassword) {
    try {
      loginForm.password = atob(savedPassword)
    } catch {
      loginForm.password = savedPassword
    }
  }
})

const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value
}

const handleLogin = async () => {
  showError.value = false
  const username = loginForm.username.trim()
  const password = loginForm.password

  // 1. 原型规定的非空拦截校验
  if (!username || !password) {
    errorText.value = '请输入账号和密码'
    showError.value = true
    return
  }

  // 2. 原型规定的简易校验拦截 (当账号为 admin 且密码不是 admin 时)
  if (username === 'admin' && password !== 'admin') {
    errorText.value = '账号或密码错误'
    showError.value = true
    return
  }

  loading.value = true
  try {
    // 3. 执行真实后端联调登录
    await authStore.login({ username, password })

    if (rememberMe.value) {
      localStorage.setItem('saved_username', username)
      localStorage.setItem('saved_password', btoa(password))
    } else {
      localStorage.removeItem('saved_username')
      localStorage.removeItem('saved_password')
    }

    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (error: any) {
    console.error('Login error:', error)
    // 4. 将 API 层的报错显示在原型规定的红框中
    errorText.value = error.message || '账号或密码错误'
    showError.value = true
  } finally {
    loading.value = false
  }
}

const forgotPassword = () => {
  ElMessage.info('请使用系统预置演示账号 ethan / 123456 进行登录')
}

const goToRegister = () => {
  router.push({ name: 'register' })
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=Outfit:wght@400;500;600;700;800&display=swap');

.page {
  display: grid;
  grid-template-columns: 1.15fr 1fr;
  width: 100vw;
  height: 100vh;
  background:
    radial-gradient(ellipse 60% 70% at 20% 50%, rgba(255, 255, 255, 0.85) 0%, transparent 100%),
    linear-gradient(135deg, #eef3ff 0%, #f0f4ff 50%, #edf1fb 100%);
  font-family: 'Inter', 'Outfit', -apple-system, BlinkMacSystemFont, 'PingFang SC', sans-serif;
  color: #1f2937;
}

.preview {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 18px 7vw 24px 6vw;
  overflow: hidden;
  position: relative;
  text-align: left;
}

.preview::before {
  content: '';
  position: absolute;
  width: 520px;
  height: 520px;
  border-radius: 50%;
  border: 1px solid rgba(147, 197, 253, 0.2);
  top: -180px;
  left: 36%;
  pointer-events: none;
}

.preview::after {
  content: '';
  position: absolute;
  width: 240px;
  height: 240px;
  border-radius: 50%;
  border: 1px solid rgba(191, 219, 254, 0.25);
  bottom: -80px;
  left: -80px;
  pointer-events: none;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(180deg, #4f7cff 0%, #2f5bff 100%);
  box-shadow: 0 8px 20px -8px rgba(47, 91, 255, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.brand-icon svg {
  width: 18px;
  height: 18px;
}

.brand-name {
  font-size: 15px;
  font-weight: 700;
  color: #111827;
}

.hero {
  margin-top: 24px;
  margin-bottom: 16px;
}

.hero-title {
  font-size: clamp(2rem, 3.2vw, 3rem);
  font-weight: 800;
  letter-spacing: -0.04em;
  color: #0c1538;
  line-height: 1;
  margin-bottom: 4px;
}

.hero-sub {
  font-size: 13.5px;
  color: #4b5563;
  font-weight: 500;
}

.cards-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-height: 0;
  z-index: 10;
}

.top-cards {
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 14px;
  min-height: 0;
  flex: 1;
}

.panel {
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 16px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  min-height: 0;
  box-shadow: 0 8px 30px -10px rgba(0, 0, 0, 0.04);
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13.5px;
  font-weight: 700;
  color: #0f172a;
}

.panel-title svg {
  width: 16px;
  height: 16px;
  color: #2f5bff;
}

.panel-link {
  font-size: 11.5px;
  font-weight: 700;
  color: #2f5bff;
  text-decoration: none;
}

.panel-link:hover {
  text-decoration: underline;
}

.contact-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
  overflow-y: auto;
  padding-right: 2px;
}

.contact-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 10px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.2s ease;
}

.contact-item:hover {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(47, 91, 255, 0.15);
  transform: translateY(-0.5px);
}

.contact-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  background: #f1f5f9;
  border: 1px solid #fff;
  box-shadow: 0 4px 10px -4px rgba(0,0,0,0.1);
  flex-shrink: 0;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.contact-info {
  line-height: 1.25;
}

.contact-name-row {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12.5px;
  font-weight: 700;
  color: #1f2937;
  white-space: nowrap;
}

.tag {
  display: inline-flex;
  align-items: center;
  padding: 1px 7px;
  border-radius: 999px;
  font-size: 10.5px;
  font-weight: 700;
  white-space: nowrap;
}

.tag-green {
  background: #dcfce7;
  color: #15803d;
}

.tag-blue {
  background: #dbeafe;
  color: #1d4ed8;
}

.contact-org {
  font-size: 11px;
  color: #94a3b8;
  font-weight: 500;
  margin-top: 1px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.contact-topic {
  font-size: 11px;
  font-weight: 700;
  color: #2f5bff;
  margin-top: 1px;
}

.contact-time {
  font-size: 11.5px;
  font-weight: 600;
  color: #94a3b8;
  flex-shrink: 0;
  white-space: nowrap;
  padding-top: 2px;
}

.todo-list {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  gap: 0;
}

.todo-item {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 6px 0;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  gap: 8px;
}

.todo-item:last-of-type {
  border-bottom: none;
}

.todo-left {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  min-width: 0;
}

.todo-check {
  width: 15px;
  height: 15px;
  border: 1.5px solid #cbd5e1;
  border-radius: 4px;
  flex-shrink: 0;
  margin-top: 1px;
  background: #fff;
}

.todo-text {
  font-size: 12.5px;
  font-weight: 600;
  color: #334155;
  line-height: 1.45;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.todo-date {
  font-size: 11.5px;
  font-weight: 600;
  color: #94a3b8;
  flex-shrink: 0;
  white-space: nowrap;
  padding-top: 1px;
  display: flex;
  align-items: center;
  gap: 3px;
}

.todo-date.hot {
  color: #ef4444;
}

.todo-date svg {
  width: 11px;
  height: 11px;
}

.panel-footer-btn {
  width: 100%;
  height: 30px;
  margin-top: 12px;
  border: 1px solid rgba(226, 232, 240, 0.9);
  border-radius: 8px;
  background: #fff;
  color: #2f5bff;
  font-size: 12px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  cursor: pointer;
  flex-shrink: 0;
  transition: all 0.2s;
}

.panel-footer-btn svg {
  width: 12px;
  height: 12px;
}

.ai-panel {
  padding: 10px 14px;
  flex-shrink: 0;
}

.ai-head {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 700;
  color: #334155;
  margin-bottom: 8px;
}

.ai-head svg {
  width: 14px;
  height: 14px;
  color: #6366f1;
}

.ai-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.ai-avatar {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  background: linear-gradient(180deg, #3b5bff, #111827);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.ai-avatar svg {
  width: 12px;
  height: 12px;
}

.ai-bubble {
  flex: 1;
  padding: 6px 10px;
  border-radius: 10px;
  border: 1px solid rgba(226, 232, 240, 0.85);
  background: #f8fbff;
  font-size: 11.5px;
  font-weight: 600;
  color: #475569;
}

.ai-actions {
  display: flex;
  gap: 8px;
}

.ai-action {
  flex: 1;
  height: 28px;
  padding: 0 8px;
  border-radius: 8px;
  border: 1px solid rgba(226, 232, 240, 0.9);
  background: #fff;
  color: #475569;
  font-size: 11px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}

.ai-action svg {
  width: 12px;
  height: 12px;
}

.ai-action:nth-child(1) svg { color: #3b82f6; }
.ai-action:nth-child(2) svg { color: #10b981; }
.ai-action:nth-child(3) svg { color: #f59e0b; }

.preview-footer {
  font-size: 11px;
  color: #94a3b8;
  font-weight: 500;
  margin-top: 6px;
  flex-shrink: 0;
}

/* 右侧登录 */
.auth-col {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px 50px;
  overflow-y: auto;
  height: 100%;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 20px 40px -10px rgba(15, 23, 42, 0.08);
  padding: 40px;
  margin: auto 0;
  position: relative;
  text-align: left;
}

.auth-avatar-header {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #eff6ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2f5bff;
  margin: 0 auto 16px;
  box-shadow: 0 8px 16px -8px rgba(47, 91, 255, 0.5);
}

.auth-avatar-header svg {
  width: 26px;
  height: 26px;
}

.auth-title {
  font-size: 24px;
  font-weight: 800;
  color: #0f172a;
  text-align: center;
  margin-bottom: 4px;
  letter-spacing: -0.02em;
}

.auth-sub {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
  text-align: center;
  margin-bottom: 24px;
}

/* 错误提示红框 */
.error-alert {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  border-radius: 10px;
  border: 1px solid #ffd0d4;
  background: #fff5f5;
  color: #ef4444;
  font-size: 12.5px;
  font-weight: 600;
  margin-bottom: 16px;
}

.error-alert svg {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.form-group {
  margin-bottom: 16px;
}

.form-label {
  display: block;
  font-size: 13px;
  font-weight: 700;
  color: #334155;
  margin-bottom: 6px;
}

.input-wrap {
  position: relative;
}

.input-wrap input {
  width: 100%;
  height: 44px;
  padding: 0 40px;
  border-radius: 10px;
  border: 1.5px solid #e2e8f0;
  background: #fff;
  font-family: inherit;
  font-size: 13px;
  font-weight: 500;
  color: #0f172a;
  transition: all 0.2s ease;
  outline: none;
}

.input-wrap input::placeholder {
  color: #94a3b8;
}

.input-wrap input:focus {
  border-color: #2f5bff;
  box-shadow: 0 0 0 4px rgba(47, 91, 255, 0.08);
}

.field-icon {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: #94a3b8;
  left: 12px;
  pointer-events: none;
}

.toggle-pw {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: #94a3b8;
  right: 12px;
  cursor: pointer;
  transition: color 0.15s ease;
}

.toggle-pw:hover {
  color: #475569;
}

.form-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 14px 0 20px 0;
}

.checkbox-container {
  display: flex;
  align-items: center;
  position: relative;
  padding-left: 24px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  user-select: none;
  line-height: 16px;
}

.checkbox-container input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.checkmark {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  left: 0;
  height: 16px;
  width: 16px;
  background-color: #fff;
  border: 1.5px solid #cbd5e1;
  border-radius: 4px;
  transition: all 0.15s ease;
}

.checkbox-container:hover input ~ .checkmark {
  border-color: #2f5bff;
}

.checkbox-container input:checked ~ .checkmark {
  background-color: #2f5bff;
  border-color: #2f5bff;
}

.checkmark:after {
  content: "";
  position: absolute;
  display: none;
}

.checkbox-container input:checked ~ .checkmark:after {
  display: block;
}

.checkbox-container .checkmark:after {
  left: 4px;
  top: 1px;
  width: 4px;
  height: 8px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.forgot-link {
  color: #2f5bff;
  font-weight: 700;
  text-decoration: none;
  font-size: 13px;
}

.forgot-link:hover {
  text-decoration: underline;
}

.btn-login {
  width: 100%;
  height: 44px;
  border-radius: 10px;
  border: none;
  background: linear-gradient(135deg, #4f46e5 0%, #2f5bff 100%);
  color: #fff;
  font-family: inherit;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 10px 20px -8px rgba(47, 91, 255, 0.4);
  transition: all 0.2s ease;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-login:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 12px 24px -6px rgba(47, 91, 255, 0.5);
  background: linear-gradient(135deg, #4338ca 0%, #1d4ed8 100%);
}

.btn-login:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.register-row {
  text-align: center;
  font-size: 13px;
  font-weight: 600;
  color: #94a3b8;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-bottom: 24px;
  position: relative;
}

.register-row::before,
.register-row::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e2e8f0;
}

.register-row span,
.register-row a {
  background: #fff;
  padding: 0 8px;
  z-index: 1;
}

.register-row a {
  color: #2f5bff;
  font-weight: 700;
  text-decoration: none;
  margin-left: -8px;
}

.register-row a:hover {
  text-decoration: underline;
}

.security-tips {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #94a3b8;
  font-size: 11px;
  font-weight: 500;
}

.security-tips svg {
  width: 12px;
  height: 12px;
}

@media (max-width: 900px) {
  .page {
    grid-template-columns: 1fr;
    height: auto;
  }
  .preview {
    display: none;
  }
  .auth-col {
    min-height: 100vh;
    padding: 30px 20px;
  }
  .auth-card {
    padding: 30px 24px;
    box-shadow: none;
    border: 1px solid #e2e8f0;
  }
}
</style>
