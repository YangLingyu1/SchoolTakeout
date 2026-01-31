<template>
  <div class="business-hours">
    <el-card class="hours-card">
      <template #header>
        <div class="card-header">
          <span>营业时间设置</span>
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
        <el-form-item label="每日营业时间">
          <el-time-picker
            v-model="form.startTime"
            placeholder="开始时间"
            format="HH:mm"
            value-format="HH:mm"
            style="margin-right: 10px"
          />
          <span>至</span>
          <el-time-picker
            v-model="form.endTime"
            placeholder="结束时间"
            format="HH:mm"
            value-format="HH:mm"
            style="margin-left: 10px"
          />
        </el-form-item>

        <el-divider />

        <el-form-item label="特殊时段配送">
          <div class="special-hours">
            <div
              v-for="(item, index) in form.specialHours"
              :key="index"
              class="special-hour-item"
            >
              <el-select
                v-model="item.dayOfWeek"
                placeholder="选择星期"
                style="width: 150px; margin-right: 10px"
              >
                <el-option label="全部" value="all" />
                <el-option label="周一" value="1" />
                <el-option label="周二" value="2" />
                <el-option label="周三" value="3" />
                <el-option label="周四" value="4" />
                <el-option label="周五" value="5" />
                <el-option label="周六" value="6" />
                <el-option label="周日" value="0" />
              </el-select>
              <el-time-picker
                v-model="item.startTime"
                placeholder="开始时间"
                format="HH:mm"
                value-format="HH:mm"
                style="margin-right: 10px"
              />
              <span>至</span>
              <el-time-picker
                v-model="item.endTime"
                placeholder="结束时间"
                format="HH:mm"
                value-format="HH:mm"
                style="margin-left: 10px; margin-right: 10px"
              />
              <el-button
                type="danger"
                size="small"
                @click="removeSpecialHour(index)"
                :icon="'D'"
              >
                删除
              </el-button>
            </div>
            <el-button type="primary" @click="addSpecialHour" :icon="'P'">
              添加特殊时段
            </el-button>
          </div>
        </el-form-item>

        <el-divider />

        <el-form-item label="夜间配送时段">
          <el-time-picker
            v-model="form.nightStartTime"
            placeholder="夜间开始时间"
            format="HH:mm"
            value-format="HH:mm"
            style="margin-right: 10px"
          />
          <span>至</span>
          <el-time-picker
            v-model="form.nightEndTime"
            placeholder="夜间结束时间"
            format="HH:mm"
            value-format="HH:mm"
            style="margin-left: 10px"
          />
        </el-form-item>

        <el-form-item label="夜间配送说明">
          <el-input
            v-model="form.nightDeliveryRemark"
            type="textarea"
            rows="3"
            placeholder="请输入夜间配送说明"
          />
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
  startTime: '08:00',
  endTime: '22:00',
  nightStartTime: '22:00',
  nightEndTime: '06:00',
  nightDeliveryRemark: '夜间配送仅限指定楼栋',
  specialHours: [
    {
      dayOfWeek: 'all',
      startTime: '12:00',
      endTime: '14:00'
    }
  ]
})

const loadSettings = async () => {
  loading.value = true
  try {
    const response = await fetch('/api/admin/settings/all')
    const data = await response.json()
    
    if (data.code === 200) {
      const settings = data.data
      if (settings.business_hours) {
        const hours = JSON.parse(settings.business_hours)
        form.startTime = hours.startTime || '08:00'
        form.endTime = hours.endTime || '22:00'
      }
      if (settings.night_hours) {
        const night = JSON.parse(settings.night_hours)
        form.nightStartTime = night.startTime || '22:00'
        form.nightEndTime = night.endTime || '06:00'
      }
      if (settings.special_hours) {
        form.specialHours = JSON.parse(settings.special_hours)
      }
      if (settings.night_delivery_remark) {
        form.nightDeliveryRemark = settings.night_delivery_remark
      }
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
        key: 'business_hours',
        value: JSON.stringify({
          startTime: form.startTime,
          endTime: form.endTime
        }),
        description: '每日营业时间'
      },
      {
        key: 'night_hours',
        value: JSON.stringify({
          startTime: form.nightStartTime,
          endTime: form.nightEndTime
        }),
        description: '夜间配送时段'
      },
      {
        key: 'special_hours',
        value: JSON.stringify(form.specialHours),
        description: '特殊时段配送'
      },
      {
        key: 'night_delivery_remark',
        value: form.nightDeliveryRemark,
        description: '夜间配送说明'
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

const addSpecialHour = () => {
  form.specialHours.push({
    dayOfWeek: 'all',
    startTime: '',
    endTime: ''
  })
}

const removeSpecialHour = (index: number) => {
  form.specialHours.splice(index, 1)
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.business-hours {
  padding: 20px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
}

.special-hours {
  width: 100%;
}

.special-hour-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
</style>