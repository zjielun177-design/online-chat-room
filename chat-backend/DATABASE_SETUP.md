# 数据库初始化指南

## 前置条件
- MySQL 8.0+
- 具有管理员权限的MySQL用户

## 初始化步骤

### 1. 创建数据库和表
```bash
# 使用 MySQL 命令行客户端
mysql -u root -p < src/main/resources/db/schema.sql
```

或者在 MySQL 中手动执行：
```sql
-- 复制 src/main/resources/db/schema.sql 文件中的内容并执行
```

### 2. 初始化数据
```bash
mysql -u root -p < src/main/resources/db/data.sql
```

或者在 MySQL 中手动执行：
```sql
-- 复制 src/main/resources/db/data.sql 文件中的内容并执行
```

## 修改数据库连接

编辑 `src/main/resources/application.yml` 文件，根据你的MySQL配置修改：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/chat?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root      # 修改为你的MySQL用户名
    password: root      # 修改为你的MySQL密码
```

## 验证数据库初始化

可以通过以下SQL命令验证数据库是否正确初始化：

```sql
USE chat;

-- 查看所有表
SHOW TABLES;

-- 查看用户数据
SELECT * FROM t_user;

-- 查看频道数据
SELECT * FROM t_channel;

-- 查看用户-频道关联
SELECT * FROM t_user_channel;
```

## 测试用户

初始化后，可以使用以下测试账户：

| 用户名 | 邮箱 | 密码 |
|--------|------|------|
| admin | admin@chat.com | test123 |
| user1 | user1@chat.com | test123 |
| user2 | user2@chat.com | test123 |

## 常见问题

### 问题1：连接被拒绝
确保 MySQL 服务正在运行，并且用户名和密码正确。

### 问题2：数据库已存在
如果需要重新初始化，先删除数据库：
```sql
DROP DATABASE IF EXISTS chat;
```
然后重新运行 schema.sql

### 问题3：字符编码问题
确保在执行SQL时使用UTF-8编码，MySQL表已配置为 `utf8mb4_unicode_ci`
