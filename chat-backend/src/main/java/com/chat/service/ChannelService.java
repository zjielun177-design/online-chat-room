package com.chat.service;

import com.chat.dto.ChannelDTO;
import com.chat.entity.TChannel;

import java.util.List;

/**
 * 频道业务接口
 */
public interface ChannelService {

    /**
     * 创建频道
     */
    TChannel createChannel(String name, String description, Long creatorId);

    /**
     * 获取频道详情
     */
    TChannel getChannelById(Long channelId);

    /**
     * 获取所有频道
     */
    List<TChannel> getAllChannels();

    /**
     * 获取用户加入的频道
     */
    List<TChannel> getUserChannels(Long userId);

    /**
     * 获取频道的所有成员ID
     */
    List<Long> getChannelMembers(Long channelId);

    /**
     * 用户加入频道
     */
    void joinChannel(Long userId, Long channelId);

    /**
     * 用户离开频道
     */
    void leaveChannel(Long userId, Long channelId);

    /**
     * 检查用户是否在频道中
     */
    boolean isUserInChannel(Long userId, Long channelId);

    /**
     * 更新频道
     */
    TChannel updateChannel(Long channelId, String name, String description);

    /**
     * 删除频道
     */
    void deleteChannel(Long channelId);
}
