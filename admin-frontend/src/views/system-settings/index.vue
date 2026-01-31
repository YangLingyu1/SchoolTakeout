<template>
  <div class="system-settings">
    <h2 class="page-title">系统设置</h2>

    <el-card class="settings-card">
      <template #header>
        <div class="card-header">
          <span>系统配置</span>
          <el-button type="primary" @click="saveSettings" :icon="'C'">
            保存设置
          </el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        label-width="150px"
      >
        <el-form-item label="客服电话">
          <el-input
            v-model="form.servicePhone"
            placeholder="请输入客服电话"
            style="width: 300px"
          />
        </el-form-item>

        <el-form-item label="客服企业微信">
          <el-input
            v-model="form.serviceWechat"
            placeholder="请输入客服企业微信号"
            style="width: 300px"
          />
        </el-form-item>

        <el-form-item label="系统名称">
          <el-input
            v-model="form.systemName"
            placeholder="请输入系统名称"
            style="width: 300px"
          />
        </el-form-item>

        <el-form-item label="最低起送金额">
          <el-input-number
            v-model="form.minOrderAmount"
            :min="0"
            :precision="2"
            :step="1"
            style="width: 200px"
          />
          <span style="margin-left: 10px; color: #909399">元</span>
        </el-form-item>

        <el-form-item label="配送费">
          <el-input-number
            v-model="form.deliveryFee"
            :min="0"
            :precision="2"
            :step="1"
            style="width: 200px"
          />
          <span style="margin-left: 10px; color: #909399">元</span>
        </el-form-item>

        <el-form-item label="免费配送门槛">
          <el-input-number
            v-model="form.freeDeliveryThreshold"
            :min="0"
            :precision="2"
            :step="1"
            style="width: 200px"
          />
          <span style="margin-left: 10px; color: #909399">元</span>
        </el-form-item>

        <el-form-item label="骑手收益计算">
          <el-input
            v-model="form.riderEarningsFormula"
            placeholder="例如: 0.1*楼层数 + 0.1*商品数"
            style="width: 400px"
          />
        </el-form-item>

        <el-form-item label="骑手等级设置">
          <div class="rider-levels">
            <div class="level-item">
              <el-tag type="info">普通骑手</el-tag>
              <span style="margin: 0 10px">同时配送</span>
              <el-input-number
                v-model="form.normalRiderMaxOrders"
                :min="1"
                :max="10"
                style="width: 100px"
              />
              <span style="margin-left: 10px">单</span>
            </div>
            <div class="level-item">
              <el-tag type="warning">白银骑手</el-tag>
              <span style="margin: 0 10px">完成</span>
              <el-input-number
                v-model="form.silverRiderThreshold"
                :min="1"
                style="width: 100px"
              />
              <span style="margin: 0 10px">单升级，同时配送</span>
              <el-input-number
                v-model="form.silverRiderMaxOrders"
                :min="1"
                :max="10"
                style="width: 100px"
              />
              <span style="margin-left: 10px">单</span>
            </div>
            <div class="level-item">
              <el-tag type="success">黄金骑手</el-tag>
              <span style="margin: 0 10px">完成</span>
              <el-input-number
                v-model="form.goldRiderThreshold"
                :min="1"
                style="width: 100px"
              />
              <span style="margin: 0 10px">单升级，同时配送</span>
              <el-input-number
                v-model="form.goldRiderMaxOrders"
                :min="1"
                :max="10"
                style="width: 100px"
              />
              <span style="margin-left: 10px">单</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="库存预警阈值">
          <el-input-number
            v-model="form.stockWarningThreshold"
            :min="0"
            style="width: 200px"
          />
          <span style="margin-left: 10px; color: #909399">低于此数值时提醒</span>
        </el-form-item>

        <el-form-item label="订单自动取消">
          <el-input-number
            v-model="form.orderAutoCancelMinutes"
            :min="0"
            style="width: 200px"
          />
          <span style="margin-left: 10px; color: #909399">分钟未支付自动取消</span>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'

const formRef = ref<FormInstance>()
const loading = ref<boolean>(false)

const form = reactive({
  servicePhone: '1234567890',
  serviceWechat: '',
  systemName: '校园外卖配送',
  minOrderAmount: 10,
  deliveryFee: 3,
  freeDeliveryThreshold: 30,
  riderEarningsFormula: '0.1*楼层数 + 0.1*商品数',
  normalRiderMaxOrders: 1,
  silverRiderThreshold: 10,
  silverRiderMaxOrders: 3,
  goldRiderThreshold: 50,
  goldRiderMaxOrders: 5,
  stockWarningThreshold: 10,
  orderAutoCancelMinutes: 15
})

