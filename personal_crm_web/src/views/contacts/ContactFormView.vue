<template>
  <div class="contact-form-page-wrapper">
    <!-- 主两栏布局 -->
    <div class="contact-form-layout" v-loading="loading">
      <!-- 左侧：表单 Card -->
      <div class="card form-main-card">
        <form @submit.prevent="handleSubmit">
          
          <!-- 分组 1: 基本信息 -->
          <div class="form-section">
            <h3 class="section-title">基本信息</h3>
            <div class="section-flex">
              <!-- 头像上传 -->
              <div class="avatar-upload-card" v-loading="uploading" element-loading-text="上传中...">
                <div class="avatar-upload-placeholder" id="avatarBox" @click="triggerFileUpload">
                  <svg v-if="!avatarPreview" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" style="width: 48px; height: 48px; color: #b2bac7;">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
                  </svg>
                  <img v-else :src="avatarPreview" class="avatar-preview-img" alt="头像预览">
                </div>
                <input type="file" ref="fileInputRef" accept="image/png, image/jpeg, image/jpg, image/webp" style="display: none;" @change="handleFileChange">
                <button type="button" class="btn btn-secondary btn-sm upload-btn" @click="triggerFileUpload" :disabled="uploading">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:12px;height:12px;margin-right:4px;display:inline-block;vertical-align:middle;">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/>
                  </svg>
                  上传头像
                </button>
                <span class="upload-tip">支持 jpg/png/jpeg/webp，最大 2MB</span>
              </div>

              <!-- 右侧基本字段 -->
              <div class="basic-fields-wrapper">
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
                  <select id="gender" class="select-control" v-model="form.gender">
                    <option value="">请选择性别</option>
                    <option :value="1">男</option>
                    <option :value="2">女</option>
                  </select>
                </div>

                <!-- 标签展示 (平铺点击选择) -->
                <div class="form-group tag-平铺组">
                  <label class="form-group-label">标签</label>
                  <div class="tags-flat-container">
                    <button type="button" class="add-tag-btn" @click="handleAddTag">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" style="width:13px;height:13px;"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                      添加标签
                    </button>
                    <button
                      v-for="item in tagList"
                      :key="item.tagId"
                      type="button"
                      :class="['tag-chip-btn', isTagSelected(item.tagId) ? 'active' : '']"
                      :style="isTagSelected(item.tagId) ? getSelectedTagStyle(item.color) : {}"
                      @click="toggleTag(item.tagId)"
                    >
                      <span class="tag-color-dot" :style="{ backgroundColor: item.color }"></span>
                      {{ item.name }}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="divider"></div>

          <!-- 分组 2: 联系方式 -->
          <div class="form-section">
            <h3 class="section-title">联系方式</h3>
            <div class="fields-grid-2">
              <!-- 手机号 -->
              <div class="form-group iconized-input-wrap" :class="{ 'has-error': errors.phone }">
                <label class="form-group-label" for="phone">手机号 <span class="required-star">*</span></label>
                <div class="input-with-icon">
                  <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
                  </svg>
                  <input
                    type="text"
                    id="phone"
                    class="input-control"
                    v-model="form.phone"
                    placeholder="请输入手机号码"
                    @input="clearError('phone')"
                  >
                </div>
                <span class="input-error-msg">手机号格式不正确 (必须输入合法数字)</span>
              </div>

              <!-- 微信 -->
              <div class="form-group iconized-input-wrap">
                <label class="form-group-label" for="wechat">微信</label>
                <div class="input-with-icon">
                  <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
                  </svg>
                  <input type="text" id="wechat" class="input-control" v-model="form.wechat" placeholder="请输入微信账号">
                </div>
              </div>

              <!-- 邮箱 -->
              <div class="form-group iconized-input-wrap">
                <label class="form-group-label" for="email">邮箱</label>
                <div class="input-with-icon">
                  <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/>
                  </svg>
                  <input type="email" id="email" class="input-control" v-model="form.email" placeholder="请输入邮箱地址">
                </div>
              </div>

              <!-- QQ -->
              <div class="form-group iconized-input-wrap">
                <label class="form-group-label" for="qq">QQ</label>
                <div class="input-with-icon">
                  <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                  </svg>
                  <input type="text" id="qq" class="input-control" v-model="form.qq" placeholder="请输入 QQ 号">
                </div>
              </div>
            </div>
          </div>

          <div class="divider"></div>

          <!-- 分组 3: 更多信息 -->
          <div class="form-section">
            <h3 class="section-title">更多信息</h3>
            <div class="fields-grid-2">
              <!-- 生日 -->
              <div class="form-group iconized-input-wrap">
                <label class="form-group-label" for="birthday">生日</label>
                <div class="input-with-icon">
                  <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>
                  </svg>
                  <input type="date" id="birthday" class="input-control" v-model="form.birthday" style="padding-right: 8px;">
                </div>
              </div>

              <!-- 地址 -->
              <div class="form-group iconized-input-wrap">
                <label class="form-group-label" for="address">地址</label>
                <div class="input-with-icon">
                  <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/>
                  </svg>
                  <input type="text" id="address" class="input-control" v-model="form.address" placeholder="请输入详细地址">
                </div>
              </div>

              <!-- 邮编 -->
              <div class="form-group iconized-input-wrap">
                <label class="form-group-label" for="postcode">邮编</label>
                <div class="input-with-icon">
                  <svg class="field-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="2" y="3" width="20" height="14" rx="2" ry="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/>
                  </svg>
                  <input type="text" id="postcode" class="input-control" v-model="form.postcode" placeholder="请输入邮编">
                </div>
              </div>

              <!-- 备注 -->
              <div class="form-group remarks-textarea-group" style="grid-column: span 2;">
                <label class="form-group-label" for="remarks">备注</label>
                <div class="textarea-container">
                  <textarea
                    id="remarks"
                    class="input-control textarea-control"
                    v-model="form.remarks"
                    placeholder="请输入备注信息（可选）"
                    maxlength="500"
                  ></textarea>
                  <span class="word-count">{{ form.remarks.length }} / 500</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="form-actions-bar">
            <button type="button" class="btn btn-secondary" @click="handleCancel">取消</button>
            <button type="submit" class="btn btn-primary" :disabled="submitting">
              {{ submitting ? '保存中...' : '保存联系人' }}
            </button>
          </div>
        </form>
      </div>

      <!-- 右侧：信息预览 Card -->
      <div class="preview-card">
        <h3 class="preview-card-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="width:18px;height:18px;color:var(--color-primary);">
            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
          </svg>
          信息预览
        </h3>

        <!-- 头像与基本名字 -->
        <div class="preview-header">
          <div class="preview-avatar">
            <img v-if="avatarPreview" :src="avatarPreview" alt="预览头像">
            <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" style="width: 32px; height: 32px; color: #b2bac7;">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
            </svg>
          </div>
          <div class="preview-name-group">
            <h4 :class="['preview-name', form.name.trim() ? 'active' : 'placeholder']">
              {{ form.name.trim() ? form.name.trim() : '未命名' }}
            </h4>
            <div class="preview-tags">
              <span v-if="selectedTagsList.length === 0" class="no-tags-placeholder">暂无标签</span>
              <span
                v-else
                v-for="tag in selectedTagsList"
                :key="tag.tagId"
                class="preview-tag-badge"
                :style="{ backgroundColor: tag.color + '1a', color: tag.color, border: '1px solid ' + tag.color + '30' }"
              >
                {{ tag.name }}
              </span>
            </div>
          </div>
        </div>

        <div class="preview-body">
          <!-- 手机 -->
          <div class="preview-item">
            <svg class="preview-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
            </svg>
            <div class="preview-value-wrap">
              <span class="preview-label">手机号</span>
              <span :class="['preview-value', form.phone.trim() ? 'active' : 'placeholder']">{{ form.phone.trim() ? form.phone.trim() : '未填写' }}</span>
            </div>
          </div>

          <!-- 微信 -->
          <div class="preview-item">
            <svg class="preview-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
            </svg>
            <div class="preview-value-wrap">
              <span class="preview-label">微信</span>
              <span :class="['preview-value', form.wechat.trim() ? 'active' : 'placeholder']">{{ form.wechat.trim() ? form.wechat.trim() : '未填写' }}</span>
            </div>
          </div>

          <!-- 邮箱 -->
          <div class="preview-item">
            <svg class="preview-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/>
            </svg>
            <div class="preview-value-wrap">
              <span class="preview-label">邮箱</span>
              <span :class="['preview-value', form.email.trim() ? 'active' : 'placeholder']">{{ form.email.trim() ? form.email.trim() : '未填写' }}</span>
            </div>
          </div>

          <!-- QQ -->
          <div class="preview-item">
            <svg class="preview-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
            </svg>
            <div class="preview-value-wrap">
              <span class="preview-label">QQ</span>
              <span :class="['preview-value', form.qq.trim() ? 'active' : 'placeholder']">{{ form.qq.trim() ? form.qq.trim() : '未填写' }}</span>
            </div>
          </div>

          <!-- 生日 -->
          <div class="preview-item">
            <svg class="preview-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>
            </svg>
            <div class="preview-value-wrap">
              <span class="preview-label">生日</span>
              <span :class="['preview-value', form.birthday ? 'active' : 'placeholder']">{{ form.birthday ? form.birthday : '未设置' }}</span>
            </div>
          </div>

          <!-- 地址 -->
          <div class="preview-item">
            <svg class="preview-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/>
            </svg>
            <div class="preview-value-wrap">
              <span class="preview-label">地址</span>
              <span :class="['preview-value', form.address.trim() ? 'active' : 'placeholder']">{{ form.address.trim() ? form.address.trim() : '未填写' }}</span>
            </div>
          </div>

          <!-- 备注 -->
          <div class="preview-item" style="border-top: 1px dashed rgba(226, 232, 240, 0.8); margin-top: 16px; padding-top: 16px;">
            <svg class="preview-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-top: 2px;">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/>
            </svg>
            <div class="preview-value-wrap">
              <span class="preview-label">备注</span>
              <span :class="['preview-value', form.remarks.trim() ? 'active' : 'placeholder']" style="font-weight: 500; font-size: 13px; line-height: 1.5; white-space: pre-wrap;">{{ form.remarks.trim() ? form.remarks.trim() : '暂无备注信息' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加自定义标签弹窗（复用通用 AppDialog 苹果风样式组件） -->
    <AppDialog
      v-model="showAddTagDialog"
      title="添加自定义标签"
      description="请输入新标签名称，系统将自动生成对应色彩的主题标签"
      confirm-text="确定"
      cancel-text="取消"
      :loading="addTagLoading"
      @confirm="confirmAddTag"
      @cancel="cancelAddTag"
    >
      <div style="margin-top: 8px;">
        <el-input
          v-model="newTagName"
          placeholder="例如：合作伙伴"
          maxlength="20"
          clearable
          @keyup.enter="confirmAddTag"
        />
      </div>
    </AppDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import AppDialog from '@/components/common/AppDialog.vue'
import { getContactDetailApi, createContactApi, updateContactApi } from '@/api/contact'
import type { ContactSaveParams } from '@/api/contact'
import { getTagsApi, createTagApi } from '@/api/tag'
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

// 获取标签列表
const fetchTagList = async () => {
  try {
    tagList.value = await getTagsApi()
  } catch (error) {
    console.error('Failed to fetch tags:', error)
  }
}

// 标签选中计算属性
const selectedTagsList = computed(() => {
  return tagList.value.filter(tag => form.tagIds.includes(tag.tagId))
})

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
  remarks: '',
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
    form.remarks = data.remarks || ''
    
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
  
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp']
  const isImage = allowedTypes.includes(file.type)
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isImage) {
    ElMessage.error('只支持上传 JPG, JPEG, PNG, WEBP 格式的图片文件')
    ;(e.target as HTMLInputElement).value = ''
    return
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    ;(e.target as HTMLInputElement).value = ''
    return
  }
  
  pendingFile.value = file
  const reader = new FileReader()
  reader.onload = () => {
    avatarPreview.value = reader.result as string
  }
  reader.readAsDataURL(file)
  ;(e.target as HTMLInputElement).value = ''
}

