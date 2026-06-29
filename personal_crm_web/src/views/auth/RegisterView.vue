<template>
  <div class="page">
    <!-- ══ 左侧预览 (根据原型 register.html) ══ -->
    <section class="preview">
      <!-- 品牌 -->
      <div class="brand">
        <div class="brand-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
          </svg>
        </div>
        <span class="brand-name">Personal CRM</span>
      </div>

      <!-- 主标题 -->
      <div class="hero">
        <h1 class="hero-title">AI 驱动的人脉管理系统</h1>
        <p class="hero-sub">智能管理联系人，AI 助力关系维护，让每一次连接都产生价值</p>
      </div>

      <!-- 4大特性卡网格 -->
      <div class="features-grid">
        <div class="feature-item">
          <div class="feature-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
            </svg>
          </div>
          <div class="feature-meta">
            <h3>联系人管理</h3>
            <p>统一管理所有联系人，分类清晰</p>
          </div>
        </div>
        <div class="feature-item">
          <div class="feature-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/><path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/>
            </svg>
          </div>
          <div class="feature-meta">
            <h3>待办提醒</h3>
            <p>智能提醒跟进，不再错过重要关系</p>
          </div>
        </div>
        <div class="feature-item">
          <div class="feature-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="18" cy="18" r="3"/><circle cx="6" cy="6" r="3"/><circle cx="18" cy="6" r="3"/><circle cx="6" cy="18" r="3"/><line x1="6" y1="9" x2="6" y2="15"/><line x1="9" y1="6" x2="15" y2="6"/><line x1="18" y1="9" x2="18" y2="15"/><line x1="9" y1="18" x2="15" y2="18"/>
            </svg>
          </div>
          <div class="feature-meta">
            <h3>关系图谱</h3>
            <p>可视化关系网络，发现更多可能</p>
          </div>
        </div>
        <div class="feature-item">
          <div class="feature-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
              <path d="m12 3-1.912 5.886L4.2 9l5.887 1.912L12 21l1.912-5.887L19.8 15l-5.887-1.912Z"/>
            </svg>
          </div>
          <div class="feature-meta">
            <h3>AI 跟进建议</h3>
            <p>提供个性化沟通建议，提高成功率</p>
          </div>
        </div>
      </div>

      <!-- 人脉拓扑图与今日建议卡片 (毛玻璃效果) -->
      <div class="preview-card">
        <!-- 人脉拓扑关系图 -->
        <div class="network-diagram">
          <svg class="network-lines">
            <!-- 动态连接虚线 -->
            <line x1="110" y1="130" x2="110" y2="31" stroke="rgba(99, 102, 241, 0.4)" stroke-width="1.5" stroke-dasharray="4,4"/>
            <line x1="110" y1="130" x2="26" y2="126" stroke="rgba(99, 102, 241, 0.4)" stroke-width="1.5" stroke-dasharray="4,4"/>
            <line x1="110" y1="130" x2="186" y2="126" stroke="rgba(99, 102, 241, 0.4)" stroke-width="1.5" stroke-dasharray="4,4"/>
            <line x1="110" y1="130" x2="110" y2="216" stroke="rgba(99, 102, 241, 0.4)" stroke-width="1.5" stroke-dasharray="4,4"/>
            <!-- 外层连线轨道 -->
            <circle cx="110" cy="130" r="80" stroke="rgba(99, 102, 241, 0.08)" stroke-width="1.5" fill="none" stroke-dasharray="3,3"/>
          </svg>

          <!-- 中心节点 -->
          <div class="node node-center">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
            </svg>
          </div>
          <!-- 周边联系人节点 -->
          <div class="node node-top">
            <img src="https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=80&auto=format&fit=crop&q=80" alt="张雨薇">
            <span>张雨薇</span>
          </div>
          <div class="node node-left">
            <img src="https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=80&auto=format&fit=crop&q=80" alt="李明轩">
            <span>李明轩</span>
          </div>
          <div class="node node-right">
            <img src="https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=80&auto=format&fit=crop&q=80" alt="陈泽宇">
            <span>陈泽宇</span>
          </div>
          <div class="node node-bottom">
            <img src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=80&auto=format&fit=crop&q=80" alt="王思颖">
            <span>王思颖</span>
          </div>
        </div>

        <!-- AI建议区 -->
        <div class="ai-suggestions">
          <div class="suggestions-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="m12 3-1.912 5.886L4.2 9l5.887 1.912L12 21l1.912-5.887L19.8 15l-5.887-1.912Z"/>
            </svg>
            <span>AI 今日建议</span>
          </div>
          <div class="suggestion-list">
            <div class="suggestion-item">
              <div class="s-icon s-icon-orange">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              </div>
              <div class="s-content">
                <div class="s-header">
                  <span class="s-name">张雨薇</span>
                  <span class="s-badge badge-orange">7天未联系</span>
                </div>
                <p class="s-desc">建议跟进产品需求进展</p>
              </div>
            </div>
            <div class="suggestion-item">
              <div class="s-icon s-icon-green">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              </div>
              <div class="s-content">
                <div class="s-header">
                  <span class="s-name">李明轩</span>
                  <span class="s-badge badge-green">3天后生日</span>
                </div>
                <p class="s-desc">发送生日祝福</p>
              </div>
            </div>
            <div class="suggestion-item">
              <div class="s-icon s-icon-blue">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              </div>
              <div class="s-content">
                <div class="s-header">
                  <span class="s-name">王思颖</span>
                  <span class="s-badge badge-blue">14天未联系</span>
                </div>
                <p class="s-desc">可能弱化关系，建议跟进</p>
              </div>
            </div>
            <div class="suggestion-item">
              <div class="s-icon s-icon-purple">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              </div>
              <div class="s-content">
                <div class="s-header">
                  <span class="s-name">陈泽宇</span>
                  <span class="s-badge badge-purple">项目合作机会</span>
                </div>
                <p class="s-desc">有合作机会，建议联系</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 用户评价气泡 -->
      <div class="testimonial-card">
        <div class="quote-icon">“</div>
        <div class="testimonial-content">
          <p class="testimonial-text">自从使用 Personal CRM，我再也不会忘记跟进重要的人，人脉关系变得更紧密，工作机会也更多了。</p>
          <span class="testimonial-author">— 产品经理 · 使用了 3 个月</span>
        </div>
      </div>

      <!-- 版权 -->
      <div class="preview-footer">© 2024 Personal CRM. 让每一段关系更有价值。</div>
    </section>

    <!-- ══ 右侧注册表单 ══ -->
    <section class="auth-col">
      <div class="auth-card">
        <div class="auth-avatar-header">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
          </svg>
        </div>
        <h2 class="auth-title">创建你的账号</h2>
        <p class="auth-sub">立即注册，开启智能人脉管理之旅</p>

        <!-- 错误提示红框 -->
        <div v-show="showError" class="error-alert">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
          </svg>
          <span>{{ errorText }}</span>
        </div>

        <form @submit.prevent="handleRegister">
          <!-- 注册邮箱 -->
          <div class="form-group">
            <label class="form-label" for="email">注册邮箱</label>
            <div class="input-wrap">
              <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                <polyline points="22,6 12,13 2,6"/>
              </svg>
              <input
                id="email"
                v-model="registerForm.email"
                type="text"
                placeholder="请输入您的注册邮箱"
                autocomplete="email"
              />
            </div>
          </div>

          <!-- 邮箱验证码 -->
          <div class="form-group">
            <label class="form-label" for="code">邮箱验证码</label>
            <div class="code-row">
              <div class="input-wrap">
                <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
                <input
                  id="code"
                  v-model="registerForm.code"
                  type="text"
                  placeholder="6位验证码"
                  maxlength="6"
                />
              </div>
              <SendCodeButton
                ref="sendCodeBtnRef"
                purpose="REGISTER"
                :email="registerForm.email"
              />
            </div>
          </div>

          <!-- 密码 -->
          <div class="form-group">
            <label class="form-label" for="password">密码</label>
            <div class="input-wrap">
              <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect width="18" height="11" x="3" y="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>
              </svg>
              <input
                id="password"
                v-model="registerForm.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码（至少8位）"
                autocomplete="new-password"
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
                <g v-if="showPassword">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/>
                </g>
                <g v-else>
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
                </g>
              </svg>
            </div>
            <!-- 密码长度实时提示 -->
            <div class="feedback-msg" v-if="registerForm.password">
              <span :class="{ 'valid': isPasswordLengthValid, 'invalid': !isPasswordLengthValid }">
                <svg v-if="isPasswordLengthValid" class="feedback-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
                <svg v-else class="feedback-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                  <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
                密码长度至少 8 位（当前: {{ registerForm.password.length }} 位）
              </span>
            </div>
          </div>

          <!-- 确认密码 -->
          <div class="form-group">
            <label class="form-label" for="confirmPassword">确认密码</label>
            <div class="input-wrap">
              <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect width="18" height="11" x="3" y="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>
              </svg>
              <input
                id="confirmPassword"
                v-model="registerForm.confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                placeholder="请再次输入密码"
                autocomplete="new-password"
              />
              <svg
                class="toggle-pw"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                @click="toggleConfirmPasswordVisibility"
              >
                <g v-if="showConfirmPassword">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/>
                </g>
                <g v-else>
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
                </g>
              </svg>
            </div>
            <!-- 两次密码一致性实时提示 -->
            <div class="feedback-msg" v-if="registerForm.confirmPassword">
              <span :class="{ 'valid': isPasswordMatch === true, 'invalid': isPasswordMatch === false }">
                <svg v-if="isPasswordMatch === true" class="feedback-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
                <svg v-else class="feedback-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                  <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
                {{ isPasswordMatch === true ? '两次密码输入一致' : '两次密码输入不一致' }}
              </span>
            </div>
          </div>

          <!-- 用户协议勾选 -->
          <div class="agreement-row">
            <label class="checkbox-container">
              <input id="agreement" v-model="agreement" type="checkbox" />
              <span class="checkmark"></span>
              <span class="agreement-text">
                我已阅读并同意
                <a href="#" @click.prevent="showAgreement">《用户协议》</a>
                和
                <a href="#" @click.prevent="showPrivacy">《隐私政策》</a>
              </span>
            </label>
          </div>

          <button type="submit" class="btn-register" :disabled="loading">
            {{ loading ? '正在注册...' : '立即注册' }}
          </button>
          
          <div class="login-row">
            <span>已有账号？ <a href="#" @click.prevent="goToLogin">去登录</a></span>
          </div>

          <!-- 数据安全提示 -->
          <div class="security-tips">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
              <rect width="18" height="11" x="3" y="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>
            </svg>
            <span>我们将严格保护你的数据安全</span>
          </div>
        </form>
      </div>
    </section>


  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { registerApi } from '@/api/auth'
