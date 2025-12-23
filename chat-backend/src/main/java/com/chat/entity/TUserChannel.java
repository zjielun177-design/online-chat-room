package com.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户-频道关联实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_user_channel", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "channel_id"})
})
public class TUserChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 频道ID
     */
    @Column(nullable = false)
    private Long channelId;

    /**
     * 加入时间
     */
    @Column(nullable = false)
    private LocalDateTime joinTime;

}

