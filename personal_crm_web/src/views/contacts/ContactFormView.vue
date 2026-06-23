<template>
  <div class="contact-form-page-wrapper">
    <!-- 主体内容 -->
    <form @submit.prevent="handleSubmit">
      <div class="card form-layout" v-loading="loading">
        <!-- 左侧：头像上传 -->
        <div class="avatar-upload-card" v-loading="uploading" element-loading-text="上传中...">
          <div class="avatar-upload-placeholder" id="avatarBox" @click="triggerFileUpload">
            <svg v-if="!avatarPreview" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" style="width: 52px; height: 52px; color: #b2bac7;">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
            </svg>
            <img v-else :src="avatarPreview" class="avatar-preview-img" alt="头像预览" style="display: block;">
          </div>
          <input type="file" ref="fileInputRef" accept="image/png, image/jpeg, image/jpg, image/webp" style="display: none;" @change="handleFileChange">
          <button type="button" class="btn btn-secondary btn-sm" @click="triggerFileUpload" style="color:var(--color-primary);border-color:var(--color-primary-border);" :disabled="uploading">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:12px;height:12px;margin-right:4px;display:inline-block;vertical-align:middle;">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/>
            </svg>
            上传头像
          </button>
          <span class="upload-tip">支持 jpg/png/jpeg/webp，最大 2MB</span>
        </div>

        <!-- 右侧：表单区 -->
        <div style="display: flex; flex-direction: column; justify-content: space-between; min-height: 440px;">
          <div class="fields-grid">
            <!-- 姓名 -->
            <div class="form-group" :class="{ 'has-error': errors.name }">
              <label class="form-group-label" for="name">姓名 <span class="required-star">*</span></label>
              <input
                type="text"
                id="name"
                class="input-control"
                v-model="form.name"
                placeholder="请输入联系人姓名"
                @input="clearError('name')"
              >
              <span class="input-error-msg">请输入联系人姓名</span>
            </div>

            <!-- 性别 -->
            <div class="form-group">
              <label class="form-group-label" for="gender">性别</label>
              <select id="gender" class="select-control" v-model="form.gender" style="width: 100%;">
                <option value="">请选择性别</option>
                <option :value="1">男</option>
                <option :value="2">女</option>
              </select>
            </div>

            <!-- 手机号 -->
            <div class="form-group" :class="{ 'has-error': errors.phone }">
              <label class="form-group-label" for="phone">手机号 <span class="required-star">*</span></label>
              <input
                type="text"
                id="phone"
                class="input-control"
                v-model="form.phone"
                placeholder="请输入手机号"
                @input="clearError('phone')"
              >
              <span class="input-error-msg">手机号格式不正确 (必须输入合法数字)</span>
            </div>

            <!-- 邮箱 -->
            <div class="form-group">
              <label class="form-group-label" for="email">邮箱</label>
              <input type="email" id="email" class="input-control" v-model="form.email" placeholder="请输入邮箱">
            </div>

            <!-- QQ -->
            <div class="form-group">
              <label class="form-group-label" for="qq">QQ</label>
              <input type="text" id="qq" class="input-control" v-model="form.qq" placeholder="请输入 QQ 号">
            </div>

            <!-- 微信 -->
            <div class="form-group">
              <label class="form-group-label" for="wechat">微信</label>
              <input type="text" id="wechat" class="input-control" v-model="form.wechat" placeholder="请输入微信号">
            </div>

            <!-- 详细地址 -->
            <div class="form-group col-span-2">
              <label class="form-group-label" for="address">地址</label>
              <input type="text" id="address" class="input-control" v-model="form.address" placeholder="请输入详细地址">
            </div>

            <!-- 邮编 -->
            <div class="form-group">
              <label class="form-group-label" for="postcode">邮编</label>
              <input type="text" id="postcode" class="input-control" v-model="form.postcode" placeholder="请输入邮编">
            </div>

            <!-- 生日 -->
            <div class="form-group" style="position: relative;">
              <label class="form-group-label" for="birthday">生日</label>
              <input type="date" id="birthday" class="input-control" v-model="form.birthday" style="width: 100%;">
            </div>

            <!-- 关系标签 (多选) -->
            <div class="form-group col-span-2">
              <label class="form-group-label" for="tagIds">关系标签</label>
              <el-select
                id="tagIds"
                v-model="form.tagIds"
                multiple
                clearable
                placeholder="请选择标签"
                style="width: 100%;"
              >
                <el-option
                  v-for="item in tagList"
                  :key="item.tagId"
                  :label="item.name"
                  :value="item.tagId"
                >
                  <div style="display: flex; align-items: center; gap: 8px;">
                    <span
                      style="width: 12px; height: 12px; border-radius: 50%; display: inline-block;"
                      :style="{ backgroundColor: item.color }"
                    ></span>
                    <span>{{ item.name }}</span>
                  </div>
                </el-option>
              </el-select>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="form-actions-bar">
            <button type="button" class="btn btn-secondary" @click="handleCancel">取消</button>
            <button type="submit" class="btn btn-primary" :disabled="submitting">
              {{ submitting ? '保存中...' : '保存联系人' }}
            </button>
          </div>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getContactDetailApi, createContactApi, updateContactApi } from '@/api/contact'
