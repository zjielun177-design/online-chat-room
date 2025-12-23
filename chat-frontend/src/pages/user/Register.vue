<template>
  <div class="register-container">
    <div class="register-box">
      <h2>注册</h2>
      <el-form :model="form" @submit.prevent="register" class="register-form">
        <el-form-item>
          <el-input
            v-model="form.username"
            placeholder="用户名"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.email"
            placeholder="邮箱"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="确认密码"
            clearable
            @keyup.enter="register"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="register" style="width: 100%">
            注册
          </el-button>
        </el-form-item>
        <div class="register-link">
          已有账户？<router-link to="/login">登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const register = async () => {
  if (!form.username || !form.email || !form.password) {
    ElMessage.error('请填写所有字段')
    return
  }

  if (form.password !== form.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  if (form.password.length < 6) {
    ElMessage.error('密码长度不能少于6位')
    return
  }

  try {
    const response = await request.post('/auth/register', {
      username: form.username,
      email: form.email,
      password: form.password
    })

    console.log('注册响应:', response)

    // response 是 ApiResponse 对象: { code, message, data: { token, user } }
    if (response.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(response.message || '注册失败')
    }
  } catch (error) {
    console.error('注册错误:', error)
    let errorMsg = '注册失败'

    if (error.message) {
      errorMsg = error.message
    } else if (typeof error === 'object' && error.message) {
      errorMsg = error.message
    }

    ElMessage.error(errorMsg)
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-box {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 350px;
}

.register-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.register-form {
  margin-bottom: 20px;
}

.register-link {
  text-align: center;
  font-size: 14px;
}

.register-link a {
  color: #667eea;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>
