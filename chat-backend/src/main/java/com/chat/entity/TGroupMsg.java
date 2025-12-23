package com.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 群消息实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_group_msg")
public class TGroupMsg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 发送者ID
     */
    @Column(nullable = false)
    private Long senderId;

    /**
     * 频道ID
     */
    @Column(nullable = false)
    private Long channelId;

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

