package com.chat.service.impl;

import com.chat.entity.TGroupMsg;
import com.chat.entity.TPrivateMsg;
import com.chat.repository.GroupMsgRepository;
import com.chat.repository.PrivateMsgRepository;
import com.chat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息业务实现
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private GroupMsgRepository groupMsgRepository;

    @Autowired
    private PrivateMsgRepository privateMsgRepository;

    @Override
    @Transactional
    public TGroupMsg saveGroupMessage(Long senderId, Long channelId, String content) {
        TGroupMsg message = TGroupMsg.builder()
                .senderId(senderId)
                .channelId(channelId)
                .content(content)
                .sendTime(LocalDateTime.now())
                .build();
        return groupMsgRepository.save(message);
    }

    @Override
    @Transactional
    public TPrivateMsg savePrivateMessage(Long senderId, Long receiverId, String content) {
        TPrivateMsg message = TPrivateMsg.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .sendTime(LocalDateTime.now())
                .build();
        return privateMsgRepository.save(message);
    }

    @Override
    public List<TGroupMsg> getChannelMessages(Long channelId, int limit) {
        Pageable pageable = PageRequest.of(0, Math.max(limit, 50)); // 至少50条，最多按传入的limit
        return groupMsgRepository.findByChannelIdOrderBySendTimeDesc(channelId, pageable).getContent();
    }

    @Override
    public List<TPrivateMsg> getPrivateMessages(Long userId1, Long userId2, int limit) {
        Pageable pageable = PageRequest.of(0, Math.max(limit, 50));
        return privateMsgRepository.findPrivateMessages(userId1, userId2, pageable);
    }

    @Override
    public List<TPrivateMsg> getPrivateMessageConversations(Long userId) {
        return privateMsgRepository.findPrivateMessageConversations(userId);
    }

    @Override
    @Transactional
    public void deleteGroupMessage(Long messageId) {
        groupMsgRepository.deleteById(messageId);
    }

    @Override
    @Transactional
    public void deletePrivateMessage(Long messageId) {
        privateMsgRepository.deleteById(messageId);
    }

    @Override
    public TGroupMsg getGroupMessage(Long messageId) {
        return groupMsgRepository.findById(messageId).orElse(null);
    }

    @Override
    public TPrivateMsg getPrivateMessage(Long messageId) {
        return privateMsgRepository.findById(messageId).orElse(null);
    }
}
