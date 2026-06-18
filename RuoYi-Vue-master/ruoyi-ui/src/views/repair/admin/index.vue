<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="报修原因" prop="title">
        <el-input v-model="queryParams.title" placeholder="报修原因" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="报修人" prop="createBy">
        <el-input v-model="queryParams.createBy" placeholder="登录账号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="状态" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['repair:admin:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['repair:admin:list']">导出Excel</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="requestList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="requestId" width="80" />
      <el-table-column label="报修人" align="center" prop="userName" width="100" />
      <el-table-column label="账号" align="center" prop="createBy" width="100" />
      <el-table-column label="报修原因" align="center" prop="title" :show-overflow-tooltip="true" />
      <el-table-column label="地点" align="center" prop="location" :show-overflow-tooltip="true" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" width="120" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)" size="small">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['repair:admin:query']">详情</el-button>
          <el-button v-if="scope.row.status !== '3' && scope.row.status !== '2'" size="mini" type="text" icon="el-icon-edit" @click="handleProcess(scope.row)" v-hasPermi="['repair:admin:edit']">处理</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="报修详情" :visible.sync="detailOpen" width="620px" append-to-body>
      <el-descriptions :column="1" border v-if="detail">
        <el-descriptions-item label="报修人">{{ detail.userName }}（{{ detail.createBy }}）</el-descriptions-item>
        <el-descriptions-item label="报修原因">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="报修地点">{{ detail.location || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detail.contactPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="问题描述">{{ detail.description }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(detail.status)" size="small">{{ statusLabel(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="管理员回复" v-if="detail.adminReply">{{ detail.adminReply }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ parseTime(detail.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" v-if="detail.remark">{{ detail.remark }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button
          v-if="detail && detail.status !== '3' && detail.status !== '2'"
          type="primary"
          @click="handleProcessFromDetail"
          v-hasPermi="['repair:admin:edit']"
        >处理</el-button>
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="处理报修" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="报修原因"><span>{{ form.title }}</span></el-form-item>
        <el-form-item label="处理状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择">
            <el-option label="待受理" value="0" />
            <el-option label="处理中" value="1" />
            <el-option label="已完成" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="回复用户" prop="adminReply">
          <el-input v-model="form.adminReply" type="textarea" :rows="3" placeholder="处理说明或结果反馈" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="内部备注（选填）" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRepairRequest, getRepairRequest, handleRepairRequest, delRepairRequest } from '@/api/repair/request'

const STATUS_OPTIONS = [
  { value: '0', label: '待受理' },
  { value: '1', label: '处理中' },
  { value: '2', label: '已完成' },
  { value: '3', label: '已取消' }
]

export default {
  name: 'RepairAdmin',
  data() {
    return {
      loading: true,
      ids: [],
      multiple: true,
      showSearch: true,
      total: 0,
      requestList: [],
      open: false,
      detailOpen: false,
      detail: null,
      statusOptions: STATUS_OPTIONS,
      queryParams: { pageNum: 1, pageSize: 10, title: undefined, createBy: undefined, status: undefined },
      form: {},
      rules: { status: [{ required: true, message: '请选择处理状态', trigger: 'change' }] }
    }
  },
  created() { this.getList() },
  methods: {
    statusLabel(status) {
      const item = STATUS_OPTIONS.find(o => o.value === status)
      return item ? item.label : status
    },
    statusTagType(status) {
      const map = { '0': 'warning', '1': '', '2': 'success', '3': 'info' }
      return map[status] || 'info'
    },
    getList() {
      this.loading = true
      listRepairRequest(this.queryParams).then(response => {
        this.requestList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    cancel() { this.open = false },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.requestId)
      this.multiple = !selection.length
    },
    handleView(row) {
      getRepairRequest(row.requestId).then(response => { this.detail = response.data; this.detailOpen = true })
    },
    openProcessForm(data) {
      this.form = {
        requestId: data.requestId,
        title: data.title,
        status: data.status === '0' ? '1' : data.status,
        adminReply: data.adminReply || '',
        remark: data.remark || ''
      }
      this.open = true
    },
    handleProcessFromDetail() {
      if (!this.detail) return
      this.detailOpen = false
      this.openProcessForm(this.detail)
    },
    handleProcess(row) {
      getRepairRequest(row.requestId).then(response => {
        this.openProcessForm(response.data)
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          handleRepairRequest(this.form).then(() => {
            this.$modal.msgSuccess('处理成功')
            this.open = false
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      const requestIds = row.requestId || this.ids
      this.$modal.confirm('是否确认删除报修编号为"' + requestIds + '"的数据？').then(() => delRepairRequest(requestIds))
        .then(() => { this.getList(); this.$modal.msgSuccess('删除成功') }).catch(() => {})
    },
    handleExport() {
      this.download('repair/request/export', { ...this.queryParams }, `报修数据_${Date.now()}.xlsx`)
    }
  }
}
</script>
