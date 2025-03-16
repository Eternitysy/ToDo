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
        <div class="button-container" style="padding-top: 3px">
          <el-button @click="goToCreatePlan" type="primary">Ai智能制定计划</el-button>
          <el-button @click="goToCreatePlanBySelf" type="success" style="margin-left: 40px">个人手动制定计划</el-button>
        </div>
        <!-- 任务日历 -->
        <el-card style="margin-top: 30px">
          <div class="calendar-container custom-calendar">
            <h3>任务日历</h3>
            <el-calendar v-model="selectedDate">
              <template slot="dateCell" slot-scope="{ data }">
                <div class="date-cell" @click="showTaskDetail(data)">
                  <p class="cell-day">{{ data.day.split("-").slice(2).join() }}</p>
                  <span v-if="hasTask(data.day)" class="dot"></span>
                </div>
              </template>
            </el-calendar>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧任务预览与日历 -->
      <el-col :span="13">
        <!-- 在右侧el-col内添加时间模块 -->
        <el-col :xl="14" :lg="24" :md="24" :sm="24" class="right-panel">
          <!-- 新增时间显示卡片 -->
          <el-card class="time-card" shadow="never" style="height: 220px">
            <div class="time-container">
              <div class="time-content">
                <span class="date">{{ currentDate }}</span>
                <span class="time">{{ currentTime }}</span>
                <span class="week">{{ currentWeek }}</span>
              </div>
              <i class="el-icon-alarm-clock clock-icon"></i>
            </div>
          </el-card>
          <el-card style="margin-top: 90px">
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
        </el-col>
      </el-col>
    </el-row>

    <!-- 任务详情对话框 -->
    <el-dialog :visible.sync="taskDetailDialogVisible" width="500px">
      <h3>{{ taskDetail.taskName }}</h3>
      <p><strong>任务描述:</strong> {{ taskDetail.description }}</p>
      <p><strong>优先级:</strong> {{ taskDetail.orderNum }}</p>
      <p><strong>开始日期:</strong> {{ taskDetail.startTime }}</p>
      <p><strong>截止日期:</strong> {{ taskDetail.deadline }}</p>
      <p><strong>状态:</strong> {{ taskDetail.status }}</p>
      <el-button @click="closeDialog">关闭</el-button>
    </el-dialog>
  </div>
</template>

<script>
import {listTask} from "@/api/tasks/task";

export default {
  data() {
    return {
      currentTime: '',
      currentDate: '',
      currentWeek: '',
      timer: null,
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
  mounted() {
    this.updateTime()
    this.timer = setInterval(this.updateTime, 1000)
  },

  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },

  methods: {
    updateTime() {
      const now = new Date()
      this.currentTime = this.formatDateTime(now, 'HH:mm:ss')
      this.currentDate = this.formatDateTime(now, 'YYYY年MM月DD日')
      // this.currentWeek = formatDateTime(now, 'WW')
      this.currentWeek = '星期' + ['日', '一', '二', '三', '四', '五', '六'][now.getDay()]
    },
    goToCreatePlan() {
      this.$router.push('tasks/ai');
    },
    goToCreatePlanBySelf() {
      this.$router.push('tasks/task');
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
        this.tasksCount.pending = rawTasks.filter(task => task.status === '0').length;
        this.tasksCount.inProgress = processedTasks.filter(task => task.status === '1').length;
        this.tasksCount.completed = processedTasks.filter(task => task.status === '2').length;
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
    // 时间日期格式化工具函数
    formatDateTime(date = new Date(), format = 'YYYY-MM-DD HH:mm:ss') {
      // 补零函数
      const padZero = num => num.toString().padStart(2, '0')

      // 星期映射
      const weekMap = ['日', '一', '二', '三', '四', '五', '六']

      // 提取时间组件
      const year = date.getFullYear()
      const month = padZero(date.getMonth() + 1)
      const day = padZero(date.getDate())
      const hours = padZero(date.getHours())
      const minutes = padZero(date.getMinutes())
      const seconds = padZero(date.getSeconds())
      const week = weekMap[date.getDay()]

      // 替换格式标记
      return format
        .replace(/YYYY/g, year)
        .replace(/MM/g, month)
        .replace(/DD/g, day)
        .replace(/HH/g, hours)
        .replace(/mm/g, minutes)
        .replace(/ss/g, seconds)
        .replace(/WW/g, `星期${week}`)
        .replace(/W/g, week)
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
  text-align: center;
  height: 295px;
}

.custom-calendar ::v-deep .el-calendar-table .el-calendar-day {
  height: 26px !important;
  position: relative;
}

.date-cell {
  cursor: pointer;
  position: relative;
}

.cell-day {
  font-size: 10px;
  font-weight: bold;
  margin: 0;
  text-align: center;
}

/* 圆点标记 */
.dot {
  width: 5px;
  height: 5px;
  background-color: #ff4d51;
  border-radius: 50%;
  display: inline-block;
  position: absolute;
  top: 11px;
  left: 50%;
  transform: translateX(-50%);
}

.time-card {
  margin-bottom: 24px;
  background: linear-gradient(135deg, #accfec, #bde8a3);
  color: white;

  .time-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;

    .time-content {
      display: flex;
      flex-direction: column;

      .date {
        font-size: 32px;
        margin-bottom: 8px;
      }

      .time {
        font-size: 50px;
        font-weight: bold;
        letter-spacing: 2px;
      }

      .week {
        font-size: 30px;
        margin-top: 8px;
        opacity: 0.9;
      }
    }

    .clock-icon {
      font-size: 100px;
      opacity: 0.5;
      margin-left: 30px;
      color: #1B1F22;
    }
  }
}

</style>
