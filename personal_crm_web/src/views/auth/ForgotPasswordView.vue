<template>
  <div class="forgot-page">
    <!-- ══ 页头 (左上角固定返回登录，无突兀状态标签) ══ -->
    <header class="forgot-header">
      <a href="#" class="header-back-link" @click.prevent="goBackToLogin">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
          <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
        </svg>
        返回登录
      </a>
    </header>

    <!-- ══ 主体内容区 ══ -->
    <main class="forgot-main">
      <!-- ══ 左侧：账号恢复中心说明与静态清单 ══ -->
      <section class="recovery-info-panel">
        <!-- 科技感极淡背景 (透明度调低至约 4% ~ 5%，充当背景) -->
        <div class="glow-sphere"></div>
        <div class="tech-bg-circles">
          <svg viewBox="0 0 200 200">
            <circle cx="100" cy="100" r="80" stroke="rgba(37, 99, 235, 0.05)" stroke-width="1" fill="none" stroke-dasharray="4,4" />
            <circle cx="100" cy="100" r="55" stroke="rgba(37, 99, 235, 0.03)" stroke-width="1.2" fill="none" />
            <line x1="20" y1="100" x2="180" y2="100" stroke="rgba(37, 99, 235, 0.015)" stroke-width="1"/>
            <line x1="100" y1="20" x2="100" y2="180" stroke="rgba(37, 99, 235, 0.015)" stroke-width="1"/>
            <circle cx="40" cy="60" r="1.5" fill="rgba(37, 99, 235, 0.08)" />
            <circle cx="160" cy="140" r="2" fill="rgba(37, 99, 235, 0.06)" />
          </svg>
        </div>
        <!-- 极淡浮动盾牌 (5% 透明度) -->
        <div class="floating-shield-bg">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
          </svg>
        </div>

        <!-- 恢复中心正文 -->
        <div class="recovery-content">
          <div class="recovery-shield-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
            </svg>
          </div>
          <h1 class="recovery-title">账号恢复中心</h1>
          <p class="recovery-desc">为了保护您的账号安全，恢复流程包含以下步骤。</p>
          
          <div class="recovery-divider"></div>
          
          <!-- 静态步骤清单 (去重，职责清晰) -->
          <ul class="recovery-steps-list">
            <li>
              <span class="status-indicator">✓</span>
              <span class="step-text">验证身份</span>
            </li>
            <li>
              <span class="status-indicator">✓</span>
              <span class="step-text">设置新密码</span>
            </li>
            <li>
              <span class="status-indicator">✓</span>
              <span class="step-text">完成恢复</span>
            </li>
          </ul>
        </div>
      </section>

      <!-- ══ 右侧表单白卡片 (高度自适应紧凑) ══ -->
      <section class="forgot-card-wrap">
        <div class="forgot-card">
          <!-- 步骤切换过渡动画 -->
          <transition name="slide-fade" mode="out-in">
            <!-- ══ 步骤 1: 验证邮箱 ══ -->
            <div v-if="step === 1" key="step1" class="step-container">
              <!-- 顶部“安全恢复”与副描述 (三层层级) -->
              <div class="safety-recovery-tag">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                </svg>
                <span>安全恢复</span>
              </div>
              <h2 class="title">忘记密码</h2>
              <p class="title-desc">请输入注册邮箱完成身份验证</p>

              <!-- 自定义 Progress 连线进度条 -->
              <div class="progress-bar-wrap">
                <div class="progress-track">
                  <div class="progress-fill" :style="{ width: '0%' }"></div>
                </div>
                <div class="progress-nodes">
                  <div class="progress-node active">
                    <div class="node-dot">1</div>
                    <span class="node-label">验证身份</span>
                  </div>
                  <div class="progress-node">
                    <div class="node-dot">2</div>
                    <span class="node-label">设置密码</span>
                  </div>
                </div>
              </div>

              <!-- 表单输入 -->
              <div class="form-body">
                <div class="form-group">
                  <label class="form-label">注册邮箱</label>
                  <div class="input-wrap" :class="{ focus: focusedField === 'email' }">
                    <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                      <polyline points="22,6 12,13 2,6"/>
                    </svg>
                    <input
                      v-model="forgotForm.email"
                      type="text"
                      placeholder="请输入您的注册邮箱"
                      @focus="focusedField = 'email'"
                      @blur="focusedField = ''"
                    />
                  </div>
                  <!-- 验证码成功发送提示 -->
                  <p v-if="codeSent" class="send-success-tip">
                    验证码已发送至 {{ forgotForm.email }}
                  </p>
                </div>

                <div class="form-group">
                  <label class="form-label">验证码</label>
                  <div class="code-row">
                    <div class="input-wrap" :class="{ focus: focusedField === 'code' }">
                      <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                      </svg>
                      <input
                        v-model="forgotForm.code"
                        type="text"
                        placeholder="请输入 6 位验证码"
                        maxlength="6"
                        @focus="focusedField = 'code'"
                        @blur="focusedField = ''"
                      />
                    </div>
                    <button
                      class="send-btn"
                      type="button"
                      :disabled="countdown > 0 || codeLoading"
                      @click="handleSendCode"
                    >
                      {{ codeLoading ? '发送中...' : countdown > 0 ? `重新发送 (${countdown}s)` : '发送验证码' }}
                    </button>
                  </div>
                </div>

                <!-- 安心感提示文案 -->
                <div class="safety-notes">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/>
                  </svg>
                  <div class="notes-content">
                    <p>验证码有效期：5 分钟</p>
                    <p>不会修改您的账号信息，仅用于身份验证。</p>
                    <p>整个恢复过程预计不到一分钟。</p>
                  </div>
                </div>

                <button class="btn-next" :disabled="loading" @click="handleVerifyCode">
                  {{ loading ? '正在验证...' : '下一步' }}
                </button>
              </div>
            </div>

            <!-- ══ 步骤 2: 重置密码 ══ -->
            <div v-else-if="step === 2" key="step2" class="step-container">
              <!-- 顶部“安全恢复”与描述 (三层层级) -->
              <div class="safety-recovery-tag">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                </svg>
                <span>安全恢复</span>
              </div>
              <h2 class="title">重置密码</h2>
              <p class="title-desc">请设置您的新密码，确保账户安全</p>

              <!-- 自定义 Progress 连线进度条 (步骤1为绿勾) -->
              <div class="progress-bar-wrap">
                <div class="progress-track">
                  <div class="progress-fill" :style="{ width: '50%' }"></div>
                </div>
                <div class="progress-nodes">
                  <div class="progress-node active done">
                    <div class="node-dot">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3.2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="20 6 9 17 4 12"/>
                      </svg>
                    </div>
                    <span class="node-label">验证身份</span>
                  </div>
                  <div class="progress-node active">
                    <div class="node-dot">2</div>
                    <span class="node-label">设置密码</span>
                  </div>
                </div>
              </div>

              <!-- 表单输入 -->
              <div class="form-body">
                <div class="form-group">
                  <label class="form-label">新密码</label>
                  <div class="input-wrap" :class="{ focus: focusedField === 'newPassword' }">
                    <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                      <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                    </svg>
                    <input
                      v-model="forgotForm.newPassword"
                      :type="showNewPassword ? 'text' : 'password'"
                      placeholder="请输入新密码，至少 8 位"
                      @focus="focusedField = 'newPassword'"
                      @blur="focusedField = ''"
                    />
                    <svg
                      class="toggle-pw"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      @click="showNewPassword = !showNewPassword"
                    >
                      <path v-if="showNewPassword" d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line v-if="showNewPassword" x1="1" y1="1" x2="23" y2="23"/>
                      <path v-else d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle v-if="!showNewPassword" cx="12" cy="12" r="3"/>
                    </svg>
                  </div>
                </div>

                <div class="form-group">
                  <label class="form-label">确认新密码</label>
                  <div class="input-wrap" :class="{ focus: focusedField === 'confirmPassword' }">
                    <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                      <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                    </svg>
                    <input
                      v-model="forgotForm.confirmPassword"
                      :type="showConfirmPassword ? 'text' : 'password'"
                      placeholder="请再次输入新密码"
                      @focus="focusedField = 'confirmPassword'"
                      @blur="focusedField = ''"
                    />
                    <svg
                      class="toggle-pw"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      @click="showConfirmPassword = !showConfirmPassword"
                    >
                      <path v-if="showConfirmPassword" d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line v-if="showConfirmPassword" x1="1" y1="1" x2="23" y2="23"/>
                      <path v-else d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle v-if="!showConfirmPassword" cx="12" cy="12" r="3"/>
                    </svg>
                  </div>
                </div>

                <!-- 密码强度实时判定列表 -->
                <div class="pwd-checker">
                  <div class="check-item" :class="{ success: isMinLength }">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                      <polyline points="20 6 9 17 4 12"/>
                    </svg>
                    <span>至少 8 个字符</span>
                  </div>
                  <div class="check-item" :class="{ success: hasLettersAndNumbers }">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                      <polyline points="20 6 9 17 4 12"/>
                    </svg>
                    <span>包含字母和数字</span>
                  </div>
                  <div class="check-item" :class="{ success: hasCaseVariation }">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                      <polyline points="20 6 9 17 4 12"/>
                    </svg>
                    <span>区分大小写（可选）</span>
                  </div>
                </div>

                <button class="btn-next" :disabled="loading || !isStrengthValid" @click="handleResetPassword">
                  {{ loading ? '正在重置...' : '重置密码' }}
                </button>
              </div>
            </div>

            <!-- ══ 步骤 3: 重置成功 (3s 自动倒计时跳转) ══ -->
            <div v-else key="step3" class="step-container result-container">
              <!-- 大号绿色成功勾状态图标 -->
              <div class="success-icon-wrap">
                <div class="success-circle">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="20 6 9 17 4 12"/>
                  </svg>
                </div>
                <!-- 闪烁粒子背景 -->
                <div class="sparkles">
                  <span class="sparkle sp-1"></span>
                  <span class="sparkle sp-2"></span>
                  <span class="sparkle sp-3"></span>
                  <span class="sparkle sp-4"></span>
                </div>
              </div>

              <h2 class="title">重置成功</h2>
              <p class="subtitle">您的密码已成功重置，现在可以使用新密码登录您的账户了</p>

              <div class="form-body">
                <button class="btn-next" @click="goBackToLogin">
                  立即登录 ({{ jumpCountdown }}s)
                </button>
              </div>
            </div>
          </transition>
        </div>
      </section>
    </main>

    <!-- ══ 页脚 (帮助提示邮箱极简排版) ══ -->
    <footer class="forgot-footer">
      <span>需要帮助？<a href="mailto:support@personalcrm.com">support@personalcrm.com</a></span>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { sendEmailCode, verifyEmailCode, resetPassword } from '@/api/auth'

