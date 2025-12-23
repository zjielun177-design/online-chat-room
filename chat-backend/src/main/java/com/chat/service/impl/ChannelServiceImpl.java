package com.chat.service.impl;

import com.chat.entity.TChannel;
import com.chat.entity.TUserChannel;
import com.chat.repository.ChannelRepository;
import com.chat.repository.UserChannelRepository;
import com.chat.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 频道业务实现
 */
@Service
public class ChannelServiceImpl implements ChannelService {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserChannelRepository userChannelRepository;

    @Override
    @Transactional
    public TChannel createChannel(String name, String description, Long creatorId) {
        // 检查频道名是否已存在
        if (channelRepository.findByName(name) != null) {
            throw new IllegalArgumentException("频道名称已存在");
        }

        TChannel channel = TChannel.builder()
                .name(name)
                .description(description)
                .creatorId(creatorId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        channel = channelRepository.save(channel);

        // 创建者自动加入频道
        TUserChannel userChannel = TUserChannel.builder()
                .userId(creatorId)
                .channelId(channel.getId())
                .joinTime(LocalDateTime.now())
                .build();
        userChannelRepository.save(userChannel);

        return channel;
    }

    @Override
    public TChannel getChannelById(Long channelId) {
        return channelRepository.findById(channelId).orElse(null);
    }

    @Override
    public List<TChannel> getAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public List<TChannel> getUserChannels(Long userId) {
        return channelRepository.findChannelsByUserId(userId);
    }

    @Override
    public List<Long> getChannelMembers(Long channelId) {
        List<TUserChannel> userChannels = userChannelRepository.findByChannelId(channelId);
        return userChannels.stream()
                .map(TUserChannel::getUserId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void joinChannel(Long userId, Long channelId) {
        // 检查用户是否已在频道中
        if (isUserInChannel(userId, channelId)) {
            throw new IllegalArgumentException("用户已在频道中");
        }

        TUserChannel userChannel = TUserChannel.builder()
                .userId(userId)
                .channelId(channelId)
                .joinTime(LocalDateTime.now())
                .build();
        userChannelRepository.save(userChannel);
    }

    @Override
    @Transactional
    public void leaveChannel(Long userId, Long channelId) {
        userChannelRepository.deleteByUserIdAndChannelId(userId, channelId);
    }

    @Override
    public boolean isUserInChannel(Long userId, Long channelId) {
        return userChannelRepository.findByUserIdAndChannelId(userId, channelId).isPresent();
    }

    @Override
    @Transactional
    public TChannel updateChannel(Long channelId, String name, String description) {
        TChannel channel = getChannelById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("频道不存在");
        }

        if (name != null && !name.isEmpty()) {
            channel.setName(name);
        }
        if (description != null) {
            channel.setDescription(description);
        }
        channel.setUpdateTime(LocalDateTime.now());

        return channelRepository.save(channel);
    }

    @Override
    @Transactional
    public void deleteChannel(Long channelId) {
        // 删除所有用户与频道的关联
        List<TUserChannel> userChannels = userChannelRepository.findByChannelId(channelId);
        userChannelRepository.deleteAll(userChannels);

        // 删除频道
        channelRepository.deleteById(channelId);
    }
}
