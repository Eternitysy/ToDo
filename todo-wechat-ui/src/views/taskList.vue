<template>
    <div>
        <h2>{{ task.title }}</h2>
        <p>截止日期：{{ task.deadline }}</p>
        <el-select v-model="task.status" @change="updateStatus">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
        </el-select>
    </div>
</template>

<script>
import axios from "axios";

export default {
    data() {
        return {
            task: {},
            statusOptions: [
                { value: 0, label: "未开始" },
                { value: 1, label: "进行中" },
                { value: 2, label: "已完成" }
            ]
        };
    },
    async created() {
        const taskId = this.$route.params.id;
        const res = await axios.get(`/api/task/${taskId}`);
        this.task = res.data;
    },
    methods: {
        async updateStatus() {
            await axios.post(`/api/task/${this.task.id}/status`, { status: this.task.status });
            this.$message.success("状态更新成功");
        }
    }
};
</script>
