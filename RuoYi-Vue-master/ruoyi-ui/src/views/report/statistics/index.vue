<template>
  <div class="app-container report-statistics-page">
    <el-card shadow="never" class="filter-card">
      <div slot="header" class="filter-card__header">
        <span>报表统计</span>
        <el-radio-group v-model="activeModule" size="small" @change="handleModuleChange">
          <el-radio-button v-if="canViewRepair" label="repair">维修管理</el-radio-button>
          <el-radio-button v-if="canViewAlarm" label="alarm">告警中心</el-radio-button>
        </el-radio-group>
      </div>

      <el-form ref="queryForm" :model="queryParams" label-width="76px" size="small" inline class="filter-form">
        <el-form-item label="时间范围">
          <el-radio-group v-model="queryParams.rangeType" size="small" @change="handleRangeTypeChange">
            <el-radio-button label="today">今日</el-radio-button>
            <el-radio-button label="week">本周</el-radio-button>
            <el-radio-button label="month">本月</el-radio-button>
            <el-radio-button label="year">本年</el-radio-button>
            <el-radio-button label="custom">自定义</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="queryParams.rangeType === 'custom'" label="起止时间">
          <el-date-picker
            v-model="customDateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 360px"
          />
        </el-form-item>

        <el-form-item label="农场">
          <el-select v-model="queryParams.farmName" clearable filterable placeholder="全部农场" class="filter-select">
            <el-option v-for="item in filterOptions.farmOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>

        <el-form-item label="地块">
          <el-select v-model="queryParams.plotLocation" clearable filterable placeholder="全部地块" class="filter-select">
            <el-option v-for="item in filterOptions.plotOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>

        <el-form-item label="设备类型">
          <el-select v-model="queryParams.deviceType" clearable filterable placeholder="全部类型" class="filter-select">
            <el-option v-for="item in filterOptions.deviceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>

        <el-form-item label="设备编号">
          <el-select v-model="queryParams.nodeCode" clearable filterable placeholder="全部设备" class="filter-select">
            <el-option v-for="item in filterOptions.deviceCodeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>

        <el-form-item :label="activeModule === 'repair' ? '维修人员' : '处理人员'">
          <el-select v-model="queryParams.personName" clearable filterable placeholder="全部人员" class="filter-select">
            <el-option v-for="item in filterOptions.personOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>

        <el-form-item v-if="activeModule === 'alarm'" label="告警等级">
          <el-select v-model="queryParams.alarmLevel" clearable placeholder="全部等级" class="filter-select filter-select--sm">
            <el-option v-for="item in alarmLevelOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>

        <el-form-item class="filter-actions">
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button type="warning" plain icon="el-icon-download" @click="handleExport">导出Excel</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-alert
      v-if="loadError"
      :title="loadError"
      type="error"
      show-icon
      :closable="false"
      class="page-alert"
    />

    <div v-loading="loading" class="report-body">
      <template v-if="activeModule === 'repair'">
        <el-row :gutter="16" class="metric-row">
          <el-col v-for="card in repairSummaryCards" :key="card.label" :xs="12" :sm="8" :lg="4">
            <el-card shadow="hover" class="metric-card" :class="card.className">
              <div class="metric-card__value">{{ card.value }}</div>
              <div class="metric-card__label">{{ card.label }}</div>
              <div class="metric-card__extra" v-if="card.extra">{{ card.extra }}</div>
            </el-card>
          </el-col>
        </el-row>

        <el-empty v-if="!loading && !loadError && !repairHasData" description="当前筛选条件下暂无维修统计数据" />

        <template v-else>
          <div class="chart-grid chart-grid--double">
            <el-card shadow="never" class="chart-card">
              <div slot="header">工单趋势</div>
              <div ref="repairTrendChart" class="chart-box" />
            </el-card>
            <el-card shadow="never" class="chart-card">
              <div slot="header">设备类型维修分布</div>
              <div ref="repairDeviceChart" class="chart-box" />
            </el-card>
          </div>

          <div class="chart-grid chart-grid--double">
            <el-card shadow="never" class="chart-card">
              <div slot="header">故障类型分布</div>
              <div ref="repairFaultChart" class="chart-box" />
            </el-card>
            <el-card shadow="never" class="chart-card">
              <div slot="header">维修人员效率</div>
              <div ref="repairTechChart" class="chart-box" />
            </el-card>
          </div>

          <div class="chart-grid chart-grid--rank">
            <el-card shadow="never" class="chart-card">
              <div slot="header">故障最多设备 Top 10</div>
              <div class="rank-list">
                <div v-for="(item, index) in repairData.deviceTop10" :key="item.code + index" class="rank-item">
                  <div class="rank-item__left">
                    <span class="rank-item__index">{{ index + 1 }}</span>
                    <div>
                      <div class="rank-item__title">{{ item.label }}</div>
                      <div class="rank-item__sub">{{ item.code || '--' }}</div>
                    </div>
                  </div>
                  <div class="rank-item__value">{{ item.count }}</div>
                </div>
              </div>
            </el-card>
          </div>
        </template>
      </template>

      <template v-else-if="activeModule === 'alarm'">
        <el-row :gutter="16" class="metric-row">
          <el-col v-for="card in alarmSummaryCards" :key="card.label" :xs="12" :sm="8" :lg="4">
            <el-card shadow="hover" class="metric-card" :class="card.className">
              <div class="metric-card__value">{{ card.value }}</div>
              <div class="metric-card__label">{{ card.label }}</div>
              <div class="metric-card__extra" v-if="card.extra">{{ card.extra }}</div>
            </el-card>
          </el-col>
        </el-row>

        <el-empty v-if="!loading && !loadError && !alarmHasData" description="当前筛选条件下暂无告警统计数据" />

        <template v-else>
          <div class="chart-grid chart-grid--double">
            <el-card shadow="never" class="chart-card">
              <div slot="header">告警趋势</div>
              <div ref="alarmTrendChart" class="chart-box" />
            </el-card>
            <el-card shadow="never" class="chart-card">
              <div slot="header">告警等级占比</div>
              <div ref="alarmLevelChart" class="chart-box" />
            </el-card>
          </div>

          <div class="chart-grid chart-grid--double">
            <el-card shadow="never" class="chart-card">
              <div slot="header">设备类型告警分布</div>
              <div ref="alarmDeviceChart" class="chart-box" />
            </el-card>
            <el-card shadow="never" class="chart-card">
              <div slot="header">区域告警分布</div>
              <div ref="alarmAreaChart" class="chart-box" />
            </el-card>
          </div>

          <div class="chart-grid chart-grid--double">
            <el-card shadow="never" class="chart-card">
              <div slot="header">告警最多设备 Top 10</div>
              <div class="rank-list">
                <div v-for="(item, index) in alarmData.deviceTop10" :key="item.code + index" class="rank-item">
                  <div class="rank-item__left">
                    <span class="rank-item__index">{{ index + 1 }}</span>
                    <div>
                      <div class="rank-item__title">{{ item.label }}</div>
                      <div class="rank-item__sub">{{ item.code || '--' }}</div>
                    </div>
                  </div>
                  <div class="rank-item__value">{{ item.count }}</div>
                </div>
              </div>
            </el-card>

            <el-card shadow="never" class="chart-card">
              <div slot="header">重复告警设备 Top 10</div>
              <div class="rank-list">
                <div v-for="(item, index) in alarmData.repeatTop10" :key="item.code + index" class="rank-item">
                  <div class="rank-item__left">
                    <span class="rank-item__index">{{ index + 1 }}</span>
                    <div>
                      <div class="rank-item__title">{{ item.label }}</div>
                      <div class="rank-item__sub">{{ item.code || '--' }}</div>
                    </div>
                  </div>
                  <div class="rank-item__value">{{ item.count }}</div>
                </div>
              </div>
            </el-card>
          </div>
        </template>
      </template>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons')
