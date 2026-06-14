<template>
  <div class="dashboard-container">
    <div class="container">
      <div class="tableBar">
        <label style="margin-right: 5px">
          员工姓名:
        </label>
        <el-input v-model="name" placeholder="请输入员工姓名" style="width: 15%" />
        <el-button type="primary" style="margin-left: 25px" @click="pageQuery()">
          查询
        </el-button>
        <el-button
          v-permission="'employee:user:add'"
          type="primary"
          style="float: right"
          @click="handleAddEmp"
        >
          +添加员工
        </el-button>
      </div>
      <el-table
        :data="records"
        stripe
        style="width: 100%"
      >
        <el-table-column
          prop="name"
          label="员工姓名"
          width="180"
        />
        <el-table-column
          prop="username"
          label="账号"
          width="180"
        />
        <el-table-column
          prop="phone"
          label="手机号"
        />
        <el-table-column
          prop="status"
          label="账号状态"
        >
          <template slot-scope="scope">
            {{ scope.row.status === 0 ? '禁用' : '启用' }}
          </template>
        </el-table-column>
        <el-table-column
          prop="updateTime"
          label="最后操作数据"
        />
        <el-table-column label="操作" min-width="280">
          <template slot-scope="scope">
            <el-button
              v-permission="'employee:user:edit'"
              type="text"
              @click="handleUpdateEmp(scope.row)"
            >
              修改
            </el-button>
            <el-button
              v-permission="'employee:user:assign-role'"
              type="text"
              @click="handleAssignRoles(scope.row)"
            >
              角色分配
            </el-button>
            <el-button
              v-permission="'employee:user:authorize-view'"
              type="text"
              @click="handleViewPermissions(scope.row)"
            >
              权限查看
            </el-button>
            <el-button
              v-permission="'employee:user:status'"
              type="text"
              @click="handleStartOrStop(scope.row)"
            >
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        class="pageList"
        :current-page="page"
        :page-sizes="[10, 20, 30, 40, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />

      <el-dialog
        title="角色分配"
        :visible.sync="roleDialogVisible"
        width="520px"
        @close="handleRoleDialogClose"
      >
        <div class="dialog-title-row">
          <span>员工：</span>
          <strong>{{ currentEmployee.name || '-' }}</strong>
          <span class="dialog-sub-text">（{{ currentEmployee.username || '-' }}）</span>
        </div>
        <el-checkbox-group v-model="selectedRoleIds" class="role-checkbox-group">
          <el-checkbox
            v-for="item in roleOptions"
            :key="item.id"
            :label="item.id"
            border
            class="role-checkbox-item"
          >
            {{ item.roleName }}
          </el-checkbox>
        </el-checkbox-group>
        <span slot="footer" class="dialog-footer">
          <el-button @click="handleRoleDialogClose">
            取 消
          </el-button>
          <el-button type="primary" :loading="roleSaving" @click="handleRoleSave">
            保 存
          </el-button>
        </span>
      </el-dialog>

      <el-dialog
        title="权限查看"
        :visible.sync="permissionDialogVisible"
        width="720px"
        @close="handlePermissionDialogClose"
      >
        <div class="permission-dialog-section">
          <div class="dialog-title-row">
            <span>员工：</span>
            <strong>{{ permissionInfo.employeeName || '-' }}</strong>
            <span class="dialog-sub-text">（{{ permissionInfo.username || '-' }}）</span>
          </div>
        </div>
        <div class="permission-dialog-section">
          <div class="section-label">
            已分配角色
          </div>
          <div v-if="permissionInfo.roleNames && permissionInfo.roleNames.length" class="tag-list">
            <el-tag
              v-for="(roleName, index) in permissionInfo.roleNames"
              :key="`${roleName}-${index}`"
              size="small"
              type="success"
              class="tag-item"
            >
              {{ roleName }}
            </el-tag>
          </div>
          <el-empty v-else :image-size="64" description="暂未分配角色" />
        </div>
        <div class="permission-dialog-section">
          <div class="section-label">
            权限编码
          </div>
          <div v-if="permissionInfo.permissionCodes && permissionInfo.permissionCodes.length" class="tag-list">
            <el-tag
              v-for="(code, index) in permissionInfo.permissionCodes"
              :key="`${code}-${index}`"
              size="small"
              class="tag-item"
            >
              {{ code }}
            </el-tag>
          </div>
          <el-empty v-else :image-size="64" description="当前暂无权限编码" />
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script lang="ts">
import {
  getEmployeeList,
  enableOrDisableEmployee,
  getEmployeeRoleOptions,
  getEmployeePermissionInfo,
  assignEmployeeRoles
} from '@/api/employee'

