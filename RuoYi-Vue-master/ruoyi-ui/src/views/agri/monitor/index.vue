<template>
  <div class="app-container">
    <el-row :gutter="12" class="mb8" type="flex" align="middle">
      <el-col v-if="isAdmin" :xs="24" :sm="10" :md="8">
        <span class="label-inline">所属用户</span>
        <el-select
          v-model="ownerUser"
          placeholder="请先选择用户"
          filterable
          clearable
          style="width: 220px"
          @change="onOwnerUserChange"
        >
          <el-option
            v-for="u in userOptions"
            :key="u.userName"
            :label="u.nickName + ' (' + u.userName + ')'"
            :value="u.userName"
          />
        </el-select>
      </el-col>
      <el-col :xs="24" :sm="10" :md="8">
        <span class="label-inline">传感节点</span>
        <el-select
          v-model="nodeId"
          placeholder="请选择节点"
          filterable
          style="width: 220px"
          :disabled="isAdmin && !ownerUser"
          @change="loadAll"
        >
          <el-option v-for="n in nodeOptions" :key="n.nodeId" :label="n.nodeName + ' (' + n.nodeCode + ')'" :value="n.nodeId" />
        </el-select>
      </el-col>
      <el-col :xs="24" :sm="8" :md="6">
        <span class="label-inline">时间窗口</span>
        <el-select v-model="hours" style="width: 120px" @change="loadAll">
          <el-option :value="6" label="近 6 小时" />
          <el-option :value="12" label="近 12 小时" />
          <el-option :value="24" label="近 24 小时" />
          <el-option :value="48" label="近 48 小时" />
        </el-select>
      </el-col>
      <el-col :xs="24" :sm="6" :md="10">
        <el-button type="primary" icon="el-icon-refresh" size="small" @click="loadAll" v-hasPermi="['agri:monitor:view']">刷新数据</el-button>
        <el-button type="warning" plain icon="el-icon-download" size="small" @click="handleExport" v-hasPermi="['agri:monitor:view']">导出Excel</el-button>
        <el-button type="success" icon="el-icon-upload2" size="small" @click="doSimulate" v-hasPermi="['agri:monitor:ingest']" :disabled="!nodeId">模拟上报</el-button>
        <el-button type="info" icon="el-icon-connection" size="small" @click="checkNetwork" v-hasPermi="['agri:monitor:view']">出网检测</el-button>
      </el-col>
    </el-row>

    <el-alert
      v-if="isAdmin && !ownerUser"
      title="请先选择需要查看的用户，再选择其传感节点。"
      type="info"
      :closable="false"
      class="mb8"
    />

    <el-row :gutter="12" v-if="latest">
      <el-col :xs="12" :sm="6" :md="4">
        <el-card shadow="hover" class="metric-card">
          <div class="metric-title">土壤湿度</div>
          <div class="metric-value">{{ fmt(latest.soilMoisturePct) }}<small>%</small></div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="4">
        <el-card shadow="hover" class="metric-card">
          <div class="metric-title">土壤温度</div>
          <div class="metric-value">{{ fmt(latest.soilTempC) }}<small>℃</small></div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="4">
        <el-card shadow="hover" class="metric-card">
          <div class="metric-title">空气温度</div>
          <div class="metric-value">{{ fmt(latest.airTempC) }}<small>℃</small></div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="4">
        <el-card shadow="hover" class="metric-card">
          <div class="metric-title">空气湿度</div>
          <div class="metric-value">{{ fmt(latest.airHumidityPct) }}<small>%</small></div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="4">
        <el-card shadow="hover" class="metric-card">
          <div class="metric-title">光照强度</div>
          <div class="metric-value">{{ latest.lightLux != null ? latest.lightLux : '--' }}<small> lux</small></div>
        </el-card>
      </el-col>
    </el-row>
    <el-alert
      v-else
      title="暂无最新读数：请选择节点后刷新，或使用「模拟上报」生成演示数据。"
      type="info"
      :closable="false"
      class="mb8"
    />

    <el-row :gutter="12" class="mb8" v-if="nodeAlarms.length" v-hasPermi="['agri:monitor:view']">
      <el-col :span="24">
        <el-alert
          title="当前节点存在未处理阈值告警"
          type="error"
          show-icon
          :closable="false"
        >
          <template slot="default">
            <ul class="alarm-inline-list">
              <li v-for="a in nodeAlarms" :key="a.alarmId">
                <el-tag :type="a.alarmLevel === '1' ? 'danger' : 'warning'" size="mini">{{ a.metricName }}</el-tag>
                {{ a.alarmMessage }}
              </li>
            </ul>
            <el-button type="text" size="mini" @click="goAlarmCenter">前往告警中心</el-button>
          </template>
        </el-alert>
      </el-col>
    </el-row>

    <el-row :gutter="12" class="mb8" v-hasPermi="['agri:monitor:view']">
      <el-col :span="24">
        <el-card shadow="never" class="public-weather-card" v-loading="weatherLoading">
          <div slot="header" class="clearfix">
            <span>公网实时天气（Open-Meteo）</span>
            <el-button type="text" size="mini" style="float: right; padding: 3px 0" @click="loadWeather">刷新</el-button>
          </div>
          <el-row :gutter="12" v-if="weatherPublic">
            <el-col :xs="12" :sm="6" :md="4">
              <div class="metric-card metric-card--inline">
                <div class="metric-title">公网气温</div>
                <div class="metric-value">{{ fmt(weatherPublic.temperatureC) }}<small>℃</small></div>
              </div>
            </el-col>
            <el-col :xs="12" :sm="6" :md="4">
              <div class="metric-card metric-card--inline">
                <div class="metric-title">公网相对湿度</div>
                <div class="metric-value">{{ fmtIntOrDec(weatherPublic.relativeHumidityPct) }}<small>%</small></div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="16">
              <p class="weather-meta">
                观测时间 {{ weatherPublic.observationTime || '--' }} · 坐标 {{ weatherPublic.latitude }}, {{ weatherPublic.longitude }}
                · 来源 {{ weatherPublic.source }}
              </p>
            </el-col>
          </el-row>
          <p v-else-if="!weatherLoading" class="weather-unavailable">未能从公网获取气温与湿度，请确认服务器可访问 api.open-meteo.com 且白名单已配置。</p>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12">
      <el-col :span="24">
        <el-card shadow="never">
          <div slot="header" class="clearfix">
            <span>环境趋势（{{ hours }} 小时）</span>
          </div>
          <div ref="chartRef" style="height: 380px; width: 100%;" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12" class="mt12">
      <el-col :span="24">
        <el-card shadow="never">
          <div slot="header" class="clearfix">
            <span>智能化作物生长建议</span>
            <el-tag v-if="advice && advice.overallLevel" :type="levelTag(advice.overallLevel)" size="mini" style="margin-left: 10px">
              {{ levelText(advice.overallLevel) }}
            </el-tag>
            <span v-if="advice && advice.healthScore != null" class="health-score">健康指数 {{ advice.healthScore }} / 100</span>
          </div>
          <el-alert
            v-if="advice && advice.smartSummary"
            type="info"
            :closable="false"
            show-icon
            class="mb8 advice-smart-alert"
          >
            <template slot="title">
              <span class="advice-smart-text">{{ advice.smartSummary }}</span>
            </template>
          </el-alert>
          <div v-if="advice && advice.items && advice.items.length">
            <p v-if="advice.cropType" class="crop-hint">当前作物：<b>{{ advice.cropType }}</b>（近 {{ advice.windowHours }} 小时传感数据 + 作物规则 + 趋势；默认融合公网参考气象）</p>
            <el-timeline>
              <el-timeline-item v-for="(it, idx) in advice.items" :key="idx" :type="timelineType(it.severity)" :timestamp="it.category" placement="top">
                {{ it.suggestion }}
              </el-timeline-item>
            </el-timeline>
          </div>
          <el-empty v-else description="暂无建议：请先选择节点并加载数据" />
        </el-card>
      </el-col>
    </el-row>
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
    isAdmin() {
      return this.$store.getters.id === 1
    }
  },
  mounted() {
    if (this.isAdmin) {
      this.loadUsers()
    } else {
      this.loadNodes()
    }
    this.loadWeather()
    this.startAlarmPoll()
    window.addEventListener('resize', this.resizeChart)
  },
  beforeDestroy() {
    this.stopAlarmPoll()
    window.removeEventListener('resize', this.resizeChart)
    if (this.chart) {
      this.chart.dispose()
      this.chart = null
    }
  },
  methods: {
    fmt(v) {
      if (v === undefined || v === null) return '--'
      return Number(v).toFixed(2)
    },
    fmtIntOrDec(v) {
      if (v === undefined || v === null) return '--'
      const n = Number(v)
      return Number.isInteger(n) ? String(n) : n.toFixed(1)
    },
    levelTag(lv) {
      if (lv === 'risk') return 'danger'
      if (lv === 'warning') return 'warning'
      return 'success'
    },
    levelText(lv) {
      if (lv === 'risk') return '风险偏高'
      if (lv === 'warning') return '需关注'
      return '整体正常'
    },
    timelineType(sev) {
      if (sev === 'danger') return 'danger'
      if (sev === 'warning') return 'warning'
      return 'primary'
    },
    loadUsers() {
      listUser({ pageNum: 1, pageSize: 500, status: '0' }).then(res => {
        this.userOptions = res.rows || []
      })
    },
    onOwnerUserChange() {
      this.nodeId = undefined
      this.nodeOptions = []
      this.latest = null
      this.advice = null
      this.nodeAlarms = []
      this.renderChart([])
      if (this.ownerUser) {
        this.loadNodes()
      }
    },
    loadNodes() {
      if (this.isAdmin && !this.ownerUser) {
        this.nodeOptions = []
        return
      }
      const params = { pageNum: 1, pageSize: 500, status: '0' }
      if (this.isAdmin) {
        params.createBy = this.ownerUser
      }
      listAgriNode(params).then(res => {
        this.nodeOptions = res.rows || []
        if (!this.nodeId && this.nodeOptions.length) {
          this.nodeId = this.nodeOptions[0].nodeId
          this.loadAll()
        }
      })
    },
    loadAll() {
      if (!this.nodeId) {
        this.latest = null
        this.advice = null
        this.renderChart([])
        return
      }
      getLatestReading(this.nodeId).then(res => {
        this.latest = res.data || null
      })
      getTrend(this.nodeId, this.hours).then(res => {
        this.renderChart(res.data || [])
      })
      getAdvice(this.nodeId, this.hours).then(res => {
        this.advice = res.data || null
      })
      this.loadNodeAlarms()
    },
    loadNodeAlarms() {
      if (!this.nodeId) {
        this.nodeAlarms = []
        return
      }
      listAgriAlarm({ pageNum: 1, pageSize: 20, nodeId: this.nodeId, status: '0' }).then(res => {
        this.nodeAlarms = res.rows || []
      }).catch(() => {
        this.nodeAlarms = []
      })
    },
    startAlarmPoll() {
      this.pollUnhandledAlarms()
      this.alarmPollTimer = setInterval(() => this.pollUnhandledAlarms(), 60000)
    },
    stopAlarmPoll() {
      if (this.alarmPollTimer) {
        clearInterval(this.alarmPollTimer)
        this.alarmPollTimer = null
      }
    },
    pollUnhandledAlarms() {
      getUnhandledAlarmCount().then(res => {
        const count = res.data != null ? res.data : 0
        if (this.alarmCountInitialized && count > this.unhandledTotal) {
          this.$notify({
            title: '传感阈值告警',
            message: '有 ' + count + ' 条未处理告警，请到「告警中心」查看。',
            type: 'warning',
            duration: 8000
          })
        }
        this.alarmCountInitialized = true
        this.unhandledTotal = count
      }).catch(() => {})
    },
    goAlarmCenter() {
      this.$router.push({ path: '/agri/agriAlarm' })
    },
    doSimulate() {
      if (!this.nodeId) return
      simulateReading(this.nodeId).then(() => {
        this.$modal.msgSuccess('已模拟上报一条读数，系统已自动检测阈值')
        this.loadAll()
        this.pollUnhandledAlarms()
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
        if (d && typeof d === 'object' && (d.temperatureC !== undefined || d.relativeHumidityPct !== undefined)) {
          this.weatherPublic = d
        } else {
          this.weatherPublic = null
        }
      }).catch(() => {
        this.weatherPublic = null
      }).finally(() => {
        this.weatherLoading = false
      })
    },
    resizeChart() {
      if (this.chart) this.chart.resize()
    },
    renderChart(rows) {
      const times = rows.map(r => this.parseTime(r.readingTime))
      const soil = rows.map(r => r.soilMoisturePct)
      const soilT = rows.map(r => r.soilTempC)
      const airT = rows.map(r => r.airTempC)
      const hum = rows.map(r => r.airHumidityPct)
      const lux = rows.map(r => r.lightLux)
      this.$nextTick(() => {
        if (!this.$refs.chartRef) return
        if (!this.chart) {
          this.chart = echarts.init(this.$refs.chartRef, 'macarons')
        }
        this.chart.setOption({
          tooltip: { trigger: 'axis' },
          legend: { data: ['土壤湿度%', '土壤温度℃', '气温℃', '湿度%', '光照 lux'] },
          grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
          xAxis: { type: 'category', boundaryGap: false, data: times },
          yAxis: [
            { type: 'value', name: '% / ℃' },
            { type: 'value', name: 'lux', splitLine: { show: false } }
          ],
          series: [
            { name: '土壤湿度%', type: 'line', smooth: true, data: soil },
            { name: '土壤温度℃', type: 'line', smooth: true, data: soilT },
            { name: '气温℃', type: 'line', smooth: true, data: airT },
            { name: '湿度%', type: 'line', smooth: true, data: hum },
            { name: '光照 lux', type: 'line', smooth: true, yAxisIndex: 1, data: lux }
          ]
        }, true)
      })
    },
    handleExport() {
      this.download('agri/reading/export', { nodeId: this.nodeId, hours: this.hours }, `传感器数据_${Date.now()}.xlsx`)
    }
  }
}
</script>

<style scoped>
.label-inline {
  margin-right: 8px;
  color: #606266;
  font-size: 14px;
}
.metric-card {
  text-align: center;
  margin-bottom: 12px;
}
.metric-title {
  font-size: 13px;
  color: #909399;
}
.metric-value {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin-top: 6px;
}
.metric-value small {
  font-size: 13px;
  font-weight: normal;
  color: #909399;
}
.mt12 {
  margin-top: 12px;
}
.public-weather-card {
  min-height: 88px;
}
.metric-card--inline {
  margin-bottom: 0;
}
.weather-meta {
  margin: 8px 0 0;
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
}
.weather-unavailable {
  margin: 0;
  color: #909399;
  font-size: 13px;
}
.crop-hint {
  color: #606266;
  margin-bottom: 12px;
}
.mb8 {
  margin-bottom: 12px;
}
.health-score {
  float: right;
  margin-right: 12px;
  font-size: 13px;
  color: #606266;
}
.advice-smart-text {
  font-size: 13px;
  line-height: 1.55;
  white-space: normal;
}
.alarm-inline-list {
  margin: 0 0 8px;
  padding-left: 18px;
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
}
</style>
