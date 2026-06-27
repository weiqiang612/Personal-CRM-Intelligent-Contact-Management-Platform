<template>
  <div class="todo-form-page-wrapper">
    <div class="todo-form-layout">
      <!-- 左侧表单 card -->
      <div class="card" style="padding: 24px;">
        <form @submit.prevent="handleSubmit">
          <!-- 选择联系人 -->
          <div class="form-group" style="margin-bottom: 24px; position:relative;">
            <label class="form-group-label">选择联系人 <span class="required-star">*</span></label>
            
            <!-- 如果是带入的联系人，禁用修改，仅展示 Chip -->
            <div v-if="isContactFixed" class="selected-contact-chip" style="display: flex;">
              <div class="avatar-group">
                <span class="avatar-round-text" style="width:32px;height:32px;line-height:32px;font-size:14px;background-color:var(--color-primary-light);color:var(--color-primary);border-radius:50%;text-align:center;display:inline-block;margin-right:8px;font-weight:600;">
                  {{ (selectedContact?.name || '联').substring(0, 1) }}
                </span>
                <div>
                  <span class="avatar-name">{{ selectedContact?.name }}</span><br>
                  <span style="font-size:11px;color:var(--text-muted);">{{ selectedContact?.phone }}</span>
                </div>
              </div>
              <!-- 不提供清除按钮，因为是强制指定的联系人 -->
            </div>

            <!-- 普通创建时，支持远程搜索选择 -->
            <div v-else>
              <el-select
                v-model="form.contactId"
                filterable
                placeholder="请输入或选择联系人"
                class="select-control-el"
                style="width: 100%;"
                @change="handleContactChange"
              >
                <el-option
                  v-for="item in allContacts"
                  :key="item.contactId"
                  :label="`${item.name} (${item.phone})`"
                  :value="item.contactId"
                />
              </el-select>

              <!-- 选中后在下方展示 Chip -->
              <div v-if="selectedContact" class="selected-contact-chip" style="display: flex; margin-top: 10px;">
                <div class="avatar-group">
                  <span class="avatar-round-text" style="width:32px;height:32px;line-height:32px;font-size:14px;background-color:var(--color-primary-light);color:var(--color-primary);border-radius:50%;text-align:center;display:inline-block;margin-right:8px;font-weight:600;">
                    {{ selectedContact.name.substring(0, 1) }}
                  </span>
                  <div>
                    <span class="avatar-name">{{ selectedContact.name }}</span><br>
                    <span style="font-size:11px;color:var(--text-muted);">{{ selectedContact.phone }}</span>
                  </div>
                </div>
                <button type="button" class="btn btn-secondary btn-icon btn-sm" @click="clearSelectedContact" style="border-radius: var(--radius-full); padding: 4px;">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:12px;height:12px;"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                </button>
              </div>
            </div>
            
            <span class="input-error-msg" v-if="errors.contact">请先选择一个联系人</span>
          </div>

          <!-- 事项时间 -->
          <div class="form-group" style="margin-bottom: 24px;">
            <label class="form-group-label">事项时间 <span class="required-star">*</span></label>
            <input
              type="datetime-local"
              v-model="rawTime"
              class="input-control"
              style="width: 100%;"
              @input="validateTime"
            >
            <span class="input-error-msg" v-if="errors.time">请设置一个时间点</span>
          </div>

          <!-- 事项内容 -->
          <div class="form-group" style="margin-bottom: 24px;">
            <label class="form-group-label">事项内容 <span class="required-star">*</span></label>
            <textarea
              v-model="form.content"
              class="input-control"
              style="width: 100%; height: 120px; resize:none;"
              placeholder="请输入需要提醒的事项内容，例如：沟通产品合作协议细节"
              maxlength="500"
              @input="validateContent"
            ></textarea>
            <div style="display: flex; justify-content: space-between; align-items:center; margin-top:4px;">
              <span class="input-error-msg" v-if="errors.content" style="margin-top:0;">请输入事项内容</span>
              <span style="font-size:11px;color:var(--text-muted);margin-left:auto;">{{ form.content.length }} / 500</span>
            </div>
          </div>

          <!-- 优先级 -->
          <div class="form-group" style="margin-bottom: 24px;">
            <label class="form-group-label">优先级</label>
            <div class="priority-select-group">
              <button
                type="button"
                :class="['priority-btn', form.priority === 0 ? 'active' : '']"
                @click="setPriority(0)"
              >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>
                普通
              </button>
              <button
                type="button"
                :class="['priority-btn', form.priority === 1 ? 'active important' : '']"
                @click="setPriority(1)"
              >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;"><path d="M4 15s1-1 4-1 5 2 8 2 4-1 4-1V3s-1 1-4 1-5-2-8-2-4 1-4 1z"/><line x1="4" y1="22" x2="4" y2="15"/></svg>
                重要
              </button>
              <button
                type="button"
                :class="['priority-btn', form.priority === 2 ? 'active emergency' : '']"
                @click="setPriority(2)"
              >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:14px;height:14px;"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
                紧急
              </button>
            </div>
          </div>

          <!-- 提示条 -->
          <div class="info-alert-strip">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>
            新增事项默认状态为待完成
          </div>

          <!-- 表单操作 -->
          <div class="form-actions-bar" style="margin-top: 32px; display: flex; gap: 12px; justify-content: flex-end;">
            <button type="button" class="btn btn-secondary" @click="handleCancel">取消</button>
            <button type="submit" class="btn btn-primary" :disabled="submitLoading">
              {{ submitLoading ? '正在保存...' : '保存事项' }}
            </button>
          </div>
        </form>
      </div>

      <!-- 右侧预览 card -->
      <div class="preview-card">
        <h3 class="card-title" style="margin-bottom: 20px; display: flex; align-items: center; gap: 8px;">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="width:18px;height:18px;color:var(--color-primary);"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
          事项预览
        </h3>
        
        <!-- 预览联系人 -->
        <div v-if="selectedContact">
          <div class="avatar-group">
            <span class="avatar-round-text" style="width:40px;height:40px;line-height:40px;font-size:16px;background-color:var(--color-primary-light);color:var(--color-primary);border-radius:50%;text-align:center;display:inline-block;margin-right:12px;font-weight:600;box-shadow:var(--shadow-sm);">
              {{ selectedContact.name.substring(0, 1) }}
            </span>
            <div>
              <span class="avatar-name" style="font-size:14px; font-weight: 600;">{{ selectedContact.name }}</span><br>
              <span style="font-size:12px;color:var(--text-muted);">{{ selectedContact.phone }}</span>
            </div>
          </div>
        </div>
        
        <div v-else style="font-size:13px;color:var(--text-muted);display: flex; align-items:center; gap:8px; padding: 8px 0;">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><line x1="17" y1="8" x2="23" y2="14"/><line x1="23" y1="8" x2="17" y2="14"/></svg>
          未选择联系人
        </div>

        <!-- 事项时间 -->
        <div class="preview-item">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;color:var(--text-muted);margin-top:2px;"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
          <div class="preview-label-group">
            <span class="preview-label">事项时间</span>
            <span :class="['preview-value', rawTime ? 'text-main' : 'text-muted']">
              {{ formattedPreviewTime }}
            </span>
          </div>
        </div>

        <!-- 事项内容 -->
        <div class="preview-item">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;color:var(--text-muted);margin-top:2px;"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
          <div class="preview-label-group">
            <span class="preview-label">事项内容</span>
            <span :class="['preview-value', form.content.trim() ? 'text-main' : 'text-muted']">
              {{ form.content.trim() ? form.content : '暂无内容' }}
            </span>
          </div>
        </div>

        <!-- 优先级 -->
        <div class="preview-item">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;color:var(--text-muted);margin-top:2px;"><path d="M4 15s1-1 4-1 5 2 8 2 4-1 4-1V3s-1 1-4 1-5-2-8-2-4 1-4 1z"/><line x1="4" y1="22" x2="4" y2="15"/></svg>
          <div class="preview-label-group">
            <span class="preview-label">优先级</span>
            <span class="preview-value">
              <span :class="['badge', getPriorityClass(form.priority)]">
                {{ formatPriority(form.priority) }}
              </span>
            </span>
          </div>
        </div>

        <!-- 默认状态 -->
        <div class="preview-item">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width:16px;height:16px;color:var(--text-muted);margin-top:2px;"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
          <div class="preview-label-group">
            <span class="preview-label">状态</span>
            <span class="preview-value">
              <span class="badge badge-todo-pending">待完成</span>
            </span>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getContactDetailApi, getContactsApi } from '@/api/contact'
