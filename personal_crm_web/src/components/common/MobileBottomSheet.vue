<template>
  <Teleport to="body">
    <Transition name="sheet-backdrop">
      <div
        v-if="modelValue"
        class="mobile-sheet-backdrop"
        :style="backdropStyle"
        @click="handleBackdropClick"
      ></div>
    </Transition>

    <Transition name="sheet-slide">
      <div
        v-if="modelValue"
        ref="sheetRef"
        class="mobile-bottom-sheet"
        :class="[presetClass, { dragging: isDragging }]"
        :style="sheetStyle"
      >
        <!-- 顶部 Handle 拖拽条 -->
        <div
          v-if="showHandle"
          class="sheet-handle-wrapper"
          @touchstart="onTouchStart"
          @touchmove="onTouchMove"
          @touchend="onTouchEnd"
          @mousedown="onMouseDown"
        >
          <div class="sheet-handle"></div>
        </div>

        <!-- Sheet Header (也绑定拖拽手势) -->
        <div
          class="sheet-header"
          @touchstart="onTouchStart"
          @touchmove="onTouchMove"
          @touchend="onTouchEnd"
          @mousedown="onMouseDown"
        >
          <slot name="header">
            <div class="sheet-title">{{ title }}</div>
            <button v-if="showClose" class="sheet-close-btn" @click.stop="close" aria-label="关闭">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width: 18px; height: 18px;">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
              </svg>
            </button>
          </slot>
        </div>

        <!-- Sheet Body 内容区 -->
        <div class="sheet-body" ref="bodyRef">
          <slot></slot>
        </div>

        <!-- Sheet Footer 底部沉底区 (例如 AI 输入框) -->
        <div v-if="$slots.footer" class="sheet-footer">
          <slot name="footer"></slot>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch, onBeforeUnmount } from 'vue'

const props = withDefaults(
  defineProps<{
    modelValue: boolean
    title?: string
    preset?: 'notification-empty' | 'notification' | 'agent' | 'custom'
    initialHeight?: string
    showHandle?: boolean
    showClose?: boolean
    closeOnBackdrop?: boolean
  }>(),
  {
    title: '',
    preset: 'custom',
    initialHeight: '',
    showHandle: true,
    showClose: true,
    closeOnBackdrop: true
  }
)

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'open'): void
  (e: 'close'): void
  (e: 'height-change', height: string): void
}>()

const sheetRef = ref<HTMLElement | null>(null)
const bodyRef = ref<HTMLElement | null>(null)

// 当前高度状态管理
const currentHeight = ref<string>('')
const isDragging = ref(false)
const dragStartY = ref(0)
const startHeightPx = ref(0)
const currentHeightPx = ref(0)

// 判定默认高度
const defaultHeight = computed(() => {
  if (props.initialHeight) return props.initialHeight
  switch (props.preset) {
    case 'notification-empty':
      return '32vh'
    case 'notification':
      return '55vh'
    case 'agent':
      return '60vh'
    default:
      return '50vh'
  }
})

const presetClass = computed(() => `preset-${props.preset}`)

const sheetStyle = computed(() => {
  if (isDragging.value && currentHeightPx.value > 0) {
    return {
      height: `${currentHeightPx.value}px`,
      maxHeight: '95vh',
      transition: 'none'
    }
  }
  return {
    height: currentHeight.value || defaultHeight.value,
    maxHeight: '95vh'
  }
})

// 动态 Backdrop 毛玻璃样式
const backdropStyle = computed(() => {
  let blur = '4px'
  let bg = 'rgba(15, 23, 42, 0.35)'
  
  if (props.preset === 'notification-empty' || props.preset === 'notification') {
    blur = '4px'
    bg = 'rgba(0, 0, 0, 0.35)'
  } else {
    const h = currentHeight.value || defaultHeight.value
    if (h === '95vh') {
      blur = '2px'
      bg = 'rgba(15, 23, 42, 0.2)'
    } else if (h === '80vh') {
      blur = '4px'
      bg = 'rgba(15, 23, 42, 0.35)'
    } else {
      blur = '6px'
      bg = 'rgba(15, 23, 42, 0.35)'
    }
  }
  return {
    backdropFilter: `blur(${blur})`,
    webkitBackdropFilter: `blur(${blur})`,
    background: bg,
    transition: 'all 0.25s cubic-bezier(0.25, 1, 0.5, 1)'
  }
})

