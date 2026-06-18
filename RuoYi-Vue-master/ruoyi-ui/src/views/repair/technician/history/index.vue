<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="报修原因" prop="title">
        <el-input v-model="queryParams.title" placeholder="报修原因" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-tag type="success" size="medium">{{ total }} 条已完成维修</el-tag>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['repair:tech:history']">导出Excel</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="requestList">
      <el-table-column label="编号" align="center" prop="requestId" width="80" />
      <el-table-column label="报修人" align="center" prop="userName" width="100" />
      <el-table-column label="报修原因" align="center" prop="title" :show-overflow-tooltip="true" />
      <el-table-column label="地点" align="center" prop="location" :show-overflow-tooltip="true" />
      <el-table-column label="维修费用" align="center" prop="repairCost" width="100">
        <template slot-scope="scope">
          <span v-if="scope.row.repairCost != null">¥{{ scope.row.repairCost }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <el-tag type="success" size="small">已完成</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="完成时间" align="center" prop="repairFinishAt" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.repairFinishAt) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="100">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['repair:tech:query']">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情弹窗 - 含完整维修记录 -->
    <el-dialog title="维修详情" :visible.sync="detailOpen" width="620px" append-to-body>
      <el-descriptions :column="1" border v-if="detail">
        <el-descriptions-item label="报修人">{{ detail.userName }}（{{ detail.createBy }}）</el-descriptions-item>
        <el-descriptions-item label="报修原因">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="报修地点">{{ detail.location || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detail.contactPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="问题描述">{{ detail.description }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag type="success" size="small">已完成</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="维修人员">{{ detail.technicianName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="受理时间">{{ parseTime(detail.acceptedAt) }}</el-descriptions-item>
        <el-descriptions-item label="维修开始">{{ parseTime(detail.repairStartAt) }}</el-descriptions-item>
        <el-descriptions-item label="维修完成">{{ parseTime(detail.repairFinishAt) }}</el-descriptions-item>
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
  </div>
</template>

<script>
import { listRepairHistory, getTicketDetail } from '@/api/repair/technician'

export default {
  name: 'RepairHistory',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      requestList: [],
      detailOpen: false,
      detail: null,
      queryParams: { pageNum: 1, pageSize: 10, title: undefined }
    }
  },
  created() { this.getList() },
  methods: {
    getList() {
      this.loading = true
      listRepairHistory(this.queryParams).then(response => {
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
    handleExport() {
      this.download('repair/technician/history/export', { ...this.queryParams }, `维修记录_${Date.now()}.xlsx`)
    }
  }
}
</script>
