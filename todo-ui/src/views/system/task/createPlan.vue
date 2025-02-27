<template>
  <div class="createPlan">
    <el-form :model="planForm">
      <el-form-item label="学习目标">
        <el-input v-model="planForm.goal" placeholder="例如：在30天内完成学习计划"></el-input>
      </el-form-item>

      <el-form-item label="计划天数">
        <el-input-number v-model="planForm.days" :min="1" :max="365" label="天数"></el-input-number>
      </el-form-item>

      <el-button type="primary" @click="generatePlan">生成计划</el-button>
    </el-form>

    <el-divider />

    <h3>任务拆解</h3>
    <el-table :data="tasks">
      <el-table-column label="任务" prop="task" />
    </el-table>

    <el-divider />

    <h3>调整任务</h3>
    <el-button @click="adjustTasks">调整任务</el-button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      planForm: {
        goal: '',
        days: 30
      },
      tasks: []
    };
  },
  methods: {
    async generatePlan() {
      // 调用后端拆解任务接口
      try {
        const response = await this.$axios.post('/api/plan/split', this.planForm);
        this.tasks = response.data;
      } catch (error) {
        this.$message.error('生成任务失败');
      }
    },
    adjustTasks() {
      // 提供任务调整界面或操作
      this.$message.info('调整任务功能尚未实现');
    }
  }
};
</script>