// 广播全局事件，用于联动隐藏悬浮按钮 (FAB)
const notifyGlobalState = (isOpen: boolean) => {
  window.dispatchEvent(
    new CustomEvent('mobile-sheet-state-change', {
      detail: { isOpen }
    })
  )
}

watch(
  () => props.modelValue,
  (val) => {
    if (val) {
      currentHeight.value = defaultHeight.value
      notifyGlobalState(true)
      emit('open')
    } else {
      notifyGlobalState(false)
      emit('close')
    }
  },
  { immediate: true }
)

onBeforeUnmount(() => {
  if (props.modelValue) {
    notifyGlobalState(false)
  }
})

const close = () => {
  emit('update:modelValue', false)
}

// 智能背景点击逻辑
const handleBackdropClick = () => {
  if (!props.closeOnBackdrop) return
  const h = currentHeight.value || defaultHeight.value
  if (props.preset === 'agent' && (h === '80vh' || h === '95vh')) {
    currentHeight.value = '60vh'
    emit('height-change', '60vh')
  } else {
    close()
  }
}

// 触摸手势拖拽（支持上下拖动调节高度 / 下划关闭）
const onTouchStart = (e: TouchEvent) => {
  const target = e.target as HTMLElement
  if (target.closest('.sheet-close-btn') || target.closest('.chat-header-actions')) return
  
  if (e.touches.length !== 1) return
  isDragging.value = true
  dragStartY.value = e.touches[0].clientY
  if (sheetRef.value) {
    startHeightPx.value = sheetRef.value.getBoundingClientRect().height
    currentHeightPx.value = startHeightPx.value
  }
}

const onTouchMove = (e: TouchEvent) => {
  if (!isDragging.value) return
  const deltaY = e.touches[0].clientY - dragStartY.value
  let newHeight = startHeightPx.value - deltaY
  const windowHeight = window.innerHeight
  const minHeight = windowHeight * 0.2
  const maxHeight = windowHeight * 0.95

  if (newHeight < minHeight) newHeight = minHeight
  if (newHeight > maxHeight) newHeight = maxHeight

  currentHeightPx.value = newHeight
}

const onTouchEnd = () => {
  if (!isDragging.value) return
  isDragging.value = false
  const windowHeight = window.innerHeight
  const finalRatio = currentHeightPx.value / windowHeight

  // 下划超过 22% 屏幕高度则关掉
  if (finalRatio < 0.22) {
    close()
    return
  }

  // 智能吸附 Snap Points (60%, 80%, 95%)
  if (props.preset === 'agent') {
    if (finalRatio < 0.70) {
      currentHeight.value = '60vh'
    } else if (finalRatio < 0.88) {
      currentHeight.value = '80vh'
    } else {
      currentHeight.value = '95vh'
    }
  } else {
    if (finalRatio < 0.45) {
      currentHeight.value = '35vh'
    } else if (finalRatio < 0.72) {
      currentHeight.value = '60vh'
    } else if (finalRatio < 0.88) {
      currentHeight.value = '80vh'
    } else {
      currentHeight.value = '95vh'
    }
  }
  emit('height-change', currentHeight.value)
}

