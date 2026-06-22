<template>
  <div class="app-container repair-apply-page">
    <!-- ====== 统计图表区 ====== -->
    <div class="stats-row">
      <!-- 报修地点分布柱状图 -->
      <div class="stats-card">
        <div class="stats-card__head">报修地点分布</div>
        <div class="stats-card__body"><div ref="locChart" class="stats-chart" /></div>
      </div>
      <!-- 高频报修原因占比饼图 -->
      <div class="stats-card">
        <div class="stats-card__head">高频报修原因占比</div>
        <div class="stats-card__body"><div ref="reasonChart" class="stats-chart" /></div>
      </div>
      <!-- 设备故障类型统计环形图 -->
      <div class="stats-card">
        <div class="stats-card__head">设备故障类型统计</div>
        <div class="stats-card__body"><div ref="typeChart" class="stats-chart" /></div>
      </div>
    </div>

    <!-- ====== 原有功能区 ====== -->
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
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['repair:apply:list']">导出Excel</el-button>
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
import * as echarts from 'echarts'
require('echarts/theme/macarons')
import { listRepairRequest, getRepairRequest, addRepairRequest, cancelRepairRequest } from '@/api/repair/request'

const STATUS_OPTIONS = [
  { value: '0', label: '待受理' },
  { value: '1', label: '处理中' },
  { value: '2', label: '已完成' },
  { value: '3', label: '已取消' }
]

// 故障类型分类规则（根据报修原因关键词匹配）
const FAULT_CATEGORIES = [
  { key: '灌溉设备', keywords: ['灌溉', '水', '漏水', '阀门', '水管', '泵', '滴灌', '喷灌'] },
  { key: '传感器检测', keywords: ['传感器', '探头', '检测', '监测', '感应', '读数', '采集'] },
  { key: '环境控制', keywords: ['温度', '湿度', '光照', '通风', '遮阳', '风机', '湿帘', 'CO2', '二氧化碳'] },
  { key: '结构设施', keywords: ['大棚', '温室', '棚膜', '骨架', '墙', '门', '窗', '覆盖'] },
  { key: '电气系统', keywords: ['电', '线路', '开关', '控制柜', '配电', '电机', '电源'] }
]

