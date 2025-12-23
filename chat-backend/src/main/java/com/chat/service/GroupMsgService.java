package com.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.entity.TGroupMsg;

import java.util.List;

/**
 * 群消息服务接口
 */
public interface GroupMsgService extends IService<TGroupMsg> {

    /**
     * 获取频道的聊天记录
     */
    List<TGroupMsg> getChannelMessages(Long channelId, int pageNum, int pageSize);

}
