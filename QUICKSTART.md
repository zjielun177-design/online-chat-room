# 快速启动指南

## 项目结构
- `chat-backend/` - Spring Boot 后端项目
- `chat-frontend/` - Vue3 + Vite 前端项目

## 前置条件
- JDK 19+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

## 后端启动

### 1. 初始化数据库
参考 `chat-backend/DATABASE_SETUP.md`

### 2. 修改数据库配置
编辑 `chat-backend/src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/chat?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root  # 改为你的用户名
    password: root  # 改为你的密码
```

### 3. 启动后端
```bash
cd chat-backend
mvn clean install
mvn spring-boot:run
```

后端会运行在 `http://localhost:8080`

## 前端启动

### 1. 安装依赖
```bash
cd chat-frontend
npm install
```

### 2. 启动开发服务器
```bash
npm run dev
```

前端会运行在 `http://localhost:5173`

### 3. 构建生产版本
```bash
npm run build
```

## 测试登录

1. 打开浏览器访问 `http://localhost:5173`
2. 登录页面会出现，使用以下凭证：
   - 用户名：`admin`
   - 密码：`test123`

## API 端点

### 认证相关
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录

### 响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@chat.com",
      "nickname": "管理员",
      "avatar": null
    }
  }
}
```

## 常见命令

### 后端
```bash
# 编译
mvn clean compile

# 运行测试
mvn test

# 打包
mvn package

# 清理
mvn clean
```

### 前端
```bash
# 开发模式
npm run dev

# 生产构建
npm run build

# 预览构建结果
npm run preview
```

## 故障排除

### 后端无法启动
- 检查 JDK 版本：`java -version`
- 检查 MySQL 是否运行
- 检查端口 8080 是否被占用

### 前端无法连接后端
- 检查后端是否正在运行
- 确认 vite.config.js 中的代理配置正确
- 检查浏览器控制台的 CORS 错误

### 数据库连接失败
- 验证 MySQL 服务状态
- 检查用户名和密码
- 确保数据库 `chat` 已创建

## 下一步

- 实现聊天消息功能
- 实现 WebSocket 实时通信
- 优化前端界面
- 添加更多业务功能
