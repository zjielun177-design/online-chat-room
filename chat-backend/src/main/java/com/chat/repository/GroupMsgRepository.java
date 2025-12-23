package com.chat.repository;

import com.chat.entity.TGroupMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 群消息Repository
 */
@Repository
public interface GroupMsgRepository extends JpaRepository<TGroupMsg, Long> {

    List<TGroupMsg> findByChannelIdOrderBySendTimeDesc(Long channelId);

}