import type { ContactSaveParams } from '@/api/contact'
import { getTagsApi } from '@/api/tag'
import type { TagInfo } from '@/api/tag'
import { uploadContactAvatar } from '@/api/upload'
import { resolveAvatarUrl } from '@/utils/avatar'

const route = useRoute()
const router = useRouter()

const loading = ref<boolean>(false)
const submitting = ref<boolean>(false)
const uploading = ref<boolean>(false)
const fileInputRef = ref<HTMLInputElement | null>(null)
const avatarPreview = ref<string>('')
const pendingFile = ref<File | null>(null)

// 检查是否为编辑模式
const isEdit = computed(() => route.name === 'contact-edit')
const contactId = computed(() => route.params.contactId as string)

const tagList = ref<TagInfo[]>([])

const fetchTagList = async () => {
  try {
    tagList.value = await getTagsApi()
  } catch (error) {
    console.error('Failed to fetch tags:', error)
  }
}

// 表单对象
const form = reactive({
  name: '',
  gender: '' as string | number,
  phone: '',
  email: '',
  qq: '',
  wechat: '',
  address: '',
  postcode: '',
  birthday: '',
  tagIds: [] as number[]
})

// 校验错误状态
const errors = reactive({
  name: false,
  phone: false
})

function getAvatarUrl(url: string | null): string {
  return resolveAvatarUrl(url)
}

// 获取现有详情
const loadContactDetail = async () => {
  loading.value = true
  try {
    const data = await getContactDetailApi(contactId.value)
    form.name = data.name
    form.gender = data.gender !== null ? data.gender : ''
    form.phone = data.phone
    form.email = data.email || ''
    form.qq = data.qq || ''
    form.wechat = data.wechat || ''
    form.address = data.address || ''
    form.postcode = data.postcode || ''
    form.birthday = data.birthday || ''
    
    if (data.tags && data.tags.length > 0) {
      form.tagIds = data.tags
        .map(tagName => tagList.value.find(t => t.name === tagName)?.tagId)
        .filter((id): id is number => id !== undefined)
    } else {
      form.tagIds = []
    }
    
    if (data.avatarUrl) {
      avatarPreview.value = getAvatarUrl(data.avatarUrl)
    }
  } catch (error) {
    console.error('Failed to load contact detail:', error)
    ElMessage.error('无法加载联系人详情，已自动返回列表')
    router.push('/contacts')
  } finally {
    loading.value = false
  }
}

// 触发头像上传
const triggerFileUpload = () => {
  if (uploading.value) return
  fileInputRef.value?.click()
}

// 头像即时预览
const handleFileChange = async (e: Event) => {
  const files = (e.target as HTMLInputElement).files
  if (!files || files.length === 0) return
  
  const file = files[0]
  if (!file) return
  
  // 校验大小和类型
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp']
  const isImage = allowedTypes.includes(file.type)
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isImage) {
    ElMessage.error('只支持上传 JPG, JPEG, PNG, WEBP 格式的图片文件')
    // 重置选择器
    ;(e.target as HTMLInputElement).value = ''
    return
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    ;(e.target as HTMLInputElement).value = ''
    return
  }
  
  // 如果是编辑模式，选择后立即触发上传
  if (isEdit.value) {
    try {
      uploading.value = true
      const res = await uploadContactAvatar(file, contactId.value)
      // 成功后在表单内保存 accessUrl 并展示预览
      avatarPreview.value = getAvatarUrl(res.accessUrl)
      ElMessage.success('头像上传成功')
    } catch (error: any) {
      console.error('Upload contact avatar failed:', error)
      ElMessage.error(error.message || '头像上传失败，已保留原头像')
    } finally {
      uploading.value = false
      ;(e.target as HTMLInputElement).value = ''
    }
  } else {
    // 新建模式下，由于没有 contactId，先进行本地预览，并在保存时统一上传
    pendingFile.value = file
    const reader = new FileReader()
    reader.onload = () => {
      avatarPreview.value = reader.result as string
    }
    reader.readAsDataURL(file)
    // 重置选择器以便可重复选择同一文件
    ;(e.target as HTMLInputElement).value = ''
  }
}

const clearError = (field: 'name' | 'phone') => {
  errors[field] = false
}

