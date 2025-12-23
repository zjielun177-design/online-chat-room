<template>
  <el-dialog
    title="编辑资料"
    :model-value="modelValue"
    width="420px"
    destroy-on-close
    @close="handleClose"
  >
    <el-form :model="form" label-position="top" label-width="80px">
      <el-form-item label="邮箱">
        <el-input v-model="form.email" placeholder="输入新的邮箱" clearable />
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="form.nickname" placeholder="设置昵称" clearable />
      </el-form-item>
      <el-form-item label="头像">
        <el-input v-model="form.avatar" placeholder="头像地址（可选）" clearable />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input
          v-model="form.newPassword"
          type="password"
          placeholder="留空则不修改密码"
          clearable
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const props = defineProps({
  modelValue: Boolean,
  user: {
    type: Object,
    default: () => ({})
  }
})
const emit = defineEmits(['update:modelValue', 'saved'])

const form = reactive({
  email: '',
  nickname: '',
  avatar: '',
  newPassword: ''
})

const resetForm = (source) => {
  form.email = source?.email || ''
  form.nickname = source?.nickname || ''
  form.avatar = source?.avatar || ''
  form.newPassword = ''
}

watch(
  () => props.user,
  (next) => {
    if (props.modelValue) {
      resetForm(next)
    }
  }
)

watch(
  () => props.modelValue,
  (visible) => {
    if (visible) {
      resetForm(props.user)
    }
  }
)

const handleClose = () => {
  emit('update:modelValue', false)
}

const handleSave = async () => {
  try {
    const response = await request.put('/auth/profile', {
      email: form.email,
      nickname: form.nickname,
      avatar: form.avatar,
      newPassword: form.newPassword
    })
    emit('saved', response?.data || null)
    emit('update:modelValue', false)
    ElMessage.success('资料更新成功')
  } catch (error) {
    const message = error?.message || error?.msg || '资料更新失败'
    ElMessage.error(message)
  }
}
</script>
