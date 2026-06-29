<template>
  <el-button
    class="send-code-btn"
    :class="{ 'is-countdown': countdown > 0 }"
    :type="type"
    :size="size"
    :disabled="isButtonDisabled"
    :loading="loading"
    @click="handleSend"
  >
    {{ buttonText }}
  </el-button>
</template>

<script setup lang="ts">
import { ref, computed, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { sendEmailCode } from '@/api/auth'
import type { SendEmailCodeParams } from '@/api/auth'

const props = withDefaults(
  defineProps<{
    email: string
    purpose: 'REGISTER' | 'RESET_PASSWORD' | 'CHANGE_EMAIL'
    disabled?: boolean
    beforeSend?: () => boolean | Promise<boolean>
    type?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | ''
    size?: 'large' | 'default' | 'small'
  }>(),
  {
    disabled: false,
    type: '',
    size: 'default'
  }
)

const emit = defineEmits<{
  (e: 'sent'): void
}>()

const countdown = ref<number>(0)
const loading = ref<boolean>(false)
let timer: ReturnType<typeof setInterval> | null = null

const isButtonDisabled = computed(() => {
  return props.disabled || countdown.value > 0 || loading.value
})

const buttonText = computed(() => {
  if (loading.value) {
    return '发送中...'
  }
  if (countdown.value > 0) {
    return `重新发送 (${countdown.value}s)`
  }
  return '获取验证码'
})

function startTimer(seconds = 60) {
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

async function handleSend() {
  if (isButtonDisabled.value) return

  if (props.beforeSend) {
    const canSend = await props.beforeSend()
    if (!canSend) return
  }

  const emailTrimmed = props.email ? props.email.trim() : ''
  if (!emailTrimmed) {
    ElMessage.warning('请输入电子邮箱')
    return
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(emailTrimmed)) {
    ElMessage.warning('请输入正确的邮箱格式')
    return
  }

  try {
    loading.value = true
    await sendEmailCode({
      email: emailTrimmed,
      purpose: props.purpose
    })
    ElMessage.success('验证码已发送，请注意查收')
    emit('sent')
    startTimer(60)
  } catch (error: any) {
    console.error('Failed to send email code:', error)
  } finally {
    loading.value = false
  }
}

// 暴露手动启动倒计时的 API
defineExpose({
  startTimer
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
})
</script>

<style scoped>
.send-code-btn {
  background: transparent !important;
  border: 1.5px solid #dbeafe !important;
  color: #2563eb !important;
  font-weight: 600 !important;
  border-radius: 8px !important;
  white-space: nowrap !important;
  transition: all 0.2s ease !important;
  font-size: 12.5px !important;
}

.send-code-btn:hover:not(:disabled) {
  background: #eff6ff !important;
  border-color: #bfdbfe !important;
  color: #2563eb !important;
}

.send-code-btn.is-countdown,
.send-code-btn.is-countdown:disabled,
.send-code-btn.is-countdown.is-disabled {
  background-color: #f1f5f9 !important;
  border: 1.5px solid #e2e8f0 !important;
  color: #94a3b8 !important;
  cursor: not-allowed !important;
  opacity: 1 !important;
  box-shadow: none !important;
  font-weight: 500 !important;
}
</style>
