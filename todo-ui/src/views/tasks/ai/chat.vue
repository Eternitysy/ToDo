<template>
  <div class="app-container">
    <el-row :gutter="20" class="main-content">
      <!-- 左侧聊天区域 -->
      <el-col :span="14" class="chat-container">
        <el-card class="chat-card">
          <div class="chat-header">
            <h2>与AI对话</h2>
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
              width="100"
            ></el-table-column>
            <el-table-column
              label="任务描述"
              prop="taskName"
              width="150"
            ></el-table-column>
            <el-table-column
              label="优先级"
              prop="orderNum"
              width="90"
            ></el-table-column>
            <el-table-column
              label="开始日期"
              prop="startTime"
              width="160"
            ></el-table-column>
            <el-table-column
              label="截止日期"
              prop="deadline"
              width="160"
            ></el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="scope">
                <el-button
                  @click="editTask(scope.row)"
                  type="text"
                >编辑</el-button
                >
                <el-button
                  @click="deleteTask(scope.row)"
                  type="text"
                  style="color: red"
                >删除</el-button
                >
              </template>
            </el-table-column>
          </el-table>
          <!-- 提交按钮 -->
          <div class="submit-container">
            <el-button type="success" @click="submitTasks">
              提交任务
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
// 假设已封装好与后端交互的 API 方法，如 generateTask、chat、deleteTask、submitTask 等
import { generateTask, chat, addTasks } from "@/api/tasks/task";

export default {
  data() {
    return {
      goal: "", // 用户输入的目标任务（用于拆解任务）
      taskList: [], // AI拆解后的任务列表
      conversation: [], // 聊天记录：每个消息包含 type ('user' 或 'ai') 和 text
      userQuestion: "", // 用户输入的聊天内容
    };
  },
  methods: {
    // 调用 AI 接口拆解用户目标，生成任务列表
    async generateTasks() {
      if (!this.goal.trim()) {
        this.$message.warning("请输入目标任务！");
        return;
      }
      // 将用户输入目标加入对话记录
      this.conversation.push({ type: "user", text: this.goal });
      try {
        // 调用 generateTask API，传入用户目标及最大token数
        this.taskList = await generateTask(this.goal, 500);
        // 将 AI 拆解结果添加到对话记录
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
    // 向 AI 提问（对话功能）
    async sendChat() {
      const question = this.userQuestion.trim();
      if (!question) {
        this.$message.warning("请输入问题！");
        return;
      }
      // 将用户问题加入对话记录
      this.conversation.push({ type: "user", text: question });
      this.userQuestion = "";
      this.scrollChatToBottom();
      try {
        const response = await chat(question, 500);
        // 假设返回结果是纯文本回答
        this.conversation.push({ type: "ai", text: response });
        this.scrollChatToBottom();
      } catch (error) {
        console.error("获取AI建议失败:", error);
        this.$message.error("获取AI建议失败，请稍后再试！");
      }
    },
    // 编辑任务（此处仅示例，具体实现可弹窗或跳转至编辑页面）
    editTask(task) {
      this.$message.info(`编辑任务: ${task.taskName}`);
      // 可跳转至任务编辑页面或弹出编辑对话框
    },
    // 删除任务
    deleteTask(task) {
      this.$confirm(`确定删除任务: ${task.taskName}?`, "提示", {
        type: "warning",
      }).then(() => {
        this.taskList = this.taskList.filter(
          (item) => item.taskName !== task.taskName
        );
        this.$message.success("任务删除成功");
      });
    },
    // 提交任务（将最终任务拆解结果提交到后端）
    async submitTasks() {
      try {
        await addTasks(this.taskList);
        this.$message.success("任务提交成功");
      } catch (error) {
        console.error("任务提交失败:", error);
        this.$message.error("任务提交失败，请稍后再试！");
      }
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

/* 主区域 */
.main-content {
  margin-bottom: 20px;
}

/* 左侧聊天区域 */
.chat-container {
  background: #fff;
  border-radius: 4px;
  height: calc(100vh - 100px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
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
  text-align: center;
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
  position: relative;
  padding: 10px;
  background-color: #fff;
  border-top: 1px solid #ebeef5;
  display: flex;
  align-items: center;
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

/* 提交按钮容器 */
.submit-container {
  text-align: center;
  margin-top: 15px;
}
</style>
