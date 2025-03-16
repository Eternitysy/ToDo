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

        </el-card>
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
            v-hasPermi="['tasks:ai:chatStream']"
            style="margin-left: 5px"
          >
            发送
          </el-button>
        </div>
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
                class="generate-button"
              >
                <i class="el-icon-magic-stick"></i> 智能拆解
              </el-button>
            </div>
          </div>
          <el-table :data="taskList" style="width: 100%; margin-top: 10px">
            <el-table-column
              label="任务名称"
              prop="taskName"
              width="150"
            ></el-table-column>
            <el-table-column
              label="任务描述"
              prop="taskName"
              width="160"
            ></el-table-column>
            <el-table-column
              label="优先级"
              prop="orderNum"
              width="50"
            ></el-table-column>
            <el-table-column
              label="截止日期"
              prop="deadline"
              width="100"
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
      if (this.isAIResponding) {
        this.$message.warning('请等待当前响应完成');
        return;
      }

      const question = this.userQuestion.trim();
      if (!question) {
        this.$message.warning("请输入问题！");
        return;
      }

      // 添加用户消息
      this.conversation.push({
        type: "user",
        text: question,
        show: true
      });

      // 添加初始AI消息
      this.conversation.push({
        type: "ai",
        text: "",
        loading: true,
        show: false
      });
      this.currentAiMessageIndex = this.conversation.length - 1;
      this.userQuestion = "";
      this.isAIResponding = true;
      this.scrollChatToBottom();

      this.abortController = new AbortController();

      try {
        const url = `/dev-api/tasks/ai/chatStream?message=${question}`;
        const response = await fetch(url, {
          method: 'POST',
          headers: {
            'Accept': 'text/event-stream',
          },
          signal: this.abortController.signal,
        });

        if (!response.ok) {
          throw new Error(`请求失败，状态码：${response.status}`);
        }

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let buffer = '';

        while (true) {
          const { done, value } = await reader.read();
          if (done) break;

          buffer += decoder.decode(value, { stream: true });

          // 处理完整的事件（以\n\n分隔）
          let eventEndIndex;
          while ((eventEndIndex = buffer.indexOf('\n\n')) !== -1) {
            const eventStr = buffer.substring(0, eventEndIndex);
            buffer = buffer.substring(eventEndIndex + 2);

            // 解析事件数据
            let data = '';
            eventStr.split('\n').forEach(line => {
              if (line.startsWith('data:')) {
                data += line.slice(5).trim() + '\n';
              }
            });
            data = data.trimEnd();

            if (data === '[DONE]') {
              break;
            }

            if (data) {
              this.updateAiMessage(data);
            }
          }
        }
      } catch (error) {
        if (error.name !== 'AbortError') {
          console.error('请求失败:', error);
          this.$message.error('请求失败: ' + error.message);
          this.conversation[this.currentAiMessageIndex].text = '响应获取失败，请重试';
        }
      } finally {
        this.isAIResponding = false;
        if (this.conversation[this.currentAiMessageIndex]) {
          this.conversation[this.currentAiMessageIndex].loading = false;
        }
        this.abortController = null;
        this.scrollChatToBottom();
      }
    },

// 移除parseSSEEvent方法（已整合到主流程）

    updateAiMessage(content) {
      const currentMsg = this.conversation[this.currentAiMessageIndex];
      this.$set(this.conversation, this.currentAiMessageIndex, {
        ...currentMsg,
        text: currentMsg.text + content
      });
      this.scrollChatToBottom();
    },

    // 任务生成逻辑
    async generateTasks() {
      if (!this.goal.trim()) return;

      this.taskGenerating = true;
      try {
        this.taskList = await generateTask(this.goal, 500);
        this.conversation.push({
          type: "aiTask",
          text: "目标已拆解，任务列表已更新。",
        });
        this.scrollChatToBottom();
      } catch (error) {
        console.error("任务拆解失败:", error);
        this.$message.error("任务拆解失败，请稍后再试！");
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

.chat-message.aiTask {
  text-align: left;
  background-color: #f9f2eb;
  color: #eea04a;
}

/* 固定在聊天区域底部的输入区域 */
.chat-input-container {
  padding-bottom: 10px;
  background-color: #fff;
  border-bottom: 5px solid #ebeef5;
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
