<template>
  <div class="chat-container">
    <div class="chat-sidebar">
      <div class="user-info">
        <div class="avatar">{{ user?.username?.[0].toUpperCase() }}</div>
        <div class="info">
          <p class="username">{{ user?.username }}</p>
          <button @click="logout" class="logout-btn">登出</button>
        </div>
      </div>
      <div class="channels">
        <h3>频道</h3>
        <div class="channel-list">
          <div
            v-for="channel in channels"
            :key="channel.id"
            :class="['channel-item', { active: currentChannel?.id === channel.id }]"
            @click="selectChannel(channel)"
          >
            # {{ channel.name }}
          </div>
        </div>
      </div>
    </div>

    <div class="chat-main">
      <div class="chat-header">
        <h2>{{ currentChannel?.name || '选择频道' }}</h2>
      </div>
      <div class="chat-messages">
        <div
          v-for="msg in messages"
          :key="msg.id"
          :class="['message', { own: msg.senderId === user?.id }]"
        >
          <div class="message-header">
            <span class="author">{{ msg.senderName }}</span>
            <span class="time">{{ formatTime(msg.sendTime) }}</span>
          </div>
          <div class="message-content">{{ msg.content }}</div>
        </div>
      </div>
      <div class="chat-input">
        <el-input
          v-model="inputMessage"
          placeholder="输入消息..."
          @keyup.enter="sendMessage"
          clearable
        />
        <button @click="sendMessage" class="send-btn">发送</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import websocket from '../../utils/websocket'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const store = useStore()

const inputMessage = ref('')
const channels = computed(() => store.state.channels)
const currentChannel = computed(() => store.state.currentChannel)
const messages = computed(() => store.state.messages)
const user = computed(() => store.state.user)

const selectChannel = (channel) => {
  store.commit('setCurrentChannel', channel)
  loadMessages(channel.id)
}

const loadMessages = async (channelId) => {
  try {
    const response = await request.get(`/message/channel/${channelId}`)
    // response 是 ApiResponse 对象: { code, message, data: [...] }
    console.log('消息API响应:', response)
    store.commit('setMessages', response.data || [])
  } catch (error) {
    console.error('加载消息失败:', error)
    ElMessage.error('加载消息失败')
  }
}

const sendMessage = () => {
  if (!inputMessage.value.trim()) {
    return
  }

  if (!currentChannel.value) {
    ElMessage.error('请选择频道')
    return
  }

  const message = {
    type: 'group_message',
    channelId: currentChannel.value.id,
    content: inputMessage.value
  }

  websocket.send(message)
  inputMessage.value = ''
}

const logout = async () => {
  try {
    await request.post('/auth/logout')
  } catch (error) {
    console.error('登出错误:', error)
  }

  store.commit('setToken', null)
  store.commit('setUser', null)
  websocket.close()
  router.push('/login')
}

const formatTime = (time) => {
  if (!time) return ''
  try {
    const date = new Date(time)
    return date.toLocaleTimeString('zh-CN', {
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (e) {
    return time
  }
}

const loadChannels = async () => {
  try {
    console.log('开始加载频道...');
    const response = await request.get('/channel/list')
    // response 是 ApiResponse 对象: { code, message, data: [...] }
    console.log('频道API原始响应:', response)
    console.log('频道API响应类型:', typeof response)
    console.log('是否有 data 字段:', 'data' in response)
    const channelList = response.data || []
    console.log('提取的频道列表:', channelList)
    store.commit('setChannels', channelList)
    console.log('Vuex 中的频道已更新:', store.state.channels)
    // 如果有频道，自动选择第一个
    if (channelList && channelList.length > 0) {
      console.log('自动选择第一个频道:', channelList[0].name)
      selectChannel(channelList[0])
    } else {
      console.warn('频道列表为空!')
    }
  } catch (error) {
    console.error('加载频道失败:', error)
    console.error('错误详情:', error.message)
    ElMessage.error('加载频道失败')
  }
}

// 设置WebSocket消息处理器
websocket.onMessage = (message) => {
  console.log('收到WebSocket消息:', message)
  if (message.type === 'group_message' && message.channelId === currentChannel.value?.id) {
    store.commit('addMessage', {
      id: message.id,
      senderId: message.senderId,
      senderName: message.senderName,
      content: message.content,
      sendTime: message.sendTime,
      channelId: message.channelId
    })
  }
}

onMounted(() => {
  if (!user.value || !store.state.token) {
    router.push('/login')
    return
  }

  loadChannels()
  websocket.connect(store.state.token)
})

onUnmounted(() => {
  // 保持连接，只在登出时关闭
})
</script>

<style scoped>
.chat-container {
  display: flex;
  height: 100vh;
  background: #f5f5f5;
}

.chat-sidebar {
  width: 250px;
  background: #fff;
  border-right: 1px solid #ddd;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.user-info {
  padding: 15px;
  border-bottom: 1px solid #ddd;
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #667eea;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.info {
  flex: 1;
}

.username {
  margin: 0;
  font-weight: bold;
  color: #333;
}

.logout-btn {
  padding: 4px 8px;
  background: #f56c6c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  width: 100%;
  margin-top: 5px;
}

.logout-btn:hover {
  background: #f78989;
}

.channels {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

.channels h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 14px;
}

.channel-list {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.channel-item {
  padding: 8px 10px;
  cursor: pointer;
  border-radius: 4px;
  color: #666;
  transition: all 0.3s;
}

.channel-item:hover {
  background: #f0f0f0;
}

.channel-item.active {
  background: #667eea;
  color: white;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 20px;
  border-bottom: 1px solid #ddd;
  background: white;
}

.chat-header h2 {
  margin: 0;
  color: #333;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.message {
  background: white;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 10px;
}

.message.own {
  align-self: flex-end;
  background: #667eea;
  color: white;
  max-width: 70%;
}

.message-header {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  margin-bottom: 5px;
  color: #999;
}

.message.own .message-header {
  color: rgba(255, 255, 255, 0.7);
}

.message-content {
  word-wrap: break-word;
}

.chat-input {
  padding: 20px;
  background: white;
  border-top: 1px solid #ddd;
  display: flex;
  gap: 10px;
}

.send-btn {
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.send-btn:hover {
  background: #5568d3;
}
</style>
