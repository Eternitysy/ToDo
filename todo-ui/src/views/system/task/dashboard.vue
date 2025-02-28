<template>
  <div class="dashboard">
      <!-- 左侧任务统计 -->
      <el-col :span="6">
        <el-card style="width: 600px;height: 220px">
          <h2>任务统计</h2>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="task-stat">
                <div class="task-title">未完成</div>
                <div class="task-count"style="color: #ff4d51">{{ tasksCount.pending }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="task-stat">
                <div class="task-title">进行中</div>
                <div class="task-count"style="color: #1c84c6">{{ tasksCount.inProgress }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="task-stat">
                <div class="task-title">已完成</div>
                <div class="task-count"style="color: #1ab394">{{ tasksCount.completed }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
        <!-- 创建计划按钮 -->
        <div class="button-container">
          <el-button @click="goToCreatePlan" type="primary">制定计划</el-button>
        </div>
      </el-col>

    <!-- 任务日历 -->
    <div class="calendar-container custom-calendar" style="max-width: 800px;height: auto;margin-left: 800px">
      <!-- 右侧近期任务预览 -->
      <el-col :span="18">
        <el-card>
          <h3>近期任务预览</h3>
          <el-table :data="recentTasks" style="width: 100%">
            <el-table-column label="任务名称" prop="title"></el-table-column>
            <el-table-column label="截止日期" prop="deadline"></el-table-column>
            <el-table-column label="优先级" prop="priority"></el-table-column>
            <el-table-column label="状态" prop="status"></el-table-column>
          </el-table>
        </el-card>

      <h3 style="margin-top: 30px;">任务日历</h3>
          <el-calendar v-model="selectedDate">
            <div
              slot="dateCell"
              slot-scope="{ data }"
              @click="showTaskDetail(data)"
              v-popover:popover
            >
              <p style="font-size: 12px;margin-top: 2px">
                {{ data.day.split("-").slice(2).join()}}
              </p>
            </div>
          </el-calendar>
      </el-col>
    </div>

    <!-- 任务详情对话框 -->
    <el-dialog :visible.sync="taskDetailDialogVisible" width="500px">
      <h3>{{ taskDetail.taskName }}</h3>
      <p><strong>描述:</strong> {{ taskDetail.description }}</p>
      <p><strong>优先级:</strong> {{ taskDetail.priority }}</p>
      <p><strong>状态:</strong> {{ taskDetail.status }}</p>
      <el-button @click="closeDialog">关闭</el-button>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      tasksCount: {
        pending: 5,
        inProgress: 3,
        completed: 10
      },
      recentTasks: [
        { taskName: "任务1", deadline: "2025-02-15", priority: "高", status: "进行中" },
        { taskName: "任务2", deadline: "2023-10-16", priority: "中", status: "未开始" },
        { taskName: "任务3", deadline: "2025-02-17", priority: "中", status: "已完成" },
      ],
      selectedDate: '', // 设置为初始空值，点击后更新
      taskDates: [
        { date: '2025-02-15', isTask: true },
        { date: '2023-10-16', isTask: true },
        { date: '2025-02-17', isTask: true },
      ],
      taskDetail: {},
      taskDetailDialogVisible: false
    };
  },
  methods: {
    goToCreatePlan() {
      this.$router.push('/system/task/createPlan');
    },
    // 显示任务详情对话框
    showTaskDetail(date) {
      // 根据点击的日期找到任务
      this.selectedDate = date;  // 更新selectedDate
      const task = this.recentTasks.find(task => task.deadline === date.day);
      if (task) {
        this.taskDetail = task;
        this.taskDetailDialogVisible = true;
      } else {
        this.$message.warning("没有任务在此日期");
      }
    },
    closeDialog() {
      this.taskDetailDialogVisible = false;
    },
    getTasksForCalendar() {
      // 假设这里从API获取任务数据并进行处理，将任务日期与日历绑定
      // 例如：this.taskDates = [{date: "2023-10-15", isTask: true}, {date: "2023-10-16", isTask: false}]
    }
  },
  mounted() {
    // 获取任务数据并设置日历的任务标记
    this.getTasksForCalendar();
  }
};
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.task-stat {
  text-align: center;
}

.task-title {
  font-size: 25px;
  color: #666;
}

.task-count{
  padding-top: 15px;
  font-size: 40px;
  font-weight: bold;
}
.button-container {
  margin-top: 20px; /* 根据需要调整间距 */
  width: 100%; /* 确保按钮宽度适应父容器 */
  display: flex;
  justify-content: center; /* 水平居中 */
}

.calendar-container {
  text-align: center;
}
/* 调整日历单元格大小 */
.custom-calendar /deep/ .el-calendar-table .el-calendar-day {
  height: 30px !important;
}


.el-button {
  margin-top: 20px;
}
</style>