const router = useRouter()
const route = useRoute()

// 步骤控制: 1 = 邮箱验证, 2 = 重置密码, 3 = 重置成功
const step = ref<1 | 2 | 3>(1)
const loading = ref<boolean>(false)
const focusedField = ref<string>('')

// 验证码倒计时控制
const countdown = ref<number>(0)
const codeLoading = ref<boolean>(false)
const codeSent = ref<boolean>(false)
let timer: ReturnType<typeof setInterval> | null = null

// 自动跳转 3s 计时器
const jumpCountdown = ref<number>(3)
let jumpTimer: ReturnType<typeof setInterval> | null = null

// 明暗文切换
const showNewPassword = ref<boolean>(false)
const showConfirmPassword = ref<boolean>(false)

// 表单数据
const forgotForm = reactive({
  email: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
})

onMounted(() => {
  // 若登录页传递了邮箱 query 进来，自动回填
  if (route.query.email && typeof route.query.email === 'string') {
    forgotForm.email = route.query.email
  }
})

// 计算属性：对邮箱进行安全性脱敏展示
const maskedEmail = computed(() => {
  const email = forgotForm.email.trim()
  if (!email) return ''
  const parts = email.split('@')
  if (parts.length !== 2) return email
  const name = parts[0]
  const domain = parts[1]
  if (!name || !domain) return email
  if (name.length <= 3) {
    return name.substring(0, 1) + '***@' + domain
  }
  return name.substring(0, 3) + '***@' + domain
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
  if (jumpTimer) {
    clearInterval(jumpTimer)
    jumpTimer = null
  }
})