// 表单提交
const handleSubmit = async () => {
  let hasError = false
  
  // 验证姓名
  if (!form.name.trim()) {
    errors.name = true
    hasError = true
  }
  
  // 验证手机号 (必须不为空且符合简单数字模式)
  const phoneRegex = /^[0-9+\-()\s]{7,20}$/
  if (!form.phone.trim() || !phoneRegex.test(form.phone.trim())) {
    errors.phone = true
    hasError = true
  }
  
  if (hasError) {
    ElMessage.warning('请检查表单中必填项的填写状态与格式')
    return
  }
  
  submitting.value = true
  
  // 组装保存参数
  const saveParams: ContactSaveParams = {
    name: form.name.trim(),
    phone: form.phone.trim(),
    gender: form.gender !== '' ? Number(form.gender) : null,
    email: form.email.trim() || null,
    qq: form.qq.trim() || null,
    wechat: form.wechat.trim() || null,
    address: form.address.trim() || null,
    postcode: form.postcode.trim() || null,
    birthday: form.birthday || null,
    tagIds: form.tagIds
  }
  
  try {
    if (isEdit.value) {
      await updateContactApi(contactId.value, saveParams)
      ElMessage.success('联系人修改成功')
      router.push('/contacts')
    } else {
      const newContact = await createContactApi(saveParams)
      // 新建成功，判断是否需要上传头像
      if (pendingFile.value) {
        try {
          await uploadContactAvatar(pendingFile.value, newContact.contactId)
        } catch (uploadError: any) {
          console.error('Pending avatar upload failed:', uploadError)
          ElMessage.warning('联系人创建成功，但头像上传失败')
        }
      }
      ElMessage.success('联系人创建成功')
      router.push('/contacts')
    }
  } catch (error: any) {
    console.error('Save contact error:', error)
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  router.push('/contacts')
}

onMounted(async () => {
  await fetchTagList()
  if (isEdit.value) {
    loadContactDetail()
  }
})
</script>

<style scoped>
.contact-form-page-wrapper {
  animation: fadeIn 0.4s ease-out;
}

.form-layout {
  display: grid;
  grid-template-columns: minmax(280px, 332px) minmax(0, 1fr);
  gap: 20px;
  padding: 18px;
  border-radius: 22px;
  background:
    radial-gradient(circle at top right, rgba(239, 246, 255, 0.72), rgba(255, 255, 255, 0) 28%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(250, 252, 255, 0.98) 100%);
}

.avatar-upload-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 28px 20px 24px;
  border: 1px solid rgba(226, 232, 240, 0.96);
  border-radius: 20px;
  background: linear-gradient(180deg, #ffffff 0%, #fbfcff 100%);
  height: 100%;
  min-height: 438px;
}

.avatar-upload-placeholder {
  width: 142px;
  height: 142px;
  border-radius: var(--radius-full);
  border: 2px solid #dbe3ee;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at center, #ffffff 0%, #f8fbff 100%);
  margin-bottom: 20px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 18px 38px -30px rgba(15, 23, 42, 0.18);
  cursor: pointer;
  transition: border-color var(--transition-fast);
}

.avatar-upload-placeholder:hover {
  border-color: var(--color-primary);
}

.avatar-preview-img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-tip {
  font-size: 11px;
  color: var(--text-muted);
  margin-top: 12px;
  text-align: center;
  line-height: 1.5;
}

.fields-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px 20px;
}

.col-span-2 {
  grid-column: span 2;
}

.form-group-label {
  font-size: 13px;
  font-weight: 600;
  color: #0f172a;
  margin-bottom: 6px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.required-star {
  color: var(--color-danger);
}

.input-error-msg {
  font-size: 11px;
  color: var(--color-danger);
  margin-top: 4px;
  display: none;
}

.form-group.has-error input {
  border-color: var(--color-danger);
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.08);
}

.form-group.has-error .input-error-msg {
  display: block;
  animation: slideInDown 0.15s ease-out;
}

.tag-select-wrapper {
  position: relative;
}

.tag-chips-input {
  min-height: 44px;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 6px 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  background-color: #f8fafc;
}

.form-actions-bar {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 18px;
  padding-top: 14px;
  border-top: 1px solid var(--border-color);
}

.input-control,
.select-control {
  height: 44px;
  padding: 0 12px;
  border-radius: 12px;
  font-size: 13px;
  border: 1px solid #dfe6ef;
  background-color: #fff;
  transition: all var(--transition-fast);
}

.input-control:focus,
.select-control:focus {
  border-color: var(--color-primary);
  outline: none;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.avatar-upload-card .btn {
  min-width: 126px;
  height: 38px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
}

.form-actions-bar .btn {
  min-width: 112px;
  height: 40px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
}

.form-actions-bar .btn-primary {
  min-width: 132px;
  box-shadow: 0 16px 30px -22px rgba(37, 99, 235, 0.55);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideInDown {
  from { opacity: 0; transform: translateY(-4px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 1280px) {
  .form-layout {
    grid-template-columns: 1fr;
  }
  .avatar-upload-card {
    min-height: auto;
  }
}
</style>
