package com.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.entity.TPrivateMsg;
import com.chat.mapper.PrivateMsgMapper;
import com.chat.service.PrivateMsgService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 私聊消息服务实现类
 */
@Service
public class PrivateMsgServiceImpl extends ServiceImpl<PrivateMsgMapper, TPrivateMsg> implements PrivateMsgService {

    @Override
    public List<TPrivateMsg> getPrivateMessages(Long userId1, Long userId2, int pageNum, int pageSize) {
        QueryWrapper<TPrivateMsg> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(q -> q.eq("sender_id", userId1).eq("receiver_id", userId2))
                .or(q -> q.eq("sender_id", userId2).eq("receiver_id", userId1))
                .orderByDesc("send_time");
        return this.baseMapper.selectList(queryWrapper);
    }

}
