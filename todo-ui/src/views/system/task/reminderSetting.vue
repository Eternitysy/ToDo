<template>
  <div class="reminderSetting">
    <el-form :model="reminderForm">
      <el-form-item label="提醒频率">
        <el-select v-model="reminderForm.frequency" placeholder="选择提醒频率">
          <el-option label="每日" value="daily" />
          <el-option label="每周" value="weekly" />
          <el-option label="每月" value="monthly" />
        </el-select>
      </el-form-item>

      <el-form-item label="提醒时间">
        <el-time-picker v-model="reminderForm.reminderTime" placeholder="选择提醒时间" />
      </el-form-item>

      <el-button type="primary" @click="saveReminder">保存设置</el-button>
    </el-form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      reminderForm: {
        frequency: 'daily',
        reminderTime: '09:00'
      }
    };
  },
  methods: {
    saveReminder() {
      // 保存提醒设置
      this.$message.success('提醒设置保存成功');
    }
  }
};
</script>
