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
              <span class="settings-desc">{{ (user?.username || 'ethan').toLowerCase() }}@example.com</span>
            </div>
            <button class="btn btn-secondary btn-sm" @click="tipFeature('修改邮箱')">修改邮箱</button>
          </div>

          <div class="settings-row">
            <div class="settings-info">
              <span class="settings-title">手机号码</span>
              <span class="settings-desc">138 0013 8000</span>
            </div>
            <button class="btn btn-secondary btn-sm" @click="tipFeature('修改手机')">修改手机</button>
          </div>

          <div class="settings-row">
            <div class="settings-info">
              <span class="settings-title">密码管理</span>
              <span class="settings-desc">为了您的账号安全，请定期更换复杂密码</span>
            </div>
            <button class="btn btn-secondary btn-sm" @click="tipFeature('修改密码')">修改密码</button>
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
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { ElNotification, ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { uploadUserAvatar } from '@/api/upload'

const router = useRouter()
const authStore = useAuthStore()
const { user } = storeToRefs(authStore)

const fileInputRef = ref<HTMLInputElement | null>(null)
const uploading = ref<boolean>(false)

const defaultAvatar = 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=120&auto=format&fit=crop&q=60'

// 计算展示的头像 URL，支持后端相对地址拼接
const avatarUrl = computed(() => {
  if (user.value?.avatarUrl) {
    if (user.value.avatarUrl.startsWith('http')) {
      return user.value.avatarUrl
    }
    return `http://localhost:8080${user.value.avatarUrl}`
  }
  return ''
})

// 触发隐藏的 file 选择器
function triggerFileInput() {
  if (uploading.value) return
  fileInputRef.value?.click()
}

// 演示占位气泡
function tipFeature(name: string) {
  ElMessage.info(`${name}功能属于 Phase 2 账号管理范围，目前已完美完成 UI 视觉占位`)
}

// 退出登录
function handleLogout() {
  authStore.logout()
  ElMessage.success('您已安全退出系统')
  router.push('/login')
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

@media (max-width: 640px) {
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
