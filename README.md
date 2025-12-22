# online-chat-room
# 网络聊天平台

## 项目简介

该项目是一个基于 Vue3 和 Spring Boot 构建的网络聊天平台，用户可以通过该平台进行群聊和私聊。所有的聊天记录和用户数据都保存在数据库中，私聊和群聊功能均通过 WebSocket 实现。

## 功能

- 用户注册、登录、登出
- 用户信息编辑
- 选择聊天室并进入
- 群聊功能
- 私聊功能
- 聊天记录查看
- WebSocket 实时通信

## 技术栈

- 前端：Vue3, Vuex, Vue Router, WebSocket
- 后端：Spring Boot, Spring Security, WebSocket
- 数据库：MySQL, Hibernate/JPA
- 测试：JUnit, Selenium

## 安装与运行

### 前端部分

1. 克隆仓库：
    ```bash
    git clone https://github.com/your-username/chat-platform.git
    cd frontend
    ```

2. 安装依赖：
    ```bash
    npm install
    ```

3. 启动开发服务器：
    ```bash
    npm run serve
    ```

### 后端部分

1. 克隆仓库：
    ```bash
    git clone https://github.com/your-username/chat-platform.git
    cd backend
    ```

2. 配置数据库：
   - 修改 `application.properties` 配置数据库连接。

3. 运行 Spring Boot 应用：
    ```bash
    mvn spring-boot:run
    ```

## 项目结构

```bash
chat-platform/
├── backend/                     # 后端代码
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   ├── com/
│   │   │   │   │   ├── chat/
│   │   │   │   │   │   ├── controller/   # 后端控制器
│   │   │   │   │   │   ├── service/       # 后端服务层
│   │   │   │   │   │   └── model/         # 数据模型
│   ├── resources/
│   └── pom.xml
├── frontend/                    # 前端代码
│   ├── src/
│   │   ├── components/           # 公共组件（例如聊天框、输入框等）
│   │   ├── pages/                # 页面组件（注册、登录、聊天等）
│   │   ├── store/                # 状态管理
│   │   └── App.vue               # 根组件
│   └── package.json
├── .gitignore                   # Git 忽略文件
├── README.md                    # 项目说明文档
└── LICENSE                      # 开源许可证

