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
        <el-tag :type="statusTagType(activeStatus)" size="medium">{{ total }} 条工单</el-tag>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['repair:tech:list']">导出Excel</el-button>
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
      <el-table-column label="受理时间" align="center" prop="acceptedAt" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.acceptedAt) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="240">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['repair:tech:query']">详情</el-button>
          <el-button v-if="scope.row.status === '1' && !scope.row.repairStartAt" size="mini" type="text" icon="el-icon-s-tools" @click="handleStart(scope.row)" v-hasPermi="['repair:tech:start']">开始维修</el-button>
          <el-button v-if="scope.row.status === '1'" size="mini" type="text" icon="el-icon-circle-check" @click="handleComplete(scope.row)" v-hasPermi="['repair:tech:complete']">完成维修</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="工单详情" :visible.sync="detailOpen" width="620px" append-to-body>
      <el-descriptions :column="1" border v-if="detail">
        <el-descriptions-item label="报修人">{{ detail.userName }}（{{ detail.createBy }}）</el-descriptions-item>
        <el-descriptions-item label="报修原因">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="报修地点">{{ detail.location || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detail.contactPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="问题描述">{{ detail.description }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(detail.status)" size="small">{{ statusLabel(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="受理人" v-if="detail.technicianName">{{ detail.technicianName }}</el-descriptions-item>
        <el-descriptions-item label="受理时间" v-if="detail.acceptedAt">{{ parseTime(detail.acceptedAt) }}</el-descriptions-item>
        <el-descriptions-item label="维修开始时间" v-if="detail.repairStartAt">{{ parseTime(detail.repairStartAt) }}</el-descriptions-item>
        <el-descriptions-item label="维修完成时间" v-if="detail.repairFinishAt">{{ parseTime(detail.repairFinishAt) }}</el-descriptions-item>
        <el-descriptions-item label="维修日志" v-if="detail.repairLog">{{ detail.repairLog }}</el-descriptions-item>
        <el-descriptions-item label="使用配件" v-if="detail.partsUsed">{{ detail.partsUsed }}</el-descriptions-item>
        <el-descriptions-item label="维修费用" v-if="detail.repairCost != null">¥{{ detail.repairCost }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ parseTime(detail.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" v-if="detail.remark">{{ detail.remark }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="完成维修" :visible.sync="completeOpen" width="560px" append-to-body>
      <el-form ref="completeForm" :model="completeForm" :rules="completeRules" label-width="100px">
        <el-form-item label="工单编号"><span>#{{ completeForm.requestId }}</span></el-form-item>
        <el-form-item label="报修原因"><span>{{ completeForm.title }}</span></el-form-item>
        <el-form-item label="维修日志" prop="repairLog">
          <el-input v-model="completeForm.repairLog" type="textarea" :rows="4" placeholder="请填写维修过程、故障原因、处理措施等" />
        </el-form-item>
        <el-form-item label="使用配件" prop="partsUsed">
          <el-input v-model="completeForm.partsUsed" placeholder="更换的零件名称，如：温度传感器、PVC水管" />
        </el-form-item>
        <el-form-item label="维修费用" prop="repairCost">
          <el-input-number v-model="completeForm.repairCost" :min="0" :precision="2" :step="10" style="width: 200px" placeholder="元" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitComplete">确 定</el-button>
        <el-button @click="cancelComplete">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMyTickets, getTicketDetail, startRepair, completeRepair } from '@/api/repair/technician'

const STATUS_OPTIONS = [
  { value: '1', label: '处理中' },
  { value: '2', label: '已完成' }
]

export default {
  name: 'RepairMyTickets',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      requestList: [],
      activeStatus: undefined,
      statusOptions: STATUS_OPTIONS,
      detailOpen: false,
      detail: null,
      completeOpen: false,
      queryParams: { pageNum: 1, pageSize: 10, title: undefined, status: undefined },
      completeForm: {},
      completeRules: {
        repairLog: [{ required: true, message: '请填写维修日志', trigger: 'blur' }],
        partsUsed: [{ required: true, message: '请填写使用配件', trigger: 'blur' }],
        repairCost: [{ required: true, message: '请填写维修费用', trigger: 'blur' }]
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
      listMyTickets(this.queryParams).then(response => {
        this.requestList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleView(row) {
      getTicketDetail(row.requestId).then(response => { this.detail = response.data; this.detailOpen = true })
    },
    handleStart(row) {
      const self = this
      this.$modal.confirm('确认开始维修工单 #' + row.requestId + '「' + row.title + '」？').then(() => {
        return startRepair(row.requestId)
      }).then(() => {
        self.$modal.msgSuccess('已开始维修，请完成维修后回填记录')
        self.getList()
      }).catch(() => {})
    },
    handleComplete(row) {
      this.completeForm = {
        requestId: row.requestId,
        title: row.title,
        repairLog: row.repairLog || '',
        partsUsed: row.partsUsed || '',
        repairCost: row.repairCost || 0
      }
      this.completeOpen = true
    },
    cancelComplete() {
      this.completeOpen = false
      this.resetForm('completeForm')
    },
    submitComplete() {
      this.$refs['completeForm'].validate(valid => {
        if (valid) {
          completeRepair(this.completeForm).then(() => {
            this.$modal.msgSuccess('维修完成，工单已归档')
            this.completeOpen = false
            this.getList()
          })
        }
      })
    },
    handleExport() {
      this.download('repair/technician/my-tickets/export', { ...this.queryParams }, `我的工单_${Date.now()}.xlsx`)
    }
  }
}
</script>
