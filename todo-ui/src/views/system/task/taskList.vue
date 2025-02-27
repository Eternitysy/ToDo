<template>
  <div class="taskList">
    <el-row class="task-filter">
      <el-col :span="4">
        <el-button type="primary" @click="newTask">新建任务</el-button>
      </el-col>
      <el-col :span="4" offset="16">
        <el-input v-model="searchQuery" placeholder="搜索任务..." clearable></el-input>
      </el-col>
    </el-row>
    <el-table :data="tasks" style="width: 100%">
      <el-table-column label="任务名称" prop="title"></el-table-column>
      <el-table-column label="截止日期" prop="deadline"></el-table-column>
      <el-table-column label="优先级" prop="priority"></el-table-column>
      <el-table-column label="状态" prop="status"></el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button @click="editTask(scope.row)" type="text">编辑</el-button>
          <el-button @click="deleteTask(scope.row)" type="text">删除</el-button>
          <el-button @click="markComplete(scope.row)" type="text">标记完成</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchQuery: "",
      tasks: [
        { title: "任务1", deadline: "2023-10-15", priority: "高", status: "未完成" },
        { title: "任务2", deadline: "2023-10-16", priority: "中", status: "进行中" }
      ]
    };
  },
  methods: {
    newTask() {
      this.$router.push('/task-create');
    },
    editTask(task) {
      this.$router.push({ name: 'TaskDetail', params: { id: task.id } });
    },
    deleteTask(task) {
      this.tasks = this.tasks.filter(t => t.id !== task.id);
    },
    markComplete(task) {
      task.status = "已完成";
    }
  }
};
</script>

<style scoped>
.task-filter {
  margin-bottom: 20px;
}
</style>