export default {
  data() {
    return {
      name: '',
      page: 1,
      pageSize: 10,
      total: 0,
      records: [],
      roleDialogVisible: false,
      permissionDialogVisible: false,
      roleSaving: false,
      roleOptions: [],
      selectedRoleIds: [],
      currentEmployee: {},
      permissionInfo: {
        employeeId: null,
        employeeName: '',
        username: '',
        roleIds: [],
        roleCodes: [],
        roleNames: [],
        permissionCodes: []
      }
    }
  },
  created() {
    this.pageQuery()
  },
  methods: {
    pageQuery() {
      const params = { name: this.name, page: this.page, pageSize: this.pageSize }
      getEmployeeList(params).then(res => {
        if (res.data.code === 1) {
          this.total = res.data.data.total
          this.records = res.data.data.records
        }
      }).catch(err => {
        this.$message.error('请求出错了：' + err.message)
      })
    },
    loadRoleOptions() {
      return getEmployeeRoleOptions().then(res => {
        if (res.data.code === 1) {
          this.roleOptions = res.data.data || []
        } else {
          this.$message.error(res.data.msg || '角色选项加载失败')
        }
      }).catch(err => {
        this.$message.error('角色选项加载失败：' + err.message)
      })
    },
    async handleAssignRoles(row) {
      if (row.username === 'admin') {
        this.$message.warning('admin为系统管理员账号，请谨慎调整其角色')
      }
      this.currentEmployee = { ...row }
      await this.loadRoleOptions()
      getEmployeePermissionInfo(row.id).then(res => {
        if (res.data.code === 1) {
          const data = res.data.data || {}
          this.selectedRoleIds = data.roleIds || []
          this.roleDialogVisible = true
        } else {
          this.$message.error(res.data.msg || '员工权限详情加载失败')
        }
      }).catch(err => {
        this.$message.error('员工权限详情加载失败：' + err.message)
      })
    },
    handleRoleSave() {
      if (!this.selectedRoleIds.length) {
        this.$message.warning('请至少选择一个角色')
        return
      }
      this.roleSaving = true
      assignEmployeeRoles(this.currentEmployee.id, {
        employeeId: this.currentEmployee.id,
        roleIds: this.selectedRoleIds
      }).then(res => {
        if (res.data.code === 1) {
          this.$message.success('员工角色分配成功')
          this.roleDialogVisible = false
        } else {
          this.$message.error(res.data.msg || '员工角色分配失败')
        }
      }).catch(err => {
        this.$message.error('员工角色分配失败：' + err.message)
      }).finally(() => {
        this.roleSaving = false
      })
    },
    handleRoleDialogClose() {
      this.roleDialogVisible = false
      this.selectedRoleIds = []
      this.currentEmployee = {}
    },
    handleViewPermissions(row) {
      getEmployeePermissionInfo(row.id).then(res => {
        if (res.data.code === 1) {
          this.permissionInfo = res.data.data || {
            employeeId: null,
            employeeName: '',
            username: '',
            roleIds: [],
            roleCodes: [],
            roleNames: [],
            permissionCodes: []
          }
          this.permissionDialogVisible = true
        } else {
          this.$message.error(res.data.msg || '员工权限详情加载失败')
        }
      }).catch(err => {
        this.$message.error('员工权限详情加载失败：' + err.message)
      })
    },
    handlePermissionDialogClose() {
      this.permissionDialogVisible = false
      this.permissionInfo = {
        employeeId: null,
        employeeName: '',
        username: '',
        roleIds: [],
        roleCodes: [],
        roleNames: [],
        permissionCodes: []
      }
    },
    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.pageQuery()
    },
    handleCurrentChange(page) {
      this.page = page
      this.pageQuery()
    },
    handleStartOrStop(row) {
      if (row.username === 'admin') {
        this.$message.error('admin为系统的管理员账号，不能更改账号状态！')
        return
      }

      this.$confirm('确认要修改当前员工账号的状态吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const p = {
          id: row.id,
          status: !row.status ? 1 : 0
        }

        enableOrDisableEmployee(p).then(res => {
          if (res.data.code === 1) {
            this.$message.success('员工的账号状态修改成功！')
            this.pageQuery()
          }
        })
      })
    },
    handleAddEmp() {
      this.$router.push('/employee/add')
    },
    handleUpdateEmp(row) {
      if (row.username === 'admin') {
        this.$message.error('admin为系统的管理员账号，不能修改！')
        return
      }

      this.$router.push({
        path: '/employee/add',
        query: { id: row.id }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.disabled-text {
  color: #bac0cd !important;
}

.dialog-title-row {
  margin-bottom: 16px;
  color: #303133;
}

.dialog-sub-text {
  margin-left: 6px;
  color: #909399;
}

.role-checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.role-checkbox-item {
  margin-right: 0;
}

.permission-dialog-section {
  margin-bottom: 18px;
}

.section-label {
  margin-bottom: 10px;
  font-weight: 600;
  color: #303133;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  margin-right: 0;
}
</style>
