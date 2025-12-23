package com.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 频道DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDTO {

    private Long id;
    private String name;
    private String description;
    private Long creatorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer memberCount;
}
