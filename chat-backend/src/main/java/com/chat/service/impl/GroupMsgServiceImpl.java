package com.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.entity.TGroupMsg;
import com.chat.mapper.GroupMsgMapper;
import com.chat.service.GroupMsgService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 群消息服务实现类
 */
@Service
public class GroupMsgServiceImpl extends ServiceImpl<GroupMsgMapper, TGroupMsg> implements GroupMsgService {

    @Override
    public List<TGroupMsg> getChannelMessages(Long channelId, int pageNum, int pageSize) {
        QueryWrapper<TGroupMsg> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("channel_id", channelId)
                .orderByDesc("send_time");
        return this.baseMapper.selectList(queryWrapper);
    }

}