import SendCodeButton from '@/components/SendCodeButton.vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref<boolean>(false)
const showPassword = ref<boolean>(false)
const showConfirmPassword = ref<boolean>(false)
const agreement = ref<boolean>(false)

// 错误提示控制
const showError = ref<boolean>(false)
const errorText = ref<string>('请完整填写注册信息')

const sendCodeBtnRef = ref<InstanceType<typeof SendCodeButton> | null>(null)

const registerForm = reactive({
  email: '',
  code: '',
  password: '',
  confirmPassword: ''
})

const isPasswordLengthValid = computed(() => {
  return registerForm.password.length >= 8
})

const isPasswordMatch = computed(() => {
  if (!registerForm.confirmPassword) return null
  return registerForm.password === registerForm.confirmPassword
})

const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value
}

const toggleConfirmPasswordVisibility = () => {
  showConfirmPassword.value = !showConfirmPassword.value
}

const showAgreement = () => {
  ElMessage.info('用户协议（模拟）：我们承诺尊重和保护您的个人隐私数据，本平台仅作为智能联系人协助与事项记录之用。')
}

const showPrivacy = () => {
  ElMessage.info('隐私政策（模拟）：本平台对您的联系人及事项信息实行强加密处理，绝不会泄露给第三方。')
}

const goToLogin = () => {
  router.push({ name: 'login' })
}

