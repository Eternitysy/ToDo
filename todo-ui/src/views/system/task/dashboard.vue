<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <!-- 左侧任务统计 -->
      <el-col :span="6">
        <el-card>
          <h3>任务统计</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="task-stat">
                <div class="task-title">未完成</div>
                <div class="task-count">{{ tasksCount.pending }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="task-stat">
                <div class="task-title">进行中</div>
                <div class="task-count">{{ tasksCount.inProgress }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="task-stat">
                <div class="task-title">已完成</div>
                <div class="task-count">{{ tasksCount.completed }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>

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
      </el-col>
    </el-row>

    <!-- 创建计划按钮 -->
    <el-button @click="goToCreatePlan" type="primary" style="margin-top: 20px;">制定计划</el-button>

    <!-- 任务日历 -->
    <div class="calendar-container">
      <h3 style="margin-top: 30px;">任务日历</h3>
      <el-calendar v-model="selectedDate">
      <div
        slot="dateCell"
        slot-scope="{ data }"
        @click="showTaskDetail(data)"
        v-popover:popover
      >
        <p>
          {{ data.day.split("-").slice(2).join()}}
        </p>
      </div>
      </el-calendar>
    </div>

    <!-- 任务详情对话框 -->
    <el-dialog :visible.sync="taskDetailDialogVisible" width="40%">
      <h3>{{ taskDetail.title }}</h3>
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
        { title: "任务1", deadline: "2025-02-15", priority: "高", status: "进行中" },
        { title: "任务2", deadline: "2023-10-16", priority: "中", status: "未完成" }
      ],
      selectedDate: '', // 设置为初始空值，点击后更新
      taskDates: [
        { date: '2023-10-15', isTask: true },
        { date: '2023-10-16', isTask: true }
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
      console.log(date)
      this.taskDetailDialogVisible = true;
      // 根据点击的日期找到任务
      this.selectedDate = date;  // 更新selectedDate
      const task = this.recentTasks.find(task => task.deadline === date);
      console.log(task)
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
  font-size: 16px;
  color: #666;
}

.task-count {
  font-size: 24px;
  font-weight: bold;
}

.calendar-container {
  margin-top: 30px;
  text-align: center;
}

.el-button {
  margin-top: 20px;
}
</style>
