<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="报修原因" prop="title">
        <el-input v-model="queryParams.title" placeholder="报修原因" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="报修地点" prop="location">
        <el-input v-model="queryParams.location" placeholder="报修地点" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-tag type="warning" size="medium">{{ total }} 条待受理工单</el-tag>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['repair:tech:pool']">导出Excel</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="requestList">
      <el-table-column label="编号" align="center" prop="requestId" width="80" />
      <el-table-column label="报修人" align="center" prop="userName" width="100" />
      <el-table-column label="报修原因" align="center" prop="title" :show-overflow-tooltip="true" />
      <el-table-column label="地点" align="center" prop="location" :show-overflow-tooltip="true" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" width="120" />
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)" size="small">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['repair:tech:query']">详情</el-button>
          <el-button size="mini" type="primary" icon="el-icon-check" @click="handleAccept(scope.row)" v-hasPermi="['repair:tech:accept']">受理</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="工单详情" :visible.sync="detailOpen" width="600px" append-to-body>
      <el-descriptions :column="1" border v-if="detail">
        <el-descriptions-item label="报修人">{{ detail.userName }}（{{ detail.createBy }}）</el-descriptions-item>
        <el-descriptions-item label="报修原因">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="报修地点">{{ detail.location || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detail.contactPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="问题描述">{{ detail.description }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(detail.status)" size="small">{{ statusLabel(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ parseTime(detail.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" v-if="detail.remark">{{ detail.remark }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleAcceptFromDetail" v-hasPermi="['repair:tech:accept']" v-if="detail">受理工单</el-button>
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTicketPool, acceptTicket, getTicketDetail } from '@/api/repair/technician'

const STATUS_OPTIONS = [
  { value: '0', label: '待受理' },
  { value: '1', label: '处理中' },
  { value: '2', label: '已完成' },
  { value: '3', label: '已取消' }
]

export default {
  name: 'RepairTicketPool',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      requestList: [],
      detailOpen: false,
      detail: null,
      queryParams: { pageNum: 1, pageSize: 10, title: undefined, location: undefined }
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
      listTicketPool(this.queryParams).then(response => {
        this.requestList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleView(row) {
      getTicketDetail(row.requestId).then(response => { this.detail = response.data; this.detailOpen = true })
    },
    handleAcceptFromDetail() {
      if (!this.detail) return
      this.detailOpen = false
      this.doAccept(this.detail)
    },
    handleAccept(row) { this.doAccept(row) },
    doAccept(row) {
      const self = this
      this.$modal.confirm('确认受理工单 #' + row.requestId + '「' + row.title + '」？').then(() => {
        return acceptTicket(row.requestId)
      }).then(() => {
        self.$modal.msgSuccess('受理成功，该工单已纳入「我的工单」')
        self.getList()
      }).catch(() => {})
    },
    handleExport() {
      this.download('repair/technician/pool/export', { ...this.queryParams }, `工单池_${Date.now()}.xlsx`)
    }
  }
}
</script>
