<template>
  <div class="login-container">
    <div class="login-box">
      <h2>登录</h2>
      <el-form :model="form" @submit.prevent="login" class="login-form">
        <el-form-item>
          <el-input
            v-model="form.username"
            placeholder="用户名或邮箱"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            clearable
            @keyup.enter="login"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login" style="width: 100%">
            登录
          </el-button>
        </el-form-item>
        <div class="login-link">
          没有账户？<router-link to="/register">注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const store = useStore()

const form = reactive({
  username: '',
  password: ''
})

const login = async () => {
  if (!form.username || !form.password) {
    ElMessage.error('请输入用户名和密码')
    return
  }

  try {
    const response = await request.post('/auth/login', {
      username: form.username,
      password: form.password
    })

    console.log('登录响应:', response)

    // response 是 ApiResponse 对象: { code, message, data: { token, user } }
    if (response.code === 200 && response.data) {
      store.commit('setToken', response.data.token)
      store.commit('setUser', response.data.user)
      ElMessage.success('登录成功')
      router.push('/chat')
    } else {
      ElMessage.error(response.message || '登录失败')
    }
  } catch (error) {
    console.error('登录错误:', error)
    let errorMsg = '登录失败'

    if (error.message) {
      errorMsg = error.message
    } else if (error.message) {
      errorMsg = error.message
    } else if (typeof error === 'object' && error.message) {
      errorMsg = error.message
    }

    ElMessage.error(errorMsg)
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 350px;
}

.login-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.login-form {
  margin-bottom: 20px;
}

.login-link {
  text-align: center;
  font-size: 14px;
}

.login-link a {
  color: #667eea;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
