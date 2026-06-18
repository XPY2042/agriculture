<template>
  <div class="app-container heatmap-page">
    <el-form
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      class="filter-panel"
      label-width="88px"
    >
      <el-form-item v-if="isAdmin" label="所属用户" prop="createBy">
        <el-select
          v-model="queryParams.createBy"
          placeholder="全部用户"
          filterable
          clearable
          style="width: 220px"
          @change="handleQuery"
        >
          <el-option
            v-for="u in userOptions"
            :key="u.userName"
            :label="u.nickName + ' (' + u.userName + ')'"
            :value="u.userName"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="报修状态" prop="repairState">
        <el-select
          v-model="queryParams.repairState"
          placeholder="全部"
          clearable
          style="width: 150px"
          @change="handleQuery"
        >
          <el-option label="需要报修" value="1" />
          <el-option label="无需报修" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="告警状态" prop="alarmState">
        <el-select
          v-model="queryParams.alarmState"
          placeholder="全部"
          clearable
          style="width: 160px"
          @change="handleQuery"
        >
          <el-option label="有未处理告警" value="1" />
          <el-option label="无未处理告警" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="12" class="stats-row">
      <el-col :xs="12" :sm="4" :md="4">
        <div class="stat-card stat-total" @click="quickFilter()">
          <span class="stat-icon-box icon-total"><i class="el-icon-s-grid" /></span>
          <div class="stat-info">
            <div class="stat-num">{{ nodes.length }}<small> 个</small></div>
            <div class="stat-label">当前节点</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="4" :md="4">
        <div class="stat-card stat-ok" @click="quickFilter('0', '0')">
          <span class="stat-icon-box icon-ok"><i class="el-icon-circle-check" /></span>
          <div class="stat-info">
            <div class="stat-num">{{ okCount }}<small> 个</small></div>
            <div class="stat-label">正常运行</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="4" :md="4">
        <div class="stat-card stat-warn" @click="quickFilter(undefined, '1')">
          <span class="stat-icon-box icon-warn"><i class="el-icon-warning-outline" /></span>
          <div class="stat-info">
            <div class="stat-num">{{ warnCount }}<small> 个</small></div>
            <div class="stat-label">预警节点</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="4" :md="4">
        <div class="stat-card stat-danger" @click="quickFilter(undefined, '1')">
          <span class="stat-icon-box icon-danger"><i class="el-icon-warning" /></span>
          <div class="stat-info">
            <div class="stat-num">{{ dangerCount }}<small> 个</small></div>
            <div class="stat-label">危险告警</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="4" :md="4">
        <div class="stat-card stat-repair" @click="quickFilter('1')">
          <span class="stat-icon-box icon-repair"><i class="el-icon-s-tools" /></span>
          <div class="stat-info">
            <div class="stat-num">{{ repairCount }}<small> 个</small></div>
            <div class="stat-label">需要报修</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-card shadow="never" class="map-card" v-loading="loading">
      <div slot="header" class="map-header">
        <div class="map-title-wrap">
          <span class="map-title">
            <i class="el-icon-map-location title-icon" />
            智慧农场 · 大棚热力地图
          </span>
          <span v-if="lastRefresh" class="refresh-hint">更新于 {{ lastRefresh }}</span>
        </div>
        <div class="map-actions">
          <span class="legend-item"><span class="legend-dot legend-ok" /><span>正常</span></span>
          <span class="legend-item"><span class="legend-dot legend-warn" /><span>预警</span></span>
          <span class="legend-item"><span class="legend-dot legend-danger" /><span>告警</span></span>
          <span class="legend-item"><span class="legend-dot legend-repair" /><span>报修</span></span>
          <el-divider direction="vertical" />
          <el-button type="primary" icon="el-icon-refresh" size="small" :loading="loading" @click="loadData">刷新</el-button>
        </div>
      </div>

      <div class="map-viewport" :style="{ minHeight: mapHeight + 'px' }">
        <div v-if="displayNodes.length" class="node-layer">
          <button
            v-for="(node, index) in displayNodes"
            :key="node.nodeId"
            type="button"
            class="node-marker"
            :class="'node-marker--' + nodeSeverity(node)"
            @click="openDetail(node)"
          >
            <span class="node-pin">
              <i :class="nodeIcon(node)" />
              <span class="node-index">{{ index + 1 }}</span>
            </span>
            <span class="node-copy">
              <span class="node-title-row">
                <span class="node-name" :title="node.nodeName">{{ node.nodeName }}</span>
                <span v-if="isAdmin" class="owner-badge" :title="'所属用户：' + (node.createBy || '-')">
                  {{ node.createBy || '-' }}
                </span>
              </span>
              <span class="node-desc" :title="nodeDescription(node)">
                {{ nodeDescription(node) }}
              </span>
              <span class="node-state-row">
                <span class="state-pill" :class="hasRepair(node) ? 'state-pill--repair' : 'state-pill--quiet'">
                  <i class="el-icon-s-tools" />
                  {{ repairLabel(node) }}
                </span>
                <span class="state-pill" :class="hasAlarm(node) ? 'state-pill--alarm' : 'state-pill--quiet'">
                  <i class="el-icon-bell" />
                  {{ alarmLabel(node) }}
                </span>
              </span>
            </span>
          </button>
        </div>
        <el-empty v-if="!loading && displayNodes.length === 0" description="暂无符合条件的在线大棚节点" class="map-empty" />
      </div>
    </el-card>

    <el-dialog
      :title="'大棚详情 · ' + (selectedNode.nodeName || '')"
      :visible.sync="detailOpen"
      width="700px"
      append-to-body
      class="detail-dialog"
    >
      <template v-if="selectedNode.nodeId">
        <el-descriptions :column="2" border size="small" class="detail-desc">
          <el-descriptions-item label="节点名称">{{ selectedNode.nodeName }}</el-descriptions-item>
          <el-descriptions-item label="设备编码">{{ selectedNode.nodeCode }}</el-descriptions-item>
          <el-descriptions-item label="所在位置">{{ selectedNode.plotLocation || '-' }}</el-descriptions-item>
          <el-descriptions-item label="种植作物">{{ selectedNode.cropType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属用户">{{ selectedNode.createBy || '-' }}</el-descriptions-item>
          <el-descriptions-item label="运行状态">
            <el-tag :type="severityTag(nodeSeverity(selectedNode))" size="small">
              {{ statusLabel(selectedNode) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="是否需要报修">
            <el-tag :type="hasRepair(selectedNode) ? 'warning' : 'success'" size="small">
              {{ repairLabel(selectedNode) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="未处理告警">
            <el-tag :type="hasAlarm(selectedNode) ? 'danger' : 'success'" size="small">
              {{ alarmLabel(selectedNode) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">未处理告警</el-divider>

        <el-table :data="nodeAlarms" v-loading="alarmLoading" size="small" max-height="240">
          <el-table-column label="指标" prop="metricName" width="100" />
          <el-table-column label="级别" width="80">
            <template slot-scope="s">
              <el-tag :type="s.row.alarmLevel === '2' ? 'danger' : 'warning'" size="mini">
                {{ s.row.alarmLevel === '2' ? '危险' : '预警' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="告警信息" prop="alarmMessage" :show-overflow-tooltip="true" />
          <el-table-column label="时间" prop="alarmTime" width="160">
            <template slot-scope="s">{{ parseTime(s.row.alarmTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template slot-scope="s">
              <el-button type="text" size="mini" @click="confirmOne(s.row.alarmId)">确认</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!alarmLoading && nodeAlarms.length === 0" description="该大棚暂无未处理告警" :image-size="60" />

        <template v-if="canViewRepairList">
          <el-divider content-position="left">报修记录</el-divider>

          <el-table :data="nodeRepairs" v-loading="repairLoading" size="small" max-height="200">
            <el-table-column label="原因" prop="title" width="120" :show-overflow-tooltip="true" />
            <el-table-column label="状态" width="80">
              <template slot-scope="s">
                <el-tag :type="repairStatusTag(s.row.status)" size="mini">
                  {{ repairStatusText(s.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="描述" prop="description" :show-overflow-tooltip="true" />
            <el-table-column label="时间" prop="createTime" width="160">
              <template slot-scope="s">{{ parseTime(s.row.createTime) }}</template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!repairLoading && nodeRepairs.length === 0" description="该大棚暂无报修记录" :image-size="60" />
        </template>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getNodeHeatmap } from '@/api/agri/node'
import { listAgriAlarm, confirmAlarms } from '@/api/agri/alarm'
import { listRepairRequest } from '@/api/repair/request'
import { listUser } from '@/api/system/user'

export default {
  name: 'AgriHeatmap',
  data() {
    return {
      loading: false,
      nodes: [],
      userOptions: [],
      mapHeight: 480,
      lastRefresh: '',
      detailOpen: false,
      selectedNode: {},
      nodeAlarms: [],
      alarmLoading: false,
      nodeRepairs: [],
      repairLoading: false,
      pollTimer: null,
      queryParams: {
        createBy: undefined,
        repairState: undefined,
        alarmState: undefined
      }
    }
  },
  computed: {
    isAdmin() {
      return this.$store.getters.id === 1
    },
    canViewRepairList() {
      const permissions = this.$store.getters.permissions || []
      const repairPerms = ['repair:apply:list', 'repair:admin:list', 'repair:worker:list']
      return this.isAdmin || permissions.includes('*:*:*') || permissions.some(p => repairPerms.includes(p))
    },
    displayNodes() {
      return this.sortNodes(this.nodes)
    },
    okCount() {
      return this.nodes.filter(n => this.nodeSeverity(n) === 'ok').length
    },
    warnCount() {
      return this.nodes.filter(n => this.nodeSeverity(n) === 'warn').length
    },
    dangerCount() {
      return this.nodes.filter(n => this.nodeSeverity(n) === 'danger').length
    },
    repairCount() {
      return this.nodes.filter(n => this.hasRepair(n)).length
    }
  },
  mounted() {
    if (this.isAdmin) {
      this.loadUsers()
    }
    this.loadData()
    this.pollTimer = setInterval(() => this.loadData(), 120000)
  },
  beforeDestroy() {
    if (this.pollTimer) {
      clearInterval(this.pollTimer)
      this.pollTimer = null
    }
  },
  methods: {
    loadUsers() {
      listUser({ pageNum: 1, pageSize: 500, status: '0' }).then(res => {
        this.userOptions = res.rows || []
      })
    },
    buildQuery() {
      const params = {}
      if (this.isAdmin && this.queryParams.createBy) {
        params.createBy = this.queryParams.createBy
      }
      if (this.queryParams.repairState !== undefined && this.queryParams.repairState !== '') {
        params.repairState = this.queryParams.repairState
      }
      if (this.queryParams.alarmState !== undefined && this.queryParams.alarmState !== '') {
        params.alarmState = this.queryParams.alarmState
      }
      return params
    },
    loadData() {
      this.loading = true
      getNodeHeatmap(this.buildQuery()).then(res => {
        this.nodes = res.data || []
        this.lastRefresh = this.parseTime(new Date())
        this.mapHeight = this.calcMapHeight(this.nodes.length)
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.loadData()
    },
    resetQuery() {
      this.queryParams = {
        createBy: undefined,
        repairState: undefined,
        alarmState: undefined
      }
      this.$nextTick(() => this.loadData())
    },
    quickFilter(repairState, alarmState) {
      this.queryParams.repairState = repairState
      this.queryParams.alarmState = alarmState
      this.loadData()
    },
    calcMapHeight(count) {
      if (count <= 0) return 480
      const rows = Math.ceil(count / 3)
      return Math.max(480, Math.min(820, rows * 136 + 120))
    },
    hasRepair(node) {
      return (node.repairCount || 0) > 0
    },
    hasAlarm(node) {
      return (node.alarmCount || 0) > 0
    },
    nodeSeverity(node) {
      if (node.maxAlarmLevel === '2') return 'danger'
      if (node.maxAlarmLevel === '1') return 'warn'
      if (this.hasRepair(node)) return 'repair'
      return 'ok'
    },
    severityTag(severity) {
      if (severity === 'danger') return 'danger'
      if (severity === 'warn' || severity === 'repair') return 'warning'
      return 'success'
    },
    nodeIcon(node) {
      const severity = this.nodeSeverity(node)
      if (severity === 'danger') return 'el-icon-warning'
      if (severity === 'warn') return 'el-icon-warning-outline'
      if (severity === 'repair') return 'el-icon-s-tools'
      return 'el-icon-location-information'
    },
    nodeDescription(node) {
      const crop = node.cropType || '未设置作物'
      const location = node.plotLocation || '未设置位置'
      const code = node.nodeCode || '--'
      return `${location} · ${crop} · ${code}`
    },
    repairLabel(node) {
      const count = node.repairCount || 0
      if (!count) return '无需报修'
      const status = node.maxRepairStatus === '1' ? '处理中' : '待受理'
      return `需报修 ${count} 条（${status}）`
    },
    alarmLabel(node) {
      const count = node.alarmCount || 0
      return count ? `未处理告警 ${count} 条` : '无未处理告警'
    },
    statusLabel(node) {
      const severity = this.nodeSeverity(node)
      if (severity === 'danger') return `危险 · ${node.alarmCount || 0} 条未处理告警`
      if (severity === 'warn') return `预警 · ${node.alarmCount || 0} 条未处理告警`
      if (severity === 'repair') return this.repairLabel(node)
      return '正常运行'
    },
    sortNodes(nodes) {
      const order = { danger: 0, warn: 1, repair: 2, ok: 3 }
      return [...nodes].sort((a, b) => {
        const sa = order[this.nodeSeverity(a)]
        const sb = order[this.nodeSeverity(b)]
        if (sa !== sb) return sa - sb
        const owner = (a.createBy || '').localeCompare(b.createBy || '', 'zh-CN')
        if (owner !== 0) return owner
        const loc = (a.plotLocation || '').localeCompare(b.plotLocation || '', 'zh-CN')
        if (loc !== 0) return loc
        return (a.nodeName || '').localeCompare(b.nodeName || '', 'zh-CN')
      })
    },
    openDetail(node) {
      this.selectedNode = node
      this.detailOpen = true
      this.loadNodeAlarms(node.nodeId)
      if (this.canViewRepairList) {
        this.loadNodeRepairs(node.nodeId)
      } else {
        this.nodeRepairs = []
      }
    },
    loadNodeAlarms(nodeId) {
      this.alarmLoading = true
      this.nodeAlarms = []
      listAgriAlarm({ pageNum: 1, pageSize: 50, nodeId, status: '0' }).then(res => {
        this.nodeAlarms = res.rows || []
      }).finally(() => {
        this.alarmLoading = false
      })
    },
    confirmOne(alarmId) {
      confirmAlarms([alarmId]).then(() => {
        this.$modal.msgSuccess('已确认')
        this.loadNodeAlarms(this.selectedNode.nodeId)
        this.loadData()
      })
    },
    loadNodeRepairs(nodeId) {
      this.repairLoading = true
      this.nodeRepairs = []
      listRepairRequest({ nodeId, pageNum: 1, pageSize: 50 }).then(res => {
        this.nodeRepairs = res.rows || []
      }).finally(() => {
        this.repairLoading = false
      })
    },
    repairStatusTag(status) {
      if (status === '0') return 'warning'
      if (status === '1') return ''
      if (status === '2') return 'success'
      return 'info'
    },
    repairStatusText(status) {
      if (status === '0') return '待受理'
      if (status === '1') return '处理中'
      if (status === '2') return '已完成'
      if (status === '3') return '已取消'
      return '--'
    }
  }
}
</script>

<style scoped lang="scss">
.heatmap-page {
  padding-bottom: 16px;
}

.filter-panel {
  margin-bottom: 14px;
  padding: 12px 14px 0;
  background: #fff;
  border: 1px solid #e6e8eb;
  border-radius: 8px;
}

.stats-row {
  margin-bottom: 14px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 10px;
  min-height: 72px;
  background: #fff;
  border: 1px solid #e6e8eb;
  border-radius: 8px;
  padding: 14px 16px;
  transition: all 0.2s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 18px rgba(31, 35, 41, 0.08);
    border-color: #cdd3dc;
  }

  .stat-icon-box {
    width: 42px;
    height: 42px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    i {
      font-size: 22px;
      color: #fff;
    }
  }

  .icon-total  { background: #5f6f85; }
  .icon-ok     { background: #2f9e74; }
  .icon-warn   { background: #d9822b; }
  .icon-danger { background: #d64545; }
  .icon-repair { background: #6266d9; }

  .stat-info {
    flex: 1;
    min-width: 0;
  }

  .stat-num {
    font-size: 22px;
    font-weight: 700;
    line-height: 1.2;
    color: #303133;
    font-family: "SF Mono", "Consolas", "Menlo", monospace;

    small {
      font-size: 12px;
      font-weight: 400;
      color: #909399;
    }
  }

  .stat-label {
    font-size: 12px;
    color: #909399;
    margin-top: 2px;
  }
}

.map-card {
  border: none;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 8px rgba(31, 35, 41, 0.06);

  ::v-deep .el-card__header {
    background: #fbfcfd;
    border-bottom: 1px solid #e6e8eb;
    padding: 14px 20px;
  }

  ::v-deep .el-card__body {
    padding: 0;
  }
}

.map-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 10px;
}

.map-title-wrap {
  display: flex;
  align-items: baseline;
  gap: 12px;
  flex-wrap: wrap;
}

.map-title {
  font-size: 16px;
  font-weight: 700;
  color: #303133;

  .title-icon {
    margin-right: 6px;
    font-size: 18px;
    color: #5f6f85;
  }
}

.refresh-hint {
  font-size: 12px;
  color: #909399;
}

.map-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
}

.legend-item {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  color: #606266;
  font-weight: 500;
}

.legend-dot {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 2px;

  &.legend-ok     { background: #2f9e74; }
  &.legend-warn   { background: #d9822b; }
  &.legend-danger { background: #d64545; }
  &.legend-repair { background: #6266d9; }
}

.map-viewport {
  position: relative;
  width: 100%;
  padding: 28px;
  background-color: #fff;
  background-image:
    linear-gradient(#d9dde3 1px, transparent 1px),
    linear-gradient(90deg, #d9dde3 1px, transparent 1px);
  background-size: 32px 32px;
  transition: min-height 0.2s ease;
}

.node-layer {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 22px;
}

.node-marker {
  display: grid;
  grid-template-columns: 52px minmax(0, 1fr);
  align-items: center;
  gap: 12px;
  width: 100%;
  min-height: 104px;
  padding: 14px;
  text-align: left;
  background: rgba(255, 255, 255, 0.96);
  border: 2px solid #d8dde5;
  border-radius: 8px;
  box-shadow: 0 4px 14px rgba(31, 35, 41, 0.08);
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 22px rgba(31, 35, 41, 0.12);
  }

  &:focus {
    outline: none;
    border-color: #409eff;
    box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.16);
  }
}

.node-pin {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  border-radius: 50%;
  color: #fff;
  box-shadow: 0 3px 10px rgba(31, 35, 41, 0.22);

  i {
    font-size: 24px;
  }
}

.node-index {
  position: absolute;
  right: -4px;
  bottom: -4px;
  min-width: 20px;
  height: 20px;
  padding: 0 4px;
  border: 2px solid #fff;
  border-radius: 10px;
  background: #303133;
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  line-height: 16px;
  text-align: center;
  font-family: "SF Mono", "Consolas", "Menlo", monospace;
}

.node-marker--ok {
  border-color: rgba(47, 158, 116, 0.45);

  .node-pin { background: #2f9e74; }
}

.node-marker--warn {
  border-color: rgba(217, 130, 43, 0.55);

  .node-pin { background: #d9822b; }
}

.node-marker--danger {
  border-color: rgba(214, 69, 69, 0.58);

  .node-pin { background: #d64545; }
}

.node-marker--repair {
  border-color: rgba(98, 102, 217, 0.5);

  .node-pin { background: #6266d9; }
}

.node-copy {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.node-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.node-name {
  flex: 1;
  min-width: 0;
  color: #303133;
  font-size: 15px;
  font-weight: 700;
  line-height: 1.35;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.owner-badge {
  max-width: 92px;
  padding: 2px 6px;
  border-radius: 4px;
  background: #eef2f7;
  color: #5f6f85;
  font-size: 12px;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.node-desc {
  display: block;
  margin-top: 5px;
  color: #606266;
  font-size: 12px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.node-state-row {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 10px;
}

.state-pill {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  max-width: 100%;
  padding: 4px 7px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  line-height: 1.2;

  i {
    font-size: 13px;
  }
}

.state-pill--quiet {
  background: #eef8f3;
  color: #2f9e74;
}

.state-pill--repair {
  background: #f1f0ff;
  color: #5257c8;
}

.state-pill--alarm {
  background: #fff1f0;
  color: #d64545;
}

.map-empty {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
}

::v-deep .detail-dialog {
  .el-dialog__header {
    background: #fbfcfd;
  }

  .detail-desc {
    margin-bottom: 8px;
  }
}

@media (max-width: 768px) {
  .map-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .map-viewport {
    padding: 18px;
  }

  .node-layer {
    grid-template-columns: 1fr;
    gap: 14px;
  }

  .node-marker {
    grid-template-columns: 46px minmax(0, 1fr);
    padding: 12px;
  }

  .node-pin {
    width: 42px;
    height: 42px;
  }
}
</style>