// 密码强度检测计算属性
const isMinLength = computed(() => forgotForm.newPassword.length >= 8)
const hasLettersAndNumbers = computed(() => {
  const pwd = forgotForm.newPassword
  return /[A-Za-z]/.test(pwd) && /[0-9]/.test(pwd)
})
const hasCaseVariation = computed(() => {
  const pwd = forgotForm.newPassword
  return /[a-z]/.test(pwd) && /[A-Z]/.test(pwd)
})
// 最低满足强度判定（至少8位，同时包含字母和数字）
const isStrengthValid = computed(() => isMinLength.value && hasLettersAndNumbers.value)

// ══ 监听步骤变化，成功后自动开启跳转定时器 ══
watch(step, (newStep) => {
  if (newStep === 3) {
    startJumpCountdown()
  }
})

const startJumpCountdown = () => {
  jumpCountdown.value = 3
  if (jumpTimer) clearInterval(jumpTimer)
  jumpTimer = setInterval(() => {
    jumpCountdown.value--
    if (jumpCountdown.value <= 0) {
      if (jumpTimer) clearInterval(jumpTimer)
      goBackToLogin()
    }
  }, 1000)
}

// ══ 触发发送验证码 ══
const handleSendCode = async () => {
  const emailVal = forgotForm.email.trim()
  if (!emailVal) {
    ElMessage.warning('请输入注册邮箱')
    return
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(emailVal)) {
    ElMessage.warning('请输入正确的邮箱格式')
    return
  }

  codeLoading.value = true
  try {
    await sendEmailCode({
      email: emailVal,
      purpose: 'RESET_PASSWORD'
    })
    ElMessage.success('验证码已发送至您的邮箱')
    codeSent.value = true
    startCountdown(60)
  } catch (error: any) {
    console.error('发送验证码失败:', error)
  } finally {
    codeLoading.value = false
  }
}