function classifyFault(title) {
  if (!title) return '其他'
  for (const cat of FAULT_CATEGORIES) {
    if (cat.keywords.some(kw => title.includes(kw))) return cat.key
  }
  return '其他'
}

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
      },
      locChartInst: null,
      reasonChartInst: null,
      typeChartInst: null
    }
  },
  created() { this.getList() },
  mounted() { window.addEventListener('resize', this.resizeCharts) },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts);
    ['locChartInst', 'reasonChartInst', 'typeChartInst'].forEach(k => { if (this[k]) { this[k].dispose(); this[k] = null } })
  },
  methods: {
    statusLabel(status) { const item = STATUS_OPTIONS.find(o => o.value === status); return item ? item.label : status },
    statusTagType(status) { const map = { '0': 'warning', '1': '', '2': 'success', '3': 'info' }; return map[status] || 'info' },
    getList() {
      this.loading = true
      listRepairRequest(this.queryParams).then(response => {
        this.requestList = response.rows; this.total = response.total; this.loading = false
        this.loadStats()  // 数据加载后同步刷新统计图表
      }).catch(() => { this.loading = false })
    },
    // 获取全量数据用于统计（取消分页限制，共用相同筛选条件）
    loadStats() {
      listRepairRequest({ ...this.queryParams, pageNum: 1, pageSize: 9999 }).then(response => {
        const all = response.rows || []
        this.renderLocChart(all)
        this.renderReasonChart(all)
        this.renderTypeChart(all)
      }).catch(() => {})
    },
    // ====== 图表渲染 ======
    initChart(refKey, instKey) {
      if (this[instKey]) return this[instKey]
      if (!this.$refs[refKey]) return null
      this[instKey] = echarts.init(this.$refs[refKey], 'macarons')
      return this[instKey]
    },
    // 柱状图：报修地点分布
    renderLocChart(rows) {
      const chart = this.initChart('locChart', 'locChartInst')
      if (!chart) return
      const map = {}
      rows.forEach(r => { const loc = r.location || '未填写'; map[loc] = (map[loc] || 0) + 1 })
      const data = Object.entries(map).sort((a, b) => b[1] - a[1])
      chart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '5%', bottom: '10%', top: '5%', containLabel: true },
        xAxis: { type: 'category', data: data.map(d => d[0]), axisLabel: { rotate: 30, fontSize: 10 } },
        yAxis: { type: 'value', name: '报修数' },
        series: [{ type: 'bar', barWidth: '55%', itemStyle: { borderRadius: [4, 4, 0, 0], color: '#409eff' }, data: data.map(d => d[1]) }]
      }, true)
    },
    // 饼图：报修原因占比
    renderReasonChart(rows) {
      const chart = this.initChart('reasonChart', 'reasonChartInst')
      if (!chart) return
      const map = {}
      rows.forEach(r => { const t = r.title || '未填写'; map[t] = (map[t] || 0) + 1 })
      const data = Object.entries(map).map(([name, value]) => ({ name, value }))
      chart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c}条 ({d}%)' },
        legend: {
          type: 'scroll',
          orient: 'horizontal',
          left: 8,
          right: 8,
          bottom: 0,
          itemWidth: 10,
          itemHeight: 10,
          textStyle: { fontSize: 10 },
          pageIconSize: 10,
          pageTextStyle: { fontSize: 10 }
        },
        series: [{
          type: 'pie',
          radius: ['34%', '60%'],
          center: ['50%', '42%'],
          avoidLabelOverlap: true,
          label: { fontSize: 10, formatter: '{d}%' },
          labelLine: { length: 8, length2: 6 },
          data
        }]
      }, true)
    },
    // 环形图：设备故障类型统计
    renderTypeChart(rows) {
      const chart = this.initChart('typeChart', 'typeChartInst')
      if (!chart) return
      const map = {}
      rows.forEach(r => { const cat = classifyFault(r.title); map[cat] = (map[cat] || 0) + 1 })
      const data = Object.entries(map).map(([name, value]) => ({ name, value }))
      const colors = { '灌溉设备': '#67c23a', '传感器检测': '#409eff', '环境控制': '#e6a23c', '结构设施': '#f56c6c', '电气系统': '#909399', '其他': '#c0c4cc' }
      chart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c}条 ({d}%)' },
        legend: {
          type: 'scroll',
          orient: 'horizontal',
          left: 8,
          right: 8,
          bottom: 0,
          itemWidth: 10,
          itemHeight: 10,
          textStyle: { fontSize: 10 },
          pageIconSize: 10,
          pageTextStyle: { fontSize: 10 }
        },
        series: [{
          type: 'pie',
          radius: ['36%', '62%'],
          center: ['50%', '42%'],
          avoidLabelOverlap: true,
          label: { fontSize: 10, formatter: '{d}%' },
          labelLine: { length: 8, length2: 6 },
          data,
          itemStyle: { color: params => colors[params.name] || '#c0c4cc' }
        }]
      }, true)
    },
    // ====== 原有方法 ======
    resizeCharts() {
      ['locChartInst', 'reasonChartInst', 'typeChartInst'].forEach(k => { if (this[k]) this[k].resize() })
    },
    cancel() { this.open = false; this.reset() },
    reset() {
      this.form = { title: undefined, location: undefined, contactPhone: undefined, description: undefined, remark: undefined }
      this.resetForm('form')
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleAdd() { this.reset(); this.open = true; this.title = '提交报修' },
    handleView(row) { getRepairRequest(row.requestId).then(response => { this.detail = response.data; this.detailOpen = true }) },
    handleCancel(row) {
      this.$modal.confirm('确认取消该报修申请？').then(() => cancelRepairRequest({ requestId: row.requestId }))
        .then(() => { this.getList(); this.$modal.msgSuccess('已取消') }).catch(() => {})
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          addRepairRequest(this.form).then(() => {
            this.$modal.msgSuccess('提交成功，管理员将尽快处理')
            this.open = false; this.getList()
          })
        }
      })
    },
    handleExport() { this.download('repair/request/export', { ...this.queryParams }, `我的报修_${Date.now()}.xlsx`) }
  }
}
</script>

<style scoped>
/* ====== 统计图表区：三栏均分 ====== */
.stats-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 18px; }
.stats-card { background: #fff; border-radius: 10px; box-shadow: 0 1px 6px rgba(0,0,0,.05); overflow: hidden; display: flex; flex-direction: column; }
.stats-card__head { font-size: 14px; font-weight: 600; color: #303133; padding: 14px 16px; border-bottom: 1px solid #f0f0f0; flex-shrink: 0; }
.stats-card__body { padding: 6px; flex: 1; min-height: 0; }
.stats-chart { height: 280px; width: 100%; }
.mb8 { margin-bottom: 12px; }
@media (max-width: 1200px) { .stats-row { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 768px) { .stats-row { grid-template-columns: 1fr; } }
</style>