const loadSettings = async () => {
  loading.value = true
  try {
    const response = await fetch('/api/admin/settings/all')
    const data = await response.json()
    
    if (data.code === 200) {
      const settings = data.data
      
      if (settings.service_phone) form.servicePhone = settings.service_phone
      if (settings.service_wechat) form.serviceWechat = settings.service_wechat
      if (settings.system_name) form.systemName = settings.system_name
      if (settings.min_order_amount) form.minOrderAmount = parseFloat(settings.min_order_amount)
      if (settings.delivery_fee) form.deliveryFee = parseFloat(settings.delivery_fee)
      if (settings.free_delivery_threshold) form.freeDeliveryThreshold = parseFloat(settings.free_delivery_threshold)
      if (settings.rider_earnings_formula) form.riderEarningsFormula = settings.rider_earnings_formula
      if (settings.normal_rider_max_orders) form.normalRiderMaxOrders = parseInt(settings.normal_rider_max_orders)
      if (settings.silver_rider_threshold) form.silverRiderThreshold = parseInt(settings.silver_rider_threshold)
      if (settings.silver_rider_max_orders) form.silverRiderMaxOrders = parseInt(settings.silver_rider_max_orders)
      if (settings.gold_rider_threshold) form.goldRiderThreshold = parseInt(settings.gold_rider_threshold)
      if (settings.gold_rider_max_orders) form.goldRiderMaxOrders = parseInt(settings.gold_rider_max_orders)
      if (settings.stock_warning_threshold) form.stockWarningThreshold = parseInt(settings.stock_warning_threshold)
      if (settings.order_auto_cancel_minutes) form.orderAutoCancelMinutes = parseInt(settings.order_auto_cancel_minutes)
    }
  } catch (err: any) {
    ElMessage.error('获取设置失败')
  } finally {
    loading.value = false
  }
}

const saveSettings = async () => {
  try {
    const settings = [
      {
        key: 'service_phone',
        value: form.servicePhone,
        description: '客服电话'
      },
      {
        key: 'service_wechat',
        value: form.serviceWechat,
        description: '客服企业微信'
      },
      {
        key: 'system_name',
        value: form.systemName,
        description: '系统名称'
      },
      {
        key: 'min_order_amount',
        value: form.minOrderAmount.toString(),
        description: '最低起送金额'
      },
      {
        key: 'delivery_fee',
        value: form.deliveryFee.toString(),
        description: '配送费'
      },
      {
        key: 'free_delivery_threshold',
        value: form.freeDeliveryThreshold.toString(),
        description: '免费配送门槛'
      },
      {
        key: 'rider_earnings_formula',
        value: form.riderEarningsFormula,
        description: '骑手收益计算公式'
      },
      {
        key: 'normal_rider_max_orders',
        value: form.normalRiderMaxOrders.toString(),
        description: '普通骑手最大同时配送订单数'
      },
      {
        key: 'silver_rider_threshold',
        value: form.silverRiderThreshold.toString(),
        description: '白银骑手升级门槛'
      },
      {
        key: 'silver_rider_max_orders',
        value: form.silverRiderMaxOrders.toString(),
        description: '白银骑手最大同时配送订单数'
      },
      {
        key: 'gold_rider_threshold',
        value: form.goldRiderThreshold.toString(),
        description: '黄金骑手升级门槛'
      },
      {
        key: 'gold_rider_max_orders',
        value: form.goldRiderMaxOrders.toString(),
        description: '黄金骑手最大同时配送订单数'
      },
      {
        key: 'stock_warning_threshold',
        value: form.stockWarningThreshold.toString(),
        description: '库存预警阈值'
      },
      {
        key: 'order_auto_cancel_minutes',
        value: form.orderAutoCancelMinutes.toString(),
        description: '订单自动取消时间'
      }
    ]

    for (const setting of settings) {
      await fetch('/api/admin/settings/update', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(setting)
      })
    }

    ElMessage.success('保存成功')
  } catch (err: any) {
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.system-settings {
  padding: 20px 0;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #303133;
}

.settings-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
}

.rider-levels {
  width: 100%;
}

.level-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}
</style>