<template>
  <div class="monitor-dashboard">
    <!-- ========== 顶部控制栏：筛选区 + 功能按钮 同一行 ========== -->
    <div class="control-bar">
      <div class="control-filters">
        <template v-if="isAdmin">
          <span class="filter-label">所属用户</span>
          <el-select v-model="ownerUser" placeholder="选择用户" filterable clearable size="small" class="filter-select" @change="onOwnerUserChange">
            <el-option v-for="u in userOptions" :key="u.userName" :label="u.nickName + ' (' + u.userName + ')'" :value="u.userName" />
          </el-select>
        </template>
        <span class="filter-label">传感节点</span>
        <el-select v-model="nodeId" placeholder="选择节点" filterable size="small" class="filter-select filter-node" :disabled="isAdmin && !ownerUser" @change="loadAll">
          <el-option v-for="n in nodeOptions" :key="n.nodeId" :label="n.nodeName + ' (' + n.nodeCode + ')'" :value="n.nodeId" />
        </el-select>
        <span class="filter-label">时间窗口</span>
        <el-select v-model="hours" size="small" class="filter-select filter-hours" @change="loadAll">
          <el-option :value="6" label="近 6 小时" />
          <el-option :value="12" label="近 12 小时" />
          <el-option :value="24" label="近 24 小时" />
          <el-option :value="48" label="近 48 小时" />
        </el-select>
      </div>
      <div class="control-actions">
        <el-button type="primary" icon="el-icon-refresh" size="small" @click="loadAll" v-hasPermi="['agri:monitor:view']">刷新数据</el-button>
        <el-button type="info" icon="el-icon-connection" size="small" @click="checkNetwork" v-hasPermi="['agri:monitor:view']">出网检测</el-button>
        <el-button type="warning" plain icon="el-icon-download" size="small" @click="handleExport" v-hasPermi="['agri:monitor:view']">导出Excel</el-button>
        <el-button type="success" icon="el-icon-upload2" size="small" @click="doSimulate" v-hasPermi="['agri:monitor:ingest']" :disabled="!nodeId">模拟上报</el-button>
      </div>
    </div>

    <!-- ========== 管理员提示 ========== -->
    <el-alert v-if="isAdmin && !ownerUser" title="请先选择需要查看的用户，再选择其传感节点" type="info" :closable="false" class="mb16" />

    <!-- ========== 传感器数据卡片 - 网格布局 ========== -->
    <div class="metrics-grid" v-if="latest">
      <div class="metric-item">
        <div class="metric-icon icon-moisture"><i class="el-icon-waterdrop" /></div>
        <div class="metric-body">
          <div class="metric-label">土壤湿度</div>
          <div class="metric-num">{{ fmt(latest.soilMoisturePct) }}<small>%</small></div>
        </div>
      </div>
      <div class="metric-item">
        <div class="metric-icon icon-temp"><i class="el-icon-s-home" /></div>
        <div class="metric-body">
          <div class="metric-label">土壤温度</div>
          <div class="metric-num">{{ fmt(latest.soilTempC) }}<small>℃</small></div>
        </div>
      </div>
      <div class="metric-item">
        <div class="metric-icon icon-air"><i class="el-icon-cloudy-and-sunny" /></div>
        <div class="metric-body">
          <div class="metric-label">空气温度</div>
          <div class="metric-num">{{ fmt(latest.airTempC) }}<small>℃</small></div>
        </div>
      </div>
      <div class="metric-item">
        <div class="metric-icon icon-humi"><i class="el-icon-cloudy" /></div>
        <div class="metric-body">
          <div class="metric-label">空气湿度</div>
          <div class="metric-num">{{ fmt(latest.airHumidityPct) }}<small>%</small></div>
        </div>
      </div>
      <div class="metric-item">
        <div class="metric-icon icon-light"><i class="el-icon-sunny" /></div>
        <div class="metric-body">
          <div class="metric-label">光照强度</div>
          <div class="metric-num">{{ latest.lightLux != null ? latest.lightLux : '--' }}<small>lux</small></div>
        </div>
      </div>
      <div class="metric-item">
        <div class="metric-icon icon-ph"><i class="el-icon-s-marketing" /></div>
        <div class="metric-body">
          <div class="metric-label">土壤 pH</div>
          <div class="metric-num">{{ fmt(latest.soilPh) }}</div>
        </div>
      </div>
      <div class="metric-item">
        <div class="metric-icon icon-co2"><i class="el-icon-wind-power" /></div>
        <div class="metric-body">
          <div class="metric-label">CO₂ 浓度</div>
          <div class="metric-num">{{ latest.co2Ppm != null ? latest.co2Ppm : '--' }}<small>ppm</small></div>
        </div>
      </div>
      <div class="metric-item">
        <div class="metric-icon icon-water"><i class="el-icon-cup" /></div>
        <div class="metric-body">
          <div class="metric-label">水体 pH</div>
          <div class="metric-num">{{ fmt(latest.waterPh) }}</div>
        </div>
      </div>
    </div>
    <el-alert v-else title="暂无最新读数：请选择节点后刷新，或使用「模拟上报」生成演示数据" type="info" :closable="false" class="mb16" />

    <!-- ========== 告警横幅 ========== -->
    <el-alert v-if="nodeAlarms.length" type="error" show-icon :closable="false" class="mb16" v-hasPermi="['agri:monitor:view']">
      <template slot="title">当前节点存在未处理阈值告警</template>
      <ul class="alarm-inline-list">
        <li v-for="a in nodeAlarms" :key="a.alarmId">
          <el-tag :type="a.alarmLevel === '1' ? 'danger' : 'warning'" size="mini">{{ a.metricName }}</el-tag>
          {{ a.alarmMessage }}
        </li>
      </ul>
      <el-button type="text" size="mini" @click="goAlarmCenter">前往告警中心 →</el-button>
    </el-alert>

    <!-- ========== 公网天气 ========== -->
    <el-card shadow="never" class="section-card" v-hasPermi="['agri:monitor:view']" v-loading="weatherLoading">
      <div slot="header" class="card-header"><span>公网实时天气（Open-Meteo）</span><el-button type="text" size="mini" @click="loadWeather">刷新</el-button></div>
      <div class="weather-inline" v-if="weatherPublic">
        <div class="weather-stat"><span class="weather-val">{{ fmt(weatherPublic.temperatureC) }}℃</span><span class="weather-lbl">气温</span></div>
        <div class="weather-stat"><span class="weather-val">{{ fmtIntOrDec(weatherPublic.relativeHumidityPct) }}%</span><span class="weather-lbl">相对湿度</span></div>
        <div class="weather-meta">观测 {{ weatherPublic.observationTime || '--' }} · {{ weatherPublic.latitude }}, {{ weatherPublic.longitude }} · {{ weatherPublic.source }}</div>
      </div>
      <p v-else-if="!weatherLoading" class="text-muted">未能获取公网气象数据</p>
    </el-card>

    <!-- ========== 环境趋势图 ========== -->
    <el-card shadow="never" class="section-card">
      <div slot="header" class="card-header"><span>环境趋势（{{ hours }} 小时）</span></div>
      <div ref="chartRef" class="chart-container" />
    </el-card>

    <!-- ========== 作物生长建议 ========== -->
    <el-card shadow="never" class="section-card">
      <div slot="header" class="card-header">
        <span>智能化作物生长建议</span>
        <span class="header-badges">
          <el-tag v-if="advice && advice.overallLevel" :type="levelTag(advice.overallLevel)" size="mini">{{ levelText(advice.overallLevel) }}</el-tag>
          <span v-if="advice && advice.healthScore != null" class="health-score">健康指数 {{ advice.healthScore }}/100</span>
        </span>
      </div>
      <el-alert v-if="advice && advice.smartSummary" type="info" :closable="false" show-icon class="mb16">
        <template slot="title"><span class="advice-smart-text">{{ advice.smartSummary }}</span></template>
      </el-alert>
      <div v-if="advice && advice.items && advice.items.length">
        <p v-if="advice.cropType" class="crop-hint">作物：<b>{{ advice.cropType }}</b>（近 {{ advice.windowHours }}h 数据 + 公网气象参考）</p>
        <el-timeline>
          <el-timeline-item v-for="(it, idx) in advice.items" :key="idx" :type="timelineType(it.severity)" :timestamp="it.category" placement="top">{{ it.suggestion }}</el-timeline-item>
        </el-timeline>
      </div>
      <el-empty v-else description="暂无建议：请选择节点并加载数据" />
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons')
import { listAgriNode } from '@/api/agri/node'
import { listUser } from '@/api/system/user'
import { pingNetwork, getOpenMeteoWeather } from '@/api/agri/network'
import { getLatestReading, getTrend, getAdvice, simulateReading } from '@/api/agri/reading'
import { listAgriAlarm, getUnhandledAlarmCount } from '@/api/agri/alarm'

