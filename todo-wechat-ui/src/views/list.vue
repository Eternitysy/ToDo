<template>
    <div class="container">
        <van-nav-bar
                title="任务列表"
        />
        <van-tabs v-model="activeIndex" @click="tabSwitch">
            <van-tab
                    v-for="(item,key) in tabList"
                    :key="key"
                    :title="item.title"
            >
            </van-tab>
        </van-tabs>

        <div class="list-wrap">
            <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
                <van-list
                        v-model="loading"
                        :finished="finished"
                        finished-text="没有更多了"
                        @load="onLoad"
                        :immediate-check="false"
                >
                    <van-cell v-for="item in list" :key="item.id" @click="info(item.id, item.taskId)">
                        <template slot="default">
                            <div class="item-wrap">
                                <div class="item-header">
                                    <img src="../assets/task.jpg"
                                         alt="">
                                    <h3>{{ item.taskName }}</h3>
                                </div>
                                    <span style="display: block;">{{ item.description }}</span>
                                    <span style="display: block;">{{ item.deadline }}</span>
                            </div>
                        </template>
                    </van-cell>
                </van-list>
            </van-pull-refresh>
        </div>
    </div>
</template>

<script>
import api from '@/api/task'

export default {
    name: "process",

    data() {
        return {
            list: [],
            loading: false,
            finished: false,
            refreshing: false,

            pageNo: 1,
            pageSize: 10,
            pages: 1,

            activeIndex: 0,
            tabList: [
                {title: "未开始",},
                {title: "进行中",},
                {title: "已完成",}
            ]
        };
    },

    created() {
        this.activeIndex = parseInt(this.$route.params.activeIndex);
        this.onLoad()
    },

    methods: {
        tabSwitch() {
            //tab切换，重新初始化数据
            this.list = []
            this.pageNo = 1
            this.finished = false

            //tabs切换时，如果之前的tab已经滚动到底部（list加载到底部），直接点击其他的tab，将再触发一次onload事件。
            //可能调用2次onLoad()方法，延迟执行，通过时间差解决问题
            setTimeout(() => {
                if (!this.finished) {
                    this.onLoad();
                }
            }, 500);
        },

        onLoad() {
            if (this.activeIndex === 0) {
                this.findUnStarted()
            }
            if (this.activeIndex === 1) {
                this.findUnFinished()
            }
            if (this.activeIndex === 2) {
                this.findFinished()
            }
        },

        onRefresh() {
            // 清空列表数据
            this.finished = false;

            this.pageNo = 1;
            // 重新加载数据
            // 将 loading 设置为 true，表示处于加载状态
            this.loading = true;
            this.onLoad();
        },

        findUnStarted() {
            console.log(this.pageNo)
            api.findUnStarted("0",this.pageNo, this.pageSize).then(response => {
                if (this.refreshing) {
                    this.list = [];
                    this.refreshing = false;
                }
                console.log(response.list.length);
                for (let i = 0; i < response.list.length; i++) {
                    let item = response.list[i]
                    console.log(item);
                    this.list.push(item);
                }
                if (this.pageNo >= this.pages) {
                    this.finished = true;
                }
                this.loading = false;
                this.pageNo++;
            });
        },

        findUnFinished() {
            console.log(this.pageNo)
            api.findUnFinished("1",this.pageNo, this.pageSize).then(response => {
                if (this.refreshing) {
                    this.list = [];
                    this.refreshing = false;
                }
                console.log(response.list.length);
                for (let i = 0; i < response.list.length; i++) {
                    let item = response.list[i]
                    console.log(item);
                    this.list.push(item);
                }
                if (this.pageNo >= this.pages) {
                    this.finished = true;
                }
                this.loading = false;
                this.pageNo++;
            });
        },

        findFinished() {
            console.log(this.pageNo)
            api.findFinished("2",this.pageNo, this.pageSize).then(response => {
                if (this.refreshing) {
                    this.list = [];
                    this.refreshing = false;
                }
                console.log(response.list.length);
                for (let i = 0; i < response.list.length; i++) {
                    let item = response.list[i]
                    console.log(item);
                    this.list.push(item);
                }
                if (this.pageNo >= this.pages) {
                    this.finished = true;
                }
                this.loading = false;
                this.pageNo++;
            });
        },

        info(id, taskId) {
            this.$router.push({path: '/show/' + id + '/' + taskId})
        }
    }
}
</script>

<style lang="scss" scoped>
/deep/ .van-nav-bar {
  background: #1D1E20;
}

/deep/ .van-nav-bar__title {
  color: #fff;
}

.container {
  padding-bottom: 50px;

  .list-wrap {
    margin-top: 4px;
    border-top: 1px solid #ebedf0;
  }

  .item-wrap {
    font-size: 12px;
    color: #A7A8A9;

    .item-header {
      display: flex;
      //align-items: center;

      img {
        width: 20px;
        height: 20px;
        border-radius: 4px;
        margin-right: 4px;
      }

      h3 {
        flex: 1;
        font-size: 15px;
        color: #000;
        padding: 0;
        margin: 0;
      }

    }

    .item-block {
      padding: 4px 0;
      font-size: 14px;

      p {
        padding: 0;
        margin: 0;
        line-height: 20px;
      }
    }

    .item-status {
      .pass {
        color: #4CB971;
      }

      .refused {
        color: #EB8473;
      }
    }
  }
}
</style>
