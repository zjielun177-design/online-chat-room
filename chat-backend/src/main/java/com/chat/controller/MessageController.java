package com.chat.controller;

import com.chat.dto.ApiResponse;
import com.chat.dto.MessageDTO;
import com.chat.entity.TGroupMsg;
import com.chat.entity.TPrivateMsg;
import com.chat.entity.TUser;
import com.chat.service.MessageService;
import com.chat.service.UserService;
import com.chat.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息管理接口
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    /**
     * 获取频道的消息历史
     */
    @GetMapping("/channel/{channelId}")
    public ApiResponse<List<MessageDTO>> getChannelMessages(
            @PathVariable Long channelId,
            @RequestParam(defaultValue = "50") int limit) {
        try {
            List<TGroupMsg> messages = messageService.getChannelMessages(channelId, limit);
            List<MessageDTO> dtos = messages.stream()
                    .map(this::convertGroupMsgToDTO)
                    .collect(Collectors.toList());
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取消息失败：" + e.getMessage());
        }
    }

    /**
     * 获取与某用户的私聊消息
     */
    @GetMapping("/private/{userId}")
    public ApiResponse<List<MessageDTO>> getPrivateMessages(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "50") int limit) {
        try {
            String tokenValue = token.replace("Bearer ", "");
            Long currentUserId = TokenUtil.getUserIdFromToken(tokenValue);

            List<TPrivateMsg> messages = messageService.getPrivateMessages(currentUserId, userId, limit);
            List<MessageDTO> dtos = messages.stream()
                    .map(this::convertPrivateMsgToDTO)
                    .collect(Collectors.toList());
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取私聊消息失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有私聊会话
     */
    @GetMapping("/private")
    public ApiResponse<List<MessageDTO>> getPrivateConversations(
            @RequestHeader("Authorization") String token) {
        try {
            String tokenValue = token.replace("Bearer ", "");
            Long userId = TokenUtil.getUserIdFromToken(tokenValue);

            List<TPrivateMsg> messages = messageService.getPrivateMessageConversations(userId);
            List<MessageDTO> dtos = messages.stream()
                    .map(this::convertPrivateMsgToDTO)
                    .collect(Collectors.toList());
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取私聊会话失败：" + e.getMessage());
        }
    }

    /**
     * 保存消息（REST备用接口）
     */
    @PostMapping
    public ApiResponse<MessageDTO> saveMessage(
            @RequestHeader("Authorization") String token,
            @RequestBody MessageRequest request) {
        try {
            String tokenValue = token.replace("Bearer ", "");
            Long userId = TokenUtil.getUserIdFromToken(tokenValue);

            if ("group".equals(request.getType())) {
                TGroupMsg msg = messageService.saveGroupMessage(userId, request.getChannelId(), request.getContent());
                return ApiResponse.success(convertGroupMsgToDTO(msg));
            } else if ("private".equals(request.getType())) {
                TPrivateMsg msg = messageService.savePrivateMessage(userId, request.getReceiverId(), request.getContent());
                return ApiResponse.success(convertPrivateMsgToDTO(msg));
            } else {
                return ApiResponse.error(400, "消息类型不支持");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "保存消息失败：" + e.getMessage());
        }
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    public ApiResponse<String> deleteMessage(
            @PathVariable Long messageId,
            @RequestHeader("Authorization") String token) {
        try {
            String tokenValue = token.replace("Bearer ", "");
            Long userId = TokenUtil.getUserIdFromToken(tokenValue);

            // 查找消息并验证权限（简化处理，实际应分别查询）
            TGroupMsg groupMsg = messageService.getGroupMessage(messageId);
            if (groupMsg != null) {
                if (!groupMsg.getSenderId().equals(userId)) {
                    return ApiResponse.error(403, "只能删除自己的消息");
                }
                messageService.deleteGroupMessage(messageId);
                return ApiResponse.success("消息已删除");
            }

            TPrivateMsg privateMsg = messageService.getPrivateMessage(messageId);
            if (privateMsg != null) {
                if (!privateMsg.getSenderId().equals(userId)) {
                    return ApiResponse.error(403, "只能删除自己的消息");
                }
                messageService.deletePrivateMessage(messageId);
                return ApiResponse.success("消息已删除");
            }

            return ApiResponse.error(404, "消息不存在");
        } catch (Exception e) {
            return ApiResponse.error(500, "删除消息失败：" + e.getMessage());
        }
    }

    private MessageDTO convertGroupMsgToDTO(TGroupMsg msg) {
        TUser sender = userService.getUserById(msg.getSenderId());
        return MessageDTO.builder()
                .id(msg.getId())
                .type("group")
                .senderId(msg.getSenderId())
                .senderName(sender != null ? sender.getUsername() : "Unknown")
                .senderAvatar(sender != null ? sender.getAvatar() : "")
                .channelId(msg.getChannelId())
                .content(msg.getContent())
                .sendTime(msg.getSendTime())
                .build();
    }

    private MessageDTO convertPrivateMsgToDTO(TPrivateMsg msg) {
        TUser sender = userService.getUserById(msg.getSenderId());
        return MessageDTO.builder()
                .id(msg.getId())
                .type("private")
                .senderId(msg.getSenderId())
                .senderName(sender != null ? sender.getUsername() : "Unknown")
                .senderAvatar(sender != null ? sender.getAvatar() : "")
                .receiverId(msg.getReceiverId())
                .content(msg.getContent())
                .sendTime(msg.getSendTime())
                .build();
    }

    /**
     * 消息请求DTO
     */
    public static class MessageRequest {
        private String type; // 'group' 或 'private'
        private Long channelId;
        private Long receiverId;
        private String content;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Long getChannelId() {
            return channelId;
        }

        public void setChannelId(Long channelId) {
            this.channelId = channelId;
        }

        public Long getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(Long receiverId) {
            this.receiverId = receiverId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