const clearError = (field: 'name' | 'phone') => {
  errors[field] = false
}

// 平铺标签点击状态
const isTagSelected = (tagId: number) => {
  return form.tagIds.includes(tagId)
}

const toggleTag = (tagId: number) => {
  const index = form.tagIds.indexOf(tagId)
  if (index > -1) {
    form.tagIds.splice(index, 1)
  } else {
    form.tagIds.push(tagId)
  }
}

// 标签选中半透明激活样式生成
const getSelectedTagStyle = (color: string) => {
  return {
    backgroundColor: color + '12', // 7% 透明度
    color: color,
    borderColor: color + '3f' // 25% 透明度
  }
}

// 随机标签色彩辅助函数
const getRandomTagColor = () => {
  const colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899', '#06b6d4', '#14b8a6']
  return colors[Math.floor(Math.random() * colors.length)]
}

// 弹窗状态管理
const showAddTagDialog = ref<boolean>(false)
const addTagLoading = ref<boolean>(false)
const newTagName = ref<string>('')

// 动态弹窗创建新标签
const handleAddTag = () => {
  newTagName.value = ''
  showAddTagDialog.value = true
}

const cancelAddTag = () => {
  showAddTagDialog.value = false
  newTagName.value = ''
}

const confirmAddTag = async () => {
  const tagName = newTagName.value.trim()
  if (!tagName) {
    ElMessage.warning('标签名称不能为空')
    return
  }
  if (tagList.value.some(t => t.name === tagName)) {
    ElMessage.warning('该标签已存在！')
    return
  }
  addTagLoading.value = true
  try {
    const newTag = await createTagApi({
      name: tagName,
      color: getRandomTagColor()
    })
    ElMessage.success('标签创建成功！')
    await fetchTagList()
    form.tagIds.push(newTag.tagId)
    showAddTagDialog.value = false
    newTagName.value = ''
  } catch (err: any) {
    ElMessage.error(err.message || '创建标签失败')
  } finally {
    addTagLoading.value = false
  }
}

