package com.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private Long id;
    private String type; // 'group' 或 'private'
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Long channelId;
    private Long receiverId;
    private String content;
    private LocalDateTime sendTime;
}
