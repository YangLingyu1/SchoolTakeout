<template>
  <div class="finance">
    <h2 class="page-title">财务管理</h2>

    <el-tabs v-model="activeTab" class="finance-tabs">
      <el-tab-pane label="收入统计" name="income">
        <IncomeStatistics />
      </el-tab-pane>
      <el-tab-pane label="骑手结算" name="settlement">
        <RiderSettlement />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import IncomeStatistics from './components/IncomeStatistics.vue'
import RiderSettlement from './components/RiderSettlement.vue'

const activeTab = ref('income')
</script>

<style scoped>
.finance {
  padding: 20px 0;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #303133;
}

.finance-tabs {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style>