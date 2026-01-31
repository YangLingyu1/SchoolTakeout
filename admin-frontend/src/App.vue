<template>
  <router-view v-if="!loading" />
  <div v-else class="loading">加载中...</div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const loading = ref(true)

onMounted(() => {
  // 模拟加载
  setTimeout(() => {
    loading.value = false
  }, 500)
})
</script>

<style scoped>
.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-size: 20px;
  color: #409eff;
}
</style>