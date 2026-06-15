<template>
  <div class="app-container heatmap-page">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="12" class="stats-row">
      <el-col :xs="12" :sm="6" :md="6">
        <el-card shadow="hover" class="stat-card stat-total">
          <div class="stat-num">{{ nodes.length }}</div>
          <div class="stat-label">大棚总数</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6">
        <el-card shadow="hover" class="stat-card stat-ok">
          <div class="stat-num">{{ okCount }}</div>
          <div class="stat-label">正常运行</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6">
        <el-card shadow="hover" class="stat-card stat-warn">
          <div class="stat-num">{{ warnCount }}</div>
          <div class="stat-label">预警大棚</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6">
        <el-card shadow="hover" class="stat-card stat-danger">
          <div class="stat-num">{{ dangerCount }}</div>
          <div class="stat-label">告警大棚</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 虚拟农场地图 -->
    <el-card shadow="never" class="map-card" v-loading="loading">
      <div slot="header" class="map-header">
        <span class="map-title"><i class="el-icon-map-location"></i> 智慧农场 · 大棚热力地图</span>
        <div class="map-actions">
          <el-tooltip content="绿色=正常 · 橙色=预警 · 红色=告警 · 灰色=无数据">
            <el-tag size="small" type="info">图例</el-tag>
          </el-tooltip>
          <span class="legend-dot legend-ok"></span><span class="legend-text">正常</span>
          <span class="legend-dot legend-warn"></span><span class="legend-text">预警</span>
          <span class="legend-dot legend-danger"></span><span class="legend-text">告警</span>
          <el-divider direction="vertical" />
          <el-button type="primary" icon="el-icon-refresh" size="small" @click="loadData">刷新</el-button>
        </div>
      </div>

      <!-- 地图主体 -->
      <div ref="mapRef" class="map-container" />

      <!-- 空状态 -->
      <el-empty v-if="!loading && nodes.length === 0" description="暂无在线大棚节点" />
    </el-card>

    <!-- 节点告警详情弹窗 -->
    <el-dialog
      :title="'大棚详情 · ' + selectedNode.nodeName"
      :visible.sync="detailOpen"
      width="680px"
      append-to-body
    >
      <template v-if="selectedNode.nodeId">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="节点名称">{{ selectedNode.nodeName }}</el-descriptions-item>
          <el-descriptions-item label="设备编码">{{ selectedNode.nodeCode }}</el-descriptions-item>
          <el-descriptions-item label="所在位置">{{ selectedNode.plotLocation || '-' }}</el-descriptions-item>
          <el-descriptions-item label="种植作物">{{ selectedNode.cropType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属用户">{{ selectedNode.createBy || '-' }}</el-descriptions-item>
          <el-descriptions-item label="告警总数">
            <el-tag :type="levelTag(selectedNode.maxAlarmLevel)" size="small">
              {{ selectedNode.alarmCount || 0 }} 条
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">未处理告警列表</el-divider>

        <el-table :data="nodeAlarms" v-loading="alarmLoading" size="small" max-height="280">
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
      </template>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getNodeHeatmap } from '@/api/agri/node'
import { listAgriAlarm, confirmAlarms } from '@/api/agri/alarm'

const ALARM_COLORS = {
  ok: '#67c23a',
  warn: '#e6a23c',
  danger: '#f56c6c',
  noData: '#c0c4cc'
}

export default {
  name: 'AgriHeatmap',
  data() {
    return {
      loading: false,
      nodes: [],
      chart: null,
      detailOpen: false,
      selectedNode: {},
      nodeAlarms: [],
      alarmLoading: false,
      pollTimer: null
    }
  },
  computed: {
    okCount() {
      return this.nodes.filter(n => !n.maxAlarmLevel || (n.alarmCount || 0) === 0).length
    },
    warnCount() {
      return this.nodes.filter(n => n.maxAlarmLevel === '1').length
    },
    dangerCount() {
      return this.nodes.filter(n => n.maxAlarmLevel === '2').length
    }
  },
  mounted() {
    this.loadData()
    this.pollTimer = setInterval(() => this.loadData(), 120000)
    window.addEventListener('resize', this.resizeChart)
  },
  beforeDestroy() {
    if (this.pollTimer) clearInterval(this.pollTimer)
    window.removeEventListener('resize', this.resizeChart)
    if (this.chart) { this.chart.dispose(); this.chart = null }
  },
  methods: {
    levelTag(level) {
      if (level === '2') return 'danger'
      if (level === '1') return 'warning'
      return 'success'
    },
    alarmColor(level) {
      if (level === '2') return ALARM_COLORS.danger
      if (level === '1') return ALARM_COLORS.warn
      return ALARM_COLORS.ok
    },
    loadData() {
      this.loading = true
      getNodeHeatmap().then(res => {
        this.nodes = res.data || []
        this.$nextTick(() => this.renderChart())
      }).finally(() => {
        this.loading = false
      })
    },
    resizeChart() {
      if (this.chart) this.chart.resize()
    },
    renderChart() {
      if (!this.$refs.mapRef) return
      if (!this.chart) {
        this.chart = echarts.init(this.$refs.mapRef)
        this.chart.on('click', (params) => {
          if (params.data && params.data.nodeId) {
            this.openDetail(params.data)
          }
        })
      }

      const nodes = this.nodes
      if (!nodes.length) {
        this.chart.clear()
        return
      }

      // 按位置自动布局：每行4个，自动换行
      const COLS = 4
      const CELL_W = 22  // 每个大棚宽度%
      const CELL_H = 18  // 每个大棚高度%
      const GAP_X = 2
      const GAP_Y = 3
      const START_X = 4
      const START_Y = 6

      const graphics = []
      const labels = []

      nodes.forEach((n, i) => {
        const row = Math.floor(i / COLS)
        const col = i % COLS
        const x = START_X + col * (CELL_W + GAP_X)
        const y = START_Y + row * (CELL_H + GAP_Y)

        const color = this.alarmColor(n.maxAlarmLevel)
        const alarmLabel = n.alarmCount > 0 ? n.alarmCount + '条告警' : '正常'

        // 大棚矩形
        graphics.push({
          type: 'rect',
          shape: { x: 0, y: 0, width: CELL_W, height: CELL_H },
          left: x + '%',
          top: y + '%',
          style: {
            fill: color,
            stroke: '#fff',
            lineWidth: 2,
            shadowBlur: 8,
            shadowColor: 'rgba(0,0,0,0.25)',
            shadowOffsetY: 2
          },
          z: 10
        })
        // 顶部深色条
        graphics.push({
          type: 'rect',
          shape: { x: 0, y: 0, width: CELL_W, height: 3.5 },
          left: x + '%',
          top: y + '%',
          style: { fill: 'rgba(0,0,0,0.18)' },
          z: 11
        })
        // 作物图标 — 使用文字
        labels.push({
          type: 'text',
          left: (x + 1) + '%',
          top: (y + 0.5) + '%',
          style: {
            text: n.cropType ? '🌱 ' + n.cropType : '🏠',
            font: 'bold 11px "Microsoft YaHei"',
            fill: '#fff'
          },
          z: 12
        })
        // 大棚名称
        labels.push({
          type: 'text',
          left: (x + 1) + '%',
          top: (y + 4.5) + '%',
          style: {
            text: n.nodeName,
            font: 'bold 13px "Microsoft YaHei"',
            fill: '#fff'
          },
          z: 12
        })
        // 位置
        labels.push({
          type: 'text',
          left: (x + 1) + '%',
          top: (y + 6.5) + '%',
          style: {
            text: '📍 ' + (n.plotLocation || '--'),
            font: '10px "Microsoft YaHei"',
            fill: 'rgba(255,255,255,0.8)'
          },
          z: 12
        })
        // 告警状态
        labels.push({
          type: 'text',
          left: (x + 1) + '%',
          top: (y + 8.5) + '%',
          style: {
            text: alarmLabel,
            font: 'bold 11px "Microsoft YaHei"',
            fill: n.alarmCount > 0 ? '#fff' : 'rgba(255,255,255,0.75)'
          },
          z: 12
        })
        // 告警图标
        if (n.maxAlarmLevel === '2') {
          labels.push({
            type: 'text',
            right: (100 - x - CELL_W + 0.5) + '%',
            top: (y + 0.5) + '%',
            style: { text: '🔴', font: '14px sans-serif' },
            z: 13
          })
        } else if (n.maxAlarmLevel === '1') {
          labels.push({
            type: 'text',
            right: (100 - x - CELL_W + 0.5) + '%',
            top: (y + 0.5) + '%',
            style: { text: '⚠️', font: '14px sans-serif' },
            z: 13
          })
        }

        // 数据点（用于点击）
        labels.push({
          type: 'rect',
          shape: { x: 0, y: 0, width: CELL_W, height: CELL_H },
          left: x + '%',
          top: y + '%',
          style: { fill: 'transparent' },
          onclick: () => this.openDetail(n),
          cursor: 'pointer',
          z: 20
        })
      })

      // 计算总行数
      const rows = Math.ceil(nodes.length / COLS)
      const totalH = START_Y + rows * (CELL_H + GAP_Y) + 5

      this.chart.setOption({
        backgroundColor: '#1a3c1a',
        graphic: [...graphics, ...labels],
        tooltip: {
          show: true,
          backgroundColor: 'rgba(30,30,30,0.9)',
          borderColor: '#555',
          textStyle: { color: '#fff', fontSize: 12 },
          formatter: (params) => {
            if (!params.data || !params.data.nodeId) return ''
            const n = params.data
            const lvl = n.maxAlarmLevel === '2' ? '🔴 危险' : n.maxAlarmLevel === '1' ? '⚠️ 预警' : '✅ 正常'
            return `<b>${n.nodeName}</b><br/>
              作物：${n.cropType || '--'}<br/>
              位置：${n.plotLocation || '--'}<br/>
              状态：${lvl}<br/>
              告警：${n.alarmCount || 0} 条<br/>
              <span style="color:#909399;font-size:11px">点击查看详情</span>`
          }
        },
        // 散布隐藏的散点用于 tooltip 和 click
        series: [{
          type: 'scatter',
          data: nodes.map((n, i) => {
            const row = Math.floor(i / COLS)
            const col = i % COLS
            const cx = START_X + col * (CELL_W + GAP_X) + CELL_W / 2
            const cy = START_Y + row * (CELL_H + GAP_Y) + CELL_H / 2
            return {
              value: [cx, cy],
              nodeId: n.nodeId,
              nodeName: n.nodeName,
              cropType: n.cropType,
              plotLocation: n.plotLocation,
              maxAlarmLevel: n.maxAlarmLevel,
              alarmCount: n.alarmCount
            }
          }),
          symbolSize: 1,
          itemStyle: { color: 'transparent' },
          emphasis: { scale: false }
        }],
        xAxis: { show: false, min: 0, max: 100 },
        yAxis: { show: false, min: 0, max: Math.max(totalH, 50) }
      }, true)
    },
    openDetail(node) {
      this.selectedNode = node
      this.detailOpen = true
      this.loadNodeAlarms(node.nodeId)
    },
    loadNodeAlarms(nodeId) {
      this.alarmLoading = true
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
    }
  }
}
</script>

<style scoped lang="scss">
.heatmap-page {
  padding-bottom: 16px;
}

.stats-row {
  margin-bottom: 12px;
}

.stat-card {
  text-align: center;
  border-left: 4px solid #ddd;

  .stat-num {
    font-size: 28px;
    font-weight: 700;
    line-height: 1.2;
  }
  .stat-label {
    font-size: 13px;
    color: #909399;
    margin-top: 4px;
  }

  &.stat-total { border-left-color: #409eff; .stat-num { color: #409eff; } }
  &.stat-ok    { border-left-color: #67c23a; .stat-num { color: #67c23a; } }
  &.stat-warn  { border-left-color: #e6a23c; .stat-num { color: #e6a23c; } }
  &.stat-danger{ border-left-color: #f56c6c; .stat-num { color: #f56c6c; } }
}

.map-card {
  border: none;

  ::v-deep .el-card__body {
    padding: 0;
  }
}

.map-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
}

.map-title {
  font-size: 16px;
  font-weight: 600;

  i { color: #67c23a; margin-right: 6px; }
}

.map-actions {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
}

.legend-dot {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 2px;
  &.legend-ok     { background: #67c23a; }
  &.legend-warn   { background: #e6a23c; }
  &.legend-danger { background: #f56c6c; }
}

.legend-text {
  color: #606266;
  margin-right: 6px;
}

.map-container {
  width: 100%;
  height: 500px;
  border-radius: 0 0 4px 4px;
}
</style>
