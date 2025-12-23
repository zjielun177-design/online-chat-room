package com.chat;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * 生成正确的加密密码
 */
public class GeneratePassword {

    private static final String SALT = "chat-app-salt-key";

    public static String encodePassword(String password) {
        try {
            String saltedPassword = password + SALT;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(saltedPassword.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    public static void main(String[] args) {
        String password = "test123";
        String encoded = encodePassword(password);

        System.out.println("=================================");
        System.out.println("原始密码: " + password);
        System.out.println("加密后:   " + encoded);
        System.out.println("=================================");
        System.out.println("");
        System.out.println("请在 MySQL 中执行以下命令:");
        System.out.println("");
        System.out.println("UPDATE t_user SET password = '" + encoded + "' WHERE username IN ('admin', 'user1', 'user2');");
        System.out.println("");
        System.out.println("然后重试登录");
    }
}