import { getAlarmReport, getRepairReport, getReportFilterOptions } from '@/api/report/statistics'

export default {
  name: 'ReportStatistics',
  data() {
    return {
      loading: false,
      loadError: '',
      canViewRepair: false,
      canViewAlarm: false,
      activeModule: 'repair',
      customDateRange: [],
      filterOptions: {
        farmOptions: [],
        plotOptions: [],
        deviceTypeOptions: [],
        deviceCodeOptions: [],
        personOptions: []
      },
      alarmLevelOptions: [
        { label: '严重', value: '1' },
        { label: '警告', value: '2' }
      ],
      queryParams: {
        rangeType: 'month',
        farmName: undefined,
        plotLocation: undefined,
        deviceType: undefined,
        nodeCode: undefined,
        personName: undefined,
        alarmLevel: undefined
      },
      repairData: {
        summary: {},
        trend: [],
        deviceTypeStats: [],
        faultTypeStats: [],
        technicianStats: [],
        deviceTop10: []
      },
      alarmData: {
        summary: {},
        trend: [],
        levelShare: [],
        deviceTypeStats: [],
        areaStats: [],
        deviceTop10: [],
        repeatTop10: []
      },
      chartInstances: {}
    }
  },
  computed: {
    repairHasData() {
      return Number(this.repairData.summary.totalCount || 0) > 0
    },
    alarmHasData() {
      return Number(this.alarmData.summary.totalCount || 0) > 0
    },
    repairSummaryCards() {
      const summary = this.repairData.summary || {}
      return [
        { label: '工单总数', value: this.toNumber(summary.totalCount), className: 'is-primary' },
        { label: '待处理', value: this.toNumber(summary.pendingCount), className: 'is-warning' },
        { label: '处理中', value: this.toNumber(summary.processingCount), className: 'is-info' },
        { label: '已完成', value: this.toNumber(summary.doneCount), className: 'is-success' },
        { label: '已取消', value: this.toNumber(summary.cancelledCount), className: 'is-muted' },
        {
          label: '超时工单',
          value: this.toNumber(summary.timeoutCount),
          className: 'is-danger',
          extra: '超时率 ' + this.formatPercent(summary.timeoutRate)
        },
        { label: '平均响应', value: this.formatHour(summary.avgResponseHours), className: 'is-primary' },
        { label: '平均完成', value: this.formatHour(summary.avgCompleteHours), className: 'is-success' }
      ]
    },
    alarmSummaryCards() {
      const summary = this.alarmData.summary || {}
      return [
        { label: '告警总数', value: this.toNumber(summary.totalCount), className: 'is-primary' },
        { label: '未确认', value: this.toNumber(summary.unconfirmedCount), className: 'is-danger' },
        { label: '已确认', value: this.toNumber(summary.confirmedCount), className: 'is-success' },
        { label: '处理中', value: this.toNumber(summary.processingCount), className: 'is-warning' },
        { label: '已恢复', value: this.toNumber(summary.recoveredCount), className: 'is-info' },
        { label: '误报数', value: this.toNumber(summary.falsePositiveCount), className: 'is-muted' },
        { label: '平均确认', value: this.formatHour(summary.avgConfirmHours), className: 'is-primary' },
        { label: '平均恢复', value: this.formatHour(summary.avgRecoverHours), className: 'is-info' }
      ]
    }
  },
  created() {
    this.canViewRepair = this.$auth.hasPermiOr(['report:statistics:view', 'repair:admin:list', 'repair:tech:list'])
    this.canViewAlarm = this.$auth.hasPermiOr(['report:statistics:view', 'agri:monitor:view'])
    if (!this.canViewRepair && this.canViewAlarm) {
      this.activeModule = 'alarm'
    }
    this.loadFilterOptions()
    this.loadReport()
  },
  mounted() {
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts)
    Object.keys(this.chartInstances).forEach(key => {
      if (this.chartInstances[key]) {
        this.chartInstances[key].dispose()
        this.chartInstances[key] = null
      }
    })
  },
  methods: {
    loadFilterOptions() {
      getReportFilterOptions().then(res => {
        this.filterOptions = Object.assign({}, this.filterOptions, res.data || {})
      })
    },
    handleRangeTypeChange(value) {
      if (value !== 'custom') {
        this.customDateRange = []
      }
    },
    handleModuleChange() {
      this.loadReport()
    },
    handleQuery() {
      this.loadReport()
    },
    resetQuery() {
      this.queryParams = {
        rangeType: 'month',
        farmName: undefined,
        plotLocation: undefined,
        deviceType: undefined,
        nodeCode: undefined,
        personName: undefined,
        alarmLevel: undefined
      }
      this.customDateRange = []
      this.loadReport()
    },
    buildParams() {
      const params = Object.assign({}, this.queryParams)
      if (params.rangeType === 'custom' && this.customDateRange.length === 2) {
        params.beginTime = this.customDateRange[0]
        params.endTime = this.customDateRange[1]
      }
      if (this.activeModule !== 'alarm') {
        delete params.alarmLevel
      }
      return params
    },
    loadReport() {
      this.loading = true
      this.loadError = ''
      const params = this.buildParams()
      const request = this.activeModule === 'repair' ? getRepairReport(params) : getAlarmReport(params)
      request.then(res => {
        if (this.activeModule === 'repair') {
          this.repairData = this.normalizeRepairData(res.data || {})
          this.$nextTick(() => this.renderRepairCharts())
        } else {
          this.alarmData = this.normalizeAlarmData(res.data || {})
          this.$nextTick(() => this.renderAlarmCharts())
        }
      }).catch(() => {
        this.loadError = '统计数据加载失败，请检查筛选条件或稍后重试'
      }).finally(() => {
        this.loading = false
      })
    },
    handleExport() {
      const params = this.buildParams()
      const isRepair = this.activeModule === 'repair'
      const url = isRepair ? 'report/statistics/repair/export' : 'report/statistics/alarm/export'
      const name = isRepair ? '维修报表' : '告警报表'
      this.download(url, params, `${name}_${Date.now()}.xlsx`)
    },
    normalizeRepairData(data) {
      return Object.assign({
        summary: {},
        trend: [],
        deviceTypeStats: [],
        faultTypeStats: [],
        technicianStats: [],
        deviceTop10: []
      }, data)
    },
    normalizeAlarmData(data) {
      return Object.assign({
        summary: {},
        trend: [],
        levelShare: [],
        deviceTypeStats: [],
        areaStats: [],
        deviceTop10: [],
        repeatTop10: []
      }, data)
    },
    getChart(refName) {
      if (!this.$refs[refName]) {
        return null
      }
      if (!this.chartInstances[refName]) {
        this.chartInstances[refName] = echarts.init(this.$refs[refName], 'macarons')
      }
      return this.chartInstances[refName]
    },
    renderRepairCharts() {
      if (!this.repairHasData) {
        this.clearCharts(['repairTrendChart', 'repairDeviceChart', 'repairFaultChart', 'repairTechChart'])
        return
      }
      const trendChart = this.getChart('repairTrendChart')
      const deviceChart = this.getChart('repairDeviceChart')
      const faultChart = this.getChart('repairFaultChart')
      const techChart = this.getChart('repairTechChart')
      const trend = this.repairData.trend || []
      const deviceStats = this.repairData.deviceTypeStats || []
      const faultStats = this.repairData.faultTypeStats || []
      const technicianStats = this.repairData.technicianStats || []

      trendChart && trendChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { top: 0 },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: trend.map(item => item.label) },
        yAxis: { type: 'value', name: '工单数' },
        series: [
          { name: '总数', type: 'line', smooth: true, data: trend.map(item => item.totalCount || 0) },
          { name: '待处理', type: 'line', smooth: true, data: trend.map(item => item.pendingCount || 0) },
          { name: '处理中', type: 'line', smooth: true, data: trend.map(item => item.processingCount || 0) },
          { name: '已完成', type: 'line', smooth: true, data: trend.map(item => item.doneCount || 0) }
        ]
      }, true)

      deviceChart && deviceChart.setOption(this.buildBarOption('维修数', deviceStats, false), true)
      faultChart && faultChart.setOption(this.buildBarOption('维修数', faultStats, true), true)
      techChart && techChart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: { top: 0 },
        grid: { left: '3%', right: '4%', bottom: '10%', containLabel: true },
        xAxis: { type: 'category', data: technicianStats.map(item => item.label), axisLabel: { rotate: 20 } },
        yAxis: [
          { type: 'value', name: '工单数' },
          { type: 'value', name: '完成率(%)' }
        ],
        series: [
          { name: '工单数', type: 'bar', data: technicianStats.map(item => item.count || 0), barMaxWidth: 28 },
          { name: '完成率', type: 'line', yAxisIndex: 1, smooth: true, data: technicianStats.map(item => item.ratio || 0) }
        ]
      }, true)
    },
    renderAlarmCharts() {
      if (!this.alarmHasData) {
        this.clearCharts(['alarmTrendChart', 'alarmLevelChart', 'alarmDeviceChart', 'alarmAreaChart'])
        return
      }
      const trendChart = this.getChart('alarmTrendChart')
      const levelChart = this.getChart('alarmLevelChart')
      const deviceChart = this.getChart('alarmDeviceChart')
      const areaChart = this.getChart('alarmAreaChart')
      const trend = this.alarmData.trend || []
      const levelShare = this.alarmData.levelShare || []

      trendChart && trendChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { top: 0 },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: trend.map(item => item.label) },
        yAxis: { type: 'value', name: '告警数' },
        series: [
          { name: '总数', type: 'line', smooth: true, data: trend.map(item => item.totalCount || 0) },
          { name: '未确认', type: 'line', smooth: true, data: trend.map(item => item.unconfirmedCount || 0) },
          { name: '已确认', type: 'line', smooth: true, data: trend.map(item => item.confirmedCount || 0) }
        ]
      }, true)

      levelChart && levelChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} 条 ({d}%)' },
        legend: { orient: 'vertical', right: 10, top: 'center' },
        series: [{
          type: 'pie',
          radius: ['45%', '72%'],
          center: ['38%', '52%'],
          label: { formatter: '{b}\n{c}条' },
          data: levelShare.map(item => ({ name: item.label, value: item.count || 0 }))
        }]
      }, true)

      deviceChart && deviceChart.setOption(this.buildBarOption('告警数', this.alarmData.deviceTypeStats || [], false), true)
      areaChart && areaChart.setOption(this.buildBarOption('告警数', this.alarmData.areaStats || [], true), true)
    },
    buildBarOption(name, list, rotate) {
      return {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '4%', bottom: rotate ? '12%' : '3%', containLabel: true },
        xAxis: { type: 'category', data: list.map(item => item.label), axisLabel: { rotate: rotate ? 25 : 0 } },
        yAxis: { type: 'value', name },
        series: [{
          name,
          type: 'bar',
          data: list.map(item => item.count || 0),
          barMaxWidth: 28,
          itemStyle: { borderRadius: [4, 4, 0, 0] }
        }]
      }
    },
    clearCharts(refNames) {
      refNames.forEach(refName => {
        if (this.chartInstances[refName]) {
          this.chartInstances[refName].clear()
        }
      })
    },
    resizeCharts() {
      Object.keys(this.chartInstances).forEach(key => {
        if (this.chartInstances[key]) {
          this.chartInstances[key].resize()
        }
      })
    },
    toNumber(value) {
      return Number(value || 0)
    },
    formatPercent(value) {
      return Number(value || 0).toFixed(2) + '%'
    },
    formatHour(value) {
      if (value == null || value === '') {
        return '--'
      }
      return Number(value).toFixed(2) + 'h'
    }
  }
}
</script>