const handleRegister = async () => {
  showError.value = false
  const email = registerForm.email.trim()
  const code = registerForm.code.trim()
  const password = registerForm.password
  const confirmPassword = registerForm.confirmPassword

  // 1. 非空校验
  if (!email || !code || !password || !confirmPassword) {
    errorText.value = '请完整填写注册信息'
    showError.value = true
    return
  }

  // 2. 邮箱格式校验
  const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailPattern.test(email)) {
    errorText.value = '请输入有效的电子邮箱地址'
    showError.value = true
    return
  }

  // 3. 验证码格式校验
  if (code.length !== 6) {
    errorText.value = '请输入 6 位数字验证码'
    showError.value = true
    return
  }

  // 4. 密码长度校验
  if (password.length < 8) {
    errorText.value = '密码长度至少为 8 位'
    showError.value = true
    return
  }

  // 5. 两次密码一致性校验
  if (password !== confirmPassword) {
    errorText.value = '两次密码输入不一致'
    showError.value = true
    return
  }

  // 6. 用户协议校验
  if (!agreement.value) {
    errorText.value = '请先阅读并同意用户协议和隐私政策'
    showError.value = true
    return
  }

  loading.value = true
  try {
    // 7. 调用后端注册 API (同时将 username 设为 email 以保持兼容)
    await registerApi({
      username: email,
      email: email,
      code: code,
      password: password
    })
    
    ElMessage.success('注册并激活成功！正在为您自动登录...')
    
    // 8. 自动静默登录并跳转至 dashboard
    await authStore.login({
      username: email,
      password: password
    })
    
    router.push('/dashboard')
  } catch (error: any) {
    console.error('Register error:', error)
    errorText.value = error.message || '注册失败，请稍后重试'
    showError.value = true
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=Outfit:wght@400;500;600;700;800&display=swap');

*, *::before, *::after {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

.page {
  display: grid;
  grid-template-columns: 1.25fr 1fr;
  width: 100vw;
  height: 100vh;
  background: 
    radial-gradient(circle at 5% 10%, rgba(99, 102, 241, 0.12) 0%, transparent 40%),
    radial-gradient(circle at 85% 85%, rgba(59, 130, 246, 0.12) 0%, transparent 45%),
    linear-gradient(135deg, #f0f4ff 0%, #f7f9fc 50%, #edf1fb 100%);
  font-family: 'Inter', 'Outfit', -apple-system, BlinkMacSystemFont, 'PingFang SC', sans-serif;
  color: #1f2937;
  overflow: hidden;
}

/* ═══════════════════════════════════
   左侧预览区
 ═══════════════════════════════════ */
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
  width: 600px; height: 600px;
  border-radius: 50%;
  border: 1px solid rgba(99, 102, 241, 0.05);
  top: -200px; left: 30%;
  pointer-events: none;
}

/* 顶部品牌 */
.brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: #2f5bff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 8px 20px -8px rgba(47, 91, 255, 0.7);
  flex-shrink: 0;
}
.brand-icon svg {
  width: 18px;
  height: 18px;
}

.brand-name {
  font-size: 16px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.01em;
  font-family: 'Outfit', sans-serif;
}

/* 主标题区 */
.hero {
}

.hero-title {
  font-size: clamp(1.8rem, 2.8vw, 2.6rem);
  font-weight: 800;
  letter-spacing: -0.03em;
  color: #0f172a;
  line-height: 1.2;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #0f172a 30%, #312e81 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hero-sub {
  font-size: clamp(0.8rem, 1vw, 0.95rem);
  color: #475569;
  font-weight: 500;
  line-height: 1.5;
}

/* 4大特性卡网格 */
.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: rgba(255, 255, 255, 0.45);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 12px;
  padding: 12px 14px;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  text-align: left;
}
.feature-item:hover {
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.75);
  box-shadow: 0 10px 25px -10px rgba(47, 91, 255, 0.1);
}

