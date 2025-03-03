<template>
    <div>
        <h2>我的任务</h2>
        <el-table :data="tasks">
            <el-table-column prop="title" label="任务名称"></el-table-column>
            <el-table-column prop="deadline" label="截止日期"></el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button size="mini" @click="viewTask(scope.row.id)">查看</el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
import axios from "axios";

export default {
    data() {
        return { tasks: [] };
    },
    async created() {
        const openid = localStorage.getItem("openid");
        if (!openid) {
            window.location.href = getWeChatAuthUrl();
        } else {
            const res = await axios.get(`/api/tasks?openid=${openid}`);
            this.tasks = res.data;
        }
    },
    methods: {
        viewTask(id) {
            this.$router.push(`/task/${id}`);
        }
    }
};
</script>
