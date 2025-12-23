package com.chat.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密工具类
 */
public class PasswordUtil {

    private static final String SALT = "chat-app-salt-key";

    /**
     * 加密密码
     */
    public static String encodePassword(String password) {
        try {
            String saltedPassword = password + SALT;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(saltedPassword.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 验证密码
     */
    public static boolean verifyPassword(String rawPassword, String encodedPassword) {
        String encoded = encodePassword(rawPassword);
        return encoded.equals(encodedPassword);
    }

}
