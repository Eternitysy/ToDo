<template>
  <div class="app-container">
    <el-row :gutter="20" class="main-content">
      <!-- 左侧聊天区域 -->
      <el-col :span="14" class="chat-container">
        <el-card class="chat-card">
          <div class="chat-header">
            <h3>与AI对话</h3>
          </div>
          <!-- 聊天记录区域 -->
          <div class="chat-history" ref="chatHistory">
            <div
              v-for="(msg, index) in conversation"
              :key="index"
              :class="['chat-message', msg.type]"
            >
              <div class="message-content">{{ msg.text }}</div>
            </div>
          </div>
          <!-- 固定在容器底部的聊天输入区域 -->
          <div class="chat-input-container">
            <el-input
              v-model="userQuestion"
              placeholder="请输入您的问题并按回车发送"
              @keyup.enter.native="sendChat"
              clearable
            />
            <el-button type="primary" @click="sendChat">发送</el-button>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧任务拆解区域 -->
      <el-col :span="10" class="task-container">
        <el-card class="task-card">
          <div class="task-header">
            <h3>任务拆解</h3>
            <el-input
              v-model="goal"
              placeholder="例如：在30天内完成学习计划"
              type="textarea"
              rows="3"
            />
            <el-button
              @click="generateTasks"
              type="primary"
              style="margin-top: 10px"
            >
              生成任务
            </el-button>
          </div>
          <el-table :data="taskList" style="width: 100%; margin-top: 15px">
            <el-table-column
              label="任务名称"
              prop="taskName"
              width="200"
            ></el-table-column>
            <el-table-column
              label="优先级"
              prop="priority"
              width="120"
            ></el-table-column>
            <el-table-column
              label="截止日期"
              prop="deadline"
              width="160"
            ></el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="scope">
                <el-button
                  @click="deleteTask(scope.row)"
                  type="text"
                  style="color: red"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>

import {chat,generateTask,listTask} from "@/api/tasks/task";

export default {
  data() {
    return {
      goal: "", // 用户输入的目标任务
      taskList: [], // 拆解后的任务列表
      conversation: [], // 聊天记录：每个消息有 type ('user' 或 'ai') 和 text
      userQuestion: "", // 用户输入的聊天内容
    };
  },
  methods: {
    // 用户点击“生成任务”，调用AI接口拆解目标
    async generateTasks() {
      if (!this.goal.trim()) {
        this.$message.warning("请输入目标任务！");
        return;
      }
      // 将用户目标添加到对话记录
      this.conversation.push({ type: "user", text: this.goal });
      try {
        this.taskList = await generateTask(message,500);
        this.conversation.push({
          type: "ai",
          text: "目标已拆解，任务列表已更新。",
        });
        this.scrollChatToBottom();
      } catch (error) {
        console.error("任务拆解失败:", error);
        this.$message.error("任务拆解失败，请稍后再试！");
      }
    },
    // 发送聊天消息，向AI提问
    async sendChat() {
      const message = this.userQuestion.trim();
      console.log(message)
      if (!message) {
        this.$message.warning("请输入问题！");
        return;
      }
      // 将用户问题添加到对话记录
      this.conversation.push({ type: "user", text: message });
      this.userQuestion = "";
      this.scrollChatToBottom();
      try {
        const response = await chat(message,500);
        console.log(response)
        this.conversation.push({ type: "ai", text: response });
        this.scrollChatToBottom();
      } catch (error) {
        console.error("获取AI建议失败:", error);
        this.$message.error("获取AI建议失败，请稍后再试！");
      }
    },
    // 删除任务
    deleteTask(task) {
      this.$confirm(`确定删除任务: ${task.taskName}?`, "提示", {
        type: "warning",
      }).then(() => {
        this.taskList = this.taskList.filter(
          (item) => item.taskId !== task.taskId
        );
        this.$message.success("任务删除成功");
      });
    },
    // 滚动聊天记录到底部
    scrollChatToBottom() {
      this.$nextTick(() => {
        const chatHistory = this.$refs.chatHistory;
        if (chatHistory) {
          chatHistory.scrollTop = chatHistory.scrollHeight;
        }
      });
    },
  },
};
</script>

<style scoped>
.app-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.main-content {
  margin-bottom: 20px;
}

/* 左侧聊天区域 */
.chat-container {
  background: #fff;
  border-radius: 4px;
  height: calc(100vh - 100px);
  position: relative;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.chat-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 0;
}

.chat-header {
  padding: 10px;
  border-bottom: 1px solid #ebeef5;
  background-color: #fff;
}

.chat-history {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  background-color: #fdfdfd;
}

.chat-message {
  margin-bottom: 10px;
  padding: 8px;
  border-radius: 4px;
  word-break: break-word;
}

.chat-message.user {
  text-align: right;
  background-color: #ecf5ff;
  color: #409eff;
}

.chat-message.ai {
  text-align: left;
  background-color: #f0f9eb;
  color: #67c23a;
}

/* 固定在聊天区域底部的输入区域 */
.chat-input-container {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: #fff;
  border-top: 1px solid #ebeef5;
}

.chat-input-container .el-input {
  flex: 1;
  margin-right: 10px;
}

/* 右侧任务区域 */
.task-container {
  background: #fff;
  border-radius: 4px;
  height: calc(100vh - 100px);
  overflow-y: auto;
  padding: 10px;
}

.task-card {
  padding: 10px;
}

.task-header h3 {
  margin-bottom: 10px;
}

</style>