import { createTodo } from '@/api/todo'
import type { ContactInfo } from '@/api/contact'

const route = useRoute()
const router = useRouter()

// 状态
const isContactFixed = ref(false)
const submitLoading = ref(false)

const selectedContact = ref<ContactInfo | null>(null)
const allContacts = ref<ContactInfo[]>([])

// 原始绑定输入框的时间
const rawTime = ref('')

// 表单数据
const form = reactive({
  contactId: '',
  todoTime: '',
  content: '',
  priority: 0 // 0 普通, 1 重要, 2 紧急
})

// 校验错误状态
const errors = reactive({
  contact: false,
  time: false,
  content: false
})

// 预览时间格式化计算
const formattedPreviewTime = computed(() => {
  if (!rawTime.value) return '未设定时间'
  return rawTime.value.replace('T', ' ')
})

// 优先级样式和文案
function getPriorityClass(priority: number) {
  if (priority === 2) return 'badge-priority-emergency'
  if (priority === 1) return 'badge-priority-important'
  return 'badge-priority-normal'
}

function formatPriority(priority: number) {
  if (priority === 2) return '紧急'
  if (priority === 1) return '重要'
  return '普通'
}

// 切换优先级
const setPriority = (prio: number) => {
  form.priority = prio
}

// 联系人下拉框选择事件
const handleContactChange = (val: string) => {
  errors.contact = false
  const hit = allContacts.value.find(item => item.contactId === val)
  if (hit) {
    selectedContact.value = hit
  } else {
    selectedContact.value = null
  }
}

