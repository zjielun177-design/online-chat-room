#!/bin/bash

# 测试后端是否启动
echo "========== 测试后端连接 =========="
echo "访问健康检查端点..."
curl -s http://localhost:8080/health | jq . 2>/dev/null || curl -s http://localhost:8080/health
echo ""

# 测试登录
echo "========== 测试登录 =========="
echo "使用 admin / test123 登录..."
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"test123"}' \
  -w "\nHTTP Status: %{http_code}\n" 2>/dev/null | jq . 2>/dev/null

echo ""
echo "========== 检查数据库 =========="
echo "确保MySQL中有test数据..."
mysql -u root -proot -e "use chat; select id, username, email, nickname from t_user limit 3;" 2>/dev/null || echo "MySQL连接失败，请确保MySQL服务正在运行"
