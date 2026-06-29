<template>
  <div class="settings-container">
    <div class="settings-layout">
      <!-- 个人资料卡片 -->
      <div class="card">
        <h3 class="card-title" style="margin-bottom: 20px; display:flex; align-items:center; gap:8px;">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:24px;height:24px;color:#334155;"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          个人资料
        </h3>
        
        <div class="settings-profile-summary">
          <div class="settings-avatar-wrap" @click="triggerFileInput" style="position:relative; cursor:pointer;">
            <img :src="avatarUrl || defaultAvatar" alt="Avatar" class="settings-avatar">
            <div v-if="uploading" class="avatar-loading-overlay">
              <div class="spinner"></div>
            </div>
          </div>
          <div class="settings-profile-meta">
            <h4 style="font-size:16px; font-weight:700; margin:0;">{{ user?.username ? (user.username.charAt(0).toUpperCase() + user.username.slice(1)) : 'Ethan' }}</h4>
            <span style="font-size:12px; color:var(--text-muted);">系统管理员 · 尊享 Pro 专业版</span>
            <button class="btn btn-secondary btn-sm" style="color:var(--color-primary);border-color:var(--color-primary-border); display:flex; align-items:center; gap:4px; margin-top:4px;" @click="triggerFileInput">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:12px;height:12px;"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/></svg>
              更换头像
            </button>
            <input
              type="file"
              ref="fileInputRef"
              style="display: none;"
              accept="image/png, image/jpeg, image/jpg, image/webp"
              @change="handleFileChange"
            />
          </div>
        </div>

        <!-- 具体字段列表 -->
        <div class="info-list" style="margin-top: 20px;">
          <div class="settings-row">
            <div class="settings-info">
              <span class="settings-title">电子邮箱</span>
              <span class="settings-desc">{{ user?.email || '未绑定电子邮箱' }}</span>
            </div>
            <button class="btn btn-secondary btn-sm" @click="openEmailDialog">修改邮箱</button>
          </div>

          <div class="settings-row">
            <div class="settings-info">
              <span class="settings-title">手机号码</span>
              <span class="settings-desc">{{ user?.phone || '未绑定手机号码' }}</span>
            </div>
            <button class="btn btn-secondary btn-sm" @click="openPhoneDialog">修改手机</button>
          </div>

          <div class="settings-row">
            <div class="settings-info">
              <span class="settings-title">密码管理</span>
              <span class="settings-desc">为了您的账号安全，请定期更换复杂密码</span>
            </div>
            <button class="btn btn-secondary btn-sm" @click="openPasswordDialog">修改密码</button>
          </div>
        </div>
      </div>

      <!-- 敏感操作区卡片 -->
      <div class="card">
        <h3 class="card-title" style="margin-bottom: 20px; color: var(--color-danger-text); display:flex; align-items:center; gap:8px;">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color: var(--color-danger); width:24px;height:24px;">
            <path d="M20 13c0 5-3.5 7.5-7.66 8.95a1 1 0 0 1-.67-.01C7.5 20.5 4 18 4 13V6a1 1 0 0 1 1-1c2 0 4.5-1.2 6.24-2.72a1.17 1.17 0 0 1 1.52 0C14.51 3.81 17 5 19 5a1 1 0 0 1 1 1z"></path>
            <path d="M12 8v4"></path>
            <path d="M12 16h.01"></path>
          </svg>
          敏感操作区
        </h3>

        <div class="settings-row">
          <div class="settings-info">
            <span class="settings-title">安全退出登录</span>
            <span class="settings-desc">安全清除本地缓存并重定向回到登录页面</span>
          </div>
          <button class="btn btn-primary btn-sm" style="background-color: var(--color-danger); border:none;" @click="handleLogout">退出登录</button>
        </div>
      </div>
    </div>

    <!-- 修改邮箱弹窗 -->
    <AppDialog
      v-model="emailDialogVisible"
      title="修改电子邮箱"
      description="请输入新的邮箱地址及验证码，校验成功后生效。"
      confirm-text="保存"
      :loading="submitLoading"
      @confirm="handleUpdateEmail"
    >
      <el-form :model="emailForm" :rules="emailRules" ref="emailFormRef" label-position="top" @submit.prevent="handleUpdateEmail">
        <el-form-item label="新电子邮箱" prop="newEmail">
          <el-input v-model="emailForm.newEmail" placeholder="请输入新电子邮箱" />
        </el-form-item>
        <el-form-item label="6位验证码" prop="code">
          <div style="display: flex; gap: 10px; width: 100%;">
            <el-input v-model="emailForm.code" placeholder="请输入 6 位验证码" maxlength="6" style="flex: 1;" />
            <SendCodeButton
              :email="emailForm.newEmail"
              purpose="CHANGE_EMAIL"
            />
          </div>
        </el-form-item>
      </el-form>
    </AppDialog>

    <!-- 修改手机弹窗 -->
    <AppDialog
      v-model="phoneDialogVisible"
      title="修改手机号码"
      description="请输入新的手机号码，用于接收系统重要通知。"
      confirm-text="保存"
      :loading="submitLoading"
      @confirm="handleUpdatePhone"
    >
      <el-form :model="phoneForm" :rules="phoneRules" ref="phoneFormRef" label-position="top">
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="phoneForm.phone" placeholder="请输入新手机号码" />
        </el-form-item>
      </el-form>
    </AppDialog>

    <!-- 修改密码弹窗 -->
    <AppDialog
      v-model="passwordDialogVisible"
      title="修改密码"
      description="为了您的账号安全，请定期更换复杂密码。"
      confirm-text="保存"
      :loading="submitLoading"
      @confirm="handleUpdatePassword"
    >
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-position="top">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入当前密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入 8 位以上新密码" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
    </AppDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import AppDialog from '@/components/common/AppDialog.vue'
