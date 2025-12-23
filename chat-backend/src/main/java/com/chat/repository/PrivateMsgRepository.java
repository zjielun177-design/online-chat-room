package com.chat.repository;

import com.chat.entity.TPrivateMsg;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 私聊消息Repository
 */
@Repository
public interface PrivateMsgRepository extends JpaRepository<TPrivateMsg, Long> {

    @Query("SELECT m FROM TPrivateMsg m WHERE " +
           "(m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.receiverId = :userId1) " +
           "ORDER BY m.sendTime DESC")
    List<TPrivateMsg> findPrivateMessages(@Param("userId1") Long userId1, @Param("userId2") Long userId2, Pageable pageable);

    /**
     * 获取用户的所有私聊会话（按最新消息排序）
     */
    @Query("SELECT DISTINCT m FROM TPrivateMsg m WHERE m.senderId = :userId OR m.receiverId = :userId ORDER BY m.sendTime DESC")
    List<TPrivateMsg> findPrivateMessageConversations(@Param("userId") Long userId);
}

