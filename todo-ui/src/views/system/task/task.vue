<template>
  <el-table :data="tasks">
    <el-table-column label="Title" prop="title"></el-table-column>
    <el-table-column label="Priority" prop="priority"></el-table-column>
    <el-table-column label="Status" prop="status"></el-table-column>
    <el-table-column label="Deadline" prop="deadline"></el-table-column>
    <el-table-column label="Actions">
      <template slot-scope="scope">
        <el-button @click="editTask(scope.row)">Edit</el-button>
        <el-button @click="deleteTask(scope.row)">Delete</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
export default {
  data() {
    return {
      tasks: []
    };
  },
  mounted() {
    this.getTasks();
  },
  methods: {
    async getTasks() {
      try {
        const response = await this.$axios.get('/api/tasks/list');
        this.tasks = response.data;
      } catch (error) {
        this.$message.error('Failed to load tasks');
      }
    },
    editTask(task) {
      this.$router.push({ name: 'EditTask', params: { id: task.id } });
    },
    async deleteTask(task) {
      try {
        await this.$axios.delete(`/api/tasks/${task.id}`);
        this.getTasks();
      } catch (error) {
        this.$message.error('Failed to delete task');
      }
    }
  }
};
</script>
