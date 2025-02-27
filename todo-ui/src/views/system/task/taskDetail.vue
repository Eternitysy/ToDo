<template>
  <div class="taskDetail">
    <el-form :model="task" label-width="120px">
      <el-form-item label="任务名称">
        <el-input v-model="task.title"></el-input>
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="task.description" type="textarea"></el-input>
      </el-form-item>
      <el-form-item label="截止日期">
        <el-date-picker v-model="task.deadline" type="date" placeholder="选择日期"></el-date-picker>
      </el-form-item>
      <el-form-item label="优先级">
        <el-select v-model="task.priority" placeholder="选择优先级">
          <el-option label="高" value="高"></el-option>
          <el-option label="中" value="中"></el-option>
          <el-option label="低" value="低"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="task.status" placeholder="选择状态">
          <el-option label="未完成" value="未完成"></el-option>
          <el-option label="进行中" value="进行中"></el-option>
          <el-option label="已完成" value="已完成"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="saveTask">保存</el-button>
      </el-form-item>
    </el-form>

    <el-divider></el-divider>

    <h3>智能建议</h3>
    <el-card>
      <el-button @click="getSuggestion" type="primary">获取优化建议</el-button>
      <p v-if="suggestion">{{ suggestion }}</p>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      task: {
        title: "任务1",
        description: "任务描述",
        deadline: "2023-10-15",
        priority: "高",
        status: "未完成"
      },
      suggestion: ""
    };
  },
  methods: {
    saveTask() {
      console.log('任务已保存', this.task);
    },
    async getSuggestion() {
      try {
        const response = await this.$axios.post('/api/ai/suggestion', this.task.description);
        this.suggestion = response.data;
      } catch (error) {
        console.error("获取建议失败", error);
      }
    }
  }
};
</script>

<style scoped>
.task-detail {
  padding: 20px;
}
</style>
