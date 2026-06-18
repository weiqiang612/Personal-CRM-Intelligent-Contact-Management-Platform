<template>
  <div class="placeholder-container">
    <div class="glow-bg"></div>
    <div class="glass-card">
      <div class="icon-wrapper">
        <div class="pulse-ring"></div>
        <el-icon class="rocket-icon"><Notification /></el-icon>
      </div>
      <h2 class="module-title">《{{ routeTitle }}》 模块建设中</h2>
      <p class="module-desc">
        本功能属于系统的 <strong>Phase 2 (核心业务完善)</strong> 阶段。当前已完成数据表设计、API 契约制定以及前端路由绑定，我们正在加紧实现完整的前后端闭环。
      </p>
      <div class="roadmap-preview">
        <div class="roadmap-step completed">
          <span class="step-dot"></span>
          <span class="step-text">Stage 1: 基础设施与鉴权 (当前)</span>
        </div>
        <div class="roadmap-step active">
          <span class="step-dot"></span>
          <span class="step-text">Stage 2: 业务功能实现 (即将推出)</span>
        </div>
        <div class="roadmap-step">
          <span class="step-dot"></span>
          <span class="step-text">Stage 3: Agent 查询与写操作流</span>
        </div>
      </div>
      <el-button type="primary" class="back-btn" @click="goHome">返回看板</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Notification } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const routeTitle = computed(() => (route.meta.title as string) || '未知')

const goHome = () => {
  router.push('/dashboard')
}
</script>

<style scoped>
.placeholder-container {
  position: relative;
  width: 100%;
  height: calc(100vh - 120px);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background-color: #fafbfc;
}

/* 渐变光晕背景 */
.glow-bg {
  position: absolute;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.15) 0%, rgba(244, 63, 94, 0.05) 50%, rgba(255, 255, 255, 0) 100%);
  filter: blur(40px);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
}

/* 现代毛玻璃卡片 */
.glass-card {
  position: relative;
  z-index: 2;
  width: 90%;
  max-width: 520px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 24px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.04), 0 1px 3px rgba(0, 0, 0, 0.02);
  text-align: center;
}

/* 脉冲光环 */
.icon-wrapper {
  position: relative;
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pulse-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background-color: rgba(99, 102, 241, 0.1);
  animation: pulse 2s infinite ease-in-out;
}

.rocket-icon {
  font-size: 36px;
  color: #6366f1;
}

.module-title {
  font-size: 22px;
  color: #1e293b;
  font-weight: 700;
  margin-bottom: 16px;
  letter-spacing: -0.5px;
}

.module-desc {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 30px;
}

/* 路线预览样式 */
.roadmap-preview {
  background: rgba(248, 250, 252, 0.8);
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 32px;
  text-align: left;
  border: 1px solid #f1f5f9;
}

.roadmap-step {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 13px;
  color: #94a3b8;
}

.roadmap-step:last-child {
  margin-bottom: 0;
}

.step-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #cbd5e1;
  margin-right: 12px;
}

.step-text {
  font-weight: 500;
}

/* 激活与完成状态 */
.roadmap-step.completed {
  color: #10b981;
}
.roadmap-step.completed .step-dot {
  background-color: #10b981;
  box-shadow: 0 0 8px rgba(16, 185, 129, 0.4);
}

.roadmap-step.active {
  color: #6366f1;
}
.roadmap-step.active .step-dot {
  background-color: #6366f1;
  box-shadow: 0 0 10px rgba(99, 102, 241, 0.5);
  animation: scale-up 1.5s infinite ease-in-out;
}

.back-btn {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%) !important;
  border: none !important;
  padding: 12px 30px !important;
  height: auto !important;
  font-weight: 600 !important;
  border-radius: 12px !important;
  box-shadow: 0 4px 14px rgba(99, 102, 241, 0.3);
  transition: all 0.2s;
}

.back-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(99, 102, 241, 0.4);
}

@keyframes pulse {
  0% {
    transform: scale(0.9);
    opacity: 1;
  }
  100% {
    transform: scale(1.4);
    opacity: 0;
  }
}

@keyframes scale-up {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.3);
  }
}
</style>
