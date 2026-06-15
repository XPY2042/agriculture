<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="报修原因" prop="title">
        <el-input v-model="queryParams.title" placeholder="报修原因" clearable @keyup.enter.native="handleQuery" />
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['repair:apply:add']">提交报修</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="requestList">
      <el-table-column label="编号" align="center" prop="requestId" width="80" />
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
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['repair:apply:query']">详情</el-button>
          <el-button v-if="scope.row.status === '0'" size="mini" type="text" icon="el-icon-close" @click="handleCancel(scope.row)" v-hasPermi="['repair:apply:cancel']">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="报修原因" prop="title">
          <el-input v-model="form.title" placeholder="请填写报修原因" />
        </el-form-item>
        <el-form-item label="报修地点" prop="location">
          <el-input v-model="form.location" placeholder="如：温室3号、东区大棚" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="方便联系的手机号" />
        </el-form-item>
        <el-form-item label="问题描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请详细描述故障现象" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="其他补充说明（选填）" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">提 交</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="报修详情" :visible.sync="detailOpen" width="600px" append-to-body>
      <el-descriptions :column="1" border v-if="detail">
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
    </el-dialog>
  </div>
</template>

<script>
import { listRepairRequest, getRepairRequest, addRepairRequest, cancelRepairRequest } from '@/api/repair/request'

const STATUS_OPTIONS = [
  { value: '0', label: '待受理' },
  { value: '1', label: '处理中' },
  { value: '2', label: '已完成' },
  { value: '3', label: '已取消' }
]

export default {
  name: 'RepairApply',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      requestList: [],
      title: '',
      open: false,
      detailOpen: false,
      detail: null,
      statusOptions: STATUS_OPTIONS,
      queryParams: { pageNum: 1, pageSize: 10, title: undefined, status: undefined },
      form: {},
      rules: {
        title: [{ required: true, message: '报修原因不能为空', trigger: 'blur' }],
        description: [{ required: true, message: '问题描述不能为空', trigger: 'blur' }]
      }
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
    cancel() { this.open = false; this.reset() },
    reset() {
      this.form = { title: undefined, location: undefined, contactPhone: undefined, description: undefined, remark: undefined }
      this.resetForm('form')
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleAdd() { this.reset(); this.open = true; this.title = '提交报修' },
    handleView(row) {
      getRepairRequest(row.requestId).then(response => { this.detail = response.data; this.detailOpen = true })
    },
    handleCancel(row) {
      this.$modal.confirm('确认取消该报修申请？').then(() => cancelRepairRequest({ requestId: row.requestId }))
        .then(() => { this.getList(); this.$modal.msgSuccess('已取消') }).catch(() => {})
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          addRepairRequest(this.form).then(() => {
            this.$modal.msgSuccess('提交成功，管理员将尽快处理')
            this.open = false
            this.getList()
          })
        }
      })
    }
  }
}
</script>
