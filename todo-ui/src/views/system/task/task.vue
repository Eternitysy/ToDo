<template>
  <el-table :data="tasks">
    <el-table-column label="任务名称" prop="taskName"></el-table-column>
    <el-table-column label="截止日期" prop="deadline"></el-table-column>
    <el-table-column label="优先级" prop="priority"></el-table-column>
    <el-table-column label="状态" prop="status"></el-table-column>
    <el-table-column label="操作">
      <template slot-scope="scope">
        <el-button @click="editTask(scope.row)">Edit</el-button>
        <el-button @click="deleteTask(scope.row)">Delete</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
import { listTask, getTask, delTask, addTask, updateTask, runTask, changeTAskStatus } from "@/api/system/task";

export default {
  data() {
    return {
      loading:true,
      total:0,
      tasks: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        taskName: undefined,
        taskGroup: undefined,
        status: undefined
      },
    };
  },
  created() {
    this.getTasks();
  },
  methods: {
    getTasks() {
      this.loading = true;
      listTask(this.queryParams).then(response => {
        this.tasks = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    editTask(task) {
      this.$router.push({ name: 'EditTask', params: { id: task.id } });
    },
    async deleteTask(task) {
      try {
        await this.$axios.delete(`/task/delete/${task.id}`);
        this.getTasks();
      } catch (error) {
        this.$message.error('Failed to delete task');
      }
    }
  }
};
</script>