import SendCodeButton from '@/components/SendCodeButton.vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { ElNotification, ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { uploadUserAvatar } from '@/api/upload'
import { resolveAvatarUrl } from '@/utils/avatar'
import { updatePhoneApi, updatePasswordApi, changeEmail } from '@/api/auth'

const router = useRouter()
const authStore = useAuthStore()
const { user } = storeToRefs(authStore)

const fileInputRef = ref<HTMLInputElement | null>(null)
const uploading = ref<boolean>(false)

const defaultAvatar = 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=120&auto=format&fit=crop&q=60'

// 计算展示的头像 URL，支持后端相对地址拼接
const avatarUrl = computed(() => {
  if (user.value?.avatarUrl) {
    return resolveAvatarUrl(user.value.avatarUrl)
  }
  return ''
})

// 触发隐藏的 file 选择器
function triggerFileInput() {
  if (uploading.value) return
  fileInputRef.value?.click()
}

// 退出登录
function handleLogout() {
  authStore.logout()
  ElMessage.success('您已安全退出系统')
  router.push('/login')
}

// 弹窗可见性与状态
const emailDialogVisible = ref(false)
const phoneDialogVisible = ref(false)
const passwordDialogVisible = ref(false)
const submitLoading = ref(false)

const emailFormRef = ref<FormInstance>()
const phoneFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

const emailForm = ref({ newEmail: '', code: '' })
const phoneForm = ref({ phone: '' })
const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

const validateEmailFormat = (rule: any, value: any, callback: any) => {
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  if (value && !emailRegex.test(value)) {
    callback(new Error('请输入正确的邮箱格式'))
  } else {
    callback()
  }
}

// 表单验证规则
const emailRules = ref<FormRules>({
  newEmail: [
    { required: true, message: '请输入新电子邮箱', trigger: 'blur' },
    { validator: validateEmailFormat, trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码必须为 6 位', trigger: 'blur' }
  ]
})

const phoneRules = ref<FormRules>({
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码格式', trigger: 'blur' }
  ]
})

const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的新密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = ref<FormRules>({
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, message: '密码长度不能少于 8 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
})

// 将后端英文错误消息映射为对用户友好的中文提示
function getFriendlyErrorMessage(msg: string): string {
  if (!msg) return '网络连接异常，请重试'
  const message = msg.toLowerCase()
  if (message.includes('invalid old password')) {
    return '原密码输入错误，请重新输入'
  }
  if (message.includes('user profile not found')) {
    return '未找到用户信息，请重新登录'
  }
  if (message.includes('email cannot be blank')) {
    return '电子邮箱不能为空'
  }
  if (message.includes('invalid email format')) {
    return '电子邮箱格式不正确'
  }
  if (message.includes('phone cannot be blank')) {
    return '手机号码不能为空'
  }
  if (message.includes('invalid phone number format')) {
    return '手机号码格式不正确'
  }
  return msg
}

// 开启弹窗并重置表单
function openEmailDialog() {
  emailForm.value.newEmail = ''
  emailForm.value.code = ''
  emailDialogVisible.value = true
}

function openPhoneDialog() {
  phoneForm.value.phone = user.value?.phone || ''
  phoneDialogVisible.value = true
}