// 表单提交
const handleSubmit = async () => {
  let hasError = false
  
  if (!form.name.trim()) {
    errors.name = true
    hasError = true
  }
  
  const phoneRegex = /^[0-9+\-()\s]{7,20}$/
  if (!form.phone.trim() || !phoneRegex.test(form.phone.trim())) {
    errors.phone = true
    hasError = true
  }
  
  if (hasError) {
    ElMessage.warning('请检查并完善必填项数据！')
    return
  }
  
  submitting.value = true
  
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
    remarks: form.remarks.trim() || null,
    tagIds: form.tagIds
  }
  
  try {
    if (isEdit.value) {
      await updateContactApi(contactId.value, saveParams)
      if (pendingFile.value) {
        try {
          await uploadContactAvatar(pendingFile.value, contactId.value)
        } catch (uploadError: any) {
          console.error('Pending avatar upload failed:', uploadError)
          ElMessage.warning('联系人修改成功，但头像上传失败')
        }
      }
      ElMessage.success('联系人修改成功')
      router.push('/contacts')
    } else {
      const newContact = await createContactApi(saveParams)
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



/* 两栏布局 */
.contact-form-layout {
  display: grid;
  grid-template-columns: 7fr 3fr;
  gap: 24px;
}

.form-main-card {
  padding: 24px;
  border-radius: 20px;
  box-shadow: var(--shadow-card);
  background-color: var(--bg-card);
}

/* 分组部分结构 */
.form-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 14px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 18px;
  padding-left: 8px;
  border-left: 3px solid var(--color-primary);
  line-height: 1;
}

.divider {
  height: 1px;
  background-color: rgba(226, 232, 240, 0.7);
  margin: 24px 0;
}

/* 基本信息 Flex 分栏 */
.section-flex {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.avatar-upload-card {
  width: 180px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px;
  border: 1px dashed rgba(226, 232, 240, 0.9);
  border-radius: 16px;
  background-color: #fafbfc;
}

.avatar-upload-placeholder {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  border: 2px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at center, #ffffff 0%, #f8fbff 100%);
  margin-bottom: 12px;
  position: relative;
  overflow: hidden;
  box-shadow: var(--shadow-sm);
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

.upload-btn {
  min-width: 110px;
  height: 32px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  color: var(--color-primary) !important;
  border-color: var(--color-primary-border) !important;
}

.upload-tip {
  font-size: 10px;
  color: var(--text-muted);
  margin-top: 8px;
  text-align: center;
  line-height: 1.4;
}

/* 右侧基本字段容器 */
.basic-fields-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

#gender {
  max-width: 240px;
  width: 100%;
}

/* 平铺标签组 */
.tags-flat-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 10px;
  margin-top: 8px;
}

.tag-chip-btn {
  border: 1px solid var(--border-color);
  background-color: #f8fafc;
  color: var(--color-neutral-text);
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.tag-chip-btn:hover {
  background-color: #f1f5f9;
  border-color: #cbd5e1;
}

.tag-chip-btn.active {
  box-shadow: inset 0 1px 1px rgba(0,0,0,0.02);
}

.tag-color-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.add-tag-btn {
  border: 1px dashed var(--border-color);
  background-color: transparent;
  color: var(--text-muted);
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.add-tag-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background-color: rgba(37, 99, 235, 0.02);
}

/* 二列网格（联系方式 & 更多信息） */
.fields-grid-2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px 20px;
}

/* 控件基本样式 */
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

.input-control,
.select-control {
  width: 100%;
  height: 40px;
  padding: 0 12px;
  border-radius: 10px;
  font-size: 13px;
  border: 1px solid #dfe6ef;
  background-color: #fff;
  transition: all var(--transition-fast);
}

.input-control:focus,
.select-control:focus {
  border-color: var(--color-primary);
  outline: none;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.08);
}

