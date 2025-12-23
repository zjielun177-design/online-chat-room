<template>
  <div class="chat-layout">
    <div class="chat-sidebar">
      <div class="sidebar-header">
        <div class="avatar">{{ avatarInitial }}</div>
        <div class="user-info">
          <p class="display-name">{{ displayName }}</p>
          <p class="user-email" v-if="user?.email">{{ user.email }}</p>
          <div class="actions">
            <el-button type="text" size="small" @click="profileDialogVisible = true">
              编辑资料
            </el-button>
            <el-button type="text" size="small" class="logout-link" @click="logout">
              登出
            </el-button>
          </div>
        </div>
      </div>

      <div class="channel-section">
        <h3>频道</h3>
        <div class="channel-list">
          <div
            v-for="channel in channels"
            :key="channel.id"
            :class="['channel-item', { active: currentChannel?.id === channel.id }]"
            @click="selectChannel(channel)"
          >
            <div># {{ channel.name }}</div>
            <span class="member-count">{{ channel.memberCount || 0 }} 人</span>
          </div>
        </div>
      </div>

      <div class="members-section" v-if="channelMembers.length">
        <h3>频道成员</h3>
        <div class="member-list">
          <div
            v-for="member in channelMembers"
            :key="member.id"
            :class="[
              'member-item',
              {
                active:
                  activeConversationType === 'private' && privatePeer?.id === member.id
              }
            ]"
            @click="startPrivateChat(member)"
          >
            <div class="member-info">
              <span class="member-name">{{ member.nickname || member.username }}</span>
              <span class="member-meta" v-if="member.id === user?.id">(我)</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="chat-main">
      <div class="chat-header">
        <div>
          <h2 v-if="activeConversationType === 'group'">
            {{ currentChannel?.name || '请选择频道' }}
          </h2>
          <h2 v-else>与 {{ privatePeerDisplayName }} 私聊</h2>
          <p
            class="subline"
            v-if="activeConversationType === 'group' && currentChannel?.description"
          >
            {{ currentChannel.description }}
          </p>
        </div>
        <el-button type="text" size="small" @click="historyDialogVisible = true">
          聊天记录
        </el-button>
      </div>

      <div class="chat-messages">
        <div
          v-for="msg in messages"
          :key="msg.id"
          :class="['message', { own: msg.senderId === user?.id }]"
        >
          <div class="message-header">
            <span class="author">{{ msg.senderName || '未知用户' }}</span>
            <span class="time">{{ formatTime(msg.sendTime) }}</span>
          </div>
          <div class="message-content">{{ msg.content }}</div>
        </div>
      </div>

      <div class="chat-input">
        <el-input
          v-model="inputMessage"
          :placeholder="activeConversationType === 'group' ? '在当前频道发言...' : '在私聊中输入...'"
          clearable
          @keyup.enter="sendMessage"
        />
        <button class="send-btn" @click="sendMessage">发送</button>
      </div>
    </div>

    <ProfileDialog
      :model-value="profileDialogVisible"
      :user="user"
      @update:modelValue="profileDialogVisible = $event"
      @saved="handleProfileSaved"
    />
    <HistoryDialog
      :model-value="historyDialogVisible"
      :type="activeConversationType"
      :channel-id="currentChannel?.id"
      :peer-id="privatePeer?.id"
      :channel-name="currentChannel?.name"
      :peer-name="privatePeerDisplayName"
      @update:modelValue="historyDialogVisible = $event"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import websocket from '../../utils/websocket'
import request from '../../utils/request'
import ProfileDialog from './ProfileDialog.vue'
import HistoryDialog from './HistoryDialog.vue'

const router = useRouter()
const store = useStore()

const profileDialogVisible = ref(false)
const historyDialogVisible = ref(false)
const inputMessage = ref('')

const user = computed(() => store.state.user)
const channels = computed(() => store.state.channels)
const currentChannel = computed(() => store.state.currentChannel)
const messages = computed(() => store.state.messages)
const activeConversationType = ref('group')
const privatePeer = ref(null)
const channelMembers = ref([])
const privatePeerDisplayName = computed(() => {
  const target = privatePeer.value
  if (!target) return ''
  const nickname = target.nickname?.trim()
  return nickname || target.username || '未知用户'
})

const displayName = computed(() => {
  const target = user.value
  if (!target) return '未登录用户'
  const nickname = target.nickname?.trim()
  return nickname || target.username || '未知用户'
})

const avatarInitial = computed(() => {
  const next = displayName.value.trim()
  return next ? next[0].toUpperCase() : 'U'
})

const selectChannel = async (channel) => {
  if (!channel) return
  activeConversationType.value = 'group'
  privatePeer.value = null
  store.commit('setCurrentChannel', channel)
  await loadMessages(channel.id)
  await loadChannelMembers(channel.id)
}

const loadMessages = async (channelId) => {
  try {
    const response = await request.get(`/message/channel/${channelId}`)
    store.commit('setMessages', response?.data || [])
  } catch (error) {
    console.error('加载频道消息失败', error)
    ElMessage.error('加载消息失败，请稍候再试')
  }
}

const loadChannelMembers = async (channelId) => {
  if (!channelId) {
    channelMembers.value = []
    return
  }
  try {
    const response = await request.get(`/channel/${channelId}/members-detail`)
    channelMembers.value = response?.data || []
  } catch (error) {
    console.error('加载频道成员失败', error)
    channelMembers.value = []
  }
}

