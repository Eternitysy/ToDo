<template>
    <div>授权中...</div>
</template>

<script>
import { getOpenId } from "@/utils/wechat";

export default {
    async created() {
        const urlParams = new URLSearchParams(window.location.search);
        const code = urlParams.get("code");
        if (code) {
            const openid = await getOpenId(code);
            localStorage.setItem("openid", openid);
            this.$router.replace("/"); // 跳回首页
        } else {
            console.error("获取 code 失败");
        }
    }
};
</script>