/* 前缀 Iconized 文本框 */
.input-with-icon {
  position: relative;
  width: 100%;
}

.input-with-icon .field-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  color: #94a3b8;
  pointer-events: none;
}

.input-with-icon .input-control {
  padding-left: 38px;
}

/* 备注 Textarea 容器 */
.textarea-container {
  position: relative;
  width: 100%;
}

.textarea-control {
  height: 100px;
  padding: 10px 12px;
  font-family: inherit;
  resize: none;
}

.word-count {
  position: absolute;
  right: 12px;
  bottom: 8px;
  font-size: 11px;
  color: var(--text-muted);
}

.input-error-msg {
  font-size: 11px;
  color: var(--color-danger);
  margin-top: 4px;
  display: none;
}

.form-group.has-error .input-control {
  border-color: var(--color-danger);
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.06);
}

.form-group.has-error .input-error-msg {
  display: block;
  animation: slideInDown 0.15s ease-out;
}

/* 操作底部工具条 */
.form-actions-bar {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 18px;
  border-top: 1px solid rgba(226, 232, 240, 0.7);
}

.form-actions-bar .btn {
  min-width: 100px;
  height: 38px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
}

.form-actions-bar .btn-primary {
  min-width: 120px;
  box-shadow: 0 16px 28px -20px rgba(37, 99, 235, 0.5);
}