// 倒计时工具
const startCountdown = (seconds: number) => {
  if (timer) clearInterval(timer)
  countdown.value = seconds
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      if (timer) clearInterval(timer)
      timer = null
    }
  }, 1000)
}

// ══ 步骤 1：下一步验证验证码 ══
const handleVerifyCode = async () => {
  const emailVal = forgotForm.email.trim()
  const codeVal = forgotForm.code.trim()

  if (!emailVal) {
    ElMessage.warning('请输入注册邮箱')
    return
  }
  if (!codeVal || codeVal.length !== 6) {
    ElMessage.warning('请输入 6 位数字验证码')
    return
  }

  loading.value = true
  try {
    await verifyEmailCode({
      email: emailVal,
      code: codeVal,
      purpose: 'RESET_PASSWORD'
    })
    step.value = 2
  } catch (error: any) {
    console.error('验证验证码失败:', error)
  } finally {
    loading.value = false
  }
}

// ══ 步骤 2：提交重置密码 ══
const handleResetPassword = async () => {
  const newPw = forgotForm.newPassword
  const confirmPw = forgotForm.confirmPassword

  if (!isStrengthValid.value) {
    ElMessage.warning('密码强度不符合要求')
    return
  }
  if (newPw !== confirmPw) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  loading.value = true
  try {
    await resetPassword({
      email: forgotForm.email.trim(),
      code: forgotForm.code.trim(),
      newPassword: newPw
    })
    ElMessage.success('密码重置成功')
    step.value = 3
  } catch (error: any) {
    console.error('重置密码失败:', error)
  } finally {
    loading.value = false
  }
}

// 导航逻辑
const goBackToLogin = () => {
  const emailToPass = step.value === 3 ? forgotForm.email.trim() : (route.query.email || '')
  router.push({ name: 'login', query: { email: emailToPass } })
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Outfit:wght@400;500;600;700&display=swap');

.forgot-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  width: 100vw;
  background:
    radial-gradient(ellipse 60% 70% at 20% 50%, rgba(255, 255, 255, 0.85) 0%, transparent 100%),
    linear-gradient(135deg, #eef3ff 0%, #f0f4ff 50%, #edf1fb 100%);
  font-family: 'Inter', 'Outfit', -apple-system, BlinkMacSystemFont, 'PingFang SC', sans-serif;
  color: #1f2937;
  box-sizing: border-box;
  overflow-x: hidden;
}

/* 页头样式 (单左上角操作，开阔纯净) */
.forgot-header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 24px 8vw 12px;
}