// 兼容 Mouse 拖拽（调试用）
const onMouseDown = (e: MouseEvent) => {
  const target = e.target as HTMLElement
  if (target.closest('.sheet-close-btn') || target.closest('.chat-header-actions')) return

  isDragging.value = true
  dragStartY.value = e.clientY
  if (sheetRef.value) {
    startHeightPx.value = sheetRef.value.getBoundingClientRect().height
    currentHeightPx.value = startHeightPx.value
  }
  const onMouseMove = (me: MouseEvent) => {
    if (!isDragging.value) return
    const deltaY = me.clientY - dragStartY.value
    let newHeight = startHeightPx.value - deltaY
    const windowHeight = window.innerHeight
    if (newHeight < windowHeight * 0.2) newHeight = windowHeight * 0.2
    if (newHeight > windowHeight * 0.95) newHeight = windowHeight * 0.95
    currentHeightPx.value = newHeight
  }
  const onMouseUp = () => {
    isDragging.value = false
    window.removeEventListener('mousemove', onMouseMove)
    window.removeEventListener('mouseup', onMouseUp)
    const windowHeight = window.innerHeight
    const finalRatio = currentHeightPx.value / windowHeight
    if (finalRatio < 0.22) {
      close()
      return
    }
    if (props.preset === 'agent') {
      if (finalRatio < 0.70) currentHeight.value = '60vh'
      else if (finalRatio < 0.88) currentHeight.value = '80vh'
      else currentHeight.value = '95vh'
    } else {
      if (finalRatio < 0.45) currentHeight.value = '35vh'
      else if (finalRatio < 0.72) currentHeight.value = '60vh'
      else if (finalRatio < 0.88) currentHeight.value = '80vh'
      else currentHeight.value = '95vh'
    }
    emit('height-change', currentHeight.value)
  }
  window.addEventListener('mousemove', onMouseMove)
  window.addEventListener('mouseup', onMouseUp)
}

// 暴露调高方法（供外部在打字或需要全屏时调用）
const setHeight = (heightStr: string) => {
  currentHeight.value = heightStr
  emit('height-change', heightStr)
}

defineExpose({
  setHeight
})
</script>

<style scoped>
/* 蒙层动画 */
.sheet-backdrop-enter-active,
.sheet-backdrop-leave-active {
  transition: opacity 0.22s ease;
}
.sheet-backdrop-enter-from,
.sheet-backdrop-leave-to {
  opacity: 0;
}

/* 滑动动画 */
.sheet-slide-enter-active {
  transition: transform 0.22s cubic-bezier(0.25, 1, 0.5, 1), opacity 0.22s ease;
}
.sheet-slide-leave-active {
  transition: transform 0.18s cubic-bezier(0.25, 1, 0.5, 1), opacity 0.18s ease;
}
.sheet-slide-enter-from,
.sheet-slide-leave-to {
  transform: translateY(100%);
  opacity: 0;
}

.mobile-sheet-backdrop {
  position: fixed;
  inset: 0;
  z-index: 2000;
}

.mobile-bottom-sheet {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2001;
  background: var(--bg-surface, #ffffff);
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  box-shadow: 0 -4px 24px rgba(15, 23, 42, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: height 0.25s cubic-bezier(0.25, 1, 0.5, 1);
  touch-action: none;
}

/* 拖拽 Indicator Handle 规范 */
.sheet-handle-wrapper {
  width: 100%;
  padding: 10px 0 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: grab;
  touch-action: none;
  flex-shrink: 0;
}
.sheet-handle {
  width: 46px;
  height: 5px;
  border-radius: 999px;
  background: #CBD5E1;
}

/* Header 统一样式 */
.sheet-header {
  padding: 4px 20px 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
  border-bottom: 1px solid var(--border-color-light, #f0f0f0);
  cursor: grab;
  touch-action: none;
}
.sheet-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary, #1d2129);
  line-height: 1.4;
}
.sheet-close-btn {
  background: none;
  border: none;
  padding: 6px;
  margin-right: -6px;
  cursor: pointer;
  color: var(--text-secondary, #86909c);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background 0.15s;
}
.sheet-close-btn:hover {
  background: rgba(0, 0, 0, 0.05);
}

/* Body 滚动区 */
.sheet-body {
  flex: 1;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  padding: 16px 20px;
}

/* Footer 沉底区 */
.sheet-footer {
  flex-shrink: 0;
  background: var(--bg-surface, #ffffff);
  border-top: 1px solid var(--border-color-light, #f0f0f0);
}

/* 在 Web 桌面端视口下 (width > 768px)，彻底隐藏底部抽屉与遮罩，避免与右上角浮窗重叠 */
@media (min-width: 769px) {
  .mobile-sheet-backdrop,
  .mobile-bottom-sheet {
    display: none !important;
  }
}
</style>
