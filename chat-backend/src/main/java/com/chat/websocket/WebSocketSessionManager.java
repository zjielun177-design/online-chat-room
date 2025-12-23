package com.chat.websocket;

import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket会话管理器
 */
public class WebSocketSessionManager {

    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**
     * 添加session
     */
    public static void add(String userId, WebSocketSession session) {
        sessions.put(userId, session);
    }

    /**
     * 移除session
     */
    public static void remove(String userId) {
        sessions.remove(userId);
    }

    /**
     * 获取session
     */
    public static WebSocketSession get(String userId) {
        return sessions.get(userId);
    }

    /**
     * 获取所有sessions
     */
    public static ConcurrentHashMap<String, WebSocketSession> getAll() {
        return sessions;
    }

    /**
     * 判断用户是否在线
     */
    public static boolean isOnline(String userId) {
        return sessions.containsKey(userId);
    }

}