.header-back-link {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #4b5563;
  text-decoration: none;
  transition: all 0.2s ease;
  user-select: none;
}

.header-back-link svg {
  width: 16px;
  height: 16px;
  transition: transform 0.2s ease;
}

.header-back-link:hover {
  color: #2563eb;
}

.header-back-link:hover svg {
  transform: translateX(-4px);
}

/* 布局主体 */
.forgot-main {
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: center;
  padding: 20px 8vw 60px;
  gap: 8vw;
}

/* ══ 左侧：账号恢复中心说明 ══ */
.recovery-info-panel {
  flex: 1.1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  max-width: 480px;
  min-height: 380px;
  text-align: left;
  z-index: 10;
}

/* 科技背景 (极弱化至 4% ~ 5% 透明度) */
.glow-sphere {
  position: absolute;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.08) 0%, rgba(96, 165, 250, 0.02) 60%, transparent 100%);
  border-radius: 50%;
  filter: blur(30px);
  z-index: 1;
  left: -50px;
  pointer-events: none;
}

.tech-bg-circles {
  position: absolute;
  width: 320px;
  height: 320px;
  z-index: 2;
  left: -20px;
  pointer-events: none;
  opacity: 0.9;
}

.tech-bg-circles svg {
  width: 100%;
  height: 100%;
}

.floating-shield-bg {
  position: absolute;
  right: 0;
  top: 10%;
  width: 180px;
  height: 180px;
  color: #2563eb;
  opacity: 0.04;
  z-index: 2;
  pointer-events: none;
  animation: floatIllustration 8s infinite alternate ease-in-out;
}

.floating-shield-bg svg {
  width: 100%;
  height: 100%;
}

/* 恢复中心正文 */
.recovery-content {
  position: relative;
  z-index: 5;
  padding-left: 10px;
}

.recovery-shield-icon {
  width: 48px;
  height: 48px;
  background: #eff6ff;
  border: 1.5px solid #dbeafe;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2563eb;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.05);
}

.recovery-shield-icon svg {
  width: 22px;
  height: 22px;
}

.recovery-title {
  font-size: 30px;
  font-weight: 800;
  color: #1e293b;
  margin: 0 0 12px 0;
  letter-spacing: -0.8px;
}

.recovery-desc {
  font-size: 14.5px;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 28px 0;
}

.recovery-divider {
  width: 100%;
  height: 1px;
  background: linear-gradient(90deg, rgba(229, 231, 235, 0.6) 0%, rgba(229, 231, 235, 0.05) 100%);
  margin-bottom: 28px;
}

/* 静态步骤清单 (职责分明，只展示故事说明) */
.recovery-steps-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.recovery-steps-list li {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14.5px;
  font-weight: 550;
  color: #64748b;
}

.recovery-steps-list li .status-indicator {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #eff6ff;
  border: 1.5px solid #dbeafe;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  color: #2563eb;
  font-weight: bold;
}

