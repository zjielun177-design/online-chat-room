#!/usr/bin/env node

const crypto = require('crypto');
const base64 = require('buffer').Buffer;

function encodePassword(password) {
  const salt = 'chat-app-salt-key';
  const saltedPassword = password + salt;
  const hashedBytes = crypto.createHash('sha256').update(saltedPassword).digest();
  return hashedBytes.toString('base64');
}

// 生成 test123 的加密密码
const password = 'test123';
const encoded = encodePassword(password);

console.log('原始密码:', password);
console.log('加密后:', encoded);
console.log('');
console.log('SQL 更新语句:');
console.log(`UPDATE t_user SET password = '${encoded}' WHERE username IN ('admin', 'user1', 'user2');`);
