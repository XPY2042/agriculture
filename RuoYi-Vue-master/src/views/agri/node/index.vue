<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item v-if="isAdmin" label="所属用户" prop="createBy">
        <el-select v-model="queryParams.createBy" placeholder="请先选择用户" filterable clearable style="width: 200px" @change="handleUserChange">
          <el-option v-for="u in userOptions" :key="u.userName" :label="u.nickName + ' (' + u.userName + ')'" :value="u.userName" />
        </el-select>
      </el-form-item>
      <el-form-item label="设备编码" prop="nodeCode">
        <el-input v-model="queryParams.nodeCode" placeholder="设备编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="节点名称" prop="nodeName">
        <el-input v-model="queryParams.nodeName" placeholder="节点名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="状态" clearable>
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery" :disabled="isAdmin && !queryParams.createBy">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-alert v-if="isAdmin && !queryParams.createBy" title="请先选择需要查看的用户，再查看或管理其传感节点。" type="info" :closable="false" class="mb8" />

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['agri:node:add']" :disabled="isAdmin && !queryParams.createBy">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['agri:node:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['agri:node:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['agri:node:list']">导出Excel</el-button>
      </el-col>
      <el-button type="text" icon="el-icon-data-line" size="mini" @click="showStats=!showStats">{{ showStats ? '返回列表' : '数据报表' }}</el-button>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="nodeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="节点ID" align="center" prop="nodeId" width="80" />
      <el-table-column v-if="isAdmin" label="所属用户" align="center" prop="createBy" width="100" />
      <el-table-column label="设备编码" align="center" prop="nodeCode" width="130" />
      <el-table-column label="节点名称" align="center" prop="nodeName" :show-overflow-tooltip="true" />
      <el-table-column label="位置" align="center" prop="plotLocation" :show-overflow-tooltip="true" />
      <el-table-column label="作物" align="center" prop="cropType" width="100" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success" size="small">正常</el-tag>
          <el-tag v-else type="info" size="small">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['agri:node:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['agri:node:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item v-if="isAdmin" label="所属用户">
          <el-input :value="queryParams.createBy" disabled />
        </el-form-item>
        <el-form-item label="设备编码" prop="nodeCode">
          <el-input v-model="form.nodeCode" placeholder="全局唯一编码" :disabled="form.nodeId != null" />
        </el-form-item>
        <el-form-item label="节点名称" prop="nodeName">
          <el-input v-model="form.nodeName" placeholder="名称" />
        </el-form-item>
        <el-form-item label="位置" prop="plotLocation">
          <el-input v-model="form.plotLocation" placeholder="地块 / 温室" />
        </el-form-item>
        <el-form-item label="作物类型" prop="cropType">
          <el-input v-model="form.cropType" placeholder="如：小麦、番茄" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-row v-if="showStats" :gutter="16" class="mt16">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never"><div slot="header"><span>节点读数频率</span></div><div ref="nodeBarChart" style="height:320px" /></el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never">
          <div slot="header"><span>日均温湿度趋势</span><el-select v-model="statsNodeId" size="mini" style="float:right;width:160px" @change="loadDailyAvg"><el-option v-for="n in nodeList" :key="n.nodeId" :label="n.nodeName" :value="n.nodeId" /></el-select></div>
          <div ref="dailyChart" style="height:320px" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { listAgriNode, getAgriNode, delAgriNode, addAgriNode, updateAgriNode } from '@/api/agri/node'
import { listUser } from '@/api/system/user'
import { nodeReadingCount, readingDaily } from '@/api/agri/statistics'
import * as echarts from 'echarts'
require('echarts/theme/macarons')
export default {
  name: 'AgriNode',
  data() {
    return {
      loading: false,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      showStats: false,
      statsNodeId: null,
      total: 0,
      nodeList: [],
      userOptions: [],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        createBy: undefined,
        nodeCode: undefined,
        nodeName: undefined,
        status: undefined
      },
      form: {},
      rules: {
        nodeCode: [{ required: true, message: '设备编码不能为空', trigger: 'blur' }],
        nodeName: [{ required: true, message: '节点名称不能为空', trigger: 'blur' }]
      }
    }
  },
  computed: {
    isAdmin() {
      return this.$store.getters.id === 1
    }
  },
  created() {
    if (this.isAdmin) {
      this.loadUsers()
    } else {
      this.getList()
    }
  },
  methods: {
    loadUsers() {
      listUser({ pageNum: 1, pageSize: 500, status: '0' }).then(res => {
        this.userOptions = res.rows || []
      }).catch(() => {})
    },
    getList() {
      if (this.isAdmin && !this.queryParams.createBy) {
        this.nodeList = []
        this.total = 0
        this.loading = false
        return
      }
      this.loading = true
      listAgriNode(this.queryParams).then(response => {
        this.nodeList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    handleUserChange() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        nodeId: undefined,
        nodeCode: undefined,
        nodeName: undefined,
        plotLocation: undefined,
        cropType: undefined,
        status: '0',
        remark: undefined
      }
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      const createBy = this.queryParams.createBy
      this.resetForm('queryForm')
      if (this.isAdmin) {
        this.queryParams.createBy = createBy
      }
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.nodeId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      if (this.isAdmin && !this.queryParams.createBy) {
        this.$modal.msgWarning('请先选择所属用户')
        return
      }
      this.reset()
      this.open = true
      this.title = '添加传感节点'
    },
    handleUpdate(row) {
      this.reset()
      const nodeId = row.nodeId || this.ids[0]
      getAgriNode(nodeId).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改传感节点'
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.nodeId != null) {
            updateAgriNode(this.form).then(() => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            }).catch(() => {})
          } else {
            const payload = { ...this.form }
            if (this.isAdmin) {
              payload.createBy = this.queryParams.createBy
            }
            addAgriNode(payload).then(() => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            }).catch(() => {})
          }
        }
      })
    },
    handleDelete(row) {
      const nodeIds = row.nodeId || this.ids
      this.$modal.confirm('是否确认删除节点编号为"' + nodeIds + '"的数据项？').then(() => {
        return delAgriNode(nodeIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleExport() {
      this.download('agri/node/export', { ...this.queryParams }, `传感节点_${Date.now()}.xlsx`)
    },
    loadNodeStats() {
      nodeReadingCount().then(res => this.renderNodeBar(res.data || []))
      if (this.statsNodeId || (this.nodeList && this.nodeList.length)) {
        const nid = this.statsNodeId || this.nodeList[0].nodeId
        this.statsNodeId = nid
        this.loadDailyAvg()
      }
    },
    loadDailyAvg() {
      if (!this.statsNodeId) return
      readingDaily(this.statsNodeId, 7).then(res => this.renderDailyChart(res.data || []))
    },
    renderNodeBar(data) {
      if (!this.$refs.nodeBarChart) return
      const chart = echarts.init(this.$refs.nodeBarChart, 'macarons')
      chart.setOption({ tooltip:{trigger:'axis'}, xAxis:{type:'category',data:data.map(d=>d.nodeName),axisLabel:{rotate:30}}, yAxis:{type:'value'}, series:[{type:'bar',data:data.map(d=>d.readingCount)}] })
    },
    renderDailyChart(data) {
      if (!this.$refs.dailyChart) return
      const chart = echarts.init(this.$refs.dailyChart, 'macarons')
      chart.setOption({ tooltip:{trigger:'axis'}, legend:{data:['均温℃','均湿度%','均土壤湿度%']}, xAxis:{type:'category',data:data.map(d=>d.dateStr)}, yAxis:{type:'value'}, series:[{name:'均温℃',type:'line',smooth:true,data:data.map(d=>d.avgTemp)},{name:'均湿度%',type:'line',smooth:true,data:data.map(d=>d.avgHumidity)},{name:'均土壤湿度%',type:'line',smooth:true,data:data.map(d=>d.avgMoisture)}] })
    }
  },
  watch: {
    showStats(val) { if (val) this.$nextTick(() => this.loadNodeStats()) }
  }
}
</script>

<style scoped>
.mb8 { margin-bottom: 12px; }
</style>
