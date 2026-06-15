<template>
  <div class="app-container agri-remote-page">
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card" :class="networkOk ? 'stat-card--ok' : 'stat-card--warn'">
          <div class="stat-card__inner">
            <i :class="networkOk ? 'el-icon-success' : 'el-icon-warning'" class="stat-card__icon" />
            <div>
              <div class="stat-card__value">{{ networkOk ? t.networkConnected : t.networkUnchecked }}</div>
              <div class="stat-card__label">{{ t.serverOutbound }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card stat-card--info">
          <div class="stat-card__inner">
            <i class="el-icon-time stat-card__icon" />
            <div>
              <div class="stat-card__value">{{ pingMs != null ? pingMs + ' ms' : '--' }}</div>
              <div class="stat-card__label">{{ t.pingLatency }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card stat-card--primary">
          <div class="stat-card__inner">
            <i class="el-icon-s-operation stat-card__icon" />
            <div>
              <div class="stat-card__value">{{ total }}</div>
              <div class="stat-card__label">{{ t.remoteLogCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="panel-card">
          <div slot="header" class="panel-header">
            <span><i class="el-icon-cpu"></i> {{ t.remoteDeviceOps }}</span>
          </div>
          <el-form size="small" label-width="88px">
            <el-form-item v-if="isAdmin" :label="t.ownerUser">
              <el-select v-model="ownerUser" :placeholder="t.selectUserFirst" filterable clearable style="width: 100%" @change="onOwnerUserChange">
                <el-option v-for="u in userOptions" :key="u.userName" :label="u.nickName + ' (' + u.userName + ')'" :value="u.userName" />
              </el-select>
            </el-form-item>
            <el-form-item :label="t.sensorNode">
              <el-select
                v-model="operateNodeId"
                :placeholder="t.selectNode"
                filterable
                style="width: 100%"
                :disabled="isAdmin && !ownerUser"
              >
                <el-option
                  v-for="n in nodeOptions"
                  :key="n.nodeId"
                  :label="n.nodeName + ' (' + n.nodeCode + ')'"
                  :value="n.nodeId"
                />
              </el-select>
            </el-form-item>
          </el-form>
          <div class="cmd-grid">
            <el-button
              v-for="(label, type) in t.commandTypes"
              :key="type"
              size="small"
              :type="cmdButtonType(type)"
              :icon="cmdIcon(type)"
              :disabled="!operateNodeId"
              v-hasPermi="['agri:remote:operate']"
              @click="handleSendCommand(type, label)"
            >{{ label }}</el-button>
          </div>
          <el-button
            type="success"
            plain
            size="small"
            icon="el-icon-upload2"
            class="mt8"
            :disabled="!operateNodeId"
            v-hasPermi="['agri:remote:operate']"
            @click="handleSimulate"
          >{{ t.simulateIngest }}</el-button>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="panel-card" v-loading="statusLoading">
          <div slot="header" class="panel-header">
            <span><i class="el-icon-connection"></i> {{ t.networkPanel }}</span>
            <el-button type="text" size="mini" style="float: right; padding: 3px 0" @click="loadNetworkStatus">{{ t.refresh }}</el-button>
          </div>
          <el-descriptions v-if="networkStatus" :column="1" size="small" border>
            <el-descriptions-item :label="t.outboundSwitch">{{ networkStatus.enabled ? t.enabled : t.disabled }}</el-descriptions-item>
            <el-descriptions-item :label="t.pingUrl">{{ networkStatus.pingUrl || '--' }}</el-descriptions-item>
            <el-descriptions-item :label="t.allowedHosts">
              <el-tag v-for="h in (networkStatus.allowedHosts || [])" :key="h" size="mini" class="host-tag">{{ h }}</el-tag>
              <span v-if="!(networkStatus.allowedHosts || []).length">--</span>
            </el-descriptions-item>
          </el-descriptions>
          <div class="network-actions mt8">
            <el-button type="primary" size="small" icon="el-icon-aim" v-hasPermi="['agri:remote:view']" @click="handlePing">{{ t.outboundPing }}</el-button>
          </div>
          <el-divider content-position="left">{{ t.publicWeather }}</el-divider>
          <el-row :gutter="8" type="flex" align="middle">
            <el-col :span="8">
              <el-input-number v-model="weatherLat" :precision="4" :step="0.1" controls-position="right" style="width: 100%" />
              <div class="field-hint">{{ t.latitude }}</div>
            </el-col>
            <el-col :span="8">
              <el-input-number v-model="weatherLon" :precision="4" :step="0.1" controls-position="right" style="width: 100%" />
              <div class="field-hint">{{ t.longitude }}</div>
            </el-col>
            <el-col :span="8">
              <el-button type="info" plain size="small" icon="el-icon-partly-cloudy" v-hasPermi="['agri:remote:view']" @click="handleWeather">{{ t.query }}</el-button>
            </el-col>
          </el-row>
          <p v-if="weatherResult" class="weather-result">{{ weatherLine }}</p>
          <el-divider content-position="left">{{ t.whitelistFetch }}</el-divider>
          <el-input v-model="fetchUrl" placeholder="https://api.open-meteo.com/..." size="small">
            <el-button slot="append" icon="el-icon-download" v-hasPermi="['agri:remote:operate']" @click="handleFetch">{{ t.fetch }}</el-button>
          </el-input>
          <el-input v-if="fetchResult" type="textarea" :rows="4" class="mt8" readonly :value="fetchResult" />
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="mt12">
      <div slot="header" class="panel-header">
        <span><i class="el-icon-document"></i> {{ t.remoteLog }}</span>
        <el-button type="text" size="mini" style="float: right; padding: 3px 0" @click="getList">{{ t.refresh }}</el-button>
      </div>
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="80px">
        <el-form-item :label="t.node" prop="nodeId">
          <el-select v-model="queryParams.nodeId" :placeholder="t.allNodes" clearable filterable style="width: 200px">
            <el-option v-for="n in nodeOptions" :key="n.nodeId" :label="n.nodeName" :value="n.nodeId" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t.status" prop="status">
          <el-select v-model="queryParams.status" :placeholder="t.all" clearable style="width: 120px">
            <el-option :label="t.success" value="1" />
            <el-option :label="t.fail" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ t.search }}</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ t.reset }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="commandList" border>
        <el-table-column :label="t.colTime" prop="createTime" width="160" />
        <el-table-column :label="t.colNode" prop="nodeName" min-width="120" show-overflow-tooltip />
        <el-table-column :label="t.colCommand" width="120">
          <template slot-scope="scope">
            {{ displayCommandLabel(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column :label="t.colStatus" prop="status" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'" size="mini">
              {{ scope.row.status === '1' ? t.success : t.fail }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t.colResult" prop="resultMessage" min-width="220" show-overflow-tooltip />
        <el-table-column :label="t.colOperator" prop="createBy" width="100" />
      </el-table>
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>
  </div>
</template>

<script>
import labelsZh from './labels-zh'
import { listAgriNode } from '@/api/agri/node'
import { listUser } from '@/api/system/user'
import { simulateReading } from '@/api/agri/reading'
import { getNetworkStatus, pingNetwork, fetchNetworkUrl, getOpenMeteoWeather } from '@/api/agri/network'
import { listRemoteCommand, sendRemoteCommand } from '@/api/agri/remote'

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
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        nodeId: undefined,
        status: undefined
      },
      networkStatus: null,
      networkOk: false,
      pingMs: null,
      weatherLat: 39.9042,
      weatherLon: 116.4074,
      weatherResult: null,
      fetchUrl: 'https://api.open-meteo.com/v1/forecast?latitude=39.9&longitude=116.4&current=temperature_2m',
      fetchResult: ''
    }
  },
  computed: {
    isAdmin() {
      return this.$store.getters.id === 1
    },
    weatherLine() {
      if (!this.weatherResult) return ''
      return t.weatherLine
        .replace('{t}', this.fmt(this.weatherResult.temperatureC))
        .replace('{h}', this.fmtIntOrDec(this.weatherResult.relativeHumidityPct))
        .replace('{time}', this.weatherResult.observationTime || '--')
    }
  },
  mounted() {
    if (this.isAdmin) {
      this.loadUsers()
    } else {
      this.loadNodes()
    }
    this.loadNetworkStatus()
    this.getList()
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
    cmdButtonType(type) {
      if (type === 'REBOOT') return 'danger'
      if (type === 'READ_SENSOR') return 'primary'
      if (type.endsWith('_ON')) return 'success'
      return 'warning'
    },
    cmdIcon(type) {
      if (type.startsWith('IRRIGATE')) return 'el-icon-heavy-rain'
      if (type.startsWith('FAN')) return 'el-icon-wind-power'
      if (type === 'READ_SENSOR') return 'el-icon-odometer'
      return 'el-icon-refresh-right'
    },
    loadUsers() {
      listUser({ pageNum: 1, pageSize: 500, status: '0' }).then(res => {
        this.userOptions = res.rows || []
      })
    },
    onOwnerUserChange() {
      this.operateNodeId = undefined
      this.loadNodes()
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
        if (!this.operateNodeId && this.nodeOptions.length) {
          this.operateNodeId = this.nodeOptions[0].nodeId
        }
      })
    },
    displayCommandLabel(row) {
      if (row && row.commandType && t.commandTypes[row.commandType]) {
        return t.commandTypes[row.commandType]
      }
      let label = (row && row.commandLabel) ? row.commandLabel : ''
      return label.replace(/\u704c\u6d43/g, '\u704c\u6e89').replace(/\u704c\u6d4d/g, '\u704c\u6e89')
    },
    loadNetworkStatus() {
      this.statusLoading = true
      getNetworkStatus().then(res => {
        this.networkStatus = res.data || null
      }).finally(() => {
        this.statusLoading = false
      })
    },
    getList() {
      this.loading = true
      listRemoteCommand(this.queryParams).then(res => {
        this.commandList = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams.nodeId = undefined
      this.queryParams.status = undefined
      this.handleQuery()
    },
    handleSendCommand(commandType, label) {
      if (!this.operateNodeId) return
      this.$modal.confirm(t.confirmSend.replace('{label}', label)).then(() => {
        return sendRemoteCommand({ nodeId: this.operateNodeId, commandType })
      }).then(() => {
        this.$modal.msgSuccess(t.cmdSent)
        this.getList()
      }).catch(() => {})
    },
    handleSimulate() {
      if (!this.operateNodeId) return
      simulateReading(this.operateNodeId).then(() => {
        this.$modal.msgSuccess(t.simulateOk)
      })
    },
    handlePing() {
      pingNetwork().then(res => {
        const ms = res.data && res.data.costMs != null ? res.data.costMs : null
        this.pingMs = ms
        this.networkOk = true
        this.$modal.msgSuccess(t.pingOk.replace('{ms}', ms))
      }).catch(() => {
        this.networkOk = false
      })
    },
    handleWeather() {
      getOpenMeteoWeather(this.weatherLat, this.weatherLon).then(res => {
        this.weatherResult = res.data || null
        if (!this.weatherResult) {
          this.$modal.msgWarning(t.weatherFail)
        }
      })
    },
    handleFetch() {
      if (!this.fetchUrl) {
        this.$modal.msgWarning(t.urlRequired)
        return
      }
      fetchNetworkUrl(this.fetchUrl).then(res => {
        const body = res.data
        this.fetchResult = typeof body === 'string' ? body : JSON.stringify(body, null, 2)
      })
    }
  }
}
</script>

<style scoped>
.stat-row {
  margin-bottom: 16px;
}
.stat-card {
  margin-bottom: 12px;
  cursor: default;
}
.stat-card__inner {
  display: flex;
  align-items: center;
  gap: 14px;
}
.stat-card__icon {
  font-size: 32px;
}
.stat-card--ok .stat-card__icon { color: #67c23a; }
.stat-card--warn .stat-card__icon { color: #e6a23c; }
.stat-card--info .stat-card__icon { color: #909399; }
.stat-card--primary .stat-card__icon { color: #409eff; }
.stat-card__value {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
}
.stat-card__label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}
.panel-card {
  margin-bottom: 12px;
  min-height: 320px;
}
.panel-header {
  font-weight: 600;
}
.cmd-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.host-tag {
  margin-right: 6px;
  margin-bottom: 4px;
}
.field-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  text-align: center;
}
.weather-result {
  margin: 10px 0 0;
  font-size: 13px;
  color: #606266;
}
.mt8 { margin-top: 8px; }
.mt12 { margin-top: 12px; }
.network-actions { margin-top: 4px; }
</style>
