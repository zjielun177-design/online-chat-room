package com.chat.repository;

import com.chat.entity.TChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 频道Repository
 */
@Repository
public interface ChannelRepository extends JpaRepository<TChannel, Long> {

    /**
     * 根据创建者ID查找频道
     */
    List<TChannel> findByCreatorId(Long creatorId);

    /**
     * 获取用户加入的所有频道
     */
    @Query("SELECT c FROM TChannel c JOIN TUserChannel uc ON c.id = uc.channelId WHERE uc.userId = :userId")
    List<TChannel> findChannelsByUserId(@Param("userId") Long userId);

    /**
     * 根据频道名称查找
     */
    TChannel findByName(String name);
}
