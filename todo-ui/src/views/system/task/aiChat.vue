<template>
  <div class="aiChat">
    <el-input v-model="userQuestion" placeholder="请输入您的问题"></el-input>
    <el-button @click="getAdvice" type="primary">获取建议</el-button>

    <el-dialog :visible.sync="dialogVisible">
      <span>{{ modelResponse }}</span>
      <el-button @click="dialogVisible = false">关闭</el-button>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userQuestion: '',
      modelResponse: '',
      dialogVisible: false
    };
  },
  methods: {
    async getAdvice() {
      try {
        const response = await this.$axios.post('/api/ai/suggestion', this.userQuestion);
        this.modelResponse = response.data;
        this.dialogVisible = true;
      } catch (error) {
        this.$message.error('获取建议失败');
      }
    }
  }
};
</script>
