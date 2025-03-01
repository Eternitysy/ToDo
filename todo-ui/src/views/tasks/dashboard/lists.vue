<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <!-- 左侧任务统计和创建计划按钮 -->
      <el-col :span="10">
        <el-card style="height: 220px">
          <h2>任务统计</h2>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="task-stat">
                <div class="task-title">未完成</div>
                <div class="task-count" style="color: #ff4d51">{{ tasksCount.pending }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="task-stat">
                <div class="task-title">进行中</div>
                <div class="task-count" style="color: #1c84c6">{{ tasksCount.inProgress }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="task-stat">
                <div class="task-title">已完成</div>
                <div class="task-count" style="color: #1ab394">{{ tasksCount.completed }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
        <div class="button-container">
          <el-button @click="goToCreatePlan" type="primary">制定计划</el-button>
        </div>
      </el-col>

      <!-- 右侧任务预览与日历 -->
      <el-col :span="13">
        <el-card>
          <h3>近期任务预览</h3>
          <el-table :data="recentTasksList" style="width: 100%">
            <el-table-column prop="taskName" label="任务名称" style="width: 40%"></el-table-column>
            <el-table-column prop="orderNum" label="排序" style="width: 15%"></el-table-column>
            <el-table-column prop="deadline" label="截止时间" style="width: 30%"></el-table-column>
            <el-table-column label="状态" style="width: 15%">
              <template slot-scope="scope">
                <span>{{ scope.row.status }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 任务日历 -->
        <div class="calendar-container custom-calendar">
          <h3 style="margin-top: 30px;">任务日历</h3>
          <el-calendar v-model="selectedDate">
            <template slot="dateCell" slot-scope="{ data }">
              <div class="date-cell" @click="showTaskDetail(data)">
                <p class="cell-day">{{ data.day.split("-").slice(2).join()}}</p>
                <span v-if="hasTask(data.day)" class="dot"></span>
              </div>
            </template>
          </el-calendar>
        </div>
      </el-col>
    </el-row>

    <!-- 任务详情对话框 -->
    <el-dialog :visible.sync="taskDetailDialogVisible" width="500px">
      <h3>{{ taskDetail.taskName }}</h3>
      <p><strong>任务描述:</strong> {{ taskDetail.description }}</p>
      <p><strong>优先级:</strong> {{ taskDetail.orderNum }}</p>
      <p><strong>截止日期:</strong> {{ taskDetail.deadline }}</p>
      <p><strong>状态:</strong> {{ taskDetail.status }}</p>
      <el-button @click="closeDialog">关闭</el-button>
    </el-dialog>
  </div>
</template>

<script>
import { listTask } from "@/api/tasks/task";

export default {
  data() {
    return {
      tasksCount: {
        pending: 0,
        inProgress: 0,
        completed: 0
      },
      // 处理后的最高父级任务数组
      recentTasksList: [],
      // 日历选中的日期，初始值为当前日期
      selectedDate: new Date(),
      // 存储所有任务截止日期的数组（格式："YYYY-MM-DD"）
      calendarTaskDates: [],
      taskDetail: {},
      taskDetailDialogVisible: false,
      queryParams: {} // 根据需要传递参数
    };
  },
  created() {
    this.getTaskList();
  },
  methods: {
    goToCreatePlan() {
      this.$router.push('tasks/ai/chat');
    },
    // 获取任务列表，并手动处理为最高父级任务数组
    getTaskList() {
      listTask(this.queryParams).then(response => {
        // 假设 response.data 是一个对象数组（树形结构）
        const rawTasks = response.data;
        // 手动处理，筛选出 parentId 为空、null 或 0 的任务，即最高级任务
        const processedTasks = this.handleTasks(rawTasks);
        console.log(rawTasks)
        console.log(processedTasks)
        this.recentTasksList = processedTasks;
        // 提取截止日期用于日历标记
        this.calendarTaskDates = rawTasks.map(task => this.formatDate(task.deadline));
        // 统计任务状态（根据实际字段调整）
        this.tasksCount.pending = rawTasks.filter(task => task.status === '-1').length;
        this.tasksCount.inProgress = processedTasks.filter(task => task.status === '1').length;
        this.tasksCount.completed = processedTasks.filter(task => task.status === '0').length;
      });
    },
    // 处理任务数据，筛选出最高父级任务
    handleTasks(tasks) {
      return tasks.filter(task => !task.parentId || task.parentId === 0 || task.parentId === "0");
    },
    // 将 Date 对象转换为 "YYYY-MM-DD" 格式字符串
    formatDate(date) {
      const d = new Date(date);
      const year = d.getFullYear();
      let month = (d.getMonth() + 1).toString();
      let day = d.getDate().toString();
      if (month.length < 2) month = '0' + month;
      if (day.length < 2) day = '0' + day;
      return `${year}-${month}-${day}`;
    },
    // 判断指定日期是否有任务
    hasTask(dateStr) {
      return this.calendarTaskDates.includes(dateStr);
    },
    // 显示任务详情对话框，根据点击的日历单元格数据
    showTaskDetail(date) {
      this.selectedDate = date; // 更新选中日期
      // 查找截止日期等于选中日期的任务，取第一个匹配的任务
      const task = this.recentTasksList.find(task => this.formatDate(task.deadline) === date.day);
      if (task) {
        this.taskDetail = task;
        this.taskDetailDialogVisible = true;
      } else {
        this.$message.warning("该日期暂无任务");
      }
    },
    closeDialog() {
      this.taskDetailDialogVisible = false;
    }
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

.task-count {
  padding-top: 15px;
  font-size: 40px;
  font-weight: bold;
}

.button-container {
  margin-top: 20px;
  width: 100%;
  display: flex;
  justify-content: center;
}

.calendar-container {
  margin-top: 30px;
  text-align: center;
}

.custom-calendar ::v-deep .el-calendar-table .el-calendar-day {
  height: 40px !important;
  position: relative;
}

.date-cell {
  cursor: pointer;
  position: relative;
}

.cell-day {
  font-size: 13px;
  font-weight: bold;
  margin: 0;
  text-align: center;
}

/* 圆点标记 */
.dot {
  width: 6px;
  height: 6px;
  background-color: #ff4d51;
  border-radius: 50%;
  display: inline-block;
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
}
</style>
