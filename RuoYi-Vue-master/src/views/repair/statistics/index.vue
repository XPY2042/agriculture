<template>
  <div class="app-container">
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--warning" @click.native="activeTab='status'">
          <div class="stat-card__inner"><i class="el-icon-s-claim stat-card__icon" /><div><div class="stat-card__value">{{ summary.pending }}</div><div class="stat-card__label">待受理</div></div></div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--info" @click.native="activeTab='status'">
          <div class="stat-card__inner"><i class="el-icon-loading stat-card__icon" /><div><div class="stat-card__value">{{ summary.processing }}</div><div class="stat-card__label">处理中</div></div></div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--success" @click.native="activeTab='status'">
          <div class="stat-card__inner"><i class="el-icon-circle-check stat-card__icon" /><div><div class="stat-card__value">{{ summary.done }}</div><div class="stat-card__label">已完成</div></div></div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--primary">
          <div class="stat-card__inner"><i class="el-icon-money stat-card__icon" /><div><div class="stat-card__value">{{ summary.totalCost | formatCost }}</div><div class="stat-card__label">总维修费用</div></div></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <div slot="header"><span>工单状态分布</span></div>
          <div ref="statusChart" style="height:350px" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <div slot="header"><span>维修人员工作量</span></div>
          <div ref="techChart" style="height:350px" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="mt16">
      <el-col :span="24">
        <el-card shadow="never" class="chart-card">
          <div slot="header">
            <span>月度费用趋势</span>
            <el-select v-model="costMonths" size="mini" style="float:right;width:120px" @change="loadCostTrend">
              <el-option :value="3" label="近3月" />
              <el-option :value="6" label="近6月" />
              <el-option :value="12" label="近12月" />
            </el-select>
          </div>
          <div ref="costChart" style="height:350px" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons')
import { repairStatusStats, repairTechnicianStats, repairCostTrend } from '@/api/agri/statistics'

export default {
  name: 'RepairStatistics',
  data() {
    return {
      summary: { pending: 0, processing: 0, done: 0, totalCost: 0 },
      costMonths: 6
    }
  },
  mounted() {
    this.loadAll()
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts)
    if (this.statusChart) { this.statusChart.dispose(); this.statusChart = null }
    if (this.techChart) { this.techChart.dispose(); this.techChart = null }
    if (this.costChart) { this.costChart.dispose(); this.costChart = null }
  },
  methods: {
    loadAll() {
      this.loadStatusStats()
      this.loadTechnicianStats()
      this.loadCostTrend()
    },
    loadStatusStats() {
      repairStatusStats().then(res => {
        const data = res.data || []
        this.summary.pending = 0; this.summary.processing = 0; this.summary.done = 0
        data.forEach(d => {
          if (d.code === '0') this.summary.pending = d.count || 0
          if (d.code === '1') this.summary.processing = d.count || 0
          if (d.code === '2') this.summary.done = d.count || 0
        })
        this.renderStatusChart(data)
      })
    },
    loadTechnicianStats() {
      repairTechnicianStats().then(res => {
        const data = res.data || []
        let totalCost = 0
        data.forEach(d => { totalCost += (d.totalCost || 0) })
        this.summary.totalCost = totalCost
        this.renderTechChart(data)
      })
    },
    loadCostTrend() {
      repairCostTrend(this.costMonths).then(res => {
        this.renderCostChart(res.data || [])
      })
    },
    renderStatusChart(data) {
      this.$nextTick(() => {
        if (!this.statusChart) this.statusChart = echarts.init(this.$refs.statusChart, 'macarons')
        this.statusChart.setOption({
          tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
          series: [{
            type: 'pie', radius: ['40%', '70%'],
            label: { show: true, formatter: '{b}\n{c}单' },
            data: data.map(d => ({ name: d.label, value: d.count }))
          }]
        }, true)
      })
    },
    renderTechChart(data) {
      this.$nextTick(() => {
        if (!this.techChart) this.techChart = echarts.init(this.$refs.techChart, 'macarons')
        this.techChart.setOption({
          tooltip: { trigger: 'axis' },
          xAxis: { type: 'category', data: data.map(d => d.label), axisLabel: { rotate: 30 } },
          yAxis: [
            { type: 'value', name: '完成数' },
            { type: 'value', name: '费用(元)' }
          ],
          series: [
            { name: '完成工单', type: 'bar', data: data.map(d => d.count) },
            { name: '费用合计', type: 'bar', yAxisIndex: 1, data: data.map(d => d.totalCost) }
          ]
        }, true)
      })
    },
    renderCostChart(data) {
      this.$nextTick(() => {
        if (!this.costChart) this.costChart = echarts.init(this.$refs.costChart, 'macarons')
        this.costChart.setOption({
          tooltip: { trigger: 'axis' },
          xAxis: { type: 'category', data: data.map(d => d.label) },
          yAxis: [
            { type: 'value', name: '费用(元)' },
            { type: 'value', name: '工单数' }
          ],
          series: [
            { name: '维修费用', type: 'line', smooth: true, data: data.map(d => d.totalCost) },
            { name: '工单数', type: 'line', smooth: true, yAxisIndex: 1, data: data.map(d => d.count) }
          ]
        }, true)
      })
    },
    resizeCharts() {
      if (this.statusChart) this.statusChart.resize()
      if (this.techChart) this.techChart.resize()
      if (this.costChart) this.costChart.resize()
    }
  },
  filters: {
    formatCost(v) { return v != null ? '¥' + Number(v).toFixed(2) : '¥0.00' }
  }
}
</script>

<style scoped>
.stat-row { margin-bottom: 16px; }
.stat-card { cursor: pointer; margin-bottom: 8px; }
.stat-card--warning { border-left: 4px solid #e6a23c; }
.stat-card--info { border-left: 4px solid #909399; }
.stat-card--success { border-left: 4px solid #67c23a; }
.stat-card--primary { border-left: 4px solid #409eff; }
.stat-card__inner { display: flex; align-items: center; gap: 14px; }
.stat-card__icon { font-size: 32px; color: #909399; }
.stat-card--warning .stat-card__icon { color: #e6a23c; }
.stat-card--success .stat-card__icon { color: #67c23a; }
.stat-card--primary .stat-card__icon { color: #409eff; }
.stat-card__value { font-size: 28px; font-weight: 600; color: #303133; line-height: 1.2; }
.stat-card__label { font-size: 13px; color: #909399; margin-top: 4px; }
.chart-card { margin-bottom: 12px; }
.mt16 { margin-top: 16px; }
</style>
