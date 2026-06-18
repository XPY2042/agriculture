<template>
  <div class="app-container user-manage-page">
    <!-- 页面标题 -->
    <el-card class="page-header-card" shadow="never">
      <div class="page-header-inner">
        <div class="page-header-text">
          <h2 class="page-title"><i class="el-icon-user-solid"></i> 用户管理</h2>
          <p class="page-desc">维护系统登录账号，分配角色；普通监测员账号仅可查看智慧农业功能。</p>
        </div>
        <div class="page-stats">
          <div class="stat-item">
            <span class="stat-value">{{ total }}</span>
            <span class="stat-label">用户总数</span>
          </div>
          <div class="stat-item">
            <span class="stat-value stat-active">{{ pageActiveCount }}</span>
            <span class="stat-label">本页正常</span>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="table-card" shadow="never">
      <!-- 搜索 -->
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="72px" class="search-form">
        <el-form-item label="用户名称" prop="userName">
          <el-input v-model="queryParams.userName" placeholder="登录账号" clearable style="width: 160px" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="用户昵称" prop="nickName">
          <el-input v-model="queryParams.nickName" placeholder="昵称" clearable style="width: 160px" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phonenumber">
          <el-input v-model="queryParams.phonenumber" placeholder="手机号" clearable style="width: 160px" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 工具栏 -->
      <el-row :gutter="10" class="mb8 toolbar-row">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['system:user:add']">新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['system:user:edit']">修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:user:remove']">删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="info" plain icon="el-icon-upload2" size="mini" @click="handleImport" v-hasPermi="['system:user:import']">导入</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['system:user:export']">导出</el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns" />
      </el-row>

      <!-- 用户表格 -->
      <el-table v-loading="loading" :data="userList" stripe class="user-table" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="48" align="center" />
        <el-table-column label="用户" min-width="160" v-if="columns.userName.visible">
          <template slot-scope="scope">
            <div class="user-cell">
              <el-avatar :size="36" icon="el-icon-user-solid" class="user-avatar" />
              <div class="user-cell-text">
                <a class="user-name-link" @click="handleViewData(scope.row)">{{ scope.row.userName }}</a>
                <span class="user-nick">{{ scope.row.nickName || '—' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="手机" align="center" prop="phonenumber" width="120" v-if="columns.phonenumber.visible" />
        <el-table-column label="状态" align="center" width="100" v-if="columns.status.visible">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              active-value="0"
              inactive-value="1"
              :disabled="scope.row.userId === 1"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" width="158" v-if="columns.createTime.visible">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="200" fixed="right" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <template v-if="scope.row.userId !== 1">
              <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:user:edit']">修改</el-button>
              <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['system:user:remove']">删除</el-button>
              <el-dropdown size="mini" @command="(cmd) => handleCommand(cmd, scope.row)" v-hasPermi="['system:user:resetPwd', 'system:user:edit']">
                <el-button size="mini" type="text" icon="el-icon-more">更多</el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="handleResetPwd" icon="el-icon-key" v-hasPermi="['system:user:resetPwd']">重置密码</el-dropdown-item>
                  <el-dropdown-item command="handleAuthRole" icon="el-icon-s-check" v-hasPermi="['system:user:edit']">分配角色</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </template>
            <el-tag v-else size="mini" type="warning" effect="plain">超级管理员</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="620px" append-to-body class="user-form-dialog">
      <el-form ref="form" :model="form" :rules="rules" label-width="88px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="用户昵称" prop="nickName">
              <el-input v-model="form.nickName" placeholder="显示名称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phonenumber">
              <el-input v-model="form.phonenumber" placeholder="11位手机号" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="选填" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.userId == undefined">
            <el-form-item label="登录账号" prop="userName">
              <el-input v-model="form.userName" placeholder="2-20位字符" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.userId == undefined">
            <el-form-item label="登录密码" prop="password" :rules="pwdValidator">
              <el-input v-model="form.password" type="password" placeholder="初始密码" maxlength="20" show-password />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="form.sex" placeholder="请选择" style="width: 100%">
                <el-option v-for="dict in dict.type.sys_user_sex" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色" prop="roleIds">
              <el-select v-model="form.roleIds" multiple placeholder="请选择角色（必选）" style="width: 100%">
                <el-option v-for="item in roleOptions" :key="item.roleId" :label="item.roleName" :value="item.roleId" :disabled="item.status == 1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注信息" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">保 存</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <user-view-drawer ref="userViewRef" />
    <excel-import-dialog
      ref="importUserRef"
      title="导入用户"
      action="/system/user/importData"
      template-action="/system/user/importTemplate"
      template-file-name="user_template"
      update-support-label="是否更新已存在的用户"
      @success="getList"
    />
  </div>
</template>

<script>
import { listUser, getUser, delUser, addUser, updateUser, resetUserPwd, changeUserStatus } from '@/api/system/user'
import ExcelImportDialog from '@/components/ExcelImportDialog'
import UserViewDrawer from './view'
import passwordRule from '@/utils/passwordRule'

/** 默认归属部门：农业监测中心 */
const DEFAULT_DEPT_ID = 100

export default {
  name: 'User',
  mixins: [passwordRule],
  dicts: ['sys_normal_disable', 'sys_user_sex'],
  components: { ExcelImportDialog, UserViewDrawer },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      userList: [],
      title: '',
      open: false,
      initPassword: '123456',
      roleOptions: [],
      form: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        nickName: undefined,
        phonenumber: undefined,
        status: undefined
      },
      columns: {
        userName: { label: '用户', visible: true },
        phonenumber: { label: '手机', visible: true },
        status: { label: '状态', visible: true },
        createTime: { label: '创建时间', visible: true }
      },
      rules: {
        userName: [
          { required: true, message: '登录账号不能为空', trigger: 'blur' },
          { min: 2, max: 20, message: '长度 2-20 位', trigger: 'blur' }
        ],
        nickName: [{ required: true, message: '用户昵称不能为空', trigger: 'blur' }],
        roleIds: [{ required: true, type: 'array', min: 1, message: '请至少选择一个角色', trigger: 'change' }],
        email: [{ type: 'email', message: '邮箱格式不正确', trigger: ['blur', 'change'] }],
        phonenumber: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
      }
    }
  },
  computed: {
    pageActiveCount() {
      if (!this.userList || !this.userList.length) return 0
      return this.userList.filter(u => u.status === '0').length
    }
  },
  created() {
    this.getList()
    this.getConfigKey('sys.user.initPassword').then(res => {
      if (res && res.data) this.initPassword = res.data
    }).catch(() => {})
  },
  methods: {
    getList() {
      this.loading = true
      listUser(this.queryParams).then(response => {
        this.userList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleStatusChange(row) {
      const text = row.status === '0' ? '启用' : '停用'
      this.$modal.confirm(`确认要${text}用户「${row.userName}」吗？`).then(() => {
        return changeUserStatus(row.userId, row.status)
      }).then(() => {
        this.$modal.msgSuccess(text + '成功')
      }).catch(() => {
        row.status = row.status === '0' ? '1' : '0'
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        userId: undefined,
        deptId: DEFAULT_DEPT_ID,
        userName: undefined,
        nickName: undefined,
        password: undefined,
        phonenumber: undefined,
        email: undefined,
        sex: undefined,
        status: '0',
        remark: undefined,
        roleIds: []
      }
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.userId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleCommand(command, row) {
      if (command === 'handleResetPwd') this.handleResetPwd(row)
      else if (command === 'handleAuthRole') this.handleAuthRole(row)
    },
    handleAdd() {
      this.reset()
      getUser().then(response => {
        this.roleOptions = response.roles || []
        this.open = true
        this.title = '新增用户'
        this.form.password = this.initPassword
      })
    },
    handleUpdate(row) {
      this.reset()
      const userId = row.userId || this.ids[0]
      getUser(userId).then(response => {
        this.form = response.data
        this.form.deptId = DEFAULT_DEPT_ID
        this.roleOptions = response.roles || []
        this.$set(this.form, 'roleIds', response.roleIds || [])
        this.open = true
        this.title = '修改用户'
      })
    },
    handleResetPwd(row) {
      this.$prompt(`请输入「${row.userName}」的新密码`, '重置密码', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        closeOnClickModal: false,
        inputValidator: this.pwdPromptValidator
      }).then(({ value }) => {
        resetUserPwd(row.userId, value).then(() => {
          this.$modal.msgSuccess('密码已重置')
        })
      }).catch(() => {})
    },
    handleAuthRole(row) {
      this.$router.push('/system/user-auth/role/' + row.userId)
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.form.deptId = DEFAULT_DEPT_ID
        this.form.postIds = []
        const req = this.form.userId != null ? updateUser(this.form) : addUser(this.form)
        req.then(() => {
          this.$modal.msgSuccess(this.form.userId != null ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const userIds = row.userId || this.ids
      this.$modal.confirm('是否确认删除所选用户？').then(() => delUser(userIds)).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleExport() {
      this.download('system/user/export', { ...this.queryParams }, `user_${Date.now()}.xlsx`)
    },
    handleViewData(row) {
      this.$refs.userViewRef.open(row.userId)
    },
    handleImport() {
      this.$refs.importUserRef.open()
    }
  }
}
</script>

<style scoped lang="scss">
.user-manage-page {
  padding-bottom: 16px;
}

.page-header-card {
  margin-bottom: 12px;
  border-left: 4px solid #67c23a;
}

.page-header-inner {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.page-title {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;

  i {
    color: #67c23a;
    margin-right: 8px;
  }
}

.page-desc {
  margin: 0;
  font-size: 13px;
  color: #909399;
  line-height: 1.6;
  max-width: 560px;
}

.page-stats {
  display: flex;
  gap: 28px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 72px;
}

.stat-value {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  line-height: 1.2;

  &.stat-active {
    color: #67c23a;
  }
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.table-card {
  border: none;

  ::v-deep .el-card__body {
    padding: 12px 16px 8px;
  }
}

.search-form {
  margin-bottom: 4px;
}

.toolbar-row {
  margin-bottom: 12px;
}

.user-table {
  width: 100%;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  background: #e8f5e9;
  color: #67c23a;
  flex-shrink: 0;
}

.user-cell-text {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.user-name-link {
  color: #409eff;
  font-weight: 500;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }
}

.user-nick {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}
</style>