function openPasswordDialog() {
  passwordForm.value.oldPassword = ''
  passwordForm.value.newPassword = ''
  passwordForm.value.confirmPassword = ''
  passwordDialogVisible.value = true
}

// 修改邮箱提交
async function handleUpdateEmail() {
  if (!emailFormRef.value) return
  await emailFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitLoading.value = true
        await changeEmail({
          newEmail: emailForm.value.newEmail,
          code: emailForm.value.code
        })
        await authStore.fetchUserProfile()
        ElNotification.success({
          title: '修改成功',
          message: '绑定电子邮箱修改成功！',
          duration: 3000
        })
        emailDialogVisible.value = false
      } catch (error: any) {
        ElNotification.error({
          title: '修改失败',
          message: getFriendlyErrorMessage(error.message),
          duration: 5000
        })
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 修改手机号提交
async function handleUpdatePhone() {
  if (!phoneFormRef.value) return
  await phoneFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitLoading.value = true
        await updatePhoneApi(phoneForm.value.phone)
        await authStore.fetchUserProfile()
        ElNotification.success({
          title: '修改成功',
          message: '您的手机号码已更新！',
          duration: 3000
        })
        phoneDialogVisible.value = false
      } catch (error: any) {
        ElNotification.error({
          title: '修改失败',
          message: getFriendlyErrorMessage(error.message),
          duration: 5000
        })
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 修改密码提交
async function handleUpdatePassword() {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitLoading.value = true
        await updatePasswordApi({
          oldPassword: passwordForm.value.oldPassword,
          newPassword: passwordForm.value.newPassword
        })
        ElNotification.success({
          title: '密码修改成功',
          message: '请使用新密码重新登录',
          duration: 3000
        })
        passwordDialogVisible.value = false
        handleLogout()
      } catch (error: any) {
        ElNotification.error({
          title: '修改失败',
          message: getFriendlyErrorMessage(error.message),
          duration: 5000
        })
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 文件变更并执行上传
async function handleFileChange(event: Event) {
  const target = event.target as HTMLInputElement
  const files = target.files
  if (!files || files.length === 0) return

  const file = files[0]
  if (!file) return

  // 限制格式：仅允许 jpg、jpeg、png、webp
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp']
  if (!allowedTypes.includes(file.type)) {
    ElNotification.error({
      title: '上传失败',
      message: '仅支持 JPG, JPEG, PNG, WEBP 格式的图片文件！',
      duration: 5000
    })
    target.value = ''
    return
  }

  // 限制大小：不超过 2MB
  const maxSize = 2 * 1024 * 1024 // 2MB
  if (file.size > maxSize) {
    ElNotification.error({
      title: '上传失败',
      message: '图片大小不能超过 2MB！',
      duration: 5000
    })
    target.value = ''
    return
  }

  try {
    uploading.value = true
    await uploadUserAvatar(file)
    // 成功后，同步刷新全局用户信息以更新侧边栏头像
    await authStore.fetchUserProfile()
    ElNotification.success({
      title: '上传成功',
      message: '您的个人头像已更新！',
      duration: 3000
    })
  } catch (error: any) {
    console.error('Failed to upload user avatar:', error)
    ElNotification.error({
      title: '头像上传失败',
      message: error.message || '网络连接异常，请重试',
      duration: 5000
    })
  } finally {
    uploading.value = false
    target.value = ''
  }
}
</script>

<style scoped>
.settings-container {
  width: 100%;
}

.settings-layout {
  max-width: 800px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin: 0;
}

.settings-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid var(--border-color);
}

.settings-row:last-child {
  border-bottom: none;
}

.settings-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.settings-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-main);
}

.settings-desc {
  font-size: 12px;
  color: var(--text-muted);
}

.settings-profile-summary {
  display: flex;
  align-items: center;
  gap: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
}

.settings-avatar-wrap {
  width: 88px;
  height: 88px;
  flex-shrink: 0;
  border-radius: var(--radius-full);
  overflow: hidden;
  background: linear-gradient(135deg, #eef4ff 0%, #dbeafe 100%);
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.12);
}

.settings-avatar {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: inherit;
}

.settings-profile-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
}

.avatar-loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(37, 99, 235, 0.2);
  border-radius: 50%;
  border-top-color: #2563eb;
  animation: spin 0.8s infinite linear;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .settings-profile-summary {
    align-items: flex-start;
    gap: 16px;
  }

  .settings-avatar-wrap {
    width: 72px;
    height: 72px;
  }

}

</style>