/* ══ 右侧表单白卡片 ══ */
.forgot-card-wrap {
  flex: 0 0 440px;
  flex-shrink: 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.forgot-card {
  width: 100%;
  background: rgba(255, 255, 255, 0.98);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 20px;
  box-shadow: 
    0 4px 6px -1px rgba(0, 0, 0, 0.01),
    0 15px 35px -5px rgba(37, 99, 235, 0.05);
  padding: 32px 32px 28px;
  box-sizing: border-box;
  overflow: hidden;
  /* 严格间距，卡片高度紧凑完美 (500px ~ 560px) */
  min-height: 480px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.step-container {
  width: 100%;
}

.safety-recovery-tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 11px;
  font-weight: 700;
  color: #2563eb;
  text-transform: uppercase;
  letter-spacing: 0.8px;
  margin-bottom: 6px;
}

.safety-recovery-tag svg {
  width: 12px;
  height: 12px;
}

.title {
  font-size: 23px;
  font-weight: 750;
  color: #0f172a;
  text-align: left;
  margin: 0 0 4px 0;
  letter-spacing: -0.5px;
}

.title-desc {
  font-size: 13.5px;
  color: #64748b;
  text-align: left;
  margin: 0 0 20px 0;
}

/* 真正的 Progress 进度连线条 */
.progress-bar-wrap {
  position: relative;
  margin-bottom: 24px;
  padding-top: 10px;
}

.progress-track {
  position: absolute;
  top: 20px;
  left: 25px;
  right: 25px;
  height: 2.5px;
  background: #f1f5f9;
  z-index: 1;
  border-radius: 2px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6 0%, #10b981 100%);
  width: 0%;
  transition: width 0.4s cubic-bezier(0.25, 1, 0.5, 1);
  border-radius: 2px;
}

.progress-nodes {
  display: flex;
  justify-content: space-between;
  position: relative;
  z-index: 2;
}

.progress-node {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 50px;
}

.progress-node .node-dot {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #ffffff;
  border: 2px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 700;
  color: #94a3b8;
  transition: all 0.3s ease;
}

.progress-node .node-label {
  font-size: 11.5px;
  font-weight: 600;
  color: #94a3b8;
  margin-top: 6px;
  white-space: nowrap;
  transition: all 0.3s ease;
}

.progress-node.active .node-dot {
  border-color: #2563eb;
  color: #2563eb;
  background: #eff6ff;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
}

.progress-node.active .node-label {
  color: #2563eb;
}

.progress-node.active.done .node-dot {
  background: #dcfce7;
  border-color: #10b981;
  color: #10b981;
  box-shadow: none;
}

.progress-node.active.done .node-dot svg {
  width: 12px;
  height: 12px;
}

.progress-node.active.done .node-label {
  color: #10b981;
}

/* 表单主体 */
.form-body {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  text-align: left;
}

.form-label {
  font-size: 12.5px;
  font-weight: 600;
  color: #475569;
}

.input-wrap {
  position: relative;
  display: flex;
  align-items: center;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 0 12px;
  transition: all 0.2s ease;
}

.input-wrap.focus {
  background: #ffffff;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.08);
}

.field-icon {
  width: 17px;
  height: 17px;
  color: #94a3b8;
  flex-shrink: 0;
}

.input-wrap input {
  flex: 1;
  border: none;
  background: transparent;
  outline: none;
  height: 40px;
  padding: 0 8px;
  font-size: 13.5px;
  color: #1e293b;
  width: 100%;
}

.input-wrap input::placeholder {
  color: #94a3b8;
}

/* 验证码行 */
.code-row {
  display: flex;
  gap: 10px;
  align-items: stretch;
}

.code-row .input-wrap {
  flex: 1;
}

/* Ghost 细边线框发送验证码按钮 (样式降权，倒计时变灰) */
.send-btn {
  background: transparent;
  border: 1.5px solid #dbeafe;
  color: #2563eb;
  border-radius: 8px;
  padding: 0 14px;
  font-size: 12.5px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.send-btn:hover:not(:disabled) {
  background: #eff6ff;
  border-color: #bfdbfe;
}

.send-btn:disabled {
  background: #f1f5f9;
  border-color: #e2e8f0;
  color: #94a3b8;
  cursor: not-allowed;
  font-weight: 500;
}

.send-success-tip {
  font-size: 11.5px;
  color: #10b981;
  margin: 1px 0 0 0;
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 550;
  animation: slideDown 0.2s ease-out;
}

.send-success-tip::before {
  content: "✓";
  font-weight: bold;
}

/* 安心感提示文案 */
.safety-notes {
  background: #f8fafc;
  border: 1px solid #f1f5f9;
  border-radius: 8px;
  padding: 10px 12px;
  display: flex;
  gap: 8px;
  align-items: flex-start;
  text-align: left;
  margin: 2px 0;
}

.safety-notes svg {
  width: 15px;
  height: 15px;
  color: #3b82f6;
  flex-shrink: 0;
  margin-top: 1px;
}

.notes-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.notes-content p {
  font-size: 11.5px;
  color: #64748b;
  margin: 0;
  line-height: 1.4;
}

.notes-content p:first-child {
  font-weight: 600;
  color: #475569;
}

/* 主提交按钮 */
.btn-next {
  width: 100%;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  border: none;
  color: #ffffff;
  border-radius: 8px;
  height: 42px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba(37, 99, 235, 0.12);
  transition: all 0.2s ease;
  margin-top: 4px;
}

.btn-next:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 14px rgba(37, 99, 235, 0.2);
}

.btn-next:active:not(:disabled) {
  transform: translateY(0);
}

.btn-next:disabled {
  background: #cbd5e1;
  box-shadow: none;
  cursor: not-allowed;
}

/* 眼睛明暗文切换 */
.toggle-pw {
  width: 17px;
  height: 17px;
  color: #94a3b8;
  cursor: pointer;
  transition: color 0.2s ease;
}

.toggle-pw:hover {
  color: #475569;
}

/* 密码强度指示器 */
.pwd-checker {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin: 2px 0 4px;
}

.check-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11.5px;
  color: #94a3b8;
  transition: color 0.2s ease;
}

