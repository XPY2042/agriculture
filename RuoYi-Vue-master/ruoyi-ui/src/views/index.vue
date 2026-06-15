<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :span="24">
        <h2 class="home-title">{{ projectTitle }}</h2>
        <p class="home-subtitle">
          基于物联网的农田环境监测：采集土壤湿度、空气温湿度、光照等数据，结合分析规则给出作物生长建议。
        </p>
        <p class="home-version">当前版本：v{{ version }}</p>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="agri-entry-row">
      <el-col :span="24">
        <el-card shadow="hover" class="agri-entry-card">
          <div slot="header" class="agri-entry-header">
            <span><i class="el-icon-data-line"></i> 功能入口</span>
            <el-tag type="success" size="mini" effect="plain">IoT</el-tag>
          </div>
          <p class="agri-entry-desc">
            在<strong>环境监测</strong>中查看实时读数、历史趋势与生长建议；在<strong>传感节点</strong>中维护设备档案；在<strong>远程互联</strong>中通过网络远程操作设备。无硬件时可使用「模拟上报」生成演示数据。
          </p>
          <ul class="agri-entry-list">
            <li>环境监测：多参数曲线、最新读数、刷新与模拟上报</li>
            <li>生长建议：按阈值与滑动窗口均值输出提示</li>
            <li>传感节点：设备编码、地块、作物类型等</li>
            <li>远程互联：出网检测、公网天气、远程指令与操作记录</li>
          </ul>
          <div class="agri-entry-actions">
            <el-button type="primary" icon="el-icon-view" @click="goAgriMonitor">进入环境监测</el-button>
            <el-button type="success" plain icon="el-icon-cpu" @click="goAgriNode">传感节点管理</el-button>
            <el-button type="warning" plain icon="el-icon-connection" @click="goAgriRemote">远程互联</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import defaultSettings from '@/settings'

export default {
  name: 'Index',
  data() {
    return {
      projectTitle: defaultSettings.title,
      version: '1.0.0'
    }
  },
  methods: {
    goAgriMonitor() {
      // 与 sys_menu 中「环境监测」path 一致（勿用 /agri/monitor，会与系统监控路由冲突）
      this.$router.push({ path: '/agri/agriEnv' }).catch(() => {})
    },
    goAgriNode() {
      this.$router.push({ path: '/agri/agriSensor' }).catch(() => {})
    },
    goAgriRemote() {
      this.$router.push({ path: '/agri/agriRemote' }).catch(() => {})
    }
  }
}
</script>

<style scoped lang="scss">
.home-title {
  margin-top: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}
.home-subtitle {
  margin-top: 12px;
  line-height: 1.7;
  color: #606266;
  font-size: 14px;
}
.home-version {
  margin-top: 8px;
  color: #909399;
  font-size: 13px;
}

.agri-entry-row {
  margin-top: 20px;
}
.agri-entry-card {
  border-left: 4px solid #67c23a;
}
.agri-entry-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
}
.agri-entry-desc {
  line-height: 1.7;
  color: #606266;
}
.agri-entry-list {
  margin: 12px 0 16px;
  padding-left: 20px;
  color: #606266;
  line-height: 1.8;
  list-style: disc;
}
.agri-entry-actions {
  margin-bottom: 0;
}

.home {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
  font-size: 14px;
  color: #606266;
}
</style>
