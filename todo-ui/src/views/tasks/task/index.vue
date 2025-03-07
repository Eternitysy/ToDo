<template>
  <div class="app-container">
    <!-- 查询区域 -->
    <el-form :model="queryParams" ref="queryForm" size="small" inline>
      <el-form-item label="任务名称" prop="taskName">
        <el-input
          v-model="queryParams.taskName"
          placeholder="请输入任务名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择任务状态" clearable>
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
            <span :style="{ color: item.color }">{{ item.label }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:task:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-sort"
          size="mini"
          @click="toggleExpandAll"
        >展开/折叠</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 任务列表表格 -->
    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="taskList"
      row-key="taskId"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="taskName" label="任务名称" width="260"></el-table-column>
      <el-table-column prop="orderNum" label="排序" width="200"></el-table-column>
      <el-table-column prop="description" label="任务描述" width="200"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" align="center" width="200">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="startTime" label="开始时间" width="200"></el-table-column>
      <el-table-column prop="deadline" label="截止时间" width="200"></el-table-column>
      <el-table-column prop="status" label="状态" width="150">
        <template slot-scope="scope">
          <!-- 内嵌下拉框直接修改任务状态 -->
          <el-select v-model="scope.row.status" size="mini" @change="updateStatus(scope.row)">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
              <span :style="{ color: item.color }">{{ item.label }}</span>
            </el-option>
          </el-select>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:task:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-plus" @click="handleAdd(scope.row)" v-hasPermi="['system:task:add']">新增</el-button>
          <el-button
            v-if="scope.row.parentId != 0"
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:task:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改任务对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="24" v-if="form.parentId !== 0">
            <el-form-item label="上级任务" prop="parentId">
              <treeselect v-model="form.parentId" :options="taskOptions" :normalizer="normalizer" placeholder="选择上级任务" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="任务名称" prop="taskName">
              <el-input v-model="form.taskName" placeholder="请输入任务名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="任务描述" prop="description">
              <el-input v-model="form.description" placeholder="请输入任务描述" maxlength="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务开始时间" prop="startTime">
              <el-input v-model="form.startTime" placeholder="请输入任务开始时间" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务期限" prop="deadline">
              <el-input v-model="form.deadline" placeholder="请输入任务期限" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="任务状态">
              <!-- 在对话框中状态仅显示，不可编辑 -->
              <el-radio-group v-model="form.status" disabled>
                <el-radio v-for="item in statusOptions" :key="item.value" :label="item.value">
                  <span :style="{ color: item.color }">{{ item.label }}</span>
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTask, getTask, delTask, addTask, updateTask, listTaskExcludeChild } from "@/api/tasks/task";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

export default {
  name: "Task",
  dicts: ["sys_normal_disable"],
  components: { Treeselect },
  data() {
    return {
      loading: true,
      showSearch: true,
      taskList: [],
      taskOptions: [],
      title: "",
      open: false,
      isExpandAll: true,
      refreshTable: true,
      queryParams: {
        taskName: undefined,
        status: undefined,
      },
      form: {},
      rules: {
        parentId: [{ required: true, message: "上级任务不能为空", trigger: "blur" }],
        taskName: [{ required: true, message: "任务名称不能为空", trigger: "blur" }],
        orderNum: [{ required: true, message: "显示排序不能为空", trigger: "blur" }],
      },
      // 任务状态选项：0-未开始，1-进行中，2-已完成
      statusOptions: [
        { value: 0, label: "未开始", color: "#909399" },
        { value: 1, label: "进行中", color: "#1c84c6" },
        { value: 2, label: "已完成", color: "#1ab394" },
      ],
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询任务列表 */
    getList() {
      this.loading = true;
      listTask(this.queryParams).then((response) => {
        this.taskList = this.handleTree(response.data, "taskId");
        this.loading = false;
      });
    },
    /** 转换任务数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children;
      }
      return {
        id: node.taskId,
        label: node.taskName,
        children: node.children,
      };
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    reset() {
      this.form = {
        taskId: undefined,
        parentId: undefined,
        taskName: undefined,
        orderNum: undefined,
        description: undefined,
        startTime: undefined,
        deadline: undefined,
        status: 0,
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd(row) {
      this.reset();
      if (row != undefined) {
        this.form.parentId = row.taskId;
      }
      this.open = true;
      this.title = "添加任务";
      listTask().then((response) => {
        this.taskOptions = this.handleTree(response.data, "taskId");
      });
    },
    /** 展开/折叠操作 */
    toggleExpandAll() {
      this.refreshTable = false;
      this.isExpandAll = !this.isExpandAll;
      this.$nextTick(() => {
        this.refreshTable = true;
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      getTask(row.taskId).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改任务";
        listTaskExcludeChild(row.taskId).then((response) => {
          this.taskOptions = this.handleTree(response.data, "taskId");
          if (this.taskOptions.length == 0) {
            const noResultsOptions = { taskId: this.form.parentId, taskName: this.form.parentName, children: [] };
            this.taskOptions.push(noResultsOptions);
          }
        });
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.taskId != undefined) {
            updateTask(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addTask(this.form).then((response) => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$modal
        .confirm('是否确认删除名称为"' + row.taskName + '"的数据项？')
        .then(() => {
          return delTask(row.taskId);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    /** 更新任务状态 **/
    updateStatus(row) {
      updateTask(row)
        .then(() => {
          const statusLabel =
            row.status === 0 ? "未开始" : row.status === 1 ? "进行中" : "已完成";
          this.$message.success(`任务 "${row.taskName}" 状态更新为: ${statusLabel}`);
        })
        .catch((error) => {
          console.error("状态更新失败:", error);
          this.$message.error("状态更新失败，请稍后再试！");
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

.mb8 {
  margin-bottom: 8px;
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
