<template>
  <div class="app-container">
    <el-row :gutter="20" class="main-content">
      <!-- 左侧聊天区域 -->
      <el-col :span="14" class="chat-container">
        <el-card class="chat-card">
          <div class="chat-header">
            <h2>智能任务助手</h2>
          </div>
          <!-- 聊天记录区域 -->
          <div class="chat-history" ref="chatHistory">
            <transition-group name="message-fade">
              <div
                v-for="(msg, index) in conversation"
                :key="index"
                :class="['chat-message', msg.type]"
              >
                <div class="message-content">
                  <!-- 流式消息特殊处理 -->
                  <template v-if="msg.isStreaming">
                    <span v-html="msg.text"></span>
                    <span class="stream-cursor"></span>
                  </template>
                  <template v-else>
                    {{ msg.text }}
                  </template>
                </div>
              </div>
            </transition-group>
            <div v-if="loading" class="loading-indicator">
              <i class="el-icon-loading"></i> AI正在思考中...
            </div>
          </div>
          <!-- 输入区域 -->
          <div class="chat-input-container">
            <el-input
              v-model="userQuestion"
              placeholder="请输入您的问题，Shift+Enter换行"
              type="textarea"
              :rows="2"
              resize="none"
              @keyup.enter.native.ctrl="sendChat"
              :disabled="isProcessing"
            />
            <el-button
              type="primary"
              @click="sendChat"
              :loading="isProcessing"
              class="send-button"
            >
              发送
            </el-button>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧任务拆解区域 -->
      <el-col :span="10" class="task-container">
        <el-card class="task-card">
          <div class="task-header">
            <h3>智能任务拆解</h3>
            <div class="goal-input">
              <el-input
                v-model="goal"
                placeholder="例如：完成季度销售目标提升30%"
                type="textarea"
                :rows="3"
                show-word-limit
                maxlength="200"
              />
              <el-button
                type="primary"
                @click="generateTasks"
                :loading="taskGenerating"
                class="generate-button"
              >
                <i class="el-icon-magic-stick"></i> 智能拆解
              </el-button>
            </div>
          </div>
          <el-table
            :data="taskList"
            v-loading="taskGenerating"
            class="task-table"
            stripe
          >
            <el-table-column label="序号" width="60" align="center">
              <template slot-scope="scope">
                {{ scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column
              label="任务名称"
              prop="taskName"
              min-width="100"
            ></el-table-column>
            <el-table-column
              label="任务描述"
              prop="description"
              min-width="150"
            ></el-table-column>
            <el-table-column
              label="优先级"
              prop="orderNum"
              width="90"
              sortable
            >
              <template slot-scope="scope">
                <el-tag :type="getPriorityTag(scope.row.orderNum)">
                  {{ scope.row.orderNum }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              label="时间范围"
              min-width="120"
            >
              <template slot-scope="scope">
                {{ formatDateRange(scope.row) }}
              </template>
            </el-table-column>
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
          <div class="submit-container">
            <el-button
              type="success"
              @click="submitTasks"
              :disabled="taskList.length === 0"
              class="submit-button"
            >
              <i class="el-icon-upload"></i> 提交任务计划
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { generateTask, chatStream, addTasks } from "@/api/tasks/task";

export default {
  data() {
    return {
      goal: "",
      taskList: [],
      conversation: [],
      userQuestion: "",
      isProcessing: false,
      taskGenerating: false,
      eventSource: null
    };
  },
  methods: {
    // 流式聊天处理
    async sendChat() {
      const question = this.userQuestion.trim();
      if (!question || this.isProcessing) return;

      this.isProcessing = true;
      this.conversation.push({
        type: "user",
        text: question,
        timestamp: new Date().getTime()
      });
      this.userQuestion = "";

      try {
        const emitter = await chatStream(question, 500);
        const aiMessage = {
          type: "ai",
          text: "",
          isStreaming: true,
          timestamp: new Date().getTime()
        };
        this.conversation.push(aiMessage);

        emitter.on('message', (event) => {
          try {
            const data = event.data;
            if (data === '[DONE]') {
              aiMessage.isStreaming = false;
              return;
            }
            const result = JSON.parse(data);
            const content = result.choices[0].delta.content || '';
            aiMessage.text += content;
          } catch (e) {
            console.error('解析错误:', e);
          }
        });

        emitter.on('complete', () => {
          this.isProcessing = false;
          this.scrollChatToBottom();
        });

        emitter.on('error', (err) => {
          this.handleError('对话流异常，请检查连接');
          this.isProcessing = false;
        });

      } catch (error) {
        this.handleError('连接服务器失败');
        this.isProcessing = false;
      }
    },

    // 任务生成逻辑
    async generateTasks() {
      if (!this.goal.trim()) return;

      this.taskGenerating = true;
      try {
        const response = await generateTask(this.goal, 500);
        this.taskList = response.map(item => ({
          ...item,
          startTime: this.formatDate(item.startTime),
          deadline: this.formatDate(item.deadline)
        }));

        this.conversation.push({
          type: "system",
          text: `已生成${response.length}个子任务`,
          timestamp: new Date().getTime()
        });
      } catch (error) {
        this.handleError('任务拆解失败');
      } finally {
        this.taskGenerating = false;
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
.stream-cursor {
  display: inline-block;
  width: 8px;
  height: 1em;
  background: #67c23a;
  animation: blink 1s infinite;
}

@keyframes blink {
  50% { opacity: 0; }
}

.loading-indicator {
  padding: 10px;
  text-align: center;
  color: #909399;
}

.task-table {
  margin-top: 15px;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}

.generate-button {
  width: 100%;
  margin-top: 10px;
  padding: 12px;
}

.submit-button {
  width: 100%;
  padding: 12px;
}

.message-fade-enter-active, .message-fade-leave-active {
  transition: all 0.3s ease;
}
.message-fade-enter, .message-fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
