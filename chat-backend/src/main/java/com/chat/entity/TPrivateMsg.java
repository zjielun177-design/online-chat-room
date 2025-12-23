package com.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 私聊消息实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_private_msg")
public class TPrivateMsg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 发送者ID
     */
    @Column(nullable = false)
    private Long senderId;

    /**
     * 接收者ID
     */
    @Column(nullable = false)
    private Long receiverId;

    /**
     * 消息内容
     */
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    /**
     * 发送时间
     */
    @Column(nullable = false)
    private LocalDateTime sendTime;

}

