<template>
  <el-dialog
    title="聊天记录"
    :model-value="modelValue"
    width="460px"
    destroy-on-close
    @close="handleClose"
  >
    <div class="history-meta">
      <span v-if="props.type === 'group'">
        当前频道：{{ props.channelName || '未选择' }}
      </span>
      <span v-else>
        私聊对象：{{ props.peerName || '未选择' }}
      </span>
    </div>
    <el-scrollbar class="history-list">
      <div v-if="loading" class="history-loading">加载中…</div>
      <div v-else-if="history.length === 0" class="history-empty">
        暂无消息记录
      </div>
      <div v-else class="history-row" v-for="record in history" :key="record.id">
        <div class="record-header">
          <span class="sender">{{ record.senderName || '未知用户' }}</span>
          <span class="time">{{ formatTime(record.sendTime) }}</span>
        </div>
        <div class="record-content">{{ record.content }}</div>
      </div>
    </el-scrollbar>
    <template #footer>
      <el-button type="primary" @click="handleClose">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const props = defineProps({
  modelValue: Boolean,
  type: {
    type: String,
    default: 'group'
  },
  channelId: Number,
  peerId: Number,
  channelName: String,
  peerName: String
})
const emit = defineEmits(['update:modelValue'])

const history = ref([])
const loading = ref(false)

const fetchHistory = async () => {
  if (!props.modelValue) {
    return
  }
  loading.value = true
  try {
    let response
    if (props.type === 'group') {
      if (!props.channelId) {
        history.value = []
        return
      }
      response = await request.get(`/message/channel/${props.channelId}?limit=200`)
    } else {
      if (!props.peerId) {
        history.value = []
        return
      }
      response = await request.get(`/message/private/${props.peerId}?limit=200`)
    }
    history.value = response?.data || []
  } catch (error) {
    console.error('加载记录失败', error)
    ElMessage.error('加载记录失败')
  } finally {
    loading.value = false
  }
}

watch(
  () => props.modelValue,
  (visible) => {
    if (visible) {
      fetchHistory()
    }
  }
)

watch(
  () => [props.channelId, props.peerId, props.type],
  () => {
    if (props.modelValue) {
      fetchHistory()
    }
  }
)

const formatTime = (value) => {
  if (!value) return ''
  try {
    const date = new Date(value)
    return date.toLocaleString('zh-CN', { hour12: false })
  } catch (err) {
    return value
  }
}

const handleClose = () => {
  emit('update:modelValue', false)
  history.value = []
}
</script>

<style scoped>
.history-meta {
  margin-bottom: 12px;
  color: #666;
  font-size: 14px;
}

.history-list {
  max-height: 320px;
  padding: 4px 0;
}

.history-loading,
.history-empty {
  text-align: center;
  color: #999;
  padding: 40px 0;
}

.history-row {
  padding: 10px 12px;
  border-bottom: 1px solid #f2f2f2;
}

.record-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
  font-size: 12px;
  color: #999;
}

.sender {
  font-weight: bold;
  color: #333;
}

.record-content {
  word-break: break-word;
  color: #333;
}
</style>