.feature-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: #eff6ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2f5bff;
  flex-shrink: 0;
}
.feature-icon svg {
  width: 16px;
  height: 16px;
}

.feature-meta h3 {
  font-size: 13px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 2px;
}
.feature-meta p {
  font-size: 11px;
  color: #64748b;
  line-height: 1.35;
}

/* ── 人脉与建议毛玻璃卡片 ── */
.preview-card {
  background: rgba(255, 255, 255, 0.45);
  border: 1px solid rgba(255, 255, 255, 0.65);
  border-radius: 20px;
  box-shadow: 0 20px 40px -15px rgba(30, 50, 100, 0.06);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  display: grid;
  grid-template-columns: 1.15fr 1fr;
  padding: 16px 20px;
  gap: 20px;
  align-items: center;
  flex: 1;
  min-height: 0;
  z-index: 10;
}

/* 左侧人脉图 */
.network-diagram {
  position: relative;
  width: 220px;
  height: 250px;
  margin: 0 auto;
}

.network-lines {
  position: absolute;
  top: 0; left: 0;
  width: 100%; height: 100%;
  z-index: 1;
  pointer-events: none;
}

.node {
  position: absolute;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 2;
}
.node img {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  border: 2px solid #fff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  object-fit: cover;
}
.node span {
  font-size: 11px;
  font-weight: 700;
  color: #475569;
  margin-top: 4px;
  background: rgba(255, 255, 255, 0.85);
  padding: 1px 6px;
  border-radius: 99px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.node-center {
  top: 108px; left: 88px;
  width: 44px; height: 44px;
  border-radius: 50%;
  background: #4f46e5;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 20px rgba(79, 70, 229, 0.3);
  border: 3px solid #fff;
}
.node-center svg {
  width: 18px;
  height: 18px;
}

.node-top { top: 10px; left: 89px; }
.node-left { top: 105px; left: 5px; }
.node-right { top: 105px; left: 165px; }
.node-bottom { top: 195px; left: 89px; }

/* 右侧 AI 建议 */
.ai-suggestions {
  display: flex;
  flex-direction: column;
  text-align: left;
  min-height: 0;
  height: 100%;
}

.suggestions-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13.5px;
  font-weight: 800;
  color: #312e81;
  margin-bottom: 12px;
  flex-shrink: 0;
}
.suggestions-title svg {
  width: 15px;
  height: 15px;
  color: #4f46e5;
}