.check-item svg {
  width: 11px;
  height: 11px;
  color: #cbd5e1;
}

.check-item.success {
  color: #10b981;
}

.check-item.success svg {
  color: #10b981;
}

/* ══ 成功状态样式 ══ */
.result-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.success-icon-wrap {
  position: relative;
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.success-circle {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  box-shadow: 0 8px 16px rgba(16, 185, 129, 0.25);
}

.success-circle svg {
  width: 26px;
  height: 26px;
}

/* 烟花粒子装饰效果 */
.sparkles {
  position: absolute;
  width: 90px;
  height: 90px;
  pointer-events: none;
}

.sparkle {
  position: absolute;
  width: 3.5px;
  height: 3.5px;
  border-radius: 50%;
  background: #10b981;
  opacity: 0.7;
}

.sp-1 { top: 15px; left: 15px; animation: bounceParticle 2s infinite ease-in-out; }
.sp-2 { top: 15px; right: 15px; background: #60a5fa; animation: bounceParticle 2.5s infinite ease-in-out; }
.sp-3 { bottom: 15px; left: 15px; background: #fbbf24; animation: bounceParticle 2.2s infinite ease-in-out; }
.sp-4 { bottom: 15px; right: 15px; animation: bounceParticle 1.8s infinite ease-in-out; }

/* 页脚 (极简化邮箱，极低阅读压力) */
.forgot-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  font-size: 12px;
  color: #94a3b8;
  border-top: 1px solid rgba(229, 231, 235, 0.2);
}

.forgot-footer a {
  color: #2563eb;
  text-decoration: none;
  font-weight: 550;
  margin-left: 4px;
}

.forgot-footer a:hover {
  text-decoration: underline;
}

/* ══ 动画声明 ══ */
@keyframes floatIllustration {
  0% { transform: translateY(0) rotate(0deg); }
  100% { transform: translateY(-8px) rotate(3deg); }
}

@keyframes bounceParticle {
  0%, 100% { transform: scale(1) translateY(0); opacity: 0.4; }
  50% { transform: scale(1.4) translateY(-4px); opacity: 0.9; }
}

@keyframes slideDown {
  from { transform: translateY(-5px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

/* 横向切换动画 */
.slide-fade-enter-active {
  transition: all 0.3s cubic-bezier(0.25, 1, 0.5, 1);
}
.slide-fade-leave-active {
  transition: all 0.2s cubic-bezier(0.25, 1, 0.5, 1);
}
.slide-fade-enter-from {
  transform: translateX(30px);
  opacity: 0;
}
.slide-fade-leave-to {
  transform: translateX(-30px);
  opacity: 0;
}

/* 响应式适配 */
@media (max-width: 900px) {
  .forgot-main {
    flex-direction: column;
    padding: 20px 20px 60px;
    gap: 40px;
  }
  .recovery-info-panel {
    display: none;
  }
  .forgot-card {
    padding: 24px 20px;
    box-shadow: none;
    border: 1px solid #e2e8f0;
    min-height: auto;
  }
  .forgot-header {
    padding: 16px 20px;
  }
  .forgot-footer {
    padding: 16px;
    font-size: 11px;
    text-align: center;
  }
}
</style>
