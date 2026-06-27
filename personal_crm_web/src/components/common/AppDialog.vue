<template>
  <el-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    :title="title"
    class="app-dialog"
    align-center
    append-to-body
    destroy-on-close
    :before-close="handleBeforeClose"
  >
    <div v-if="description" class="dialog-description">{{ description }}</div>
    
    <div class="dialog-body-content">
      <slot></slot>
    </div>

    <template #footer>
      <slot name="footer">
        <div class="dialog-footer-actions">
          <el-button @click="handleCancel">{{ cancelText }}</el-button>
          <el-button :type="confirmType" :loading="loading" @click="handleConfirm">
            {{ confirmText }}
          </el-button>
        </div>
      </slot>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
const props = withDefaults(
  defineProps<{
    modelValue: boolean
    title: string
    description?: string
    confirmText?: string
    cancelText?: string
    confirmType?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
    loading?: boolean
  }>(),
  {
    confirmText: '确定',
    cancelText: '取消',
    confirmType: 'primary',
    loading: false
  }
)

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'confirm'): void
  (e: 'cancel'): void
}>()

function handleCancel() {
  emit('cancel')
  emit('update:modelValue', false)
}

function handleConfirm() {
  emit('confirm')
}

function handleBeforeClose(done: () => void) {
  emit('cancel')
  done()
}
</script>

<style>
/* 全局遮罩定制，调整为 rgba(0,0,0,.35) */
.el-overlay {
  background-color: rgba(0, 0, 0, 0.35) !important;
}

/* ------------------------------------------
   AppDialog macOS 苹果质感通用弹窗规范
   ------------------------------------------ */
.app-dialog {
  border-radius: 24px !important;
  box-shadow: 0 32px 64px -16px rgba(0, 0, 0, 0.12) !important;
  border: 1px solid var(--border-color, #e2e8f0) !important;
  padding: 32px !important;
  width: 90% !important;
  max-width: 440px !important;
  background-color: #ffffff !important;
}

.app-dialog .el-dialog__header {
  padding: 0 0 12px 0 !important;
  margin-right: 0 !important;
  border-bottom: none !important;
}

.app-dialog .el-dialog__title {
  font-family: var(--font-sans, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif) !important;
  font-size: 20px !important;
  font-weight: 600 !important;
  color: var(--text-main, #0f172a) !important;
  letter-spacing: -0.01em !important;
}

/* 弹窗说明文字 */
.app-dialog .dialog-description {
  font-size: 14px !important;
  color: var(--text-muted, #64748b) !important;
  margin-top: 4px !important;
  margin-bottom: 20px !important;
  line-height: 1.5 !important;
}

.app-dialog .el-dialog__headerbtn {
  top: 32px !important;
  right: 32px !important;
  width: 24px !important;
  height: 24px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.app-dialog .el-dialog__body {
  padding: 0 !important;
}

.app-dialog .el-dialog__footer {
  padding: 20px 0 0 0 !important;
  border-top: none !important;
}

.dialog-footer-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 极简表单标签 */
.app-dialog .el-form-item {
  margin-bottom: 20px !important;
}

.app-dialog .el-form-item__label {
  font-size: 13px !important;
  font-weight: 600 !important;
  color: var(--text-main, #0f172a) !important;
  margin-bottom: 8px !important;
  padding: 0 !important;
  line-height: 1.2 !important;
}

/* 输入框扁平苹果风 (高度 46px 左右) */
.app-dialog .el-input__wrapper {
  background-color: #f8fafc !important;
  border-radius: var(--radius-md, 8px) !important;
  box-shadow: none !important;
  border: 1px solid #e2e8f0 !important;
  padding: 12px 14px !important;
  transition: all 0.2s ease !important;
  height: 46px !important;
}

.app-dialog .el-input__wrapper.is-focus {
  border-color: var(--color-primary, #2563eb) !important;
  background-color: #ffffff !important;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.08) !important;
}

.app-dialog .el-input__inner {
  color: var(--text-main, #0f172a) !important;
  font-size: 14px !important;
}

/* 按钮定制 */
.app-dialog .el-button {
  border-radius: var(--radius-md, 8px) !important;
  font-weight: 600 !important;
  font-size: 14px !important;
  height: 40px !important;
  padding: 0 20px !important;
  transition: all 0.2s ease !important;
  border: none !important;
}

.app-dialog .el-button:not(.el-button--primary):not(.el-button--danger) {
  background-color: #f1f5f9 !important;
  color: var(--color-neutral-text, #475569) !important;
}

.app-dialog .el-button:not(.el-button--primary):not(.el-button--danger):hover {
  background-color: #e2e8f0 !important;
  color: var(--text-main, #0f172a) !important;
}

.app-dialog .el-button--primary {
  background-color: var(--color-primary, #2563eb) !important;
  color: #ffffff !important;
}

.app-dialog .el-button--danger {
  background-color: var(--color-danger, #ef4444) !important;
  color: #ffffff !important;
}

/* 移动端响应式适配 */
@media (max-width: 768px) {
  .app-dialog {
    width: 92% !important;
    max-width: 380px !important;
    padding: 24px 20px !important;
  }

  .app-dialog .el-dialog__headerbtn {
    top: 24px !important;
    right: 20px !important;
  }

  .dialog-footer-actions {
    flex-direction: column-reverse !important;
    gap: 8px !important;
  }

  .dialog-footer-actions .el-button {
    width: 100% !important;
    margin-left: 0 !important;
  }
}
</style>