<style>
/* 全局遮罩定制，调整为 rgba(0,0,0,.35) */
.el-overlay {
  background-color: rgba(0, 0, 0, 0.35) !important;
}

/* ------------------------------------------
   Web 端弹窗 (macOS 风格)
   ------------------------------------------ */
.settings-dialog {
  border-radius: 24px !important;
  box-shadow: 0 32px 64px -16px rgba(0, 0, 0, 0.12) !important;
  border: 1px solid var(--border-color) !important;
  padding: 32px !important;
  width: 90% !important;
  max-width: 440px !important;
  background-color: #ffffff !important;
}

.settings-dialog .el-dialog__header {
  padding: 0 0 12px 0 !important;
  margin-right: 0 !important;
  border-bottom: none !important; /* 取消下划线，显的更轻盈 */
}

.settings-dialog .el-dialog__title {
  font-family: var(--font-sans) !important;
  font-size: 20px !important;
  font-weight: 600 !important;
  color: var(--text-main) !important;
  letter-spacing: -0.01em !important;
}

/* 弹窗说明文字 */
.settings-dialog .dialog-description {
  font-size: 14px !important;
  color: var(--text-muted) !important;
  margin-top: 4px !important;
  margin-bottom: 24px !important;
  line-height: 1.5 !important;
}

.settings-dialog .el-dialog__headerbtn {
  top: 28px !important;
  right: 28px !important;
}

.settings-dialog .el-dialog__body {
  padding: 0 !important; /* 紧贴说明文字下方 */
}

.settings-dialog .el-dialog__footer {
  padding: 16px 0 0 0 !important;
  border-top: none !important; /* 取消下划线，轻量感 */
}

/* 极简表单标签 */
.settings-dialog .el-form-item {
  margin-bottom: 20px !important;
}

.settings-dialog .el-form-item__label {
  font-size: 13px !important;
  font-weight: 600 !important;
  color: var(--text-main) !important;
  margin-bottom: 8px !important;
  padding: 0 !important;
  line-height: 1.2 !important;
}

/* 输入框扁平苹果风 (高度 46px 左右) */
.settings-dialog .el-input__wrapper {
  background-color: #f8fafc !important;
  border-radius: var(--radius-md) !important;
  box-shadow: none !important;
  border: 1px solid #e2e8f0 !important;
  padding: 12px 14px !important; /* 增加 padding */
  transition: all var(--transition-fast) !important;
  height: 46px !important;
}

.settings-dialog .el-input__wrapper.is-focus {
  border-color: var(--color-primary) !important;
  background-color: #ffffff !important;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.08) !important;
}

.settings-dialog .el-input__inner {
  color: var(--text-main) !important;
  font-size: 14px !important;
  font-family: var(--font-sans) !important;
}

/* 按钮定制 */
.settings-dialog .el-button {
  border-radius: var(--radius-md) !important;
  font-weight: 600 !important;
  font-size: 14px !important;
  transition: all var(--transition-fast) !important;
  border: none !important;
}

.settings-dialog .el-button:active {
}

.settings-dialog .el-button:not(.el-button--primary) {
  background-color: #f1f5f9 !important;
  border-color: #f1f5f9 !important;
  color: var(--color-neutral-text) !important;
}

.settings-dialog .el-button:not(.el-button--primary):hover {
  background-color: #e2e8f0 !important;
  color: var(--text-main) !important;
}

.settings-dialog .el-button--primary {
  background-color: var(--color-primary) !important;
  border-color: var(--color-primary) !important;
}

.settings-dialog .el-button--primary:hover {
  background-color: var(--color-primary-hover) !important;
  border-color: var(--color-primary-hover) !important;
}

/* 移动端响应式适配 */
@media (max-width: 768px) {
  .settings-dialog {
    width: 92% !important;
    max-width: 380px !important;
    padding: 20px 20px 24px 20px !important;
  }

  .settings-dialog .el-dialog__headerbtn {
    top: 16px !important;
    right: 16px !important;
  }

  .settings-dialog .el-dialog__body {
    padding: 16px 0 8px 0 !important;
  }

  .settings-dialog .el-dialog__footer {
    padding: 12px 0 0 0 !important;
  }

  /* 底部按钮纵向竖版排列，方便单手操作 */
  .settings-dialog .el-dialog__footer > div {
    flex-direction: column-reverse !important;
    gap: 8px !important;
  }

  .settings-dialog .el-dialog__footer .el-button {
    width: 100% !important;
    margin-left: 0 !important; /* 强制清除 element-plus 默认的左外边距 */
  }
}
</style>
