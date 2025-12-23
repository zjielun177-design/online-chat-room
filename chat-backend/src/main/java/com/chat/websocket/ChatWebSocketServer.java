package com.chat.websocket;

import com.alibaba.fastjson2.JSON;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

/**
 * WebSocket服务端处理器
 */
public class ChatWebSocketServer extends TextWebSocketHandler {

    /**
     * 连接建立时的处理
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = extractUserId(session);
        WebSocketSessionManager.add(userId, session);
        System.out.println("用户 " + userId + " 连接成功");
    }

    /**
     * 接收消息时的处理
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Map<String, Object> msgMap = JSON.parseObject(payload, Map.class);

        String userId = extractUserId(session);
        String type = (String) msgMap.get("type");

        // 后续处理消息逻辑
        System.out.println("用户 " + userId + " 发送消息: " + payload);
    }

    /**
     * 连接关闭时的处理
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = extractUserId(session);
        WebSocketSessionManager.remove(userId);
        System.out.println("用户 " + userId + " 断开连接");
    }

    /**
     * 从session中提取用户ID（后续可根据token进行解析）
     */
    private String extractUserId(WebSocketSession session) {
        String query = session.getHandshakeHeaders().getFirst("sec-websocket-key");
        if (query == null) {
            query = "anonymous_" + System.currentTimeMillis();
        }
        return query;
    }

}