// 清除所选联系人
const clearSelectedContact = () => {
  form.contactId = ''
  selectedContact.value = null
}

// 校验字段
const validateContact = () => {
  errors.contact = !form.contactId
  return !errors.contact
}

const validateTime = () => {
  errors.time = !rawTime.value
  return !errors.time
}

const validateContent = () => {
  errors.content = !form.content.trim()
  return !errors.content
}

// 保存事项
const handleSubmit = async () => {
  const isContactValid = validateContact()
  const isTimeValid = validateTime()
  const isContentValid = validateContent()

  if (!isContactValid || !isTimeValid || !isContentValid) {
    ElMessage.warning('请检查并完善必填项数据！')
    return
  }

  // 格式化时间：将 datetime-local 的 YYYY-MM-DDTHH:mm 转换成 YYYY-MM-DD HH:mm:ss
  // 例如 2026-06-21T15:30 -> 2026-06-21 15:30:00
  form.todoTime = rawTime.value.replace('T', ' ')
  if (form.todoTime.length === 16) {
    form.todoTime += ':00'
  }

  submitLoading.value = true
  try {
    await createTodo({
      contactId: form.contactId,
      todoTime: form.todoTime,
      content: form.content,
      priority: form.priority
    })
    ElMessage.success('事项创建成功！')
    router.push('/todos')
  } catch (error: any) {
    ElMessage.error(error.message || '事项创建失败')
  } finally {
    submitLoading.value = false
  }
}

// 取消并返回
const handleCancel = () => {
  router.push('/todos')
}

// 初始化
onMounted(async () => {
  const contactIdParam = route.query.contactId as string
  
  // 无论是否带入联系人，均预加载联系人列表以备普通创建或预览时使用
  try {
    const res = await getContactsApi({ page: 1, pageSize: 1000 })
    allContacts.value = res.list
  } catch (error) {
    console.error('获取联系人列表失败', error)
  }
  
  if (contactIdParam) {
    isContactFixed.value = true
    form.contactId = contactIdParam
    errors.contact = false

    // 详情页带入：
    // 1. 自动调用获取联系人详情 API
    try {
      const detail = await getContactDetailApi(contactIdParam)
      selectedContact.value = detail
    } catch (error) {
      console.error('获取带入联系人详情失败', error)
      ElMessage.error('获取带入联系人详情失败')
    }

    // 2. 预设优先级为 重要 (1)
    form.priority = 1

    // 3. 预设默认时间为 明天此时
    const d = new Date()
    d.setDate(d.getDate() + 1)
    d.setMinutes(0)
    
    const pad = (n: number) => n.toString().padStart(2, '0')
    rawTime.value = `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:00`
  }
})
</script>