export default {
  name: 'AgriMonitor',
  data() {
    return {
      ownerUser: undefined,
      userOptions: [],
      nodeId: undefined,
      nodeOptions: [],
      hours: 24,
      latest: null,
      advice: null,
      chart: null,
      weatherPublic: null,
      weatherLoading: false,
      nodeAlarms: [],
      unhandledTotal: 0,
      alarmCountInitialized: false,
      alarmPollTimer: null
    }
  },
  computed: {
    isAdmin() { return this.$store.getters.id === 1 }
  },
  mounted() {
    if (this.isAdmin) { this.loadUsers() } else { this.loadNodes() }
    this.loadWeather()
    this.startAlarmPoll()
    window.addEventListener('resize', this.resizeChart)
  },
  beforeDestroy() {
    this.stopAlarmPoll()
    window.removeEventListener('resize', this.resizeChart)
    if (this.chart) { this.chart.dispose(); this.chart = null }
  },
  methods: {
    fmt(v) { if (v === undefined || v === null) return '--'; return Number(v).toFixed(1) },
    fmtIntOrDec(v) { if (v === undefined || v === null) return '--'; const n = Number(v); return Number.isInteger(n) ? String(n) : n.toFixed(1) },
    levelTag(lv) { if (lv === 'risk') return 'danger'; if (lv === 'warning') return 'warning'; return 'success' },
    levelText(lv) { if (lv === 'risk') return '风险偏高'; if (lv === 'warning') return '需关注'; return '整体正常' },
    timelineType(sev) { if (sev === 'danger') return 'danger'; if (sev === 'warning') return 'warning'; return 'primary' },
    loadUsers() { listUser({ pageNum: 1, pageSize: 500, status: '0' }).then(res => { this.userOptions = res.rows || [] }) },
    onOwnerUserChange() {
      this.nodeId = undefined; this.nodeOptions = []; this.latest = null; this.advice = null; this.nodeAlarms = []; this.renderChart([])
      if (this.ownerUser) this.loadNodes()
    },
    loadNodes() {
      if (this.isAdmin && !this.ownerUser) { this.nodeOptions = []; return }
      const params = { pageNum: 1, pageSize: 500, status: '0' }
      if (this.isAdmin) params.createBy = this.ownerUser
      listAgriNode(params).then(res => {
        this.nodeOptions = res.rows || []
        if (!this.nodeId && this.nodeOptions.length) { this.nodeId = this.nodeOptions[0].nodeId; this.loadAll() }
      })
    },
    loadAll() {
      if (!this.nodeId) { this.latest = null; this.advice = null; this.renderChart([]); return }
      getLatestReading(this.nodeId).then(res => { this.latest = res.data || null })
      getTrend(this.nodeId, this.hours).then(res => { this.renderChart(res.data || []) })
      getAdvice(this.nodeId, this.hours).then(res => { this.advice = res.data || null })
      this.loadNodeAlarms()
    },
    loadNodeAlarms() {
      if (!this.nodeId) { this.nodeAlarms = []; return }
      listAgriAlarm({ pageNum: 1, pageSize: 20, nodeId: this.nodeId, status: '0' }).then(res => { this.nodeAlarms = res.rows || [] }).catch(() => { this.nodeAlarms = [] })
    },
    startAlarmPoll() { this.pollUnhandledAlarms(); this.alarmPollTimer = setInterval(() => this.pollUnhandledAlarms(), 60000) },
    stopAlarmPoll() { if (this.alarmPollTimer) { clearInterval(this.alarmPollTimer); this.alarmPollTimer = null } },
    pollUnhandledAlarms() {
      getUnhandledAlarmCount().then(res => {
        const count = res.data != null ? res.data : 0
        if (this.alarmCountInitialized && count > this.unhandledTotal) {
          this.$notify({ title: '传感阈值告警', message: '有 ' + count + ' 条未处理告警，请到「告警中心」查看', type: 'warning', duration: 8000 })
        }
        this.alarmCountInitialized = true; this.unhandledTotal = count
      }).catch(() => {})
    },
    goAlarmCenter() { this.$router.push({ path: '/agri/agriAlarm' }) },
    doSimulate() {
      if (!this.nodeId) return
      simulateReading(this.nodeId).then(() => {
        this.$modal.msgSuccess('已模拟上报一条读数')
        this.loadAll(); this.pollUnhandledAlarms()
      })
    },
    checkNetwork() {
      pingNetwork().then(res => {
        const ms = res.data && res.data.costMs != null ? res.data.costMs : '--'
        this.$modal.msgSuccess('出网正常，往返约 ' + ms + ' ms')
      }).catch(() => {})
    },
    loadWeather() {
      this.weatherLoading = true
      getOpenMeteoWeather(39.9042, 116.4074).then(res => {
        const d = res.data
        this.weatherPublic = (d && typeof d === 'object' && (d.temperatureC !== undefined || d.relativeHumidityPct !== undefined)) ? d : null
      }).catch(() => { this.weatherPublic = null }).finally(() => { this.weatherLoading = false })
    },
    resizeChart() { if (this.chart) this.chart.resize() },
    renderChart(rows) {
      const times = rows.map(r => this.parseTime(r.readingTime))
      const soil = rows.map(r => r.soilMoisturePct); const soilT = rows.map(r => r.soilTempC)
      const airT = rows.map(r => r.airTempC); const hum = rows.map(r => r.airHumidityPct)
      const lux = rows.map(r => r.lightLux); const soilPh = rows.map(r => r.soilPh)
      const co2 = rows.map(r => r.co2Ppm); const waterPh = rows.map(r => r.waterPh)
      this.$nextTick(() => {
        if (!this.$refs.chartRef) return
        if (!this.chart) this.chart = echarts.init(this.$refs.chartRef, 'macarons')
        this.chart.setOption({
          tooltip: { trigger: 'axis' },
          legend: { data: ['土壤湿度%', '土壤温度℃', '气温℃', '湿度%', '光照 lux', '土壤pH', 'CO2 ppm', '水体pH'] },
          grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
          xAxis: { type: 'category', boundaryGap: false, data: times },
          yAxis: [{ type: 'value', name: '% / ℃' }, { type: 'value', name: 'lux / ppm', splitLine: { show: false } }, { type: 'value', name: 'pH', splitLine: { show: false } }],
          series: [
            { name: '土壤湿度%', type: 'line', smooth: true, data: soil }, { name: '土壤温度℃', type: 'line', smooth: true, data: soilT },
            { name: '气温℃', type: 'line', smooth: true, data: airT }, { name: '湿度%', type: 'line', smooth: true, data: hum },
            { name: '光照 lux', type: 'line', smooth: true, yAxisIndex: 1, data: lux }, { name: 'CO2 ppm', type: 'line', smooth: true, yAxisIndex: 1, data: co2 },
            { name: '土壤pH', type: 'line', smooth: true, yAxisIndex: 2, data: soilPh }, { name: '水体pH', type: 'line', smooth: true, yAxisIndex: 2, data: waterPh }
          ]
        }, true)
      })
    },
    handleExport() { this.download('agri/reading/export', { nodeId: this.nodeId, hours: this.hours }, `传感器数据_${Date.now()}.xlsx`) }
  }
}
</script>

