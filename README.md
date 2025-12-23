# 网络聊天平台需求文档

## 功能需求

### 1. 用户管理
- **注册功能**：用户可以使用邮箱、用户名和密码注册账户。
- **登录功能**：用户通过邮箱/用户名和密码登录系统。
- **登出功能**：用户可以登出，结束当前会话。
- **编辑个人资料**：用户可以编辑自己的个人信息，如头像、昵称等。

### 2. 聊天管理
- **聊天室选择**：用户可以选择加入不同的聊天室。
- **群聊功能**：用户可以在聊天室内与其他用户进行实时群聊。
- **私聊功能**：用户可以与聊天室内的在线用户进行私聊。
- **消息发送与接收**：使用 WebSocket 实现实时消息传递，群聊与私聊分开管理。

### 3. 聊天记录
- **聊天记录存储**：所有的群聊和私聊消息都会存储在数据库中。
- **查看聊天记录**：用户可以查看自己的聊天记录，包括群聊和私聊。
  
## 技术需求

- **前端**：Vue3，使用 Vue Router 实现页面跳转，Vuex 管理状态。
- **后端**：Spring Boot 2.6，JDK 19.0.2，使用 MySQL 连接数据库。
- **数据库**：MySQL 8.x，设计合适的表结构。
- **WebSocket**：使用 Spring Boot WebSocket 实现实时通信。
- **依赖**：Spring Boot Starter, WebSocket, MySQL Driver, JPA/Hibernate。

## 数据库设计

1. **用户表** (`users`)
   - id: 主键，自增
   - username: 唯一
   - email: 唯一
   - password: 加密密码
   - nickname: 昵称
   - avatar: 头像URL

2. **聊天室表** (`chat_rooms`)
   - id: 主键，自增
   - name: 聊天室名称
   - description: 聊天室描述

3. **聊天室成员表** (`chat_room_members`)
   - user_id: 外键，关联用户表
   - chat_room_id: 外键，关联聊天室表

4. **消息表** (`messages`)
   - id: 主键，自增
   - sender_id: 外键，关联用户表
   - receiver_id: 外键，关联用户表（私聊时使用）
   - chat_room_id: 外键，关联聊天室表（群聊时使用）
   - message: 消息内容
   - timestamp: 发送时间

## 系统架构
1. **前端**：用户界面，采用 Vue3，使用 WebSocket 与后端实时通信。
2. **后端**：Spring Boot，负责用户管理、聊天室管理、消息推送等。
3. **数据库**：MySQL 存储用户数据、聊天记录等信息。

## 安全与性能
- 使用 JWT 或 Session 管理用户会话。
- 对敏感信息（如密码）进行加密。
- 对聊天记录进行分页查询，避免数据库查询过于耗时。

## 任务分工
1. 人员1：前端 - 用户注册、登录、登出模块
2. 人员2：前端 - 聊天界面与消息显示
3. 人员3：后端 - 用户管理与数据存储
4. 人员4：后端 - 聊天记录与 WebSocket 实现
5. 人员5：全栈 - 数据库设计与配置管理

## 开发工具与环境
- **IDE**：IntelliJ IDEA 或 VS Code
- **JDK**：19.0.2
- **数据库**：MySQL 8.x
- **前端**：Vue3，Vite，Vue Router，Vuex
- **后端**：Spring Boot 2.6，Spring Security，Spring WebSocket
- **版本控制**：Git，GitHub
