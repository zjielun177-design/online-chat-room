class WebSocketUtil {
  constructor() {
    this.ws = null
    this.url = `ws://${window.location.hostname}:8080/api/ws/chat`
    this.reconnectAttempts = 0
    this.reconnectDelay = 1000
  }

  connect(token) {
    if (this.ws) {
      return
    }

    try {
      this.ws = new WebSocket(`${this.url}?token=${token}`)

      this.ws.onopen = () => {
        console.log('WebSocket连接成功')
        this.reconnectAttempts = 0
      }

      this.ws.onmessage = (event) => {
        console.log('收到消息:', event.data)
        const message = JSON.parse(event.data)
        this.onMessage?.(message)
      }

      this.ws.onerror = (error) => {
        console.error('WebSocket错误:', error)
        this.onError?.(error)
      }

      this.ws.onclose = () => {
        console.log('WebSocket连接关闭')
        this.ws = null
        this.reconnect(token)
      }
    } catch (error) {
      console.error('创建WebSocket连接失败:', error)
      this.reconnect(token)
    }
  }

  reconnect(token) {
    if (this.reconnectAttempts < 5) {
      this.reconnectAttempts++
      setTimeout(() => {
        console.log(`正在尝试重新连接... (${this.reconnectAttempts}/5)`)
        this.connect(token)
      }, this.reconnectDelay * this.reconnectAttempts)
    }
  }

  send(message) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(message))
    } else {
      console.warn('WebSocket未连接')
    }
  }

  close() {
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
  }

  isConnected() {
    return this.ws && this.ws.readyState === WebSocket.OPEN
  }
}

export default new WebSocketUtil()
