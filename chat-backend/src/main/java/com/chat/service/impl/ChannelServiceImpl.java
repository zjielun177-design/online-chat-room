package com.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.entity.TChannel;
import com.chat.mapper.ChannelMapper;
import com.chat.service.ChannelService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 频道服务实现类
 */
@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, TChannel> implements ChannelService {

    @Override
    public List<TChannel> getUserChannels(Long userId) {
        // 后续实现从用户-频道关联表中查询
        return this.baseMapper.selectList(null);
    }

}
