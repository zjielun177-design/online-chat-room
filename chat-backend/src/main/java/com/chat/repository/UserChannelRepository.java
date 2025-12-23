package com.chat.repository;

import com.chat.entity.TUserChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户频道关联Repository
 */
@Repository
public interface UserChannelRepository extends JpaRepository<TUserChannel, Long> {

    /**
     * 查找用户是否加入了某个频道
     */
    @Query("SELECT uc FROM TUserChannel uc WHERE uc.userId = :userId AND uc.channelId = :channelId")
    Optional<TUserChannel> findByUserIdAndChannelId(@Param("userId") Long userId, @Param("channelId") Long channelId);

    /**
     * 获取频道的所有成员
     */
    @Query("SELECT uc FROM TUserChannel uc WHERE uc.channelId = :channelId")
    List<TUserChannel> findByChannelId(@Param("channelId") Long channelId);

    /**
     * 获取用户加入的所有频道
     */
    @Query("SELECT uc FROM TUserChannel uc WHERE uc.userId = :userId")
    List<TUserChannel> findByUserId(@Param("userId") Long userId);

    /**
     * 删除用户与频道的关联
     */
    void deleteByUserIdAndChannelId(Long userId, Long channelId);
}