<style scoped>
/* 移植 css */
.todo-form-page-wrapper {
  padding: 0;
}

/* 两栏布局 */
.todo-form-layout {
  display: grid;
  grid-template-columns: 68fr 32fr;
  gap: 24px;
}

/* 优先级并排单选框 */
.priority-select-group {
  display: flex;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  overflow: hidden;
  background-color: #f9fafb;
}

.priority-btn {
  flex: 1;
  border: none;
  background: none;
  padding: 10px 16px;
  font-size: 13px;
  font-weight: 600;
  color: var(--color-neutral-text);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.priority-btn:not(:last-child) {
  border-right: 1px solid var(--border-color);
}

.priority-btn:hover {
  background-color: #fff;
  color: var(--color-primary);
}

.priority-btn.active {
  background-color: #fff;
  color: var(--color-primary);
  box-shadow: inset 0 2px 4px rgba(0,0,0,0.02);
}

.priority-btn.active.emergency {
  color: var(--color-danger);
}

.priority-btn.active.important {
  color: #f97316; /* 重要：橙色 */
}

/* 选中联系人浮层 */
.selected-contact-chip {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border: 1px solid var(--border-color);
  background-color: #f9fafb;
  padding: 8px 16px;
  border-radius: var(--radius-md);
  margin-top: 10px;
}

.avatar-group {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: var(--text-color);
}

.avatar-name {
  font-weight: 600;
  color: var(--text-main);
}

/* 事项预览面板 */
.preview-card {
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 24px;
  background-color: var(--bg-card);
  box-shadow: var(--shadow-card);
  height: fit-content;
}

.preview-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-top: 16px;
  border-top: 1px dashed var(--border-color);
  padding-top: 16px;
}

.preview-label-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
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
  line-height: 1.4;
}

.preview-value.text-muted {
  color: var(--text-muted) !important;
  font-weight: 500;
}

.preview-value.text-main {
  color: var(--text-main) !important;
}

/* 提示条 */
.info-alert-strip {
  background-color: var(--color-primary-light);
  border: 1px solid var(--color-primary-border);
  color: var(--color-primary);
  border-radius: var(--radius-sm);
  padding: 10px 14px;
  font-size: 12px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 16px;
}

/* 优先级标签样式 */
.badge-priority-emergency {
  background-color: #fee2e2;
  color: #ef4444;
}

.badge-priority-important {
  background-color: #fff7ed;
  color: #f97316;
}

.badge-priority-normal {
  background-color: #f0fdf4;
  color: #22c55e;
}

/* 事项状态标签 */
.badge-todo-pending {
  background-color: #e0f2fe;
  color: #0284c7;
}

/* 覆盖 Element Plus Select 的样式，让它与主题协调 */
:deep(.select-control-el .el-input__wrapper) {
  background-color: transparent !important;
  box-shadow: none !important;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  padding: 4px 12px;
}

:deep(.select-control-el .el-input__wrapper:hover) {
  border-color: var(--color-primary-border);
}

:deep(.select-control-el .el-input__wrapper.is-focus) {
  border-color: var(--color-primary) !important;
}

.required-star {
  color: var(--color-danger);
  margin-left: 2px;
}

@media (max-width: 768px) {
  .todo-form-page-wrapper {
    padding-bottom: 24px;
  }
  .todo-form-layout {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  .priority-btn {
    padding: 8px 10px;
    font-size: 12px;
  }
  .form-actions-bar {
    justify-content: space-between;
  }
  .form-actions-bar .btn {
    flex: 1;
    min-width: 0;
  }
  .preview-card {
    padding: 16px;
  }
}
</style>
