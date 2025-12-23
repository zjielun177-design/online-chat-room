package com.chat.service;

import com.chat.entity.TGroupMsg;
import com.chat.entity.TPrivateMsg;

import java.util.List;

/**
 * 消息业务接口
 */
public interface MessageService {

    /**
     * 保存群组消息
     */
    TGroupMsg saveGroupMessage(Long senderId, Long channelId, String content);

    /**
     * 保存私聊消息
     */
    TPrivateMsg savePrivateMessage(Long senderId, Long receiverId, String content);

    /**
     * 获取频道消息历史
     */
    List<TGroupMsg> getChannelMessages(Long channelId, int limit);

    /**
     * 获取与某用户的私聊消息
     */
    List<TPrivateMsg> getPrivateMessages(Long userId1, Long userId2, int limit);

    /**
     * 获取所有私聊会话
     */
    List<TPrivateMsg> getPrivateMessageConversations(Long userId);

    /**
     * 删除群组消息
     */
    void deleteGroupMessage(Long messageId);

    /**
     * 删除私聊消息
     */
    void deletePrivateMessage(Long messageId);

    /**
     * 获取群组消息
     */
    TGroupMsg getGroupMessage(Long messageId);

    /**
     * 获取私聊消息
     */
    TPrivateMsg getPrivateMessage(Long messageId);
}
