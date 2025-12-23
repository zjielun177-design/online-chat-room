package com.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.entity.TPrivateMsg;

import java.util.List;

/**
 * 私聊消息服务接口
 */
public interface PrivateMsgService extends IService<TPrivateMsg> {

    /**
     * 获取两个用户之间的私聊记录
     */
    List<TPrivateMsg> getPrivateMessages(Long userId1, Long userId2, int pageNum, int pageSize);

}
