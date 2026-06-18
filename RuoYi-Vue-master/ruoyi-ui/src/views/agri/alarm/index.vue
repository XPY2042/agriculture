<template>
  <div class="app-container agri-alarm-page">
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card stat-card--danger" @click.native="filterByStatus('0')">
          <div class="stat-card__inner">
            <i class="el-icon-warning stat-card__icon" />
            <div>
              <div class="stat-card__value">{{ unhandledCount }}</div>
              <div class="stat-card__label">{{ t.unhandledAlarms }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card stat-card--warn">
          <div class="stat-card__inner">
            <i class="el-icon-bell stat-card__icon" />
            <div>
              <div class="stat-card__value">{{ statWarning }}</div>
              <div class="stat-card__label">{{ t.pageWarningLevel }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card stat-card--info">
          <div class="stat-card__inner">
            <i class="el-icon-data-analysis stat-card__icon" />
            <div>
              <div class="stat-card__value">{{ total }}</div>
              <div class="stat-card__label">{{ t.matchedRecords }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="88px"
    >
      <el-form-item :label="t.sensorNode" prop="nodeId">
        <el-select v-model="queryParams.nodeId" :placeholder="t.allNodes" clearable filterable style="width: 220px">
          <el-option
            v-for="n in nodeOptions"
            :key="n.nodeId"
            :label="n.nodeName + ' (' + n.nodeCode + ')'"
            :value="n.nodeId"
          />
        </el-select>
      </el-form-item>
      <el-form-item :label="t.metric" prop="metricCode">
        <el-select v-model="queryParams.metricCode" :placeholder="t.allMetrics" clearable style="width: 140px">
          <el-option v-for="m in metricOptions" :key="m.value" :label="m.label" :value="m.value" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t.alarmLevel" prop="alarmLevel">
        <el-select v-model="queryParams.alarmLevel" :placeholder="t.allLevels" clearable style="width: 120px">
          <el-option :label="t.levelCritical" value="1" />
          <el-option :label="t.levelWarning" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t.handleStatus" prop="status">
        <el-select v-model="queryParams.status" :placeholder="t.allStatus" clearable style="width: 120px">
          <el-option :label="t.statusOpen" value="0" />
          <el-option :label="t.statusDone" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t.alarmTime">
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          :range-separator="t.rangeSep"
          :start-placeholder="t.timeStart"
          :end-placeholder="t.timeEnd"
          value-format="yyyy-MM-dd HH:mm:ss"
          style="width: 360px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ t.search }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ t.reset }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-check"
          size="mini"
          :disabled="multiple"
          @click="handleConfirm"
          v-hasPermi="['agri:monitor:view']"
        >{{ t.batchConfirm }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-refresh"
          size="mini"
          @click="handleScan"
          v-hasPermi="['agri:monitor:view']"
        >{{ t.scanLatest }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-monitor" size="mini" @click="goMonitor" v-hasPermi="['agri:monitor:view']">
          {{ t.envMonitor }}
        </el-button>
      </el-col>
      <el-col :span="14" class="quick-tabs">
        <el-radio-group v-model="quickStatus" size="mini" @change="onQuickStatusChange">
          <el-radio-button label="">{{ t.tabAll }}</el-radio-button>
          <el-radio-button label="0">{{ t.tabOpen }}</el-radio-button>
          <el-radio-button label="1">{{ t.tabDone }}</el-radio-button>
        </el-radio-group>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['agri:monitor:view']">导出Excel</el-button>
      </el-col>
      <el-button type="text" icon="el-icon-data-line" size="mini" @click="showStats=!showStats">{{ showStats ? '返回列表' : '统计视图' }}</el-button>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-alert
      v-if="unhandledCount > 0 && queryParams.status !== '1'"
      :title="bannerText"
      type="error"
      show-icon
      :closable="false"
      class="mb8"
    />

    <el-table
      v-loading="loading"
      :data="alarmList"
      row-key="alarmId"
      @selection-change="handleSelectionChange"
      :row-class-name="tableRowClassName"
    >
      <el-table-column type="selection" width="50" align="center" :selectable="rowSelectable" />
      <el-table-column :label="t.colLevel" align="center" width="76">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.alarmLevel === '1'" type="danger" size="small" effect="dark">{{ t.levelCritical }}</el-tag>
          <el-tag v-else type="warning" size="small">{{ t.levelWarning }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="t.colNode" align="center" prop="nodeName" width="150" :show-overflow-tooltip="true" />
      <el-table-column :label="t.colMetric" align="center" width="100">
        <template slot-scope="scope">
          <span class="metric-name">{{ metricLabel(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="t.colActual" align="center" prop="actualValue" width="100" />
      <el-table-column :label="t.colThreshold" align="center" prop="thresholdValue" width="100" />
      <el-table-column :label="t.colDirection" align="center" width="72">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.direction === 'low'" type="info" size="mini">{{ t.dirLow }}</el-tag>
          <el-tag v-else-if="scope.row.direction === 'high'" type="info" size="mini">{{ t.dirHigh }}</el-tag>
          <span v-else>{{ t.dash }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="t.colMessage" align="left" min-width="280">
        <template slot-scope="scope">
          <span class="alarm-msg">{{ displayMessage(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="t.colStatus" align="center" width="88">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="danger" size="small">{{ t.statusOpen }}</el-tag>
          <el-tag v-else type="success" size="small">{{ t.statusDone }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="t.colTime" align="center" width="158">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.alarmTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="t.colAction" align="center" width="120" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">{{ t.detail }}</el-button>
          <el-button
            v-if="scope.row.status === '0'"
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleConfirmOne(scope.row)"
            v-hasPermi="['agri:monitor:view']"
          >{{ t.confirm }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-empty v-if="!loading && alarmList.length === 0" :description="t.emptyDesc">
      <el-button type="primary" size="small" @click="handleScan" v-hasPermi="['agri:monitor:view']">{{ t.scanLatest }}</el-button>
    </el-empty>

    <el-drawer :title="t.drawerTitle" :visible.sync="detailOpen" direction="rtl" size="480px" append-to-body>
      <div v-if="detailRow" class="detail-body">
        <el-descriptions :column="1" border size="medium">
          <el-descriptions-item :label="t.dAlarmId">{{ detailRow.alarmId }}</el-descriptions-item>
          <el-descriptions-item :label="t.dLevel">
            <el-tag v-if="detailRow.alarmLevel === '1'" type="danger" size="small">{{ t.levelCritical }}</el-tag>
            <el-tag v-else type="warning" size="small">{{ t.levelWarning }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item :label="t.dNode">{{ detailRow.nodeName || t.dash }}</el-descriptions-item>
          <el-descriptions-item :label="t.dMetric">
            {{ metricLabel(detailRow) }}��{{ detailRow.metricCode }}��
          </el-descriptions-item>
          <el-descriptions-item :label="t.dActualThreshold">
            {{ detailRow.actualValue }} / {{ detailRow.thresholdValue }}
          </el-descriptions-item>
          <el-descriptions-item :label="t.dDirection">{{ directionText(detailRow.direction) }}</el-descriptions-item>
          <el-descriptions-item :label="t.dMessage">{{ displayMessage(detailRow) }}</el-descriptions-item>
          <el-descriptions-item :label="t.dStatus">
            <el-tag v-if="detailRow.status === '0'" type="danger" size="small">{{ t.statusOpen }}</el-tag>
            <el-tag v-else type="success" size="small">{{ t.statusDone }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item :label="t.dTime">{{ parseTime(detailRow.alarmTime) }}</el-descriptions-item>
          <el-descriptions-item v-if="detailRow.status === '1'" :label="t.dHandler">{{ detailRow.handleBy || t.dash }}</el-descriptions-item>
          <el-descriptions-item v-if="detailRow.status === '1'" :label="t.dHandleTime">{{ parseTime(detailRow.handleTime) }}</el-descriptions-item>
          <el-descriptions-item :label="t.dReadingId">{{ detailRow.readingId || t.dash }}</el-descriptions-item>
        </el-descriptions>
        <div class="detail-actions" v-if="detailRow.status === '0'">
          <el-button type="primary" icon="el-icon-check" @click="handleConfirmOne(detailRow)" v-hasPermi="['agri:monitor:view']">
            {{ t.confirmHandled }}
          </el-button>
        </div>
      </div>
    </el-drawer>

    <!-- ====== 统计视图 ====== -->
    <div v-if="showStats" class="stats-dashboard">
      <!-- 第一行：饼图 + 柱状图 左右分栏 -->
      <div class="stats-row-top">
        <div class="stats-card">
          <div class="stats-card__head">告警等级占比</div>
          <div class="stats-card__body"><div ref="levelChart" class="stats-chart" /></div>
        </div>
        <div class="stats-card">
          <div class="stats-card__head">告警指标分布</div>
          <div class="stats-card__body"><div ref="metricChart" class="stats-chart" /></div>
        </div>
      </div>
      <!-- 第二行：趋势图通栏 -->
      <div class="stats-card stats-card--full">
        <div class="stats-card__head">近30天告警趋势</div>
        <div class="stats-card__body"><div ref="trendChart" class="stats-chart" /></div>
      </div>
    </div>
  </div>
</template>

<script>
import t from './labels-zh'
import { listAgriAlarm, getAgriAlarm, getUnhandledAlarmCount, confirmAlarms, scanAlarms } from '@/api/agri/alarm'
import { listAgriNode } from '@/api/agri/node'
import { alarmDistribution, alarmTrend } from '@/api/agri/statistics'
import * as echarts from 'echarts'
require('echarts/theme/macarons')

const METRIC_CODE_LABEL = {
  soil_moisture: t.metricSoil,
  air_temp: t.metricAirT,
  air_humidity: t.metricAirH,
  light: t.metricLight,
  soil_temp: t.metricSoilT,
  soil_ph: t.metricSoilPh,
  co2: t.metricCo2,
  water_ph: t.metricWaterPh
}

export default {
  name: 'AgriAlarmCenter',
  data() {
    return {
      t,
      loading: false,
      showSearch: true,
      showStats: false,
      levelChartInst: null,
      metricChartInst: null,
      trendChartInst: null,
      total: 0,
      alarmList: [],
      nodeOptions: [],
      metricOptions: [
        { label: t.metricSoil, value: 'soil_moisture' },
        { label: t.metricAirT, value: 'air_temp' },
        { label: t.metricAirH, value: 'air_humidity' },
        { label: t.metricLight, value: 'light' },
        { label: t.metricSoilT, value: 'soil_temp' },
        { label: t.metricSoilPh, value: 'soil_ph' },
        { label: t.metricCo2, value: 'co2' },
        { label: t.metricWaterPh, value: 'water_ph' }
      ],
      unhandledCount: 0,
      statWarning: 0,
      ids: [],
      multiple: true,
      dateRange: [],
      quickStatus: '',
      detailOpen: false,
      detailRow: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        nodeId: undefined,
        metricCode: undefined,
        alarmLevel: undefined,
        status: undefined
      }
    }
  },
  computed: {
    bannerText() {
      return t.alertBanner.replace('{n}', String(this.unhandledCount))
    }
  },
  created() {
    this.applyRouteQuery()
    this.loadNodes()
    this.getList()
    this.loadUnhandledCount()
  },
  methods: {
    metricLabel(row) {
      if (!row) return t.dash
      const code = row.metricCode
      if (code && METRIC_CODE_LABEL[code]) {
        return METRIC_CODE_LABEL[code]
      }
      const name = row.metricName
      if (name && !/[\uFFFD?]/.test(name) && !/^[\?]+$/.test(name)) {
        return name
      }
      return code || t.dash
    },
    displayMessage(row) {
      if (!row) return ''
      const msg = row.alarmMessage
      if (!msg) return ''
      if (/[\uFFFD]/.test(msg) || (msg.indexOf('?') >= 0 && msg.replace(/\?/g, '').length < msg.length * 0.5)) {
        const node = row.nodeName ? '\u3010' + row.nodeName + '\u3011' : ''
        return node + this.metricLabel(row) + '\uff1a' + (row.actualValue || '') + ' / ' + (row.thresholdValue || '')
      }
      return msg
    },
    applyRouteQuery() {
      const q = this.$route.query
      if (q.status === '0' || q.status === '1') {
        this.queryParams.status = q.status
        this.quickStatus = q.status
      }
    },
    loadNodes() {
      listAgriNode({ pageNum: 1, pageSize: 500, status: '0' }).then(res => {
        this.nodeOptions = res.rows || []
      })
    },
    loadUnhandledCount() {
      getUnhandledAlarmCount().then(res => {
        this.unhandledCount = res.data != null ? res.data : 0
      }).catch(() => {
        this.unhandledCount = 0
      })
    },
    calcPageStats() {
      let w = 0
      this.alarmList.forEach(row => {
        if (row.alarmLevel === '2') w++
      })
      this.statWarning = w
    },
    getList() {
      this.loading = true
      const params = { ...this.queryParams }
      if (this.dateRange && this.dateRange.length === 2) {
        params.params = { beginTime: this.dateRange[0], endTime: this.dateRange[1] }
      }
      listAgriAlarm(params).then(res => {
        this.alarmList = res.rows || []
        this.total = res.total || 0
        this.calcPageStats()
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.syncQuickStatus()
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
      this.quickStatus = ''
      this.resetForm('queryForm')
      this.queryParams.status = undefined
      this.handleQuery()
    },
    syncQuickStatus() {
      this.quickStatus = this.queryParams.status != null && this.queryParams.status !== ''
        ? this.queryParams.status
        : ''
    },
    onQuickStatusChange(val) {
      this.queryParams.status = val === '' ? undefined : val
      this.queryParams.pageNum = 1
      this.getList()
    },
    filterByStatus(status) {
      this.quickStatus = status
      this.queryParams.status = status
      this.queryParams.pageNum = 1
      this.getList()
    },
    rowSelectable(row) {
      return row.status === '0'
    },
    tableRowClassName({ row }) {
      if (row.status === '0' && row.alarmLevel === '1') {
        return 'row-critical'
      }
      if (row.status === '0') {
        return 'row-pending'
      }
      return ''
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.alarmId)
      this.multiple = !selection.length
    },
    handleConfirm() {
      if (!this.ids.length) return
      this.doConfirm(this.ids)
    },
    handleConfirmOne(row) {
      this.doConfirm([row.alarmId])
    },
    doConfirm(alarmIds) {
      this.$modal.confirm(t.confirmPrompt).then(() => {
        return confirmAlarms(alarmIds)
      }).then(() => {
        this.$modal.msgSuccess(t.confirmOk)
        this.detailOpen = false
        this.getList()
        this.loadUnhandledCount()
      }).catch(() => {})
    },
    handleScan() {
      this.$modal.confirm(t.scanPrompt).then(() => {
        return scanAlarms()
      }).then(res => {
        this.$modal.msgSuccess(res.msg || t.scanOk)
        this.getList()
        this.loadUnhandledCount()
      }).catch(() => {})
    },
    handleDetail(row) {
      getAgriAlarm(row.alarmId).then(res => {
        this.detailRow = res.data || row
        this.detailOpen = true
      }).catch(() => {
        this.detailRow = row
        this.detailOpen = true
      })
    },
    directionText(dir) {
      if (dir === 'low') return t.dirLow
      if (dir === 'high') return t.dirHigh
      return t.dash
    },
    goMonitor() {
      this.$router.push({ path: '/agri/agriEnv' }).catch(() => {})
    },
    handleExport() {
      this.download('agri/alarm/export', { ...this.queryParams }, `告警数据_${Date.now()}.xlsx`)
    },
    // ====== 统计图表方法 ======
    loadStats() {
      alarmDistribution('level').then(res => this.renderLevelChart(res.data || []))
      alarmDistribution('metric').then(res => {
        const deduped = []; const seen = new Set()
        ;(res.data || []).forEach(d => { if (!seen.has(d.name)) { seen.add(d.name); deduped.push(d) } })
        this.renderMetricChart(deduped)
      })
      alarmTrend(30).then(res => this.renderTrendChart(res.data || []))
    },
    initOrGetChart(refKey, instKey) {
      if (this[instKey]) return this[instKey]
      if (!this.$refs[refKey]) return null
      this[instKey] = echarts.init(this.$refs[refKey], 'macarons')
      return this[instKey]
    },
    renderLevelChart(data) {
      const chart = this.initOrGetChart('levelChart', 'levelChartInst')
      if (!chart) return
      chart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} 条 ({d}%)' },
        legend: { orient: 'vertical', right: 10, top: 'center', textStyle: { fontSize: 12 } },
        series: [{
          type: 'pie', radius: ['45%', '72%'], center: ['40%', '52%'],
          label: { formatter: '{b}\n{c}条', fontSize: 11 },
          emphasis: { label: { fontSize: 16, fontWeight: 'bold' } },
          data: data.map(d => ({ name: d.name, value: d.count }))
        }]
      }, true)
    },
    renderMetricChart(data) {
      const chart = this.initOrGetChart('metricChart', 'metricChartInst')
      if (!chart) return
      chart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '8%', bottom: '12%', top: '8%', containLabel: true },
        xAxis: { type: 'category', data: data.map(d => d.name), axisLabel: { rotate: 30, fontSize: 11 } },
        yAxis: { type: 'value', name: '告警数', nameTextStyle: { fontSize: 12 } },
        series: [{
          type: 'bar', barWidth: '50%',
          itemStyle: { borderRadius: [4, 4, 0, 0] },
          data: data.map(d => d.count)
        }]
      }, true)
    },
    renderTrendChart(data) {
      const chart = this.initOrGetChart('trendChart', 'trendChartInst')
      if (!chart) return
      chart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', top: '8%', containLabel: true },
        xAxis: { type: 'category', data: data.map(d => d.dateStr), axisLabel: { rotate: 45, fontSize: 10 } },
        yAxis: { type: 'value', name: '告警数' },
        series: [{
          type: 'line', smooth: true, symbol: 'circle', symbolSize: 6,
          areaStyle: { opacity: 0.08 }, data: data.map(d => d.alarmCount)
        }]
      }, true)
    }
  },
  watch: {
    showStats(val) {
      if (val) this.$nextTick(() => this.loadStats())
    }
  },
  beforeDestroy() {
    this.stopAlarmPoll()
    window.removeEventListener('resize', this.resizeChart)
    if (this.chart) { this.chart.dispose(); this.chart = null }
    // 清理统计图表实例
    ['levelChartInst','metricChartInst','trendChartInst'].forEach(k => {
      if (this[k]) { this[k].dispose(); this[k] = null }
    })
  }
}
</script>

<style scoped>
.stat-row {
  margin-bottom: 16px;
}
.stat-card {
  cursor: pointer;
  margin-bottom: 8px;
}
.stat-card--danger {
  border-left: 4px solid #f56c6c;
}
.stat-card--warn {
  border-left: 4px solid #e6a23c;
}
.stat-card--info {
  border-left: 4px solid #409eff;
}
.stat-card__inner {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-card__icon {
  font-size: 36px;
  color: #909399;
}
.stat-card--danger .stat-card__icon {
  color: #f56c6c;
}
.stat-card--warn .stat-card__icon {
  color: #e6a23c;
}
.stat-card__value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1.2;
}
.stat-card__label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}
.quick-tabs {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  min-height: 28px;
}
.alarm-msg {
  font-size: 13px;
  line-height: 1.5;
  color: #606266;
}
.metric-name {
  font-weight: 500;
}
.detail-body {
  padding: 0 20px 24px;
}
.detail-actions {
  margin-top: 24px;
  text-align: center;
}
/* ====== 统计视图 ====== */
.stats-dashboard { display: flex; flex-direction: column; gap: 16px; margin-top: 4px; }
.stats-row-top { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.stats-card { background: #fff; border-radius: 10px; box-shadow: 0 1px 6px rgba(0,0,0,.05); overflow: hidden; }
.stats-card__head { font-size: 14px; font-weight: 600; color: #303133; padding: 14px 18px; border-bottom: 1px solid #f0f0f0; }
.stats-card__body { padding: 8px; }
.stats-chart { height: 320px; width: 100%; }
.stats-card--full { width: 100%; }
@media (max-width: 992px) { .stats-row-top { grid-template-columns: 1fr; } }
</style>

<style>
.agri-alarm-page .row-critical {
  background-color: #fef0f0 !important;
}
.agri-alarm-page .row-pending {
  background-color: #fdf6ec !important;
}
</style>
