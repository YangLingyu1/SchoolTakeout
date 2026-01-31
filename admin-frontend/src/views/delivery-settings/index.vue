<template>
  <div class="delivery-settings">
    <h2 class="page-title">配送设置</h2>

    <el-tabs v-model="activeTab" class="delivery-tabs">
      <el-tab-pane label="配送范围" name="range">
        <DeliveryRange />
      </el-tab-pane>
      <el-tab-pane label="营业时间" name="hours">
        <BusinessHours />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import DeliveryRange from './components/DeliveryRange.vue'
import BusinessHours from './components/BusinessHours.vue'

const activeTab = ref('range')
</script>

<style scoped>
.delivery-settings {
  padding: 20px 0;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #303133;
}

.delivery-tabs {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style>