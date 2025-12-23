import { createStore } from 'vuex'

export default createStore({
  state: {
    user: null,
    token: localStorage.getItem('token') || null,
    currentChannel: null,
    channels: [],
    messages: [],
    onlineUsers: []
  },

  mutations: {
    setUser(state, user) {
      state.user = user
    },
    setToken(state, token) {
      state.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },
    setCurrentChannel(state, channel) {
      state.currentChannel = channel
    },
    setChannels(state, channels) {
      state.channels = channels
    },
    setMessages(state, messages) {
      state.messages = messages
    },
    addMessage(state, message) {
      state.messages.push(message)
    },
    setOnlineUsers(state, users) {
      state.onlineUsers = users
    }
  },

  actions: {
    login({ commit }, { user, token }) {
      commit('setUser', user)
      commit('setToken', token)
    },
    logout({ commit }) {
      commit('setUser', null)
      commit('setToken', null)
    }
  },

  getters: {
    isLoggedIn: state => !!state.token
  }
})
