package com.chat.websocket;

import com.alibaba.fastjson2.JSON;
import com.chat.entity.TGroupMsg;
import com.chat.entity.TPrivateMsg;
import com.chat.entity.TUser;
import com.chat.service.ChannelService;
import com.chat.service.MessageService;
import com.chat.service.UserService;
import com.chat.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebSocket服务端处理器
 */
@Component
public class ChatWebSocketServer extends TextWebSocketHandler {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private UserService userService;

    /**
     * 连接建立时的处理
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = extractUserId(session);
        if (userId != null) {
            WebSocketSessionManager.add(userId.toString(), session);
            System.out.println("用户 " + userId + " WebSocket连接成功");
        } else {
            session.close();
        }
    }

    /**
     * 接收消息时的处理
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Map<String, Object> msgMap = JSON.parseObject(payload, Map.class);

        Long userId = extractUserId(session);
        if (userId == null) {
            return;
        }

        String type = (String) msgMap.get("type");

        try {
            if ("group_message".equals(type)) {
                handleGroupMessage(userId, msgMap);
            } else if ("private_message".equals(type)) {
                handlePrivateMessage(userId, msgMap);
            } else if ("ping".equals(type)) {
                // 心跳包，直接返回pong
                Map<String, Object> response = new HashMap<>();
                response.put("type", "pong");
                session.sendMessage(new TextMessage(JSON.toJSONString(response)));
            }
        } catch (Exception e) {
            System.err.println("处理WebSocket消息失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 连接关闭时的处理
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = extractUserId(session);
        if (userId != null) {
            WebSocketSessionManager.remove(userId.toString());
            System.out.println("用户 " + userId + " WebSocket断开连接");
        }
    }

    /**
     * 处理群组消息
     */
    private void handleGroupMessage(Long userId, Map<String, Object> msgMap) throws Exception {
        Long channelId = Long.parseLong(msgMap.get("channelId").toString());
        String content = (String) msgMap.get("content");

        if (content == null || content.trim().isEmpty()) {
            return;
        }

        // 检查用户是否在频道中，若不在则尝试自动加入（避免旧用户直接发送失败）
        if (!channelService.isUserInChannel(userId, channelId)) {
            try {
                channelService.joinChannel(userId, channelId);
            } catch (Exception joinError) {
                System.err.println("自动加入频道失败: " + joinError.getMessage());
            }
        }
        if (!channelService.isUserInChannel(userId, channelId)) {
            return;
        }

        // 保存消息到数据库
        TGroupMsg dbMsg = messageService.saveGroupMessage(userId, channelId, content);

        // 构建响应消息
        TUser sender = userService.getUserById(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("type", "group_message");
        response.put("id", dbMsg.getId());
        response.put("senderId", dbMsg.getSenderId());
        response.put("senderName", formatDisplayName(sender));
        response.put("senderAvatar", sender != null ? sender.getAvatar() : "");
        response.put("channelId", dbMsg.getChannelId());
        response.put("content", dbMsg.getContent());
        response.put("sendTime", dbMsg.getSendTime().toString());

        // 广播消息给频道中的所有用户
        List<Long> memberIds = channelService.getChannelMembers(channelId);
        String msgStr = JSON.toJSONString(response);

        for (Long memberId : memberIds) {
            WebSocketSession memberSession = WebSocketSessionManager.get(memberId.toString());
            if (memberSession != null && memberSession.isOpen()) {
                memberSession.sendMessage(new TextMessage(msgStr));
            }
        }
    }

    /**
     * 处理私聊消息
     */
    private void handlePrivateMessage(Long userId, Map<String, Object> msgMap) throws Exception {
        Long receiverId = Long.parseLong(msgMap.get("receiverId").toString());
        String content = (String) msgMap.get("content");

        if (content == null || content.trim().isEmpty()) {
            return;
        }

        // 保存消息到数据库
        TPrivateMsg dbMsg = messageService.savePrivateMessage(userId, receiverId, content);

        // 构建响应消息
        TUser sender = userService.getUserById(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("type", "private_message");
        response.put("id", dbMsg.getId());
        response.put("senderId", dbMsg.getSenderId());
        response.put("senderName", formatDisplayName(sender));
        response.put("senderAvatar", sender != null ? sender.getAvatar() : "");
        response.put("receiverId", dbMsg.getReceiverId());
        response.put("content", dbMsg.getContent());
        response.put("sendTime", dbMsg.getSendTime().toString());

        String msgStr = JSON.toJSONString(response);

        // 发送给接收者
        WebSocketSession receiverSession = WebSocketSessionManager.get(receiverId.toString());
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(msgStr));
        }

        // 发送确认给发送者
        WebSocketSession senderSession = WebSocketSessionManager.get(userId.toString());
        if (senderSession != null && senderSession.isOpen()) {
            senderSession.sendMessage(new TextMessage(msgStr));
        }
    }

    /**
     * 从session中提取用户ID
     */
    private Long extractUserId(WebSocketSession session) {
        try {
            // 从URL查询参数中获取token
            String uri = session.getHandshakeHeaders().getFirst("sec-websocket-key");
            String queryString = session.getUri() != null ? session.getUri().getQuery() : null;

            if (queryString != null && queryString.contains("token=")) {
                String token = queryString.substring(queryString.indexOf("token=") + 6);
                // 移除后续的&或其他参数
                if (token.contains("&")) {
                    token = token.substring(0, token.indexOf("&"));
                }

                // 验证token并提取userId
                if (TokenUtil.validateToken(token)) {
                    return TokenUtil.getUserIdFromToken(token);
                }
            }
        } catch (Exception e) {
            System.err.println("提取用户ID失败: " + e.getMessage());
        }
        return null;
    }

    private String formatDisplayName(TUser user) {
        if (user == null) {
            return "Unknown";
        }
        String nickname = user.getNickname();
        if (nickname != null && !nickname.trim().isEmpty()) {
            return nickname.trim();
        }
        return user.getUsername() != null ? user.getUsername() : "Unknown";
    }
}
