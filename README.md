# 网络聊天平台 - Online Chat Room

完整的实时聊天应用，使用Vue3 + Spring Boot + MySQL + WebSocket实现。

## 项目完成状态

✅ **已完成功能：**
- 用户注册和登录（JWT认证）
- 频道管理（创建、加入、离开、删除频道）
- 实时群组消息（WebSocket）
- 私聊消息（WebSocket）
- 消息历史查询（REST API）
- 用户认证和授权
- 全局异常处理

## 快速开始

### 前置要求

- JDK 19+
- Maven 3.6+
- MySQL 8.0+
- Node.js 16+ (用于前端开发)

### 1. 数据库配置

```bash
# 创建数据库
mysql -u root -p < chat-backend/src/main/resources/db/schema.sql

# 初始化测试数据
mysql -u root -p chat < chat-backend/src/main/resources/db/data.sql
```

### 2. 启动后端

```bash
cd chat-backend

# 方式1：直接运行
mvn spring-boot:run

# 方式2：打包后运行
mvn clean package
java -jar target/chat-backend-1.0.0.jar
```

后端将在 `http://localhost:8080` 启动。

### 3. 启动前端

```bash
cd chat-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端将在 `http://localhost:5173` 启动。

### 4. 测试账户

使用数据库初始化脚本的测试账户：
- **用户名**：admin 或 user1 或 user2
- **密码**：test123

## API 接口文档

### 认证相关

#### 注册
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "newuser",
  "email": "user@example.com",
  "password": "password123"
}
```

#### 登录
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "test123"
}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "nickname": "Administrator",
      "avatar": null
    }
  }
}
```

#### 登出
```
POST /api/auth/logout
Authorization: Bearer {token}
```

### 频道管理

#### 获取所有频道
```
GET /api/channel/list
```

#### 获取用户加入的频道
```
GET /api/channel/my-channels
Authorization: Bearer {token}
```

#### 创建频道
```
POST /api/channel
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "general",
  "description": "General discussion"
}
```

#### 加入频道
```
POST /api/channel/{channelId}/join
Authorization: Bearer {token}
```

#### 离开频道
```
POST /api/channel/{channelId}/leave
Authorization: Bearer {token}
```

#### 获取频道成员
```
GET /api/channel/{channelId}/members
```

### 消息管理

#### 获取频道消息
```
GET /api/message/channel/{channelId}?limit=50
```

#### 获取私聊消息
```
GET /api/message/private/{userId}?limit=50
Authorization: Bearer {token}
```

#### 发送消息（REST备用接口）
```
POST /api/message
Authorization: Bearer {token}
Content-Type: application/json

{
  "type": "group",
  "channelId": 1,
  "content": "Hello everyone!"
}
```

或私聊：
```
{
  "type": "private",
  "receiverId": 2,
  "content": "Hello user!"
}
```

### WebSocket 实时通信

#### 连接
```
ws://localhost:8080/api/ws/chat?token={JWT_TOKEN}
```

#### 发送群组消息
```json
{
  "type": "group_message",
  "channelId": 1,
  "content": "Hello everyone!"
}
```

#### 发送私聊消息
```json
{
  "type": "private_message",
  "receiverId": 2,
  "content": "Hello user!"
}
```

#### 心跳包（保持连接）
```json
{
  "type": "ping"
}
```

## 项目结构

```
chat-backend/
├── src/main/java/com/chat/
│   ├── controller/        # REST 控制器
│   ├── service/           # 业务逻辑层
│   ├── repository/        # 数据访问层
│   ├── entity/            # JPA 实体
│   ├── dto/               # 数据传输对象
│   ├── websocket/         # WebSocket 处理
│   ├── filter/            # JWT 认证过滤器
│   ├── exception/         # 异常处理
│   ├── util/              # 工具类
│   └── config/            # 配置类

chat-frontend/
├── src/
│   ├── pages/             # 页面组件
│   ├── router/            # 路由配置
│   ├── store/             # Vuex 状态管理
│   ├── utils/             # 工具函数
│   ├── styles/            # 全局样式
│   ├── App.vue            # 根组件
│   └── main.js            # 入口文件
```

## 技术栈

### 后端
- Spring Boot 3.2.5
- Spring Data JPA
- Spring WebSocket
- MySQL 8.0
- JWT (JJWT 0.12.3)
- FastJSON2
- Lombok

### 前端
- Vue 3.4+
- Vite 5
- Vue Router 4.2
- Vuex 4.1
- Axios 1.6
- Element Plus 2.5

## 核心功能实现

### 1. JWT认证
- TokenUtil: 生成和验证JWT令牌
- JwtTokenFilter: 请求过滤和令牌验证
- 令牌有效期：7天

### 2. WebSocket 实时通信
- 自动连接管理和重连机制
- 心跳包保活
- 群组消息广播
- 私聊消息直达

### 3. 数据库设计
- TUser: 用户表
- TChannel: 频道表
- TUserChannel: 用户-频道关联表
- TGroupMsg: 群组消息表
- TPrivateMsg: 私聊消息表

## 常见问题排查

### WebSocket 连接失败
- 检查后端是否正常运行
- 确认JWT令牌有效
- 检查浏览器开发工具的 Network 标签

### 消息发送失败
- 确认用户已加入该频道（群组消息）
- 检查接收方用户ID是否正确（私聊）
- 查看浏览器控制台的错误信息

### 数据库连接错误
- 检查 application.yml 中的数据库配置
- 确认MySQL服务正常运行
- 验证数据库用户名密码

## 许可证

MIT License