/* 右侧信息预览卡片 */
.preview-card {
  border: 1px solid var(--border-color);
  border-radius: 20px;
  padding: 24px;
  background-color: var(--bg-card);
  box-shadow: var(--shadow-card);
  height: fit-content;
}

.preview-card-title {
  font-size: 14px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.preview-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.6);
  margin-bottom: 20px;
}

.preview-avatar {
  width: 58px;
  height: 58px;
  border-radius: 50%;
  border: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8fafc;
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  flex-shrink: 0;
}

.preview-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-name-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.preview-name {
  font-size: 15px;
  font-weight: 700;
  margin: 0;
}

.preview-name.placeholder {
  color: var(--text-muted);
  font-weight: 500;
}

.preview-name.active {
  color: var(--text-main);
}

.preview-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.no-tags-placeholder {
  font-size: 11px;
  color: var(--text-muted);
}

.preview-tag-badge {
  font-size: 10px;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 6px;
}

.preview-body {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.preview-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.preview-icon {
  width: 15px;
  height: 15px;
  color: #94a3b8;
  margin-top: 3px;
  flex-shrink: 0;
}

.preview-value-wrap {
  display: flex;
  flex-direction: column;
  gap: 3px;
  flex: 1;
}

.preview-label {
  font-size: 11px;
  color: var(--text-muted);
  font-weight: 500;
}

.preview-value {
  font-size: 13px;
  font-weight: 600;
}

.preview-value.placeholder {
  color: var(--text-muted);
  font-weight: 400;
}

.preview-value.active {
  color: var(--text-main);
}

/* 动效 */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideInDown {
  from { opacity: 0; transform: translateY(-4px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 移动端媒体查询自适应适配 */
@media (max-width: 1280px) {
  .contact-form-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .contact-form-page-wrapper {
    padding-bottom: 24px;
  }

  .form-main-card {
    padding: 16px;
  }
  .divider {
    margin: 18px 0;
  }
  .section-flex {
    flex-direction: column;
    align-items: center;
    gap: 16px;
  }
  .avatar-upload-card {
    width: 100%;
    padding: 14px;
  }
  .basic-fields-wrapper {
    width: 100%;
  }
  #gender {
    max-width: 100%;
  }
  .fields-grid-2 {
    grid-template-columns: 1fr 1fr;
    gap: 10px 12px;
  }
  .input-control,
  .select-control {
    height: 38px;
    padding: 0 8px;
    font-size: 12px;
    border-radius: 8px;
  }
  .input-with-icon .input-control {
    padding-left: 34px;
  }
  .input-with-icon .field-icon {
    left: 10px;
    width: 14px;
    height: 14px;
  }
  .textarea-control {
    height: 90px;
    padding: 8px;
  }
  .form-actions-bar {
    justify-content: space-between;
    margin-top: 16px;
    padding-top: 12px;
  }
  .form-actions-bar .btn {
    flex: 1;
    min-width: 0;
    height: 36px;
    font-size: 13px;
  }
  .preview-card {
    padding: 16px;
  }
}
</style>
