<!--
  ============================================================
  远程互联 — 重构布局
  结构：统计卡片行 → 左右双栏（设备操作 / 网络互联）→ 指令表格
  ============================================================
-->
<template>
  <div class="remote-dashboard">

    <!-- ====== 顶部统计卡片 — 3列均分 ====== -->
    <div class="stats-row">
      <div class="stat-item" :class="networkOk ? 'stat--ok' : 'stat--warn'">
        <div class="stat-icon"><i :class="networkOk ? 'el-icon-success' : 'el-icon-warning'" /></div>
        <div class="stat-body">
          <div class="stat-value">{{ networkOk ? t.networkConnected : t.networkUnchecked }}</div>
          <div class="stat-sub" v-if="pingMs != null">{{ t.pingCost.replace('{ms}', pingMs) }}</div>
          <div class="stat-sub" v-else>{{ t.notYetPinged }}</div>
        </div>
      </div>
      <div class="stat-item stat--info">
        <div class="stat-icon"><i class="el-icon-cpu" /></div>
        <div class="stat-body">
          <div class="stat-value">{{ nodeOptions.length }}</div>
          <div class="stat-sub">{{ t.sensorNode }}</div>
        </div>
      </div>
      <div class="stat-item stat--primary">
        <div class="stat-icon"><i class="el-icon-s-order" /></div>
        <div class="stat-body">
          <div class="stat-value">{{ total }}</div>
          <div class="stat-sub">{{ t.commandRecord }}</div>
        </div>
      </div>
    </div>

    <!-- ====== 左右双栏 ====== -->
    <div class="dual-panels">
      <!-- 左栏：远程设备操作 -->
      <div class="panel panel-left">
        <div class="panel-head"><i class="el-icon-cpu" /> {{ t.remoteDeviceOps }}</div>
        <div class="panel-body">
          <!-- 节点选择 -->
          <div class="field-group">
            <label class="field-label" v-if="isAdmin">{{ t.ownerUser }}</label>
            <el-select v-if="isAdmin" v-model="ownerUser" :placeholder="t.selectUserFirst" filterable clearable size="small" class="field-input" @change="onOwnerUserChange">
              <el-option v-for="u in userOptions" :key="u.userName" :label="u.nickName + ' (' + u.userName + ')'" :value="u.userName" />
            </el-select>
            <label class="field-label">{{ t.sensorNode }}</label>
            <el-select v-model="operateNodeId" :placeholder="t.selectNode" filterable size="small" class="field-input">
              <el-option v-for="n in nodeOptions" :key="n.nodeId" :label="n.nodeName + ' (' + n.nodeCode + ')'" :value="n.nodeId" />
            </el-select>
          </div>
          <!-- 操作指令按钮组 — 按功能分组 -->
          <div class="cmd-section">
            <div class="section-label">设备指令</div>
            <div class="cmd-grid">
              <el-button v-for="(label, type) in t.commandTypes" :key="type" size="small" :type="cmdButtonType(type)" :icon="cmdIcon(type)" :disabled="!operateNodeId" v-hasPermi="['agri:remote:operate']" @click="handleSendCommand(type, label)">{{ label }}</el-button>
            </div>
          </div>
          <!-- 模拟上报 -->
          <div class="cmd-section">
            <div class="section-label">数据模拟</div>
            <el-button type="success" plain size="small" icon="el-icon-upload2" :disabled="!operateNodeId" v-hasPermi="['agri:remote:operate']" @click="handleSimulate">{{ t.simulateIngest }}</el-button>
          </div>
        </div>
      </div>

      <!-- 右栏：网络互联检测 -->
      <div class="panel panel-right" v-loading="statusLoading">
        <div class="panel-head">
          <i class="el-icon-connection" /> {{ t.networkPanel }}
          <el-button type="text" size="mini" class="panel-head-btn" @click="loadNetworkStatus">{{ t.refresh }}</el-button>
        </div>
        <div class="panel-body">
          <!-- 网络状态 -->
          <div class="info-block" v-if="networkStatus">
            <div class="info-row"><span class="info-label">{{ t.outboundSwitch }}</span><span class="info-val">{{ networkStatus.enabled ? t.enabled : t.disabled }}</span></div>
            <div class="info-row"><span class="info-label">{{ t.pingUrl }}</span><span class="info-val">{{ networkStatus.pingUrl || '--' }}</span></div>
            <div class="info-row"><span class="info-label">{{ t.allowedHosts }}</span><span class="info-val"><el-tag v-for="h in (networkStatus.allowedHosts || [])" :key="h" size="mini" class="host-tag">{{ h }}</el-tag><span v-if="!(networkStatus.allowedHosts || []).length">--</span></span></div>
          </div>
          <!-- 出网探测 -->
          <div class="action-row"><el-button type="primary" size="small" icon="el-icon-aim" v-hasPermi="['agri:remote:view']" @click="handlePing">{{ t.outboundPing }}</el-button></div>
          <!-- 公网天气 -->
          <div class="sub-section">
            <div class="sub-head">{{ t.publicWeather }}</div>
            <div class="weather-row">
              <div class="weather-field">
                <span class="field-label-sm">{{ t.latitude }}</span>
                <el-input-number v-model="weatherLat" :precision="4" :step="0.1" controls-position="right" size="small" class="w-input" @change="onWeatherLocationChange" />
                <span class="coord-unit">{{ latitudeUnit }}</span>
              </div>
              <div class="weather-field">
                <span class="field-label-sm">{{ t.longitude }}</span>
                <el-input-number v-model="weatherLon" :precision="4" :step="0.1" controls-position="right" size="small" class="w-input" @change="onWeatherLocationChange" />
                <span class="coord-unit">{{ longitudeUnit }}</span>
              </div>
              <el-button type="info" plain size="small" icon="el-icon-partly-cloudy" v-hasPermi="['agri:remote:view']" @click="handleWeather">{{ t.query }}</el-button>
            </div>
            <p v-if="weatherResult" class="weather-result">{{ weatherLine }}</p>
          </div>
          <!-- 白名单抓取 -->
          <div class="sub-section">
            <div class="sub-head">{{ t.whitelistFetch }}</div>
            <el-input v-model="fetchUrl" placeholder="https://api.open-meteo.com/..." size="small" @input="fetchUrlEdited = true">
              <el-button slot="append" icon="el-icon-download" v-hasPermi="['agri:remote:operate']" @click="handleFetch">{{ t.fetch }}</el-button>
            </el-input>
            <el-input v-if="fetchResult" type="textarea" :rows="3" class="fetch-result" readonly :value="fetchResult" />
          </div>
        </div>
      </div>
    </div>

    <!-- ====== 指令记录表格 ====== -->
    <div class="table-card">
      <div class="table-head">
        <span><i class="el-icon-s-order" /> {{ t.commandRecords }}</span>
        <el-button type="text" size="mini" @click="getList">{{ t.refresh }}</el-button>
      </div>
      <!-- 筛选栏 — 统一高度居中对齐 -->
      <div class="table-filters">
        <el-select v-model="queryParams.nodeId" :placeholder="t.allNodes" clearable filterable size="small" class="tf-select">
          <el-option v-for="n in nodeOptions" :key="n.nodeId" :label="n.nodeName + ' (' + n.nodeCode + ')'" :value="n.nodeId" />
        </el-select>
        <el-select v-model="queryParams.status" :placeholder="t.allStatus" clearable size="small" class="tf-select-sm">
          <el-option :label="t.statusPending" value="0" />
          <el-option :label="t.statusOk" value="1" />
          <el-option :label="t.statusFail" value="2" />
        </el-select>
        <el-button type="primary" icon="el-icon-search" size="small" @click="handleQuery">{{ t.search }}</el-button>
        <el-button type="warning" plain icon="el-icon-download" size="small" @click="handleExport" v-hasPermi="['agri:remote:view']">导出Excel</el-button>
      </div>
      <!-- 表格 -->
      <el-table v-loading="loading" :data="commandList">
        <el-table-column :label="t.colId" prop="commandId" width="80" align="center" />
        <el-table-column :label="t.colNode" prop="nodeName" min-width="150" show-overflow-tooltip />
        <el-table-column :label="t.colCmdType" width="120">
          <template slot-scope="scope"><el-tag size="small">{{ displayCommandLabel(scope.row) }}</el-tag></template>
        </el-table-column>
        <el-table-column :label="t.colCmdStatus" width="80" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === '1'" type="success" size="small">{{ t.statusOk }}</el-tag>
            <el-tag v-else-if="scope.row.status === '2'" type="danger" size="small">{{ t.statusFail }}</el-tag>
            <el-tag v-else type="info" size="small">{{ t.statusPending }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t.colResult" prop="resultMessage" min-width="220" show-overflow-tooltip />
        <el-table-column :label="t.colOperator" prop="createBy" width="100" />
      </el-table>
      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </div>
  </div>
</template>

<script>
import labelsZh from './labels-zh'
import { listAgriNode } from '@/api/agri/node'
import { listUser } from '@/api/system/user'
import { simulateReading } from '@/api/agri/reading'
import { getNetworkStatus, pingNetwork, fetchNetworkUrl, getOpenMeteoWeather } from '@/api/agri/network'
import { listRemoteCommand, sendRemoteCommand } from '@/api/agri/remote'
import { DEFAULT_WEATHER_LOCATION, buildOpenMeteoCurrentUrl, getWeatherLocation, latitudeDirection, longitudeDirection, saveWeatherLocation } from '@/utils/agriWeatherLocation'
const t = labelsZh
export default {
  name: 'AgriRemoteLink',
  data() {
    return {
      t,
      loading: false,
      statusLoading: false,
      total: 0,
      commandList: [],
      nodeOptions: [],
      userOptions: [],
      ownerUser: undefined,
      operateNodeId: undefined,
      queryParams: { pageNum: 1, pageSize: 10, nodeId: undefined, status: undefined },
      networkStatus: null,
      networkOk: false,
      pingMs: null,
      weatherLat: DEFAULT_WEATHER_LOCATION.latitude,
      weatherLon: DEFAULT_WEATHER_LOCATION.longitude,
      weatherResult: null,
      fetchUrl: '',
      fetchUrlEdited: false,
      fetchResult: ''
    }
  },
  computed: {
    isAdmin() { return this.$store.getters.roles.includes('admin') },
    latitudeUnit() { return latitudeDirection(this.weatherLat) },
    longitudeUnit() { return longitudeDirection(this.weatherLon) },
    weatherLine() {
      if (!this.weatherResult) return ''
      return t.weatherLine.replace('{t}', this.fmt(this.weatherResult.temperatureC)).replace('{h}', this.fmtIntOrDec(this.weatherResult.relativeHumidityPct)).replace('{time}', this.weatherResult.observationTime || '--')
    }
  },
  mounted() {
    this.initWeatherLocation()
    if (this.isAdmin) { this.loadUsers(); this.loadNodes() } else { this.loadNodes() }
    this.loadNetworkStatus(); this.getList()
  },
  methods: {
    fmt(v) { if (v === undefined || v === null) return '--'; return Number(v).toFixed(1) },
    fmtIntOrDec(v) { if (v === undefined || v === null) return '--'; const n = Number(v); return Number.isInteger(n) ? n : n.toFixed(1) },
    cmdButtonType(type) { if (type === 'REBOOT') return 'danger'; if (type === 'READ_SENSOR') return 'primary'; if (type.endsWith('_ON')) return 'success'; return '' },
    cmdIcon(type) { if (type.startsWith('IRRIGATE')) return 'el-icon-heavy-rain'; if (type.startsWith('FAN')) return 'el-icon-wind-power'; if (type === 'READ_SENSOR') return 'el-icon-odometer'; return 'el-icon-set-up' },
    loadUsers() { listUser({ pageNum: 1, pageSize: 500, status: '0' }).then(res => { this.userOptions = res.rows || [] }).catch(() => {}) },
    onOwnerUserChange() {
      this.operateNodeId = undefined
      this.queryParams.nodeId = undefined
      this.queryParams.pageNum = 1
      this.loadNodes()
      this.getList()
    },
    loadNodes() {
      const params = { pageNum: 1, pageSize: 500, status: '0' }
      if (this.isAdmin && this.ownerUser) params.createBy = this.ownerUser
      listAgriNode(params).then(res => {
        this.nodeOptions = res.rows || []
        if (!this.operateNodeId && this.nodeOptions.length) this.operateNodeId = this.nodeOptions[0].nodeId
      }).catch(() => {})
    },
    displayCommandLabel(row) { if (row && row.commandType && t.commandTypes[row.commandType]) return t.commandTypes[row.commandType]; let label = (row && row.commandLabel) ? row.commandLabel : ''; return label.replace(/灌[浃浇]/g, '灌溉') || '--' },
    loadNetworkStatus() { this.statusLoading = true; getNetworkStatus().then(res => { this.networkStatus = res.data || null }).finally(() => { this.statusLoading = false }) },
    initWeatherLocation() {
      const location = getWeatherLocation()
      this.weatherLat = location.latitude
      this.weatherLon = location.longitude
      this.fetchUrl = buildOpenMeteoCurrentUrl(this.weatherLat, this.weatherLon)
      this.fetchUrlEdited = false
    },
    onWeatherLocationChange() {
      const location = saveWeatherLocation(this.weatherLat, this.weatherLon)
      if (!location) return
      this.weatherLat = location.latitude
      this.weatherLon = location.longitude
      if (!this.fetchUrlEdited) {
        this.fetchUrl = buildOpenMeteoCurrentUrl(location.latitude, location.longitude)
      }
    },
    getList() { this.loading = true; listRemoteCommand(this.queryParams).then(res => { this.commandList = res.rows || []; this.total = res.total || 0 }).finally(() => { this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.queryParams.nodeId = undefined; this.queryParams.status = undefined; this.handleQuery() },
    handleSendCommand(commandType, label) {
      if (!this.operateNodeId) return
      this.$modal.confirm(t.confirmSend.replace('{label}', label)).then(() => sendRemoteCommand({ nodeId: this.operateNodeId, commandType })).then(() => { this.$modal.msgSuccess(t.cmdSent); this.getList() }).catch(() => {})
    },
    handleSimulate() { if (!this.operateNodeId) return; simulateReading(this.operateNodeId).then(() => this.$modal.msgSuccess(t.simulateOk)).catch(() => {}) },
    handlePing() { pingNetwork().then(res => { const ms = res.data && res.data.costMs != null ? res.data.costMs : null; this.pingMs = ms; this.networkOk = true; this.$modal.msgSuccess(t.pingOk.replace('{ms}', ms)) }).catch(() => { this.networkOk = false }) },
    handleWeather() { this.onWeatherLocationChange(); getOpenMeteoWeather(this.weatherLat, this.weatherLon).then(res => { this.weatherResult = res.data || null; if (!this.weatherResult) this.$modal.msgWarning(t.weatherFail) }).catch(() => {}) },
    handleFetch() { if (!this.fetchUrl) { this.$modal.msgWarning(t.urlRequired); return }; fetchNetworkUrl(this.fetchUrl).then(res => { const body = res.data; this.fetchResult = typeof body === 'string' ? body : JSON.stringify(body, null, 2) }).catch(() => {}) },
    handleExport() { this.download('agri/remote/command/export', { ...this.queryParams }, `远程指令_${Date.now()}.xlsx`) }
  }
}
</script>

<style scoped>
/* ====== 全局容器 ====== */
.remote-dashboard { display: flex; flex-direction: column; gap: 16px; }

/* ====== 统计卡片 ====== */
.stats-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.stat-item { display: flex; align-items: center; gap: 14px; background: #fff; border-radius: 10px; padding: 18px 20px; box-shadow: 0 1px 6px rgba(0,0,0,.05); cursor: default; }
.stat-icon { font-size: 30px; flex-shrink: 0; }
.stat--ok .stat-icon { color: #67c23a; } .stat--warn .stat-icon { color: #e6a23c; }
.stat--info .stat-icon { color: #909399; } .stat--primary .stat-icon { color: #409eff; }
.stat-value { font-size: 20px; font-weight: 700; color: #1a1a2e; line-height: 1.3; }
.stat-sub { font-size: 12px; color: #909399; margin-top: 2px; }

/* ====== 左右双栏 ====== */
.dual-panels { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.panel { background: #fff; border-radius: 10px; box-shadow: 0 1px 6px rgba(0,0,0,.05); display: flex; flex-direction: column; }
.panel-head { font-size: 15px; font-weight: 600; color: #303133; padding: 16px 20px; border-bottom: 1px solid #f0f0f0; display: flex; align-items: center; gap: 8px; }
.panel-head-btn { margin-left: auto; }
.panel-body { padding: 16px 20px; flex: 1; display: flex; flex-direction: column; gap: 16px; }

/* ====== 左栏：字段 + 指令分组 ====== */
.field-group { display: flex; flex-direction: column; gap: 10px; }
.field-label { font-size: 13px; color: #606266; }
.field-input { width: 100%; }
.cmd-section { display: flex; flex-direction: column; gap: 8px; }
.section-label { font-size: 12px; color: #909399; font-weight: 500; }
.cmd-grid { display: flex; flex-wrap: wrap; gap: 8px; }

/* ====== 右栏：网络 ====== */
.info-block { display: flex; flex-direction: column; gap: 8px; background: #fafbfc; border-radius: 8px; padding: 12px 14px; }
.info-row { display: flex; align-items: flex-start; gap: 12px; font-size: 13px; }
.info-label { color: #909399; flex-shrink: 0; white-space: nowrap; }
.info-val { color: #303133; word-break: break-all; }
.host-tag { margin-right: 4px; margin-bottom: 4px; }
.action-row { padding-top: 4px; }
.sub-section { display: flex; flex-direction: column; gap: 8px; }
.sub-head { font-size: 13px; font-weight: 500; color: #606266; }
.weather-row { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.weather-field { display: flex; align-items: center; gap: 6px; }
.field-label-sm { font-size: 12px; color: #909399; white-space: nowrap; }
.coord-unit { min-width: 28px; font-size: 12px; color: #606266; white-space: nowrap; }
.w-input { width: 140px; }
.weather-result { font-size: 13px; color: #606266; margin: 0; }
.fetch-result { margin-top: 6px; }

/* ====== 表格卡片 ====== */
.table-card { background: #fff; border-radius: 10px; box-shadow: 0 1px 6px rgba(0,0,0,.05); }
.table-head { font-size: 15px; font-weight: 600; color: #303133; padding: 16px 20px; border-bottom: 1px solid #f0f0f0; display: flex; align-items: center; gap: 8px; }
.table-head .el-button { margin-left: auto; }
.table-filters { display: flex; align-items: center; gap: 10px; padding: 14px 20px; flex-wrap: wrap; }
.tf-select { width: 180px; }
.tf-select-sm { width: 110px; }

/* ====== 响应式 ====== */
@media (max-width: 992px) {
  .dual-panels, .stats-row { grid-template-columns: 1fr; }
}
</style>