<style scoped>
.report-statistics-page {
  background: #f5f7fa;
}

.filter-card {
  margin-bottom: 16px;
}

.filter-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
}

.filter-select {
  width: 180px;
}

.filter-select--sm {
  width: 120px;
}

.filter-actions {
  margin-left: auto;
}

.page-alert {
  margin-bottom: 16px;
}

.metric-row {
  margin-bottom: 16px;
}

.metric-card {
  margin-bottom: 16px;
  border-radius: 8px;
  min-height: 118px;
}

.metric-card__value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1.25;
}

.metric-card__label {
  margin-top: 8px;
  font-size: 13px;
  color: #909399;
}

.metric-card__extra {
  margin-top: 10px;
  font-size: 12px;
  color: #606266;
}

.metric-card.is-primary {
  border-top: 3px solid #409eff;
}

.metric-card.is-success {
  border-top: 3px solid #67c23a;
}

.metric-card.is-warning {
  border-top: 3px solid #e6a23c;
}

.metric-card.is-danger {
  border-top: 3px solid #f56c6c;
}

.metric-card.is-info {
  border-top: 3px solid #909399;
}

.metric-card.is-muted {
  border-top: 3px solid #c0c4cc;
}

.chart-grid {
  display: grid;
  gap: 16px;
  margin-bottom: 16px;
}

.chart-grid--double {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.chart-grid--rank {
  grid-template-columns: minmax(0, 1fr);
}

.chart-card {
  border-radius: 8px;
}

.chart-box {
  height: 340px;
}

.rank-list {
  display: flex;
  flex-direction: column;
}

.rank-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f2f5;
}

.rank-item:last-child {
  border-bottom: 0;
}

.rank-item__left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.rank-item__index {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #ecf5ff;
  color: #409eff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 24px;
  font-size: 12px;
  font-weight: 600;
}

.rank-item__title {
  font-size: 14px;
  color: #303133;
}

.rank-item__sub {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

.rank-item__value {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  flex: 0 0 auto;
}

@media (max-width: 1200px) {
  .chart-grid--double {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .filter-card__header {
    flex-direction: column;
    align-items: flex-start;
  }

  .filter-select,
  .filter-select--sm {
    width: 100%;
  }

  .chart-box {
    height: 300px;
  }

  .rank-item__value {
    font-size: 18px;
  }
}
</style>