const loadChannels = async () => {
  try {
    const response = await request.get('/channel/list')
    const list = response?.data || []
    store.commit('setChannels', list)
    if (list.length > 0) {
      await selectChannel(list[0])
    } else {
      store.commit('setCurrentChannel', null)
      store.commit('setMessages', [])
      channelMembers.value = []
    }
  } catch (error) {
    console.error('加载频道列表失败', error)
    ElMessage.error('频道加载失败，请刷新重试')
  }
}

const loadPrivateMessages = async (peerId) => {
  try {
    const response = await request.get(`/message/private/${peerId}`)
    store.commit('setMessages', response?.data || [])
  } catch (error) {
    console.error('加载私聊消息失败', error)
    ElMessage.error('私聊记录加载失败')
    store.commit('setMessages', [])
  }
}

const startPrivateChat = async (member) => {
  if (!member || member.id === user.value?.id) {
    return
  }
  activeConversationType.value = 'private'
  privatePeer.value = member
  store.commit('setMessages', [])
  await loadPrivateMessages(member.id)
}

const sendMessage = () => {
  if (!inputMessage.value.trim()) {
    return
  }
  if (!websocket.isConnected()) {
    ElMessage.error('正在连接中，稍后再试')
    return
  }

  const content = inputMessage.value.trim()

  if (activeConversationType.value === 'private') {
    if (!privatePeer.value) {
      ElMessage.error('请选择私聊对象')
      return
    }
    websocket.send({
      type: 'private_message',
      receiverId: privatePeer.value.id,
      content
    })
    inputMessage.value = ''
    return
  }

  if (!currentChannel.value) {
    ElMessage.error('请选择频道')
    return
  }

  websocket.send({
    type: 'group_message',
    channelId: currentChannel.value.id,
    content
  })
  inputMessage.value = ''
}

const logout = async () => {
  try {
    await request.post('/auth/logout')
  } catch (error) {
    console.error('登出异常', error)
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
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  } catch (err) {
    return time
  }
}

const handleProfileSaved = (updatedUser) => {
  if (updatedUser) {
    store.commit('setUser', updatedUser)
  }
}

websocket.onMessage = (message) => {
  if (
    message.type === 'group_message' &&
    activeConversationType.value === 'group' &&
    message.channelId === currentChannel.value?.id
  ) {
    store.commit('addMessage', {
      id: message.id,
      senderId: message.senderId,
      senderName: message.senderName || '未知用户',
      content: message.content,
      sendTime: message.sendTime,
      channelId: message.channelId
    })
    return
  }

  if (message.type === 'private_message' && activeConversationType.value === 'private' && privatePeer.value) {
    const peerId = privatePeer.value.id
    if (message.senderId === peerId || message.receiverId === peerId) {
      store.commit('addMessage', {
        id: message.id,
        senderId: message.senderId,
        senderName: message.senderName || '未知用户',
        content: message.content,
        sendTime: message.sendTime,
        receiverId: message.receiverId
      })
    }
  }
}

onMounted(() => {
  if (!store.state.token || !user.value) {
    router.push('/login')
    return
  }

  loadChannels()
  websocket.connect(store.state.token)
})
</script>

<style scoped>
.chat-layout {
  display: flex;
  height: 100vh;
  background: #f5f5f5;
}

.chat-sidebar {
  width: 260px;
  background: #fff;
  border-right: 1px solid #e6e6e6;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-header {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-bottom: 1px solid #e6e6e6;
  align-items: center;
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #667eea;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 18px;
}

.user-info {
  flex: 1;
}

.display-name {
  margin: 0;
  font-weight: bold;
}

.user-email {
  margin: 2px 0 6px;
  font-size: 12px;
  color: #999;
}

.actions {
  display: flex;
  gap: 4px;
}

.logout-link {
  color: #f56c6c;
}

.channel-section {
  padding: 12px 16px;
}

.channel-section h3 {
  margin: 0 0 12px;
  font-size: 14px;
  color: #555;
}

.channel-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.channel-item {
  padding: 10px 12px;
  border-radius: 6px;
  background: #fafafa;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.channel-item.active {
  background: #667eea;
  color: #fff;
}

.member-count {
  font-size: 12px;
  color: inherit;
}

.members-section {
  padding: 0 16px 12px;
}

.members-section h3 {
  margin: 0 0 10px;
  font-size: 14px;
  color: #555;
}

.member-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.member-item {
  padding: 8px 10px;
  border-radius: 6px;
  background: #f8f8f8;
  cursor: pointer;
  transition: background 0.2s;
}

.member-item.active {
  background: #e1eaff;
}

.member-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.member-meta {
  color: #999;
  font-size: 12px;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-header {
  background: #fff;
  padding: 20px;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.chat-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.subline {
  margin: 4px 0 0;
  font-size: 12px;
  color: #888;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: #fefefe;
}

.message {
  padding: 12px 14px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.message.own {
  align-self: flex-end;
  background: #667eea;
  color: white;
}

.message-header {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
  margin-bottom: 6px;
}

.message.own .message-header {
  color: rgba(255, 255, 255, 0.8);
}

.message-content {
  word-break: break-word;
}

.chat-input {
  padding: 16px;
  background: #fff;
  border-top: 1px solid #e6e6e6;
  display: flex;
  gap: 12px;
}

.send-btn {
  min-width: 100px;
  border: none;
  border-radius: 6px;
  background: #667eea;
  color: white;
  padding: 10px 16px;
  cursor: pointer;
}

.send-btn:hover {
  background: #5568d3;
}
</style>