<style scoped>
/* ====== 控制栏 ====== */
.control-bar {
  display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 10px;
  background: #fff; border-radius: 8px; padding: 14px 18px; margin-bottom: 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,.06);
}
.control-filters { display: flex; align-items: center; gap: 6px; flex-wrap: wrap; }
.filter-label { font-size: 13px; color: #606266; white-space: nowrap; }
.filter-select { width: 160px; }
.filter-node { width: 200px; }
.filter-hours { width: 120px; }
.control-actions { display: flex; align-items: center; gap: 8px; flex-shrink: 0; }

/* ====== 数据卡片网格 ====== */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 16px;
}
.metric-item {
  display: flex; align-items: center; gap: 14px;
  background: #fff; border-radius: 10px; padding: 18px 16px;
  box-shadow: 0 1px 6px rgba(0,0,0,.05);
  transition: box-shadow .2s, transform .2s;
  cursor: default;
}
.metric-item:hover { box-shadow: 0 4px 16px rgba(0,0,0,.1); transform: translateY(-1px); }
.metric-icon {
  width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center;
  font-size: 22px; flex-shrink: 0;
}
.icon-moisture { background: #e0f2fe; color: #0284c7; }
.icon-temp    { background: #fef3c7; color: #d97706; }
.icon-air     { background: #dbeafe; color: #2563eb; }
.icon-humi    { background: #e0e7ff; color: #6366f1; }
.icon-light   { background: #fef9c3; color: #ca8a04; }
.icon-ph      { background: #dcfce7; color: #16a34a; }
.icon-co2     { background: #f3e8ff; color: #9333ea; }
.icon-water   { background: #cffafe; color: #0891b2; }
.metric-body { min-width: 0; }
.metric-label { font-size: 12px; color: #909399; margin-bottom: 4px; }
.metric-num { font-size: 26px; font-weight: 700; color: #1a1a2e; line-height: 1.2; }
.metric-num small { font-size: 13px; font-weight: 400; color: #909399; margin-left: 2px; }

/* ====== 公网天气 ====== */
.weather-inline { display: flex; align-items: center; gap: 24px; flex-wrap: wrap; }
.weather-stat { display: flex; flex-direction: column; align-items: center; gap: 2px; }
.weather-val { font-size: 20px; font-weight: 600; color: #303133; }
.weather-lbl { font-size: 11px; color: #909399; }
.weather-meta { font-size: 12px; color: #909399; margin-left: auto; }

/* ====== 通用卡片 ====== */
.section-card { margin-bottom: 16px; border-radius: 8px; }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.header-badges { display: flex; align-items: center; gap: 10px; }
.chart-container { height: 380px; width: 100%; }
.text-muted { color: #909399; font-size: 13px; margin: 0; }
.crop-hint { color: #606266; font-size: 13px; margin-bottom: 12px; }
.health-score { font-size: 13px; color: #606266; }
.advice-smart-text { font-size: 13px; line-height: 1.55; white-space: normal; }
.alarm-inline-list { margin: 0 0 6px; padding-left: 18px; color: #606266; font-size: 13px; line-height: 1.6; }
.mb16 { margin-bottom: 16px; }

/* ====== 适配小屏 ====== */
@media (max-width: 992px) {
  .metrics-grid { grid-template-columns: repeat(2, 1fr); }
  .filter-node, .filter-select { width: 140px; }
}
@media (max-width: 576px) {
  .metrics-grid { grid-template-columns: 1fr; }
  .control-bar { flex-direction: column; align-items: stretch; }
}
</style>
