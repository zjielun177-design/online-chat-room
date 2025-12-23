package com.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.entity.TChannel;

import java.util.List;

/**
 * 频道服务接口
 */
public interface ChannelService extends IService<TChannel> {

    /**
     * 获取用户加入的所有频道
     */
    List<TChannel> getUserChannels(Long userId);

}
