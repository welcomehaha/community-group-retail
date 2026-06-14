<template>
  <div class="addBrand-container">
    <div class="container">
      <el-form ref="ruleForm" :model="ruleForm" :rules="rules" label-width="180px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="ruleForm.username" />
        </el-form-item>
        <el-form-item label="员工姓名" prop="name">
          <el-input v-model="ruleForm.name" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="ruleForm.phone" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio v-model="ruleForm.sex" label="1">
            男
          </el-radio>
          <el-radio v-model="ruleForm.sex" label="2">
            女
          </el-radio>
        </el-form-item>
        <el-form-item label="身份证号" prop="idNumber">
          <el-input v-model="ruleForm.idNumber" />
        </el-form-item>
        <el-form-item
          label="员工角色"
          prop="roleIds"
        >
          <el-checkbox-group v-model="ruleForm.roleIds" class="role-checkbox-group">
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
        </el-form-item>
        <div class="subBox">
          <el-button type="primary" @click="submitForm('ruleForm',false)">
            保存
          </el-button>
          <el-button
            v-if="optType === 'add'"
            type="primary"
            @click="submitForm('ruleForm',true)"
          >
            保存并继续添加员工
          </el-button>
          <el-button @click="() => this.$router.push('/employee')">
            返回
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import {
  createEmployeeWithRoles,
  getEmployeeRoleOptions,
  getEmployeeById,
  updateEmployee,
  getEmployeePermissionInfo,
  assignEmployeeRoles
} from '@/api/employee'
import {
  validateEmployeeIdNumber,
  validateEmployeeName,
  validateEmployeePhone,
  validateEmployeeRoleIds,
  validateEmployeeUsername
} from '@/utils/validators/employee'
export default {
  data() {
    return {
      optType: '',//当前操作的类型，新增或者修改
      roleOptions: [],
      ruleForm: {
        name: '',
        username: '',
        sex: '1',
        phone: '',
        idNumber: '',
        roleIds: []
      }, // 员工表单同时承载基础信息和角色信息，保证编辑页授权闭环。
      rules: {
        name: [
          { trigger: 'blur', validator: validateEmployeeName }
        ],
        username: [
          { trigger: 'blur', validator: validateEmployeeUsername }
        ],
        phone: [
          { trigger: 'blur', validator: validateEmployeePhone }
        ],
        idNumber: [
          { trigger: 'blur', validator: validateEmployeeIdNumber }
        ],
        roleIds: [
          {
            trigger: 'change',
            validator: validateEmployeeRoleIds
          }
        ]
      }
    }
  },
  created() {
    // 获取路由参数（id），如果有则为修改操作，否则为新增操作。
    this.optType = this.$route.query.id ? 'update' : 'add'
    this.initPage()
  },
  methods: {
    async initPage() {
      await this.loadRoleOptions()
      if (this.optType === 'update') {
        await this.loadEmployeeDetail()
      }
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
    loadEmployeeDetail() {
      const employeeId = Number(this.$route.query.id)
      return Promise.all([
        getEmployeeById(employeeId),
        getEmployeePermissionInfo(employeeId)
      ]).then(([employeeRes, permissionRes]) => {
        if (employeeRes.data.code !== 1) {
          this.$message.error(employeeRes.data.msg || '员工信息加载失败')
          return
        }
        if (permissionRes.data.code !== 1) {
          this.$message.error(permissionRes.data.msg || '员工角色加载失败')
          return
        }
        const employeeData = employeeRes.data.data || {}
        const permissionData = permissionRes.data.data || {}
        this.ruleForm = {
          ...this.ruleForm,
          ...employeeData,
          username: (employeeData.username || '').trim(),
          name: (employeeData.name || '').trim(),
          phone: (employeeData.phone || '').trim(),
          idNumber: (employeeData.idNumber || '').trim(),
          roleIds: permissionData.roleIds || []
        }
      }).catch(err => {
        this.$message.error('员工编辑信息加载失败：' + err.message)
      })
    },
    normalizeRuleForm() {
      // 提交前统一去除首尾空格，确保与后端校验口径保持一致。
      this.ruleForm = {
        ...this.ruleForm,
        username: (this.ruleForm.username || '').trim(),
        name: (this.ruleForm.name || '').trim(),
        phone: (this.ruleForm.phone || '').trim(),
        idNumber: (this.ruleForm.idNumber || '').trim()
      }
    },
    submitForm(formName,isContinue){
      this.normalizeRuleForm()
      // 进行表单校验，通过后再执行保存。
      this.$refs[formName].validate(async (valid) => {
        if(valid) {
          // 表单校验通过，发起 Ajax 请求，将数据提交到后端。
          if(this.optType === 'add'){ //新增操作
            createEmployeeWithRoles(this.ruleForm).then((res) => {
                if(res.data.code === 1){
                  this.$message.success('员工添加并分配角色成功！')
                  if(isContinue){
                  this.ruleForm = {
                      name: '',
                      username: '',
                      sex: '1',
                      phone: '',
                      idNumber: '',
                      roleIds: []
                    }
                  }else {
                    this.$router.push('/employee')
                  }
                }else {
                  this.$message.error(res.data.msg)
                }
              })
          }else{ //修改操作
            const employeeId = Number(this.$route.query.id)
            try {
              const updateRes = await updateEmployee(this.ruleForm)
              if (updateRes.data.code !== 1) {
                this.$message.error(updateRes.data.msg || '员工信息修改失败')
                return
              }

              const roleRes = await assignEmployeeRoles(employeeId, {
                employeeId,
                roleIds: this.ruleForm.roleIds
              })
              if (roleRes.data.code !== 1) {
                this.$message.error(roleRes.data.msg || '员工角色保存失败')
                return
              }

              this.$message.success('员工信息与角色修改成功！')
              this.$router.push('/employee')
            } catch (err) {
              this.$message.error('员工编辑保存失败：' + err.message)
            }
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.addBrand {
  &-container {
    margin: 30px;
    margin-top: 30px;
    .HeadLable {
      background-color: transparent;
      margin-bottom: 0px;
      padding-left: 0px;
    }
    .container {
      position: relative;
      z-index: 1;
      background: #fff;
      padding: 30px;
      border-radius: 4px;
      // min-height: 500px;
      .subBox {
        padding-top: 30px;
        text-align: center;
        border-top: solid 1px $gray-5;
      }
    }
    .idNumber {
      margin-bottom: 39px;
    }

    .el-form-item {
      margin-bottom: 29px;
    }
    .el-input {
      width: 293px;
    }
    .role-checkbox-group {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
      width: 520px;
    }
    .role-checkbox-item {
      margin-right: 0;
    }
  }
}
</style>
