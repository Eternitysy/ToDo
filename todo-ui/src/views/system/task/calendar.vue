<template>
  <div class="calendar">
    <el-calendar v-model="selectedDate" :dates="taskDates" />
    <el-dialog :visible.sync="taskDetailDialogVisible">
      <p>{{ taskDetail.title }}</p>
      <p>{{ taskDetail.description }}</p>
      <el-button @click="closeDialog">关闭</el-button>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      selectedDate: '',
      taskDates: [],
      taskDetail: {},
      taskDetailDialogVisible: false
    };
  },
  methods: {
    getTasksForCalendar() {
      // 获取任务数据API
    },
    showTaskDetail(task) {
      this.taskDetail = task;
      this.taskDetailDialogVisible = true;
    },
    closeDialog() {
      this.taskDetailDialogVisible = false;
    }
  },
  mounted() {
    this.getTasksForCalendar();
  }
};
</script>