.suggestion-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
  overflow-y: auto;
  padding-right: 4px;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 8px 12px;
  transition: all 0.2s ease;
}
.suggestion-item:hover {
  transform: translateX(4px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 4px 15px rgba(0,0,0,0.03);
}

.s-icon {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.s-icon svg {
  width: 14px;
  height: 14px;
}
.s-icon-orange { background: #fff7ed; color: #ea580c; }
.s-icon-green { background: #f0fdf4; color: #16a34a; }
.s-icon-blue { background: #eff6ff; color: #2563eb; }
.s-icon-purple { background: #faf5ff; color: #9333ea; }

.s-content {
  flex: 1;
  min-width: 0;
}

.s-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1px;
  gap: 6px;
}

.s-name {
  font-size: 11.5px;
  font-weight: 700;
  color: #1e293b;
}

.s-badge {
  font-size: 10px;
  font-weight: 700;
  padding: 1px 6px;
  border-radius: 99px;
  white-space: nowrap;
}
.badge-orange { background: #ffedd5; color: #ea580c; }
.badge-green { background: #dcfce7; color: #15803d; }
.badge-blue { background: #dbeafe; color: #1d4ed8; }
.badge-purple { background: #f3e8ff; color: #7e22ce; }

.s-desc {
  font-size: 10.5px;
  color: #64748b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: left;
}

/* ── 用户评价 (Testimonial) ── */
.testimonial-card {
  display: flex;
  gap: 12px;
  background: rgba(255, 255, 255, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 14px;
  padding: 10px 16px;
  position: relative;
  text-align: left;
}

.quote-icon {
  font-size: 36px;
  color: rgba(99, 102, 241, 0.25);
  font-family: Georgia, serif;
  line-height: 1;
  height: 20px;
  margin-top: -8px;
  flex-shrink: 0;
}

.testimonial-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.testimonial-text {
  font-size: 12px;
  color: #475569;
  line-height: 1.5;
  font-weight: 500;
}

.testimonial-author {
  font-size: 11px;
  color: #64748b;
  font-weight: 700;
}

/* 页脚 */
.preview-footer {
  font-size: 11px;
  color: #94a3b8;
  font-weight: 500;
  margin-top: auto;
  flex-shrink: 0;
  text-align: left;
}

/* ═══════════════════════════════════
   右侧表单区
 ═══════════════════════════════════ */
.auth-col {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 40px;
  overflow-y: auto;
  height: 100%;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 20px 40px -10px rgba(15, 23, 42, 0.08);
  padding: 20px 30px;
  margin: auto 0;
  position: relative;
  text-align: left;
}

/* 头部图标 */
.auth-avatar-header {
  width: 40px; height: 40px;
  border-radius: 50%;
  background: #eff6ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2f5bff;
  margin: 0 auto 6px;
  box-shadow: 0 8px 16px -8px rgba(47, 91, 255, 0.5);
}
.auth-avatar-header svg {
  width: 20px; height: 20px;
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
  margin-bottom: 12px;
}

/* 错误提示 */
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
  width: 16px; height: 16px;
  flex-shrink: 0;
}

/* 表单 */
.form-group {
  margin-bottom: 8px;
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
  height: 36px;
  padding: 0 36px;
  border-radius: 8px;
  border: 1.5px solid #e2e8f0;
  background: #fff;
  font-family: inherit;
  font-size: 12.5px;
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
  top: 50%; transform: translateY(-50%);
  width: 18px; height: 18px;
  color: #94a3b8;
  left: 12px;
  pointer-events: none;
}
.toggle-pw {
  position: absolute;
  top: 50%; transform: translateY(-50%);
  width: 18px; height: 18px;
  color: #94a3b8;
  right: 12px;
  cursor: pointer;
  transition: color 0.15s ease;
}
.toggle-pw:hover {
  color: #475569;
}

/* 服务协议勾选 */
.agreement-row {
  margin: 8px 0 12px 0;
}

.checkbox-container {
  display: flex;
  align-items: flex-start;
  position: relative;
  padding-left: 24px;
  cursor: pointer;
  font-size: 12px;
  color: #64748b;
  user-select: none;
  line-height: 16px;
  text-align: left;
}

.checkbox-container input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0; width: 0;
}

.checkmark {
  position: absolute;
  top: 0; left: 0;
  height: 16px; width: 16px;
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
  left: 4px; top: 1px;
  width: 4px; height: 8px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.agreement-text a {
  color: #2f5bff;
  text-decoration: none;
  font-weight: 600;
}
.agreement-text a:hover {
  text-decoration: underline;
}

/* 注册按钮 */
.btn-register {
  width: 100%;
  height: 38px;
  border-radius: 8px;
  border: none;
  background: linear-gradient(135deg, #4f46e5 0%, #2f5bff 100%);
  color: #fff;
  font-family: inherit;
  font-size: 13.5px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 8px 16px -8px rgba(47, 91, 255, 0.4);
  transition: all 0.2s ease;
  margin-bottom: 12px;
  display: flex; align-items: center; justify-content: center;
}
.btn-register:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 10px 20px -6px rgba(47, 91, 255, 0.5);
  background: linear-gradient(135deg, #4338ca 0%, #1d4ed8 100%);
}
.btn-register:active:not(:disabled) {
  transform: translateY(1px);
}
.btn-register:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* 去登录行 */
.login-row {
  text-align: center;
  font-size: 13px;
  font-weight: 600;
  color: #94a3b8;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-bottom: 12px;
  position: relative;
}
.login-row::before, .login-row::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e2e8f0;
}
.login-row span, .login-row a {
  background: #fff;
  padding: 0 8px;
  z-index: 1;
}
.login-row a {
  color: #2f5bff;
  font-weight: 700;
  text-decoration: none;
  margin-left: -8px;
}
.login-row a:hover {
  text-decoration: underline;
}

/* 安全保证 */
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

/* 成功提示弹窗 */
.success-modal {
  position: fixed;
  top: 0; left: 0;
  width: 100vw; height: 100vh;
  background: rgba(15, 23, 42, 0.3);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease-out;
}

.modal-content {
  background: #fff;
  border-radius: 16px;
  padding: 32px 40px;
  text-align: center;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.08), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  max-width: 320px;
  width: 90%;
  animation: scaleIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.success-icon-wrap {
  width: 56px; height: 56px;
  background: #dcfce7;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  margin: 0 auto 16px;
  color: #22c55e;
}
.success-icon-wrap svg {
  width: 26px; height: 26px;
}

.modal-content h3 {
  font-size: 19px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 8px;
}
.modal-content p {
  font-size: 13.5px;
  color: #64748b;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes scaleIn {
  from { transform: scale(0.95); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

.code-row {
  display: flex;
  gap: 12px;
  align-items: center;
}

.code-row .input-wrap {
  flex: 1;
}

.code-row :deep(.send-code-btn) {
  flex-shrink: 0;
  width: 110px;
  height: 36px;
}

/* 响应式 */
@media (max-width: 900px) {
  .page {
    grid-template-columns: 1fr;
    height: auto;
    overflow-y: auto;
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

.feedback-msg {
  margin-top: 3px;
  font-size: 11px;
  display: flex;
  align-items: center;
}
.feedback-msg span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
}
.feedback-msg .valid {
  color: #10b981;
}
.feedback-msg .invalid {
  color: #ef4444;
}
.feedback-icon {
  width: 12px;
  height: 12px;
  flex-shrink: 0;
}
</style>
