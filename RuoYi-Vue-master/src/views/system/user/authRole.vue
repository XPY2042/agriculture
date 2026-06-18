<template>
  <div class="app-container auth-role-page">
    <el-card shadow="never" class="auth-card">
      <div slot="header" class="auth-card-header">
        <span><i class="el-icon-s-check"></i> 分配角色</span>
        <el-button type="text" icon="el-icon-back" @click="close">返回用户列表</el-button>
      </div>

      <el-descriptions :column="2" border size="small" class="user-brief">
        <el-descriptions-item label="登录账号">{{ form.userName }}</el-descriptions-item>
        <el-descriptions-item label="用户昵称">{{ form.nickName }}</el-descriptions-item>
      </el-descriptions>

      <h4 class="section-title">可选角色（勾选后提交）</h4>
      <el-table
        v-loading="loading"
        :row-key="getRowKey"
        ref="table"
        :data="roles.slice((pageNum - 1) * pageSize, pageNum * pageSize)"
        @row-click="clickRow"
        @selection-change="handleSelectionChange"
        stripe
      >
        <el-table-column label="序号" type="index" align="center" width="55">
          <template slot-scope="scope">
            <span>{{ (pageNum - 1) * pageSize + scope.$index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column type="selection" :reserve-selection="true" :selectable="checkSelectable" width="55" />
        <el-table-column label="角色名称" align="center" prop="roleName" min-width="120" />
        <el-table-column label="权限字符" align="center" prop="roleKey" width="140" />
        <el-table-column label="状态" align="center" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'info'" size="mini">
              {{ scope.row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="160">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" :page.sync="pageNum" :limit.sync="pageSize" />

      <div class="auth-footer">
        <el-button type="primary" @click="submitForm">保存授权</el-button>
        <el-button @click="close">取消</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getAuthRole, updateAuthRole } from '@/api/system/user'

export default {
  name: 'AuthRole',
  data() {
    return {
      loading: true,
      total: 0,
      pageNum: 1,
      pageSize: 10,
      roleIds: [],
      roles: [],
      form: {}
    }
  },
  created() {
    const userId = this.$route.params && this.$route.params.userId
    if (userId) {
      this.loadRoles(userId)
    }
  },
  methods: {
    loadRoles(userId) {
      this.loading = true
      getAuthRole(userId).then(response => {
        this.form = response.user || {}
        this.roles = response.roles || []
        this.total = this.roles.length
        this.$nextTick(() => {
          if (this.$refs.table) {
            this.roles.forEach(row => {
              if (row.flag) {
                this.$refs.table.toggleRowSelection(row)
              }
            })
          }
        })
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    clickRow(row) {
      if (this.checkSelectable(row)) {
        this.$refs.table.toggleRowSelection(row)
      }
    },
    handleSelectionChange(selection) {
      this.roleIds = selection.map(item => item.roleId)
    },
    getRowKey(row) {
      return row.roleId
    },
    checkSelectable(row) {
      return row.status === '0'
    },
    submitForm() {
      const userId = this.form.userId
      const roleIds = this.roleIds.join(',')
      updateAuthRole({ userId, roleIds }).then(() => {
        this.$modal.msgSuccess('授权成功')
        this.close()
      })
    },
    close() {
      this.$router.push({ path: '/system/user' }).catch(() => {})
    }
  }
}
</script>

<style scoped lang="scss">
.auth-role-page {
  max-width: 960px;
}

.auth-card {
  border-left: 4px solid #67c23a;
}

.auth-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;

  i {
    color: #67c23a;
    margin-right: 6px;
  }
}

.user-brief {
  margin-bottom: 20px;
}

.section-title {
  margin: 0 0 12px;
  font-size: 14px;
  font-weight: 600;
  color: #606266;
}

.auth-footer {
  margin-top: 24px;
  text-align: center;
}
</style>
